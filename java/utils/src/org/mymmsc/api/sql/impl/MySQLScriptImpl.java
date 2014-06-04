/**
 * @(#)MySQLScriptImpl.java	6.3.9 09/10/02
 *
 * Copyright 2000-2010 MyMMSC Software Foundation (MSF), Inc. All rights reserved.
 * MyMMSC PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.mymmsc.api.sql.impl;

import java.io.BufferedReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

import org.mymmsc.api.assembly.Api;
import org.mymmsc.api.assembly.RegExp;
import org.mymmsc.api.sql.AbstractScript;
import org.mymmsc.api.sql.FieldObject;
import org.mymmsc.api.sql.ScriptParser;
import org.mymmsc.api.sql.TableObject;

/**
 * 解析MySQL数据库脚本
 * 
 * @author WangFeng(wangfeng@yeah.net)
 * @company MyMMSC Software Foundation (MSF)
 * @version 6.3.9 09/10/02
 * @since mymmsc-dba 6.3.9
 */
public class MySQLScriptImpl extends AbstractScript {

	/**
	 * MySQLScriptImpl构造函数
	 */
	public MySQLScriptImpl() {
		super();
	}

	/* (non-Javadoc)
	 * @see org.mymmsc.dba.adapter.ScriptParser#parse()
	 */
	@Override
	public void parse() {
		if (!isMotified()) {
			// 如果没有更新则不改变 [WangFeng at 2009-12-27 下午07:55:46]
			return;
		} else {
			warn("重新加载");
		}
		BufferedReader reader = getBufferedReader();
		if (reader != null) {
			try {
				String sbuf = "";
				boolean bGetDatabase = false;
				String sTable = "";
				boolean bField = false;
				TableObject to = null;
				ArrayList<FieldObject> listFields = null;
				ArrayList<String> l = null;
				while ((sbuf = reader.readLine()) != null) {
					sbuf = sbuf.trim();
					//System.out.println("脚本当前行内容: <" + sbuf + ">");

					// 取得数据库名
					if (!bGetDatabase) {
						l = RegExp.match(sbuf, "USE\\s+([\\w\\d_]+)\\s*;");
						if (l != null && l.size() > 0) {
							bGetDatabase = true;
							// context.put("db_name", l.get(0));
							//System.out.println("数据库名: " + l.get(0));
						}
					}
					// 取得数据表的名称
					if (sbuf.indexOf("CREATE TABLE") >= 0) {
						String[] sl = sbuf.split("\\.");
						sTable = sl[1].replaceAll("`", "");
						to = new TableObject();
						to.setName(sTable);
						// System.out.println("数据表名: " + sl[1]);
						// context.put("Tables", sl[1]);
					} else if (!bField && sbuf.indexOf("(") == 0) {
						// 字段解析 开始
						bField = true;
						listFields = new ArrayList<FieldObject>();
					} else if (to != null && bField && sbuf.startsWith(")")) {
						// 字段解析 结束
						info("增加数据表: " + sTable);
						bField = false;
						to.setFields(listFields);
						// listTables.add(to);
						put(sTable, to);
						info("目前完成数据表结构分析: " + getTableNumber());
						to = null;
					} else if (bField && !sbuf.startsWith("KEY ")) {
						FieldObject fo = new FieldObject();
						String fieldKey = "";
						String fieldName = "";
						String fieldTag = "";
						String fieldDesc = "";
						String fieldType = "";
						int fieldLength = 0;
						String fieldProperty = "";
						String fieldDefaultValue = "";

						boolean fieldIsPrimary = false;
						boolean fieldIsNull = true;
						boolean fieldIsIndex = false;
						boolean fieldIsList = false;
						boolean fieldIsEdit = true;
						boolean fieldIsSearch = false;

						String formObject = "INPUT";
						Map<String, String> formObjectValue = null;
						String formObjectEvent = "";
						int formObjectMaxLength = 70;
						int formObjectSize = 20;
						String formObjectValueS = "";
						String formObjectProperty = "";

						String exp = null;
						// 字段解析
						sbuf = sbuf.replaceAll("`", "");
						String[] sl = sbuf.split("\\s*COMMENT\\s*\\'|\\',");
						// 数据表字段解析
						if (sl.length >= 1) {
							//System.out.println(sl[0]);
							exp = "(\\w+)\\s+(\\w+)\\s+(\\(([0-9]+)\\))?\\s*(BINARY|UNSIGNED|UNSIGNED ZEROFILL)?\\s*(NULL|NOT NULL)?\\s*(DEFAULT '(.*?)')?\\s*(AUTO_INCREMENT)?\\s*(PRIMARY KEY)?\\s*";
							l = RegExp.match(sl[0], exp);
							if (l != null && l.size() > 0) {
								// 字段
								fieldName = l.get(0);
								// 关键字
								fieldKey = fieldName.toLowerCase();
								// 字段类型
								fieldType = l.get(1);
								// 字段长度
								fieldLength = Api.valueOf(int.class, l.get(3));
								fieldProperty = l.get(4);
								fieldDefaultValue = l.get(7);
								// String value_list = "";
								fieldIsPrimary = Api.toString(l.get(9)).equalsIgnoreCase("PRIMARY KEY");

								fieldIsNull = Api.toString(l.get(5)).equalsIgnoreCase("NULL");

								// 表单对象名称
								formObject = "input";
								formObjectEvent = "";
								if (fieldType.equalsIgnoreCase("DATETIME")) {
									// 给日期型字段设置日历功能
									formObjectEvent = "onclick=\"popUpCalendar(this, this, 'yyyy-mm-dd')\"";
								}
								// 表单对象允许输入的最大长度
								int tmpLength = Api.stristr(fieldType,
										"INT") ? 11 : fieldLength;
								// int form_object_maxlength = tmpLength;
								if (fieldType.equalsIgnoreCase("text")) {
									formObjectMaxLength = 10240;
									tmpLength = 70;
									formObject = "textarea";
								} else {
									formObjectMaxLength = tmpLength;
								}
								// 表单对象的尺寸
								formObjectSize = (tmpLength >= 70 ? 70
										: tmpLength);
							}
						}
						// 表字段表单特性解析
						if (sl.length >= 2) {
							// 字段描述及SELECT Optios选项
							// exp = ":\\s*|,\\s*";
							exp = ":";
							sl[1] = sl[1].trim();
							String[] ld = sl[1].split(exp);
							if (ld.length > 0) {
								fieldDesc = ld[0].trim();
								String[] posDesc = fieldDesc.split("\\(");
								fieldTag = posDesc[0];
								//fieldDesc = "";
							}
							if (ld.length > 1) {
								formObjectValueS = ld[1].trim();
								formObjectValue = RegExp
										.match(formObjectValueS);
								if (formObjectValue == null
										|| formObjectValue.size() == 0) {
									fieldDesc = formObjectValueS;
									formObjectValue = null;
									formObjectValueS = "";
								} else {
									formObject = "select";
								}
							}
							if (fieldDesc.contains("?")) {
								fieldIsSearch = true;
							}
							if (fieldDesc.contains("$")) {
								fieldIsList = true;
							}
							if (fieldDesc.contains("!")) {
								formObject = "file";
							}
						}
						// 表字段表单特性解析
						if (sl.length >= 3) {
							if (formObjectEvent.length() > 0) {
								formObjectEvent += " " + sl[2].trim();
							}
						}
						// 表字段表单特性解析
						if (sl.length >= 8) {
							formObjectProperty = sl[7].trim();
						}
						fo.setKey(fieldKey);
						fo.setField(fieldName);
						fo.setType(fieldType);
						fo.setName(fieldTag);
						fo.setDescription(fieldDesc);
						fo.setLength(fieldLength);
						fo.setProperty(fieldProperty);
						fo.setDefaultValue(fieldDefaultValue);
						fo.setIsPrimary(fieldIsPrimary);

						fo.setIsNull(fieldIsNull);
						fo.setIndexible(fieldIsIndex);
						fo.setListible(fieldIsList);
						fo.setEditible(fieldIsEdit);
						fo.setSearchible(fieldIsSearch);

						fo.setFormObject(formObject);
						// 如果是INPUT对象且没有按键事件
						String formRegex = "";
						if (formObject.equalsIgnoreCase("input")
								&& !Api
										.stristr(formObjectEvent, "OnKeyUp")) {
							if (Api.stristr(fieldKey, "mail")) {
								// 匹配电子邮箱地址
								formRegex = "regex_email";
							} else if (Api.stristr(fieldType, "int")
									|| Api.stristr(fieldKey, "port")) {
								// 匹配正整数
								formRegex = "regex_int";
							} else if (fieldKey.length() > 2
									&& Api.stristr(fieldKey, "id")) {
								// 匹配某个编码
								formRegex = "regex_id";
							} else if (Api.stristr(fieldName, "IP")) {
								// 匹配IP地址
								formRegex = "regex_ip";
							} else {
								formRegex = "";
							}
							if (formRegex.length() > 0) {
								formObjectEvent = "OnKeyUp='myjs_checkall("
										+ fieldKey + ", " + formRegex + ")'";
							}
						}
						fo.setFormObjectEvent(formObjectEvent);
						fo.setFormObjectValue(formObjectValue);
						fo.setForm_object_value_s(formObjectValueS);
						fo.setFormObjectSize(formObjectSize);
						fo.setFormObjectMaxLength(formObjectMaxLength);
						fo.setFormObjectProperty(formObjectProperty);
						listFields.add(fo);
					}
				}
			} catch (Exception e) {
				error("", e);
				reset();
			}
		}
	}

	public static void main(String[] args) throws SQLException {
		ScriptParser sp = new MySQLScriptImpl();

		sp.open("C:/projects/ifengzi/interface/dsmp/WebContent/WEB-INF/dsmp.mysql");
		sp.parse();

		return;
	}
}
