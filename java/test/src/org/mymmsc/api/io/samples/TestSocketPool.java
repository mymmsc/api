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
package org.mymmsc.api.io.samples;

import org.mymmsc.api.io.SocketPoolEx;

/**
 * <p>
 * Title: MyMMSC SocketPool
 * </p>
 * 
 * <p>
 * Description: SocketPool of MyMMSC
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2000-2009 mymmsc.org
 * </p>
 * 
 * <p>
 * Company: MyMMSC Software Foundation (MSF)
 * </p>
 * 
 * @author WangFeng
 * @version 6.3.9
 */
public class TestSocketPool {

	public static void main(String[] args) {
		SocketPoolEx MySocket = new SocketPoolEx();
		MySocket.Init("127.0.0.1", 7890, 1, 30);
		MySocket.Connect(30);
		String content = "hello";
		MySocket.Send(content.getBytes(), 0, content.length());
		byte[] recvData = MySocket.Recv(100);
		String line = new String(recvData, 0, recvData.length);
		System.out.println(line);
	}
}
