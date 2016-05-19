package org.cy.photo;

import java.io.File;

import org.cy.photo.crop.Crop;
import org.cy.photo.crop.CropHelp;
import org.cy.photo.utils.PhotoSelecterUtils;
import org.cy.photo.utils.KEY;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends Activity {

	ImageView imageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		imageView = (ImageView) findViewById(R.id.imageView1);
	}

	public void onSingle(View v) {
		PhotoSelecterUtils.startSelecter(this, true);

	}

	public void onClick(View v) {
		PhotoSelecterUtils.startSelecter(this, false);
	}

	private void beginCrop(Uri source) {
		Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped"));
		Crop.of(source, destination).start(this);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {

			if (requestCode == PhotoSelecterUtils.PHOTO_RESULT) {
				boolean isSingle = data.getBooleanExtra(KEY.IS_SINGLE, false);
				if (isSingle) {
					String path = PhotoSelecterUtils.getPhotoReult(data);
					beginCrop(Uri.fromFile(new File(path)));
				}
			} else if (requestCode == Crop.REQUEST_CROP) {
				Log.e("", "dddddd" + requestCode);
				CropHelp mHelper = new CropHelp(this);
				Uri uri = Crop.getOutput(data);
				mHelper.loadInput(uri);
				// imageView.setImageURI(uri);
				imageView.setImageBitmap(mHelper.getBitmap());
			}
		}
	}
}
