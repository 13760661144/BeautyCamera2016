package cn.poco.dynamicSticker;

import android.content.Context;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * 构造{@link #StickerSoundManager()}后{@link #init(Context)}<br/>
 * <p>
 * http://www.jianshu.com/p/5302ca3ab071<br/>
 *
 * @author lmx
 *         Created by lmx on 2017/7/17.
 */

public class StickerSoundManager implements AudioManager.OnAudioFocusChangeListener,
        StickerMediaPlayer.OnMediaPlayerListener,
        HeadSetPlugReceiver.OnHeadSetPlugListener,
        VolumeChangeReceiver.OnVolumeChangedListener
{
    private static final String TAG = "bbb";

    //base
    private AudioManager mAudioManager;

    private int mLastStreamVolume;
    private int mMuteStreamVolume;
    private boolean isMusicActive;
    private boolean isGifMode;

    private boolean isStickerMute;//贴纸是否静音

    //callback
    private StickerSoundMPListener mListener;
    private SparseArray<StickerMediaPlayer.OnMediaPlayerListener> mMediaListener;

    //cache
    private LinkedHashMap<TypeValue.SoundType, ArrayList<BaseSound>> mPlayerMaps;
    private BgmSoundCaches mBgmSoundCaches;
    private MyCountDownTimer mCountDownTimer;

    //headset receiver
    private HeadSetPlugReceiver mHeadSetPlugReceiver;
    private boolean mHeadSetIn;

    //volume change receiver
    private VolumeChangeReceiver mVolumeChangeReceiver;

    /**
     * init 注册监听器
     *
     * @param context
     */
    public void init(@NonNull Context context)
    {
        //unregisterHeadSetReceiver(context);
        //unregisterVolumeChangeReceiver(context);

        if (mAudioManager != null)
        {
            abandonAudioFocus();
            mAudioManager = null;
        }

        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int maxStreamVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        mLastStreamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        isStickerMute = mLastStreamVolume <= mMuteStreamVolume;
        //Log.d(TAG, "StickerSoundManager --> init: maxStreamVolume:" + maxStreamVolume + ", mLastStreamVolume:" + mLastStreamVolume);

        registerVolumeChangeReceiver(context);
        requestAudioFocus();
    }

    public void setMediaPlayerListener(StickerSoundMPListener listener)
    {
        this.mListener = listener;
    }

    public void setIsGifMode(boolean isGifMode)
    {
        this.isGifMode = isGifMode;
    }

    public StickerSoundManager()
    {
        mMuteStreamVolume = 0;

        //maps
        if (mPlayerMaps == null)
        {
            mPlayerMaps = new LinkedHashMap<>();
        }
        else
        {
            releaseAllPlayList();
            mPlayerMaps.clear();
        }

        //listener
        if (mMediaListener == null)
        {
            mMediaListener = new SparseArray<>();
        }
        else
        {
            mMediaListener.clear();
        }

        //cache
        if (mBgmSoundCaches == null)
        {
            mBgmSoundCaches = new BgmSoundCaches();
        }
        else
        {
            mBgmSoundCaches.release();
        }

        //time task
        {
            cancelTimeTask();
        }
    }


    /**
     * 请求音频焦点
     */
    private void requestAudioFocus()
    {
//        isMusicActive = mAudioManager.isMusicActive();
//        if (isMusicActive)
//        {
//            int result = mAudioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
//
//            if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED)
//            {
//                // 请求成功
//                Log.d(TAG, "StickerSoundManager --> requestAudioFocus: AUDIOFOCUS_REQUEST_GRANTED");
//            }
//            else if (result == AudioManager.AUDIOFOCUS_REQUEST_FAILED)
//            {
//                // 请求失败
//                Log.d(TAG, "StickerSoundManager --> requestAudioFocus: AUDIOFOCUS_REQUEST_FAILED");
//            }
//        }
    }

    /**
     * 放弃音频焦点
     */
    private void abandonAudioFocus()
    {
//        if (isMusicActive)
//        {
//            mAudioManager.abandonAudioFocus(this);
//        }
    }

    private void registerHeadSetReceiver(Context context)
    {
        if (context != null)
        {
            mHeadSetPlugReceiver = new HeadSetPlugReceiver(this);
            IntentFilter intentFilter = new IntentFilter(HeadSetPlugReceiver.ACTION);
            context.registerReceiver(mHeadSetPlugReceiver, intentFilter);
        }
    }

    private void unregisterHeadSetReceiver(Context context)
    {
        if (context != null && mHeadSetPlugReceiver != null)
        {
            mHeadSetPlugReceiver.setListener(null);
            context.unregisterReceiver(mHeadSetPlugReceiver);
            mHeadSetPlugReceiver = null;
        }
    }

    private void registerVolumeChangeReceiver(Context context)
    {
        if (context != null && mVolumeChangeReceiver == null)
        {
            mVolumeChangeReceiver = new VolumeChangeReceiver(this);
            IntentFilter intentFilter = new IntentFilter(VolumeChangeReceiver.ACTION);
            context.registerReceiver(mVolumeChangeReceiver, intentFilter);
        }
    }

    private void unregisterVolumeChangeReceiver(Context context)
    {
        if (context != null && mVolumeChangeReceiver != null)
        {
            mVolumeChangeReceiver.setListener(null);
            context.unregisterReceiver(mVolumeChangeReceiver);
            mVolumeChangeReceiver = null;
        }
    }


    /**
     * 判断耳机是否插入
     *
     * @return
     */
    public boolean isHeadSetIn()
    {
        return mHeadSetIn;
    }


    /**
     * 构造音效资源对象list（需要主线程）
     *
     * @param stickerSoundRes
     * @return
     */
    public synchronized boolean setStickerSoundRes(@NonNull Context context, StickerSoundRes stickerSoundRes)
    {
        //释放之前的media player资源
        releaseAllPlayList();
        cancelTimeTask();

        if (stickerSoundRes != null && stickerSoundRes.mStickerSounds != null)
        {
            for (StickerSound stickerSound : stickerSoundRes.mStickerSounds)
            {
                BaseSound baseSound = createMediaPlayer(context, stickerSound);
                if (baseSound != null)
                {
                    //先添加到list集合，再prepare资源
                    addMediaPlayer(baseSound, stickerSound.getSoundType());
                    baseSound.build();
                }
            }
        }
        return true;
    }


    /**
     * 音效开关 （{@link MediaPlayer#setVolume(float, float)}）
     *
     * @param mute      静音
     * @param setPlayer 设置media play音量
     */
    public synchronized boolean setStickerMute(boolean mute, boolean setPlayer)
    {
        isStickerMute = mute;
        if (mPlayerMaps != null && setPlayer)
        {
            for (TypeValue.SoundType key : mPlayerMaps.keySet())
            {
                ArrayList<BaseSound> baseSounds = mPlayerMaps.get(key);
                if (baseSounds != null)
                {
                    for (BaseSound baseSound : baseSounds)
                    {
                        if (baseSound != null)
                        {
                            float volume = mute ? 0F : 1F;
                            baseSound.setVolume(volume, volume);
                        }
                    }
                }
            }
        }
        return true;
    }

    public synchronized boolean isStickerMute()
    {
        return isStickerMute;
    }


    /**
     * 返回音效状态
     *
     * @param soundType
     */
    public synchronized TypeValue.SoundStatus getStickerSoundStatus(TypeValue.SoundType soundType)
    {
        if (soundType != null)
        {
            ArrayList<BaseSound> list = getSound(soundType);
            if (list != null)
            {
                for (BaseSound baseSound : list)
                {
                    if (baseSound != null && baseSound.getPlayer() != null)
                    {
                        return baseSound.getPlayer().getStatus();
                    }
                }
            }
        }
        return null;
    }

    /**
     * 创建media player资源对象
     *
     * @param context
     * @param stickerSound
     * @return
     */
    private BaseSound createMediaPlayer(Context context, StickerSound stickerSound)
    {
        if (stickerSound != null)
        {
            if (stickerSound.isBGMSound())//bgm音效
            {
                BgmSound bgmSound = new BgmSound(context, stickerSound.getResourceUri());
                bgmSound.setListener(this);
                bgmSound.setBgmSound(true);//是否为bgm音效
                bgmSound.setLooping(true);//是否循环播放
                bgmSound.setStickerSound(stickerSound);
                bgmSound.setSoundType(stickerSound.getSoundType());
                bgmSound.create();
                return bgmSound;
            }
            else
            {
                EffectSound effectSound = new EffectSound(context, stickerSound.getResourceUri());
                effectSound.setListener(this);
                effectSound.setBgmSound(false);//是否为bgm音效
                effectSound.setLooping(false);//是否循环播放
                effectSound.setStickerSound(stickerSound);
                effectSound.setSoundType(stickerSound.getSoundType());
                effectSound.setSolo(stickerSound.isSolo);
                effectSound.setBgmContinue(stickerSound.isBgmContinue);
                effectSound.setTriggerAction(stickerSound.mActionTriggerType);
                effectSound.setActionTrigger(stickerSound.isActionTrigger);
                effectSound.create();
                return effectSound;
            }
        }
        return null;
    }

    /**
     * @param effectSound 添加音效对象到对应list
     * @param soundType
     */
    private void addMediaPlayer(BaseSound effectSound, TypeValue.SoundType soundType)
    {
        ArrayList<BaseSound> playList = null;
        if (mPlayerMaps != null)
        {
            if (mPlayerMaps.containsKey(soundType))
            {
                playList = mPlayerMaps.get(soundType);
            }

            if (playList == null)
            {
                playList = new ArrayList<>();
                mPlayerMaps.put(soundType, playList);
            }
            playList.add(effectSound);
        }
    }


    /**
     * 释放音效media player资源对象
     *
     * @param src
     * @param soundType
     */
    public void releasePlayList(ArrayList<StickerMediaPlayer> src, TypeValue.SoundType soundType)
    {
        if (src != null)
        {
            Iterator<StickerMediaPlayer> iterator = src.iterator();
            while (iterator.hasNext())
            {
                StickerMediaPlayer player = iterator.next();
                if (player != null)
                {
                    player.release();
                    player.clear();
                    player = null;
                }
                iterator.remove();
            }
        }
    }


    public synchronized void releaseStickerSound()
    {
        releaseAllPlayList();
        cancelTimeTask();
        clearBgmCaches();
    }

    /**
     * 释放全部音效media player资源对象
     */
    private void releaseAllPlayList()
    {
        if (mPlayerMaps != null)
        {
            for (TypeValue.SoundType key : mPlayerMaps.keySet())
            {
                ArrayList<BaseSound> baseSounds = mPlayerMaps.get(key);
                if (baseSounds != null)
                {
                    Iterator<BaseSound> valueIterator = baseSounds.iterator();
                    while (valueIterator.hasNext())
                    {
                        BaseSound baseSound = valueIterator.next();
                        if (baseSound != null)
                        {
                            baseSound.release();
                            baseSound.clear();
                            baseSound = null;
                        }
                        valueIterator.remove();
                    }
                }
                //keyIterator.remove();
            }
        }
    }

    public static StickerSound CheckHasStickerBgm(StickerSoundRes stickerSoundRes)
    {
        if (stickerSoundRes != null && stickerSoundRes.mStickerSounds != null)
        {
            for (StickerSound stickerSound : stickerSoundRes.mStickerSounds)
            {
                if (stickerSound != null && stickerSound.isBGMSound())
                {
                    return stickerSound;
                }
            }
        }
        return null;
    }


    public boolean checkHasBgmSound()
    {
        if (mPlayerMaps != null)
        {
            ArrayList<BaseSound> baseSounds = mPlayerMaps.get(TypeValue.SoundType.BGM);
            if (baseSounds != null && baseSounds.size() > 0)
            {
                return true;
            }
        }
        return false;
    }

    public synchronized void startBgmSound()
    {
        ArrayList<BaseSound> list = getSound(TypeValue.SoundType.BGM);
        if (list != null)
        {
            for (BaseSound baseSound : list)
            {
                if (baseSound != null && baseSound instanceof BgmSound)
                {
                    baseSound.start();
                    break;
                }
            }
        }
    }

    public synchronized BaseSound pauseBgmSound()
    {
        ArrayList<BaseSound> list = getSound(TypeValue.SoundType.BGM);
        if (list != null)
        {
            for (BaseSound baseSound : list)
            {
                if (baseSound != null)
                {
                    baseSound.pause();
                    return baseSound;
                }
            }
        }
        return null;
    }


    public synchronized BaseSound seekToBgmSound(int msec)
    {
        ArrayList<BaseSound> list = getSound(TypeValue.SoundType.BGM);
        if (list != null)
        {
            for (BaseSound baseSound : list)
            {
                if (baseSound != null)
                {
                    baseSound.seekTo(msec);
                    return baseSound;
                }
            }
        }
        return null;
    }

    public synchronized void resumeBgmSound()
    {
        ArrayList<BaseSound> list = getSound(TypeValue.SoundType.BGM);
        if (list != null)
        {
            for (BaseSound baseSound : list)
            {
                if (baseSound != null)
                {
                    baseSound.start();
                }
            }
        }
    }


    public synchronized void onPause(Context context)
    {
        abandonAudioFocus();
//        unregisterHeadSetReceiver(context);
        unregisterVolumeChangeReceiver(context);
        if (mPlayerMaps != null)
        {
            for (Map.Entry<TypeValue.SoundType, ArrayList<BaseSound>> entry : mPlayerMaps.entrySet())
            {
                if (entry != null)
                {
                    ArrayList<BaseSound> list = entry.getValue();
                    if (list != null)
                    {
                        for (BaseSound baseSound : list)
                        {
                            if (baseSound != null)
                            {
                                baseSound.pause();
                            }
                        }
                    }
                }
            }
        }
    }

    public synchronized void onResume(Context context)
    {
        requestAudioFocus();
//        registerHeadSetReceiver(context);
        registerVolumeChangeReceiver(context);
        //重新检查是否静音
        boolean isMute = resumeSoundMute();
        if (mPlayerMaps != null)
        {
            for (Map.Entry<TypeValue.SoundType, ArrayList<BaseSound>> entry : mPlayerMaps.entrySet())
            {
                if (entry != null)
                {
                    ArrayList<BaseSound> list = entry.getValue();
                    if (list != null)
                    {
                        for (BaseSound baseSound : list)
                        {
                            if (baseSound != null)
                            {
                                if (baseSound instanceof BgmSound)
                                {   //resume bgm音效
                                    float v = isMute ? 0f : 1f;
                                    baseSound.setVolume(v, v);
                                    baseSound.resume();
                                }
                                else if (baseSound instanceof EffectSound)
                                {
                                    //重置effect音效
                                    float v = isMute ? 0f : 1f;
                                    baseSound.setVolume(v, v);
                                    baseSound.reset();
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * @return true: 静音
     */
    private boolean resumeSoundMute()
    {
        boolean isMute = false;
        if (mAudioManager != null)
        {
//            //判断音量键调节
//            int streamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
//            if (streamVolume > mMuteStreamVolume)
//            {
//                mLastStreamVolume = streamVolume;
//            }
//
//            boolean tempIsStickerMute = streamVolume <= mMuteStreamVolume;
//
//            //调节音量按钮时候，设置media 是否静音
//            if (isStickerMute != tempIsStickerMute)
//            {
//                isMute = tempIsStickerMute;
//            }
//            isStickerMute = tempIsStickerMute;

            isMute = isStickerMute;

            if (mListener != null)
            {
                mListener.onVolumeChanged(AudioManager.STREAM_MUSIC, mLastStreamVolume, isStickerMute);
            }
        }
        return isMute;
    }


    /**
     * 人脸动作触发音效（需要在主线程）
     *
     * @param faceAction {@link FaceAction}
     */
    public synchronized void startFaceActionSound(String faceAction)
    {
        if (!TextUtils.isEmpty(faceAction))
        {
            TypeValue.TriggerType triggerType = TypeValue.TriggerType.HasType(faceAction);
            if (triggerType != null && triggerType.isFaceAction())//动作音效
            {
                ArrayList<BaseSound> list = getSound(TypeValue.SoundType.EFFECT_ACTION);//获取动作音效list
                if (list != null)
                {
                    for (BaseSound baseSound : list)
                    {
                        if (baseSound != null && baseSound instanceof EffectSound)
                        {
                            boolean isActionTrigger = ((EffectSound) baseSound).isActionTrigger;            //是否动作触发
                            TypeValue.TriggerType triggerAction = ((EffectSound) baseSound).mTriggerAction; //动作触发类型


                            if (!isActionTrigger || !triggerAction.isFaceAction())
                            {
                                //非动作触发 continue
                                continue;
                            }

                            if (baseSound.getPlayer() != null && baseSound.getPlayer().isPrepared())
                            {
                                isSoloSound(baseSound);

                                baseSound.start();
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 动画帧触发音效
     */
    public synchronized void startAnimSound()
    {
        ArrayList<BaseSound> list = getSound(TypeValue.SoundType.EFFECT_DELAY);//动画帧音效
        if (list != null)
        {
            for (BaseSound baseSound : list)
            {
                if (baseSound instanceof EffectSound)
                {
                    if (baseSound.getPlayer() != null && baseSound.getPlayer().isPrepared())
                    {
                        isSoloSound(baseSound);

                        baseSound.start();
                    }
                }
            }
        }
    }

    /**
     * 停止动画帧音效
     */
    public synchronized void stopAnimSound()
    {
        ArrayList<BaseSound> list = getSound(TypeValue.SoundType.EFFECT_DELAY);//动画帧音效
        if (list != null)
        {
            for (BaseSound baseSound : list)
            {
                if (baseSound instanceof EffectSound)
                {
                    if (baseSound.getPlayer() != null && baseSound.getPlayer().isPrepared())
                    {
                        baseSound.reset();//pause and reset status
                    }
                }
            }

            //判断是否恢复bgm音效
            isResumeBgmSound();
        }
    }


    /**
     * 判断是否solo音效，暂停bgm音效
     */
    private void isSoloSound(BaseSound baseSound)
    {
        if (baseSound == null) return;

        boolean isSoloSound = baseSound.isSolo;             //独占音效，pause bgm音效
        boolean isBgmContinue = baseSound.isBgmContinue;    //bgm是否继续，isSolo为true才有效
        if (isSoloSound)
        {
            BaseSound pauseBgmSound = pauseBgmSound();      //暂停bgm音效，put到缓存队列，动作音效完成后恢复bgm音效
            boolean b = putCaches(pauseBgmSound, true, isBgmContinue);
            if (b)
            {
                startCountTimeTask((long) baseSound.getPlayer().getDuration());
            }
        }
    }

    /**
     * 判断是否continue bgm音效
     */
    private void isResumeBgmSound()
    {
        BgmSoundCaches.Caches caches = pollCaches();
        if (caches != null && caches.baseSound != null && caches.baseSound instanceof BgmSound)
        {
            boolean isSolo = caches.isSolo;
            boolean isBgmContinue = caches.isBgmContinue;
            if (isSolo && isBgmContinue)
            {
                //Log.d(TAG, "StickerSoundManager --> isResumeBgmSound: ");
                caches.baseSound.start();
            }
        }
    }

    private synchronized ArrayList<BaseSound> getSound(TypeValue.SoundType soundType)
    {
        if (mPlayerMaps != null && soundType != null)
        {
            if (mPlayerMaps.containsKey(soundType))
            {
                return mPlayerMaps.get(soundType);
            }
        }
        return null;
    }


    private synchronized void startCountTimeTask(long millisInFuture)
    {
        cancelTimeTask();

        if (millisInFuture == -1L) return;

        millisInFuture += 200L;//误差延迟

        mCountDownTimer = new MyCountDownTimer(millisInFuture, 1000L);
        mCountDownTimer.start();
    }

    private synchronized void addMediaListener(InnerMediaListener listener)
    {
        if (mMediaListener != null && listener != null)
        {
            int index = mMediaListener.indexOfKey(listener.id);
            if (index < 0)
            {
                mMediaListener.put(listener.id, listener);
            }
            else
            {
                mMediaListener.setValueAt(index, listener);
            }
        }
    }


    private synchronized void removeMediaListener(InnerMediaListener listener)
    {
        if (mMediaListener != null && listener != null)
        {
            int index = mMediaListener.indexOfKey(listener.id);
            if (index >= 0)
            {
                mMediaListener.removeAt(index);
            }
        }
    }

    private synchronized void clearMediaListener()
    {
        if (mMediaListener != null)
        {
            mMediaListener.clear();
        }
    }

    private synchronized boolean putCaches(BaseSound sound, boolean isSolo, boolean isBgmContinue)
    {
        if (mBgmSoundCaches != null)
        {
            BgmSoundCaches.Caches caches = new BgmSoundCaches.Caches(sound, isSolo, isBgmContinue);
            return mBgmSoundCaches.addCache(caches);
        }
        return false;
    }


    private synchronized BgmSoundCaches.Caches pollCaches()
    {
        if (mBgmSoundCaches != null)
        {
            return mBgmSoundCaches.pollCache();
        }
        return null;
    }


    public void clearAll(Context context)
    {
        releaseStickerSound();
        abandonAudioFocus();
        clearMediaListener();

        if (mPlayerMaps != null)
        {
            mPlayerMaps.clear();
        }
        if (mBgmSoundCaches != null)
        {
            mBgmSoundCaches.release();
        }

        mBgmSoundCaches = null;
        mMediaListener = null;
        mPlayerMaps = null;
        mAudioManager = null;
    }

    @Override
    public void onAudioFocusChange(int focusChange)
    {
        if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT)  //暂时失去Audio Focus，并会很快再次获得，可不释饭media资源
        {

        }
        else if (focusChange == AudioManager.AUDIOFOCUS_LOSS)       //失去了Audio Focus，并将会持续很长的时间
        {

        }
    }

    @Override
    public void onCompletion(StickerMediaPlayer stickerMediaPlayer, MediaPlayer mediaPlayer, long completeMillis)
    {
        if (mListener != null)
        {
            mListener.onCompletion(stickerMediaPlayer, mediaPlayer, completeMillis);
        }
    }

    @Override
    public void onPrepared(StickerMediaPlayer stickerMediaPlayer, MediaPlayer mediaPlayer)
    {
        //prepared 完成后设置音量
        if (stickerMediaPlayer != null)
        {
            float volume = isStickerMute ? 0F : 1F;
            stickerMediaPlayer.setVolume(volume);
        }

        if (mListener != null)
        {
            mListener.onPrepared(stickerMediaPlayer, mediaPlayer);
        }
    }

    @Override
    public boolean onError(StickerMediaPlayer stickerMediaPlayer, MediaPlayer mp, int what, int extra)
    {
        if (mListener != null)
        {
            return mListener.onError(stickerMediaPlayer, mp, what, extra);
        }
        return false;
    }

    @Override
    public void onSeekComplete(StickerMediaPlayer stickerMediaPlayer, MediaPlayer mediaPlayer)
    {
        if (mListener != null)
        {
            mListener.onSeekComplete(stickerMediaPlayer, mediaPlayer);
        }
    }

    @Override
    public void onStart(StickerMediaPlayer stickerMediaPlayer, MediaPlayer mediaPlayer, long startMillis)
    {
        if (mListener != null)
        {
            mListener.onStart(stickerMediaPlayer, mediaPlayer, startMillis);
        }
    }


    private void cancelTimeTask()
    {
        if (mCountDownTimer != null)
        {
            mCountDownTimer.cancel();
        }
        mCountDownTimer = null;
    }


    private void clearBgmCaches()
    {
        if (mBgmSoundCaches != null)
        {
            mBgmSoundCaches.release();
        }
    }

    @Override
    public void onHeadSetPlugChange(boolean in)
    {
        this.mHeadSetIn = in;
        //Log.d(TAG, "StickerSoundManager --> onHeadSetPlugChange: " + in);
    }

    @Override
    public void onVolumeChanged()
    {
        if (mAudioManager != null)
        {
            int streamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            if (streamVolume > mMuteStreamVolume)
            {
                mLastStreamVolume = streamVolume;
            }

            boolean tempIsStickerMute = streamVolume <= mMuteStreamVolume;

            //调节音量按钮时候，设置media 是否静音
            if (isStickerMute != tempIsStickerMute)
            {
                setStickerMute(tempIsStickerMute, true);
            }
            isStickerMute = tempIsStickerMute;

            //Log.d(TAG, "StickerSoundManager --> onVolumeChanged: " + streamVolume);
            if (mListener != null)
            {
                mListener.onVolumeChanged(AudioManager.STREAM_MUSIC, mLastStreamVolume, isStickerMute);
            }
        }
    }


    /**
     * 主要用于恢复bgm音效
     */
    protected class MyCountDownTimer extends CountDownTimer
    {
        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        public MyCountDownTimer(long millisInFuture, long countDownInterval)
        {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished)
        {
            //Log.d(TAG, "MyCountDownTimer --> onTick: " + millisUntilFinished);
        }

        @Override
        public void onFinish()
        {
            //Log.d(TAG, "MyCountDownTimer --> onFinish: ");

            isResumeBgmSound();

            cancelTimeTask();
        }
    }


    private class InnerMediaListener implements StickerMediaPlayer.OnMediaPlayerListener
    {
        //sound type value
        private int id = -1;

        public InnerMediaListener(int id)
        {
            this.id = id;
        }

        @Override
        public void onCompletion(StickerMediaPlayer stickerMediaPlayer, MediaPlayer mediaPlayer, long completeMillis)
        {

        }

        @Override
        public void onPrepared(StickerMediaPlayer stickerMediaPlayer, MediaPlayer mediaPlayer)
        {

        }

        @Override
        public boolean onError(StickerMediaPlayer stickerMediaPlayer, MediaPlayer mp, int what, int extra)
        {
            return false;
        }

        @Override
        public void onSeekComplete(StickerMediaPlayer stickerMediaPlayer, MediaPlayer mediaPlayer)
        {

        }

        @Override
        public void onStart(StickerMediaPlayer stickerMediaPlayer, MediaPlayer mediaPlayer, long startMillis)
        {

        }
    }

    public static abstract class StickerSoundMPListener implements StickerMediaPlayer.OnMediaPlayerListener
    {
        @Override
        public void onCompletion(StickerMediaPlayer stickerMediaPlayer, MediaPlayer mediaPlayer, long completeMillis)
        {

        }

        @Override
        public void onPrepared(StickerMediaPlayer stickerMediaPlayer, MediaPlayer mediaPlayer)
        {
            if (stickerMediaPlayer != null)
            {
                if (stickerMediaPlayer.isBgmSound() && stickerMediaPlayer.isPrepared()) //bgm音效
                {
                    //Log.d(TAG, "StickerSoundMPListener --> onPrepared: ");
                    onPreparedBgmSound(stickerMediaPlayer, mediaPlayer);
                }
            }
        }

        @Override
        public boolean onError(StickerMediaPlayer stickerMediaPlayer, MediaPlayer mp, int what, int extra)
        {
            return false;
        }

        @Override
        public void onSeekComplete(StickerMediaPlayer stickerMediaPlayer, MediaPlayer mediaPlayer)
        {

        }

        @Override
        public void onStart(StickerMediaPlayer stickerMediaPlayer, MediaPlayer mediaPlayer, long startMillis)
        {

        }

        //bgm 音效prepared
        public abstract void onPreparedBgmSound(StickerMediaPlayer stickerMediaPlayer, MediaPlayer mediaPlayer);

        public abstract void onVolumeChanged(int streamType, int streamVolume, boolean isMute);
    }

    public static abstract class BaseSound implements ISound, ISoundResource
    {
        protected Uri mUri;
        protected Context mContext;
        protected StickerMediaPlayer mMediaPlayer;
        protected StickerMediaPlayer.OnMediaPlayerListener mListener;
        protected boolean isBgmSound;
        protected boolean isLooping;
        protected boolean isSolo;
        protected boolean isBgmContinue;
        protected TypeValue.SoundType mSoundType;
        protected StickerSound mStickerSound;

        public BaseSound(Context context, Uri uri)
        {
            this.mContext = context;
            this.mUri = uri;
        }

        public StickerMediaPlayer getPlayer()
        {
            return mMediaPlayer;
        }

        public void setLooping(boolean looping)
        {
            isLooping = looping;
        }

        public void setSolo(boolean solo)
        {
            isSolo = solo;
        }

        public void setBgmSound(boolean bgmSound)
        {
            isBgmSound = bgmSound;
        }

        public void setSoundType(TypeValue.SoundType mSoundType)
        {
            this.mSoundType = mSoundType;
        }

        public void setBgmContinue(boolean bgmContinue)
        {
            isBgmContinue = bgmContinue;
        }

        public void setListener(StickerMediaPlayer.OnMediaPlayerListener mListener)
        {
            this.mListener = mListener;
        }

        @Override
        public void seekTo(int msec)
        {
            if (mMediaPlayer != null)
            {
                mMediaPlayer.seekTo(msec);
            }
        }

        public StickerMediaPlayer.OnMediaPlayerListener getListener()
        {
            return this.mListener;
        }

        @Override
        public void setStickerSound(StickerSound sound)
        {
            mStickerSound = sound;
        }

        @Override
        public StickerSound getStickerSound()
        {
            return mStickerSound;
        }

        @Override
        public void reset()
        {
            if (mMediaPlayer != null)
            {
                mMediaPlayer.reset();
            }
        }

        @Override
        public void resume()
        {
            if (mMediaPlayer != null)
            {
                mMediaPlayer.resume();
            }
        }

        @Override
        public void setVolume(float left, float right)
        {
            if (mMediaPlayer != null)
            {
                mMediaPlayer.setVolume(left, right);
            }
        }

        @Override
        public boolean start()
        {
            if (mMediaPlayer != null)
            {
                return mMediaPlayer.start();
            }
            return false;
        }

        @Override
        public void pause()
        {
            if (mMediaPlayer != null)
            {
                mMediaPlayer.pause();
            }
        }

        @Override
        public void release()
        {
            if (mMediaPlayer != null)
            {
                mMediaPlayer.release();
                mMediaPlayer.setListener(null);
            }
            mMediaPlayer = null;
            mListener = null;
        }

        public void clear()
        {
            if (mMediaPlayer != null)
            {
                mMediaPlayer.clear();
            }
            mMediaPlayer = null;
        }

        /**
         * set之后才create
         */
        abstract void create();

        /**
         * create之后才build
         */
        abstract void build();
    }

    //动作、动画音效
    protected static class EffectSound extends BaseSound
    {
        public TypeValue.TriggerType mTriggerAction;
        public boolean isActionTrigger;

        public EffectSound(Context context, Uri uri)
        {
            super(context, uri);
        }

        public void setTriggerAction(TypeValue.TriggerType mTriggerAction)
        {
            this.mTriggerAction = mTriggerAction;
        }

        public void setActionTrigger(boolean actionTrigger)
        {
            isActionTrigger = actionTrigger;
        }

        @Override
        void create()
        {
            mMediaPlayer = new StickerMediaPlayer(mContext, mUri);
            mMediaPlayer.setStickerSound(mStickerSound);
            mMediaPlayer.setListener(mListener);
            mMediaPlayer.setLooping(isLooping);
            mMediaPlayer.setBgmSound(isBgmSound);
        }

        @Override
        void build()
        {
            if (mMediaPlayer != null)
            {
                mMediaPlayer.build();
            }
        }
    }

    //bgm
    protected static class BgmSound extends BaseSound
    {
        public BgmSound(Context context, Uri uri)
        {
            super(context, uri);
            isBgmSound = true;
            isLooping = true;
        }

        @Override
        void create()
        {
            mMediaPlayer = new StickerMediaPlayer(mContext, mUri);
            mMediaPlayer.setStickerSound(mStickerSound);
            mMediaPlayer.setListener(mListener);
            mMediaPlayer.setLooping(isLooping);
            mMediaPlayer.setBgmSound(isBgmSound);
        }

        @Override
        void build()
        {
            if (mMediaPlayer != null)
            {
                mMediaPlayer.build();
            }
        }
    }


    /**
     * 缓存执行的bgm音效
     */
    protected static class BgmSoundCaches
    {
        public static class Caches
        {
            private BaseSound baseSound;
            private boolean isSolo;
            private boolean isBgmContinue;

            public Caches(BaseSound baseSound, boolean isSolo, boolean isBgmContinue)
            {
                this.baseSound = baseSound;
                this.isSolo = isSolo;
                this.isBgmContinue = isBgmContinue;
            }
        }

        private LinkedList<Caches> mCaches;

        public BgmSoundCaches()
        {
            mCaches = new LinkedList<>();
        }

        /**
         * @param caches
         * @return
         */
        public boolean addCache(Caches caches)
        {
            if (mCaches.size() > 0)
            {
                mCaches.clear();
            }

            return mCaches.add(caches);
        }

        public Caches pollCache()
        {
            if (mCaches.size() == 0)
            {
                return null;
            }
            else
            {
                return mCaches.poll();
            }
        }

        public boolean check(BaseSound baseSound)
        {
            if (baseSound != null)
            {
                try
                {
                    Caches pop = mCaches.pop();
                    if (pop != null)
                    {
                        return pop.baseSound == baseSound;
                    }
                }
                catch (Exception e)
                {
                    return false;
                }

            }
            return false;
        }


        public void release()
        {
            mCaches.clear();
        }
    }
}
