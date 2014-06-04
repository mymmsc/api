package org.mymmsc.android.net;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.client.ClientProtocolException;
import org.mymmsc.android.app.util.Utils;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 下载
 * 
 * @author WangFeng(mail:wangfeng@yeah.net, mobile:13911113230)
 * @version 1.0.1 2011-5-28
 * @since mymmsc-android 1.0.1
 */
public class Download {
	private Activity mActivity = null;
	private ProgressBar mBar = null;
	private TextView mInfo = null;
	private int fileSize = 0;
	private int downLoadFileSize = 0;
	private String mFilename = null;

	public Download(Activity activity) {
		this.mActivity = activity;
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (!Thread.currentThread().isInterrupted()) {
				switch (msg.what) {
				case 0:
					mBar.setMax(fileSize);
				case 1:
					mBar.setProgress(downLoadFileSize);
					int result = downLoadFileSize * 100 / fileSize;
					mInfo.setText(result + "%");
					break;
				case 2:
					Toast.makeText(mActivity, "下载完毕！", 1).show();
					break;
				case -1:
					String error = msg.getData().getString("error");
					Toast.makeText(mActivity, error, 1).show();
					break;
				}
			}
			super.handleMessage(msg);
		}
	};

	public void start(View view, final String uri, final String path) {
		this.mBar = (ProgressBar) view.findViewWithTag("progress");
		this.mInfo = (TextView) view.findViewWithTag("info");
		new Thread() {
			public void run() {
				try {
					down_file(Utils.formatUri(uri), path);
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}.start();

	}

	private void down_file(String url, String path) throws IOException {
		mFilename = url.substring(url.lastIndexOf("/") + 1);
		URL myURL = new URL(url);
		URLConnection conn = myURL.openConnection();
		conn.connect();
		InputStream is = conn.getInputStream();
		this.fileSize = conn.getContentLength();
		if (this.fileSize <= 0) {
			throw new RuntimeException("长度不正确");
		}
		if (is == null) {
			throw new RuntimeException("stream is null");
		}
		FileOutputStream fos = new FileOutputStream(path + mFilename);
		byte buf[] = new byte[1024];
		downLoadFileSize = 0;
		sendMsg(0);
		do {
			int numread = is.read(buf);
			if (numread == -1) {
				break;
			}
			fos.write(buf, 0, numread);
			downLoadFileSize += numread;

			sendMsg(1);
		} while (true);
		sendMsg(2);
		try {
			is.close();
		} catch (Exception ex) {
			Log.e("tag", "error: " + ex.getMessage(), ex);
		}

	}

	private void sendMsg(int flag) {
		Message msg = new Message();
		msg.what = flag;
		handler.sendMessage(msg);
	}

}