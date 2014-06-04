/**
 * @(#)ResultSetEx.java	6.3.9 09/10/02
 *
 * Copyright 2000-2010 MyMMSC Software Foundation (MSF), Inc. All rights reserved.
 * MyMMSC PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.mymmsc.api.sql;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.mymmsc.api.assembly.Api;
import org.mymmsc.api.category.Encoding;

/**
 * 数据表记录集封装
 * 
 * @author WangFeng(wangfeng@yeah.net)
 * @version 6.3.9 09/10/02
 * @since mymmsc-api 6.3.9
 */
public class ResultSetEx {
	private ResultSet pRst = null;
	private ResultSetMetaData pRstMeta = null;
	private int colCount = 0;
	private String encDb = null;
	private String encApps = null;

	public ResultSetEx(ResultSet rs, String enc_db, String enc_apps) {
		pRst = rs;
		encDb = enc_db;
		encApps = enc_apps;
		try {
			pRstMeta = pRst.getMetaData();
			colCount = pRstMeta.getColumnCount();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public ResultSetEx(ResultSet rs) {
		this(rs, Encoding.MySQL, Encoding.GB18030);
	}

	public void close() {
		if (pRst != null) {
			try {
				pRst.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * finalize
	 * 
	 * @remark 自动回收
	 */
	protected void finalize() {
		close();
	}

	public boolean next() throws SQLException {
		return pRst.next();
	}

	/**
	 * 获取字段数值 -- 字符串
	 * 
	 * @param field
	 *            字段名
	 */
	public String getStr(String field) {
		String value = null;
		try {
			value = pRst.getString(field);
		} catch (SQLException e) {
			value = null;
		}
		if (value == null) {
			value = "";
		}
		value = Api.iconv(value, encDb, encApps);
		return value.trim();
	}

	/**
	 * 转换设定字段内容的字符集
	 * 
	 * @param value
	 * @return String
	 */
	public static String setField2String(String value) {
		return Api.iconv(value, Encoding.MySQL, Encoding.GB18030);
	}

	/**
	 * getInt
	 * 
	 * @param rs
	 *            ResultSet
	 * @param sField
	 *            String
	 * @return int
	 */
	public int getInt(String field) {
		int __nValue = 0;
		String __sValue = getStr(field);
		if (__sValue == "" || __sValue.length() == 0) {
			__sValue = "0";
		}
		__nValue = (new Double(__sValue).intValue());
		return __nValue;
	}

	/*
	 * 将rs结果转换成对象列表
	 * 
	 * @param rs jdbc结果集
	 * 
	 * @param clsFields 对象的映射类 return 封装了对象的结果列表
	 * 
	 * @param maxrows 最大行数
	 */
	public List<Object> getRow(Class<?> clsFields, int maxrows) {
		List<Object> list = new ArrayList<Object>();
		int row = 0;
		try {
			// 业务对象的属性数组
			// Class.forName(clsFields.getName());
			Field[] fields = clsFields.getDeclaredFields();
			Field f = null;
			while (pRst.next() && row++ < maxrows) {// 对每一条记录进行操作
				Object obj = clsFields.newInstance();// 构造业务对象实体
				// 将每一个字段取出进行赋值
				for (int i = 1; i <= colCount; i++) {
					Object value = pRst.getObject(i);
					// 寻找该列对应的对象属性
					for (int j = 0; j < fields.length; j++) {
						f = fields[j];
						System.out.println(f.getName() + ","
								+ pRstMeta.getColumnName(i));
						// 如果匹配进行赋值
						if (f.getName().equalsIgnoreCase(
								pRstMeta.getColumnName(i))) {
							boolean flag = f.isAccessible();
							f.setAccessible(true);
							f.set(obj, value);
							f.setAccessible(flag);
						}
					}
				}
				list.add(obj);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		return list;
	}
}
