/**
 * @(#)Logger.java	8.0.1 2011-6-5
 *
 * Copyright 2004-2011 mymmsc.org (MyMMSC), Inc. All rights reserved.
 * MyMMSC PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.mymmsc.android.app.util;

import android.util.Log;

/**
 * 日志记录器
 * 
 * @author WangFeng(mail:wangfeng@yeah.net, mobile:13911113230)
 * @version 1.0.1 2011-5-28
 * @since mymmsc-android 1.0.1
 */
public class LoggerImpl implements Logger {
	private String tag = null;

	private void init(String tag) {
		this.tag = tag;
	}

	public void info(String msg) {
		Log.i(tag, msg);
	}

	public void error(String msg) {
		Log.e(tag, msg);
	}

	public void error(String msg, Throwable tr) {
		Log.e(tag, msg, tr);
	}

	@Override
	public Logger newInstance(String tag) {
		LoggerImpl logger = new LoggerImpl();
		logger.init(tag);
		return logger;
	}
}
