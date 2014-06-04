/**
 * 
 */
package org.mymmsc.api.j2ee.samples;

import java.util.HashMap;
import java.util.Map;

import org.mymmsc.api.io.HttpClient;
import org.mymmsc.api.io.HttpResult;

/**
 * @author WangFeng
 * @remark 测试cookie跨域
 */
public class TestP3P {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int timeout = 30;
		String uri = "http://localhost:8080/apps";
		uri = "http://redis.api.mymmsc.org:16080/wifi-api/sso.cgi?domain=sas.api.mymmsc.org";
		String key = "UserTc7ib084US";
		HttpResult hRet = null;
		HttpClient hc = new HttpClient(uri, timeout);
		hRet = hc.post(null, null);
		System.out.println("http-status=[" + hRet.getStatus() + "], body=["
				+ hRet.getBody() + "], message=" + hRet.getError());
		uri = "http://sas.api.mymmsc.org:16080/wifi-api/reg.cgi";
		hc = new HttpClient(uri, timeout);
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("P3P", "CP=\"CURa ADMa DEVa PSAo PSDo OUR BUS UNI PUR INT DEM STA PRE COM NAV OTC NOI DSP COR\"");
		hRet = hc.post(headers, null);
		System.out.println("http-status=[" + hRet.getStatus() + "], body=["
				+ hRet.getBody() + "], message=" + hRet.getError());
		
	}

}
