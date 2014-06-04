/**
 * 
 */
package org.mymmsc.j2ee.http;

import redis.clients.jedis.ShardedJedis;

/**
 * @author WangFeng
 * 
 */
public class TestHttpSyncObject extends HttpSyncObject {
	private String key = "abc";
	private int value = 0;
	
	public Object service(ShardedJedis redis) {
		String oRet = null;
		oRet = redis.set(key, String.valueOf(++value));
		if (oRet.equalsIgnoreCase(RedisApi.SUCCESS)) {
			oRet = String.valueOf(value);
		}
		return oRet;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		TestHttpSyncObject thso = new TestHttpSyncObject();
		for (int i = 0; i < 100; i++) {
			System.out.println(thso.exec(3));
		}
	}

}
