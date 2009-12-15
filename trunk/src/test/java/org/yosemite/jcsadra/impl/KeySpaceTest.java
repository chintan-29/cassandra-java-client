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

import static org.junit.Assert.*;


import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import org.apache.cassandra.service.SuperColumn;
import org.apache.cassandra.service.Cassandra;
import org.apache.cassandra.service.Column;
import org.apache.cassandra.service.ColumnParent;
import org.apache.cassandra.service.ColumnPath;
import org.apache.cassandra.service.InvalidRequestException;
import org.apache.cassandra.service.NotFoundException;
import org.apache.cassandra.service.SlicePredicate;
import org.apache.cassandra.service.SliceRange;
import org.apache.cassandra.service.UnavailableException;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.junit.BeforeClass;
import org.junit.Test;
import org.yosemite.jcsadra.CassandraClient;
import org.yosemite.jcsadra.CassandraClientPool;
import org.yosemite.jcsadra.KeySpace;


/**
 * Target test space as bellow:
 * <Keyspaces>
    <Keyspace Name="Keyspace1">
      <ColumnFamily CompareWith="BytesType"
                    Name="Standard1"
                    FlushPeriodInMinutes="60"/>
      <ColumnFamily CompareWith="UTF8Type" Name="Standard2"/>
      <ColumnFamily CompareWith="TimeUUIDType" Name="StandardByUUID1"/>
      <ColumnFamily ColumnType="Super"
                    CompareWith="UTF8Type"
                    CompareSubcolumnsWith="UTF8Type"
                    Name="Super1"/>
    </Keyspace>
  </Keyspaces>
 * @author dayong
 *
 */
public class KeySpaceTest extends ServerBasedTestCase {
	
	public static CassandraClientPool pool ;
	
	@BeforeClass
	public  static void createClientPool(){
		pool = new SimpleCassandraClientPool("localhost",9160);
	}
	
	@Test
	public void testInsertAndGetAndRemove() throws IllegalArgumentException,
			NoSuchElementException, IllegalStateException, NotFoundException,
			Exception {
		if(skipNeedServerCase){
			return ;
		}
		
		CassandraClient cl = pool.getClient() ;
		KeySpace ks = cl.getKeySpace("Keyspace1") ;
		
		// insert value
		ColumnPath cp = new ColumnPath("Standard1" , null, "testInsertAndGetAndRemove".getBytes("utf-8")); 
		for(int i = 0 ; i < 100 ; i++){
			ks.insert("testInsertAndGetAndRemove_"+i, cp , ("testInsertAndGetAndRemove_value_"+i).getBytes("utf-8"));
		}
		
		//get value
		for(int i = 0 ; i < 100 ; i++){
			Column col = ks.getColumn("testInsertAndGetAndRemove_"+i, cp);
			String value = new String(col.getValue(),"utf-8") ;
			assertTrue( value.equals("testInsertAndGetAndRemove_value_"+i) ) ;
		}
		
		
		//remove value
		for(int i = 0 ; i < 100 ; i++){
			ks.remove("testInsertAndGetAndRemove_"+i, cp);
		}
		
		
		try{
			ks.remove("testInsertAndGetAndRemove_not_exist", cp);
		}catch(Exception e){
			fail("remove not exist row should not throw exceptions");
		}
		
		//get already removed value
		for(int i = 0 ; i < 100 ; i++){
			try{
				Column col = ks.getColumn("testInsertAndGetAndRemove_"+i, cp);
				fail("the value should already being deleted");
			}catch(NotFoundException e){
				
			}catch(Exception e){
				fail("throw out other exception, should be NotFoundException." + e.toString() );
			}
		}
		
		pool.releaseClient(cl) ;
		pool.close() ;
		
	}
	
	
	@Test
	public void testBatchInsertColumn() throws IllegalArgumentException,
			NoSuchElementException, IllegalStateException, NotFoundException,
			TException, Exception {
		if(skipNeedServerCase){
			return ;
		}
		
		KeySpace ks = pool.getClient().getKeySpace("Keyspace1") ;
		
		for(int i = 0 ; i < 10 ; i++){
			HashMap<String , List<Column>> cfmap = new HashMap<String , List<Column>>(10);
			ArrayList<Column> list = new ArrayList<Column>(100); 
			for(int j = 0 ; j < 10 ;  j++ ){
				Column col = new Column(("testBatchInsertColumn_"+j).getBytes("utf-8") ,
						("testBatchInsertColumn_value_"+j).getBytes("utf-8"), System.currentTimeMillis());
				list.add(col);				
			}			
			cfmap.put("Standard1" , list);
			//cfmap.put("Standard2", (List)list.clone());
			
			ks.batchInsert("testBatchInsertColumn_"+i, cfmap , null);
		}
		
		//get value
		for(int i = 0 ; i < 10 ; i++){
			for(int j = 0 ; j < 10 ;  j++){
				ColumnPath cp = new ColumnPath("Standard1" , null, ("testBatchInsertColumn_"+j).getBytes("utf-8"));
				//ColumnPath cp1 = new ColumnPath("Standard2" , null, ("testBatchInsertAndGetAndRemove_"+j).getBytes("utf-8"));
				Column col = ks.getColumn("testBatchInsertColumn_"+i, cp);
				String value = new String(col.getValue(),"utf-8") ;
				assertTrue( value.equals("testBatchInsertColumn_value_"+j) ) ;
				
			}			
		}
		
		//remove value
		for(int i = 0 ; i < 100 ; i++){
			for(int j = 0 ; j < 10 ;  j++){
				ColumnPath cp = new ColumnPath("Standard1" , null, ("testBatchInsertColumn_"+j).getBytes("utf-8"));
				ks.remove("testBatchInsertColumn_"+i, cp);
			}
		}
	}


	@Test
	public void testGetCount() throws IllegalArgumentException,
			NoSuchElementException, IllegalStateException, NotFoundException,
			TException, Exception {
		if(skipNeedServerCase){
			return ;
		}
		
		
		KeySpace ks = pool.getClient().getKeySpace("Keyspace1") ;
		
		// insert value
		 
		for(int i = 0 ; i < 100 ; i++){
			ColumnPath cp = new ColumnPath("Standard1" , null, ("testInsertAndGetAndRemove_"+i).getBytes("utf-8"));
			ks.insert("testGetCount" , cp , ("testInsertAndGetAndRemove_value_"+i).getBytes("utf-8"));
		}
		
		//get value
		ColumnParent clp =  new ColumnParent("Standard1", null);
		int count = ks.getCount("testGetCount", clp);
		assertTrue(count == 100);	
		
		
		ColumnPath cp = new ColumnPath("Standard1" , null, null);
		ks.remove("testGetCount", cp);
		

	}

	/**
	 * my server can not support this query, so skip test
	 */
	@Test
	public void testGetKeyRange() {
		if(skipNeedServerCase || true){
			return ;
		}
		
		fail("Not yet implemented");
	}

	
	
	
	@Test
	public void testGetSlice() throws IllegalArgumentException,
			NoSuchElementException, IllegalStateException, NotFoundException,
			TException, Exception {
		if(skipNeedServerCase){
			return ;
		}
		
		KeySpace ks = pool.getClient().getKeySpace("Keyspace1") ;
		
		// insert value		 
		ArrayList<String> columnnames = new ArrayList<String>(100);
		for(int i = 0 ; i < 100 ; i++){
			ColumnPath cp = new ColumnPath("Standard2" , null, ("testGetSlice_"+i).getBytes("utf-8"));
			ks.insert("testGetSlice" , cp , ("testGetSlice_Value_"+i).getBytes("utf-8"));
			columnnames.add("testGetSlice_"+i);
		}
		
		//get value
		ColumnParent clp =  new ColumnParent("Standard2", null);
		SliceRange sr = new SliceRange(new byte[0] , new byte[0], false , 150 );
		SlicePredicate  sp = new SlicePredicate(null , sr );
		List<Column> cols = ks.getSlice("testGetSlice", clp , sp ) ;
		
		assertTrue(cols!=null);
		assertTrue(cols.size()==100) ;
		
		
		Collections.sort(columnnames);
		ArrayList<String> gotlist = new ArrayList<String>(100) ;
		for(int i = 0 ; i< 100 ; i++){
			gotlist.add(new String(cols.get(i).getName()) ) ;
		}
		assertTrue( gotlist.equals( columnnames ) ) ;
		
		ColumnPath cp = new ColumnPath("Standard2" , null, null);
		ks.remove("testGetSlice_", cp);
		
		
	}

	
	@Test
	public void testGetSuperSlice() throws IllegalArgumentException,
			NoSuchElementException, IllegalStateException, NotFoundException,
			TException, Exception {
		if(skipNeedServerCase){
			return ;
		}
		
		KeySpace ks = pool.getClient().getKeySpace("Keyspace1") ;
		
		// insert value		 
		for(int i = 0 ; i < 100 ; i++){
			ColumnPath cp = new ColumnPath("Super1", "SuperColumn_1"
					.getBytes("utf-8"), ("testGetSuperSlice_" + i).getBytes("utf-8"));
			
			ColumnPath cp2 = new ColumnPath("Super1", "SuperColumn_2"
					.getBytes("utf-8"), ("testGetSuperSlice_" + i).getBytes("utf-8"));
			
			ks.insert("testGetSuperSlice" , cp , ("testGetSuperSlice_Value_"+i).getBytes("utf-8"));
			ks.insert("testGetSuperSlice" , cp2 , ("testGetSuperSlice_Value_"+i).getBytes("utf-8"));
		}
		
		//get value
		ColumnParent clp =  new ColumnParent("Super1", null);
		SliceRange sr = new SliceRange(new byte[0] , new byte[0], false , 150 );
		SlicePredicate  sp = new SlicePredicate(null , sr );
		List<SuperColumn> cols = ks.getSuperSlice("testGetSuperSlice", clp , sp ) ;
		
		assertTrue(cols!=null);
		assertTrue(cols.size()==2) ;
		
		ColumnPath cp = new ColumnPath("Super1" , null, null);
		ks.remove("testGetSuperSlice", cp);
	}

	
	
	@Test
	public void testGetSuperColumn() throws IllegalArgumentException,
			NoSuchElementException, IllegalStateException, NotFoundException,
			TException, Exception {
		if(skipNeedServerCase){
			return ;
		}
		KeySpace ks = pool.getClient().getKeySpace("Keyspace1");

		HashMap<String, List<SuperColumn>> cfmap = new HashMap<String, List<SuperColumn>>(
				10);
		ArrayList<Column> list = new ArrayList<Column>(100);
		for (int j = 0; j < 10; j++) {
			Column col = new Column(("testGetSuperColumn_" + j)
					.getBytes("utf-8"), ("testGetSuperColumn_value_" + j)
					.getBytes("utf-8"), System.currentTimeMillis());
			list.add(col);
		}
		ArrayList<SuperColumn> superlist = new ArrayList<SuperColumn>(1);
		SuperColumn sc = new SuperColumn("SuperColumn_1".getBytes("utf-8") , list);
		superlist.add(sc);
		cfmap.put("Super1", superlist);
		ks.batchInsert("testGetSuperColumn_1", null, cfmap);
		
		ColumnPath cp = new ColumnPath( "Super1" ,  "SuperColumn_1".getBytes("utf-8") , null );
		try{
			SuperColumn superc = ks.getSuperColumn("testGetSuperColumn_1", cp);

			assertTrue(superc != null);
			assertTrue(superc.getColumns() != null);
			assertTrue(superc.getColumns().size() == 10);
		}finally{
			ks.remove("testGetSuperColumn_1", cp);
		}
	}
	

	
	
	@Test
	public void testMultigetColumn() throws IllegalArgumentException,
			NoSuchElementException, IllegalStateException, NotFoundException,
			TException, Exception {
		
		if(skipNeedServerCase){
			return ;
		}
		
		KeySpace ks = pool.getClient().getKeySpace("Keyspace1") ;
		
		// insert value
		ColumnPath cp = new ColumnPath("Standard1" , null, "testMultigetColumn".getBytes("utf-8"));
		ArrayList<String>  keys =  new ArrayList<String>(100);
		for(int i = 0 ; i < 100 ; i++){
			ks.insert("testMultigetColumn_"+i, cp , ("testMultigetColumn_value_"+i).getBytes("utf-8"));
			keys.add("testMultigetColumn_"+i);
		}
		
		
		//get value
		 Map<String, Column> ms = ks.multigetColumn(keys, cp) ;
		for(int i = 0 ; i < 100 ; i++){
			Column cl = ms.get(keys.get(i));
			assertTrue( cl != null ) ;
			assertTrue( new String(cl.getValue(), "utf-8").equals("testMultigetColumn_value_"+i));
		}
		
		
		//remove value
		for(int i = 0 ; i < 100 ; i++){
			ks.remove("testMultigetColumn_"+i, cp);
		}
	}

	
	
	@Test
	public void testMultigetSuperColumn() throws IllegalArgumentException,
			NoSuchElementException, IllegalStateException, NotFoundException,
			TException, Exception {
		if(skipNeedServerCase){
			return ;
		}
		
		KeySpace ks = pool.getClient().getKeySpace("Keyspace1");

		HashMap<String, List<SuperColumn>> cfmap = new HashMap<String, List<SuperColumn>>(10);
		ArrayList<Column> list = new ArrayList<Column>(100);
		for (int j = 0; j < 10; j++) {
			Column col = new Column(("testMultigetSuperColumn_" + j)
					.getBytes("utf-8"), ("testMultigetSuperColumn_value_" + j)
					.getBytes("utf-8"), System.currentTimeMillis());
			list.add(col);
		}
		ArrayList<SuperColumn> superlist = new ArrayList<SuperColumn>(1);
		SuperColumn sc = new SuperColumn("SuperColumn_1".getBytes("utf-8") , list);
		superlist.add(sc);
		cfmap.put("Super1", superlist);
		ks.batchInsert("testMultigetSuperColumn_1", null, cfmap);
		
		ColumnPath cp = new ColumnPath( "Super1" ,  "SuperColumn_1".getBytes("utf-8") , null);
		try{
			List<String> keys = new ArrayList<String>();
			keys.add("testMultigetSuperColumn_1");
			
			// TODO seems the multigetSuperColumn was not implement, should using multigetSuperSlice()
			Map<String, SuperColumn> superc = ks.multigetSuperColumn(keys, cp);     

			assertTrue(superc != null);
			assertTrue(superc.size() == 1 );
			assertTrue(superc.get("testMultigetSuperColumn_1").columns.size() == 10);
		}finally{
			ks.remove("testMultigetSuperColumn_1", cp);
		}
	}

	
	

	
	
	@Test
	public void testMultigetSlice() throws IllegalArgumentException,
			NoSuchElementException, IllegalStateException, NotFoundException,
			TException, Exception {

		if(skipNeedServerCase){
			return ;
		}
		
		KeySpace ks = pool.getClient().getKeySpace("Keyspace1") ;
		
		// insert value
		ColumnPath cp = new ColumnPath("Standard1" , null, "testMultigetSlice".getBytes("utf-8"));
		ArrayList<String>  keys =  new ArrayList<String>(100);
		for(int i = 0 ; i < 100 ; i++){
			ks.insert("testMultigetSlice_"+i, cp , ("testMultigetSlice_value_"+i).getBytes("utf-8"));
			keys.add("testMultigetSlice_"+i);
		}
		
		
		//get value
		ColumnParent clp =  new ColumnParent("Standard1", null);
		SliceRange sr = new SliceRange(new byte[0] , new byte[0], false , 150 );
		SlicePredicate  sp = new SlicePredicate(null , sr );
		Map<String, List<Column>> ms = ks.multigetSlice(keys, clp , sp) ;
		for(int i = 0 ; i < 100 ; i++){
			List<Column> cl = ms.get(keys.get(i));
			assertTrue( cl != null ) ;
			assertTrue( cl.size() == 1);
			assertTrue( new String ( cl.get(0).getValue()).startsWith("testMultigetSlice_") );
		}
		
		
		//remove value
		for(int i = 0 ; i < 100 ; i++){
			ks.remove("testMultigetSlice_"+i, cp);
		}
	}

	
	
	@Test
	public void testMultigetSlice_1() throws IllegalArgumentException,
			NoSuchElementException, IllegalStateException, NotFoundException,
			TException, Exception {
		if(skipNeedServerCase){
			return ;
		}
		
		KeySpace ks = pool.getClient().getKeySpace("Keyspace1");

		HashMap<String, List<SuperColumn>> cfmap = new HashMap<String, List<SuperColumn>>(10);
		ArrayList<Column> list = new ArrayList<Column>(100);
		for (int j = 0; j < 10; j++) {
			Column col = new Column(("testMultigetSuperSlice_" + j)
					.getBytes("utf-8"), ("testMultigetSuperSlice_value_" + j)
					.getBytes("utf-8"), System.currentTimeMillis());
			list.add(col);
		}
		ArrayList<SuperColumn> superlist = new ArrayList<SuperColumn>(1);
		SuperColumn sc = new SuperColumn("SuperColumn_1".getBytes("utf-8") , list);
		SuperColumn sc2 = new SuperColumn("SuperColumn_2".getBytes("utf-8") , list);
		superlist.add(sc);
		superlist.add(sc2);
		cfmap.put("Super1", superlist);
		ks.batchInsert("testMultigetSuperSlice_1", null, cfmap);
		ks.batchInsert("testMultigetSuperSlice_2", null, cfmap);
		ks.batchInsert("testMultigetSuperSlice_3", null, cfmap);
		
		try{
			List<String> keys = new ArrayList<String>();
			keys.add("testMultigetSuperSlice_1");
			keys.add("testMultigetSuperSlice_2");
			keys.add("testMultigetSuperSlice_3");
			
			ColumnParent clp =  new ColumnParent("Super1", "SuperColumn_1".getBytes("utf-8"));
			SliceRange sr = new SliceRange(new byte[0] , new byte[0], false , 150 );
			SlicePredicate  sp = new SlicePredicate(null , sr );
			Map<String, List<Column>>  superc = ks.multigetSlice( keys, clp , sp );     // throw

			assertTrue(superc != null);
			assertTrue(superc.size() == 3 );
			List<Column> scls = superc.get("testMultigetSuperSlice_1") ;
			assertTrue(scls != null);
			assertTrue(scls.size()==10);
			
		}finally{
			// insert value
			ColumnPath cp = new ColumnPath("Super1" , null, null);
			ks.remove("testMultigetSuperSlice_1", cp);
			ks.remove("testMultigetSuperSlice_2", cp);
			ks.remove("testMultigetSuperSlice_3", cp);
		}
	}
	
	
	@Test
	public void testMultigetSuperSlice() throws IllegalArgumentException,
			NoSuchElementException, IllegalStateException, NotFoundException,
			TException, Exception {
		if(skipNeedServerCase){
			return ;
		}
		
		KeySpace ks = pool.getClient().getKeySpace("Keyspace1");

		HashMap<String, List<SuperColumn>> cfmap = new HashMap<String, List<SuperColumn>>(10);
		ArrayList<Column> list = new ArrayList<Column>(100);
		for (int j = 0; j < 10; j++) {
			Column col = new Column(("testMultigetSuperSlice_" + j)
					.getBytes("utf-8"), ("testMultigetSuperSlice_value_" + j)
					.getBytes("utf-8"), System.currentTimeMillis());
			list.add(col);
		}
		ArrayList<SuperColumn> superlist = new ArrayList<SuperColumn>(1);
		SuperColumn sc = new SuperColumn("SuperColumn_1".getBytes("utf-8") , list);
		SuperColumn sc2 = new SuperColumn("SuperColumn_2".getBytes("utf-8") , list);
		superlist.add(sc);
		superlist.add(sc2);
		cfmap.put("Super1", superlist);
		ks.batchInsert("testMultigetSuperSlice_1", null, cfmap);
		ks.batchInsert("testMultigetSuperSlice_2", null, cfmap);
		ks.batchInsert("testMultigetSuperSlice_3", null, cfmap);
		
		try{
			List<String> keys = new ArrayList<String>();
			keys.add("testMultigetSuperSlice_1");
			keys.add("testMultigetSuperSlice_2");
			keys.add("testMultigetSuperSlice_3");
			
			ColumnParent clp =  new ColumnParent("Super1", null );
			SliceRange sr = new SliceRange(new byte[0] , new byte[0], false , 150 );
			SlicePredicate  sp = new SlicePredicate(null , sr );
			Map<String, List<SuperColumn>>  superc = ks.multigetSuperSlice( keys, clp , sp );     // throw

			assertTrue(superc != null);
			assertTrue(superc.size() == 3 );
			List<SuperColumn> scls = superc.get("testMultigetSuperSlice_1") ;
			assertTrue(scls != null);
			assertTrue(scls.size()==2);
			assertTrue(scls.get(0).getColumns()!=null);
			assertTrue(scls.get(0).getColumns().size()==10);
			assertTrue(scls.get(0).getColumns().get(0).value!=null);
		}finally{
			// insert value
			ColumnPath cp = new ColumnPath("Super1" , null, null);
			ks.remove("testMultigetSuperSlice_1", cp);
		}
	}

	
	
	@Test
	public void testMultigetSuperSlice_1() throws IllegalArgumentException,
			NoSuchElementException, IllegalStateException, NotFoundException,
			TException, Exception {
		if(skipNeedServerCase){
			return ;
		}
		
		KeySpace ks = pool.getClient().getKeySpace("Keyspace1");

		HashMap<String, List<SuperColumn>> cfmap = new HashMap<String, List<SuperColumn>>(10);
		ArrayList<Column> list = new ArrayList<Column>(100);
		for (int j = 0; j < 10; j++) {
			Column col = new Column(("testMultigetSuperSlice_" + j)
					.getBytes("utf-8"), ("testMultigetSuperSlice_value_" + j)
					.getBytes("utf-8"), System.currentTimeMillis());
			list.add(col);
		}
		ArrayList<SuperColumn> superlist = new ArrayList<SuperColumn>(1);
		SuperColumn sc = new SuperColumn("SuperColumn_1".getBytes("utf-8") , list);
		SuperColumn sc2 = new SuperColumn("SuperColumn_2".getBytes("utf-8") , list);
		superlist.add(sc);
		superlist.add(sc2);
		cfmap.put("Super1", superlist);
		ks.batchInsert("testMultigetSuperSlice_1", null, cfmap);
		ks.batchInsert("testMultigetSuperSlice_2", null, cfmap);
		ks.batchInsert("testMultigetSuperSlice_3", null, cfmap);
		
		try{
			List<String> keys = new ArrayList<String>();
			keys.add("testMultigetSuperSlice_1");
			keys.add("testMultigetSuperSlice_2");
			keys.add("testMultigetSuperSlice_3");
			
			ColumnParent clp =  new ColumnParent("Super1", "SuperColumn_1".getBytes("utf-8"));
			SliceRange sr = new SliceRange(new byte[0] , new byte[0], false , 150 );
			SlicePredicate  sp = new SlicePredicate(null , sr );
			Map<String, List<SuperColumn>>  superc = ks.multigetSuperSlice( keys, clp , sp );     // throw

			assertTrue(superc != null);
			assertTrue(superc.size() == 3 );
			List<SuperColumn> scls = superc.get("testMultigetSuperSlice_1") ;
			assertTrue(scls != null);
			assertTrue(scls.size() == 1);
			assertTrue(scls.get(0).getColumns()!=null);
			assertTrue(scls.get(0).getColumns().size()==10);
			assertTrue(scls.get(0).getColumns().get(0).value!=null);
		}finally{
			// insert value
			ColumnPath cp = new ColumnPath("Super1" , null, null);
			ks.remove("testMultigetSuperSlice_1", cp);
		}
	}


}
