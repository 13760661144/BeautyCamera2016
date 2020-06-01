package cn.poco.home.home4.widget;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by lgd on 2017/10/20.
 * 两边圆角
 */

public class RoundColorDrawable extends Drawable
{
    private Paint mPaint;
    private RectF mRectF;
    public RoundColorDrawable(int color)
    {
        super();
        mPaint = new Paint();
        mPaint.setColor(color);
        mPaint.setAntiAlias(true);
        mRectF = new RectF();
    }

    @Override
    public void setBounds(int left, int top, int right, int bottom)
    {
        super.setBounds(left, top, right, bottom);
        mRectF.set(left,top,right,bottom);
    }

    @Override
    public void draw(@NonNull Canvas canvas)
    {
        int h = (int) (mRectF.bottom - mRectF.top);
        int w = (int) (mRectF.right - mRectF.left);
        int ry = h/2;
        int rx = w > h ? ry : w/2;
        canvas.drawRoundRect(mRectF,rx,ry,mPaint);
    }

    @Override
    public void setAlpha(@IntRange(from = 0, to = 255) int alpha)
    {
        mPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter)
    {

    }

    @Override
    public int getOpacity()
    {
        return 0;
    }
}
