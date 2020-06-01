package cn.poco.video;

import android.support.annotation.FloatRange;
import android.view.SurfaceView;
import android.view.TextureView;

import java.util.ArrayList;

/**
 * @author lmx
 *         Created by lmx on 2017/8/22.
 */

public interface IPlayer
{
    void setSurfaceView(SurfaceView surfaceView);

    void setVideoTextureView(TextureView textureView);

    void setDataSource(ArrayList<String> dataSource);

    void setVolume(@FloatRange(from = 0.0f, to = 1.0f) float volume);

    void prepare();

    void start();

    void pause();

    void resume();

    void seekTo(long millisecond);

    long getCurrentPosition();

    long getDurations();

    void reset();

    void release();
}
