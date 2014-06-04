/**
 * @(#)SQLCharset.java	6.3.9 09/10/02
 *
 * Copyright 2000-2010 MyMMSC Software Foundation (MSF), Inc. All rights reserved.
 * MyMMSC PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.mymmsc.api.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;

/**
 * 数据库字符集 -- MySQL
 * 
 * @author WangFeng(wangfeng@yeah.net)
 * @version 6.3.9 09/10/02
 * @since mymmsc-api 6.3.9
 * @remark 目前只支持MySQL
 */
public class SQLCharset {
	@SuppressWarnings("unused")
	private static final long serialVersionUID = -1498535122997239767L;
	private Hashtable<String, String> m_charset = null;
	private final static String DEFAULT = "utf-8";
	public String Result = "utf-8";
	public String Apps = "utf-8";

	/**
	 * DatabaseCharset构造函数
	 */
	public SQLCharset() {
		m_charset = new Hashtable<String, String>();
	}

	/**
	 * 获取字符集列表
	 * 
	 * @param conn
	 *            有效的数据库连接
	 * @remark 只是在需要的时候读取一下列表, 不关闭Connection.
	 */
	public void init(Connection conn) {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String charset = DEFAULT;
		try {
			pstmt = conn.prepareStatement("SHOW VARIABLES LIKE 'character%'");
			rs = pstmt.executeQuery();			
			while (rs != null && rs.next()) {
				m_charset.put(rs.getString(1).trim(), rs.getString(2).trim());
			}
			// 获得数据连接
			charset = getCharset("character_set_connection");
			if (charset == null || charset.equals("")) {
				charset = getCharset("character_set_client");
			}
			if (charset == null || charset.length() == 0) {
				Apps = DEFAULT;
			} else {
				Apps = charset;
			}
			charset = getCharset("character_set_results");
			if (charset == null || charset.equals("")) {
				charset = getCharset("character_set_database");
			}
			if (charset == null || charset.length() == 0) {
				Result = DEFAULT;
			} else {
				Result = charset;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			SQLApi.closeQuietly(rs);
			SQLApi.closeQuietly(pstmt);
		}
	}

	/**
	 * 得到指定关键字的字符集
	 * 
	 * @param key
	 * @return
	 */
	public String getCharset(String key) {
		return m_charset.get(key).trim();
	}
}
