package org.mymmsc.api.crypto;

import org.mymmsc.api.encoding.InterfaceEncoder;

import sun.misc.BASE64Decoder;

/**
 * BASE64
 * 
 * @author WangFeng(wangfeng@yeah.net)
 * @version 6.3.9
 */
public class Base64 implements InterfaceEncoder {
	/**
	 * BASE64
	 */
	public Base64() {
		super();
	}

	/**
	 * encode 将 s 进行 BASE64 编码
	 * 
	 * @param s
	 *            String
	 * @return String
	 */
	public String encode(String s) {
		if (s == null) {
			return null;
		}
		return (new sun.misc.BASE64Encoder()).encode(s.getBytes());
	}

	/**
	 * encode
	 * 
	 * @param b
	 *            byte[]
	 * @return String
	 */
	public String encode(byte[] b) {
		if (b == null) {
			return null;
		}
		return (new sun.misc.BASE64Encoder().encode(b));
	}

	/**
	 * decode 将 BASE64 编码的字符串 s 进行解码
	 * 
	 * @param s
	 *            String
	 * @return String
	 */
	public String decode(String s) {
		if (s == null) {
			return null;
		}
		BASE64Decoder decoder = new BASE64Decoder();
		try {
			byte[] b = decoder.decodeBuffer(s);
			return new String(b);
		} catch (Exception e) {
			return null;
		}
	}

	public String decode(byte[] b) {
		return null;
	}
}
