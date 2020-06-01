package cn.poco.makeup.makeup_abs;


import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import cn.poco.makeup.MySeekBar;
import cn.poco.recycleview.AbsAdapter;
import cn.poco.recycleview.AbsConfig;
import cn.poco.tianutils.ShareData;

public abstract class BaseAlphaFrItem extends LinearLayout {
    public BaseItemWithAlphaFrMode m_Item;
    public FrameLayout m_alphaFr;
    public AbsConfig m_config;
    private boolean m_isOpenAnim = false;
    protected ValueAnimator mAlphaFrValueAnimator;
    protected MySeekBar mSeekBar;
    protected MySeekBar.OnProgressChangeListener mProgressChangeListener;
    protected AbsAdapter.ItemInfo m_itemInfo;
    protected AnimChangeCallBack m_animchangeCB;
    protected int mWidth;
    protected int mItemWidth;
    public BaseAlphaFrItem(Context context,AbsConfig config) {
        super(context);
        m_config = config;
        initData();
        initUI();
    }

    private void initData()
    {
        mWidth = m_config.def_item_w;
        mItemWidth = m_config.def_item_w;
        mAlphaFrValueAnimator = new ValueAnimator();
        mAlphaFrValueAnimator.setDuration(300);
        mAlphaFrValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mWidth = (int) animation.getAnimatedValue();
                if(m_alphaFr != null && mWidth > mItemWidth)
                {
                    LinearLayout.LayoutParams fl = (LinearLayout.LayoutParams) m_alphaFr.getLayoutParams();
                    fl.width = mWidth - mItemWidth;
                    m_alphaFr.setLayoutParams(fl);
                }

                if(m_animchangeCB != null)
                {
                      View view = m_Item;
                      m_animchangeCB.change(view,animation.getAnimatedFraction(),m_isOpenAnim);
                }
            }
        });
        mAlphaFrValueAnimator.addListener(new ValueAnimator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                if(mProgressChangeListener != null)
                {
                    MySeekBarAllCallBack mySeekBarCB = (MySeekBarAllCallBack) mProgressChangeListener;
                    mySeekBarCB.AlphaFrLayoutStart(mSeekBar);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if(mWidth == mItemWidth)
                {
                    if(m_alphaFr != null)
                    {
                        BaseAlphaFrItem.this.removeView(m_alphaFr);
                        m_alphaFr = null;
                    }
                }

                if(mProgressChangeListener != null)
                {
                    final RecyclerView recyclerView = (RecyclerView) BaseAlphaFrItem.this.getParent();
                    if(recyclerView != null)
                    {
                        if(recyclerView.getScrollState() == RecyclerView.SCROLL_STATE_IDLE)
                        {
                            if(mProgressChangeListener != null)
                            {
                                MySeekBarAllCallBack mySeekBarCB = (MySeekBarAllCallBack) mProgressChangeListener;
                                mySeekBarCB.AlphaFrLayoutFinish(mSeekBar);
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

    public void setChangeCB(AnimChangeCallBack cb)
    {
        m_animchangeCB = cb;
    }

    public void setOnProgressChangeListener(MySeekBar.OnProgressChangeListener onProgressChangeListener)
    {
        mProgressChangeListener = onProgressChangeListener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(mWidth, MeasureSpec.EXACTLY), heightMeasureSpec);
    }

    private void initUI()
    {
        this.setOrientation(LinearLayout.HORIZONTAL);
        m_Item = initBaseItem();
        LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(m_config.def_item_w,m_config.def_item_h);
        this.addView(m_Item,ll);
    }

    public void setItemInfo(AbsAdapter.ItemInfo itemInfo)
    {
        this.m_itemInfo = itemInfo;
        m_Item.SetData(itemInfo,-1);
    }

    RecyclerView.OnScrollListener m_scrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            if(newState == RecyclerView.SCROLL_STATE_IDLE)
            {
                recyclerView.removeOnScrollListener(m_scrollListener);
                if(mProgressChangeListener != null)
                {
                    MySeekBarAllCallBack mySeekBarCB = (MySeekBarAllCallBack) mProgressChangeListener;
                    mySeekBarCB.AlphaFrLayoutFinish(mSeekBar);
                }
            }
            super.onScrollStateChanged(recyclerView, newState);
        }
    };

    protected void openAlphaFr()
    {
        m_isOpenAnim = true;
        if(m_alphaFr != null)
        {
            this.removeView(m_alphaFr);
            m_alphaFr = null;
        }

        m_alphaFr = initAlphaFr();
        findSeekBar();
        int width = ShareData.m_screenWidth - m_config.def_item_w - m_config.def_item_l;
        if(m_alphaFr != null)
        {
            LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(width,m_config.def_item_h);
            this.addView(m_alphaFr,ll);
        }

        mAlphaFrValueAnimator.setIntValues(mItemWidth,mItemWidth + width);
        mAlphaFrValueAnimator.start();
    }

    public void closeAlphaFr()
    {
        m_isOpenAnim = false;
        int width = ShareData.m_screenWidth - m_config.def_item_w - m_config.def_item_l;
        mAlphaFrValueAnimator.setIntValues(mItemWidth +width,mItemWidth);
        mAlphaFrValueAnimator.start();
    }


    private void findSeekBar()
    {
        if(m_alphaFr != null && m_alphaFr.getChildCount() > 0)
        {
            for(int i = 0; i < m_alphaFr.getChildCount(); i++)
            {
                if(m_alphaFr.getChildAt(i) instanceof MySeekBar)
                {
                    mSeekBar = (MySeekBar) m_alphaFr.getChildAt(i);
                    mSeekBar.setOnProgressChangeListener(mProgressChangeListener);
                    break;
                }
            }
        }
    }

    protected abstract BaseItemWithAlphaFrMode initBaseItem();

    protected abstract FrameLayout initAlphaFr();

    //调整透明度ui在做弹出收回动画时的回调接口
    interface AnimChangeCallBack
    {
        public void change(View view,float value,boolean isOpening);
    }

    //调整透明度的ui的回调接口
    interface MySeekBarAllCallBack extends MySeekBar.OnProgressChangeListener
    {
        public void AlphaFrLayoutStart(MySeekBar mySeekBar);

        public void AlphaFrLayoutFinish(MySeekBar mySeekBar);
    }
}
