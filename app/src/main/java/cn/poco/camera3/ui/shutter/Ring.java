package cn.poco.camera3.ui.shutter;

import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;

public class Ring
{
    /**
     * 圆心
     */
    public PointF mCenter = new PointF();
    /**
     * 快门直径
     */
    public float mShutterDiameter;
    /**
     * 与 view 底部距离
     */
    public float mOffsetY; // 为了计算圆心位置
    /**
     * 与 view 左边距离
     */
    public float mOffsetX; // 为了计算圆心位置

    public boolean mRingIsDrawArc = true;
    /**
     * 圆环半径
     */
    public float mRingRadius;
    /**
     * 圆环宽
     */
    public float mRingWidth;
    RectF mRingRectF = new RectF();

    public void setRingRectF()
    {
        mRingRectF.set(-mRingRadius, -mRingRadius, mRingRadius, mRingRadius);
    }

    /**
     * 圆环颜色
     */
    public int mRingColor;

    public boolean mIsDrawInner = true;
    /**
     * 内圆颜色
     */
    public int mInCircleColor;

    public float mInnerAlpha = 1.0f;
    /**
     * 内圆半径
     */
    public float mInCircleRadius;
    float mInCircleRx = -1; // 圆角矩形
    float mInCircleRy = -1;
    RectF mInnerRoundRect = new RectF();

    public void setInnerRoundRect(float rx, float ry)
    {
        if (rx < 0)
        {
            rx = mInCircleRadius;
        }
        if (ry < 0)
        {
            ry = mInCircleRadius;
        }
        mInnerRoundRect.set(-mInCircleRadius, -mInCircleRadius, mInCircleRadius, mInCircleRadius);
        mInCircleRx = rx;
        mInCircleRy = ry;
    }

    /**
     * 进度条颜色
     */
    public int mProgressColor;
    public int mProgressSelColor;
    public float mProgressWidth;
    public float mProgressRadius;
    RectF mProgressRectF = new RectF();

    public void setProgressRect()
    {
        mProgressRectF.set(-mProgressRadius, -mProgressRadius, mProgressRadius, mProgressRadius);
    }

    /**
     * 快门文本
     */
    public String mMidText;
    /**
     * 快门文本颜色
     */
    public int mTextColor;

    public int mVideoLogoWH;
    /**
     * 快门文本大小
     */
    public float mTextSize;
    Rect mTextRect = new Rect();// 计算字符串长宽

    public boolean mIsDrawShadow = false;
    /**
     * 四周阴影
     */
    public float mShadowRadius;
    /**
     * 阴影主要颜色
     */
    public int mShadowCenColor;
    /**
     * 阴影边缘色
     */
    public int mShadowEdgeColor;

    public int mAdaptionOffsetY = 0;

    public float mAdaptionRadiusScale = 1;

    public int mAdaptionRecordTextOffsetY = 0;

    public Ring copy()
    {
        Ring out = new Ring();
        out.mCenter.x = mCenter.x;
        out.mCenter.y = mCenter.y;

        out.mShutterDiameter = mShutterDiameter;
        out.mOffsetY = mOffsetY;
        out.mOffsetX = mOffsetX;

        out.mProgressRadius = mProgressRadius;
        out.mProgressWidth = mProgressWidth;
        out.mProgressRectF.set(mProgressRectF);
        out.mProgressColor = mProgressColor;
        out.mProgressSelColor = mProgressSelColor;

        out.mRingIsDrawArc = mRingIsDrawArc;
        out.mRingRadius = mRingRadius;
        out.mRingWidth = mRingWidth;
        out.mRingColor = mRingColor;
        out.mRingRectF.set(mRingRectF);

        out.mInCircleColor = mInCircleColor;
        out.mInnerAlpha = mInnerAlpha;
        out.mIsDrawInner = mIsDrawInner;
        out.mInCircleRadius = mInCircleRadius;
        out.mInCircleRx = mInCircleRx;
        out.mInCircleRy = mInCircleRy;
        out.mInnerRoundRect.set(mInnerRoundRect);

        out.mTextColor = mTextColor;
        out.mTextRect.set(mTextRect);
        out.mMidText = mMidText;
        out.mTextSize = mTextSize;

        out.mShadowRadius = mShadowRadius;
        out.mShadowCenColor = mShadowCenColor;
        out.mShadowEdgeColor = mShadowEdgeColor;
        out.mIsDrawShadow = mIsDrawShadow;

        out.mAdaptionOffsetY = mAdaptionOffsetY;
        out.mAdaptionRadiusScale = mAdaptionRadiusScale;
        out.mAdaptionRecordTextOffsetY = mAdaptionRecordTextOffsetY;

        out.mVideoLogoWH = mVideoLogoWH;

        return out;
    }
}
