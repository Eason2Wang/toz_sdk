package com.tozmart.toz_sdk.retrofit.network;

import android.util.Base64;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
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

public class HttpsJsonInterceptor implements Interceptor {

    Map<String, Object> params;
    public HttpsJsonInterceptor(Map<String, Object> params){
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
                HttpUrl.Builder httpUrlBuilder = oldRequest.url().newBuilder();
                for (String key : params.keySet()) {
                    httpUrlBuilder.addQueryParameter(key, String.valueOf(params.get(key)));
                }
                newRequestBuilder.url(httpUrlBuilder.build());
            } else {
                //如果原请求是表单请求
                if (oldRequest.body() instanceof FormBody) {
                    FormBody oldFormBody = (FormBody) oldRequest.body();
                    int size = oldFormBody.size();
                    for (int i = 0; i < size; i++) {
                        params.put(oldFormBody.name(i), oldFormBody.value(i));
                    }
                }
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
