/*
 * @(#)AbstractCrypto.java	6.3.9 09/09/13
 *
 * Copyright 2009 MyMMSC Software Foundation (MSF), Inc. All rights reserved.
 */
package org.mymmsc.api.crypto;

import java.security.Security;

import javax.crypto.SecretKey;

import org.mymmsc.api.adapter.AutoObject;

/**
 * @author WangFeng(wangfeng@yeah.net)
 * @version 6.3.9 09/10/02
 * @since mymmsc-api 6.3.9
 */
public abstract class AbstractCrypto extends AutoObject{
	protected boolean bIsBase64 = true;
	protected String sAlgorithm = null;
	protected SecretKey deskey = null;
	protected byte[] key = null;
	private boolean bInited = false;

	/**
	 * 构造
	 */
	public AbstractCrypto() {
		if (!bInited) {
			Security.addProvider(new com.sun.crypto.provider.SunJCE());
			bInited = true;
		}
	}
}
