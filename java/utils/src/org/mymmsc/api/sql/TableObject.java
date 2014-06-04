/**
 * @(#)TableObject.java	6.3.9 09/10/02
 *
 * Copyright 2000-2010 MyMMSC Software Foundation (MSF), Inc. All rights reserved.
 * MyMMSC PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.mymmsc.api.sql;

import java.util.ArrayList;

import org.mymmsc.api.category.Encoding;

/**
 * 数据表结构封装类
 * 
 * @author WangFeng(wangfeng@yeah.net)
 * @version 6.3.9 09/10/02
 * @since mymmsc-boss 6.3.9
 */
public class TableObject {

	private String m_name = "";
	private ArrayList<FieldObject> m_fields = null;
	private String m_encoding = Encoding.GBK;

	public TableObject() {
		//
	}

	public TableObject(String name, ArrayList<FieldObject> fields) {
		m_name = name;
		m_fields = fields;
	}

	public void setEncoding(String encoding) {
		m_encoding = encoding;
	}

	public String getEncoding() {
		return m_encoding;
	}

	/**
	 * @return the m_fields
	 */
	public ArrayList<FieldObject> getFields() {
		return m_fields;
	}

	/**
	 * @param m_fields
	 *            the m_fields to set
	 */
	public void setFields(ArrayList<FieldObject> fields) {
		m_fields = fields;
	}

	/**
	 * @return the m_name
	 */
	public String getName() {
		return m_name;
	}

	/**
	 * @param m_name
	 *            the m_name to set
	 */
	public void setName(String name) {
		m_name = name;
	}

}
