package cn.poco.gldraw2;

import java.nio.IntBuffer;

/**
 * Created by zwq on 2017/07/26 16:12.<br/><br/>
 */

public interface OnCaptureFrameListener {
    /**
     * @param frameType 0：视频截屏，1：拍照截屏，2：gif截屏
     * @param data
     * @param width
     * @param height
     */
    void onCaptureFrame(int frameType, IntBuffer data, int width, int height);
}
