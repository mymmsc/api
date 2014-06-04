package org.mymmsc.android.app.util;

public abstract interface Logger {

	public abstract Logger newInstance(String tag);

	public abstract void info(String msg);

	public abstract void error(String msg);

	public abstract void error(String msg, Throwable tr);
}
