package cn.poco.camera;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import my.beautyCamera.R;

public class CameraSound {

    private SoundPool soundPool;
    private HashMap<Integer, Integer> soundPoolMap;
    private int initState;

    private int lastPlayId = -1;
    private int workingSoundId = -1;

    public boolean soundIsBusy = false;
    public int playId = -1;
    public int soundPlayDelay = 3000;

    private Timer mTimer;

    public boolean hasInit() {
        if (soundPool == null || soundPoolMap == null || soundPoolMap.size() == 0) {
            return false;
        }
        return true;
    }

    public void initSounds(Context context) {
        try {
            if (!hasInit()) {
                soundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 100);
                soundPoolMap = new HashMap<Integer, Integer>();
                initState = 1;
                soundPoolMap.put(1, soundPool.load(context, R.raw.self_capture_sound_center, 1));
                soundPoolMap.put(2, soundPool.load(context, R.raw.self_capture_sound_left, 1));
                soundPoolMap.put(3, soundPool.load(context, R.raw.self_capture_sound_up, 1));
                soundPoolMap.put(4, soundPool.load(context, R.raw.self_capture_sound_right, 1));
                soundPoolMap.put(5, soundPool.load(context, R.raw.self_capture_sound_down, 1));

                soundPoolMap.put(6, soundPool.load(context, R.raw.self_capture_sound_on, 1));
                soundPoolMap.put(7, soundPool.load(context, R.raw.self_capture_sound_left_more, 1));
                soundPoolMap.put(8, soundPool.load(context, R.raw.self_capture_sound_up_more, 1));
                soundPoolMap.put(9, soundPool.load(context, R.raw.self_capture_sound_right_more, 1));
                soundPoolMap.put(10, soundPool.load(context, R.raw.self_capture_sound_down_more, 1));

                soundPoolMap.put(11, soundPool.load(context, R.raw.tickta, 1));
                soundPoolMap.put(12, soundPool.load(context, R.raw.focusing, 1));
                if (initState == 0) {
                    soundPoolMap.clear();
                    soundPoolMap = null;
                }
                initState = 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopSound() {
        if (workingSoundId != -1 && soundPool != null) {
            if (mTimer != null) {
                mTimer.cancel();
            }
            soundPool.stop(workingSoundId);
            soundIsBusy = false;
            workingSoundId = -1;
            playId = -1;
            soundPlayDelay = 3000;
        }
    }

    public void playSound(int repeat, int id) {
        playSound(repeat, id, false);
    }

    public void playSound(int repeat, int id, boolean clearFlag) {
        /* 使用正确音量播放声音 */
        if (playId == id)
            return;
        if (soundIsBusy)
            return;

        // 确认过就要拍了
        if (playId == 1)
            return;

        if (soundPool != null && soundPoolMap != null && soundPoolMap.get(id) != null) {
            playId = id;
            soundIsBusy = true;

            if (lastPlayId == id) {
                workingSoundId = soundPool.play(soundPoolMap.get(id + 5), 1.0f, 1.0f, 1, repeat, 1f);
                lastPlayId = id;
            } else {
                workingSoundId = soundPool.play(soundPoolMap.get(id), 1.0f, 1.0f, 1, repeat, 1f);
                if (id == 2 || id == 3 || id == 4 || id == 5) {
                    lastPlayId = id;
                } else {
                    lastPlayId = -1;
                }
            }

            if (mTimer != null) {
                mTimer.cancel();
            }
            mTimer = new Timer("sound");
            mTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (playId != 1) {
                        soundIsBusy = false;
                        workingSoundId = -1;
                        playId = -1;
                    }
                    if (soundPlayDelay != 3000) {
                        soundPlayDelay = 3000;
                    }
                }
            }, soundPlayDelay, soundPlayDelay);
        }
        if (clearFlag) {
            playId = -1;
        }
    }

    public void clearSound() {
        if (soundPoolMap != null) {
            if (initState == 1) {
                initState = 0;
            } else {
                soundPoolMap.clear();
                soundPoolMap = null;
            }
        }
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        soundIsBusy = false;
        workingSoundId = -1;
        playId = -1;
        soundPlayDelay = 3000;
    }
}
