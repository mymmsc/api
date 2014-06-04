// Decompiled by Jad v1.5.7g. Copyright 2000 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/SiliconValley/Bridge/8617/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi
// Source File Name:   HexTranslator.java

package org.mymmsc.api.security;

// Referenced classes of package org.bouncycastle.util.encoders:
//            Translator

public class HexTranslator implements Translator {

	private static final byte hexTable[] = { 48, 49, 50, 51, 52, 53, 54, 55,
			56, 57, 97, 98, 99, 100, 101, 102 };

	public HexTranslator() {
	}

	public int getEncodedBlockSize() {
		return 2;
	}

	public int encode(byte in[], int inOff, int length, byte out[], int outOff) {
		int i = 0;
		for (int j = 0; i < length; j += 2) {
			out[outOff + j] = hexTable[in[inOff] >> 4 & 0xf];
			out[outOff + j + 1] = hexTable[in[inOff] & 0xf];
			inOff++;
			i++;
		}

		return length * 2;
	}

	public int getDecodedBlockSize() {
		return 1;
	}

	public int decode(byte in[], int inOff, int length, byte out[], int outOff) {
		int halfLength = length / 2;
		for (int i = 0; i < halfLength; i++) {
			byte left = in[inOff + i * 2];
			byte right = in[inOff + i * 2 + 1];
			if (left < 97) {
				out[outOff] = (byte) (left - 48 << 4);
			} else {
				out[outOff] = (byte) ((left - 97) + 10 << 4);
			}
			if (right < 97) {
				out[outOff] += (byte) (right - 48);
			} else {
				out[outOff] += (byte) ((right - 97) + 10);
			}
			outOff++;
		}

		return halfLength;
	}

}
