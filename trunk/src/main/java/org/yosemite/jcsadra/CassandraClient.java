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
import org.apache.cassandra.service.ColumnOrSuperColumn;
import org.apache.cassandra.service.ColumnPath;
import org.apache.cassandra.service.InvalidRequestException;
import org.apache.cassandra.service.NotFoundException;
import org.apache.cassandra.service.UnavailableException;
import org.apache.cassandra.service.Cassandra.Client;
import org.apache.thrift.TException;


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
 * @author sanli
 */
public class CassandraClient {
	

	/**
	 * constuctor function, for provent other one create Client object by
	 * hand, so make it protected.
	 * @param cassandra
	 */
	protected CassandraClient(Cassandra.Client cassandra){
		this._cassandra = cassandra ;
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
	public Cassandra.Client getCassandra() {
		return _cassandra;
	}
	
	
	
	
	
	// thrift object
	Cassandra.Client _cassandra ;
	
	
	
	
	
}
