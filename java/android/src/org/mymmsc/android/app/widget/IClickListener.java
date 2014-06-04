/**
 * @(#)IClickListener.java	8.0.1 2011-6-4
 *
 * Copyright 2004-2011 mymmsc.org (MyMMSC), Inc. All rights reserved.
 * MyMMSC PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.mymmsc.android.app.widget;

import android.view.View;

/**
 * 单击事件接口类
 * 
 * @author WangFeng(mail:wangfeng@yeah.net, mobile:13911113230)
 * @version 1.0.1 2011-5-28
 * @since mymmsc-android 1.0.1
 */
public abstract interface IClickListener extends
		android.view.View.OnClickListener {

	/**
	 * 按钮单击事件
	 * 
	 * @param v
	 */
	public abstract void onClick(View v);
}
