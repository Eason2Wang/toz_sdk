package com.tozmart.toz_sdk.widge;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by tracy on 2018/1/19.
 */

public class SensorTipView extends View {
    private Paint mPaint;
    private float strokeWidth = 10;
    private float innerRadius = 40;

    private float outterRadius = 80;

    private float ballRadius = 15;

    private PointF ballPosition = new PointF();

    private PointF viewCenterPoint;

    public SensorTipView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setAntiAlias(true);
    }

    public void setCircleWidth(float width){
        strokeWidth = width;
        mPaint.setStrokeWidth(width);
    }

    /**
     * 设置半径
     * @param outter
     */
    public void setRadius(float outter){
        outterRadius = outter - strokeWidth * 0.5f;
        innerRadius = outterRadius * 0.5f;
        ballRadius = innerRadius * 0.5f;
    }

    /**
     * 设置圆球的位置
     * @param position
     * @return
     */
    public void setBallPosition(PointF position){
        ballPosition = position;
        invalidate();
    }

    /**
     * 设置圆球的位置
     * @param percentX
     * @param percentY
     */
    public void setBallPosition(float percentX, float percentY){
        ballPosition = new PointF(getWidth() * percentX, getHeight() * percentY);
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        viewCenterPoint = new PointF(w / 2, h / 2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setARGB(200, 238, 238, 238);
        canvas.drawCircle(viewCenterPoint.x, viewCenterPoint.y, outterRadius, mPaint);

        mPaint.setARGB(200,234,67,53);
        canvas.drawCircle(viewCenterPoint.x, viewCenterPoint.y, innerRadius, mPaint);

        // 以整个view中心点为原点，左为x轴正方向，上为y轴正方向建立坐标系，
        // 重新计算ball相对于原点的位置
        float relativePositionX = ballPosition.x - viewCenterPoint.x;
        float relativePositionY = viewCenterPoint.y - ballPosition.y;
        float dis = (float) getDistance(relativePositionX, relativePositionY, 0, 0);
        if (dis + ballRadius > outterRadius){
            float adjustY = (dis + ballRadius - outterRadius) / dis * relativePositionY;
            ballPosition.y = viewCenterPoint.y - (relativePositionY - adjustY);
            float adjustX = (dis + ballRadius - outterRadius) / dis * relativePositionX;
            ballPosition.x = relativePositionX - adjustX + viewCenterPoint.x;
        } else if (dis <= innerRadius - ballRadius){
            mPaint.setARGB(200, 0, 255, 0);
            canvas.drawCircle(viewCenterPoint.x, viewCenterPoint.y, innerRadius, mPaint);
        }
        mPaint.setARGB(200, 52, 168, 83);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(ballPosition.x, ballPosition.y, ballRadius, mPaint);
    }

    /**
     * 计算两点之间的距离
     */
    private double getDistance(float x1, float y1, float x2, float y2){
        return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }
}
