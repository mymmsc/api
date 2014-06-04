package org.mymmsc.api.security;

// Referenced classes of package org.bouncycastle.util.encoders:
//            HexTranslator

public class Hex {

	private static HexTranslator encoder = new HexTranslator();

	public Hex() {
	}

	public static byte[] encode(byte array[]) {
		return encode(array, 0, array.length);
	}

	public static byte[] encode(byte array[], int off, int length) {
		byte enc[] = new byte[length * 2];
		encoder.encode(array, off, length, enc, 0);
		return enc;
	}

	public static byte[] decode(String string) {
		byte bytes[] = new byte[string.length() / 2];
		String buf = string.toLowerCase();
		for (int i = 0; i < buf.length(); i += 2) {
			char left = buf.charAt(i);
			char right = buf.charAt(i + 1);
			int index = i / 2;
			if (left < 'a') {
				bytes[index] = (byte) (left - 48 << 4);
			} else {
				bytes[index] = (byte) ((left - 97) + 10 << 4);
			}
			if (right < 'a') {
				bytes[index] += (byte) (right - 48);
			} else {
				bytes[index] += (byte) ((right - 97) + 10);
			}
		}

		return bytes;
	}

	public static byte[] decode(byte array[]) {
		byte bytes[] = new byte[array.length / 2];
		encoder.decode(array, 0, array.length, bytes, 0);
		return bytes;
	}

}
