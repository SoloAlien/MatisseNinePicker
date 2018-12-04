package com.soloalien.ninepicker.preview;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.soloalien.ninepicker.entity.ImageItem;
import com.soloalien.ninepicker.widget.CheckChooseView;
import com.zhihu.matisse.internal.utils.Platform;
import com.zhihu.matisse.listener.OnFragmentInteractionListener;
import com.zhihu.matisse.ninepicker.R;

import java.util.ArrayList;

public class PreviewActivity extends AppCompatActivity implements View.OnClickListener, ViewPager.OnPageChangeListener, OnFragmentInteractionListener, OnChooseChangeListener {
    public static final String LIST_EXTRA = "list";
    public static final String CURRENT_ITEM = "currentItem";
    public static final String DEL_LIST = "dellist";

    protected ViewPager mPager;

    protected PreviewAdapter mAdapter;

    protected TextView mButtonBack;
    protected TextView mButtonApply;

    private FrameLayout mBottomToolbar;

    private boolean mIsToolbarHide = false;

    private ArrayList<ImageItem> list;

    private ArrayList<ImageItem> delList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        //加入此属性，使得图片全屏，且状态栏透明
        if (Platform.hasKitKat()) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        mPager = findViewById(R.id.pager);
        mPager.addOnPageChangeListener(this);
        mAdapter = new PreviewAdapter(getSupportFragmentManager(), null);
        list = getIntent().getParcelableArrayListExtra(LIST_EXTRA);
        list.remove(list.get(list.size()-1));
        for (ImageItem item:list){
            Log.e("TAG", "onCreate: "+item.getIndex() );
        }
        mAdapter.addAll(list);
        mPager.setAdapter(mAdapter);

        int position = getIntent().getIntExtra(CURRENT_ITEM, 0);
        mPager.setCurrentItem(position, false);

        mBottomToolbar = findViewById(R.id.bottom_toolbar);
        mButtonBack = findViewById(R.id.button_back);
        mButtonApply = findViewById(R.id.button_apply);
        initListener();

    }

    private void initListener() {
        mBottomToolbar.setOnClickListener(this);
        mButtonBack.setOnClickListener(this);
        mButtonApply.setOnClickListener(this);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.button_back) {
            onBackPressed();
        } else if (view.getId() == R.id.button_apply) {
            sendBackResult();
            finish();
        }
    }

    @Override
    public void onClick() {
        if (mIsToolbarHide) {
            mBottomToolbar.animate()
                    .translationYBy(-mBottomToolbar.getMeasuredHeight())
                    .setInterpolator(new FastOutSlowInInterpolator())
                    .start();
        } else {
            mBottomToolbar.animate()
                    .setInterpolator(new FastOutSlowInInterpolator())
                    .translationYBy(mBottomToolbar.getMeasuredHeight())
                    .start();
        }
        mIsToolbarHide = !mIsToolbarHide;
    }

    protected void sendBackResult() {
        Intent intent = new Intent();
        intent.putParcelableArrayListExtra(DEL_LIST,delList);
        setResult(Activity.RESULT_OK, intent);
    }

    @Override
    public void onChange(View view, ImageItem item) {
        if (item.isChoosen()) {
            ((CheckChooseView) view).setChecked(false);
            item.setChoosen(false);
            delList.add(item);
        } else {
            if (delList.contains(item)) {
                ((CheckChooseView) view).setChecked(true);
                item.setChoosen(true);
                delList.remove(item);
            }
        }
        updateApplyButton();
    }

    private void updateApplyButton() {
        if (delList.size() == 0) {
            mButtonApply.setText(R.string.button_apply_default);
            mButtonApply.setEnabled(true);
        } else if (delList.size() > 0) {
            mButtonApply.setText(getString(R.string.button_apply, delList.size()));
            mButtonApply.setEnabled(true);
        }
    }
}
