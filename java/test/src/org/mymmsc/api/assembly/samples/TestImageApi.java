/**
 * 
 */
package org.mymmsc.api.assembly.samples;

import org.mymmsc.api.io.ImageApi;

/**
 * @author WangFeng
 * 
 */
public class TestImageApi {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String src = "C:/temp/wangfeng.jpg";
/*
		// 1-缩放图像：
		// 方法一：按比例缩放
		ImageApi.scale(src, "c:/temp/wangfeng_scale.jpg", 2, true);// 测试OK
		// 方法二：按高度和宽度缩放
		ImageApi.scale2(src, "c:/temp/wangfeng_scale2.jpg", 500, 300, true);// 测试OK

		// 2-切割图像：
		// 方法一：按指定起点坐标和宽高切割
		ImageApi.cut(src, "c:/temp/wangfeng_cut.jpg", 0, 0, 400, 400);// 测试OK
		// 方法二：指定切片的行数和列数
		ImageApi.cut2(src, "e:/", 2, 2);// 测试OK
		// 方法三：指定切片的宽度和高度
		ImageApi.cut3(src, "e:/", 300, 300);// 测试OK

		// 3-图像类型转换：
		ImageApi.convert(src, "GIF", "c:/temp/wangfeng_convert.gif");// 测试OK
*/
		// 4-彩色转黑白：
		ImageApi.bw(src, "c:/temp/wangfeng_gray.jpg");// 测试OK
/*
		// 5-给图片添加文字水印：
		// 方法一：
		ImageApi.pressText("我是水印文字", src, "c:/temp/wangfeng_pressText.jpg",
				"宋体", Font.BOLD, Color.white, 80, 0, 0, 0.5f);// 测试OK
		// 方法二：
		ImageApi.pressText2("我也是水印文字", src, "c:/temp/wangfeng_pressText2.jpg",
				"黑体", 36, Color.white, 80, 0, 0, 0.5f);// 测试OK

		// 6-给图片添加图片水印：
		ImageApi.pressImage("e:/abc2.jpg", src,
				"c:/temp/wangfeng_pressImage.jpg", 0, 0, 0.5f);// 测试OK
*/
	}

}
