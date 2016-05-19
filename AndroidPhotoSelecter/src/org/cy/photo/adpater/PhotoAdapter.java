package org.cy.photo.adpater;

import java.util.ArrayList;
import java.util.List;

import org.cy.photo.R;
import org.cy.photo.controller.CheckedManager;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class PhotoAdapter extends BaseAdapter {

	private LayoutInflater inflater;

	private ArrayList<String> list;
	private Activity activity;
	private boolean isSingle;
	// private LayoutParams itemLayoutParams;
	private int itemWidth;
	private OnItemPhotoClick mClick;

	public void setOnPhotoClick(OnItemPhotoClick click) {
		this.mClick = click;
	}

	public interface OnItemPhotoClick {

		void onPhotoChecked(boolean isChecked, String path);

		void onClick(int position);
	}

	public ArrayList<String> getList() {
		return list;
	}

	public PhotoAdapter(Activity context, boolean isSingle) {
		this.activity = context;
		inflater = LayoutInflater.from(context);
		list = new ArrayList<String>();
		this.isSingle = isSingle;
		setItemWidth();
	}

	public void updateResult(List<String> result) {
		list.clear();
		list.addAll(result);
		activity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				notifyDataSetChanged();
			}
		});
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
	}

	/** 设置每一个Item的宽高 */
	private void setItemWidth() {
		DisplayMetrics dm = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		itemWidth = (dm.widthPixels) / 3;
		// this.itemLayoutParams = new LayoutParams(itemWidth, itemWidth);
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View cv, ViewGroup parent) {

		ViewHolder holder = null;

		if (cv == null) {
			holder = new ViewHolder();
			cv = inflater.inflate(R.layout.item_photo_selecter, parent, false);
			holder.imv = (ImageView) cv
					.findViewById(R.id.imv_item_photo_selecter);
			holder.tv = (TextView) cv
					.findViewById(R.id.tv_item_photo_selecter_check);
			// cv.setLayoutParams(itemLayoutParams);
			cv.setTag(holder);
		} else {
			holder = (ViewHolder) cv.getTag();
		}

		String path = list.get(position);

		android.view.ViewGroup.LayoutParams params = cv.getLayoutParams();
		params.height = itemWidth;
		params.width = itemWidth;

		if (isSingle) {
			holder.tv.setVisibility(View.GONE);
		} else {
			holder.tv.setVisibility(View.VISIBLE);
			boolean isChecked = CheckedManager.contains(path);
			if (isChecked) {
				holder.tv.setActivated(true);
				holder.imv.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
			} else {
				holder.tv.setActivated(false);
				holder.imv.clearColorFilter();
			}
		}

		Glide.with(activity).load(path).into(holder.imv);
		holder.imv.setTag(R.id.tag_first, path);
		holder.imv.setTag(R.id.tag_second, position);
		holder.tv.setTag(holder.imv);
		holder.tv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ImageView imv = (ImageView) v.getTag();
				String path = (String) imv.getTag(R.id.tag_first);
				if (!v.isActivated()) {
					if (CheckedManager.addChousPath(path)) {
						v.setActivated(true);
						imv.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
					} else {
						CheckedManager.toastShow(activity);
						v.setActivated(false);
						imv.clearColorFilter();
					}
				} else {
					CheckedManager.removeChousPath(path);
					v.setActivated(false);
					imv.clearColorFilter();
				}
				if (mClick != null)
					mClick.onPhotoChecked(v.isActivated(), path);
			}
		});

		holder.imv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int position = (Integer) v.getTag(R.id.tag_second);
				if (mClick != null)
					mClick.onClick(position);
			}
		});

		return cv;
	}

	// private void setColorMatrix(ImageView imageView, int saturation) {
	// imageView.setImageAlpha(saturation);
	// }

	private class ViewHolder {
		ImageView imv;
		TextView tv;
	}

}
