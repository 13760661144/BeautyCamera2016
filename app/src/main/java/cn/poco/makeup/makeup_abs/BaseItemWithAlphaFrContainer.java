package cn.poco.makeup.makeup_abs;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import cn.poco.makeup.MySeekBar;
import cn.poco.recycleview.AbsExConfig;
import cn.poco.recycleview.BaseItemContainer;
import cn.poco.tianutils.ShareData;

/**
 * 包含调节透明度ui的Item
 */
public abstract class BaseItemWithAlphaFrContainer extends BaseItemContainer{

    protected ValueAnimator mAlphaFrValueAnimator;
    protected FrameLayout mAlphaFr;
    protected MySeekBar mSeekBar;
    protected MySeekBar.OnProgressChangeListener mProgressChangeListener;
    protected int m_alphaSubIndex = -1;
    protected AnimaChangeCallBack m_changeCB;
    protected boolean m_isOpenAnim = false;
    public BaseItemWithAlphaFrContainer(Context context, AbsExConfig config) {
        super(context, config);
        initData();
    }

    public void initData()
    {
        mAlphaFrValueAnimator = new ValueAnimator();
        mAlphaFrValueAnimator.setDuration(300);
        mAlphaFrValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mWidth = (int) animation.getAnimatedValue();
                if(mAlphaFr != null && mWidth > mMaxW)
                {
                    LinearLayout.LayoutParams fl = (LinearLayout.LayoutParams) mAlphaFr.getLayoutParams();
                    fl.width = mWidth - mMaxW;
                    mAlphaFr.setLayoutParams(fl);
                }

                if(m_changeCB != null)
                {
                    if(m_alphaSubIndex > -1 && m_alphaSubIndex < BaseItemWithAlphaFrContainer.this.getChildCount())
                    {
                        View view = BaseItemWithAlphaFrContainer.this.getChildAt(m_alphaSubIndex);
                        m_changeCB.change(view,animation.getAnimatedFraction(),m_isOpenAnim);
                    }
                }
            }
        });
        mAlphaFrValueAnimator.addListener(new ValueAnimator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                if(mProgressChangeListener != null)
                {
                    MySeekBarCB mySeekBarCB = (MySeekBarCB) mProgressChangeListener;
                    mySeekBarCB.onSeekBarShowStart(mSeekBar);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if(mWidth == mMaxW)
                {
                    if(mAlphaFr != null)
                    {
                        BaseItemWithAlphaFrContainer.this.removeView(mAlphaFr);
                        mAlphaFr = null;
                    }
                }

                if(mProgressChangeListener != null)
                {
                    final RecyclerView recyclerView = (RecyclerView) BaseItemWithAlphaFrContainer.this.getParent();
                    if(recyclerView != null)
                    {
                        if(recyclerView.getScrollState() == RecyclerView.SCROLL_STATE_IDLE)
                        {
                            if(mProgressChangeListener != null)
                            {
                                MySeekBarCB mySeekBarCB = (MySeekBarCB) mProgressChangeListener;
                                mySeekBarCB.onSeekBarLayoutFinish(mSeekBar);
                            }
                        }
                        else
                        {
                            recyclerView.addOnScrollListener(m_scrollListener);
                        }
                    }
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    RecyclerView.OnScrollListener m_scrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            if(newState == RecyclerView.SCROLL_STATE_IDLE)
            {
                recyclerView.removeOnScrollListener(m_scrollListener);
                if(mProgressChangeListener != null)
                {
                    MySeekBarCB mySeekBarCB = (MySeekBarCB) mProgressChangeListener;
                    mySeekBarCB.onSeekBarLayoutFinish(mSeekBar);
                }
            }
            super.onScrollStateChanged(recyclerView, newState);
        }
    };

    public abstract FrameLayout initAlphaFr(Context context, AbsExConfig config,MySeekBar.OnProgressChangeListener cb);


    public void openAlphaFr(int index)
    {
        m_alphaSubIndex = index;
        mAlphaFr = initAlphaFr(getContext(),mConfig,mProgressChangeListener);
        findSeekBar();
        int width = ShareData.m_screenWidth - mConfig.def_sub_padding_l - mConfig.def_sub_w;
        FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(width,FrameLayout.LayoutParams.MATCH_PARENT);
        mAlphaFr.setLayoutParams(fl);
        this.addView(mAlphaFr,index + 1);

        m_isOpenAnim = true;
        mAlphaFrValueAnimator.setIntValues(mMaxW,mMaxW + width);
        mAlphaFrValueAnimator.start();
    }
    
    public void setChangeCB(AnimaChangeCallBack cb)
    {
        m_changeCB = cb;
    }

    public void setOnProgressChangeListener(MySeekBar.OnProgressChangeListener onProgressChangeListener)
    {
        mProgressChangeListener = onProgressChangeListener;
    }

    public void closeAlphaFr()
    {
        m_isOpenAnim = false;
        int width = ShareData.m_screenWidth - mConfig.def_sub_padding_l - mConfig.def_sub_w;
        mAlphaFrValueAnimator.setIntValues(mMaxW + width,mMaxW);
        mAlphaFrValueAnimator.start();
    }

    private void findSeekBar()
    {
        if(mAlphaFr != null && mAlphaFr.getChildCount() > 0)
        {
            for(int i = 0; i < mAlphaFr.getChildCount(); i++)
            {
                if(mAlphaFr.getChildAt(i) instanceof MySeekBar)
                {
                    mSeekBar = (MySeekBar) mAlphaFr.getChildAt(i);
                    break;
                }
            }
        }
    }

    interface MySeekBarCB extends MySeekBar.OnProgressChangeListener
    {
        void onSeekBarShowStart(MySeekBar mySeekBar);

        void onSeekBarLayoutFinish(MySeekBar mySeekBar);
    }

    interface AnimaChangeCallBack
    {
        void change(View view,float value,boolean isOpen);
    }

}
