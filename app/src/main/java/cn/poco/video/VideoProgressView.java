package cn.poco.video;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import java.util.Formatter;
import java.util.Locale;

import cn.poco.camera3.util.CameraPercentUtil;
import my.beautyCamera.R;

/**
 * 视频预览进度条
 * Created by Gxx on 2017/9/27.
 */

public class VideoProgressView extends View
{
    private final float mCircleScale;
    private final int mCircleWH;
    private final int mTouchHeight;
    private final int mShadowRadius;
    private final RadialGradient mGradient;
    private int mWidth;
    private int mHeight;
    private RectF mRectF;
    private RectF mAllRectF;
    private Paint mPaint;
    private OnProgressChangeListener mProgressChangeListener;
    private int mProgressBarHeight;
    private int mProgressColor;

    private Matrix mCircleMatrix;
    private Bitmap mCircle;

    private int mTipsCirCleWH;
    private String mTips;
    private long mDuration;
    private Rect mDurationTextRect;
    private float mTextX;
    private float mTextY;
    private boolean mIsDrawTipsText = false;
    private boolean mIsProgressAlpha = true;
    private boolean mIsProgressShow = true;
    private float mAlpha = 1.0f;
    private boolean mUIEnable = true;
    private float mRatio;
    private boolean mIsTransXToDrawCircle = false;
    private boolean mIsTouch;

    private Formatter mFormatter;
    private StringBuilder mStringBuilder;

    public VideoProgressView(Context context)
    {
        super(context);
        mRectF = new RectF();
        mAllRectF = new RectF();
        mPaint = new Paint();
        mCircleMatrix = new Matrix();
        mCircle = BitmapFactory.decodeResource(context.getResources(), R.drawable.video_preview_progress_circle);

        mTouchHeight = CameraPercentUtil.HeightPxToPercent(36);
        mCircleWH = CameraPercentUtil.WidthPxToPercent(32);
        mCircleScale = mCircleWH * 1f / mCircle.getWidth();

        mTipsCirCleWH = CameraPercentUtil.WidthPxToPercent(110);

        mDurationTextRect = new Rect();

        mShadowRadius = CameraPercentUtil.WidthPxToPercent(60);

        mGradient = new RadialGradient(0, 0, mShadowRadius,
                new int[]{0x2E000000, 0x2E000000, 0x00000000},
                new float[]{0f, 0.4f, 1f}, Shader.TileMode.CLAMP);

        mStringBuilder = new StringBuilder();
        mFormatter = new Formatter(mStringBuilder, Locale.CHINA);
    }

    private String getDurationString(long timeMs)
    {
        // return new la("mm:ss", Locale.CHINA).format(new Date((long) (mDuration * ratio)));
        long totalSeconds = (timeMs + 500) / 1000;
        long seconds = totalSeconds % 60;
        long minutes = (totalSeconds / 60) % 60;
        long hours = totalSeconds / 3600;
        if (mStringBuilder != null) { mStringBuilder.setLength(0);}
        if (mFormatter != null){
            return hours > 0 ? mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString()
                    : mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
        return "";
    }

    public void setDuration(long duration)
    {
        mDuration = duration;
    }

    public void setProgress(float progress)
    {
        if (mIsTouch) return;

        setProgressInTouch(progress);
    }

    private void setProgressInTouch(float progress)
    {
        if (mProgressBarHeight == 0 && getMeasuredHeight() > 0)
        {
            mProgressBarHeight = getMeasuredHeight();
        }

        float ratio = progress * 1f / 100;
        mRatio = ratio;
        mRectF.setEmpty();
        mRectF.left = 0;
        mRectF.top = mHeight - mProgressBarHeight / 2f - mCircleWH / 2f;

        mRectF.right = mWidth * ratio;
        mRectF.bottom = mRectF.top + mProgressBarHeight;

        mCircleMatrix.reset();
        mCircleMatrix.postScale(mCircleScale, mCircleScale);
        float x = mRectF.right - mCircleWH / 2.0f;
        float y = mRectF.top - mCircleWH / 2.0f + mProgressBarHeight / 2f;
        mCircleMatrix.postTranslate(x, y);

        invalidate();
    }

    public void setProgressColor(int color)
    {
        mProgressColor = color;
    }

    public void setProgressBarHeight(int height)
    {
        mProgressBarHeight = height;
    }

    public void setIsTransXToDrawCircle(boolean is)
    {
        mIsTransXToDrawCircle = is;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;

        mAllRectF.top = mHeight - mProgressBarHeight / 2f - mCircleWH / 2f;
        mAllRectF.right = mWidth;
        mAllRectF.bottom = mAllRectF.top + mProgressBarHeight;

        if (mIsTransXToDrawCircle)
        {
            mWidth -= mCircleWH;
            mAllRectF.right = mWidth;
        }
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        setAlpha(mAlpha);

        if (mIsProgressAlpha)
        {
            if (mAlpha < 0.9)
            {
                mAlpha += 0.05;
            }
            else
            {
                mAlpha = 1;
            }
        }
        else
        {
            if (mAlpha > 0.1)
            {
                mAlpha -= 0.05;
            }
            else
            {
                mAlpha = 0;
            }
        }

        if (!mIsProgressShow) return;

        canvas.save();
        canvas.translate(mIsTransXToDrawCircle ? mCircleWH / 2f : 0, 0);
        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setColor(0x4dffffff);
        canvas.drawRect(mAllRectF, mPaint);

        mPaint.reset();
        mPaint.setAntiAlias(true);
        if (mProgressColor == 0)
        {
            mProgressColor = Color.RED;
        }
        mPaint.setColor(mProgressColor);
        canvas.drawRect(mRectF, mPaint);

        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setFilterBitmap(true);
        canvas.drawBitmap(mCircle, mCircleMatrix, mPaint);
        canvas.restore();

        if (mIsDrawTipsText)
        {
            canvas.save();
            canvas.translate(mTextX, mTextY);

            mPaint.reset();
            mPaint.setAntiAlias(true);
            mPaint.setShader(mGradient);
            canvas.drawCircle(0, 0, mShadowRadius, mPaint);

            mPaint.reset();
            mPaint.setAntiAlias(true);
            mPaint.setFilterBitmap(true);
            mPaint.setColor(Color.WHITE);
            canvas.drawCircle(0, 0, mTipsCirCleWH / 2f, mPaint);
            canvas.restore();

            canvas.save();
            mPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12, getResources().getDisplayMetrics()));
            mPaint.setTypeface(Typeface.DEFAULT);
            mPaint.setColor(Color.BLACK);
            canvas.translate(mTextX, mTextY + mDurationTextRect.height() / 2f);
            mPaint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(mTips, 0, 0, mPaint);
            canvas.restore();
        }
    }

    public void resetStatus()
    {
        if (mIsTouch)
        {
            mProgressChangeListener.onStopTouch((long) (mDuration * mRatio));
            mIsDrawTipsText = false;
            mIsTouch = false;
        }
    }

    public void setProgressAlpha(boolean alpha)
    {
        mIsProgressAlpha = alpha;
        mUIEnable = alpha;
    }

    public void setProgressShow(boolean show)
    {
        mIsProgressShow = show;
        mUIEnable = show;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if (mUIEnable && mProgressChangeListener != null)
        {
            float x = mIsTransXToDrawCircle ? event.getX() - mCircleWH / 2f : event.getX();

            float ratio = x * 1f / mWidth;

            if (ratio > 1)
            {
                ratio = 1;
            }
            else if (ratio < 0)
            {
                ratio = 0;
            }

            switch (event.getAction())
            {
                case MotionEvent.ACTION_DOWN:
                {
                    if (event.getY() < getMeasuredHeight() - mTouchHeight)
                    {
                        return true;
                    }
                    mIsTouch = true;
                    mIsDrawTipsText = true;
                    long pos = (long) (mDuration * ratio);
                    mProgressChangeListener.onStartTouch(pos);
                    mTips = getDurationString(pos);
                    setDurationTextWHRect(mTips);
                    updateTextLocation(event);
                    setProgressInTouch(ratio * 100);
                    break;
                }

                case MotionEvent.ACTION_MOVE:
                {
                    if (event.getY() < getMeasuredHeight() - mTouchHeight)
                    {
                        return true;
                    }
                    long pos = (long) (mDuration * ratio);
                    mProgressChangeListener.onProgressChanged(pos);
                    mTips = getDurationString(pos);
                    setDurationTextWHRect(mTips);
                    updateTextLocation(event);
                    setProgressInTouch(ratio * 100);
                    break;
                }
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_OUTSIDE:
                case MotionEvent.ACTION_UP:
                {
                    if (mIsTouch)
                    {
                        mProgressChangeListener.onStopTouch((long) (mDuration * mRatio));
                    }
                    mIsDrawTipsText = false;
                    mIsTouch = false;
                    break;
                }
            }

            return true;
        }
        return super.onTouchEvent(event);
    }

    public void updateTextLocation(MotionEvent event)
    {
        mTextX = event.getX();
        mTextY = mHeight - mCircleWH - CameraPercentUtil.HeightPxToPercent(46) - mTipsCirCleWH / 2f;

        if (mIsTransXToDrawCircle)
        {
            if (mTextX <= mTipsCirCleWH / 2f + mCircleWH / 2f)
            {
                mTextX = mTipsCirCleWH / 2f + mCircleWH / 2f;
            }
            else if (mTextX >= mWidth + mCircleWH / 2f - mTipsCirCleWH / 2f)
            {
                mTextX = mWidth + mCircleWH / 2f - mTipsCirCleWH / 2f;
            }
        }
        else
        {
            if (mTextX <= mTipsCirCleWH / 2f)
            {
                mTextX = mTipsCirCleWH / 2f;
            }
            else if (mTextX >= mWidth - mTipsCirCleWH / 2f)
            {
                mTextX = mWidth - mTipsCirCleWH / 2f;
            }
        }
    }

    private void setDurationTextWHRect(String text)
    {
        mDurationTextRect.setEmpty();
        mPaint.reset();
        mPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12, getResources().getDisplayMetrics()));
        mPaint.setTypeface(Typeface.DEFAULT);
        mPaint.getTextBounds(text, 0, text.length(), mDurationTextRect);
    }

    public void setOnProgressChangeListener(OnProgressChangeListener listener)
    {
        mProgressChangeListener = listener;
    }

    interface OnProgressChangeListener
    {
        void onStartTouch(long pos);

        void onProgressChanged(long pos);

        void onStopTouch(long pos);
    }
}
