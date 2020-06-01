package cn.poco.gldraw;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.opengl.EGL14;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLSurface;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.Build;
import android.util.Log;

import java.nio.IntBuffer;
import java.util.Locale;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import cn.poco.glfilter.base.GlUtil;
import cn.poco.glfilter.sticker.OnDrawStickerResListener;
import cn.poco.resource.FilterRes;
import cn.poco.video.encoder.MediaMuxerWrapper;
import cn.poco.video.encoder.MediaVideoEncoder;
import cn.poco.video.encoder.RecordState;

@SuppressLint("NewApi")
public class CameraRecordRenderer implements GL10SurfaceView.Renderer {
    private static final String TAG = "bbb";

    private Context mContext;
    private CameraGLSurfaceView.CameraHandler mCameraHandler;
    private int mPatchDegree = 90;
    private boolean mIsFront;
    private boolean mBeautyEnable;
    private boolean mStickerEnable;

    private int mTextureId = GlUtil.NO_TEXTURE;
    private FullFrameRectV3 mFullScreen;
    private SurfaceTexture mSurfaceTexture;
    private final float[] mSTMatrix = new float[16];
    private final float[] projectionMatrix = new float[16];
    private final float[] viewMatrix = new float[16];
    private final float[] viewProjectionMatrix = new float[16];

    private EGLDisplay mSavedEglDisplay;
    private EGLSurface mSavedEglDrawSurface;
    private EGLSurface mSavedEglReadSurface;
    private EGLContext mSavedEglContext;

    private MediaMuxerWrapper mMediaMuxerWrapper;
    private MediaVideoEncoder mMediaVideoEncoder;

    private float mMvpScaleX = 1.0f, mMvpScaleY = 1.0f;
    private int mSurfaceWidth, mSurfaceHeight;
    private int mVideoWidth, mVideoHeight;

    private int mRecordState;
    private int mLastRecordState;
    private boolean mDrawEnding;
    private IntBuffer mFrameBuf;
    private OnCaptureFrameListener mOnCaptureFrameListener;
    private int mFrameTopPadding;
    private boolean mCaptureGifFrameEnable;
    private int mInvalidFrameCount;//gif录制时前几帧无效

    private boolean mUseCustomSurfaceView;
    private boolean mUseGLFinish;

    public CameraRecordRenderer(Context context, CameraGLSurfaceView.CameraHandler cameraHandler) {
        mContext = context;
        mCameraHandler = cameraHandler;

        String model = Build.MODEL.toUpperCase(Locale.CHINA);
        if (model.equals("OPPO R9TM")) {
            mUseGLFinish = true;
        }
    }

    public void setMediaMuxerWrapper(MediaMuxerWrapper mediaMuxerWrapper) {
        mMediaMuxerWrapper = mediaMuxerWrapper;
        if (mMediaMuxerWrapper != null) {
            mMediaVideoEncoder = (MediaVideoEncoder) mMediaMuxerWrapper.getVideoEncoder();
        }
    }

    public void setCameraPreviewSize(int width, int height) {
        if (mFullScreen != null) {
            float surfaceRatio = mSurfaceHeight * 1.0f / mSurfaceWidth;
            float targetHeight = mSurfaceWidth * (height * 1.0f / width);
            float surfaceHeight = mSurfaceHeight;
            if (targetHeight / mSurfaceWidth != surfaceRatio) {
                targetHeight = mSurfaceHeight;
            }
            mMvpScaleX = 1.0f;
            mMvpScaleY = targetHeight / surfaceHeight;
            mFullScreen.scaleMVPMatrix(mMvpScaleX, mMvpScaleY);
        }
    }

    public void setCameraSize(int width, int height) {
        if (mFullScreen != null) {
            mFullScreen.setCameraSize(width, height);
        }
    }

    public void runOnGLThread() {
        if (mFullScreen != null) {
            mFullScreen.runOnGLThread();
        }
    }

    public void setPreviewDegree(int degree, boolean isFront) {
        mPatchDegree = degree;
        mIsFront = isFront;
        if (mFullScreen != null) {
            mFullScreen.setPreviewDegree(mPatchDegree, mIsFront);
        }
    }

    public void setBeautyEnable(boolean enable) {
        mBeautyEnable = enable;
        if (mFullScreen != null) {
            mFullScreen.setBeautyEnable(enable);
        }
    }

    public void setFaceAdjustEnable(boolean enable) {
        if (mFullScreen != null) {
            mFullScreen.setFaceAdjustEnable(enable);
        }
    }

    public void setStickerEnable(boolean enable) {
        mStickerEnable = enable;
        if (mFullScreen != null) {
            mFullScreen.setStickerEnable(enable);
        }
    }

    public void setShapeEnable(boolean enable) {
        if (mFullScreen != null) {
            mFullScreen.setShapeEnable(enable);
        }
    }

    public void setShapeEnable(boolean enable, boolean canDraw) {
        if (mFullScreen != null) {
            mFullScreen.setShapeEnable(enable);
        }
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        onSurfaceCreated(null);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        onSurfaceChanged(width, height);
        mUseCustomSurfaceView = false;
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        onDrawFrame();
    }

    public void onSurfaceCreated(android.opengl.EGLConfig config) {
//        Log.i(TAG, "onSurfaceCreated");
        Matrix.setIdentityM(mSTMatrix, 0);
        mFullScreen = new FullFrameRectV3(mContext);
        mFullScreen.initFilter();
        mFullScreen.setPreviewDegree(mPatchDegree, mIsFront);
        mFullScreen.setBeautyEnable(mBeautyEnable);
        mFullScreen.setStickerEnable(mStickerEnable);
        mTextureId = mFullScreen.createTexture();
        mSurfaceTexture = new SurfaceTexture(mTextureId);
        mCameraHandler.obtainMessage(CameraGLSurfaceView.ON_SURFACE_CREATE, mSurfaceTexture).sendToTarget();
    }

    public void onSurfaceChanged(int width, int height) {
//        Log.i(TAG, "onSurfaceChanged width:" + width + ", height:" + height);
        mUseCustomSurfaceView = true;
        mSurfaceWidth = width;
        mSurfaceHeight = height;

        GLES20.glViewport(0, 0, width, height);

        float mRatio = (float) width / height;
        int offset = 0;
        float left = -mRatio;
        float right = mRatio;
        float bottom = -1f;
        float top = 1f;
        float near = 3f;
        float far = 7f;
        Matrix.frustumM(projectionMatrix, offset, left, right, bottom, top, near, far);

        // Set the camera position (View matrix)
        int rmOffset = 0;
        float eyeX = 0.0f;
        float eyeY = 0.0f;
        float eyeZ = 3f;
        float centerX = 0f;
        float centerY = 0f;
        float centerZ = 0f;
        float upX = 0f;
        float upY = 1.0f;
        float upZ = 0.0f;
        Matrix.setLookAtM(viewMatrix, rmOffset, eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ);
        Matrix.multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
        mFullScreen.setMVPMatrix(viewProjectionMatrix);
        mFullScreen.scaleMVPMatrix(mMvpScaleX, mMvpScaleY);
        mFullScreen.setViewSize(width, height);
        mCameraHandler.obtainMessage(CameraGLSurfaceView.START_CAMERA_PREVIEW, width, height).sendToTarget();

        mDrawCount = 0;
    }

    private int mDrawCount;

    public boolean onDrawFrame() {
//        Log.i(TAG, "onDrawFrame");
        if (mDrawCount == 0 && mSurfaceTexture != null) {
            try {
                mSurfaceTexture.updateTexImage();
            } catch (Exception e) {
                e.printStackTrace();
                mDrawCount = 0;
                return false;
            }
        }
//        if (mDrawCount != 0 && mCameraHandler != null) {
//            mCameraHandler.sendEmptyMessage(CameraGLSurfaceView.REQUEST_RENDER);
//        }
//        mDrawCount = (mDrawCount + 1) % 2;

        if (mUseCustomSurfaceView) {
            renderDraw2();
        } else {
            renderDraw();
        }
        return true;
    }

    public void renderDraw() {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

//        long t = System.currentTimeMillis();
        if (mSurfaceTexture != null) {
            mSurfaceTexture.getTransformMatrix(mSTMatrix);
        }
        if (mFullScreen != null) {
            mFullScreen.setDrawType(false);
            mFullScreen.setIsDrawCache(false);
            mFullScreen.drawFrame(mTextureId, mSTMatrix);
        }

//        long t2 = System.currentTimeMillis();
        boolean result = onDrawVideoFrame(mTextureId, mSTMatrix, mUseGLFinish, true);
//        long t3 = System.currentTimeMillis();

        if (result && !mUseGLFinish) {
            mFullScreen.setDrawType(false);
            mFullScreen.setIsDrawCache(true);
            mFullScreen.drawFrame(mTextureId, mSTMatrix);
        }

        onCaptureAFrame();

//        long t4 = System.currentTimeMillis();
//        Log.i(TAG, "onDrawVideoFrame: preview:" + (t2 - t) + ", " + (t3 - t2) + ", " + (t4 - t3) + ", all:" + (t4 - t));

        mFullScreen.loadNextTexture(true);
    }

    public void renderDraw2() {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        GLES20.glEnable(GLES20.GL_SCISSOR_TEST);//裁剪测试
        GLES20.glScissor(0, 0, mSurfaceWidth, mSurfaceHeight);
        if (mSurfaceTexture != null) {
            mSurfaceTexture.getTransformMatrix(mSTMatrix);
        }
        if (mFullScreen != null) {
            mFullScreen.setDrawType(false);
            mFullScreen.setIsDrawCache(false);
            mFullScreen.drawFrame(mTextureId, mSTMatrix);
        }
        GLES20.glDisable(GLES20.GL_SCISSOR_TEST);

        onCaptureAFrame();
    }

    public boolean onDrawOffscreenFrame() {
        boolean result = onDrawVideoFrame(mTextureId, mSTMatrix, false, false);
        if (mFullScreen != null) {
            mFullScreen.loadNextTexture(true);
        }
        return result;
    }

    public void updatePreviewFrame(byte[] previewData, int width, int height) {
        if (mFullScreen != null) {
            mFullScreen.updatePreviewFrame(previewData, width, height);
        }
    }

    public void resetFilterData() {
        if (mFullScreen != null) {
            mFullScreen.resetFilterData();
        }
    }

    /**
     * 美形参数设置
     *
     * @param beauty      美颜
     * @param thinFace    瘦脸
     * @param bigEye      大眼
     * @param shrinkNose  瘦鼻
     * @param whitenTeeth 美牙
     */
    public void setBeautifyParams(float beauty, float thinFace, float bigEye, float shrinkNose, float whitenTeeth) {
        if (mFullScreen != null) {
            mFullScreen.setBeautifyParams(beauty, thinFace, bigEye, shrinkNose, whitenTeeth);
        }
    }

    public void changeCameraFilter(int filterType) {
        if (mFullScreen != null) {
            mFullScreen.changeCameraFilter(filterType);
        }
    }

    @Deprecated
    public void changeColorFilter(int filterType) {
        if (mFullScreen != null) {
            mFullScreen.changeColorFilter(filterType);
        }
    }

    public void changeColorFilter(FilterRes filterRes) {
        if (mFullScreen != null) {
            mFullScreen.changeColorFilter(filterRes);
        }
    }

    public void setRatioAndOrientation(float width_height_ratio, int flip, int upCut) {
        if (mFullScreen != null) {
            mFullScreen.setRatioAndOrientation(width_height_ratio, flip, upCut);
        }
    }

    public void changeShapeFilter(int filterType) {
        if (mFullScreen != null) {
            mFullScreen.changeShapeFilter(filterType);
        }
    }

    /**
     * Saves the current projection matrix and EGL state.
     */
    private void saveRenderState() {
        mSavedEglDisplay = EGL14.eglGetCurrentDisplay();
        mSavedEglDrawSurface = EGL14.eglGetCurrentSurface(EGL14.EGL_DRAW);
        mSavedEglReadSurface = EGL14.eglGetCurrentSurface(EGL14.EGL_READ);
        mSavedEglContext = EGL14.eglGetCurrentContext();
    }

    /**
     * Saves the current projection matrix and EGL state.
     */
    private void restoreRenderState() {
        // switch back to previous state
        if (!EGL14.eglMakeCurrent(mSavedEglDisplay, mSavedEglDrawSurface, mSavedEglReadSurface, mSavedEglContext)) {
            throw new RuntimeException("eglMakeCurrent failed");
        }
    }

    public void setRecordState(int state) {
        mLastRecordState = mRecordState;
//        Log.i(TAG, "setRecordState mLastRecordState:"+mLastRecordState+", state:"+state+" mMediaMuxerWrapper:"+(mMediaMuxerWrapper == null?"null":"not null"));
        switch (state) {
            case RecordState.IDLE:
                mRecordState = RecordState.IDLE;
                break;
            case RecordState.PREPARE:
                if (mRecordState == RecordState.IDLE) {
                    mRecordState = RecordState.PREPARE;
                }
                break;
            case RecordState.WAIT:
                break;
            case RecordState.START:
                if (mRecordState == RecordState.WAIT) {
                    mRecordState = RecordState.START;
                }
                break;
            case RecordState.RESUME:
                if (mRecordState == RecordState.PAUSE) {
                    mRecordState = RecordState.RESUME;
                }
                break;
            case RecordState.RECORDING:
                break;
            case RecordState.PAUSE:
                if (mRecordState == RecordState.RECORDING) {
                    mRecordState = RecordState.PAUSE;
                }
                break;
            case RecordState.STOP:
                if (mRecordState == RecordState.RECORDING) {
                    mRecordState = RecordState.STOP;
                }
                break;
            case RecordState.CAPTURE_A_FRAME:
                if (mRecordState == RecordState.IDLE || mRecordState == RecordState.WAIT) {
                    mRecordState = RecordState.CAPTURE_A_FRAME;
                }
                break;
        }
    }

    public void restoreRecordState() {
        mRecordState = mLastRecordState;
    }

    public void setDrawEndingEnable(boolean enable) {
        mDrawEnding = enable;
    }

    public void setOnCaptureFrameListener(OnCaptureFrameListener listener) {
        mOnCaptureFrameListener = listener;
    }

    public void setOnDrawStickerResListener(OnDrawStickerResListener listener) {
        if (mFullScreen != null) {
            mFullScreen.setOnDrawStickerResListener(listener);
        }
    }

    public void setFrameTopPadding(int topPadding) {
        mFrameTopPadding = topPadding;
    }

    public void setCaptureGifFrameEnable(boolean enable) {
        mCaptureGifFrameEnable = enable;
    }

    private int mFrameCount;
    private long mStartRecordTime = -1;
    private int mFps = 10;
    private long mEndingTimeLong = 2000;
    private int mEndingFrameCount = 10;
    private Bitmap mLastFrame;

    private boolean onDrawVideoFrame(int textureId, final float[] texMatrix, boolean useGLFinish, boolean changeRenderState) {
        if (mRecordState == RecordState.IDLE) {
            return false;
        }
        if (mMediaMuxerWrapper != null && mMediaVideoEncoder != null) {
            mVideoWidth = mMediaVideoEncoder.getVideoWidth();
            mVideoHeight = mMediaVideoEncoder.getVideoHeight();
            /*if (mStartRecordTime != -1 && mInvalidFrameCount > 0) {
                long rt = System.currentTimeMillis() - mStartRecordTime;
                if (rt >= 10000) {
                    mRecordState = RecordState.STOP;
                }
            }*/
            if (mRecordState == RecordState.PREPARE && !mMediaMuxerWrapper.isPrepared()) {
//                Log.i(TAG, "onDrawVideoFrame prepare " + mSurfaceWidth + ", " + mSurfaceHeight);
                boolean prepareSuccess = true;
                try {
                    mMediaMuxerWrapper.prepare();
                } catch (Exception e) {
                    e.printStackTrace();
                    mMediaMuxerWrapper.setPrepared(false);
                    prepareSuccess = false;
                }
                if (prepareSuccess) {
                    mMediaMuxerWrapper.setViewSize(mSurfaceWidth, mSurfaceHeight);//480,640
                    mRecordState = RecordState.WAIT;
                }
                mInvalidFrameCount = 0;
            }
            if (mRecordState == RecordState.START) {
//                Log.i(TAG, "onDrawVideoFrame startRecording");
                mMediaMuxerWrapper.startRecording();
                mRecordState = RecordState.RECORDING;

                mFrameCount = 0;
                mStartRecordTime = System.currentTimeMillis();
            } else if (mRecordState == RecordState.RESUME) {
                mMediaMuxerWrapper.resumeRecording();
                mRecordState = RecordState.RECORDING;

            } else if (mRecordState == RecordState.PAUSE) {
                mMediaMuxerWrapper.pauseRecording();

            } else if (mRecordState == RecordState.ENDING) {
//                Log.i(TAG, "onDrawVideoFrame ending");
                if (mFrameCount == mEndingFrameCount) {
//                    if (mStartRecordTime != -1) {
//                        int mFps = (int) (mFrameCount * 1000f / (System.currentTimeMillis() - mStartRecordTime) + 0.5f);
//                        Log.i(TAG, "onDrawVideoFrame stop, ending mFps:"+mFps+", time:"+(System.currentTimeMillis() - mStartRecordTime));
//                        mStartRecordTime = -1;
//                    }
                    mFrameCount = 0;
                    mDrawEnding = false;
                    mRecordState = RecordState.STOP;
                    if (mLastFrame != null && !mLastFrame.isRecycled()) {
                        mLastFrame.recycle();
                    }
                    mLastFrame = null;
                }
            }
            if (mRecordState == RecordState.STOP) {
//            Log.i(TAG, "onDrawVideoFrame stop");
               /* onCaptureAFrame(0, mSurfaceWidth, mSurfaceHeight, 0, new OnCaptureFrameListener() {
                    @Override
                    public void onCaptureFrame(int type, IntBuffer data, int width, int height) {
//                        mLastFrame = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
//                        mLastFrame.copyPixelsFromBuffer(data);
                        if (mOnCaptureFrameListener != null) {
                            mOnCaptureFrameListener.onCaptureFrame(type, data, width, height);
                        }
                    }
                });*/
//                Log.i(TAG, "onDrawVideoFrame: mFrameCount:"+mFrameCount+", time:"+(System.currentTimeMillis() - mStartRecordTime));
                if (!mDrawEnding) {
                    mMediaMuxerWrapper.stopRecording();
                    mRecordState = RecordState.IDLE;
                } else {
                    mRecordState = RecordState.ENDING;

                    if (mStartRecordTime != -1) {
                        mFps = (int) (mFrameCount * 1000f / (System.currentTimeMillis() - mStartRecordTime) + 0.5f);
                    }
                    if (mFps < 6) {
                        mFps = 6;
                    }
                    mEndingFrameCount = Math.round(mFps * (mEndingTimeLong / 1000.0f) / 3 + 0.5f);//片尾帧数
                    mFrameCount = 0;
                    mStartRecordTime = -1;
//                    mStartRecordTime = System.currentTimeMillis();
//                    Log.i(TAG, "onDrawVideoFrame start draw ending, mFps:"+mFps+", mEndingFrameCount:"+mEndingFrameCount);
//
//                    onCaptureAFrame(true, true, new OnCaptureFrameListener() {
//                        @Override
//                        public void onCaptureFrame(boolean isVideoFrame, IntBuffer data, int width, int height) {
//                            mLastFrame = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
//                            mLastFrame.copyPixelsFromBuffer(data);
//                            if (mOnCaptureFrameListener != null) {
//                                mOnCaptureFrameListener.onCaptureFrame(isVideoFrame, data, width, height);
//                            }
//                        }
//                    });
                }
//                onCaptureAFrame(0, mSurfaceWidth, mSurfaceHeight, mFrameTopPadding, mOnCaptureFrameListener);
                return false;
            }
            if ((mRecordState == RecordState.RECORDING || mRecordState == RecordState.ENDING) && mMediaMuxerWrapper.canRecord() && mMediaVideoEncoder != null) {
//                if (mCaptureGifFrameEnable) {
//                    mInvalidFrameCount++;
//                    if (mInvalidFrameCount < 3) {
//                        return false;
//                    }
//                }
//                Log.i(TAG, "onDrawVideoFrame Recording");
                if (useGLFinish) {
                    GLES20.glFinish();
                }
                if (changeRenderState) {
                    saveRenderState();
                }
//                long t = System.currentTimeMillis();

                // switch to recorder state
                try {
                    mMediaVideoEncoder.makeCurrent();//耗时，影响录制
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i(TAG, "recording-->mMediaVideoEncoder makeCurrent fail");
                    if (changeRenderState) {
                        restoreRenderState();
                        return false;
                    }
                    return true;
                }
//                long t2 = System.currentTimeMillis();

                mMediaVideoEncoder.setProjectionMatrix(texMatrix);
                mMediaVideoEncoder.setViewport();
                // render everything again
                mFullScreen.setVideoSize(mVideoWidth, mVideoHeight, mFrameTopPadding);
                mFullScreen.setDrawType(true);
                mFullScreen.setIsDrawCache(true);
                if (mDrawEnding && mRecordState == RecordState.ENDING) {
                    mFullScreen.setDrawEnding(true);
                    mFullScreen.setEndingFrameCount(mEndingFrameCount, mLastFrame);
                }
                mFullScreen.drawFrame(textureId, texMatrix);

//                long t3 = System.currentTimeMillis();
                //capture gif frame
                if (mCaptureGifFrameEnable) {
                    onCaptureAFrame(2, mVideoWidth, mVideoHeight, 0, mOnCaptureFrameListener);
                }
//                long t4 = System.currentTimeMillis();
                mMediaVideoEncoder.swapBuffers();//耗时，影响录制
                mFrameCount++;

//                long t5 = System.currentTimeMillis();
                if (changeRenderState) {
                    restoreRenderState();
                }

//                Log.i(TAG, "onDrawVideoFrame: " + (t2 - t) + ", " + (t3 - t2) + ", " + (t4 - t3) + ", " + (t5 - t4) + ", total:" + (t5 - t));
                return true;
            }
        }
        return false;
    }

    public interface OnCaptureFrameListener {
        /**
         * @param frameType 0：视频截屏，1：拍照截屏，2：gif截屏
         * @param data
         * @param width
         * @param height
         */
        void onCaptureFrame(int frameType, IntBuffer data, int width, int height);
    }

    private void onCaptureAFrame() {
        if (mRecordState == RecordState.CAPTURE_A_FRAME) {
            restoreRecordState();
            onCaptureAFrame(1, mSurfaceWidth, mSurfaceHeight, 0, mOnCaptureFrameListener);
        }
    }

    private void onCaptureAFrame(int frameType, int srcWidth, int srcHeight, int topPadding, OnCaptureFrameListener listener) {
        try {
            if (mFrameBuf == null) {
                mFrameBuf = IntBuffer.allocate(srcWidth * srcHeight);
            }
            if (mFrameBuf.capacity() != srcWidth * srcHeight) {
                mFrameBuf.clear();
                mFrameBuf = IntBuffer.allocate(srcWidth * srcHeight);
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
        if (mFrameBuf != null) {
            mFrameBuf.rewind();
            GLES20.glReadPixels(0, topPadding, srcWidth, srcHeight, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, mFrameBuf);//avg:300ms
            mFrameBuf.rewind();

            if (listener != null) {
                listener.onCaptureFrame(frameType, mFrameBuf, srcWidth, srcHeight);
            }
        }
//        int[] srcBuf = mFrameBuf.array();
//        boolean needScale = false;
//        if (needScale) {
//            //缩放
//            srcBuf = scale(srcBuf, srcWidth, srcHeight, destWidth, destHeight);
//            srcWidth = destWidth;
//            srcHeight = destHeight;
//        }
//        //垂直翻转
//        int flip = 0;
//        if (flip == 1) {
//            int[] destBuf = new int[srcBuf.length];
//            for (int i = 0; i < srcHeight; i++) {
//                for (int j = 0; j < srcWidth; j++) {
//                    destBuf[(srcHeight - 1 - i) * srcWidth + j] = srcBuf[i * srcWidth + j];//使用IntBuffer转换比较快
//                }
//            }
//            srcBuf = destBuf;
//        }
//        if (listener != null) {
//            listener.onCaptureFrame(type, IntBuffer.wrap(srcBuf), srcWidth, srcHeight);
//        }
    }

    private int[] scale(int[] src, int srcW, int srcH, int destW, int destH) {
        float stepX = srcW * 1.0f / destW;
        float stepY = srcH * 1.0f / destH;
        int[] scaleData = new int[destW * destH];
        int m = 0, n;
        for (float y = 0; y < srcH && m < destH; y += stepY, m++) {
            n = 0;
            for (float x = 0; x < srcW && n < destW; x += stepX, n++) {
                scaleData[m * destW + n] = src[Math.round(y) * srcW + Math.round(x)];
            }
        }
        return scaleData;
    }

    public void notifyPausing() {
        if (mSurfaceTexture != null) {
            mSurfaceTexture.release();
            mSurfaceTexture = null;
        }
        if (mFullScreen != null) {
            mFullScreen.release(true);     // assume the GLSurfaceView EGL context is about
            mFullScreen = null;             // to be destroyed
        }
    }

    public void clearAll() {
        notifyPausing();
        mContext = null;
        mCameraHandler = null;
        mMediaMuxerWrapper = null;
        mMediaVideoEncoder = null;
        mFrameBuf = null;
        mOnCaptureFrameListener = null;
    }
}