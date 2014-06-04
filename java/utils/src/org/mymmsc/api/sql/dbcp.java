/*
 * @(#)dbcp.java	6.3.9 09/09/25
 *
 * Copyright 2009 MyMMSC Software Foundation (MSF), Inc. All rights reserved.
 * MSF PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.mymmsc.api.sql;

/**
 * 公用基础类 -- 数据库连接池默认参数定义
 * 
 * @author WangFeng(wangfeng@yeah.net)
 * @version 6.3.9 09/10/02
 * @since mymmsc-api 6.3.9
 */
public final class dbcp {
	public final static int SIZE = 20; // 数据库连接池尺寸
	public final static int MAXIDLE = 5; // 最大空闲数据库连接数
	public final static int TIMEOUT = 30; // 超时回收时间
}
