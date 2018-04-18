package com.tozmart.toz_sdk.retrofit.uploadfile;

import com.tozmart.toz_sdk.retrofit.beans.UploadFileResultBean;

import io.reactivex.Flowable;
import okhttp3.MultipartBody;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by tracy on 17/10/19.
 */

public interface FileUploadService {
    @Multipart
    @POST("api/pub/upload")
    Flowable<UploadFileResultBean> upload(@Part MultipartBody.Part file);
}
