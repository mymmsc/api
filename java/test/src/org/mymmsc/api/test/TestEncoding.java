/**
 * @(#)TestEncoding.java	6.3.9 09/10/02
 *
 * Copyright 2000-2010 MyMMSC Software Foundation (MSF), Inc. All rights reserved.
 * MyMMSC PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.mymmsc.api.test;

import org.mymmsc.api.assembly.RegExp;
import org.mymmsc.api.crypto.Base64;

/**
 * @author WangFeng(wangfeng@yeah.net)
 * @version 6.3.9 09/10/02
 * @since mymmsc-test 6.3.9
 */
public class TestEncoding {

	/**
	 * TestEncoding
	 */
	public TestEncoding() {
		//
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String s = "123";
		s = "\u65e9\u81ea\u4e60";
		s = "\\\\u65e9\\\\u81ea\\\\u4e60";
		s = s.replaceAll("\\\\u","u");
		System.out.println(s);
		Base64 api = new Base64();
		
		System.out.println(api.encode(s));
		String exp = "/[\u4E00-\u9FA5]|[\uFE30-\uFFA0]/gi";
		exp = "([\u4E00-\u9FA5]+)";
		exp = "([\u4E00-\u9FA5]+|[\uFE30-\uFFA0]+)";
		s = "中国";
		System.out.println(s);
		String charset = "iso8859_1";
		boolean b = RegExp.valid(s, exp);
		if(b) {
			charset = "utf-8";
		}
		System.out.println(charset);
		System.out.println(b);

	}

}
