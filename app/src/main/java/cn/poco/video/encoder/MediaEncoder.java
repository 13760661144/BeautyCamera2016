package cn.poco.video.encoder;

import android.annotation.SuppressLint;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;

/**
 * Created by zwq on 2016/06/22 11:23.<br/><br/>
 */
@SuppressLint("NewApi")//requires API level 16
public abstract class MediaEncoder implements Runnable {

    private static final String TAG = "MediaEncoder";
    private static final boolean DEBUG = false;    // TODO set false on release

    protected static final int TIMEOUT_USEC = 60000;    // 10[msec]
    protected static final int MSG_FRAME_AVAILABLE = 1;

    public interface EncoderState {
        public final int IDLE = 0;
        public final int START = 1;
        public final int RESUME = 2;
        public final int ENCODING = 3;
        public final int PAUSE = 4;
        public final int STOP = 5;
    }

    public interface MediaEncoderListener {
        void onPrepared(MediaEncoder encoder);

        void onStarted(MediaEncoder encoder);

        void onResumed(MediaEncoder encoder);

        void onPaused(MediaEncoder encoder);

        void onStopped(MediaEncoder encoder);

        void onReleased(MediaEncoder encoder);

        void onError(MediaEncoder encoder, String msg);
    }

    protected final Object mSync = new Object();
    /**
     * Flag that indicate this encoder is capturing now.
     */
    protected volatile boolean mIsCapturing;
    /**
     * Flag that indicate the frame data will be available soon.
     */
    private int mRequestDrain;
    /**
     * Flag to request stop capturing
     */
    protected volatile boolean mRequestStop;

    protected volatile int mRecordingState = EncoderState.IDLE;
    /**
     * Flag that indicate encoder received EOS(End Of Stream)
     */
    protected boolean mIsEOS;
    /**
     * Flag the indicate the muxer is running
     */
    protected boolean mMuxerStarted;
    /**
     * Track Number
     */
    protected int mTrackIndex;
    /**
     * MediaCodec instance for encoding
     */
    protected MediaCodec mMediaCodec;                // API >= 16(Android4.1.2)
    /**
     * Weak reference of MediaMuxerWarapper instance
     */
    protected final WeakReference<MediaMuxerWrapper> mWeakMuxer;
    /**
     * BufferInfo instance for dequeuing
     */
    private MediaCodec.BufferInfo mBufferInfo;        // API >= 16(Android4.1.2)

    protected MediaEncoderListener mListener;

    protected Thread mThread;
    /**
     * true:Video, false:Audio
     */
    protected boolean mMediaType;
    protected String mMediaTypeStr = "";

    protected boolean mIsLiveEncoder;//直播编码
    protected LiveEncoderServer mLiveEncoderServer;

    public MediaEncoder(final MediaMuxerWrapper muxer, final MediaEncoderListener listener, boolean isLiveEncoder) {
        mIsLiveEncoder = isLiveEncoder;
        if (isLiveEncoder) {
            mWeakMuxer = null;
        } else {
            if (muxer == null) {
                throw new NullPointerException("MediaMuxerWrapper is null");
            }
            mWeakMuxer = new WeakReference<MediaMuxerWrapper>(muxer);
            muxer.addEncoder(this);
        }
        /*if (listener == null) {
            throw new NullPointerException("MediaEncoderListener is null");
        }*/
        mListener = listener;
        synchronized (mSync) {
            // create BufferInfo here for effectiveness(to reduce GC)
            mBufferInfo = new MediaCodec.BufferInfo();
            // wait for starting thread
            mThread = new Thread(this, getClass().getSimpleName());
            mThread.start();
            try {
                mSync.wait();
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public MediaEncoder(final MediaMuxerWrapper muxer, final MediaEncoderListener listener) {
        this(muxer, listener, false);
    }

    public void setLiveEncoderServer(LiveEncoderServer liveEncoderServer) {
        mLiveEncoderServer = liveEncoderServer;
    }

    public boolean isLiveEncoder() {
        return mIsLiveEncoder;
    }

    public String getOutputPath() {
        final MediaMuxerWrapper muxer = mWeakMuxer != null ? mWeakMuxer.get() : null;
        return muxer != null ? muxer.getOutputPath() : null;
    }

    /**
     * the method to indicate frame data is soon available or already available
     *
     * @return return true if encoder is ready to encod.
     */
    public boolean frameAvailableSoon() {
//    	if (DEBUG) Log.v(TAG, "frameAvailableSoon");
        synchronized (mSync) {
            if (!mIsCapturing || mRequestStop) {
                return false;
            }
            mRequestDrain++;
            mSync.notifyAll();
        }
        return true;
    }

    /**
     * encoding loop on private thread
     */
    @Override
    public void run() {
//		android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
        synchronized (mSync) {
            mRequestStop = false;
            mRequestDrain = 0;
            mSync.notify();
        }

        /*if (mIsLiveEncoder) {
            mLiveEncoderServer = new LiveEncoderServer();
            if (mLiveEncoderServer != null) {
                mLiveEncoderServer.init();
            }
        }*/

        final boolean isRunning = true;
        boolean localRequestStop;
        boolean localRequestDrain;
        boolean canRelease = true;
        while (isRunning) {
            synchronized (mSync) {
                localRequestStop = mRequestStop;
                localRequestDrain = (mRequestDrain > 0);
                if (localRequestDrain) {
                    mRequestDrain--;
                }
            }
            if (localRequestStop) {
                drain();
                // request stop recording
                if (DEBUG)
                    Log.d(TAG, mMediaTypeStr + "sending EOS to encoder");
                signalEndOfInputStream();

                // process output data again for EOS signale
                drain();

                releaseMediaCodec();

                //check can release
                final MediaMuxerWrapper muxer = mWeakMuxer != null ? mWeakMuxer.get() : null;
                if (muxer != null) {
                    muxer.setReleaseCount(1);
                    //Make sure that all the mediaCodec write End Of Stream, and then to release
                    canRelease = muxer.canRelease(mMediaType);

                    if (canRelease) {
                        release();
                        if (mMediaType) {
                            muxer.setReleaseAudioEnable(true);//Make sure the video MediaCodec release finish
                        }
                    }
                } else {
                    // release all related objects
                    release();
                }
                break;
            }
            if (localRequestDrain) {
                drain();
            } else {
                synchronized (mSync) {
                    try {
                        mSync.wait();
                    } catch (final Throwable e) {//InterruptedException
//                        e.printStackTrace();
                        break;
                    }
                }
            }
        } // end of while

        if (!canRelease) {
            try {
                final MediaMuxerWrapper muxer = mWeakMuxer != null ? mWeakMuxer.get() : null;
                if (muxer != null) {
                    while (!muxer.canRelease(mMediaType)) {
                        if (DEBUG) Log.d(TAG, mMediaTypeStr + " waiting to release");
                        //waiting to release
                        continue;
                    }
                    if (DEBUG) Log.d(TAG, mMediaTypeStr + " release before thread exit");
                    release();
                    if (mMediaType) {
                        muxer.setReleaseAudioEnable(true);
                    }
                } else {
                    if (DEBUG)
                        Log.d(TAG, mMediaTypeStr + " release before thread exit, muxer is null");
                    release();
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }

        if (mIsLiveEncoder && mLiveEncoderServer != null) {
            if (mLiveEncoderServer.isCanStopServer()) {
                mLiveEncoderServer.stopServer();
            }
            mLiveEncoderServer = null;
            mIsLiveEncoder = false;
        }

        if (DEBUG) Log.d(TAG, mMediaTypeStr + "Encoder thread exiting");
        synchronized (mSync) {
            mRequestStop = true;
            mIsCapturing = false;
            mRecordingState = EncoderState.STOP;
        }
    }

    /*
    * prepareing method for each sub class
    * this method should be implemented in sub class, so set this as abstract method
    * @throws IOException
    */
   /*package*/
    public abstract boolean prepare() throws Exception;

    public void setViewSize(float width, float height) {
        if (DEBUG) Log.i(TAG, "setViewSize:" + width + ", " + height);
    }

    public boolean canRecord() {
        return mRecordingState == EncoderState.START || mRecordingState == EncoderState.RESUME || mRecordingState == EncoderState.ENCODING;//mIsCapturing;
    }

    public void startRecording() {
        if (DEBUG) Log.d(TAG, mMediaTypeStr + "startRecording");
        synchronized (mSync) {
            mIsCapturing = true;
            mRequestStop = false;
            mRecordingState = EncoderState.START;
            mSync.notifyAll();
        }
        if (mListener != null) {
            try {
                mListener.onStarted(this);
            } catch (final Throwable e) {
                Log.e(TAG, "failed onStarted", e);
            }
        }
    }

    public boolean isEncoding() {
        return mRecordingState == EncoderState.ENCODING;
    }

    public void pauseRecording() {
        if (DEBUG) Log.d(TAG, mMediaTypeStr + "pauseRecording");
        synchronized (mSync) {
            if (mRecordingState != EncoderState.ENCODING) {
                return;
            }
            mRecordingState = EncoderState.PAUSE;
            mSync.notifyAll();
        }
        if (mListener != null) {
            try {
                mListener.onPaused(this);
            } catch (final Throwable e) {
                Log.e(TAG, "failed onPaused", e);
            }
        }
    }

    public void resumeRecording() {
        if (DEBUG) Log.d(TAG, mMediaTypeStr + "resumeRecording");
        synchronized (mSync) {
            if (mRecordingState != EncoderState.PAUSE) {
                return;
            }
            mRecordingState = EncoderState.RESUME;
            mSync.notifyAll();
        }
        if (mListener != null) {
            try {
                mListener.onResumed(this);
            } catch (final Throwable e) {
                Log.e(TAG, "failed onResumed", e);
            }
        }
    }

    /**
     * the method to request stop encoding
     */
    public void stopRecording() {
        if (DEBUG) Log.d(TAG, mMediaTypeStr + "stopRecording");
        synchronized (mSync) {
            if (!mIsCapturing || mRequestStop) {
                return;
            }
            mRequestStop = true;    // for rejecting newer frame
            mRecordingState = EncoderState.STOP;
            mSync.notifyAll();
            // We can not know when the encoding and writing finish.
            // so we return immediately after request to avoid delay of caller thread
        }
        if (mListener != null) {
            try {
                mListener.onStopped(this);
            } catch (final Throwable e) {
                Log.e(TAG, "failed onStopped", e);
            }
        }
    }

//********************************************************************************
//********************************************************************************

    protected void releaseMediaCodec() {
        releaseMediaCodec(true);
    }

    public void releaseMediaCodec(boolean stop) {
        if (mMediaCodec != null) {
            try {
                if (stop) {
                    mMediaCodec.stop();
                }
                mMediaCodec.release();
                mMediaCodec = null;
            } catch (final Throwable e) {
                Log.e(TAG, "failed releasing MediaCodec", e);
            }
        }
    }

    /**
     * Release all releated objects
     */
    protected void release() {
        if (DEBUG) Log.d(TAG, mMediaTypeStr + "release:");
        mIsCapturing = false;
        releaseMediaCodec();
        if (mMuxerStarted) {
            final MediaMuxerWrapper muxer = mWeakMuxer != null ? mWeakMuxer.get() : null;
            if (muxer != null) {
                try {
                    muxer.stop();
                    if (DEBUG)
                        Log.d(TAG, mMediaTypeStr + "release: muxer stop");
                } catch (final Throwable e) {
                    e.printStackTrace();
                    Log.e(TAG, "failed stopping muxer", e);
                }
            }
        }
        mBufferInfo = null;
        if (mListener != null) {
            try {
                mListener.onReleased(this);
            } catch (final Throwable e) {
                Log.e(TAG, "failed onReleased", e);
            }
            mListener = null;
        }
    }

    protected void signalEndOfInputStream() {
//        if (DEBUG) Log.d(TAG, "sending EOS to encoder");
        // signalEndOfInputStream is only avairable for video encoding with surface
        // and equivalent sending a empty buffer with BUFFER_FLAG_END_OF_STREAM flag.
//		mMediaCodec.signalEndOfInputStream();	// API >= 18
        encode(null, 0, getPTSUs());
    }

    /**
     * Method to set byte array to the MediaCodec encoder
     *
     * @param buffer
     * @param length             　length of byte array, zero means EOS.
     * @param presentationTimeUs
     */
    protected void encode(final ByteBuffer buffer, final int length, final long presentationTimeUs) {
        if (!mIsCapturing) return;
        final ByteBuffer[] inputBuffers = mMediaCodec.getInputBuffers();
        while (mIsCapturing) {
            if (mRecordingState == EncoderState.START || mRecordingState == EncoderState.RESUME) {
                mRecordingState = EncoderState.ENCODING;
            }
            if (mRecordingState == EncoderState.PAUSE) {
                continue;
            }
            final int inputBufferIndex = mMediaCodec.dequeueInputBuffer(TIMEOUT_USEC);
            if (inputBufferIndex >= 0) {
                final ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
                inputBuffer.clear();
                if (buffer != null) {
                    inputBuffer.put(buffer);
                }
//	            if (DEBUG) Log.v(TAG, "encode:queueInputBuffer "+inputBufferIndex);
                if (length <= 0) {
                    // send EOS
                    mIsEOS = true;
                    if (DEBUG) Log.i(TAG, "send BUFFER_FLAG_END_OF_STREAM " + inputBufferIndex);
                    mMediaCodec.queueInputBuffer(inputBufferIndex, 0, 0, presentationTimeUs, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                    break;
                } else {
                    mMediaCodec.queueInputBuffer(inputBufferIndex, 0, length, presentationTimeUs, 0);
                }
                break;
            } else if (inputBufferIndex == MediaCodec.INFO_TRY_AGAIN_LATER) {
                // wait for MediaCodec encoder is ready to encode
                // nothing to do here because MediaCodec#dequeueInputBuffer(TIMEOUT_USEC)
                // will wait for maximum TIMEOUT_USEC(10msec) on each call
            }
        }
    }

    /**
     * drain encoded data and write them to muxer
     */
    protected void drain() {
        if (mMediaCodec == null) return;
        final MediaMuxerWrapper muxer = mWeakMuxer != null ? mWeakMuxer.get() : null;
        if (!mIsLiveEncoder && muxer == null) {
//        	throw new NullPointerException("muxer is unexpectedly null");
            Log.w(TAG, "muxer is unexpectedly null");
            return;
        }
        int encoderStatus = MediaCodec.INFO_TRY_AGAIN_LATER;
        int count = 0;
        ByteBuffer[] encoderOutputBuffers = null;
        try {
            encoderOutputBuffers = mMediaCodec.getOutputBuffers();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        if (encoderOutputBuffers == null) {
            return;
        }
        LOOP:
        while (mIsCapturing) {
            if (mRecordingState == EncoderState.START || mRecordingState == EncoderState.RESUME) {
                mRecordingState = EncoderState.ENCODING;
            }
            if (mRecordingState == EncoderState.PAUSE) {
                continue;
            }
            // get encoded data with maximum timeout duration of TIMEOUT_USEC(=10[msec])
            try {
                encoderStatus = mMediaCodec.dequeueOutputBuffer(mBufferInfo, TIMEOUT_USEC);
            } catch (Throwable t) {
                t.printStackTrace();
            }
            if (encoderStatus == MediaCodec.INFO_TRY_AGAIN_LATER) {
                // wait 5 counts(=TIMEOUT_USEC x 5 = 50msec) until data/EOS come
                if (!mIsEOS) {
                    if (++count > 5)
                        break LOOP;        // out of while
                }
            } else if (encoderStatus == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                if (DEBUG) Log.v(TAG, "INFO_OUTPUT_BUFFERS_CHANGED");
                // this shoud not come when encoding
                encoderOutputBuffers = mMediaCodec.getOutputBuffers();
            } else if (encoderStatus == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                if (DEBUG) Log.v(TAG, "INFO_OUTPUT_FORMAT_CHANGED");
                // this status indicate the output format of codec is changed
                // this should come only once before actual encoded data
                // but this status never come on Android4.3 or less
                // and in that case, you should treat when MediaCodec.BUFFER_FLAG_CODEC_CONFIG come.
                if (mMuxerStarted) {    // second time request is error
                    throw new RuntimeException("format changed twice");
                }
                // get output format from codec and pass them to muxer
                // getOutputFormat should be called after INFO_OUTPUT_FORMAT_CHANGED otherwise crash.
                final MediaFormat format = mMediaCodec.getOutputFormat(); // API >= 16

                if (mIsLiveEncoder && mLiveEncoderServer != null) {
                    /*ByteBuffer sps = format.getByteBuffer("csd-0");
                    ByteBuffer pps = format.getByteBuffer("csd-1");*/
                    mLiveEncoderServer.setSpsPpsData(format.getByteBuffer("csd-0"), format.getByteBuffer("csd-1"));
                }

                if (muxer != null) {
                    mTrackIndex = muxer.addTrack(format);
                }
                mMuxerStarted = true;
                if (muxer != null && !muxer.start()) {
                    // we should wait until muxer is ready
                    synchronized (muxer) {
                        while (!muxer.isStarted())
                            try {
                                muxer.wait(100);
                            } catch (final InterruptedException e) {
                                e.printStackTrace();
                                break LOOP;
                            }
                    }
                }
            } else if (encoderStatus < 0) {
                // unexpected status
                if (DEBUG)
                    Log.w(TAG, "drain:unexpected result from encoder#dequeueOutputBuffer: " + encoderStatus);
            } else {
                final ByteBuffer encodedData = encoderOutputBuffers[encoderStatus];
//                final ByteBuffer encodedData = mMediaCodec.getOutputBuffer(encoderStatus);
                if (encodedData == null) {
                    // this never should come...may be a MediaCodec internal error
                    throw new RuntimeException("encoderOutputBuffer " + encoderStatus + " was null");
                }
                if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
                    // You shoud set output format to muxer here when you target Android4.3 or less
                    // but MediaCodec#getOutputFormat can not call here(because INFO_OUTPUT_FORMAT_CHANGED don't come yet)
                    // therefor we should expand and looperPrepare output format from buffer data.
                    // This sample is for API>=18(>=Android 4.3), just ignore this flag here
                    if (DEBUG)
                        Log.d(TAG, mMediaTypeStr + "drain:BUFFER_FLAG_CODEC_CONFIG");
                    mBufferInfo.size = 0;
                }

                if (mBufferInfo.size != 0) {
                    // encoded data is ready, clear waiting counter
                    count = 0;
                    if (!mMuxerStarted) {
                        // muxer is not ready...this will prrograming failure.
                        throw new RuntimeException("drain:muxer hasn't started");
                    }
                    // write encoded data to muxer(need to adjust presentationTimeUs.
//                    prevOutputPTSUs = getPTSUs();
//                    prevOutputPTSUs = muxer.getPresentationTimeUs(mMediaType, getPTSUs());
                    if (mIsLiveEncoder && mLiveEncoderServer != null) {
                        prevOutputPTSUs = getPTSUs();
                        mBufferInfo.presentationTimeUs = prevOutputPTSUs;
                        mLiveEncoderServer.writeSampleData(encodedData, mBufferInfo);

                    } else if (muxer != null){
                        long timePTSUs = muxer.getPresentationTimeUs(mMediaType, getPTSUs());
                        if (timePTSUs <= prevOutputPTSUs) {
                            prevOutputPTSUs += prevPTSUsStep;
                        } else {
                            prevOutputPTSUs = timePTSUs;
                        }
                        // 更新时间戳，如果不更新，会导致显示的pts出错，导致偶然崩溃、预览进度条本不对的情况
                        mBufferInfo.presentationTimeUs = prevOutputPTSUs;
                        muxer.writeSampleData(mTrackIndex, encodedData, mBufferInfo);
                        if (mRecordingState == EncoderState.STOP) {
                            onEncodeEachFrame();
//                          mRecordingState = EncoderState.IDLE;
                        }
                    }
                }
                // return buffer to encoder
                mMediaCodec.releaseOutputBuffer(encoderStatus, false);
                if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                    // when EOS come.
                    mIsCapturing = false;
                    if (DEBUG) Log.d(TAG, mMediaTypeStr + "End Of Stream");
                    break;      // out of while
                }
            }
        }
    }

    public void onEncodeEachFrame() {

    }

    /**
     * previous presentationTimeUs for writing
     */
    protected long prevOutputPTSUs = 0;
    protected long prevPTSUsStep = 25;

    /**
     * get next encoding presentationTimeUs
     *
     * @return
     */
    protected long getPTSUs() {
        long result = System.nanoTime() / 1000L;
        // presentationTimeUs should be monotonic
        // otherwise muxer fail to write
        if (result <= prevOutputPTSUs) {
            result = prevOutputPTSUs + prevPTSUsStep;
        }
        return result;
    }

    public void releaseAll() {
        if (mThread != null) {
            try {
                mThread.interrupt();
            } catch (Throwable e) {
                e.printStackTrace();
            }
            mThread = null;
        }
        mListener = null;
    }
}
