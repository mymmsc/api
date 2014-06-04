/**
 * @(#)SoftInfo.java	8.0.1 2011-5-28
 *
 * Copyright 2004-2011 mymmsc.org (MyMMSC), Inc. All rights reserved.
 * MyMMSC PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.mymmsc.android.app.upgrade;

import android.graphics.drawable.Drawable;

/**
 * 应用程序信息
 * 
 * @author WangFeng(mail:wangfeng@yeah.net, mobile:13911113230)
 * @version 1.0.1 2011-5-28
 * @since mymmsc-android 1.0.1
 */
public class SoftInfo {
	private String name;
	private String descript;
	private String version;
	private String uri;
	private Drawable icon;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescript() {
		return descript;
	}

	public void setDescript(String descript) {
		this.descript = descript;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public void setIcon(Drawable icon) {
		this.icon = icon;
	}

	public Drawable getIcon() {
		return icon;
	}

}
