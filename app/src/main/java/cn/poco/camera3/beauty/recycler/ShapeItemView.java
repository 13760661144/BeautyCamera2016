package cn.poco.camera3.beauty.recycler;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import cn.poco.dynamicSticker.newSticker.CropCircleTransformation;
import cn.poco.recycleview.AbsAdapter;
import cn.poco.recycleview.BaseItem;
import cn.poco.tianutils.ShareData;
import my.beautyCamera.R;

/**
 * @author lmx
 *         Created by lmx on 2017-12-08.
 */

public class ShapeItemView extends BaseItem
{
    protected ShapeExAdapterConfig mConfig;

    protected FrameLayout mFrHead;
    protected ShapeCircleView mIVLogo;
    protected ImageView mIVIcon;
    protected TextView mTVTitle;

    protected FrameLayout mSubFr;
    protected ValueAnimator mSubValueAnimator;

    protected ShapeSubAdapter mSubAdapter;
    protected RecyclerView mSubRCView;
    protected OnChangeSubFrCB mCb;
    protected OnSubControlCB mSCb;
    protected OnSubItemClickCB mSICb;

    protected CropCircleTransformation mTransformation;

    private static final int DURATION = 200;
    private boolean isSelect = false;
    private boolean isAnimation = false;
    private boolean isOpenSub = false;

    protected int mWidth;

    public ShapeItemView(@NonNull Context context, final ShapeExAdapterConfig mConfig)
    {
        super(context);
        this.mConfig = mConfig;
        mWidth = mConfig.def_item_w;
        initView();
        mSubValueAnimator = new ValueAnimator();
        mSubValueAnimator.setDuration(DURATION);
        mSubValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                mWidth = (Integer) animation.getAnimatedValue();
                if (mSubFr != null)
                {
                    LayoutParams params = (LayoutParams) mSubFr.getLayoutParams();
                    params.width = mWidth - mConfig.def_item_w;
                    mSubFr.requestLayout();
                }
                requestLayout();
                if (mCb != null)
                {
                    mCb.change(ShapeItemView.this, animation.getAnimatedFraction());
                }
            }
        });
        mSubValueAnimator.addListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationStart(Animator animation)
            {
                isAnimation = true;

                if (mSCb != null)
                {
                    mSCb.onStartSubFrAnimation(ShapeItemView.this);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation)
            {
                isAnimation = false;
                if (mWidth == mConfig.def_item_w)
                {
                    //关闭二级
                    removeSubFr();
                    if (mSCb != null)
                    {
                        mSCb.onFinishSubFrAnimation(ShapeItemView.this);
                    }
                }
                else
                {
                    RecyclerView recyclerView = (RecyclerView) ShapeItemView.this.getParent();
                    if (recyclerView != null)
                    {
                        if (recyclerView.getScrollState() == RecyclerView.SCROLL_STATE_IDLE)
                        {
                            if (mSCb != null)
                            {
                                mSCb.onFinishSubFrAnimation(ShapeItemView.this);
                            }
                        }
                        else
                        {
                            recyclerView.addOnScrollListener(mScrollListener);
                        }
                    }
                }
            }
        });
    }

    private void initView()
    {
        mFrHead = new FrameLayout(getContext());
        LayoutParams params = new LayoutParams(mConfig.def_item_w, mConfig.def_item_h);
        params.gravity = Gravity.CENTER_VERTICAL | Gravity.START;
        this.addView(mFrHead, params);
        {
            mIVLogo = new ShapeCircleView(getContext());
            mIVLogo.setScaleType(ImageView.ScaleType.FIT_CENTER);
            params = new LayoutParams(mConfig.def_item_w, mConfig.def_item_w);
            params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
            mFrHead.addView(mIVLogo, params);

            mIVIcon = new ImageView(getContext());
            mIVIcon.setVisibility(View.GONE);
            mIVIcon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            params = new LayoutParams(mConfig.def_item_mask_icon_w, mConfig.def_item_mask_icon_w);
            params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
            params.topMargin = (mConfig.def_item_w - mConfig.def_item_mask_icon_w) / 2;
            mFrHead.addView(mIVIcon, params);

            mTVTitle = new TextView(getContext());
            mTVTitle.setTextColor(0xff000000);
            mTVTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 11);
            mTVTitle.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);
            mTVTitle.setSingleLine();
            params = new LayoutParams(mConfig.def_item_w, LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
            params.topMargin = mConfig.def_item_w + mConfig.def_title_top_margin;
            mFrHead.addView(mTVTitle, params);
        }
    }

    public View getHeadView()
    {
        return mFrHead;
    }

    protected RecyclerView.OnScrollListener mScrollListener = new RecyclerView.OnScrollListener()
    {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState)
        {
            if (newState == RecyclerView.SCROLL_STATE_IDLE)
            {
                if (mSCb != null)
                {
                    mSCb.onFinishSubFrAnimation(ShapeItemView.this);
                }
                recyclerView.removeOnScrollListener(mScrollListener);
            }
            super.onScrollStateChanged(recyclerView, newState);
        }
    };

    public void setTransformation(CropCircleTransformation mTransformation)
    {
        this.mTransformation = mTransformation;
    }

    @Override
    public void setOnTouchListener(OnTouchListener l)
    {
        //super.setOnTouchListener(l);
        if (mFrHead != null)
        {
            mFrHead.setOnTouchListener(l);
        }
    }

    @Override
    public void setTag(Object tag)
    {
        super.setTag(tag);
        if (mFrHead != null)
        {
            mFrHead.setTag(tag);
        }
    }

    public boolean isAnimation()
    {
        return isAnimation;
    }

    public void setSubControlCB(OnSubControlCB mSCb)
    {
        this.mSCb = mSCb;
    }

    public void setChangeSubFrCB(OnChangeSubFrCB mcb)
    {
        this.mCb = mcb;
    }

    public void setSubItemClickCB(OnSubItemClickCB mSICb)
    {
        this.mSICb = mSICb;
    }

    public FrameLayout initSubFr(int position, ArrayList<ShapeExAdapter.ShapeSubInfo> list)
    {
        mSubRCView = new RecyclerView(getContext());
        mSubRCView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        mSubRCView.setHorizontalScrollBarEnabled(false);
        mSubRCView.setOverScrollMode(OVER_SCROLL_NEVER);
        mSubRCView.setPadding(0, mConfig.def_sub_top_padding, 0, 0);
        mSubRCView.setClipToPadding(false);
        mSubRCView.addItemDecoration(mConfig.def_shape_sub_item_decoration);
        ((SimpleItemAnimator) mSubRCView.getItemAnimator()).setSupportsChangeAnimations(false);
        mSubRCView.getItemAnimator().setChangeDuration(0);

        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        params.gravity = Gravity.CENTER_VERTICAL | Gravity.START;
        params.leftMargin = mConfig.def_open_sub_parent_left_margin;
        mSubFr = new FrameLayout(getContext());
        mSubFr.addView(mSubRCView, params);

        mSubAdapter = new ShapeSubAdapter(mConfig, list);
        mSubAdapter.setSubClickCB(mSubClickListener);
        mSubRCView.setAdapter(mSubAdapter);
        return mSubFr;
    }

    public void removeSubFr()
    {
        if (mSubRCView != null)
        {
            mSubRCView.removeItemDecoration(mConfig.def_shape_sub_item_decoration);
            mSubRCView = null;
        }
        if (mSubFr != null)
        {
            mSubFr.removeAllViews();
            removeView(mSubFr);
            mSubFr = null;
        }

        if (mSubAdapter != null)
        {
            mSubAdapter.clear();
            mSubAdapter = null;
        }
    }

    public void clearAll()
    {
        removeSubFr();
        mCb = null;
        mSCb = null;
        mSICb = null;
        mScrollListener = null;
        mSubValueAnimator = null;
        mConfig = null;
        mTransformation = null;
    }

    private ShapeSubAdapter.OnSubItemClickListener mSubClickListener = new ShapeSubAdapter.OnSubItemClickListener()
    {
        @Override
        public void onSubClick(int position, ShapeExAdapter.ShapeSubInfo subInfo)
        {

            if (mSICb != null) mSICb.onSubItemClick((Integer) getTag(), position, subInfo);
        }
    };

    public void openSubFr(int position, ArrayList<ShapeExAdapter.ShapeSubInfo> subList, OnChangeSubFrCB cb)
    {
        mCb = cb;
        removeSubFr();
        mSubFr = initSubFr(position, subList);
        int width = ShareData.m_screenRealWidth - mConfig.def_item_w - mConfig.def_open_sub_parent_offset_left;
        LayoutParams params = new LayoutParams(width, LayoutParams.MATCH_PARENT);
        params.gravity = Gravity.CENTER_VERTICAL | Gravity.START;
        params.leftMargin = mConfig.def_item_w;
        this.addView(mSubFr, params);
        if (mSubValueAnimator != null)
        {
            mSubValueAnimator.setIntValues(mConfig.def_item_w, ShareData.m_screenRealWidth - mConfig.def_open_sub_parent_offset_left);
            mSubValueAnimator.start();
        }
        isOpenSub = true;
    }

    public void closeSubFr(OnChangeSubFrCB cb)
    {
        mCb = cb;
        if (mSubValueAnimator != null)
        {
            mSubValueAnimator.setIntValues(ShareData.m_screenRealWidth - mConfig.def_open_sub_parent_offset_left, mConfig.def_item_w);
            mSubValueAnimator.start();
        }
        isOpenSub = false;
    }

    public boolean isOpenSub()
    {
        return isOpenSub;
    }

    public void openSubMode()
    {
        mIVIcon.setVisibility(View.VISIBLE);
        mIVIcon.setImageResource(R.drawable.ic_shape_item_back);
        if (mIVLogo != null) mIVLogo.onOpenSub(true);
    }

    public void closeSubMode()
    {
        if (isSelect())
        {
            onSelected();
        }
        else
        {
            onUnSelected();
        }
        if (mIVLogo != null) mIVLogo.onOpenSub(false);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(MeasureSpec.makeMeasureSpec(mWidth, MeasureSpec.EXACTLY), heightMeasureSpec);
    }

    public boolean isSelect()
    {
        return this.isSelect;
    }

    @Override
    public void onSelected()
    {
        this.isSelect = true;
        mIVIcon.setVisibility(View.VISIBLE);
        mIVIcon.setImageResource(R.drawable.ic_shape_item_edit);
        if (mIVLogo != null) mIVLogo.onSelected();
    }

    @Override
    public void onUnSelected()
    {
        this.isSelect = false;
        mIVIcon.setVisibility(View.GONE);
        if (mIVLogo != null) mIVLogo.onUnSelected();
    }


    @Override
    public void onClick()
    {

    }

    @Override
    public void SetData(AbsAdapter.ItemInfo info, int index)
    {
        if (info != null && info instanceof ShapeExAdapter.ShapeExItemInfo)
        {
            if (mTransformation != null)
            {
                Glide.with(getContext())
                        .load(((ShapeExAdapter.ShapeExItemInfo) info).m_logo)
                        .asBitmap()
                        .transform(mTransformation)
                        .into(mIVLogo);
            }
            else
            {
                Glide.with(getContext())
                        .load(((ShapeExAdapter.ShapeExItemInfo) info).m_logo)
                        .into(mIVLogo);
            }
            mTVTitle.setText(((ShapeExAdapter.ShapeExItemInfo) info).m_name);
        }
    }

    // ====== animation callback =======
    public interface OnChangeSubFrCB
    {
        void change(ShapeItemView itemView, float value);
    }

    public interface OnSubControlCB
    {
        void onStartSubFrAnimation(ShapeItemView itemView);

        void onFinishSubFrAnimation(ShapeItemView itemView);
    }

    // ====== sub recycler adapter click callback =======
    public interface OnSubItemClickCB
    {
        void onSubItemClick(int parentPosition, int subPosition, ShapeExAdapter.ShapeSubInfo subInfo);
    }
}
