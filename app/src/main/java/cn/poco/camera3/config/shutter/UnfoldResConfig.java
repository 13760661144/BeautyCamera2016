package cn.poco.camera3.config.shutter;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;

import cn.poco.advanced.ImageUtils;
import cn.poco.camera3.ui.shutter.Ring;
import cn.poco.camera3.util.CameraPercentUtil;
import my.beautyCamera.R;

/**
 * @author Gxx
 *         Created by Gxx on 2017/8/15.
 *         展开素材列表时 四种类型快门 配置
 */

public class UnfoldResConfig extends BaseConfig
{
    public UnfoldResConfig(Context context, View view)
    {
        super(context, view);
    }

    @Override
    protected void initGifData()
    {
        mGifRing = new Ring();
        mGifRing.mShutterDiameter = CameraPercentUtil.WidthPxToPercent(110);
        mGifRing.mOffsetY = CameraPercentUtil.WidthPxToPercent(15);

        mGifRing.mInCircleColor = 0xffffcc00;
        mGifRing.mInCircleRadius = CameraPercentUtil.WidthPxToPercent(45);
        mGifRing.setInnerRoundRect(-1, -1);

        mGifRing.mRingIsDrawArc = false;
        mGifRing.mRingColor = 0xffffffff;
        mGifRing.mRingRadius = CameraPercentUtil.WidthPxToPercent(55);
        mGifRing.setRingRectF();

        mGifRing.mMidText = mContext.getString(R.string.sticker_shutter_gif_text);
        mGifRing.mTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 14, mContext.getResources().getDisplayMetrics());
        mGifRing.mTextColor = 0xffffffff;

        mGifRing.mIsDrawShadow = true;
        mGifRing.mShadowCenColor = 0x26000000;
        mGifRing.mShadowEdgeColor = 0x00000000;
        mGifRing.mShadowRadius = CameraPercentUtil.WidthPxToPercent(65);
    }

    @Override
    protected void initPhotoData()
    {
    }

    @Override
    protected void init169MakeupData()
    {
        mMakeupRing169 = new Ring();
        mMakeupRing169.mShutterDiameter = CameraPercentUtil.WidthPxToPercent(110);
        mMakeupRing169.mOffsetY = CameraPercentUtil.WidthPxToPercent(15);

        mMakeupRing169.mInCircleColor = ImageUtils.GetSkinColor();
        mMakeupRing169.mInCircleRadius = CameraPercentUtil.WidthPxToPercent(45);
        mMakeupRing169.setInnerRoundRect(-1, -1);

        mMakeupRing169.mRingColor = 0xffffffff;
        mMakeupRing169.mRingRadius = CameraPercentUtil.WidthPxToPercent(50);
        mMakeupRing169.mRingWidth = CameraPercentUtil.WidthPxToPercent(10);
        mMakeupRing169.setRingRectF();

        mMakeupRing169.mIsDrawShadow = true;
        mMakeupRing169.mShadowCenColor = 0x26000000;
        mMakeupRing169.mShadowEdgeColor = 0x00000000;
        mMakeupRing169.mShadowRadius = CameraPercentUtil.WidthPxToPercent(65);
    }

    @Override
    protected void init169VideoData()
    {
        mVideoRing169 = new Ring();
        mVideoRing169.mShutterDiameter = CameraPercentUtil.WidthPxToPercent(110);
        mVideoRing169.mOffsetY = CameraPercentUtil.WidthPxToPercent(15);

        mVideoRing169.mInCircleColor = 0xfffc3745;
        mVideoRing169.mInCircleRadius = CameraPercentUtil.WidthPxToPercent(45);
        mVideoRing169.setInnerRoundRect(-1, -1);

        mVideoRing169.mRingIsDrawArc = false;
        mVideoRing169.mRingColor = 0xffffffff;
        mVideoRing169.mRingRadius = CameraPercentUtil.WidthPxToPercent(55);

        mVideoRing169.mIsDrawShadow = true;
        mVideoRing169.mShadowCenColor = 0x26000000;
        mVideoRing169.mShadowEdgeColor = 0x00000000;
        mVideoRing169.mShadowRadius = CameraPercentUtil.WidthPxToPercent(65);
        mVideoRing169.mVideoLogoWH = CameraPercentUtil.WidthPxToPercent(40);
    }

    @Override
    protected void init43MakeupData()
    {
        mMakeupRing43 = new Ring();
        mMakeupRing43.mShutterDiameter = CameraPercentUtil.WidthPxToPercent(110);
        mMakeupRing43.mOffsetY = CameraPercentUtil.WidthPxToPercent(15);

        mMakeupRing43.mInCircleColor = ImageUtils.GetSkinColor();
        mMakeupRing43.mInCircleRadius = CameraPercentUtil.WidthPxToPercent(45);
        mMakeupRing43.setInnerRoundRect(-1, -1);

        mMakeupRing43.mRingColor = 0xffffffff;
        mMakeupRing43.mRingRadius = CameraPercentUtil.WidthPxToPercent(50);
        mMakeupRing43.mRingWidth = CameraPercentUtil.WidthPxToPercent(10);
        mMakeupRing43.setRingRectF();

        mMakeupRing43.mIsDrawShadow = true;
        mMakeupRing43.mShadowCenColor = 0x26000000;
        mMakeupRing43.mShadowEdgeColor = 0x00000000;
        mMakeupRing43.mShadowRadius = CameraPercentUtil.WidthPxToPercent(65);
    }

    @Override
    protected void init43VideoData()
    {
        mVideoRing43 = new Ring();
        mVideoRing43.mShutterDiameter = CameraPercentUtil.WidthPxToPercent(110);
        mVideoRing43.mOffsetY = CameraPercentUtil.WidthPxToPercent(15);

        mVideoRing43.mInCircleColor = 0xfffc3745;
        mVideoRing43.mInCircleRadius = CameraPercentUtil.WidthPxToPercent(45);
        mVideoRing43.setInnerRoundRect(-1, -1);

        mVideoRing43.mRingIsDrawArc = false;
        mVideoRing43.mRingColor = 0xffffffff;
        mVideoRing43.mRingRadius = CameraPercentUtil.WidthPxToPercent(55);

        mVideoRing43.mIsDrawShadow = true;
        mVideoRing43.mShadowCenColor = 0x26000000;
        mVideoRing43.mShadowEdgeColor = 0x00000000;
        mVideoRing43.mShadowRadius = CameraPercentUtil.WidthPxToPercent(65);
        mVideoRing43.mVideoLogoWH = CameraPercentUtil.WidthPxToPercent(40);
    }

    @Override
    protected void initPauseRing43()
    {
        mVideoPauseRing43 = new Ring();
        mVideoPauseRing43.mShutterDiameter = CameraPercentUtil.WidthPxToPercent(110);
        mVideoPauseRing43.mOffsetY = CameraPercentUtil.WidthPxToPercent(15);

        mVideoPauseRing43.mInCircleColor = 0xfffc3745;
        mVideoPauseRing43.mInCircleRadius = CameraPercentUtil.WidthPxToPercent(45);
        mVideoPauseRing43.setInnerRoundRect(-1, -1);

        mVideoPauseRing43.mRingIsDrawArc = false;
        mVideoPauseRing43.mRingColor = 0xffffffff;
        mVideoPauseRing43.mRingRadius = CameraPercentUtil.WidthPxToPercent(55);

        mVideoPauseRing43.mProgressColor = 0xfffc3745;
        mVideoPauseRing43.mProgressRadius = CameraPercentUtil.WidthPxToPercent(53);
        mVideoPauseRing43.mProgressWidth = CameraPercentUtil.WidthPxToPercent(4);
        mVideoPauseRing43.setProgressRect();

        mVideoPauseRing43.mIsDrawShadow = true;
        mVideoPauseRing43.mShadowCenColor = 0x26000000;
        mVideoPauseRing43.mShadowEdgeColor = 0x00000000;
        mVideoPauseRing43.mShadowRadius = CameraPercentUtil.WidthPxToPercent(65);
    }

    @Override
    protected void initPauseRing169()
    {
        mVideoPauseRing169 = new Ring();
        mVideoPauseRing169.mShutterDiameter = CameraPercentUtil.WidthPxToPercent(110);
        mVideoPauseRing169.mOffsetY = CameraPercentUtil.WidthPxToPercent(15);

        mVideoPauseRing169.mInCircleColor = 0xfffc3745;
        mVideoPauseRing169.mInCircleRadius = CameraPercentUtil.WidthPxToPercent(45);
        mVideoPauseRing169.setInnerRoundRect(-1, -1);

        mVideoPauseRing169.mRingIsDrawArc = false;
        mVideoPauseRing169.mRingColor = 0xffffffff;
        mVideoPauseRing169.mRingRadius = CameraPercentUtil.WidthPxToPercent(55);

        mVideoPauseRing169.mProgressColor = 0xfffc3745;
        mVideoPauseRing169.mProgressRadius = CameraPercentUtil.WidthPxToPercent(53);
        mVideoPauseRing169.mProgressWidth = CameraPercentUtil.WidthPxToPercent(4);
        mVideoPauseRing169.setProgressRect();

        mVideoPauseRing169.mIsDrawShadow = true;
        mVideoPauseRing169.mShadowCenColor = 0x26000000;
        mVideoPauseRing169.mShadowEdgeColor = 0x00000000;
        mVideoPauseRing169.mShadowRadius = CameraPercentUtil.WidthPxToPercent(65);
    }
}
