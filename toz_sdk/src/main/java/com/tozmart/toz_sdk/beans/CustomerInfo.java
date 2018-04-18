package com.tozmart.toz_sdk.beans;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by tracy on 17/11/15.
 */

public class CustomerInfo implements Parcelable{
    private String userName;
    private String userGender;
    private float userHeight;
    private float userWeight;
    private String guestPersonId;
    private boolean isGuest;
    private String iconPath;//记录的头像照片url
    private int frontOffsetX;
    private int sideOffsetX;
    private boolean isSelectFromGallery;

    // 用户照片调整确认的最终轮廓点
    private String frontPointsStr;
    private String sidePointsStr;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserGender() {
        return userGender;
    }

    public void setUserGender(String userGender) {
        this.userGender = userGender;
    }

    public float getUserHeight() {
        return userHeight;
    }

    public void setUserHeight(float userHeight) {
        this.userHeight = userHeight;
    }

    public float getUserWeight() {
        return userWeight;
    }

    public void setUserWeight(float userWeight) {
        this.userWeight = userWeight;
    }

    public void setGuestPersonId(String guestPersonId) {
        this.guestPersonId = guestPersonId;
    }

    public String getGuestPersonId() {
        return guestPersonId;
    }

    public void setIsGuest(boolean isGuest) {
        this.isGuest = isGuest;
    }

    public boolean getIsGuest() {
        return isGuest;
    }

    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
    }

    public String getIconPath() {
        return iconPath;
    }

    public boolean isSelectFromGallery() {
        return isSelectFromGallery;
    }

    public void setSelectFromGallery(boolean selectFromGallery) {
        isSelectFromGallery = selectFromGallery;
    }

    public String getFrontPointsStr() {
        return frontPointsStr;
    }

    public void setFrontPointsStr(String frontPointsStr) {
        this.frontPointsStr = frontPointsStr;
    }

    public String getSidePointsStr() {
        return sidePointsStr;
    }

    public void setSidePointsStr(String sidePointsStr) {
        this.sidePointsStr = sidePointsStr;
    }

    public int getFrontOffsetX() {
        return frontOffsetX;
    }

    public void setFrontOffsetX(int frontOffsetX) {
        this.frontOffsetX = frontOffsetX;
    }

    public int getSideOffsetX() {
        return sideOffsetX;
    }

    public void setSideOffsetX(int sideOffsetX) {
        this.sideOffsetX = sideOffsetX;
    }

    public CustomerInfo() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.userName);
        dest.writeString(this.userGender);
        dest.writeFloat(this.userHeight);
        dest.writeFloat(this.userWeight);
        dest.writeString(this.guestPersonId);
        dest.writeByte(this.isGuest ? (byte) 1 : (byte) 0);
        dest.writeString(this.iconPath);
        dest.writeInt(this.frontOffsetX);
        dest.writeInt(this.sideOffsetX);
        dest.writeByte(this.isSelectFromGallery ? (byte) 1 : (byte) 0);
        dest.writeString(this.frontPointsStr);
        dest.writeString(this.sidePointsStr);
    }

    protected CustomerInfo(Parcel in) {
        this.userName = in.readString();
        this.userGender = in.readString();
        this.userHeight = in.readFloat();
        this.userWeight = in.readFloat();
        this.guestPersonId = in.readString();
        this.isGuest = in.readByte() != 0;
        this.iconPath = in.readString();
        this.frontOffsetX = in.readInt();
        this.sideOffsetX = in.readInt();
        this.isSelectFromGallery = in.readByte() != 0;
        this.frontPointsStr = in.readString();
        this.sidePointsStr = in.readString();
    }

    public static final Creator<CustomerInfo> CREATOR = new Creator<CustomerInfo>() {
        @Override
        public CustomerInfo createFromParcel(Parcel source) {
            return new CustomerInfo(source);
        }

        @Override
        public CustomerInfo[] newArray(int size) {
            return new CustomerInfo[size];
        }
    };
}
