/**
 * @(#)ActionFilter.java	6.3.9 09/10/02
 *
 * Copyright 2000-2010 MyMMSC Software Foundation (MSF), Inc. All rights reserved.
 * MyMMSC PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.mymmsc.j2ee.struts;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.xpath.XPathExpressionException;

import org.mymmsc.api.Environment;
import org.mymmsc.api.adapter.AutoObject;
import org.mymmsc.api.assembly.Api;
import org.mymmsc.api.assembly.XmlParser;
import org.mymmsc.api.category.Encoding;
import org.mymmsc.api.context.JsonAdapter;
import org.mymmsc.api.context.Templator;
import org.mymmsc.api.context.VariableNotDefinedException;
import org.mymmsc.api.io.ActionStatus;
import org.mymmsc.j2ee.http.Category;
import org.mymmsc.j2ee.http.HttpCookie;
import org.mymmsc.j2ee.http.HttpParameter;
import org.mymmsc.j2ee.http.RedisApi;
import org.w3c.dom.NodeList;

/**
 * FastAction过滤器
 * 
 * @author WangFeng(wangfeng@yeah.net)
 * @version 6.3.13 13/11/06
 * @remark 替代Struts产品
 * @since 6.3.13 去掉web.xml的配置信息, 应用接口免除strust.xml的配置
 * @since mymmsc-struts 6.3.9
 */
@WebFilter(filterName = "MyMMSC-Filter", urlPatterns = { "*.cgi", "*.action" })
public class ActionFilter extends AutoObject implements Filter {
	/** 是否已经初始化 */
	private static boolean isInit = false;
	private Map<String, Class<?>> map = null;
	private ServletContext context = null;
	private static String project = null;
	private static String webPath = null;
	private static XmlParser xp = null;
	private static RedisApi redisApi = null;
	private String expire = null;
	private int iFlag = 0;
	
	public ActionFilter() {
		super();
	}

	/**
	 * 修复URL请求路径
	 * 
	 * @param uri
	 * @return
	 */
	private String fixUri(String uri) {
		StringBuffer sb = new StringBuffer();
		String[] as = uri.split("[/]+");
		if (as.length > 2) {
			for (int i = 2; i < as.length; i++) {
				if (sb.length() > 0) {
					sb.append("/");
				}
				sb.append(as[i]);
			}
		} else if (as.length == 1) {
			sb.append(as[0]);
		} else {
			sb.append("/");
		}

		return sb.toString();
	}

	private String actionForInfo(String request) {
		String clazz = null;
		String uri = fixUri(request);
		String type = uri.endsWith("." + APS.ACTION_EXT) ? APS.ACTION_EXT
				: APS.CGI_EXT;
		if (type != null) {
			int iEnd = uri.lastIndexOf("." + type);
			if (iEnd > 0) {
				clazz = uri.substring(0, iEnd);
			}
		}
		if (clazz != null) {
			clazz = clazz.trim();
		}
		return clazz;
	}

	private String actionForInfo(HttpServletRequest request) {
		String requestUri = request.getRequestURI();
		return actionForInfo(requestUri);
	}
	
	private Class<?> classForAction(String action) {
		Class<?> clazz = null;
		if (map != null) {
			clazz = map.get(action);
		}
		return clazz;
	}

	private void doService(ServletRequest req, ServletResponse resp, FilterChain chain,
			Class<?> clazz) throws IOException, SecurityException,
			NoSuchMethodException, IllegalArgumentException,
			IllegalAccessException, InvocationTargetException,
			XPathExpressionException, ClassNotFoundException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
		HttpParameter parameter = new HttpParameter(Encoding.Default,
				Encoding.Default);
		parameter.init(request, response);
		HttpAction action = (HttpAction) parameter.valueOf(clazz);
		action.setClientIp(request.getRemoteAddr());
		action.setClientPort(request.getRemotePort());
		action.setParameter(parameter);
		action.setBody(parameter.getBody());
		HttpCookie cookie = new HttpCookie(Category.SESSION_TIMEOUT);
		cookie.init(request, response);
		action.setHttpCookie(cookie);
		action.setProject(project);
		action.setWebPath(webPath);
		action.setQuery(request.getQueryString());
		action.setRequest(request.getRequestURI());
		// Action传入HttpServlet对象, 以备特殊需求调用
		action.setServlet(request, response);
		action.setRedis(redisApi);
		action.setContext(context);

		byte[] data = null;
		try {
			Method exec = clazz.getMethod("execute", new Class[] {});
			data = (byte[]) exec.invoke(action);
		} catch (Exception e) {
			error("", e);
		}
		// 重置 字符集
		response.setCharacterEncoding(action.getCharset());
		// 判断是否抓发
		if (action.httpStatus.equalsIgnoreCase(APS.SC_FORWARD)) {
			// 转发, 这里不做任务业务逻辑的处理
			String url = action.getRequest();
			String actionName = actionForInfo(url);
			if (actionName != null) {
				clazz = classForAction(actionName);
				if (clazz == null) {
					response.sendError(404);
					//info("Not found " + actionName);
				} else {
					doService(request, response, chain, clazz);
				}
			} else if (Api.isFile(webPath + url)) {
				request.getRequestDispatcher(url).forward(req, resp);
			} else {
				chain.doFilter(req, resp);
			}
		} else if (action.httpStatus.equalsIgnoreCase(APS.SC_REDIRECT)) {
			// 旧版业务重定向, 需要做重定向
			if (data != null && data.length > 0) {
				response.sendRedirect(new String(data, action.getCharset()));
			} else {
				// 业务action已经重定向, 这里不做任务业务逻辑的处理
			}
		} else if (data == null || data.length == 0) {
			response.sendError(500);
		} else {
			// 更新HTTP-Response-Header
			Map<String, String> tmpMap = action.header();
			if (tmpMap != null) {
				for (String k : tmpMap.keySet()) {
					response.setHeader(k, tmpMap.get(k));
				}
			}
			// 设置Content-Length
			response.setContentLength(data.length);
			// PrintWriter out = response.getWriter();
			// out.println(data);
			ServletOutputStream out = response.getOutputStream();
			out.write(data);
			out.flush();
			out.close();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest,
	 * javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	public void doFilter(ServletRequest req, ServletResponse resp,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
		int errno = -1;
		int eBase = 1000;
		String message = null;
		if (iFlag < 0) {
			String tm = Api.toString(new Date(), "yyyy-MM-dd HH:mm:ss");
			if (tm.compareTo(expire) >= 0) {
				errno = eBase + 1;
				message = "应用服务授权已过期";
			} else {
				errno = 0;
			}
		} else if (iFlag == 1) {
			errno = eBase + 2;
			message = "应用服务尚未授权";
		} else if (iFlag == 2) {
			errno = eBase + 2;
			message = "应用服务授权无效";
		} else if (iFlag == 3) {
			errno = eBase + 3;
			message = "应用服务该服务器未授权";
		}
		if (errno == -1) {
			chain.doFilter(req, resp);
		} else if (errno == 0) {
			try {
				String actionName = actionForInfo(request);
				if (actionName != null) {
					Class<?> clazz = classForAction(actionName);
					if (clazz == null) {
						response.sendError(404);
						info("Not found " + actionName);
					} else {
						doService(request, response, chain, clazz);
					}
				} else {
					chain.doFilter(req, resp);
				}
			} catch (XPathExpressionException e) {
				warn(e);
			} catch (ClassNotFoundException e) {
				warn(e);
			} catch (SecurityException e) {
				warn(e);
			} catch (IllegalArgumentException e) {
				warn(e);
			} catch (NoSuchMethodException e) {
				warn(e);
			} catch (IllegalAccessException e) {
				warn(e);
			} catch (InvocationTargetException e) {
				warn(e);
			}
		} else {
			response.setCharacterEncoding("utf-8");
			//response.setContentType("text/xml; charset=utf-8");
			response.setHeader("Pragma", "no-cache");
			response.setHeader("Cache-Control", "no-cache");
			PrintWriter out = response.getWriter();
			ActionStatus as = new ActionStatus();
			as.set(errno, message);
			String sReturn = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
					+ "<license>" + "  <status>" + errno + "</status>"
					+ "  <description>" + message + "</description>"
					+ "</license>";
			sReturn = JsonAdapter.get(as, true);
			out.println(sReturn);
			out.flush();
			out.close();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	public void init(FilterConfig config) throws ServletException {
		// 已经初始化, 直接返回
		if (isInit) {
			//return;
		}
		// 读取工程路径
		//info("初始化struts5...");
		context = config.getServletContext();
		project = context.getContextPath().replaceAll("/", "");
		project = project.replaceAll("\\\\", "");
		webPath = context.getRealPath("/");
		webPath = webPath.replaceAll("\\\\", "/");
		// 日志路径为环境变量${MSF_LOGS}或者
		// 用户主目录下runtime/logs [wangfeng@2012-5-30 上午8:51:12]
		String logsPath = Environment.get("MSF_LOGS");
		if (logsPath == null || logsPath.length() < 1) {
			logsPath = Environment.get("user.home");
			if (!logsPath.endsWith("/runtime")) {
				logsPath += "/runtime";
			}
			logsPath += "/logs";
		}
		logsPath = logsPath.replaceAll("\\\\", "/");
		// 修订log4j.properties文件路径
		String filename = webPath + "WEB-INF/classes/" + Category.Log4jFilename;
		Templator tpl;
		try {
			// 刷新log4j配置文件
			tpl = new Templator(this.getClass().getResourceAsStream(
					Category.Log4jTpl));
			htmlSetVariable(tpl, "logs.root", logsPath);
			htmlSetVariable(tpl, "project", project);
			tpl.generateOutput(filename);
			// 读取路径信息
			Properties prop = new Properties();
			InputStream is = new FileInputStream(filename);
			prop.load(is);
			String logPath = prop.getProperty("log4j.appender.LOGFILE.File");
			File pfile = new File(filename);
			logPath = pfile.getPath();
			Api.mkdirs(logPath);
			Class<?> clazz = Class
					.forName("org.apache.log4j.PropertyConfigurator");
			Method m = clazz.getMethod("configure",
					new Class[] { String.class });
			m.invoke(clazz, new Object[] { filename });
		} catch (Exception e) {
			error(e);
		}
		String jarFile = webPath + "/WEB-INF/lib/mymmsc-j2ee.jar";
		if (Api.isFile(jarFile)) {
			String fileName = webPath + "/WEB-INF/license.msf";
			if (!Api.isFile(fileName)) {
				iFlag = 1;
			} else {
				Properties prop = new Properties();
				File file = new File(fileName);
				FileInputStream input = null;
				try {
					input = new FileInputStream(file);
					prop.load(input);
					String userId = prop.getProperty("userid");
					String key = prop.getProperty("key");
					if (key == null) {
						key = "xxx";
					} else {
						key = key.trim();
					}
					expire = prop.getProperty("expire");
					String tmpKey = Api.md5(userId + "mymmsc" + expire + "j2ee" + project);
					if (tmpKey.equalsIgnoreCase(key)) {
						iFlag = -1;
					} else {
						iFlag = 2;
					}
				} catch (FileNotFoundException e) {
					// e.printStackTrace();
				} catch (IOException e) {
					// e.printStackTrace();
				} finally {
					//
				}
			}
		}
		filename = webPath + "WEB-INF/api.xml";
		if (Api.isFile(filename)) {
			try {
				xp = new XmlParser(filename, true);
			} catch (Exception e) {
				error(e);
				if (xp != null) {
					xp.close();
				}
			}
		}
		//info("扫描action...");
		if (map == null) {
			map = new LinkedHashMap<String, Class<?>>();
			String packageName = webPath + "WEB-INF/classes";
			//packageName = ".";
			List<String> classNames = PackageUtil.getClassNameByFile(packageName,
					true);
			if (classNames != null) {
				for (String className : classNames) {
					//info(className);
					try {
						Class<?> clazz = Class.forName(className);
						if (IAction.class.isAssignableFrom(clazz)) { // 判断是不是一个接口
							if (!IAction.class.equals(clazz)) { // 本身不加进去
								//info("HttpAction: " + className);
								Annotation[] anns = null;
								Annotation ann = null;
								anns = clazz.getDeclaredAnnotations();
								if (anns.length > 0) {
									for (int k = 0; k < anns.length; k++) {
										ann = anns[k];
										if (ann instanceof WebAction) {
											WebAction wa = (WebAction) ann;
											if (wa != null) {
												String url = wa.url();
												if (url != null) {
													url = url.trim();
													//info("action=[" + url + "], class=[" + clazz.getName() + "]");
													map.put(url, clazz);
												}
											}
										}
									}
								}
							}
						}
					} catch (ClassNotFoundException e) {
						error("", e);
					}
				}
			}
			String struts = webPath + "WEB-INF/struts.xml";
			if (Api.isFile(struts)) {
				XmlParser tmpXp = null;
				try {
					tmpXp = new XmlParser(struts, true);
					String exp = "//package/action";
					NodeList list = tmpXp.query(exp);
					if (list.getLength() > 0) {
						for (int i = 0; i < list.getLength(); i++) {
							org.w3c.dom.Node node = list.item(i);
							String action = tmpXp.valueOf(node, "name");
							String clsName = tmpXp.valueOf(node, "class");
							if (!Api.isEmpty(action) && !Api.isEmpty(clsName)) {
								try {
									Class<?> clazz = Class.forName(clsName);
									//info("action=[" + action + "], class=[" + clsName + "]");
									map.put(action, clazz);
								} catch (Exception e) {
									error("action节点定义出错", e);
								}
							}
						}
					}
					tmpXp.close();
				} catch (Exception e) {
					error("扫描struts.xml出错", e);
					if (tmpXp != null) {
						tmpXp.close();
					}
				}
			}
		}
		//info("扫描action...结束");
		// 初始化RedisApi
		if (xp != null) {
			String redisHost = "redis.api.mymmsc.org";
			int redisPort = 6379;
			String prefix = "//api/constant[@name='%s']";
			String exp = String.format(prefix, "redis.host");
			try {
				NodeList list = xp.query(exp);
				if (list != null && list.getLength() > 0) {
					org.w3c.dom.Node node = list.item(0);
					redisHost = xp.valueOf(node, "redis.host");
				}
			} catch (XPathExpressionException e) {
				//
			}
			exp = String.format(prefix, "redis.port");
			try {
				NodeList list = xp.query(exp);
				if (list != null && list.getLength() > 0) {
					org.w3c.dom.Node node = list.item(0);
					String tmpPort = xp.valueOf(node, "redis.port");
					redisPort = Api.valueOf(int.class, tmpPort);
				}
			} catch (XPathExpressionException e) {
				//
			}
			if (redisHost != null) {
				redisApi = RedisApi.getInstance(redisHost, redisPort);
			}
		}
		// 设置已初始化标示
		isInit = true;
	}

	protected boolean htmlSetVariable(Templator templator, String name,
			String value) {
		boolean bRet = false;
		try {
			templator.setVariable(name, value);
			bRet = true;
		} catch (VariableNotDefinedException e) {
			error(e);
		}

		return bRet;
	}

	@Override
	public void close() {
		//
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.Filter#destroy()
	 */
	public void destroy() {
		if (xp != null) {
			xp.close();
		}
	}

}
