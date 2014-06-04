/**
 * @(#)InterfaceHashTable.java	6.3.9 09/10/02
 *
 * Copyright 2000-2010 MyMMSC Software Foundation (MSF), Inc. All rights reserved.
 * MyMMSC PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.mymmsc.api.algorithms;

import java.io.IOException;

/**
 * Hash Table 接口
 * 
 * @author WangFeng(wangfeng@yeah.net)
 * @version 6.3.9 09/10/02
 * @since mymmsc-api 6.3.9
 */
public interface InterfaceHashTable {
	/**
	 * 打开一个数据库
	 * 
	 * @param dbname
	 * @return
	 * @throws IOException
	 */
	public boolean open(String dbname) throws IOException;

	/**
	 * 获取一个元素
	 * 
	 * @param key
	 * @param cls
	 * @return a special object
	 * @throws IOException
	 */
	public Object get(String key, Class<? extends Object> cls) throws Exception;

	/**
	 * 保存一个元素
	 * 
	 * @param key
	 * @param obj
	 * @return
	 * @throws IOException
	 */
	public boolean set(String key, Object obj) throws Exception;

	/**
	 * 删除一个元素
	 * 
	 * @param key
	 * @return
	 */
	public boolean remove(String key) throws Exception;
}
