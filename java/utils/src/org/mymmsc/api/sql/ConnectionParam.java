/**
 * @(#)ConnectionParam.java	6.3.9 09/10/02
 *
 * Copyright 2000-2010 MyMMSC Software Foundation (MSF), Inc. All rights reserved.
 * MyMMSC PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.mymmsc.api.sql;

import java.io.Serializable;

/**
 * 实现数据库连接的参数类
 * 
 * @author mark
 * 
 */
public class ConnectionParam implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/** 数据库驱动程序 */
	private String driver = null;
	/** 数据连接的URL */
	private String url = null;
	/** 数据库用户名 */
	private String user = null;
	/** 数据库密码 */
	private String password = null;

	public String getDriver() {
		return driver;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public ConnectionParam(String driver, String url, String user,
			String password) {
		this.driver = driver;
		this.url = url;
		this.user = user;
		this.password = password;
	}

	/**
	 * @see java.lang.Object#clone()
	 */
	public Object clone() {
		ConnectionParam param = new ConnectionParam(driver, url, user, password);
		return param;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (obj instanceof ConnectionParam) {
			ConnectionParam param = (ConnectionParam) obj;
			return ((driver.compareToIgnoreCase(param.getDriver()) == 0)
					&& (url.compareToIgnoreCase(param.getUrl()) == 0)
					&& (user.compareToIgnoreCase(param.getUser()) == 0) && (password
					.compareToIgnoreCase(param.getPassword()) == 0));
		}
		return false;
	}

}
