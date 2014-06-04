/**
 * @(#)AsyncImageLoader.java	8.0.1 2011-5-28
 *
 * Copyright 2004-2011 mymmsc.org (MyMMSC), Inc. All rights reserved.
 * MyMMSC PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.mymmsc.android.net;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

import org.mymmsc.android.app.util.Utils;

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * 异步加载图片
 * 
 * @author WangFeng(mail:wangfeng@yeah.net, mobile:13911113230)
 * @version 1.0.1 2011-5-28
 * @since mymmsc-android-base 1.0.1
 */
public class AsyncImageLoader {

	private static HashMap<String, SoftReference<Drawable>> imageCache = null;

	public AsyncImageLoader() {
		if (imageCache == null) {
			imageCache = new HashMap<String, SoftReference<Drawable>>();
		}
	}

	public Drawable loadDrawable(final String imageUrl,
			final ImageCallback imageCallback) {

		if (imageCache.containsKey(imageUrl)) {
			SoftReference<Drawable> softReference = imageCache.get(imageUrl);
			Drawable drawable = softReference.get();
			if (drawable != null) {
				return drawable;
			}
		}
		final Handler handler = new Handler() {
			public void handleMessage(Message message) {
				imageCallback.imageLoaded((Drawable) message.obj, imageUrl);
			}
		};
		new Thread() {
			@Override
			public void run() {
				Drawable drawable = loadImageFromUrl(imageUrl);
				if (drawable != null) {
					imageCache.put(imageUrl, new SoftReference<Drawable>(
							drawable));
					Message message = handler.obtainMessage(0, drawable);
					handler.sendMessage(message);
				}
			}
		}.start();
		return null;
	}

	public static Drawable loadImageFromUrl(String url) {
		Drawable dRet = null;
		URL urlImage = null;
		InputStream is = null;

		try {
			urlImage = new URL(Utils.formatUri(url));
			// is = (InputStream) urlImage.getContent();
			is = urlImage.openStream();
			// String fn = down_file(Utils.formatUri(url), "/sdcard/");
			// is = new FileInputStream("/sdcard/" + fn);
			dRet = Drawable.createFromStream(is, "src");
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return dRet;
	}

	@SuppressWarnings("unused")
	private static String down_file(String url, String path) throws IOException {
		String mFilename = url.substring(url.lastIndexOf("/") + 1);
		URL myURL = new URL(url);
		URLConnection conn = myURL.openConnection();
		conn.connect();
		InputStream is = conn.getInputStream();
		int fileSize = conn.getContentLength();
		if (fileSize <= 0) {
			throw new RuntimeException("长度不正确");
		}
		if (is == null) {
			throw new RuntimeException("stream is null");
		}
		FileOutputStream fos = new FileOutputStream(path + mFilename);
		byte buf[] = new byte[1024];
		int downLoadFileSize = 0;
		do {
			int numread = is.read(buf);
			if (numread == -1) {
				break;
			}
			fos.write(buf, 0, numread);
			downLoadFileSize += numread;
		} while (true);

		try {
			is.close();
		} catch (Exception ex) {
			Log.e("tag", "error: " + ex.getMessage(), ex);
		}
		return fileSize == downLoadFileSize ? mFilename : null;
	}

	public interface ImageCallback {
		public void imageLoaded(Drawable imageDrawable, String imageUrl);
	}

}