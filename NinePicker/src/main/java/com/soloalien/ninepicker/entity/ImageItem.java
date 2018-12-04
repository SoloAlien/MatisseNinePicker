package com.soloalien.ninepicker.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.UUID;

public class ImageItem implements Parcelable {
    private boolean choosen;
    private String imgPath;
    private String index;

    public ImageItem(boolean choosen, String imgPath) {
        this(choosen,imgPath,UUID.randomUUID().toString());
    }

    public ImageItem(boolean choosen, String imgPath, String index) {
        this.choosen = choosen;
        this.imgPath = imgPath;
        this.index = index;
    }

    public boolean isChoosen() {
        return choosen;
    }

    public void setChoosen(boolean choosen) {
        this.choosen = choosen;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public String getIndex() {
        return index;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.choosen ? (byte) 1 : (byte) 0);
        dest.writeString(this.imgPath);
        dest.writeString(this.index);
    }

    protected ImageItem(Parcel in) {
        this.choosen = in.readByte() != 0;
        this.imgPath = in.readString();
        this.index = in.readString();
    }

    public static final Parcelable.Creator<ImageItem> CREATOR = new Parcelable.Creator<ImageItem>() {
        @Override
        public ImageItem createFromParcel(Parcel source) {
            return new ImageItem(source);
        }

        @Override
        public ImageItem[] newArray(int size) {
            return new ImageItem[size];
        }
    };
}
