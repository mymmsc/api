/**
 * @(#)PreparedStatementProxy.java	6.3.9 09/10/02
 *
 * Copyright 2000-2010 MyMMSC Software Foundation (MSF), Inc. All rights reserved.
 * MyMMSC PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.mymmsc.api.sql;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 拟HOOK数据表操作
 * 
 * @author WangFeng(wangfeng@yeah.net)
 * @version 6.3.9 09/10/02
 * @since mymmsc-api 6.3.9
 */
public class PreparedStatementProxy implements InvocationHandler {

	/**
	 * PreparedStatementProxy构造函数
	 */
	public PreparedStatementProxy() {
		// 
	}

	/* (non-Javadoc)
	 * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object, java.lang.reflect.Method, java.lang.Object[])
	 */
	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		// 
		return null;
	}

}
