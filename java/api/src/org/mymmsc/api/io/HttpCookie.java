/**
 * 
 */
package org.mymmsc.api.io;

import org.mymmsc.api.assembly.Api;

/**
 * @author WangFeng
 * 
 */
public class HttpCookie {
	/** 资源有效路径 */
	private String resourcePath = null;
	/** 名称 */
	private String name = null;
	/** 值 */
	private String value = "";
	/** 私有信息 */
	private String comment = null;
	/** 有效期 */
	private int expires = 0;
	/** Max-Age */
	private int maxAge = 0;
	/** 主机名 */
	private String domain = "";
	/** 路径 */
	private String path = "";
	/** 安全链接HTTPS */
	private boolean secure = false;
	/** 版本号 */
	private String version = null;

	public static HttpCookie parse(String s) {
		HttpCookie cookie = null;
		String[] params = s.split(";");
		int pos = -1;
		String key = null;
		String value = null;
		boolean bFirst = true;
		for (String kv : params) {
			pos = kv.indexOf('=');
			if (pos < 0) {
				key = kv.trim();
				value = "";
			} else {
				key = kv.substring(0, pos).trim();
				value = kv.substring(pos + 1).trim();
			}
			if (cookie == null) {
				cookie = new HttpCookie();
			}
			if (bFirst) {
				Api.setValue(cookie, "name", key);
				Api.setValue(cookie, "value", value);
				bFirst = false;
			} else {
				if (key.equalsIgnoreCase("max-age")) {
					key = "maxAge";
				}
				if (key.equalsIgnoreCase("secure")) {
					value = "true";
				}
				Api.setValue(cookie, key, value);
			}
		}
		return cookie;
	}

	/**
	 * 修复cookie有效路径
	 * 
	 * @param url
	 */
	public void fixPath(String url) {
		String cookiePath = null;
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
		resourcePath = domain;
		if (!path.startsWith("/")) {
			resourcePath += "/";
		}
		resourcePath += path;
		if (resourcePath.equals("/")) {
			resourcePath = cookiePath;
		}
	}

	/**
	 * @return the resource
	 */
	public String getResourcePath() {
		return resourcePath;
	}

	/**
	 * @param resource
	 *            the resource to set
	 */
	public void setResourcePath(String resource) {
		this.resourcePath = resource;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String sRet = null;
		if (!Api.isEmpty(name)) {
			sRet = String.format("%s=%s", name, value);
			if (!Api.isEmpty(comment)) {
				sRet += String.format("; comment=%s", comment);
			}
			if (expires > 0) {
				sRet += String.format("; expires=%d", expires);
				if (maxAge > 0) {
					sRet += String.format("; Max-Age=%d", maxAge);
				}
			}
			if (!Api.isEmpty(path)) {
				sRet += String.format("; path=%s", path);
			}
			if (!Api.isEmpty(domain)) {
				sRet += String.format("; domain=%s", domain);
			}
			if (secure) {
				sRet += "; Secure";
			}
			if (!Api.isEmpty(version)) {
				sRet += String.format("; version=%s", version);
			}
		}
		return sRet;
	}
	
	public String toSet() {
		String sRet = null;
		if (!Api.isEmpty(name)) {
			sRet = String.format("%s=%s", name, value);
			if (!Api.isEmpty(comment)) {
				sRet += String.format("; comment=%s", comment);
			}
			if (expires > 0) {
				sRet += String.format("; expires=%d", expires);
				if (maxAge > 0) {
					sRet += String.format("; Max-Age=%d", maxAge);
				}
			}
			if (!Api.isEmpty(path)) {
				sRet += String.format("; path=%s", path);
			}
			if (!Api.isEmpty(domain)) {
				sRet += String.format("; domain=%s", domain);
			}
			if (secure) {
				sRet += "; Secure";
			}
			if (!Api.isEmpty(version)) {
				sRet += String.format("; version=%s", version);
			}
		}
		return sRet;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * @return the comment
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * @param comment
	 *            the comment to set
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}

	/**
	 * @return the expires
	 */
	public int getExpires() {
		return expires;
	}

	/**
	 * @param expires
	 *            the expires to set
	 */
	public void setExpires(int expires) {
		this.expires = expires;
	}

	/**
	 * @return the maxAge
	 */
	public int getMaxAge() {
		return maxAge;
	}

	/**
	 * @param maxAge
	 *            the maxAge to set
	 */
	public void setMaxAge(int maxAge) {
		this.maxAge = maxAge;
	}

	/**
	 * @return the domain
	 */
	public String getDomain() {
		return domain;
	}

	/**
	 * @param domain
	 *            the domain to set
	 */
	public void setDomain(String domain) {
		this.domain = domain;
	}

	/**
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * @param path
	 *            the path to set
	 */
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * @return the secure
	 */
	public boolean isSecure() {
		return secure;
	}

	/**
	 * @param secure
	 *            the secure to set
	 */
	public void setSecure(boolean secure) {
		this.secure = secure;
	}

	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * @param version
	 *            the version to set
	 */
	public void setVersion(String version) {
		this.version = version;
	}

}
