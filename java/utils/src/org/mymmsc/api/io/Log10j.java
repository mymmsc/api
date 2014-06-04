/**
 * @(#)Log10j.java	6.3.9 09/10/02
 *
 * Copyright 2000-2010 MyMMSC Software Foundation (MSF), Inc. All rights reserved.
 * MyMMSC PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.mymmsc.api.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Date;
import java.util.Formatter;

import org.mymmsc.api.assembly.Api;
import org.mymmsc.api.category.Encoding;

/**
 * 日志系统
 * 
 * @author WangFeng(wangfeng@yeah.net)
 * @version 6.3.9 09/10/02
 * @since mymmsc-api 6.3.9
 */
public class Log10j extends WriterAdapter {
	/** 普通文本日志 */
	public final static int TEXT = 0x00;
	/** 营账日志 */
	public final static int BILL = 0x88;
	// 日志文件默认存放路径
	public static final String PATH = "/logs";
	// 杂项日志文件名
	public static final String UTIL = "util";
	// 日志内容时间格式
	public static final String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";

	/** 调试标志 */
	private boolean m_debug = false;
	/** 日志文件存储类型 */
	private int m_nType = 0;
	/** 日志文件的路径 */
	private String m_sRootPath = "";
	/** 日志文件名 */
	private String m_sPrefix = "";
	/** 写操作的临时日志文件名称 */
	private String m_filename = "";
	/** 输出日志文件对象 */
	private PrintWriter m_writer = null;
	/** 默认为追加模式 */
	private boolean m_bAppend = true;
	/** 文件缓存尺寸为8K */
	private int m_nBufferSize = 8 * 1024;

	/**
	 * Log10j构造函数
	 * 
	 * @param mainpath
	 * @param fileprefix
	 * @param type
	 */
	public Log10j(String mainpath, String fileprefix, int type) {
		if (mainpath.trim().length() == 0) {
			m_sRootPath = PATH;
		} else {
			m_sRootPath = mainpath;
		}
		if (fileprefix.trim().length() == 0) {
			m_sPrefix = UTIL;
		} else {
			m_sPrefix = fileprefix;
		}
		m_nType = type;
	}

	/**
	 * Log10j
	 * 
	 * @todo 构造函数
	 * @param mainpath
	 *            String
	 * @param fileprefix
	 *            String
	 */
	public Log10j(String mainpath, String fileprefix) {
		this(mainpath, fileprefix, TEXT);
	}

	/**
	 * Log10j
	 * 
	 * @todo 构造函数
	 * @param fileprefix
	 *            String
	 */
	public Log10j(String fileprefix) {
		this("", fileprefix, 0);
	}

	/**
	 * setDebug
	 * 
	 * @param debug
	 *            boolean
	 */
	public void setDebug(boolean debug) {
		m_debug = debug;
	}

	/**
	 * getTimeString
	 * 
	 * @todo Get the time gap as a String. In hours, minutes, seconds and
	 *       milliseconds.
	 * @return String
	 */
	private String getTimeString() {
		return Api.toString(new Date(), "yyyy-MM-dd HH:mm:ss.SSS");
	}

	/**
	 * setLogDirectory
	 * 
	 * @todo 设置日志文件路径
	 */
	private void setLogDirectory() {
		Date tmpDate = new Date();
		// 检查日志路径
		String tmpLogPath = String.format("%s/%s", m_sRootPath,
				Api.toString(tmpDate, "yyyyMMdd"));
		Api.mkdirs(tmpLogPath);
		String tmpFilename = "";
		if (m_nType == TEXT) {
			tmpFilename = String.format("%s/%s.log", tmpLogPath, m_sPrefix);
		} else {
			tmpFilename = String.format("%s/%s_%s", tmpLogPath,
					Api.toString(tmpDate, "HH"), m_sPrefix);
		}

		if (m_filename.compareTo(tmpFilename) != 0) {
			close();
		}
		m_filename = tmpFilename;
	}

	/**
	 * OpenLog
	 * 
	 * @todo 打开日志文件
	 */
	private void OpenLog() {
		setLogDirectory();

		if (m_writer != null) {
			if (!m_writer.checkError()) {
				return;
			} else {
				m_writer.close();
				m_writer = null;
			}
		}

		FileOutputStream ostream = null;
		try {
			//
			// attempt to create file
			//
			ostream = new FileOutputStream(m_filename, m_bAppend);
		} catch (FileNotFoundException ex) {
			//
			// if parent directory does not exist then
			// attempt to create it and try to create file
			// see bug 9150
			//
			String parentName = new File(m_filename).getParent();
			if (parentName != null) {
				File parentDir = new File(parentName);
				if (!parentDir.exists() && parentDir.mkdirs()) {
					try {
						ostream = new FileOutputStream(m_filename, m_bAppend);
					} catch (FileNotFoundException ex1) {
						ex1.printStackTrace();
					}
				} else {
					try {
						throw ex;
					} catch (FileNotFoundException ex2) {
						ex2.printStackTrace();
					}
				}
			} else {
				try {
					throw ex;
				} catch (FileNotFoundException ex3) {
					ex3.printStackTrace();
				}
			}
		}
		Writer fw = createWriter(ostream);
		fw = new BufferedWriter(fw, m_nBufferSize);
		m_writer = new PrintWriter(fw);
	}

	/**
	 * 将文本信息写入日志文件
	 * 
	 * @param flag
	 * @param format
	 * @param args
	 */
	private void output(String flag, String format, Object... args) {
		OpenLog();
		String msg = "";
		try {
			msg = new Formatter().format(format, args).toString();
		} catch (Exception e) {
			// 不抛出异常
		}
		String sTemp = null;
		try {
			sTemp = new String(msg.getBytes(Encoding.FileSystem));
			if (sTemp.compareTo(msg) != 0) {
				msg = new String(msg.getBytes("8859_1"), "GB18030");
			}
		} catch (UnsupportedEncodingException ex) {
			ex.printStackTrace();
		}

		sTemp = String.format("[%s] %s> %s - %s", getTimeString(),
				flag.toUpperCase(), Thread.currentThread().getName(), msg);
		m_writer.println(sTemp);
		flush();
		if (m_debug) {
			System.out.println(m_filename + "-->" + sTemp);
		}
	}

	/**
	 * 一般信息
	 * 
	 * @param format
	 * @param args
	 */
	public void info(String format, Object... args) {
		output("info", format, args);
	}

	/**
	 * 调试信息
	 * 
	 * @param format
	 * @param args
	 */
	public void debug(String format, Object... args) {
		output("debug", format, args);
	}

	/**
	 * 错误信息
	 * 
	 * @param strMessage
	 */
	public void error(String strMessage) {
		output("error", strMessage);
	}

	/**
	 * 输出错误栈信息
	 * 
	 * @param e
	 */
	public void error(Exception e) {
		/*
		 * StringBuffer sbuff = new StringBuffer(); StackTraceElement[] ste =
		 * e.getStackTrace(); for (StackTraceElement stackTraceElement : ste) {
		 * sbuff.append(stackTraceElement.toString() + "\n"); } output("error",
		 * sbuff.toString());
		 */
		OpenLog();
		String sTemp = String.format("[%s] %s> ", getTimeString(),
				"ERROR".toUpperCase());
		m_writer.print(sTemp);
		e.printStackTrace(m_writer);
	}

	/**
	 * 警告信息
	 * 
	 * @param format
	 * @param args
	 */
	public void warn(String format, Object... args) {
		output("warn", format, args);
	}

	/**
	 * 致命错误信息
	 * 
	 * @param format
	 * @param args
	 */
	public void fatal(String format, Object... args) {
		output("fatal", format, args);
	}

	/**
	 * flush
	 */
	public synchronized void flush() {
		if (m_writer != null) {
			m_writer.flush();
		}
	}

	/**
	 * close
	 */
	public void close() {
		if (m_writer != null) {
			m_writer.close();
			m_writer = null;
		}
	}

	/**
	 * finalize
	 */
	protected void finalize() {
		close();
	}
}
