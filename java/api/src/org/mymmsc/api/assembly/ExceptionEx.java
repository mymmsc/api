/**
 * @(#)ExceptionEx.java	6.3.9 09/11/02
 *
 * Copyright 2000-2010 MyMMSC Software Foundation (MSF), Inc. All rights reserved.
 * MyMMSC PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.mymmsc.api.assembly;

import java.io.IOException;


/**
 * 异常扩展类
 * 
 * @author WangFeng(wangfeng@yeah.net)
 * @version 6.3.9 09/10/02
 * @since mymmsc-api 6.3.9
 */
public class ExceptionEx {
	/**
	 * getExceptionString
	 * 
	 * @param ex
	 *            IOException
	 * @return String
	 */
	public static String getString(IOException ex) {
		return Api.Sprintf("%s-> %s", ex.getClass().getSimpleName(), ex
				.getMessage());
	}

	/**
	 * getExceptionString
	 * 
	 * @param steArray
	 *            StackTraceElement[]
	 * @param filter
	 *            String
	 * @return String
	 */
	public static String getString(StackTraceElement[] steArray, String filter) {
		String _str = "";
		// for(int i = 0; i < steArray.length; i++)
		for (StackTraceElement ste : steArray) {
			if (Api.getPatternString(ste.getClassName(), filter).length() == 0) {
				continue;
			}
			_str += Api
					.Sprintf("%s,%s(%d) -> %s\n", ste.getClassName(), ste
							.getFileName(), ste.getLineNumber(), ste
							.getMethodName());
		}
		return _str;
	}

	/**
	 * Fake
	 * 
	 * @param throwable
	 *            Throwable
	 * @param throwable1
	 *            Throwable
	 * @return Throwable
	 */
	public static Throwable Fake(Throwable throwable, Throwable throwable1) {
		try {
			throwable.getClass().getMethod("initCause",
					new Class[] { java.lang.Throwable.class }).invoke(
					throwable, new Object[] { throwable1 });
		} catch (Exception exception) {
			//
		}
		return throwable;
	}
}
