package cn.poco.video.music;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.ViewPropertyAnimation;

import java.util.ArrayList;

import cn.poco.advanced.ImageUtils;
import cn.poco.audio.CommonUtils;
import cn.poco.camera3.util.CameraPercentUtil;
import cn.poco.campaignCenter.widget.PullDownRefreshRecyclerView.IRecyclerView;
import cn.poco.campaignCenter.widget.component.RefreshHeaderView;
import cn.poco.dynamicSticker.newSticker.CropCircleTransformation;
import cn.poco.tianutils.ShareData;
import cn.poco.utils.OnAnimationClickListener;
import cn.poco.video.AudioStore;
import cn.poco.video.FileUtils;
import cn.poco.video.view.MusicCoverView;
import cn.poco.widget.PressedButton;
import my.beautyCamera.R;

/**
 * @author lmx
 *         Created by lmx on 2017/8/18.
 */

public class SelectMusicPage extends FrameLayout
{
    private static final int MSG_LOAD = 1;

    protected FrameLayout mTopBarLayout;
    protected PressedButton mBackBtn;

    protected IRecyclerView mRecyclerView;
    protected MusicAdapter mAdapter;

    protected ArrayList<AudioStore.AudioInfo> mList;

    protected boolean isFold = true;
    protected boolean isLoaded = false;

    protected OnCallBack mCb;

    public interface OnCallBack
    {
        public void onClickBack();

        public void onClick(AudioStore.AudioInfo audioInfo);
    }

    protected Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case MSG_LOAD:
                    isLoaded = true;
                    mList = (ArrayList<AudioStore.AudioInfo>) msg.obj;
                    loadDataComplete();
                    break;
            }
        }
    };

    private class MThread extends Thread
    {
        @Override
        public void run()
        {
            ArrayList<AudioStore.AudioInfo> audioInfos = AudioStore.GetAudioInfos(getContext(), true);
            audioInfos = filterSampleRate(audioInfos);
            if (mHandler != null) mHandler.obtainMessage(MSG_LOAD, audioInfos).sendToTarget();
        }
    }

    protected MusicAdapter.OnClickCallback mCallback = new MusicAdapter.OnClickCallback()
    {
        @Override
        public void onClick(int position, AudioStore.AudioInfo audioInfo)
        {
            if (mCb != null)
            {
                mCb.onClick(audioInfo);
            }
        }
    };

    protected IRecyclerView.OnRefreshListener mOnRefreshListener = new IRecyclerView.OnRefreshListener()
    {
        @Override
        public void onRefresh()
        {
            //刷新数据
            loadData(false);
        }
    };

    public SelectMusicPage(@NonNull Context context, OnCallBack cb)
    {
        super(context);
        this.mCb = cb;
        init();
    }

    private void init()
    {
        this.setBackgroundColor(0xFFF0F0F0);

        mTopBarLayout = new FrameLayout(getContext());
        mTopBarLayout.setBackgroundColor(Color.WHITE);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(90));
        params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        this.addView(mTopBarLayout, params);

        mBackBtn = new PressedButton(getContext());
        mBackBtn.setButtonImage(R.drawable.framework_back_btn, R.drawable.framework_back_btn, ImageUtils.GetSkinColor(), 0.5f);
        params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER_VERTICAL | Gravity.LEFT;
        mBackBtn.setOnTouchListener(new OnAnimationClickListener()
        {
            @Override
            public void onAnimationClick(View v)
            {
                if (mCb != null)
                {
                    mCb.onClickBack();
                }
            }
        });
        mTopBarLayout.addView(mBackBtn, params);

        TextView title = new TextView(getContext());
        params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        title.setTextColor(0xff333333);
        title.setText(R.string.lightapp06_video_bgm_local);
        mTopBarLayout.addView(title, params);

        mRecyclerView = (IRecyclerView) LayoutInflater.from(getContext()).inflate(R.layout.layout_select_music_recyclerview, null);
        mRecyclerView.setPadding(0, ShareData.PxToDpi_xhdpi(20), 0, 0);
        params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        params.topMargin = ShareData.PxToDpi_xhdpi(90);
        this.addView(mRecyclerView, params);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setSmoothScrollbarEnabled(true);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setClipToPadding(false);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setRefreshEnabled(true);

        //头部刷新view
        RefreshHeaderView refreshHeaderView = new RefreshHeaderView(getContext());
        refreshHeaderView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, CameraPercentUtil.WidthPxToPercent(90)));
        mRecyclerView.setRefreshHeaderView(refreshHeaderView);

        //listener
        mRecyclerView.setOnRefreshListener(mOnRefreshListener);

        mAdapter = new MusicAdapter(getContext(), mCallback);
        mRecyclerView.setIAdapter(mAdapter);
    }

    public void loadData()
    {
        loadData(true);
    }

    public void loadData(boolean postDelay)
    {
        MThread mThread = new MThread();
        mThread.start();

        if (mRecyclerView != null)
        {
            if (postDelay)
            {
                mRecyclerView.postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if (mRecyclerView != null)
                        {
                            mRecyclerView.setRefreshing(true);
                        }
                    }
                }, 100);
            }
            else
            {
                mRecyclerView.setRefreshing(true);
            }
        }
    }

    private void loadDataComplete()
    {
        if (mAdapter != null)
        {
            mAdapter.setData(mList);
        }

        if (mRecyclerView != null)
        {
            mRecyclerView.setRefreshing(false);
        }
    }

    private ArrayList<AudioStore.AudioInfo> filterSampleRate(ArrayList<AudioStore.AudioInfo> src)
    {
        //TODO 过滤高采样率音频
        return src;
    }

    public void clearAll()
    {
        if (mList != null)
        {
            mList.clear();
        }

        if (mAdapter != null)
        {
            mAdapter.clearAll();
        }

        isLoaded = false;
        mRecyclerView = null;
        mCallback = null;
        mList = null;
        mHandler = null;
        mOnRefreshListener = null;
    }


    public boolean isLoaded()
    {
        return isLoaded;
    }

    public boolean isFold()
    {
        return isFold;
    }

    public void setFold(boolean fold)
    {
        isFold = fold;
    }


    public static class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MusicHolder>
    {
        public interface OnClickCallback
        {
            void onClick(int position, AudioStore.AudioInfo audioInfo);
        }

        protected ArrayList<AudioStore.AudioInfo> mList;
        protected CropCircleTransformation mTransformation;
        protected Context mContext;
        protected OnClickCallback mCallback;
        protected Toast mToast = null;

        protected ViewPropertyAnimation.Animator animationObject = new ViewPropertyAnimation.Animator()
        {
            @Override
            public void animate(View view)
            {
                view.setAlpha(0f);
                ObjectAnimator fadeAnim = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);
                fadeAnim.setDuration(2000);
                fadeAnim.start();
            }
        };

        public MusicAdapter(Context context, OnClickCallback callback)
        {
            this.mContext = context;
            this.mCallback = callback;
            this.mTransformation = new CropCircleTransformation(context);
        }

        public void setData(ArrayList<AudioStore.AudioInfo> list)
        {
            this.mList = list;
            notifyDataSetChanged();
        }

        public void clearAll()
        {
            dimissToast();
            this.mContext = null;
            this.mCallback = null;
            this.mClick = null;
        }

        private View.OnClickListener mClick = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (v != null && v.getTag() != null && mList != null && mCallback != null)
                {
                    int position = (Integer) v.getTag();
                    AudioStore.AudioInfo audioInfo = mList.get(position);
                    if (!checkSimpleRate(audioInfo))
                    {
                        showToast(R.string.lightapp06_video_bgm_un_support);
                        return;
                    }
                    dimissToast();
                    mCallback.onClick(position, audioInfo);
                }
            }
        };

        @Override
        public MusicHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            MusicLayout view = new MusicLayout(parent.getContext());
            view.setBackgroundColor(Color.WHITE);
            RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(124));
            view.setLayoutParams(params);

            MusicHolder holder = new MusicHolder(view);
            holder.mCoverView = view.mCoverView;
            holder.mTitleView = view.mTitleView;
            holder.mTimeView = view.mTimeView;
            return holder;
        }

        @Override
        public void onBindViewHolder(MusicHolder holder, int position)
        {
            holder.itemView.setTag(position);
            holder.itemView.setOnClickListener(mClick);
            AudioStore.AudioInfo audioInfo = mList.get(position);
            if (holder.mTitleView != null)
            {
                holder.mTitleView.setText(audioInfo.getTitle());
            }
            if (holder.mTimeView != null)
            {
                holder.mTimeView.setText(audioInfo.getTimeFormat());
            }
            if (holder.mCoverView != null)
            {
                if (mTransformation != null)
                {
                    Glide.with(mContext)
                            .load(audioInfo.getCoverPath())
                            .bitmapTransform(mTransformation)
                            .animate(animationObject)
                            .placeholder(R.drawable.local_music_cover_default)
                            .error(R.drawable.local_music_cover_default)
                            .into(holder.mCoverView);
                }
                else
                {
                    Glide.with(mContext)
                            .load(audioInfo.getCoverPath())
                            .animate(animationObject)
                            .placeholder(R.drawable.local_music_cover_default)
                            .error(R.drawable.local_music_cover_default)
                            .into(holder.mCoverView);
                }
            }
        }

        @Override
        public void onViewRecycled(MusicHolder holder)
        {
            if (holder.mCoverView != null) Glide.clear(holder.mCoverView);
            super.onViewRecycled(holder);
        }

        @Override
        public int getItemCount()
        {
            return mList != null ? mList.size() : 0;
        }


        /**
         * 检查采样率，底层音频暂时不支持太低太高的采样率音频 （默认44100）
         * FIXME 优化支持
         *
         * @param audioInfo
         * @return
         */
        private boolean checkSimpleRate(AudioStore.AudioInfo audioInfo)
        {
            if (audioInfo != null && FileUtils.isFileExists(audioInfo.getPath()))
            {
                long sampleRate = audioInfo.getSampleRate();
                if (sampleRate == 0)
                {
                    try
                    {
                        sampleRate = CommonUtils.getAudioSampleRate(audioInfo.getPath());
                    }
                    catch (Throwable t)
                    {
                        t.printStackTrace();
                    }
                    audioInfo.setSampleRate(sampleRate);
                }
                //Log.d("bbb", "MusicAdapter --> checkSimpleRate: rate " + sampleRate);
                if (sampleRate <= 38000 || sampleRate >= 48000)
                {
                    return false;
                }
            }
            return true;
        }

        private void showToast(@StringRes int resId)
        {
            if (mContext == null) return;

            dimissToast();
            mToast = Toast.makeText(mContext, resId, Toast.LENGTH_SHORT);
            mToast.show();
        }

        private void dimissToast()
        {
            if (mToast != null)
            {
                mToast.cancel();
                mToast = null;
            }
        }


        class MusicHolder extends RecyclerView.ViewHolder
        {
            protected MusicCoverView mCoverView;
            protected TextView mTitleView;
            protected TextView mTimeView;

            public MusicHolder(View itemView)
            {
                super(itemView);
            }
        }

        class MusicLayout extends FrameLayout
        {
            protected MusicCoverView mCoverView;
            protected TextView mTitleView;
            protected TextView mTimeView;

            public MusicLayout(Context context)
            {
                super(context);
                init();
            }

            private void init()
            {
                mCoverView = new MusicCoverView(getContext());
                mCoverView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ShareData.PxToDpi_xhdpi(84), ShareData.PxToDpi_xhdpi(84));
                params.gravity = Gravity.CENTER_VERTICAL | Gravity.LEFT;
                params.leftMargin = ShareData.PxToDpi_xhdpi(30);
                addView(mCoverView, params);

                mTitleView = new TextView(getContext());
                mTitleView.setSingleLine(true);
                mTitleView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                mTitleView.setMarqueeRepeatLimit(-1);
                mTitleView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                mTitleView.setTextColor(0xff5c5c5c);
                params = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                params.gravity = Gravity.CENTER_VERTICAL | Gravity.LEFT;
                params.leftMargin = ShareData.PxToDpi_xhdpi(30 * 2 + 84);
                params.rightMargin = ShareData.PxToDpi_xhdpi(30 + 75);
                addView(mTitleView, params);

                mTimeView = new TextView(getContext());
                mTimeView.setSingleLine(true);
                mTimeView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 11);
                mTimeView.setTextColor(0xff868686);
                params = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                params.gravity = Gravity.CENTER_VERTICAL | Gravity.RIGHT;
                params.rightMargin = ShareData.PxToDpi_xhdpi(30);
                addView(mTimeView, params);

                View line = new View(getContext());
                line.setBackgroundColor(0xFFDFDFDF);
                params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);
                params.gravity = Gravity.BOTTOM | Gravity.LEFT;
                params.leftMargin = ShareData.PxToDpi_xhdpi(30 * 2 + 84);
                addView(line, params);
            }
        }
    }

}
