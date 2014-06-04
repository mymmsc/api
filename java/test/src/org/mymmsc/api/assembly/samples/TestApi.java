/**
 * @(#)ApiContext.java	6.3.9 09/10/02
 *
 * Copyright 2000-2010 MyMMSC Software Foundation (MSF), Inc. All rights reserved.
 * MyMMSC PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.mymmsc.api.assembly.samples;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Date;

import org.mymmsc.api.assembly.Api;

/**
 * @author wangfeng
 * 
 */
public class TestApi {

	/**
	 * @param args
	 * @throws UnsupportedEncodingException 
	 * @throws IOException 
	 * @throws JsonParseException 
	 */
	public static void main(String[] args) throws UnsupportedEncodingException {
		String a = Api.shell("cmd /c dir");
		System.out.println("原始串: " + a);
		System.out.println("原始串视图转化成UTF-8: " + Api.iconv(a, "gb2312", "utf-8"));
		System.out.println("平台默认字符集: " + Charset.defaultCharset().name());
		System.out.println("字符串使用getBytes()检测: " + Api.detectCharset(a.getBytes(), a.getBytes().length));
		String charset = Api.detectCharset(a);
		System.out.println("字符串由char转bytes后再检测: " + charset);
		String s1 = Api.iconv(a, "utf-8", charset);
		//byte[] b1 = a.getBytes("gb2312");
		System.out.println("字符串转成UTF-8: " + s1);
		System.out.println("字符串转成UTF-8: " + Api.iconv(s1, charset, "utf-8"));
		String s = "2012-05-11 10:14:55.000";
		if (Api.isDate(s, "yyyy-MM-dd HH:mm:ss")) {
			Date da = Api.toDate(s, "yyyy-MM-dd HH:mm:ss");
			System.out.println(((java.util.Date) da).toString());
		}
		String value = "1111111111111111111111111111111111111111111111111112131321313213132132123132131309.09ab";
		value = " 09.09ab";
		//value = "0";
		System.out.println("  value = " + value + ", cast:");
		// String b = Api.dirName(value);
		// System.out.println(b);
		boolean aBoolean = Api.valueOf(boolean.class, value);
		System.out.println("boolean => " + aBoolean);
		byte aByte = Api.valueOf(byte.class, value);
		System.out.println("  byte.o  => " + aByte);
		System.out.println("  byte.s  => " + Api.toString(aByte));
		int aInt = Api.valueOf(int.class, value);
		System.out.println("   int.o  => " + aInt);
		System.out.println("   int.s  => " + Api.toString(aInt));
		long aLong = Api.valueOf(long.class, value);
		System.out.println("  long.o  => " + aLong);
		System.out.println("  long.s  => " + Api.toString(aLong));
		Long lLong = Api.valueOf(Long.class, value);
		System.out.println("  Long.o  => " + lLong);
		System.out.println("  Long.s  => " + Api.toString(lLong));
		float aFloat = Api.valueOf(float.class, value);
		System.out.println(" float.o  => " + aFloat);
		System.out.println(" float.s  => " + Api.toString(aFloat));
		double aDouble = Api.valueOf(double.class, value);
		System.out.println("double.o  => " + aDouble);
		System.out.println("double.s  => " + Api.toString(aDouble));
		String aString = Api.valueOf(String.class, value);
		System.out.println("String.o  => " + aString);
		System.out.println("String.s  => " + Api.toString(aString));
		Date aDate = Api.valueOf(Date.class, value);
		System.out.println("  Date.o  => " + aDate);
		System.out.println("  Date.s  => " + Api.toString(aDate));
	}

}
