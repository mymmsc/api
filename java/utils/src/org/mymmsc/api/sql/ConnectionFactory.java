/**
 * @(#)ConnectionFactory.java	6.3.9 09/10/02
 *
 * Copyright 2000-2010 MyMMSC Software Foundation (MSF), Inc. All rights reserved.
 * MyMMSC PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.mymmsc.api.sql;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedHashSet;

/**
 * 数据库连接池工厂类
 * 
 * @author WangFeng(wangfeng@yeah.net)
 * @version 6.3.9 09/10/02
 * @since mymmsc-api 6.3.9
 */
public class ConnectionFactory {

	private static ConnectionFactory m_instance = null;

	/** 在使用的连接池 */
	private LinkedHashSet<ConnectionProxy> m_ConnectionPool = null;

	/** 空闲连接池 */
	private LinkedHashSet<ConnectionProxy> m_FreeConnectionPool = null;

	/** 最大连接数 */
	private int m_MaxConnectionCount = 10;

	/** 最小连接数S */
	private int m_MinConnectionCount = 2;

	/** 当前连接数 */
	private int m_current_conn_count = 0;

	/** 连接参数 */
	private ConnectionParam m_connparam = null;

	/** 是否创建工厂的标志 */
	private boolean m_isflag = false;

	/** 是否支持事务 */
	private boolean m_supportTransaction = false;

	/** 定义管理策略 */
	private int m_ManageType = 0;

	/**
	 * 构造器
	 */
	private ConnectionFactory() {
		m_ConnectionPool = new LinkedHashSet<ConnectionProxy>();
		m_FreeConnectionPool = new LinkedHashSet<ConnectionProxy>();
	}

	/**
	 * 使用指定的参数创建一个连接池
	 * 
	 * @throws SQLException
	 */
	public ConnectionFactory(ConnectionParam param, FactoryParam fparam)
			throws SQLException {
		if ((param == null) || (fparam == null)) {
			throw new SQLException("ConnectionParam和FactoryParam不能为空");
		}
		if (m_instance == null) {
			synchronized (ConnectionFactory.class) {
				if (m_instance == null) {
					// 参数定制
					m_instance = new ConnectionFactory();
					m_connparam = param;
					m_instance.m_connparam = m_connparam;
					m_instance.m_MaxConnectionCount = fparam
							.getMaxConnectionCount();
					m_instance.m_MinConnectionCount = fparam
							.getMinConnectionCount();
					m_instance.m_ManageType = fparam.getManageType();
					m_instance.m_isflag = true;

					// 初始化，创建MinConnectionCount个连接
					System.out.println("connection factory 创建！");
					try {
						for (int i = 0; i < m_instance.m_MinConnectionCount; i++) {
							ConnectionProxy conn = ConnectionProxy
									.getConnection(m_instance,
											m_instance.m_connparam);
							if (conn == null) {
								continue;
							}
							System.out.println("connection创建");
							m_instance.m_FreeConnectionPool.add(conn);
							// 加入空闲连接池
							m_instance.m_current_conn_count++;
							// 标志是否支持事务
							m_instance.m_supportTransaction = conn
									.isSupportTransaction();
						}
					} catch (Exception ex) {
						ex.printStackTrace();
					}

					// 根据策略判断是否需要查询
					if (m_instance.m_ManageType != 0) {
						Thread t = new Thread(new FactoryManageThread(
								m_instance));
						t.start();
					}
				}
			}
		}
	}

	/**
	 * 标志工厂是否已经创建
	 * 
	 * @return boolean
	 */
	public boolean isCreate() {
		return m_instance.m_isflag;
	}

	/**
	 * 从连接池中取一个空闲的连接
	 * 
	 * @return Connection
	 * @throws SQLException
	 */
	public synchronized Connection getFreeConnection() throws SQLException {
		Connection cn = null;
		// 获取空闲连接
		Iterator<?> ir = m_instance.m_FreeConnectionPool.iterator();
		while (ir.hasNext()) {
			ConnectionProxy conn = (ConnectionProxy) ir.next();
			// 找到未用的连接
			if (!conn.isFree()) {
				cn = conn.getFreeConnection();
				conn.setFree(true);
				// 移出空闲区
				m_instance.m_FreeConnectionPool.remove(conn);

				// 加入连接池
				m_instance.m_ConnectionPool.add(conn);
				break;
			}
		}

		// 检查空闲池是否为空
		if (m_instance.m_FreeConnectionPool.isEmpty()) {
			// 再检查是否能够分配
			if (m_instance.m_current_conn_count < m_instance.m_MaxConnectionCount) {
				// 新建连接到空闲连接池
				int newCount = 0;
				// 取得要建立的数目
				if (m_instance.m_MaxConnectionCount
						- m_instance.m_current_conn_count >= m_instance.m_MinConnectionCount) {
					newCount = m_instance.m_MinConnectionCount;
				} else {
					newCount = m_instance.m_MaxConnectionCount
							- m_instance.m_current_conn_count;
				}
				// 创建连接
				for (int i = 0; i < newCount; i++) {
					ConnectionProxy _conn = ConnectionProxy.getConnection(
							m_instance, m_connparam);
					m_instance.m_FreeConnectionPool.add(_conn);
					m_instance.m_current_conn_count++;
				}
			} else {
				// 如果不能新建，检查是否有已经归还的连接
				ir = m_instance.m_ConnectionPool.iterator();
				while (ir.hasNext()) {
					ConnectionProxy _conn = (ConnectionProxy) ir.next();
					if (!_conn.isFree()) {
						cn = _conn.getFreeConnection();
						_conn.setFree(false);
						m_instance.m_ConnectionPool.remove(_conn);
						m_instance.m_FreeConnectionPool.add(_conn);
						break;
					}
				}
			}
		}

		// 再次检查是否能分配连接
		if (cn == null) {
			ir = m_instance.m_FreeConnectionPool.iterator();
			while (ir.hasNext()) {
				ConnectionProxy _conn = (ConnectionProxy) ir.next();
				if (!_conn.isFree()) {
					cn = _conn.getFreeConnection();
					_conn.setFree(true);
					m_instance.m_FreeConnectionPool.remove(_conn);
					m_instance.m_ConnectionPool.add(_conn);
					break;
				}
			}
			if (cn == null) {
				// 如果不能则说明无连接可用
				throw new SQLException("没有可用的数据库连接");
			}
		}

		// System.out.println("Get Connection");
		return cn;
	}

	/**
	 * 关闭该连接池中的所有数据库连接
	 * 
	 * @throws SQLException
	 */
	public synchronized void close() throws SQLException {
		this.m_isflag = false;
		SQLException sqlError = null;
		// 关闭空闲池
		Iterator<?> ir = m_instance.m_FreeConnectionPool.iterator();
		while (ir.hasNext()) {
			try {
				((ConnectionProxy) ir.next()).close();
				System.out.println("Close connection:Free");
				m_instance.m_current_conn_count--;
			} catch (Exception ex) {
				if (ex instanceof SQLException) {
					sqlError = (SQLException) ex;
				}
			}
		}

		// 关闭在使用的连接池
		ir = m_instance.m_ConnectionPool.iterator();
		while (ir.hasNext()) {
			try {
				((ConnectionProxy) ir.next()).close();
				System.out.println("Close connection:Using");
			} catch (Exception ex) {
				if (ex instanceof SQLException) {
					sqlError = (SQLException) ex;
				}
			}
		}

		if (sqlError != null) {
			throw sqlError;
		}
	}

	/**
	 * 返回是否支持事务
	 * 
	 * @return boolean
	 */
	public boolean isSupportTransaction() {
		return m_instance.m_supportTransaction;
	}

	/**
	 * 连接池调度管理
	 * 
	 */
	public void schedule() {
		// 再检查是否能够分配
		Iterator<?> ir = null;
		// 检查是否有已经归还的连接
		ir = m_instance.m_ConnectionPool.iterator();
		while (ir.hasNext()) {
			ConnectionProxy _conn = (ConnectionProxy) ir.next();
			if (!_conn.isFree()) {
				_conn.setFree(false);
				m_instance.m_ConnectionPool.remove(_conn);
				m_instance.m_FreeConnectionPool.add(_conn);
				break;
			}
		}

		if (m_instance.m_current_conn_count < m_instance.m_MaxConnectionCount) {
			// 新建连接到空闲连接池
			int newCount = 0;
			// 取得要建立的数目
			if (m_instance.m_MaxConnectionCount
					- m_instance.m_current_conn_count >= m_instance.m_MinConnectionCount) {
				newCount = m_instance.m_MinConnectionCount;
			} else {
				newCount = m_instance.m_MaxConnectionCount
						- m_instance.m_current_conn_count;
			}
			// 创建连接
			for (int i = 0; i < newCount; i++) {
				ConnectionProxy _conn = ConnectionProxy.getConnection(
						m_instance, m_connparam);
				m_instance.m_FreeConnectionPool.add(_conn);
				m_instance.m_current_conn_count++;
			}
		}
	}

}