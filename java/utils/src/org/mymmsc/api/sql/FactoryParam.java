/**
 * @(#)FactoryParam.java	6.3.9 09/10/02
 *
 * Copyright 2000-2010 MyMMSC Software Foundation (MSF), Inc. All rights reserved.
 * MyMMSC PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.mymmsc.api.sql;

/**
 * 连接池工厂参数
 * 
 * @author WangFeng(wangfeng@yeah.net)
 * @version 6.3.9 2009/09/13
 * @since 6.3.9
 */
public class FactoryParam {
	// 最大连接数
	private int MaxConnectionCount = 10;

	// 最小连接数
	private int MinConnectionCount = 2;

	// 回收策略
	private int ManageType = 0;

	public FactoryParam() {
		//
	}

	/**
	 * 构造连接池工厂参数的对象
	 * 
	 * @param max
	 *            最大连接数
	 * @param min
	 *            最小连接数
	 * @param type
	 *            管理策略
	 */
	public FactoryParam(int max, int min, int type) {
		this.MaxConnectionCount = max;
		this.MinConnectionCount = min;
		this.ManageType = type;
	}

	public int getManageType() {
		return ManageType;
	}

	public void setManageType(int manageType) {
		ManageType = manageType;
	}

	public int getMaxConnectionCount() {
		return MaxConnectionCount;
	}

	public void setMaxConnectionCount(int maxConnectionCount) {
		MaxConnectionCount = maxConnectionCount;
	}

	public int getMinConnectionCount() {
		return MinConnectionCount;
	}

	public void setMinConnectionCount(int minConnectionCount) {
		MinConnectionCount = minConnectionCount;
	}
}