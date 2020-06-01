package cn.poco.video;

import cn.poco.video.encoder.MediaMuxerWrapper;

/**
 * Created by zwq on 2016/07/04 16:37.<br/><br/>
 */
public interface OnRecordListener {

    void onPrepare(MediaMuxerWrapper mediaMuxerWrapper);

    void onStart(MediaMuxerWrapper mediaMuxerWrapper);

    void onResume();

    void onProgressChange(float progress);

    void onPause();

    void onStop(boolean isValid, long duration, String filePath);
}
