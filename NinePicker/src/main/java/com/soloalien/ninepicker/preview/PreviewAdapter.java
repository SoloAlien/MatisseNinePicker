package com.soloalien.ninepicker.preview;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;


import com.soloalien.ninepicker.entity.ImageItem;

import java.util.ArrayList;
import java.util.List;

public class PreviewAdapter extends FragmentPagerAdapter {

    private ArrayList<ImageItem> mItems = new ArrayList<>();
    private OnPrimaryItemSetListener mListener;

    public PreviewAdapter(FragmentManager fm,OnPrimaryItemSetListener listener) {
        super(fm);
        mListener=listener;
    }

    @Override
    public Fragment getItem(int i) {
        return PreviewFrgment.newInstance(mItems.get(i));
    }

    @Override
    public int getCount() {
        return null==mItems?0:mItems.size();
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
        if (mListener != null) {
            mListener.onPrimaryItemSet(position);
        }
    }


    public void addAll(List<ImageItem> items) {
        mItems.addAll(items);
    }

    interface OnPrimaryItemSetListener {

        void onPrimaryItemSet(int position);
    }
}
