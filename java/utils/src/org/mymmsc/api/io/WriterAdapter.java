/**
 * @(#)Environment.java	6.3.9 09/10/02
 *
 * Copyright 2000-2010 MyMMSC Software Foundation (MSF), Inc. All rights reserved.
 * MyMMSC PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.mymmsc.api.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.mymmsc.api.category.Encoding;

/**
 * @author WangFeng(wangfeng@yeah.net)
 * @version 6.3.9 09/10/02
 * @since mymmsc-api 6.3.9
 */
public abstract class WriterAdapter {
	/** 编码格式 */
	private String encoding;

	public WriterAdapter() {
		super();
		this.encoding = Encoding.FileSystem;
	}

	/**
	 * createWriter
	 * 
	 * @param os
	 *            OutputStream
	 * @return OutputStreamWriter
	 */
	protected OutputStreamWriter createWriter(OutputStream os) {
		OutputStreamWriter retval = null;

		if (encoding != null && encoding.length() > 0) {
			try {
				retval = new OutputStreamWriter(os, encoding);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (retval == null) {
			retval = new OutputStreamWriter(os);
		}
		return retval;
	}

	/**
	 * getEncoding
	 * 
	 * @return String
	 */
	protected String getEncoding() {
		return encoding.trim();
	}

	/**
	 * setEncoding
	 * 
	 * @param enc
	 *            String
	 */
	protected void setEncoding(String enc) {
		encoding = enc.trim();
	}

}
