/**
 * 
 */
package org.mymmsc.api.encoding;

/**
 * 字符集检测
 * 
 * @author WangFeng
 * 
 */
public class Charsets {
	
	/**
	 * 检测字符串的字符集
	 * @param s
	 * @return
	 */
	public static native String detect(String s);

	static {
		System.loadLibrary("mymmsc-charset");
	}
}
