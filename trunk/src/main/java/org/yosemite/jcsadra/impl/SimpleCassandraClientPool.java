package org.yosemite.jcsadra.impl;

import java.util.NoSuchElementException;

import org.yosemite.jcsadra.CassandraClient;
import org.yosemite.jcsadra.CassandraClientPool;


/**
 * A simple client pool, have some configuration item to make it can work under
 * different request. it based on org.apache.commons.pool.GenericObjectPool.
 * 
 * configuration item include:
 *    maxActive controls the maximum number of client that can be created 
 *    by the pool at a given time.  When non-positive, there is no
 *    limit to the number of client that can be managed by the pool at one time.
 *    When maxActive is reached, the pool is said to be exhausted. 
 *    The default setting for this parameter is 15(because from our test, the 15 concurrent
 *    access perform very well in commons pc server)<br>
 *    
 *    maxIdle controls the maximum number of objects that can sit idle in the pool 
 *    at any time.  When negative, there is no limit to the number of objects that may 
 *    be idle at one time. The default setting for this parameter is 5( 1/3 of maxActive).<br>
 *  
 *    exhaustedAction specifies the behavior of the getClinet() method when 
 *    the pool is exhausted:
 *      When exhaustedAction is #WHEN_EXHAUSTED_FAIL getClient() will throw a 
 *      NoSuchElementException
 *      
 *      When {@link #setWhenExhaustedAction <i>whenExhaustedAction</i>} is
 *      {@link #WHEN_EXHAUSTED_GROW}, {@link #borrowObject} will create a new
 *      object and return it (essentially making {@link #setMaxActive <i>maxActive</i>}
 *      meaningless.)
 *    </li>
 *    <li>
 *      When {@link #setWhenExhaustedAction <i>whenExhaustedAction</i>}
 *      is {@link #WHEN_EXHAUSTED_BLOCK}, {@link #borrowObject} will block
 *      (invoke {@link Object#wait()}) until a new or idle object is available.
 *      If a positive {@link #setMaxWait <i>maxWait</i>}
 *      value is supplied, then {@link #borrowObject} will block for at
 *      most that many milliseconds, after which a {@link NoSuchElementException}
 *      will be thrown.  If {@link #setMaxWait <i>maxWait</i>} is non-positive,
 *      the {@link #borrowObject} method will block indefinitely.
 *    </li>
 *    </ul>
 *    The default <code>whenExhaustedAction</code> setting is
 *    {@link #WHEN_EXHAUSTED_BLOCK} and the default <code>maxWait</code>
 *    setting is -1. By default, therefore, <code>borrowObject</code> will
 *    block indefinitely until an idle instance becomes available.
 *  </li>
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
