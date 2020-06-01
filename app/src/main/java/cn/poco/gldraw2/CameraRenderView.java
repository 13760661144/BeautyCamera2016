package cn.poco.gldraw2;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;

import cn.poco.camera2.CameraHandler;
import cn.poco.camera2.CameraSurface;
import cn.poco.camera2.CameraThread;
import cn.poco.utils.CpuUtils;

/**
 * Created by zwq on 2017/07/24 11:46.<br/><br/>
 *
 * 1.按Back键：surfaceDestroyed->onPause  CameraThread和RenderThread都被销毁
 * 2.按Home键: onPause->surfaceDestroyed  CameraThread和RenderThread都被销毁
 * 3.按Power键: onPause  只销毁CameraThread
 * 4.PagePopUp: surfaceDestroy  不会调用onPause
 */
public class CameraRenderView extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = "vvv CameraRenderView";
    public final boolean mIsDebug = false;
    private ArrayList<RenderRunnable> mMessageQueue;
    private boolean mCameraIsRelease;
    private boolean mNeedReleaseSurface;
    private boolean mRenderReady;

    public CameraRenderView(Context context) {
        super(context);
        getHolder().addCallback(this);

        //获取cpu信息
        CpuUtils.getCpuInfo();

        RenderHelper.clearAll();
        startCameraThread(true);
    }

    public void setPreviewSize(int width, int height, int sizeType) {
        RenderHelper.sCameraWidth = width > height ? width : height;
        RenderHelper.sCameraHeight = width > height ? height : width;
        RenderHelper.sCameraSizeType = sizeType;
    }

    public void setPreviewSize(int width, int height) {
        setPreviewSize(width, height, 3);
    }

    public void onResume() {
        if (mIsDebug) {
            Log.i(TAG, "onResume: ");
        }
        startCameraThread(false);
    }

    public void onPause() {
        if (mIsDebug) {
            Log.i(TAG, "onPause: ");
        }
        stopCameraThread();
    }

    public void onDestroy() {
        mMessageQueue = null;
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        if (mIsDebug) {
            Log.i(TAG, "surfaceCreated");
        }
        mNeedReleaseSurface = false;
        startRenderThread(getContext().getApplicationContext(), surfaceHolder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {
        if (mIsDebug) {
            Log.i(TAG, "surfaceChanged");
        }
        mNeedReleaseSurface = false;
        onSurfaceChanged(width, height);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        if (mIsDebug) {
            Log.i(TAG, "surfaceDestroyed");
        }
        mNeedReleaseSurface = true;
        stopRenderThread();
        if (!mCameraIsRelease) {
            stopCameraThread();
        }
    }

    public void startCameraThread(boolean isInit) {
        if (RenderHelper.sCameraThread == null) {
            try {
                RenderHelper.sCameraThread = new CameraThread(getContext());
                RenderHelper.sCameraThread.setName("CameraThread");
                RenderHelper.sCameraThread.start();
                RenderHelper.sCameraThread.waitUntilReady();

                /*
                按power键关闭，RenderThread不会被销毁，重新按power键打开会执行以下两个方法
                 */
                setCameraSurface();
                setCameraCallback();

                CameraHandler cameraHandler = RenderHelper.getCameraHandler();
                if (cameraHandler != null && RenderHelper.sCurrentCameraId != -1) {
                    if (RenderHelper.sCameraSizeType >= 0) {
                        RenderHelper.sCameraThread.setPreviewSize(RenderHelper.sCameraWidth, RenderHelper.sCameraHeight, RenderHelper.sCameraSizeType);
                    } else {
                        RenderHelper.sCameraThread.setPreviewSize(RenderHelper.sCameraWidth, RenderHelper.sCameraHeight);
                    }
                    //按home键退出后重新打开会调用
                    cameraHandler.openCamera(RenderHelper.sCurrentCameraId);
                }
                RenderHelper.sPreviewDataLength = 0;
                RenderHelper.sCameraIsChange = true;
                if (!isInit) {
                    RenderHelper.sCameraOpenCount++;
                }
                mCameraIsRelease = false;
                RenderHelper.sContextHashCode = getContext().hashCode();
                //Log.i(TAG, "startCameraThread: "+RenderHelper.sContextHashCode);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setCameraSurface() {
        //if the rendering hasn't been shutdown, just reset the surface and start the camera.
        //this happens when the device is turned off with the app running.
        if (RenderHelper.sRenderThread != null) {
            RenderHandler renderHandler = RenderHelper.getRenderHandler();
            if (renderHandler != null) {
                renderHandler.setSurfaceListener();
            }
            final SurfaceTexture surfaceTexture = RenderHelper.sRenderThread.getSurfaceTexture();

            CameraHandler cameraHandler = RenderHelper.getCameraHandler();
            if (cameraHandler != null && surfaceTexture != null) {
                if (mIsDebug) {
                    Log.i(TAG, "render view setCameraSurface: ");
                }
                cameraHandler.setCameraSurface(new CameraSurface(surfaceTexture));
            }
        }
    }

    public void stopCameraThread() {
        if (RenderHelper.sCameraThread != null) {
            //Log.i(TAG, "stopCameraThread: "+RenderHelper.sContextHashCode+", "+getContext().hashCode());
            if (RenderHelper.sContextHashCode != getContext().hashCode()) {
                return;
            }
            try {
                CameraHandler cameraHandler = RenderHelper.sCameraThread.getHandler();
                if (cameraHandler != null) {
                    if (cameraHandler.getCamera() != null) {//退出线程前记录镜头id
                        RenderHelper.sCurrentCameraId = cameraHandler.getCamera().getCurrentCameraId();
                    }
                    if (mIsDebug) {
                        Log.i(TAG, "render view stopCameraThread release: "+mNeedReleaseSurface);
                    }
                    cameraHandler.quitThread(mNeedReleaseSurface);
                }
                RenderHelper.sCameraThread.join();

            } catch (InterruptedException ie) {
                throw new RuntimeException("camera thread join in onPause was interrupted");
            } catch (Exception e) {
                e.printStackTrace();
            }
            RenderHelper.sCameraThread = null;
            mCameraIsRelease = true;
            RenderHelper.sContextHashCode = -1;
        }
    }

    public void startRenderThread(Context context, SurfaceHolder surfaceHolder) {
        if (RenderHelper.sRenderThread == null) {
            try {
                mRenderReady = false;
                RenderHelper.sRenderThread = new RenderThread(context, surfaceHolder);
                RenderHelper.sRenderThread.setOnReadyCallback(new RenderThread.OnReadyCallback() {
                    @Override
                    public void onReady() {
                        RenderHandler renderHandler = RenderHelper.getRenderHandler();
                        processMsgQueue(renderHandler);
                        mRenderReady = true;
                    }
                });
                RenderHelper.sRenderThread.setName("RenderThread");
                RenderHelper.sRenderThread.start();
                RenderHelper.sRenderThread.waitUntilReady();

                setCameraCallback();

                RenderHandler renderHandler = RenderHelper.getRenderHandler();
                if (renderHandler != null) {
                    renderHandler.onSurfaceCreate();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setCameraCallback() {
        if (RenderHelper.sCameraThread != null && RenderHelper.sRenderThread != null) {
            if (mIsDebug) {
                Log.i(TAG, "render view setCameraCallback: ");
            }
            RenderHelper.sCameraThread.setPreviewCallback(RenderHelper.sRenderThread);
            RenderHelper.sCameraThread.setScreenOrientationChangeListener(RenderHelper.sRenderThread);
        }
    }

    public void onSurfaceChanged(int width, int height) {
        try {
            RenderHandler renderHandler = RenderHelper.getRenderHandler();
            if (mIsDebug) {
                Log.i(TAG, "render view onSurfaceChanged: ");
            }
            if (renderHandler != null) {
                RenderHelper.sSurfaceIsChange = true;
                renderHandler.onSurfaceChange(width, height);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopRenderThread() {
        if (RenderHelper.sRenderThread != null) {
            try {
                RenderHandler renderHandler = RenderHelper.getRenderHandler();
                if (renderHandler != null) {
                    renderHandler.quitThread();
                }
                RenderHelper.sRenderThread.join();
            } catch (InterruptedException ie) {
                // not expected
                throw new RuntimeException("render thread shutdown join was interrupted", ie);
            } catch (Exception e) {
                e.printStackTrace();
            }
            RenderHelper.sRenderThread = null;
            mRenderReady = false;
        }
    }

    private void processMsgQueue(RenderHandler renderHandler) {
        if (mMessageQueue != null && !mMessageQueue.isEmpty() && renderHandler != null) {
            synchronized (mMessageQueue) {
                for (RenderRunnable runnable : mMessageQueue) {
                    if (runnable != null) {
                        renderHandler.sendMsg(runnable);
                        runnable = null;
                    }
                }
                mMessageQueue.clear();
            }
        }
    }

    public int queueEvent(RenderRunnable runnable, int delay) {
        if (runnable != null) {
            RenderHandler renderHandler = RenderHelper.getRenderHandler();
            if (mRenderReady && renderHandler != null) {
                processMsgQueue(renderHandler);
                return renderHandler.sendMsg(runnable, delay);
            } else {
                if (mMessageQueue == null) {
                    mMessageQueue = new ArrayList<>();
                }
                synchronized (mMessageQueue) {
                    mMessageQueue.add(runnable);
                }
            }
        }
        return -1;
    }

    public int queueEvent(RenderRunnable runnable) {
        return queueEvent(runnable, 0);
    }

    /*private void processMsgQueue(RenderThread renderThread) {
        if (mMessageQueue != null && !mMessageQueue.isEmpty() && renderThread != null) {
            synchronized (mMessageQueue) {
                renderThread.queueEvent(mMessageQueue);
                mMessageQueue.clear();
            }
        }
    }*/

    public void runOnGLThread(RenderRunnable runnable) {
        runOnGLThread(-1, runnable);
    }

    /**
     *
     * @param target target <= 90
     * @param runnable
     */
    public void runOnGLThread(int target, RenderRunnable runnable) {
        if (runnable != null) {
            if (mRenderReady && RenderHelper.sRenderThread != null) {
                processMsgQueue(RenderHelper.sRenderThread.getHandler());
                if (target < 0) {
                    RenderHelper.sRenderThread.queueEvent(runnable);
                } else {
                    RenderHelper.sRenderThread.queueEvent(target + 30, runnable);
                }
            } else {
                if (mMessageQueue == null) {
                    mMessageQueue = new ArrayList<>();
                }
                synchronized (mMessageQueue) {
                    mMessageQueue.add(runnable);
                }
            }
        }
    }

}
