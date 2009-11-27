/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.yosemite.jcsadra.impl;

import java.util.NoSuchElementException;


import org.apache.cassandra.service.Cassandra;
import org.apache.commons.pool.BasePoolableObjectFactory;
import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.commons.pool.impl.GenericObjectPoolFactory;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import org.apache.log4j.Logger;

import org.yosemite.jcsadra.CassandraClient;
import org.yosemite.jcsadra.CassandraClientPool;




/**
 * A simple client pool, have some configuration item to make it can work under
 * different request. it based on org.apache.commons.pool.GenericObjectPool.
 * 
 * configuration item include:
 *    maxActive controls the maximum number of client that can be created 
 *    by the pool at a given time.  When non-positive, there is no
 *    limit to the number of client that can be managed by the pool at one time.
 *    When maxActive is reached, the pool is said to be exhausted. 
 *    The default setting for this parameter is 50, this is large number, but should 
 *    work for us, because cassandra was a server cluster, if we using round-robine DNS
 *    LB, and have 10 server in cluster, there was avg 5 connect to each server, that
 *    would be not a problem
 *    
 *    maxIdle controls the maximum number of objects that can sit idle in the pool 
 *    at any time.  When negative, there is no limit to the number of objects that may 
 *    be idle at one time. The default setting for this parameter is 5( 1/10 of maxActive, for same the 
 *    client resource).<br>
 *  
 *    exhaustedAction specifies the behavior of the getClinet() method when 
 *    the pool is exhausted:
 *      When exhaustedAction is WHEN_EXHAUSTED_FAIL getClient() will throw a 
 *      NoSuchElementException
 *      
 *      When exhaustedAction WHEN_EXHAUSTED_GROW getClinet will create a new
 *      object and return it (essentially making maxActive meaningless.)
 *    
 *      When exhaustedAction is WHEN_EXHAUSTED_BLOCK, getClient() will block
 *      until a new or idle object is available.
 *      If a positive maxWait value is supplied, then getClient will block for at
 *      most that many milliseconds, after which a NoSuchElementException
 *      will be thrown.  If maxWait is non-positive, the getClient() method will 
 *      block indefinitely.
 *    
 *    The default exhaustedAction setting is WHEN_EXHAUSTED_BLOCK} and the default 
 *    maxWaitWhenBlock setting is 60000. By default, therefore, getClinet() will
 *    block 1 minutes, then throw out a exception, if we can not get a connect in 1 
 *    minutes, there must be something wrong.
 *  
 *  
 *  state table:
 *  =========                    ===========                        =========                         ============
 *  |init(0)|  ->  getClient  -> |maxActive| ->  ReleaseClient   -> |maxIdle| ->  Wait Idle time   -> |minIdle(0)|
 *  =========                    ===========                        =========                         ============
 *                                   ^                                   |                                 | 
 *                                   |-------------------when new getClient request arrive-----------------|
 *  
 * @author dayong
 */
public class SimpleCassandraClientPool implements CassandraClientPool {
	
	Logger logger = Logger.getLogger(this.getClass());
	
	
	/**
	 * default max active number is 50, because Cassandra have multiple servers
	 * so 50 should be a good value.
	 */
	public static final int DEFAULT_MAX_ACTIVE = 50 ;
	
	/**
	 * default max idle number is 5, so when the client keep idle, the total connection 
	 * number will release to 5 
	 */
	public static final int DEFAULT_MAX_IDLE = 5 ;
	
	
	/**
	 * default exhausted action is block for 1 minute, because in many time, cassandra was being 
	 * used in a web backed, so too long time block will cause very bad user experience.
	 * for some other non-synchronize application, this value can configure to a more long time.  
	 */
	public static final ExhaustedAction DEFAULT_EXHAUSTED_ACTION = ExhaustedAction.WHEN_EXHAUSTED_BLOCK ;
	
	/**
	 * the default max wait time when exhausted happened, default value is 1 minute
	 */
	public static final long DEFAULT_MAX_WAITTIME_WHEN_EXHAUSTED = 1 * 60 * 1000  ;
	
	
	/**
	 * the object pool, all client instance managed by this pool
	 */
	GenericObjectPoolFactory _poolfactory = null  ;
	GenericObjectPool _pool = null ;
	PoolableClientFactory _clientfactory = null ;
	

	
	
	public SimpleCassandraClientPool(String serviceURL , int port ){
		this(serviceURL, port, null , DEFAULT_MAX_ACTIVE,
				DEFAULT_EXHAUSTED_ACTION, DEFAULT_MAX_WAITTIME_WHEN_EXHAUSTED , 
				DEFAULT_MAX_IDLE );
	}
	

	public void close() {
		try {
			_pool.close() ;
		} catch (Exception e) {
			logger.error("close client pool error",e);
		}
	}

	public int getAvailableNum() {
		return _pool.getNumIdle();
	}

	public CassandraClient getClient() throws Exception,
			NoSuchElementException, IllegalStateException {
		CassandraClient cc = (CassandraClient)_pool.borrowObject();
		return cc;
	}


	public int getActiveNum() {
		return _pool.getNumActive();
	}

	public void releaseClient(CassandraClient client) throws Exception {
		_pool.returnObject(client);
	}


	
	/**
	 * inner class for create and destory Cassandra.Client 
	 * @author dayong
	 */
	public class PoolableClientFactory extends BasePoolableObjectFactory
			implements PoolableObjectFactory {
		
		@Override
		public void destroyObject(Object obj) throws Exception {
			CassandraClient client = (CassandraClient)obj ;
			if(logger.isDebugEnabled())
				logger.debug("close client " + client.toString());			
			
			closeClient(client) ;
		}

		@Override
		public Object makeObject() throws Exception {
			if(logger.isDebugEnabled())
				logger.debug("create cassandra client [" + serviceURL + ":" + port + "]" );
			
			try{
				return createClient(serviceURL , port ) ;
			}catch(TTransportException e){
				logger.error("create client error:" , e) ;
				throw e ;
			} catch (TException e) {
				logger.error("create client error:" , e) ;
				throw e ;
			}
		}
		
		/**
		 * do a simple cassandra request
		 */
		@Override
		public boolean validateObject(Object obj) {
			return validateClient((CassandraClient)obj);
		}
		
	}
	
	public int getMaxActive() {
		return maxActive;
	}


	public void setMaxActive(int maxActive) {
		this.maxActive = maxActive;
		_pool.setMaxActive(maxActive);
	}


	public int getMaxIdle() {
		return maxIdle;
	}


	public void setMaxIdle(int maxIdle) {
		this.maxIdle = maxIdle;
		_pool.setMaxIdle(maxIdle);
	}


	public long getMaxWaitWhenBlockByExhausted() {
		return maxWaitWhenBlockByExhausted;
	}


	public void setMaxWaitWhenBlockByExhausted(long maxWaitWhenBlockByExhausted) {
		this.maxWaitWhenBlockByExhausted = maxWaitWhenBlockByExhausted;
		_pool.setMaxWait(maxWaitWhenBlockByExhausted);
	}


	/**
	 * serviceURL is read only, can not change after pool created
	 * @return
	 */
	public String getServiceURL() {
		return serviceURL;
	}


	/**
	 * port number was read only, can not change after pool created
	 * @return
	 */
	public int getPort() {
		return port;
	}

	
	
	// ************************************ protected method ***********************************
	
	
	
	/**
	 * this construct method wamaxWaitWhenBlockExhausteds for unit test, for regulary usage, should not given the client factory 
	 */
	protected SimpleCassandraClientPool(String serviceURL, int port,
			PoolableObjectFactory clientfactory, int maxActive, 
			ExhaustedAction exhaustedAction , long maxWait, int maxIdle) {
		this.serviceURL = serviceURL ; 
		this.port = port ;
		this.maxActive = maxActive ;
		this.exhaustedAction = exhaustedAction ;
		this.maxWaitWhenBlockByExhausted = maxWait ; 
		this.maxIdle = maxIdle;
		
		// if not give  a client factory, will create the default PoolableClientFactory
		if(clientfactory==null){
			this._clientfactory = new PoolableClientFactory();
			_poolfactory = new GenericObjectPoolFactory( _clientfactory , maxActive ,
					getObjectPoolExhaustedAction(exhaustedAction) ,
					maxWait , maxIdle );
		}else{
			_poolfactory = new GenericObjectPoolFactory( clientfactory , maxActive ,
					getObjectPoolExhaustedAction(exhaustedAction) ,
					maxWait , maxIdle );
		}
		
		
		
		_pool = (GenericObjectPool) _poolfactory.createPool() ; 
	}
	
	
	public static CassandraClient createClient(String  serviceURL , int port ) throws TTransportException , TException {

			TTransport tr = new TSocket( serviceURL, port );
			TProtocol proto = new TBinaryProtocol(tr);
			Cassandra.Client client = new Cassandra.Client(proto);
			tr.open();
			
			CassandraClientImpl cclient = new CassandraClientImpl(client) ;
			cclient.init() ;
			return cclient ;
		
	}
	
	
	public static void closeClient(CassandraClient cclient) {
		Cassandra.Client client = cclient.getCassandra() ;
		
		client.getInputProtocol().getTransport().close() ;
		client.getOutputProtocol().getTransport().close() ;
	}
	
	
	public static boolean validateClient(CassandraClient client){
		//TODO send simple reqesut to cassandra, this request 
		// should very quickly and light
		return true ;
	}
	
	public static byte getObjectPoolExhaustedAction(ExhaustedAction exhaustedAction){
		switch(exhaustedAction){
			case WHEN_EXHAUSTED_FAIL :
				return GenericObjectPool.WHEN_EXHAUSTED_FAIL ;
			case WHEN_EXHAUSTED_BLOCK :
				return GenericObjectPool.WHEN_EXHAUSTED_BLOCK ;
			case WHEN_EXHAUSTED_GROW :
				return GenericObjectPool.WHEN_EXHAUSTED_GROW ;
			default:
				return GenericObjectPool.WHEN_EXHAUSTED_BLOCK ;
		}
	}
	
	
	// **********************************  private method *********************************
	
	private int maxActive  ;
	
	private int maxIdle  ;
	
	private ExhaustedAction exhaustedAction ;
	
	private long maxWaitWhenBlockByExhausted ;
	
	
	// the Cassandra service url, 
	private String serviceURL = "localhost" ;
	
	// port
	private int port = 9096 ;

	
	

}
