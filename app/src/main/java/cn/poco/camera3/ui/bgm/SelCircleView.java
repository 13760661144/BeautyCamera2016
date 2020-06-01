package cn.poco.camera3.ui.bgm;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import cn.poco.home.home4.utils.PercentUtil;

/**
 * Created by gxx on 2017/7/28.
 *
 */

public class SelCircleView extends View
{
    private int mW, mH;
    private Paint mPaint;
    private float mRadius;
    private int mProgressColor = Color.TRANSPARENT;

    public SelCircleView(Context context)
    {
        super(context);
        mPaint = new Paint();
    }

    public void setProgressColor(int color)
    {
        mProgressColor = color;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);
        mW = w;
        mH = h;
        mRadius = w / 2f - PercentUtil.WidthPxToPercent(2);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        canvas.save();
        canvas.save();
        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setFilterBitmap(true);
        mPaint.setStrokeWidth(PercentUtil.WidthPxToPercent(4));
        mPaint.setColor(mProgressColor);
        mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(mW * 1f / 2f, mH * 1f / 2f, mRadius, mPaint);
        canvas.restore();
        canvas.restore();
    }
}
