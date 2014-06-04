/**
 * @(#)IniConfig.java	6.3.9 09/11/02
 *
 * Copyright 2000-2010 MyMMSC Software Foundation (MSF), Inc. All rights reserved.
 * MyMMSC PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.mymmsc.api.io;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ConcurrentHashMap;

import org.mymmsc.api.assembly.Api;
import org.mymmsc.api.assembly.ExceptionEx;

/**
 * @author WangFeng(wangfeng@yeah.net)
 * @version 6.3.9 09/10/02
 * @since mymmsc-api 6.3.9
 */
public class IniConfig {
	// 定义一个Hash表
	// private static Map IniHashMap = new HashMap();
	private ConcurrentHashMap<String, String> IniHashMap = new ConcurrentHashMap<String, String>();

	/**
	 * IniConfig
	 * 
	 * @todo 从ini文件读取参数到HashMap表
	 * @param IniConfig
	 *            String
	 */
	public IniConfig(String IniConfig) {
		super();
		FileInputStream fileInputStream = null;
		String tempOption = "";
		try {
			fileInputStream = new FileInputStream(IniConfig);
			BufferedReader bufferedreader = new BufferedReader(
					new InputStreamReader(fileInputStream));
			String strTemp = null;
			while ((strTemp = bufferedreader.readLine()) != null) {
				int j = 0;
				int k = 0;
				int l = 0;
				String strPrefix = null;
				String strSuffix = null;
				strTemp = strTemp.trim();
				if (strTemp.length() != 0 && strTemp.charAt(0) != '#'
						&& strTemp.charAt(0) != ';') {
					j = strTemp.indexOf("#");
					k = strTemp.indexOf(";");
					if (j > 0 && k > 0) {
						l = j >= k ? k : j;
					}
				} else {
					if (j > 0) {
						l = j;
					} else {
						if (k > 0) {
							l = k;
						} else {
							l = -1;
						}
					}
					if (l > 0) {
						strTemp = strTemp.substring(0, l);
						strTemp = strTemp.trim();
					} else {
						continue;
					}
				}
				// 判断是否区域
				j = strTemp.indexOf("[");
				k = strTemp.indexOf("]");
				;
				if (j >= 0 && k >= 1) {
					tempOption = strTemp.substring(j + 1, k);
				}
				// 读取参数放进hash表
				j = strTemp.indexOf("=");
				k = strTemp.length();
				if (j > 0 && k >= 2) {
					strPrefix = strTemp.substring(0, j).trim();
					if (j >= k) {
						strSuffix = "";
					} else {
						strSuffix = strTemp.substring(j + 1).trim();
					}
					IniHashMap.put(tempOption.length() > 0 ? Api
							.StringCat(tempOption, ".", strPrefix) : strPrefix,
							strSuffix);
				}
			}
			fileInputStream.close();
		} catch (IOException ex1) {
			System.out.println(ExceptionEx.getString(ex1.getStackTrace(),
					org.mymmsc.api.VersionInfo.NAME));
		}
	}

	/**
	 * 取得参数值
	 * 
	 * @todo 取得ini配置参数
	 * @param strParam
	 *            String
	 * @return String
	 */
	public synchronized String getConfig(String strParam) {
		if (IniHashMap.containsKey(strParam)) {
			return (String) IniHashMap.get(strParam);
		} else {
			return "";
		}
	}

	/**
	 * getHashMap
	 * 
	 * @return ConcurrentHashMap
	 */
	public synchronized ConcurrentHashMap<String, String> getHashMap() {
		if (IniHashMap == null) {
			return null;
		} else {
			return IniHashMap;
		}
	}

	public static void main(String[] args) {
		String iniFile = "/mymmsc/conf/vnet.properties";
		IniConfig iniconfig = new IniConfig(iniFile);
		System.out.println(iniconfig.getConfig("VNET.UserMsgSubmit.Response"));
		System.out.println(iniconfig.getConfig("ErrorCode.401"));
	}
}
