/**
 * @(#)TaskThread.java	6.3.9 09/10/02
 *
 * Copyright 2000-2010 MyMMSC Software Foundation (MSF), Inc. All rights reserved.
 * MyMMSC PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.mymmsc.api.concurrent;

/**
 * @author WangFeng(wangfeng@yeah.net)
 * @version 6.3.9 09/10/02
 * @since mymmsc-api 6.3.9
 */
public abstract class TaskThread implements InterfaceThread {
	public final static int UNKNOWN = 0x0000;

	public final static int DOING = 0x0100;
	public final static int FINISHED = 0x0200;

	public final static int CLOSE = 0x0001;
	public final static int PAUSE = 0x0002;
	public final static int START = 0x0004;
	public final static int STOP = 0x0008;
	public final static int RUN = 0x0010;

	public final static int CLOSING = DOING | CLOSE;
	public final static int PAUSING = DOING | PAUSE;
	public final static int SATRTING = DOING | START;
	public final static int STOPPING = DOING | STOP;
	public final static int RUNNING = DOING | RUN;

	public final static int CLOSED = FINISHED | CLOSE;
	public final static int PAUSED = FINISHED | PAUSE;
	public final static int STARTED = FINISHED | START;
	public final static int STOPPED = FINISHED | STOP;

	private int m_thread_status = UNKNOWN;
	private String m_name = null;

	/**
	 * AbstractThread构造函数
	 */
	public TaskThread() {
		this(null);
	}

	/**
	 * TaskThread构造函数
	 * 
	 * @param name
	 */
	public TaskThread(String name) {
		// 停止状态
		m_thread_status = UNKNOWN;
		setName(name);
	}

	/**
	 * 设置线程名称
	 * 
	 * @param m_name
	 *            the m_name to set
	 */
	public void setName(String m_name) {
		this.m_name = m_name;
	}

	/**
	 * 返回线程名
	 * 
	 * @see org.mymmsc.api.concurrent.InterfaceThread#getName()
	 * @return the m_name
	 */
	public String getName() {
		return m_name == null ? toString() : m_name;
	}

	/**
	 * 返回线程状态
	 * 
	 * @return 当前线程状态
	 */
	public int getStatus() {
		return m_thread_status;
	}

	/*
	 * 设置线程状态
	 * 
	 * @see org.mymmsc.api.concurrent.InterfaceThread#setStatus(int)
	 */
	@Override
	public synchronized void setStatus(int status) {
		m_thread_status = status;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mymmsc.api.concurrent.InterfaceThread#doPause()
	 */
	@Override
	public synchronized void doPause() {
		m_thread_status = PAUSING;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mymmsc.api.concurrent.InterfaceThread#isPause()
	 */
	@Override
	public synchronized boolean isPause() {
		return (m_thread_status == PAUSED);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mymmsc.api.concurrent.InterfaceThread#isRunning()
	 */
	@Override
	public boolean isRunning() {
		return (m_thread_status & RUNNING) > 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mymmsc.api.concurrent.InterfaceThread#doStop()
	 */
	@Override
	public synchronized void doStop() {
		m_thread_status = STOPPING;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mymmsc.api.concurrent.InterfaceThread#isStopped()
	 */
	@Override
	public synchronized boolean isStopped() {
		return (m_thread_status == STOPPED);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mymmsc.api.concurrent.InterfaceThread#isStopping()
	 */
	@Override
	public boolean isStopping() {
		// return (m_thread_status & STOPPING) > 0;
		return (m_thread_status == STOPPING);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mymmsc.api.concurrent.InterfaceThread#getTimes()
	 */
	@Override
	public long getTimes() {
		return 0;
	}
}
