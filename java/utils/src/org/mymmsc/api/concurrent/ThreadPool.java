/**
 * @(#)ThreadPool.java	6.3.9 09/10/02
 *
 * Copyright 2000-2010 MyMMSC Software Foundation (MSF), Inc. All rights reserved.
 * MyMMSC PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.mymmsc.api.concurrent;

import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.mymmsc.api.assembly.Api;

/**
 * 线程池
 * 
 * @author WangFeng(wangfeng@yeah.net)
 * @see java.util.concurrent.ExecutorService
 * @see java.util.concurrent.Executors
 * @version 6.3.9 09/10/02
 * @since mymmsc-api 6.3.9
 */
public class ThreadPool {

	private ExecutorService m_thread_pool = null;
	private LinkedList<InterfaceThread> m_thread_list = null;
	private int m_max_threads = 1; // 线程池最大线程数
	private int m_size = 0; // 已经投放的线程数

	/**
	 * ThreadPool构造函数
	 * 
	 * @param nThreads
	 *            线程数
	 */
	public ThreadPool(int nThreads) {
		m_size = 0;
		if (nThreads < 1) {
			nThreads = 1;
		}
		m_max_threads = nThreads;
		m_thread_pool = Executors.newFixedThreadPool(m_max_threads);
		m_thread_list = new LinkedList<InterfaceThread>();
	}

	public void close() {
		/*
		 * // shutdownNow 不能立即停止所有线程 // m_thread_pool.shutdownNow();
		 * m_thread_pool.shutdown(); // Disable new tasks from being submitted
		 * try { // Wait a while for existing tasks to terminate if
		 * (!m_thread_pool.awaitTermination(1, TimeUnit.SECONDS)) {
		 * m_thread_pool.shutdownNow(); // Cancel currently executing tasks //
		 * Wait a while for tasks to respond to being cancelled if
		 * (!m_thread_pool.awaitTermination(60, TimeUnit.SECONDS))
		 * System.err.println("Pool did not terminate"); } } catch
		 * (InterruptedException ie) { // (Re-)Cancel if current thread also
		 * interrupted m_thread_pool.shutdownNow(); // Preserve interrupt status
		 * Thread.currentThread().interrupt(); }
		 */
		int status = 0;
		for (InterfaceThread thread : m_thread_list) {
			System.out.print(String.format("正在关闭线程[%s]: ", thread.getName()));
			thread.doStop();
			while (true) {
				status = thread.getStatus();
				if (status == TaskThread.CLOSED || status == TaskThread.STOPPED) {
					System.out.println(" 完成.");
					break;
				} else {
					System.out.print('.');
					Api.sleep(1000);
				}
			}
		}
		m_thread_list.clear();
		m_thread_pool.shutdown();
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		close();
	}

	/**
	 * 投递?个线程任务
	 * 
	 * @param thread
	 * @param num
	 * @return
	 */
	public synchronized boolean push(InterfaceThread thread, int num) {
		boolean bRet = false;
		if (num < 1) {
			num = 1;
		}
		if (m_max_threads < m_size + num) {
			bRet = false;
		} else {
			m_size += num;
			for (int i = 0; i < num; i++) {
				thread.setStatus(TaskThread.SATRTING);
				m_thread_pool.submit(thread);
				thread.setStatus(TaskThread.RUNNING);
			}
			m_thread_list.add(thread);
			bRet = true;
		}
		return bRet;
	}

	/**
	 * 投递一个任务
	 * 
	 * @param thread
	 * @return
	 */
	public boolean push(InterfaceThread thread) {
		return push(thread, 1);
	}

}
