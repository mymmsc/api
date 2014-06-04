package org.mymmsc.android.app;

import java.util.List;

import org.mymmsc.android.app.util.AppHelpers;
import org.mymmsc.android.net.AsyncImageLoaderTask;
import org.mymmsc.android.net.ViewHolder;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 资源下载适配器
 * 
 * @param <T>
 * @author WangFeng(mail:wangfeng@yeah.net, mobile:13911113230)
 * @version 1.0.1 2011-5-28
 * @since mymmsc-android 1.0.1
 */
public class ListViewAdapter<T> extends ArrayAdapter<T> {
	private final static String TagOfImage = "image";
	private final static String TagOfTitle = "title";
	private final static String TagOfInfo = "info";
	private final static String TagOfButton = "button";
	@SuppressWarnings("unused")
	private final static String TagOfImageButton = "imageButton";
	private final static String TagOfLayout = "middleLayout";
	private Activity mActivity = null;
	private int mLayoutId = 0;
	@SuppressWarnings("unused")
	private int mHeight = 0;
	private int mWidth = 320;
	private IViewClick mViewClick = null;
	private LayoutInflater mInflater = null;
	private List<T> mList = null;

	/**
	 * @param activity
	 * @param list
	 * @param id
	 * @param clkView
	 */
	public ListViewAdapter(Activity activity, List<T> list, int id,
			IViewClick clkView) {
		super(activity, 0, list);

		this.mActivity = activity;
		this.mList = list;
		this.mLayoutId = id;
		this.mViewClick = clkView;
		this.mInflater = mActivity.getLayoutInflater();
		this.mWidth = this.mActivity.getWindowManager().getDefaultDisplay()
				.getWidth();
		this.mHeight = this.mActivity.getWindowManager().getDefaultDisplay()
				.getHeight();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		System.out.println("1. " + position + " start.");
		T value = mList.get(position);
		IResource rb = (IResource) value;
		ViewHolder holder = null;
		if (convertView != null) {
			holder = (ViewHolder) convertView.getTag();
			System.out.println("2.1. " + position + ", old: " + holder.index
					+ "->" + holder.state);
			if (position != holder.index) {
				convertView = null;
			}
		}
		if (convertView == null) {
			System.out.println("2.1. " + position + ", new");
			holder = new ViewHolder();
			holder.index = position;
			convertView = mInflater.inflate(mLayoutId, null);
			holder.image = (ImageView) convertView.findViewWithTag(TagOfImage);
			holder.image.setOnTouchListener(AppHelpers.TouchForView);
			holder.title = (TextView) convertView.findViewWithTag(TagOfTitle);
			holder.info = (TextView) convertView.findViewWithTag(TagOfInfo);
			holder.button = (Button) convertView.findViewWithTag(TagOfButton);
			holder.state = ViewHolder.ST_NONE;
			convertView.setTag(holder);
		}
		if (position == holder.index && holder.state == ViewHolder.ST_NONE) {
			System.out.println("2.2. " + position + ", " + holder.index);
			holder.title.setText(rb.getTitle());
			holder.info.setText(rb.getInfo());
			LinearLayout ll = (LinearLayout) convertView
					.findViewWithTag(TagOfLayout);
			ll.getLayoutParams().width = mWidth * 2 / 3 - 20;

			holder.button.setOnClickListener(new onButtonListener(position));
			holder.image.setOnClickListener(new onImageListener(position));

			if (rb.getImgUrl() != null
					&& holder.state != ViewHolder.ST_NOTFOUND) {
				new AsyncImageLoaderTask().execute(rb.getImgUrl(), holder);
			}

			convertView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					ViewHolder holder = (ViewHolder) v.getTag();
					mViewClick.doView(holder);
				}
			});
		}
		System.out.println("2. " + position + " stop.");
		return convertView;
	}

	class onImageListener implements OnClickListener {
		private int pos = -1;

		public onImageListener(int p) {
			pos = p;
		}

		@Override
		public void onClick(View v) {
			// int vid = v.getId();
			// if (vid == holder.image.getId()) {
			IResource rb = (IResource) mList.get(pos);
			ImageView iv = (ImageView) v.findViewWithTag(TagOfImage);
			mViewClick.doImage(rb.getTitle(), iv.getDrawable());
			// }
		}
	}

	class onButtonListener implements OnClickListener {
		private int pos = -1;

		public onButtonListener(int p) {
			pos = p;
		}

		@Override
		public void onClick(View v) {
			// int vid = v.getId();
			// if (vid == holder.button.getId()) {
			IResource rb = (IResource) mList.get(pos);
			// Special.setMealTeamPid(rb.consignmentid);
			mViewClick.doButton(rb);
			// }
		}
	}
}
