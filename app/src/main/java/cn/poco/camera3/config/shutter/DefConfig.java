package cn.poco.camera3.config.shutter;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.TypedValue;
import android.view.View;

import cn.poco.advanced.ImageUtils;
import cn.poco.camera.CameraConfig;
import cn.poco.camera3.ui.shutter.Ring;
import cn.poco.camera3.util.CameraPercentUtil;
import cn.poco.tianutils.ShareData;
import my.beautyCamera.R;

/**
 * @author Gxx
 *         Created by Gxx on 2017/8/15.
 *         四种类型快门的 默认模式 配置
 */

public class DefConfig extends BaseConfig
{
    public DefConfig(Context context, View view)
    {
        super(context, view);
    }

    @Override
    public void AutoAdaptionDynamicUI(boolean needAdaption)
    {
        autoAdaptionDynamicUI(mGifRing, needAdaption);
        autoAdaptionDynamicUI(mPhotoRing, needAdaption);
        autoAdaptionDynamicUI(mMakeupRing43, needAdaption);
        autoAdaptionDynamicUI(mVideoRing43, needAdaption);
    }

    @Override
    public void AutoAdaptionStaticUI(boolean needAdaption)
    {
        autoAdaptionStaticUI(mGifRing, needAdaption);
        autoAdaptionStaticUI(mPhotoRing, needAdaption);
        autoAdaptionStaticUI(mMakeupRing43, needAdaption);
        autoAdaptionStaticUI(mVideoRing43, needAdaption);
    }

    @Override
    protected void initGifData()//gif 1:1 参数
    {
        mGifRing = new Ring();
        mGifRing.mShutterDiameter = CameraPercentUtil.WidthPxToPercent(146);
        mGifRing.mOffsetY = CameraPercentUtil.WidthPxToPercent(115);

        mGifRing.mInCircleColor = 0xffffcc00;
        mGifRing.mInCircleRadius = CameraPercentUtil.WidthPxToPercent(59);
        mGifRing.setInnerRoundRect(-1, -1);

        mGifRing.mRingIsDrawArc = false;
        mGifRing.mRingColor = 0xfff5f5f5;
        mGifRing.mRingRadius = CameraPercentUtil.WidthPxToPercent(73);
        mGifRing.setRingRectF();

        mGifRing.mMidText = mContext.getString(R.string.sticker_shutter_gif_text);
        mGifRing.mTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, mContext.getResources().getDisplayMetrics());
        mGifRing.mTextColor = 0xffffffff;
    }

    @Override
    protected void initPhotoData()//拍照 4:3 参数
    {
        mPhotoRing = new Ring();
        mPhotoRing.mShutterDiameter = CameraPercentUtil.WidthPxToPercent(136);
        mPhotoRing.mOffsetY = CameraPercentUtil.HeightPxToPercent((int) (ShareData.getScreenW() * CameraConfig.PreviewRatio.Ratio_16_9) > ShareData.m_screenRealHeight && Build.VERSION.SDK_INT < 18 ? 100 : 120);

        mPhotoRing.mIsDrawInner = false;
        mPhotoRing.mInCircleColor = Color.TRANSPARENT;
        mPhotoRing.mInCircleRadius = CameraPercentUtil.WidthPxToPercent(58);

        mPhotoRing.mRingColor = ImageUtils.GetSkinColor();
        mPhotoRing.mRingRadius = CameraPercentUtil.WidthPxToPercent(63);
        mPhotoRing.mRingWidth = CameraPercentUtil.WidthPxToPercent(10);
        mPhotoRing.setRingRectF();
    }

    @Override
    protected void init169MakeupData()//萌妆照 16:9 参数
    {
        mMakeupRing169 = new Ring();
        mMakeupRing169.mShutterDiameter = CameraPercentUtil.WidthPxToPercent(146);
        mMakeupRing169.mOffsetY = CameraPercentUtil.WidthPxToPercent(115);

        mMakeupRing169.mInCircleColor = ImageUtils.GetSkinColor();
        mMakeupRing169.mInCircleRadius = CameraPercentUtil.WidthPxToPercent(59);
        mMakeupRing169.setInnerRoundRect(-1, -1);

        mMakeupRing169.mRingColor = 0x99ffffff;
        mMakeupRing169.mRingRadius = CameraPercentUtil.WidthPxToPercent(66);
        mMakeupRing169.mRingWidth = CameraPercentUtil.WidthPxToPercent(14);
        mMakeupRing169.setRingRectF();
    }

    @Override
    protected void init169VideoData()//视频 16:9 参数
    {
        mVideoRing169 = new Ring();
        mVideoRing169.mShutterDiameter = CameraPercentUtil.WidthPxToPercent(146);
        mVideoRing169.mOffsetY = CameraPercentUtil.WidthPxToPercent(115);

        mVideoRing169.mInCircleColor = 0xfffc3745;
        mVideoRing169.mInCircleRadius = CameraPercentUtil.WidthPxToPercent(59);
        mVideoRing169.setInnerRoundRect(-1, -1);

        mVideoRing169.mRingIsDrawArc = false;
        mVideoRing169.mRingColor = 0x99ffffff;
        mVideoRing169.mRingRadius = CameraPercentUtil.WidthPxToPercent(73);
        mVideoRing169.mVideoLogoWH = CameraPercentUtil.WidthPxToPercent(50);
    }

    @Override
    protected void init43MakeupData()//萌妆照 4:3 参数
    {
        mMakeupRing43 = new Ring();

        mMakeupRing43.mShutterDiameter = CameraPercentUtil.WidthPxToPercent(136);
        mMakeupRing43.mOffsetY = CameraPercentUtil.WidthPxToPercent(120);

        mMakeupRing43.mInCircleColor = Color.WHITE;
        mMakeupRing43.mInCircleRadius = CameraPercentUtil.WidthPxToPercent(58);
        mMakeupRing43.mInnerAlpha = 0f;
        mMakeupRing43.setInnerRoundRect(-1, -1);

        mMakeupRing43.mRingColor = ImageUtils.GetSkinColor();
        mMakeupRing43.mRingRadius = CameraPercentUtil.WidthPxToPercent(63);
        mMakeupRing43.mRingWidth = CameraPercentUtil.WidthPxToPercent(10);
        mMakeupRing43.setRingRectF();

    }

    @Override
    protected void init43VideoData()//视频 4:3 参数
    {
        mVideoRing43 = new Ring();
        mVideoRing43.mShutterDiameter = CameraPercentUtil.WidthPxToPercent(146);
        mVideoRing43.mOffsetY = CameraPercentUtil.WidthPxToPercent(115);

        mVideoRing43.mInCircleColor = 0xfffc3745;
        mVideoRing43.mInCircleRadius = CameraPercentUtil.WidthPxToPercent(59);
        mVideoRing43.setInnerRoundRect(-1, -1);

        mVideoRing43.mRingIsDrawArc = false;
        mVideoRing43.mRingColor = 0xfff0f0f0;
        mVideoRing43.mRingRadius = CameraPercentUtil.WidthPxToPercent(73);
        mVideoRing43.mVideoLogoWH = CameraPercentUtil.WidthPxToPercent(50);
    }
}
