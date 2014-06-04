/**
 * @(#)AbstractScript.java	6.3.9 09/10/02
 *
 * Copyright 2000-2010 MyMMSC Software Foundation (MSF), Inc. All rights reserved.
 * MyMMSC PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.mymmsc.api.sql;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import org.mymmsc.api.adapter.AutoObject;
import org.mymmsc.api.assembly.Api;
import org.mymmsc.api.category.Encoding;

/**
 * 脚本解析抽象类
 * 
 * @author WangFeng(wangfeng@yeah.net)
 * @version 6.3.9 09/10/02
 * @since mymmsc-commons 6.3.9
 */
public abstract class AbstractScript extends AutoObject implements ScriptParser {
	private File m_file = null;
	private FileInputStream m_file_in = null;
	private String m_scriptfile = null;
	private long m_lastmotified = 0;
	private HashMap<String, TableObject> m_hmTables = null;

	/**
	 * AbstractScript构造函数
	 */
	public AbstractScript() {
		super();
		m_lastmotified = 0;
		m_scriptfile = null;
		m_hmTables = new HashMap<String, TableObject>();
	}

	public void close() {
		try {
			m_file_in.close();
		} catch (IOException e) {
			error("文件关闭失败", e);
		}
		m_hmTables.clear();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#finalize()
	 */
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
	}

	public boolean open(String filename) {
		boolean bRet = false;
		m_file = new File(filename);
		if (!m_file.exists()) {
			bRet = true;
		}
		try {
			m_file_in = new FileInputStream(m_file);
			m_scriptfile = filename;
			bRet = true;
		} catch (FileNotFoundException e) {
			error("文件不存在", e);
		}
		return bRet;
	}
	
	protected void reset() {
		m_lastmotified = 0;
	}
	
	protected boolean isMotified() {
		boolean bRet = false;
		long tmpLast = Api.getLastModified(m_scriptfile);
		if (m_lastmotified == 0 || m_lastmotified < tmpLast) {
			m_lastmotified = tmpLast;
			bRet = true;
		}
		return bRet;
	}

	@SuppressWarnings("resource")
	protected BufferedReader getBufferedReader() {
		BufferedReader reader = null;
		try {
			InputStreamReader read = new InputStreamReader (new FileInputStream(m_file), Encoding.Default);
			reader = new BufferedReader(read);
			//reader = new BufferedReader(new FileReader(m_file));
		} catch (FileNotFoundException e) {
			error("文件关闭失败", e);
		} catch (UnsupportedEncodingException e) {
			error("文件关闭失败", e);
		}
		
		return reader;
	}

	/**
	 * 压入一个表结构分析列表
	 * 
	 * @param tableName
	 * @param to
	 */
	protected void put(String tableName, TableObject to) {
		m_hmTables.put(tableName, to);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mymmsc.framework.boss.adapter.ScriptParser#getTable(java.lang.String)
	 */
	@Override
	public TableObject getTable(String name) {
		return m_hmTables.get(name);
	}

	public int getTableNumber() {
		return m_hmTables.size();
	}
}
