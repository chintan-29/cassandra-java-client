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
 * in localhost:9106, if can not found the server, call test will be skiped.
 * 
 * @author sanli
 *
 */
public class CassandraClientTest {
	

	public static boolean skipNeedServerCase = false ;
	
	/**
	 * check whether the server was started, if server not start will
	 * set the skipNeedServerCase=true, then all case will be skipped 
	 */
	@BeforeClass
	public static void checkServerStatus(){
		TTransport tr = new TSocket( "localhost" , 9160 );
		TProtocol proto = new TBinaryProtocol(tr);
		Cassandra.Client client = new Cassandra.Client(proto);
		CassandraClientImpl cclient = new CassandraClientImpl(client) ;
		
		try {
			tr.open();
			cclient.init();	
		} catch (TTransportException e) {
			System.out.println("can not found test server, will skip server relate test");
			e.printStackTrace();
			skipNeedServerCase = true ;
		} catch (TException e) {
			System.out.println("can not found test server, will skip server relate test");
			e.printStackTrace();
			skipNeedServerCase = true ;
		}
	}
	
	
	public CassandraClientImpl createClient() throws TException{
		TTransport tr = new TSocket( "localhost" , 9160 );
		TProtocol proto = new TBinaryProtocol(tr);
		Cassandra.Client client = new Cassandra.Client(proto);
		CassandraClientImpl cclient = new CassandraClientImpl(client) ;
		tr.open();
		cclient.init() ;
		return cclient ;
	}
	
	
	public void closeClient(CassandraClient cclient){
		cclient.getCassandra().getInputProtocol().getTransport().close();
		cclient.getCassandra().getOutputProtocol().getTransport().close();
	}
	
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
