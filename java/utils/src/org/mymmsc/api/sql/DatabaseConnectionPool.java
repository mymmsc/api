/**
 * @(#)DatabaseConnectionPool.java	6.3.9 09/10/02
 *
 * Copyright 2000-2010 MyMMSC Software Foundation (MSF), Inc. All rights reserved.
 * MyMMSC PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.mymmsc.api.sql;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 数据库连接池
 * 
 * @author WangFeng(wangfeng@yeah.net)
 * @version 6.3.9 2009/09/13
 * @since 6.3.9
 */
public class DatabaseConnectionPool {
	/** 数据库连接参数 */
	private ConnectionParam param = null;
	/** 数据库连接工厂 */
	private ConnectionFactory factory = null;
	/** 数据库连接工厂参数 */
	private FactoryParam factoryParam = null;
	/** 性能指标 */
	private double speed = 0;

	/**
	 * 构造函数
	 * 
	 * @param driver
	 * @param url
	 * @param user
	 * @param password
	 * @param min
	 * @param max
	 * @throws SQLException
	 */
	public DatabaseConnectionPool(String driver, String url, String user,
			String password, int min, int max) throws SQLException {
		param = new ConnectionParam(driver, url, user, password);
		factoryParam = new FactoryParam(max, min, 0);
		factory = new ConnectionFactory(param, factoryParam);
	}

	/**
	 * 构造函数
	 * 
	 * @param params 连接池参数
	 * @throws SQLException
	 */
	public DatabaseConnectionPool(JdbcParams params) throws SQLException {
		this(params.getDriver(), params.getUrl(), params.getUsername(), params
				.getPassword(), params.getMin(), params.getMax());
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#finalize()
	 */
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		System.out.println("性能指标" + speed + "/s.");
		if (factory != null) {
			close();
		}
	}

	public void close() {
		try {
			System.out.print("数据库连接池正在回收...");
			factory.close();
			System.out.println("ok!");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public Connection getConnection() throws SQLException {
		long time = System.currentTimeMillis();
		Connection conn = factory.getFreeConnection();
		time = System.currentTimeMillis() - time;
		if (speed == 0) {
			speed = time;
		} else {
			speed = (speed + time) / 2;
		}
		return conn;
	}

	public double getSpeed() {
		return speed;
	}
}
