package com.tozmart.toz_sdk.utils;

/**
 * Created by wys on 17/5/16.
 * 按钮防抖动
 */

public class ClickUtils {
    private static long lastClickTime;
    private final static int SPACE_TIME = 500;
    public synchronized static boolean isDoubleClick() {
        long currentTime = System.currentTimeMillis();
        boolean isClick2;
        isClick2 = currentTime - lastClickTime <= SPACE_TIME;
        lastClickTime = currentTime;
        return isClick2;
    }
}
