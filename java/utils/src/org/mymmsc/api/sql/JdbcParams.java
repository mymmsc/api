/**
 * @(#)JdbcParams.java	6.3.9 09/10/02
 *
 * Copyright 2000-2010 MyMMSC Software Foundation (MSF), Inc. All rights reserved.
 * MyMMSC PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.mymmsc.api.sql;

import java.io.Serializable;

/**
 * DatabaseConnectionPool构造参数
 * 
 * @author WangFeng
 * @version 6.3.9 2009/09/13
 * @since 6.3.9
 */
public class JdbcParams implements Serializable {
	private static final long serialVersionUID = 6098548636669268054L;
	private String driver;
	private String url;
	private String username;
	private String password;
	private int min;
	private int max;
	private int timeout;
	private String charset = null;

	public JdbcParams() {
		setDriver(null);
		setUrl(null);
		setUsername(null);
		setPassword(null);
		setMin(0);
		setMax(0);
		setTimeout(0);
		setCharset("utf-8");
	}

	/**
	 * @param driver
	 *            the driver to set
	 */
	public void setDriver(String driver) {
		this.driver = driver;
	}

	/**
	 * @return the driver
	 */
	public String getDriver() {
		return driver;
	}

	/**
	 * @param url
	 *            the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param user
	 *            the user to set
	 */
	public void setUsername(String user) {
		this.username = user;
	}

	/**
	 * @return the user
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param password
	 *            the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param min
	 *            the min to set
	 */
	public void setMin(int min) {
		this.min = min;
	}

	/**
	 * @return the min
	 */
	public int getMin() {
		return min;
	}

	/**
	 * @param max
	 *            the max to set
	 */
	public void setMax(int max) {
		this.max = max;
	}

	/**
	 * @return the max
	 */
	public int getMax() {
		return max;
	}

	/**
	 * @param timeout
	 *            the timeout to set
	 */
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	/**
	 * @return the timeout
	 */
	public int getTimeout() {
		return timeout;
	}

	/**
	 * @param charset the charset to set
	 */
	public void setCharset(String charset) {
		this.charset = charset;
	}

	/**
	 * @return the charset
	 */
	public String getCharset() {
		return charset;
	}
}
