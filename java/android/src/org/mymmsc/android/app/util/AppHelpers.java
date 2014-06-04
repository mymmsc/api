/**
 * @(#)Helpers.java	8.0.1 2011-6-5
 *
 * Copyright 2004-2011 mymmsc.org (MyMMSC), Inc. All rights reserved.
 * MyMMSC PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.mymmsc.android.app.util;

import java.util.HashMap;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * Android集合杂类
 * 
 * @author WangFeng(mail:wangfeng@yeah.net, mobile:13911113230)
 * @version 1.0.1 2011-5-28
 * @since mymmsc-android 1.0.1
 */
public class AppHelpers {
	private static PackageInfo appInfo = null;
	private static HashMap<String, Integer> hmPages = new HashMap<String, Integer>();
	/** 软件升级地址 */
	private static String appUri;
	/** 是否继续提醒 */
	private static boolean isAlert = true;
	/** 视图点击效果 */
	public static final OnTouchListener TouchForView = new View.OnTouchListener() {
		@Override
		public boolean onTouch(View paramView, MotionEvent event) {
			ImageView btn = (ImageView) paramView;
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				btn.getDrawable().setAlpha(150);// 设置图片透明度0~255，0完全透明，255不透明
				btn.invalidate();
			} else {
				btn.getDrawable().setAlpha(255);// 还原图片
				btn.invalidate();
			}
			return false;
		}
	};

	/**
	 * 弹出提示信息条
	 * 
	 * @param ctx
	 *            上下文对象
	 * @param message
	 *            消息内容
	 */
	public static void alert(Context ctx, String message) {
		int height = ctx.getWallpaperDesiredMinimumHeight() / 2;
		alert(ctx, message, height);
	}

	/**
	 * 弹出提示信息条
	 * 
	 * @param ctx
	 *            上下文对象
	 * @param message
	 *            消息内容
	 * @param height
	 *            高度
	 */
	public static void alert(Context ctx, String message, int height) {
		Toast.makeText(ctx, message, height).show();
	}

	/**
	 * 弹出对话框
	 * 
	 * @param ctx
	 * @param title
	 * @param message
	 */
	public static void alert(Context ctx, String title, String message) {
		Dialog dialog = new AlertDialog.Builder(ctx).setTitle(title)
				.setMessage(message)
				// 设置内容
				.setPositiveButton("确定",// 设置确定按钮
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								//
							}
						}).create();// 创建
		// 显示对话框
		dialog.show();
	}

	/**
	 * @return the appInfo
	 */
	public static PackageInfo getAppInfo() {
		return appInfo;
	}

	/**
	 * @return the appVersion
	 */
	public static String getAppVersion() {
		return appInfo.versionName;
	}

	/**
	 * @param appInfo
	 *            the appInfo to set
	 */
	public static void setAppInfo(PackageInfo appInfo) {
		AppHelpers.appInfo = appInfo;
	}

	public static void setCurrentPage(String key, int currentPage) {
		hmPages.put(key, Integer.valueOf(currentPage));
	}

	public static int getCurrentPage(String key) {
		int currentPage = 1;
		Integer v = hmPages.get(key);
		if (v != null) {
			currentPage = v.intValue();
		}
		return currentPage;
	}

	public static void setAppUri(String appUri) {
		AppHelpers.appUri = appUri;
	}

	public static String getAppUri() {
		return appUri;
	}

	public static void setAlert(boolean isAlert) {
		AppHelpers.isAlert = isAlert;
	}

	public static boolean isAlert() {
		return isAlert;
	}
}
