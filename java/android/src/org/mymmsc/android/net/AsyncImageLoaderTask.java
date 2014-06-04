/**
 * 
 */
package org.mymmsc.android.net;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import org.mymmsc.android.app.util.Utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

/**
 * 异步加载图片
 * 
 * @author WangFeng(mail:wangfeng@yeah.net, mobile:13911113230)
 * @version 1.0.1 2011-5-28
 * @since mymmsc-android-base 1.0.1
 */
public class AsyncImageLoaderTask extends AsyncTask<Object, Object, Bitmap> {
	private String classTag = this.getClass().getSimpleName();
	private ViewHolder holder = null;
	private static HashMap<String, SoftReference<Bitmap>> imageCache = null;

	public AsyncImageLoaderTask() {
		if (imageCache == null) {
			imageCache = new HashMap<String, SoftReference<Bitmap>>();
		}
	}

	@Override
	protected Bitmap doInBackground(Object... params) {
		Bitmap bitmap = null;
		String url = Utils.formatUri((String) params[0]);
		holder = (ViewHolder) params[1];
		if (holder == null) {
			Log.e(classTag, classTag + "> value of image is null.");
		}
		if (url != null) {
			if (imageCache.containsKey(url)) {
				SoftReference<Bitmap> mapSoft = imageCache.get(url);
				bitmap = mapSoft.get();
				if (bitmap != null) {
					return bitmap;
				}
			}
			holder.state = ViewHolder.ST_LOADING;
			URL fromUrl = null;
			try {
				fromUrl = new URL(url);
				HttpURLConnection conn = (HttpURLConnection) fromUrl
						.openConnection();
				conn.setDoInput(true);
				conn.setConnectTimeout(30 * 1000);
				conn.setReadTimeout(30 * 1000);
				conn.setRequestProperty("pragma", "no-cache");
				conn.setRequestProperty("Cache-Control", "no-cache");
				conn.connect();
				InputStream input = conn.getInputStream();
				bitmap = BitmapFactory.decodeStream(input);
				input.close();
			} catch (MalformedURLException e) {
				Log.e(classTag,
						classTag + "> doInBackground-1: " + e.getMessage());
				holder.state = ViewHolder.ST_NOTFOUND;
			} catch (IOException e) {
				Log.e(classTag,
						classTag + "> doInBackground-2: " + e.getMessage());
				holder.state = ViewHolder.ST_NOTFOUND;
			} catch (Exception e) {
				Log.e(classTag,
						classTag + "> doInBackground-3: " + e.getMessage());
			}
			if (bitmap != null) {
				imageCache.put(url, new SoftReference<Bitmap>(bitmap));
			} else if (holder.state != ViewHolder.ST_NOTFOUND) {
				holder.state = ViewHolder.ST_NONE;
				new AsyncImageLoaderTask().execute(url, holder);
			}
		}
		return bitmap;
	}

	@Override
	protected void onPostExecute(Bitmap result) {
		super.onPostExecute(result);
		if (result != null) {
			this.holder.image.setImageBitmap(result);
			this.holder.state = ViewHolder.ST_FINISHED;
			this.holder = null;
		}
	}

}
