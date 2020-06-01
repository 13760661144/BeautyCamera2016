package cn.poco.home.home4.widget;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import cn.poco.home.home4.utils.PercentUtil;

/**
 * Created by lgd on 2017/11/30.
 */

public class RingDrawable extends Drawable
{
    private Rect rect;
    private Paint paint;
    private int centerX;
    private int centerY;
    private int radius;
    private int strokeWidth;
    private int color;
    public RingDrawable()
    {
        super();
        paint = new Paint();
        paint.setColor(0x87ffffff);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        strokeWidth = PercentUtil.RadiusPxToPercent(2);
        paint.setStrokeWidth(strokeWidth);
        rect = new Rect();
    }

    @Override
    public void setBounds(int left, int top, int right, int bottom)
    {
        super.setBounds(left, top, right, bottom);
        centerX = (right-left)/2;
        centerY = (bottom-left)/2;
        radius = Math.min(centerX,centerY);
    }

    @Override
    public void setBounds(@NonNull Rect bounds)
    {
        super.setBounds(bounds);
        rect.set(bounds);
    }

    @Override
    public void draw(@NonNull Canvas canvas)
    {
        canvas.drawCircle(centerX,centerY,radius- strokeWidth/2,paint);
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
        return 0;
    }

    public void setColor(int color)
    {
        this.color = color;
        paint.setColor(color);
    }

    public void setStrokeWidth(int strokeWidth)
    {
        this.strokeWidth = strokeWidth;
        paint.setStrokeWidth(strokeWidth);
    }
}
