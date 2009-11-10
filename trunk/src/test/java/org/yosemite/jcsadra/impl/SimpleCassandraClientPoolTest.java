package org.yosemite.jcsadra.impl;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.cassandra.service.Cassandra;
import org.apache.commons.pool.BasePoolableObjectFactory;
import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;

import org.yosemite.jcsadra.CassandraClient;
import org.yosemite.jcsadra.CassandraClientPool;
import org.yosemite.jcsadra.CassandraClientPool.ExhaustedAction;
import org.yosemite.jcsadra.impl.CassandraClientImpl;
import org.yosemite.jcsadra.impl.SimpleCassandraClientPool;
import org.yosemite.jcsadra.impl.SimpleCassandraClientPool.PoolableClientFactory;


/**
 * test simple cassandra client
 * @author sanli
 */
public class SimpleCassandraClientPoolTest {


	private int maxIdle;
	private long maxWaitWhenBlockByExhausted;
	private ExhaustedAction exhaustedAction;
	private int maxActive;
	private int port;
	private String serviceURL;
	SimpleCassandraClientPool pool ;

	@Before
	public void createPool(){
		this.serviceURL = "localhost" ; 
		this.port = 9160 ;
		this.maxActive = 50 ;
		this.exhaustedAction = CassandraClientPool.ExhaustedAction.WHEN_EXHAUSTED_BLOCK ;
		this.maxWaitWhenBlockByExhausted = 5000 ; 
		this.maxIdle = 5 ;
		PoolableObjectFactory factory = new DumyPoolableClientFactory() ;
		
		pool = new SimpleCassandraClientPool(serviceURL, port,
				 factory , maxActive, exhaustedAction ,  
				 maxWaitWhenBlockByExhausted,  maxIdle) ; 
		
		count = new AtomicInteger(0) ;
	}
	
	public void closePool(){
		pool.close() ;
	}
	
	@Test
	public void testGetClient() throws NoSuchElementException, IllegalStateException, Exception{
		
			int start = pool.getAvailableNum() ;
			CassandraClient client = pool.getClient() ;
			int end = pool.getAvailableNum() ;
			assertTrue( pool.getActiveNum() == 1 ) ;
			pool.releaseClient(client);
			assertTrue( pool.getActiveNum() == 0 ) ;
			end = pool.getAvailableNum() ;
			assertTrue( ( end - start )  == 1 ) ;
			assertTrue( count.intValue()  == 1 ) ;
			

	}
	
	@Test
	public void testGetMultiplClient() throws NoSuchElementException, IllegalStateException, Exception{
		
		HashSet<CassandraClient> clients = new HashSet<CassandraClient>();
		for(int i = 0 ; i<= pool.getMaxActive() - 1 ; i++){
			CassandraClient cli = pool.getClient() ;	
			clients.add(cli);
		}
		assertTrue(clients.size() == pool.getMaxActive()) ;
		
		try{
			// because all client was borrow out, so this time get will fail and throw
			// out exception.
			pool.getClient();
			fail("The pool should already exhaushted.");
		}catch(Exception e){
			System.out.println(e);				
		}
		
		for(CassandraClient cli : clients){
			pool.releaseClient(cli) ;
		}
		
		assertTrue( pool.getAvailableNum()  == 5 ) ;
		assertTrue( pool.getActiveNum()  == 0 ) ;
	}
	
	
	AtomicInteger count ;

	
	/**
	 * inner class for create and destory Cassandra.Client 
	 * @author sanli
	 */
	public class DumyPoolableClientFactory extends BasePoolableObjectFactory
			implements PoolableObjectFactory {
		
		
		@Override
		public void destroyObject(Object obj) throws Exception {
			CassandraClient client = (CassandraClient)obj ;
			count.decrementAndGet() ;
		}

		@Override
		public Object makeObject() throws Exception {
			TTransport tr = new TSocket(serviceURL, port);
			TProtocol proto = new TBinaryProtocol(tr);
			Cassandra.Client client = new Cassandra.Client(proto);
			CassandraClient cclient = new CassandraClientImpl(client);
			count.incrementAndGet();
			return cclient;
		}
		
		/**
		 * do a simple cassandra request
		 */
		@Override
		public boolean validateObject(Object obj) {
			return true;
		}
		
	}
	
	
	
	

}
