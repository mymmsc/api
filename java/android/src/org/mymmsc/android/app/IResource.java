package org.mymmsc.android.app;

/**
 * 资源列表单元接口
 * 
 * @author WangFeng(mail:wangfeng@yeah.net, mobile:13911113230)
 * @version 1.0.1 2011-5-28
 * @since mymmsc-android 1.0.1
 * @category 资源列表共三列：第1列图片；第2列分三行，第1行标题，第2行信息内容，第3行描述；第3列按钮。
 */
public abstract interface IResource {
	public final static String TagOfImage = "image";
	public final static String TagOfTitle = "title";
	public final static String TagOfInfo = "info";
	public final static String TagOfButton = "button";
	public final static String TagOfImageButton = "imageButton";
	public final static String TagOfLayout = "middleLayout";

	/**
	 * 得到图片URL
	 * 
	 * @return
	 */
	public abstract String getImgUrl();

	/**
	 * 取得标题
	 * 
	 * @return String
	 * @category 第一行文本
	 */
	public abstract String getTitle();

	/**
	 * 取得信息
	 * 
	 * @return String
	 * @category 第二行文本，可能是空
	 */
	public abstract String getInfo();

	/**
	 * 取得简易描述
	 * 
	 * @return
	 * @category 第三行文本
	 */
	public abstract String getDesc();

	/**
	 * 取得详细内容
	 * 
	 * @return String
	 * @category 完成的词条内容
	 */
	public abstract String getContent();

	/**
	 * 获得一个按钮
	 * 
	 * @return Button
	 */
	public abstract String getButtonText();
}
