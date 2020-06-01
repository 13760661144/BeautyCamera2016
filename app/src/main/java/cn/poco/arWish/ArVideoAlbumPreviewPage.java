package cn.poco.arWish;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.Toast;

import com.pili.pldroid.player.PLMediaPlayer;
import com.pili.pldroid.player.widget.PLVideoView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

import cn.poco.advanced.ImageUtils;
import cn.poco.album.model.VideoInfo;

import cn.poco.album.site.AlbumSite100;
import cn.poco.arWish.widget.MediaControllerBar;
import cn.poco.camera3.ui.drawable.RoundRectDrawable;
import cn.poco.camera3.util.CameraPercentUtil;
import cn.poco.statistics.MyBeautyStat;
import cn.poco.tianutils.ShareData;
import cn.poco.utils.OnAnimationClickListener;
import my.beautyCamera.R;

/**
 * Created by Gxx on 2018/1/29.
 */

public class ArVideoAlbumPreviewPage extends FrameLayout implements View.OnClickListener
{
    private String mVideoSum;
    private String mVideoPath;

    private ImageView mBackBtn;
    private TextView mTitle;
    private TextView mContinueBtn;
    private ViewPager mViewPager;

    private FrameLayout mPreviewLayout;
    private View mCoverView;
    private PLVideoView mVideoView;

    private OnAnimationClickListener mAnimClickListener;
    private ViewPager.OnPageChangeListener mPagerChangeListener;
    private ArAlbumPreviewAdapter.OnClickListener mPagerItemSelectedListener;

    private int mCurrentIndex;

    private FixedSpeedScroller mScroller;

    private AlbumSite100 mSite;
    private boolean mPause;
    private MediaControllerBar mMediaController;

    public ArVideoAlbumPreviewPage(Context context, AlbumSite100 site)
    {
        super(context);
        mSite = site;
        setBackgroundColor(Color.WHITE);
        initCB();
        mScroller = new FixedSpeedScroller(context);
        initView(context);
    }

    private void initCB()
    {
        mAnimClickListener = new OnAnimationClickListener()
        {
            @Override
            public void onAnimationClick(View v)
            {
                if (v == mBackBtn)
                {
                    MyBeautyStat.onClickByRes(R.string.AR祝福_送祝福_相册视频预览页_返回);
                    showPreviewPage(false);
                }
                else if (v == mContinueBtn)
                {
                    MyBeautyStat.onClickByRes(R.string.AR祝福_送祝福_相册视频预览页_继续);
                    if (mSite != null)
                    {
                        HashMap<String, Object> params = new HashMap<>();
                        params.put(ARHideWishPrePage.KEY_VIDEO_PATH, mVideoPath);
                        mSite.onVideoSelected(getContext(), params);
                    }

                    MyBeautyStat.onPageEndByRes(R.string.ar祝福_送祝福_相册视频预览页);
                }
            }
        };

        mPagerChangeListener = new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
            {
            }

            @Override
            public void onPageSelected(int position)
            {
                mCurrentIndex = position;
                updateVideoPath(position);
                setTitleMsg(position + 1);
            }

            @Override
            public void onPageScrollStateChanged(int state)
            {
            }
        };

        mPagerItemSelectedListener = new ArAlbumPreviewAdapter.OnClickListener()
        {
            @Override
            public void onSelected(String path)
            {
                playVideo(path, true);
            }
        };
    }

    private void initView(Context context)
    {
        FrameLayout bar = new FrameLayout(context);
        bar.setBackgroundColor(0xf4ffffff);
        bar.setClickable(true);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, CameraPercentUtil.WidthPxToPercent(90));
        addView(bar, params);
        {
            mBackBtn = new ImageView(context);
            mBackBtn.setOnTouchListener(mAnimClickListener);
            mBackBtn.setImageResource(R.drawable.framework_back_btn);
            mBackBtn.setPadding(CameraPercentUtil.WidthPxToPercent(2), 0, CameraPercentUtil.WidthPxToPercent(2), 0);
            ImageUtils.AddSkin(getContext(), mBackBtn);
            params = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
            params.gravity = Gravity.CENTER_VERTICAL;
            bar.addView(mBackBtn, params);

            mTitle = new TextView(context);
            mTitle.setTextColor(0xff333333);
            mTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
            params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER;
            bar.addView(mTitle, params);

            RoundRectDrawable bg = new RoundRectDrawable();
            bg.setRoundRectParams(CameraPercentUtil.WidthPxToPercent(25), CameraPercentUtil.WidthPxToPercent(25));
            bg.setColor(ImageUtils.GetSkinColor());
            mContinueBtn = new TextView(context);
            mContinueBtn.setOnTouchListener(mAnimClickListener);
            mContinueBtn.setBackgroundDrawable(bg);
            mContinueBtn.setText(R.string.ar_album_preview_go_on);
            mContinueBtn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
            mContinueBtn.setTextColor(Color.WHITE);
            mContinueBtn.setGravity(Gravity.CENTER);
            params = new FrameLayout.LayoutParams(CameraPercentUtil.WidthPxToPercent(108), CameraPercentUtil.WidthPxToPercent(50));
            params.gravity = Gravity.END | Gravity.CENTER_VERTICAL;
            params.rightMargin = CameraPercentUtil.WidthPxToPercent(28);
            bar.addView(mContinueBtn, params);
        }

        mViewPager = new ViewPager(context)
        {
            private float mDownX, mDownY;

            @Override
            public boolean dispatchTouchEvent(MotionEvent ev)
            {
                if (ev.getAction() == MotionEvent.ACTION_DOWN)
                {
                    mDownX = ev.getX();
                    mDownY = ev.getY();
                }
                return super.dispatchTouchEvent(ev);
            }

            @Override
            public boolean onInterceptTouchEvent(MotionEvent ev)
            {
                if (ev.getAction() == MotionEvent.ACTION_MOVE)
                {
                    int touchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
                    if (Math.abs(ev.getX() - mDownX) > touchSlop || Math.abs(ev.getY() - mDownY) > touchSlop)
                    {
                        return true;
                    }
                }
                return super.onInterceptTouchEvent(ev);
            }
        };
        setViewPagerScrollSpeed();
        mViewPager.setOffscreenPageLimit(4);
        mViewPager.addOnPageChangeListener(mPagerChangeListener);
        params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.topMargin = CameraPercentUtil.WidthPxToPercent(90);
        addView(mViewPager, params);

        mPreviewLayout = new FrameLayout(context);
        mPreviewLayout.setOnClickListener(this);
        mPreviewLayout.setVisibility(GONE);
        mPreviewLayout.setBackgroundColor(Color.BLACK);
        params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        addView(mPreviewLayout, params);
        {
            mVideoView = new PLVideoView(getContext());
            mVideoView.setLooping(true);

//            mVideoView.setOnPreparedListener(new PLMediaPlayer.OnPreparedListener()
//            {
//                @Override
//                public void onPrepared(PLMediaPlayer plMediaPlayer, int i)
//                {
//                    plMediaPlayer.start();
//                }
//            });

            mVideoView.setOnErrorListener(new PLMediaPlayer.OnErrorListener()
            {
                @Override
                public boolean onError(PLMediaPlayer plMediaPlayer, int errorCode)
                {
                    switch (errorCode)
                    {
//                        case PLMediaPlayer.ERROR_CODE_IO_ERROR:
//                            /**
//                             * SDK will do reconnecting automatically
//                             */
//                            Log.e("xxx", "IO Error!");
//                            return false;
                        case PLMediaPlayer.ERROR_CODE_OPEN_FAILED:
                            stopVideo();
                            hideVideoView();
                            Toast.makeText(getContext(), "播放视频失败", Toast.LENGTH_LONG).show();
                            break;
                        case PLMediaPlayer.ERROR_CODE_SEEK_FAILED:
//                            showToastTips("failed to seek !");
                            playVideo(mVideoPath, true);
                            break;
                        default:
//                            Toast.makeText(getContext(), "视频出错了", Toast.LENGTH_LONG).show();
                            break;
                    }
                    return true;
                }
            });

            params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            params.gravity = Gravity.CENTER;
            mPreviewLayout.addView(mVideoView, params);

            mCoverView = new View(getContext());
            mCoverView.setOnClickListener(this);
            mCoverView.setBackgroundColor(Color.BLACK);
            params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            mPreviewLayout.addView(mCoverView, params);

            ProgressBar progressBar = new ProgressBar(getContext());
            progressBar.setVisibility(GONE);
            params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER;
            mPreviewLayout.addView(progressBar, params);

            mVideoView.setCoverView(mCoverView);
            mVideoView.setBufferingIndicator(progressBar);
        }
    }

    private void updateVideoPath(int position)
    {
        if (mViewPager != null)
        {
            PagerAdapter adapter = mViewPager.getAdapter();
            if (adapter instanceof ArAlbumPreviewAdapter)
            {
                VideoInfo info = ((ArAlbumPreviewAdapter) adapter).getInfoByIndex(position);
                if (info != null)
                {
                    mVideoPath = info.getPath();
                }
            }
        }
    }

    public void setData(ArrayList<VideoInfo> data)
    {
        if (data == null) return;

        mVideoSum = "/" + String.valueOf(data.size());
        setTitleMsg(mCurrentIndex + 1);
        ArAlbumPreviewAdapter adapter = new ArAlbumPreviewAdapter(data);
        adapter.setSelectedListener(mPagerItemSelectedListener);
        mViewPager.setAdapter(adapter);
        updateVideoPath(mCurrentIndex);
    }

    public void setTitleMsg(int value)
    {
        if (mTitle != null)
        {
            String text = String.valueOf(value) + mVideoSum;
            mTitle.setText(text);
        }
    }

    private void playVideo(String path, final boolean playNow)
    {
        if (TextUtils.isEmpty(path))
        {
            return;
        }

        if (mVideoView != null && mPreviewLayout != null)
        {
            mPreviewLayout.setVisibility(VISIBLE);
            mVideoView.setVideoPath(path);
            if (playNow)
            {
                mVideoView.start();
            }
        }
    }

    public void setSelectedIndex(int index)
    {
        if (mViewPager != null)
        {
            if (mCurrentIndex == index) return;

            if (mScroller != null)
            {
                mScroller.mDuration = 0;
            }
            mViewPager.setCurrentItem(index);
        }
    }

    public void showPreviewPage(final boolean show)
    {
        if (show)
        {
            MyBeautyStat.onPageStartByRes(R.string.ar祝福_送祝福_相册视频预览页);
            this.setVisibility(VISIBLE);
        }
        else
        {
            MyBeautyStat.onPageEndByRes(R.string.ar祝福_送祝福_相册视频预览页);
        }
        int start = show ? ShareData.m_screenRealWidth : 0;
        int end = show ? 0 : ShareData.m_screenRealWidth;
        ObjectAnimator anim = ObjectAnimator.ofFloat(this, "translationX", start, end);
        anim.addListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                if (mScroller != null)
                {
                    mScroller.mDuration = 300;
                }
                if (!show)
                {
                    setVisibility(GONE);
                }
            }
        });
        anim.setDuration(300);
        anim.start();
    }

    @Override
    public void onClick(View v)
    {
        if (v == mPreviewLayout || v == mCoverView)
        {
            if (mVideoView != null && mVideoView.isPlaying())
            {
                mPause = false;
                mVideoView.pause();
                mCoverView.setVisibility(VISIBLE);
            }

            mPreviewLayout.setVisibility(GONE);
        }
    }

    public boolean isPreviewVideo()
    {
        return mPreviewLayout != null && mPreviewLayout.getVisibility() == View.VISIBLE && mVideoView != null && mVideoView.isPlaying();
    }

    public void hideVideoView()
    {
        mCoverView.setVisibility(VISIBLE);
        mPreviewLayout.setVisibility(GONE);
    }

    public void stopVideo()
    {
        if (mVideoView != null && mVideoView.isPlaying())
        {
            mPause = false;
            mVideoView.pause();
        }
    }

    public void clearVideo()
    {
        if (mVideoView != null)
        {
            mVideoView.setOnPreparedListener(null);
            mVideoView.setOnErrorListener(null);
            mVideoView.pause();
            mVideoView.stopPlayback();
            mVideoView = null;
        }
    }

    public void pauseVideo()
    {
        if (mVideoView != null && mVideoView.isPlaying())
        {
            mPause = true;
            mVideoView.pause();
        }
    }

    public void resumeVideo()
    {
        if (mVideoView != null && mPause)
        {
            mPause = false;
            mVideoView.start();
        }
    }

    /**
     * 修改viewpager setCurrentItem 滚动速度
     */
    private void setViewPagerScrollSpeed()
    {
        try
        {
            Field scroller = ViewPager.class.getDeclaredField("mScroller");
            scroller.setAccessible(true);

            scroller.set(mViewPager, mScroller);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private static class FixedSpeedScroller extends Scroller
    {
        private int mDuration = 300;

        FixedSpeedScroller(Context context)
        {
            super(context);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int duration)
        {
            super.startScroll(startX, startY, dx, dy, mDuration);
        }
    }
}
