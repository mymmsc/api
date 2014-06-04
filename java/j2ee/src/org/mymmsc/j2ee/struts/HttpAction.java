/**
 * @(#)HttpAction.java	6.3.12 12/06/02
 *
 * Copyright 2000-2010 MyMMSC Software Foundation (MSF), Inc. All rights reserved.
 * MyMMSC PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.mymmsc.j2ee.struts;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.mymmsc.api.adapter.AutoObject;
import org.mymmsc.api.assembly.Api;
import org.mymmsc.api.category.Encoding;
import org.mymmsc.api.context.BlockNotDefinedException;
import org.mymmsc.api.context.TemplateSyntaxException;
import org.mymmsc.api.context.Templator;
import org.mymmsc.api.context.VariableNotDefinedException;
import org.mymmsc.j2ee.http.HttpCookie;
import org.mymmsc.j2ee.http.HttpParameter;
import org.mymmsc.j2ee.http.RedisApi;

/**
 * HttpAction 适配器
 * 
 * @author WangFeng(wangfeng@yeah.net)
 * @version 6.3.12 12/06/02
 * @see HttpServlet
 * @since mymmsc-j2ee 6.3.12
 * @since mymmsc-j2ee 6.3.9
 */
public abstract class HttpAction extends AutoObject implements IAction {
	/** 默认字符集 */
	private String charset = Encoding.Default;
	/** 状态码 */
	public String httpStatus = APS.SC_SUCCESS;
	/** HTTP协议header域 */
	private Map<String, String> _header = null;

	protected HttpServletRequest servletRequest = null;
	protected HttpServletResponse servletResponse = null;
	protected ServletContext context = null;
	protected HttpSession httpSession = null;
	protected RedisApi httpRedis = null;
	protected HttpCookie httpCookie = null;
	/** 请求参数 */
	protected HttpParameter parameter = null;
	/** 项目名称 */
	protected String project = null;
	/** 工程路径 */
	protected String webPath = null;
	/** 请求路径 */
	protected String request = null;
	/** 请求串 */
	protected String query = null;
	/** 模版 */
	private Templator m_templator = null;
	/** HTTP-Body */
	private byte[] body = null;
	/** 客户端IP */
	protected String clientIp = null;
	/** 客户端PORT */
	protected int clientPort = -1;
	/** 主机 */
	protected String host = null;

	/**
	 * 构造函数, 初始化AutoObject
	 */
	public HttpAction() {
		super();
	}

	/* (non-Javadoc)
	 * @see org.mymmsc.j2ee.struts.IAction#execute()
	 */
	public abstract byte[] execute();

	/**
	 * 重定向
	 * 
	 * @param path
	 * @throws IOException
	 */
	protected void sendRedirect(String path) {
		try {
			servletResponse.sendRedirect(path);
			httpStatus = APS.SC_REDIRECT;
		} catch (IOException e) {
			error("", e);
		}
	}

	/**
	 * 转发请求
	 * 
	 * @param path
	 * @throws ServletException
	 * @throws IOException
	 */
	protected void forward(String path) {
		request = path;
		// 设定重定向状态
		httpStatus = APS.SC_FORWARD;
	}
	
	/**
	 * 转发请求
	 * 
	 * @param path
	 * @throws ServletException
	 * @throws IOException
	 */
	protected void forward1(String path) {
		RequestDispatcher dispatcher = servletRequest
				.getRequestDispatcher(path);
		try {
			dispatcher.forward(servletRequest, servletResponse);
			// 设定重定向状态
			httpStatus = APS.SC_FORWARD;
		} catch (ServletException e) {
			error("", e);
		} catch (IOException e) {
			error("", e);
		}
	}
	
	/**
	 * 转发请求
	 * 
	 * @param path
	 * @throws ServletException
	 * @throws IOException
	 */
	protected void forward2(String path) {
		RequestDispatcher dispatcher = context.getNamedDispatcher(path);
		try {
			dispatcher.forward(servletRequest, servletResponse);
			// 设定重定向状态
			httpStatus = APS.SC_FORWARD;
		} catch (ServletException e) {
			error("", e);
		} catch (IOException e) {
			error("", e);
		}
	}

	/**
	 * 设定RedisApi实例
	 * 
	 * @param redis
	 */
	public void setRedis(RedisApi redis) {
		httpRedis = redis;
	}

	/**
	 * 设置工程路径
	 * 
	 * @param webPath
	 */
	public void setWebPath(String webPath) {
		this.webPath = webPath;
	}

	/**
	 * 设置工程名称
	 * 
	 * @param project
	 */
	public void setProject(String project) {
		this.project = project;
	}

	public String getRequest() {
		return request;
	}
	
	/**
	 * 设定请求串
	 * 
	 * @param request
	 */
	public void setRequest(String request) {
		this.request = request;
	}

	/** 设定请求参数 */
	public void setQuery(String query) {
		this.query = query;
	}

	/**
	 * HTTP 响应头
	 * 
	 * @return
	 */
	public Map<String, String> header() {
		return _header;
	}

	protected void addHeader(String name, String value) {
		if (_header == null) {
			_header = new HashMap<String, String>();
		}
		if (name != null && value != null) {
			name = name.trim();
			value = value.trim();
			if (name.length() > 0 && value.length() > 0) {
				_header.put(name, value);
			}
		}
	}

	/**
	 * @return the charset
	 */
	public String getCharset() {
		return charset;
	}

	/**
	 * @param charset
	 *            the charset to set
	 */
	public void setCharset(String charset) {
		this.charset = charset;
	}

	protected Templator getTemplate(String filename)
			throws TemplateSyntaxException, IOException {
		return getTemplate(filename, "utf-8");
	}

	/**
	 * 获得一个模板处理器
	 * 
	 * @param filename
	 * @return
	 * @throws TemplateSyntaxException
	 * @throws IOException
	 */
	protected Templator getTemplate(String filename, String charset)
			throws TemplateSyntaxException, IOException {
		m_templator = new Templator(webPath + "/WEB-INF/templates/" + filename,
				charset);
		htmlSetVariable(m_templator, "copyright", "(mymmsc.org 提供技术支持)");
		htmlSetVariable(m_templator, "project", project);
		String str = servletRequest.getServerName() + "@" + Api.getLocalIp();
		htmlSetVariable(m_templator, "dispatcher", str);
		htmlSetVariable(m_templator, "create_timestamp", Calendar.getInstance()
				.getTime().toString());
		htmlSetVariable(m_templator, "css_path", "/" + project + "/css");
		htmlSetVariable(m_templator, "js_path", "/" + project + "/js");
		htmlSetVariable(m_templator, "images_path", "/" + project + "/images");

		return m_templator;
	}

	protected void htmlAddBlock(Templator templator, String name) {
		try {
			templator.addBlock(name);
		} catch (BlockNotDefinedException e) {
			warn(e);
		}
	}

	protected boolean htmlSetVariable(Templator templator, String name,
			String value) {
		boolean bRet = false;
		try {
			templator.setVariable(name, value);
			bRet = true;
		} catch (VariableNotDefinedException e) {
			warn(e);
		}

		return bRet;
	}

	public boolean htmlSetVariable(String name, String value) {
		boolean bRet = false;
		try {
			m_templator.setVariable(name, value);
			bRet = true;
		} catch (VariableNotDefinedException e) {
			warn(e);
		}

		return bRet;
	}

	/**
	 * @param servletRequest
	 *            the servletRequest to set
	 */
	public void setServlet(HttpServletRequest servletRequest,
			HttpServletResponse servletResponse) {
		this.servletRequest = servletRequest;
		this.servletResponse = servletResponse;
		httpSession = servletRequest.getSession(true);
	}

	public HttpCookie getHttpCookie() {
		return httpCookie;
	}

	public void setHttpCookie(HttpCookie httpCookie) {
		this.httpCookie = httpCookie;
	}

	/**
	 * @return the body
	 */
	public byte[] getBody() {
		return body;
	}

	/**
	 * @param body
	 *            the body to set
	 */
	public void setBody(byte[] body) {
		this.body = body;
	}

	public HttpParameter getParameter() {
		return parameter;
	}

	public void setParameter(HttpParameter parameter) {
		this.parameter = parameter;
	}

	/**
	 * @return the clientIp
	 */
	public String getClientIp() {
		return clientIp;
	}

	/**
	 * @param clientIp
	 *            the clientIp to set
	 */
	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}

	/**
	 * @return the clientPort
	 */
	public int getClientPort() {
		return clientPort;
	}

	/**
	 * @param clientPort
	 *            the clientPort to set
	 */
	public void setClientPort(int clientPort) {
		this.clientPort = clientPort;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * 设定请求参数
	 * 
	 * @param key
	 *            关键字
	 * @param value
	 *            对象
	 * @remark 目的为转发设定参数
	 */
	protected void setAttribute(String key, Object value) {
		servletRequest.setAttribute(key, value);
	}

	/**
	 * 获取属性参数
	 * 
	 * @param key
	 * @return
	 */
	protected Object getAttribute(String key) {
		Object obj = servletRequest.getAttribute(key);
		return obj;
	}

	/**
	 * @return the context
	 */
	public ServletContext getContext() {
		return context;
	}

	/**
	 * @param context the context to set
	 */
	public void setContext(ServletContext context) {
		this.context = context;
	}
}
