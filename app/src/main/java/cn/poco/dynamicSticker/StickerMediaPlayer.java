package cn.poco.dynamicSticker;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.text.TextUtils;

/**
 * 音效的MediaPlayer对象
 *
 * @author lmx
 *         Created by lmx on 2017/7/12.
 */

public class StickerMediaPlayer implements ISoundResource, MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnSeekCompleteListener, MediaPlayer.OnErrorListener
{
    //base
    protected Context mContext;
    protected float mDuration = -1;   //毫秒

    protected boolean isPrepared;     //是否prepare player
    protected boolean isLooping;      //是否循环
    protected boolean isBuild;        //是否build
    protected boolean isBgmSound;     //bgm音效

    protected static final int STREAM_TYPE = AudioManager.STREAM_MUSIC;
    protected TypeValue.SoundStatus mStatus = TypeValue.SoundStatus.IDLE;

    //callback
    protected OnMediaPlayerListener mListener;

    //sticker sound res
    private StickerSound mStickerSound;

    protected MediaPlayer mPlayer;
    protected Uri mUri;

    protected int mCurrentPos;

    public StickerMediaPlayer(Context context)
    {
        this.mContext = context;
    }

    public StickerMediaPlayer(Context context, Uri uri)
    {
        this.mContext = context;
        this.mUri = uri;
    }

    /**
     * 构造media player对象，并且prepared资源
     */
    public void build()
    {
        build(mUri);
    }

    private void build(Uri uri)
    {
        if (isBuild || uri == null) return;

        try
        {
            release();
            isBuild = true;
            mPlayer = new MediaPlayer();
            mPlayer.setOnCompletionListener(this);
            mPlayer.setOnPreparedListener(this);
            mPlayer.setOnSeekCompleteListener(this);
            mPlayer.setOnErrorListener(this);
            mPlayer.setLooping(isLooping);
            mPlayer.setAudioStreamType(STREAM_TYPE);
            mPlayer.setDataSource(mContext, uri);
            mPlayer.prepareAsync();
        }
        catch (Exception e)
        {
            mPlayer = null;
            isBuild = false;
        }
    }


    public void build(AssetFileDescriptor fd)
    {
        if (isBuild || fd == null) return;

        try
        {
            release();
            isBuild = true;
            mPlayer = new MediaPlayer();
            mPlayer.setOnCompletionListener(this);
            mPlayer.setOnPreparedListener(this);
            mPlayer.setOnSeekCompleteListener(this);
            mPlayer.setOnErrorListener(this);
            mPlayer.setLooping(isLooping);
            mPlayer.setAudioStreamType(STREAM_TYPE);
            mPlayer.setDataSource(fd.getFileDescriptor(), fd.getStartOffset(), fd.getLength());
            mPlayer.prepareAsync();
        }
        catch (Exception e)
        {
            mPlayer = null;
            isBuild = false;
        }
    }

    public void build(String path)
    {
        if (isBuild || TextUtils.isEmpty(path)) return;

        try
        {
            release();
            isBuild = true;
            mPlayer = new MediaPlayer();
            mPlayer.setOnCompletionListener(this);
            mPlayer.setOnPreparedListener(this);
            mPlayer.setOnSeekCompleteListener(this);
            mPlayer.setOnErrorListener(this);
            mPlayer.setLooping(isLooping);
            mPlayer.setAudioStreamType(STREAM_TYPE);
            mPlayer.setDataSource(path);
            mPlayer.prepareAsync();
        }
        catch (Exception e)
        {
            mPlayer = null;
            isBuild = false;
        }
    }


    public MediaPlayer getMediaPlayer()
    {
        return mPlayer;
    }

    public float getDuration()
    {
        return mDuration;
    }

    public void setDataSource(Uri uri)
    {
        this.mUri = uri;
        this.isBuild = false;
        build(uri);
    }

    public void setDataSource(AssetFileDescriptor assetFileDescriptor)
    {
        this.mUri = null;
        this.isBuild = false;
        build(assetFileDescriptor);
    }

    public void setDataSource(String path)
    {
        this.mUri = null;
        this.isBuild = false;
        build(path);
    }

    public void setListener(OnMediaPlayerListener listener)
    {
        this.mListener = listener;
    }

    public void setLooping(boolean isLooping)
    {
        this.isLooping = isLooping;
        if (mPlayer != null)
        {
            mPlayer.setLooping(isLooping);
        }
    }

    public void setBgmSound(boolean bgmSound)
    {
        this.isBgmSound = bgmSound;
    }

    public boolean isBgmSound()
    {
        return isBgmSound;
    }

    public boolean isPrepared()
    {
        return isPrepared;
    }

    public void setVolume(float left, float right)
    {
        try
        {
            if (mPlayer != null)
            {
                mPlayer.setVolume(left, right);
            }
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
    }

    public void setVolume(float volume)
    {
        setVolume(volume, volume);
    }


    public boolean isPlaying()
    {
        return this.mStatus.isPlaying();
    }

    public boolean isStop()
    {
        return this.mStatus.isStop();
    }

    public TypeValue.SoundStatus getStatus()
    {
        return mStatus;
    }

    public void release()
    {
        if (mPlayer != null)
        {
            try
            {
                pause();
                mPlayer.setOnCompletionListener(null);
                mPlayer.setOnPreparedListener(null);
                mPlayer.setOnSeekCompleteListener(null);
                mPlayer.setOnErrorListener(null);
                mPlayer.stop();
                mPlayer.reset();
                mPlayer.release();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            finally
            {
                mPlayer = null;
                isBuild = false;
                resetStatus();
            }
        }
    }

    public void clear()
    {
        release();
        mStickerSound = null;
        mListener = null;
        mContext = null;
    }

    private void resetStatus()
    {
        this.mStatus = TypeValue.SoundStatus.IDLE;
    }

    public void pause()
    {
        if (mPlayer != null)
        {
            if (isPlaying() || mPlayer.isPlaying())
            {
                if (isStop())
                {
                    return;
                }

                try
                {
                    mCurrentPos = mPlayer.getCurrentPosition();
                    mPlayer.pause();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
        mStatus = TypeValue.SoundStatus.STOP;
    }

    public void reset()
    {
        if (mPlayer != null)
        {
            try
            {
                pause();

//                mPlayer.reset();
                mPlayer.seekTo(0);

                resetStatus();
            }
            catch (Exception ignored)
            {

            }
        }
    }

    /**
     * @return true 开始播放，false 出错或正在播放
     */
    public boolean start()
    {
        if (mPlayer != null)
        {
            try
            {
                if (!mPlayer.isPlaying() && !isPlaying() && isPrepared)
                {
                    mPlayer.start();
                    mStatus = TypeValue.SoundStatus.PLAYING;

                    if (mListener != null)
                    {
                        mListener.onStart(this, this.mPlayer, System.currentTimeMillis());
                    }
                    return true;
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    public void reStart()
    {
        // TODO: 2017/7/19 StickerMediaPlayer --> reStart
    }

    public void seekTo(int msec)
    {
        seekTo(msec, true);
    }

    public void seekTo(int msec, boolean resume)
    {
        if (mPlayer != null)
        {
            mCurrentPos = msec;
            mPlayer.seekTo(msec);
            if (resume)
            {
                resume();
            }
        }
    }

    public void setCurrentPos(int mCurrentPos)
    {
        this.mCurrentPos = mCurrentPos;
    }

    /**
     * 当player pause后resume才有效
     */
    public void resume()
    {
        resume(false);
    }

    /**
     * 当player pause后resume才有效
     */
    public void resume(boolean reStart)
    {
        if (mPlayer != null)
        {
            try
            {
                if (isStop() && isPrepared())
                {
                    if (reStart) mCurrentPos = 0;
                    mPlayer.seekTo(mCurrentPos);
                    if (!mPlayer.isPlaying())
                    {
                        mPlayer.start();
                    }
                    mStatus = TypeValue.SoundStatus.PLAYING;
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * 1.当播放完成的时候会被回调<br/>
     * 2.当mMediaPlayer.setDataSource(）;方法没有调用，使用 mMediaPlayer.getDuration()<br/>
     * 3.当mMediaPlayer.setDataSource(）;方法没有调用，使用mMediaPlayer.seekto();的时候<br/>
     *
     * @param mediaPlayer
     */
    @Override
    public void onCompletion(MediaPlayer mediaPlayer)
    {
        resetStatus();

        if (mListener != null)
        {
            mListener.onCompletion(this, mediaPlayer, System.currentTimeMillis());
        }
    }

    @Override
    public void onSeekComplete(MediaPlayer mediaPlayer)
    {
        if (mListener != null)
        {
            mListener.onSeekComplete(this, mediaPlayer);
        }
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer)
    {
        isPrepared = true;

        if (mListener != null)
        {
            mListener.onPrepared(this, mediaPlayer);
        }

        this.mDuration = mediaPlayer.getDuration() * 1.0f;
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra)
    {
        isPrepared = false;

        if (mListener != null)
        {
            return mListener.onError(this, mp, what, extra);
        }
        return false;
    }

    @Override
    public void setStickerSound(StickerSound sound)
    {
        this.mStickerSound = sound;
    }

    @Override
    public StickerSound getStickerSound()
    {
        return mStickerSound;
    }


    public interface OnMediaPlayerListener
    {
        public void onCompletion(StickerMediaPlayer stickerMediaPlayer, MediaPlayer mediaPlayer, long completeMillis);

        public void onPrepared(StickerMediaPlayer stickerMediaPlayer, MediaPlayer mediaPlayer);

        public boolean onError(StickerMediaPlayer stickerMediaPlayer, MediaPlayer mp, int what, int extra);

        public void onSeekComplete(StickerMediaPlayer stickerMediaPlayer, MediaPlayer mediaPlayer);

        public void onStart(StickerMediaPlayer stickerMediaPlayer, MediaPlayer mediaPlayer, long startMillis);
    }
}
