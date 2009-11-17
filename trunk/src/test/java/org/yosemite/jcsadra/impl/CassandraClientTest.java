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

import static org.junit.Assert.assertTrue;

import static org.junit.Assert.fail;

import java.util.Map;

import org.apache.cassandra.service.Cassandra;
import org.apache.cassandra.service.NotFoundException;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.junit.BeforeClass;
import org.junit.Test;
import org.yosemite.jcsadra.CassandraClient;
import org.yosemite.jcsadra.KeySpace;


/**
 * Test CassandraClientImpl, this test need start a really exist Cassandra server
 * in localhost:9106, if can not found the server, skipNeedServerCase test will be skiped.
 * 
 * @author dayong
 *
 */
public class CassandraClientTest extends ServerBasedTestCase{
	


	@Test
	public void testInit() throws TException{
		if(skipNeedServerCase){
			return ;
		}
		
		CassandraClientImpl cclient = createClient();
		assertTrue(cclient.getClusterName() != null);
		assertTrue(cclient.getClusterName().equals("Test Cluster"));
		assertTrue(cclient.getKeyspaces().size() > 0 );
		assertTrue(cclient.getKeyspaces().contains("Keyspace1") );
		assertTrue(cclient.getKeyspaces().contains("system") );
		assertTrue(cclient.getConfigFile() != null ) ;
		assertTrue(cclient.getTokenMap() != null );
		closeClient(cclient);
	}
	
	@Test
	public void testGetKeySpaces() throws TException, IllegalArgumentException, NotFoundException{
		if(skipNeedServerCase){
			return ;
		}
		
		CassandraClientImpl cclient = createClient();
		
		KeySpace ks = cclient.getKeySpace("Keyspace1");
		Map<String , Map<String , String>> cfs = ks.describeKeyspace() ;
		assertTrue(cfs.size() > 0 );
		assertTrue(cfs.containsKey("Standard1") );
		
		closeClient(cclient);		
	}
	
	
	
	
	
	
	
	

}
