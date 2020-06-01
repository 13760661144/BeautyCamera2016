package cn.poco.gldraw2.core;

/**
 * Created by zwq on 2018/03/01 18:23.<br/><br/>
 */

public abstract class EglSurfaceAbs<SURFACE> {

    public EglSurfaceAbs(EglCoreAbs eglCore) {
    }

    public abstract void createWindowSurface(Object surface);

    public abstract void createOffscreenSurface(int width, int height);

    public abstract SURFACE getEGLSurface();

    public abstract int getWidth();

    public abstract int getHeight();

    public abstract void makeCurrent();

    public abstract void makeCurrentReadFrom(EglSurfaceAbs readSurface);

    public abstract boolean swapBuffers();

    public abstract void setPresentationTime(long nanosecond);

    public void release() {
    }

    public abstract void releaseEglSurface();
}
