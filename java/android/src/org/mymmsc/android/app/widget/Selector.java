/**
 * 
 */
package org.mymmsc.android.app.widget;

import java.util.List;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

/**
 * @author WangFeng(mail:wangfeng@yeah.net, mobile:13911113230)
 * @version 1.0.1 2011-5-28
 * @since mymmsc-android 1.0.1
 */
public class Selector {
	private Spinner spinner = null;
	private Context content = null;

	public Selector(Context root, Spinner spinner) {
		this.content = root;
		this.spinner = spinner;
	}

	private void init() {
		// 第五步：为下拉列表设置各种事件的响应，这个事响应菜单被选中
		spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				/* 将所选mySpinner 的值带入myTextView 中 */
				// myTextView.setText("您选择的是：" + apIsp.getItem(arg2));
				/* 将mySpinner 显示 */
				arg0.setVisibility(View.VISIBLE);
			}

			public void onNothingSelected(AdapterView<?> arg0) {
				// myTextView.setText("NONE");
				arg0.setVisibility(View.VISIBLE);
			}
		});
		/* 下拉菜单弹出的内容选项触屏事件处理 */
		spinner.setOnTouchListener(new Spinner.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				/* 将mySpinner 隐藏，不隐藏也可以，看自己爱好 */
				// v.setVisibility(View.INVISIBLE);
				return false;
			}
		});
		/* 下拉菜单弹出的内容选项焦点改变事件处理 */
		spinner.setOnFocusChangeListener(new Spinner.OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				v.setVisibility(View.VISIBLE);
				// spType.refreshDrawableState();
			}
		});
	}

	/**
	 * 设置下拉列表
	 * 
	 * @param list
	 * @param index
	 *            默认选项
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void setList(List<?> list, int index) {
		ArrayAdapter<?> array = null;
		array = new ArrayAdapter(content, android.R.layout.simple_spinner_item,
				list);
		// 第三步：为适配器设置下拉列表下拉时的菜单样式。
		array.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// 第四步：将适配器添加到下拉列表上
		spinner.setAdapter(array);
		spinner.setSelection(index);
		init();
	}
}
