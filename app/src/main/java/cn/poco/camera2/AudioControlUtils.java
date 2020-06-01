package cn.poco.camera2;

import android.content.Context;
import android.media.AudioManager;

/**
 * Created by zwq on 2017/01/05 10:06.<br/><br/>
 */
public class AudioControlUtils {

    private static final String TAG = "bbb";
    private static AudioManager sAudioManager;
    private static boolean sIsActive;
    private static boolean mCanResume = true;

    /**
     * 暂停其它播放器的播放
     *
     * @param context
     */
    public static void pauseOtherMusic(Context context) {
        if (context != null && !sIsActive) {
            sAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

            if (sAudioManager != null) {
                sIsActive = sAudioManager.isMusicActive();//判断系统是否有音乐在播放
//                Log.i(TAG, "sIsActive:" + sIsActive);
                if (sIsActive) {
                    int result = sAudioManager.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
//                    if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
//                        Log.d(TAG, "requestAudioFocus successfully.");
//                    } else {
//                        Log.d(TAG, "requestAudioFocus failed.");
//                    }
                }
            }
        }
    }

    public static void setCanResumeMusic(boolean canResume) {
        mCanResume = canResume;
    }

    /**
     * 恢复其它播放器的播放
     *
     * @param context
     */
    public static void resumeOtherMusic(Context context) {
        if (!mCanResume) return;
        if (sIsActive && sAudioManager != null) {
            sAudioManager.abandonAudioFocus(null);
        }
        sAudioManager = null;
        sIsActive = false;
    }
}
