/**
 * @(#)Logger.java	6.3.9 09/10/02
 *
 * Copyright 2000-2010 MyMMSC Software Foundation (MSF), Inc. All rights reserved.
 * MyMMSC PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.mymmsc.api.io;

/**
 * 日志接口
 * 
 * @author WangFeng(wangfeng@yeah.net)
 * @version 6.3.9 09/10/02
 * @since mymmsc-api 6.3.9
 */
public abstract interface Logger {
	
	public abstract void info(Object arg);

	public abstract void info(Object arg0, Throwable arg1);

	public abstract void error(Object arg);

	public abstract void error(Object arg0, Throwable arg1);

	public abstract void warn(Object arg);

	public abstract void warn(Object arg0, Throwable arg1);

}
