package cn.poco.camera3.ui.drawable;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import cn.poco.camera3.util.CameraPercentUtil;

/**
 * 白色圆圈 + 背景
 * Created by admin on 2018/1/26.
 */

public class AddCircleDrawable extends Drawable
{
    private float mRadius;
    private Paint mPaint;
    private int mPlusLength;
    private int mPaintWidth;
    private int w;
    private int h;

    public AddCircleDrawable()
    {
        mPaintWidth = CameraPercentUtil.WidthPxToPercent(4);
        mPlusLength = CameraPercentUtil.WidthPxToPercent(30);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.WHITE);
        mPaint.setStrokeWidth(mPaintWidth);
    }

    @Override
    public void setBounds(int left, int top, int right, int bottom)
    {
        super.setBounds(left, top, right, bottom);

        w = right - left;
        h = bottom - top;
        int len = Math.min(w, h);
        mRadius = len * 1f / 2f - mPaintWidth;
    }

    @Override
    public void draw(@NonNull Canvas canvas)
    {
        canvas.save();
        canvas.drawCircle(w / 2f, h / 2f, mRadius, mPaint);

        canvas.drawLine(w / 2 - mPlusLength, h / 2f, w / 2 + mPlusLength, h / 2f, mPaint);
        canvas.drawLine(w / 2, h / 2f - mPlusLength, w / 2, h / 2f + mPlusLength, mPaint);
        canvas.restore();
    }

    @Override
    public void setAlpha(int alpha)
    {

    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter)
    {

    }

    @Override
    public int getOpacity()
    {
        return PixelFormat.TRANSPARENT;
    }
}
