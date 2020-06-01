package cn.poco.dynamicSticker.newSticker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.ColorInt;
import android.view.View;

/**
 * @author lmx
 *         Created by lmx on 2017/9/7.
 */

public class PointCircle extends View
{
    protected Paint mPaint;
    protected int mColor = Color.WHITE;

    public PointCircle(Context context)
    {
        super(context);
        init();
    }

    private void init()
    {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
    }

    public void setColor(@ColorInt int mColor)
    {
        this.mColor = mColor;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        canvas.save();
        mPaint.setColor(mColor);
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, getWidth() / 2, mPaint);
        canvas.restore();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int size = Math.min(getMeasuredWidth(), getMeasuredHeight());
        setMeasuredDimension(size, size);
    }
}
