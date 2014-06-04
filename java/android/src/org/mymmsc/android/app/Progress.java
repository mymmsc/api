/**
 * @(#)Progress.java	8.0.1 2011-6-6
 *
 * Copyright 2004-2011 mymmsc.org (MyMMSC), Inc. All rights reserved.
 * MyMMSC PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.mymmsc.android.app;

import org.mymmsc.api.assembly.Api;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;

/**
 * 进度条
 * 
 * @author WangFeng(mail:wangfeng@yeah.net, mobile:13911113230)
 * @version 1.0.1 2011-5-28
 * @since mymmsc-android 1.0.1
 */
public class Progress {
	private int mMaxNumber = 9000;
	private int mSpeed = 1;
	private Context mContext = null;
	private int m_dlgCount = 0;
	private ProgressDialog m_dlgResult = null;
	private Dialog m_dlgWait = null;
	private int xResult = 0;
	private IProgressCallback mCallback = null;
	private boolean mbOkey = true;

	/**
	 * 构造方法
	 * 
	 * @param context
	 */
	public Progress(Context context) {
		mContext = context;
	}

	public void setMax(int max, int speed) {
		mMaxNumber = max;
		mSpeed = speed;
	}

	/**
	 * 是否显示确认框
	 * 
	 * @param bOk
	 */
	public void isOkey(boolean bOk) {
		mbOkey = bOk;
	}

	/**
	 * 启动进度条
	 * 
	 * @param callback
	 *            回调接口实例
	 */
	public void start(IProgressCallback callback) {
		mCallback = callback;
		m_dlgCount = 0;
		// 创建ProgressDialog对象
		m_dlgResult = new ProgressDialog(mContext);
		// 设置进度条风格，风格为长形
		m_dlgResult.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		// m_dlgResult.setProgressStyle(android.R.attr.progressBarStyleHorizontal);
		// 设置ProgressDialog 标题
		m_dlgResult.setTitle(mCallback.getProcessMessage());
		// 设置ProgressDialog 提示信息
		// m_dlgResult.setMessage("这是一个长形对话框进度条");
		// 设置ProgressDialog 标题图标
		// m_dlgResult.setIcon(android.R.drawable.stat_notify_sync);
		// 设置ProgressDialog 进度条进度
		m_dlgResult.setProgress(mMaxNumber);
		// 设置ProgressDialog 的进度条是否不明确
		m_dlgResult.setIndeterminate(false);
		// 设置ProgressDialog 是否可以按退回按键取消
		m_dlgResult.setCancelable(false);
		// 让ProgressDialog显示
		m_dlgResult.show();

		new Thread() {
			public void run() {
				while (m_dlgCount <= mMaxNumber) {
					// 由线程来控制进度。
					m_dlgResult.setProgress(m_dlgCount++);
					Api.sleep(100 * mSpeed);
				}
				if (m_dlgResult.isShowing()) {
					m_dlgResult.setProgress(mMaxNumber);
					m_dlgResult.cancel();
				}
				m_dlgCount = mMaxNumber + 1;
			}
		}.start();
		new Thread() {
			public void run() {
				// 初始化数据
				mCallback.execute();
				m_dlgResult.setProgress(mMaxNumber);
				if (m_dlgResult.isShowing()) {
					m_dlgResult.cancel();
					m_dlgCount = mMaxNumber + 2;
				} else {
					m_dlgCount = mMaxNumber + 3;
				}
				handler.sendEmptyMessage(mbOkey ? 0 : 1);
			}
		}.start();
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			xResult = msg.what;
			m_dlgResult.dismiss();
			if (xResult == 0) {
				mCallback.forward();
				// 对话框
				m_dlgWait = new AlertDialog.Builder(mContext)
						.setTitle(mCallback.getTitle())
						.setMessage(mCallback.getMessage())
						// .setView(dialogView)
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) {
										mCallback.pass();
									}
								}).create();// 创建按钮
				m_dlgWait.show();
			} else if (xResult == 1) {
				mCallback.forward();
				mCallback.pass();
			}
		}
	};
}
