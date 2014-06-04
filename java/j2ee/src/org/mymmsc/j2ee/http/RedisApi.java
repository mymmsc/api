/**
 * 
 */
package org.mymmsc.j2ee.http;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.mymmsc.api.adapter.AutoObject;
import org.mymmsc.api.assembly.Api;

import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

/**
 * Redis API
 * 
 * @author WangFeng
 * 
 */
public class RedisApi extends AutoObject{
	private static RedisApi instance = null;
	private ShardedJedisPool redisPool = null;
	private static String host = "redis.api.mymmsc.org";
	private static int port = 6379;
	// 加锁标志
	private static final String LOCKED = "TRUE";
	private static final long ONE_MILLI_NANOS = 1000000L;
	// 默认超时时间（毫秒）
	@SuppressWarnings("unused")
	private static final long DEFAULT_TIME_OUT = 3000;
	private static final Random r = new Random();
	// 锁的超时时间（秒），过期删除
	private static final int EXPIRE = 5 * 60;
	// 锁状态标志
	private boolean locked = false;
	private String keyLock = null;
	/** 同步执行成功状态 */
	public static final String SUCCESS = "OK";
	public static final int GET = 1;
	public static final int SET = 2;

	/**
	 * 初始化
	 * 
	 * @param hostname
	 *            主机名
	 * @param hostport
	 *            主机端口
	 */
	public static synchronized RedisApi getInstance(String hostname,
			int hostport) {
		if (instance == null) {
			instance = new RedisApi(hostname, hostport);
		}
		return instance;
	}
	
	public static synchronized RedisApi getInstance(){
		return getInstance(host, port);
	}

	private RedisApi(String hostname, int hostport) {
		host = hostname;
		port = hostport;
		JedisPoolConfig conf = new JedisPoolConfig();
		conf.setMaxActive(20);
		conf.setMaxIdle(10);
		conf.setMaxWait(1000);
		JedisShardInfo info = new JedisShardInfo(host, port);
		List<JedisShardInfo> list = new ArrayList<JedisShardInfo>();
		list.add(info);
		redisPool = new ShardedJedisPool(conf, list);
	}

	/**
	 * 保存一个对象
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public boolean set(String key, String value) {
		boolean bRet = false;
		ShardedJedis jedis = null;
		try {
			jedis = redisPool.getResource();
			jedis.set(key, value);
			bRet = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			redisPool.returnResource(jedis);
		}
		return bRet;
	}

	/**
	 * 获取一个对象
	 * 
	 * @param key
	 * @return
	 */
	public String get(String key) {
		String value = null;
		ShardedJedis jedis = null;
		try {
			jedis = redisPool.getResource();
			value = jedis.get(key);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			redisPool.returnResource(jedis);
		}
		return value;
	}

	/**
	 * 删除一个对象
	 * 
	 * @param key
	 */
	public boolean delete(String key) {
		boolean bRet = false;
		ShardedJedis jedis = null;
		try {
			jedis = redisPool.getResource();
			jedis.del(key);
			bRet = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			redisPool.returnResource(jedis);
		}
		return bRet;
	}
	
	/**
	 * 执行同步事务方法
	 * 
	 * @param timeout 秒
	 * @param method
	 * @return
	 */
	public Object exec(int timeout, HttpSyncMethod method) {
		Object obj = null;
		ShardedJedis jedis = redisPool.getResource();
		try {
			boolean bRet = false;
			bRet = lock(jedis, timeout * 1000);
			if (bRet) {
				obj = method.service(jedis);
			}			
		} catch (Exception e) {
			error("执行任务失败", e);
		} finally {
			unlock(jedis);
		}

		return obj;
	}

	public boolean lock(ShardedJedis jedis, long timeout) {
		long nano = System.nanoTime();
		timeout *= ONE_MILLI_NANOS;
		keyLock = Api.o3String(128);
		try {
			while ((System.nanoTime() - nano) < timeout) {
				if (jedis.setnx(keyLock, LOCKED) == 1) {
					jedis.expire(keyLock, EXPIRE);
					locked = true;
					return locked;
				}
				// 短暂休眠，nano避免出现活锁
				Thread.sleep(3, r.nextInt(500));
			}
		} catch (Exception e) {
			//
		}
		return false;
	}

	// 无论是否加锁成功，必须调用
	public void unlock(ShardedJedis jedis) {
		try {
			if (locked) {
				jedis.del(keyLock);
			}
		} finally {
			redisPool.returnResource(jedis);
		}
	}

	@Override
	public void close() {
		//
	}
	
	public static void main(String[] argv) {
		RedisApi api = RedisApi.getInstance();
		final String key = "abc";
		String value = "1";
		System.out.println("value = " + value);
		value = (String)api.exec(3, new HttpSyncMethod() {
			public Object service(ShardedJedis redis) {
				return redis.set(key, "2");
			}
		});
		System.out.println("value = " + value);
		value = (String)api.exec(3, new HttpSyncMethod() {
			public Object service(ShardedJedis redis) {
				return redis.get(key);
			}
		});
		System.out.println("value = " + value);
	}
}
