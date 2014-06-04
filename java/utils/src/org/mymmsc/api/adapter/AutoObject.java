/**
 * @(#)AutoObject.java	6.3.9 09/11/02
 *
 * Copyright 2000-2010 MyMMSC Software Foundation (MSF), Inc. All rights reserved.
 * MyMMSC PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.mymmsc.api.adapter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mymmsc.api.assembly.Api;

/**
 * 全局的一个对象抽象类
 * 
 * @author WangFeng(wangfeng@yeah.net)
 * @version 6.3.9 09/10/02
 * @since mymmsc-api 6.3.9
 * @remark 对象增加Log4j日志功能, 需要有common-logging.jar和log4j.jar
 */
public abstract class AutoObject {
	/** log4j适配对象 */
	private Log m_logger = null;
	/** 是否控制台输出信息 */
	private boolean bConsole = false;

	/**
	 * 自动适配对象
	 */
	public AutoObject() {
		init();
	}

	public AutoObject(boolean bConsole) {
		this.bConsole = bConsole;
		init();
	}
	
	/**
	 * 获取资源文件流
	 * @param name
	 * @return
	 */
	protected InputStream getResource(String name) {
		return getClass().getResourceAsStream(name);
	}
	
	/**
	 * 缓存资源文件
	 * @param name
	 * @return
	 */
	protected String storeResouce(String name) {
		String sRet = null;
		String tmpPath = Api.getTempDir();
		String pkgName = this.getClass().getPackage().getName().replaceAll("\\.", "/");
		tmpPath += '/' + pkgName;
		Api.mkdirs(tmpPath);
		tmpPath += '/' + name;
		byte[] buff = new byte[4096];
		int len = -1;
		InputStream is = getResource(name);
		File file = new File(tmpPath);
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);
			while ((len = is.read(buff)) > 0) {
				fos.write(buff, 0, len);
			}
			sRet = tmpPath;
		} catch (IOException e) {
			error("缓存资源文件失败", e);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					//
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					//
				}
			}
		}
		return sRet;
	}
	
	/**
	 * 重新初始化配置信息
	 */
	private void init() {
		try {
			Class.forName("org.apache.log4j.PropertyConfigurator");
			Class.forName("org.apache.log4j.Logger");
			m_logger = LogFactory.getLog(getClass());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 关闭对象
	 */
	public abstract void close();

	/**
	 * 控制台输出
	 * 
	 * @param arg
	 */
	protected void print(Object arg) {
		if (bConsole) {
			System.out.println(arg);
		}
	}

	/**
	 * 一般信息输出
	 * 
	 * @param arg
	 *            信息内容
	 */
	protected void info(Object arg) {
		print(arg);
		if (m_logger != null) {
			m_logger.info(arg);
		}
	}

	/**
	 * 一般信息输出
	 * 
	 * @param arg0
	 * @param arg1
	 */
	protected void info(Object arg0, Throwable arg1) {
		if (m_logger != null) {
			m_logger.info(arg0, arg1);
		}
	}

	/**
	 * 错误信息输出
	 * 
	 * @param arg
	 */
	protected void error(Object arg) {
		if (m_logger != null) {
			m_logger.error(arg);
		}
	}

	/**
	 * 错误信息输出
	 * 
	 * @param arg0
	 * @param arg1
	 */
	protected void error(Object arg0, Throwable arg1) {
		if (m_logger != null) {
			m_logger.error(arg0, arg1);
		}
	}

	/**
	 * 警告信息
	 * 
	 * @param arg
	 */
	protected void warn(Object arg) {
		if (m_logger != null) {
			m_logger.warn(arg);
		}
	}

	/**
	 * 警告信息
	 * 
	 * @param arg0
	 * @param arg1
	 */
	protected void warn(Object arg0, Throwable arg1) {
		if (m_logger != null) {
			m_logger.warn(arg0, arg1);
		}
	}
}
