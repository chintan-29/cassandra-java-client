package org.yosemite.jcsadra.impl;

import java.util.NoSuchElementException;

import org.yosemite.jcsadra.CassandraClient;
import org.yosemite.jcsadra.CassandraClientPool;


/**
 * A simple client pool, have some configuration item to make it can work under
 * different request. it based on org.apache.commons.pool.GenericObjectPool.
 * 
 * 
 * @author sanli
 */
public class SimpleCassandraClientPool implements CassandraClientPool {

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

	@Override
	public int getAvailableNum() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public CassandraClient getClient() throws Exception,
			NoSuchElementException, IllegalStateException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getUsingNum() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void releaseClient(CassandraClient client) throws Exception {
		// TODO Auto-generated method stub

	}

}
