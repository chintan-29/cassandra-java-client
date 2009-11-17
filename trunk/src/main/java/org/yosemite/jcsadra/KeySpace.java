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
package org.yosemite.jcsadra;

import java.util.List;
import java.util.Map;

import org.apache.cassandra.service.Column;
import org.apache.cassandra.service.ColumnParent;
import org.apache.cassandra.service.ColumnPath;
import org.apache.cassandra.service.InvalidRequestException;
import org.apache.cassandra.service.NotFoundException;
import org.apache.cassandra.service.SlicePredicate;
import org.apache.cassandra.service.SuperColumn;
import org.apache.cassandra.service.UnavailableException;
import org.apache.thrift.TException;


/**
 * KeySpace object, fire operation to really cassandra server , this 
 * was just a simple wrap for Cassandra.Client interface, about
 * each function's detail further please refer to Cassandra Thrift define 
 * file.
 * 
 * The main target for this KeySpace object is to make Cassandra call more 
 * simple and easy to use.
 * 
 * @author dayong
 */
public interface KeySpace {

	/**
	 * Get the Column at the given column_path. If no value is
	 * present, NotFoundException is thrown. (This is the only method that can
	 * throw an exception under non-failure conditions.)
	 * 
	 * @param key
	 * @param column_path
	 * @return
	 * @throws InvalidRequestException
	 * @throws NotFoundException
	 * @throws UnavailableException
	 * @throws TException
	 */
    public Column getColumn(String key, ColumnPath column_path)
			throws InvalidRequestException, NotFoundException,
			UnavailableException, TException;

    
    
	/**
	 * Get the SuperColumn at the given column_path. If no value is
	 * present, NotFoundException is thrown. (This is the only method that can
	 * throw an exception under non-failure conditions.)
	 * by default will return column with native order and return list is unlimit
	 * 
	 * @param key
	 * @param column_path
	 * @return
	 * @throws InvalidRequestException
	 * @throws NotFoundException
	 * @throws UnavailableException
	 * @throws TException
	 */
	public SuperColumn getSuperColumn(String key, ColumnPath column_path)
			throws InvalidRequestException, NotFoundException,
			UnavailableException, TException;
	
	
	
	/**
	 * Get the SuperColumn at the given column_path. If no value is
	 * present, NotFoundException is thrown. (This is the only method that can
	 * throw an exception under non-failure conditions.)
	 * 
	 * @param key
	 * @param columnPath
	 * @param reversed    the result Column sort
	 * @param size     the result column size
	 * @return
	 * @throws InvalidRequestException
	 * @throws NotFoundException
	 * @throws UnavailableException
	 * @throws TException
	 */
	public SuperColumn getSuperColumn(String key, ColumnPath columnPath,
			boolean reversed, int size) throws InvalidRequestException,
			NotFoundException, UnavailableException, TException;

	/**
	 * Get the group of columns contained by column_parent (either a
	 * ColumnFamily name or a !ColumnFamily/!SuperColumn name pair) specified by
	 * the given predicate. If no matching values are found, an empty list is
	 * returned.
	 * 
	 * @param key
	 * @param column_parent
	 * @param predicate
	 * @return 
	 * @throws InvalidRequestException
	 * @throws NotFoundException
	 * @throws UnavailableException
	 * @throws TException
	 */
    public List<Column> getSlice(String key,
			ColumnParent column_parent, SlicePredicate predicate)
			throws InvalidRequestException, NotFoundException,
			UnavailableException, TException;
    
    
    /**
     * Get the group of superColumn contained by column_parent (either a
	 * ColumnFamily name or a !ColumnFamily/!SuperColumn name pair) specified by
	 * the given predicate. If no matching values are found, an empty list is
	 * returned.
     * 
     * TODO this function need to do some test, maybe can not work.
     * @param key
     * @param column_parent
     * @param predicate
     * @return
     * @throws InvalidRequestException
     * @throws NotFoundException
     * @throws UnavailableException
     * @throws TException
     */
    public List<SuperColumn> getSuperSlice(String key,
			ColumnParent column_parent, SlicePredicate predicate)
			throws InvalidRequestException, NotFoundException,
			UnavailableException, TException;

	/**
	 * Perform a get for column_path in parallel on the given list<string> keys.
	 * The return value maps keys to the Column found. If no value
	 * corresponding to a key is present, the key will still be in the map, but
	 * both the column and super_column references of the ColumnOrSuperColumn
	 * object it maps to will be null.
	 * 
	 * @param keys
	 * @param column_path
	 * @return
	 * @throws InvalidRequestException
	 * @throws UnavailableException
	 * @throws TException
	 */
    public Map<String, Column> multigetColumn(List<String> keys,
			ColumnPath column_path) throws InvalidRequestException,
			UnavailableException, TException;

	/**
	 * Perform a get for column_path in parallel on the given list<string> keys.
	 * The return value maps keys to the ColumnOrSuperColumn found. If no value
	 * corresponding to a key is present, the key will still be in the map, but
	 * both the column and super_column references of the ColumnOrSuperColumn
	 * object it maps to will be null.
	 * 
	 * @param keys
	 * @param column_path
	 * @return
	 * @throws InvalidRequestException
	 * @throws UnavailableException
	 * @throws TException
	 */
    public Map<String, SuperColumn> multigetSuperColumn(List<String> keys,
			ColumnPath column_path) throws InvalidRequestException,
			UnavailableException, TException;
    
    
    /**
     * Perform a get for column_path in parallel on the given list<string> keys.
	 * The return value maps keys to the ColumnOrSuperColumn found. If no value
	 * corresponding to a key is present, the key will still be in the map, but
	 * both the column and super_column references of the ColumnOrSuperColumn
	 * object it maps to will be null.
	 * 
     * @param keys
     * @param columnPath
     * @param reversed
     * @param size
     * @return
     * @throws InvalidRequestException
     * @throws UnavailableException
     * @throws TException
     */
    public Map<String, SuperColumn> multigetSuperColumn(List<String> keys,
			ColumnPath columnPath , boolean reversed , int size) throws InvalidRequestException,
			UnavailableException, TException ;

	/**
	 * Performs a get_slice for column_parent and predicate for the given keys
	 * in parallel.
	 * 
	 * @param keys
	 * @param column_parent
	 * @param predicate
	 * @return
	 * @throws InvalidRequestException
	 * @throws UnavailableException
	 * @throws TException
	 */
    public Map<String, List<Column>> multigetSlice(
			List<String> keys, ColumnParent column_parent,
			SlicePredicate predicate) throws InvalidRequestException,
			UnavailableException, TException;

	/**
	 * Performs a get_slice for column_parent and predicate for the given keys
	 * in parallel.
	 * 
	 * @param keys
	 * @param column_parent
	 * @param predicate
	 * @return
	 * @throws InvalidRequestException
	 * @throws UnavailableException
	 * @throws TException
	 */
    public Map<String, List<SuperColumn>> multigetSuperSlice(
			List<String> keys, ColumnParent column_parent,
			SlicePredicate predicate) throws InvalidRequestException,
			UnavailableException, TException;


    
    /**
     * 
     * @param keyspace
     * @param key
     * @param column_path
     * @param value
     * @throws InvalidRequestException
     * @throws UnavailableException
     * @throws TException
     */
    public void insert( String key, ColumnPath column_path,
			byte[] value) throws InvalidRequestException,
			UnavailableException, TException;

	/**
	 * Insert Columns or SuperColumns across different Column Families for the
	 * same row key. batch_mutation is a map<string, list<BaseColumn>>
	 * -- a map which pairs column family names with the relevant
	 * ColumnOrSuperColumn objects to insert.
	 * 
	 * @param keyspace
	 * @param key
	 * @param cfmap
	 * @throws InvalidRequestException
	 * @throws UnavailableException
	 * @throws TException
	 */
    public void batchInsert(String key,
			Map<String, List<Column>> cfmap , Map<String, List<SuperColumn>> superColumnMap )
			throws InvalidRequestException, UnavailableException, TException;

	/**
	 * Remove data from the row specified by key at the granularity specified by
	 * column_path, and the given timestamp. Note that all the values in
	 * column_path besides column_path.column_family are truly optional: you can
	 * remove the entire row by just specifying the ColumnFamily, or you can
	 * remove a SuperColumn or a single Column by specifying those levels too.
	 * 
	 * @param key
	 * @param column_path
	 * @throws InvalidRequestException
	 * @throws UnavailableException
	 * @throws TException
	 */
    public void remove(String key, ColumnPath column_path)
			throws InvalidRequestException, UnavailableException, TException;
    
    
    /**
     * get describe of specified keyspace
     * @return
     * @throws NotFoundException
     * @throws TException
     */
    public Map<String, Map<String, String>> describeKeyspace()
			throws NotFoundException, TException;

    
    
	/**
	 * Counts the columns present in column_parent.
	 * 
	 * @param key
	 * @param column_parent
	 * @return
	 * @throws InvalidRequestException
	 * @throws UnavailableException
	 * @throws TException
	 */
    public int getCount(String key, ColumnParent column_parent)
			throws InvalidRequestException, UnavailableException, TException;

	/**
	 * Returns a list of keys starting with start, ending with finish (both
	 * inclusive), and at most count long. The empty string ("") can be used as
	 * a sentinel value to get the first/last existing key. (The semantics are
	 * similar to the corresponding components of SliceRange.) This method is
	 * only allowed when using an order-preserving partitioner.
	 * 
	 * @param keyspace
	 * @param column_family
	 * @param start
	 * @param finish
	 * @param count
	 * @return
	 * @throws InvalidRequestException
	 * @throws UnavailableException
	 * @throws TException
	 */
    public List<String> getKeyRange(String column_family,
			String start, String finish, int count)
			throws InvalidRequestException, UnavailableException, TException;


}
