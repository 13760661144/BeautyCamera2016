package cn.poco.video;

import cn.poco.video.encoder.MediaMuxerWrapper;

/**
 * @author lmx
 *         Created by lmx on 2017/6/29.
 */

public interface OnRecordMixListener
{
    void onPrepare(MediaMuxerWrapper mediaMuxerWrapper, long millis);

    void onStart(MediaMuxerWrapper mediaMuxerWrapper, long millis);

    void onResume();

    void onPause();

    void onStop(boolean isValid, long duration, String filePath, long millis);

    void onAudioStart(MediaMuxerWrapper mediaMuxerWrapper, long millis, int audioDuration);

    void onAudioEnd(MediaMuxerWrapper mediaMuxerWrapper, long millis, int audioDuration);
}
