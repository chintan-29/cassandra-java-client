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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import java.util.Map;

import org.apache.cassandra.service.Cassandra;
import org.apache.cassandra.service.ColumnOrSuperColumn;
import org.apache.cassandra.service.SliceRange;
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


/**
 * KeySpace object implement
 * 
 * @author dayong
 */
public class KeySpaceImpl implements KeySpace {

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
	
	
	public Column getColumn(String key, ColumnPath columnPath)
			throws InvalidRequestException, NotFoundException,
			UnavailableException, TException {
		if(!valideColumnPath(columnPath))
			throw new InvalidRequestException("give column was not a valid column");
		
		ColumnOrSuperColumn cosc = _cassandra.get(keyspaceName, key, columnPath, getRealConsistencyLevel(consistencyLevel)) ;
		return cosc.getColumn();
	}
	
	
	
	@Override
	public SuperColumn getSuperColumn(String key, ColumnPath columnPath)
			throws InvalidRequestException, NotFoundException,
			UnavailableException, TException {
		return getSuperColumn(key , columnPath , false , Integer.MAX_VALUE );
	}
	
	
	/**
	 * get super column value, looks the cassandra.get() command  can not retrieve super column, we should 
     * using get slice for get a supercolumn value
	 */
	public SuperColumn getSuperColumn(String key, ColumnPath columnPath , boolean reversed , int size )
			throws InvalidRequestException, NotFoundException,
			UnavailableException, TException {
		if(!valideSuperColumnPath(columnPath))
			throw new InvalidRequestException("give super column was not a valid column");
		
		//only can get super column by get slice 
		ColumnParent clp =  new ColumnParent(columnPath.getColumn_family() , columnPath.getSuper_column() );
		SliceRange sr = new SliceRange(new byte[0] , new byte[0], reversed , size );
		SlicePredicate  sp = new SlicePredicate(null , sr );
		List<ColumnOrSuperColumn> cosc = _cassandra.get_slice(keyspaceName, key, clp,
				sp, getRealConsistencyLevel(consistencyLevel));
		
		
		return new SuperColumn(columnPath.getSuper_column() , getColumnList(cosc) );
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

	
	/**
	 * get slice object, the given column parent should be a valid column path, all result
	 * will be convert to a Column list, if result include some SuperColumn, will being ignored.
	 */
	public List<Column> getSlice(String key, ColumnParent columnParent,
			SlicePredicate predicate) throws InvalidRequestException,
			NotFoundException, UnavailableException, TException {
		List<ColumnOrSuperColumn> cosclist = _cassandra.get_slice(keyspaceName, key, columnParent, predicate,
				getRealConsistencyLevel(consistencyLevel));
		
		ArrayList<Column> result = new ArrayList<Column>(cosclist.size());
		for(ColumnOrSuperColumn cosc : cosclist){
			result.add(cosc.getColumn());
		}
		return result;
	}



	/**
	 * get clice object, the given column parent should be a valide super column path, all result
	 * will be conver to a SuperColumn list, if result include some standard column, will be ignored.
	 */
	public List<SuperColumn> getSuperSlice(String key,
			ColumnParent columnParent, SlicePredicate predicate)
			throws InvalidRequestException, NotFoundException,
			UnavailableException, TException {
		List<ColumnOrSuperColumn> cosclist = _cassandra.get_slice(keyspaceName, key, columnParent, predicate,
				getRealConsistencyLevel(consistencyLevel));
		
		ArrayList<SuperColumn> result = new ArrayList<SuperColumn>(cosclist.size());
		for(ColumnOrSuperColumn cosc : cosclist){
			result.add(cosc.getSuper_column());
		}
		return result;
	}


	/**
	 * insert column, seems only can insert on column, if you want insert batch column, should 
	 * using batchInsert() 
	 */
	public void insert(String key, ColumnPath columnPath,
			byte[] value) throws InvalidRequestException, UnavailableException,
			TException {
		if(!valideColumnPath(columnPath)){
			throw new InvalidRequestException("specified column family not exist");
		}
		
		_cassandra.insert(keyspaceName, key, columnPath, value, this
				.getTimeStamp(), getRealConsistencyLevel(consistencyLevel));		
	}
	
	
	
	/**
	 * batch insert multi-columns into one row, the column can be standard column or super column.
	 */
	public void batchInsert( String key,
			Map<String, List<Column>> columnMap , Map<String, List<SuperColumn>> superColumnMap )
			throws InvalidRequestException, UnavailableException, TException {
		if (columnMap == null && superColumnMap == null)
			throw new InvalidRequestException(
					"columnMap and SuperColumnMap can not be null at same time");
		
		int size = (columnMap == null ? 0 : columnMap.size())
				+ (columnMap == null ? 0 : columnMap.size());
		Map<String, List<ColumnOrSuperColumn>> cfmap = new HashMap<String, List<ColumnOrSuperColumn>>(size * 2);
		
		if(columnMap != null){
			for(Map.Entry<String, List<Column>> entry : columnMap.entrySet()){
				cfmap.put(entry.getKey(), getSoscList(entry.getValue()));
			}
		}

		if(superColumnMap != null){
			for(Map.Entry<String, List<SuperColumn>> entry : superColumnMap.entrySet()){
				cfmap.put(entry.getKey(), getSoscSuperList(entry.getValue()));
			}
		}
		
		// do really insert
		_cassandra.batch_insert(keyspaceName , key, cfmap , getRealConsistencyLevel(consistencyLevel));
	}


	/**
	 * multi get column
	 */
	public Map<String, Column> multigetColumn(List<String> keys,
			ColumnPath columnPath) throws InvalidRequestException,
			UnavailableException, TException {
		if(!valideColumnPath(columnPath)){
			throw new InvalidRequestException("specified column family not exist");
		}
		
		Map<String, ColumnOrSuperColumn> cfmap = _cassandra.multiget(
				keyspaceName, keys, columnPath,
				getRealConsistencyLevel(consistencyLevel));
		
		Map<String, Column> result = new HashMap<String , Column>();
		for(Map.Entry<String, ColumnOrSuperColumn> entry : cfmap.entrySet()){
			result.put(entry.getKey(), entry.getValue().getColumn());
		}
		return result;
	}

	
	
	public Map<String, SuperColumn> multigetSuperColumn(List<String> keys,
			ColumnPath columnPath ) throws InvalidRequestException,
			UnavailableException, TException {
		return multigetSuperColumn(keys, columnPath , false , Integer.MAX_VALUE );
	}
	
	
	/**
	 * multi get super column, result is a map of key and supercolumn, if the key can not found given super
	 * column, it will not being include into result map.
	 */
	public Map<String, SuperColumn> multigetSuperColumn(List<String> keys,
			ColumnPath columnPath , boolean reversed , int size) throws InvalidRequestException,
			UnavailableException, TException {
		if(!valideSuperColumnPath(columnPath)){
			throw new InvalidRequestException("specified column family not exist");
		}
		
		
		//only can get supercolumn by multigetSuperSlice 
		ColumnParent clp =  new ColumnParent(columnPath.getColumn_family() , columnPath.getSuper_column() );
		SliceRange sr = new SliceRange(new byte[0] , new byte[0], reversed , size );
		SlicePredicate  sp = new SlicePredicate(null , sr );
		Map<String, List<SuperColumn>>  sclist = multigetSuperSlice(keys, clp , sp );
		
		HashMap<String, SuperColumn> result = new HashMap<String, SuperColumn>(keys.size()*2);
		for(Map.Entry<String , List<SuperColumn> > entry : sclist.entrySet()){
			List<SuperColumn> sclistByKey = entry.getValue();
			SuperColumn sc ;
			if( sclistByKey.size() > 0 ){
				result.put( entry.getKey() ,  sclistByKey.get(0));
			}
		}

		return result;
	}
	
	

	/**
	 * multi get slice of column
	 */
	public Map<String, List<Column>> multigetSlice(List<String> keys,
			ColumnParent columnParent, SlicePredicate predicate)
			throws InvalidRequestException, UnavailableException, TException {
		Map<String,List<ColumnOrSuperColumn>> cfmap = _cassandra.multiget_slice(
				keyspaceName, keys, columnParent, predicate,
				getRealConsistencyLevel(consistencyLevel));
		
		Map<String, List<Column>> result = new HashMap<String , List<Column>>();
		for(Map.Entry<String, List<ColumnOrSuperColumn>> entry : cfmap.entrySet()){
			result.put(entry.getKey(), getColumnList(entry.getValue()));
		}
		return result;
	}



	/**
	 * multi get slice of super column
	 */
	public Map<String, List<SuperColumn>> multigetSuperSlice(List<String> keys,
			ColumnParent columnParent, SlicePredicate predicate)
			throws InvalidRequestException, UnavailableException, TException {
		Map<String,List<ColumnOrSuperColumn>> cfmap = _cassandra.multiget_slice(
				keyspaceName, keys, columnParent, predicate,
				getRealConsistencyLevel(consistencyLevel));
		
		// if user not given super column name, the multiget_slice will return List filled with
		// super column, if user given a column name, the return List will filled with column,
		// this is a bad interface design.
		if(columnParent.getSuper_column()==null){
			Map<String, List<SuperColumn>> result = new HashMap<String , List<SuperColumn>>();
			for(Map.Entry<String, List<ColumnOrSuperColumn>> entry : cfmap.entrySet()){
				result.put(entry.getKey(), getSuperColumnList(entry.getValue()));
			}
			return result; 
		}else{
			Map<String, List<SuperColumn>> result = new HashMap<String , List<SuperColumn>>();
			for(Map.Entry<String, List<ColumnOrSuperColumn>> entry : cfmap.entrySet()){
				SuperColumn spc = new SuperColumn( columnParent.getSuper_column() ,  getColumnList(entry.getValue()) );
				ArrayList<SuperColumn> spclist = new ArrayList<SuperColumn>(1);
				spclist.add(spc) ;
				result.put(entry.getKey(), spclist );
			}
			return result;
		}
		
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

	
	
	public static String CF_COMPAREWITH = "CompareWith" ;
	public static String CF_DESC = "Desc" ;
	public static String CF_TYPE = "Type" ;
	public static String CF_TYPE_STANDARD = "Standard" ;
	public static String CF_TYPE_SUPER = "Super" ;
	public static String CF_FLUSHPERIOD = "FlushPeriodInMinutes" ;
	
	
	/**
	 * getKeyspaceDesc 
	 *
	 * the result map like bellow: 
     * {    
     *     StandardByUUID1={
     *         CompareWith=org.apache.cassandra.db.marshal.TimeUUIDType, 
     *         Desc=Keyspace1.StandardByUUID1(ROW_KEY, COLUMN_MAP(COLUMN_KEY, COLUMN_VALUE, COLUMN_TIMESTAMP)), 
     *         Type=Standard, FlushPeriodInMinutes=0
     *     }, 
     *     
     *     Super1={
     *         CompareSubcolumnsWith=org.apache.cassandra.db.marshal.UTF8Type, 
     *         CompareWith=org.apache.cassandra.db.marshal.UTF8Type, 
     *         Type=Super, 
     *         FlushPeriodInMinutes=0, 
     *         Desc=Keyspace1.Super1(ROW_KEY, SUPER_COLUMN_MAP(SUPER_COLUMN_KEY, COLUMN_MAP(COLUMN_KEY, COLUMN_VALUE, COLUMN_TIMESTAMP)))
     *     }, 
     *     
     *     Standard2={
     *         CompareWith=org.apache.cassandra.db.marshal.UTF8Type, 
     *         Desc=Keyspace1.Standard2(ROW_KEY, COLUMN_MAP(COLUMN_KEY, COLUMN_VALUE, COLUMN_TIMESTAMP)), 
     *         Type=Standard, FlushPeriodInMinutes=0},
     * 
     *    Standard1={
     *        CompareWith=org.apache.cassandra.db.marshal.BytesType, 
     *        Desc=Keyspace1.Standard1(ROW_KEY, COLUMN_MAP(COLUMN_KEY, COLUMN_VALUE, COLUMN_TIMESTAMP)), 
     *        Type=Standard, FlushPeriodInMinutes=60}
     * }
     *
	 * @return
	 */
	public Map<String, Map<String, String>> getKeyspaceDesc() {
		return keyspaceDesc;
	}
	
	
	
	// ======================= private =======================

	
	// check if the given column path was a Column
	private boolean valideColumnPath(ColumnPath columnPath) throws InvalidRequestException{
		String cf = columnPath.getColumn_family() ;
		
		Map<String, String> cfdefine;
		if( (cfdefine = keyspaceDesc.get(cf)) != null ){
 
			if(cfdefine.get(CF_TYPE).equals(CF_TYPE_STANDARD) && columnPath.getColumn()!=null ){
				// if the column family is a standard column
				return true ;	
			} else if (cfdefine.get(CF_TYPE).equals(CF_TYPE_SUPER)
					&& columnPath.getSuper_column() != null
					&& columnPath.getColumn() != null) {
				// if the column family is a super column and also give the super_column name
				return true ;
			}
			return false ;
		}
		throw new InvalidRequestException("specified column family not exist:"+cf);
	}

	// check if the given column path was a SuperColumn
	private boolean valideSuperColumnPath(ColumnPath columnPath) throws InvalidRequestException{
		String cf = columnPath.getColumn_family() ;
		
		Map<String, String> cfdefine;
		if( (cfdefine = keyspaceDesc.get(cf)) != null ){
			return cfdefine.get(CF_TYPE).equals(CF_TYPE_SUPER) && columnPath.getSuper_column()!= null ;	
		}
		throw new InvalidRequestException("specified super column family not exist:"+cf);
	}

	
	
	private List<ColumnOrSuperColumn> getSoscList(List<Column> columns){
		ArrayList<ColumnOrSuperColumn> list = new ArrayList<ColumnOrSuperColumn>(columns.size());
		for(Column col : columns){
			list.add(new ColumnOrSuperColumn(col , null ));
		}
		return list;
	}
	
	private List<Column>  getColumnList(List<ColumnOrSuperColumn> columns){
		ArrayList<Column> list = new ArrayList<Column>(columns.size());
		for(ColumnOrSuperColumn col : columns){
			list.add(col.getColumn());
		}
		return list;
	}

	
	private List<ColumnOrSuperColumn> getSoscSuperList(List<SuperColumn> columns){
		ArrayList<ColumnOrSuperColumn> list = new ArrayList<ColumnOrSuperColumn>(columns.size());
		for(SuperColumn col : columns){
			list.add(new ColumnOrSuperColumn(null , col ));
		}
		return list;
	}
	
	private List<SuperColumn>  getSuperColumnList(List<ColumnOrSuperColumn> columns){
		ArrayList<SuperColumn> list = new ArrayList<SuperColumn>(columns.size());
		for(ColumnOrSuperColumn col : columns){
			list.add(col.getSuper_column());
		}
		return list;
	}
	
	
	private ColumnOrSuperColumn getSosc(Column column , SuperColumn superColumn){
		return new ColumnOrSuperColumn( column , superColumn );
	}
	
	
	
	
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


	// consistency level
	private ConsistencyLevel consistencyLevel;


	


}
