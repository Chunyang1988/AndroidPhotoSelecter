package org.cy.photo.adpater;

import java.util.ArrayList;
import java.util.List;

import org.cy.photo.R;
import org.cy.photo.moels.PhotoAlbum;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class PhotoAlbumAdapter extends BaseAdapter {

	private Activity activity;
	private LayoutInflater inflater;
	private List<PhotoAlbum> list;
	private int chousePosition = 0;

	public PhotoAlbumAdapter(Activity activity) {
		inflater = LayoutInflater.from(activity);
		this.activity = activity;
		list = new ArrayList<PhotoAlbum>();
	}

	public void updateResult(List<PhotoAlbum> result) {
		this.list.clear();
		list.addAll(result);
		activity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				notifyDataSetChanged();
			}
		});
	}

	public void setChousItem(int position) {
		this.chousePosition = position;
		notifyDataSetChanged();
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
			cv = inflater.inflate(R.layout.item_photo_album_selecter, parent,
					false);
			holder.imv = (ImageView) cv
					.findViewById(R.id.imv_item_photo_album_photo);
			holder.name = (TextView) cv
					.findViewById(R.id.tv_item_photo_album_name);
			holder.count = (TextView) cv
					.findViewById(R.id.tv_item_photo_album_count);
			holder.rb = (TextView) cv.findViewById(R.id.rb_item_photo_album);
			cv.setTag(holder);
		} else {
			holder = (ViewHolder) cv.getTag();
		}
		PhotoAlbum pa = list.get(position);
		String name = pa.getName();
		int count = pa.getCount();
		holder.name.setText(name);
		holder.count.setText(String.format("%s张", count));
		Glide.with(activity).load(pa.getRecent()).into(holder.imv);

		if (chousePosition == position)
			holder.rb.setActivated(true);
		else
			holder.rb.setActivated(false);
		return cv;
	}

	private class ViewHolder {
		ImageView imv;
		TextView name, count, rb;

	}
}
