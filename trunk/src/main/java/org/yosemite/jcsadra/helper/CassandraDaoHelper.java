package org.yosemite.jcsadra.helper;

import org.apache.cassandra.service.InvalidRequestException;
import org.apache.cassandra.service.NotFoundException;
import org.apache.cassandra.service.UnavailableException;
import org.apache.thrift.TException;
import org.yosemite.jcsadra.CassandraClient;
import org.yosemite.jcsadra.CassandraClientPool;
import org.yosemite.jcsadra.KeySpace;



/**
 * A basic Cassandra dao object, like Springs JDBCTemplate
 * this helper class will help do the things like:  get CassandraClient
 * run function, close client.
 * 
 * @author sanli
 */
public class CassandraDaoHelper {
	
	protected CassandraClientPool pool ;	
	
	public CassandraDaoHelper(CassandraClientPool pool ){
		this.pool = pool ; 
	}
	
	/**
	 * do action on key space help class
	 * @throws Exception 
	 */
	public void doAction(Action action) throws Exception{
		CassandraClient cc = pool.getClient();

		try{
			action.go(cc);
		}finally{
			// force release cc
			pool.releaseClient(cc);
		}
	}
	
	public interface Action{
		void go(CassandraClient cc );
	}
	
	/**
	 * do action on key space help class
	 * @throws Exception 
	 */
	public void doActionOnKeySpace(String keyspace , KeySpaceAction action) throws Exception{
		CassandraClient cc = pool.getClient();
		KeySpace ks = cc.getKeySpace(keyspace);
		try{
			action.go(ks);
		}finally{
			// force release cc
			pool.releaseClient(cc);
		}
	}
	
	public interface KeySpaceAction{
		void go(KeySpace ks) throws InvalidRequestException,
				UnavailableException, TException, NotFoundException;
	}
	
	
	

}
