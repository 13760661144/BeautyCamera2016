package cn.poco.video;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.exoplayer2.ExoPlaybackException;

import java.util.ArrayList;

import cn.poco.acne.view.CirclePanel;
import cn.poco.advanced.ImageUtils;
import cn.poco.beauty.view.ColorSeekBar;
import cn.poco.camera3.info.PreviewBgmInfo;
import cn.poco.camera3.util.CameraPercentUtil;
import cn.poco.dynamicSticker.VolumeChangeReceiver;
import cn.poco.filter4.WatermarkItem;
import cn.poco.framework.IPage;
import cn.poco.framework.MyFramework;
import cn.poco.image.filter;
import cn.poco.lightApp06.LightApp06Page;
import cn.poco.lightApp06.VideoBgmPage;
import cn.poco.resource.PreviewBgmResMgr2;
import cn.poco.resource.WatermarkResMgr2;
import cn.poco.setting.SettingInfoMgr;
import cn.poco.statistics.MyBeautyStat;
import cn.poco.statistics.TongJi2;
import cn.poco.tianutils.CommonUtils;
import cn.poco.tianutils.MakeBmpV2;
import cn.poco.tianutils.ShareData;
import cn.poco.utils.OnAnimationClickListener;
import cn.poco.video.music.SelectMusicPage;
import cn.poco.video.view.AutoRoundProgressBar;
import cn.poco.video.view.AutoRoundProgressView;
import cn.poco.video.view.ClipMusicView;
import cn.poco.view.PictureView;
import cn.poco.view.material.VerFilterViewEx;
import my.beautyCamera.R;

/**
 * @author lmx
 *         Created by lmx on 2017/8/23.
 */
@Deprecated
public class VideoPreviewPage extends FrameLayout implements View.OnTouchListener,
        AudioManager.OnAudioFocusChangeListener, VolumeChangeReceiver.OnVolumeChangedListener
{
    private static final String TAG = "bbb";

    private boolean isPause = false;

    public static final int ID_BACK = 1001;
    public static final int ID_SHARE = 1002;

    private PictureView mPictureView;
    private ImageView mVideoSoundBtn;
    private ImageView mBgmSoundBtn;
    private ImageView mBackBtn;
    private ImageView mShareBtn;
    private FrameLayout mCtrFr;

    // 水印
    private int mWaterMarkId = -1;

    private VideoView mVideoView;
    private VideoBgmPage mVideoBgmPage;
    private SelectMusicPage mSelectMusicPage;
    private ClipMusicView mClipMusicView;

    private AutoRoundProgressView mProgressDialog;

    private Toast mToast;

    private boolean isRecordAudioEnable = true;     //是否有视频录音
    private boolean isVideoMute = false;            //视频是否静音
    private boolean isVideoMode = false;            //是否视频模式
    private boolean isAnimation = false;            //是否在动画中
    private boolean isAudioActive = false;          //是否在活跃中
    private boolean isThirdParty = false;           //是否第三方调用
    private boolean isMixing = false;               //混合视频中
    private boolean isAddLocalBGM = false;          //是否已经选择过bgm音乐
    private boolean mIsKeepScreenOn = false;        //保持屏幕常亮
    private boolean mInitPage = false;

    private boolean isHideVideoMuteBtn;         //是否隐藏视频录音按钮
    private boolean isHideBgMusicBtn;           //是否隐藏音乐选择按钮
    private boolean isHideVideoProgress;        //是否隐藏视频进度条
    private boolean isFullVideoScreen = false;  //是否是全屏视频

    //虚拟键隐藏与显示
    private boolean isHideNavigationBar = true;
    private int mOriginVisibility = -1; //还原系统设置
    private int mSystemUiVisibility = -1;//当前设置系统

    //音频开始时间 单位：毫秒
    private long mStartBgmTime = 0L;

    private double mBgmVolumeAdjust = 1.0D;
    private String mVideoOutputPath;
    private float mVideoOutDuration;
    private Object[] mPreviewRes;
    private float mPreviewRatio;

    private AudioManager mAudioManager;
    private VolumeChangeReceiver mVolumeChangeReceiver;
    private BrightObserver mBrightObserver;

    private OnClickListener mListener;
    private int mOrientation; // 视频或者图片的旋转角度
    private long mDuration;

    private Bitmap mDialogBk;

    private CirclePanel mRecordCirclePanel;
    private CirclePanel mMusicCirclePanel;
    private final float mCircleRadius;

    //默认音量
    private int mRecordValue = 100;
    private int mMusicValue = 60;

    private boolean isSupportFadeVolume = true;                     //是否支持淡入淡出
    private boolean isNeedResumeFadeVolume = false;                 //是否需要恢复音量
    private static final int MAX_FADE_IN_VOLUME_DURATION = 1500;    //最大淡入时间（单位 毫秒）
    private static final int MAX_FADE_OUT_VOLUME_DURATION = 1500;   //最大淡出时间（单位 毫秒）

    private String mChannelValue;
    private boolean mPlayerError;

    public VideoPreviewPage(@NonNull Context context)
    {
        super(context);
        mInitPage = true;
        mCircleRadius = CameraPercentUtil.WidthPxToPercent(55);
        initData();
    }

    private void initData()
    {
        mAudioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);

        mVolumeChangeReceiver = new VolumeChangeReceiver(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(VolumeChangeReceiver.ACTION);
        getContext().registerReceiver(mVolumeChangeReceiver, intentFilter);

        mBrightObserver = new BrightObserver(new Handler());
        registerBrightnessObserver();
    }

    private void initDialog()
    {
        if (mProgressDialog != null) return;
        mProgressDialog = new AutoRoundProgressView(getContext());
        mProgressDialog.setListener(mOnProgressListener);
        if (mDialogBk != null && !mDialogBk.isRecycled())
        {
            mProgressDialog.setBackgroundThumb(mDialogBk);
        }
        else
        {
            mDialogBk = filter.fakeGlassBeauty(CommonUtils.GetScreenBmp((Activity) getContext(), ShareData.m_screenWidth, ShareData.m_screenHeight), 0x19000000);
            mProgressDialog.setBackgroundThumb(mDialogBk);
        }
    }

    private void initVideoCtrFr()
    {
        mCtrFr = new FrameLayout(getContext());
        mCtrFr.setPadding(0, 0, 0, CameraPercentUtil.HeightPxToPercent(36));
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, CameraPercentUtil.HeightPxToPercent(166));
        params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
        mCtrFr.setLayoutParams(params);
        addView(mCtrFr);

        // 叉
        mBackBtn = new ImageView(getContext());
        mBackBtn.setScaleType(ImageView.ScaleType.CENTER);
        mBackBtn.setBackgroundResource(R.drawable.light_app06_ctr_bg);
        mBackBtn.setImageResource(R.drawable.light_app06_share_back_new);
        mBackBtn.setOnTouchListener(mOnAnimationClickListener);
        params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.START | Gravity.BOTTOM;
        params.leftMargin = CameraPercentUtil.WidthPxToPercent(50);
        mCtrFr.addView(mBackBtn, params);

        //视频音量按钮
        if (!this.isHideVideoMuteBtn)
        {
            mVideoSoundBtn = new ImageView(getContext());
            mVideoSoundBtn.setScaleType(ImageView.ScaleType.CENTER);
            mVideoSoundBtn.setBackgroundResource(R.drawable.light_app06_ctr_bg);
            isVideoMute = !isRecordAudioEnable;
            updateVideoSoundBtnState(isVideoMute);
            mVideoSoundBtn.setOnTouchListener(mOnAnimationClickListener);
            params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

            if (this.isHideBgMusicBtn)
            {
                //居中排版
                params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
            }
            else
            {
                params.gravity = Gravity.START | Gravity.BOTTOM;
                params.leftMargin = CameraPercentUtil.WidthPxToPercent(50 + 100 + 74);
            }
            mCtrFr.addView(mVideoSoundBtn, params);
        }

        //bgm按钮
        if (!this.isHideBgMusicBtn)
        {
            mBgmSoundBtn = new ImageView(getContext());
            mBgmSoundBtn.setScaleType(ImageView.ScaleType.CENTER);
            mBgmSoundBtn.setBackgroundResource(R.drawable.light_app06_ctr_bg);
            mBgmSoundBtn.setImageBitmap(ImageUtils.AddSkin(getContext(),
                    BitmapFactory.decodeResource(getResources(), R.drawable.light_app06_bgm)));
            mBgmSoundBtn.setOnTouchListener(mOnAnimationClickListener);
            params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            if (this.isHideVideoMuteBtn)
            {
                //居中排版
                params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
            }
            else
            {
                params.gravity = Gravity.END | Gravity.BOTTOM;
                params.rightMargin = CameraPercentUtil.WidthPxToPercent(50 + 100 + 74);
            }
            mCtrFr.addView(mBgmSoundBtn, params);
        }

        //share按钮
        mShareBtn = new ImageView(getContext());
        mShareBtn.setScaleType(ImageView.ScaleType.CENTER);
        mShareBtn.setImageResource(R.drawable.gif_preview_share);
        mShareBtn.setOnTouchListener(mOnAnimationClickListener);
        mShareBtn.setBackgroundDrawable(new BitmapDrawable(getResources(), cn.poco.advanced.ImageUtils.AddSkin(getContext(), BitmapFactory.decodeResource(getResources(), R.drawable.light_app06_ctr_bg))));
        params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.END | Gravity.BOTTOM;
        params.rightMargin = CameraPercentUtil.WidthPxToPercent(50);
        mCtrFr.addView(mShareBtn, params);
    }

    public void setOrientation(int orientation)
    {
        mOrientation = orientation;
    }

    public void setVideoDuration(long duration)
    {
        mDuration = duration;

        isSupportFadeVolume = mDuration >= 3500L;
    }

    public void setHideVideoMuteBtn(boolean hideVideoMuteBtn)
    {
        this.isHideVideoMuteBtn = hideVideoMuteBtn;
        if (mVideoSoundBtn != null) mVideoSoundBtn.setVisibility(hideVideoMuteBtn ? GONE : VISIBLE);
    }

    public void setHideBgMusicBtn(boolean hideBgMusicBtn)
    {
        this.isHideBgMusicBtn = hideBgMusicBtn;
        if (mBgmSoundBtn != null) mBgmSoundBtn.setVisibility(hideBgMusicBtn ? GONE : VISIBLE);
    }

    public void setHideVideoProgress(boolean hideVideoProgress)
    {
        this.isHideVideoProgress = hideVideoProgress;
        if (mVideoView != null)
        {
            mVideoView.setProgressViewShow(!hideVideoProgress);
        }
    }

    public void setRecordAudioEnable(boolean enable)
    {
        this.isRecordAudioEnable = enable;
        this.isVideoMute = !enable;
        if (!isRecordAudioEnable)
        {
            mRecordValue = 0;
        }
        updateVideoSoundBtnState(this.isVideoMute);
    }

    public void setChannelValue(String mChannelValue)
    {
        this.mChannelValue = mChannelValue;
    }

    public void setFullVideoScreen(boolean fullVideoScreen)
    {
        this.isFullVideoScreen = fullVideoScreen;
        if (mVideoView != null)
        {
            LayoutParams layoutParams = (LayoutParams) mVideoView.getLayoutParams();
            layoutParams.width = LayoutParams.MATCH_PARENT;
            layoutParams.height = LayoutParams.MATCH_PARENT;
            mVideoView.requestLayout();
        }
    }

    private void initVideoView()
    {
        mVideoView = new VideoView(getContext());
        mVideoView.setOnTouchListener(this);
        mVideoView.setCallback(mVideoCallback);
        mVideoView.setDuration(mDuration);
        mVideoView.setProgressViewShow(!isHideVideoProgress);
        LayoutParams params;
        if (isFullVideoScreen)
        {
            params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        }
        else
        {
            params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ShareData.m_screenRealHeight - CameraPercentUtil.HeightPxToPercent(166) + CameraPercentUtil.HeightPxToPercent(14));
        }
        mVideoView.setAdaptation(!isFullVideoScreen);
        params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        addView(mVideoView, 0, params);
    }

    private void initPictureView()
    {
        mCtrFr = new FrameLayout(getContext());
        mCtrFr.setBackgroundColor(Color.WHITE);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, CameraPercentUtil.HeightPxToPercent(88));
        params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
        mCtrFr.setLayoutParams(params);
        addView(mCtrFr);
        {
            // 叉
            mBackBtn = new ImageView(getContext());
            mBackBtn.setScaleType(ImageView.ScaleType.CENTER);
            mBackBtn.setImageResource(R.drawable.beautify_cancel);
            mBackBtn.setOnTouchListener(mOnAnimationClickListener);
            params = new LayoutParams(CameraPercentUtil.WidthPxToPercent(88), CameraPercentUtil.HeightPxToPercent(88));
            params.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
            mCtrFr.addView(mBackBtn, params);

            //share按钮
            mShareBtn = new ImageView(getContext());
            mShareBtn.setScaleType(ImageView.ScaleType.CENTER);
            mShareBtn.setImageResource(R.drawable.beautify_ok);
            ImageUtils.AddSkin(getContext(), mShareBtn);
            mShareBtn.setOnTouchListener(mOnAnimationClickListener);
            params = new LayoutParams(CameraPercentUtil.WidthPxToPercent(88), CameraPercentUtil.HeightPxToPercent(88));
            params.gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
            mCtrFr.addView(mShareBtn, params);
        }

        mPictureView = new PictureView(getContext());
        mPictureView.setVerFilterCB(new VerFilterViewEx.ControlCallback()
        {
            @Override
            public void OnSelFaceIndex(int index)
            {

            }

            @Override
            public void OnAnimFinish()
            {

            }

            @Override
            public void OnFingerDown(int fingerCount)
            {

            }

            @Override
            public void OnFingerUp(int fingerCount)
            {

            }

            @Override
            public void OnClickWaterMask()
            {

            }
        });
        params = new LayoutParams(LayoutParams.MATCH_PARENT, ShareData.m_screenRealHeight - CameraPercentUtil.HeightPxToPercent(88));
        addView(mPictureView, 0, params);

        postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                setWaterMark();
            }
        }, 150);

        //水印layout
    }

    private void setWaterMark()
    {
        if (mPictureView != null)
        {
            WatermarkItem item = getWaterMarkById(mWaterMarkId);
            if (item != null)
            {
                mWaterMarkId = item.mID;
                if (mWaterMarkId != WatermarkResMgr2.getInstance().GetNonWatermarkId(getContext()))
                {
                    mPictureView.setDrawWaterMark(true);
                    mPictureView.AddWaterMark(MakeBmpV2.DecodeImage(getContext(), item.res, 0, -1, -1, -1, Bitmap.Config.ARGB_8888), false);
                }
                else
                {
                    mPictureView.setDrawWaterMark(false);
                    mPictureView.invalidate();
                }
            }
        }
    }

    private WatermarkItem getWaterMarkById(int id)
    {
        return WatermarkResMgr2.getInstance().GetWaterMarkById(id);
    }

    public WatermarkItem getWaterMark()
    {
        return getWaterMarkById(mWaterMarkId);
    }

    private void initBgmView()
    {
        if (mVideoBgmPage != null) return;
        mVideoBgmPage = new VideoBgmPage(getContext());
        mVideoBgmPage.setBackgroundColor(0xe6f0f0f0);
        mVideoBgmPage.setCallback(mCallback);
        mVideoBgmPage.setVisibility(GONE);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, cn.poco.camera3.util.CameraPercentUtil.HeightPxToPercent(320));
        params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
        addView(mVideoBgmPage, params);
    }

    private void initSelectMusicPage()
    {
        if (mSelectMusicPage != null) return;
        mSelectMusicPage = new SelectMusicPage(getContext(), mSelectMusicCallback);
        mSelectMusicPage.setFold(true);
        mSelectMusicPage.setVisibility(GONE);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        params.gravity = Gravity.CENTER;
        addView(mSelectMusicPage, params);
    }

    private void initClipMusicView(ClipMusicView.FrequencyInfo info)
    {
        if (mClipMusicView != null) removeView(mClipMusicView);

        mClipMusicView = new ClipMusicView(getContext(), info);
        mClipMusicView.setFold(true);
        mClipMusicView.setBackgroundColor(Color.WHITE);
        mClipMusicView.setOnCallBack(mClipMusicCallback);
        mClipMusicView.setVisibility(GONE);
        mClipMusicView.setClickable(true);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, cn.poco.camera3.util.CameraPercentUtil.HeightPxToPercent(320));
        params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
        addView(mClipMusicView, params);

        if (mRecordCirclePanel != null) removeView(mRecordCirclePanel);

        mRecordCirclePanel = new CirclePanel(getContext());
        params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(120));
        params.gravity = Gravity.BOTTOM;
        params.bottomMargin = ShareData.PxToDpi_xhdpi(256);
        addView(mRecordCirclePanel, params);

        if (mMusicCirclePanel != null) removeView(mMusicCirclePanel);

        mMusicCirclePanel = new CirclePanel(getContext());
        params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(120));
        params.gravity = Gravity.BOTTOM;
        params.bottomMargin = ShareData.PxToDpi_xhdpi(166);
        addView(mMusicCirclePanel, params);
    }

    private void showCirclePanel(ColorSeekBar seekBar, CirclePanel circlePanel, int progress)
    {
        if (seekBar == null) return;
        int[] seekBarLoc = new int[2];
        seekBar.getLocationOnScreen(seekBarLoc);
        int circleWidth = seekBar.getCircleWith();
        int width = seekBar.getWidth() - circleWidth;
        float circleX = seekBarLoc[0] + circleWidth / 2f + progress / 100f * width;
        float circleY = circlePanel.getHeight() * 1.0f / 2 - ShareData.PxToDpi_xhdpi(3);
        circlePanel.change(circleX, circleY, mCircleRadius);
        circlePanel.setText(String.valueOf(progress));
        circlePanel.show();
    }

    /**
     * 初始化视频/图片数据
     *
     * @param isVideo  是否是视频
     * @param listener 监听
     * @param preview  视频/图片
     */
    public void init(boolean isVideo, OnClickListener listener, float previewRatio, Object... preview)
    {
        if (preview == null || preview.length == 0) return;

        this.mPreviewRes = preview;
        this.mListener = listener;
        this.isVideoMode = isVideo;
        this.mPreviewRatio = previewRatio;

        this.setBackgroundColor(isVideo ? 0xffffffff : 0xfff5f5f5);
        if (isVideo)
        {
            requestAudioFocus();
            keepScreenWakeUp(true);
            initVideoView();
            initVideoCtrFr();
            ArrayList<String> videoList = new ArrayList<>();
            for (int i = 0; i < preview.length; i++)
            {
                videoList.add((String) preview[i]);
            }
            mVideoView.setVideoPath(videoList);
            mVideoView.prepared();
            mVideoView.start();
        }
        else
        {
            if (mDialogBk != null && !mDialogBk.isRecycled())
            {
                mDialogBk.recycle();
                mDialogBk = null;
            }

            mWaterMarkId = SettingInfoMgr.GetSettingInfo(getContext()).GetPhotoWatermarkId(
                    WatermarkResMgr2.getInstance().GetDefaultWatermarkId(getContext()));

            initPictureView();
            Object image = preview[0];
            Bitmap bmp = null;
            if (image instanceof Bitmap)
            {
                bmp = (Bitmap) image;
            }
            else if (image instanceof String)
            {
                bmp = MakeBmpV2.DecodeImage(getContext(), image, 0, -1, -1, -1, Bitmap.Config.ARGB_8888);
            }
            else if (image instanceof Uri)
            {
                Uri uri = (Uri) image;
                final String scheme = uri.getScheme();

                String data = null;
                if (scheme == null || ContentResolver.SCHEME_FILE.equals(scheme))
                {
                    data = uri.getPath();
                }
                else if (ContentResolver.SCHEME_CONTENT.equals(scheme))
                {
                    Cursor cursor = getContext().getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
                    if (null != cursor)
                    {
                        if (cursor.moveToFirst())
                        {
                            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                            if (index > -1)
                            {
                                data = cursor.getString(index);
                            }
                        }
                        cursor.close();
                    }
                }

                bmp = MakeBmpV2.DecodeImage(getContext(), data, 0, -1, -1, -1, Bitmap.Config.ARGB_8888);
            }
            mPictureView.setImage(bmp);
        }
    }

    public void setListener(OnClickListener listener)
    {
        this.mListener = listener;
    }

    /**
     * 请求音频焦点
     */
    private void requestAudioFocus()
    {
        isAudioActive = mAudioManager.isMusicActive();
        if (isAudioActive)
        {
            int result = mAudioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);

            if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED)
            {
                // 请求成功
            }
            else if (result == AudioManager.AUDIOFOCUS_REQUEST_FAILED)
            {
                // 请求失败
            }
        }
    }

    /**
     * 放弃音频焦点
     */
    private void abandonAudioFocus()
    {
        if (isAudioActive && mAudioManager != null)
        {
            mAudioManager.abandonAudioFocus(this);
        }
        isAudioActive = false;
    }

    /**
     * 获取音频焦点回调
     *
     * @param focusChange
     */
    @Override
    public void onAudioFocusChange(int focusChange)
    {
        if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT)  //暂时失去Audio Focus，并会很快再次获得，可不释饭media资源
        {
        }
        else if (focusChange == AudioManager.AUDIOFOCUS_LOSS)       //失去了Audio Focus，并将会持续很长的时间
        {
        }
        else if (focusChange == AudioManager.AUDIOFOCUS_GAIN)
        {
        }
    }


    /**
     * 音量调节改变监听
     */
    @Override
    public void onVolumeChanged()
    {
        if (mAudioManager != null)
        {
            int streamMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            int streamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            if (streamMaxVolume > 0)
            {
                mBgmVolumeAdjust = (streamVolume * 1D) / (streamMaxVolume * 1D) + 0.5D;//误差补点
                //Log.d(TAG, "VideoPreviewPage --> onVolumeChanged: mBgmVolumeAdjust " + mBgmVolumeAdjust);
            }
        }
    }

    /**
     * 保持屏幕常亮
     *
     * @param wakeup
     */
    private void keepScreenWakeUp(boolean wakeup)
    {
        try
        {
            if (wakeup && !mIsKeepScreenOn)
            {
                ((Activity) getContext()).getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                mIsKeepScreenOn = true;
            }
            else if (!wakeup && mIsKeepScreenOn)
            {
                ((Activity) getContext()).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                mIsKeepScreenOn = false;
            }
        }
        catch (Throwable t)
        {

        }
    }

    public void registerBrightnessObserver()
    {
        getContext().getContentResolver().registerContentObserver(Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS), true, mBrightObserver);
    }

    public void unregisterBrightnessObserver()
    {
        getContext().getContentResolver().unregisterContentObserver(mBrightObserver);
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility)
    {
        super.onWindowVisibilityChanged(visibility);
        if (isCurrentPage())
        {
            changeSystemUiVisibility(visibility == View.GONE ?
                    View.VISIBLE : View.GONE);
        }
        else
        {
            changeSystemUiVisibility(View.VISIBLE);
            ShareData.changeSystemUiVisibility(getContext(), false);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus)
    {
        super.onWindowFocusChanged(hasWindowFocus);
        if (hasWindowFocus && isCurrentPage())
        {
            postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    resetSystemUiVisibility();
                    changeSystemUiVisibility(View.GONE);
                }
            }, 200);
        }
    }

    /**
     * @param visibility {@link View#VISIBLE}：显示导航栏（非沉浸式），{@link View#GONE}：沉浸式导航栏处理
     */
    public void changeSystemUiVisibility(int visibility)
    {
        if (visibility != mSystemUiVisibility)
        {
            int vis = ShareData.showOrHideStatusAndNavigation(getContext(), visibility == View.VISIBLE, mOriginVisibility, visibility == View.VISIBLE);
            if (mOriginVisibility == -1 && visibility == View.GONE)
            {
                mOriginVisibility = vis;
            }
            mSystemUiVisibility = visibility;
        }
    }

    public void resetSystemUiVisibility()
    {
        mSystemUiVisibility = -1;//重置当前设置系统
    }


    /**
     * 是否是当前page
     *
     * @return
     */
    private boolean isCurrentPage()
    {
        //TODO 目前的完成Page是LightApp06Page，若有变更请修正
        IPage iPage = MyFramework.GetTopPage(getContext());
        if (iPage != null && iPage instanceof LightApp06Page)
        {
            return true;
        }
        return false;
    }


    private class BrightObserver extends ContentObserver
    {

        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */
        public BrightObserver(Handler handler)
        {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange)
        {
            super.onChange(selfChange);
            if (getContext() == null) return;
            int mode = -1;
            int brightness = 0;
            try
            {
                mode = Settings.System.getInt(getContext().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE);
                brightness = Settings.System.getInt(getContext().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
            }
            catch (Settings.SettingNotFoundException e)
            {
                e.printStackTrace();
            }
            if (mode == Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL && getContext() != null)
            {
                Window window = ((Activity) getContext()).getWindow();
                WindowManager.LayoutParams wmParams = window.getAttributes();
                wmParams.screenBrightness = brightness / 255f;
                window.setAttributes(wmParams);
            }
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
        if (mClipMusicView != null && !mClipMusicView.isFold())
        {
            //收缩编辑音乐列表
            setClipMusicViewFold(true);
            return false;
        }

        if (mVideoBgmPage != null && !mVideoBgmPage.isFold())
        {
            if (mOnAnimationClickListener != null)
            {
                mOnAnimationClickListener.onAnimationClick(mBgmSoundBtn);
            }
            return false;
        }
        return false;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        //4:3下 底部btn 减小边距
        super.onSizeChanged(w, h, oldw, oldh);

//        int dy = ShareData.m_screenRealHeight - ShareData.getScreenH();
//
//        if (dy > 0)
//        {
//            if (isVideoMode && mCtrFr != null && mPreviewRatio == CameraConfig.PreviewRatio.Ratio_16_9)
//            {
//                mCtrFr.setPadding(0, 0, 0, cn.poco.camera3.util.CameraPercentUtil.HeightPxToPercent(36 / 2 - 2));
//            }
//        }
//        else
//        {
//            if (isVideoMode && mCtrFr != null && mPreviewRatio == CameraConfig.PreviewRatio.Ratio_16_9)
//            {
//                mCtrFr.setPadding(0, 0, 0, cn.poco.camera3.util.CameraPercentUtil.HeightPxToPercent(36));
//            }
//        }
    }

    private void showToast(@StringRes int resId)
    {
        dismissToast();
        mToast = Toast.makeText(getContext(), resId, Toast.LENGTH_SHORT);
        mToast.show();
    }

    private void dismissToast()
    {
        if (mToast != null)
        {
            mToast.cancel();
            mToast = null;
        }
    }

    public void setThirdParty(boolean thirdParty)
    {
        isThirdParty = thirdParty;
    }

    public void setPageShow(boolean show)
    {
    }

    /**
     * 返回合成视频路径，null的话调用原视频路径
     *
     * @return
     */
    public String getVideoOutputPath()
    {
        return mVideoOutputPath;
    }

    /**
     * 返回合成视频返回的总时长
     *
     * @return
     */
    public float getVideoDuration()
    {
        return mVideoOutDuration;
    }

    /**
     * 是否视频预览
     *
     * @return true 视频预览 false 照片预览
     */
    public boolean isVideoMode()
    {
        return isVideoMode;
    }

    public void onPause(boolean isChangeNavigationBar)
    {
        onPause(false, isChangeNavigationBar);
    }

    /**
     * @param screenWakeUp 保持亮屏幕
     */
    public void onPause(boolean screenWakeUp, boolean isChangeNavigationBar)
    {
        isPause = true;
        abandonAudioFocus();
        keepScreenWakeUp(screenWakeUp);
        if (mVideoView != null) mVideoView.pause();
        if (mVideoBgmPage != null) mVideoBgmPage.onPause();
        if (isChangeNavigationBar)
        {
            changeSystemUiVisibility(View.VISIBLE);
        }
    }

    public void onResume(boolean isChangeNavigationBar)
    {
        isPause = false;
        requestAudioFocus();
        keepScreenWakeUp(true);
        if (isMixing) return;
        if (mSelectMusicPage != null && !mSelectMusicPage.isFold()) return;
        if (mVideoView != null) mVideoView.resume();
        if (mVideoBgmPage != null) mVideoBgmPage.onResume();
        if (isChangeNavigationBar)
        {
            changeSystemUiVisibility(View.GONE);
        }
    }

    public void onClose()
    {
        changeSystemUiVisibility(View.VISIBLE);
        dismissToast();
        abandonAudioFocus();
        keepScreenWakeUp(false);
        unregisterBrightnessObserver();
        if (mVolumeChangeReceiver != null)
        {
            getContext().unregisterReceiver(mVolumeChangeReceiver);
            mVolumeChangeReceiver.setListener(null);
        }

        if (mVideoBgmPage != null)
        {
            mVideoBgmPage.clear();
        }

        if (mSelectMusicPage != null)
        {
            mSelectMusicPage.clearAll();
        }

        if (mProgressDialog != null)
        {
            MyFramework.ClearTopView(getContext());
            mProgressDialog.cancel();
            mProgressDialog.setListener(null);
            mProgressDialog.release();
        }

        if (mVideoView != null)
        {
            mVideoView.release();
        }

        if (mPictureView != null)
        {
            mPictureView.setVerFilterCB(null);
        }
        mOnProgressListener = null;
        mBrightObserver = null;
        mProgressDialog = null;
        mVolumeChangeReceiver = null;
        mVideoBgmPage = null;
        mSelectMusicPage = null;
        mVideoView = null;
        mPictureView = null;
        mVideoCallback = null;
        mOnAnimationClickListener = null;
        System.gc();
    }

    public interface OnClickListener
    {
        public void click(int id, boolean isVideo);

        //视频已经准备好
        public void onReady();

        //开始混合视频
        public void onStartMix();
    }

    protected ClipMusicView.OnCallBack mClipMusicCallback = new ClipMusicView.OnCallBack()
    {
        @Override
        public void onStop(int second)
        {

            //跳转到指定时间戳（秒）
            mStartBgmTime = second * 1000L;

            if (isPause)
            {
                if (mVideoBgmPage != null)
                {
                    mVideoBgmPage.setCurrentPosition((int) mStartBgmTime);
                }
                return;
            }

            if (mVideoBgmPage != null)
            {
                mVideoBgmPage.seekTo((int) mStartBgmTime);
            }

            //视频从新开始播放
            if (mVideoView != null)
            {
                mVideoView.seekTo(0);
            }
        }

        @Override
        public void onScroll(int mScrollStartTime)
        {

        }

        @Override
        public void onProgressChanged(ColorSeekBar seekBar, int progress, boolean isMusic)
        {
            showCirclePanel(seekBar, isMusic ? mMusicCirclePanel : mRecordCirclePanel, progress);

            if (isMusic)
            {
                //调节bgm音量
                if (mVideoBgmPage != null)
                {
                    mVideoBgmPage.setVolume(progress / 100f);
                }
            }
            else
            {
                //调节录音音量
                if (mVideoView != null)
                {
                    mVideoView.setVolume(progress / 100f);
                }
            }
        }

        @Override
        public void onStartTrackingTouch(ColorSeekBar seekBar, int progress, boolean isMusic)
        {
            showCirclePanel(seekBar, isMusic ? mMusicCirclePanel : mRecordCirclePanel, progress);
        }

        @Override
        public void onStopTrackingTouch(ColorSeekBar seekBar, int progress, boolean isMusic)
        {
            if (isMusic)
            {
                if (mMusicCirclePanel != null)
                {
                    mMusicCirclePanel.hide();
                }
                mMusicValue = progress;
            }
            else
            {
                if (mRecordCirclePanel != null)
                {
                    mRecordCirclePanel.hide();
                }

                //更新按钮
                boolean needUpdate = false;
                if (mRecordValue != progress && progress == 0)
                {
                    needUpdate = true;
                }
                else if (mRecordValue != progress && mRecordValue == 0)
                {
                    needUpdate = true;
                }
                mRecordValue = progress;
                if (mVideoSoundBtn != null && needUpdate)
                {
                    updateVideoSoundBtnState(progress == 0);
                    isVideoMute = progress == 0;
                }
            }
        }

        @Override
        public void onFoldView(boolean fold)
        {
            setClipMusicViewFold(fold);
        }

        @Override
        public boolean recordEnable()
        {
            return isRecordAudioEnable;
        }

        @Override
        public boolean isVideoMute()
        {
            return isVideoMute;
        }
    };

    protected SelectMusicPage.OnCallBack mSelectMusicCallback = new SelectMusicPage.OnCallBack()
    {
        @Override
        public void onClickBack()
        {
            //展开bgm列表
            if (mVideoBgmPage != null && mVideoBgmPage.isFold())
            {
                if (mOnAnimationClickListener != null)
                {
                    mOnAnimationClickListener.onAnimationClick(mBgmSoundBtn);
                }
            }

            setSelectMusicFrFold(!mSelectMusicPage.isFold());
        }

        @Override
        public void onClick(AudioStore.AudioInfo audioInfo)
        {
            //展开裁剪view，收缩本地音乐列表
            if (audioInfo == null) return;

            if (((audioInfo.getDuration() / 1000f) < 1.1f))
            {
                showToast(R.string.lightapp06_video_bgm_short_audio_tip);
                return;
            }
            dismissToast();

            //Log.d(TAG, "TestPage --> onClick: " + audioInfo.toString());

            initBgmView();
            mVideoBgmPage.setMusicPath(audioInfo.getPath());

            //弹出音乐横向列表
            setSelectMusicFrFold(true);
            setBgmFrFold(!mVideoBgmPage.isFold());
            //插入到列表中去，更新下标
            if (!isAddLocalBGM)
            {
                isAddLocalBGM = true;
                insertBgmLocalInfo(audioInfo);
            }
            else
            {
                updateBgmLocalInfo(audioInfo, PreviewBgmResMgr2.BMG_INFO_LOCAL_SELECT_ID);
            }
        }
    };

    private OnAnimationClickListener mOnAnimationClickListener = new OnAnimationClickListener()
    {
        @Override
        public void onAnimationClick(View v)
        {
            if (v == mBackBtn) //返回
            {
                MyBeautyStat.onClickByRes(VideoPreviewPage.this.isVideoMode ? R.string.拍照_视频预览页_主页面_返回 : R.string.拍照_萌妆照预览页_主页面_返回);
                if (mListener != null) mListener.click(ID_BACK, isVideoMode);
            }
            else if (v == mShareBtn) //分享
            {
                onPause(true, false);
                if (isVideoMode)
                {
                    //开始合成视频
                    MyBeautyStat.onClickByRes(R.string.拍照_视频预览页_主页面_保存);
                    mixVideo();
                }
                else
                {
                    MyBeautyStat.onClickByRes(R.string.拍照_萌妆照预览页_主页面_保存);
                    mVideoOutputPath = null;
                    clickToShare(false);
                }
            }
            else if (v == mBgmSoundBtn) //bgm音效
            {
                if (isAnimation) return;

                initBgmView();
                boolean fold = mVideoBgmPage.isFold();
                mVideoView.doAnim(fold);
                setBgmFrFold(!fold);
                MyBeautyStat.onClickByRes(R.string.拍照_视频预览页_主页面_音乐);
                TongJi2.AddCountByRes(getContext(), R.integer.拍照_动态贴纸_预览页_背景音乐按钮);
            }
            else if (v == mVideoSoundBtn) //视频声音
            {
                boolean isRecordVoiceEnable = VideoPreviewPage.this.isRecordAudioEnable;
                if (!isRecordVoiceEnable)
                {
                    showToast(R.string.lightapp06_share_mic_error);
                    mRecordValue = 0;
                    isVideoMute = true;
                    return;
                }

                if (mVideoView != null)
                {
                    isVideoMute = !isVideoMute;
                    mRecordValue = !isVideoMute ? 100 : 0;
                    float volume = isVideoMute ? 0f : mRecordValue / 100f;
                    mVideoView.setVolume(volume);
                    updateVideoSoundBtnState(isVideoMute);
                    showToast(isVideoMute ? R.string.lightapp06_share_mic_mute : R.string.lightapp06_share_mic_unmute);
                    /*埋点（录音开关）*/
                    MyBeautyStat.onClickByRes(isVideoMute ? R.string.拍照_视频预览页_主页面_原声关 : R.string.拍照_视频预览页_主页面_原声开);
                    TongJi2.AddCountByRes(getContext(), isVideoMute ? R.integer.拍照_动态贴纸_预览页_静音_开 : R.integer.拍照_动态贴纸_预览页_静音_关);
                }
            }
        }
    };

    private void updateVideoSoundBtnState(boolean isVideoMute)
    {
        if (mVideoSoundBtn != null)
        {
            mVideoSoundBtn.setImageBitmap(ImageUtils.AddSkin(getContext(),
                    BitmapFactory.decodeResource(getResources(), isVideoMute ? R.drawable.light_app06_video_sound_off : R.drawable.light_app06_video_sound_on)));
        }
    }

    /**
     * 构造混音 开始混音
     */
    private void mixVideo()
    {
        ProcessInfo info = new ProcessInfo();
        info.m_bg_music_start = mStartBgmTime;          //音乐起始时间
        info.m_bg_music_path = mVideoBgmPage != null
                ? mVideoBgmPage.getMusicPath() : null;  //音乐路径
        info.m_bg_volume_adjust = mMusicValue / 100f;   //音量调节
        info.m_video_volume_adjust = mRecordValue / 100f;

        info.is_clear_temp_cache = true;                //是否清楚缓存
        if (isVideoMode && mPreviewRes != null)         //分段视频路径
        {
            String[] paths = new String[mPreviewRes.length];
            for (int i = 0; i < mPreviewRes.length; i++)
            {
                paths[i] = (String) mPreviewRes[i];
            }
            info.m_video_paths = paths;
        }
        info.m_video_rotation = mOrientation;
        info.is_silence_play = isVideoMute;             //视频录音是否静音
        //info.is_output_video_to_sys = isThirdParty;     //是否为第三方调用
        info.is_clear_temp_cache = true;               //是否清理缓存
        String tempPath = FileUtils.getVideoOutputSysPath(); //输出路径
        VideoMixProcessorV2 processor = new VideoMixProcessorV2(info, tempPath);
        processor.setOnProgressListener(mixCallback);
        processor.start();
    }

    VideoMixProcessorV2.OnProcessListener mixCallback = new VideoMixProcessorV2.OnProcessListener()
    {
        @Override
        public void onStart()
        {
            isMixing = true;
            initDialog();
            showProgressDialog(false);
            if (mListener != null)
            {
                mListener.onStartMix();
            }
        }

        @Override
        public void onProgress(long timeStamp)
        {
            //nothing to do
        }

        @Override
        public void onFinish(VideoMixProcessorV2.MixOutInfo mixOutInfo)
        {
            if (isVideoMode)
            {
                //视频分享
                mVideoOutputPath = mixOutInfo.mPath;
                mVideoOutDuration = mixOutInfo.mDuration;
            }

            if (isMixing)
            {
                //结束进度旋转，目的是想让进度走完，最终回调dialog的监听
                showProgressDialog(true);
            }
            else
            {
                isMixing = false;
                dismissProgressDialog();
                clickToShare(true);
            }
        }

        @Override
        public void onError(int what)
        {
            if (what == VideoMixProcessorV2.ERROR_CODE_NO_VIDEOS) //无视频
            {
                showToast(R.string.camerapage_invalid_video);
            }
            else if (what == VideoMixProcessorV2.ERROR_CODE_VIDEOS_MISSING) //视频丢失
            {
                showToast(R.string.camerapage_invalid_video);
            }

            if (isMixing)
            {
                //结束进度旋转，目的是想让进度走完，最终回调dialog的监听
                showProgressDialog(true);
            }
            else
            {
                isMixing = false;
                dismissProgressDialog();
            }
        }
    };

    /**
     * 进度dialog 监听
     */
    private AutoRoundProgressBar.OnProgressListener mOnProgressListener = new AutoRoundProgressBar.OnProgressListener()
    {
        @Override
        public void onProgress(int progress)
        {
        }

        @Override
        public void onFinish()
        {
            dismissProgressDialog();

            if (isMixing)
            {
                isMixing = false;
                clickToShare(isVideoMode);
            }
        }
    };

    private void clickToShare(boolean isVideo)
    {
        if (mListener != null) mListener.click(ID_SHARE, isVideo);
    }

    /**
     * 显示dialog
     *
     * @param isFinishProgress 是否结束进度
     */
    private void showProgressDialog(boolean isFinishProgress)
    {
        if (mProgressDialog != null)
        {
            if (!mProgressDialog.isStart())
            {
                FrameLayout.LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
                MyFramework.AddTopView(getContext(), mProgressDialog, layoutParams);
                mProgressDialog.start();

            }
            mProgressDialog.setFinishProgress(isFinishProgress);
        }
    }

    /**
     * 隐藏dialog
     */
    private void dismissProgressDialog()
    {
        if (mProgressDialog != null)
        {
            MyFramework.ClearTopView(getContext());
            mProgressDialog.cancel();
        }
    }


    public void setProgressDlgBackground(Bitmap in)
    {
        mDialogBk = in;
    }

    private void setClipMusicViewFold(final boolean fold)
    {
        if (mClipMusicView == null) return;

        float startY, endY;
        if (fold)
        {
            startY = 0;
            endY = cn.poco.camera3.util.CameraPercentUtil.HeightPxToPercent(320);
        }
        else
        {
            startY = cn.poco.camera3.util.CameraPercentUtil.HeightPxToPercent(320);
            endY = 0;
        }
        mClipMusicView.setFold(fold);
        if (!fold)
        {
            mClipMusicView.setVisibility(VISIBLE);
        }

        TranslateAnimation anim = new TranslateAnimation(0, 0, startY, endY);
        anim.setDuration(300);
        anim.setAnimationListener(new Animation.AnimationListener()
        {
            @Override
            public void onAnimationStart(Animation animation)
            {
                isAnimation = true;
            }

            @Override
            public void onAnimationEnd(Animation animation)
            {
                isAnimation = false;
                if (fold && mClipMusicView != null)
                {
                    mClipMusicView.setVisibility(GONE);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation)
            {

            }
        });
        mClipMusicView.startAnimation(anim);
    }

    private void setSelectMusicFrFold(final boolean fold)
    {
        if (mSelectMusicPage == null) return;

        float startY, endY;
        if (fold)
        {
            startY = 0;
            endY = ShareData.m_screenRealHeight;
        }
        else
        {
            startY = ShareData.m_screenRealHeight;
            endY = 0;
        }
        if (!fold)
        {
            mSelectMusicPage.setVisibility(VISIBLE);
        }
        mSelectMusicPage.setFold(fold);

        TranslateAnimation anim = new TranslateAnimation(0, 0, startY, endY);
        anim.setDuration(300);
        anim.setAnimationListener(new Animation.AnimationListener()
        {
            @Override
            public void onAnimationStart(Animation animation)
            {
                isAnimation = true;
            }

            @Override
            public void onAnimationEnd(Animation animation)
            {
                isAnimation = false;
                if (fold && mSelectMusicPage != null)
                {
                    mSelectMusicPage.setVisibility(GONE);
                    if (mVideoBgmPage != null)
                    {
                        mVideoBgmPage.setBtnClickable(true);
                    }
                }
                if (!fold && mSelectMusicPage != null && !mSelectMusicPage.isLoaded())
                {
                    mSelectMusicPage.loadData();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation)
            {

            }
        });
        mSelectMusicPage.startAnimation(anim);

        if (fold)
        {
            if (mVideoView != null) mVideoView.resume();
            if (mVideoBgmPage != null) mVideoBgmPage.onResume();
        }
        else
        {
            //展开
            if (mVideoView != null) mVideoView.pause();
            if (mVideoBgmPage != null) mVideoBgmPage.onPause();
        }
    }

    private void updateBgmLocalInfo(AudioStore.AudioInfo audioInfo, int info_id)
    {
        if (mVideoBgmPage != null)
        {
            mVideoBgmPage.updateAdapterInfo(audioInfo, info_id);
        }
    }

    private void insertBgmLocalInfo(AudioStore.AudioInfo audioInfo)
    {
        if (mVideoBgmPage != null)
        {
            mVideoBgmPage.insertBgmLocalInfo(audioInfo);
        }
    }

    /**
     * bgm音乐列表收展
     *
     * @param fold true 收
     */
    private void setBgmFrFold(final boolean fold)
    {
        float startY, endY;
        if (fold)
        {
            startY = 0;
            endY = cn.poco.camera3.util.CameraPercentUtil.HeightPxToPercent(320);
        }
        else
        {
            startY = cn.poco.camera3.util.CameraPercentUtil.HeightPxToPercent(320);
            endY = 0;
        }

        if (!fold)
        {
            mVideoBgmPage.setVisibility(VISIBLE);
        }

        mVideoBgmPage.setFold(fold);

        TranslateAnimation anim = new TranslateAnimation(0, 0, startY, endY);
        anim.setDuration(300);
        anim.setAnimationListener(new Animation.AnimationListener()
        {
            @Override
            public void onAnimationStart(Animation animation)
            {
                isAnimation = true;
                mVideoBgmPage.setUIEnable(false);
            }

            @Override
            public void onAnimationEnd(Animation animation)
            {
                isAnimation = false;
                if (fold && mVideoBgmPage != null)
                {
                    mVideoBgmPage.setVisibility(GONE);
                }
                if (mVideoBgmPage != null) mVideoBgmPage.setUIEnable(true);
            }

            @Override
            public void onAnimationRepeat(Animation animation)
            {

            }
        });
        if (isVideoMode)
        {
            mVideoView.setProgressAlpha(fold);
        }
        mVideoBgmPage.startAnimation(anim);
    }

    /**
     * 收缩列表，视频合成等back操作
     *
     * @return true 表示进行操作中，false 无操作
     */
    public boolean onBack()
    {
        if (mClipMusicView != null)
        {
            if (!mClipMusicView.isFold())
            {
                setClipMusicViewFold(true);
                return true;
            }
        }

        if (mVideoBgmPage != null)
        {
            if (!mVideoBgmPage.isFold())
            {
                if (mOnAnimationClickListener != null)
                {
                    mOnAnimationClickListener.onAnimationClick(mBgmSoundBtn);
                }
                return true;
            }
        }

        if (mSelectMusicPage != null)
        {
            if (!mSelectMusicPage.isFold())
            {
                //弹出音乐横向列表
                setSelectMusicFrFold(true);
                setBgmFrFold(!mVideoBgmPage.isFold());
                return true;
            }
        }

        if (mClipMusicView != null)
        {
            if (!mClipMusicView.isFold())
            {
                setClipMusicViewFold(true);
                return true;
            }
        }

        //视频合成中
        if (isMixing) return true;

        return false;
    }

    private void showErrorDialog()
    {
        //FIXME if (true) return;
        new AlertDialog.Builder(getContext())
                .setCancelable(true)
                .setTitle(R.string.tips)
                .setMessage("player error")
                .setPositiveButton(R.string.ok, null).create().show();
    }

    protected VideoView.OnVideoViewPlayCallback mVideoCallback = new VideoView.OnVideoViewPlayCallback()
    {

        @Override
        public void onReady()
        {
            if (mListener != null)
            {
                mListener.onReady();
            }
        }

        @Override
        public void onPlayerError(ExoPlaybackException error,String errorMsg)
        {
            mPlayerError = true;

            showErrorDialog();

            if (mVideoBgmPage != null)
            {
                mVideoBgmPage.seekTo(0, false);
            }
        }

        @Override
        public void onVideoPlayCompleted()
        {
            //重新开始播放音频
            if (mVideoBgmPage != null)
            {
                mVideoBgmPage.seekTo((int) mStartBgmTime);
            }
        }

        @Override
        public void onVideoSeekTo(long millSecond)
        {
            if (isPause)
            {
                return;
            }

            if (mVideoBgmPage != null)
            {
                long duration = mVideoBgmPage.getMusicDuration();
                if (duration > 0)
                {
                    int startTime = (int) ((millSecond + mStartBgmTime) % duration);
                    mVideoBgmPage.seekTo(startTime);
                }
            }
        }

        @Override
        public void onVideoProgressStartTouch()
        {
            if (mVideoBgmPage != null)
            {
                mVideoBgmPage.onPause();
            }
        }

        @Override
        public void onVideoPlayPosition(long duration, long position)
        {
            float progress = position * 1f / duration * 100f;
            if (isSupportFadeVolume)
            {
                if (progress >= 0)
                {
                    //当前播放视频时间戳
                    int currentDuration = (int) (progress * mDuration / 100);

                    if (currentDuration <= MAX_FADE_IN_VOLUME_DURATION)
                    {
                        //淡入
                        isNeedResumeFadeVolume = true;
                        float music = ((mMusicValue / 100f) * currentDuration) / MAX_FADE_IN_VOLUME_DURATION;
                        float video = ((mRecordValue / 100f) * currentDuration) / MAX_FADE_IN_VOLUME_DURATION;
                        if (mVideoBgmPage != null)
                        {
                            mVideoBgmPage.setVolume(music);
                        }
                        if (mVideoView != null)
                        {
                            mVideoView.setVolume(video);
                        }
                        //Log.d(TAG, "VideoPreviewPage --> onVideoPlayPosition: 淡入 ds " + currentDuration + " bgm " + music + " video " + video);
                    }
                    else if (mDuration - MAX_FADE_OUT_VOLUME_DURATION <= currentDuration)
                    {
                        //淡出
                        isNeedResumeFadeVolume = true;
                        currentDuration = (int) (mDuration - currentDuration);
                        float music = ((mMusicValue / 100f) * currentDuration) / MAX_FADE_OUT_VOLUME_DURATION;
                        float video = ((mRecordValue / 100f) * currentDuration) / MAX_FADE_OUT_VOLUME_DURATION;
                        if (mVideoBgmPage != null)
                        {
                            mVideoBgmPage.setVolume(music);
                        }
                        if (mVideoView != null)
                        {
                            mVideoView.setVolume(video);
                        }
                        //Log.d(TAG, "VideoPreviewPage --> onVideoPlayPosition: 淡出 ds " + currentDuration + " bgm " + music + " video " + video);
                    }
                    else if (isNeedResumeFadeVolume)
                    {
                        isNeedResumeFadeVolume = false;
                        if (mVideoBgmPage != null) mVideoBgmPage.setVolume(mMusicValue / 100f);
                        if (mVideoView != null) mVideoView.setVolume(mRecordValue / 100f);
                        //Log.d(TAG, "VideoPreviewPage --> onVideoPlayPosition: 恢复");
                    }
                }
            }
        }

        @Override
        public boolean isPause()
        {
            return isPause;
        }
    };

    protected VideoBgmPage.Callback mCallback = new VideoBgmPage.Callback()
    {
        @Override
        public void onFold(boolean isFold)
        {
            if (isFold) //收
            {
                if (mOnAnimationClickListener != null)
                {
                    mOnAnimationClickListener.onAnimationClick(mBgmSoundBtn);
                }
            }
        }

        @Override
        public float getVolume()
        {
            return mMusicValue / 100f;
        }

        @Override
        public void onMediaStart()
        {
            //重新播放视频
            if (mVideoView != null)
            {
                mVideoView.seekTo(0);
            }
        }

        @Override
        public void openLocalMusic()
        {
            //打开本地音乐列表
            MyBeautyStat.onClickByRes(R.string.拍照_视频预览页_主页面_本地音乐);
            initSelectMusicPage();
            setSelectMusicFrFold(!mSelectMusicPage.isFold());

            if (mVideoBgmPage != null && !mVideoBgmPage.isFold())
            {
                setBgmFrFold(true);
            }
        }

        /**
         * 打开裁剪音频频段view
         * @param info
         */
        @Override
        public void openClipView(PreviewBgmInfo info)
        {
            if (mClipMusicView != null && mClipMusicView.getFrequencyInfo() != null)
            {
                if (mClipMusicView.getFrequencyInfo().id == info.getId())
                {
                    setClipMusicViewFold(!mClipMusicView.isFold());
                    mClipMusicView.setBtnStatus(false);
                    mClipMusicView.setRecordSeekBar(isVideoMute ? 0 : mRecordValue, !isVideoMute);
                    mClipMusicView.setMusicSeekBar(mMusicValue, true);
                    return;
                }
            }

            if ((info.getDuration() / 1000f) < 1.1f)
            {
                showToast(R.string.lightapp06_video_bgm_short_audio);
                return;
            }
            dismissToast();

            //弹出裁剪音频view
            ClipMusicView.FrequencyInfo clipInfo = new ClipMusicView.FrequencyInfo();
            clipInfo.id = info.getId();
            clipInfo.musicPath = (String) info.getRes();
            clipInfo.musicTime = (int) (info.getDuration() / 1000f);
            clipInfo.videoTime = (int) (mDuration / 1000f);
            clipInfo.startTime = 0;
            initClipMusicView(clipInfo);
            mClipMusicView.setRecordSeekBar(isVideoMute ? 0 : mRecordValue, !isVideoMute);
            mClipMusicView.setRecordSeekBarCanScroll(isRecordAudioEnable);
            mClipMusicView.setMusicSeekBar(mMusicValue, true);
            setClipMusicViewFold(!mClipMusicView.isFold());
        }

        @Override
        public void onSelectMusic(boolean isNonMusic)
        {
            //选择无音乐素材时候，录音音量为100%
            mStartBgmTime = 0;
            mMusicValue = 60;
            if (isNonMusic)
            {
                //无音乐素材，如果recordValue > 0 ,则恢复100
                if (mRecordValue > 0)
                {
                    mRecordValue = 100;
                }

                if (!isRecordAudioEnable)
                {
                    mRecordValue = 0;
                }

                updateVideoSoundBtnState(mRecordValue == 0);
            }

            if (mVideoView != null)
            {
                mVideoView.setVolume(mRecordValue / 100f);
            }

            if (mClipMusicView != null)
            {
                removeView(mClipMusicView);
                mClipMusicView = null;
            }
        }
    };
}
