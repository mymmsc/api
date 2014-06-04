/**
 * @(#)Configure.java	6.3.9 09/10/02
 *
 * Copyright 2000-2010 MyMMSC Software Foundation (MSF), Inc. All rights reserved.
 * MyMMSC PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.mymmsc.api.io;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 属性文件操作类
 * 
 * @author WangFeng(wangfeng@yeah.net)
 * @version 6.3.9 09/10/02
 * @since mymmsc-api 6.3.9
 */
public class Configure {
	public static final int TYPE_INI = 0x01;
	public static final int TYPE_PROPERTIES = 0x02;
	private InputStream is = null;
	private Properties properties = null;

	/**
	 * 加载属性文件
	 * 
	 * @param filename
	 * @throws FileNotFoundException
	 */
	public Configure(String filename) throws FileNotFoundException {
		// InputStream is = getClass().getResourceAsStream(filename);
		is = new FileInputStream(filename);
		load(is);
	}

	/**
	 * 加载属性文件, 从类包路径中以短类名
	 * 
	 * @param clazz
	 * @remark 此方法尚未得到验证
	 */
	public Configure(Class<?> clazz) {
		is = clazz.getResourceAsStream(clazz.getSimpleName());
		load(is);
	}

	public boolean load(InputStream is) {
		boolean bRet = true;
		properties = new Properties();
		try {
			properties.load(is);
		} catch (IOException ex) {
			ex.printStackTrace();
			bRet = false;
		}
		return bRet;
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		properties.clear();
		// 在自动回收中主动关闭InputStream会出现本地资源释放异常, 这种情况应该和JDK版本有关.
		// is.close();
	}

	/**
	 * 得到模板ID
	 * 
	 * @return String 模板ID
	 */
	public String get(String key) {
		// 此处的templateId就是templateId.properties属性文件中的templateId。
		return properties.getProperty(key);
	}

	public static void main(String args[]) {
		Configure conf = null;
		try {
			conf = new Configure("d:/mymmsc/conf/test.conf");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		System.out.println("templateId=" + conf.get("templateId")); // 测试调用
	}
}
