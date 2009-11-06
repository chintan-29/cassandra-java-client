package org.yosemite.jcsadra.impl;

import org.apache.cassandra.service.Cassandra;
import org.yosemite.jcsadra.CassandraClient;

public class BaseCassandraClient implements CassandraClient {

	protected BaseCassandraClient(Cassandra.Client client){
		this._client = client ;
	}
	
	private Cassandra.Client _client ;
	
}
