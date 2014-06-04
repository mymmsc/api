/**
 * @(#)LogTailer.java	6.3.9 09/10/02
 *
 * Copyright 2000-2010 MyMMSC Software Foundation (MSF), Inc. All rights reserved.
 * MyMMSC PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.mymmsc.j2ee.http;

import java.io.File;
import java.io.PrintWriter;

import org.mymmsc.api.io.LogFileTailer;
import org.mymmsc.api.io.LogFileTailerListener;

/**
 * @author WangFeng(wangfeng@yeah.net)
 * @version 6.3.9 09/10/02
 * @since mymmsc-j2ee 6.3.9
 */
public class LogTailer implements LogFileTailerListener {
	/** The log file m_tailer */
	private LogFileTailer m_tailer;
	private StringBuffer m_buff;

	/**
	 * LogTailer构造函数
	 */
	public LogTailer(String filename) {
		m_buff = new StringBuffer();
		m_tailer = new LogFileTailer(new File(filename), 1000, false);
		m_tailer.addLogFileTailerListener(this);
		m_tailer.start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mymmsc.api.file_io.LogFileTailerListener#newLogFileLine(java.lang
	 * .String)
	 */
	public synchronized void newLogFileLine(String line) {
		m_buff.append(line);
	}
	
	public synchronized void output(PrintWriter out) {
		out.print(m_buff.toString());
		m_buff = new StringBuffer();
	}

}
