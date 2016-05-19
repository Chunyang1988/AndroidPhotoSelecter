package org.cy.photo;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.polites.GestureImageView;

public class PreviewFragment extends Fragment {

	GestureImageView imageView;
	ProgressBar progress;

	public static PreviewFragment newFragment(String path) {
		PreviewFragment f = new PreviewFragment();
		Bundle bundle = new Bundle();
		bundle.putString("photo", path);
		// bundle.putParcelable("photo", photo);
		f.setArguments(bundle);
		return f;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_preview, container, false);
		imageView = (GestureImageView) v.findViewById(R.id.gimv_photo_preview);
		imageView.setVisibility(View.GONE);
		imageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				PhotoPreviewActivity pa = (PhotoPreviewActivity) getActivity();
				pa.changeActionbar();
			}
		});
		progress = (ProgressBar) v.findViewById(R.id.pro_photo_preview);
		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initData();
	}

	private void initData() {
		String path = getArguments().getString("photo");
		Glide.with(getContext()).load(path).asBitmap()
				.into(new SimpleTarget<Bitmap>() {

					@Override
					public void onResourceReady(Bitmap arg0,
							GlideAnimation<? super Bitmap> arg1) {
						imageView.setVisibility(View.VISIBLE);
						imageView.setImageBitmap(arg0);
						progress.setVisibility(View.GONE);
					}
				});
	}
}
