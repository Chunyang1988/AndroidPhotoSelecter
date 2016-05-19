package org.cy.photo.adpater;

import java.util.ArrayList;

import org.cy.photo.PreviewFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class PreviewFragmentPagerAdpater extends FragmentStatePagerAdapter {
	ArrayList<String> list;

	public PreviewFragmentPagerAdpater(FragmentManager fm,
			ArrayList<String> list) {
		super(fm);
		this.list = list;
	}

	public ArrayList<String> getList() {
		return list;
	}

	@Override
	public Fragment getItem(int arg0) {
		return PreviewFragment.newFragment(list.get(arg0));
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}
}
