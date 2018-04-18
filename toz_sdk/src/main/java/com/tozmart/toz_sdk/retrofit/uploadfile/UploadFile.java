package com.tozmart.toz_sdk.retrofit.uploadfile;

import android.content.Context;

import com.tozmart.toz_sdk.TozSDK;
import com.tozmart.toz_sdk.retrofit.beans.UploadFileResultBean;
import com.tozmart.toz_sdk.retrofit.callback.LoadDataCallBack;
import com.tozmart.toz_sdk.retrofit.network.RetrofitManager;
import com.tozmart.toz_sdk.retrofit.network.RxSchedulers;

import java.io.File;

import io.reactivex.disposables.Disposable;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static com.tozmart.toz_sdk.contants.HttpsAuthString.IMEASURE_BASE_SERVER_URL;
import static com.tozmart.toz_sdk.contants.HttpsAuthString.YIHU_SERVER_URL;

/**
 * Created by tracy on 17/10/19.
 */

public class UploadFile {
    public static void upload(Context context, String filePath, boolean isYihu, final LoadDataCallBack callback){
        File file = new File(filePath);
        RequestBody requestFile =
                RequestBody.create(MediaType.parse("multipart/form-data"), file);

        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
        Disposable disposable;
        if (isYihu){
            disposable = RetrofitManager.getInstance(
                    null, YIHU_SERVER_URL, RetrofitManager.TYPE.FILE)
                    .create(YihuFileUploadService.class)
                    .upload(body)
                    .compose(RxSchedulers.<UploadFileResultBean>io_main(TozSDK.getContext()))
                    .subscribeWith(new UploadFileSubscriber() {
                        @Override public void handlerSuccess(UploadFileResultBean data) {
                            if (data != null) {
                                callback.onDataLoaded(data.getData());
                            } else {
                                callback.onDataLoadFailed();
                            }
                        }
                    });
        } else {
            disposable = RetrofitManager.getInstance(
                    null, IMEASURE_BASE_SERVER_URL, RetrofitManager.TYPE.FILE)
                    .create(FileUploadService.class)
                    .upload(body)
                    .compose(RxSchedulers.<UploadFileResultBean>io_main(TozSDK.getContext()))
                    .subscribeWith(new UploadFileSubscriber() {
                        @Override
                        public void handlerSuccess(UploadFileResultBean data) {
                            if (data != null) {
                                callback.onDataLoaded(data.getData());
                            } else {
                                callback.onDataLoadFailed();
                            }
                        }
                    });
        }
        String key = context.getPackageName() + "." + context.getClass().getSimpleName();
        RetrofitManager.add(key, disposable);
    }
}
