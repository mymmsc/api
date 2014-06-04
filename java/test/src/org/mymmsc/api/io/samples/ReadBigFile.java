package org.mymmsc.api.io.samples;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * 快速读取超大文件
 * @author WangFeng
 * @remark 测试实验阶段
 */
public class ReadBigFile {
	public static void readBigFile() throws IOException {
		String fileName = "/Users/mc2/Desktop/youku.txt";
		RandomAccessFile randomFile = null;
		randomFile = new RandomAccessFile(fileName, "r");
		long fileLength = randomFile.length();
		System.out.println("文件大小:" + fileLength);
		
		byte[] bytes = null; 
        ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte buff[] = new byte[1024];
		int len = 0;
		
		while ((len = randomFile.read(buff)) != -1) {
			out.write(buff, 0, len);
			fileLength += len;
		}
		bytes = out.toByteArray();
		
		System.out.println(bytes.length);
		System.out.println(new String(bytes, "UTF-8"));
		
		
		if (randomFile != null) {
			randomFile.close();
		}

	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		ReadBigFile.readBigFile();
	}

}