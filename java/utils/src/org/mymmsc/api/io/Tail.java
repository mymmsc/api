/**
 * @(#)Tail.java	6.3.9 09/10/02
 *
 * Copyright 2000-2010 MyMMSC Software Foundation (MSF), Inc. All rights reserved.
 * MyMMSC PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.mymmsc.api.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.Selector;

import org.mymmsc.api.assembly.Api;


/**
 * Tail like Unix/Linux "tail -f [filename]"
 * 
 * @author WangFeng(wangfeng@yeah.net)
 * @version 6.3.9 09/10/02
 * @since mymmsc-api 6.3.9
 */
public class Tail {
	
	public Tail() {
		//
	}
	
	public void open(String filename) {
		@SuppressWarnings("unused")
		FileChannel fc = null;
		File file = new File(filename);
		long tm = Api.getLastModified(filename);
		FileInputStream fis;
		try {
			fis = new FileInputStream(file);
			fc = fis.getChannel();
			@SuppressWarnings("unused")
			Selector hIocp = Selector.open();
			long tmpStamp = 0;
			byte[] buff = new byte[8192];
			while(true) {
				tmpStamp = Api.getLastModified(filename);
				if (tmpStamp >  tm) {
					tm = tmpStamp;
					if(fis.read(buff) > 0) {
						System.out.println(buff);
					}
				} else {
					Api.sleep(100);
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Tail tail = new Tail();
		tail.open("E:/projects/mymmsc/api/libjava/.metadata/.plugins/org.eclipse.wst.server.core/tmp0/wtpwebapps/boss/logs/20091206/Operate.Form.log");

	}

}
