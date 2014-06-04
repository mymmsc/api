package org.mymmsc.j2ee.struts;

public interface IAction {

	/**
	 * HTTP 响应
	 * 
	 * @param object
	 *            HTTP请求的Bean
	 * @return byte[] 输出字节数组
	 * @remark 不允许抛出异常, 所有异常必须自己解决
	 */
	public abstract byte[] execute();

}