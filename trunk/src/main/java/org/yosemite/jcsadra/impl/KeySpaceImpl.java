package org.yosemite.jcsadra.impl;

import org.apache.cassandra.service.Cassandra;
import org.yosemite.jcsadra.CassandraClient;
import org.yosemite.jcsadra.KeySpace;

public class KeySpaceImpl implements KeySpace {
	
	/*
	 * Constructor method, only inner class can create a 
	 * key space instance.
	 */
	protected KeySpaceImpl(CassandraClient client){
		this._client = client ;
		this._cassadra = client.getCassandra();
	}
	
	
	
	// Cassandra client object
	private Cassandra.Client _cassadra ;
	
	// My Cassandra client
	private CassandraClient _client ;
	

}
