/**
 * @(#)ConnectionProxy.java	6.3.9 09/10/02
 *
 * Copyright 2000-2010 MyMMSC Software Foundation (MSF), Inc. All rights reserved.
 * MyMMSC PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.mymmsc.api.sql;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 定义数据库连接的代理类
 * 
 * @author WangFeng(wangfeng@yeah.net)
 * @version 6.3.9 2009/09/13
 * @since 6.3.9
 */
public class ConnectionProxy implements InvocationHandler {

	// 定义连接
	private Connection m_conn = null;
	// 定义监控连接创建的语句
	private Statement m_statRef = null;
	private PreparedStatement m_preStatRef = null;
	// 是否支持事务标志
	private boolean m_supportTransaction = false;
	// 数据库的忙状态
	private boolean m_isFree = false;
	// 最后一次访问时间
	private long m_lastAccessTime = 0;
	// 定义要接管的函数的名字
	private static String CREATESTATE = "createStatement";
	private static String CLOSE = "close";
	private static String PREPARESTATEMENT = "prepareStatement";
	private static String COMMIT = "commit";
	private static String ROLLBACK = "rollback";

	/**
	 * 构造函数, 采用私有, 防止被直接创建
	 * 
	 * @param param
	 *            连接参数
	 */
	private ConnectionProxy(ConnectionParam param) {
		// 记录日志
		try {
			// 创建连接
			Class.forName(param.getDriver());
			m_conn = DriverManager.getConnection(param.getUrl(), param
					.getUser(), param.getPassword());
			DatabaseMetaData dm = null;
			dm = m_conn.getMetaData();
			// 判断是否支持事务
			m_supportTransaction = dm.supportsTransactions();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object,
	 *      java.lang.reflect.Method, java.lang.Object[])
	 */
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		Object obj = null;
		// 判断是否调用了close的方法，如果调用close方法则把连接置为无用状态
		if (CLOSE.equals(method.getName())) {
			// 设置不使用标志
			setFree(false);
			if (m_statRef != null) {
				m_statRef.close();
			}
			if (m_preStatRef != null) {
				m_preStatRef.close();
			}

			return null;
		}

		// 判断是使用了createStatement语句
		if (CREATESTATE.equals(method.getName())) {
			try {
				obj = method.invoke(m_conn, args);
				m_statRef = (Statement) obj;
				return obj;
			} catch (ClassCastException ex) {
				ex.printStackTrace();
			}
		}

		// 判断是使用了prepareStatement语句
		if (PREPARESTATEMENT.equals(method.getName())) {
			obj = method.invoke(m_conn, args);
			m_preStatRef = (PreparedStatement) obj;
			return obj;
		}

		// 如果不支持事务，就不执行该事物的代码
		if ((COMMIT.equals(method.getName()) || ROLLBACK.equals(method
				.getName()))
				&& (!isSupportTransaction())) {
			return null;
		}

		obj = method.invoke(m_conn, args);

		// 设置最后一次访问时间，以便及时清除超时的连接
		setLastAccessTime(System.currentTimeMillis());

		return obj;
	}

	/**
	 * 创建连接的工厂, 只能让工厂调用
	 * 
	 * @param factory
	 *            要调用工厂, 并且一定被正确初始化
	 * @param param
	 *            连接参数
	 * @return 连接
	 */
	public static ConnectionProxy getConnection(ConnectionFactory factory,
			ConnectionParam param) {
		// 判断是否正确初始化的工厂
		if (factory.isCreate()) {
			ConnectionProxy _conn = new ConnectionProxy(param);
			return _conn;
		} else {
			return null;
		}

	}

	public Connection getFreeConnection() {
		// 返回数据库连接conn的接管类，以便截住close方法
		//Connection cn = (Connection) Proxy.newProxyInstance(m_conn.getClass().getClassLoader(), m_conn.getClass().getInterfaces(), this);
		Connection cn = (Connection) Proxy.newProxyInstance(m_conn.getClass().getClassLoader(), new Class[]{Connection.class}, this);
		return cn;
	}

	/**
	 * 该方法真正的关闭了数据库的连接
	 * 
	 * @throws SQLException
	 */
	public void close() throws SQLException {
		// 由于类属性conn是没有被接管的连接，因此一旦调用close方法后就直接关闭连接
		m_conn.close();
	}

	public void setFree(boolean isFree) {
		this.m_isFree = isFree;
	}

	public boolean isFree() {
		return m_isFree;
	}

	/**
	 * 判断是否支持事务
	 * 
	 * @return boolean
	 */
	public boolean isSupportTransaction() {
		return m_supportTransaction;
	}

	public void setLastAccessTime(long lastAccessTime) {
		this.m_lastAccessTime = lastAccessTime;
	}

	public long getLastAccessTime() {
		return m_lastAccessTime;
	}
}