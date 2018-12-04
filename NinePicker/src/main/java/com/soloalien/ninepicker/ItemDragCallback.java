package com.soloalien.ninepicker;

import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;

import com.soloalien.ninepicker.entity.ImageItem;

import java.util.Collections;
import java.util.List;

public class ItemDragCallback extends ItemTouchHelper.Callback {
    private List<ImageItem> arrayList;
    private GridAdapter gridAdapter;

    public ItemDragCallback(List<ImageItem> arrayList, GridAdapter gridAdapter) {
        this.arrayList = arrayList;
        this.gridAdapter = gridAdapter;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {

        if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
            final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN |
                    ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
            final int swipeFlags = 0;
            return makeMovementFlags(dragFlags, swipeFlags);
        } else {
            final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            final int swipeFlags = 0;
            return makeMovementFlags(dragFlags, swipeFlags);
        }
//        //判断recyclerview的layoutManager的类型，根据不同的类型设置不同的滑动方向和拖拽方向
//        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
//            //拖拽方向
//            int dragFlag;
//            //滑动方向
//            int swapeFlag;
//            //得到linearLayoutManager的列表方向
//            int orientation = ((LinearLayoutManager) recyclerView.getLayoutManager())
//                    .getOrientation();
//            //如果列表方向是竖向的
//            if (orientation == LinearLayoutManager.VERTICAL) {
//                //拖拽方向设置为上或者下
//                dragFlag = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
//                //滑动方向设置为左或者右
//                swapeFlag = 0;
//            } else {
//                //拖拽方向设置为左或者右
//                dragFlag = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
//                //滑动方向设置为上或者下
//                swapeFlag = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
//            }
//            //最后通过makeMovementFlags返回拖拽和滑动方向
//            return makeMovementFlags(dragFlag, swapeFlag);
//        } else {
//            //因为gridLayoutManager上下左右都可以拖拽，所以设置拖拽方向为四种，滑动方向为0
//            int dragFlag = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT |
//                    ItemTouchHelper.RIGHT;
//            int swapeFlag = 0;
//            return makeMovementFlags(dragFlag, swapeFlag);
//        }
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        //得到需要拖拽item的位置
        int fromPosition = viewHolder.getAdapterPosition();
        Log.e("TAG", "onMove: " + fromPosition);
        //得到item拖拽到目标的位置
        int toPosition = target.getAdapterPosition();
        //判断是否是选择状态
        if (gridAdapter.isPlusEnable()) {
            if (gridAdapter.isPlusItem(fromPosition)){//判断点中的条目是否是加号
                return false;
            }
            if (fromPosition > toPosition) {
                //如果是从下往上拖拽的就依次将需要拖拽的item的位置和目标的位置（拖拽item的位置-1）交换
                for (int i = fromPosition; i > toPosition; i--) {
                    Collections.swap(arrayList, i, i - 1);
                }

            } else {
                //如果是从上往下拖拽的就依次将需要拖拽的item的位置和目标的位置（拖拽item的位置+1）交换
                for (int i = fromPosition; i < toPosition; i++) {
                    Collections.swap(arrayList, i, i + 1);
                }
            }
        }else return false;
        //最后通过适配器将item移动
        gridAdapter.notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return false;
    }
}
