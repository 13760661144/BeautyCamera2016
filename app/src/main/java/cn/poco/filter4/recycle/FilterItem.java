package cn.poco.filter4.recycle;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import cn.poco.makeup.MySeekBar;
import cn.poco.recycleview.AbsExConfig;
import cn.poco.recycleview.BaseExAdapter;
import cn.poco.recycleview.BaseGroup;
import cn.poco.recycleview.BaseItem;
import cn.poco.recycleview.BaseItemContainer;
import cn.poco.tianutils.ShareData;

public class FilterItem extends BaseItemContainer
{
    protected ValueAnimator mAlphaValueAnimator;
    protected OnChangeAlphaFrCB mAlphaCB;
    protected OnProgressChangeCB mProgressChangeCB;

    protected FrameLayout mAlphaFr;
    private View line;

    private int leftMargin;
    private int rightMargin;
    private boolean isDrawLine = false;

    protected MySeekBar mAlphaSeekBar;

    public FilterItem(Context context, AbsExConfig config)
    {
        super(context, config);
        if (!((FilterConfig) mConfig).isCamera)
        {
            mAlphaValueAnimator = new ValueAnimator();
            mAlphaValueAnimator.setDuration(DURATION);
            mAlphaValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
            {
                @Override
                public void onAnimationUpdate(ValueAnimator animation)
                {
                    mWidth = (Integer) animation.getAnimatedValue();
                    if (mAlphaFr != null)
                    {
                        LinearLayout.LayoutParams layoutParams = (LayoutParams) mAlphaFr.getLayoutParams();
                        layoutParams.width = mWidth - mMaxW;
                        mAlphaFr.setLayoutParams(layoutParams);
                    }
                    requestLayout();
                    if (mAlphaCB != null)
                    {
                        mAlphaCB.change(animation.getAnimatedFraction());
                    }
                }
            });
            mAlphaValueAnimator.addListener(new AnimatorListenerAdapter()
            {
                @Override
                public void onAnimationEnd(Animator animation)
                {
                    if (mWidth == mMaxW)
                    {
                        removeAlphaFr();
                        if (mProgressChangeCB != null)
                        {
                            mProgressChangeCB.onFinishLayoutAlphaFr(mAlphaSeekBar);
                        }
                    }
                    else
                    {
                        RecyclerView recyclerView = (RecyclerView) FilterItem.this.getParent();
                        if (recyclerView != null)
                        {
                            if (recyclerView.getScrollState() == RecyclerView.SCROLL_STATE_IDLE)
                            {
                                if (mProgressChangeCB != null)
                                {
                                    mProgressChangeCB.onFinishLayoutAlphaFr(mAlphaSeekBar);
                                }
                            }
                            else
                            {
                                recyclerView.addOnScrollListener(mScrollListener);
                            }
                        }
                    }

                }

                @Override
                public void onAnimationStart(Animator animation)
                {
                    if (mProgressChangeCB != null)
                    {
                        mProgressChangeCB.onSeekBarStartShow(mAlphaSeekBar);
                    }
                }
            });
        }
    }

    public void setProgressChangeCB(OnProgressChangeCB mProgressChangeCB)
    {
        this.mProgressChangeCB = mProgressChangeCB;
    }

    public FrameLayout initAlphaFr(int progress)
    {
        mAlphaFr = new FrameLayout(getContext());
        mAlphaSeekBar = new MySeekBar(getContext());
        mAlphaSeekBar.setOnProgressChangeListener(mOnSeekBarListener);
        mAlphaSeekBar.setBackgroundColor(0x57000000);
        mAlphaSeekBar.setMax(100);
        mAlphaSeekBar.setProgress(progress);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ShareData.PxToDpi_xhdpi(480), LayoutParams.MATCH_PARENT);
        layoutParams.gravity = Gravity.CENTER_VERTICAL | Gravity.LEFT;
        layoutParams.leftMargin = ShareData.PxToDpi_xhdpi(32);
        mAlphaSeekBar.setLayoutParams(layoutParams);
        mAlphaFr.addView(mAlphaSeekBar);
        return mAlphaFr;
    }

    public void removeAlphaFr()
    {
        if (mAlphaFr != null)
        {
            this.removeView(mAlphaFr);
            mAlphaFr = null;
        }
    }

    public void openAlphaFr(int index, int progress, OnChangeAlphaFrCB cb)
    {
        mAlphaCB = cb;
        removeAlphaFr();
        mAlphaFr = initAlphaFr(progress);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ShareData.m_screenWidth - mConfig.def_sub_w - mConfig.def_sub_l, LinearLayout.LayoutParams.MATCH_PARENT);
        mAlphaFr.setLayoutParams(params);
        this.addView(mAlphaFr, index + 1);
        if (mAlphaValueAnimator != null)
        {
            mAlphaValueAnimator.setIntValues(mMaxW, mMaxW + (ShareData.m_screenWidth - mConfig.def_sub_w - mConfig.def_sub_l));
            mAlphaValueAnimator.start();
        }
    }

    public void closeAlphaFr(OnChangeAlphaFrCB cb)
    {
        mAlphaCB = cb;
        FilterConfig config = (FilterConfig) mConfig;
        if (mAlphaValueAnimator != null)
        {
            mAlphaValueAnimator.setIntValues(mMaxW + (ShareData.m_screenWidth - config.def_sub_w - config.def_alphafr_leftMargin), mMaxW);
            mAlphaValueAnimator.start();
        }
    }

    @Override
    public void setItemInfo(BaseExAdapter.ItemInfo itemInfo, int position)
    {
        super.setItemInfo(itemInfo, position);
        if (itemInfo instanceof FilterAdapter.ItemInfo)
        {
            isDrawLine = ((FilterAdapter.ItemInfo) itemInfo).isDrawLine;
        }
        if (isDrawLine)
        {
            leftMargin = ShareData.PxToDpi_xhdpi(40) - mConfig.def_sub_padding_r;
            rightMargin = ShareData.PxToDpi_xhdpi(40) - mConfig.def_item_l;
            mMaxW += leftMargin + rightMargin + ShareData.PxToDpi_xhdpi(1);
        }
    }

    @Override
    public void addItemViews()
    {
        super.addItemViews();
        if (isDrawLine)
        {
            LinearLayout.LayoutParams params = new LayoutParams(ShareData.PxToDpi_xhdpi(1), ShareData.PxToDpi_xhdpi(100));
            params.leftMargin = leftMargin;
            params.rightMargin = rightMargin;
            params.gravity = Gravity.CENTER;
            line = new View(getContext());
            line.setBackgroundColor(0x4d000000);//30%
            addView(line, params);
        }
    }

    @Override
    public void removeItemViews()
    {
        super.removeItemViews();
        if (isDrawLine)
        {
            removeView(line);
        }
    }

    @Override
    public BaseGroup initGroupView()
    {
        return new FilterGroup(getContext());
    }

    @Override
    public BaseItem initItemView()
    {
        return new FilterSubItem(getContext(), (FilterConfig) mConfig);
    }

    protected RecyclerView.OnScrollListener mScrollListener = new RecyclerView.OnScrollListener()
    {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState)
        {
            if (newState == RecyclerView.SCROLL_STATE_IDLE)
            {
                if (mProgressChangeCB != null)
                {
                    mProgressChangeCB.onFinishLayoutAlphaFr(mAlphaSeekBar);
                }
            }
            super.onScrollStateChanged(recyclerView, newState);
        }
    };

    protected MySeekBar.OnProgressChangeListener mOnSeekBarListener = new MySeekBar.OnProgressChangeListener()
    {
        @Override
        public void onProgressChanged(MySeekBar seekBar, int progress)
        {
            if (mProgressChangeCB != null)
            {
                mProgressChangeCB.onProgressChanged(seekBar, progress);
            }
        }

        @Override
        public void onStartTrackingTouch(MySeekBar seekBar)
        {
            if (mProgressChangeCB != null)
            {
                mProgressChangeCB.onStartTrackingTouch(seekBar);
            }
        }

        @Override
        public void onStopTrackingTouch(MySeekBar seekBar)
        {
            if (mProgressChangeCB != null)
            {
                mProgressChangeCB.onStopTrackingTouch(seekBar);
            }
        }
    };

    interface OnChangeAlphaFrCB
    {
        void change(float value);
    }

    interface OnProgressChangeCB extends MySeekBar.OnProgressChangeListener
    {
        public void onSeekBarStartShow(MySeekBar seekBar);

        public void onFinishLayoutAlphaFr(MySeekBar seekBar);
    }
}
