package org.mymmsc.api.category;

/**
 * 提供非标准base64的url编码, 解码功能函数
 * 
 * @author wangfeng
 * @version 3.0.2 2012/05/27
 * @since 1.0.0
 * @remark ying test
 */
public class PseudoBase64 {

	// Base64编码字符表
	static char BASE64CHAR[] = "TKajD7AZcF2snPr5EwiHNRygmupU0IXx96BWb-hMCGJo_V8fkQz1YdvL3OletqS4"
			.toCharArray();// 这个数组用来在编码中根据前6六位的数值来选择相应的字符
	static byte LOW[] = { 0x0, 0x1, 0x3, 0x7, 0xF, 0x1F, 0x3F };
	static char BASE64VAL[] = {// 这个数组中的下标值表示ASCII码中字符的值，而数组对应下标的值是字符在BASE64CHAR中的下标
	(char) -1, (char) -1, (char) -1, (char) -1, (char) -1, (char) -1,
			(char) -1, (char) -1, (char) -1, (char) -1, (char) -1, (char) -1,
			(char) -1, (char) -1, (char) -1, (char) -1, (char) -1, (char) -1,
			(char) -1, (char) -1, (char) -1, (char) -1, (char) -1, (char) -1,
			(char) -1, (char) -1, (char) -1, (char) -1, (char) -1, (char) -1,
			(char) -1, (char) -1, (char) -1, (char) -1, (char) -1, (char) -1,
			(char) -1, (char) -1, (char) -1, (char) -1, (char) -1, (char) -1,
			(char) -1, (char) -1, (char) -1, 37, (char) -1, (char) -1, 28, 51,
			10, 56, 63, 15, 33, 5, 46, 32, (char) -1, (char) -1, (char) -1,
			(char) -1, (char) -1, (char) -1, (char) -1, 6, 34, 40, 4, 16, 9,
			41, 19, 29, 42, 1, 55, 39, 20, 57, 13, 49, 21, 62, 0, 27, 45, 35,
			30, 52, 7, (char) -1, (char) -1, (char) -1, (char) -1, 44,
			(char) -1, 2, 36, 8, 53, 59, 47, 23, 38, 18, 3, 48, 58, 24, 12, 43,
			26, 61, 14, 11, 60, 25, 54, 17, 31, 22, 50, (char) -1, (char) -1,
			(char) -1, (char) -1, (char) -1 };

	/**
	 * 编码函数
	 * 
	 * @author ying
	 * @since 1.0.0
	 * @param in
	 *            待编码字符串
	 * @return String 编码后字符串
	 */
	public static String encode(String in) {
		if (in == null || in.length() < 1) {
			return null;
		}
		byte[] inByte = in.getBytes();// 源字节数组
		int inlen = in.getBytes().length; // 源字节数组长度
		int outlen = 0; // 输出字节数组长度
		if (inlen % 3 > 0) {
			outlen = inlen * 8 / 6 + 1;// ?
		} else {
			outlen = inlen * 8 / 6 + 0;
		}
		char[] out = new char[outlen];

		int l = outlen;
		byte b = 0;// base64char里面的下标
		int n = 0;// 值是从6开始用
		int p = 0;// 源byte数组当前下标

		while (l > 0) {
			b = 0;
			if (n > 0) {
				if (inByte[p] < 0) {
					b |= ((inByte[p] + 256) & LOW[n]) << (6 - n);
					p++;
				} else {
					b |= (inByte[p] & LOW[n]) << (6 - n);
					p++;
				}
			}
			n = 6 - n;
			if (n > 0 && inlen > 0) {
				if (p < inlen) {
					if (inByte[p] < 0) {
						b |= (inByte[p] + 256) >> (8 - n);
						n = 8 - n;
					} else {
						b |= inByte[p] >> (8 - n);// 右移
						n = 8 - n;
					}
				} else if (p == inlen) {
					b |= 0 >> (8 - n);
					n = 8 - n;
				}
			}
			out[outlen - l] = BASE64CHAR[b];
			l--;
		}
		return new String(out);
	}

	/**
	 * 解码函数
	 * 
	 * @author ying
	 * @since 1.0.0
	 * @param in
	 *            待解码字符串
	 * @return String 解码后字符串
	 */
	public static String decode(String in) {
		if (in == null) {
			return null;
		}
		in = in.trim();
		int inlen = in.getBytes().length;
		int outlen = 0;

		if (inlen < 1 || (inlen * 6) % 8 >= 6) {
			return null;
		}

		byte[] inByte = in.getBytes();
		for (int i = 0; i < inlen; i++) {
			if (inByte[i] > 127 || BASE64VAL[inByte[i]] == (char) -1) {
				return null;
			}
		}

		outlen = inlen * 6 / 8;
		char[] out = new char[outlen];
		byte[] outbyte = new byte[outlen];

		int l = outlen;
		int n = 0;
		int p = 0;

		while (l > 0) {
			if (n > 0) {
				out[outlen - l] |= (BASE64VAL[in.charAt(p)] & LOW[n]) << (8 - n);
				p++;
			}
			n = 8 - n;
			if (n >= 6) {
				out[outlen - l] |= BASE64VAL[in.charAt(p)] << (n - 6);
				n -= 6;
				p++;
			}
			if (n > 0) {
				out[outlen - l] |= BASE64VAL[in.charAt(p)] >> (6 - n);
				n = 6 - n;
			}
			l--;
		}
		for (int i = 0; i < outlen; i++) {
			if (out[i] > 127) {
				outbyte[i] = (byte) (out[i] - 256);
			} else {
				outbyte[i] = (byte) out[i];
			}
		}
		return new String(outbyte);

	}
}
