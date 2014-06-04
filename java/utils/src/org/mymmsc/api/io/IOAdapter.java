/**
 * @(#)IOAdapter.java	6.3.9 09/10/02
 *
 * Copyright 2000-2010 MyMMSC Software Foundation (MSF), Inc. All rights reserved.
 * MyMMSC PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.mymmsc.api.io;

import java.io.FileNotFoundException;
import java.io.RandomAccessFile;

/**
 * I/O 适配器
 * 
 * @author WangFeng(wangfeng@yeah.net)
 * @version 6.3.9 09/10/02
 * @see RandomAccessFile
 * @since mymmsc-api 6.3.9
 */
public abstract class IOAdapter extends RandomAccessFile {
	/**
	 * IOAdapter构造函数
	 * 
	 * @param name
	 * @param mode
	 * @throws FileNotFoundException
	 */
	public IOAdapter(String name, String mode) throws FileNotFoundException {
		super(name, mode);
	}
}
