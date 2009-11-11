package org.yosemite.jcsadra.impl;

import java.util.List;

import java.util.Map;

import org.apache.cassandra.service.Cassandra;
import org.apache.cassandra.service.ColumnOrSuperColumn;
import org.apache.cassandra.service.SuperColumn;
import org.apache.cassandra.service.Column;
import org.apache.cassandra.service.ColumnParent;
import org.apache.cassandra.service.ColumnPath;
import org.apache.cassandra.service.InvalidRequestException;
import org.apache.cassandra.service.NotFoundException;
import org.apache.cassandra.service.SlicePredicate;
import org.apache.cassandra.service.UnavailableException;
import org.apache.thrift.TException;

import org.yosemite.jcsadra.CassandraClient;
import org.yosemite.jcsadra.KeySpace;


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
		this._cassandra = client.getCassandra();
		this.keyspaceName = keyspaceName ;
		this.keyspaceDesc = keyspaceDesc ;
		this.consistencyLevel = clevel ;
	}
	
	


	public void batchInsert( String key,
			Map<String, List<Column>> columnMap , Map<String, List<SuperColumn>> superColumnMap )
			throws InvalidRequestException, UnavailableException, TException {
		//_cassandra.batch_insert(keyspaceName , key, cfmap , getRealConsistencyLevel(consistencyLevel));
	}
	
	


	
	public Column getColumn(String key, ColumnPath columnPath)
			throws InvalidRequestException, NotFoundException,
			UnavailableException, TException {
		// TODO Auto-generated method stub
		return null;
	}

	public int getCount(String key, ColumnParent columnParent)
			throws InvalidRequestException, UnavailableException, TException {
		return _cassandra.get_count(keyspaceName, key, columnParent,
				getRealConsistencyLevel(consistencyLevel));		
	}

	public List<String> getKeyRange( String columnFamily,
			String start, String finish, int count)
			throws InvalidRequestException, UnavailableException, TException {
		return _cassandra.get_key_range(keyspaceName, columnFamily , start, finish, count, 
				getRealConsistencyLevel(consistencyLevel));
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

	public Map<String, List<Column>> multigetSlice(List<String> keys,
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


	public Map<String, List<SuperColumn>> multigetSuperSlice(List<String> keys,
			ColumnParent columnParent, SlicePredicate predicate)
			throws InvalidRequestException, UnavailableException, TException {
		Map<String, List<ColumnOrSuperColumn>> res = _cassandra.multiget_slice(
				keyspaceName, keys, columnParent, predicate,
				getRealConsistencyLevel(consistencyLevel));
		
		//TODO update this
		return null ; 
		
	}

	
	
	/**
	 * remove column 
	 */
	public void remove(String key, ColumnPath columnPath)
			throws InvalidRequestException, UnavailableException, TException {
		_cassandra.remove(keyspaceName, key, columnPath, getTimeStamp(),
				getRealConsistencyLevel(consistencyLevel));
	}
	
	
	
	
	/**
	 * get key space name
	 * @return
	 */
	public String getKeyspaceName() {
		return keyspaceName;
	}

	public Map<String, Map<String, String>> describeKeyspace()
			throws NotFoundException, TException {
		return getKeyspaceDesc();
	}

	
	/**
	 * getKeyspaceDesc
	 * @return
	 */
	public Map<String, Map<String, String>> getKeyspaceDesc() {
		return keyspaceDesc;
	}
	
	
	// ======================= private =======================
	private long getTimeStamp(){
		return System.currentTimeMillis() ;
	}
	
	
	private int getRealConsistencyLevel(CassandraClient.ConsistencyLevel cclevel){
		switch (cclevel){
			case ONE:
				return org.apache.cassandra.service.ConsistencyLevel.ONE;
			case QUORUM:
				return org.apache.cassandra.service.ConsistencyLevel.QUORUM;
			case ALL:
				return org.apache.cassandra.service.ConsistencyLevel.ALL;
			case ZERO:
				return org.apache.cassandra.service.ConsistencyLevel.ZERO;
			default:
				return org.apache.cassandra.service.ConsistencyLevel.QUORUM;
		}
	}
	
	private String keyspaceName;
	
	private Map<String, Map<String, String>> keyspaceDesc;
	
	
	// Cassandra client object
	private Cassandra.Client _cassandra ;
	
	// My Cassandra client
	private CassandraClient _client ;

	public void insert(String key, ColumnPath columnPath, byte[] value)
			throws InvalidRequestException, UnavailableException, TException {
		// TODO Auto-generated method stub
		
	}

}
