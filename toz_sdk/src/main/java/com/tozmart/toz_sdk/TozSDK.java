package com.tozmart.toz_sdk;

import android.content.Context;

public class TozSDK {

    private static Context mContext;

    public TozSDK() {
    }

    public static synchronized void init(Context context) {
        if (mContext == null) {
            mContext = context.getApplicationContext();
        }
    }

    public static Context getContext() {
        if (mContext == null) {
            throw new NullPointerException("not initialize Application Context");
        }

        return mContext;
    }
}
