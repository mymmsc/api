/**
 * @(#)DsoUtils.java	6.3.9 09/10/02
 *
 * Copyright 2000-2010 MyMMSC Software Foundation (MSF), Inc. All rights reserved.
 * MyMMSC PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.mymmsc.w3c.dso.driver;

import java.util.Properties;

/**
 * 串口通信
 * 
 * @author WangFeng(wangfeng@yeah.net)
 * @version 6.3.9 09/10/02
 * @since mymmsc-gsm 6.3.9
 */
public final class DsoUtils {
	private static boolean m_isWindows = false;
	private static String m_os = null;
	private static Properties m_properties = null;

	/**
	 * 初始化静态类
	 */
	private static void init() {
		// TODO Auto-generated constructor stub
		if (m_os == null) {
			m_properties = System.getProperties();
			m_os = m_properties.getProperty("os.name");
			if (m_os.indexOf("Windows") >= 0) {
				m_isWindows = true;
			}
		}
	}

	/**
	 * mkdirs 创建目录
	 * 
	 * @param path
	 *            String
	 * @return int
	 */
	public static int mkdirs(String path) {
		if (path == null) {
			return 1;
		}
		java.io.File dir = new java.io.File(path);
		if (dir == null) {
			return 1;
		}
		if (dir.isFile()) {
			return 2;
		}
		if (dir.exists()) {
			return 3;
		}
		if (!dir.mkdirs()) {
			return 4;
		}

		return 0;
	}

	/**
	 * 获得资源路径
	 * 
	 * @param cls
	 * @return the special resource path
	 * @see Class#getResource(String)
	 */
	public static String getResourcePath(Class<?> cls) {
		String path = cls.getResource("./").toString();
		path = path.substring(6, path.length() - 1);
		return path;
	}

	public static String getSerialPort(int n) {
		init();
		return String.format("%s%d", m_isWindows ? "COM" : "/dev/ttyS", n);
	}

	public static String getTempPath() {
		init();
		return m_properties.getProperty("java.io.tmpdir") + "/mymmsc";
	}
}
