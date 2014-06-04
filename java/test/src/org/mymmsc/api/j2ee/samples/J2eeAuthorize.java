/**
 * 
 */
package org.mymmsc.api.j2ee.samples;

import org.mymmsc.api.assembly.Api;

/**
 * @author wangfeng
 *
 */
public class J2eeAuthorize {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String project = "wifiweb";
		String macAddr = "";
		String userId = "SH0001";
		String expire = "2014-06-01 00:00:00";
		
		String tmpKey = Api.md5(userId + "mymmsc" + expire + "j2ee" + project);
		System.out.println("key=[" + tmpKey + "]");
	}

}
