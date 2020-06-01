package cn.poco.lightApp06;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.VideoView;

import java.io.File;

import cn.poco.tianutils.ShareData;

public class VideoPage extends FrameLayout {
    private VideoView m_video;
    private String m_videoPath;
    private boolean isLooping = true;

    private int m_pos;
    private boolean m_show = false;

    private OnMediaPlayerListener mListener;

    public VideoPage(Context context) {
        this(context, null);
    }

    public VideoPage(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoPage(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Init();
    }

    public void setListener(OnMediaPlayerListener mListener) {
        this.mListener = mListener;
    }

    private void Init() {
        ShareData.InitData(getContext());
        //CommonUtils.CancelViewGPU(this);
        LayoutParams fl = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        fl.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        m_video = new VideoView(getContext());
//        m_video.setZOrderOnTop(true);//防止打开时黑屏
        this.addView(m_video, fl);

        fl = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        final TextView maskView = new TextView(getContext());
        maskView.setBackgroundColor(Color.WHITE);
        this.addView(maskView, fl);

        m_video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                if (maskView != null) {
                    maskView.setVisibility(View.GONE);
                }
                if (m_show) {
                    if (m_video != null) m_video.start();
                }

                if (mListener != null) {
                    mListener.onPrepared(mp);
                }
            }
        });
        m_video.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (isLooping) {
                    if (m_video != null) m_video.start();
                }
                if (mListener != null) {
                    mListener.onCompletion(mp);
                }
            }
        });
        m_video.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            int resumeTimes;
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
//                Log.i("bbb", "onError: what:"+what+", extra:"+extra);
                if (m_video != null && resumeTimes == 0 && what == MediaPlayer.MEDIA_ERROR_UNKNOWN) {
                    resumeTimes++;
                    m_video.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (m_video != null) {
                                m_video.resume();
                            }
                        }
                    }, 150);
                }

                if (mListener != null)
                {
                    return mListener.onError(mp, what, extra);
                }
                return true;
            }
        });
    }

    public void setLooping(boolean isLooping)
    {
        this.isLooping = isLooping;
    }

    public void setVideoPath(String path) {
        if (m_video == null)
            return;
        if (path == null || path.length() <= 0 || !new File(path).exists())
            return;
        m_videoPath = path;
        if (m_video.isPlaying())
            m_video.pause();
        m_video.setVideoPath(m_videoPath);
    }

    public void start() {
        if (m_video == null)
            return;
        m_video.start();
    }

    public void seekTo(int msec)
    {
        if (m_video != null)
        {
            m_pos = msec;
            m_video.seekTo(msec);
        }
    }

    public String getVideoPath()
    {
        return m_videoPath;
    }

    public void setPageShow(boolean show) {
        m_show = show;
    }

    public boolean onBack() {
        return false;
    }

    public boolean onStop() {
        return false;
    }

    public boolean onPause() {
        if (m_video != null) {
            m_pos = m_video.getCurrentPosition();
            if (m_video.isPlaying())
                m_video.pause();
        }
        return false;
    }

    public boolean onDestroy() {
        return false;
    }

    public boolean onStart() {
        return false;
    }

    public boolean onResume() {
        if (m_video != null) {
            m_video.seekTo(m_pos);
            if (!m_video.isPlaying())
                m_video.start();
        }
        return false;
    }

    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        return false;
    }

    public boolean onActivityKeyDown(int keyCode, KeyEvent event) {
        return false;
    }

    public boolean onActivityKeyUp(int keyCode, KeyEvent event) {
        return false;
    }

    public void onClose() {
        m_video = null;
    }

    public void onRestore() {
    }

    public interface OnMediaPlayerListener
    {
        public void onPrepared(MediaPlayer mp);
        public void onCompletion(MediaPlayer mp);
        public boolean onError(MediaPlayer mp, int what, int extra);
    }
}
