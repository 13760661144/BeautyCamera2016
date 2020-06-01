package cn.poco.lightApp06;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.ContentObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.util.TypedValue;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.ExoPlaybackException;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import cn.poco.acne.view.CirclePanel;
import cn.poco.advanced.ImageUtils;
import cn.poco.beauty.view.ColorSeekBar;
import cn.poco.camera.BrightnessUtils;
import cn.poco.camera.CameraSetDataKey;
import cn.poco.camera2.AudioControlUtils;
import cn.poco.camera3.VideoMgr;
import cn.poco.camera3.info.PreviewBgmInfo;
import cn.poco.camera3.util.CameraPercentUtil;
import cn.poco.framework.BaseSite;
import cn.poco.framework.DataKey;
import cn.poco.framework.IPage;
import cn.poco.framework.MyFramework;
import cn.poco.image.filter;
import cn.poco.lightApp06.site.BeautyVideoPageSite;
import cn.poco.lightApp06.site.BeautyVideoPageSite500;
import cn.poco.resource.PreviewBgmResMgr2;
import cn.poco.statistics.MyBeautyStat;
import cn.poco.statistics.TongJi2;
import cn.poco.system.SysConfig;
import cn.poco.tianutils.MakeBmp;
import cn.poco.tianutils.ShareData;
import cn.poco.utils.FileUtil;
import cn.poco.utils.OnAnimationClickListener;
import cn.poco.video.AudioStore;
import cn.poco.video.FileUtils;
import cn.poco.video.NativeUtils;
import cn.poco.video.ProcessInfo;
import cn.poco.video.VideoMixProcessorV2;
import cn.poco.video.VideoView;
import cn.poco.video.music.SelectMusicPage;
import cn.poco.video.view.AutoRoundProgressBar;
import cn.poco.video.view.AutoRoundProgressView;
import cn.poco.video.view.ClipMusicView;
import my.beautyCamera.R;

/**
 * @author lmx
 *         Created by lmx on 2017-12-21.
 */

public class BeautyVideoPage extends IPage implements AudioManager.OnAudioFocusChangeListener
{
    private BeautyVideoPageSite mSite;

    private AudioManager mAudioManager;
    private BrightObserver mBrightObserver;
    private VideoMgr mVideoMgr;

    private VideoView mVideoView;
    private VideoBgmPage mVideoBgmPage;
    private SelectMusicPage mSelectMusicPage;
    private ClipMusicView mClipMusicView;
    private AutoRoundProgressView mDialog;

    private BeautyVideoSharePage mSharePage;

    private BtnFr mBackBtn;
    private BtnFr mMusicBtn;
    private BtnFr mRecordBtn;
    private ImageView mSaveBtn;

    private Bitmap mPreIconBmp;
    private Bitmap mGlassBmp;
    private Toast mToast;
    private CirclePanel mRecordCirclePanel;
    private CirclePanel mMusicCirclePanel;
    private float mCircleRadius;

    private int mRecordValue = 100;                 //录音默认音量
    private int mMusicValue = 60;                   //bgm默认音量
    private long mDuration = 0;
    private float mPreviewRatio;                    //预览比例
    private int mOrientation;
    private String mMp4Path;
    private String mFirstVideoPath;
    private int mFilterId;
    private int mResId;
    private String mResTjId;

    //镜头视频预览比例
    private int mVideoPreViewWidth;
    private int mVideoPreviewHeight;

    private long mStartBgmTime = 0L;                //音频开始时间 单位：毫秒

    private boolean isPause;

    private boolean isRecordAudioEnable = true;     //是否有视频录音
    private boolean isVideoMute = false;            //视频是否静音
    private boolean isAnimation = false;            //是否在动画中
    private boolean isAudioActive = false;          //是否在活跃中
    private boolean isThirdParty = false;           //是否第三方调用
    private boolean isMixing = false;               //混合视频中
    private boolean isAddLocalBGM = false;          //是否已经选择过bgm音乐
    private boolean mIsKeepScreenOn = false;        //保持屏幕常亮
    private boolean mInitPage = false;              //是否初始化完成
    private boolean isSaveVideo = false;            //是否保存过视频
    private boolean isSaveJump = false;             //视频合成成功后直接跳转到save，不弹出分享页

    private boolean isSupportFadeVolume = true;                     //是否支持淡入淡出
    private boolean isNeedResumeFadeVolume = false;                 //是否需要恢复音量
    private static final int MAX_FADE_IN_VOLUME_DURATION = 1500;    //最大淡入时间（单位 毫秒）
    private static final int MAX_FADE_OUT_VOLUME_DURATION = 1500;   //最大淡出时间（单位 毫秒）

    //虚拟键隐藏与显示
    private int mOriginVisibility = -1; //还原系统设置
    private int mSystemUiVisibility = -1;//当前设置系统


    private String mSavePath;
    private float mSaveDuration;

    public BeautyVideoPage(Context context, BaseSite site)
    {
        super(context, site);
        mSite = (BeautyVideoPageSite) site;
        initData();
        initView();
        MyBeautyStat.onPageStartByRes(R.string.拍照_视频预览页_主页面);
    }

    private void initData()
    {

        mCircleRadius = CameraPercentUtil.WidthPxToPercent(55);

        mAudioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        mBrightObserver = new BrightObserver(new Handler());
        registerBrightnessObserver();


        BrightnessUtils instance = BrightnessUtils.getInstance();
        if (instance != null)
        {
            instance.setContext(getContext()).unregisterBrightnessObserver();
            instance.resetToDefault();
            instance.clearAll();
        }
        AudioControlUtils.pauseOtherMusic(getContext());//暂停后台音乐
    }

    private void initView()
    {
        setBackgroundColor(0xffffffff);

        mVideoView = new VideoView(getContext());
        mVideoView.setOnTouchListener(new OnTouchListener()
        {
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
                        mOnAnimationClickListener.onAnimationClick(mMusicBtn);
                    }
                    return false;
                }
                return false;
            }
        });
        mVideoView.setCallback(mVideoCallback);
        mVideoView.setProgressViewShow(true);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ShareData.m_screenRealHeight - CameraPercentUtil.HeightPxToPercent(166) + CameraPercentUtil.HeightPxToPercent(14));
        params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        this.addView(mVideoView, 0, params);

        RelativeLayout mCtrFr = new RelativeLayout(getContext());
        mCtrFr.setBackgroundColor(Color.WHITE);
        params = new LayoutParams(LayoutParams.MATCH_PARENT, CameraPercentUtil.HeightPxToPercent(132));
        params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
        this.addView(mCtrFr, params);
        {

            mBackBtn = new BtnFr(getContext(), R.drawable.camera_pre_back_gray,
                    R.string.back, mOnAnimationClickListener, false);
            mBackBtn.setId(R.id.beauty_video_btn_back);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            lp.leftMargin = CameraPercentUtil.WidthPxToPercent(38);
            lp.leftMargin = mBackBtn.hasOffset() > 0 ? lp.leftMargin - mBackBtn.hasOffset() : lp.leftMargin;
            mCtrFr.addView(mBackBtn, lp);

            mMusicBtn = new BtnFr(getContext(), R.drawable.camera_pre_music,
                    R.string.video_preview_clip_music, mOnAnimationClickListener, true);
            mMusicBtn.setId(R.id.beauty_video_btn_music);
            lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            lp.addRule(RelativeLayout.RIGHT_OF, R.id.beauty_video_btn_back);
            lp.leftMargin = CameraPercentUtil.WidthPxToPercent(114);
            lp.leftMargin = mMusicBtn.hasOffset() > 0 ? lp.leftMargin - mMusicBtn.hasOffset() : lp.leftMargin;
            mCtrFr.addView(mMusicBtn, lp);

            mSaveBtn = new ImageView(getContext());
            mSaveBtn.setId(R.id.beauty_video_btn_save);
            mSaveBtn.setOnTouchListener(mOnAnimationClickListener);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            {
                mSaveBtn.setBackground(new BitmapDrawable(getResources(),
                        ImageUtils.AddSkin(getContext(), BitmapFactory.decodeResource(getResources(), R.drawable.camera_pre_video_save_icon_bg))));
            }
            else
            {
                mSaveBtn.setBackgroundDrawable(new BitmapDrawable(getResources(),
                        ImageUtils.AddSkin(getContext(), BitmapFactory.decodeResource(getResources(), R.drawable.camera_pre_video_save_icon_bg))));
            }
            mSaveBtn.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            mSaveBtn.setImageResource(R.drawable.camera_pre_video_save_icon);
            lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            lp.rightMargin = CameraPercentUtil.WidthPxToPercent(48);
            mCtrFr.addView(mSaveBtn, lp);

            mRecordBtn = new BtnFr(getContext(), R.drawable.camera_pre_record_on,
                    R.string.video_preview_clip_record, mOnAnimationClickListener, true);
            mRecordBtn.setId(R.id.beauty_video_btn_record);
            lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            lp.addRule(RelativeLayout.LEFT_OF, R.id.beauty_video_btn_save);
            lp.rightMargin = CameraPercentUtil.WidthPxToPercent(99);
            lp.rightMargin = mRecordBtn.hasOffset() > 0 ? lp.rightMargin - mRecordBtn.hasOffset() : lp.rightMargin;
            mCtrFr.addView(mRecordBtn, lp);
        }
    }

    private void initBgmView()
    {
        if (mVideoBgmPage != null) return;
        mVideoBgmPage = new VideoBgmPage(getContext());
        mVideoBgmPage.setBackgroundColor(0xe6f0f0f0);
        mVideoBgmPage.setCallback(mBgmCallback);
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
        params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, CameraPercentUtil.HeightPxToPercent(120));
        params.gravity = Gravity.BOTTOM;
        params.bottomMargin = CameraPercentUtil.HeightPxToPercent(256);
        this.addView(mRecordCirclePanel, params);

        if (mMusicCirclePanel != null) removeView(mMusicCirclePanel);

        mMusicCirclePanel = new CirclePanel(getContext());
        params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, CameraPercentUtil.HeightPxToPercent(120));
        params.gravity = Gravity.BOTTOM;
        params.bottomMargin = CameraPercentUtil.HeightPxToPercent(166);
        this.addView(mMusicCirclePanel, params);
    }

    private void initDialog()
    {
        if (mDialog != null) return;
        mDialog = new AutoRoundProgressView(getContext());
        mDialog.setListener(mOnProgressListener);
        if (mGlassBmp != null && !mGlassBmp.isRecycled())
        {
            mDialog.setBackgroundThumb(mGlassBmp);
        }
        else
        {
            Bitmap bitmap = mVideoView.getBitmap();
            if (bitmap != null)
            {
                mGlassBmp = filter.fakeGlassBeauty(MakeBmp.CreateBitmap(bitmap, ShareData.m_screenWidth, ShareData.m_screenHeight, -1, 0, Bitmap.Config.ARGB_8888), 0x19000000);
                mDialog.setBackgroundThumb(mGlassBmp);
            }
        }
    }

    /**
     * 显示dialog
     *
     * @param isFinishAutoProgress 是否结束自动进度
     */
    private void showDialog(boolean isFinishAutoProgress)
    {
        if (mDialog != null)
        {
            if (!mDialog.isStart())
            {
                FrameLayout.LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
                MyFramework.AddTopView(getContext(), mDialog, layoutParams);
                mDialog.start();

            }
            mDialog.setFinishProgress(isFinishAutoProgress);
        }
    }

    /**
     * 隐藏dialog
     */
    private void dismissDialog()
    {
        if (mDialog != null)
        {
            MyFramework.ClearTopView(getContext());
            mDialog.cancel();
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

    public void setVideoDuration(long duration)
    {
        mDuration = duration;

        isSupportFadeVolume = mDuration >= 3500L;
    }


    @Override
    public void SetData(HashMap<String, Object> params)
    {
        if (params != null)
        {
            if (params.containsKey(DataKey.COLOR_FILTER_ID))
            {
                mFilterId = (Integer) params.get(DataKey.COLOR_FILTER_ID);
            }
            if (params.containsKey("res_id"))
            {
                this.mResId = (Integer) params.get("res_id");
            }
            if (params.containsKey("res_tj_id"))
            {
                this.mResTjId = (String) params.get("res_tj_id");
            }
            if (params.containsKey("mp4_path"))
            {
                mMp4Path = (String) params.get("mp4_path");
            }
            if (params.containsKey("width"))
            {
                mVideoPreViewWidth = (Integer) params.get("width");
            }
            if (params.containsKey("height"))
            {
                mVideoPreviewHeight = (Integer) params.get("height");
            }
            if (params.containsKey("ratio"))
            {
                mPreviewRatio = (Float) params.get("ratio");
            }
            if (params.containsKey("video_mgr"))
            {
                mVideoMgr = (VideoMgr) params.get("video_mgr");
            }
            if (params.containsKey("orientation"))
            {
                mOrientation = (int) params.get("orientation");
            }
            if (params.containsKey("save_jump"))
            {
                this.isSaveJump = (Boolean) params.get("save_jump");
            }
            if (params.containsKey("record_audio_enable"))
            {
                this.isRecordAudioEnable = (Boolean) params.get("record_audio_enable");
            }
            if (params.containsKey("thirdParty"))
            {
                this.isThirdParty = (Boolean) params.get("thirdParty");
            }
        }


        //判断视频是否有录音
        isVideoMute = !isRecordAudioEnable;
        updateVideoSoundBtnState(isVideoMute);

        mVideoView.setOrientation(mOrientation);
        mVideoView.setVideoPreviewWidth(mVideoPreViewWidth);
        mVideoView.setVideoPreviewHeight(mVideoPreviewHeight);

        if (mVideoMgr != null
                && mVideoMgr.getVideoList() != null
                && mVideoMgr.getVideoNum() > 0)
        {
            setVideoDuration(mVideoMgr.getRecordDuration());
            mVideoView.setDuration(mVideoMgr.getRecordDuration());

            ArrayList<String> videoPath = new ArrayList<>();
            for (int i = 0, size = mVideoMgr.getVideoNum(); i < size; i++)
            {
                VideoMgr.SubVideo subVideo = mVideoMgr.getVideoList().get(i);
                if (subVideo != null)
                {
                    if (i == 0)
                    {
                        mFirstVideoPath = subVideo.mPath;
                    }

                    if (!FileUtils.isFileValid(subVideo.mPath))
                    {
                        continue;
                    }

                    videoPath.add(subVideo.mPath);
                }
            }

            if (videoPath.size() > 0)
            {
                mVideoView.setVideoPath(videoPath);
                mVideoView.prepared();
                mVideoView.start();
            }
            else
            {
                Toast.makeText(getContext(), getResources().getString(R.string.lightapp06_not_exist_video), Toast.LENGTH_LONG).show();
            }
        }
        else
        {
            Toast.makeText(getContext(), getResources().getString(R.string.lightapp06_not_exist_video), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBack()
    {
        //视频合成中
        if (isMixing) return;

        if (isShowSharePage())
        {
            mSharePage.onBack();
            return;
        }

        if (mClipMusicView != null && !mClipMusicView.isFold())
        {
            setClipMusicViewFold(true);
            return;
        }

        if (mSelectMusicPage != null && !mSelectMusicPage.isFold())
        {
            //弹出音乐横向列表
            setSelectMusicFrFold(true);
            resumeBgmAndVideo();
            return;
        }

        if (mVideoBgmPage != null && !mVideoBgmPage.isFold())
        {
            if (mOnAnimationClickListener != null)
            {
                mOnAnimationClickListener.onAnimationClick(mMusicBtn);
            }
            return;
        }

        if (mClipMusicView != null && !mClipMusicView.isFold())
        {
            setClipMusicViewFold(true);
            return;
        }

        if (mVideoView != null) mVideoView.pause();
        if (mVideoBgmPage != null) mVideoBgmPage.onPause();

        if (isThirdParty)
        {
            mSite.onThirdPartyBack(getContext());
            return;
        }
        mSite.onBack(getContext(), getVideoData());
    }

    @Override
    public void onResume()
    {
        super.onResume();
        isPause = false;
        requestAudioFocus();
        keepScreenWakeUp(true);

        //分享页打开，不resume视频和背景音乐
        if (isShowSharePage())
        {
            mSharePage.onResume();
            return;
        }

        //混合视频中，本地音乐列表打开中，不resume视频和背景音乐的
        if (isMixing || (mSelectMusicPage != null && !mSelectMusicPage.isFold())) return;
        if (mVideoView != null) mVideoView.resume();
        if (mVideoBgmPage != null) mVideoBgmPage.onResume();
    }


    @Override
    public void onPause()
    {
        super.onPause();
        isPause = true;
        abandonAudioFocus();
        keepScreenWakeUp(false);
        if (mVideoView != null) mVideoView.pause();
        if (mVideoBgmPage != null) mVideoBgmPage.onPause();
        if (isShowSharePage()) mSharePage.onPause();
    }

    @Override
    public void onClose()
    {
        changeSystemUiVisibility(View.VISIBLE);
        dismissToast();
        abandonAudioFocus();
        keepScreenWakeUp(false);
        unregisterBrightnessObserver();
        AudioControlUtils.setCanResumeMusic(true);
        AudioControlUtils.resumeOtherMusic(getContext());
        MyFramework.ClearTopView(getContext());

        if (mVideoBgmPage != null)
        {
            mVideoBgmPage.clear();
        }

        if (mSelectMusicPage != null)
        {
            mSelectMusicPage.clearAll();
        }

        if (mDialog != null)
        {
            mDialog.cancel();
            mDialog.setListener(null);
            mDialog.release();
        }

        if (mVideoView != null)
        {
            mVideoView.release();
        }
        if (isShowSharePage())
        {
            mSharePage.onClose();
        }

        mBrightObserver = null;
        mVideoBgmPage = null;
        mSelectMusicPage = null;
        mVideoView = null;
        mVideoCallback = null;
        mOnAnimationClickListener = null;
        mClipMusicCallback = null;
        mSelectMusicCallback = null;
        System.gc();

        MyBeautyStat.onPageEndByRes(R.string.拍照_视频预览页_主页面);
        super.onClose();
    }

    @Override
    public void onPageResult(int siteID, HashMap<String, Object> params)
    {
        if (isShowSharePage())
        {
            mSharePage.onPageResult(siteID, params);
        }
        super.onPageResult(siteID, params);
    }

    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (isShowSharePage())
        {
            return mSharePage.onActivityResult(requestCode, resultCode, data);
        }
        return super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    protected void onWindowVisibilityChanged(int visibility)
    {
        super.onWindowVisibilityChanged(visibility);
        changeSystemUiVisibility(visibility == View.GONE ?
                View.VISIBLE : View.GONE);
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus)
    {
        super.onWindowFocusChanged(hasWindowFocus);
        if (hasWindowFocus)
        {
            postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    resetSystemUiVisibility();
                    changeSystemUiVisibility(View.GONE);
                }
            }, 100);
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

    private HashMap<String, Object> getVideoData()
    {
        HashMap<String, Object> params = null;
        if (!isSaveVideo && mVideoMgr != null)
        {
            params = new HashMap<>();
            params.put(CameraSetDataKey.KEY_IS_RESUME_VIDEO_PAUSE, true);
            params.put(CameraSetDataKey.KEY_RESUME_VIDEO_PAUSE_MGR, mVideoMgr);
        }
        else
        {
            if (mVideoMgr != null) mVideoMgr.clearAll();
        }
        if (params == null)
        {
            params = new HashMap<>();
        }
        params.put(CameraSetDataKey.KEY_IS_SHOW_STICKER_SELECTOR, false);
        mVideoMgr = null;
        return params;
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

    private ClipMusicView.OnCallBack mClipMusicCallback = new ClipMusicView.OnCallBack()
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
                if (mRecordBtn != null && needUpdate)
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

    private SelectMusicPage.OnCallBack mSelectMusicCallback = new SelectMusicPage.OnCallBack()
    {
        @Override
        public void onClickBack()
        {
            //展开bgm列表
            if (mVideoBgmPage != null && mVideoBgmPage.isFold())
            {
                if (mOnAnimationClickListener != null)
                {
                    mOnAnimationClickListener.onAnimationClick(mMusicBtn);
                }
            }

            if (!mSelectMusicPage.isFold())
            {
                setSelectMusicFrFold(true);
            }
            resumeBgmAndVideo();
        }

        @Override
        public void onClick(final AudioStore.AudioInfo audioInfo)
        {
            if (audioInfo == null) return;
            //Log.d(TAG, "TestPage --> onClick: " + audioInfo.toString());

            //展开裁剪view，收缩本地音乐列表
            if (((audioInfo.getDuration() / 1000f) < 1.1f))
            {
                showToast(R.string.lightapp06_video_bgm_short_audio_tip);
                return;
            }
            dismissToast();

            initBgmView();
            if (mVideoBgmPage.isFold())
            {
                setBgmFrFold(false);
            }
            setSelectMusicFrFold(true);

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

            postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    mVideoBgmPage.setMusicPath(audioInfo.getPath());
                }
            }, 300);
        }
    };

    private void insertBgmLocalInfo(AudioStore.AudioInfo audioInfo)
    {
        if (mVideoBgmPage != null)
        {
            mVideoBgmPage.insertBgmLocalInfo(audioInfo);
        }
    }

    private void updateBgmLocalInfo(AudioStore.AudioInfo audioInfo, int info_id)
    {
        if (mVideoBgmPage != null)
        {
            mVideoBgmPage.updateAdapterInfo(audioInfo, info_id);
        }
    }

    private VideoBgmPage.Callback mBgmCallback = new VideoBgmPage.Callback()
    {
        @Override
        public void onFold(boolean isFold)
        {
            if (isFold) //收
            {
                if (mOnAnimationClickListener != null)
                {
                    mOnAnimationClickListener.onAnimationClick(mMusicBtn);
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
            if (mSelectMusicPage.isFold())
            {
                setSelectMusicFrFold(false);
            }
            pauseBgmAndVideo();
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
            clipInfo.videoTime = Math.round(mDuration / 1000f);
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

    private VideoView.OnVideoViewPlayCallback mVideoCallback = new VideoView.OnVideoViewPlayCallback()
    {
        @Override
        public void onReady()
        {
            //开始获取高斯模糊
            if (mGlassBmp == null || mPreIconBmp == null)
            {
                new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        mPreIconBmp = getBackGroundThumb(mFirstVideoPath);
                        if (mPreIconBmp != null && !mPreIconBmp.isRecycled())
                        {
                            mGlassBmp = filter.fakeGlassBeauty(mPreIconBmp.copy(Bitmap.Config.ARGB_8888, true), 0x19000000);
                        }
                    }
                }).start();
            }
        }

        @Override
        public void onPlayerError(ExoPlaybackException error, String errorMsg)
        {
            //TODO 出错跳出
            if (SysConfig.IsDebug())
            {
                new AlertDialog.Builder(getContext())
                        .setCancelable(true)
                        .setTitle(R.string.tips)
                        .setMessage("player error " + (errorMsg != null ? errorMsg : ""))
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                dialog.dismiss();
                                onBack();
                            }
                        }).create().show();
            }
        }

        @Override
        public void onVideoPlayCompleted()
        {
            if (isMixing || isPause) return;

            if (mVideoView != null)
            {
                mVideoView.reset();
                mVideoView.start();
            }

            if (mVideoBgmPage != null)
            {
                mVideoBgmPage.seekTo((int) mStartBgmTime);
            }
        }

        @Override
        public void onVideoSeekTo(long millSecond)
        {
            if (isPause || isMixing)
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

        long offsetDuration = 0;
        @Override
        public void onVideoPlayPosition(long duration, long position)
        {
            // setVideoDuration(duration);
            if (Math.abs(offsetDuration - duration) > 500L) {
                duration = mDuration;
            } else {
                setVideoDuration(duration);
            }
            offsetDuration = duration;

            if (isSupportFadeVolume)
            {
                float progress = position * 1f / duration * 100f;
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

        /**
         * 背景图
         *
         * @param mp4Path
         * @return
         */
        private Bitmap getBackGroundThumb(String mp4Path)
        {
            Bitmap out = null;
            if (FileUtil.isFileExists(mp4Path))
            {
                try
                {
                    out = NativeUtils.getNextFrameBitmapFromFile(mp4Path, 0);
                }
                catch (Throwable e)
                {
                    e.printStackTrace();
                }
                finally
                {
                    NativeUtils.cleanVideoGroupByIndex(0);
                }
                if (out == null)
                {
                    out = FileUtil.getLocalVideoThumbnail(mp4Path, true);
                }

                if (mOrientation > 0 && out != null && !out.isRecycled())
                {
                    Matrix matrix = new Matrix();
                    matrix.postRotate(mOrientation);
                    Bitmap temp = Bitmap.createBitmap(out, 0, 0, out.getWidth(), out.getHeight(), matrix, true);
                    if (temp != out) out.recycle();
                    out = temp;
                    temp = null;
                }
            }

            if (out == null && mVideoView != null)
            {
                out = mVideoView.getBitmap();
                if (out != null)
                {
                    out = MakeBmp.CreateBitmap(out, ShareData.m_screenWidth, ShareData.m_screenHeight, -1, 0, Bitmap.Config.ARGB_8888);
                }
            }
            return out;
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
            dismissDialog();

            if (isMixing)
            {
                isMixing = false;
                mixVideoSuccess();
            }
        }
    };

    private OnAnimationClickListener mOnAnimationClickListener = new OnAnimationClickListener()
    {
        @Override
        public void onAnimationClick(View v)
        {
            if (v == mSaveBtn)
            {
                MyBeautyStat.onClickByRes(R.string.拍照_视频预览页_主页面_保存);
                keepScreenWakeUp(true);
                onPause();
                if (isMixing) return;
                mixVideo();
            }
            else if (v == mBackBtn)
            {
                MyBeautyStat.onClickByRes(R.string.拍照_视频预览页_主页面_返回);
                TongJi2.AddCountByRes(getContext(), R.integer.拍照_动态贴纸_预览_返回);
                onBack();
            }
            else if (v == mMusicBtn)
            {
                if (isAnimation) return;

                initBgmView();
                boolean fold = mVideoBgmPage.isFold();
                mVideoView.doAnim(fold);
                setBgmFrFold(!fold);
                MyBeautyStat.onClickByRes(R.string.拍照_视频预览页_主页面_音乐);
                TongJi2.AddCountByRes(getContext(), R.integer.拍照_动态贴纸_预览页_背景音乐按钮);
            }
            else if (v == mRecordBtn)
            {
                boolean isRecordVoiceEnable = BeautyVideoPage.this.isRecordAudioEnable;
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
        if (mRecordBtn != null)
        {
            mRecordBtn.icon.setImageBitmap(ImageUtils.AddSkin(getContext(),
                    BitmapFactory.decodeResource(getResources(), isVideoMute ? R.drawable.light_app06_video_sound_off : R.drawable.light_app06_video_sound_on)));
        }
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

    /**
     * 构造混音 开始混音
     */
    private void mixVideo()
    {
        ProcessInfo info = new ProcessInfo();
        info.m_bg_music_start = mStartBgmTime;                                              //音乐起始时间
        info.m_bg_music_path = mVideoBgmPage != null ? mVideoBgmPage.getMusicPath() : null; //音乐路径
        info.m_bg_volume_adjust = mMusicValue / 100f;                                       //音量调节
        info.m_video_volume_adjust = mRecordValue / 100f;
        info.is_clear_temp_cache = true;                //是否清楚缓存
        if (mVideoView != null && mVideoView.getVideoPath() != null)         //分段视频路径
        {
            String[] videos = new String[mVideoView.getVideoPath().size()];
            int index = 0;
            for (String s : mVideoView.getVideoPath()) {
                videos[index] = s;
                index++;
            }
            info.m_video_paths = videos;
        }
        info.m_video_rotation = mOrientation;
        info.is_silence_play = isVideoMute;             //视频录音是否静音
        info.is_clear_temp_cache = true;               //是否清理缓存
        mSavePath = FileUtils.getVideoOutputSysPath();
        VideoMixProcessorV2 processor = new VideoMixProcessorV2(info, mSavePath);
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
            showDialog(false);

            initSharePage();
        }

        @Override
        public void onProgress(long timeStamp)
        {
            //nothing to do
        }

        @Override
        public void onFinish(VideoMixProcessorV2.MixOutInfo mixOutInfo)
        {
            mSavePath = mixOutInfo.mPath;
            mSaveDuration = mixOutInfo.mDuration;
            isSaveVideo = true;

            if (isMixing)
            {
                //结束进度旋转，目的是想让进度走完，最终回调dialog的监听
                showDialog(true);
            }
            else
            {
                isMixing = false;
                dismissDialog();
                mixVideoSuccess();
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
                showDialog(true);
            }
            else
            {
                isMixing = false;
                dismissDialog();
            }
        }
    };

    private void initSharePage()
    {
        closeSharePage();
        if (isThirdParty || isSaveJump) return;
        mSharePage = new BeautyVideoSharePage(getContext(), mSite, true);
        mSharePage.setAnimationCallback(new BeautyVideoSharePage.AnimationCallback()
        {
            @Override
            public void onAnimationEnd(boolean openShare)
            {
                if (!openShare)
                {
                    closeSharePage();
                    onResume();
                    changeSystemUiVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationStart(boolean openShare)
            {
                if (!openShare)
                {
                    showVideoView(true);
                }
            }
        });
        mSharePage.setVideoBackground(mPreIconBmp);
        mSharePage.setVisibility(GONE);

        FrameLayout.LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        lp.gravity = Gravity.CENTER;
        this.addView(mSharePage, lp);
    }


    private void showSharePage()
    {
        // 打开分享页
        if (mSharePage == null) initSharePage();
        mSharePage.setVisibility(VISIBLE);
        HashMap<String, Object> params = new HashMap<>();
        params.put("type", 1);
        params.put("path", mSavePath);
        params.put("video_duration", (long) mSaveDuration);
        //params.put("channelValue", mChannelValue);
        params.put("res_id", mResId);
        params.put("res_tj_id", mResTjId);
        mSharePage.SetData(params);
    }

    private void closeSharePage()
    {
        if (mSharePage != null)
        {
            mSharePage.setAnimationCallback(null);
            mSharePage.onClose();
            this.removeView(mSharePage);
            mSharePage = null;
        }
    }

    private void showVideoView(boolean show)
    {
        if (mVideoView != null) mVideoView.setVisibility(show ? VISIBLE : GONE);
    }

    private void mixVideoSuccess()
    {
        File file = new File(mSavePath);
        if (file.exists())
        {
            getContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
        }

        if (isThirdParty)
        {
            mSite.onThirdPartySave(getContext(), mSavePath);
            return;
        }
        if (isSaveJump)
        {
            mSite.onSaveToCommunity(getContext(), mSavePath, BeautyVideoSharePage.makeCircleExtra(mResId, mResTjId));
            return;
        }
        if (mSite instanceof BeautyVideoPageSite500) {
            ((BeautyVideoPageSite500) mSite).onSave(getContext(), mSavePath);
            return;
        }
        showVideoView(false);
        showSharePage();
    }

    private boolean isShowSharePage()
    {
        if (mSharePage != null && mSharePage.getVisibility() == View.VISIBLE)
        {
            return true;
        }
        return false;
    }


    private void resumeBgmAndVideo()
    {
        if (mVideoView != null) mVideoView.resume();
        if (mVideoBgmPage != null) mVideoBgmPage.onResume();
    }

    private void pauseBgmAndVideo()
    {
        if (mVideoView != null) mVideoView.pause();
        if (mVideoBgmPage != null) mVideoBgmPage.onPause();
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
        mVideoView.setProgressAlpha(fold);
        mVideoBgmPage.startAnimation(anim);
    }

    private static class BtnFr extends FrameLayout
    {
        ImageView icon;
        TextView title;
        int titleW;
        int defW;

        public BtnFr(@NonNull Context context,
                     @DrawableRes int resId,
                     @StringRes int txtId,
                     OnAnimationClickListener listener,
                     boolean sysColor)
        {
            super(context);
            setOnTouchListener(listener);
            icon = new ImageView(context);
            icon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            icon.setImageResource(resId);
            if (sysColor)
            {
                cn.poco.advanced.ImageUtils.AddSkin(context, icon);
            }
            LayoutParams params = new LayoutParams(defW = CameraPercentUtil.WidthPxToPercent(70), CameraPercentUtil.HeightPxToPercent(70));
            params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
            this.addView(icon, params);

            title = new TextView(context);
            title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 11);
            title.setTextColor(sysColor ? ImageUtils.GetSkinColor() : 0xff999999);
            title.setGravity(Gravity.CENTER);
            title.setText(txtId);
            params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
            params.topMargin = CameraPercentUtil.HeightPxToPercent(68);
            titleW = (int) title.getPaint().measureText(getResources().getString(txtId));
            this.addView(title, params);
        }

        int hasOffset()
        {
            return (titleW - defW) / 2;
        }
    }

    private class BrightObserver extends ContentObserver
    {
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
}
