package cn.poco.gldraw2.core;

public abstract class EglCoreAbs<CONTEXT, CONFIG, SURFACE> {

    public EglCoreAbs() {
        this(null, 0);
    }

    public EglCoreAbs(CONTEXT sharedContext, int flags) {
        //must overwrite this method
    }

    public abstract int[] chooseConfig(CONFIG[] configs, int flags, int renderableType);

    public abstract void release();

    public abstract void releaseSurface(SURFACE eglSurface);

    public abstract SURFACE createWindowSurface(Object surface);

    public abstract SURFACE createOffscreenSurface(int width, int height);

    public abstract void makeCurrent(SURFACE eglSurface);

    public abstract void makeCurrent(SURFACE drawSurface, SURFACE readSurface);

    public abstract void makeNothingCurrent();

    public abstract boolean swapBuffers(SURFACE eglSurface);

    public abstract void setPresentationTime(SURFACE eglSurface, long nanosecond);

    public abstract boolean isCurrent(SURFACE eglSurface);

    public abstract int querySurface(SURFACE eglSurface, int what);

    public abstract String queryString(int what);

    public int getGlVersion() {
        return -1;
    }
}
