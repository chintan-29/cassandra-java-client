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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.cassandra.service.Cassandra;
import org.apache.cassandra.service.NotFoundException;
import org.apache.thrift.TException;
import org.yosemite.jcsadra.CassandraClient;
import org.yosemite.jcsadra.KeySpace;

public class CassandraClientImpl implements CassandraClient {

	public final static String PROP_CLUSTER_NAME = "cluster name";
	public final static String PROP_CONFIG_FILE = "config file";
	public final static String PROP_TOKEN_MAP = "token map";
	public final static String PROP_KEYSPACE = "keyspaces";
	public final static String PROP_VERSION = "version";
	

	/**
	 * constuctor function, for provent other one create Client object by hand,
	 * so make it protected.
	 * 
	 * @param cassandra
	 * @throws TException
	 */
	protected CassandraClientImpl(Cassandra.Client cassandra) {
		this._cassandra = cassandra;
	}

	/**
	 * init client object, get clusterName & Keyspaces & configuration files
	 * from server
	 * 
	 * @throws TException
	 */
	protected void init() throws TException {
		clusterName = _cassandra.get_string_property(PROP_CLUSTER_NAME);
		tokenMap = _cassandra.get_string_property(PROP_TOKEN_MAP);
		configFile = _cassandra.get_string_property(PROP_CONFIG_FILE);
		keyspaces = _cassandra.get_string_list_property(PROP_KEYSPACE);
		keyspaceMap = new HashMap<String, KeySpace>(keyspaces.size() * 2 ) ;
		serverVersion = _cassandra.get_string_property(PROP_VERSION) ;
	}

	/**
	 * return Cassandra.Client
	 */
	public Cassandra.Client getCassandra() {
		return _cassandra;
	}

	/**
	 * get the KeySpace object, if the keyspace object already exist, will
	 * return to user, if keyspace not exist will create a new one, and feed
	 * keyspace description from server.
	 * 
	 * when thrift happend error will throw out TException.
	 * 
	 * Before send the request, we already check the existence of keyspace, so
	 * should not throw out NotFoundException, but you better to catch it at all.
	 *  
	 * @throws TException 
	 * @throws NotFoundException 
	 */
	public KeySpace getKeySpace(String keySpaceName)
			throws IllegalArgumentException, NotFoundException, TException   {
		return getKeySpace(keySpaceName , this.defaultConsistencyLevel );
	}

	public KeySpace getKeySpace(String keySpaceName,
			ConsistencyLevel consitencyLevel) throws IllegalArgumentException,
			NotFoundException, TException {
		if(keyspaceMap.containsKey(keySpaceName)){
			return keyspaceMap.get(keySpaceName);
		}
		
		if(keyspaces.contains(keySpaceName)){
			Map<String, Map<String, String>> keyspaceDesc = _cassandra
					.describe_keyspace(keySpaceName);
			KeySpaceImpl ks = new KeySpaceImpl(this , keySpaceName , keyspaceDesc , consitencyLevel);			
			keyspaceMap.put( keySpaceName , ks ) ;
			return ks ;
		}else{
			throw new IllegalArgumentException("request key space not exist, keyspaceName=" + keySpaceName);
		}
	}

	public String getStringProperty(String propertyName) throws TException {
		return _cassandra.get_string_property(propertyName);
	}

	
	
	public ConsistencyLevel getDefaultConsistencyLevel() {
		return defaultConsistencyLevel;
	}

	public void setDefaultConsistencyLevel(ConsistencyLevel defaultConsistencyLevel) {
		this.defaultConsistencyLevel = defaultConsistencyLevel;
	}

	public List<String> getKeyspaces() {
		return keyspaces;
	}


	public String getClusterName() {
		return clusterName;
	}

	public String getTokenMap() {
		return tokenMap;
	}

	public String getConfigFile() {
		return configFile;
	}

	public String getServerVersion() {
		return serverVersion;
	}

	
	
	// ======================== private field & method  ========================
	private List<String> keyspaces;
	
	private HashMap<String, KeySpace> keyspaceMap ;

	private String clusterName;

	private String tokenMap;

	private String configFile;

	private String serverVersion;


	// thrift object
	Cassandra.Client _cassandra;
	
	// consistencyLevel 
	ConsistencyLevel defaultConsistencyLevel = CassandraClient.ConsistencyLevel.QUORUM ;

}
