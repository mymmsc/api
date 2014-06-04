/**
 * 
 */
package org.mymmsc.api.io.samples;

import org.mymmsc.api.io.HttpCookie;

/**
 * @author WangFeng
 *
 */
public class TestHttpCookie {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String s = "JSESSIONID=aaa-IDd_HAhQ-_YUPiH1t; path=/; secure";
		HttpCookie cookie = HttpCookie.parse(s);
		System.out.println(cookie.toString());
	}

}
