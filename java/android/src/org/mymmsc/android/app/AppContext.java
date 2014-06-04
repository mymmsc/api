/**
 * @(#)AppContext.java	8.0.1 2011-6-5
 *
 * Copyright 2004-2011 mymmsc.org (MyMMSC), Inc. All rights reserved.
 * MyMMSC PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.mymmsc.android.app;

import org.mymmsc.android.app.util.AppHelpers;
import org.mymmsc.android.app.util.Cache;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;

/**
 * Android平台应用程序上下文
 * 
 * @author WangFeng(mail:wangfeng@yeah.net, mobile:13911113230)
 * @version 1.0.1 2011-5-28
 * @since mymmsc-android 1.0.1
 */
public abstract class AppContext extends Activity implements IActivity {
	// 当前Activity
	private Activity theApp = null;
	private int resourceId = -1;
	@SuppressWarnings("unused")
	private Bundle m_bundle = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		m_bundle = savedInstanceState;
		Cache.init(this, "UserInfo");
	}

	@Override
	public void init(Activity context, int resourceId) {
		this.theApp = context;
		this.resourceId = resourceId;
		if (this.resourceId > -1) {
			setContentView(this.resourceId);
		}
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}

	@Override
	public void init(Activity context) {
		init(context, -1);
	}

	/**
	 * 禁止控件
	 * 
	 * @category 只对ImageView类有效
	 */
	protected void setDisable(int id) {
		ImageView ib = (ImageView) findViewById(id);
		ib.setAlpha(50);
		ib.setEnabled(false);
	}

	/**
	 * 绑定一个图片按钮
	 * 
	 * @param id
	 * @return
	 */
	protected ImageButton findById(int id) {
		ImageButton ibRet = (ImageButton) theApp.findViewById(id);
		ibRet.setOnTouchListener(AppHelpers.TouchForView);
		return ibRet;
	}

	/**
	 * 页面转向
	 * 
	 * @param cls
	 * @return
	 */
	protected boolean doPage(Class<?> cls) {
		boolean bRet = false;
		Intent intent = new Intent(theApp, cls);
		try {
			startActivity(intent);
			bRet = true;
		} catch (ActivityNotFoundException e) {
			//
		}

		return bRet;
	}

	/**
	 * 弹出对话框
	 * 
	 * @param title
	 * @param message
	 */
	protected void alert(String title, String message) {
		AppHelpers.alert(theApp, title, message);
	}

	/**
	 * 使用默认浏览器打开一个网址
	 * 
	 * @param url
	 */
	protected void openUrl(String url) {
		Uri uri = Uri.parse(url);
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		startActivity(intent);
	}
}
