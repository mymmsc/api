/**
 * @(#)DsoLibrary.java	6.3.9 09/10/02
 *
 * Copyright 2000-2010 MyMMSC Software Foundation (MSF), Inc. All rights reserved.
 * MyMMSC PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.mymmsc.w3c.dso;

/**
 * 动态库接口通用处理方法
 * 
 * @author WangFeng(wangfeng@yeah.net)
 * @version 6.3.9 09/10/02
 * @since mymmsc-gsm 6.3.9
 */
public interface DsoLibrary {
	public void load(String filename);

	public void forName(String clazz) throws ClassNotFoundException;

	public boolean close();

	public boolean isWindows();

	public String getWindowsClass();

	public String getUnixClass();
}
