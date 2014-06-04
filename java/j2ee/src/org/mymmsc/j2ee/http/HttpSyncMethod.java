/**
 * 
 */
package org.mymmsc.j2ee.http;

import redis.clients.jedis.ShardedJedis;

/**
 * HTTP同步方法
 * 
 * @author WangFeng
 *
 */
public abstract interface HttpSyncMethod {
	public abstract Object service(ShardedJedis redis);
}
