package cn.poco.gifEmoji;

import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.support.annotation.DrawableRes;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.VideoView;

import java.io.File;
import java.util.HashMap;

import cn.poco.camera3.util.CameraPercentUtil;
import cn.poco.framework.BaseSite;
import cn.poco.framework.IPage;
import cn.poco.tianutils.ShareData;
import my.beautyCamera.R;

/**
 * @author lmx
 *         Created by lmx on 2017/11/17.
 */

public class GifEmojiSharePreviewPage extends IPage implements MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener, View.OnClickListener
{
    private VideoView mVideo;
    private ContourTextView mText;
    private ImageView mWatermark;

    private GifEmojiSharePreviewPageSite mSite;

    private PreviewData mData;

    public GifEmojiSharePreviewPage(Context context, BaseSite site)
    {
        super(context, site);
        mSite = (GifEmojiSharePreviewPageSite) site;
        ShareData.InitData(context);
        setBackgroundColor(0xe6ffffff);
        initView();
    }


    private void initView()
    {
        setOnClickListener(this);

        mVideo = new VideoView(getContext());
        mVideo.setOnPreparedListener(this);
        mVideo.setOnCompletionListener(this);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams((int) (ShareData.getScreenW() * (540 * 1f / 720)), (int) (ShareData.getScreenW() * (540 * 1f / 720)));
        params.gravity = Gravity.CENTER_HORIZONTAL;
        params.topMargin = CameraPercentUtil.HeightPxToPercent(268);
        this.addView(mVideo, params);

        mText = new ContourTextView(getContext());
        mText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 27);
        mText.setColor(0xb3000000, 0xffffffff);
        mText.setTextColor(Color.WHITE);
        mText.setText(R.string.gif_edit_add_text_tip);
        mText.setSingleLine(true);
        mText.setGravity(Gravity.CENTER);
        mText.setShadowLayer(ShareData.PxToDpi_xhdpi(5), ShareData.PxToDpi_xhdpi(1), ShareData.PxToDpi_xhdpi(1), 0x40000000);
        params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER_HORIZONTAL;
        params.topMargin = CameraPercentUtil.HeightPxToPercent(706);
        this.addView(mText, params);

        mWatermark = new ImageView(getContext());
        //mWatermark.setImageResource(R.drawable.gif_watermark);
        mWatermark.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        params = new LayoutParams((int) (ShareData.getScreenW() * (170 * 1f / 720)), (int) (ShareData.m_screenRealHeight * (70 * 1f / 1280)));
        params.gravity = Gravity.END;
        params.rightMargin = (int) (ShareData.getScreenW() * (90 * 1f / 720));
        params.topMargin = CameraPercentUtil.HeightPxToPercent(268);
        this.addView(mWatermark, params);
    }

    public void playVideo(PreviewData data)
    {
        mData = data;
        if (mData != null && !TextUtils.isEmpty(mData.videoPath))
        {
            File file = new File(mData.videoPath);
            if (file.exists())
            {
                if (mVideo != null)
                {
                    mVideo.setVideoPath(file.getPath());
                }
            }

            mText.setText(mData.titleTxt);
            if (mData.waterTxtId == 0)
            {
                mData.waterTxtId = R.drawable.gif_watermark;
            }
            mWatermark.setImageResource(mData.waterTxtId);
        }
    }

    /**
     * VideoView 播完回调
     */
    @Override
    public void onCompletion(MediaPlayer mp)
    {
        if (mVideo != null)
        {
            mVideo.start();
        }
    }

    /**
     * VideoView 准备回调
     */
    @Override
    public void onPrepared(MediaPlayer mp)
    {
        if (mVideo != null)
        {
            mVideo.start();
        }
    }

    @Override
    public void SetData(HashMap<String, Object> params)
    {
        if (params != null)
        {
            if (params.containsKey(KEY_SET_DATA_PREVIEW_DATA))
            {
                mData = (PreviewData) params.get(KEY_SET_DATA_PREVIEW_DATA);
            }
        }

        playVideo(mData);
    }

    @Override
    public void onBack()
    {
        if (mSite != null) mSite.OnBack(getContext());
    }

    @Override
    public void onClose()
    {
        if (mVideo != null)
        {
            this.removeView(mVideo);
            mVideo.suspend();
            mVideo.setOnCompletionListener(null);
            mVideo.setOnPreparedListener(null);
            mVideo = null;
        }

        mData = null;
        setOnClickListener(null);
        removeAllViews();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (mVideo != null)
        {
            mVideo.pause();
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (mVideo != null)
        {
            mVideo.resume();
        }
    }

    @Override
    public void onClick(View v)
    {
        if (v == this)
        {
            onBack();
        }
    }


    public static final String KEY_SET_DATA_PREVIEW_DATA = "preview_data";

    public static class PreviewData
    {
        String videoPath;
        String titleTxt;
        @DrawableRes  int waterTxtId;
    }
}
