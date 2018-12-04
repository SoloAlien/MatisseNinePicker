package com.soloalien.ninepicker;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.soloalien.ninepicker.entity.ImageItem;
import com.zhihu.matisse.ninepicker.R;

import java.util.ArrayList;

public class GridAdapter extends RecyclerView.Adapter<GridAdapter.GridViewHolder> {
    private ItemClickListener itemClickListener;
    private ArrayList<ImageItem> arrayList = new ArrayList<>();
    private int mImageResize;
    private Context context;
    private RecyclerView recyclerView;
    private ImageItem plusItem;

    /**
     * 最大可选图片数量
     */
    private int maxItemCount;
    /**
     * 是否显示加号
     */
    private boolean plusEnable;

    public GridAdapter(Context context, RecyclerView recyclerView) {
        this.context = context;
        this.recyclerView = recyclerView;
        initPlusItem();
    }

    private void initPlusItem() {
        plusItem = new ImageItem(false, getResourcesUri(R.drawable.bga_pp_ic_plus));
        arrayList.add(plusItem);
    }

    @NonNull
    @Override
    public GridViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new GridViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.griditem, viewGroup, false));
    }

    /**
     * 绑定数据项，并且根据控件尺寸设置图片
     *
     * @param gridViewHolder
     * @param i
     */
    @Override
    public void onBindViewHolder(@NonNull GridViewHolder gridViewHolder, int i) {
        if (!plusEnable) {//如果不是添加模式
            gridViewHolder.cancleView.setVisibility(View.GONE);
        } else {
            gridViewHolder.itemView.setVisibility(View.VISIBLE);
            if (i >= getMaxItemCount()) {//如果条目数量大于等于最大条目数量
                Log.e("TAG", "id:" + i);
                gridViewHolder.itemView.setVisibility(View.GONE);//隐藏加号
            }
            if (isPlusItem(i)) {//如果是加号，那么隐藏掉删除按钮
                Log.e("TAG", "isPlus:" + i + "    " + isPlusItem(i));
                gridViewHolder.cancleView.setVisibility(View.GONE);
            } else {
                gridViewHolder.cancleView.setVisibility(View.VISIBLE);
            }

        }
        RequestOptions options = new RequestOptions()
                .override(getImageResize(context), getImageResize(context))
                .centerCrop();
        Glide.with(context).load(arrayList.get(i).getImgPath()).apply(options).into(gridViewHolder.imageView);
    }

    @Override
    public int getItemCount() {
        return null == arrayList ? 0 : arrayList.size();
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    /**
     * 添加多张图片，并根据是选择状态判断是否添加加号图片
     *
     * @param imgs
     */
    public void addMultiData(ArrayList<ImageItem> imgs) {
        arrayList.addAll(arrayList.size() - 1, imgs);
        notifyDataSetChanged();
    }

    public ArrayList<ImageItem> getArrayList() {
        return arrayList;
    }

    /**
     * 删除图片
     *
     * @param position
     */
    public void removeData(int position) {
        if (isPlusEnable()) {
            arrayList.remove(position);
            notifyItemRemoved(position);
            notifyItemChanged(getItemCount()-1);
        }
    }


    /**
     * 测量图片控件的大小
     *
     * @param context
     * @return
     */
    private int getImageResize(Context context) {
        if (mImageResize == 0) {
            RecyclerView.LayoutManager lm = recyclerView.getLayoutManager();
            int spanCount = ((GridLayoutManager) lm).getSpanCount();
            int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
            int availableWidth = screenWidth - context.getResources().getDimensionPixelSize(
                    R.dimen.media_grid_spacing) * (spanCount - 1);
            mImageResize = availableWidth / spanCount;
            mImageResize = (int) (mImageResize * 0.5f);
        }
        return mImageResize;
    }

    public boolean isPlusEnable() {
        return plusEnable;
    }

    public void setPlusEnable(boolean plusEnable) {
        this.plusEnable = plusEnable;
    }

    public int getMaxItemCount() {
        return maxItemCount;
    }

    public void setMaxItemCount(int maxItemCount) {
        this.maxItemCount = maxItemCount;
    }

    /**
     * 根据位置判断是否是加号
     *
     * @param position
     * @return
     */
    public boolean isPlusItem(int position) {
        return isPlusEnable() && getItemCount() <= getMaxItemCount() && position == getItemCount()-1;
    }


    public String getResourcesUri(@DrawableRes int id) {
        Resources resources = context.getResources();
        String uriPath = ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                resources.getResourcePackageName(id) + "/" +
                resources.getResourceTypeName(id) + "/" +
                resources.getResourceEntryName(id);
        return uriPath;
    }


    /**
     * 剩余可选图片数量
     *
     * @return
     */
    public int getOptionalSize() {
        return getMaxItemCount() - (getItemCount() - 1);
    }

    class GridViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView, cancleView;

        public GridViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.img);
            cancleView = itemView.findViewById(R.id.cancelview);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null == itemClickListener) {
                        throw new NullPointerException("点击事件未设置");
                    } else {
                        itemClickListener.OnItemClickListener(v, getLayoutPosition());
                    }
                }
            });

            cancleView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null == itemClickListener) {
                        throw new NullPointerException("点击事件未设置");
                    } else {
                        itemClickListener.OnItemCancelClickListener(v, getLayoutPosition());
                    }
                }
            });
        }
    }
}

