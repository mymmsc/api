package org.mymmsc.api.encoding;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String s = Charsets.detect("中国人");
		System.out.println("CHARSET = " + s);
	}

}
