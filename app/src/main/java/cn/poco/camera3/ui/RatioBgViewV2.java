package cn.poco.camera3.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;

import cn.poco.camera3.util.CameraPercentUtil;
import cn.poco.camera3.util.RatioBgUtils;
import cn.poco.tianutils.ShareData;
import my.beautyCamera.R;

/**
 * @author Gxx
 *         Created by Gxx on 2017/8/14.
 */

public class RatioBgViewV2 extends View
{
    private RectF mRectF;
    private Paint mPaint;
    private float mRatio;
    private int mBgColor;
    private int mTopBgHeight;
    private int mBottomBgHeight;
    private int mViewHeight;
    private int mMaskAlpha;
    private boolean mIsDrawFocus = false;
    private Bitmap mFocusBmp;
    private Matrix mFocusMatrix;
    private int mFocusWH;
    private float mFocusX;
    private float mFocusY;
    private int mMaskColor;

    public RatioBgViewV2(Context context)
    {
        super(context);
        initConfig();
        initBmp();
    }

    public void clearMemory()
    {
        if (mFocusBmp != null && !mFocusBmp.isRecycled())
        {
            mFocusBmp.recycle();
            mFocusBmp = null;
        }
    }

    private void initBmp()
    {
        if (mFocusBmp == null || mFocusBmp.isRecycled())
        {
            mFocusBmp = BitmapFactory.decodeResource(getResources(), R.drawable.camera_focus_metering);
        }

        mFocusMatrix = new Matrix();
        mFocusWH = CameraPercentUtil.WidthPxToPercent(110);
    }

    private void initConfig()
    {
        mPaint = new Paint();
        mBgColor = 0xffffffff;
        mMaskAlpha = 255;
        mMaskColor = Color.BLACK;
    }

    public void setBgColor(int bgColor)
    {
        mBgColor = bgColor;
    }

    public void setRatio(float ratio)
    {
        mRatio = ratio;
        mTopBgHeight = RatioBgUtils.GetTopHeightByRatio(ratio);
        mBottomBgHeight = RatioBgUtils.GetBottomHeightByRation(ratio);
        invalidate();
    }

    public void setFocusLocation(float fx, float fy)
    {
        mIsDrawFocus = true;
        mFocusX = fx;
        mFocusY = fy;
        invalidate();
    }

    public void setFocusFinish()
    {
        if (mIsDrawFocus)
        {
            mIsDrawFocus = false;
            invalidate();
        }
    }

    private float checkBound(float value, float min, float max)
    {
        if (value < min)
        {
            value = min;
        }
        else if (value > max)
        {
            value = max;
        }
        return value;
    }

    public int getDisplayAreaCenterY(float ratio)
    {
        int top = RatioBgUtils.GetTopHeightByRatio(ratio);
        int bottom = RatioBgUtils.GetBottomHeightByRation(ratio);
        return (int) ((ShareData.m_screenRealHeight - top - bottom) / 2f + top);
    }

    public void DoMaskDismissAnim()
    {
        ValueAnimator anim = ValueAnimator.ofInt(255, 0);
        anim.setInterpolator(new LinearInterpolator());
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                mMaskAlpha = (int) animation.getAnimatedValue();
                invalidate();
            }
        });
        anim.addListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                if (mOnRatioChangeListener != null)
                {
                    mOnRatioChangeListener.onDismissMaskEnd();
                }
            }
        });
        anim.setDuration(500);
        anim.start();
    }

    public void showSplashMask()
    {
        mMaskColor = Color.WHITE;

        ValueAnimator valueAnimator = ValueAnimator.ofInt(0, 255, 0);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.setDuration(250);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                mMaskAlpha = (int) animation.getAnimatedValue();
                invalidate();
            }
        });

        valueAnimator.addListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                if (mOnRatioChangeListener != null)
                {
                    mOnRatioChangeListener.onSplashMaskEnd();
                }
            }
        });
        valueAnimator.start();
    }

    public void showBlackMask()
    {
        mMaskAlpha = 255;
        mMaskColor = Color.BLACK;
        invalidate();
    }

    public void DoChangedRatioAnim(float nextRatio)
    {
        mIsDrawFocus = false;
        mRatio = nextRatio;
        showBlackMask();

        int nextRatioTopH = RatioBgUtils.GetTopHeightByRatio(nextRatio);
        int nextRatioBottomH = RatioBgUtils.GetBottomHeightByRation(nextRatio);

        ValueAnimator topAnim = ValueAnimator.ofInt(mTopBgHeight, nextRatioTopH);
        topAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                mTopBgHeight = (int) animation.getAnimatedValue();
                invalidate();
            }
        });

        ValueAnimator bottomAnim = ValueAnimator.ofInt(mBottomBgHeight, nextRatioBottomH);
        bottomAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                mBottomBgHeight = (int) animation.getAnimatedValue();
                invalidate();
            }
        });

        AnimatorSet set = new AnimatorSet();
        set.playTogether(topAnim, bottomAnim);
        set.setDuration(300);
        set.addListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                if (mOnRatioChangeListener != null)
                {
                    mOnRatioChangeListener.onRatioChange(mRatio);
                }
            }
        });
        set.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if (mMaskAlpha > 0)
        {
            return true;
        }
        if (mBottomBgHeight > 0)
        {
            int top = getMeasuredHeight() - mBottomBgHeight;
            if (event.getY() > top)
            {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);
        mViewHeight = h;
        if (mRectF == null)
        {
            mRectF = new RectF(0, 0, w, 0);
        }
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        canvas.save();
        if (mTopBgHeight > 0)
        {
            mRectF.top = 0;
            mRectF.bottom = mTopBgHeight;
            mPaint.reset();
            mPaint.setColor(mBgColor);
            canvas.drawRect(mRectF, mPaint);
        }

        if (mBottomBgHeight > 0)
        {
            mRectF.top = mViewHeight - mBottomBgHeight;
            mRectF.bottom = mViewHeight;
            mPaint.reset();
            mPaint.setColor(mBgColor);
            canvas.drawRect(mRectF, mPaint);
        }

        mRectF.top = mTopBgHeight;
        mRectF.bottom = mViewHeight - mBottomBgHeight;
        mPaint.reset();
        mPaint.setColor(mMaskColor);
        mPaint.setAlpha(mMaskAlpha);
        canvas.drawRect(mRectF, mPaint);
        canvas.restore();

        if (mIsDrawFocus && mMaskAlpha == 0)
        {
            canvas.save();
            mPaint.reset();
            mPaint.setAntiAlias(true);
            mPaint.setFilterBitmap(true);
            mFocusMatrix.reset();
            mFocusMatrix.postScale(mFocusWH * 1f / mFocusBmp.getWidth(), mFocusWH * 1f / mFocusBmp.getHeight());
            float x = checkBound(mFocusX, mFocusWH / 2f, getMeasuredWidth() - mFocusWH / 2f);
            float y = checkBound(mFocusY, mTopBgHeight + mFocusWH / 2f, getMeasuredHeight() - mBottomBgHeight - mFocusWH / 2f);
            canvas.translate(x - mFocusWH / 2f, y - mFocusWH / 2f);
            canvas.drawBitmap(mFocusBmp, mFocusMatrix, mPaint);
            canvas.restore();
        }
    }

    private OnRatioChangeListener mOnRatioChangeListener;

    public void SetOnRatioChangeListener(OnRatioChangeListener listener)
    {
        mOnRatioChangeListener = listener;
    }

    public interface OnRatioChangeListener
    {
        void onRatioChange(float ratio);

        void onDismissMaskEnd();

        void onSplashMaskEnd();
    }
}
