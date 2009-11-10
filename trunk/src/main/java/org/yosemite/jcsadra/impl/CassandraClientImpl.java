package org.yosemite.jcsadra.impl;

import java.util.List;

import org.apache.cassandra.service.Cassandra;
import org.apache.thrift.TException;
import org.yosemite.jcsadra.CassandraClient;
import org.yosemite.jcsadra.KeySpace;

public class CassandraClientImpl implements CassandraClient {

	public final static String PROP_CLUSTER_NAME = "cluster name" ; 
	public final static String PROP_CONFIG_FILE = "config file" ;
	public final static String PROP_TOKEN_MAP = "token map" ;
	public final static String PROP_KEYSPACE = "keyspace" ;


	/**
	 * constuctor function, for provent other one create Client object by
	 * hand, so make it protected.
	 * @param cassandra
	 * @throws TException 
	 */
	protected CassandraClientImpl(Cassandra.Client cassandra) throws TException{
		this._cassandra = cassandra ;
		clusterName = _cassandra.get_string_property(PROP_CLUSTER_NAME); 
		keyspaces = _cassandra.get_string_list_property(PROP_KEYSPACE);
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


	public String getStringProperty(String propertyName) {
		// TODO Auto-generated method stub
		return null;
	}


	
	//=============================== private field & method ============================
	private List<String> keyspaces;
	
	private String clusterName;
	
	
}
