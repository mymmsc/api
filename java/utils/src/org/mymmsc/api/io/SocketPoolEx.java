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
package org.mymmsc.api.io;

/**
 * <p>Title: MyMMSC SocketPool</p>
 *
 * <p>Description: SocketPool of MyMMSC</p>
 *
 * <p>Copyright: Copyright (c) 2000-2009 mymmsc.org</p>
 *
 * <p>Company: MyMMSC Software Foundation (MSF)</p>
 *
 * @author WangFeng
 * @version 6.3.9
 */
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;


public class SocketPoolEx {
	// 错误码常量
	private static final int ERROR_CODE_CONNECT_FAIL = 107;
	@SuppressWarnings("unused")
	private static final int ERROR_CODE_LOGIN_FAIL = 108;
	@SuppressWarnings("unused")
	private static final int ERROR_CODE_GET_RESP_FAIL = 109;
	@SuppressWarnings("unused")
	private static final int ERROR_CODE_QUEUE_FULL = 111;
	@SuppressWarnings("unused")
	private static final int ERROR_CODE_EXCEED_LIMIT = 112;
	private static String m_sHostAddr = null;
	@SuppressWarnings("unused")
	private static int m_nHostPort = -1;
	private static DataInputStream DataRec;
	private static DataOutputStream DataSend;
	@SuppressWarnings("unused")
	private int nErrorCode;
	private static SocketInfo m_pSocketInfo;
	public static SocketPool m_pSocketPool = new SocketPool();
	public static SocketPool m_pSocketPoolMO = new SocketPool();
	private static boolean bOutPutException;

	/**
	 * SocketPoolEx
	 */
	public SocketPoolEx() {
		m_pSocketInfo = null;
		bOutPutException = false;
		m_pSocketInfo = new SocketInfo();
	}

	/**
	 * SocketPoolEx
	 * 
	 * @param flag
	 *            boolean
	 */
	public SocketPoolEx(boolean flag) {
		m_pSocketInfo = null;
		bOutPutException = flag;
		m_pSocketInfo = new SocketInfo();
	}

	/**
	 * Init
	 * 
	 * @param sHostIP
	 *            String 远程主机IP
	 * @param nPort
	 *            int 远程主机端口
	 * @param nCachedSocketCount
	 *            int 初始化套接字总数
	 * @param nRequestTimeout
	 *            int 请求超时时间
	 */
	@SuppressWarnings("static-access")
	public void Init(String sHostIP, int nPort, int nCachedSocketCount,
			int nRequestTimeout) {
		this.m_sHostAddr = sHostIP;
		this.m_nHostPort = nPort;
		m_pSocketPool.InitSocketPool(sHostIP, nPort, nCachedSocketCount,
				nRequestTimeout);
		m_pSocketPool.SetOption(bOutPutException);
	}

	/**
	 * Connect
	 * 
	 * @param nRequestTimeout
	 *            int 超时时间,单位为秒
	 * @return int
	 */
	public int Connect(int nRequestTimeout) {
		int MyPool[] = new int[1];
		if (m_pSocketPool.PopUp(m_pSocketInfo, MyPool, nRequestTimeout, 0) != 0) {
			nErrorCode = ERROR_CODE_CONNECT_FAIL;
			return 1;
		}
		try {
			if (DataRec != null) {
				DataRec = null;
			}
			DataRec = new DataInputStream(m_pSocketInfo.pSocket
					.getInputStream());
			if (DataSend != null) {
				DataSend = null;
			}
			DataSend = new DataOutputStream(m_pSocketInfo.pSocket
					.getOutputStream());
		} catch (IOException ioexception) {
			nErrorCode = ERROR_CODE_CONNECT_FAIL;
			if (bOutPutException) {
				System.err.println("Failed I/O: " + ioexception);
			}
			DestroyConnection();
			return 1;
		}
		return 0;
	}

	/**
	 * CloseConnection
	 * 
	 * @return int
	 */
	public int CloseConnection() {
		if (m_pSocketInfo.pSocket != null) {
			m_pSocketPool.PullIn(m_pSocketInfo);
		}
		return 0;
	}

	/**
	 * DestroyConnection
	 * 
	 * @return int
	 */
	public int DestroyConnection() {
		try {
			if (DataRec != null) {
				DataRec.close();
			}
			if (DataSend != null) {
				DataSend.close();
			}
			if (m_pSocketInfo.pSocket != null) {
				m_pSocketPool.Close(m_pSocketInfo);
			}
		} catch (IOException ioexception) {
			if (bOutPutException) {
				System.err.println("Failed I/O to server" + m_sHostAddr + ":"
						+ ioexception);
			}
			return 1;
		}
		DataRec = null;
		DataSend = null;
		return 0;
	}

	/**
	 * Send
	 * 
	 * @param data
	 *            byte[]
	 * @param offset
	 *            int
	 * @param length
	 *            int
	 * @return int
	 */
	public int Send(byte[] data, int offset, int length) {
		try {
			DataSend.write(data, offset, length);
			DataSend.flush();
		} catch (IOException ex) {
			System.err.println("Failed I/O: " + ex);
			return 1;
		}
		return 0;
	}

	/**
	 * Recv
	 * 
	 * @param length
	 *            int
	 * @return byte[]
	 */
	public byte[] Recv(int length) {
		byte[] tempData = new byte[length];
		try {
			int sLength = DataRec.read(tempData, 0, length);
			byte[] recvData = new byte[sLength];
			System.arraycopy(tempData, 0, recvData, 0, sLength);
			return recvData;
		} catch (IOException ex) {
			System.err.println("Failed I/O: " + ex);
			return null;
		}
	}

	public static void main(String[] args) {
		String aa = "1234";
		String bb = "13";
		if (aa.matches(String.format("%s.*", bb))) {
			System.out.println("OK");
		} else {
			System.out.println("ERROR");
		}
		@SuppressWarnings("unused")
		SocketPoolEx socketpoolex = new SocketPoolEx();
		m_pSocketPool.InitSocketPool("127.0.0.1", 4700, 1, 1);
		m_pSocketPool.SetOption(bOutPutException);

		int MyPool[] = new int[1];
		m_pSocketPool.PopUp(m_pSocketInfo, MyPool, 1, 1);
		try {
			if (DataRec != null) {
				DataRec = null;
			}
			DataRec = new DataInputStream(m_pSocketInfo.pSocket
					.getInputStream());
			if (DataSend != null) {
				DataSend = null;
			}
			DataSend = new DataOutputStream(m_pSocketInfo.pSocket
					.getOutputStream());
		} catch (IOException ioexception) {
			// nErrorCode = 107;
			if (bOutPutException) {
				System.err.println("Failed I/O: " + ioexception);
			}
			// DestroyConnection(k);
			return;
		}

		try {
			String line = "C:Hello World";
			DataSend.write(line.getBytes(), 0, line.length());
			DataSend.flush();
			byte abyte3[] = new byte[100];
			int sLength = DataRec.read(abyte3, 0, 100);
			line = new String(abyte3, 0, sLength);

			System.out.println(line);
		} catch (IOException ex) {
			System.err.println("Failed I/O: " + ex);
		}
	}
}
