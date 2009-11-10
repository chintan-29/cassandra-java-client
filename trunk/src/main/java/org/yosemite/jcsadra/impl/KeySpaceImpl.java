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

	@Override
	public void batchInsert(String keyspace, String key,
			Map<String, List<BaseColumn>> cfmap)
			throws InvalidRequestException, UnavailableException, TException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Map<String, Map<String, String>> describeKeyspace()
			throws NotFoundException, TException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Column getColumn(String key, ColumnPath columnPath)
			throws InvalidRequestException, NotFoundException,
			UnavailableException, TException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getCount(String key, ColumnParent columnParent)
			throws InvalidRequestException, UnavailableException, TException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<String> getKeyRange(String keyspace, String columnFamily,
			String start, String finish, int count)
			throws InvalidRequestException, UnavailableException, TException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Column> getSlice(String key, ColumnParent columnParent,
			SlicePredicate predicate) throws InvalidRequestException,
			NotFoundException, UnavailableException, TException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SuperColumn getSuperColumn(String key, ColumnPath columnPath)
			throws InvalidRequestException, NotFoundException,
			UnavailableException, TException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<SuperColumn> getSuperSlice(String key,
			ColumnParent columnParent, SlicePredicate predicate)
			throws InvalidRequestException, NotFoundException,
			UnavailableException, TException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void insert(String keyspace, String key, ColumnPath columnPath,
			byte[] value) throws InvalidRequestException, UnavailableException,
			TException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Map<String, Column> multigetColumn(List<String> keys,
			ColumnPath columnPath) throws InvalidRequestException,
			UnavailableException, TException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, List<BaseColumn>> multigetSlice(List<String> keys,
			ColumnParent columnParent, SlicePredicate predicate)
			throws InvalidRequestException, UnavailableException, TException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, SuperColumn> multigetSuperColumn(List<String> keys,
			ColumnPath columnPath) throws InvalidRequestException,
			UnavailableException, TException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, List<BaseColumn>> multigetSuperSlice(List<String> keys,
			ColumnParent columnParent, SlicePredicate predicate)
			throws InvalidRequestException, UnavailableException, TException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void remove(String key, ColumnPath columnPath)
			throws InvalidRequestException, UnavailableException, TException {
		// TODO Auto-generated method stub
		
	}
	

}
