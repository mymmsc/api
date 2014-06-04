package org.mymmsc.android.app;

/**
 * 列表资源单行内容抽象类
 * 
 * @author WangFeng(mail:wangfeng@yeah.net, mobile:13911113230)
 * @version 1.0.1 2011-5-28
 * @since mymmsc-android 1.0.1
 */
public abstract class AResource {
	protected String mButtonText = null;

	protected String cloneString(String value) {
		String sRet = "";
		if (value != null && value.trim().length() > 0) {
			sRet = value.trim();
		}
		return sRet;
	}

	protected void setButtonText(String text) {
		mButtonText = text;
	}
}
