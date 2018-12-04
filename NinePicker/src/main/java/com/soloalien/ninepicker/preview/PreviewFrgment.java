package com.soloalien.ninepicker.preview;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.soloalien.ninepicker.entity.ImageItem;
import com.soloalien.ninepicker.widget.CheckChooseView;
import com.zhihu.matisse.listener.OnFragmentInteractionListener;
import com.zhihu.matisse.ninepicker.R;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;
import it.sephiroth.android.library.imagezoom.ImageViewTouchBase;

public class PreviewFrgment extends Fragment {
    private static final String ITEM="item";
    private OnFragmentInteractionListener mListener;
    private OnChooseChangeListener changeListener;

    public static PreviewFrgment newInstance(ImageItem img) {
        PreviewFrgment fragment = new PreviewFrgment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ITEM, img);
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_preview, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final ImageItem img=getArguments().getParcelable(ITEM);
        if (null==img){
            return;
        }
        //将播放按钮设置为隐藏
        view.findViewById(com.zhihu.matisse.R.id.video_play_button).setVisibility(View.GONE);

        ImageViewTouch image = (ImageViewTouch) view.findViewById(R.id.image_view);
        image.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);

        image.setSingleTapListener(new ImageViewTouch.OnImageViewTouchSingleTapListener() {
            @Override
            public void onSingleTapConfirmed() {
                if (mListener != null) {
                    mListener.onClick();
                }
            }
        });
        CheckChooseView chooseView=view.findViewById(R.id.chooseView);
        if (img.isChoosen()){
            chooseView.setChecked(true);
        }else chooseView.setChecked(false);
        chooseView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (changeListener!=null) {
                    changeListener.onChange(view,img);
                }
            }
        });
        Glide.with(getContext()).load(img.getImgPath()).into(image);
        //TODO  应该由创建一个图片对象，由其传入具体的size和图片地址，这里暂时设置为固定值
//        SelectionSpec.getInstance().imageEngine.loadImage(getContext(), 300, 800, image,
//                resId);
    }

    public void resetView() {
        if (getView() != null) {
            ((ImageViewTouch) getView().findViewById(com.zhihu.matisse.R.id.image_view)).resetMatrix();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
        if (context instanceof OnChooseChangeListener){
            changeListener= (OnChooseChangeListener) context;
        }else {
            throw new RuntimeException(context.toString()+"must implement OnChooseChangeListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        changeListener=null;
    }
}
