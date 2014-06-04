/**
 * 
 */
package org.mymmsc.api.io.samples;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.mymmsc.api.context.JsonAdapter;
import org.mymmsc.api.http.samples.FengBagMenuMemberCardList;
import org.mymmsc.api.io.HttpClient;
import org.mymmsc.api.io.HttpResult;

/**
 * @author wangfeng
 * 
 */
public class TestHttpClient {
	public InputStream getStream(URL url, String post, URL cookieurl) {
		HttpURLConnection connection;
		String cookieVal = null;
		String sessionId = "";
		String key = null;
		if (cookieurl != null) {
			try {
				connection = (HttpURLConnection) cookieurl.openConnection();
				for (int i = 1; (key = connection.getHeaderFieldKey(i)) != null; i++) {
					if (key.equalsIgnoreCase("set-cookie")) {
						cookieVal = connection.getHeaderField(i);
						cookieVal = cookieVal.substring(0,
								cookieVal.indexOf(";"));
						sessionId = sessionId + cookieVal + ";";
					}
				}
				@SuppressWarnings("unused")
				InputStream in = connection.getInputStream();
				System.out.println(sessionId);
			} catch (MalformedURLException e) {
				System.out.println("url can't connection");
				return null;
			} catch (IOException e) {
				System.out.println(e.getMessage());
				return null;
			}
		}
		try {
			connection = (HttpURLConnection) url.openConnection();
			// 这个要写在Post前,否则会取不到值,原因我不知道
			if (cookieurl != null) {
				connection.setRequestProperty("Cookie", sessionId);
			}
			if (post != "") {
				connection.setDoOutput(true);
				connection.setRequestMethod("POST");
				connection.getOutputStream().write(post.getBytes());
				connection.getOutputStream().flush();
				connection.getOutputStream().close();
			}
			int responseCode = connection.getResponseCode();
			int contentLength = connection.getContentLength();
			//
			System.out.println("Content length: " + contentLength);
			if (responseCode != HttpURLConnection.HTTP_OK)
				return (null);
			InputStream in = connection.getInputStream();
			return (in);
		} catch (Exception e) {
			// System.out.println(e);
			// e.printStackTrace();
			return (null);
		}
	}

	public static void main(String argv[]) {
		String uri = "http://ifengzi.cn/apps";
		int timeout = 30;
		HttpResult hRet = null;
		HttpClient hc1 = new HttpClient(uri + "/login.action", timeout);
		hc1.addField("username", "18610203667");
		hc1.addField("password", "123456");
		hRet = hc1.post(null, null);
		System.out.println("http-status=[" + hRet.getStatus() + "], body=["
				+ hRet.getBody() + "], message=" + hRet.getError());

		HttpClient hc = new HttpClient(uri + "/getCode.action", 30);
		hc.addField("id", "abcd000138");
		hRet = hc.post(null, null);
		System.out.println("http-status=[" + hRet.getStatus() + "], body=["
				+ hRet.getBody() + "], message=" + hRet.getError());
		// 服务器正常的话, 应该看到http请求的状态码以及json串
		JsonAdapter json = JsonAdapter.parse(hRet.getBody());
		FengBagMenuMemberCardList list = json.get(FengBagMenuMemberCardList.class);
		System.out.println(list);
	}

}
