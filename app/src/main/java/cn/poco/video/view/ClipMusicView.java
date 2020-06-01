package cn.poco.video.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.poco.beauty.view.ColorSeekBar;
import cn.poco.camera3.util.CameraPercentUtil;
import cn.poco.filterPendant.MyStatusButton;
import cn.poco.tianutils.ShareData;
import cn.poco.video.FileUtils;
import cn.poco.video.VideoUtils;
import cn.poco.video.music.WaveBitmapFactory;
import cn.poco.widget.PressedButton;
import my.beautyCamera.R;

/**
 * @author lmx
 *         Created by lmx on 2017/8/21.
 */

public class ClipMusicView extends FrameLayout
{
    private static final String TAG = "ClipMusicView";
    private final int mSeekBarHeight;
    private FrameLayout mVolLayout;
    private FrameLayout mClipLayout;

    private FrameLayout mRecordLayout;
    private FrameLayout mMusicLayout;
    private ColorSeekBar mRecordSeekBar;
    private ColorSeekBar mMusicSeekBar;

    /**
     * 全部以秒单位
     */
    static public class FrequencyInfo
    {
        public int id;
        public int musicTime;
        public int videoTime;
        public String musicPath;
        public int startTime;
        public String musicFormat;
    }

    public interface OnCallBack
    {
        //音乐播放需要seekto 到这时间
        void onStop(int second);

        void onScroll(int mScrollStartTime);

        void onProgressChanged(ColorSeekBar seekBar, int progress, boolean isMusic);

        void onStartTrackingTouch(ColorSeekBar seekBar, int progress, boolean isMusic);

        void onStopTrackingTouch(ColorSeekBar seekBar, int progress, boolean isMusic);

        void onFoldView(boolean fold);

        boolean recordEnable();

        boolean isVideoMute();
    }

    protected PressedButton mBackBtn;
    protected ClipHorizontalScrollView mScrollView;
    protected ClipAreaView mClipAreaView;
    protected TextView mClipTimeStart;
    protected TextView mClipTimeEnd;

    protected MyStatusButton mClipTitleView;
    protected MyStatusButton mVolTitleView;

    protected boolean isClipMode = true;

    protected boolean isFold = true;

    protected int mSpan;                 //每秒的间隔
    protected int mWaveW;                //频段宽度
    protected int mClipW;                //裁剪区域大小
    protected int mClipH;                //裁剪区域高度
    protected int mScrollStartTime = 0; //滚动条的裁剪时间（秒）
    protected int mEmptyViewW;          //空白频段区域
    protected int mTopMargin;           //顶部高度间距

    protected FrequencyInfo mInfo;

    public ClipMusicView(@NonNull Context context, @NonNull FrequencyInfo info)
    {
        super(context);
        this.mInfo = info;
        mSeekBarHeight = CameraPercentUtil.WidthPxToPercent(50);
        initData();
        initView();
    }

    private void initData()
    {
//        mSpan = CameraPercentUtil.WidthPxToPercent(5);
//        mWaveW = mSpan * mInfo.musicTime;
//        mClipW = CameraPercentUtil.WidthPxToPercent(354) - ClipAreaView.verticalLineW * 2;
//        if (mClipW > mWaveW)
//        {
//           //视屏大于音乐长度，不允许滚动
//            mWaveW = mClipW;
//            mSpan = mWaveW/ mInfo.musicTime;
//        }
        mClipW = CameraPercentUtil.WidthPxToPercent(354) - ClipAreaView.verticalLineW * 2;
        if (mInfo.videoTime >= mInfo.musicTime)
        {
            //视屏大于音乐长度，不允许滚动
            mWaveW = mClipW;
            mSpan = mWaveW / mInfo.musicTime;
        }
        else
        {
            mSpan = CameraPercentUtil.WidthPxToPercent(15);
            mWaveW = mSpan * (mInfo.musicTime - mInfo.videoTime) + mClipW;
        }

        mClipH = CameraPercentUtil.WidthPxToPercent(84);
        mTopMargin = CameraPercentUtil.HeightPxToPercent(88);
        mScrollStartTime = mInfo.startTime;
        mEmptyViewW = (int) ((ShareData.m_screenWidth - mClipW) * 1.0f / 2 + 0.5f);//头部和尾部需要增加空view ,大小减去clipW/2
    }

    private void initView()
    {
        FrameLayout topLayout = new FrameLayout(getContext());
        topLayout.setBackgroundColor(Color.WHITE);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, CameraPercentUtil.HeightPxToPercent(88));
        params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        params.bottomMargin = CameraPercentUtil.HeightPxToPercent(232);
        topLayout.setClickable(true);
        this.addView(topLayout, params);
        {
            mClipTitleView = new MyStatusButton(getContext());
            mClipTitleView.setOnClickListener(mOnClickListener);
            mClipTitleView.setBtnStatus(true, false);
            mClipTitleView.setData(R.drawable.video_preview_clip_logo, getContext().getString(R.string.video_preview_clip_title));
            params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
            params.leftMargin = CameraPercentUtil.WidthPxToPercent(154);
            topLayout.addView(mClipTitleView, params);

            mVolTitleView = new MyStatusButton(getContext());
            mVolTitleView.setOnClickListener(mOnClickListener);
            mVolTitleView.setBtnStatus(false, false);
            mVolTitleView.setData(R.drawable.video_preview_vol_logo, getContext().getString(R.string.video_preview_vol_title));
            params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
            params.gravity = Gravity.END;
            params.rightMargin = CameraPercentUtil.WidthPxToPercent(154);
            topLayout.addView(mVolTitleView, params);
        }

        mClipLayout = new FrameLayout(getContext());
        mClipLayout.setBackgroundColor(0xfff0f0f0);
        params = new LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, CameraPercentUtil.HeightPxToPercent(232));
        params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        this.addView(mClipLayout, params);

        mScrollView = new ClipHorizontalScrollView(getContext());
        if (mWaveW > mClipW)
        {
            mScrollView.setScrollViewListener(mScrollViewListener);
        }
        mScrollView.setHorizontalScrollBarEnabled(false);
        params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, mClipH);
        params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        params.topMargin = mTopMargin;
        mClipLayout.addView(mScrollView, params);
        {
            LinearLayout parent = new LinearLayout(getContext());
            parent.setOrientation(LinearLayout.HORIZONTAL);
            params = new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
            mScrollView.addView(parent, params);
            {
                //空白view
                LinearLayout.LayoutParams lParams;
                View leftView = new View(getContext());
                lParams = new LinearLayout.LayoutParams(mEmptyViewW, LinearLayout.LayoutParams.MATCH_PARENT);
                parent.addView(leftView, lParams);

                //音频频段view，构造默认view
                LineView lineView = new LineView(getContext());
                lParams = new LinearLayout.LayoutParams(mWaveW, LinearLayout.LayoutParams.MATCH_PARENT);
                lineView.setLayoutParams(lParams);
                parent.addView(lineView);

                //空白view
                View rightView = new View(getContext());
                lParams = new LinearLayout.LayoutParams(mEmptyViewW, LinearLayout.LayoutParams.MATCH_PARENT);
                parent.addView(rightView, lParams);
            }
        }

        //左边白色遮罩view，非裁剪区域变灰
        View leftMask = new View(getContext());
        leftMask.setBackgroundColor(0xfff0f0f0);
        leftMask.setAlpha(0.8f);
        params = new LayoutParams(mEmptyViewW - ClipAreaView.verticalLineW, mClipH);
        params.gravity = Gravity.START | Gravity.TOP;
        params.topMargin = mTopMargin;
        mClipLayout.addView(leftMask, params);

        //中间裁剪区域边框（圆角）
        mClipAreaView = new ClipAreaView(getContext());
        params = new LayoutParams(mClipW + ClipAreaView.verticalLineW * 2, mClipH);
        params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        params.topMargin = mTopMargin;
        mClipLayout.addView(mClipAreaView, params);

        //右边白色遮罩view，非裁剪区域变灰
        View rightMask = new View(getContext());
        rightMask.setBackgroundColor(0xfff0f0f0);
        rightMask.setAlpha(0.8f);
        params = new LayoutParams(mEmptyViewW - ClipAreaView.verticalLineW, mClipH);
        params.gravity = Gravity.END | Gravity.TOP;
        params.topMargin = mTopMargin;
        mClipLayout.addView(rightMask, params);

        //时间区域
        mClipTimeStart = new TextView(getContext());
        mClipTimeStart.setText(transformTime(0, mInfo.videoTime)[0]);
        mClipTimeStart.setTextColor(0xE6000000);
        mClipTimeStart.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 11);
        mClipTimeStart.setMinHeight(CameraPercentUtil.HeightPxToPercent(25));
        mClipTimeStart.setGravity(Gravity.CENTER);
        params = new LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.LEFT | Gravity.BOTTOM;
        params.bottomMargin = CameraPercentUtil.HeightPxToPercent(60 + 84 + 14);
        params.leftMargin = mEmptyViewW - ClipAreaView.verticalLineW;
        mClipLayout.addView(mClipTimeStart, params);

        mClipTimeEnd = new TextView(getContext());
        mClipTimeEnd.setText(transformTime(0, mInfo.videoTime)[1]);
        mClipTimeEnd.setTextColor(0xE6000000);
        mClipTimeEnd.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 11);
        mClipTimeEnd.setMinHeight(CameraPercentUtil.HeightPxToPercent(25));
        mClipTimeEnd.setGravity(Gravity.CENTER);
        params = new LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.END | Gravity.BOTTOM;
        params.bottomMargin = CameraPercentUtil.HeightPxToPercent(60 + 84 + 14);
        params.rightMargin = mEmptyViewW - ClipAreaView.verticalLineW;
        mClipLayout.addView(mClipTimeEnd, params);

        mScrollView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
        {
            @Override
            public void onGlobalLayout()
            {
                if (mScrollView != null)
                {
                    mScrollView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    mScrollView.setScrollX(mSpan * mScrollStartTime);
                }
                String[] times = transformTime(mScrollStartTime, mScrollStartTime + mInfo.videoTime);
                if (mClipTimeStart != null)
                {
                    mClipTimeStart.setText(times[0]);
                }
                if (mClipTimeEnd != null)
                {
                    mClipTimeEnd.setText(times[1]);
                }
            }
        });

        mVolLayout = new FrameLayout(getContext());
        mVolLayout.setVisibility(GONE);
        mVolLayout.setBackgroundColor(0xfff0f0f0);
        params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, CameraPercentUtil.HeightPxToPercent(232));
        params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        this.addView(mVolLayout, params);
        {
            //录音
            mRecordLayout = new FrameLayout(getContext());
            params = new LayoutParams(LayoutParams.MATCH_PARENT, mSeekBarHeight);
            params.gravity = Gravity.CENTER_HORIZONTAL;
            params.topMargin = CameraPercentUtil.HeightPxToPercent(46);
            mVolLayout.addView(mRecordLayout, params);

            //音乐
            mMusicLayout = new FrameLayout(getContext());
            params = new LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, mSeekBarHeight);
            params.gravity = Gravity.CENTER_HORIZONTAL;
            params.topMargin = CameraPercentUtil.HeightPxToPercent(136);
            mVolLayout.addView(mMusicLayout, params);

            String text = getContext().getString(R.string.video_preview_clip_record);
            TextView textViewRecord = new TextView(getContext());
            textViewRecord.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 11);
            textViewRecord.setTypeface(Typeface.DEFAULT);
            textViewRecord.setTextColor(0xE6333333);
            textViewRecord.setText(text);
            textViewRecord.setGravity(Gravity.END);
            int textWidth1 = (int) textViewRecord.getPaint().measureText(text) + 1;

            text = getContext().getString(R.string.video_preview_clip_music);
            TextView textViewMusic = new TextView(getContext());
            textViewMusic.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 11);
            textViewMusic.setTypeface(Typeface.DEFAULT);
            textViewMusic.setTextColor(0xE6333333);
            textViewMusic.setText(getContext().getString(R.string.video_preview_clip_music));
            textViewMusic.setGravity(Gravity.END);
            int textWidth2 = (int) textViewMusic.getPaint().measureText(text) + 1;

            mRecordSeekBar = getDefaultSeekBar();
            FrameLayout.LayoutParams params1 = new FrameLayout.LayoutParams(CameraPercentUtil.WidthPxToPercent(488), FrameLayout.LayoutParams.MATCH_PARENT);
            params1.leftMargin = CameraPercentUtil.WidthPxToPercent(46 + 48) + textWidth1;
            params1.gravity = Gravity.CENTER_VERTICAL | Gravity.START;
            mRecordLayout.addView(mRecordSeekBar, params1);

            mMusicSeekBar = getDefaultSeekBar();
            params1 = new FrameLayout.LayoutParams(CameraPercentUtil.WidthPxToPercent(488), FrameLayout.LayoutParams.MATCH_PARENT);
            params1.leftMargin = CameraPercentUtil.WidthPxToPercent(46 + 48) + textWidth1;
            params1.gravity = Gravity.CENTER_VERTICAL | Gravity.START;
            mMusicLayout.addView(mMusicSeekBar, params1);

            int textRCW = CameraPercentUtil.WidthPxToPercent(48);
            int textMCW = textRCW;
            int abs = Math.abs(textWidth1 - textWidth2);
            if (textWidth1 > textWidth2)
            {
                textMCW += abs / 2;
            }
            else
            {
                textRCW += abs / 2;
            }

            params1 = new FrameLayout.LayoutParams(textWidth1, FrameLayout.LayoutParams.WRAP_CONTENT);
            params1.gravity = Gravity.CENTER_VERTICAL | Gravity.START;
            params1.leftMargin = textRCW;
            mRecordLayout.addView(textViewRecord, params1);

            params1 = new FrameLayout.LayoutParams(textWidth2, FrameLayout.LayoutParams.WRAP_CONTENT);
            params1.gravity = Gravity.CENTER_VERTICAL | Gravity.START;
            params1.leftMargin = textMCW;
            mMusicLayout.addView(textViewMusic, params1);
        }
    }

    private ColorSeekBar getDefaultSeekBar()
    {
        ColorSeekBar seekBar = new ColorSeekBar(getContext());
        seekBar.setMax(100);
        seekBar.setProgress(0);
        seekBar.setOnSeekBarChangeListener(mSeekBarListener);

        return seekBar;
    }

    private ColorSeekBar.OnSeekBarChangeListener mSeekBarListener = new ColorSeekBar.OnSeekBarChangeListener()
    {
        @Override
        public void onProgressChanged(ColorSeekBar seekBar, int progress)
        {
            if (mOnCallBack != null)
            {
                if (seekBar == mRecordSeekBar)
                {
                    mOnCallBack.onProgressChanged(seekBar, seekBar.getProgress(), false);
                }
                else if (seekBar == mMusicSeekBar)
                {
                    mOnCallBack.onProgressChanged(seekBar, seekBar.getProgress(), true);
                }
            }
        }

        @Override
        public void onStartTrackingTouch(ColorSeekBar seekBar)
        {
            disableSeekBar(seekBar);

            if (mOnCallBack != null)
            {
                if (seekBar == mRecordSeekBar)
                {
                    mOnCallBack.onStartTrackingTouch(seekBar, seekBar.getProgress(), false);
                }
                else if (seekBar == mMusicSeekBar)
                {
                    mOnCallBack.onStartTrackingTouch(seekBar, seekBar.getProgress(), true);
                }
            }
        }

        @Override
        public void onStopTrackingTouch(ColorSeekBar seekBar)
        {
            if (mOnCallBack != null)
            {
                if (seekBar == mRecordSeekBar)
                {
                    mOnCallBack.onStopTrackingTouch(seekBar, seekBar.getProgress(), false);
                }
                else if (seekBar == mMusicSeekBar)
                {
                    mOnCallBack.onStopTrackingTouch(seekBar, seekBar.getProgress(), true);
                }
            }

            enableSeekBar();
        }
    };

    private void enableSeekBar()
    {
        mRecordSeekBar.setEnabled(mOnCallBack == null || mOnCallBack.recordEnable());
        mMusicSeekBar.setEnabled(true);
    }

    private void disableSeekBar(ColorSeekBar seekBar)
    {

        if (seekBar == null)
        {
            mRecordSeekBar.setEnabled(false);
            mMusicSeekBar.setEnabled(false);
        }

        if (seekBar == mMusicSeekBar)
        {
            mRecordSeekBar.setEnabled(false);
        }

        if (seekBar == mRecordSeekBar)
        {
            mMusicSeekBar.setEnabled(false);
        }
    }

    private OnCallBack mOnCallBack;

    public void setOnCallBack(OnCallBack mOnCallBack)
    {
        this.mOnCallBack = mOnCallBack;
    }


    private ClipHorizontalScrollView.ScrollViewListener mScrollViewListener = new ClipHorizontalScrollView.ScrollViewListener()
    {
        @Override
        public void onScrollChanged(ClipHorizontalScrollView.ScrollType scrollType, int scrollX)
        {
            mScrollStartTime = (int) (scrollX * (mInfo.musicTime - mInfo.videoTime) * 1.0f / (mWaveW - mClipW) + 0.5f);
            String[] times = transformTime(mScrollStartTime, mScrollStartTime + mInfo.videoTime);
            mClipTimeStart.setText(times[0]);
            mClipTimeEnd.setText(times[1]);
            if (scrollType == ClipHorizontalScrollView.ScrollType.IDLE)
            {
                //位移的最大距离为
                if (mOnCallBack != null)
                {
                    mOnCallBack.onStop(mScrollStartTime);
                }
            }
            else
            {
                if (mOnCallBack != null)
                {
                    mOnCallBack.onScroll(mScrollStartTime);
                }
            }
        }
    };

    public boolean isFold()
    {
        return isFold;
    }

    public FrequencyInfo getFrequencyInfo()
    {
        return mInfo;
    }

    public void setFold(boolean fold)
    {
        isFold = fold;
    }

    private String[] transformTime(int startTime, int endTime)
    {
        String[] times = new String[2];

        StringBuilder stringBuilder = new StringBuilder();
        int min = startTime / 60;
        int sec = startTime % 60;
        if (min < 10)
        {
            stringBuilder.append("0");
        }
        stringBuilder.append(String.valueOf(min));
        stringBuilder.append(":");
        if (sec < 10)
        {
            stringBuilder.append("0");
        }
        stringBuilder.append(String.valueOf(sec));
        times[0] = stringBuilder.toString();

        stringBuilder = new StringBuilder();
        min = endTime / 60;
        sec = endTime % 60;
        if (min < 10)
        {
            stringBuilder.append("0");
        }
        stringBuilder.append(String.valueOf(min));
        stringBuilder.append(":");
        if (sec < 10)
        {
            stringBuilder.append("0");
        }
        stringBuilder.append(String.valueOf(sec));
        times[1] = stringBuilder.toString();
        return times;
    }

    /**
     * 得到音频频段信息
     *
     * @param width
     * @param height
     * @return
     */
    private WaveBitmapFactory.WaveInfo getWaveInfo(int width, int height)
    {
        String path = mInfo.musicPath;
        if (!((path.endsWith(FileUtils.MP3_FORMAT) || path.endsWith(FileUtils.WAV_FORMAT))))
        {
            path = FileUtils.getTempPath(FileUtils.WAV_FORMAT);
            boolean result = VideoUtils.changeToAac(mInfo.musicPath, path);
            if (!result)
            {
                return null;
            }
        }
        WaveBitmapFactory waveBitmapFactory = new WaveBitmapFactory(width, height);
        waveBitmapFactory.setZoom(1.3f);
        boolean b = waveBitmapFactory.setSoundFilePath(path);
        if (!b)
        {
            return null;
        }
        return waveBitmapFactory.getData();
    }


    public void setBtnStatus(boolean isFold)
    {
        if (isClipMode)
        {
            if (mClipTitleView != null)
            {
                mClipTitleView.setBtnStatus(mClipTitleView.isSeletecd(), isFold);
            }
        }
        else
        {
            if (mVolTitleView != null)
            {
                mVolTitleView.setBtnStatus(mVolTitleView.isSeletecd(), isFold);
            }
        }
    }

    public void setRecordSeekBarCanScroll(boolean isCanScroll)
    {
        if (mRecordSeekBar != null)
        {
            mRecordSeekBar.setEnabled(isCanScroll);
        }
    }

    public void setRecordSeekBar(int progress, boolean isCanScroll)
    {
        if (mRecordSeekBar != null)
        {
            mRecordSeekBar.setProgress(progress);
            //mRecordSeekBar.setEnabled(isCanScroll);
        }
        //if (mRecordLayout != null) mRecordLayout.setAlpha(isCanScroll ? 1f : 0.4f);
    }

    public void setMusicSeekBar(int progress, boolean isCanScroll)
    {
        if (mMusicSeekBar != null)
        {
            mMusicSeekBar.setProgress(progress);
            //mMusicSeekBar.setEnabled(isCanScroll);
        }
        //if (mMusicLayout != null) mMusicLayout.setAlpha(isCanScroll ? 1f : 0.4f);
    }

    protected OnClickListener mOnClickListener = new OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            if (v == mClipTitleView)
            {
                if (isClipMode)
                {
                    if (mClipTitleView.isDown())
                    {
                        if (mOnCallBack != null && isFold) mOnCallBack.onFoldView(false);
                        mClipTitleView.setBtnStatus(true, false);
                        mClipLayout.setVisibility(VISIBLE);
                    }
                    else
                    {
                        if (mOnCallBack != null && !isFold) mOnCallBack.onFoldView(true);
                        mClipTitleView.setBtnStatus(true, true);
                    }
                }
                else
                {
                    if (mOnCallBack != null && isFold) mOnCallBack.onFoldView(false);
                    mClipTitleView.setBtnStatus(true, false);
                    mClipLayout.setVisibility(VISIBLE);
                }
                isClipMode = true;
                mVolTitleView.setBtnStatus(false, mClipTitleView.isDown());
                mVolLayout.setVisibility(GONE);
            }
            else if (v == mVolTitleView)
            {
                if (isClipMode)
                {
                    if (mOnCallBack != null && isFold) mOnCallBack.onFoldView(false);
                    mVolTitleView.setBtnStatus(true, false);
                    mVolLayout.setVisibility(VISIBLE);
                }
                else
                {
                    if (mVolTitleView.isDown())
                    {
                        if (mOnCallBack != null && isFold) mOnCallBack.onFoldView(false);
                        mVolTitleView.setBtnStatus(true, false);
                        mVolLayout.setVisibility(VISIBLE);
                    }
                    else
                    {
                        if (mOnCallBack != null && !isFold) mOnCallBack.onFoldView(true);
                        mVolTitleView.setBtnStatus(true, true);
                    }

                }
                isClipMode = false;
                mClipTitleView.setBtnStatus(false, mVolTitleView.isDown());
                mClipLayout.setVisibility(GONE);
            }
        }
    };
}
