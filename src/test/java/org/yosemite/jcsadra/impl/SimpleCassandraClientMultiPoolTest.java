package org.yosemite.jcsadra.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.cassandra.service.Cassandra;
import org.apache.commons.pool.BaseKeyedPoolableObjectFactory;
import org.apache.commons.pool.BasePoolableObjectFactory;
import org.apache.commons.pool.KeyedPoolableObjectFactory;
import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.yosemite.jcsadra.CassandraClient;
import org.yosemite.jcsadra.CassandraClientPool;
import org.yosemite.jcsadra.CassandraClientPool.ExhaustedAction;
import org.yosemite.jcsadra.impl.SimpleCassandraClientPoolTest.DumyPoolableClientFactory;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * test simple cassandra client
 * @author dayong
 */
public class SimpleCassandraClientMultiPoolTest {
	
    private SimpleCassandraClientMultiPool pool;
    private Map<String, SimpleCassandraClientMultiPool.CassandraCluster> clusters = new HashMap<String, SimpleCassandraClientMultiPool.CassandraCluster>();
	private int maxActive;
	private ExhaustedAction exhaustedAction;
	private int maxWaitWhenBlockByExhausted;
	private int maxIdle;


    private static final String CASSANDRA_CLUSTER_KEY1 = "CASSANDRA_CLUSTER_KEY1";
    private static final String CASSANDRA_CLUSTER_KEY2 = "CASSANDRA_CLUSTER_KEY2";
	private static final String CASSANDRA_SERVER_IP = null;
	private static final String CASSANDRA_SERVER_IP2 = null;
	private static final int CASSANDRA_SERVER_PORT = 0;
	private static final int CASSANDRA_SERVER_PORT2 = 0;

    @Before
    public void createPool() throws Exception {

		
		this.maxActive = 50 ;
		this.exhaustedAction = CassandraClientPool.ExhaustedAction.WHEN_EXHAUSTED_BLOCK ;
		this.maxWaitWhenBlockByExhausted = 5000 ; 
		this.maxIdle = 5 ;
		
        clusters.put(CASSANDRA_CLUSTER_KEY1, new SimpleCassandraClientMultiPool.CassandraCluster(CASSANDRA_SERVER_IP, 9160));
        clusters.put(CASSANDRA_CLUSTER_KEY2, new SimpleCassandraClientMultiPool.CassandraCluster(CASSANDRA_SERVER_IP2, 9160));
        
        DumyKeyedPoolableClientFactory factory = new DumyKeyedPoolableClientFactory() ;
		pool = new SimpleCassandraClientMultiPool(clusters ,
				 factory , maxActive, exhaustedAction ,  
				 maxWaitWhenBlockByExhausted,  maxIdle) ; 

		
    }

    @After
    public void closePool() throws Exception {
        pool.close();
    }

    /**
     * Same behavior as SimpleCassandraClientPool when get client for one cluster
     */
    @Test
    public void testExceedMaxActive() {
        for (int i = 0; i < pool.getMaxActive(); i++) {
            try {
                pool.getClient(CASSANDRA_CLUSTER_KEY1);
            } catch (Exception e) {
                e.printStackTrace();
                fail("Exception caught when creating client");
            }
        }

        try {
            pool.getClient(CASSANDRA_CLUSTER_KEY1);
            fail("Exception should be thrown when pool exceeds its max active number");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Set a new max active value, and watch the behavior
     */
    @Test
    public void testSetMaxActiveWithNewValue() {
        pool.setMaxActive(10);
        assertEquals(10, pool.getMaxActive());

        for (int i = 0; i < 10; i++) {
            try {
                pool.getClient(CASSANDRA_CLUSTER_KEY1);
            } catch (Exception e) {
                fail("Exception caught when creating client");
            }
        }

        try {
            pool.getClient(CASSANDRA_CLUSTER_KEY1);
            fail("Exception should be thrown when pool exceeds its max active number");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testMaxIdle() {
        assertEquals(0, pool.getAvailableNum());
        for (int i = 0; i < pool.getMaxActive(); i++) {
            try {
                CassandraClient client = pool.getClient(CASSANDRA_CLUSTER_KEY1);
                pool.releaseClient(CASSANDRA_CLUSTER_KEY1, client);
            } catch (Exception e) {
                fail("Exception caught when creating client");
            }
        }
        assertEquals(1, pool.getAvailableNum());

        ArrayList<CassandraClient> clients = new ArrayList<CassandraClient>();
        for (int i = 0; i < pool.getMaxActive(); i++) {
            try {
                clients.add(pool.getClient(CASSANDRA_CLUSTER_KEY1));
            } catch (Exception e) {
                fail("Exception caught when creating client");
            }
        }

        for (int i = 0; i < pool.getMaxActive(); i++) {
            try {
                pool.releaseClient(CASSANDRA_CLUSTER_KEY1, clients.get(i));
                if (i < pool.getMaxIdle())
                    assertEquals(i + 1, pool.getAvailableNum());
                else
                    assertEquals(pool.getMaxIdle(), pool.getAvailableNum());
            } catch (Exception e) {
                fail("Exception caught when release client.");
                e.printStackTrace();
            }
        }
    }
    
    @Test
    public void testGetClientForCluster2() {
        for (int i = 0; i < pool.getMaxActive(); i++) {
            try {
                pool.getClient(CASSANDRA_CLUSTER_KEY1);
            } catch (Exception e) {
                e.printStackTrace();
                fail("Exception caught when creating client");
            }
        }
        System.out.println(pool.getActiveNum());

        try {
            pool.getClient(CASSANDRA_CLUSTER_KEY2);
        } catch (Exception e) {
            fail("Exception caught when get client for cluster 2");
            e.printStackTrace();
        }
    }

    
	/**
	 * inner class for create and destory Cassandra.Client 
	 * @author dayong
	 */
	public class DumyKeyedPoolableClientFactory extends BaseKeyedPoolableObjectFactory
			implements KeyedPoolableObjectFactory {

		@Override
		public Object makeObject(Object key) throws Exception {
			String serviceURL = clusters.get(key).serviceURL;
			int port= clusters.get(key).port;
			
			TTransport tr = new TSocket(serviceURL, port);
			TProtocol proto = new TBinaryProtocol(tr);
			Cassandra.Client client = new Cassandra.Client(proto);
			CassandraClient cclient = new CassandraClientImpl(client);
			return cclient;
		}
		
		@Override
		public void destroyObject(Object key, Object obj) throws Exception {
			CassandraClient client = (CassandraClient)obj ;
		}


	    public boolean validateObject(Object key, Object obj) {
	        return true;
	    }
		
	}

}
