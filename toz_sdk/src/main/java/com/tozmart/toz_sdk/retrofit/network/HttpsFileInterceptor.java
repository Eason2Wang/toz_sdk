package com.tozmart.toz_sdk.retrofit.network;

import android.util.Base64;

import java.io.IOException;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import static com.tozmart.toz_sdk.contants.HttpsAuthString.HTTPS_SERVER_PASSWORD;
import static com.tozmart.toz_sdk.contants.HttpsAuthString.HTTPS_SERVER_USER_NAME;

/**
 * Created by wys on 17/5/5.
 */

public class HttpsFileInterceptor implements Interceptor {

    Map<String, Object> params;
    public HttpsFileInterceptor(Map<String, Object> params){
        this.params = params;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request oldRequest = chain.request();
        Request.Builder builder = oldRequest.newBuilder();
        builder.addHeader("Content-type", "multipart/form-data");
        builder.addHeader("Accept", "application/json");
        builder.addHeader("Authorization", "Basic " +
                Base64.encodeToString(
                        (HTTPS_SERVER_USER_NAME
                                + ":"
                                + HTTPS_SERVER_PASSWORD).getBytes(), Base64.NO_WRAP));

        Request.Builder requestBuilder = builder.method(oldRequest.method(), oldRequest.body());
        return chain.proceed(requestBuilder.build());
    }
}
