/**
 * @(#)Communication.java	6.3.9 09/10/02
 *
 * Copyright 2000-2010 MyMMSC Software Foundation (MSF), Inc. All rights reserved.
 * MyMMSC PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.mymmsc.w3c.gsm.samples;

import java.util.Calendar;
import org.mymmsc.w3c.dso.driver.DsoUtils;
import org.mymmsc.w3c.gsm.Communicator;


/**
 * @author WangFeng(wangfeng@yeah.net)
 * @version 6.3.9 09/10/02
 * @since mymmsc-gsm 6.3.9
 */
public class TestComm {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println(DsoUtils.getSerialPort(1));
		Communicator comm = new Communicator();
		comm.listSerialPort();
		//comm.sendmsg("测试", "13910798662");
		String tmpStr = "Hello, 测试猫!";
		System.out.println(tmpStr);
		//String s = new String(tmpStr.getBytes("GBK"), "");
		String s = tmpStr;
		//comm.sendmsg(s, "13910798662");
		comm.sendmsg(s, "13911113230");
		long tm = 0;
		while (true) {
			try {
				if (tm == 0) {
					comm.sendmsg(Calendar.getInstance()
							.getTime().toString(), "10658658014");
				}
				System.out.println("====================< 读取所有短信  >====================");
				comm.readAllMessage(4);
				System.out.println("====================< ECHO所有短信  >====================");
				//comm.sendAll();
				comm.checkAll();
				
				System.out.println("====================<  删除所有短信  >====================");
				comm.removeAll();
				System.out.println("============================================================");
				Thread.sleep(1000);
				tm += 1000;
				if (tm > 600000) {
					tm = 0;
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}

}
