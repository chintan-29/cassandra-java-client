package org.yosemite.jcsadra.impl;

import org.apache.cassandra.service.Cassandra;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.junit.BeforeClass;
import org.yosemite.jcsadra.CassandraClient;

public class ServerBasedTestCase {
	
	
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

}
