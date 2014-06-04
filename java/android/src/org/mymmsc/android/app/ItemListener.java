/**
 * @(#)ItemListener.java	1.0.1 2011-5-28
 *
 * Copyright 2004-2011 mymmsc.org (MyMMSC), Inc. All rights reserved.
 * MyMMSC PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.mymmsc.android.app;

/**
 * ListView按钮事件
 * 
 * @author WangFeng(mail:wangfeng@yeah.net, mobile:13911113230)
 * @version 1.0.1 2011-5-28
 * @since mymmsc-android 1.0.1
 */
public abstract interface ItemListener {
	/**
	 * 图片点击事件
	 * 
	 * @param rb
	 */
	public abstract void onImageClick(IResource rb);

	/**
	 * 按钮点击事件
	 * 
	 * @param title
	 * @param id
	 */
	public abstract void onButtonClick(String title, int id);

	/**
	 * item点击事件
	 */
	public abstract void onItemClick();
}
