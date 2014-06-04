/**
 * @(#)HttpClient.java	6.3.12 2012/05/11
 *
 * Copyright 2000-2010 MyMMSC Software Foundation (MSF), Inc. All rights reserved.
 * MyMMSC PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.mymmsc.api.io;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.mymmsc.api.assembly.Api;

/**
 * HttpClient
 * 
 * @author WangFeng(wangfeng@yeah.net)
 * @version 6.3.12 2012/05/11
 * @since mymmsc-api 6.3.12
 * @since mymmsc-api 6.3.9
 * @remark 已支持cookie和session, [wangfeng @2012/06/19]
 * @remark 暂时不支持cookie, 主要没考虑完全, 如果是移动终端在调用, cookie怎么保存的问题.
 */
public class HttpClient {
	private static final String ENCODING = "utf-8";
	private static final String METHOD_POST = "POST";
	private static final String METHOD_GET = "GET";

	private String url = null;
	private int timeout = 30; // 默认超时30秒
	private String cookiePath = null;
	private String boundary = null;
	private ByteArrayOutputStream data = null; // HTTP-Body区域的二进制数据
	@SuppressWarnings("unused")
	private HashMap<String, Object> respHeaders = null; // 预备后续比较复杂的应用,
														// 获取HTTP-Header域的内容
	/** 用户会话保持 */
	private static HashMap<String, String> session = null;
	/** HTTP方法 */
	private String method = null;

	/**
	 * HttpClient 构造函数
	 * 
	 * @param url
	 * @param timeout
	 *            超时秒数
	 */
	public HttpClient(String url, int timeout) {
		this.url = url;
		this.timeout = timeout;
		// 计算一个boundary
		this.boundary = Api.o3String(14);
		this.data = new ByteArrayOutputStream();
		this.respHeaders = new HashMap<String, Object>();
		if (session == null) {
			session = new HashMap<String, String>();
		}
		int pos = url.lastIndexOf("/");
		if (pos > 0) {
			cookiePath = url.substring(0, pos);
		} else {
			cookiePath = url;
		}
		pos = cookiePath.indexOf("://");
		if (pos > 0) {
			cookiePath = cookiePath.substring(pos + 3);
		}
		// String exp = "http://([^/]+)";
		// cookiePath = RegExp.get(url, exp, null);
		System.out.println("cookiePath = " + cookiePath);
	}

	/**
	 * @return the method
	 */
	public String getMethod() {
		return method;
	}

	/**
	 * @param method
	 *            the method to set
	 */
	public void setMethod(String method) {
		this.method = method;
	}

	/**
	 * 获得Session
	 * 
	 * @param connection
	 * @return
	 */
	private String getSession(HttpURLConnection connection) {
		String sessionId = "";
		String key = null;
		String cookie = null;
		int pos = -1;
		for (int i = 1; (key = connection.getHeaderFieldKey(i)) != null; i++) {
			System.out.print(key + ":");
			System.out.println(connection.getHeaderField(key));
			if (key.equalsIgnoreCase("set-cookie")) {
				cookie = connection.getHeaderField(i);
				pos = cookie.indexOf(";");
				if (pos > 0) {
					cookie = cookie.substring(0, pos);
				}
				sessionId += "; " + cookie;
			}
		}

		if (sessionId.length() > 2) {
			sessionId = sessionId.substring(2);
		}
		return sessionId;
	}

	/**
	 * 增加一个表单字段, 二进制方式
	 * 
	 * @param name
	 *            字段名
	 * @param value
	 *            值
	 */
	public void addField(String name, byte[] value) {
		String temp = String.format(
				"--%s\r\nContent-Disposition: form-data; name=\"%s\"\r\n\r\n",
				boundary, name);
		try {
			data.write(temp.getBytes(ENCODING));
			data.write(value);
			temp = "\r\n";
			data.write(temp.getBytes(ENCODING));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 增加一个表单字段
	 * 
	 * @param name
	 *            字段名
	 * @param value
	 *            值, 可以是除了自定义类以外的任何类型的值, 包括基础数据类型或类对象
	 */
	public void addField(String name, Object value) {
		String temp = Api.toString(value);
		try {
			addField(name, temp.getBytes(ENCODING));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 增加二进制文件
	 * 
	 * @param name
	 *            字段名
	 * @param filename
	 *            文件名称
	 * @param type
	 *            文件类型, 如果type为null, 则默认为二进制文件, 图片类型的可以是"image/png" 或者
	 *            "image/jpeg", 视频文件参照对应的格式, 或者type为null, 将默认以二进制方式传输
	 * @param buff
	 *            文件数据
	 */
	public void addFile(String name, String filename, String type, byte[] buff) {
		String temp = String
				.format("--%s\r\nContent-Disposition: form-data; name=\"%s\"; filename=\"%s\"\r\n",
						boundary, name, filename);
		try {
			data.write(temp.getBytes(ENCODING));
			if (type != null) {
				temp = String.format("Content-Type: %s\r\n\r\n", type);
			} else {
				temp = String
						.format("Content-Type: application/octet-stream\r\nContent-Transfer-Encoding: binary\r\n\r\n");
			}
			data.write(temp.getBytes(ENCODING));
			data.write(buff);
			temp = "\r\n";
			data.write(temp.getBytes(ENCODING));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 基础的HTTP协议请求
	 * 
	 * @param heades
	 *            头信息map, 允许调用者在headers里面修改Content-Type
	 * @param body
	 *            消息体HashMap, 允许body为字符串, 例如传输XML或者jSON
	 * @remark 默认为UTF-8编码格式. 如果body为空, 则自动转为GET方式.
	 */
	public HttpResult post(Map<String, String> headers, Object body) {
		HttpResult hRet = new HttpResult();
		// String msg = "";
		URL httpUrl = null;
		HttpURLConnection httpConn = null;
		try {
			httpUrl = new URL(url);
			System.out.println(httpUrl.getHost() + ", " + httpUrl.getPort());
			httpConn = (HttpURLConnection) httpUrl.openConnection();
			// 处理cookie
			String theCookie = session.get(cookiePath);
			if (theCookie != null) {
				httpConn.setRequestProperty("Cookie", theCookie);
			}
			httpConn.setConnectTimeout(timeout * 1000);
			httpConn.setReadTimeout(timeout * 1000);

			String temp = null;
			// 处理HTTP-Header域
			// httpConn.setRequestProperty("Connection", "keep-alive");
			if (headers != null) {
				for (String key : headers.keySet()) {
					String value = headers.get(key);
					httpConn.setRequestProperty(key, value);
				}
			}

			// 处理HTTP-Body数据区
			if (body != null) {
				if (body instanceof String) {
					// body 是字符串, 这个应用可以是XML传输
					temp = (String) body;
					data.write(temp.getBytes(ENCODING));
				} else if (body instanceof Map) {
					@SuppressWarnings("unchecked")
					HashMap<String, Object> tmpMap = (HashMap<String, Object>) body;
					for (String key : tmpMap.keySet()) {
						Object value = tmpMap.get(key);
						addField(key, value);
					}
				}
			}
			if (data.size() > 0) {
				if (body instanceof String) {
					// 如果body是字符串, 可能是XML或json
					if (((String) body).trim().toLowerCase()
							.startsWith("<?xml")) {
						// xml
						httpConn.setRequestProperty("Content-Type",
								"text/xml; charset=utf-8");
					} else {
						// json
						httpConn.setRequestProperty("Content-Type",
								"application/json; charset=utf-8");
					}
				} else {
					// 如果body不是字符串, 则认定为表单模式, 添加最后结束标识
					temp = String.format("--%s--\r\n", boundary);
					data.write(temp.getBytes());
					// 设置Content-Type为form模式
					temp = String.format("multipart/form-data; boundary=%s",
							boundary);
					httpConn.setRequestProperty("Content-Type", temp);
				}
			}
			// 设置request Method
			if (method != null) {
				httpConn.setRequestMethod(method);
			} else if (data.size() > 0) {
				httpConn.setRequestMethod(METHOD_POST);
				httpConn.setRequestProperty("Content-Length",
						Integer.toString(data.size()));
			} else {
				// 如果body为null, 修改HTTP方法为GET, 并试图去掉之前对Content-Type的定义
				httpConn.setRequestMethod(METHOD_GET);
			}

			httpConn.setDoInput(true);
			if (data.size() > 0) {
				httpConn.setDoOutput(true);

				OutputStream out = httpConn.getOutputStream();
				out.write(data.toByteArray());
				out.flush();
				out.close();
				data.close();
			}
			int httpStatus = httpConn.getResponseCode();
			System.out.println(httpConn.getContentType());
			hRet.setStatus(httpStatus);
			hRet.setDate(new Date(httpConn.getDate()));
			String contentType = httpConn.getContentType();
			if (httpStatus == 200) {
				// 保存cookie
				String cookie = getSession(httpConn);
				if (!Api.isEmpty(cookie) && cookiePath != null) {
					session.put(cookiePath, cookie);
				}

				if (Api.isEmpty(contentType)) {
					contentType = "text/plain; charset=utf-8";
				}

				String ct = contentType.toLowerCase().trim();
				if (ct.indexOf("charset") >= 0 || ct.indexOf("text") >= 0
						|| ct.indexOf("xml") >= 0 || ct.indexOf("json") >= 0) {
					// 接收body
					String response = DataStream.RecvHttpData(
							new DataInputStream(httpConn.getInputStream()),
							ENCODING);
					hRet.setBody(response);
				} else {
					String str = httpConn.getHeaderField("Last-Modified");
					Date tm = Api.parseDate(str);
					hRet.setDate(tm);
					hRet.setData(DataStream.recv(httpConn.getInputStream()));
				}
			} else {
				//
			}
		} catch (IOException e) {
			hRet.setError(e.getMessage());
			if (hRet.getStatus() != 200) {
				hRet.setStatus(900);
			}
		} finally {
			if (httpConn != null) {
				httpConn.disconnect();
			}
		}

		return hRet;
	}
}
