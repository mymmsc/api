/**
 * @(#)Communication.java	6.3.9 09/10/02
 *
 * Copyright 2000-2010 MyMMSC Software Foundation (MSF), Inc. All rights reserved.
 * MyMMSC PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.mymmsc.w3c.gsm;

/**
 * 消息容器接口
 * 
 * @author WangFeng(wangfeng@yeah.net)
 * @version 6.3.9 09/10/02
 * @since mymmsc-gsm 6.3.9
 */
public interface InterfaceBox {
	/**
	 * 关闭容器
	 */
	public void close();

	/**
	 * 添加一条消息
	 * 
	 * @param mail
	 * @return <tt>true</tt>
	 */
	// public boolean add(PduContext mail);

	/**
	 * 取得一个特定的消息
	 * 
	 * @param index
	 * @return PduContext
	 */
	// public PduContext get(int index);

	/**
	 * 删除一条消息
	 * 
	 * @param mail
	 * @return <tt>true</tt>
	 */
	// public boolean remove(PduContext mail);
}
