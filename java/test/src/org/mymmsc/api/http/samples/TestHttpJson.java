/**
 * 
 */
package org.mymmsc.api.http.samples;

import org.mymmsc.api.assembly.Api;
import org.mymmsc.api.context.JsonAdapter;
import org.mymmsc.api.io.HttpClient;
import org.mymmsc.api.io.HttpResult;

/**
 * @author wangfeng
 * 
 */
public class TestHttpJson {
	public static void main(String argv[]) {
		int timeout = 30;
		String uri = "http://localhost:8080/apps";
		uri = "http://svn.congzheli.com/api/reg";
		String key = "UserTc7ib084US";
		HttpResult hRet = null;
		HttpClient hc = new HttpClient(uri, timeout);
		String userName = "labs";
		String email = "89009@qq.com";
		String password = "123456";
		@SuppressWarnings("unused")
		String phone = "18612033288";
		String type = "0";
		long cur=System.currentTimeMillis();
		String now=String.valueOf(cur);
		String time= now.substring(0,10);
		String postData = "{\"m_username\":\""+userName+"\",\"m_password\":\""+password+"\",\"m_email\":\""+email+"\",\"m_type\":\""+type+"\",\"m_register_time\":\""+time+"\"}";
		System.out.println(postData);
		hc.addField("postdata", postData);
		hc.addField("time", time);
		hc.addField("sign", Api.md5(postData + time+key));
		hRet = hc.post(null,null);
		System.out.println("http-status=[" + hRet.getStatus() + "], body=["
				+ hRet.getBody() + "], message=" + hRet.getError());
		// 服务器正常的话, 应该看到http请求的状态码以及json串
		JsonAdapter json = JsonAdapter.parse(hRet.getBody());
		ZDTest info = json
				.get(ZDTest.class);
		System.out.println(info);
	}
}
