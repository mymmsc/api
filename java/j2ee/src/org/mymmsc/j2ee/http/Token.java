/**
 * 
 */
package org.mymmsc.j2ee.http;

/**
 * HTTP/HTTPs网络令牌
 * 
 * @author WangFeng
 * @remark 采用3DES方式加解密数据
 */
public abstract interface Token {
	/**
	 * 初始化TOKEN
	 * 
	 * @param key
	 * @return
	 * @remark 3DES
	 */
	public abstract boolean init(String key);

	/**
	 * 加密
	 * 
	 * @param obj
	 * @return
	 */
	public abstract String encode(Object obj);

	/**
	 * 解密
	 * @param <T>
	 * @return
	 */
	public abstract <T> T decode(Class<T> clazz);
}
