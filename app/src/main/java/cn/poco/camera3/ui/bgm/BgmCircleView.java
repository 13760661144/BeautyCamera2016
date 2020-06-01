package cn.poco.camera3.ui.bgm;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import cn.poco.advanced.ImageUtils;
import cn.poco.camera3.util.CameraPercentUtil;
import cn.poco.dynamicSticker.DownloadState;
import cn.poco.dynamicSticker.newSticker.BaseStickerItemView;
import cn.poco.dynamicSticker.newSticker.CropCircleTransformation;
import my.beautyCamera.R;

/**
 * adapter 里面的 ItemView
 */
public class BgmCircleView extends BaseStickerItemView
{
    private ProgressImageView mView;
    private ImageView mStateView;
    private MaskView mMaskView;
    private ImageView mWaitView;
    private TextView mTextView;
    private SelCircleView mSelCircleView;

    // glide 圆形图片转换
    private CropCircleTransformation mCropCircleTransformation;

    public BgmCircleView(@NonNull Context context)
    {
        super(context);
    }

    public void ClearMemory()
    {
        mView.ClearMemory();
        Glide.get(getContext()).clearMemory();
        Glide.get(getContext()).getBitmapPool().clearMemory();
        mCropCircleTransformation = null;
    }

    @Override
    public void initData()
    {
        mCropCircleTransformation = new CropCircleTransformation(getContext());
    }

    @Override
    public void initView()
    {
        mView = new ProgressImageView(getContext());
        mView.setId(R.id.bgm_icon);
        mView.setProgressColor(ImageUtils.GetSkinColor());
        FrameLayout.LayoutParams params = new LayoutParams(CameraPercentUtil.WidthPxToPercent(126), CameraPercentUtil.HeightPxToPercent(126));
        params.gravity = Gravity.CENTER_HORIZONTAL;
        params.topMargin = CameraPercentUtil.HeightPxToPercent(36);
        addView(mView, params);

        mSelCircleView = new SelCircleView(getContext());
        mSelCircleView.setVisibility(GONE);
        mSelCircleView.setProgressColor(ImageUtils.GetSkinColor());
        params = new LayoutParams(CameraPercentUtil.WidthPxToPercent(128), CameraPercentUtil.HeightPxToPercent(128));
        params.topMargin = CameraPercentUtil.HeightPxToPercent(35);
        addView(mSelCircleView, params);

        mMaskView = new MaskView(getContext());
        mMaskView.setVisibility(GONE);
        params = new LayoutParams(CameraPercentUtil.WidthPxToPercent(124), CameraPercentUtil.HeightPxToPercent(124));
        params.gravity = Gravity.CENTER_HORIZONTAL;
        params.topMargin = CameraPercentUtil.HeightPxToPercent(36);
        addView(mMaskView, params);

        mWaitView = new ImageView(getContext());
        mWaitView.setVisibility(GONE);
        mWaitView.setImageResource(R.drawable.sticker_download_wait);
        params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER_HORIZONTAL;
        params.topMargin = CameraPercentUtil.HeightPxToPercent(64);
        addView(mWaitView, params);

        mStateView = new ImageView(getContext());
        mStateView.setId(R.id.bgm_state);
        mStateView.setVisibility(GONE);
        params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.leftMargin = CameraPercentUtil.WidthPxToPercent(89);
        params.topMargin = CameraPercentUtil.HeightPxToPercent(126);
        addView(mStateView, params);

        mTextView = new TextView(getContext());
        mTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
        mTextView.setTextColor(0xff333333);
        mTextView.setSingleLine(true);
        mTextView.setEllipsize(TextUtils.TruncateAt.END); // 省略号位置
        mTextView.setGravity(Gravity.CENTER);
        params = new LayoutParams(CameraPercentUtil.WidthPxToPercent(124), ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER_HORIZONTAL;
        params.topMargin = CameraPercentUtil.HeightPxToPercent(166);
        addView(mTextView, params);
    }

    @Override
    public void setIsSelected(boolean isSelected)
    {
        mSelCircleView.setVisibility(isSelected ? VISIBLE : GONE);
    }

    public void setInfoState(int infoState)
    {
        mStateView.setVisibility(GONE);
        mWaitView.setVisibility(GONE);
        mMaskView.setVisibility(GONE);

        switch (infoState)
        {
            case DownloadState.WAITTING_FOR_DOWNLOAD:
            {
                mWaitView.setVisibility(VISIBLE);
                mMaskView.setVisibility(VISIBLE);
                break;
            }

            case DownloadState.DOWNLOAD_SUCCESS:// 下载成功
            {
                mStateView.setVisibility(VISIBLE);
                mStateView.setImageResource(R.drawable.sticker_new);
                setDownloadProgress(0, true);
                break;
            }

            case DownloadState.DOWNLOAD_FAILED:// 下载失败
            case DownloadState.NEED_DOWNLOAD:// 未下载
            {
                mStateView.setVisibility(VISIBLE);
                mStateView.setImageResource(R.drawable.sticker_download);
                setDownloadProgress(0, true);
            }
        }
    }

    public void setIsClip(boolean isClip)
    {
        mView.setDrawMask(isClip);
        mView.updateUI();
    }

    @Override
    public void setDownloadProgress(int progress, boolean reset)
    {
        mView.setProgress(progress);
        mView.resetProgressState(reset);
        mView.updateUI();
    }

    @Override
    public void setThumb(Object thumb)
    {
        if (thumb != null)
        {
            if (thumb instanceof Integer)
            {
                Glide.with(getContext()).load((Integer) thumb)
                        .bitmapTransform(mCropCircleTransformation)
                        .error(R.drawable.music_cover_default)
                        .into(mView);
            }
            else if (thumb instanceof String)
            {
                Glide.with(getContext()).load((String) thumb)
                        .bitmapTransform(mCropCircleTransformation)
                        .error(R.drawable.music_cover_default)
                        .into(mView);
            }
        }
    }

    @Override
    public void setThumb(Object thumb, boolean isDownloaded)
    {

    }

    public void setThumbDegree(float degree)
    {
        mView.setImageDegree(degree);
    }

    public void setThumbAutoUpdate(boolean autoUpdate)
    {
        mView.setAutoUpdate(autoUpdate);
    }

    public void setText(CharSequence text)
    {
        mTextView.setText(text);
    }
}
