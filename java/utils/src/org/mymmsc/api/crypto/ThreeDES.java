/*
 * Licensed to the MyMMSC Software Foundation (MSF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The MSF licenses this file to You under the MyMMSC License, Version 6.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.mymmsc.org/licenses/LICENSE-6.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mymmsc.api.crypto;

/**
 * <p>Title: MyMMSC 安全部分</p>
 *
 * <p>Description: 公用基础类 -- 3DES 编码/解码</p>
 *
 * <p>Copyright: Copyright (c) 2000-2009 mymmsc.org</p>
 *
 * <p>Company: MyMMSC Software Foundation (MSF)</p>
 *
 * @author WangFeng(wangfeng@yeah.net)
 * @version 6.3.9
 */
import java.io.UnsupportedEncodingException;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.mymmsc.api.assembly.Api;

public class ThreeDES extends AbstractCrypto implements InterfaceCrypto {

	public ThreeDES() {
		super();
		sAlgorithm = "DESede";
	}

	/**
	 * 初始化KEY, keys长度必须是24
	 */
	public boolean init(byte[] keys) {
		key = new byte[24];
		Api.arrayCopy(key, keys);
		// 生成密钥
		deskey = new SecretKeySpec(key, sAlgorithm);
		return deskey == null ? false : true;
	}
	
	/**
	 * 初始化key
	 * @param key
	 * @return
	 */
	public boolean init(String key) {
		return init(key.getBytes());
	}
	
	/**
	 * 初始化KEY
	 * 
	 * @param key
	 * @param charset
	 * @return
	 */
	public boolean init(String key, String charset) {
		boolean bRet = false;
		try {
			bRet = init(key.getBytes(charset));
		} catch (UnsupportedEncodingException e) {
			error("", e);
		}
		return bRet;
	}
	
	@Override
	public void close() {
		//
	}

	/**
	 * encryptMode
	 * 
	 * @param keybyte 为加密密钥，长度为24字节
	 * @param src 为被加密的数据缓冲区（源）
	 * @return
	 */
	public byte[] encode(byte[] src) {
		byte[] bsRet = null;
		try {
			// 加密
			Cipher c1 = Cipher.getInstance(sAlgorithm);
			c1.init(Cipher.ENCRYPT_MODE, deskey);
			bsRet = c1.doFinal(src);
		} catch (java.security.NoSuchAlgorithmException e) {
			error("", e);
		} catch (javax.crypto.NoSuchPaddingException e) {
			error("", e);
		} catch (java.lang.Exception e) {
			error("", e);
		}
		return bsRet;
	}

	public byte[] decode(byte[] src) {
		byte[] bsRet = null;
		try {
			Cipher c1 = Cipher.getInstance(sAlgorithm);
			c1.init(Cipher.DECRYPT_MODE, deskey);
			bsRet = c1.doFinal(src);
		} catch (java.security.NoSuchAlgorithmException e) {
			error("", e);
		} catch (javax.crypto.NoSuchPaddingException e) {
			error("", e);
		} catch (java.lang.Exception e) {
			error("", e);
		}
		return bsRet;
	}

	public static void main(String[] args) {
		// 添加新安全算法,如果用JCE就要把它添加进去
		// Security.addProvider(new com.sun.crypto.provider.SunJCE());
		final byte[] keyBytes = { 0x11, 0x22, 0x4F, 0x58, (byte) 0x88, 0x10,
				0x40, 0x38, 0x28, 0x25, 0x79, 0x51, (byte) 0xCB, (byte) 0xDD,
				0x55, 0x66, 0x77, 0x29, 0x74, (byte) 0x98, 0x30, 0x40, 0x36,
				(byte) 0xE2 }; // 24字节的密钥
		String szSrc = "mmsreport_student,1544,1250075312062";
		ThreeDES td = new ThreeDES();
		td.init(keyBytes);
		System.out.println("加密前的字符串:" + szSrc);

		byte[] encoded = td.encode(szSrc.getBytes());
		System.out.println("加密后的字符串:" + new String(encoded));
		String sHexEnc = Api.MemToHex(encoded);
		System.out.println("加密后的字符串:" + sHexEnc);
		byte[] srcBytes = td.decode(encoded);
		System.out.println("解密后的字符串:" + (new String(srcBytes)));
		//sHexEnc = "872e33ae491e4c3d260002a0c993686858343dc9aa44103cf7b2d06e963abefe";
		// * 7c1e2d690c91a0e515efe01b65cc7333cb30da201aba2381
		srcBytes = td.decode(Api.HexToMem(sHexEnc));
		System.out.println("解密后的字符串:" + (new String(srcBytes)));
	}
}
