/**
 * @(#)Constants.java	6.3.9 09/10/02
 *
 * Copyright 2000-2010 MyMMSC Software Foundation (MSF), Inc. All rights reserved.
 * MyMMSC PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.mymmsc.w3c.gsm;

/**
 * 串口通信 常量 -- 指令
 * 
 * @author WangFeng(wangfeng@yeah.net)
 * @version 6.3.9 09/10/02
 * @since mymmsc-gsm 6.3.9
 */
public final class Constants {
	public final static String GSM_READ = "+CMGL";
	public final static String GSM_SEND = "+CMGS";
	public final static String SUCCESS = "OK";
	public final static String ERROR = "ERROR";

	// 类型
	public final static String TYPE_REC_UNREAD = "0";
	public final static String TYPE_REC_READ = "1";
	public final static String TYPE_STO_UNSENT = "2";
	public final static String TYPE_STO_SENT = "3";
	public final static String TYPE_ALL = "4";
}
