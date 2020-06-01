package cn.poco.video.encoder;

/**
 * Created by zwq on 2016/06/22 15:43.<br/><br/>
 */
public interface IFrameRenderer {

    void makeCurrent();

    void setProjectionMatrix(float[] src);

    void setViewport();

    void swapBuffers();
}
