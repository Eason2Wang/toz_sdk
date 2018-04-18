package com.tozmart.toz_sdk.retrofit.beans;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by wys on 17/5/13.
 */

public class NetworkResult implements Parcelable {
    @SerializedName("code")
    @Expose
    private int code;

    @SerializedName("codeDesc")
    @Expose
    private String codeDesc;

    @SerializedName("message")
    @Expose
    private String message;


    public int getCode(){
        return code;
    }

    public String getCodeDesc(){
        return codeDesc;
    }

    public String getMessage(){
        return message;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.code);
        dest.writeString(this.codeDesc);
        dest.writeString(this.message);
    }

    public NetworkResult() {
    }

    protected NetworkResult(Parcel in) {
        this.code = in.readInt();
        this.codeDesc = in.readString();
        this.message = in.readString();
    }

    public static final Creator<NetworkResult> CREATOR = new Creator<NetworkResult>() {
        @Override
        public NetworkResult createFromParcel(Parcel source) {
            return new NetworkResult(source);
        }

        @Override
        public NetworkResult[] newArray(int size) {
            return new NetworkResult[size];
        }
    };
}
