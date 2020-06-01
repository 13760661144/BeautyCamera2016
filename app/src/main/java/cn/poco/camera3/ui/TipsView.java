package cn.poco.camera3.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PointF;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import cn.poco.advanced.ImageUtils;
import cn.poco.camera.CameraConfig;
import cn.poco.camera3.cb.AnimationCBAdapter;
import cn.poco.camera3.config.shutter.ShutterConfig;
import cn.poco.camera3.util.CameraPercentUtil;
import cn.poco.camera3.util.LanguageUtil;
import cn.poco.camera3.util.RatioBgUtils;
import cn.poco.tianutils.ShareData;
import my.beautyCamera.R;

/**
 * Created by Gxx on 2017/08/31 12:04.<br/><br/>
 * 功能使用指引提示
 */
public class TipsView extends FrameLayout
{
    private final boolean mIsChinese;
    private FrameLayout mVideoLogoTipsLayout;

    private ImageView mCircle;

    private FrameLayout mShutterTipsLayout;

    private FrameLayout mDurationTipsLayout;

    private FrameLayout mRatioTipsLayout;

    private FrameLayout mBeautyTipsLayout;

    private int mTabType;
    private boolean mIsDoingAnim;
    private float mPreviewRatio;
    private long mAnimDuration = 300;
    private float mFullScreenRatio;

    public TipsView(Context context)
    {
        super(context);
        this.setClickable(true);
        this.setLongClickable(true);

        mIsChinese = LanguageUtil.checkSystemLanguageIsChinese(context);
        mFullScreenRatio = ShareData.m_screenRealHeight * 1.0f / ShareData.m_screenRealWidth;
        initView();
    }

    public boolean isAlive()
    {
        return getVisibility() == VISIBLE && getAlpha() == 1;
    }

    private void initView()
    {
        mRatioTipsLayout = new FrameLayout(getContext());
        mRatioTipsLayout.setAlpha(0);
        FrameLayout.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        addView(mRatioTipsLayout, params);
        {
            ImageView iv = new ImageView(getContext());
            iv.setImageResource(mIsChinese ? R.drawable.camera_video_ratio_tips2 : R.drawable.camera_video_ratio_tips2);
            params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER_HORIZONTAL;
            mRatioTipsLayout.addView(iv, params);
            ImageUtils.AddSkin(getContext(), iv);

            TextView ratioTips = new TextView(getContext());
            ratioTips.setTextColor(Color.WHITE);
            ratioTips.setTextSize(TypedValue.COMPLEX_UNIT_DIP, mIsChinese ? 14 : 12);
            ratioTips.setEllipsize(TextUtils.TruncateAt.END);
            ratioTips.setSingleLine(mIsChinese);
            ratioTips.setText(R.string.camera_use_tips_ratio_text);
            ratioTips.setGravity(Gravity.CENTER);
            ratioTips.setPadding(0, 0, 0, CameraPercentUtil.WidthPxxToPercent(mIsChinese ? 0 : 2));
            params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER;
            params.topMargin = CameraPercentUtil.HeightPxToPercent(10);
            mRatioTipsLayout.addView(ratioTips, params);
        }

        mBeautyTipsLayout = new FrameLayout(getContext());
        mBeautyTipsLayout.setAlpha(0);
        params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        addView(mBeautyTipsLayout, params);
        {
            ImageView iv = new ImageView(getContext());
            iv.setImageResource(mIsChinese ? R.drawable.camera_video_beauty_tips2 : R.drawable.camera_video_beauty_tips2);
            params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER_HORIZONTAL;
            mBeautyTipsLayout.addView(iv, params);
            ImageUtils.AddSkin(getContext(), iv);

            TextView ratioTips = new TextView(getContext());
            ratioTips.setTextColor(Color.WHITE);
            ratioTips.setTextSize(TypedValue.COMPLEX_UNIT_DIP, mIsChinese ? 14 : 12);
            ratioTips.setEllipsize(TextUtils.TruncateAt.END);
            ratioTips.setSingleLine(mIsChinese);
            ratioTips.setText(R.string.camera_use_tip_beauty_text);
            ratioTips.setGravity(Gravity.CENTER);
            ratioTips.setPadding(0, 0, 0, CameraPercentUtil.WidthPxxToPercent(mIsChinese ? 0 : 2));
            params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
            params.topMargin = CameraPercentUtil.HeightPxToPercent(20);
            mBeautyTipsLayout.addView(ratioTips, params);
        }

        mCircle = new ImageView(getContext());
        mCircle.setVisibility(GONE);
        mCircle.setImageResource(R.drawable.camera_use_tips_circle);
        ImageUtils.AddSkin(getContext(), mCircle);
        params = new LayoutParams(CameraPercentUtil.WidthPxToPercent(214), CameraPercentUtil.HeightPxToPercent(94));
        params.gravity = Gravity.BOTTOM;
        params.bottomMargin = CameraPercentUtil.HeightPxToPercent(10);
        addView(mCircle, params);

        mVideoLogoTipsLayout = new FrameLayout(getContext());
        mVideoLogoTipsLayout.setAlpha(0);
        params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.BOTTOM;
        params.bottomMargin = CameraPercentUtil.HeightPxToPercent(105);
        addView(mVideoLogoTipsLayout, params);
        {
            ImageView iv = new ImageView(getContext());
            iv.setImageResource(mIsChinese ? R.drawable.camera_video_logo_tips : R.drawable.camera_video_logo_tips_en);
            params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER_HORIZONTAL;
            mVideoLogoTipsLayout.addView(iv, params);
            ImageUtils.AddSkin(getContext(), iv);

            TextView videoLogoTips = new TextView(getContext());
            videoLogoTips.setTextColor(Color.WHITE);
            videoLogoTips.setTextSize(TypedValue.COMPLEX_UNIT_DIP, mIsChinese ? 14 : 12);
            videoLogoTips.setText(R.string.camera_use_tips_video_logo_text);
            videoLogoTips.setGravity(Gravity.CENTER);
            params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER;
            params.bottomMargin = CameraPercentUtil.HeightPxToPercent(mIsChinese ? 7 : 9);
            params.leftMargin = CameraPercentUtil.WidthPxToPercent(1);
            mVideoLogoTipsLayout.addView(videoLogoTips, params);
        }

        mDurationTipsLayout = new FrameLayout(getContext());
        mDurationTipsLayout.setAlpha(0);
        params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        addView(mDurationTipsLayout, params);
        {
            ImageView iv = new ImageView(getContext());
            iv.setImageResource(mIsChinese ? R.drawable.camera_video_duration_tips : R.drawable.camera_video_duration_tips);
            params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER_HORIZONTAL;
            mDurationTipsLayout.addView(iv, params);
            ImageUtils.AddSkin(getContext(), iv);

            TextView durationTips = new TextView(getContext());
            durationTips.setTextColor(Color.WHITE);
            durationTips.setTextSize(TypedValue.COMPLEX_UNIT_DIP, mIsChinese ? 14 : 12);
            durationTips.setEllipsize(TextUtils.TruncateAt.END);
            durationTips.setText(R.string.camera_use_tips_duration_text);
            durationTips.setGravity(Gravity.CENTER);
            params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER;
            params.bottomMargin = CameraPercentUtil.HeightPxToPercent(11);
            mDurationTipsLayout.addView(durationTips, params);
        }

        mShutterTipsLayout = new FrameLayout(getContext());
        mShutterTipsLayout.setAlpha(0);
        params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        params.bottomMargin = CameraPercentUtil.HeightPxToPercent(250);
        addView(mShutterTipsLayout, params);
        {
            ImageView iv = new ImageView(getContext());
            iv.setImageResource(mIsChinese ? R.drawable.camera_shutter_tips_bk : R.drawable.camera_shutter_tips_bk_en);
            params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER_HORIZONTAL;
            mShutterTipsLayout.addView(iv, params);
            ImageUtils.AddSkin(getContext(), iv);

            TextView shutterTips = new TextView(getContext());
            shutterTips.setTextColor(Color.WHITE);
            shutterTips.setTextSize(TypedValue.COMPLEX_UNIT_DIP, mIsChinese ? 14 : 12);
            shutterTips.setEllipsize(TextUtils.TruncateAt.END);
            shutterTips.setSingleLine(mIsChinese);
            shutterTips.setText(R.string.camera_use_tips_shutter_text);
            shutterTips.setGravity(Gravity.CENTER);
            params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER;
            params.bottomMargin = CameraPercentUtil.HeightPxToPercent(11);
            params.leftMargin = CameraPercentUtil.WidthPxToPercent(1);
            mShutterTipsLayout.addView(shutterTips, params);
        }
    }

    public void setStickerLogoLocation(PointF location)
    {
        if (location == null) return;

        if (mCircle != null)
        {
            mCircle.setTranslationX(location.x - CameraPercentUtil.WidthPxToPercent(214) / 2);
        }

        if (mVideoLogoTipsLayout != null)
        {
            mVideoLogoTipsLayout.setTranslationX(location.x - CameraPercentUtil.WidthPxToPercent(mIsChinese ? 58 : 64));
        }
    }

    public void setRationBtnLocation(PointF location)
    {
        if (location == null) return;

        if (mRatioTipsLayout != null)
        {
            float dx = location.x - CameraPercentUtil.WidthPxxToPercent(mIsChinese ? 252 / 2 : 252 / 2);
            float dy = location.y - CameraPercentUtil.HeightPxToPercent(25);
            mRatioTipsLayout.setTranslationX(dx);
            mRatioTipsLayout.setTranslationY(dy);
        }
    }

    public void setBeautyBtnLocation(PointF location)
    {
        if (location == null) return;

        if (mBeautyTipsLayout != null)
        {
            float dx = location.x - CameraPercentUtil.WidthPxToPercent(mIsChinese ? 118 : 118);
            float dy = location.y - CameraPercentUtil.WidthPxToPercent(135 - 36 + 14);
            mBeautyTipsLayout.setTranslationX(dx);
            mBeautyTipsLayout.setTranslationY(dy);
        }
    }

    public void setTabType(int type)
    {
        mTabType = type;
        if (mTabType != ShutterConfig.TabType.GIF)
        {
            this.setVisibility(VISIBLE);
        }

        switch (mTabType)
        {
            case ShutterConfig.TabType.VIDEO:
            {
                mDurationTipsLayout.setVisibility(VISIBLE);
                mShutterTipsLayout.setVisibility(VISIBLE);
                break;
            }

            case ShutterConfig.TabType.PHOTO:
            {
                mCircle.setVisibility(VISIBLE);
                mVideoLogoTipsLayout.setVisibility(VISIBLE);
                break;
            }

            case ShutterConfig.TabType.CUTE:
            {
                mRatioTipsLayout.setVisibility(VISIBLE);
                mBeautyTipsLayout.setVisibility(VISIBLE);
                break;
            }
        }
    }

    public void showNextTips()
    {
        if (mIsDoingAnim) return;
        if (mTabType == ShutterConfig.TabType.CUTE)
        {
//            if (mRatioTipsLayout.getAlpha() == 1 && mRatioTipsLayout.getVisibility() == VISIBLE)
//            {
//                doRatioTipsZoomAnim(1, 0);
//            }

            if (mBeautyTipsLayout.getAlpha() == 1 && mBeautyTipsLayout.getVisibility() == VISIBLE)
            {
                doBeautyTipsZoomAnim(1, 0);
            }
        }
        else if (mTabType == ShutterConfig.TabType.PHOTO)
        {
//            if (mVideoLogoTipsLayout.getAlpha() == 1 && mVideoLogoTipsLayout.getVisibility() == VISIBLE)
//            {
//                doVideoLogoTipsZoomAnim(1, 0);
//            }
        }
        else if (mTabType == ShutterConfig.TabType.VIDEO)
        {
//            if (mDurationTipsLayout.getAlpha() == 1 && mDurationTipsLayout.getVisibility() == VISIBLE)
//            {
//                doDurationTipsZoomAnim(1, 0);
//                doShutterTipsZoomAnim(0, 1);
//            }
//            else if (mShutterTipsLayout.getAlpha() == 1 && mShutterTipsLayout.getVisibility() == VISIBLE)
//            {
//                doShutterTipsZoomAnim(1, 0);
//            }

            doShutterTipsZoomAnim(1, 0);
        }
    }

    public void doVideoLogoTipsZoomAnim(final float from, float to)
    {
        mIsDoingAnim = true;
        float cx = mVideoLogoTipsLayout.getTranslationX() + CameraPercentUtil.WidthPxToPercent(mIsChinese ? 58 : 64);
        float cy = mVideoLogoTipsLayout.getMeasuredHeight();
        ScaleAnimation anim = new ScaleAnimation(from, to, from, to, cx, cy);
        anim.setDuration(mAnimDuration);
        anim.setAnimationListener(new AnimationCBAdapter()
        {
            @Override
            public void onAnimationEnd(Animation animation)
            {
                if (from == 1)
                {
                    mVideoLogoTipsLayout.setVisibility(GONE);
                    mVideoLogoTipsLayout.setAlpha(0);
                    mCircle.setVisibility(GONE);
                    setVisibility(GONE);
                }
                mIsDoingAnim = false;
            }
        });
        mVideoLogoTipsLayout.setAlpha(1);
        mVideoLogoTipsLayout.startAnimation(anim);
    }

    public void doShutterTipsZoomAnim(final float from, float to)
    {
        mIsDoingAnim = true;
        float cx = mShutterTipsLayout.getMeasuredWidth() / 2f;
        float cy = mShutterTipsLayout.getMeasuredHeight();
        ScaleAnimation anim = new ScaleAnimation(from, to, from, to, cx, cy);
        anim.setDuration(mAnimDuration);
        anim.setAnimationListener(new AnimationCBAdapter()
        {
            @Override
            public void onAnimationEnd(Animation animation)
            {
                if (from == 1)
                {
                    mShutterTipsLayout.setVisibility(GONE);
                    mShutterTipsLayout.setAlpha(0);
                    setVisibility(GONE);
                }
                mIsDoingAnim = false;
            }
        });
        if (mShutterTipsLayout.getAlpha() == 0)
        {
            mShutterTipsLayout.setAlpha(1);
        }
        mShutterTipsLayout.startAnimation(anim);
    }

    public void doRatioTipsZoomAnim(final float from, float to)
    {
        mIsDoingAnim = true;
        float cx = mRatioTipsLayout.getTranslationX() + CameraPercentUtil.WidthPxxToPercent(mIsChinese ? 252 / 2 : 252 / 2);
        float cy = mRatioTipsLayout.getTranslationY();
        ScaleAnimation anim = new ScaleAnimation(from, to, from, to, cx, cy);
        anim.setAnimationListener(new AnimationCBAdapter()
        {
            @Override
            public void onAnimationEnd(Animation animation)
            {
                if (from == 1)
                {
                    mRatioTipsLayout.setVisibility(GONE);
                    mRatioTipsLayout.setAlpha(0);
                }
                mIsDoingAnim = false;
            }
        });
        anim.setDuration(mAnimDuration);
        if (mRatioTipsLayout.getAlpha() == 0)
        {
            mRatioTipsLayout.setAlpha(1);
        }
        mRatioTipsLayout.startAnimation(anim);
    }

    public void doBeautyTipsZoomAnim(final float from, float to)
    {
        mIsDoingAnim = true;
        float cx = mBeautyTipsLayout.getTranslationX() + CameraPercentUtil.WidthPxToPercent(mIsChinese ? 118 : 118);
        float cy = mBeautyTipsLayout.getTranslationY() + CameraPercentUtil.WidthPxToPercent(mIsChinese ? 135 - 36 + 14 : 118);
        ScaleAnimation anim = new ScaleAnimation(from, to, from, to, cx, cy);
        anim.setAnimationListener(new AnimationCBAdapter()
        {
            @Override
            public void onAnimationEnd(Animation animation)
            {
                if (from == 1)
                {
                    mBeautyTipsLayout.setVisibility(GONE);
                    mBeautyTipsLayout.setAlpha(0);
                    setVisibility(GONE);
                }
                mIsDoingAnim = false;
            }
        });
        anim.setDuration(mAnimDuration);
        if (mBeautyTipsLayout.getAlpha() == 0)
        {
            mBeautyTipsLayout.setAlpha(1);
        }
        mBeautyTipsLayout.startAnimation(anim);
    }

    private void updateDurationLoc()
    {
        if(mDurationTipsLayout != null)
        {
            if(mPreviewRatio == CameraConfig.PreviewRatio.Ratio_4_3)
            {
                int y = -CameraPercentUtil.WidthPxToPercent(378);

                int bottomPadding = 0;
                if(mFullScreenRatio > CameraConfig.PreviewRatio.Ratio_16_9)
                {
                    int mCameraViewHeight = (int)(ShareData.getScreenW() * CameraConfig.PreviewRatio.Ratio_16_9);

                    if(mCameraViewHeight > ShareData.m_screenRealHeight)
                    {
                        mCameraViewHeight = (int)(ShareData.getScreenW() * CameraConfig.PreviewRatio.Ratio_4_3);
                    }

                    if(mFullScreenRatio > CameraConfig.PreviewRatio.Ratio_17$25_9)
                    {
                        bottomPadding = ShareData.m_screenRealHeight - mCameraViewHeight - RatioBgUtils.getTopPaddingHeight(mPreviewRatio);
                    }
                    else
                    {
                        bottomPadding = ShareData.m_screenRealHeight - mCameraViewHeight;
                    }
                }

                y += bottomPadding;
                mDurationTipsLayout.setTranslationY(y);
            }
            else
            {
                int y = -CameraPercentUtil.WidthPxToPercent(348);
                mDurationTipsLayout.setTranslationY(y);
            }
        }
    }

    public void doDurationTipsZoomAnim(final float from, float to)
    {
        mIsDoingAnim = true;
        float cx = mIsChinese ? mDurationTipsLayout.getMeasuredWidth()/2f : mDurationTipsLayout.getMeasuredWidth()/2f;
        float cy = mDurationTipsLayout.getTranslationY() + mDurationTipsLayout.getMeasuredHeight();
        ScaleAnimation anim = new ScaleAnimation(from, to, from, to, cx, cy);
        anim.setDuration(mAnimDuration);
        anim.setAnimationListener(new AnimationCBAdapter()
        {
            @Override
            public void onAnimationEnd(Animation animation)
            {
                if (from == 1)
                {
                    mDurationTipsLayout.setVisibility(GONE);
                    mDurationTipsLayout.setAlpha(0);
                }
                mIsDoingAnim = false;
            }
        });
        if (mDurationTipsLayout.getAlpha() == 0)
        {
            mDurationTipsLayout.setAlpha(1);
        }
        mDurationTipsLayout.startAnimation(anim);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        switch (event.getAction() & event.getActionMasked())
        {
            case MotionEvent.ACTION_UP:
            {
                showNextTips();
                break;
            }
        }
        return true;
    }

    public void setPreviewRatio(float ratio)
    {
        this.mPreviewRatio = ratio;
        updateDurationLoc();
    }
}
