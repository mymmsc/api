/**
 * 
 */
package org.mymmsc.api.encoding.samples;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;

import org.mymmsc.api.assembly.Api;
import org.mymmsc.api.encoding.Charsets;

/**
 * @author WangFeng
 * 
 */
public class TestCharset {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		// Initalize the nsDetector() ;
		String s = Charsets.detect("中国人");
		System.out.println("CHARSET = " + s);
		String webSite = "http://sina.com.cn";
		
		URL url = new URL(webSite);
		BufferedInputStream imp = new BufferedInputStream(url.openStream());

		byte[] buf = new byte[102400];
		int len;
		if ((len = imp.read(buf, 0, buf.length)) != -1) {
			s = Api.detectCharset(buf, len);
			System.out.println("CHARSET = " + s);

		}
	}

}
