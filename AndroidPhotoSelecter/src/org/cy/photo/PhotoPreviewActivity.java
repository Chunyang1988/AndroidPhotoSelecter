package org.cy.photo;

import java.util.ArrayList;
import java.util.List;

import org.cy.photo.adpater.PreviewFragmentPagerAdpater;
import org.cy.photo.controller.CheckedManager;
import org.cy.photo.utils.KEY;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PhotoPreviewActivity extends FragmentActivity implements
		OnClickListener {

	private ImageView barBack;
	private TextView barTitle, barDone;
	private ViewPager viewPager;
	private TextView checkBox;
	private RelativeLayout barTop, barBottom;
	private int position = 0;
	private int doneSize;
	private PreviewFragmentPagerAdpater adapter;
	private boolean isDel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initViews(R.layout.activity_photo_preview);
		initActionbar();
		initData();
	}

	private void initActionbar() {
		barBack = (ImageView) findViewById(R.id.imv_actionbar_back);
		barBack.setOnClickListener(this);
		barTitle = (TextView) findViewById(R.id.tv_actionbar_title);
		barTop = (RelativeLayout) findViewById(R.id.view_actionbar_top);
		barBottom = (RelativeLayout) findViewById(R.id.view_actionbar_bottom);
		barDone = (TextView) findViewById(R.id.tv_actionbar_done);
		barDone.setOnClickListener(this);
		checkBox = (TextView) findViewById(R.id.tv_photo_preview);
		checkBox.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String photo = getItemPhoto();
				if (!v.isActivated())
					addChouse(photo);
				else
					removeChouse(photo);
				changeDone();
			}
		});
	}

	private void initViews(int layoutResID) {
		setContentView(layoutResID);

		viewPager = (ViewPager) findViewById(R.id.vp_photo_preview);
		viewPager.addOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				changeTitle(arg0, adapter.getCount());
				changeCheckBok(getItemPhoto());
				barTop.setVisibility(View.GONE);
				barBottom.setVisibility(View.GONE);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});
	}

	private void initData() {
		Intent i = getIntent();
		Bundle bundle = i.getExtras();
		isDel = bundle.getBoolean(KEY.IS_PREV_DEL, false);
		if (isDel) {
			barDone.setText("删除");
			barBottom.setVisibility(View.GONE);
		} else {
			barBottom.setVisibility(View.VISIBLE);
		}

		new Task().execute(bundle);
	}

	private class Task extends AsyncTask<Bundle, Void, ArrayList<String>> {

		int position = 0;

		@Override
		protected ArrayList<String> doInBackground(Bundle... params) {
			Bundle bundle = params[0];
			position = bundle.getInt(KEY.POSITION);

			ArrayList<String> list = bundle.getStringArrayList(KEY.PHOTO_LIST);
			doneSize = list.size() > CheckedManager.MAX_CHOUSE ? CheckedManager.MAX_CHOUSE
					: list.size();

			return list;
		}

		@Override
		protected void onPostExecute(ArrayList<String> result) {
			super.onPostExecute(result);
			changeTitle(0, result.size());
			adapter = new PreviewFragmentPagerAdpater(
					getSupportFragmentManager(), result);
			viewPager.setAdapter(adapter);
			if (isDel) {
				viewPager.setCurrentItem(position, false);
				barTop.setVisibility(View.VISIBLE);
			} else {
				changeDone();
				if (position == 0) {
					changeCheckBok(getItemPhoto());
				} else {
					viewPager.setCurrentItem(position, false);
				}
			}

		}
	}

	private String getItemPhoto() {
		List<String> list = adapter.getList();
		return list.get(position);
	}

	private void addChouse(String path) {
		if (CheckedManager.getChouseSize() == CheckedManager.MAX_CHOUSE) {
			checkBox.setActivated(false);
			CheckedManager.toastShow(this);
			return;
		}

		if (CheckedManager.addChousPath(path)) {
			checkBox.setActivated(true);
		} else
			checkBox.setActivated(false);

	}

	private void removeChouse(String path) {
		if (CheckedManager.removeChousPath(path)) {
			checkBox.setActivated(false);
		}
	}

	private void changeTitle(int item, int size) {
		position = item;
		barTitle.setText(String.format("%s/%s", item + 1, size));
	}

	private void changeCheckBok(String path) {
		if (isDel)
			return;
		if (CheckedManager.contains(path))
			checkBox.setActivated(true);
		else
			checkBox.setActivated(false);
	}

	private void changeDone() {
		if (isDel)
			return;
		if (CheckedManager.getChouseSize() == 0)
			barDone.setText("完成");
		else {
			barDone.setText(String.format("完成(%s/" + doneSize + ")",
					CheckedManager.getChouseSize()));
		}
	}

	public void changeActionbar() {
		if (barTop.getVisibility() == View.VISIBLE) {
			hideActionbar();
		} else {
			showActionbar();
		}
	}

	/** 弹出相册列表 */
	private void showActionbar() {
		barTop.setVisibility(View.VISIBLE);
		loadAnimation(getApplicationContext(), R.anim.alpha_0_1, barTop);
		if (isDel)
			return;
		barBottom.setVisibility(View.VISIBLE);
		loadAnimation(getApplicationContext(), R.anim.alpha_0_1, barBottom);
	}

	private void loadAnimation(Context context, int resId, View view) {
		Animation anmiation = AnimationUtils.loadAnimation(context, resId);
		anmiation.setInterpolator(new LinearInterpolator());
		view.startAnimation(anmiation);
	}

	/** 隐藏相册列表 */
	private void hideActionbar() {
		loadAnimation(getApplicationContext(), R.anim.alpha_1_0, barTop);
		barTop.setVisibility(View.GONE);
		if (isDel)
			return;
		barBottom.setVisibility(View.GONE);
		loadAnimation(getApplicationContext(), R.anim.alpha_1_0, barBottom);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.imv_actionbar_back:
			onBack();
			break;
		case R.id.tv_actionbar_done:
			if (isDel) {
				String path = getItemPhoto();
				adapter.getList().remove(path);
				if (adapter.getList().size() == 0) {
					this.finish();
				} else {
					adapter.notifyDataSetChanged();
					changeTitle(position, adapter.getCount());
					removeChouse(path);
				}
			} else
				onDone();
			break;

		default:
			break;
		}

	}

	@Override
	public void finish() {
		Intent i = getIntent();
		Bundle bundle = new Bundle();
		if (isDel) {
			bundle.putStringArrayList(KEY.PHOTO_LIST,
					CheckedManager.getChouseList());
		} else {
			bundle.putBoolean(KEY.IS_DONE, false);
		}
		i.putExtras(bundle);
		setResult(RESULT_OK, i);
		super.finish();
	}

	private void onDone() {
		Intent i = getIntent();
		Bundle bundle = new Bundle();
		bundle.putBoolean(KEY.IS_DONE, true);
		i.putExtras(bundle);
		setResult(RESULT_OK, i);
		super.finish();
	}

	private void onBack() {
		this.finish();
	}
}
