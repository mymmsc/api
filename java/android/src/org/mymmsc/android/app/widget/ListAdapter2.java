/**
 * @(#)ListAdapter.java	8.0.1 2011-6-4
 *
 * Copyright 2004-2011 mymmsc.org (MyMMSC), Inc. All rights reserved.
 * MyMMSC PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.mymmsc.android.app.widget;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * ListView 抽象类
 * 
 * @author WangFeng(mail:wangfeng@yeah.net, mobile:13911113230)
 * @version 1.0.1 2011-5-28
 * @since mymmsc-android 1.0.1
 */
public abstract class ListAdapter2 extends ListActivity {
	protected IClickListener btnClick = null;
	private List<Map<String, Object>> m_list;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		m_list = getList();
		PrivateAdapter adapter = new PrivateAdapter(this);
		setListAdapter(adapter);
	}

	private List<Map<String, Object>> getList() {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		/*
		 * Map<String, Object> map = new HashMap<String, Object>();
		 * map.put("title", "G1"); map.put("info", "google 1"); map.put("img",
		 * R.drawable.i1); list.add(map);
		 * 
		 * map = new HashMap<String, Object>(); map.put("title", "G2");
		 * map.put("info", "google 2"); map.put("img", R.drawable.i2);
		 * list.add(map);
		 * 
		 * map = new HashMap<String, Object>(); map.put("title", "G3");
		 * map.put("info", "google 3"); map.put("img", R.drawable.i3);
		 * list.add(map);
		 */
		return list;
	}

	// ListView 中某项被选中后的逻辑
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {

		Log.v("MyListView4-click", (String) m_list.get(position).get("title"));
	}

	/**
	 * listview中点击按键弹出对话框
	 */
	public void showInfo() {
		new AlertDialog.Builder(this).setTitle("我的listview")
				.setMessage("介绍...")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				}).show();

	}

	public final class ViewHolder {
		public ImageView img;
		public TextView title;
		public TextView info;
		public Button button;
	}

	public class PrivateAdapter extends BaseAdapter {

		@SuppressWarnings("unused")
		private LayoutInflater mInflater;

		public PrivateAdapter(Context context) {
			this.mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return m_list.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolder holder = null;
			if (convertView == null) {

				holder = new ViewHolder();
				/*
				 * convertView = mInflater.inflate(R.layout.vlist2, null);
				 * holder.img = (ImageView) convertView.findViewById(R.id.img);
				 * holder.title = (TextView)
				 * convertView.findViewById(R.id.title); holder.info =
				 * (TextView) convertView.findViewById(R.id.info); holder.button
				 * = (Button) convertView .findViewById(R.id.view_btn);
				 * convertView.setTag(holder);
				 */
			} else {

				holder = (ViewHolder) convertView.getTag();
			}

			holder.img.setBackgroundResource((Integer) m_list.get(position)
					.get("img"));
			holder.title.setText((String) m_list.get(position).get("title"));
			holder.info.setText((String) m_list.get(position).get("info"));

			holder.button.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					showInfo();
				}
			});

			return convertView;
		}

	}
}
