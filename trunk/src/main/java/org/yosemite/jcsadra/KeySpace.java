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

import org.apache.cassandra.service.Cassandra;
import org.apache.cassandra.service.ColumnOrSuperColumn;
import org.apache.cassandra.service.ColumnParent;
import org.apache.cassandra.service.ColumnPath;
import org.apache.cassandra.service.InvalidRequestException;
import org.apache.cassandra.service.NotFoundException;
import org.apache.cassandra.service.SlicePredicate;
import org.apache.cassandra.service.UnavailableException;
import org.apache.thrift.TException;


/**
 * KeySpace object, fire operation to really cassandra server
 * @author sanli
 */
public interface KeySpace {
	
    public ColumnOrSuperColumn get(String key, ColumnPath column_path) throws InvalidRequestException, NotFoundException, UnavailableException, TException;

    public List<ColumnOrSuperColumn> get_slice(String key, ColumnParent column_parent, SlicePredicate predicate, int consistency_level) throws InvalidRequestException, NotFoundException, UnavailableException, TException;

    public Map<String,ColumnOrSuperColumn> multiget(List<String> keys, ColumnPath column_path, int consistency_level) throws InvalidRequestException, UnavailableException, TException;

    public Map<String,List<ColumnOrSuperColumn>> multiget_slice(List<String> keys, ColumnParent column_parent, SlicePredicate predicate, int consistency_level) throws InvalidRequestException, UnavailableException, TException;

    public int get_count(String key, ColumnParent column_parent, int consistency_level) throws InvalidRequestException, UnavailableException, TException;

    public List<String> get_key_range(String keyspace, String column_family, String start, String finish, int count, int consistency_level) throws InvalidRequestException, UnavailableException, TException;

    public void insert(String keyspace, String key, ColumnPath column_path, byte[] value, long timestamp, int consistency_level) throws InvalidRequestException, UnavailableException, TException;

    public void batch_insert(String keyspace, String key, Map<String,List<ColumnOrSuperColumn>> cfmap, int consistency_level) throws InvalidRequestException, UnavailableException, TException;

    public void remove(String keyspace, String key, ColumnPath column_path, long timestamp, int consistency_level) throws InvalidRequestException, UnavailableException, TException;
    
    public Map<String,Map<String,String>> describe_keyspace(String keyspace) throws NotFoundException, TException;


}
