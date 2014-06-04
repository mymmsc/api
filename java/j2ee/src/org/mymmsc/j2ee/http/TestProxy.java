/**
 * 
 */
package org.mymmsc.j2ee.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebFilter(filterName = "XXX-Filter", urlPatterns = {"/*" })
public class TestProxy implements Filter {
	/** cgi 扩展名 */
	public static final String CGI_EXT = "cgi";
	/** Action 扩展名 */
	public static final String ACTION_EXT = "action";
	private String project = null;
	private String webPath = null;
	private String expire = null;
	private int iFlag = 0;

	private boolean isFile(String filename) {
		boolean bRet = false;
		if (filename != null) {
			File file = new File(filename);
			if (file != null) {
				bRet = file.isFile();
			}
		}
		return bRet;
	}

	private byte[] md5(byte[] bytes) {
		byte[] outBytes = new byte[] {};
		try {
			outBytes = MessageDigest.getInstance("MD5").digest(bytes);
		} catch (NoSuchAlgorithmException e) {
			//
		}
		return outBytes;
	}

	private String md5(String s) {
		byte[] digest = md5(s.getBytes());
		StringBuffer sb = new StringBuffer();
		for (byte aDigest : digest) {
			String tmp = Integer.toHexString(0xFF & aDigest);
			if (tmp.length() == 1) {
				sb.append("0");
			}
			sb.append(tmp);
		}
		return sb.toString().toUpperCase();
	}

	@SuppressWarnings("unused")
	private boolean isExists(String clazz) {
		boolean bRet = false;
		try {
			Class.forName(clazz);
			bRet = true;
		} catch (ClassNotFoundException e) {
			// e.printStackTrace();
		}
		return bRet;
	}

	private String getNow() {
		String sRet = null;
		String format = "yyyy-MM-dd HH:mm:ss";
		java.text.SimpleDateFormat tempFormatter = new java.text.SimpleDateFormat(
				format);
		sRet = tempFormatter.format(new Date());
		return sRet;
	}

	private String fixUri(String uri) {
		StringBuffer sb = new StringBuffer();
		String[] as = uri.split("[/]+");
		if (as.length > 2) {
			for (int i = 2; i < as.length; i++) {
				if (sb.length() > 0) {
					sb.append("/");
				}
				sb.append(as[i]);
			}
		} else if (as.length == 1) {
			sb.append(as[0]);
		} else {
			sb.append("/");
		}

		return sb.toString();
	}

	@SuppressWarnings("unused")
	private String actionForInfo(String request) {
		String action = null;
		String uri = fixUri(request);
		if (uri.endsWith("." + ACTION_EXT)) {
			int iEnd = uri.lastIndexOf("." + ACTION_EXT);
			action = uri.substring(0, iEnd);
		} else if (uri.endsWith("." + CGI_EXT)) {
			int iEnd = uri.lastIndexOf("." + CGI_EXT);
			action = uri.substring(0, iEnd);
		}
		if (action != null) {
			action = action.trim();
		}
		return action;
	}

	public void doFilter(ServletRequest req, ServletResponse resp,
			FilterChain chain) throws ServletException, IOException {
		int errno = -1;
		int eBase = 1000;
		String message = null;
		@SuppressWarnings("unused")
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;

		if (iFlag < 0) {
			String tm = getNow();
			if (tm.compareTo(expire) >= 0) {
				errno = eBase + 1;
				message = "应用服务授权已过期";
			} else {
				errno = 0;
			}
		} else if (iFlag == 1) {
			errno = eBase + 2;
			message = "应用服务尚未授权";
		} else if (iFlag == 2) {
			errno = eBase + 2;
			message = "应用服务授权无效";
		} else if (iFlag == 3) {
			errno = eBase + 3;
			message = "应用服务该服务器未授权";
		}
		if (errno == -1) {
			chain.doFilter(req, resp);
		} else if (errno == 0) {
			// goto
			chain.doFilter(req, resp);
			/*
			String requestUri = request.getRequestURI();
			System.out.println(requestUri);
			
			String action = actionForInfo(requestUri);
			if (action == null) {
				chain.doFilter(req, resp);
			} else {
				ServletContext sc = filter.getServletContext();  
                RequestDispatcher rd = sc.getRequestDispatcher('/' + encode(action) + ".cgi");
                rd.forward(req, resp);
			}
			*/
		} else {
			response.setCharacterEncoding("utf-8");
			response.setContentType("text/xml; charset=utf-8");
			response.setHeader("Pragma", "no-cache");
			response.setHeader("Cache-Control", "no-cache");
			PrintWriter out = response.getWriter();
			String sReturn = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
					+ "<license>" + "  <status>" + errno + "</status>"
					+ "  <description>" + message + "</description>"
					+ "</license>";
			out.println(sReturn);
			out.flush();
			out.close();
		}
	}

	public void init(FilterConfig config) throws ServletException {
		project = config.getServletContext().getContextPath().replaceAll("/", "");
		project = project.replaceAll("\\\\", "");
		webPath = config.getServletContext().getRealPath("/WEB-INF")
				.replace("\\", "/");
		String jarFile = webPath + "/lib/mymmsc-j2ee.jar";
		if (isFile(jarFile)) {
			String fileName = webPath + "/license.msf";
			if (!isFile(fileName)) {
				iFlag = 1;
			} else {
				Properties prop = new Properties();
				File file = new File(fileName);
				FileInputStream input = null;
				try {
					input = new FileInputStream(file);
					prop.load(input);
					String userId = prop.getProperty("userid");
					String key = prop.getProperty("key");
					if (key == null) {
						key = "xxx";
					} else {
						key = key.trim();
					}
					expire = prop.getProperty("expire");
					String tmpKey = md5(userId + "mymmsc" + expire + "j2ee" + project);
					if (tmpKey.equalsIgnoreCase(key)) {
						iFlag = -1;
					} else {
						iFlag = 2;
					}
				} catch (FileNotFoundException e) {
					// e.printStackTrace();
				} catch (IOException e) {
					// e.printStackTrace();
				} finally {
					//
				}
			}
		}
	}

	public void destroy() {
		//
	}

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

	private static String bytes2mac(byte[] bytes) {
		// MAC 地址应为6 字节
		if (bytes == null || bytes.length != 6) {
			return null;
		}
		StringBuffer macString = new StringBuffer();
		byte currentByte;
		for (int i = 0; i < bytes.length; i++) {
			// 与11110000 作按位与运算以便读取当前字节高4 位
			currentByte = (byte) ((bytes[i] & 240) >> 4);
			macString.append(Integer.toHexString(currentByte));
			// 与00001111 作按位与运算以便读取当前字节低4 位
			currentByte = (byte) ((bytes[i] & 15));
			macString.append(Integer.toHexString(currentByte));
			// 追加字节分隔符“:”
			macString.append(":");
		}
		// 删除多加的一个“:”
		macString.delete(macString.length() - 1, macString.length());
		// 统一转换成大写形式后返回
		return macString.toString().toUpperCase();
	}

	@SuppressWarnings("unused")
	private String getLocalMac() {
		String sRet = "";
		String lip = getLocalIp();
		try {
			boolean found = false;
			String macAddr = null;
			// 枚举所有网络接口设备
			Enumeration<?> interfaces = NetworkInterface.getNetworkInterfaces();
			// 循环处理每一个网络接口设备
			while (interfaces.hasMoreElements()) {
				NetworkInterface face = (NetworkInterface) interfaces
						.nextElement();
				// 环回设备（lo）设备不处理
				if (face.isUp() && !face.getName().equals("lo")) {
					// 获取硬件地址
					byte[] mac = face.getHardwareAddress();
					if (mac != null && mac.length == 6) {
						macAddr = bytes2mac(mac);
						Enumeration<InetAddress> ips = face.getInetAddresses();
						while (ips.hasMoreElements()) {
							InetAddress id = ips.nextElement();
							if (id.getHostAddress().equalsIgnoreCase(lip)) {
								found = true;
								sRet = macAddr;
								break;
							}
						}
						if (found) {
							break;
						}
					}
				}
			}
		} catch (SocketException se) {
			// System.err.println("错误：" + se.getMessage());
		}
		return sRet;
	}

	private String getLocalIp() {
		String ip = "XXX";
		try {
			InetAddress addr = InetAddress.getLocalHost();
			ip = addr.getHostAddress();
		} catch (UnknownHostException e) {
			//
		}
		return ip;
	}
}
