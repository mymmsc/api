/**
 * 
 */
package org.mymmsc.j2ee.struts;

/**
 * HttpAction返回bean
 * 
 * @author WangFeng
 * @remark 复杂的ACTION处理结果的返回bean
 */
public class ActionResult {
	/** 状态串 */
	private String status;
	/** 数据区 */
	private byte[] data;

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
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
}
