package com.tozmart.toz_sdk.retrofit.uploadfile;

/**
 * Created by wys on 17/5/13.
 */

public class UploadFileNetworkMsgEvent {
    private int code;

    private String message;

    public UploadFileNetworkMsgEvent(int code, String message){
        this.code = code;
        this.message = message;
    }

    public int getCode(){
        return code;
    }

    public void setCode(int code){
        this.code = code;
    }

    public String getMessage(){
        return message;
    }

    public void setMessage(String message){
        this.message = message;
    }
}
