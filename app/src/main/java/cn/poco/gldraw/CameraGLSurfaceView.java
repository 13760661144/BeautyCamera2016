package cn.poco.gldraw;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.view.SurfaceHolder;

import cn.poco.camera.CameraWrapper;
import cn.poco.camera.ICameraView;

public class CameraGLSurfaceView extends GL10SurfaceView implements ICameraView, SurfaceTexture.OnFrameAvailableListener {

    private static final String TAG = "bbb";

    public static final int ON_SURFACE_CREATE = 1001;
    public static final int START_CAMERA_PREVIEW = 1002;
    public static final int REQUEST_RENDER = 1003;

    private CameraHandler mBackgroundHandler;
    private HandlerThread mHandlerThread;
    private CameraRecordRenderer mCameraRenderer;
    private CameraWrapper cameraWrapper;
    private boolean mSurfaceCreated;
    private SurfaceTexture surfaceTexture;

    public CameraGLSurfaceView(Context context) {
        this(context, null);
    }

    public CameraGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        setEGLContextClientVersion(2);
        mHandlerThread = new HandlerThread("CameraHandlerThread");
        mHandlerThread.start();

        mBackgroundHandler = new CameraHandler(mHandlerThread.getLooper(), this);
        mCameraRenderer = new CameraRecordRenderer(context.getApplicationContext(), mBackgroundHandler);
        setRenderer(mCameraRenderer);
        setRenderMode(RENDERMODE_WHEN_DIRTY);

        cameraWrapper = new CameraWrapper(getContext(), new CameraWrapper.CameraSurfaceView() {
            @Override
            public SurfaceHolder getSurfaceHolder() {
                return null;
            }

            @Override
            public SurfaceTexture getSurfaceTexture() {
                return surfaceTexture;
            }

            @Override
            public void requestView() {
                if (mSurfaceCreated) {
//                    Log.i(TAG, "requestView-->requestLayout");
                    requestLayout();
                }
            }
        });
    }

    public CameraRecordRenderer getRenderer() {
        return mCameraRenderer;
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        requestRender();
    }

    public static class CameraHandler extends Handler {
        private CameraGLSurfaceView mCameraGLSurfaceView;

        public CameraHandler(Looper looper, CameraGLSurfaceView cameraGLSurfaceView) {
            super(looper);
            this.mCameraGLSurfaceView = cameraGLSurfaceView;
        }

        @Override
        public void handleMessage(Message msg) {
            if (mCameraGLSurfaceView != null) {
                mCameraGLSurfaceView.handleAllMessage(msg);
            }
        }
    }

    private void handleAllMessage(final Message msg) {
//        Log.i(TAG, "handleAllMessage :"+msg.what);
        switch (msg.what) {
            case ON_SURFACE_CREATE:
                surfaceTexture = (SurfaceTexture) msg.obj;
                if (surfaceTexture != null) {
                    surfaceTexture.setOnFrameAvailableListener(this);
                }
                if (cameraWrapper != null) {
                    cameraWrapper.onSurfaceViewCreate();
                    mSurfaceCreated = true;
                }
                break;
            case START_CAMERA_PREVIEW:
                final int width = msg.arg1;
                final int height = msg.arg2;
                if (cameraWrapper != null) {
                    cameraWrapper.onSurfaceViewChange();
                }
                mBackgroundHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (cameraWrapper != null) {
                            Camera.Parameters parameters = cameraWrapper.getCameraParameters();
                            if (parameters != null) {
                                Camera.Size previewSize = parameters.getPreviewSize();
                                int pWidth = 0;
                                int pHeight = 0;
                                if (previewSize != null) {
                                    pWidth = previewSize.width < previewSize.height ? previewSize.width : previewSize.height;
                                    pHeight = previewSize.width < previewSize.height ? previewSize.height : previewSize.width;
//                                Log.i("bbb", "previewSize:"+previewSize.height+", "+previewSize.width+", "+width+", "+height);
                                }
                                if ((pWidth != 0 && pHeight != 0) && (pWidth != width || pHeight != height)) {
                                    mCameraRenderer.setCameraPreviewSize(pWidth, pHeight);
                                    mCameraRenderer.setCameraSize(pWidth, pHeight);
                                    return;
                                }
                            }
                        }
                        mCameraRenderer.setCameraSize(width, height);
                    }
                }, 300);
                queueEvent(new Runnable() {
                    @Override
                    public void run() {
                        if (mCameraRenderer != null) {
                            mCameraRenderer.runOnGLThread();
                        }
                    }
                });
                break;
            case REQUEST_RENDER:
                requestRender();
                break;
            default:
                break;
        }
    }

    @Override
    public CameraWrapper getCamera() {
        return cameraWrapper;
    }

    @Override
    public void switchCamera() {
        if (cameraWrapper != null) {
            cameraWrapper.switchCamera();
        }
    }

    @Override
    public void setPreviewRatio(float ratio) {

    }

    @Override
    public void onPreviewSuccess() {

    }

    @Override
    public void setFaceData(Object... objects) {

    }

    @Override
    public void setPreviewDegree(final int patchDegree) {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                if (mCameraRenderer != null) {
                    mCameraRenderer.setPreviewDegree(patchDegree, cameraWrapper == null ? false : cameraWrapper.isFront());
                }
            }
        });
    }

    @Override
    public void setPatchMode(boolean patchMode) {

    }

    @Override
    public int patchPreviewDegree() {
        int degree = 0;
        if (cameraWrapper != null) {
            degree = cameraWrapper.patchPreviewDegree();
        }
        setPreviewDegree(degree);
        return degree;
    }

    @Override
    public void setBeautyEnable(final boolean enable) {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                if (mCameraRenderer != null) {
                    mCameraRenderer.setBeautyEnable(enable);
                }
            }
        });
    }

    @Override
    public void setFilterEnable(boolean enable) {

    }

    @Deprecated
    @Override
    public void setFilterId(final int filterId) {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                if (mCameraRenderer != null) {
                    mCameraRenderer.changeColorFilter(filterId);
                }
            }
        });
    }

    @Override
    public void setDelayDestroyTime(int delay) {

    }

    @Override
    public void onResume() {
        super.onResume();
        resetFilterData();
    }

    @Override
    public void onPause() {
        mBackgroundHandler.removeCallbacksAndMessages(null);
        if (cameraWrapper != null) {
            cameraWrapper.onSurfaceViewDestory();
        }
        resetFilterData();
        queueEvent(new Runnable() {
            @Override
            public void run() {
                // 跨进程 清空 Renderer数据
                if (mCameraRenderer != null) {
                    mCameraRenderer.notifyPausing();
                }
            }
        });
        super.onPause();
    }

    public void resetFilterData() {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                if (mCameraRenderer != null) {
                    mCameraRenderer.resetFilterData();
                }
            }
        });
    }

    public void onDestroy() {
        if (mBackgroundHandler != null) {
            mBackgroundHandler.removeCallbacksAndMessages(null);
            mBackgroundHandler = null;
        }
        if (mHandlerThread != null) {
            mHandlerThread.quit();
            mHandlerThread = null;
        }
        if (mCameraRenderer != null) {
            queueEvent(new Runnable() {
                @Override
                public void run() {
                    mCameraRenderer.resetFilterData();
                    mCameraRenderer.clearAll();
                    mCameraRenderer = null;
                }
            });
        }
        cameraWrapper = null;
        if (surfaceTexture != null) {
            surfaceTexture.release();
            surfaceTexture = null;
        }
    }

    @Override
    public void recycleAll() {

    }

}