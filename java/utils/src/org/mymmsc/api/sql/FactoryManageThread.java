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
package org.mymmsc.api.sql;

/**
 * 连接池调度线程
 * 
 * @author mark
 * 
 */
public class FactoryManageThread implements Runnable {
	ConnectionFactory cf = null;

	long delay = 1000;

	public FactoryManageThread(ConnectionFactory obj) {
		cf = obj;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		while (true) {
			try {
				Thread.sleep(delay);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			//System.out.println("run.....run.....");
			// 判断是否已经关闭了工厂，那就退出监听
			if (cf.isCreate()) {
				cf.schedule();
			} else {
				// System.exit(1);
			}
		}
	}
}