/**
 * @(#)Tail.java	6.3.9 09/10/02
 *
 * Copyright 2000-2010 MyMMSC Software Foundation (MSF), Inc. All rights reserved.
 * MyMMSC PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.mymmsc.api.examples;

/**
 * @author WangFeng(wangfeng@yeah.net)
 * @version 6.3.9 09/10/02
 * @since mymmsc-j2ee 6.3.9
 */
//Import the Java classes
import java.io.File;

import org.mymmsc.api.io.LogFileTailer;
import org.mymmsc.api.io.LogFileTailerListener;

/**
 * Implements console-based log file tailing, or more specifically, tail
 * following: it is somewhat equivalent to the unix command "tail -f"
 */
public class Tail implements LogFileTailerListener {
	/**
	 * The log file tailer
	 */
	private LogFileTailer tailer;

	/**
	 * Creates a new Tail instance to follow the specified file
	 */
	public Tail(String filename) {
		tailer = new LogFileTailer(new File(filename), 1000, false);
		tailer.addLogFileTailerListener(this);
		tailer.start();
	}

	/**
	 * A new line has been added to the tailed log file
	 * 
	 * @param line
	 *            The new line that has been added to the tailed log file
	 */
	public void newLogFileLine(String line) {
		System.out.println(line);
	}

	/**
	 * Command-line launcher
	 */
	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println("Usage: Tail <filename>");
			System.exit(0);
		}
		@SuppressWarnings("unused")
		Tail tail = new Tail(args[0]);
	}
}