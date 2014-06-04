/**
 * @(#)SQLRegExp.java	6.3.9 09/10/02
 *
 * Copyright 2000-2010 MyMMSC Software Foundation (MSF), Inc. All rights reserved.
 * MyMMSC PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.mymmsc.api.sql;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则表达式
 * 
 * @author WangFeng(wangfeng@yeah.net)
 * @version 6.3.9 09/10/02
 * @since mymmsc-boss 6.3.9
 */
public class SQLRegExp {

	public static ArrayList<String> match(String s, String exp) {
		ArrayList<String> sList = null;
		// String regEx="([^-])"; String str="00-未审核,01-正常,11-应聘信息,12-招聘信息";
		Pattern p = Pattern.compile(exp, Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(s);
		boolean rs = m.find();
		if (rs) {
			for (int i = 1; i <= m.groupCount(); i++) {
				if (sList == null) {
					sList = new ArrayList<String>();
				}
				sList.add(m.group(i));
				// System.out.println(m.group(i));
			}
		}

		return sList;
	}

	public static Map<String, String> match(String s) {
		Map<String, String> map = new HashMap<String, String>();
		String[] sl = s.split(",");
		for (int i = 0; i < sl.length; i++) {
			String[] vsl = sl[i].split("-");
			if (vsl.length == 2) {
				map.put(vsl[0], vsl[1]);
			}
		}
		return map;
	}

	public static void main(String[] args) {
		String str = "00-未审核,01-正常,11-应聘信息,12-招聘信息";

		Map<String, String> sl = SQLRegExp.match(str);
		System.out.println(sl.get("01"));
	}
}
