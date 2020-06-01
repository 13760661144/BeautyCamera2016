package cn.poco.camera3.ui.drawable;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.TypedValue;

import my.beautyCamera.R;

/**
 *
 * Created by Gxx on 2017/10/30.
 */

public class StickerMgrNonDrawable extends Drawable
{
    private String mNonStickerText;
    private Paint mPaint;
    private int mViewWidth;
    private int mViewHeight;
    private Rect mTextRect;

    public StickerMgrNonDrawable(Context context)
    {
        mPaint = new Paint();
        mNonStickerText = context.getString(R.string.material_manage_none_tip);
        mTextRect = new Rect();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setColor(0x99000000);
        mPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 13, context.getResources().getDisplayMetrics()));
        mPaint.getTextBounds(mNonStickerText, 0, mNonStickerText.length(), mTextRect);
    }

    @Override
    public void setBounds(int left, int top, int right, int bottom)
    {
        super.setBounds(left, top, right, bottom);
        mViewWidth = right - left;
        mViewHeight = bottom - top;
    }

    @Override
    public void draw(@NonNull Canvas canvas)
    {
        canvas.save();
        canvas.drawText(mNonStickerText, mViewWidth /2f, (mViewHeight + mTextRect.height())/2f, mPaint);
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
