/**
 * 
 */
package org.mymmsc.api.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.mymmsc.api.assembly.Api;

/**
 * 文件操作类
 * 
 * @author wangfeng
 * @version 6.3.13
 * @remark 增强操作性能
 */
public final class FileApi {

	/**
	 * 读文件
	 * 
	 * @param filename
	 * @remark 适合小文件的一次性读取
	 * @return 字节数组
	 */
	@SuppressWarnings("resource")
	public static byte[] read(String filename) {
		File file = new File(filename);
		byte[] ret = null;
		try {
			if (filename == null || filename.equals("")) {
				throw new NullPointerException("无效的文件路径");
			}
			long len = file.length();
			byte[] bytes = new byte[(int) len];

			BufferedInputStream bis = new BufferedInputStream(
					new FileInputStream(file));
			int r = bis.read(bytes);
			if (r != len) {
				throw new IOException("读取文件不正确");
			}
			bis.close();
			ret = bytes;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			//
		}
		return ret;
	}

	public static void closeQuietly(OutputStream output) {
		try {
			if (output != null) {
				output.close();
			}
		} catch (IOException ioe) {
			//
		}
	}
	
	/**
	 * 向输出流写入一段数据
	 * @param out
	 * @param data
	 * @throws IOException
	 */
	public static void write(OutputStream out, byte[] data) throws IOException {
		out.write(data);
	}
	
	/**
	 * 写文件
	 * 
	 * @param file
	 * @param data
	 * @throws IOException
	 */
	public static void write(File file, byte[] data) throws IOException {
		OutputStream out = new FileOutputStream(file);
		try {
			out.write(data);
		} finally {
			closeQuietly(out);
		}
	}

	/**
	 * 写文件
	 * 
	 * @param filename 文件名
	 * @param data 数据
	 * @throws IOException
	 */
	public static void write(String filename, byte[] data) throws IOException {
		String path = Api.dirName(filename);
		Api.mkdirs(path);
		write(new File(filename), data);
	}

	/**
	 * 复制文件
	 * 
	 * @param sourceFile 原文件
	 * @param targetFile 目标文件
	 * @throws IOException
	 */
	public static boolean copyFile(File sourceFile, File targetFile)
			throws IOException {
		boolean bRet = false;
		// 新建文件输入流并对它进行缓冲
		FileInputStream input = new FileInputStream(sourceFile);
		BufferedInputStream inBuff = new BufferedInputStream(input);

		// 新建文件输出流并对它进行缓冲
		FileOutputStream output = new FileOutputStream(targetFile);
		BufferedOutputStream outBuff = new BufferedOutputStream(output);

		// 缓冲数组
		byte[] b = new byte[1024 * 5];
		int len;
		while ((len = inBuff.read(b)) != -1) {
			outBuff.write(b, 0, len);
		}
		// 刷新此缓冲的输出流
		outBuff.flush();

		// 关闭流
		inBuff.close();
		outBuff.close();
		output.close();
		input.close();
		bRet = true;
		return bRet;
	}

	/**
	 * 复制文件夹
	 * 
	 * @param sourceDir 原目录
	 * @param targetDir 目标路径
	 * @throws IOException
	 */
	public static void copyDirectiory(String sourceDir, String targetDir)
			throws IOException {
		// 新建目标目录
		(new File(targetDir)).mkdirs();
		// 获取源文件夹当前下的文件或目录
		File[] file = (new File(sourceDir)).listFiles();
		for (int i = 0; i < file.length; i++) {
			if (file[i].isFile()) {
				// 源文件
				File sourceFile = file[i];
				// 目标文件
				File targetFile = new File(
						new File(targetDir).getAbsolutePath() + File.separator
								+ file[i].getName());
				copyFile(sourceFile, targetFile);
			}
			if (file[i].isDirectory()) {
				// 准备复制的源文件夹
				String dir1 = sourceDir + "/" + file[i].getName();
				// 准备复制的目标文件夹
				String dir2 = targetDir + "/" + file[i].getName();
				copyDirectiory(dir1, dir2);
			}
		}
	}
	
	/**
	 * 移动文件
	 * 
	 * @param source
	 * @param dest
	 */
	public static boolean moveFile(String source, String dest) {
		boolean bRet = false;
		File s = new File(source);
		File d = new File(dest);
		try {
			bRet = s.renameTo(d);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bRet;
	}
	
	/**
	 * 复制文件
	 * 
	 * @param source
	 * @param dest
	 */
	public static boolean copyFile(String source, String dest) {
		boolean bRet = false;
		File s = new File(source);
		File d = new File(dest);
		try {
			bRet = copyFile(s, d);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bRet;
	}
	
	/**
	 * 删除文件
	 * @param filename 文件路径
	 * @return 是否删除成功
	 */
	public static boolean delete(String filename) {
		boolean bRet = false;
		File file = new File(filename);
		if (file.isFile()) {
			try {
				file.delete();
				bRet = true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return bRet;
	}
}
