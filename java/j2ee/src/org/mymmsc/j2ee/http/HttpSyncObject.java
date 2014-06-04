/**
 * 
 */
package org.mymmsc.j2ee.http;

/**
 * @author WangFeng
 * 
 */
public abstract class HttpSyncObject implements HttpSyncMethod {

	public Object exec(int timeout) {
		RedisApi api = RedisApi.getInstance();
		return api.exec(timeout, this);
	}
}
