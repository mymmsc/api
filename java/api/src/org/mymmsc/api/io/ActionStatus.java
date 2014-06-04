/**
 * 
 */
package org.mymmsc.api.io;

import org.mymmsc.api.assembly.Api;

/**
 * Action响应状态bean
 * 
 * @author wangfeng
 * @version 3.0.1 2012/05/27
 * @remark 所有接口的响应都会继承这个类
 */
public class ActionStatus {
	/** 状态码 */
	private int status;
	/** 状态描述 */
	private String message;
	/** 时间戳 */
	private String timestamp;
	/** 主机信息 */
	private String host;

	public ActionStatus() {
		status = 900;
		message = "Unknown error.";
		timestamp = Api.toString(new java.util.Date(), "yyyy-MM-dd HH:mm:ss.SSS");
		host = Api.getLocalIp();
	}

	/**
	 * @return the status
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(int status) {
		this.status = status;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message
	 *            the message to set
	 */
	public void setMessage(String message) {
		//this.message = Api.getLocalIp() + ": " + message;
		this.message = message;
	}

	/**
	 * 设定操作状态信息
	 * 
	 * @param status
	 *            状态码
	 * @param message
	 *            状态描述
	 */
	public void set(int status, String message) {
		setStatus(status);
		setMessage(message);
	}

	/**
	 * @return the timestamp
	 */
	public String getTimestamp() {
		return timestamp;
	}

	/**
	 * @param timestamp
	 *            the timestamp to set
	 */
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * @return the host
	 */
	public String getHost() {
		return host;
	}

	/**
	 * @param host the host to set
	 */
	public void setHost(String host) {
		this.host = host;
	}
}
