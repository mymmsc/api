/**
 * @(#)ApiClassLoader.java	6.3.9 09/10/02
 *
 * Copyright 2000-2010 MyMMSC Software Foundation (MSF), Inc. All rights reserved.
 * MyMMSC PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.mymmsc.api.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

/**
 * 动态加载类
 * 
 * @author WangFeng
 * @version 6.3.9 09/10/02
 * @since 6.3.9
 * @remark 2009-10-02计划中完成此功能
 */
public class ApiClassLoader extends ClassLoader {

	/**
	 * 默认构造方法
	 */
	public ApiClassLoader() {
		super(ApiClassLoader.class.getClassLoader());
	}

	/**
	 * 继承构造方法
	 * 
	 * @param parent
	 */
	public ApiClassLoader(ClassLoader parent) {
		super(parent);
	}

	@SuppressWarnings("resource")
	public Class<?> loadFromCustomRepository(String className) {
		// 取环境变量
		String classPath = System.getProperty("java.class.path");
		List<String> classRepository = new ArrayList<String>();
		// 取得该路径下的所有文件夹
		if ((classPath != null) && !(classPath.equals(""))) {
			StringTokenizer tokenizer = new StringTokenizer(classPath,
					File.pathSeparator);
			while (tokenizer.hasMoreTokens()) {
				classRepository.add(tokenizer.nextToken());
			}
		}
		Iterator<String> dirs = classRepository.iterator();
		byte[] classBytes = null;
		// 在类路径上查找该名称的类是否存在, 如果不存在继续查找
		while (dirs.hasNext()) {
			String dir = (String) dirs.next();
			String classFileName = className.replace('.', File.separatorChar);
			classFileName += ".class";
			try {
				File file = new File(dir + File.separatorChar + classFileName);
				if (file.exists()) {
					InputStream is = new FileInputStream(file);
					// 把文件读到字节文件
					classBytes = new byte[is.available()];
					is.read(classBytes);
					break;
				}
			} catch (IOException ex) {
				System.out
						.println("IOException raised while reading class file data");
				ex.printStackTrace();
				return null;
			}
		}
		// 加载类
		return this.defineClass(className, classBytes, 0, classBytes.length);
	}
}
