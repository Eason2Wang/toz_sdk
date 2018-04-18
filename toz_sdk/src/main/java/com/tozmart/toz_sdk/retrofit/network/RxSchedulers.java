package com.tozmart.toz_sdk.retrofit.network;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;

import com.tozmart.toz_sdk.event.NetworkMsgEvent;
import com.tozmart.toz_sdk.utils.NetStateUtils;

import org.greenrobot.eventbus.EventBus;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscription;

import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.tozmart.toz_sdk.retrofit.network.NetworkConstants.CONNECT_EXCEPTION;
import static com.tozmart.toz_sdk.retrofit.network.NetworkConstants.NET_CODE_CONNECT;

/**
 * Created by wys on 17/5/13.
 * 为了对请求进行预处理和简化每次都要写的线程步骤，定义了如下的一个类
 */

public class RxSchedulers {
    /**
     * 基本请求
     */
    public static <T> FlowableTransformer<T, T> io_main(final Context context) {
        return new FlowableTransformer<T, T>() {
            @Override public Publisher<T> apply(@NonNull Flowable<T> upstream) {
                return upstream
                        .subscribeOn(Schedulers.io())
                        .doOnSubscribe(new Consumer<Subscription>() {
                            @Override
                            public void accept(@NonNull Subscription subscription) throws Exception {
                                //如果无网络连接，则直接取消了
                                if (!NetStateUtils.isNetworkConnected(context)) {
                                    subscription.cancel();
                                    NetworkMsgEvent msgEvent =
                                            new NetworkMsgEvent(NET_CODE_CONNECT, context.getString(CONNECT_EXCEPTION));
                                    EventBus.getDefault().post(msgEvent);
                                }
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

    /**
     * 带进度条的请求
     */
    public static <T> FlowableTransformer<T, T> io_main(final Context context, final ProgressDialog dialog) {
        return new FlowableTransformer<T, T>() {
            @Override public Publisher<T> apply(@NonNull Flowable<T> upstream) {
                return upstream
                        //为了让进度条保持一会儿做了个延时
                        .delay(1, TimeUnit.SECONDS)
                        .subscribeOn(Schedulers.io())
                        .doOnSubscribe(new Consumer<Subscription>() {
                            @Override
                            public void accept(@NonNull final Subscription subscription) throws Exception {
                                if (!NetStateUtils.isNetworkConnected(context)) {
                                    subscription.cancel();
                                    NetworkMsgEvent msgEvent =
                                            new NetworkMsgEvent(NET_CODE_CONNECT, context.getString(CONNECT_EXCEPTION));
                                    EventBus.getDefault().post(msgEvent);
                                } else {
                                    //启动进度显示，取消进度时会取消请求
                                    if (dialog != null) {
                                        dialog.setCanceledOnTouchOutside(false);
                                        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                            @Override public void onCancel(DialogInterface dialog) {
                                                subscription.cancel();
                                            }
                                        });
                                        dialog.show();
                                    }
                                }
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }
}
