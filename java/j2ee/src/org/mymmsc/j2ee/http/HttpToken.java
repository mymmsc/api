/**
 * 
 */
package org.mymmsc.j2ee.http;

import org.mymmsc.api.assembly.Api;
import org.mymmsc.api.context.JsonAdapter;
import org.mymmsc.api.crypto.ThreeDES;

/**
 * HTTP 令牌
 * 
 * @author WangFeng
 * @remark 拟代替session的功能
 */
public class HttpToken implements Token {
	/** 3DES工具类对象 */
	private ThreeDES des3 = null;
	/** 令牌 */
	protected String token = null;

	public boolean init(String key) {
		des3 = new ThreeDES();
		return des3.init(key);
	}

	public String encode(Object obj) {
		String str = null;
		if (obj instanceof String) {
			str = (String) obj;
		} else {
			str = JsonAdapter.get(obj, true);
		}
		System.out.println("json = " + str);
		byte[] encoded = des3.encode(str.getBytes());
		token = Api.MemToHex(encoded);
		return token;
	}

	public <T> T decode(Class<T> clazz) {
		T obj = null;
		byte[] buff = des3.decode(Api.HexToMem(token));
		String str = new String(buff);
		if (Api.isBaseType(clazz)) {
			obj = Api.valueOf(clazz, str);
		} else {
			JsonAdapter json = JsonAdapter.parse(str);
			if (json != null) {
				obj = json.get(clazz);
			}
		}
		return obj;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public static void main(String[] argv) {
		HttpFile file = new HttpFile();
		file.setFieldName("abc.xml");
		byte[] keyBytes = { 0x11, 0x22 };
		file.setFileData(keyBytes);
		//file.setAuthor(new String[]{"wangfeng","lizhi"});
		HttpToken token = new HttpToken();
		token.init("com.fengxiafei.app.token");
		String str = token.encode(file);
		System.out.println("token = [" + str + "]");
		file = null;
		token.setToken(str);
		file = token.decode(HttpFile.class);
		System.out.println("file = [" + file + "]");

	}
}
