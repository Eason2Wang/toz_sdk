package com.tozmart.toz_sdk.utils;

import android.graphics.Bitmap;
import android.support.annotation.Nullable;

public class BitmapHolder {

    private static Bitmap frontBitmap;
    private static Bitmap sideBitmap;

    public static void setFrontBitmap(@Nullable Bitmap frontBitmap) {
        BitmapHolder.frontBitmap = frontBitmap != null ? frontBitmap : null;
    }

    @Nullable
    public static Bitmap getFrontBitmap() {
        return frontBitmap != null ? frontBitmap : null;
    }

    public static void setSideBitmap(@Nullable Bitmap sideBitmap) {
        BitmapHolder.sideBitmap = sideBitmap != null ? sideBitmap : null;
    }

    @Nullable
    public static Bitmap getSideBitmap() {
        return sideBitmap != null ? sideBitmap : null;
    }

    public static void recycle() {
        if (frontBitmap != null && !frontBitmap.isRecycled()){
            frontBitmap.recycle();
        }
        if (sideBitmap != null && !sideBitmap.isRecycled()){
            sideBitmap.recycle();
        }
    }
}
