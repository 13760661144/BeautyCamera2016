package cn.poco.beautifyEyes.Component.Widget;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;

import cn.poco.beauty.view.ColorSeekBar;
import cn.poco.home.home4.utils.PercentUtil;
import cn.poco.tianutils.ShareData;

/**
 * Created by Shine on 2017/2/15.
 */

public class SeekbarLayout extends FrameLayout{

    public ColorSeekBar mSeekBar, mSeekBar2;
    public int mSeekBarMargin = PercentUtil.WidthPxToPercent(80);
    private ColorSeekBar mCurrentDisplayBar;
    private int mLastDisplaySeekbarValue;

    public SeekbarLayout(Context context) {
        super(context);
        initView();
    }
    
    private void initView() {
        mSeekBar = new ColorSeekBar(getContext());
        mSeekBar.setMax(100);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
        layoutParams.leftMargin = layoutParams.rightMargin = mSeekBarMargin;
        mSeekBar.setLayoutParams(layoutParams);
        mCurrentDisplayBar = mSeekBar;
        this.addView(mSeekBar);

        mSeekBar2 = new ColorSeekBar(getContext());
        mSeekBar2.setMax(100);
        FrameLayout.LayoutParams layoutParams1 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
        layoutParams1.leftMargin = layoutParams1.rightMargin = mSeekBarMargin;
        mSeekBar2.setLayoutParams(layoutParams1);
        this.addView(mSeekBar2);
    }

    public void setUpProgressChangeListener(ColorSeekBar.OnSeekBarChangeListener listener) {
        mSeekBar.setOnSeekBarChangeListener(listener);
        mSeekBar2.setOnSeekBarChangeListener(listener);
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int pLeft;
        int pTop = 0;
        int pRight;
        int pBottom;

        if (mSeekBar != null) {
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)mSeekBar.getLayoutParams();
            pLeft = params.leftMargin;
            pTop = (this.getMeasuredHeight() - mSeekBar.getMeasuredHeight()) / 2;
            pRight = pLeft + mSeekBar.getMeasuredWidth();
            pBottom = pTop + mSeekBar.getMeasuredHeight();
            mSeekBar.layout(pLeft, pTop, pRight, pBottom);
        }

        if (mSeekBar2 != null) {
            FrameLayout.LayoutParams params2 = (FrameLayout.LayoutParams)mSeekBar2.getLayoutParams();
            pLeft = ShareData.m_screenRealWidth + params2.leftMargin;
            pRight = pLeft + mSeekBar2.getMeasuredWidth();
            pBottom = pTop + mSeekBar2.getMeasuredHeight();
            mSeekBar2.layout(pLeft, pTop, pRight, pBottom);
        }
    }


    public void fillDisplaySeekbarData(int progress) {
        mCurrentDisplayBar.setProgress(progress);
    }



    public int getDisplaySeekbarProgress() {
       return mCurrentDisplayBar.getProgress();
    }

    public ColorSeekBar getDisplayColorSeekbar() {
        return mCurrentDisplayBar;
    }

    public void startTransitionAnimation(boolean isLeft, int displayValue) {
        mCurrentDisplayBar = isLeft ? mSeekBar2 : mSeekBar;

        int startValue;
        int endValue;

        if (isLeft) {
            startValue = 0;
            endValue = -ShareData.m_screenWidth;
            mSeekBar.setProgress(mLastDisplaySeekbarValue);
            mSeekBar2.setProgress(displayValue);
        } else {
            startValue = -ShareData.m_screenWidth;
            endValue = 0;
            mSeekBar.setProgress(displayValue);
            mSeekBar2.setProgress(mLastDisplaySeekbarValue);
        }
        mLastDisplaySeekbarValue = displayValue;

        ObjectAnimator translationAnimator =  ObjectAnimator.ofFloat(mSeekBar, "translationX", startValue, endValue);
        ObjectAnimator translationAnimator2 =  ObjectAnimator.ofFloat(mSeekBar2, "translationX", startValue, endValue);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(translationAnimator, translationAnimator2);
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorSet.setDuration(350);
        animatorSet.start();
    }


    public int getSeekbarWidth() {
        return ShareData.m_screenWidth - (mSeekBarMargin * 2);
    }

    public void updateLastDisplayValue(int value) {
        this.mLastDisplaySeekbarValue = value;
    }

}
