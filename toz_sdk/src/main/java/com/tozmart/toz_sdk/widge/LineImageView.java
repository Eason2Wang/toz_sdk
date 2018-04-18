package com.tozmart.toz_sdk.widge;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.tozmart.toz_sdk.R;
import com.tozmart.toz_sdk.contants.ImageConstParams;

/**
 * Created by wangyisong on 30/11/16.
 */

public class LineImageView extends ImageView {

    final int TEXT_SIZE = 30;
    final int LINE_WIDTH = 5;
    final int MARGIN = 5;
    final int RECT_RADIUS = 5;

    Paint mTextPaint;
    Paint mImageAlphaPaint;
    Paint mLinePaint;
    Paint mShadowPaint;
    Paint mFaceRectPaint;
    Paint.FontMetrics fm;

    private Path mHeadTopPath;
    private Path mFootPath;
    private Path mMiddlePath;

    /**
     * 左右是相对于观察者
     */
    private Bitmap left_shoe;
    private float left_shoe_ratio;
    private Bitmap right_shoe;
    private float right_shoe_ratio;
    private Bitmap side_shoe;
    private float side_shoe_ratio;
    private Rect src_left_shoe_rect;
    private Rect src_right_shoe_rect;
    private Rect src_side_shoe_rect;
    private RectF dest_side_shoe_r;
    private RectF dest_right_shoe_r;
    private RectF dest_left_shoe_r;
    private PointF left_shoe_br = new PointF();//右下点
    private PointF right_shoe_bl = new PointF();//左下点
    private PointF side_shoe_bc = new PointF();
    private float left_shoe_width;
    private float right_shoe_width;
    private float side_shoe_width;
    /**
     * 是否是正面
     */
    private boolean isFront;
    /**
     * 是否显示模特
     */
    private  boolean isShowModel;

    private Bitmap frontPose;
    private Bitmap sidePose;
    private Rect src_front_pose_rect;
    private Rect src_side_pose_rect;
    private RectF dest_front_pose_r;
    private RectF dest_side_pose_r;
    private float front_pose_ratio;
    private float side_pose_ratio;
    private float pose_height;
    private PointF pose_up_center = new PointF();
    private PointF pose_down_center = new PointF();

    int viewWidth;
    int viewHeight;

    public LineImageView(final Context context) {
        this(context, null);
    }

    public LineImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LineImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        fm = new Paint.FontMetrics();
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setAntiAlias(true);

        mImageAlphaPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mImageAlphaPaint.setAlpha((int)(255 * 0.8));
        mImageAlphaPaint.setAntiAlias(true);

        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setColor(Color.argb(255, 255, 255, 255));
        mLinePaint.setAntiAlias(true);
        mLinePaint.setStrokeWidth(LINE_WIDTH);//设置粗细
        mLinePaint.setStrokeJoin(Paint.Join.ROUND); // 让画的线圆滑
        mLinePaint.setStrokeCap(Paint.Cap.ROUND);
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setPathEffect(new DashPathEffect(new float[]{10, 20}, 0));

        mShadowPaint = new Paint(0);
        mShadowPaint.setColor(0xff101010);
        mShadowPaint.setMaskFilter(new BlurMaskFilter(8, BlurMaskFilter.Blur.NORMAL));

        mFaceRectPaint = new Paint();
        mFaceRectPaint.setAntiAlias(true);
        mFaceRectPaint.setDither(true);
        mFaceRectPaint.setAlpha(128);
        mFaceRectPaint.setStrokeWidth(5);
        mFaceRectPaint.setStyle(Paint.Style.STROKE);
    }

    public void postInvalidate(boolean isFront, boolean isShowModel) {
        this.isFront = isFront;
        this.isShowModel = isShowModel;
        if (isFront) {
            if (isShowModel) {
                frontPose = BitmapFactory.decodeResource(getResources(), R.drawable.pose_front);
                front_pose_ratio = (float) frontPose.getWidth() / frontPose.getHeight();
                src_front_pose_rect = new Rect(0, 0, frontPose.getWidth(), frontPose.getHeight());
            } else{
                left_shoe = BitmapFactory.decodeResource(getResources(), R.drawable.right_shoe);
                right_shoe = BitmapFactory.decodeResource(getResources(), R.drawable.left_shoe);

                left_shoe_ratio = (float) left_shoe.getHeight() / left_shoe.getWidth();
                right_shoe_ratio = (float) right_shoe.getHeight() / right_shoe.getWidth();

                src_left_shoe_rect = new Rect(0, 0, left_shoe.getWidth(), left_shoe.getHeight());
                src_right_shoe_rect = new Rect(0, 0, right_shoe.getWidth(), right_shoe.getHeight());
            }
        } else{
            if (isShowModel) {
                sidePose = BitmapFactory.decodeResource(getResources(), R.drawable.pose_side);
                side_pose_ratio = (float) sidePose.getWidth() / sidePose.getHeight();
                src_side_pose_rect = new Rect(0, 0, sidePose.getWidth(), sidePose.getHeight());
            } else {
                side_shoe = BitmapFactory.decodeResource(getResources(), R.drawable.side_shoe);
                side_shoe_ratio = (float) side_shoe.getHeight() / side_shoe.getWidth();
                src_side_shoe_rect = new Rect(0, 0, side_shoe.getWidth(), side_shoe.getHeight());
            }
        }
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mHeadTopPath = new Path();
        mFootPath = new Path();
        mMiddlePath = new Path();

        viewWidth = w;
        viewHeight = h;

        mHeadTopPath.moveTo(0, h * ImageConstParams.DEFAULT_CROP_HEAD_TOP_RATIO);
        mHeadTopPath.quadTo(w / 2, h * ImageConstParams.DEFAULT_CROP_HEAD_TOP_RATIO, w, h * ImageConstParams.DEFAULT_CROP_HEAD_TOP_RATIO);

        mFootPath.moveTo(0, h * ImageConstParams.DEFAULT_CROP_FOOT_RATIO);
        mFootPath.quadTo(w / 2, h * ImageConstParams.DEFAULT_CROP_FOOT_RATIO, w, h * ImageConstParams.DEFAULT_CROP_FOOT_RATIO);

        mMiddlePath.moveTo(w / 2, 0);
        mMiddlePath.quadTo(w / 2, h / 2, w / 2, h);

        pose_up_center = new PointF(w / 2.f, h * ImageConstParams.DEFAULT_CROP_HEAD_TOP_RATIO_FOR_POSE);
        pose_down_center = new PointF(w / 2.f, h * ImageConstParams.DEFAULT_CROP_FOOT_RATIO_FOR_POSE);
        pose_height = -pose_up_center.y + pose_down_center.y;

        float dis2Middle = w * 0.05f;
        left_shoe_br = new PointF(w / 2.f - dis2Middle, h * ImageConstParams.DEFAULT_CROP_FOOT_RATIO_FOR_POSE);
        right_shoe_bl = new PointF(w / 2.f + dis2Middle, h * ImageConstParams.DEFAULT_CROP_FOOT_RATIO_FOR_POSE);
        side_shoe_bc = new PointF(w / 2.f, h * ImageConstParams.DEFAULT_CROP_FOOT_RATIO_FOR_POSE);
        left_shoe_width = w * 0.06f;
        right_shoe_width = w * 0.06f;
        side_shoe_width = w * 0.13f;
    }

    public void recycleBitmap(){
        if (left_shoe != null && !left_shoe.isRecycled())
            left_shoe.recycle();
        if (right_shoe != null && !right_shoe.isRecycled())
            right_shoe.recycle();
        if (side_shoe != null && !side_shoe.isRecycled())
            side_shoe.recycle();
        if (frontPose != null && !frontPose.isRecycled())
            frontPose.recycle();
        if (sidePose != null && !sidePose.isRecycled())
            sidePose.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawPath(mHeadTopPath, mLinePaint);
        canvas.drawPath(mFootPath, mLinePaint);
        canvas.drawPath(mMiddlePath, mLinePaint);

        mTextPaint.setColor(Color.BLACK);
        mTextPaint.setTextSize(TEXT_SIZE);
        mTextPaint.getFontMetrics(fm);
        canvas.drawRoundRect(15 - MARGIN, viewHeight * ImageConstParams.DEFAULT_CROP_HEAD_TOP_RATIO + fm.top + MARGIN,
                15 + mTextPaint.measureText(getContext().getResources().getString(R.string.head_top)) + MARGIN, viewHeight * ImageConstParams.DEFAULT_CROP_HEAD_TOP_RATIO + fm.bottom
                        + 2 * MARGIN, RECT_RADIUS, RECT_RADIUS, mTextPaint);
        canvas.drawRoundRect(15 - MARGIN, viewHeight * ImageConstParams.DEFAULT_CROP_FOOT_RATIO + fm.top + MARGIN,
                15 + mTextPaint.measureText(getContext().getResources().getString(R.string.foot)) + MARGIN, viewHeight * ImageConstParams.DEFAULT_CROP_FOOT_RATIO + fm.bottom
                        + 2 * MARGIN, RECT_RADIUS, RECT_RADIUS, mTextPaint);

        mTextPaint.setColor(Color.WHITE);
        canvas.drawText(getContext().getResources().getString(R.string.head_top), 15, viewHeight * ImageConstParams.DEFAULT_CROP_HEAD_TOP_RATIO + MARGIN, mTextPaint);
        canvas.drawText(getContext().getResources().getString(R.string.foot), 15, viewHeight * ImageConstParams.DEFAULT_CROP_FOOT_RATIO + MARGIN, mTextPaint);

        if (isFront) {
            if (isShowModel) {
                if (frontPose != null) {
                    dest_front_pose_r = new RectF(
                            pose_up_center.x - pose_height * front_pose_ratio / 2,
                            pose_up_center.y,
                            pose_up_center.x + pose_height * front_pose_ratio / 2,
                            pose_down_center.y);
                    canvas.drawBitmap(frontPose, src_front_pose_rect, dest_front_pose_r, mImageAlphaPaint);
                }
            } else {
                if (left_shoe != null) {
                    dest_left_shoe_r = new RectF(
                            left_shoe_br.x - left_shoe_width,
                            left_shoe_br.y - left_shoe_width * left_shoe_ratio,
                            left_shoe_br.x,
                            left_shoe_br.y);
                    canvas.drawBitmap(left_shoe, src_left_shoe_rect, dest_left_shoe_r, mTextPaint);
                }
                if (right_shoe != null) {
                    dest_right_shoe_r = new RectF(
                            right_shoe_bl.x,
                            right_shoe_bl.y - right_shoe_width * right_shoe_ratio,
                            right_shoe_bl.x + right_shoe_width,
                            right_shoe_bl.y);
                    canvas.drawBitmap(right_shoe, src_right_shoe_rect, dest_right_shoe_r, mTextPaint);
                }
            }
        }else {
            if (isShowModel) {
                if (sidePose != null) {
                    dest_side_pose_r = new RectF(
                            pose_up_center.x - pose_height * side_pose_ratio / 2,
                            pose_up_center.y,
                            pose_up_center.x + pose_height * side_pose_ratio / 2,
                            pose_down_center.y);
                    canvas.drawBitmap(sidePose, src_side_pose_rect, dest_side_pose_r, mImageAlphaPaint);
                }
            } else {
                if (side_shoe != null) {
                    dest_side_shoe_r = new RectF(
                            side_shoe_bc.x - side_shoe_width / 2,
                            side_shoe_bc.y - side_shoe_width * side_shoe_ratio,
                            side_shoe_bc.x + side_shoe_width / 2,
                            side_shoe_bc.y);
                    canvas.drawBitmap(side_shoe, src_side_shoe_rect, dest_side_shoe_r, mTextPaint);
                }
            }
        }
    }
}
