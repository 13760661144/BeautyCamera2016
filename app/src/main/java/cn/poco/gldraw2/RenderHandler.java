package cn.poco.gldraw2;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.lang.ref.WeakReference;

/**
 * Created by zwq on 2017/07/24 11:48.<br/><br/>
 */

public class RenderHandler extends Handler {

    private static final String TAG = "vvv RenderHandler";
    private final boolean mIsDebug = false;

    public static final int MSG_SET_CAMERA_SURFACE = 0x1001;
    public static final int MSG_ON_SURFACE_CREATE = 0x1002;
    public static final int MSG_ON_SURFACE_CHANGE = 0x1003;
    public static final int MSG_ON_SURFACE_DESTROY = 0x1004;
    public static final int MSG_QUIT_THREAD = 0x1005;
    public static final int MSG_SET_CAMERA_SIZE = 0x1007;
    public static final int MSG_UPDATE_TEX_IMAGE = 0x1008;
    public static final int MSG_UPDATE_FRAME = 0x1009;
    public static final int MSG_SET_SURFACE_LISTENER = 0x1010;

    public static final int MSG_RUNNABLE_TASK = 0x1050;//4176
    public static final int MAX_TASK_COUNT = 20;
    private int mTaskCount;

    private WeakReference<RenderThread> mWeakRender;
    private boolean mIsQuitThread;
    private final int mMaxUpdateCount = 10;//最多10帧在队列中
    private int mUpdateTexImageCount;
    private int mUpdateFrameCount;

    public RenderHandler(RenderThread thread) {
        mWeakRender = new WeakReference<RenderThread>(thread);
        mIsQuitThread = false;
        mTaskCount = 0;
        mUpdateTexImageCount = 0;
        mUpdateFrameCount = 0;
    }

    private RenderThread getRenderThread() {
        if (mWeakRender != null) {
            return mWeakRender.get();
        }
        return null;
    }

    public void clearAll() {
        mIsQuitThread = true;
        mWeakRender = null;
    }

    @Override
    public void handleMessage(Message msg) {
        handleMessageHasReturn(msg);
    }

    public boolean handleMessageHasReturn(Message msg) {
        RenderThread renderThread = getRenderThread();
        if (renderThread == null) return false;

        switch (msg.what) {
            case MSG_SET_CAMERA_SURFACE:
                renderThread.setCameraSurface();
                return true;
            case MSG_ON_SURFACE_CREATE:
                renderThread.onSurfaceCreate();
                return true;
            case MSG_ON_SURFACE_CHANGE:
                renderThread.onSurfaceChange(msg.arg1, msg.arg2);
                return true;
            case MSG_ON_SURFACE_DESTROY:
                renderThread.onSurfaceDestroy();
                return true;
            case MSG_QUIT_THREAD:
                renderThread.quitThread(true);
                return true;
            case MSG_SET_CAMERA_SIZE:
                renderThread.setCameraSize(msg.arg1, msg.arg2);
                return true;
            case MSG_UPDATE_TEX_IMAGE:
                if (mUpdateTexImageCount > 0) {
                    mUpdateTexImageCount--;
                }
                renderThread.updateTexImage();
                return true;
            case MSG_UPDATE_FRAME:
                if (mUpdateFrameCount > 0) {
                    mUpdateFrameCount--;
                }
                renderThread.updateFrame();
                return true;
            case MSG_SET_SURFACE_LISTENER:
                renderThread.setSurfaceListener();
                return true;

            /*case MSG_RUNNABLE_TASK:
            default: {
                if (mIsDebug) {
                    Log.i(TAG, "handleMessage: what -> " + msg.what);
                }
                if (msg.obj != null && msg.obj instanceof RenderRunnable) {
                    ((RenderRunnable) msg.obj).run(renderThread);
                }
            }
            return false;*/
        }
        if (msg.what >= MSG_RUNNABLE_TASK && msg.what <= MSG_RUNNABLE_TASK + MAX_TASK_COUNT) {
            if (mIsDebug) {
                Log.i(TAG, "handleMessage: what -> " + msg.what);
            }
            if (msg.obj != null && msg.obj instanceof RenderRunnable) {
                ((RenderRunnable) msg.obj).run(renderThread);
            }
            return true;
        }
        return false;
    }

    public int sendMsg(RenderRunnable runnable, int delay) {
        if (!mIsQuitThread && runnable != null) {
            if (mTaskCount >= MAX_TASK_COUNT) {
                mTaskCount = 0;
            }
            int what = MSG_RUNNABLE_TASK + mTaskCount;
            if (delay == 0) {
                pauseUpdate(0);
            }
            sendMessageDelayed(obtainMessage(MSG_RUNNABLE_TASK + mTaskCount, runnable), delay);
            mTaskCount++;
            return what;
        }
        return -1;
    }

    public int sendMsg(RenderRunnable runnable) {
        return sendMsg(runnable, 0);
    }

    private int sendMsg(int what) {
        if (!mIsQuitThread) {
            if (what == MSG_QUIT_THREAD) {
                mIsQuitThread = true;
                pauseUpdate(0);
            }
            sendEmptyMessage(what);
            return what;
        }
        return -1;
    }

    public void setCameraSurface() {
        sendMsg(MSG_SET_CAMERA_SURFACE);
    }

    public void onSurfaceCreate() {
        sendMsg(MSG_ON_SURFACE_CREATE);
    }

    public void onSurfaceChange(int width, int height) {
        removeMessages(MSG_ON_SURFACE_CHANGE);
        pauseUpdate(0);
        sendMessage(obtainMessage(MSG_ON_SURFACE_CHANGE, width, height));
    }

    public void surfaceDestroy() {
        sendMsg(MSG_ON_SURFACE_DESTROY);
    }

    public void quitThread() {
        sendMsg(MSG_QUIT_THREAD);
    }

    public void setCameraSize(int width, int height) {
        sendMessage(obtainMessage(MSG_SET_CAMERA_SIZE, width, height));
    }

    public void updateTexImage() {
        if (mUpdateTexImageCount > mMaxUpdateCount) {
           pauseUpdate(1);
        }
        int r = sendMsg(MSG_UPDATE_TEX_IMAGE);
        if (r > 0) {
            mUpdateTexImageCount++;
        }
    }

    public void updateFrame() {
        if (mUpdateFrameCount > mMaxUpdateCount) {
            pauseUpdate(2);
        }
        int r = sendMsg(MSG_UPDATE_FRAME);
        if (r > 0) {
            mUpdateFrameCount++;
        }
    }

    private void pauseUpdate(int type) {
        if (type == 0 || type == 1) {
            mUpdateTexImageCount = 0;
            removeMessages(MSG_UPDATE_TEX_IMAGE);
        }
        if (type == 0 || type == 2) {
            mUpdateFrameCount = 0;
            removeMessages(MSG_UPDATE_FRAME);
        }
    }

    public void setSurfaceListener() {
        sendMsg(MSG_SET_SURFACE_LISTENER);
    }

}
