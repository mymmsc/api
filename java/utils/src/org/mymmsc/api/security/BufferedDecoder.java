// Decompiled by Jad v1.5.7g. Copyright 2000 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/SiliconValley/Bridge/8617/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi
// Source File Name:   BufferedDecoder.java

package org.mymmsc.api.security;

// Referenced classes of package org.bouncycastle.util.encoders:
//            Translator

public class BufferedDecoder {

	protected byte buf[];
	protected int bufOff;
	protected Translator translator;

	public BufferedDecoder(Translator translator, int bufSize) {
		this.translator = translator;
		if (bufSize % translator.getEncodedBlockSize() != 0) {
			throw new IllegalArgumentException(
					"buffer size not multiple of input block size");
		} else {
			buf = new byte[bufSize];
			bufOff = 0;
			return;
		}
	}

	public int processByte(byte in, byte out[], int outOff) {
		int resultLen = 0;
		buf[bufOff++] = in;
		if (bufOff == buf.length) {
			resultLen = translator.decode(buf, 0, buf.length, out, outOff);
			bufOff = 0;
		}
		return resultLen;
	}

	public int processBytes(byte in[], int inOff, int len, byte out[],
			int outOff) {
		if (len < 0) {
			throw new IllegalArgumentException(
					"Can't have a negative input length!");
		}
		int resultLen = 0;
		int gapLen = buf.length - bufOff;
		if (len > gapLen) {
			System.arraycopy(in, inOff, buf, bufOff, gapLen);
			resultLen += translator.decode(buf, 0, buf.length, out, outOff);
			bufOff = 0;
			len -= gapLen;
			inOff += gapLen;
			outOff += resultLen;
			int chunkSize = len - len % buf.length;
			resultLen += translator.decode(in, inOff, chunkSize, out, outOff);
			len -= chunkSize;
			inOff += chunkSize;
		}
		if (len != 0) {
			System.arraycopy(in, inOff, buf, bufOff, len);
			bufOff += len;
		}
		return resultLen;
	}
}
