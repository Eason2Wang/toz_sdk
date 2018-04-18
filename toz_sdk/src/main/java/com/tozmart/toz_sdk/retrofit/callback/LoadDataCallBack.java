package com.tozmart.toz_sdk.retrofit.callback;

import java.util.List;

/**
 * Created by wys on 17/5/13.
 */

public interface LoadDataCallBack<T> {
    void onDataListLoaded(List<T> infoList);

    void onDataLoaded(T info);

    void onDataLoadFailed();

    void onDataEmpty();
}
