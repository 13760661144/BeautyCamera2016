package cn.poco.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.support.annotation.ColorInt;
import android.widget.FrameLayout;

import cn.poco.camera3.util.CameraPercentUtil;
import cn.poco.tianutils.ShareData;
import cn.poco.view.material.VerFilterViewEx;

/**
 * 萌妆照预览控件
 * Created by Gxx on 2017/9/29.
 */

public class PictureView extends VerFilterViewEx
{
    private int mRatioTopMargin = -1;
    private int mTmpRatioTopMargin = -1;

    protected int mParentDefHeight;
    protected int mParentShrinkHeight;

    protected boolean isNeedDoShrink;
    protected boolean isDoRatioTopMarginAnim;

    @ColorInt
    protected int mBackColor = 0xfff0f0f0;

    public PictureView(Context context)
    {
        super(context);

        mParentDefHeight = ShareData.m_screenRealHeight;
        mParentShrinkHeight = ShareData.m_screenRealHeight - CameraPercentUtil.HeightPxToPercent(320);
    }

    public void updateHeight(int def, int shrink, boolean isNeedDoShrink)
    {
        mParentDefHeight = def;
        mParentShrinkHeight = shrink;
        this.isNeedDoShrink = isNeedDoShrink;
    }

    public void setDoRatioTopMarginAnim(boolean doRatioTopMarginAnim)
    {
        isDoRatioTopMarginAnim = doRatioTopMarginAnim;
    }

    public void setRatioTopMargin(int mRatioTopMargin)
    {
        this.mRatioTopMargin = mRatioTopMargin;
        this.mTmpRatioTopMargin = mRatioTopMargin;
    }

    public void setBackColor(@ColorInt int mBackColor)
    {
        this.mBackColor = mBackColor;
        this.invalidate();
    }

    @Override
    protected void updateContent(int width, int height)
    {
        if (img == null || img.m_bmp == null) return;
        // 计算 view 宽高改变后与图片宽高的比例，取min
        float scaleA = getSpecificScale(img.m_bmp.getWidth(), img.m_bmp.getHeight(), width, height, false);

        mCanvasX = (width - img.m_bmp.getWidth() * scaleA) / 2f;
        mCanvasY = (height - img.m_bmp.getHeight() * scaleA) / 2f;

        if (mTmpRatioTopMargin >= 0)
        {
            mCanvasY = mTmpRatioTopMargin;
        }

        img.m_matrix.reset();
        img.m_matrix.postScale(scaleA, scaleA);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        canvas.save();
        canvas.drawColor(mBackColor);
        canvas.restore();
        super.onDraw(canvas);
    }

    public void doAnim(boolean shrink)
    {
        if (!this.isNeedDoShrink)
        {
            return;
        }

        final int dy = shrink ? mParentShrinkHeight - mParentDefHeight : mParentDefHeight - mParentShrinkHeight;
        final int height = shrink ? mParentDefHeight : mParentShrinkHeight;
        final boolean finalShrink = shrink;

        ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
        animator.setDuration(300);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                float value = (float) animation.getAnimatedValue();
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) getLayoutParams();
                params.height = (int) (height + dy * value);
                if (isDoRatioTopMarginAnim) {
                    if (finalShrink)
                    {
                        mTmpRatioTopMargin = (int) (mRatioTopMargin * (1f - value));
                    } else
                    {
                        mTmpRatioTopMargin = (int) (mRatioTopMargin * value);
                    }
                }
                requestLayout();
            }
        });
        animator.start();
    }

    public boolean isDefHeight()
    {
        return getMeasuredHeight() == mParentDefHeight;
    }

    @Override
    public void setDrawWaterMark(boolean drawWaterMask)
    {
        mDrawWaterMark = drawWaterMask;
    }

    @Override
    public boolean AddWaterMark(Bitmap waterMask, boolean isNonItem)
    {
        return super.AddWaterMark(waterMask, isNonItem);
    }
}
