package cn.poco.camera3.ui.drawable;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import cn.poco.camera3.util.CameraPercentUtil;
import cn.poco.tianutils.ShareData;

/**
 * 贴纸素材 + 标签 的背景图
 * Created by Gxx on 2017/10/17.
 */

public class StickerBKDrawable extends Drawable
{
    private Paint mPaint;
    private int mWidth;
    private int mHeight;
    private int mDefWidth;
    private int mDefHeight;
    private int mLineWidth;
    private boolean mShowWhiteBK;

    public StickerBKDrawable()
    {
        mPaint = new Paint();

        mDefWidth = ShareData.m_screenRealWidth;
        mDefHeight = CameraPercentUtil.HeightPxToPercent(460);
        mLineWidth = CameraPercentUtil.WidthPxToPercent(1);
    }

    public void showWhiteBK(boolean show)
    {
        mShowWhiteBK = show;
        invalidateSelf();
    }

    @Override
    public void setBounds(int left, int top, int right, int bottom)
    {
        super.setBounds(left, top, right, bottom);

        mWidth = right - left;
        mHeight = bottom - top;
    }

    @Override
    public void draw(@NonNull Canvas canvas)
    {
        canvas.save();
        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(mShowWhiteBK ? 0xffffffff : 0x66000000);
        canvas.drawRect(0, 0, mWidth, mHeight, mPaint);
        mPaint.setColor(mShowWhiteBK ? 0x1A000000 : 0x1AFFFFFF);
        mPaint.setStrokeWidth(mLineWidth);
        canvas.drawLine(CameraPercentUtil.WidthPxToPercent(132), 0, CameraPercentUtil.WidthPxToPercent(132), CameraPercentUtil.HeightPxToPercent(80), mPaint);
        canvas.drawLine(0, CameraPercentUtil.HeightPxToPercent(80), mWidth, CameraPercentUtil.HeightPxToPercent(80), mPaint);
        canvas.restore();
    }

    @Override
    public void setAlpha(@IntRange(from = 0, to = 255) int alpha) {}

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {}

    @Override
    public int getOpacity()
    {
        return PixelFormat.TRANSPARENT;
    }

    @Override
    public int getIntrinsicHeight()
    {
        return mDefHeight;
    }

    @Override
    public int getIntrinsicWidth()
    {
        return mDefWidth;
    }
}
