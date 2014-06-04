/**
 * @(#)AbstractLibrary.java	6.3.9 09/10/02
 *
 * Copyright 2000-2010 MyMMSC Software Foundation (MSF), Inc. All rights reserved.
 * MyMMSC PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.mymmsc.w3c.dso;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.mymmsc.w3c.dso.driver.DsoUtils;

/**
 * 动态库接口通用处理方法
 * 
 * @author WangFeng(wangfeng@yeah.net)
 * @version 6.3.9 09/10/02
 * @since mymmsc-gsm 6.3.9
 */
public abstract class AbstractLibrary implements DsoLibrary {
	private Class<?> m_class = null;
	private String m_drivername = null;
	private boolean m_isWindows = false;
	private String m_os = null;
	protected Logger logger = null;
	private String m_className = null;

	public AbstractLibrary() {
		m_className = getClass().getName();
		logger = Logger.getLogger(m_className);
		try {
			String tmpDir = DsoUtils.getTempPath();
			DsoUtils.mkdirs(tmpDir);
			Handler handler = new FileHandler(tmpDir + "/" + m_className
					+ ".log");
			Formatter fmt = new SimpleFormatter();
			handler.setFormatter(fmt);
			logger.addHandler(handler);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#finalize()
	 */
	@Override
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
		super.finalize();
		close();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mymmsc.w3c.dso.DsoLibrary#load(java.lang.String)
	 */
	@Override
	public void load(String filename) {
		// TODO Auto-generated method stub
		System.loadLibrary(filename);
	}

	/**
	 * 初始化静态类
	 */
	private void init() {
		// TODO Auto-generated constructor stub
		if (m_os != null) {
			return;
		}
		Properties properties = System.getProperties();
		m_os = properties.getProperty("os.name");
		if (m_os.indexOf("Windows") >= 0) {
			m_isWindows = true;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mymmsc.w3c.dso.DsoLibrary#isWindows()
	 */
	@Override
	public boolean isWindows() {
		// TODO Auto-generated method stub
		init();
		return m_isWindows;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mymmsc.w3c.dso.DsoLibrary#forName(java.lang.String)
	 */
	@Override
	public void forName(String clazz) throws ClassNotFoundException {
		// TODO Auto-generated method stub
		m_drivername = clazz;
		m_class = Class.forName(m_drivername);
	}

	/**
	 * 输出动态库路径
	 * 
	 * @see java.library.path
	 */
	public static String getLibraryPath() {
		return System.getProperty("java.library.path");
	}

	public Object newInstance() throws ClassNotFoundException,
			InstantiationException, IllegalAccessException {
		String tmpClass = null;
		if (isWindows()) {
			tmpClass = getWindowsClass();
		} else {
			tmpClass = getUnixClass();
		}
		forName(tmpClass);
		return m_class.newInstance();
	}
}
