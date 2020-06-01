package cn.poco.video.encoder;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.MediaFormat;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.IntBuffer;
import java.util.Date;

import cn.poco.gldraw2.core.EglCore;
import cn.poco.gldraw2.core.WindowSurface;

/**
 * Created by zwq on 2016/06/22 11:24.<br/><br/>
 * http://stackoverflow.com/questions/30211553/mediacodec-dequeueinputbuffer-illegalstateexception-on-sumsung-s3-android4-4
 * http://stackoverflow.com/questions/15305241/how-do-i-feed-h-264-nal-units-to-android-mediacodec-for-decoding
 * http://stackoverflow.com/questions/19742047/how-to-use-mediacodec-without-mediaextractor-for-h264
 * http://blog.csdn.net/jefry_xdz/article/details/8299901  码率
 * http://blog.csdn.net/yx_l128125/article/details/7593470
 * http://blog.csdn.net/cabbage2008/article/details/51273137
 */
@SuppressLint("NewApi")
public class MediaVideoEncoder extends MediaEncoder implements IFrameRenderer {

    private static final String TAG = "MediaVideoEncoder";
    private static final boolean DEBUG = false;    // TODO set false on release

    private static final String MIME_TYPE = "video/avc";

    // parameters for recording
    private static final int FRAME_RATE = 15;// 25 实际应用中很难达到25
    private static final int I_FRAME_INTERVAL = 2;//关键帧时间 2 seconds between I-frames
    private static final float BPP = 0.4f;
    private static int mColorFormat = MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface;

    private final int mVideoWidth;
    private final int mVideoHeight;

    private InputSurface mInputSurface;

    private EglCore mEglCore;
    private WindowSurface mRecordSurface;

    private int mViewportWidth, mViewportHeight;
    private int mViewportXoff, mViewportYoff;
    private final float mProjectionMatrix[] = new float[16];

    public MediaVideoEncoder(final MediaMuxerWrapper muxer, final MediaEncoderListener listener, final int width, final int height) {
        super(muxer, listener);
        if (DEBUG) Log.d(TAG, "MediaVideoEncoder: ");
        mMediaType = true;
        mMediaTypeStr = "video ";
        mVideoWidth = width;
        mVideoHeight = height;
    }

    /**
     * 直播助手编码
     * @param listener
     * @param width
     * @param height
     */
    public MediaVideoEncoder(final MediaEncoderListener listener, final int width, final int height) {
        super(null, listener, true);
        if (DEBUG) Log.d(TAG, "MediaVideoEncoder: ");
        mMediaType = true;
        mMediaTypeStr = "video ";
        mVideoWidth = width;
        mVideoHeight = height;
    }

    public int getVideoWidth() {
        return mVideoWidth;
    }

    public int getVideoHeight() {
        return mVideoHeight;
    }

    public void setEglCore(EglCore eglCore) {
        mEglCore = eglCore;
    }

    @Override
    public boolean prepare() throws IOException {
        if (mMediaCodec != null) {
            return false;
        }
        if (DEBUG) Log.d(TAG, "prepare: ");
        mTrackIndex = -1;
        mMuxerStarted = mIsEOS = false;

//        final MediaCodecInfo videoCodecInfo = selectVideoCodec(MIME_TYPE);
//        if (videoCodecInfo == null) {
////            Log.e(TAG, "Unable to find an appropriate codec for " + MIME_TYPE);
////            return false;
//            throw new NullPointerException("Unable to find an appropriate codec for " + MIME_TYPE);
//        }
//        if (DEBUG) Log.i(TAG, "selected codec: " + videoCodecInfo.getName());

        final MediaFormat format = MediaFormat.createVideoFormat(MIME_TYPE, mVideoWidth, mVideoHeight);
        format.setInteger(MediaFormat.KEY_COLOR_FORMAT, mColorFormat);    // API >= 18
        format.setInteger(MediaFormat.KEY_BIT_RATE, calculateBitRate());//1024 * 1024
        format.setInteger(MediaFormat.KEY_FRAME_RATE, FRAME_RATE);
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, I_FRAME_INTERVAL);
        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //format.setInteger(MediaFormat.KEY_CAPTURE_RATE, FRAME_RATE);
        //}
        //format.setInteger(MediaFormat.KEY_BITRATE_MODE, MediaCodecInfo.EncoderCapabilities.BITRATE_MODE_CBR);

        //http://blog.csdn.net/ameyume/article/details/6547923
        //format.setInteger(MediaFormat.KEY_PROFILE, MediaCodecInfo.CodecProfileLevel.AVCProfileHigh);
        //format.setInteger(MediaFormat.KEY_LEVEL, MediaCodecInfo.CodecProfileLevel.AVCLevel32);

//        byte[] header_sps = {0, 0, 0, 1, 103, 100, 0, 40, -84, 52, -59, 1, -32, 17, 31, 120, 11, 80, 16, 16, 31, 0, 0, 3, 3, -23, 0, 0, -22, 96, -108};
//        byte[] header_pps = {0, 0, 0, 1, 104, -18, 60, -128};
//        format.setByteBuffer("csd-0", ByteBuffer.wrap(header_sps));
//        format.setByteBuffer("csd-1", ByteBuffer.wrap(header_pps));
//        format.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, 1920 * 1080);
//        format.setInteger(MediaFormat.KEY_DURATION, 63446722);

//        byte[] csd_info = {0, 0, 0, 1, 103, 100, 0, 40, -84, 52, -59, 1, -32, 17, 31, 120, 11, 80, 16, 16, 31, 0, 0, 3, 3, -23, 0, 0, -22, 96, -108, 0, 0, 0, 1, 104, -18, 60, -128};
//        format.setByteBuffer("csd-0", ByteBuffer.wrap(csd_info));
////        format.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, 1920 * 1080);
//        format.setInteger(MediaFormat.KEY_DURATION, 63446722);

        if (DEBUG) Log.i(TAG, "format: " + format);

        try {
            mMediaCodec = MediaCodec.createEncoderByType(MIME_TYPE);
            //see detail:http://stackoverflow.com/questions/32238502/android-unable-to-instantiate-codec-video-avc
        } catch (Exception e) {
            e.printStackTrace();
            mMediaCodec = MediaCodec.createByCodecName("H264/AVC");
        }
        mMediaCodec.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        // get Surface for encoder input
        // this method only can call between #configure and #start

        try {
            if (mEglCore != null) {
                mRecordSurface = new WindowSurface(mEglCore, mMediaCodec.createInputSurface(), true);
                mEglCore = null;
            } else {
                mInputSurface = new InputSurface(mMediaCodec.createInputSurface());
            }
            mMediaCodec.start();
        } catch (Throwable e) {
            Log.e(TAG, "Something failed during recorder init: " + e);
            releaseMediaCodec(false);
            if (mListener != null) {
                mListener.onError(this, "init fail:" + e);
            }
            throw new RuntimeException("prepare fail");
        }

        if (DEBUG) Log.d(TAG, "prepare finishing");
        if (mListener != null) {
            try {
                mListener.onPrepared(this);
            } catch (final Throwable e) {
                e.printStackTrace();
                Log.e(TAG, "prepare:", e);
            }
        }
        return true;
    }

    @Override
    public void setViewSize(float width, float height) {
        super.setViewSize(width, height);
        configureViewport(width, height);
    }

    @Override
    protected void release() {
        if (DEBUG) Log.d(TAG, "release");
        super.release();
        if (mRecordSurface != null) {
            mRecordSurface.release();
            mRecordSurface = null;
        } else if (mInputSurface != null) {
            mInputSurface.release();
            mInputSurface = null;
        }
    }

    private int calculateBitRate() {
        final int bitrate = (int) (BPP * FRAME_RATE * mVideoWidth * mVideoHeight);
//        final int bitrate = (mVideoWidth * mVideoHeight * 3) * 2;
        if (DEBUG) Log.i(TAG, String.format("bitrate=%5.2f[Mbps]", bitrate / 1024f / 1024f));
        return bitrate;
    }

    /**
     * select the first codec that match a specific MIME type
     *
     * @param mimeType
     * @return null if no codec matched
     */
    protected static final MediaCodecInfo selectVideoCodec(final String mimeType) {
        if (DEBUG) Log.i(TAG, "selectVideoCodec:");

        // get the list of available codecs
        final int numCodecs = MediaCodecList.getCodecCount();
        for (int i = 0; i < numCodecs; i++) {
            final MediaCodecInfo codecInfo = MediaCodecList.getCodecInfoAt(i);

            if (!codecInfo.isEncoder()) {    // skipp decoder
                continue;
            }
            // select first codec that match a specific MIME type and color format
            final String[] types = codecInfo.getSupportedTypes();
            for (int j = 0; j < types.length; j++) {
                if (types[j].equalsIgnoreCase(mimeType)) {
                    if (DEBUG) Log.i(TAG, "codec:" + codecInfo.getName() + ",MIME=" + types[j]);
                    final int colorFormat = selectColorFormat(codecInfo, mimeType);
                    if (colorFormat > 0) {
                        mColorFormat = colorFormat;
                        return codecInfo;
                    }
                }
            }
        }
        return null;
    }

    /**
     * select color format available on specific codec and we can use.
     *
     * @return 0 if no colorFormat is matched
     */
    protected static final int selectColorFormat(final MediaCodecInfo codecInfo, final String mimeType) {
        if (DEBUG) Log.i(TAG, "selectColorFormat: ");
        int result = 0;
        final MediaCodecInfo.CodecCapabilities caps;
        try {
            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
            caps = codecInfo.getCapabilitiesForType(mimeType);
        } finally {
            Thread.currentThread().setPriority(Thread.NORM_PRIORITY);
        }
        int colorFormat;
        for (int i = 0; i < caps.colorFormats.length; i++) {
            colorFormat = caps.colorFormats[i];
            if (isRecognizedVideoFormat(colorFormat)) {
                if (result == 0)
                    result = colorFormat;
                break;
            }
        }
        if (result == 0)
            Log.e(TAG, "couldn't find a good color format for " + codecInfo.getName() + " / " + mimeType);
        return result;
    }

    /**
     * color formats that we can use in this class
     */
    protected static int[] recognizedFormats;

    static {
        recognizedFormats = new int[]{
//        	MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar,
//        	MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar,
//        	MediaCodecInfo.CodecCapabilities.COLOR_QCOM_FormatYUV420SemiPlanar,
                MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface,
        };
    }

    private static final boolean isRecognizedVideoFormat(final int colorFormat) {
        if (DEBUG) Log.i(TAG, "isRecognizedVideoFormat:colorFormat=" + colorFormat);
        final int n = recognizedFormats != null ? recognizedFormats.length : 0;
        for (int i = 0; i < n; i++) {
            if (recognizedFormats[i] == colorFormat) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void signalEndOfInputStream() {
        if (DEBUG) Log.d(TAG, "sending EOS to encoder");
        if (mMediaCodec != null) {
            try {
                mMediaCodec.signalEndOfInputStream();    // API >= 18
            } catch (Throwable t) {
                t.printStackTrace();
            }
            mIsEOS = true;
        }
    }

    private void configureViewport(float width, float height) {
        float arenaRatio = height / width;
        int x, y, viewWidth, viewHeight;

        if (mVideoHeight > (int) (mVideoWidth * arenaRatio)) {
            // limited by narrow width; restrict height
            viewWidth = mVideoWidth;
            viewHeight = (int) (mVideoWidth * arenaRatio);
        } else {
            // limited by short height; restrict width
            viewHeight = mVideoHeight;
            viewWidth = (int) (mVideoHeight / arenaRatio);
        }
        x = (mVideoWidth - viewWidth) / 2;
        y = (mVideoHeight - viewHeight) / 2;

        if (DEBUG) {
            Log.d(TAG, "configureViewport w=" + mVideoWidth + " h=" + mVideoHeight);
            Log.d(TAG, " --> x=" + x + " y=" + y + " gw=" + viewWidth + " gh=" + viewHeight);
        }

        mViewportXoff = x;
        mViewportYoff = y;
        mViewportWidth = viewWidth;
        mViewportHeight = viewHeight;

        Matrix.orthoM(mProjectionMatrix, 0, 0, width, 0, height, -1, 1);
    }

    @Override
    public void makeCurrent() {
        if (mRecordSurface != null) {
            mRecordSurface.makeCurrent();
        } else if (mInputSurface != null) {
            mInputSurface.makeCurrent();
        }
    }

    @Override
    public void setProjectionMatrix(float[] src) {
        System.arraycopy(src, 0, mProjectionMatrix, 0, mProjectionMatrix.length);
    }

    @Override
    public void setViewport() {
        GLES20.glViewport(mViewportXoff, mViewportYoff, mViewportWidth, mViewportHeight);
    }

    @Override
    public void swapBuffers() {
        if (mMediaCodec == null) {
            return;
        }
        frameAvailableSoon();
        if (mRecordSurface != null) {
            mRecordSurface.setPresentationTime(System.nanoTime());
            mRecordSurface.swapBuffers();
        } else if (mInputSurface != null) {
            mInputSurface.setPresentationTime(System.nanoTime());
            mInputSurface.swapBuffers();
        }
    }

    private boolean isCaptureFrame;

    public void setCaptureFrame(boolean captureFrame) {
        isCaptureFrame = captureFrame;
    }

    @Override
    public void onEncodeEachFrame() {
        super.onEncodeEachFrame();
        if (isCaptureFrame) {
            isCaptureFrame = false;
            try {
                saveFrame(bitmapDir + new Date().getTime() + ".png");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private IntBuffer mFrameBuf;
    private String bitmapDir = Environment.getExternalStorageDirectory() + "/111/";

    public void saveFrame(String filename) throws IOException {
         /*
         glReadPixels gives us a ByteBuffer filled with what is essentially big-endian RGBA
         data (i.e. a byte of red, followed by a byte of green...).  To use the Bitmap
         constructor that takes an int[] array with pixel data, we need an int[] filled
         with little-endian ARGB data.

         If we implement this as a series of buf.get() calls, we can spend 2.5 seconds just
         copying data around for a 720p frame.  It's better to do a bulk get() and then
         rearrange the data in memory.  (For comparison, the PNG compress takes about 500ms
         for a trivial frame.)

         So... we set the ByteBuffer to little-endian, which should turn the bulk IntBuffer
         get() into a straight memcpy on most Android devices.  Our ints will hold ABGR data.
         Swapping B and R gives us ARGB.  We need about 30ms for the bulk get(), and another
         270ms for the color swap.

         We can avoid the costly B/R swap here if we do it in the fragment shader (see
         http://stackoverflow.com/questions/21634450/ ).

         Having said all that... it turns out that the Bitmap#copyPixelsFromBuffer()
         method wants RGBA pixels, not ARGB, so if we create an empty bitmap and then
         copy pixel data in we can avoid the swap issue entirely, and just copy straight
         into the Bitmap from the ByteBuffer.

         Making this even more interesting is the upside-down nature of GL, which means
         our output will look upside-down relative to what appears on screen if the
         typical GL conventions are used.  (For ExtractMpegFrameTest, we avoid the issue
         by inverting the frame when we render it.)

         Allocating large buffers is expensive, so we really want mFrameBuf to be
         allocated ahead of time if possible.  We still get some allocations from the
         Bitmap / PNG creation.
         */
        if (mFrameBuf == null) {
            mFrameBuf = IntBuffer.allocate(mVideoWidth * mVideoHeight);
        }
        mFrameBuf.rewind();
        long startTime = System.currentTimeMillis();

        GLES20.glReadPixels(0, 0, mVideoWidth, mVideoHeight, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, mFrameBuf);//45
        Log.i("bbb", "total time:" + (System.currentTimeMillis() - startTime));
        mFrameBuf.rewind();
        //垂直翻转
        int[] srcBuf = mFrameBuf.array();
        int[] destBuf = new int[srcBuf.length];
        for (int i = 0; i < mVideoHeight; i++) {
            for (int j = 0; j < mVideoWidth; j++) {
//                for (int k = 0; k < 4; k++) {
//                    destBuf[(mViewHeight - 1 - i) * mViewWidth * 4 + j * 4 + k] = srcBuf[i * mViewWidth * 4 + j * 4 + k];
//                }
                destBuf[(mVideoHeight - 1 - i) * mVideoWidth + j] = srcBuf[i * mVideoWidth + j];//使用IntBuffer转换比较快
            }
        }
        Log.i("bbb", " 22 total time:" + (System.currentTimeMillis() - startTime));//30-40
        BufferedOutputStream bos = null;
        try {
            bos = new BufferedOutputStream(new FileOutputStream(filename));
            Bitmap bmp = Bitmap.createBitmap(mVideoWidth, mVideoHeight, Bitmap.Config.ARGB_8888);
            bmp.copyPixelsFromBuffer(IntBuffer.wrap(destBuf));
            bmp.compress(Bitmap.CompressFormat.PNG, 100, bos);
            bmp.recycle();
        } finally {
            if (bos != null) {
                bos.close();
            }
        }
    }
}
