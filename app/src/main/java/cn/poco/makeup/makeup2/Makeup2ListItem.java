package cn.poco.makeup.makeup2;


import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import cn.poco.makeup.MySeekBar;
import cn.poco.recycleview.AbsExConfig;
import cn.poco.recycleview.BaseGroup;
import cn.poco.recycleview.BaseItem;
import cn.poco.recycleview.BaseItemContainer;
import cn.poco.tianutils.ShareData;

public class Makeup2ListItem extends BaseItemContainer {
    protected ValueAnimator mAlphaValueAnimator;
    protected FrameLayout m_alphaFr;
    protected onChangeAlphaFrCB m_changeCB;
    public Makeup2ListItem(Context context, AbsExConfig config) {
        super(context, config);
        mAlphaValueAnimator = new ValueAnimator();
        mAlphaValueAnimator.setDuration(DURATION);
        mAlphaValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mWidth = (int) animation.getAnimatedValue();
                if(m_alphaFr != null)
                {
                    LinearLayout.LayoutParams ll = (LayoutParams) m_alphaFr.getLayoutParams();
                    ll.width = mWidth - mMaxW;
                    m_alphaFr.setLayoutParams(ll);
                }
                requestLayout();
                if(m_changeCB!= null)
                {
                    m_changeCB.change(animation.getAnimatedFraction());
                }
            }
        });
        mAlphaValueAnimator.addListener(new ValueAnimator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                if(m_progressChangeCB != null)
                {
                    m_progressChangeCB.onSeekBarStartShow(m_alphaSeekBar);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (mWidth == mMaxW) {
                    removeAlphaFr();
                    if(m_progressChangeCB != null)
                    {
                        m_progressChangeCB.onFinishLayoutAlphaFr(m_alphaSeekBar);
                    }
                }
                else
                {
                    final RecyclerView recyclerView = (RecyclerView) Makeup2ListItem.this.getParent();
                    if(recyclerView != null)
                    {
                        if(recyclerView.getScrollState() == RecyclerView.SCROLL_STATE_IDLE)
                        {
                            if(m_progressChangeCB != null)
                            {
                                m_progressChangeCB.onFinishLayoutAlphaFr(m_alphaSeekBar);
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
                if(m_progressChangeCB != null)
                {
                    m_progressChangeCB.onFinishLayoutAlphaFr(m_alphaSeekBar);
                }
            }
            super.onScrollStateChanged(recyclerView, newState);
        }
    };

    interface onChangeAlphaFrCB
    {
        void change(float value);
    }

    @Override
    public BaseGroup initGroupView() {
        return new Makeup2Group(getContext(),mConfig);
    }

    @Override
    public BaseItem initItemView() {
        return new Makeup2SubItem(getContext(),mConfig);
    }

    public void openAlphaFr(int index,onChangeAlphaFrCB cb)
    {
        m_changeCB = cb;
        removeAlphaFr();
        m_alphaFr = initAlphaFr();
        LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(ShareData.m_screenWidth - mConfig.def_sub_w - mConfig.def_sub_l, LayoutParams.MATCH_PARENT);
        m_alphaFr.setLayoutParams(ll);
        this.addView(m_alphaFr,index + 1);

        mAlphaValueAnimator.setIntValues(mMaxW, mMaxW + (ShareData.m_screenWidth - mConfig.def_sub_w - mConfig.def_sub_l));
        mAlphaValueAnimator.start();
    }

    private MySeekBar m_alphaSeekBar;
    public FrameLayout initAlphaFr()
    {
        m_alphaFr = new FrameLayout(getContext());
        m_alphaSeekBar = new MySeekBar(getContext());
        FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(ShareData.PxToDpi_xhdpi(480), LayoutParams.MATCH_PARENT);
        fl.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
        fl.leftMargin = ShareData.PxToDpi_xhdpi(30);
        m_alphaSeekBar.setLayoutParams(fl);
        m_alphaFr.addView(m_alphaSeekBar);
        m_alphaSeekBar.setOnProgressChangeListener(m_simpleSeekBarListener);
        m_alphaSeekBar.setBackgroundColor(0x57000000);
        return m_alphaFr;
    }

    interface ProgressChangeCB
    {
        public void onProgressChange(MySeekBar seekBar,int progress);

        public void onStartTrackingTouch(MySeekBar seekBar);

        public void onStopTrackingTouch(MySeekBar seekBar);

        public void onSeekBarStartShow(MySeekBar seekBar);

        public void onFinishLayoutAlphaFr(MySeekBar seekBar);
    }


    private ProgressChangeCB m_progressChangeCB;

    public void setProgressChangeCB(ProgressChangeCB progressChangeCB)
    {
        m_progressChangeCB = progressChangeCB;
    }

    MySeekBar.OnProgressChangeListener m_simpleSeekBarListener = new MySeekBar.OnProgressChangeListener() {
        @Override
        public void onProgressChanged(MySeekBar seekBar, int progress) {
            if(m_progressChangeCB != null)
            {
                m_progressChangeCB.onProgressChange(seekBar,progress);
            }
        }

        @Override
        public void onStartTrackingTouch(MySeekBar seekBar) {
            if(m_progressChangeCB != null)
            {
                m_progressChangeCB.onStartTrackingTouch(seekBar);
            }
        }

        @Override
        public void onStopTrackingTouch(MySeekBar seekBar) {
            if(m_progressChangeCB != null)
            {
                m_progressChangeCB.onStopTrackingTouch(seekBar);
            }
        }
    };

    public void removeAlphaFr()
    {
        if(m_alphaFr != null)
        {
            this.removeView(m_alphaFr);
            m_alphaFr = null;
        }
    }



    public void closeAlphaFr(onChangeAlphaFrCB cb)
    {
        m_changeCB = cb;
        Makeup2ListConfig config = (Makeup2ListConfig) mConfig;
        mAlphaValueAnimator.setIntValues(mMaxW + (ShareData.m_screenWidth - config.def_sub_w - config.def_alphafr_leftMargin),mMaxW);
        mAlphaValueAnimator.start();
    }



}
