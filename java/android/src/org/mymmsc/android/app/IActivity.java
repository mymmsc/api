/**
 * @(#)IActivity.java	8.0.1 2011-6-5
 *
 * Copyright 2004-2011 mymmsc.org (MyMMSC), Inc. All rights reserved.
 * MyMMSC PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.mymmsc.android.app;

import android.app.Activity;

/**
 * @author WangFeng(mail:wangfeng@yeah.net, mobile:13911113230)
 * @version 1.0.1 2011-5-28
 * @since mymmsc-android 1.0.1
 */
public abstract interface IActivity {
	/**
	 * 初始化
	 * 
	 * @param context
	 */
	public abstract void init(Activity context);

	/**
	 * 初始化
	 * 
	 * @param context
	 * @param resourceId
	 */
	public abstract void init(Activity context, int resourceId);
}
