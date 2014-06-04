/**
 * @(#)AbstractApi.java	6.3.9 09/10/02
 *
 * Copyright 2000-2010 MyMMSC Software Foundation (MSF), Inc. All rights reserved.
 * MyMMSC PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.mymmsc.api.services;

/**
 * 可重复加载的抽象类
 * 
 * @author WangFeng
 * @version 6.3.9 09/10/02
 * @since 6.3.9
 */
public abstract class AbstractApi {
	private InterfaceApi m_api = null;

	public void notifyReLoad(String className) throws Exception {
		m_api.close();
		ApiClassLoader loader = new ApiClassLoader();
		m_api = (InterfaceApi) loader.loadFromCustomRepository(className)
				.newInstance();
		m_api.start();
	}
}
