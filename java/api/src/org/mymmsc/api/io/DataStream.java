/**
 * @(#)DataStream.java	6.3.9 09/10/02
 *
 * Copyright 2000-2010 MyMMSC Software Foundation (MSF), Inc. All rights reserved.
 * MyMMSC PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.mymmsc.api.io;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

/**
 * 数据流封装
 * 
 * @author WangFeng(wangfeng@yeah.net)
 * @version 6.3.9 09/10/02
 * @since mymmsc-api 6.3.9
 */
public class DataStream {
	/**
	 * SendData
	 * 
	 * @param dos
	 *            DataOutputStream
	 * @param data
	 *            byte[]
	 * @return boolean
	 */
	public static boolean SendData(DataOutputStream dos, byte[] data) {
		if (dos == null) {
			return false;
		}
		try {
			dos.write(data);
		} catch (IOException ex) {
			return false;
		}
		return true;
	}

	/**
	 * SendHttpData
	 * 
	 * @param os
	 *            DataOutputStream
	 * @param data
	 *            byte[]
	 * @return boolean
	 */
	public static boolean SendData(OutputStream os, byte[] data) {
		return SendData(new DataOutputStream(os), data);
	}

	/**
	 * RecvData
	 * 
	 * @param dis
	 *            DataInputStream
	 * @return String
	 */
	public static String RecvData(DataInputStream dis) {
		return RecvHttpData(dis, null);
	}

	/**
	 * recv
	 * 
	 * @param is
	 *            InputStream
	 * @return 字节数组
	 */
	public synchronized static byte[] recv(InputStream is) {
		byte[] bRet = null;
		if (is != null) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataInputStream dis = new DataInputStream(is);
			try {
				int len = -1;
				byte[] buff = new byte[1024];

				while ((len = dis.read(buff)) != -1) {
					baos.write(buff, 0, len);
				}
				bRet = baos.toByteArray();
				baos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return bRet;
	}

	/**
	 * RecvData
	 * 
	 * @param is
	 *            InputStream
	 * @return String
	 */
	public synchronized static String RecvData(InputStream is) {
		if (is == null) {
			return "";
		}
		return RecvData(new DataInputStream(is));
	}

	/**
	 * RecvHttpData
	 * 
	 * @param dis
	 *            DataInputStream
	 * @param encoding
	 *            String
	 * @return String
	 */
	public synchronized static String RecvHttpData(DataInputStream dis,
			String encoding) {
		if (dis == null) {
			return "";
		}
		String sRet = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			int len = -1;
			byte[] buff = new byte[1024];

			while ((len = dis.read(buff)) != -1) {
				baos.write(buff, 0, len);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			if (encoding != null) {
				sRet = new String(baos.toByteArray(), encoding);
			} else {
				sRet = new String(baos.toByteArray());
			}
			baos.close();
		} catch (UnsupportedEncodingException e) {
			// e.printStackTrace();
			sRet = "";
		} catch (IOException e) {
			e.printStackTrace();
			sRet = "";
		}
		return sRet;
	}

	public synchronized static String RecvHttpData(InputStream is,
			String encoding) {
		if (is == null) {
			return "";
		}
		return RecvHttpData(new DataInputStream(is), encoding);
	}
}
