/**
 * 
 */
package org.mymmsc.api.io;

import java.util.Date;

/**
 * HttpClient类post方法返回的对象
 * 
 * @author wangfeng
 * @remark 默认错误码为404, 如果接口异常, status为900, error为异常消息内容
 * 
 */
public class HttpResult {
	/** 错误码 */
	private int status = 404;
	/** 类型 */
	private String type = null;
	/** 日期, 如果是下载动作, 此处保留文件的时间 */
	private Date date = null;
	/** 错误内容 */
	private String error = null;
	/** 二进制数据 */
	private byte[] data = null;
	/** 文本内容 */
	private String body = null;
	
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	@SuppressWarnings("deprecation")
	public String date() {
		String timeStr = String.format("%d-%02d-%02d %02d:%02d:%02d",
				1900 + date.getYear(), date.getMonth() + 1, date.getDate(),
				date.getHours(), date.getMinutes(), date.getSeconds());
		return timeStr;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	/**
	 * @return the data
	 */
	public byte[] getData() {
		return data;
	}

	/**
	 * @param data
	 *            the data to set
	 */
	public void setData(byte[] data) {
		this.data = data;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

}
