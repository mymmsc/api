/**
 * @(#)InterfaceHttpElement.java	6.3.9 09/10/02
 *
 * Copyright 2000-2010 MyMMSC Software Foundation (MSF), Inc. All rights reserved.
 * MyMMSC PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.mymmsc.j2ee.http.adapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Http协议元素接口
 * 
 * @author WangFeng(wangfeng@yeah.net)
 * @version 6.3.9 09/10/02
 * @since mymmsc-j2ee 6.3.9
 */
public abstract interface Element {
	/**
	 * 初始化对象
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public abstract boolean init(HttpServletRequest request, HttpServletResponse response);

	/**
	 * 赋值
	 * 
	 * @param <T>
	 * @param name
	 * @param value
	 * @return
	 */
	public abstract <T> boolean set(String name, T value);

	/**
	 * 取值
	 * 
	 * @param <T>
	 *            类
	 * @param name
	 *            名称
	 * @param defaultValue
	 *            默认值
	 * @return
	 */
	public abstract <T> T get(String name, T defaultValue);

	/**
	 * 删除
	 * 
	 * @param name
	 * @return
	 */
	public abstract boolean remove(String name);
}
