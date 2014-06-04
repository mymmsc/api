/**
 * @(#)XmlResult.java	8.0.1 2011-5-14
 *
 * Copyright 2004-2011 mymmsc.org (MyMMSC), Inc. All rights reserved.
 * MyMMSC PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.mymmsc.android.app.util;

/**
 * 通信接口响应结果
 * 
 * @author WangFeng(mail:wangfeng@yeah.net, mobile:13911113230)
 * @version 1.0.1 2011-5-28
 * @since mymmsc-android 1.0.1
 */
public class XmlResult {
	/** 动作 */
	public String action;
	/** 状态码 */
	public String status;
	/** 状态消息 */
	public String message;
	/** 附加内容, action=update时, 此处为新版本的URL */
	public String content;

	public XmlResult() {
		action = "login";
		status = "999999";
		message = "系统正忙, 请稍候重试......";
		content = "";
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
