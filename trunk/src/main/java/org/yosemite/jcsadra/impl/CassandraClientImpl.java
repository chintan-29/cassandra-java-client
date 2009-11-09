package org.yosemite.jcsadra.impl;

import org.apache.cassandra.service.Cassandra;
import org.yosemite.jcsadra.CassandraClient;
import org.yosemite.jcsadra.KeySpace;

public class CassandraClientImpl implements CassandraClient {

	

	/**
	 * constuctor function, for provent other one create Client object by
	 * hand, so make it protected.
	 * @param cassandra
	 */
	protected CassandraClientImpl(Cassandra.Client cassandra){
		this._cassandra = cassandra ;
	}

	
	/**
	 * return Cassandra.Client
	 */
	public Cassandra.Client getCassandra() {
		return _cassandra;
	}
	
	
	
	
	
	// thrift object
	Cassandra.Client _cassandra ;





	public KeySpace getKeySpace(String keySpaceName)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		return null;
	}


	public KeySpace getKeySpace(String keySpaceName,
			ConsistencyLevel consitencyLevel) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		return null;
	}


	
	
	
	
	
}
