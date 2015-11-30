# Introduce #

This project is a high level cassandra java client lib, basic idea is make develop Cassandra based application more easy and quickly.


# Basic Further #

  * Create cassandra client pool to support high stress concurrent request.
  * Provide some simple wrap class to make cassandra call simple and more easy to us.
  * Provide some help utility to support java object serialized into cassandra.




# Sample Code #

```

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

```

more sample please refer to unit test

# Build #
need install maven2.0 before build

  * >svn checkout https://cassandra-java-client.googlecode.com/svn/trunk/ cassandra-java-client
  * >cd cassandra-java-client
  * >mvn install
