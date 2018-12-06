package com.soloalien.ninepicker;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.soloalien.ninepicker.entity.ImageItem;
import com.soloalien.ninepicker.preview.PreviewActivity;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.filter.Filter;
import com.zhihu.matisse.internal.entity.CaptureStrategy;
import com.zhihu.matisse.internal.ui.widget.MediaGridInset;
import com.zhihu.matisse.listener.OnCheckedListener;
import com.zhihu.matisse.listener.OnSelectedListener;
import com.zhihu.matisse.ninepicker.R;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_CHOOSE = 23;
    private static final int REQUEST_PREVIEW = 20;
    private RecyclerView recyclerView;
    private GridLayoutManager gridLayoutManager;
    private GridAdapter gridAdapter;
    private ItemTouchHelper mItemTouchHelper;

    private ArrayList<ImageItem> tempList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        initData();
    }

    private void initView() {
        recyclerView = findViewById(R.id.recycler);
        gridLayoutManager = new GridLayoutManager(this, 4);
        recyclerView.setLayoutManager(gridLayoutManager);
        int spacing = getResources().getDimensionPixelSize(R.dimen.media_grid_spacing);
        recyclerView.addItemDecoration(new MediaGridInset(4, spacing, false));//添加分割线
        gridAdapter = new GridAdapter(this, recyclerView);
        recyclerView.setAdapter(gridAdapter);
        recyclerView.getItemAnimator().setChangeDuration(0);
    }

    private void initData() {
        gridAdapter.setPlusEnable(true);
        gridAdapter.setMaxItemCount(12);
        mItemTouchHelper = new ItemTouchHelper(new ItemDragCallback(gridAdapter.getArrayList(), gridAdapter));
        mItemTouchHelper.attachToRecyclerView(recyclerView);

        gridAdapter.setItemClickListener(new ItemClickListener() {
            @Override
            public void OnItemClickListener(View view, int position) {
                if (gridAdapter.isPlusItem(position)) {
                    RxPermissions rxPermissions = new RxPermissions(MainActivity.this);
                    rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            .subscribe(new Observer<Boolean>() {

                                @Override
                                public void onSubscribe(Disposable d) {

                                }

                                @Override
                                public void onNext(Boolean aBoolean) {
                                    Matisse.from(MainActivity.this)
                                            .choose(MimeType.ofImage(), false)//只筛选图片
                                            .theme(R.style.Matisse_Customer)
                                            .countable(true)//是否显示数字
                                            .capture(true)//是否可以拍照
                                            .captureStrategy(//拍照策略
                                                    new CaptureStrategy(true, "com.zhihu.matisse.sample.fileprovider", "test"))
                                            .maxSelectable(gridAdapter.getOptionalSize())//最大选择数量
                                            .addFilter(new GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))//自定义过滤器
                                            .gridExpectedSize(//设置列宽
                                                    getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
                                            .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)//设置屏幕方向
                                            .thumbnailScale(0.85f)//图片缩放比例
//                                            .imageEngine(new GlideEngine())  // for glide-V3
                                            .imageEngine(new Glide4Engine())    // for glide-V4
                                            .setOnSelectedListener(new OnSelectedListener() {//选中监听
                                                @Override
                                                public void onSelected(
                                                        @NonNull List<Uri> uriList, @NonNull List<String> pathList) {
                                                    // DO SOMETHING IMMEDIATELY HERE
                                                    Log.e("onSelected", "onSelected: pathList=" + pathList);

                                                }
                                            })
                                            .originalEnable(false)
                                            .maxOriginalSize(10)
                                            .autoHideToolbarOnSingleTap(true)
                                            .setOnCheckedListener(new OnCheckedListener() {
                                                @Override
                                                public void onCheck(boolean isChecked) {
                                                    // DO SOMETHING IMMEDIATELY HERE
                                                    Log.e("isChecked", "onCheck: isChecked=" + isChecked);
                                                }
                                            })
                                            .forResult(REQUEST_CODE_CHOOSE);
                                }

                                @Override
                                public void onError(Throwable e) {

                                }

                                @Override
                                public void onComplete() {

                                }
                            });
                } else {
                    Intent intent = new Intent(MainActivity.this, PreviewActivity.class);
                    intent.putParcelableArrayListExtra(PreviewActivity.LIST_EXTRA, gridAdapter.getArrayList());
                    for (ImageItem item : gridAdapter.getArrayList()) {
                        Log.e("TAG", "OnItemClickListener: " + item.getIndex());
                    }
                    intent.putExtra(PreviewActivity.CURRENT_ITEM, position);
                    startActivityForResult(intent, REQUEST_PREVIEW);
                }
                Toast.makeText(MainActivity.this, "click " + position + "opt:" + gridAdapter.getOptionalSize() + " total:" + gridAdapter.getItemCount(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void OnItemCancelClickListener(View view, int position) {
                gridAdapter.removeData(position);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_CHOOSE) {
                ArrayList<ImageItem> items = new ArrayList<>();
                List<String> paths = Matisse.obtainPathResult(data);
                for (int i = 0; i < paths.size(); i++) {
                    ImageItem item = new ImageItem(true, paths.get(i));
                    items.add(item);
                }
                gridAdapter.addMultiData(items);
                tempList.clear();
                tempList.addAll(gridAdapter.getArrayList());
            }
            if (requestCode == REQUEST_PREVIEW) {
                ArrayList<ImageItem> delList = data.getParcelableArrayListExtra(PreviewActivity.DEL_LIST);
                if (null == delList) {
                    return;
                }
                for (ImageItem delItem : delList) {
                    for (ImageItem item : tempList) {
                        if (delItem.getIndex().equals(item.getIndex())) {
                            gridAdapter.getArrayList().remove(item);
                        }
                    }
                }
                gridAdapter.notifyDataSetChanged();
            }
        }
    }
}
