package cn.poco.gldraw2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.Build;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;

import java.io.File;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import cn.poco.camera2.CameraHandler;
import cn.poco.camera2.CameraSurface;
import cn.poco.camera2.CameraThread;
import cn.poco.camera2.ScreenOrientationChangeListener;
import cn.poco.gldraw.VideoRendererTexture;
import cn.poco.gldraw2.core.EglCore;
import cn.poco.gldraw2.core.EglCoreAbs;
import cn.poco.gldraw2.core.EglSurfaceAbs;
import cn.poco.gldraw2.core.OffscreenSurface;
import cn.poco.gldraw2.core.WindowSurface;
import cn.poco.gldraw2.core.compat.CompatEglCore;
import cn.poco.gldraw2.core.compat.CompatOffscreenSurface;
import cn.poco.gldraw2.core.compat.CompatWindowSurface;
import cn.poco.glfilter.base.GlUtil;
import cn.poco.image.PocoFace;
import cn.poco.image.PocoFaceTracker;
import cn.poco.utils.CpuUtils;
import cn.poco.video.encoder.MediaEncoder;
import cn.poco.video.encoder.MediaMuxerWrapper;
import cn.poco.video.encoder.MediaVideoEncoder;
import cn.poco.video.encoder.RecordState;

/**
 * Created by zwq on 2017/07/24 11:48.<br/><br/>
 */
@SuppressLint("NewApi")
public class RenderThread extends Thread implements SurfaceTexture.OnFrameAvailableListener, Camera.PreviewCallback, ScreenOrientationChangeListener {

    public interface OnReadyCallback {
        void onReady();
    }

    private static final String TAG = "vvv RenderThread";
    private final boolean mIsDebug = false;
    private final boolean mShowDrawTime = false;
    private final boolean mShowFaceTrackerTime = false;
    private final boolean mShowTakePictureTime = false;
    private long mTrackerStartTime;

    private Context mContext;
    private SurfaceHolder mSurfaceHolder;
    private OnReadyCallback mOnReadyCallback;
    private boolean mRenderReady;

    private boolean mIsIndependentThread;//是否是独立线程
    private Object mLockObject = new Object();
    private boolean mReady = false;
    private boolean mIsQuitThread;

//    private ArrayList<RenderRunnable> mEventQueue = new ArrayList<RenderRunnable>();
    private HashMap<Integer, RenderRunnable> mEventQueue = new HashMap<>();
    private int mQueueEventCount;

    private RenderHandler mRenderHandler;
    private boolean mWindowSurfaceCreateSuccess;
    private boolean mUseCompat;
    private EglCoreAbs mEglCore;
    private EglSurfaceAbs mWindowSurface;//WindowSurface
    private EglSurfaceAbs mOffscreenSurface;//OffscreenSurface

    /**
     * 0: use Offscreen after swapBuffers,
     * 1: use Offscreen before swapBuffers,
     * 2: other
     */
    private int mTakePictureType;
    private String mMenufacturer;
    private String mModel;
    /**
     * 1:华为(4/6核:11)、2:高通(4/6核:21)、3:MTK(4/6核:31)
     */
    private int mCpuLevel;

    private int mTextureName;
    private SurfaceTexture mSurfaceTexture;
    private int mSurfaceWidth, mSurfaceHeight;
    private float mSurfaceRatio;
    private boolean mSurfaceSizeIsChange;
    private float mRenderScale = 1.0f;

    private int mRenderMode;
    private boolean mIsPauseState;
    private int mSkipFrameCount = -1;
    private int mDirtyFrameCount;
    private boolean mResetTracker;

    private boolean mCanDraw;
    private boolean mSwapResult;

    private float[] mSTMatrix = new float[16];
    private float mMvpScaleX = 1.0f, mMvpScaleY = 1.0f;
    private boolean mInitFilterSuccess;
    private RenderFilterManager mFilterManager;

    private DetectFaceCallback mDetectFaceCallback;
    private MediaMuxerWrapper mMediaMuxerWrapper;
    private MediaVideoEncoder mMediaVideoEncoder;
    private boolean mIsLiveEncoder;
    private int mVideoWidth, mVideoHeight;

    private int mRecordState;
    private int mLastRecordState;
    private boolean mDrawEnding;
    private IntBuffer mFrameBuf;
    private OnCaptureFrameListener mOnCaptureFrameListener;

    private int mFrameTopPadding;
    private boolean mCaptureGifFrameEnable;
    private int mInvalidFrameCount;//gif录制时前几帧无效
    private boolean mWaterMarkHasDate;
    private int mOrientation;

    private int mFrameCount;
    private long mStartRecordTime = -1;
    private int mFps = 10;
    private long mEndingTimeLong = 2000;
    private int mEndingFrameCount = 10;
    private Bitmap mLastFrame;

    private boolean mRenderVideoEnable;
    private VideoRendererTexture mVideoRendererTexture;

    public RenderThread(Context context, SurfaceHolder surfaceHolder) {
        mContext = context;
        mSurfaceHolder = surfaceHolder;

        initData();
    }

    public void setOnReadyCallback(OnReadyCallback onReadyCallback) {
        mOnReadyCallback = onReadyCallback;
        mRenderReady = false;
    }

    private void initData() {
        mTakePictureType = 0;
        mMenufacturer = Build.MANUFACTURER.toUpperCase(Locale.CHINA);
        mModel = Build.MODEL.toUpperCase(Locale.CHINA);
        //Log.i(TAG, "RenderThread: menufacturer:" + menufacturer + ", model:" + model);
        //mModel -> OPPO R9TM
        if ("HUAWEI".equals(mMenufacturer)) {//华为
            //有些机型在 swapBuffers 之后 glReadPixels 取不到数据
            mTakePictureType = 1;
        }
        /*if ("NEM-AL10".equals(model)) {//华为荣耀5C
            mTakePictureType = 2;
        }*/

        mTakePictureType = 2;
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {//兼容低于20的版本
            //GL 2.0
            mUseCompat = true;
        } else {
            mUseCompat = false;
        }

        CpuUtils.CpuInfo cpuInfo = CpuUtils.getCpuInfo();
        if (cpuInfo != null && cpuInfo.mHardware != null) {
            try {
                if (cpuInfo.mHardware.contains("HI")) {//test
                    mCpuLevel = 1;

                } else if (cpuInfo.mHardware.contains("MSM")) {
                    mCpuLevel = 2;

                } else if (cpuInfo.mHardware.contains("MT")) {
                    mCpuLevel = 3;
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
            if (mCpuLevel == 3 && cpuInfo.mProcessorCount < 6) {
                mCpuLevel = (mCpuLevel * 10) + 1;
            }
        }
    }

    @Override
    public void run() {
        mIsIndependentThread = true;
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_DISPLAY);

        Looper.prepare();
        onLooperPrepared();

        Looper.loop();
        onLooperQuited();
        mIsIndependentThread = false;
    }

    public void onLooperPrepared() {
        mRenderHandler = new RenderHandler(this);

        //create EGL context and everything else here
        if (mUseCompat) {
            mEglCore = new CompatEglCore(/*null, CompatEglCore.FLAG_TRY_GLES3*/);
        } else {
            // FLAG_RECORDABLE : 告诉EGL它创建的surface必须和视频编解码器兼容。没有这个标志，EGL可能会使用一个MediaCodec不能理解的Buffer
            mEglCore = new EglCore(null, EglCore.FLAG_RECORDABLE | EglCore.FLAG_TRY_GLES3);
        }
        synchronized (mLockObject) {
            mReady = true;
            mLockObject.notify();
        }

        if (mIsDebug) {
            Log.d(TAG, "Render thread looper started");
        }
    }

    public void onLooperQuited() {
        releaseGL();
        clearAll();
        if (mEglCore != null) {
            mEglCore.release();
            mEglCore = null;
        }
        if (mRenderHandler != null) {
            mRenderHandler.clearAll();
        }
        mRenderHandler = null;
        if (mIsDebug) {
            Log.d(TAG, "Render thread looper finished");
        }

        synchronized (mLockObject) {
            mReady = false;
        }
        mLockObject = null;
    }

    // call this from the UI thread - wait until the handler is ready.
    // Creating the handler should be very fast, but this doesn't hurt.
    public void waitUntilReady() {
        synchronized (mLockObject) {
            while (!mReady) {
                try {
                    mLockObject.wait();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void quitThread(boolean quitLooper) {
        mIsQuitThread = true;
        releaseBeforeLoopQuit();
        if (quitLooper) {
            Looper.myLooper().quit();
        }
    }

    public void queueEvent(Integer target, RenderRunnable runnable) {
        if (!mIsQuitThread && target != null && runnable != null) {
            if (mEventQueue == null) {
                mEventQueue = new HashMap<>();
            }
            synchronized (mEventQueue) {
                if (mEventQueue.containsKey(target)) {
                    mEventQueue.remove(target);
                }
                mEventQueue.put(target, runnable);
            }
        }
    }


    public void queueEvent(RenderRunnable runnable) {
        if (!mIsQuitThread && runnable != null) {
            /*if (mEventQueue == null) {
                mEventQueue = new ArrayList<RenderRunnable>();
            }
            mEventQueue.add(runnable);*/
            mQueueEventCount++;
            if (mQueueEventCount > 30) {
                mQueueEventCount = 1;
            }
            Integer target = Integer.valueOf(mQueueEventCount);
            queueEvent(target, runnable);
        }
    }

    public void queueEvent(ArrayList<RenderRunnable> eventQueue) {
        if (!mIsQuitThread && eventQueue != null) {
            /*if (mEventQueue == null) {
                mEventQueue = new ArrayList<RenderRunnable>();
            }
            mEventQueue.addAll(eventQueue);*/
        }
    }

    private void releaseBeforeLoopQuit() {
        if (mVideoRendererTexture != null) {
            mVideoRendererTexture.release();
            mVideoRendererTexture = null;
        }
        if (mSurfaceTexture != null) {
            mSurfaceTexture.setOnFrameAvailableListener(null);
        }
    }

    private void releaseGL() {
        try {
            if (mOffscreenSurface != null) {
                mOffscreenSurface.release();
                mOffscreenSurface = null;
            }
            if (mFilterManager != null) {
                mFilterManager.release(true);
                mFilterManager = null;
            }
            mInitFilterSuccess = false;
            if (mSurfaceTexture != null) {
                mSurfaceTexture.setOnFrameAvailableListener(null);
                /*mSurfaceTexture.release();*/ //release方法必须在CameraThread内调用否则出现"BufferQueue has been abandoned"错误
                mSurfaceTexture = null;
            }
            if (mWindowSurface != null) {
                mWindowSurface.release();
                mWindowSurface = null;
            }
            if (mEglCore != null) {
                mEglCore.makeNothingCurrent();
            }
            mWindowSurfaceCreateSuccess = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public RenderHandler getHandler() {
        return mRenderHandler;
    }

    public void setCameraPreviewCallback(Camera camera) {
        if (RenderHelper.sCameraThread != null) {
            if (mIsDebug) {
                Log.i(TAG, "setCameraCallback: ");
            }
            RenderHelper.sCameraThread.setPreviewCallback(this);
        }
        /*if (camera != null) {
            camera.setPreviewCallback(this);
        }*/
    }

    public SurfaceTexture getSurfaceTexture() {
        return mSurfaceTexture;
    }

    public void setCameraSurface() {
        CameraHandler cameraHandler = RenderHelper.getCameraHandler();
        if (cameraHandler != null && mSurfaceTexture != null) {
            if (mIsDebug) {
                Log.i(TAG, "render thread setCameraSurface: ");
            }
            cameraHandler.setCameraSurface(new CameraSurface(mSurfaceTexture));
        }
    }

    public void onSurfaceCreate() {
//        Log.i(TAG, "onSurfaceCreate: ");
        try {
            if (mUseCompat) {
                mWindowSurface = new CompatWindowSurface((CompatEglCore) mEglCore, mSurfaceHolder, false);
            } else {
                mWindowSurface = new WindowSurface((EglCore) mEglCore, mSurfaceHolder.getSurface(), false);
            }
            if (mWindowSurface != null) {
                mWindowSurface.makeCurrent();
            }

            if (!createSourceTexture()) {
                throw new RuntimeException("Error creating source texture");
            }
            mWindowSurfaceCreateSuccess = true;
            setSurfaceListener();
            setCameraSurface();

            Matrix.setIdentityM(mSTMatrix, 0);

            mFilterManager = new RenderFilterManager(mContext);
            if (mFilterManager != null) {
                mFilterManager.setPreviewDegree(RenderHelper.sPreviewDegree, RenderHelper.sIsFront);
                mFilterManager.setBeautyEnable(true);
                mFilterManager.resetFilterData();
            }
        } catch (Throwable e) {
            e.printStackTrace();
            mWindowSurfaceCreateSuccess = false;
        }
    }

    private boolean createSourceTexture() {
        mTextureName = GlUtil.createTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES);
        if (mIsDebug) {
            Log.i(TAG, "createSourceTexture: TextureName -> " + mTextureName);
        }
        if (mTextureName <= 0) {
            return false;
        }
        mSurfaceTexture = new SurfaceTexture(mTextureName);
        return true;
    }

    public void setSurfaceListener() {
        if (mSurfaceTexture != null) {
            mSurfaceTexture.setOnFrameAvailableListener(this);
        }
    }

    public RenderFilterManager getFilterManager() {
        return mFilterManager;
    }

    public void onSurfaceChange(int width, int height) {
//        Log.i(TAG, "onSurfaceChange: " + width + ", " + height);
        mSurfaceWidth = width;
        mSurfaceHeight = height;
        float ratio = mSurfaceHeight * 1.0f / mSurfaceWidth;
        if (mSurfaceRatio != ratio) {
            mSurfaceRatio = ratio;
            mSurfaceSizeIsChange = true;
            mSkipFrameCount = 2;
        }

        if (mOffscreenSurface != null) {
            mOffscreenSurface.release();
            mOffscreenSurface = null;
        }
        if (mTakePictureType == 0 || mTakePictureType == 1) {
            if (mUseCompat) {
                mOffscreenSurface = new CompatOffscreenSurface((CompatEglCore) mEglCore, mSurfaceWidth, mSurfaceHeight);
            } else {
                mOffscreenSurface = new OffscreenSurface((EglCore) mEglCore, mSurfaceWidth, mSurfaceHeight);
            }
        }

        RenderHelper.sSurfaceWidth = mSurfaceWidth;
        RenderHelper.sSurfaceHeight = mSurfaceHeight;
        RenderHelper.sPreviewDataLength = 0;
        RenderHelper.sSurfaceIsChange = false;

        calculateRenderSizeScale(mSurfaceWidth, mSurfaceHeight, mSurfaceRatio);
        if (mIsDebug) {
            Log.i(TAG, "onSurfaceChange: " + mSurfaceWidth + ", " + mSurfaceHeight + ", " + mSurfaceRatio + ", " + mRenderScale);
        }
        if (mFilterManager != null) {
            if (!mInitFilterSuccess) {
                mFilterManager.initFilter();
                mInitFilterSuccess = true;
            }
            mFilterManager.initMVPMatrix(mSurfaceWidth, mSurfaceHeight);
            mFilterManager.scaleMVPMatrix(mMvpScaleX, mMvpScaleY);
            mFilterManager.setSurfaceSize(mSurfaceWidth, mSurfaceHeight, mRenderScale);
            mFilterManager.initGLFramebuffer();

        } else {
            GLES20.glViewport(0, 0, mSurfaceWidth, mSurfaceHeight);
        }

        mIsPauseState = false;
        if ((mSurfaceSizeIsChange || mSkipFrameCount > 0) && getHandler() != null) {
            //预览尺寸变化后清除之前的预览数据
            getHandler().updateFrame();
        }
    }

    private void calculateRenderSizeScale(int width, int height, float ratio) {
        mRenderScale = 1.0f;
        if (ratio == 1.0f) {
            mRenderScale = 1.0f;

        } else if (ratio == 4.0f / 3) {
            if (width > 1080) {//(1080, +] (1140P)    1440x2560 2k屏
                mRenderScale = 0.75f;//1440x1920 -> 1080x1440
                //mRenderScale = 0.5f;//1440x1920 -> 720x960

            } else if (width > 720) {//(720, 1080] (1080P)
                mRenderScale = 0.88888f;//1080x1440 -> 960x1280
                //mRenderScale = 0.55555f;//1080x1440 -> 600x800

                if (mCpuLevel > 10) {
                    mRenderScale = 0.44444f;//1080x1440 -> 480x640
                }
                if ("OPPO R9TM".equals(mModel)) {//中低端机特殊处理
                    mRenderScale = 0.44444f;//1080x1440 -> 480x640
                }

            } else if (width > 600) {//(600, 720] (720P)
                mRenderScale = 0.83333f;//720x960 -> 600x800
            }
        } else if (ratio == 16.0f / 9) {
            if (width > 1080) {
                mRenderScale = 0.75f;//1440x2560 -> 1080x1920
                //mRenderScale = 0.5f;//1440x2560 -> 720x1280

            } else if (width > 720) {
                //mRenderScale = 0.75f;//1080x1920 -> 810x1440
                mRenderScale = 0.66666f;//1080x1920 -> 720x1280
                //mRenderScale = 0.5f;//1080x1920 -> 540x960
                if (mCpuLevel > 10) {
                    mRenderScale = 0.5f;//1080x1920 -> 540x960
                }

            } else if (width > 600) {
                mRenderScale = 0.75f;//720x1280 -> 540x960
            }

        } else if (ratio == 17.0f / 9) {
            //Nubia NX595J 1080x2040
            mRenderScale = 0.66666f;//1080x2040 -> 720x1360
            //mRenderScale = 0.5f;//1080x2040 -> 540x1020
            if (mCpuLevel == 3) {
                mRenderScale = 0.5f;//1080x2040 -> 540x1020
            }
        } else if (ratio == 17.25f / 9) {
            if (width > 1080) {//1440x2760 Google Pixel XL
                mRenderScale = 0.75f;//1440x2760 -> 1080x2070

            } else if (width > 720) {//1080x2070
                mRenderScale = 0.66666f;//1080x2070 -> 720x1380
            }

        } else if (ratio == 18.0f / 9) {//18:9
            mRenderScale = 0.66666f;//1080x2160 -> 720x1440
            //mRenderScale = 0.5f;//1080x2160 -> 540x1080
            if (mCpuLevel == 3) {
                mRenderScale = 0.5f;//1080x2160 -> 540x1080
            }

        } else if (ratio == 18.5f / 9) {//18.5:9
            if (width > 1080) {
                //Samsung GALAXY Note8 N9500 1440x2960
                mRenderScale = 0.75f;//1440x2960 -> 1080x2220
                //mRenderScale = 0.5f;//1440x2960 -> 720x1480
                if (mCpuLevel == 3) {
                    mRenderScale = 0.5f;//1440x2960 -> 720x1480
                }

            } else if (width > 720) {
                //Samsung SM-N9500 1080x2220
                mRenderScale = 0.66666f;//1080x2220 -> 720x1480
                if (mCpuLevel == 3) {
                    mRenderScale = 0.5f;//1080x2220 -> 540x1110
                }
            }
        } else {
            if (width > 960) {
                mRenderScale = 0.66666f;
            } else if (width > 720) {
                mRenderScale = 0.8f;
            }
        }
    }

    public void setCameraSize(int width, int height) {
        //width > height
        if (mFilterManager != null) {
            float surfaceHeight = mSurfaceHeight;
            float surfaceRatio = mSurfaceHeight * 1.0f / mSurfaceWidth;
            float targetHeight = mSurfaceWidth * (width * 1.0f / height);
            if (targetHeight / mSurfaceWidth != surfaceRatio) {
                targetHeight = mSurfaceHeight;
            }
            mMvpScaleX = 1.0f;
            mMvpScaleY = targetHeight / surfaceHeight;
            mFilterManager.scaleMVPMatrix(mMvpScaleX, mMvpScaleY);
            if (mIsDebug) {
                Log.i(TAG, "setCameraPreviewSize: " + mMvpScaleX + ", " + mMvpScaleY);
            }

            mFilterManager.setCameraSize(width, height);
        }
    }

    public void onSurfaceDestroy() {

    }

    /**
     * @param mode 0:正常拍照， 1:萌妆照/小视频， 2:Gif表情
     */
    public void setRenderMode(int mode) {//镜头成功打开会回调此方法
//        Log.i(TAG, "setRenderMode: mode:" + mode);
        /*if (mode != mRenderMode && (mode == 0 || mRenderMode == 0)) {
            mResetTracker = true;
        }*/
        mRenderMode = mode;
        //RenderHelper.sCameraSizeType = mode;
        mSurfaceSizeIsChange = false;
    }

    public void setRenderState(boolean isPause) {
        mIsPauseState = isPause;
    }

    public void setRenderVideoEnable(boolean enable) {
        if (enable && mVideoRendererTexture == null) {
            try {
                mVideoRendererTexture = new VideoRendererTexture(mContext);
            } catch (Exception e) {
                e.printStackTrace();
                mVideoRendererTexture = null;
            }
        } else if (!enable && mFilterManager != null) {
            mFilterManager.setVideoTextureEnable(false);
        }
        mRenderVideoEnable = enable;
    }

    public void setVideoRes(Object videoRes) {
        if (mFilterManager != null) {
            if (videoRes == null) {
                mFilterManager.setVideoTextureEnable(false);
            } else if (mRenderVideoEnable && mVideoRendererTexture != null) {
                mFilterManager.setVideoTextureEnable(true);
                mVideoRendererTexture.setVideoRes(videoRes);
            }
        }
    }

    public void setRenderVideoState(int state) {
        if (mVideoRendererTexture != null) {
            if (state == 1 && !mVideoRendererTexture.isPlaying()) {
                mVideoRendererTexture.startPlayer();
            } else {
                mVideoRendererTexture.stopPlayer();
            }
        }
        if (mFilterManager != null) {
            mFilterManager.setVideoTextureEnable(state == 0 ? false : true);
        }
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
//        Log.i(TAG, "onFrameAvailable: ");
        if (!mRenderReady && mOnReadyCallback != null) {
            mRenderReady = true;
            mOnReadyCallback.onReady();
        }
        if (mSurfaceSizeIsChange || mSkipFrameCount > 0) {
            if (mIsDebug) {
                Log.i(TAG, "onFrameAvailable: " + mSkipFrameCount);
            }
            mDirtyFrameCount++;
            if (mSurfaceSizeIsChange && mDirtyFrameCount > 10) {
                //当执行流程为onSurfaceChange -> setRenderMode -> onSurfaceChange时，会进入当前if
                mSurfaceSizeIsChange = false;
            }
            updateFrame();
        }
    }

    @Override
    public void onScreenOrientationChanged(int orientation, int pictureDegree, float fromDegree, float toDegree) {
        if (mIsDebug) {
            Log.i(TAG, "onScreenOrientationChanged: " + orientation + ", " + pictureDegree);
        }
        RenderHelper.sPictureDegree = pictureDegree;
        RenderHelper.sScreenOrientation = ((90 - pictureDegree) + 360) % 360 / 90;
        final int rotateDegree = (RenderHelper.sPictureDegree - 90 + 360) % 360;
        if (RenderHelper.sIsFront) {
            RenderHelper.sCameraOrientation = (360 - ((rotateDegree + 90) % 360)) % 360 / 90;
        } else {
            RenderHelper.sCameraOrientation = (90 - rotateDegree + 360) % 360 / 90;
        }
    }

    private void updateCameraParams(int dataLength) {
        CameraHandler cameraHandler = RenderHelper.getCameraHandler();
        CameraThread cameraThread = null;
        if (cameraHandler != null) {
            cameraThread = cameraHandler.getCamera();
        }
        if (cameraThread != null && (RenderHelper.sCurrentCameraId != cameraThread.getCurrentCameraId() || RenderHelper.sPreviewDataLength != dataLength)) {
            if (cameraThread.getPreviewDataLenghts() == null) {
                return;
            }
            int key = (int) (dataLength / 1.5f);
            Camera.Size mCurrentPreviewSize = cameraThread.getPreviewDataLenghts().get(key);
            if (mCurrentPreviewSize == null) {
                mCurrentPreviewSize = cameraThread.getCameraParameters().getPreviewSize();
            }
            //width > height
            RenderHelper.sPreviewDataLength = dataLength;
            RenderHelper.sCameraWidth = mCurrentPreviewSize.width > mCurrentPreviewSize.height ? mCurrentPreviewSize.width : mCurrentPreviewSize.height;
            RenderHelper.sCameraHeight = mCurrentPreviewSize.width > mCurrentPreviewSize.height ? mCurrentPreviewSize.height : mCurrentPreviewSize.width;
            RenderHelper.sCameraSizeType = cameraThread.getPreviewSizeType();
            RenderHelper.sCameraSizeRatio = RenderHelper.sCameraWidth * 1.0f / RenderHelper.sCameraHeight;
            RenderHelper.sCurrentCameraId = cameraThread.getCurrentCameraId();
            RenderHelper.sIsFront = cameraThread.isFront();
            if (mSurfaceRatio > 16.0f / 9) {
                RenderHelper.sCameraFocusRatio = RenderHelper.sCameraWidth * 1.0f / (mSurfaceWidth * RenderHelper.sCameraSizeRatio);
            } else {
                RenderHelper.sCameraFocusRatio = RenderHelper.sCameraWidth * 1.0f / mSurfaceHeight;
            }

            final int rotateDegree = (RenderHelper.sPictureDegree - 90 + 360) % 360;
            if (RenderHelper.sIsFront) {
                RenderHelper.sCameraOrientation = (360 - ((rotateDegree + 90) % 360)) % 360 / 90;
            } else {
                RenderHelper.sCameraOrientation = (90 - rotateDegree + 360) % 360 / 90;
            }

            RenderHelper.sCameraIsChange = false;

            if (mIsDebug) {
                Log.i(TAG, "onPreviewFrame: update size");
            }
            setCameraSize(RenderHelper.sCameraWidth, RenderHelper.sCameraHeight);
        }
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
//        Log.i(TAG, "onPreviewFrame: ");
        if (!mWindowSurfaceCreateSuccess || mIsQuitThread || mIsPauseState) {
            return;
        }
        if (RenderHelper.sCameraIsChange) {
            updateCameraParams(data.length);
            mResetTracker = !RenderHelper.sCameraIsChange;
        }
        if (getHandler() == null) {
            return;
        }
        getHandler().updateTexImage();

        if (mSurfaceSizeIsChange || mSkipFrameCount > 0) {
            return;
        }

        if (mShowFaceTrackerTime) {
            mTrackerStartTime = System.nanoTime();
        }
        //detect face
        if (mResetTracker) {
            mResetTracker = false;
            PocoFaceTracker.resetTrackerData(RenderHelper.sCameraWidth, RenderHelper.sCameraHeight, true);
        }
        ArrayList<PocoFace> facesList = null;
        try {
//            facesList = filter.detectFace_camera(mContext, data, RenderHelper.sCameraWidth, RenderHelper.sCameraHeight,
//                    RenderHelper.sIsFront, RenderHelper.sCameraOrientation, RenderHelper.sMaxTrackers, false);

            facesList = PocoFaceTracker.getInstance().trackMulti(mContext, data, RenderHelper.sCameraWidth, RenderHelper.sCameraHeight, RenderHelper.sCameraOrientation,
                    RenderHelper.sIsFront,RenderHelper.sMaxTrackers);
        } catch (Throwable t) {
            t.printStackTrace();
            facesList = null;
        }
        if (mShowFaceTrackerTime) {
            Log.i(TAG, "onPreviewFrame: tracker time:" + ((System.nanoTime() - mTrackerStartTime) / 1000000));
        }
        if (mDetectFaceCallback != null) {
            mDetectFaceCallback.onDetectResult(facesList, mSurfaceWidth, mSurfaceHeight);
        }

        //render frame
        if (getHandler() == null) {
            return;
        }
        getHandler().updateFrame();
    }

    public void updateTexImage() {
        if (!mWindowSurfaceCreateSuccess || mIsQuitThread || mIsPauseState) {
            return;
        }
        if (mSurfaceTexture != null) {
            try {
                mSurfaceTexture.updateTexImage();
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

    public void updateFrame() {
        if (!mWindowSurfaceCreateSuccess || mIsQuitThread || mIsPauseState) {
            return;
        }
        if (mEventQueue != null && !mEventQueue.isEmpty()) {
            RenderRunnable runnable = null;//mEventQueue.remove(0);
            synchronized (mEventQueue) {
                Integer key = null;
                for (Map.Entry<Integer, RenderRunnable> entry : mEventQueue.entrySet()) {
                    key = entry.getKey();
                    runnable = entry.getValue();
                    break;
                }
                if (key != null) {
                    mEventQueue.remove(key);
                }
            }
            if (runnable != null) {
                runnable.run(this);
                runnable = null;
//                Log.i(TAG, "updateFrame: process event");
            }
        }
        long t = 0L;
        if (mShowDrawTime) {
            t = System.nanoTime();
        }
        mCanDraw = true;
        try {
            GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

            if (RenderHelper.sSurfaceIsChange || mSurfaceSizeIsChange) {
                mCanDraw = false;
            } else if (mSkipFrameCount > 0) {
                mSkipFrameCount--;
                mCanDraw = false;
            } else {
                mDirtyFrameCount = 0;
            }

            if (mCanDraw) {
                //渲染之前停止录制
                stopRecord();

                if (mSurfaceTexture != null) {
                    try {
                        mSurfaceTexture.getTransformMatrix(mSTMatrix);
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
                //do draw method ...
                if (mFilterManager != null) {
                    if (mRenderVideoEnable && mVideoRendererTexture != null) {
                        try {
                            mVideoRendererTexture.updateTextureImage();
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }
                        mFilterManager.setVideoTextureEnable(mVideoRendererTexture.isPlaying());
                        mFilterManager.setVideoTextureSize(mVideoRendererTexture.getVideoWidth(), mVideoRendererTexture.getVideoHeight());
                        mFilterManager.setDrawType(false);
                        mFilterManager.setIsDrawCache(false);
                        mFilterManager.drawFrame(mTextureName, mSTMatrix, mVideoRendererTexture.getTextureId(), mVideoRendererTexture.getSTMatrix());

                    } else {
                        mFilterManager.setDrawType(false);
                        mFilterManager.setIsDrawCache(false);
                        mFilterManager.drawFrame(mTextureName, mSTMatrix);
                    }
                    GLES20.glFlush();

                    if (mTakePictureType == 1) {
                        boolean result = takePicture();
                        if (result) {
                            mWindowSurface.makeCurrent();
                        }
                    } else if (mTakePictureType == 2) {
                        takePicture2();
                    }
                } else {
                    mCanDraw = false;
                }
            }

            mSwapResult = mWindowSurface.swapBuffers();

            if (mCanDraw) {
                //do take picture or record video
                //录像
                boolean result = false;
                if (mIsLiveEncoder) {
                    result = drawLiveVideoFrame(mTextureName, mSTMatrix);
                } else {
                    result = drawVideoFrame(mTextureName, mSTMatrix);
                }
                if (result) {
                    // Restore.
                    mWindowSurface.makeCurrent();
                }

                //拍照
                if (mTakePictureType == 0) {
                    result = takePicture();
                    if (result) {
                        mWindowSurface.makeCurrent();
                    }
                }
            }

            if (!mSwapResult) {
                Log.e(TAG, "swapBuffers failed, killing renderer thread");
                quitThread(mIsIndependentThread);
                return;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

        if (mCanDraw && mFilterManager != null) {
            mFilterManager.loadNextTexture(true);
        }

        if (mShowDrawTime) {
            long t2 = System.nanoTime();
            Log.i(TAG, "updateFrame: " + ((t2 - t) / 1000000));
        }
    }

    private void clearAll() {
        mContext = null;
        mSurfaceHolder = null;
        mOnReadyCallback = null;
        mRenderReady = false;
        mSTMatrix = null;
        mDetectFaceCallback = null;
        mOnCaptureFrameListener = null;
        mMediaMuxerWrapper = null;
        mMediaVideoEncoder = null;
        mIsLiveEncoder = false;
        mFrameBuf = null;

        if (mEventQueue != null) {
            mEventQueue.clear();
            mEventQueue = null;
        }
    }

    public void setDetectFaceCallback(DetectFaceCallback callback) {
        mDetectFaceCallback = callback;
    }

    public void setMediaMuxerWrapper(MediaMuxerWrapper mediaMuxerWrapper) {
        mMediaMuxerWrapper = mediaMuxerWrapper;
        if (mMediaMuxerWrapper != null) {
            setMediaVideoEncoder(mediaMuxerWrapper.getVideoEncoder());
        }
    }

    public void setMediaVideoEncoder(MediaEncoder mediaEncoder) {
        mMediaVideoEncoder = null;
        mIsLiveEncoder = false;
        if (mediaEncoder != null && mediaEncoder instanceof MediaVideoEncoder) {
            mMediaVideoEncoder = (MediaVideoEncoder) mediaEncoder;
            mIsLiveEncoder = mMediaVideoEncoder.isLiveEncoder();
        }
    }

    public void setRecordState(int state) {
        mLastRecordState = mRecordState;
        if (mIsDebug) {
            Log.i(TAG, "setRecordState mLastRecordState:" + mLastRecordState + ", state:" + state + " mMediaMuxerWrapper:" + (mMediaMuxerWrapper == null ? "null" : "not null"));
        }
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

    public int getRecordState() {
        return mRecordState;
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

    public void setFrameTopPadding(int topPadding) {
        mFrameTopPadding = topPadding;
    }

    public void setVideoOrientation(int orientation) {
        mOrientation = orientation;
    }

    public void setWaterMarkHasDate(boolean hasDate) {
        mWaterMarkHasDate = hasDate;
    }

    public void setCaptureGifFrameEnable(boolean enable) {
        mCaptureGifFrameEnable = enable;
    }

    private boolean stopRecord() {
        if (mIsLiveEncoder) {
            if (mMediaVideoEncoder != null && mRecordState == RecordState.STOP) {
                try {
                    mMediaVideoEncoder.stopRecording();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                mRecordState = RecordState.IDLE;
                return true;
            }
        } else {
            if (mMediaMuxerWrapper != null && mMediaVideoEncoder != null && mRecordState == RecordState.STOP) {
                try {
                    mMediaMuxerWrapper.stopRecording();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                mRecordState = RecordState.IDLE;
                return true;
            }
        }
        return false;
    }

    private boolean drawVideoFrame(int textureId, final float[] texMatrix) {
        if (mRecordState == RecordState.IDLE) {
            return false;
        }
        if (mMediaMuxerWrapper != null && mMediaVideoEncoder != null) {
            mVideoWidth = mMediaVideoEncoder.getVideoWidth();
            mVideoHeight = mMediaVideoEncoder.getVideoHeight();
            if (mRecordState == RecordState.PREPARE) {
//                Log.i(TAG, "drawVideoFrame looperPrepare " + mRenderWidth + ", " + mRenderHeight);
                boolean prepareSuccess = false;
                if (!mMediaMuxerWrapper.isPrepared()) {
                    try {
                        if (!mUseCompat) {
                            mMediaVideoEncoder.setEglCore((EglCore) mEglCore);
                        }
                        mMediaMuxerWrapper.prepare();
                    } catch (Throwable e) {
                        e.printStackTrace();
                        mMediaMuxerWrapper.setPrepared(false);
                        mRecordState = RecordState.IDLE;
                    }
                    prepareSuccess = mMediaMuxerWrapper.isPrepared();
                } else {
                    prepareSuccess = true;
                }
                if (prepareSuccess) {
                    mFilterManager.prepareWaterMark(mWaterMarkHasDate);
                    mMediaMuxerWrapper.setViewSize(mSurfaceWidth, mSurfaceHeight);//480,640
                    mRecordState = RecordState.WAIT;
                }
                mInvalidFrameCount = 0;
            }
            if (mRecordState == RecordState.START) {
//                Log.i(TAG, "drawVideoFrame startRecording");
                //check video file exists
                if (!mMediaMuxerWrapper.isPrepared()) {
                    mRecordState = RecordState.PREPARE;
                    return false;
                }
                String path = mMediaMuxerWrapper.getOutputPath();
                if (TextUtils.isEmpty(path)) {
                    mRecordState = RecordState.IDLE;
                    return false;
                }
                File file = new File(path);
                if (file == null || !file.exists()) {
                    mRecordState = RecordState.IDLE;
                    return false;
                }
                file = null;

                mFilterManager.setWaterMarkOrientation(mOrientation);
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
//                Log.i(TAG, "drawVideoFrame ending");
                if (mFrameCount == mEndingFrameCount) {
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
                if (!mDrawEnding) {
                    //计算时间
                    /*if (mMediaMuxerWrapper.canStop()) {
                    }*/
                    mMediaMuxerWrapper.stopRecording();
                    mRecordState = RecordState.IDLE;
                    return false;

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

                    return false;
                }
//                captureFrame(0, mRenderWidth, mRenderHeight, mFrameTopPadding, mOnCaptureFrameListener);
//                if (mTakePictureType == 0) {
//                    captureFrameBySurface(0, mSurfaceWidth, mSurfaceHeight, mRenderMode == 2 ? mFrameTopPadding : 0, mOnCaptureFrameListener);
//                }
            }
            if ((mRecordState == RecordState.RECORDING || mRecordState == RecordState.ENDING) && mMediaMuxerWrapper.canRecord() && mMediaVideoEncoder != null) {
//                long t = System.currentTimeMillis();
                // switch to recorder state
                try {
                    mMediaVideoEncoder.makeCurrent();
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i(TAG, "recording-->mMediaVideoEncoder makeCurrent fail");
                    return true;
                }
//                long t2 = System.currentTimeMillis();

                mMediaVideoEncoder.setProjectionMatrix(texMatrix);
                mMediaVideoEncoder.setViewport();
//                mMediaVideoEncoder.frameAvailableSoon();

                // render everything again

                mFilterManager.setVideoSize(mVideoWidth, mVideoHeight, mFrameTopPadding);
                mFilterManager.setDrawType(true);
                mFilterManager.setIsDrawCache(true);
                mFilterManager.setWaterMarkEnable(mRenderMode == 1);
                if (mDrawEnding && mRecordState == RecordState.ENDING) {
                    mFilterManager.setDrawEnding(true);
                    mFilterManager.setEndingFrameCount(mEndingFrameCount, mLastFrame);
                }
                mFilterManager.drawFrame(textureId, texMatrix);

                GLES20.glFlush();
//                long t3 = System.currentTimeMillis();
                //capture gif frame
                if (mCaptureGifFrameEnable) {
                    captureFrame(2, mVideoWidth, mVideoHeight, 0, mOnCaptureFrameListener);
                }
//                long t4 = System.currentTimeMillis();
                mMediaVideoEncoder.swapBuffers();//耗时，影响录制帧率
                mFrameCount++;

//                long t5 = System.currentTimeMillis();
//                Log.i(TAG, "drawVideoFrame: " + (t2 - t) + ", " + (t3 - t2) + ", " + (t4 - t3) + ", " + (t5 - t4) + ", total:" + (t5 - t));
                return true;
            }
        }
        return false;
    }

    private boolean drawLiveVideoFrame(int textureId, final float[] texMatrix) {
        if (mRecordState == RecordState.IDLE) {
            return false;
        }
        if (mMediaVideoEncoder != null) {
            mVideoWidth = mMediaVideoEncoder.getVideoWidth();
            mVideoHeight = mMediaVideoEncoder.getVideoHeight();
            if (mRecordState == RecordState.PREPARE) {
//                Log.i(TAG, "drawVideoFrame looperPrepare " + mRenderWidth + ", " + mRenderHeight);
                boolean prepareSuccess = false;
                try {
                    if (!mUseCompat) {
                        mMediaVideoEncoder.setEglCore((EglCore) mEglCore);
                    }
                    prepareSuccess = mMediaVideoEncoder.prepare();
                } catch (Throwable e) {
                    e.printStackTrace();
                    prepareSuccess = false;
                    mRecordState = RecordState.IDLE;
                }
                if (prepareSuccess) {
                    mMediaVideoEncoder.setViewSize(mSurfaceWidth, mSurfaceHeight);//480,640
                    mRecordState = RecordState.WAIT;
                }
                mInvalidFrameCount = 0;
            }
            if (mRecordState == RecordState.START) {
//                Log.i(TAG, "drawVideoFrame startRecording");
                //check video file exists
                mMediaVideoEncoder.startRecording();
                mRecordState = RecordState.RECORDING;

            } else if (mRecordState == RecordState.STOP) {
                mMediaVideoEncoder.stopRecording();
                mRecordState = RecordState.IDLE;
                return false;
            }
            if ((mRecordState == RecordState.RECORDING || mRecordState == RecordState.ENDING) && mMediaVideoEncoder.canRecord()) {
//                long t = System.currentTimeMillis();
                // switch to recorder state
                try {
                    mMediaVideoEncoder.makeCurrent();
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i(TAG, "recording-->mMediaVideoEncoder makeCurrent fail");
                    return true;
                }
//                long t2 = System.currentTimeMillis();

                mMediaVideoEncoder.setProjectionMatrix(texMatrix);
                mMediaVideoEncoder.setViewport();

                // render everything again
                mFilterManager.setVideoSize(mVideoWidth, mVideoHeight, mFrameTopPadding);
                mFilterManager.setDrawType(true);
                mFilterManager.setIsDrawCache(true);
                mFilterManager.setWaterMarkEnable(false);
                mFilterManager.drawFrame(textureId, texMatrix);

                GLES20.glFlush();
//                long t3 = System.currentTimeMillis();
//                long t4 = System.currentTimeMillis();
                mMediaVideoEncoder.swapBuffers();//耗时，影响录制帧率

//                long t5 = System.currentTimeMillis();
//                Log.i(TAG, "drawVideoFrame: " + (t2 - t) + ", " + (t3 - t2) + ", " + (t4 - t3) + ", " + (t5 - t4) + ", total:" + (t5 - t));
                return true;
            }
        }
        return false;
    }

    private boolean takePicture() {
        if (mRecordState == RecordState.CAPTURE_A_FRAME && mOffscreenSurface != null) {
            long t1 = 0;
            if (mShowTakePictureTime) {
                t1 = System.nanoTime();
            }
            try {
                mOffscreenSurface.makeCurrentReadFrom(mWindowSurface);
            } catch (Throwable t) {
                t.printStackTrace();
                return false;
            }
            restoreRecordState();
            GLES20.glFinish();

            boolean result = captureFrame(1, mSurfaceWidth, mSurfaceHeight, 0, mOnCaptureFrameListener);
            mOffscreenSurface.swapBuffers();
            if (mShowTakePictureTime) {
                Log.i(TAG, "takePicture: takeType:" + mTakePictureType+", " + ((System.nanoTime() - t1) / 1000000));
            }
            return result;
        }
        return false;
    }

    private boolean takePicture2() {
        if (mRecordState == RecordState.CAPTURE_A_FRAME) {
            long t1 = 0;
            if (mShowTakePictureTime) {
                t1 = System.nanoTime();
            }
            restoreRecordState();
            captureFrame(1, mSurfaceWidth, mSurfaceHeight, 0, mOnCaptureFrameListener);
            if (mShowTakePictureTime) {
                Log.i(TAG, "takePicture2: takeType:" + mTakePictureType+", " + ((System.nanoTime() - t1) / 1000000));
            }
        }
        return false;
    }

    private boolean captureFrameBySurface(int frameType, int srcWidth, int srcHeight, int topPadding, OnCaptureFrameListener listener) {
        if (mOffscreenSurface != null) {
            try {
                mOffscreenSurface.makeCurrentReadFrom(mWindowSurface);
            } catch (Throwable t) {
                t.printStackTrace();
                return false;
            }
            boolean result = captureFrame(frameType, srcWidth, srcHeight, topPadding, listener);
            mOffscreenSurface.swapBuffers();
            return result;
        }
        return false;
    }

    private boolean captureFrame(int frameType, int srcWidth, int srcHeight, int topPadding, OnCaptureFrameListener listener) {
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
            GLES20.glReadPixels(0, topPadding, srcWidth, srcHeight, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, mFrameBuf);//avg:80ms
            mFrameBuf.rewind();

            if (listener != null) {
                listener.onCaptureFrame(frameType, mFrameBuf, srcWidth, srcHeight);
            } else {
                Log.i(TAG, "captureAFrame: listener is null, type:" + frameType);
            }
            return true;
        }
        return false;
    }

}
