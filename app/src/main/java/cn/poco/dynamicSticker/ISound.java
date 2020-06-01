package cn.poco.dynamicSticker;

/**
 * @author lmx
 *         Created by lmx on 2017/7/14.
 */

public interface ISound
{
    boolean start();

    void pause();

    void resume();

    void reset();

    void release();

    void setVolume(float left, float right);

    void seekTo(int msec);
}
