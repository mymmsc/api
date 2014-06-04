/**
 * 
 */
package org.mymmsc.api.security;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;

import org.mymmsc.api.assembly.Api;

/**
 * 自动锁, 文件锁
 * 
 * @author wangfeng
 * @version 6.3.12 2012/06/08
 */
public class AutoLock {
	private RandomAccessFile file = null;
	private FileLock fileLock = null;
	
	private AutoLock(RandomAccessFile file) {
		this.file = file;
	}
	
	public void lock() {
		do {
			try {
				fileLock = file.getChannel().tryLock();
			} catch (IOException e) {
				e.printStackTrace();
				Api.sleep(1);
			}
		} while (!fileLock.isValid());
		
	}
	public static AutoLock newLock(String filename) {
		AutoLock obj = null;
		try {
			RandomAccessFile file = new RandomAccessFile(filename, "rwd");
			obj = new AutoLock(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return obj;
	}
	
	public void close() {
		try {
			fileLock.release();
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws IOException {
		String filelock = "/Users/wangfeng/temp/autolock";
		AutoLock lock = AutoLock.newLock(filelock);
		
		try {
			lock.lock();
			Thread.sleep(60 * 1000);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (lock != null){
				lock.close();
			}
		}
	}
}
