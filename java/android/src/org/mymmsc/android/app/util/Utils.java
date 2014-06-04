/**
 * @(#)Utils.java	8.0.1 2011-5-28
 *
 * Copyright 2004-2011 mymmsc.org (MyMMSC), Inc. All rights reserved.
 * MyMMSC PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.mymmsc.android.app.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * 杂类
 * 
 * @author WangFeng(mail:wangfeng@yeah.net, mobile:13911113230)
 * @version 1.0.1 2011-5-28
 * @since mymmsc-android 1.0.1
 */
public class Utils {

	/**
	 * 判断终端是否接入互联网络
	 * 
	 * @see <uses-permission
	 *      android:name="android.permission.ACCESS_NETWORK_STATE"/>
	 */
	public static boolean isNetworking(Context ctx) {
		boolean bRet = false;
		ConnectivityManager cManager = (ConnectivityManager) ctx
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cManager.getActiveNetworkInfo();
		if (info != null && info.isAvailable()) {
			// do something
			// 能联网
			bRet = true;
		} else {
			// do something
			// 不能联网
		}
		return bRet;
	}

	/**
	 * 格式化URI
	 * 
	 * @param uri
	 * @return
	 * @category 默认为http协议
	 */
	public static String formatUri(String uri) {
		String uriRet = null;
		if (uri != null && uri.length() > 5
				&& !uri.toLowerCase().startsWith("http://")) {
			uriRet = "http://" + uri;
		} else {
			uriRet = uri;
		}
		return uriRet;
	}

	/**
	 * 读取文本文件
	 * 
	 * @param is
	 * @return
	 */
	public static String readFile(InputStream is) {
		String sRet = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			int len = -1;
			byte[] buff = new byte[1024];

			while ((len = is.read(buff)) != -1) {
				baos.write(buff, 0, len);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		sRet = new String(baos.toByteArray());
		return sRet;
	}
}
