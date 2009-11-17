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

import org.apache.cassandra.service.Cassandra;
import org.apache.cassandra.service.NotFoundException;
import org.apache.thrift.TException;

import java.lang.IllegalArgumentException; 
import java.util.HashMap;
import java.util.List;


/**
 * Client object, it was the root of Cassandra client object.
 * 
 * The structure as bellow:
 * 
 * CassandraClient   -->   KeySpace  -->  Column
 *                                   -->  SuperColumn
 *                                   -->  ColumnPath
 *                                   -->  SuperColumnPath
 *    
 * 
 * When the client object was created, it will read the define info from 
 * Cassandra meta table, all different CSD server should have exactly same
 * configureation.
 * 
 * User can call getKeySpace(String spaceName) to get space, if the space not
 * exist,will throw out exception.
 * 
 * After got the KeySpace object, user can fire really query/insert function on 
 * it.
 * 
 * The KeySpace object will be the core logic of whole Cassandra Client, just like
 * the PrepareStatement does in JDBC client.
 * 
 * 
 * @author dayong
 */
public interface CassandraClient {
	
	
	/**
	 * Conssitency Level when do read/write request
	 * ZERO : will not wait any result, this is most high performance,
	 *    but lower consistency, you should only using it within condition
	 *    like: record log.
	 * ONE : will wait for one client to send response, then at least on 
	 *    write/read was success, if the replica > 2, some data maybe was inconsistency.
	 *    
	 *
	 */
	enum ConsistencyLevel{
		  ZERO, ONE ,QUORUM, ALL
	}
	
	/**
	 * return the under line cassandra thrift object, all remote call
	 * will send to this client on the earth.
	 * 
	 * Because of the Cassandra.Client object was not thread safe, so
	 * if you want direct using the Cassandra.Client, please make sure
	 * it will not compact with other thread. 
	 * @return
	 */
	public Cassandra.Client getCassandra() ;
	
	
	
	/**
	 * return given key space, if keySpaceName not exist, will throw out 
	 * exception. the thread safe status depends on implement, please
	 * get the detail info from specify implement's documents. 
	 * 
	 * keySpace object need not manual closed by caller. 
	 * 
	 * the default consitencyLevel will be 
	 * 
	 * @param keySpaceName
	 * @return if the key spaceName exist, will return relate KeySpace object
	 * @throws IllegalArgumentException if keySpaceName not exit, will throw 
	 *   out IllegalArgumentException.
	 * @throws TException 
	 * @throws NotFoundException 
	 */
	public KeySpace getKeySpace(String keySpaceName) throws IllegalArgumentException, NotFoundException, TException ;
	
	
	/**
	 * 
	 * @param keySpaceName
	 * @param consitencyLevel
	 * @return
	 * @throws IllegalArgumentException
	 * @throws TException 
	 * @throws NotFoundException 
	 */
	public KeySpace getKeySpace(String keySpaceName , ConsistencyLevel consitencyLevel) throws IllegalArgumentException, NotFoundException, TException ;
	
	
	
	/**
	 * return some string property
	 * property include:
	 * 
	 * "cluster name": cluster name;
	 * "config file" : all config file content, if need you can try to explain it.
	 * "token map" :  get the token map from local gossip protocal.
	 * 
	 * @param propertyName
	 * @return
	 * @throws TException 
	 */
	public String getStringProperty(String propertyName) throws TException ;
	
	
	/**
	 * return default consistency level, all keyspace create from this client
	 * will have this consistency level.
	 * @return
	 */
	public ConsistencyLevel getDefaultConsistencyLevel();

	
	/**
	 * set the default consistency level
	 * @param defaultConsistencyLevel
	 */
	public void setDefaultConsistencyLevel(ConsistencyLevel defaultConsistencyLevel);
	
	
	/**
	 * return all keyspaces name of this client.
	 * @return
	 */
	public List<String> getKeyspaces() ;

	
	/**
	 * return target server cluster name
	 * @return
	 */
	public String getClusterName() ;

	
	/**
	 * return token name
	 * @return
	 */
	public String getTokenMap() ;

	
	/**
	 * return content file content
	 * @return
	 */
	public String getConfigFile() ;

	
}
