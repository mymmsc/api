/**
 * @(#)ScriptParser.java	6.3.9 09/10/02
 *
 * Copyright 2000-2010 MyMMSC Software Foundation (MSF), Inc. All rights reserved.
 * MyMMSC PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.mymmsc.api.sql;

/**
 * 脚本解析接口
 * 
 * @author WangFeng(wangfeng@yeah.net)
 * @version 6.3.9 09/10/02
 * @since mymmsc-boss 6.3.9
 */
public interface ScriptParser {
	/**
	 * 打开一个数据库脚本文件
	 * 
	 * @param filename
	 * @return
	 */
	public boolean open(String filename);

	/**
	 * 解析数据库脚本
	 * 
	 * @remark 如果脚本文件有更新, 则自动在下一次调取时重新解析
	 */
	public void parse();

	/**
	 * 获得数据库脚本关于 name 的表描述解析
	 * 
	 * @param name
	 * @return
	 */
	public TableObject getTable(String name);

	/**
	 * 获得数据表数
	 * 
	 * @return
	 */
	public int getTableNumber();
}
