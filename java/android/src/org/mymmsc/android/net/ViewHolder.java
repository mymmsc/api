package org.mymmsc.android.net;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 列表单行资源
 * 
 * @author WangFeng(mail:wangfeng@yeah.net, mobile:13911113230)
 * @version 1.0.1 2011-5-28
 * @since mymmsc-android 1.0.1
 */
public class ViewHolder {
	/** 无状态 */
	public final static int ST_NONE = 0;
	/** 正在加载 */
	public final static int ST_LOADING = 1;
	/** 完成 */
	public final static int ST_FINISHED = 2;
	/** 无资源 */
	public final static int ST_NOTFOUND = 404;

	/** 索引 */
	public int index = -1;
	/** 状态 */
	public int state = ST_NONE;
	/** 图片 */
	public ImageView image;
	/** 标题 */
	public TextView title;
	/** 信息 */
	public TextView info;
	/** 描述 */
	public TextView desc;
	/** 按钮 */
	public Button button;
}
