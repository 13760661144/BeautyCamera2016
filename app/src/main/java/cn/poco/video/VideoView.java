package cn.poco.video;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.FloatRange;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.google.android.exoplayer2.ExoPlaybackException;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;

import cn.poco.advanced.ImageUtils;
import cn.poco.camera3.util.CameraPercentUtil;
import cn.poco.tianutils.ShareData;
import cn.poco.video.view.AspectRatioFrameLayout;
import my.beautyCamera.R;

/**
 * @author lmx
 *         Created by lmx on 2017/8/23.
 */

public class VideoView extends FrameLayout
{
    private static final String TAG = "bbb";

    private long mDuration;
    private boolean isAdaptation = true;//是否横竖屏视频适配
    private boolean is16_9FullScreen = false; //16:9是否全屏

    @IntDef({State.IDLE, State.START, State.PAUSE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface State
    {
        int IDLE = 1;
        int START = 1 << 1;
        int PAUSE = 1 << 2;
    }

    @State
    protected int mState;

    protected boolean isVideoMute = false;//视频静音
    protected boolean mError = false;
    protected boolean isTouchTracking = false;
    private boolean isRealse = false;

    protected AspectRatioFrameLayout mAspectRatioLayout;
    protected VideoProgressView mProgressView;
    protected TextureView mTextureView;
    protected ProgressBar mProgressBar;

    protected VideoPlayer mVideoPlayer;

    protected ArrayList<String> mVideoPaths;

    protected OnVideoViewPlayCallback mCallback;

    protected int mParentDefHeight;
    protected int mParentShrinkHeight;
    private boolean isInitPage;

    //镜头视频旋转角度
    protected int mOrientation;

    //镜头视频预览宽高
    protected int mVideoPreviewWidth;
    protected int mVideoPreviewHeight;

    protected int mDefVideoViewWidth;
    protected int mDefVideoViewHeight;
    protected int mVideoViewWidth;
    protected int mVideoViewHeight;
    protected int mTextureHeight;

    private boolean mAccordantHeight; // 是否等高
    private boolean mAccordantWidth; // 是否等宽

    public interface OnVideoViewPlayCallback
    {
        /**
         * 视频准备好
         */
        void onReady();

        /**
         * 播放出错
         */
        void onPlayerError(ExoPlaybackException error, String errrorMsg);

        /**
         * 完成播放
         */
        void onVideoPlayCompleted();

        /**
         * 视频跳转
         *
         * @param millSecond 单位 毫秒
         */
        void onVideoSeekTo(long millSecond);

        /**
         * 触发视频进度条
         */
        void onVideoProgressStartTouch();

        /**
         * 视频播放进度
         *
         * @param duration 视频时长
         * @param position 当前播放进度
         */
        void onVideoPlayPosition(long duration, long position);

        /**
         * 检查是否pause中
         *
         * @return
         */
        boolean isPause();
    }

    public VideoView(@NonNull Context context)
    {
        super(context);
        initData();
        init();
        initView();
        isInitPage = true;
    }

    @Override
    public boolean performClick()
    {
        return super.performClick();
    }

    private void initData()
    {
        mParentDefHeight = ShareData.m_screenRealHeight - CameraPercentUtil.HeightPxToPercent(166) + CameraPercentUtil.HeightPxToPercent(14);
        mParentShrinkHeight = ShareData.m_screenRealHeight - CameraPercentUtil.HeightPxToPercent(320) + CameraPercentUtil.HeightPxToPercent(14);

        mDefVideoViewWidth = ShareData.m_screenRealWidth;
        mDefVideoViewHeight = mParentDefHeight;

        mVideoViewWidth = ShareData.m_screenRealWidth;
        mVideoViewHeight = ShareData.m_screenRealHeight - CameraPercentUtil.HeightPxToPercent(166);
        mTextureHeight = ShareData.m_screenRealHeight - CameraPercentUtil.HeightPxToPercent(166);
    }

    public void setOrientation(int mOrientation)
    {
        this.mOrientation = mOrientation;
    }

    public void setVideoPreviewWidth(int mVideoPreviewWidth)
    {
        this.mVideoPreviewWidth = mVideoPreviewWidth;
    }

    public void setVideoPreviewHeight(int mVideoPreviewHeight)
    {
        this.mVideoPreviewHeight = mVideoPreviewHeight;
    }

    /**
     * 是否适配view
     *
     * @param adaptation
     */
    public void setAdaptation(boolean adaptation)
    {
        this.isAdaptation = adaptation;
    }

    /**
     * 16:9视频下全屏
     *
     * @param is16_9FullScreen
     */
    public void setIs16_9FullScreen(boolean is16_9FullScreen)
    {
        this.is16_9FullScreen = is16_9FullScreen;
    }

    private void init()
    {
        mVideoPlayer = new VideoPlayer(getContext());
        mVideoPlayer.setVideoPlayerListener(mVideoPlayerListener);
    }

    private void initView()
    {
        mAspectRatioLayout = new AspectRatioFrameLayout(getContext());
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.gravity = Gravity.CENTER;
        addView(mAspectRatioLayout, params);

        mTextureView = new TextureView(getContext());
        mTextureView.setFocusable(true);
        mTextureView.setFocusableInTouchMode(true);
        mTextureView.requestFocus();
        params = new LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.gravity = Gravity.CENTER_HORIZONTAL;
        mAspectRatioLayout.addView(mTextureView, params);
        mVideoPlayer.setVideoTextureView(mTextureView);

        mProgressView = new VideoProgressView(getContext());
        mProgressView.setVisibility(INVISIBLE);
        mProgressView.setProgressBarHeight(CameraPercentUtil.HeightPxToPercent(6));
        mProgressView.setProgressColor(ImageUtils.GetSkinColor());
        mProgressView.setOnProgressChangeListener(new VideoProgressView.OnProgressChangeListener()
        {
            @Override
            public void onStartTouch(long pos)
            {
                isTouchTracking = true;
                pause();
                if (mCallback != null)
                {
                    mCallback.onVideoProgressStartTouch();
                }
            }

            @Override
            public void onProgressChanged(long pos)
            {
                isTouchTracking = true;
            }

            @Override
            public void onStopTouch(long pos)
            {
                isTouchTracking = false;
                boolean isPause = false;
                if (mCallback != null && mCallback.isPause())
                {
                    isPause = true;
                }

                if (isPause)
                {
                    return;
                }

                seekTo(pos);

                if (mCallback != null)
                {
                    mCallback.onVideoSeekTo(pos);
                }
            }
        });
        params = new LayoutParams(LayoutParams.MATCH_PARENT, CameraPercentUtil.HeightPxToPercent(190));
        params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
        mAspectRatioLayout.addView(mProgressView, params);

        mProgressBar = new ProgressBar(getContext(), null, android.R.attr.progressBarStyle);
        mProgressBar.setVisibility(View.GONE);
        params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        mAspectRatioLayout.addView(mProgressBar, params);
    }


    public void showLoadViewState(int visibility)
    {
        if (mProgressBar != null)
        {
            mProgressBar.setVisibility(visibility);
        }
    }

    public void setProgressViewShow(boolean show)
    {
        if (mProgressView != null)
        {
            mProgressView.setProgressShow(show);
        }
    }

    public void setProgressAlpha(boolean alpha)
    {
        if (mProgressView != null)
        {
            mProgressView.setProgressAlpha(alpha);
            mProgressView.resetStatus();
        }
    }

    public void setDuration(long duration)
    {
        mDuration = duration;
        if (mProgressView != null)
        {
            mProgressView.setDuration(duration);
        }
    }

    public long getDuration()
    {
        return mDuration;
    }

    public Bitmap getBitmap()
    {
        if (mTextureView != null)
        {
            return mTextureView.getBitmap();
        }
        return null;
    }

    public void setCallback(OnVideoViewPlayCallback mCallback)
    {
        this.mCallback = mCallback;
    }

    /**
     * @param videoPath 视频路径
     */
    public void setVideoPath(ArrayList<String> videoPath)
    {
        mVideoPaths = videoPath;
        if (mVideoPlayer != null)
        {
            mState = State.IDLE;
            mVideoPlayer.setDataSource(videoPath);
        }
    }

    public ArrayList<String> getVideoPath()
    {
        return mVideoPaths;
    }

    public void reset()
    {
        if (mVideoPlayer != null)
        {
            mState = State.IDLE;
            mVideoPlayer.reset();
        }
    }

    public void resume()
    {
        if (mVideoPlayer != null)
        {
            mState = State.START;
            mVideoPlayer.resume();
        }
    }

    public void pause()
    {
        if (mVideoPlayer != null)
        {
            mState = State.PAUSE;
            mVideoPlayer.pause();
        }
        showLoadViewState(View.GONE);
    }

    public void start()
    {
        if (mVideoPlayer != null)
        {
            mState = State.START;
            mVideoPlayer.start();
        }
    }

    public boolean isPlaying()
    {
        return mVideoPlayer != null && mVideoPlayer.isPlaying();
    }

    public void seekTo(long millisecond)
    {
        if (mVideoPlayer != null)
        {
            mVideoPlayer.seekTo(millisecond);
            mVideoPlayer.start();
        }
    }

    /**
     * 预载player
     */
    public void prepared()
    {
        if (mVideoPlayer != null)
        {
            mVideoPlayer.prepare();
            setVideoMute(this.isVideoMute);
        }
    }


    public void setVolume(@FloatRange(from = 0.0f, to = 1.0f) float volume)
    {
        if (mVideoPlayer != null) mVideoPlayer.setVolume(volume);
    }

    public void setVideoMute(boolean isVideoMute)
    {
        this.isVideoMute = isVideoMute;
        setVolume(isVideoMute ? 0f : 1f);
    }

    public void release()
    {
        isRealse = true;
        if (mVideoPlayer != null) mVideoPlayer.release();
        mVideoPaths = null;
        mVideoPlayer = null;
        mVideoPlayerListener = null;
    }

    public void doAnim(boolean shrink)
    {
        int dy = mParentShrinkHeight - mParentDefHeight;

        float scale = Math.abs(mParentShrinkHeight * 1.0f / mParentDefHeight);

        scale = (shrink ? scale : (1.0f / scale));

        if (scale > 1)
        {
            scale = 1;
        }

        if (mAccordantWidth)
        {
            dy /= 2f;
            // 平移
            ObjectAnimator obj;
            if (shrink)
            {
                obj = ObjectAnimator.ofFloat(this, "translationY", 0, dy);
            }
            else
            {
                obj = ObjectAnimator.ofFloat(this, "translationY", dy, 0);
            }
            obj.setDuration(250);
            obj.start();
        }
        else if (mAccordantHeight)
        {
            this.setPivotX(mAspectRatioLayout.getMeasuredWidth() / 2);
            this.setPivotY(0);
            // 缩放 + 平移
            ObjectAnimator scaleXAnim = ObjectAnimator.ofFloat(this, "scaleX", this.getScaleX(), scale);
            ObjectAnimator scaleYAnim = ObjectAnimator.ofFloat(this, "scaleY", this.getScaleY(), scale);

            AnimatorSet set = new AnimatorSet();
            set.playTogether(scaleXAnim, scaleYAnim);
            set.setDuration(300);
            set.start();
        }
    }

    protected VideoPlayer.OnVideoPlayerListener mVideoPlayerListener = new VideoPlayer.OnVideoPlayerListener()
    {
        @Override
        public void showLoadStateView(int visibility)
        {
            showLoadViewState(visibility);
        }

        @Override
        public void onPlayerErrorIndex(int currentWindowsIndex)
        {
            if (currentWindowsIndex >= 0
                    && mVideoPaths.size() > 0
                    && mVideoPaths.size() > currentWindowsIndex) {
                mVideoPaths.remove(currentWindowsIndex);
            }
        }

        @Override
        public void position(long duration, long position)
        {
            //播放进度
            if (isTouchTracking || isRealse || duration <= 0 || mError) return;

            //setDuration(duration);
            // 备注：这里显示的时间长跟实际录制的时长不对等，实际上，快速录制会比计时器的时长要长
            // 如果使用计时器的时长会导致进度条超屏幕的情况
            // duration = mDuration;

            if (mProgressView != null)
            {
                mProgressView.setDuration(duration);
                mProgressView.setProgress(position * 1f / duration * 100f);
            }
            if (mCallback != null)
            {
                mCallback.onVideoPlayPosition(duration, position);
            }
        }

        @Override
        public void onRenderedFirstFrame()
        {
            if (mProgressView != null && mProgressView.getVisibility() != View.VISIBLE)
            {
                mProgressView.setVisibility(VISIBLE);
            }
        }

        @Override
        public void onReady()
        {
            if (mCallback != null)
            {
                mCallback.onReady();
            }
        }

        @Override
        public void onPlayerError(ExoPlaybackException error, String errorMsg)
        {
            mError = true;
            mState = State.IDLE;

            showLoadViewState(View.GONE);
            if (mCallback != null) mCallback.onPlayerError(error, errorMsg);
        }

        @Override
        public void onPreParedError()
        {
            showLoadViewState(View.GONE);
            if (mCallback != null)
            {
                mCallback.onPlayerError(null, getContext().getString(R.string.camerapage_invalid_video));
            }
        }

        @Override
        public void onVideoFinish()
        {
            showLoadViewState(View.GONE);
            if (mCallback != null)
            {
                mCallback.onVideoPlayCompleted();
            }
        }

        @Override
        public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio)
        {
            if (isInitPage)
            {
                isInitPage = false;
                if (isAdaptation)
                {
                    if (mAspectRatioLayout != null && mTextureView != null)
                    {
                        float videoRatio = height * 1f / width;
                        //if (videoRatio == 16.0f / 9.0f || videoRatio == 960.0f / 544)//360 F4
                        if (videoRatio >= 1.7f)//360 F4
                        {
                            if (is16_9FullScreen) return;

                            // 等高处理
                            mAccordantHeight = true;
                            mVideoViewWidth = (int) (mVideoViewHeight / videoRatio);
                            mTextureHeight = mVideoViewHeight;
                            mVideoViewHeight = mDefVideoViewHeight;
                        }
                        else /*if (videoRatio == 1.0f || videoRatio == 4.0f / 3.0f || videoRatio == 3.0f / 4.0f || videoRatio == 9.0f / 16.0f || videoRatio == 544.0f / 960 || videoRatio == 400.0f / 720)*/
                        {
                            // 等宽处理
                            mAccordantWidth = true;
                            mVideoViewHeight = (int) (mVideoViewWidth * videoRatio) + CameraPercentUtil.HeightPxToPercent(14);
                            mTextureHeight = (int) (mVideoViewWidth * videoRatio);
                            mVideoViewWidth = mDefVideoViewWidth;
                        }

                        FrameLayout.LayoutParams params = (LayoutParams) mTextureView.getLayoutParams();
                        params.width = mVideoViewWidth;
                        params.height = mTextureHeight;

                        params = (LayoutParams) mAspectRatioLayout.getLayoutParams();
                        params.width = mVideoViewWidth;
                        if (mAccordantHeight)
                        {
                            //进度圆圈直径
                            params.width += CameraPercentUtil.WidthPxToPercent(32);
                            mProgressView.setIsTransXToDrawCircle(true);
                        }
                        params.height = mVideoViewHeight;
                        mAspectRatioLayout.requestLayout();
                    }
                }
            }
        }
    };
}
