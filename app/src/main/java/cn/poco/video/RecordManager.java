package cn.poco.video;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Date;

import cn.poco.utils.FileUtil;
import cn.poco.utils.PermissionHelper;
import cn.poco.video.encoder.MediaAudioEncoder;
import cn.poco.video.encoder.MediaEncoder;
import cn.poco.video.encoder.MediaMuxerWrapper;
import cn.poco.video.encoder.MediaVideoEncoder;
import cn.poco.video.encoder.RecordState;
import cn.poco.widget.MyProgressDialog;

/**
 * Created by zwq on 2016/07/04 16:42.<br/><br/>
 */
public class RecordManager implements MediaEncoder.MediaEncoderListener {

    private final String TAG = "vvv RecordManager";

    private Context mContext;
    private Handler mHandler;
    private Object mLockObject = new Object();

    private int mVideoWidth = 480;
    private int mVideoHeight = 640;
    private float mVideoRatio;
    private String mVideoFileDir;
    private String mVideoFilePath;
    private String mBgMusicPath;

    private Uri mVideoUri;

    private MediaMuxerWrapper mMediaMuxerWrapper;
    private MediaPlayer mMediaPlayer;
    private boolean mAudioRecordEnable = true;
    private boolean mIsDestroy;

    @Retention(RetentionPolicy.SOURCE)
    @interface PrepareState {
        int Idle = 0;
        int PrepareStart = 1;
        int PrepareEnd = 2;
        int PrepareFinish = 3;
    }
    private int mPrepareState = PrepareState.Idle;
    private int mRecordState;
    private int mMediaPlayerState = -1;
    private int mErrorCount;

    private boolean mNeedStartRecord;
    private OnRecordListener mOnRecordListener;
    private CountDownTimer mCountDownTimer;

    private long mVideoTime = 10200; //录制时长10s
    private long mGifTime = 3000;
    private long mVideoTimeLong = mVideoTime;
    private long mTickTime = 50;//进度更新间隔
    private long mRecordTime = -1;
    private boolean mCountDownIsFinish;
    private long mDuration;
    private boolean mStopByTime;
    private boolean mStopWithoutTime;

    private boolean mIsValidFile;
    private MyProgressDialog mDialog;

    private MediaVideoEncoder mMediaVideoEncoder;
    private MediaAudioEncoder mMediaAudioEncoder;

    private OnRecordMixListener mOnRecordMixListener;

    public RecordManager(Context context) {
        mContext = context;
    }

    public void setContext(Context context) {
        mContext = context;
    }

    public void setMessageHandler(Handler handler) {
        mHandler = handler;
    }

    public void setOnRecordListener(OnRecordListener listener) {
        mOnRecordListener = listener;
    }

    public void setOnRecordMixListener(OnRecordMixListener listener) {
        this.mOnRecordMixListener = listener;
    }

    public String initDefaultPath() {
        return initDefaultPath(true);
    }

    public String initDefaultPath(boolean deleteOld) {
        if (mVideoFileDir != null) return null;
        mVideoFileDir = FileUtils.getVideoDir() + File.separator;
        if (deleteOld) {
            FileUtil.deleteSDFile(mVideoFileDir);
        }
//        mBgMusicPath = "music/bg.mp3";
        return mVideoFileDir;
    }

    public void setVideoSize(int width, int height) {
        if (width != mVideoWidth || height != mVideoHeight) {
            try {
                if (mLockObject != null) {
                    synchronized (mLockObject) {
                        mVideoWidth = width;
                        mVideoHeight = height;
                        mVideoRatio = height * 1.0f / width;
                        releaseAll(true, false, true);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isSameSize(int width, int height) {
        if (width != mVideoWidth || height != mVideoHeight) {
            return false;
        }
        return true;
    }

    public float getVideoRatio() {
        return mVideoRatio;
    }

    /**
     * @param time 毫秒
     */
    public void setVideoTimeLong(long time) {
        mVideoTimeLong = time;
    }

    /**
     * @param type 0:video, 1:gif
     */
    public void setVideoType(int type) {
        if (type == 1) {
            mVideoTimeLong = mGifTime;
        } else {
            mVideoTimeLong = mVideoTime;
        }
    }

    /**
     * 视频保存路径
     **/
    public void setVideoPath(String dir) {
        mVideoFileDir = dir;
    }


    public void setVideoUri(Uri uri) {
        mVideoUri = uri;
    }

    /**
     * 背景音乐路径
     **/
    public void setBgMusicPath(String path) {
        mBgMusicPath = path;
        if (mMediaPlayer != null) {
            if (mBgMusicPath != null) {
                stopBgMusic();
            } else {
                mMediaPlayer.release();
                mMediaPlayer = null;
            }
            mMediaPlayerState = -1;
        }
        if (mBgMusicPath != null) {
            initMediaPlayer();
        }
    }

    public boolean hasBgMusic() {
        if (!TextUtils.isEmpty(mBgMusicPath)) {
            return true;
        }
        return false;
    }

    public void setAudioRecordEnable(boolean enable) {
        if (enable == mAudioRecordEnable) return;
        if (mLockObject != null) {
            synchronized (mLockObject) {
                mAudioRecordEnable = enable;
                if ((mPrepareState == PrepareState.PrepareEnd || mPrepareState == PrepareState.PrepareFinish) && mMediaMuxerWrapper != null) {
                    mMediaMuxerWrapper.setAudioEncoderEnable(mAudioRecordEnable);
                }
            }
        }
    }

    public void prepare() throws Exception {
        if (mLockObject != null) {
            synchronized (mLockObject) {
                if ((mVideoFileDir == null && mVideoUri != null) || (mVideoFileDir != null && mVideoUri == null)) {
                    //文件存在
                } else {
                    throw new NullPointerException("video file directory or video uri is null");
                }
                if (mPrepareState != PrepareState.Idle) {
                    return;
                }
                mPrepareState = PrepareState.PrepareStart;
                File file = Environment.getExternalStorageDirectory();
                if (file == null || !file.canWrite()) {
                    return;//没有读写权限
                }
                file = null;

                mMediaVideoEncoder = null;
                mMediaAudioEncoder = null;
                mMediaMuxerWrapper = null;
                mIsValidFile = false;
                if (!TextUtils.isEmpty(mVideoFileDir)) {
                    mMediaMuxerWrapper = new MediaMuxerWrapper(mVideoFileDir, new Date().getTime() + ".mp4");
                } else if (mVideoUri != null) {
                    mMediaMuxerWrapper = new MediaMuxerWrapper(mVideoUri);
                }

                if (mMediaMuxerWrapper != null) {
                    mMediaMuxerWrapper.setAudioEncoderEnable(mAudioRecordEnable);
                    mVideoFilePath = mMediaMuxerWrapper.getOutputPath();

                    // for video capturing
                    mMediaVideoEncoder = new MediaVideoEncoder(mMediaMuxerWrapper, this, mVideoWidth, mVideoHeight);
                    if (mAudioRecordEnable) {
                        // for audio capturing
                        mMediaAudioEncoder = new MediaAudioEncoder(mMediaMuxerWrapper, null);
                    }
                } else {
                    mPrepareState = PrepareState.Idle;
                    return;
                }
                initMediaPlayer();
                mIsDestroy = false;
                mPrepareState = PrepareState.PrepareEnd;

                if (mOnRecordMixListener != null) {
                    mOnRecordMixListener.onPrepare(mMediaMuxerWrapper, System.currentTimeMillis());
                }
                if (mOnRecordListener != null) {
                    mOnRecordListener.onPrepare(mMediaMuxerWrapper);
                }
            }
        }
    }

    public boolean isPrepareFinish() {
        return mPrepareState == PrepareState.PrepareFinish;
    }

    public void prepareAgain(int state) {
        if (mPrepareState == PrepareState.PrepareEnd) {
            if (state == RecordState.IDLE) {
                /*try {
                    prepare();
                } catch (Exception e) {
                    e.printStackTrace();
                }*/
            } else if (state == RecordState.WAIT) {
                mPrepareState = PrepareState.PrepareFinish;
            }
        }
    }

    /**
     * call this method before start
     * @param degrees
     */
    public void setOrientationHint(int degrees) {
        if (mMediaMuxerWrapper != null && (mRecordState == RecordState.IDLE || mRecordState == RecordState.PREPARE)) {
            if (degrees == 0 || degrees == 90 || degrees == 180 || degrees == 270) {

            } else {
                //Unsupported degree
                degrees = 0;
            }
            mMediaMuxerWrapper.setOrientationHint(degrees);
        }
    }

    public void initMediaPlayer() {
        if (mBgMusicPath != null) {
            if (mMediaPlayerState != -1) return;
            if (mMediaPlayer == null) {
                mMediaPlayer = new MediaPlayer();
            }
            try {
                mMediaPlayer.reset();
                if (mBgMusicPath.startsWith("/")) {
                    mMediaPlayer.setDataSource(mBgMusicPath);
                } else if (mContext != null) {
                    AssetManager assetManager = mContext.getAssets();
                    AssetFileDescriptor fileDescriptor = assetManager.openFd(mBgMusicPath);
                    mMediaPlayer.setDataSource(fileDescriptor.getFileDescriptor(), fileDescriptor.getStartOffset(), fileDescriptor.getLength());
                }
//            mMediaPlayer.setLooping(true);
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mMediaPlayer.setVolume(1.0f, 1.0f);
                mMediaPlayerState = RecordState.IDLE;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void startBgMusic() {
        if (mBgMusicPath != null && mMediaPlayer != null) {
            try {
                if (mMediaPlayerState == RecordState.IDLE) {
                    mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            mp.start();
                            if (mOnRecordMixListener != null) {
                                mOnRecordMixListener.onAudioStart(mMediaMuxerWrapper, System.currentTimeMillis(), mp.getDuration());
                            }
                            mMediaPlayerState = RecordState.START;
                        }
                    });
                    mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            if (mOnRecordMixListener != null) {
                                mOnRecordMixListener.onAudioEnd(mMediaMuxerWrapper, System.currentTimeMillis(), mp.getDuration());
                            }
                            mMediaPlayerState = RecordState.STOP;
                        }
                    });
                    mMediaPlayer.prepare();
                    mMediaPlayerState = RecordState.PREPARE;
                } else if (mMediaPlayerState == RecordState.STOP) {
                    mMediaPlayer.start();
                    mMediaPlayerState = RecordState.START;
                    if (mOnRecordMixListener != null) {
                        mOnRecordMixListener.onAudioStart(mMediaMuxerWrapper, System.currentTimeMillis(), mMediaPlayer.getDuration());
                    }
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isPlayingMusic() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()/* && mMediaPlayerState == RecordState.START*/) {
            return true;
        }
        return false;
    }

    public void stopBgMusic() {
        if (mMediaPlayer != null &&/* mMediaPlayer.isPlaying() && */mMediaPlayerState == RecordState.START) {
            mMediaPlayer.stop();
            mMediaPlayerState = RecordState.STOP;
        }
    }

    public int getMusicStartPosition() {
        if (mMediaPlayer != null && mMediaPlayerState == RecordState.START) {
            return mMediaPlayer.getCurrentPosition();
        }
        return -1;
    }

    public boolean isRecording() {
        return mPrepareState == PrepareState.PrepareFinish;
    }

    public void startRecord() throws Exception {
        if (mPrepareState == PrepareState.Idle) {
            mNeedStartRecord = true;
            prepare();
        } else if (mPrepareState == PrepareState.PrepareStart || mPrepareState == PrepareState.PrepareEnd) {
            mNeedStartRecord = true;
            return;
        }
        mNeedStartRecord = false;
        if (mOnRecordListener != null) {
            mOnRecordListener.onStart(mMediaMuxerWrapper);
        }
    }

    private void executeCountDownTimer() {
//        Log.i(TAG, "executeCountDownTimer   onStarted");
        mCountDownTimer = new CountDownTimer(mVideoTimeLong, mTickTime) {
            private long mStartTime;

            @Override
            public void onTick(long millisUntilFinished) {
                //Log.i(TAG, "onTick: "+millisUntilFinished);
                if (mPrepareState != PrepareState.PrepareFinish) {
                    if (mCountDownTimer != null) {
                        mCountDownTimer.cancel();
                        mCountDownTimer = null;
                    }
                    return;
                }
                if (mStartTime == 0 && mMediaMuxerWrapper != null) {
                    mStartTime = mMediaMuxerWrapper.getRecordStartTime();
                }
                long time = 0;
                if (mStartTime == 0) {
                    time = mVideoTimeLong - millisUntilFinished;
                } else {
                    time = System.currentTimeMillis() - mStartTime;
                }
                float progress = (time * 100.0f / mVideoTimeLong);
                if (mOnRecordListener != null) {
                    mOnRecordListener.onProgressChange(progress);
                }

                if (mStopByTime && time > 900 && mVideoTimeLong - time > 1000) {//剩余总时长不足1s继续录制
                    long t1 = time % 1000;
                    long t2 = time / 1000;
                    if (t1 > 0) {
                        if (time >= ((t2 + 1) * 1000 - 50)) {
                            //stop
                            forceStop();
                            mStopByTime = false;
                        }
                    }
                }

                if (progress >= 100) {
                    forceStop();
                }
            }

            private void forceStop() {
                if (mStartTime != 0) {
                    if (mCountDownTimer != null) {
                        mCountDownTimer.cancel();
                    }
                    stopRecord();
                    mCountDownIsFinish = true;
                }
            }

            @Override
            public void onFinish() {
                if (mOnRecordListener != null) {
                    mOnRecordListener.onProgressChange(100);
                }
                if (!mCountDownIsFinish) {
                    stopRecord();
                    mCountDownIsFinish = true;
                }
            }
        };
        mCountDownTimer.start();
        mRecordTime = System.currentTimeMillis();
        if (mOnRecordMixListener != null) {
            mOnRecordMixListener.onStart(mMediaMuxerWrapper, mRecordTime);
        }
    }

    public void resumeRecord() {
        if (mOnRecordMixListener != null) {
            mOnRecordMixListener.onResume();
        }
        if (mOnRecordListener != null) {
            mOnRecordListener.onResume();
        }
    }

    public void pauseRecord() {
        if (mOnRecordMixListener != null) {
            mOnRecordMixListener.onPause();
        }
        if (mOnRecordListener != null) {
            mOnRecordListener.onPause();
        }
    }

    public void stopRecord() {
        if (mCountDownIsFinish) {
            return;
        }
        if (mRecordState != RecordState.START) {
            mRecordTime = -1;
        }
        // 最后一秒不停止倒计时
        if (!mStopWithoutTime) {
            if (mCountDownTimer != null) {
                mCountDownTimer.cancel();
                mCountDownTimer = null;
            }
        }
        mIsValidFile = true;
        mDuration = 0;
        mNeedStartRecord = false;
        mStopByTime = false;
        if (mRecordTime == -1) {
            mIsValidFile = false;
        } else {
            mDuration = System.currentTimeMillis() - mRecordTime;
//            if (mDuration < 500) {//录制小于0.5s无效
//                mIsValidFile = false;
//            }
        }
        if (mIsValidFile) {
            //延时
//            mDialog = new MyProgressDialog(mContext);
            if (mDialog != null) {
                mDialog.setMessage("处理中...");
                mDialog.show();
            }
            if (mOnRecordMixListener != null) {
                mOnRecordMixListener.onStop(true, mDuration, null, System.currentTimeMillis());
            }
            if (mOnRecordListener != null) {
                mOnRecordListener.onStop(true, mDuration, null);
            }
        } else {
            releaseAll(true);
            if (mOnRecordMixListener != null) {
                mOnRecordMixListener.onStop(false, mDuration, null, System.currentTimeMillis());
            }
            if (mOnRecordListener != null) {
                mOnRecordListener.onStop(false, mDuration, null);
            }
        }
    }

    public void stopRecordByTime() {
        mStopByTime = true;
    }

    /**
     * 停止录制器，但不停止倒计时
     */
    public void stopRecordWithoutStopTime() {
        mStopWithoutTime = true;
        stopRecord();
    }

    public boolean releaseAll(boolean deleteFile) {
        return releaseAll(false, deleteFile, false);
    }

    private boolean releaseAll(boolean releaseEncoder, boolean deleteFile, boolean changeSize) {
        if (mLockObject != null) {
            synchronized (mLockObject) {
                if (mPrepareState != PrepareState.PrepareEnd && mPrepareState != PrepareState.PrepareFinish)
                    return false;
                if (mRecordState == RecordState.START && mCountDownTimer != null) {
                    mCountDownTimer.cancel();
                    mCountDownTimer = null;
                }
                if (mMediaMuxerWrapper != null) {
                    mMediaMuxerWrapper.stopRecording(releaseEncoder);
                }
                mMediaMuxerWrapper = null;
                if (mMediaVideoEncoder != null && changeSize) {
                    mMediaVideoEncoder.releaseMediaCodec(false);
                }
                if (mMediaPlayer != null) {
                    mMediaPlayer.stop();
                    mMediaPlayer.release();
                }
                mMediaPlayer = null;
                mMediaPlayerState = -1;
                mPrepareState = PrepareState.Idle;
                mRecordState = RecordState.IDLE;
                mRecordTime = -1;
                mNeedStartRecord = false;
                mStopByTime = false;
                mStopWithoutTime = false;
                mErrorCount = 0;

                if (deleteFile && mVideoFilePath != null) {
                    deleteInvalidFile(mVideoFilePath);
                    mVideoFilePath = null;
                }
            }
        }
        return true;
    }

    public void destroy() {
        mIsDestroy = true;
        releaseAll(false, true, true);
        if (mMediaMuxerWrapper != null) {
            mMediaMuxerWrapper.stopRecording();
        }
        mMediaMuxerWrapper = null;
        if (mMediaVideoEncoder != null) {
            mMediaVideoEncoder.releaseAll();
        }
        mMediaVideoEncoder = null;
        if (mMediaAudioEncoder != null) {
            mMediaAudioEncoder.releaseAll();
        }
        mMediaAudioEncoder = null;
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
        }
        mMediaPlayer = null;
        mMediaPlayerState = -1;
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
        mDialog = null;
        mContext = null;
        mOnRecordMixListener = null;
        mPrepareState = PrepareState.Idle;
    }

    private void deleteInvalidFile(String videoFilePath) {
        FileUtil.deleteSDFile(videoFilePath);
    }

    @Override
    public void onPrepared(MediaEncoder encoder) {
        //Log.i(TAG, "VideoEncoderListener onPrepared");
        /*if (mMediaPlayer != null) {
            try {
                mMediaPlayer.prepare();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }*/
        mErrorCount = 0;
        if (mPrepareState != PrepareState.PrepareEnd) {
            return;
        }
        if (mLockObject != null) {
            synchronized (mLockObject) {
                mPrepareState = PrepareState.PrepareFinish;
                mRecordState = RecordState.PREPARE;

                if (mNeedStartRecord) {
                    mNeedStartRecord = false;
                    if (mHandler != null) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    startRecord();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                }
            }
        }
    }

    @Override
    public void onStarted(MediaEncoder encoder) {
//        Log.i(TAG, "VideoEncoderListener onStarted");
        /*if (mMediaPlayer != null) {
            mMediaPlayer.start();
        }*/
        if (mPrepareState != PrepareState.PrepareFinish) {
            return;
        }
        if (mLockObject != null) {
            synchronized (mLockObject) {
                mRecordTime = -1;
                mRecordState = RecordState.START;
                mCountDownIsFinish = false;

                if (mHandler != null) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            executeCountDownTimer();
                        }
                    });
                }
            }
        }
    }

    @Override
    public void onResumed(MediaEncoder encoder) {
//        Log.i(TAG, "VideoEncoderListener onResumed");
        /*if (mMediaPlayer != null) {
            mMediaPlayer.start();
        }*/
    }

    @Override
    public void onPaused(MediaEncoder encoder) {
//            Log.i(TAG, "VideoEncoderListener onPaused");
        /*if (mMediaPlayer != null) {
            mMediaPlayer.pause();
        }*/
    }

    @Override
    public void onStopped(MediaEncoder encoder) {
//        Log.i(TAG, "VideoEncoderListener onStopped");
    }

    @Override
    public void onReleased(MediaEncoder encoder) {
//        Log.i(TAG, "VideoEncoderListener onReleased");
        if (mIsDestroy) {
            return;
        }
        if (mLockObject != null) {
            synchronized (mLockObject) {
                if (mIsValidFile) {
                    releaseAll(false);
                }
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mDialog != null) {
                            mDialog.dismiss();
                        }
                        if (mIsValidFile) {
                            mIsValidFile = false;
                            if (mOnRecordMixListener != null) {
                                mOnRecordMixListener.onStop(true, mDuration, mVideoFilePath, System.currentTimeMillis());
                            }
                            if (mOnRecordListener != null) {
                                mOnRecordListener.onStop(true, mDuration, mVideoFilePath);
                            }
                        }
                        mStopWithoutTime = false;
                    }
                }, 0);
            }
        }
    }

    @Override
    public void onError(MediaEncoder encoder, String msg) {
        //Log.i(TAG, "onError: ");
        if (mErrorCount < 1) {
            mErrorCount++;
            mPrepareState = PrepareState.Idle;
            if (mHandler != null) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            prepare();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
    }

    /**
     * @return false 权限没开，true正常录音
     */
    public static boolean isRecordVoiceEnable() {
        return PermissionHelper.queryAudioRecordPermission();
    }

    /**
     * 获取录制开始的时间
     * @return
     */
    public long getRecordTime() {
        return mRecordTime;
    }
}
