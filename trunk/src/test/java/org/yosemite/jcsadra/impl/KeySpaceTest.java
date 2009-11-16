package org.yosemite.jcsadra.impl;

import static org.junit.Assert.*;


import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
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
 * @author sanli
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
		
		KeySpace ks = pool.getClient().getKeySpace("Keyspace1") ;
		
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
		for(int i = 0 ; i < 100 ; i++){
			ColumnPath cp = new ColumnPath("Standard2" , null, ("testGetSlice_"+i).getBytes("utf-8"));
			ks.insert("testGetSlice" , cp , ("testGetSlice_Value_"+i).getBytes("utf-8"));
		}
		
		//get value
		ColumnParent clp =  new ColumnParent("Standard1", null);
		SliceRange sr = new SliceRange();
		SlicePredicate  sp = new SlicePredicate(null , sr );
		List<Column> cols = ks.getSlice("testGetSlice", clp , sp ) ;
		
		
		
		ColumnPath cp = new ColumnPath("Standard2" , null, null);
		ks.remove("testGetSlice_", cp);
		
		
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
		SuperColumn superc = ks.getSuperColumn("testGetSuperColumn_1", cp );
		
		assertTrue(superc != null );
		assertTrue(superc.getColumns() != null);
		assertTrue(superc.getColumns().size() == 10 );
		
		ks.remove("testGetSuperColumn_1", cp);		
	}

	
	
	
	
	@Test
	public void testGetSuperSlice() {
		if(skipNeedServerCase){
			return ;
		}
		
		fail("Not yet implemented");
	}

	
	
	
	@Test
	public void testInsertStringStringColumnPathByteArray() {
		if(skipNeedServerCase){
			return ;
		}
		
		fail("Not yet implemented");
	}

	
	
	
	@Test
	public void testMultigetColumn() {
		if(skipNeedServerCase){
			return ;
		}
		
		fail("Not yet implemented");
	}

	
	
	
	@Test
	public void testMultigetSlice() {
		if(skipNeedServerCase){
			return ;
		}
		
		fail("Not yet implemented");
	}

	
	
	
	@Test
	public void testMultigetSuperColumn() {
		if(skipNeedServerCase){
			return ;
		}
		
		fail("Not yet implemented");
	}

	
	
	
	@Test
	public void testMultigetSuperSlice() {
		if(skipNeedServerCase){
			return ;
		}
		
		fail("Not yet implemented");
	}



}
