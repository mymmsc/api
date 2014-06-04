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

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;


public final class SocketPool {
	public int m_nCachedSocketCount;
	public CachedSocket m_pCachedSocket[];
	public int m_nSocketTimeout;
	private String m_HostAddress;
	private int m_nPort;
	private boolean m_bOutputException;

	/**
	 * SocketPool
	 */
	public SocketPool() {
		m_pCachedSocket = null;
		m_nCachedSocketCount = 0;
		m_pCachedSocket = null;
		m_nSocketTimeout = 0;
	}

	/**
	 * Close
	 * 
	 * @param socketinfo
	 *            SocketInfo
	 * @return int
	 */
	public synchronized int Close(SocketInfo socketinfo) {
		try {
			if (socketinfo.pSocket != null) {
				socketinfo.pSocket.close();
				socketinfo.pSocket = null;
			}
		} catch (IOException ioexception) {
			socketinfo.pSocket = null;
			if (m_bOutputException) {
				System.out.println("Failed I/O:" + ioexception);
			}
			return 1;
		}
		if (socketinfo.nPos >= 0) {
			m_pCachedSocket[socketinfo.nPos].pSocket = null;
			m_pCachedSocket[socketinfo.nPos].nStatus = 0;
		}
		return 0;
	}

	/**
	 * CloseAll
	 */
	public void CloseAll() {
		synchronized (m_pCachedSocket) {
			for (int i = 0; i < m_nCachedSocketCount; i++) {
				try {
					m_pCachedSocket[i].pSocket.close();
					m_pCachedSocket[i].pSocket = null;
				} catch (IOException ioexception) {
					m_pCachedSocket[i].pSocket = null;
					if (m_bOutputException) {
						System.out.println("Failed I/O:" + ioexception);
					}
				}
			}

		}
	}

	/**
	 * InitSocketPool
	 * 
	 * @param HostIP
	 *            String
	 * @param nPort
	 *            int
	 * @param nCachedSocketCount
	 *            int
	 * @param nSocketTimeout
	 *            int
	 */
	public void InitSocketPool(String HostIP, int nPort,
			int nCachedSocketCount, int nSocketTimeout) {
		m_nPort = nPort;
		m_nCachedSocketCount = nCachedSocketCount;
		m_HostAddress = HostIP;
		m_pCachedSocket = new CachedSocket[nCachedSocketCount];
		if (m_pCachedSocket == null) {
			m_nCachedSocketCount = 0;
			return;
		}
		for (int nIndex = 0; nIndex < nCachedSocketCount; nIndex++) {
			m_pCachedSocket[nIndex] = new CachedSocket();
		}

		m_nSocketTimeout = nSocketTimeout;
	}

	/**
	 * PopUp
	 * 
	 * @param socketinfo
	 *            SocketInfo
	 * @param ai
	 *            int[]
	 * @param nTimeOutS
	 *            int
	 * @param j
	 *            int
	 * @return int
	 */
	public synchronized int PopUp(SocketInfo socketinfo, int ai[],
			int nTimeOutS, int j) {
		int i1 = -1;
		int l = (int) (System.currentTimeMillis() / 1000L);
		ai[0] = 0;
		for (int k = 0; k < m_nCachedSocketCount; k++) {
			try {
				if (m_pCachedSocket[k].pSocket != null
						&& m_pCachedSocket[k].nStatus == 0) {
					if (m_nSocketTimeout > 0
							&& l - m_pCachedSocket[k].nActiveTime > m_nSocketTimeout) {
						m_pCachedSocket[k].nStatus = 0;
						m_pCachedSocket[k].pSocket.close();
						m_pCachedSocket[k].pSocket = null;
						i1 = k;
					} else {
						m_pCachedSocket[k].nActiveTime = (int) (System
								.currentTimeMillis() / 1000L);
						m_pCachedSocket[k].nStatus = 1;
						socketinfo.nPos = k;
						socketinfo.pSocket = m_pCachedSocket[k].pSocket;
						ai[0] = 0;
						return 0;
					}
				} else if (m_pCachedSocket[k].pSocket == null) {
					i1 = k;
				}
			} catch (IOException ioexception) {
				if (m_bOutputException) {
					System.out.println("Failed I/O:" + ioexception);
				}
				return 1;
			}
		}

		if (i1 != -1) {
			Socket socket;
			try {
				socket = new Socket(m_HostAddress, m_nPort);
				socket.setSoTimeout(nTimeOutS * 1000);
			} catch (SocketException socketexception) {
				if (m_bOutputException) {
					System.out.println("Failed Socket:" + socketexception);
				}
				return 1;
			} catch (UnknownHostException unknownhostexception) {
				if (m_bOutputException) {
					System.out.println("Failed Socket,Unknown Host:"
							+ unknownhostexception);
				}
				return 1;
			} catch (IOException ioexception1) {
				if (m_bOutputException) {
					System.out.println("Failed I/O:" + ioexception1);
				}
				return 1;
			}
			m_pCachedSocket[i1].pSocket = socket;
			m_pCachedSocket[i1].nStatus = 1;
			m_pCachedSocket[i1].nActiveTime = (int) (System.currentTimeMillis() / 1000L);
			socketinfo.nPos = i1;
			socketinfo.pSocket = m_pCachedSocket[i1].pSocket;
			ai[0] = 1;
			return 0;
		}
		if (j == 0) {
			Socket socket1;
			try {
				socket1 = new Socket(m_HostAddress, m_nPort);
				socket1.setSoTimeout(nTimeOutS * 1000);
			} catch (SocketException socketexception1) {
				if (m_bOutputException) {
					System.out.println("Failed Socket:" + socketexception1);
				}
				return 1;
			} catch (UnknownHostException unknownhostexception1) {
				if (m_bOutputException) {
					System.out.println("Failed Socket,Unknown Host:"
							+ unknownhostexception1);
				}
				return 1;
			} catch (IOException ioexception2) {
				if (m_bOutputException) {
					System.out.println("Failed I/O:" + ioexception2);
				}
				return 1;
			}
			socketinfo.nPos = -1;
			socketinfo.pSocket = socket1;
			ai[0] = 1;
			return 0;
		} else {
			return 1;
		}
	}

	/**
	 * PullIn
	 * 
	 * @param socketinfo
	 *            SocketInfo
	 * @return int
	 */
	public synchronized int PullIn(SocketInfo socketinfo) {
		try {
			if (socketinfo.nPos == -1) {
				if (socketinfo.pSocket != null) {
					socketinfo.pSocket.close();
					socketinfo.pSocket = null;
				}
			} else {
				m_pCachedSocket[socketinfo.nPos].nStatus = 0;
			}
		} catch (IOException ioexception) {
			if (m_bOutputException) {
				System.out.println("Failed I/O:" + ioexception);
			}
			return 1;
		}
		return 0;
	}

	/**
	 * SetOption
	 * 
	 * @param flag
	 *            boolean
	 */
	public void SetOption(boolean flag) {
		m_bOutputException = flag;
	}

	/**
	 * finalize
	 */
	protected void finalize() {
		CloseAll();
	}
}
