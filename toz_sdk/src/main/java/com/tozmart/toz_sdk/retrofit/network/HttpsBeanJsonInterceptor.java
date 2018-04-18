package com.tozmart.toz_sdk.retrofit.network;

import android.util.Base64;

import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.tozmart.toz_sdk.contants.HttpsAuthString.HTTPS_SERVER_PASSWORD;
import static com.tozmart.toz_sdk.contants.HttpsAuthString.HTTPS_SERVER_USER_NAME;

/**
 * Created by wys on 17/5/5.
 */

public class HttpsBeanJsonInterceptor implements Interceptor {

    Object params;
    public HttpsBeanJsonInterceptor(Object params){
        this.params = params;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request oldRequest = chain.request();
        //如果公共请求参数不为空,则构建新的请求
        if (params != null) {
            Request.Builder newRequestBuilder = oldRequest.newBuilder();
            newRequestBuilder
                    .header("Content-type", "application/json;charset=UTF-8")
                    .header("Accept", "application/json")
                    .header("Authorization", "Basic " +
                            Base64.encodeToString(
                                    (HTTPS_SERVER_USER_NAME
                                            + ":"
                                            + HTTPS_SERVER_PASSWORD).getBytes(), Base64.NO_WRAP))
                    .build();

            //GET请求则使用HttpUrl.Builder来构建
            if ("GET".equalsIgnoreCase(oldRequest.method())) {
            } else {
                Gson gson = new Gson();
                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                RequestBody body = RequestBody.create(JSON, gson.toJson(params));
                newRequestBuilder.post(body);
            }
            return chain.proceed(newRequestBuilder.build());
        }
        return chain.proceed(oldRequest);
    }
}
