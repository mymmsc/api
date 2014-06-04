/**
 * @(#)APS.java	6.3.9 09/10/02
 *
 * Copyright 2000-2010 MyMMSC Software Foundation (MSF), Inc. All rights reserved.
 * MyMMSC PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.mymmsc.j2ee.struts;

/**
 * FireStruts 返回码常量
 * 
 * @author WangFeng(wangfeng@yeah.net)
 * @version 6.3.9 09/10/02
 * @since mymmsc-firestrtus 6.3.9
 */
public final class APS {
	/** 未知状态 */
	public static final String SC_UNKNOWN = "unknown";
	/** 成功 */
	public static final String SC_SUCCESS = "success";
	/** 失败 */
	public static final String SC_FAILURE = "failure";
	/** 错误 */
	public static final String SC_ERROR = "error";
	/** 重定向 */
	public static final String SC_REDIRECT = "redirect";
	/** 转向 */
	public static final String SC_FORWARD = "forward";
	/** cgi 扩展名 */
	public static final String CGI_EXT = "cgi";
	/** Action 扩展名 */
	public static final String ACTION_EXT = "action";
}
