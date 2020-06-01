package cn.poco.video.encoder;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.Date;

/**
 * Created by zwq on 2016/06/22 11:21.<br/><br/>
 */
@SuppressLint("NewApi")
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class MediaMuxerWrapper {

    private static final String TAG = MediaMuxerWrapper.class.getName();
    private static final boolean DEBUG = false;    // TODO set false on release
    private static final String DIR_NAME = "VideoRecordSample";

    private String mOutputPath;
    private final MediaMuxer mMediaMuxer;    // API >= 18
    private int mEncoderCount, mStartedCount;
    private boolean mIsStarted;
    private MediaEncoder mVideoEncoder, mAudioEncoder;

    private boolean mIsPrepared;
    private boolean mAudioEncoderEnable = true;
    private boolean mEncoderType;

    private long mRecordStartTime;
    private long mPresentationTimeUs;
    private int mReleaseCount;
    private boolean mReleaseAudioEnable;

    /**
     * Constructor
     *
     * @param fileName extension of output file
     * @throws IOException
     */
    public MediaMuxerWrapper(String fileName) throws IOException {
        this(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES), DIR_NAME).getAbsolutePath(), fileName);
    }

    public MediaMuxerWrapper(Uri uri) throws Exception {
        if (uri == null) {
            throw new Exception("uri is null");
        }
        File file = new File(new URI(uri.toString()));
        mOutputPath = file.getAbsolutePath();
        if (!mOutputPath.endsWith(".mp4")) {
            mOutputPath = new File(mOutputPath, String.valueOf(new Date().getTime()) + ".mp4").getAbsolutePath();
        }
        mMediaMuxer = new MediaMuxer(mOutputPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
        mEncoderCount = mStartedCount = 0;
        mReleaseCount = 0;
        mIsStarted = false;
    }

    public MediaMuxerWrapper(String fileDir, String fileName) throws IOException {
        if (TextUtils.isEmpty(fileDir) || TextUtils.isEmpty(fileName)) {
            throw new NullPointerException("the param fileDir or fileName is null");
        }
        if (!fileName.endsWith(".mp4")) {
            throw new IllegalArgumentException("file name not ends with mp4");
        }
        File file = new File(fileDir);
        file.mkdirs();
        if (file.canWrite()) {
            mOutputPath = new File(fileDir, fileName).getAbsolutePath();
            file = null;
        } else {
            throw new RuntimeException("This app has no permission of writing external storage");
        }
        mMediaMuxer = new MediaMuxer(mOutputPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
        mEncoderCount = mStartedCount = 0;
        mReleaseCount = 0;
        mIsStarted = false;
    }

    public String getOutputPath() {
        return mOutputPath;
    }

    public MediaEncoder getVideoEncoder() {
        return mVideoEncoder;
    }

    public MediaEncoder getAudioEncoder() {
        return mAudioEncoder;
    }

    /**
     * call this method before start
     * @param degrees
     */
    public void setOrientationHint(int degrees) {
        if (mMediaMuxer != null) {
            mMediaMuxer.setOrientationHint(degrees);
        }
    }

    public void setAudioEncoderEnable(boolean enable) {
        if (mAudioEncoder != null) {
            if (enable && !mAudioEncoderEnable) {
                mEncoderCount++;
            } else if (!enable && mAudioEncoderEnable) {
                mEncoderCount--;
            } else {
                return;
            }
            mAudioEncoderEnable = enable;
        }
    }

    public void prepare() throws Exception {
        if (mAudioEncoder != null && mAudioEncoderEnable) {
            mAudioEncoder.prepare();
        }
        if (mVideoEncoder != null) {
            mIsPrepared = mVideoEncoder.prepare();
        }
        mRecordStartTime = 0;
    }

    public void setViewSize(float width, float height) {
        if (mVideoEncoder != null) {
            mVideoEncoder.setViewSize(width, height);
        }
    }

    public void startRecording() {
        if (!mIsPrepared) {
            throw new IllegalStateException("not prepare");
        }
        if (mVideoEncoder != null) {
            mVideoEncoder.startRecording();
        }
        if (mAudioEncoder != null && mAudioEncoderEnable) {
            mAudioEncoder.startRecording();
        }
        if (mRecordStartTime <= 0) {
            mRecordStartTime = System.currentTimeMillis();
        }
    }

    public long getRecordStartTime() {
        return mRecordStartTime;
    }

    public void pauseRecording() {
        if (mAudioEncoder != null && mAudioEncoderEnable) {
            mAudioEncoder.pauseRecording();
        }
        if (mVideoEncoder != null) {
            mVideoEncoder.pauseRecording();
        }
    }

    public void resumeRecording() {
        if (mVideoEncoder != null) {
            mVideoEncoder.resumeRecording();
        }
        if (mAudioEncoder != null && mAudioEncoderEnable) {
            mAudioEncoder.resumeRecording();
        }
    }

    public void stopRecording() {
        stopRecording(false);
    }

    public void stopRecording(boolean release) {
        if (mAudioEncoder != null && mAudioEncoderEnable) {
            mAudioEncoder.stopRecording();
            if (release) {
                mAudioEncoder.releaseAll();
            }
        }
        mAudioEncoder = null;
        if (mVideoEncoder != null) {
            mVideoEncoder.stopRecording();
            if (release) {
                mVideoEncoder.releaseAll();
            }
        }
        mVideoEncoder = null;
    }

    public synchronized boolean canRecord() {
        boolean canRecord = mIsPrepared;
        if (mAudioEncoder != null && mAudioEncoderEnable) {
            canRecord = canRecord & mAudioEncoder.canRecord();
        }
        if (mVideoEncoder != null) {
            canRecord = canRecord & mVideoEncoder.canRecord();
        }
        return canRecord;
    }

    public synchronized boolean canStop() {
        if (mRecordStartTime > 0) {
            long time = System.currentTimeMillis() - mRecordStartTime;
            if (time > 900) {
                long t1 = time % 1000;
                long t2 = time / 1000;
                if (t1 > 0) {
                    if (time >= ((t2 + 1) * 1000 - 50)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public synchronized boolean isStarted() {
        return mIsStarted;
    }

    public synchronized void setPrepared(boolean prepared) {
        mIsPrepared = prepared;
    }

    public synchronized boolean isPrepared() {
        return mIsPrepared;
    }

    public synchronized void setReleaseCount(int num) {
        mReleaseCount += num;
    }

    public synchronized boolean canRelease(boolean isVideo) {
        return mReleaseCount == mEncoderCount && (isVideo ? true : mReleaseAudioEnable);
    }

    public synchronized void setReleaseAudioEnable(boolean enable) {
        mReleaseAudioEnable = enable;
    }

//**********************************************************************
//**********************************************************************

    /**
     * assign encoder to this calss. this is called from encoder.
     *
     * @param encoder instance of MediaVideoEncoder or MediaAudioEncoder
     */
    /*package*/ void addEncoder(final MediaEncoder encoder) {
        if (encoder instanceof MediaVideoEncoder) {
            if (mVideoEncoder != null) {
                throw new IllegalArgumentException("Video encoder already added.");
            }
            mVideoEncoder = encoder;
        } else if (encoder instanceof MediaAudioEncoder) {
            if (mAudioEncoder != null) {
                throw new IllegalArgumentException("Video encoder already added.");
            }
            mAudioEncoder = encoder;
        } else
            throw new IllegalArgumentException("unsupported encoder");
        mEncoderCount = (mVideoEncoder != null ? 1 : 0) + (mAudioEncoder != null ? 1 : 0);
    }

    /**
     * request start recording from encoder
     *
     * @return true when muxer is ready to write
     */
    /*package*/
    synchronized boolean start() {
        if (DEBUG) Log.v(TAG, "start:");
        mStartedCount++;
        if ((mEncoderCount > 0) && (mStartedCount == mEncoderCount)) {
            try {
                mMediaMuxer.start();
                mIsStarted = true;
                notifyAll();
                if (DEBUG) Log.v(TAG, "MediaMuxer started:");
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        return mIsStarted;
    }

    /**
     * request stop recording from encoder when encoder received EOS
     */
    /*package*/
    synchronized void stop() {
        if (DEBUG) Log.v(TAG, "stop:mStartedCount=" + mStartedCount);
        mStartedCount--;
        if ((mEncoderCount > 0) && (mStartedCount <= 0)) {
            try {
                mMediaMuxer.stop();
                mMediaMuxer.release();
                mIsStarted = false;
                if (DEBUG) Log.v(TAG, "MediaMuxer stopped:");
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * assign encoder to muxer
     *
     * @param format
     * @return minus value indicate error
     */
    /*package*/
    synchronized int addTrack(final MediaFormat format) {
        if (mIsStarted) {
            throw new IllegalStateException("muxer already started");
        }
        final int trackIx = mMediaMuxer.addTrack(format);
        if (DEBUG)
            Log.i(TAG, "addTrack:trackNum=" + mEncoderCount + ",trackIx=" + trackIx + ",format=" + format);
        return trackIx;
    }

    /**
     * 视轨音轨同步，音轨用视轨的时间，使音轨时长不超过视轨时长
     *
     * @param encoderType        true:VideoEncoder, false:AudioEncoder
     * @param presentationTimeUs
     * @return
     */
    /*package*/
    synchronized long getPresentationTimeUs(boolean encoderType, long presentationTimeUs) {
        if (mPresentationTimeUs == 0) {
            mEncoderType = encoderType;
            mPresentationTimeUs = presentationTimeUs;
            return mPresentationTimeUs;
        }
        if (encoderType) {
            mPresentationTimeUs = presentationTimeUs;
        }
//        if (encoderType == mMediaType) {
//            mPresentationTimeUs = time;
//        }
        return mPresentationTimeUs;
    }

    /**
     * write encoded data to muxer
     *
     * @param trackIndex
     * @param byteBuf
     * @param bufferInfo
     */
    /*package*/
    synchronized void writeSampleData(final int trackIndex, final ByteBuffer byteBuf, final MediaCodec.BufferInfo bufferInfo) {
        if (mStartedCount > 0) {
            try {
                mMediaMuxer.writeSampleData(trackIndex, byteBuf, bufferInfo);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

}
