/**
 * @(#)HttpCookie.java	6.3.9 09/10/02
 *
 * Copyright 2000-2010 MyMMSC Software Foundation (MSF), Inc. All rights reserved.
 * MyMMSC PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.mymmsc.j2ee.http;

import java.io.UnsupportedEncodingException;

import javax.servlet.http.Cookie;

import org.mymmsc.api.assembly.Api;

/**
 * Cookie集合封装类
 * 
 * @author WangFeng(wangfeng@yeah.net)
 * @version 6.3.9 09/10/02
 * @since mymmsc-j2ee 6.3.9
 */
public class HttpCookie extends HttpElement {
	private Cookie[] m_cookies = null;
	private int m_timeout = 0;

	/**
	 * HttpCookie构造函数
	 */
	public HttpCookie(int timeout) {
		super();
		m_timeout = timeout;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mymmsc.j2ee.http.InterfaceHttpObject#get(java.lang.String,
	 * java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	public <T> T get(String name, T defaultValue) {
		T value = defaultValue;
		if (m_cookies == null) {
			m_cookies = m_request.getCookies();
		}
		if (m_cookies != null) {
			for (Cookie cookie : m_cookies) {
				if (cookie.getName().equals(name)) {
					value = (T) Api.valueOf(defaultValue.getClass(), cookie
							.getValue());
					break;
				}
			}
		}
		
		return value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mymmsc.j2ee.http.InterfaceHttpObject#set(java.lang.String,
	 * java.lang.Object)
	 */
	public <T> boolean set(String name, T value) {
		String tmpValue = (String) value;
		return set(null, name, tmpValue);
	}

	public boolean set(String domain, String name, String value) {
		boolean bRet = false;
		String tmpValue = (String) value;
		try {
			tmpValue = java.net.URLEncoder.encode(tmpValue, m_response.getCharacterEncoding());
			Cookie c = new Cookie(name, tmpValue);
			if (!Api.isEmpty(domain)) {
				c.setDomain(domain);
			}
			c.setMaxAge(m_timeout);
			c.setPath("/");
			m_response.addCookie(c);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return bRet;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mymmsc.j2ee.http.InterfaceHttpObject#remove(java.lang.String)
	 */
	public boolean remove(String name) {
		Cookie c = new Cookie(name, null);
		c.setMaxAge(0);
		c.setPath("/");
		m_response.addCookie(c);
		return true;
	}

}
