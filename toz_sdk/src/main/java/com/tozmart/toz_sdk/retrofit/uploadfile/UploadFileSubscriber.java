package com.tozmart.toz_sdk.retrofit.uploadfile;

import com.tozmart.toz_sdk.TozSDK;
import com.tozmart.toz_sdk.retrofit.beans.UploadFileResultBean;
import com.tozmart.toz_sdk.retrofit.network.NetworkConstants;
import com.tozmart.toz_sdk.utils.NetStateUtils;

import org.greenrobot.eventbus.EventBus;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import io.reactivex.subscribers.DisposableSubscriber;

/**
 * Created by wys on 17/5/13.
 */

public abstract class UploadFileSubscriber extends DisposableSubscriber<UploadFileResultBean> {
    @Override
    public void onStart() {
        super.onStart();
        if (!NetStateUtils.isNetworkConnected(TozSDK.getContext())){
            EventBus.getDefault().post(new UploadFileNetworkMsgEvent(NetworkConstants.NET_CODE_CONNECT,
                    TozSDK.getContext().getString(NetworkConstants.CONNECT_EXCEPTION)));
            onComplete();
            return;
        }
    }
    @Override public void onNext(UploadFileResultBean response) {
        //业务代码为成功则将具体的数据返回，否则利用EventBus将错误发出去
        if (response.getResult().getCode() == NetworkConstants.NET_CODE_SUCCESS) {
            handlerSuccess(response);
        } else {
            EventBus.getDefault().post(new UploadFileNetworkMsgEvent(response.getResult().getCode(),
                    response.getResult().getMessage()));
        }
    }

    @Override public void onError(Throwable t) {
        UploadFileNetworkMsgEvent msgEvent;
        //处理常见的几种连接错误
        if (t instanceof SocketTimeoutException) {
            msgEvent = new UploadFileNetworkMsgEvent(NetworkConstants.NET_CODE_SOCKET_TIMEOUT,
                    TozSDK.getContext().getString(NetworkConstants.SOCKET_TIMEOUT_EXCEPTION));
        } else if (t instanceof ConnectException) {
            msgEvent = new UploadFileNetworkMsgEvent(NetworkConstants.NET_CODE_CONNECT,
                    TozSDK.getContext().getString(NetworkConstants.CONNECT_EXCEPTION));
        } else if (t instanceof UnknownHostException) {
            msgEvent = new UploadFileNetworkMsgEvent(NetworkConstants.NET_CODE_UNKNOWN_HOST,
                    TozSDK.getContext().getString(NetworkConstants.UNKNOWN_HOST_EXCEPTION));
        } else {
            msgEvent = new UploadFileNetworkMsgEvent(NetworkConstants.NET_CODE_ERROR, t.getMessage());
        }
        EventBus.getDefault().post(msgEvent);
    }

    @Override public void onComplete() {

    }

    //请求成功返回结果
    public abstract void handlerSuccess(UploadFileResultBean t);
}
