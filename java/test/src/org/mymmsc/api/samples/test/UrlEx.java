/**
 * @(#)UrlEx.java	6.3.9 09/10/02
 *
 * Copyright 2000-2010 MyMMSC Software Foundation (MSF), Inc. All rights reserved.
 * MyMMSC PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.mymmsc.api.samples.test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * URL«Î«Û≤‚ ‘
 * 
 * @author WangFeng(wangfeng@yeah.net)
 * @version 6.3.9 09/10/02
 * @since mymmsc-test 6.3.9
 */
public class UrlEx {
	public UrlEx() {
		super();
	}

	public static void main(String[] argv) throws Exception {
		URL url = new URL("http://www.xicn.net/");
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("POST");
		connection.setDoOutput(true);
		PrintWriter out = new PrintWriter(connection.getOutputStream());
		// encode the message
		String name = "name=" + URLEncoder.encode("Qusay Mahmoud", "UTF-8");
		String email = "email="
				+ URLEncoder.encode("qmahmoud@javacourses.com", "UTF-8");
		// send the encoded message
		out.println(name + "&" + email);
		out.close();
		BufferedReader in = new BufferedReader(new InputStreamReader(connection
				.getInputStream()));
		String line;
		while ((line = in.readLine()) != null) {
			System.out.println(line);
		}
		in.close();
	}

}
