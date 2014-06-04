/**
 * @(#)InterfaceCrypto.java	6.3.9 09/10/02
 *
 * Copyright 2000-2010 MyMMSC Software Foundation (MSF), Inc. All rights reserved.
 * MyMMSC PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.mymmsc.api.crypto;

/**
 * @author WangFeng(wangfeng@yeah.net)
 * @version 6.3.9 09/10/02
 * @since mymmsc-api 6.3.9
 */
public interface InterfaceCrypto {
	
	public boolean init(byte[] keys);

	public byte[] encode(byte[] src);

	public byte[] decode(byte[] src);
}
