/**
 * @(#)IProgressCallback.java	8.0.1 2011-6-6
 *
 * Copyright 2004-2011 mymmsc.org (MyMMSC), Inc. All rights reserved.
 * MyMMSC PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.mymmsc.android.app;

/**
 * 进度条回调接口
 * 
 * @author WangFeng(mail:wangfeng@yeah.net, mobile:13911113230)
 * @version 1.0.1 2011-5-28
 * @since mymmsc-android 1.0.1
 */
public abstract interface IProgressCallback {

	/**
	 * 执行业务逻辑
	 * 
	 * @return
	 */
	public abstract int execute();

	/**
	 * 执行过程中, 对话框显示的文字
	 * 
	 * @return
	 */
	public abstract String getProcessMessage();

	/**
	 * 进度结束完成后前置数据处理
	 * 
	 * @return
	 */
	public abstract int forward();

	/**
	 * 进度完成后, 结果判定
	 * 
	 * @return
	 */
	public abstract int pass();

	/**
	 * 得到进度对话框标题
	 * 
	 * @return
	 */
	public abstract String getTitle();

	/**
	 * 得到进度对话框消息
	 * 
	 * @return
	 */
	public abstract String getMessage();
}
