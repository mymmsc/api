/**
 * @(#)IUnknown.java	6.3.9 09/10/02
 *
 * Copyright 2000-2010 MyMMSC Software Foundation (MSF), Inc. All rights reserved.
 * MyMMSC PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.mymmsc.api.adapter;

import java.lang.reflect.Method;

/**
 * COM接口
 * 
 * @author WangFeng(wangfeng@yeah.net)
 * @version 6.3.9 09/10/02
 * @since mymmsc-api 6.3.9
 * @remark 预备阶段
 */
public interface IUnknown {
	
	public Method getMethod(Object obj, String name);

	/**
	 * Retrieves pointers to the supported interfaces on an object.
	 * 
	 * @return
	 */
	public String QueryInterface();

	/**
	 * Increments the reference count for an interface on an object.
	 * 
	 * @return
	 */
	public String AddRef();

	/**
	 * Decrements the reference count for an interface on an object.
	 * 
	 * @return
	 */
	public String Release();
}
