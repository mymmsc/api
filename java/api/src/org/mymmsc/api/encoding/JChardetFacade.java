package org.mymmsc.api.encoding;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;

import org.mymmsc.api.encoding.charset.nsDetector;
import org.mymmsc.api.encoding.charset.nsICharsetDetectionObserver;

public final class JChardetFacade implements nsICharsetDetectionObserver {
	private static JChardetFacade instance = null;
	private static nsDetector det;
	private byte[] buf = new byte[4096];

	private Charset codpage = null;

	private boolean m_guessing = true;

	private int amountOfVerifiers = 0;

	private JChardetFacade() {
		det = new nsDetector(0);
		det.Init(this);
		this.amountOfVerifiers = det.getProbableCharsets().length;
	}

	public static JChardetFacade getInstance() {
		if (instance == null) {
			instance = new JChardetFacade();
		}
		return instance;
	}
	
	public Charset detectCodepage(byte[] data, int len) {
		Reset();
		Charset ret = null;
		boolean isAscii = det.isAscii(data, len);
		if (!isAscii) {
			det.DoIt(data, len, false);
		} else {
			ret = Charset.forName("ASCII");
		}
		det.DataEnd();
		if (this.codpage == null) {
			if (this.m_guessing) {
				ret = guess();
			} else {
				ret = UnknownCharset.getInstance();
			}
		} else {
			ret = this.codpage;
		}
		return ret;
	}
	
	public synchronized Charset detectCodepage(InputStream in, int length)
			throws IOException {
		Reset();

		int read = 0;
		boolean done = false;
		@SuppressWarnings("unused")
		boolean isAscii = true;
		Charset ret = null;
		int len;
		do {
			len = in.read(this.buf, 0, Math.min(this.buf.length, length - read));
			if (len > 0) {
				read += len;
			}
			if (!done) {
				done = det.DoIt(this.buf, len, false);
			}
		} while ((len > 0) && (!done));
		det.DataEnd();
		if (this.codpage == null) {
			if (this.m_guessing)
				ret = guess();
			else
				ret = UnknownCharset.getInstance();
		} else {
			ret = this.codpage;
		}
		return ret;
	}

	private Charset guess() {
		Charset ret = null;
		String[] possibilities = det.getProbableCharsets();

		if (possibilities.length == this.amountOfVerifiers) {
			ret = Charset.forName("US-ASCII");
		} else {
			String check = possibilities[0];
			if (check.equalsIgnoreCase("nomatch")) {
				ret = UnknownCharset.getInstance();
			} else {
				for (int i = 0; (ret == null) && (i < possibilities.length); i++) {
					try {
						ret = Charset.forName(possibilities[i]);
					} catch (UnsupportedCharsetException uce) {
						ret = UnsupportedCharset.forName(possibilities[i]);
					}
				}
			}
		}
		return ret;
	}

	public void Notify(String charset) {
		this.codpage = Charset.forName(charset);
	}

	public void Reset() {
		det.Reset();
		this.codpage = null;
	}

	public boolean isGuessing() {
		return this.m_guessing;
	}

	public synchronized void setGuessing(boolean guessing) {
		this.m_guessing = guessing;
	}
}