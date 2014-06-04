/**
 * @(#)HttpParameter.java	6.3.9 09/10/02
 *
 * Copyright 2000-2010 MyMMSC Software Foundation (MSF), Inc. All rights reserved.
 * MyMMSC PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.mymmsc.j2ee.http;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mymmsc.api.assembly.Api;

/**
 * 解析form表单变量或文件上传
 * 
 * @author WangFeng(wangfeng@yeah.net)
 * @see {@link http://wakan.blog.51cto.com/59583/7209}
 * @version 6.3.9 09/10/02
 * @since mymmsc-j2ee 6.3.9
 */
public class HttpParameter {
	/** 请求参数MAP */
	private Hashtable<String, Object> params = null;
	/** HttpServletRequest 一个引用 */
	private HttpServletRequest request = null;
	private HttpServletResponse response = null;
	/** 应用程序编码格式 */
	private String m_encApp = null;
	/** 数据编码格式 */
	private String m_encData = null;
	/** HTTP-Body */
	private byte[] body = null;

	/**
	 * HttpParameter构造函数
	 * 
	 * @param enc_app
	 *            应用程序编码格式
	 * @param enc_data
	 *            数据编码格式
	 */
	public HttpParameter(String enc_app, String enc_data) {
		m_encApp = enc_app;
		m_encData = enc_data;
	}
	
	public Hashtable<String, Object> objects() {
		return params;
	}
	
	/**
	 * 初始化输入输出流
	 * 
	 * @param req
	 *            HttpServletRequest
	 * @param resp
	 *            HttpServletResponse
	 */
	public void init(HttpServletRequest req, HttpServletResponse resp) {
		try {
			// 设定请求字符集
			req.setCharacterEncoding(m_encApp);
			request = req;
			response = resp;
			// 设定输出
			if (resp != null) {
				resp.setCharacterEncoding(m_encApp);
				//response.setContentType(m_encApp);
				resp.setContentType(Api.Sprintf("text/html; charset=%s",
						m_encApp));
				resp.setHeader("Pragma", "no-cache");
				resp.setHeader("Cache-Control", "no-cache");
			}
			parse(128);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void parse(int paramcount) throws IOException {
		//byte[] body = null;
		int bodyLen = 0;
		byte[] bound = null;
		int boundLen = 0;
		int index = 0;
		int count = 0;

		params = new Hashtable<String, Object>();
		bodyLen = request.getContentLength();
		if (bodyLen <= 0) {
			return;
		}
		body = new byte[bodyLen];
		BufferedInputStream dataIn = new BufferedInputStream(
				request.getInputStream());
		int readed = 0;
		int cur_read = 0;
		while (readed < bodyLen) {
			cur_read = dataIn.read(body, readed, bodyLen - readed);
			if (cur_read < 0) {
				break;
			}
			readed += cur_read;
		}
		int i = 0;
		while (i < bodyLen) {
			if (body[i] == 13 && body[i + 1] == 10) {
				break;
			} else {
				i++;
			}
		}
		if (i > bodyLen) {
			return;
		}
		boundLen = i;
		bound = new byte[boundLen];
		for (int j = 0; j < boundLen; j++) {
			bound[j] = body[j + index]; // decode bound
		}
		i = i + 2; // plus 2 to skip the following bytes "0D 0A"
		index = i; // point to the beginning of first parameter
		boolean moved = false;
		if (request.getContentType().indexOf("form-data") > 0) {
			while (i < bodyLen) {
				if (!moved && count == paramcount) {
					i = bodyLen - boundLen - 5; // subst more than 4, but little
												// than 10
					moved = true;
				}
				if (!compareByteArray(copybyte(body, i, boundLen), bound)) {
					i++;
				} else {
					count++;
					int j = index;
					while ((j < i)
							&& (body[j] != 13 || body[j + 1] != 10
									|| body[j + 2] != 13 || body[j + 3] != 10)) {
						j++;
					}
					if (j >= i) {
						break;
					}
					String paramHeader = new String(body, index, j - index + 2);
					index = j;
					int m = paramHeader.indexOf("name=\"");
					if (m < 0) {
						break;
					}
					m = m + 6; // point to name value
					int n = paramHeader.indexOf("\"", m);
					if (n <= m) {
						break;
					}
					String name = paramHeader.substring(m, n); // get name
					boolean isFile = false;
					String filename = "";
					String filetype = "";
					m = paramHeader.indexOf("filename=\"", n + 1);
					if (m > n) {
						isFile = true;
						m = m + 10; // skip (filename=")
						n = paramHeader.indexOf("\"", m);
						if (n > m) {
							filename = paramHeader.substring(m, n);
						}
						m = paramHeader.indexOf("Content-Type: ", n + 1);
						if (m > n) {
							m = m + 14;
							n = m;
							while ((n < paramHeader.length())
									&& (paramHeader.charAt(n) != 13 || paramHeader
											.charAt(n + 1) != 10)) {
								n++;
							}
							if (n <= paramHeader.length()) {
								filetype = paramHeader.substring(m, n);
							}
						}
					}
					/*
					 * status: j point to the start of end flag (0D 0A 0D 0A) of
					 * current parameter's header after j + 0D 0A 0D 0A, is the
					 * start of current parameter's value (byte format) i point
					 * to the start of next boundary, that is,
					 * "(current header) 0D 0A 0D 0A (current value) 0D 0A (next boundary)"
					 * ↑ ↑ ↑ index j i the following code gets current value
					 */
					j = j + 4; // skip 0D 0A 0D 0A, point to parameter value;
					byte[] value = copybyte(body, j, i - j - 2);
					if (!isFile) {
						String tmpstr = new String(value, m_encApp);
						//m_params.put(name, tmpstr);
						put(name, tmpstr);
					} else {
						HttpFile file = new HttpFile();
						file.setFieldName(name);
						file.setFileName(filename);
						file.setFileType(filetype);
						file.setFileData(value);
						//m_params.put(name, file);
						put(name, file);
						//break;
					}
					i = i + boundLen + 2;
					index = i;
				} // end else
			} // end while
			dataIn.close();
		} else {
			String tmpQuery = new String(bound);
			tmpQuery = java.net.URLDecoder.decode(tmpQuery, m_encData);
			String[] params = tmpQuery.split("&");
			for (int j = 0; j < params.length; j++) {
				/*
				String[] nv = params[j].split("=");
				if (nv.length == 2) {
					m_params.put(nv[0].trim(), nv[1].trim());
				}
				*/
				int pos = params[j].indexOf("=");
				if (pos > 0) {
					String k = params[j].substring(0, pos);
					String v = params[j].substring(pos + 1);
					//m_params.put(k, v);
					put(k, v);
				}
			}
		}

	}

	private static boolean compareByteArray(byte[] a, byte[] b) {
		if (a.length != b.length) {
			return false;
		}
		for (int i = 0; i < a.length; i++) {
			if (a[i] != b[i]) {
				return false;
			}
		}
		return true;
	}

	private static byte[] copybyte(byte[] a, int from, int len) {
		int copylen = len;
		if ((a.length - from) < copylen) {
			copylen = a.length - from;
		}
		byte[] b = new byte[copylen];
		for (int i = 0; i < copylen; i++) {
			b[i] = a[from + i];
		}
		return b;
	}

	/**
	 * 获取HTTP请求参数
	 * 
	 * @param name
	 * @return 字符串
	 * @remark 改变获取参数的方式, 按照用户制定的习惯, 请求串中包含了一个参数, 如果在body里面又包含了这个参数,
	 *         用户的本意是body里的参数是要替换请求串中参数的.
	 */
	public String getStr(String name) {
		String s = null;
		// 先从body区域获取参数
		if (params != null) {
			Object obj = params.get(name);
			if (obj != null) {
				s = (String) obj;
			}
		}
		// 如果body区域没有参数, 再从HttpServletRequest中获取参数
		if (s == null) {
			s = request.getParameter(name);
			if (s != null) {
				try {
					s = URLDecoder.decode(s, m_encApp);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				if (Api.isUtf8(s)) {
					// s = Api.iconv(s, "utf-8", Environment.getFileEncoding());
				} else if (Api.isGBK(s)) {
					// tomcat 需要从8859_1转换, resin不需要
					// s = Api.iconv(s, "8859_1",
					// Environment.getFileEncoding());
				} else {
					// s = Api.iconv(s, "8859_1",
					// Environment.getFileEncoding());
				}
			}
		}
		// 实在没有值, 没有办法, 参数值返回空字符串
		if (s == null) {
			s = "";
		}
		return s.trim();
	}

	public int getInt(String param) {
		String s = getStr(param);
		if (s.length() == 0) {
			s = "0";
		}
		return Integer.valueOf(s);
	}

	public boolean getBool(String param) {
		String s = getStr(param);
		if (s.length() == 0) {
			s = "false";
		}
		return Boolean.valueOf(s);
	}

	public Timestamp getTimestamp(String param) {
		String s = getStr(param);
		if (s.length() == 0) {
			s = "0000-00-00 00:00:00";
		} else if (s.length() <= 10) {
			s += " 00:00:00";
		}
		return Timestamp.valueOf(s);
	}
	
	/**
	 * 从web请求参数中取值填充对象
	 * 
	 * @param <T>
	 * @param clazz
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public <T> Object valueOf(Class<T> clazz) {
		T oRet = null;
		try {
			oRet = clazz.newInstance();
			Enumeration<?> e = (Enumeration<?>)request.getParameterNames();   
		    while(e.hasMoreElements())     {   
		    	String key = (String)e.nextElement();   
		    	String value = request.getParameter(key);
		    	Api.setValue(oRet, key, value);
		    }
		    if (params.size() > 0) {
				for (String key : params.keySet()) {
					Object value = params.get(key);
					Api.setValue(oRet, key, value);
				}
			}
			Enumeration<?> er = request.getHeaderNames();
			while (er.hasMoreElements()) {
				String name = (String) er.nextElement();
				name = name.trim();
				String value = request.getHeader(name);
				if (value != null) {
					value = value.trim();
				} else {
					value = "";
				}
				name = name.replaceAll("-", "");
				Api.setValue(oRet, name, value);
			}
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}		
		return oRet;
	}

	/**
	 * @return the body
	 */
	public byte[] getBody() {
		return body;
	}

	/**
	 * @param body the body to set
	 */
	public void setBody(byte[] body) {
		this.body = body;
	}
	
	@SuppressWarnings("unchecked")
	private void put(String name, Object value) {
		if (params != null) {
			if (params.containsKey(name)) {
				// 如果包含, 转换成数组
				Object obj = params.get(name);
				if (obj == null) {
					// obj为null, 覆盖
					params.put(name, value);
				} else {
					List<Object> list = null;
					if (obj instanceof List) {
						// 如果本身就是list, 取出来
						list = (List<Object>) obj;
					} else {
						// 如果不是list, 则创建一个list, 并把当前的key-value放进去
						list = new ArrayList<Object>();
						list.add(obj);
					}
					list.add(value);
					params.put(name, list);
				}
			} else {
				// 如果不包含, 添加
				params.put(name, value);
			}
		}
	}

	public HttpServletResponse getResponse() {
		return response;
	}
	
	public HttpServletRequest getRequest() {
		return request;
	}
}