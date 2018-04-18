package com.tozmart.toz_sdk.retrofit.network;

import android.util.ArrayMap;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by wys on 17/5/13.
 * 定义retrofit的单例，结合Android的生命周期来管理订阅
 */

public class RetrofitManager {
    public class TYPE {
        public static final int NORMAL = 0;
        public static final int JSON = 1;
        public static final int FILE = 2;
    }

    private static ArrayMap<String, CompositeDisposable> netManager = new ArrayMap<>();

    public static Retrofit getInstance(Map<String,Object> params, String nativeBaseUrl, int paramsType) {
        return getRetrofit(params, nativeBaseUrl, paramsType);
    }

    public static Retrofit getInstance(Object params, String nativeBaseUrl) {
        return getRetrofit(params, nativeBaseUrl);
    }

    /**
     *
     * @param params 上传服务器的参数
     * @param nativeBaseUrl 从本地获取服务器地址
     * @param paramsType 上传参数的类型 0是普通参数；1是json；2是文件
     * @return
     */
    private static Retrofit getRetrofit(Map<String, Object> params, String nativeBaseUrl, int paramsType) {
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
        switch (paramsType){
            case TYPE.NORMAL:
                httpClientBuilder.addInterceptor(new HttpsParamsInterceptor(params));
                break;
            case TYPE.JSON:
                httpClientBuilder.addInterceptor(new HttpsJsonInterceptor(params));
                break;
            case TYPE.FILE:
                httpClientBuilder.addInterceptor(new HttpsFileInterceptor(params));
                break;
        }
//        httpClientBuilder.addInterceptor(new HttpLoggingInterceptor());
        OkHttpClient httpClient = httpClientBuilder.connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .client(httpClient)
                .baseUrl(nativeBaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        return retrofit;
    }

    private static Retrofit getRetrofit(Object params, String nativeBaseUrl) {
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
        httpClientBuilder.addInterceptor(new HttpsBeanJsonInterceptor(params));

//        httpClientBuilder.addInterceptor(new HttpLoggingInterceptor());
        OkHttpClient httpClient = httpClientBuilder.connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .client(httpClient)
                .baseUrl(nativeBaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        return retrofit;
    }

    //为了避免错误的取消了，key建议使用packagename + calssName
    public static void add(String key, Disposable disposable) {
        if (netManager.containsKey(key)) {
            netManager.get(key).add(disposable);
        } else {
            CompositeDisposable compositeDisposable = new CompositeDisposable();
            compositeDisposable.add(disposable);
            netManager.put(key, compositeDisposable);
        }
    }

    public static void remove(String key) {
        if (netManager.containsKey(key)) {
            CompositeDisposable compositeDisposable = netManager.get(key);
            compositeDisposable.clear();
        }
    }
}
