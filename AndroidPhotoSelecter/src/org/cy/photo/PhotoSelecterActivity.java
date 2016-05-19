package org.cy.photo;

import java.util.List;

import org.cy.photo.adpater.PhotoAdapter;
import org.cy.photo.adpater.PhotoAdapter.OnItemPhotoClick;
import org.cy.photo.adpater.PhotoAlbumAdapter;
import org.cy.photo.controller.CheckedManager;
import org.cy.photo.controller.PhotoController;
import org.cy.photo.moels.PhotoAlbum;
import org.cy.photo.moels.PhotoSelecter;
import org.cy.photo.presenter.PhotoSelecterIble;
import org.cy.photo.presenter.PhotoSelecterPresenter;
import org.cy.photo.utils.PhotoSelecterUtils;
import org.cy.photo.utils.KEY;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PhotoSelecterActivity extends Activity implements OnClickListener {

	private TextView barTitle, barDone;
	private ImageView barBack;
	private GridView mGridView;
	private ListView mListView;
	private RelativeLayout mDirectory;
	private TextView mDirectoryTv, mPreviewTv;

	private boolean isSingle;
	private PhotoAdapter mAdapter;
	private PhotoSelecterPresenter mPresenter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setActionbar(R.layout.actionbar_photo_selecter);
		initActionbar();
		initViews(R.layout.activity_photo_selecter);
		initData();
	}

	private void setActionbar(int layoutResID) {
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setCustomView(layoutResID);
	}

	private void initActionbar() {
		barBack = (ImageView) findViewById(R.id.imv_actionbar_back);
		barBack.setOnClickListener(this);
		barTitle = (TextView) findViewById(R.id.tv_actionbar_title);
		barDone = (TextView) findViewById(R.id.tv_actionbar_done);
		barDone.setOnClickListener(this);
		barTitle.setText("图片");
	}

	private void initViews(int layoutResID) {
		setContentView(layoutResID);
		mGridView = (GridView) findViewById(R.id.gv_photo_selecter_content);
		mListView = (ListView) findViewById(R.id.lv_photo_selector);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				PhotoAlbumAdapter adpater = (PhotoAlbumAdapter) parent
						.getAdapter();
				adpater.setChousItem(position);
				PhotoAlbum pa = (PhotoAlbum) adpater.getItem(position);
				String name = pa.getName();
				mDirectoryTv.setText(name);
				if (name.equals(PhotoController.RECENT_PHOTO)) {
					mPresenter.getRecentPhotos();
				} else
					mPresenter.getPhotoAlumbs(pa.getName());
			}
		});
		mDirectoryTv = (TextView) findViewById(R.id.tv_photo_selecter_directory);
		mDirectoryTv.setOnClickListener(this);
		mPreviewTv = (TextView) findViewById(R.id.tv_photo_selecter_preview);
		mPreviewTv.setOnClickListener(this);
		mPreviewTv.setEnabled(false);
		mDirectory = (RelativeLayout) findViewById(R.id.rl_photo_selecter_directory);
		mDirectory.setOnClickListener(this);

	}

	private void initData() {
		CheckedManager.clean();

		Intent i = getIntent();
		isSingle = i.getBooleanExtra(KEY.IS_SINGLE, false);

		if (isSingle) {
			barDone.setVisibility(View.GONE);
		} else {
			barDone.setVisibility(View.VISIBLE);
		}

		mAdapter = new PhotoAdapter(this, isSingle);
		mGridView.setAdapter(mAdapter);

		mPresenter = new PhotoSelecterPresenter(this);

		mPresenter.setPhotoSelecterIble(new PhotoSelecterIble() {

			@Override
			public void onPhotoSelecter(PhotoSelecter ps) {

				mAdapter.updateResult(ps.getPhotos());

				PhotoAlbumAdapter adapter = new PhotoAlbumAdapter(
						PhotoSelecterActivity.this);

				mListView.setAdapter(adapter);
				adapter.updateResult(ps.getPhotoAlbums());

			}

			@Override
			public void onPhotos(List<String> result) {
				mAdapter.updateResult(result);
				mDirectory.setVisibility(View.GONE);
			}
		});
		mPresenter.getPhotos();

		mAdapter.setOnPhotoClick(new OnItemPhotoClick() {

			@Override
			public void onPhotoChecked(boolean isChecked, String path) {
				changePreview();
				changeDone();
			}

			@Override
			public void onClick(int position) {
				if (isSingle) {
					String path = (String) mAdapter.getItem(position);
					doneSingle(path);
				} else {
					PhotoSelecterUtils.startPreview(PhotoSelecterActivity.this,
							mAdapter.getList(), position);
				}
			}
		});

	}

	private void changePreview() {
		if (CheckedManager.getChouseSize() == 0)
			mPreviewTv.setEnabled(false);
		else
			mPreviewTv.setEnabled(true);

	}

	private void changeDone() {
		if (CheckedManager.getChouseSize() == 0)
			barDone.setText("完成");
		else {
			barDone.setText(String.format("完成(%s/" + CheckedManager.MAX_CHOUSE
					+ ")", CheckedManager.getChouseSize()));
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode == PhotoSelecterUtils.PHOTO_2_PREVIEW) {
				Bundle bundle = data.getExtras();
				if (bundle.getBoolean(KEY.IS_DONE, false)) {
					onDone();
				} else
					updatePhoto(bundle);
			}
		}
	}

	private void updatePhoto(Bundle bundle) {

		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if (CheckedManager.getChouseSize() > 0)
					mPreviewTv.setEnabled(true);
				else
					mPreviewTv.setEnabled(false);
				mAdapter.notifyDataSetChanged();
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.imv_actionbar_back:
			onBack();
			break;
		case R.id.tv_actionbar_done:
			onDone();
			break;
		case R.id.tv_photo_selecter_directory:
			onDirectoty();
			break;
		case R.id.tv_photo_selecter_preview:
			onPreview();
			break;
		case R.id.rl_photo_selecter_directory:
			onDirectoty();
			break;
		default:
			break;
		}

	}

	@Override
	public void finish() {
		if (mDirectory.getVisibility() == View.VISIBLE)
			onDirectoty();
		else
			super.finish();
	}

	private void onBack() {
		this.finish();
	}

	private void onDone() {
		Intent i = getIntent();
		Bundle bundle = new Bundle();
		bundle.putStringArrayList(KEY.PHOTO_LIST,
				CheckedManager.getChouseList());
		i.putExtras(bundle);
		setResult(RESULT_OK, i);
		super.finish();
	}

	private void doneSingle(String path) {
		Intent i = getIntent();
		Bundle bundle = new Bundle();
		bundle.putString(KEY.PHOTO_PATH, path);
		i.putExtras(bundle);
		setResult(RESULT_OK, i);
		super.finish();
	}

	private void onDirectoty() {
		if (mDirectory.getVisibility() == View.GONE) {
			popAlbum();
		} else
			hideAlbum();
	}

	/** 弹出相册列表 */
	private void popAlbum() {
		mDirectory.setVisibility(View.VISIBLE);
		loadAnimation(this, R.anim.translate_up_current, mDirectory);
	}

	/** 隐藏相册列表 */
	private void hideAlbum() {
		loadAnimation(this, R.anim.translate_down_current, mDirectory);
		mDirectory.setVisibility(View.GONE);
	}

	private void loadAnimation(Context context, int resId, View view) {
		Animation anmiation = AnimationUtils.loadAnimation(context, resId);
		anmiation.setInterpolator(new LinearInterpolator());
		view.startAnimation(anmiation);
	}

	private void onPreview() {
		PhotoSelecterUtils.startPreview(this, 0);
	}

}
