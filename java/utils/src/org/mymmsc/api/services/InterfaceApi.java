/**
 * @(#)InterfaceApi.java	6.3.9 09/10/02
 *
 * Copyright 2000-2010 MyMMSC Software Foundation (MSF), Inc. All rights reserved.
 * MyMMSC PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.mymmsc.api.services;

/**
 * 可重复加载类的接口
 * 
 * @author WangFeng
 * @version 6.3.9 09/10/02
 * @since 6.3.9
 */
public interface InterfaceApi {
	public void start();

	public void close();

	public void doBusiness();
}
