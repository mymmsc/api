/**
 * @(#)AbstractFile.java	6.3.9 09/10/02
 *
 * Copyright 2000-2010 MyMMSC Software Foundation (MSF), Inc. All rights reserved.
 * MyMMSC PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.mymmsc.api.adapter;

import java.io.File;

import org.mymmsc.api.assembly.Api;

/**
 * 文件处理抽象类
 * 
 * @author WangFeng(wangfeng@yeah.net)
 * @version 6.3.9 09/10/02
 * @since mymmsc-api 6.3.9
 */
public abstract class AbstractFile {

	private String m_filename = null;
	private long m_lastmotified = 0;

	/**
	 * AbstractFile构造函数
	 * 
	 * @param file
	 *            已经打开的文件对象
	 */
	public AbstractFile(File file) {
		m_filename = file.getPath();
		m_lastmotified = Api.getLastModified(m_filename);
	}

	/**
	 * AbstractFile构造函数
	 * 
	 * @param filename
	 */
	public AbstractFile(String filename) {
		m_filename = filename;
		m_lastmotified = Api.getLastModified(m_filename);
	}

	/**
	 * 检查文件是否被修改
	 * 
	 * @return true
	 */
	public boolean isMotified() {
		boolean bRet = false;
		long tm = Api.getLastModified(m_filename);
		if (tm > m_lastmotified) {
			m_lastmotified = tm;
			bRet = true;
		}
		return bRet;
	}

}
