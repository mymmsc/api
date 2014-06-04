/*
 * @(#)Encoding.java	6.3.9 09/09/25
 *
 * Copyright 2009 MyMMSC Software Foundation (MSF), Inc. All rights reserved.
 * MSF PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.mymmsc.api.category;

/**
 * 公用基础类 -- 编码方式
 * 
 * @author WangFeng(wangfeng@yeah.net)
 * @version 6.3.9 09/10/02
 * @since mymmsc-api 6.3.9
 */
public final class Encoding {

	/** 文件系统默认编码 */
	public final static String FileSystem = System.getProperty("file.encoding");
	/** 默认编码: utf-8 */
	public final static String Default = "UTF-8";
	/** 页面编码: gbk */
	public final static String GBK = "GBK";
	/** 页面编码: gb18030 */
	public final static String GB18030 = "GB18030";
	/** MySQL编码: ISO8859-1 */
	public final static String MySQL = "8859_1";
	/** 页面字符集 */
	public static final String CONTENT_TYPE = "text/html; charset=utf-8";

}
