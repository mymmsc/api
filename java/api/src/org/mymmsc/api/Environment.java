/**
 * @(#)Environment.java	6.3.9 09/10/02
 *
 * Copyright 2000-2010 MyMMSC Software Foundation (MSF), Inc. All rights reserved.
 * MyMMSC PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.mymmsc.api;

import java.util.Enumeration;
import java.util.Properties;

/**
 * 环境变量存取
 * 
 * @author WangFeng(wangfeng@yeah.net)
 * @version 6.3.9 09/10/02
 * @since mymmsc-api 6.3.9
 */
public class Environment {
	private static boolean m_isWindows = false;
	private static String m_os = null;
	private static Properties m_properties = null;

	/**
	 * 初始化静态类
	 */
	private static void init() {
		if (m_os == null) {
			m_properties = System.getProperties();
			m_os = m_properties.getProperty("os.name");
			if (m_os.indexOf("Windows") >= 0) {
				m_isWindows = true;
			}
		}
	}
	
	/**
	 * 判断当前系统是否windows
	 * 
	 * @return 如果是windows, 返回true
	 */
	public static boolean isWindows() {
		init();
		return m_isWindows;
	}
	
	/**
	 * 得到文件系统的编码字符集
	 * 
	 * @return 编码字符集
	 */
	public static String getFileEncoding() {
		init();
		return m_properties.getProperty("file.encoding");
	}

	public static String getSerialPort(int n) {
		init();
		return String.format("%s%d", m_isWindows ? "COM" : "/dev/ttyS", n);
	}

	/**
	 * 获得当前工程临时目录
	 * 
	 * @return String
	 */
	public static String getTempPath() {
		return getTempPath("mymmsc");
	}

	/**
	 * 获得当前工程临时目录
	 * 
	 * @param path 相对路径
	 * @return String
	 */
	public static String getTempPath(String path) {
		init();
		path = path.trim();
		if (!path.startsWith("/") && !path.startsWith("\\")) {
			path = "/" + path;
		}
		return get("java.io.tmpdir") + path;
	}

	public static String get(String key) {
		return System.getProperty(key);
	}

	public static void main(String args[]) {
		Properties props = System.getProperties();
		for (Enumeration<?> e = props.propertyNames(); e.hasMoreElements();) {
			String key = (String) e.nextElement();
			System.out.println(key + "===>" + props.getProperty(key));
		}

		System.out.println("Java的运行环境版本：" + props.getProperty("java.version"));
		System.out.println("Java的运行环境供应商：" + props.getProperty("java.vendor"));
		System.out.println("Java供应商的URL："
				+ props.getProperty("java.vendor.url"));
		System.out.println("Java的安装路径：" + props.getProperty("java.home"));
		System.out.println("Java的虚拟机规范版本："
				+ props.getProperty("java.vm.specification.version"));
		System.out.println("Java的虚拟机规范供应商："
				+ props.getProperty("java.vm.specification.vendor"));
		System.out.println("Java的虚拟机规范名称："
				+ props.getProperty("java.vm.specification.name"));
		System.out.println("Java的虚拟机实现版本："
				+ props.getProperty("java.vm.version"));
		System.out.println("Java的虚拟机实现供应商："
				+ props.getProperty("java.vm.vendor"));
		System.out.println("Java的虚拟机实现名称：" + props.getProperty("java.vm.name"));
		System.out.println("Java运行时环境规范版本："
				+ props.getProperty("java.specification.version"));
		System.out.println("Java运行时环境规范供应商："
				+ props.getProperty("java.specification.vender"));
		System.out.println("Java运行时环境规范名称："
				+ props.getProperty("java.specification.name"));
		System.out.println("Java的类格式版本号："
				+ props.getProperty("java.class.version"));
		System.out.println("Java的类路径：" + props.getProperty("java.class.path"));
		System.out.println("加载库时搜索的路径列表："
				+ props.getProperty("java.library.path"));
		System.out.println("默认的临时文件路径：" + props.getProperty("java.io.tmpdir"));
		System.out
				.println("一个或多个扩展目录的路径：" + props.getProperty("java.ext.dirs"));
		System.out.println("操作系统的名称：" + props.getProperty("os.name"));
		System.out.println("操作系统的构架：" + props.getProperty("os.arch"));
		System.out.println("操作系统的版本：" + props.getProperty("os.version"));
		System.out.println("文件分隔符：" + props.getProperty("file.separator")); // 在unix系统中是"/"
		System.out.println("路径分隔符：" + props.getProperty("path.separator")); // 在unix系统中是":"
		System.out.println("行分隔符：" + props.getProperty("line.separator")); // 在
		// unix系统中是"/n"
		System.out.println("用户的账户名称：" + props.getProperty("user.name"));
		System.out.println("用户的主目录：" + props.getProperty("user.home"));
		System.out.println("用户的当前工作目录：" + props.getProperty("user.dir"));
		System.setProperty("javax.xml.parsers.DocumentBuilderFactory","com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");
		
		System.out.println("aa: " + System.getProperty("javax.xml.parsers.DocumentBuilderFactory"));
		System.out.println("javax.xml.xpath.XPathFactory：" + props.getProperty("javax.xml.xpath.XPathFactory"));
		
	}
}
