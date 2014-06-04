/**
 * @(#)ButtonImpl.java	8.0.1 2011-6-3
 *
 * Copyright 2004-2011 mymmsc.org (MyMMSC), Inc. All rights reserved.
 * MyMMSC PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.mymmsc.android.app;

import org.mymmsc.android.net.ViewHolder;

import android.graphics.drawable.Drawable;

/**
 * ListView按钮事件
 * 
 * @author WangFeng(mail:wangfeng@yeah.net, mobile:13911113230)
 * @version 8.0.1 2011-6-3
 * @since mymmsc-ETongMobileA 8.0.1
 */
public abstract interface IViewClick {
	/**
	 * 图片点击事件
	 * 
	 * @param title
	 * @param id
	 */
	public abstract void doImage(String title, Drawable id);

	/**
	 * 资源点击事件
	 * 
	 * @param rb
	 */
	public abstract void doButton(IResource rb);

	/**
	 * 单行事件
	 * 
	 * @param holder
	 */
	public abstract void doView(ViewHolder holder);
}
