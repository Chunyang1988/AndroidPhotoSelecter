package org.cy.photo;

import java.io.IOException;
import java.io.OutputStream;

import org.cy.photo.crop.Crop;
import org.cy.photo.crop.CropHelp;
import org.cy.photo.crop.CropUtil;
import org.cy.photo.widget.ClipImageLayout;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class CropImageActivity extends Activity implements OnClickListener {

	private ImageView barBack;
	private TextView barTitle;
	private TextView barDone;
	private ClipImageLayout mCropImage;
	private Uri saveUri;
	private Uri sourceUri;
	private CropHelp mHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setActionbar(R.layout.actionbar_photo_selecter);
		initActionbar();
		initViews();
		loadInput();
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
		barTitle.setText("");
	}

	private void initViews() {
		mCropImage = new ClipImageLayout(this);
		LayoutParams rp = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		mCropImage.setLayoutParams(rp);
		setContentView(mCropImage);
	}

	private void initData() {
		mCropImage.setImageBitmap(mHelper.getDegreeBitmap());
	}

	private void loadInput() {
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		//
		if (extras != null) {
			saveUri = extras.getParcelable(MediaStore.EXTRA_OUTPUT);
		}

		sourceUri = intent.getData();

		mHelper = new CropHelp(this);
		mHelper.loadInput(sourceUri);
	}

	private void setResultException(Throwable throwable) {
		setResult(Crop.RESULT_ERROR,
				new Intent().putExtra(Crop.Extra.ERROR, throwable));
	}

	private void saveImage(Bitmap croppedImage) {
		if (croppedImage != null) {
			final Bitmap b = croppedImage;
			ProgressDialog dialog = ProgressDialog.show(this, null, "正在保存图片",
					true, false);
			CropUtil.startBackgroundJob(dialog, new Runnable() {
				public void run() {
					saveOutput(b);
				}
			});
		} else {
			finish();
		}
	}

	private void saveOutput(Bitmap croppedImage) {
		if (saveUri != null) {
			OutputStream outputStream = null;
			try {
				outputStream = getContentResolver().openOutputStream(saveUri);
				if (outputStream != null) {
					croppedImage.compress(Bitmap.CompressFormat.JPEG, 90,
							outputStream);
				}

			} catch (IOException e) {
				e.printStackTrace();
				setResultException(e);
			} finally {
				CropUtil.closeSilently(outputStream);
				if (croppedImage != null)
					croppedImage.recycle();
				croppedImage = null;
			}
			CropUtil.copyExifRotation(CropUtil.getFromMediaUri(this,
					getContentResolver(), sourceUri), CropUtil.getFromMediaUri(
					this, getContentResolver(), saveUri));

			setResultUri(saveUri);
		}
		finish();
	}

	private void setResultUri(Uri uri) {
		Log.e("setResultUri", "setResultUri  ");
		setResult(RESULT_OK,
				new Intent().putExtra(MediaStore.EXTRA_OUTPUT, uri));
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_actionbar_done:
			saveImage(mCropImage.getCroppedImage());
			break;
		case R.id.imv_actionbar_back:
			this.finish();
			break;
		default:
			break;
		}

	}
}
