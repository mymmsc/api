/**
 * @(#)ImageUtils.java	6.3.9 09/10/02
 *
 * Copyright 2000-2010 MyMMSC Software Foundation (MSF), Inc. All rights reserved.
 * MyMMSC PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.mymmsc.api.assembly;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.sun.image.codec.jpeg.ImageFormatException;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

/**
 * JAVA 图片处理杂类封装
 * 
 * @author WangFeng(wangfeng@yeah.net)
 * @version 6.3.9 09/10/02
 * @since mymmsc-api 6.3.9
 */
public class ImageUtils {

	/**
	 * 图片压缩
	 * 
	 * @param inputDir
	 *            输入图路径
	 * @param outputDir
	 *            输出图路径
	 * @param inputFileName
	 *            输入图文件名
	 * @param outputFileName
	 *            输出图文件名
	 * @return Boolean
	 * */
	public static boolean compressPic(String inputDir, String outputDir,
			String inputFileName, String outputFileName) {
		return compressPic(inputDir, outputDir, inputFileName, outputFileName,
				100, 100, true);
	}

	/**
	 * 图片压缩
	 * 
	 * @param inputDir
	 *            输入图路径
	 * @param outputDir
	 *            输出图路径
	 * @param inputFileName
	 *            输入图文件名
	 * @param outputFileName
	 *            输出图文件名
	 * @param outputWidth
	 *            图片长
	 * @param outputHeight
	 *            图片宽
	 * @param proportion
	 *            是否是等比缩放 标记
	 * @return Boolean
	 * */
	public static boolean compressPic(String inputDir, String outputDir,
			String inputFileName, String outputFileName, int outputWidth,
			int outputHeight, boolean proportion) {
		// 获得源文件
		File file = new File(inputDir, inputFileName);
		if (!file.exists()) {
			return false;
		}
		FileOutputStream out = null;
		try {
			Image img = null;
			try {
				img = ImageIO.read(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
			// 判断图片格式是否正确
			if (img == null || img.getWidth(null) == -1) {
				return false;
			} else {
				int newWidth;
				int newHeight;
				// 判断是否是等比缩放
				if (proportion == true) {
					// 为等比缩放计算输出的图片宽度及高度
					double rate1 = ((double) img.getWidth(null))
							/ (double) outputWidth + 0.1;
					double rate2 = ((double) img.getHeight(null))
							/ (double) outputHeight + 0.1;
					// 根据缩放比率大的进行缩放控制
					double rate = rate1 > rate2 ? rate1 : rate2;
					newWidth = (int) (((double) img.getWidth(null)) / rate);
					newHeight = (int) (((double) img.getHeight(null)) / rate);
				} else {
					newWidth = outputWidth; // 输出的图片宽度
					newHeight = outputHeight; // 输出的图片高度
				}
				BufferedImage tag = new BufferedImage((int) newWidth,
						(int) newHeight, BufferedImage.TYPE_INT_RGB);

				/*
				 * Image.SCALE_SMOOTH 的缩略算法 生成缩略图片的平滑度的 优先级比速度高 生成的图片质量比较好 但速度慢
				 */
				tag.getGraphics().drawImage(
						img.getScaledInstance(newWidth, newHeight,
								Image.SCALE_SMOOTH), 0, 0, null);
				try {
					out = new FileOutputStream(new File(outputDir,
							outputFileName));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				// JPEGImageEncoder可适用于其他图片类型的转换
				JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
				try {
					encoder.encode(tag);
				} catch (ImageFormatException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return true;
	}

	/*
	 * 获得图片大小 传入参数 String path ：图片路径
	 */
	public static long getPicSize(String path) {
		File file = new File(path);
		return file.length();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String path = "/Users/etong/Desktop";
		String in = "IMG_1893.JPG";
		String out = "IMG_1893_t.JPG";
		ImageUtils.compressPic(path, path, in, out);
	}
}
