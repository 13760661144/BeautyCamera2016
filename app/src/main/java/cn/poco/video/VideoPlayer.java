package cn.poco.video;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.FloatRange;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.mediacodec.MediaCodecRenderer;
import com.google.android.exoplayer2.mediacodec.MediaCodecUtil;
import com.google.android.exoplayer2.source.DynamicConcatenatingMediaSource;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.UnrecognizedInputFormatException;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.io.EOFException;
import java.io.FileNotFoundException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;

import static cn.poco.video.VideoPlayer.State.PREPARED;

/**
 * @author lmx
 *         Created by lmx on 2017/8/22.
 */

public class VideoPlayer implements IPlayer
{
    private static final String TAG = "bbb";

    @IntDef({State.UNSET, State.PREPARED, State.START, State.PAUSE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface State
    {
        int UNSET = -1;
        int PREPARED = 1;
        int START = 2;
        int PAUSE = 3;
    }

    @VideoPlayer.State
    private int mState = State.UNSET;

    private Handler mHandler;
    private Context mContext;

    private String mUserAgent;
    private SimpleExoPlayer mSimpleExoPlayer;
    private MediaSource mConcatenatingMediaSource;
    private Timeline.Window mWindow;

    private SurfaceView mSurfaceView;
    private TextureView mTextureView;

    private ComponentListener mComponentListener;
    private OnVideoPlayerListener mListener;

    private boolean isInitDuration;
    private int mResumeWindow;
    private long mResumePosition;
    private long mDuration = C.TIME_UNSET;

    private int mErrorCount;

    private Runnable mUpdatePositionRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            updatePosition();
        }
    };

    public VideoPlayer(@NonNull Context mContext)
    {
        this.mContext = mContext;
        initData();
    }

    private void initData()
    {
        clearResumePosition();
        mComponentListener = new ComponentListener();
        mHandler = new Handler(Looper.getMainLooper());
        mUserAgent = Util.getUserAgent(this.mContext, this.mContext.getApplicationContext().getPackageName());

        mWindow = new Timeline.Window();
        mSimpleExoPlayer = ExoPlayerFactory.newSimpleInstance(this.mContext, new DefaultTrackSelector());
        mSimpleExoPlayer.setRepeatMode(Player.REPEAT_MODE_OFF);
        mSimpleExoPlayer.addVideoListener(mComponentListener);
        mSimpleExoPlayer.addListener(mComponentListener);
    }

    public void setVideoPlayerListener(OnVideoPlayerListener mListener)
    {
        this.mListener = mListener;
    }

    @Override
    public void setSurfaceView(SurfaceView surfaceView)
    {
        if (surfaceView == null) return;

        this.mSurfaceView = surfaceView;

        if (mSimpleExoPlayer != null)
        {
            mSimpleExoPlayer.setVideoSurfaceView(surfaceView);
        }
    }

    @Override
    public void setVideoTextureView(TextureView textureView)
    {
        if (textureView == null) return;

        this.mTextureView = textureView;

        if (mSimpleExoPlayer != null)
        {
            mSimpleExoPlayer.setVideoTextureView(textureView);
        }
    }

    @Override
    public void setDataSource(ArrayList<String> dataSource)
    {
        if (dataSource != null && dataSource.size() > 0)
        {
            clearConcatenatingMediaSource();

            ArrayList<MediaSource> tempExtractorMediaSources = new ArrayList<>();

            for (String path : dataSource)
            {
                if (!FileUtils.isFileValid(path))
                {
                    continue;
                }

                DataSource.Factory dataFactory = new DefaultDataSourceFactory(this.mContext, this.mUserAgent);
                Uri parse = Uri.parse(path);
                ExtractorMediaSource mediaSource = new ExtractorMediaSource.Factory(dataFactory)
                        .setExtractorsFactory(new DefaultExtractorsFactory())
                        .setMinLoadableRetryCount(5)
                        .setCustomCacheKey(parse.getPath())
                        .createMediaSource(parse);
                tempExtractorMediaSources.add(mediaSource);
            }

            if (tempExtractorMediaSources.size() > 0)
            {
                // MediaSource[] mediaSources = new MediaSource[tempExtractorMediaSources.size()];
                // for (int i = 0, size = tempExtractorMediaSources.size(); i < size; i++)
                // {
                //     mediaSources[i] = tempExtractorMediaSources.get(i);
                // }
                // mMediaSource = new ConcatenatingMediaSource(mediaSources);

                DynamicConcatenatingMediaSource dynamicConcatenatingMediaSource= new DynamicConcatenatingMediaSource();
                for (MediaSource tempExtractorMediaSource : tempExtractorMediaSources) {
                    dynamicConcatenatingMediaSource.addMediaSource(tempExtractorMediaSource);
                }
                mConcatenatingMediaSource = dynamicConcatenatingMediaSource;
            }
            else
            {
                if (mListener != null)
                {
                    mListener.onPreParedError();
                }
            }

            mState = VideoPlayer.State.PREPARED;
        }
    }

    private void updateResumePosition()
    {
        mResumeWindow = mSimpleExoPlayer.getCurrentWindowIndex();
        mResumePosition = Math.max(0, mSimpleExoPlayer.getContentPosition());
    }


    private void clearResumePosition()
    {
        mResumeWindow = C.INDEX_UNSET;
        mResumePosition = C.TIME_UNSET;
    }

    // 记录上一次位置，用于做进度条平滑处理
    private volatile long mLastPosition = 0;
    private void updatePosition()
    {
        if (mState == State.UNSET)
        {
            return;
        }

        long position = 0;
        long duration = 0;
        if (mSimpleExoPlayer != null)
        {
            long currentWindowTimeBarOffsetUs = 0;
            long durationUs = 0;

            Timeline timeline = mSimpleExoPlayer.getCurrentTimeline();
            if (!timeline.isEmpty())
            {
                int currentWindowIndex = mSimpleExoPlayer.getCurrentWindowIndex();
                int firstWindowIndex = 0;
                int lastWindowIndex = timeline.getWindowCount() - 1;
                for (int i = firstWindowIndex; i <= lastWindowIndex; i++)
                {
                    if (i == currentWindowIndex)
                    {
                        currentWindowTimeBarOffsetUs = durationUs;
                    }
                    timeline.getWindow(i, mWindow);
                    if (mWindow.durationUs == C.TIME_UNSET) {
                        break;
                    }
                    durationUs += mWindow.durationUs;
                }
            }

            mDuration = duration = C.usToMs(durationUs);
            position = C.usToMs(currentWindowTimeBarOffsetUs);
            position += mSimpleExoPlayer.getCurrentPosition();
            // // 处于切换视频段时，由于MediaCode时间戳并不准确，getCurrentPosition有可能会多次返回相同的值
            // if (position - mLastPosition >= 0 && position - mLastPosition < 15) {
            //     if (!isSeekTo)
            //     {
            //         mLastPosition = mLastPosition + 20;
            //         position = mLastPosition;
            //     }
            //     else
            //     {
            //         mLastPosition = position;
            //     }
            //     if (position > mDuration) {
            //         position = mDuration;
            //     }
            // }
            // // 在多次返回相同值之后，Position的位置比记录的时长有可能超过80毫秒，甚至更长
            // // 这里需要加快进度条的进度，保证进度条平滑过渡
            // else if (position - mLastPosition > 80) {
            //     if (!isSeekTo)
            //     {
            //         mLastPosition = mLastPosition + (position - mLastPosition) % 40;
            //         position = mLastPosition;
            //     }
            //     else
            //     {
            //          mLastPosition = position;
            //     }
            //     if (position > mDuration) {
            //         position = mDuration;
            //     }
            // } else {
            //     mLastPosition = position;
            // }

            if (mListener != null)
            {
                mListener.position(duration, position);
            }

            isSeekTo = false;
            // Cancel any pending updates and schedule a new one if necessary.
            if (mHandler != null) mHandler.removeCallbacks(mUpdatePositionRunnable);
            int playbackState = mSimpleExoPlayer == null ? Player.STATE_IDLE : mSimpleExoPlayer.getPlaybackState();
            if (playbackState != Player.STATE_IDLE && playbackState != Player.STATE_ENDED)
            {
                long delayMs = 50;
                if (mSimpleExoPlayer.getPlayWhenReady() && playbackState == Player.STATE_READY) {
                    delayMs = 70 - (position % 20);
                    if (delayMs < 60) {
                        delayMs += 10;
                    }
                }
                if (mHandler != null) mHandler.postDelayed(mUpdatePositionRunnable, delayMs);
            }
        }
    }

    /**
     * 清除media数据
     */
    private void clearConcatenatingMediaSource()
    {
        if (mConcatenatingMediaSource != null)
        {
            mConcatenatingMediaSource.releaseSource();
            mConcatenatingMediaSource = null;
        }
    }

    public boolean isPlaying()
    {
        return mSimpleExoPlayer.getPlayWhenReady();
    }

    @Override
    public void setVolume(@FloatRange(from = 0.0f, to = 1.0f) float volume)
    {
        if (mSimpleExoPlayer != null)
        {
            mSimpleExoPlayer.setVolume(volume);
        }
    }


    @Override
    public void prepare()
    {
        if (mConcatenatingMediaSource != null)
        {
            mState = VideoPlayer.State.PREPARED;
            mSimpleExoPlayer.prepare(mConcatenatingMediaSource);
        }

    }

    @Override
    public void start()
    {
        if (mState == VideoPlayer.State.PREPARED || mState == VideoPlayer.State.PAUSE)
        {
            clearResumePosition();
            mState = VideoPlayer.State.START;
            mSimpleExoPlayer.setPlayWhenReady(true);
        }
    }

    @Override
    public void pause()
    {
        if (mState == VideoPlayer.State.START)
        {
            updateResumePosition();

            mState = VideoPlayer.State.PAUSE;
            mSimpleExoPlayer.setPlayWhenReady(false);
            mHandler.removeCallbacks(mUpdatePositionRunnable);
        }
    }

    @Override
    public void resume()
    {
        if (mState == PREPARED || mState == State.PAUSE)
        {
            boolean haveResumePosition = mResumeWindow != C.INDEX_UNSET;
            if (haveResumePosition)
            {
                mSimpleExoPlayer.seekTo(mResumeWindow, mResumePosition);
            }
            clearResumePosition();
            mState = VideoPlayer.State.START;
            mSimpleExoPlayer.setPlayWhenReady(true);
            updatePosition();
        }
    }

    // 手动跳转
    private boolean isSeekTo;
    @Override
    public void seekTo(long millisecond)
    {
        if (mConcatenatingMediaSource == null || mSimpleExoPlayer == null) return;
        int windowIndex = 0;
        if (millisecond == 0)
        {
            mSimpleExoPlayer.seekTo(windowIndex, millisecond);
            return;
        }

        Timeline timeline = mSimpleExoPlayer.getCurrentTimeline();
        if (!timeline.isEmpty())
        {
            int windowCount = timeline.getWindowCount();    //视频分段个数
            while (true)
            {
                long windowDurationMs = timeline.getWindow(windowIndex, mWindow).getDurationMs();
                if (millisecond < windowDurationMs)
                {
                    break;
                }
                else if (windowIndex == windowCount - 1)
                {
                    // Seeking past the end of the last window should seek to the end of the timeline.
                    millisecond = windowDurationMs;
                    break;
                }
                millisecond -= windowDurationMs;
                windowIndex++;
            }
        }
        else
        {
            windowIndex = mSimpleExoPlayer.getCurrentWindowIndex();
        }
        isSeekTo = true;
//        Log.d(TAG, "VideoPlayer --> seekTo: windowIndex:" + windowIndex + " mill:" + millisecond);
        mSimpleExoPlayer.seekTo(windowIndex, millisecond);
    }

    @Override
    public long getCurrentPosition()
    {
        return mSimpleExoPlayer != null ? mSimpleExoPlayer.getCurrentPosition() : 0;
    }

    @Override
    public long getDurations()
    {
        return mDuration;
    }

    @Override
    public void reset()
    {
        mState = PREPARED;
        if (mSimpleExoPlayer != null)
        {
            clearResumePosition();
            mSimpleExoPlayer.seekTo(0, 0);
        }
    }

    @Override
    public void release()
    {
        mSimpleExoPlayer.stop();
        mSimpleExoPlayer.removeListener(mComponentListener);
        mSimpleExoPlayer.removeVideoListener(mComponentListener);
        mSimpleExoPlayer.clearVideoSurfaceView(mSurfaceView);
        mSimpleExoPlayer.clearVideoTextureView(mTextureView);
        mSimpleExoPlayer.removeMetadataOutput(null);
        mSimpleExoPlayer.removeVideoDebugListener(null);
        mSimpleExoPlayer.release();

        clearConcatenatingMediaSource();

        mHandler.removeCallbacks(mUpdatePositionRunnable);
        mHandler.removeCallbacksAndMessages(mContext);
        mUserAgent = null;
        mSimpleExoPlayer = null;
        mConcatenatingMediaSource = null;
        mUpdatePositionRunnable = null;
        mSurfaceView = null;
        mTextureView = null;
        mHandler = null;
    }

    private final class ComponentListener implements SimpleExoPlayer.VideoListener, Player.EventListener
    {
        @Override
        public void onTimelineChanged(Timeline timeline, Object manifest)
        {
            updatePosition();
//            Log.d(TAG, "ComponentListener --> onTimelineChanged: ");
        }

        @Override
        public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections)
        {
//            Log.d(TAG, "ComponentListener --> onTracksChanged: ");
        }

        @Override
        public void onRepeatModeChanged(int repeatMode)
        {

        }

        @Override
        public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled)
        {

        }

        @Override
        public void onLoadingChanged(boolean isLoading)
        {

        }


        @Override
        public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio)
        {
            //Log.d(TAG, "ComponentListener --> onVideoSizeChanged: ");
            if (mListener != null)
            {
                mListener.onVideoSizeChanged(width, height, unappliedRotationDegrees, pixelWidthHeightRatio);
            }
        }

        @Override
        public void onRenderedFirstFrame()
        {
            //Log.d(TAG, "ComponentListener --> onRenderedFirstFrame: ");
            if (mListener != null) mListener.onRenderedFirstFrame();
        }

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState)
        {
            // Log.d(TAG, "ComponentListener --> onPlayerStateChanged: playWhenReady " + playWhenReady + " playbackState " + playbackState);
            switch (playbackState)
            {
                case Player.STATE_BUFFERING:
                    if (playWhenReady) {
                        if (mListener != null) { mListener.showLoadStateView(View.VISIBLE);}
                    }
                    break;
                case Player.STATE_READY:
                    if (mListener != null) { mListener.showLoadStateView(View.GONE);}
                    break;
                case Player.STATE_ENDED:
                    break;
                case Player.STATE_IDLE:
                    break;
                default:
            }
            updatePosition();
            if (playWhenReady && playbackState == Player.STATE_READY)
            {
                if (mListener != null)
                {
                    mListener.onReady();
                }
            }
            else if (playWhenReady && playbackState == Player.STATE_ENDED)
            {
                //播放结束
                mState = VideoPlayer.State.PREPARED;
                if (mListener != null)
                {
                    mListener.onVideoFinish();
                }
            }
        }


        @Override
        public void onPlayerError(ExoPlaybackException error)
        {
            mState = VideoPlayer.State.PREPARED;
            mErrorCount += 1;

            String errorString = null;
            if (error.type == ExoPlaybackException.TYPE_RENDERER)
            {
                if (error.getRendererException() instanceof MediaCodecRenderer.DecoderInitializationException)
                {
                    // Special case for decoder initialization failures.
                    MediaCodecRenderer.DecoderInitializationException decoderInitializationException
                            = (MediaCodecRenderer.DecoderInitializationException) error.getRendererException();
                    if (decoderInitializationException.decoderName == null)
                    {
                        if (decoderInitializationException.getCause() instanceof MediaCodecUtil.DecoderQueryException)
                        {
                            errorString = "Unable to query device decoders";
                        }
                        else if (decoderInitializationException.secureDecoderRequired)
                        {
                            errorString = "This device does not provide a secure decoder for: " + decoderInitializationException.mimeType;
                        }
                        else
                        {
                            errorString = "This device does not provide a decoder for: " + decoderInitializationException.mimeType;
                        }
                    }
                    else
                    {
                        errorString = "Unable to instantiate decoder: " + decoderInitializationException.decoderName;
                    }
                }
            }
            else if (error.type == ExoPlaybackException.TYPE_SOURCE)
            {
                error.getSourceException().printStackTrace();
                errorString = "type source exception";
            }
            else if (error.type == ExoPlaybackException.TYPE_UNEXPECTED)
            {
                error.getUnexpectedException().printStackTrace();
                errorString = "type unexpected exception";
            }

            if (error.getSourceException() instanceof FileNotFoundException) {
                removeErrorCurrentWindowsMediaSource();
                mErrorCount = 0;
                return;
            }
            if (mErrorCount <= 1 && error.getSourceException() instanceof EOFException) {
                clearResumePosition();
                prepare();
                start();
                return;
            } else if (mErrorCount <= 2 && error.getSourceException() instanceof EOFException) {
                removeErrorCurrentWindowsMediaSource();
                return;
            } else if (error.getSourceException() instanceof UnrecognizedInputFormatException) {
                mErrorCount = 0;
                errorString = "input format error";
            }

            if (mListener != null) mListener.onPlayerError(error, errorString);
        }

        private void removeErrorCurrentWindowsMediaSource()
        {
            int currentWindowIndex = mSimpleExoPlayer == null ? C.INDEX_UNSET : mSimpleExoPlayer.getCurrentWindowIndex();
            if (currentWindowIndex != C.INDEX_UNSET
                    && mConcatenatingMediaSource != null
                    && mConcatenatingMediaSource instanceof DynamicConcatenatingMediaSource) {
                MediaSource mediaSource = ((DynamicConcatenatingMediaSource) mConcatenatingMediaSource).getMediaSource(currentWindowIndex);
                if (mediaSource != null) mediaSource.releaseSource();
                ((DynamicConcatenatingMediaSource) mConcatenatingMediaSource).removeMediaSource(currentWindowIndex);
                mErrorCount -=1;
                if (mListener != null) mListener.onPlayerErrorIndex(currentWindowIndex);
                clearResumePosition();
                prepare();
                start();
            }
        }

        @Override
        public void onPositionDiscontinuity(int reason)
        {
            updatePosition();
//            Log.d(TAG, "ComponentListener --> onPositionDiscontinuity: ");
        }

        @Override
        public void onPlaybackParametersChanged(PlaybackParameters playbackParameters)
        {

        }

        @Override
        public void onSeekProcessed()
        {

        }
    }

    public interface OnVideoPositionListener
    {
        void position(long duration, long position);
    }

    public interface OnVideoPlayerListener extends OnVideoPositionListener
    {
        /**
         * 渲染第一帧时回调
         */
        void onRenderedFirstFrame();

        /**
         * 视频大小变化时回调
         *
         * @param width  视频宽
         * @param height 视频高
         */
        void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio);

        /**
         * player加载出错
         *
         * @param error
         */
        void onPlayerError(ExoPlaybackException error, String errorMsg);

        /**
         * prepared出错
         */
        void onPreParedError();

        /**
         * 播放结束
         */
        void onVideoFinish();

        /**
         * 准备好
         */
        void onReady();

        /**
         * 显示隐藏加载布局
         *
         * @param visibility
         */
        void showLoadStateView(int visibility);

        void onPlayerErrorIndex(int currentWindowsIndex);
    }
}
