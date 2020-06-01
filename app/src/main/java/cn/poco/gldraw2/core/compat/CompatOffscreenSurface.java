package cn.poco.gldraw2.core.compat;

/**
 * Created by zwq on 2018/03/01 18:23.<br/><br/>
 */

public class CompatOffscreenSurface extends CompatEglSurfaceBase {

    public CompatOffscreenSurface(CompatEglCore eglCore, int width, int height) {
        super(eglCore);
        createOffscreenSurface(width, height);
    }

    public void release() {
        releaseEglSurface();
    }
}
