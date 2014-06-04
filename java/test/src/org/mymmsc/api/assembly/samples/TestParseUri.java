package org.mymmsc.api.assembly.samples;

import org.mymmsc.api.assembly.Api;

public class TestParseUri {

	private static String fixUri(String uri) {
		StringBuffer sb = new StringBuffer();
		String[] as = uri.split("[/]+");
		if (as.length > 2) {
			for (int i = 2; i < as.length; i++) {
				if (sb.length() > 0) {
					sb.append("/");
				}
				sb.append(as[i]);
			}
		} else {
			sb.append("/");
		}
		
		return sb.toString();
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String str = "/apps////////a/b.action";
		String id = null;
		str = "/home/runtime/data/wifi/files/devp/template/d000017/standard";
		str = "d000017";
		String[] as = str.split("[/]+");
		if (as != null && as.length > 2) {
			id = as[as.length - 2];
		} else {
			id = str;
		}
		System.out.println("id=[" + id + "]");
		System.out.println(fixUri(str));
		String uri = "id=89bd376c-124c-430f-b393-c97885d4a788&type=jpg&a=&";
		Api.getParams(uri);
		System.out.print(Api.getLocalIp());
	}

}
