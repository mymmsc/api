/**
 * @(#)InterfaceThread.java	6.3.9 09/10/02
 *
 * Copyright 2000-2010 MyMMSC Software Foundation (MSF), Inc. All rights reserved.
 * MyMMSC PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.mymmsc.api.concurrent;

/**
 * 线程接口
 * 
 * @author WangFeng(wangfeng@yeah.net)
 * @version 6.3.9 09/10/02
 * @since mymmsc-api 6.3.9
 */
public interface InterfaceThread extends Runnable {
	/**
	 * 返回线程名
	 * 
	 * @return the m_name
	 */
	public String getName();

	/**
	 * 获得运行时间长度
	 * 
	 * @return 毫秒
	 */
	public long getTimes();

	/**
	 * 返回线程状态
	 * 
	 * @return 当前线程状态
	 */
	public int getStatus();
	
	/**
	 * 设置线程状态
	 * 
	 * @param status 状态码
	 */
	public void setStatus(int status);

	/**
	 * 发送暂停指令
	 */
	public void doPause();

	/**
	 * 线程是否暂停
	 * 
	 * @return 如果暂停返回true, 否则返回false
	 */
	public boolean isPause();

	/**
	 * 发送停止运行指令
	 */
	public void doStop();

	/**
	 * 线程是否停止
	 * 
	 * @return 如果停止返回true, 否则返回false
	 */
	public boolean isStopped();
	
	/**
	 * 是否被 强制要求停止运行
	 * @return 如果被要求停止返回true, 否则返回false
	 */
	public boolean isStopping();
	
	/**
	 * 是否正在运行
	 * @return 如果可以运行返回true, 否则返回false
	 */
	public boolean isRunning();

}
