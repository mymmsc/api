/**
 * @(#)Cache.java	8.0.1 2011-6-5
 *
 * Copyright 2004-2011 mymmsc.org (MyMMSC), Inc. All rights reserved.
 * MyMMSC PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.mymmsc.android.app.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Android应用程序私有数据存取
 * 
 * @author WangFeng(mail:wangfeng@yeah.net, mobile:13911113230)
 * @version 1.0.1 2011-5-28
 * @since mymmsc-android 1.0.1
 */
public class Cache {
	private static String AppKey = null;
	private static SharedPreferences AppCache = null;

	/** 应用程序缓存数据 **/

	/**
	 * 初始化缓冲区
	 */
	public static void init(Context context, String name) {
		if (AppCache == null) {
			AppKey = name;
			AppCache = context.getSharedPreferences(AppKey,
					Context.MODE_WORLD_WRITEABLE | Context.MODE_WORLD_READABLE);
		}
	}

	/**
	 * 得到字符串
	 */
	public static String getString(String key) {
		return AppCache.getString(key, "");
	}

	/**
	 * 得到布尔值
	 * 
	 * @param key
	 * @return
	 */
	public static boolean getBoolean(String key) {
		return AppCache.getBoolean(key, false);
	}

	/**
	 * 得到整型
	 * 
	 * @param key
	 * @return
	 */
	public static int getInt(String key) {
		return AppCache.getInt(key, 0);
	}

	/**
	 * 设定缓冲String类型key-value对
	 * 
	 * @param key
	 * @param value
	 */
	public static void setString(String key, String value) {
		AppCache.edit().putString(key, value).commit();
	}

	/**
	 * 设定缓冲boolean类型key-value对
	 * 
	 * @param key
	 * @param value
	 */
	public static void setBoolean(String key, boolean value) {
		AppCache.edit().putBoolean(key, value).commit();
	}

	/**
	 * 设定缓冲int类型key-value对
	 * 
	 * @param key
	 * @param value
	 */
	public static void setInt(String key, int value) {
		AppCache.edit().putInt(key, value).commit();
	}
}
