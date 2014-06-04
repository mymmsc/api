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

import java.net.Socket;

public final class SocketInfo {
	int nPos;
	Socket pSocket;

	public SocketInfo() {
		nPos = 0;
		pSocket = null;
	}
}
