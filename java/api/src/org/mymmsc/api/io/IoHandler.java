/**
 * 
 */
package org.mymmsc.api.io;

/**
 * I/O操作接口
 * @author WangFeng
 * @version 6.3.13
 * @remark 备用技术细节
 */
public abstract interface IoHandler {
	
	/**
	 * 读取
	 * @param buff 缓冲区
	 * @return 实际读取的字节数
	 */
	public abstract int read(byte[] buff);
	
	/**
	 * 写入
	 * @param buff 缓冲区
	 * @return 实际写入的字节数
	 */
	public abstract int write(byte[] buff);
}
