package cn.poco.camera3.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import cn.poco.camera3.util.CameraPercentUtil;

/**
 * 镜头亮度 seek bar
 * Created by Gxx on 2017/12/8.
 */

public class CameraLightnessSeekBar extends View
{
    private OnSeekBarChangeListener mOnSeekBarChangeListener;

    public interface OnSeekBarChangeListener
    {

        void onProgressChanged(CameraLightnessSeekBar seekBar);

        void onStartTrackingTouch(CameraLightnessSeekBar seekBar);

        void onStopTrackingTouch(CameraLightnessSeekBar seekBar);
    }

    private Paint mPaint;

    private int mViewW;
    private int mViewH;

    private int mMax = 1;
    private int mMin = -1;

    private float mInnCircleRadius;
    private float mInnLineLength;
    private float mLineWidth;

    private float mInnerSpace;
    private float mOutSpace;

    private float mCircleX;
    private float mCircleY;
    private int mValue;
    private String mValueText;
    private RectF mValidArea;

    private boolean mDrawText;

    private float mTextAlpha = 1f;
    private boolean mTextDecrement;

    public CameraLightnessSeekBar(Context context)
    {
        super(context);

        mPaint = new Paint();

        float mInnCircleSize = CameraPercentUtil.WidthPxToPercent(18);
        mInnCircleRadius = mInnCircleSize / 2f;
        mInnLineLength = CameraPercentUtil.WidthPxToPercent(9);
        mLineWidth = CameraPercentUtil.WidthPxToPercent(3);

        mInnerSpace = CameraPercentUtil.WidthPxToPercent(5);
        mOutSpace = CameraPercentUtil.WidthPxToPercent(7);

        mValueText = String.valueOf(0);

        mValidArea = new RectF();
    }

    public void setOnSeekBarChangeListener(OnSeekBarChangeListener onSeekBarChangeListener)
    {
        mOnSeekBarChangeListener = onSeekBarChangeListener;
    }

    public void setMax(int max)
    {
        if (max > 20)
        {
            max = 20;
        }
        mMax = max;
    }

    public void setMin(int min)
    {
        if (min < -20)
        {
            min = -20;
        }
        mMin = min;
    }

    private void calculateCircleCenter(float y)
    {
        if (y < mInnCircleRadius + mInnerSpace + mInnLineLength)
        {
            y = mInnCircleRadius + mInnerSpace + mInnLineLength;
        }
        else if (y > mViewH - mInnCircleRadius - mInnerSpace - mInnLineLength)
        {
            y = mViewH - mInnCircleRadius - mInnerSpace - mInnLineLength;
        }

        mCircleY = y;

        float dis = -(y - mViewH / 2f);
        float ratio = dis / (mViewH / 2f - (mInnCircleRadius + mInnerSpace + mInnLineLength));
        mValue = Math.round(Math.abs(ratio * (dis > 0 ? mMax : mMin)) * ratio / Math.abs(ratio));
        if (mValue > 0)
        {
            mValueText = "+" + mValue;
        }
        else if (mValue < 0)
        {
            mValueText = "" + mValue;
        }
        else
        {
            mValue = 0;
            mValueText = String.valueOf(0);
        }
        invalidate();
    }

    public int getValue()
    {
        return mValue;
    }

    public void setValue(int value)
    {
        if (value > mMax || value < mMin) return;

        mDrawText = true;
        mTextDecrement = false;
        mTextAlpha = 1f;

        float y = (value * 1f / Math.abs(value > 0 ? mMax : mMin)) * (mViewH / 2f - (mInnCircleRadius + mInnerSpace + mInnLineLength));

        calculateCircleCenter(mViewH / 2f + y);

        hideValueText();
    }

    public void countCircleCenter(float dis)
    {
        mDrawText = true;
        mTextDecrement = false;
        mTextAlpha = 1f;

        float y = mCircleY - dis;

        calculateCircleCenter(y);

        if (mOnSeekBarChangeListener != null)
        {
            mOnSeekBarChangeListener.onProgressChanged(this);
        }
    }

    public void hideValueText()
    {
        if (mValue == 0)
        {
            mCircleY = mViewH / 2f;
        }

        invalidate();

        postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                mTextDecrement = true;
                invalidate();
            }
        }, 200);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);

        mViewW = w;
        mViewH = h;

        mCircleX = mViewW - CameraPercentUtil.WidthPxToPercent(16 + 30);
        mCircleY = mViewH / 2f;

        mValidArea.set(mCircleX - CameraPercentUtil.WidthPxToPercent(30), 0, mViewW, mViewH);
    }

    public boolean isInValidArea(float x, float y)
    {
        return mValidArea != null && mValidArea.contains(x, y);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
            {
                if (isInValidArea(event.getX(), event.getY()))
                {
                    mDrawText = true;
                    mTextDecrement = false;
                    mTextAlpha = 1f;
                    calculateCircleCenter(event.getY());
                    if (mOnSeekBarChangeListener != null)
                    {
                        mOnSeekBarChangeListener.onStartTrackingTouch(this);
                    }
                    return true;
                }

                break;
            }

            case MotionEvent.ACTION_MOVE:
            {
                calculateCircleCenter(event.getY());
                if (mOnSeekBarChangeListener != null)
                {
                    mOnSeekBarChangeListener.onProgressChanged(this);
                }
                return true;
            }

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_OUTSIDE:
            {
                if (mOnSeekBarChangeListener != null)
                {
                    mOnSeekBarChangeListener.onStopTrackingTouch(this);
                }

                hideValueText();
                return true;
            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.WHITE);
        mPaint.setStrokeWidth(mLineWidth);
        mPaint.setShadowLayer(CameraPercentUtil.WidthPxToPercent(4), -CameraPercentUtil.WidthPxToPercent(1), CameraPercentUtil.WidthPxToPercent(1), 0x26000000);

        canvas.save();

        canvas.drawLine(mCircleX, 0, mCircleX, mCircleY - mInnCircleRadius - mInnerSpace - mInnLineLength - mOutSpace, mPaint);
        canvas.drawLine(mCircleX, mCircleY + mInnCircleRadius + mInnerSpace + mInnLineLength + mOutSpace, mCircleX, mViewH, mPaint);

        drawSunShineLogo(canvas);

        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.WHITE);
        mPaint.setShadowLayer(CameraPercentUtil.WidthPxToPercent(4), -CameraPercentUtil.WidthPxToPercent(1), CameraPercentUtil.WidthPxToPercent(1), 0x26000000);

        canvas.drawCircle(mCircleX, mCircleY, mInnCircleRadius, mPaint);

        drawValueText(canvas);

        canvas.restore();
    }

    private void drawValueText(Canvas canvas)
    {
        if (!mDrawText) return;

        if (!mTextDecrement)
        {
            mPaint.reset();
            mPaint.setAntiAlias(true);
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(Color.WHITE);
            mPaint.setTextAlign(Paint.Align.RIGHT);
            mPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 14, getResources().getDisplayMetrics()));

            Paint.FontMetrics metrics = mPaint.getFontMetrics();
            float x = mCircleX - CameraPercentUtil.WidthPxToPercent(30 + 38);
            float y = mCircleY + (metrics.bottom - metrics.top) / 2 - metrics.bottom;
            canvas.save();
            canvas.drawText(mValueText, x, y, mPaint);
            canvas.restore();
        }
        else
        {
            drawValueTextDecrement(canvas);
        }
    }

    private void drawValueTextDecrement(Canvas canvas)
    {
        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.WHITE);
        mPaint.setTextAlign(Paint.Align.RIGHT);
        mPaint.setAlpha((int) (255 * mTextAlpha));
        mPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 14, getResources().getDisplayMetrics()));

        Paint.FontMetrics metrics = mPaint.getFontMetrics();
        float x = mCircleX - CameraPercentUtil.WidthPxToPercent(30 + 38);
        float y = mCircleY + (metrics.bottom - metrics.top) / 2 - metrics.bottom;
        canvas.save();
        canvas.drawText(mValueText, x, y, mPaint);
        canvas.restore();

        if (mTextAlpha <= 0.05)
        {
            mDrawText = false;
            mTextAlpha = 0f;
        }
        else
        {
            mTextAlpha -= 0.05f;
        }

        invalidate();
    }

    private void drawSunShineLogo(Canvas canvas)
    {
        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.WHITE);
        mPaint.setStrokeWidth(mLineWidth);
        mPaint.setShadowLayer(CameraPercentUtil.WidthPxToPercent(4), -CameraPercentUtil.WidthPxToPercent(1), CameraPercentUtil.WidthPxToPercent(1), 0x26000000);

        canvas.drawLine(mCircleX, mCircleY - mInnCircleRadius - mInnerSpace, mCircleX, mCircleY - mInnCircleRadius - mInnerSpace - mInnLineLength, mPaint);
        canvas.drawLine(mCircleX, mCircleY + mInnCircleRadius + mInnerSpace, mCircleX, mCircleY + mInnCircleRadius + mInnerSpace + mInnLineLength, mPaint);
        canvas.drawLine(mCircleX - mInnCircleRadius - mInnerSpace, mCircleY, mCircleX - mInnCircleRadius - mInnerSpace - mInnLineLength, mCircleY, mPaint);
        canvas.drawLine(mCircleX + mInnCircleRadius + mInnerSpace, mCircleY, mCircleX + mInnCircleRadius + mInnerSpace + mInnLineLength, mCircleY, mPaint);

        canvas.save();
        canvas.rotate(45, mCircleX, mCircleY);
        canvas.drawLine(mCircleX, mCircleY - mInnCircleRadius - mInnerSpace, mCircleX, mCircleY - mInnCircleRadius - mInnerSpace - mInnLineLength, mPaint);
        canvas.drawLine(mCircleX, mCircleY + mInnCircleRadius + mInnerSpace, mCircleX, mCircleY + mInnCircleRadius + mInnerSpace + mInnLineLength, mPaint);
        canvas.drawLine(mCircleX - mInnCircleRadius - mInnerSpace, mCircleY, mCircleX - mInnCircleRadius - mInnerSpace - mInnLineLength, mCircleY, mPaint);
        canvas.drawLine(mCircleX + mInnCircleRadius + mInnerSpace, mCircleY, mCircleX + mInnCircleRadius + mInnerSpace + mInnLineLength, mCircleY, mPaint);
        canvas.restore();
    }
}