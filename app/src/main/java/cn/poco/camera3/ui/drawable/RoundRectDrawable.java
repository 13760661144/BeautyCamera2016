package cn.poco.camera3.ui.drawable;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * 自动锁定背景
 * Created by Gxx on 2018/1/18.
 */

public class RoundRectDrawable extends Drawable
{
    private RectF mRect;
    private Paint mPaint;
    private int mColor;
    private int mRx;
    private int mRy;

    public RoundRectDrawable()
    {
        mRect = new RectF();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
    }

    public void setColor(int color)
    {
        mColor = color;
        mPaint.setColor(mColor);
    }

    public void setRoundRectParams(int rx, int ry)
    {
        mRx = rx;
        mRy = ry;
    }

    public void updateUI()
    {
        invalidateSelf();
    }

    @Override
    public void setBounds(int left, int top, int right, int bottom)
    {
        super.setBounds(left, top, right, bottom);
        mRect.set(left, top, right, bottom);
    }

    public void setShadowLayer(float radius, float dx, float dy, int shadowColor)
    {
        mPaint.setShadowLayer(radius, dx, dy, shadowColor);
    }

    @Override
    public void draw(@NonNull Canvas canvas)
    {
        canvas.save();
        canvas.drawRoundRect(mRect, mRx, mRy, mPaint);
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
