/**
 * 
 */
package org.mymmsc.api.http.samples;

import java.io.IOException;

import org.mymmsc.api.io.FileApi;
import org.mymmsc.api.io.HttpClient;
import org.mymmsc.api.io.HttpResult;

/**
 * @author wangfeng
 *
 */
public class Download {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String s = "http://f.ifengzi.cn/apps/show.cgi?id=9f0c30bf-18a3-34c2-8309-568a26f6778a&type=jpg";
		HttpClient hc = new HttpClient(s, 30);
		HttpResult hRet = hc.post(null, null);
		System.out.println("http-status=[" + hRet.getStatus() + "], body=["
				+ hRet.getBody() + "], message=" + hRet.getError());
		System.out.println(hRet.getData().length);
		System.out.println(hRet.date());
		try {
			FileApi.write("/Users/wangfeng/temp/test.jpg", hRet.getData());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
