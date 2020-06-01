package cn.poco.rise;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.ColorInt;
import android.view.MotionEvent;
import android.view.View;

import cn.poco.advanced.ImageUtils;
import cn.poco.camera3.util.CameraPercentUtil;

/**
 * 起点可调节
 * Created by Gxx on 2017/12/4.
 */

public class RiseSeekBar extends View
{
    private int mSeekLineBkColor;
    private int mProgressColor;
    private int mInnCircleColor = 0xfff6f5f5;
    private Paint mPaint;

    private int mOutCircleSize;
    private int mInnCircleSize;
    private int mOutCircleRadius;
    private int mInnCircleRadius;
    private int mMaxMoveDistance;

    private int mProgressStartPointSize;
    private int mProgressStartPointRadius;
    private int mProgressWidth;
    private int mProgressBKWidth;
    private int mProgress = 0;

    private int mWidth;
    private int mHeight;
    private OnSeekBarChangeListener mOnSeekBarChangeListener;

    public RiseSeekBar(Context context)
    {
        super(context);

        mProgressStartPointSize = CameraPercentUtil.WidthPxToPercent(8);
        mProgressStartPointRadius = mProgressStartPointSize / 2;
        mProgressWidth = CameraPercentUtil.WidthPxToPercent(4);
        mProgressBKWidth = CameraPercentUtil.WidthPxToPercent(2);
        mInnCircleSize = CameraPercentUtil.WidthPxToPercent(44);
        mOutCircleSize = CameraPercentUtil.WidthPxToPercent(48);
        mOutCircleRadius = mOutCircleSize / 2;
        mInnCircleRadius = mInnCircleSize / 2;

        mSeekLineBkColor = 0x59000000;
        initPaint();

        mProgressColor = ImageUtils.GetSkinColor(0xffe75988);
    }

    public void setInnCircleColor(int mInnCircleColor)
    {
        this.mInnCircleColor = mInnCircleColor;
    }

    public void setSeekLineBkColor(@ColorInt int color)
    {
        mSeekLineBkColor = color;
        invalidate();
    }

    private void initPaint()
    {
        if (mPaint == null)
        {
            mPaint = new Paint();
        }
        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setFilterBitmap(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        setMeasuredDimension(getMeasureSize(widthMode, widthSize), getMeasureSize(heightMode, heightSize));
    }

    private int getMeasureSize(int mode, int size)
    {
        int result;
        if (mode == MeasureSpec.AT_MOST)
        {
            result = CameraPercentUtil.WidthPxToPercent(150);
            if (result > size)
            {
                result = size;
            }
        }
        else
        {
            result = size;
        }

        return result;
    }

    public void setOnSeekBarChangeListener(OnSeekBarChangeListener onSeekBarChangeListener)
    {
        mOnSeekBarChangeListener = onSeekBarChangeListener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {

        if (!isEnabled())
        {
            return false;
        }

        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                mProgress = calculateProgress(event.getX());
                if (mOnSeekBarChangeListener != null)
                {
                    mOnSeekBarChangeListener.onStartTrackingTouch(this);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                mProgress = calculateProgress(event.getX());
                if (mOnSeekBarChangeListener != null)
                {
                    mOnSeekBarChangeListener.onProgressChanged(this, mProgress);
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (mOnSeekBarChangeListener != null)
                {
                    mOnSeekBarChangeListener.onStopTrackingTouch(this);
                }
                break;
        }

        invalidate();

        return true;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);

        mWidth = w;
        mHeight = h;

        mMaxMoveDistance = w - mOutCircleSize;
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        canvas.save();
        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setFilterBitmap(true);

        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(mProgressBKWidth);
        mPaint.setColor(mSeekLineBkColor);
        canvas.drawLine(0, mHeight / 2f, mWidth, mHeight / 2, mPaint);

        mPaint.setStrokeWidth(mProgressWidth);
        mPaint.setColor(mProgressColor);
        canvas.drawLine(mWidth / 2f, mHeight / 2f, mWidth / 2f + mProgress / 100f * (mMaxMoveDistance / 2f), mHeight / 2f, mPaint);

        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setFilterBitmap(true);
        mPaint.setColor(mProgressColor);
        canvas.drawCircle(mWidth/2f, mHeight/2f, mProgressStartPointRadius, mPaint);
        canvas.drawCircle(mWidth / 2f + mProgress / 100f * (mMaxMoveDistance / 2f), mHeight / 2f, mOutCircleRadius, mPaint);
        mPaint.setColor(mInnCircleColor);
        canvas.drawCircle(mWidth / 2f + mProgress / 100f * (mMaxMoveDistance / 2f), mHeight / 2f, mInnCircleRadius, mPaint);
        canvas.restore();
    }

    private int calculateProgress(float x)
    {
        float realX = x - mOutCircleRadius;
        if (realX < 0)
        {
            realX = 0;
        }
        if (realX > mMaxMoveDistance)
        {
            realX = mMaxMoveDistance;
        }
        return  (int) ((realX - mMaxMoveDistance / 2f) / (mMaxMoveDistance / 2f) * 100f);
    }

    public void setProgress(int progress)
    {
        mProgress = progress;
        invalidate();
    }

    public int getProgress()
    {
        return mProgress;
    }

    public int getValidWidth()
    {
        return mMaxMoveDistance;
    }

    public interface OnSeekBarChangeListener
    {

        void onProgressChanged(RiseSeekBar seekBar, int progress);

        void onStartTrackingTouch(RiseSeekBar seekBar);

        void onStopTrackingTouch(RiseSeekBar seekBar);
    }
}
