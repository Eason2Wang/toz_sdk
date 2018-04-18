package com.tozmart.toz_sdk.contants;

import android.os.Environment;

import com.tozmart.toz_sdk.TozSDK;

import org.opencv.core.Scalar;

/**
 * Created by tracy on 17/8/26.
 */

public class Constants {
    public static final String MALE = "1";
    public static final String FEMALE = "0";
    //保存图片的地址
    public static final String PHOTO_SAVE_PATH =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/1Measure";//通过相机拍照后所得图片的存放文件夹，包括正侧面检测所获得的原始图片;
    public static final String GROUP_MODE_PHOTO_SAVE_PATH =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/1Measure/group";//通过相机拍照后所得图片的存放文件夹，包括正侧面检测所获得的原始图片;
    public static final String SYSTEM_PHOTO_PATH =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();

    public static final String FRONT_CACHE_URL = TozSDK.getContext().getExternalCacheDir().getAbsolutePath() + "/front.png";
    public static final String SIDE_CACHE_URL = TozSDK.getContext().getExternalCacheDir().getAbsolutePath() + "/side.png";
    public static final String FRONT_CACHE_NAME = "front.png";
    public static final String SIDE_CACHE_NAME = "side.png";
    public static final String FRONTORI_CACHE_URL = TozSDK.getContext().getExternalCacheDir().getAbsolutePath() + "/frontOri.jpg";
    public static final String SIDEORI_CACHE_URL = TozSDK.getContext().getExternalCacheDir().getAbsolutePath() + "/sideOri.jpg";
    public static final String FRONTORI_CACHE_NAME = "frontOri.jpg";
    public static final String SIDEORI_CACHE_NAME = "sideOri.jpg";

    /**
     * 存放用户体型obj记录的文件夹
     */
    public static String OBJ_DIR_NAME = "/obj/";

    /**
     * 存放etailor obj的文件夹
     */
    public static String ETAILOR_OBJ_DIR_NAME = "/etailor-obj/";

    public static int ETAILOR_OBJ_FILE_PREFFIX = 100000000;

    public static final float FT2CM = 30.48f;
    public static final float INCH2CM = 2.54f;
    public static final float KG2LB = 2.205f;
    public static final float FT2INCH = 12f;

    public static final Scalar OPENCV_YELLOW = new Scalar(255, 255, 51, 0);
    public static final int OPENCV_LINE_WIDTH = 3;

    public final static int PROCESS_TIME_MS = 100;

    // 围度
    public final static String MODEL_3D_LINE_TYPE_GIRTH = "1";
    // 高度
    public final static String MODEL_3D_LINE_TYPE_HEIGHT = "3";
    // 宽度
    public final static String MODEL_3D_LINE_TYPE_WIDTH = "4";
    // 厚度
    public final static String MODEL_3D_LINE_TYPE_THICK = "5";
    // 角度
    public final static String MODEL_3D_LINE_TYPE_ANGLE = "6";
    // 高度1
    public final static String MODEL_3D_LINE_TYPE_HEIGHT1 = "31";
}
