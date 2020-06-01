package cn.poco.camera3.cb;

/**
 * @author Gxx
 *      Created by Gxx on 2017/8/23.
 */

public interface ShutterAnimListener
{
    // 快门动画监听
    void onShutterAnimStart(int mode);

    void onShutterAnimEnd(int mode);

    void onShutterAnimCancel();
}
