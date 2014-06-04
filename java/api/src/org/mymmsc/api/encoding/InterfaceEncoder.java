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
package org.mymmsc.api.encoding;

/**
 * <p>
 * Title: MyMMSC BASE
 * </p>
 * 
 * <p>
 * Description: 公用基础类
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
public interface InterfaceEncoder {
	/**
	 * encode 编码
	 * 
	 * @param s
	 *            String
	 * @return String
	 */
	public String encode(String s);

	/**
	 * encode
	 * 
	 * @param b
	 *            byte[]
	 * @return String
	 */
	public String encode(byte[] b);

	/**
	 * decode 解码
	 * 
	 * @param s
	 *            String
	 * @return String
	 */
	public String decode(String s);

	/**
	 * decode
	 * 
	 * @param b
	 *            byte[]
	 * @return String
	 */
	public String decode(byte[] b);
}
