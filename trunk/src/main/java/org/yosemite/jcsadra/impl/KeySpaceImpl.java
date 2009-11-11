package org.yosemite.jcsadra.impl;

import java.util.List;
import java.util.Map;

import org.apache.cassandra.service.Cassandra;
import org.apache.cassandra.service.ColumnParent;
import org.apache.cassandra.service.ColumnPath;
import org.apache.cassandra.service.InvalidRequestException;
import org.apache.cassandra.service.NotFoundException;
import org.apache.cassandra.service.SlicePredicate;
import org.apache.cassandra.service.UnavailableException;
import org.apache.thrift.TException;
import org.yosemite.jcsadra.BaseColumn;
import org.yosemite.jcsadra.CassandraClient;
import org.yosemite.jcsadra.Column;
import org.yosemite.jcsadra.KeySpace;
import org.yosemite.jcsadra.SuperColumn;
import org.yosemite.jcsadra.CassandraClient.ConsistencyLevel;

public class KeySpaceImpl implements KeySpace {
	
	private ConsistencyLevel consistencyLevel;

	/*
	 * Constructor method, only inner class can create a 
	 * key space instance.
	 */
	protected KeySpaceImpl(CassandraClient client, String keyspaceName,
			Map<String, Map<String, String>> keyspaceDesc,
			CassandraClient.ConsistencyLevel clevel) {
		this._client = client ;
		this._cassadra = client.getCassandra();
		this.keyspaceName = keyspaceName ;
		this.keyspaceDesc = keyspaceDesc ;
		this.consistencyLevel = clevel ;
	}
	
	
	
	// Cassandra client object
	private Cassandra.Client _cassadra ;
	
	// My Cassandra client
	private CassandraClient _client ;

	public void batchInsert(String keyspace, String key,
			Map<String, List<BaseColumn>> cfmap)
			throws InvalidRequestException, UnavailableException, TException {
		// TODO Auto-generated method stub
		
	}

	public Map<String, Map<String, String>> describeKeyspace()
			throws NotFoundException, TException {
		// TODO Auto-generated method stub
		return null;
	}

	
	public Column getColumn(String key, ColumnPath columnPath)
			throws InvalidRequestException, NotFoundException,
			UnavailableException, TException {
		// TODO Auto-generated method stub
		return null;
	}

	public int getCount(String key, ColumnParent columnParent)
			throws InvalidRequestException, UnavailableException, TException {
		// TODO Auto-generated method stub
		return 0;
	}

	public List<String> getKeyRange(String keyspace, String columnFamily,
			String start, String finish, int count)
			throws InvalidRequestException, UnavailableException, TException {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Column> getSlice(String key, ColumnParent columnParent,
			SlicePredicate predicate) throws InvalidRequestException,
			NotFoundException, UnavailableException, TException {
		// TODO Auto-generated method stub
		return null;
	}

	public SuperColumn getSuperColumn(String key, ColumnPath columnPath)
			throws InvalidRequestException, NotFoundException,
			UnavailableException, TException {
		// TODO Auto-generated method stub
		return null;
	}

	public List<SuperColumn> getSuperSlice(String key,
			ColumnParent columnParent, SlicePredicate predicate)
			throws InvalidRequestException, NotFoundException,
			UnavailableException, TException {
		// TODO Auto-generated method stub
		return null;
	}


	public void insert(String keyspace, String key, ColumnPath columnPath,
			byte[] value) throws InvalidRequestException, UnavailableException,
			TException {
		// TODO Auto-generated method stub
		
	}


	public Map<String, Column> multigetColumn(List<String> keys,
			ColumnPath columnPath) throws InvalidRequestException,
			UnavailableException, TException {
		// TODO Auto-generated method stub
		return null;
	}

	public Map<String, List<BaseColumn>> multigetSlice(List<String> keys,
			ColumnParent columnParent, SlicePredicate predicate)
			throws InvalidRequestException, UnavailableException, TException {
		// TODO Auto-generated method stub
		return null;
	}


	public Map<String, SuperColumn> multigetSuperColumn(List<String> keys,
			ColumnPath columnPath) throws InvalidRequestException,
			UnavailableException, TException {
		// TODO Auto-generated method stub
		return null;
	}


	public Map<String, List<BaseColumn>> multigetSuperSlice(List<String> keys,
			ColumnParent columnParent, SlicePredicate predicate)
			throws InvalidRequestException, UnavailableException, TException {
		// TODO Auto-generated method stub
		return null;
	}

	public void remove(String key, ColumnPath columnPath)
			throws InvalidRequestException, UnavailableException, TException {
		// TODO Auto-generated method stub
		
	}
	
	
	
	
	/**
	 * get key space name
	 * @return
	 */
	public String getKeyspaceName() {
		return keyspaceName;
	}

	/**
	 * getKeyspaceDesc
	 * @return
	 */
	public Map<String, Map<String, String>> getKeyspaceDesc() {
		return keyspaceDesc;
	}
	
	
	// ======================= private =======================
	private String keyspaceName;
	
	private Map<String, Map<String, String>> keyspaceDesc;
	


}
