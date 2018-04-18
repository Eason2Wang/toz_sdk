package com.tozmart.toz_sdk.contants;

/**
 * Created by wangyisong on 7/4/16.
 */
public class ImageConstParams {

    /**
     * 拍完照片保存的图片宽度
     */
    public static final int SCALE_IMAGE_W_HIGH = 1080;

    /**
     * 上下白线的位置比
     */
    public static final float DEFAULT_CROP_HEAD_TOP_RATIO = 0.15f;
    public static final float DEFAULT_CROP_FOOT_RATIO = 0.9f;

    /**
     * pose头顶和脚底的位置比
     */
    public static final float DEFAULT_CROP_HEAD_TOP_RATIO_FOR_POSE = DEFAULT_CROP_HEAD_TOP_RATIO * 1.2f;
    public static final float DEFAULT_CROP_FOOT_RATIO_FOR_POSE = DEFAULT_CROP_FOOT_RATIO * 0.97f;

    /**
     * 最终截图的上下比例
     */
    public static final float DEFAULT_CROP_HEAD_TOP_ROUGH_RATIO = DEFAULT_CROP_HEAD_TOP_RATIO * 0.8f;
    public static final float DEFAULT_CROP_FOOT_ROUGH_RATIO = DEFAULT_CROP_FOOT_RATIO * 1.1f;

    /**
     * 为了调整轮廓方便，图片上下留白
     */
    public static final int editPadding = 40;
}
