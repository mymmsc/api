/**
 * @(#)FieldObject.java	6.3.9 09/10/02
 *
 * Copyright 2000-2010 MyMMSC Software Foundation (MSF), Inc. All rights reserved.
 * MyMMSC PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.mymmsc.api.sql;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 单个字段映射表单对象的属性类
 * 
 * @author WangFeng(wangfeng@yeah.net)
 * @version 6.3.9 09/10/02
 * @since mymmsc-boss 6.3.9
 */
public class FieldObject {

	/** 字段关键字 */
	private String m_key = "";
	/** 字段名 */
	private String m_field = "";
	/** 字段名称 */
	private String m_name = "";
	/** 字段描述 */
	private String m_description = "";
	/** 字段数据类型 */
	private String m_type = "";
	/** 字段长度 */
	private int m_length = 0;
	/** 字段属性 */
	private String m_property = "";
	/** 是否主键 */
	private boolean m_is_primary = false;
	/** 是否可为空 */
	private boolean m_is_null = true;
	/** 是否为索引字段 */
	private boolean m_is_index = false;
	/** 默认数值 */
	private String m_default_value = "";
	/** 扩展内容 */
	private String m_excess = "";
	/** 是否在列表页显示 */
	private boolean m_is_list = false;
	/** 是否可以作为检索字段 */
	private boolean m_is_search = false;
	/** 是否可编辑 */
	private boolean m_is_edit = true;
	/** 表单对象名 */
	private String m_form_object = "input";
	/** 表单对象宽度 */
	private int m_form_object_size = 20;
	/** 表单对象最大允许输入宽度 */
	private int m_form_object_maxlength = 20;
	/** 列表或多个数值的表单对象的Hash Table */
	private String m_form_object_value_s = null;
	private Map<String, String> m_form_object_value = null;
	/** 表字段多个数值的分割符 */
	private String m_form_object_valuesplit = "/";
	/** 表单对象事件 */
	private String m_form_object_event = "";
	/** 表单对象属性, 如只读ReadOnly等等单个放置于表单对象的节点中的属性 */
	private String m_form_object_property = "";

	/**
	 * FieldObject 默认构造方法
	 */
	public FieldObject() {
		m_field = "";
		m_name = "";
		m_key = "";
		m_description = "";
		m_type = "";
		m_length = 0;
		m_is_primary = false;
		m_is_null = true;
		m_is_index = false;
		m_default_value = "";
		m_excess = "";
		m_is_list = false;
		m_is_search = false;
		m_is_edit = true;
		m_form_object = "input";
		m_form_object_size = 20;
		m_form_object_maxlength = 20;
		m_form_object_value = new HashMap<String, String>();
		m_form_object_valuesplit = "/";
		m_form_object_event = "";
		m_form_object_property = "";
	}

	/**
	 * FieldObject类 必须参数构造方法
	 * 
	 * @param m_field
	 *            字段名
	 * @param m_name
	 *            显示名称
	 * @param m_key
	 *            小写的字段名
	 * @param m_description
	 *            字段内容描述
	 */
	public FieldObject(String field, String name, String key, String description) {
		m_field = field;
		m_name = name;
		m_key = key;
		m_description = description;
	}

	public String getStr2Int(String s) {
		if (s == null) {
			s = "";
		}
		if (s.length() == 0) {
			s = "0";
		}
		return s;
	}

	/**
	 * @return the m_default_value
	 */
	public String getDefaultValue() {
		return m_default_value;
	}

	/**
	 * @param m_default_value
	 *            the m_default_value to set
	 */
	public void setDefaultValue(String default_value) {
		m_default_value = default_value;
	}

	/**
	 * @return the m_excess
	 */
	public String getExcess() {
		return m_excess;
	}

	/**
	 * @param m_excess
	 *            the m_excess to set
	 */
	public void setExcess(String excess) {
		m_excess = excess;
	}

	/**
	 * @return the m_is_index
	 */
	public boolean isIndex() {
		return m_is_index;
	}

	/**
	 * @param m_is_index
	 *            the m_is_index to set
	 */
	public void setIndexible(boolean is_index) {
		m_is_index = is_index;
	}

	/**
	 * 字段是否显示在列表页
	 * 
	 * @return the m_is_list
	 */
	public boolean isList() {
		return m_is_list;
	}

	/**
	 * 设定字段是否在列表页显示
	 * 
	 * @param m_is_list
	 *            the m_is_list to set
	 */
	public void setListible(boolean is_list) {
		m_is_list = is_list;
	}

	/**
	 * 字段是否可以被搜索
	 * 
	 * @return the m_is_search
	 */
	public boolean isSearch() {
		return m_is_search;
	}

	/**
	 * @param m_is_search
	 *            the m_is_search to set
	 */
	public void setSearchible(boolean is_search) {
		m_is_search = is_search;
	}

	/**
	 * @return the m_is_edit
	 */
	public boolean isEdit() {
		return m_is_edit;
	}

	/**
	 * @param m_is_edit
	 *            the m_is_edit to set
	 */
	public void setEditible(boolean is_edit) {
		m_is_edit = is_edit;
	}

	/**
	 * @return the m_form_object
	 */
	public String getFormObject() {
		return m_form_object;
	}

	/**
	 * @param m_form_object
	 *            the m_form_object to set
	 */
	public void setFormObject(String form_object) {
		m_form_object = form_object;
	}

	/**
	 * @return the m_form_object_size
	 */
	public int getFormObjectSize() {
		return m_form_object_size;
	}

	/**
	 * @param m_form_object_size
	 *            the m_form_object_size to set
	 */
	public void setFormObjectSize(int form_object_size) {
		m_form_object_size = form_object_size;
	}

	/**
	 * @return the m_form_object_maxlength
	 */
	public int getFormObjectMaxLength() {
		return m_form_object_maxlength;
	}

	/**
	 * @param m_form_object_maxlength
	 *            the m_form_object_maxlength to set
	 */
	public void setFormObjectMaxLength(int form_object_maxlength) {
		m_form_object_maxlength = form_object_maxlength;
	}

	/**
	 * @return the m_form_object_valuesplit
	 */
	public String getFormObjectValuesplit() {
		return m_form_object_valuesplit;
	}

	/**
	 * @param m_form_object_valuesplit
	 *            the m_form_object_valuesplit to set
	 */
	public void setFormObjectValuesplit(String form_object_valuesplit) {
		m_form_object_valuesplit = form_object_valuesplit;
	}

	/**
	 * 取得字段在表单的事件
	 * 
	 * @return the m_form_object_event
	 */
	public String getFormObjectEvent() {
		return m_form_object_event;
	}

	/**
	 * 设定字段在表单的事件
	 * 
	 * @param m_form_object_event
	 *            the m_form_object_event to set
	 */
	public void setFormObjectEvent(String form_object_event) {
		m_form_object_event = form_object_event;
	}

	/**
	 * 取得字段在表单的属性
	 * 
	 * @return the m_form_object_property
	 */
	public String getFormObjectProperty() {
		return m_form_object_property;
	}

	/**
	 * 设定字段在表单的属性
	 * 
	 * @param m_form_object_property
	 *            the m_form_object_property to set
	 */
	public void setFormObjectProperty(String form_object_property) {
		m_form_object_property = form_object_property;
	}

	/**
	 * 取得字段名
	 * 
	 * @return 字段名
	 */
	public String getField() {
		return m_field;
	}

	/**
	 * 取得字段名称
	 * 
	 * @return 字段名称
	 */
	public String getName() {
		return m_name;
	}

	/**
	 * 字段关键字
	 * 
	 * @return 字段关键字, 一般为小写的字段名
	 */
	public String getKey() {
		return m_key;
	}

	/**
	 * 取得字段描述内容
	 * 
	 * @return the m_description
	 */
	public String getDescription() {
		return m_description;
	}

	/**
	 * @param m_description
	 *            the m_description to set
	 */
	public void setDescription(String description) {
		m_description = description;
	}

	/**
	 * @return the m_type
	 */
	public String getType() {
		return m_type;
	}

	/**
	 * @param m_type
	 *            the m_type to set
	 */
	public void setType(String type) {
		m_type = type;
	}

	/**
	 * @return the m_length
	 */
	public int getLength() {
		return m_length;
	}

	/**
	 * @param m_length
	 *            the m_length to set
	 */
	public void setLength(int length) {
		m_length = length;
	}

	/**
	 * @return the m_is_primary
	 */
	public boolean isPrimary() {
		return m_is_primary;
	}

	/**
	 * @param m_is_primary
	 *            the m_is_primary to set
	 */
	public void setIsPrimary(boolean is_primary) {
		m_is_primary = is_primary;
	}

	/**
	 * @return the m_is_null
	 */
	public boolean isIsNull() {
		return m_is_null;
	}

	/**
	 * @param m_is_null
	 *            the m_is_null to set
	 */
	public void setIsNull(boolean is_null) {
		m_is_null = is_null;
	}

	/**
	 * @param m_field
	 *            the m_field to set
	 */
	public void setField(String field) {
		m_field = field;
	}

	/**
	 * @param m_name
	 *            the m_name to set
	 */
	public void setName(String name) {
		m_name = name;
	}

	/**
	 * @param m_key
	 *            the m_key to set
	 */
	public void setKey(String key) {
		m_key = key;
	}

	/**
	 * @return the m_form_object_value
	 */
	public String getFormObjectValue(String value) {
		StringBuffer sbuff = new StringBuffer();
		if (getFormObject().equalsIgnoreCase("select")) {
			String sSelected = "";
			for (Iterator<Entry<String, String>> it = m_form_object_value
					.entrySet().iterator(); it.hasNext();) {
				Entry<String, String> e = it.next();
				sSelected = "";
				if (e.getKey().equalsIgnoreCase(value)) {
					sSelected = " selected";
				}
				sbuff.append(String.format(
						"<option value=\"%s\"%s>%s</option>",
						e.getKey().trim(), sSelected, e.getValue().trim()));
			}
		} else {
			sbuff.append(value);
		}

		return sbuff.toString();
	}

	/**
	 * @param m_form_object_value
	 *            the m_form_object_value to set
	 */
	public void setFormObjectValue(Map<String, String> form_object_value) {
		m_form_object_value = form_object_value;
	}

	/**
	 * @return the m_property
	 */
	public String getProperty() {
		return m_property;
	}

	/**
	 * @param m_property
	 *            the m_property to set
	 */
	public void setProperty(String property) {
		m_property = property;
	}

	/**
	 * @return the m_form_object_value_s
	 */
	public String getForm_object_value_s() {
		return m_form_object_value_s;
	}

	/**
	 * @param m_form_object_value_s
	 *            the m_form_object_value_s to set
	 */
	public void setForm_object_value_s(String form_object_value_s) {
		m_form_object_value_s = form_object_value_s;
	}

}
