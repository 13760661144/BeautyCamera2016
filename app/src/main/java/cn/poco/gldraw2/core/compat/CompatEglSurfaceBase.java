package cn.poco.gldraw2.core.compat;

import android.util.Log;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLSurface;

import cn.poco.gldraw2.core.EglSurfaceAbs;

/**
 * Created by zwq on 2018/03/01 18:23.<br/><br/>
 */

public class CompatEglSurfaceBase extends EglSurfaceAbs<EGLSurface> {

    protected static final String TAG = "CompatEglSurfaceBase";

    protected CompatEglCore mEglCore;

    private EGLSurface mEGLSurface = EGL10.EGL_NO_SURFACE;
    private int mWidth = -1;
    private int mHeight = -1;

    public CompatEglSurfaceBase(CompatEglCore eglCore) {
        super(eglCore);
        mEglCore = eglCore;
    }

    public void createWindowSurface(Object surface) {
        if (mEGLSurface != EGL10.EGL_NO_SURFACE) {
            throw new IllegalStateException("surface already created");
        }
        mEGLSurface = mEglCore.createWindowSurface(surface);
    }

    public void createOffscreenSurface(int width, int height) {
        if (mEGLSurface != EGL10.EGL_NO_SURFACE) {
            throw new IllegalStateException("surface already created");
        }
        mEGLSurface = mEglCore.createOffscreenSurface(width, height);
        mWidth = width;
        mHeight = height;
    }

    public EGLSurface getEGLSurface() {
        return mEGLSurface;
    }

    public int getWidth() {
        if (mWidth < 0) {
            return mEglCore.querySurface(mEGLSurface, EGL10.EGL_WIDTH);
        } else {
            return mWidth;
        }
    }

    public int getHeight() {
        if (mHeight < 0) {
            return mEglCore.querySurface(mEGLSurface, EGL10.EGL_HEIGHT);
        } else {
            return mHeight;
        }
    }

    public void makeCurrent() {
        mEglCore.makeCurrent(mEGLSurface);
    }

    public void makeCurrentReadFrom(CompatEglSurfaceBase readSurface) {
        mEglCore.makeCurrent(mEGLSurface, readSurface.mEGLSurface);
    }

    @Override
    public void makeCurrentReadFrom(EglSurfaceAbs readSurface) {
        makeCurrentReadFrom((CompatEglSurfaceBase) readSurface);
    }

    public boolean swapBuffers() {
        boolean result = mEglCore.swapBuffers(mEGLSurface);
        if (!result) {
            Log.d(TAG, "WARNING: swapBuffers() failed");
        }
        return result;
    }

    public void setPresentationTime(long nanosecond) {
        mEglCore.setPresentationTime(mEGLSurface, nanosecond);
    }

    public void releaseEglSurface() {
        mEglCore.releaseSurface(mEGLSurface);
        mEGLSurface = EGL10.EGL_NO_SURFACE;
        mWidth = mHeight = -1;
    }
}
