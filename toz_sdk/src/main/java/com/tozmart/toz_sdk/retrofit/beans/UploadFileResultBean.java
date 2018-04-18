
package com.tozmart.toz_sdk.retrofit.beans;

import android.os.Parcel;
import android.os.Parcelable;

public class UploadFileResultBean implements Parcelable {
    private NetworkResult result;

    private String data;

    public NetworkResult getResult() {
        return result;
    }

    public void setResult(NetworkResult result) {
        this.result = result;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.result, flags);
        dest.writeString(this.data);
    }

    public UploadFileResultBean() {
    }

    protected UploadFileResultBean(Parcel in) {
        this.result = in.readParcelable(NetworkResult.class.getClassLoader());
        this.data = in.readString();
    }

    public static final Creator<UploadFileResultBean> CREATOR = new Creator<UploadFileResultBean>() {
        @Override
        public UploadFileResultBean createFromParcel(Parcel source) {
            return new UploadFileResultBean(source);
        }

        @Override
        public UploadFileResultBean[] newArray(int size) {
            return new UploadFileResultBean[size];
        }
    };
}
