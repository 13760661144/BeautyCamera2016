package cn.poco.camera3.beauty.page;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.HashMap;

import cn.poco.advanced.ImageUtils;
import cn.poco.camera3.beauty.BeautySeekBar;
import cn.poco.camera3.beauty.STag;
import cn.poco.camera3.beauty.TabUIConfig;
import cn.poco.camera3.beauty.callback.IShapePageCallback;
import cn.poco.camera3.beauty.data.BeautyShapeDataUtils;
import cn.poco.camera3.beauty.data.ShapeData;
import cn.poco.camera3.beauty.data.ShapeDataType;
import cn.poco.camera3.beauty.data.SuperShapeData;
import cn.poco.camera3.beauty.recycler.ShapeExAdapter;
import cn.poco.camera3.beauty.recycler.ShapeExAdapterConfig;
import cn.poco.camera3.beauty.recycler.ShapeItemView;
import cn.poco.camera3.beauty.recycler.ShapeRecyclerView;
import cn.poco.camera3.util.CameraPercentUtil;
import cn.poco.dynamicSticker.newSticker.CropCircleTransformation;
import cn.poco.recycleview.AbsAdapter;
import cn.poco.recycleview.ListItemDecoration;
import cn.poco.rise.RiseSeekBar;

/**
 * @author lmx
 *         Created by lmx on 2018-01-16.
 */

public class ShapeFramePager extends BaseFramePager
{
    public static final String SHAPE_FRAME_PAGER_TAG = "ShapeFramePager";

    public static final String BUNDLE_KEY_DATA_SHAPE_LIST = "bundle_key_data_shape_list";
    protected boolean isShowSeekBar;

    protected FrameLayout mSeekBarView;
    protected SeekBarView mSeekBar;

    protected ArrayList<ShapeExAdapter.ShapeExItemInfo> mList;
    protected ShapeRecyclerView mRecyclerView;
    protected ShapeExAdapter mAdapter;
    protected ShapeExAdapterConfig mConfig;


    // callback
    protected ShapeExAdapter.OnExItemClickListener mAdapterItemCB;
    protected ShapeExAdapter.OnAnimationScrolling mAdapterScrollCB;
    protected BeautySeekBar.OnSeekBarChangeListener mBeautySeekCB;
    protected RiseSeekBar.OnSeekBarChangeListener mRiseSeekCB;


    public static class SeekBarView
    {
        View mBar;
        @ShapeDataType
        int mType = ShapeDataType.UNSET;
    }

    public ShapeFramePager(@NonNull Context context, TabUIConfig mTabUIConfig)
    {
        super(context, mTabUIConfig);
    }

    @Override
    public void setData(HashMap<String, Object> data)
    {
        super.setData(data);
        if (data != null)
        {
            if (data.containsKey(BUNDLE_KEY_DATA_SHAPE_LIST))
            {
                mList = (ArrayList<ShapeExAdapter.ShapeExItemInfo>) data.get(BUNDLE_KEY_DATA_SHAPE_LIST);
            }
        }

        if (mList != null && mAdapter != null)
        {
            mAdapter.SetData(mList);
            mAdapter.notifyDataSetChanged();
        }
    }


    @Override
    public void onCreateContainerView(@NonNull FrameLayout parentLayout)
    {
        mSeekBarView = new FrameLayout(getContext());
        mSeekBarView.setVisibility(View.GONE);
        FrameLayout.LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, CameraPercentUtil.HeightPxToPercent(88 + 26 + 4));
        params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
        params.bottomMargin = CameraPercentUtil.HeightPxToPercent(360 - (88 + 26 + 4));
        this.addView(mSeekBarView, params);

        mSeekBar = new SeekBarView();
        mSeekBar.mBar = new BeautySeekBar(getContext());
        mSeekBar.mBar.setTag(STag.SeekBarType.SEEK_TAG_UNIDIRECTIONAL);
        ((BeautySeekBar) mSeekBar.mBar).setSeekLineBkColor(ImageUtils.GetColorAlpha(Color.BLACK, 0.24f));
        ((BeautySeekBar) mSeekBar.mBar).setInnCircleColor(Color.WHITE);
        ((BeautySeekBar) mSeekBar.mBar).setOnSeekBarChangeListener(mBeautySeekCB);
        params = new LayoutParams(CameraPercentUtil.WidthPxToPercent(646), CameraPercentUtil.HeightPxToPercent(88));
        params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
        params.topMargin = CameraPercentUtil.HeightPxToPercent(70 - 44 + 4);
        mSeekBarView.addView(mSeekBar.mBar, params);

        mRecyclerView = new ShapeRecyclerView(getContext());
        mRecyclerView.setBackgroundColor(Color.WHITE);

        //360 - (88+26+4)
        params = new LayoutParams(LayoutParams.MATCH_PARENT, CameraPercentUtil.HeightPxToPercent(360 - (88 + 26 + 4)));
        params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
        this.addView(mRecyclerView, params);

        mAdapter.setRecyclerView(mRecyclerView);
        mAdapter.setScrollingListener(mAdapterScrollCB);

        mRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mRecyclerView.setHorizontalScrollBarEnabled(false);
        mRecyclerView.setLayoutManager(mConfig.mLinearLayoutManager);
        mRecyclerView.addItemDecoration(new ListItemDecoration(mConfig.def_item_l, ListItemDecoration.HORIZONTAL));
        mRecyclerView.setPadding(
                mAdapter.m_config.def_parent_left_padding,
                mAdapter.m_config.def_parent_top_padding,
                mAdapter.m_config.def_parent_right_padding,
                mAdapter.m_config.def_parent_bottom_padding);
        mRecyclerView.setClipToPadding(false);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void initData()
    {
        initCallBack();
        mConfig = new ShapeExAdapterConfig(getContext());
        mAdapter = new ShapeExAdapter(mConfig);
        mAdapter.setExOnItemClickListener(mAdapterItemCB);
        mAdapter.setTransformation(new CropCircleTransformation(getContext()));
    }

    @Override
    public String getFramePagerTAG()
    {
        return SHAPE_FRAME_PAGER_TAG;
    }

    @Override
    public void onPageSelected(int position, Object pageTag)
    {

    }

    @Override
    public void onPause()
    {

    }

    @Override
    public void onResume()
    {
    }

    @Override
    public void onClose()
    {
        super.onClose();
        if (mAdapter != null)
        {
            mAdapter.ClearAll();
        }
        if (mConfig != null)
        {
            mConfig.ClearAll();
        }

        mAdapter = null;
        mConfig = null;
        mList = null;
        mAdapterItemCB = null;
        mAdapterScrollCB = null;
        mBeautySeekCB = null;
        mRiseSeekCB = null;
    }

    @Override
    public void notifyDataChanged()
    {

    }

    public void setSelectShapeId(final int shapeId)
    {
        if (mAdapter != null)
        {
            int index = mAdapter.GetIndex(shapeId);
            if (index != mAdapter.GetSelectIndex())
            {
                boolean delay = false;
                //先关闭原来的
                View viewByPosition = mConfig.mLinearLayoutManager.findViewByPosition(mAdapter.GetSelectIndex());
                if (mAdapter.isShowingSubFr()
                        && viewByPosition != null
                        && viewByPosition instanceof ShapeItemView
                        && ((ShapeItemView) viewByPosition).isSelect()
                        && ((ShapeItemView) viewByPosition).isOpenSub())
                {
                    mAdapter.performClick(((ShapeItemView) viewByPosition).getHeadView());
                    delay = true;
                }
                this.postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if (mAdapter != null) {
                            mAdapter.SetSelectByUri(shapeId, true, true);
                        }
                    }
                }, delay ? 300 : 0);
            }
            else if (index == mAdapter.GetSelectIndex())
            {
                if (mAdapterItemCB != null) {
                    ShapeExAdapter.ShapeExItemInfo itemInfo = (ShapeExAdapter.ShapeExItemInfo) mAdapter.GetItemInfoByIndex(index);
                    mAdapterItemCB.OnItemClick(itemInfo, index);
                }
            }
            else {
                this.postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if (mAdapter != null) {
                            mAdapter.SetSelectByUri(shapeId, true, true);
                        }
                    }
                }, 300);
            }
        }
    }

    public void setShapeSubOpen(final int shapeId, boolean open)
    {
        if (mAdapter != null)
        {
            int index = mAdapter.GetIndex(shapeId);
            if (index == mAdapter.GetSelectIndex())
            {
                View viewByPosition = mConfig.mLinearLayoutManager.findViewByPosition(mAdapter.GetSelectIndex());
                if (viewByPosition != null
                        && viewByPosition instanceof ShapeItemView)
                {
                    if (open)
                    {
                        if (!mAdapter.isShowingSubFr() && !((ShapeItemView) viewByPosition).isOpenSub())
                        {
                            mAdapter.performClick(((ShapeItemView) viewByPosition).getHeadView());
                        }
                    }
                    else
                    {
                        if (mAdapter.isShowingSubFr() && ((ShapeItemView) viewByPosition).isOpenSub())
                        {
                            mAdapter.performClick(((ShapeItemView) viewByPosition).getHeadView());
                        }
                    }
                }
            }
        }
    }

    public boolean resetCurrentShapeData()
    {
        return mAdapter != null && mAdapter.resetCurrentItemInfo();
    }

    public boolean resetShapeIdItemInfo(int shapeId)
    {
        return mAdapter != null && mAdapter.resetShapeIdItemInfo(shapeId);
    }

    public ShapeData updateSeekBarShapeData()
    {
        ShapeExAdapter.ShapeExItemInfo currentItemInfo = mAdapter.getCurrentItemInfo();
        if (currentItemInfo != null && currentItemInfo.m_uri != SuperShapeData.ID_NON_SHAPE)
        {
            //更新当前type seekbar ui
            if (isShowSeekBar() && mSeekBar != null && mSeekBar.mBar != null && mSeekBar.mType != ShapeDataType.UNSET)
            {
                int uiProgress = (int) currentItemInfo.getData(mSeekBar.mType);
                if (mSeekBar.mBar instanceof BeautySeekBar)
                {
                    ((BeautySeekBar) mSeekBar.mBar).setProgress(uiProgress);
                }
                else if (mSeekBar.mBar instanceof RiseSeekBar)
                {
                    uiProgress = BeautyShapeDataUtils.GetBidirectionalRealSize(uiProgress);
                    ((RiseSeekBar) mSeekBar.mBar).setProgress(uiProgress);
                }
            }
            return currentItemInfo.m_data.getData();
        }
        return null;
    }

    public ShapeData updateShapeData(@ShapeDataType int type, int shapeId, int uiProgress)
    {
        ShapeExAdapter.ShapeExItemInfo currentItemInfo = mAdapter.getCurrentItemInfo();
        if (currentItemInfo != null && currentItemInfo.m_uri == shapeId)
        {
            if (BeautyShapeDataUtils.GetSeekbarType(type) == STag.SeekBarType.SEEK_TAG_BIDIRECTIONAL)
            {
                //双向
                currentItemInfo.updateData(type, BeautyShapeDataUtils.GetBidirectionalRealSize(uiProgress));
            }
            else
            {
                //单向
                currentItemInfo.updateData(type, uiProgress);
            }
            //更新当前type seekbar ui
            if (isShowSeekBar() && mSeekBar != null && mSeekBar.mBar != null && mSeekBar.mType == type)
            {
                if (mSeekBar.mBar instanceof BeautySeekBar)
                {
                    ((BeautySeekBar) mSeekBar.mBar).setProgress(uiProgress);
                }
                else if (mSeekBar.mBar instanceof RiseSeekBar)
                {
                    ((RiseSeekBar) mSeekBar.mBar).setProgress(uiProgress);
                }
            }
            return currentItemInfo.m_data.getData();
        }
        return null;
    }

    public void changeSeekBarType(@STag.SeekBarType int seekBarType, @ShapeDataType int shapeDataType, int progress)
    {
        if (mSeekBar.mBar != null && mSeekBarView != null)
        {
            int currentSeekType = (Integer) mSeekBar.mBar.getTag();
            if (currentSeekType != seekBarType)
            {
                mSeekBarView.removeView(mSeekBar.mBar);
                if (mSeekBar.mBar instanceof BeautySeekBar)
                {
                    ((BeautySeekBar) mSeekBar.mBar).setOnSeekBarChangeListener(null);
                }
                else if (mSeekBar.mBar instanceof RiseSeekBar)
                {
                    ((RiseSeekBar) mSeekBar.mBar).setOnSeekBarChangeListener(null);
                }
                mSeekBar.mBar = null;
                mSeekBar.mType = ShapeDataType.UNSET;

                switch (seekBarType)
                {
                    //双向
                    case STag.SeekBarType.SEEK_TAG_BIDIRECTIONAL:
                    {
                        mSeekBar.mBar = new RiseSeekBar(getContext());
                        mSeekBar.mBar.setTag(STag.SeekBarType.SEEK_TAG_BIDIRECTIONAL);
                        mSeekBar.mType = shapeDataType;
                        ((RiseSeekBar) mSeekBar.mBar).setOnSeekBarChangeListener(mRiseSeekCB);
                        ((RiseSeekBar) mSeekBar.mBar).setInnCircleColor(Color.WHITE);
                        ((RiseSeekBar) mSeekBar.mBar).setProgress(progress);
                        ((RiseSeekBar) mSeekBar.mBar).setSeekLineBkColor(ImageUtils.GetColorAlpha(Color.BLACK, 0.24f));
                        break;
                    }
                    //单向
                    case STag.SeekBarType.SEEK_TAG_UNIDIRECTIONAL:
                    {
                        mSeekBar.mBar = new BeautySeekBar(getContext());
                        mSeekBar.mBar.setTag(STag.SeekBarType.SEEK_TAG_UNIDIRECTIONAL);
                        mSeekBar.mType = shapeDataType;
                        ((BeautySeekBar) mSeekBar.mBar).setOnSeekBarChangeListener(mBeautySeekCB);
                        ((BeautySeekBar) mSeekBar.mBar).setProgress(progress);
                        ((BeautySeekBar) mSeekBar.mBar).setInnCircleColor(Color.WHITE);
                        ((BeautySeekBar) mSeekBar.mBar).setSeekLineBkColor(ImageUtils.GetColorAlpha(Color.BLACK, 0.24f));
                        break;
                    }
                    default:
                        break;
                }
                if (mSeekBar.mBar != null)
                {
                    LayoutParams params = new LayoutParams(CameraPercentUtil.WidthPxToPercent(646), CameraPercentUtil.HeightPxToPercent(44 + 44));
                    params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
                    params.topMargin = CameraPercentUtil.HeightPxToPercent(26);
                    mSeekBarView.addView(mSeekBar.mBar, params);
                }
            }
            else
            {
                switch (currentSeekType)
                {
                    case STag.SeekBarType.SEEK_TAG_BIDIRECTIONAL:
                    {   //双向
                        ((RiseSeekBar) mSeekBar.mBar).setProgress(progress);
                        mSeekBar.mType = shapeDataType;
                        break;
                    }
                    case STag.SeekBarType.SEEK_TAG_UNIDIRECTIONAL:
                    {   //单向
                        ((BeautySeekBar) mSeekBar.mBar).setProgress(progress);
                        mSeekBar.mType = shapeDataType;
                        break;
                    }
                    default:
                        break;
                }
            }
        }
    }

    public void showTopBar(boolean showTopBar)
    {
        if (mCallback != null)
        {
            mCallback.onShowTopBar(showTopBar);
        }
    }

    public boolean isShowSeekBar()
    {
        return isShowSeekBar;
    }

    public void showSeekBar(final boolean showSeekBar)
    {
        if (this.isShowSeekBar == showSeekBar)
        {
            return;
        }
        this.isShowSeekBar = showSeekBar;
        float seekSY = 0, seekEY = 0;
        if (showSeekBar)
        {
            seekSY = CameraPercentUtil.HeightPxToPercent(124);
            seekEY = 0;
        }
        else
        {
            seekSY = 0;
            seekEY = CameraPercentUtil.HeightPxToPercent(124);
        }
        ObjectAnimator seekAnimator = ObjectAnimator.ofFloat(mSeekBarView, "translationY", seekSY, seekEY);
        seekAnimator.setDuration(200);
        seekAnimator.addListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                if (!isShowSeekBar)
                {
                    mSeekBarView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationStart(Animator animation)
            {
                if (isShowSeekBar)
                {
                    mSeekBarView.setVisibility(View.VISIBLE);
                }
            }
        });
        seekAnimator.start();
    }


    private void initCallBack()
    {
        if (mAdapterItemCB == null)
        {
            mAdapterItemCB = new ShapeExAdapter.OnExItemClickListener()
            {
                @Override
                public void onSubItemClick(int parentPosition,
                                           int subPosition,
                                           @ShapeDataType int type,
                                           ShapeExAdapter.ShapeExItemInfo itemInfo)
                {
                    //脸型 选项子item被选中
                    if (itemInfo != null && subPosition != -1)
                    {
                        int progress = (int) itemInfo.getData(type);
                        int seekBarType = BeautyShapeDataUtils.GetSeekbarType(type);
                        int uiProgress = progress;
                        if (seekBarType == STag.SeekBarType.SEEK_TAG_BIDIRECTIONAL)
                        {
                            //双向
                            uiProgress = BeautyShapeDataUtils.GetBidirectionalUISize(progress);
                        }

                        changeSeekBarType(seekBarType, type, uiProgress);
                        showTopBar(false);
                        showSeekBar(true);
                    }
                }

                @Override
                public void onResetShapeData(int parentPosition, int subPosition, ShapeExAdapter.ShapeExItemInfo itemInfo)
                {
                    //重置参数
                    if (subPosition > -1)
                    {
                        onSubItemClick(parentPosition, subPosition, itemInfo.m_subs.get(subPosition).m_type, itemInfo);
                    }
                    if (mCallback != null && mCallback instanceof IShapePageCallback)
                    {
                        ((IShapePageCallback) mCallback).onResetShapeData(-1, itemInfo, itemInfo.m_data.getData());
                    }
                }


                @Override
                public void OnItemClick(AbsAdapter.ItemInfo info, int index)
                {
                    //预设脸型item 回调
                    if (info != null)
                    {
                        if (info instanceof ShapeExAdapter.ShapeExItemInfo)
                        {
                            if (info.m_uri == SuperShapeData.ID_NON_SHAPE)
                            {
                                //无选项，关闭seek
                                showSeekBar(false);
                                showTopBar(true);
                            }

                            // 脸型数据回调
                            if (mCallback != null && mCallback instanceof IShapePageCallback)
                            {
                                ShapeExAdapter.ShapeExItemInfo itemInfo = (ShapeExAdapter.ShapeExItemInfo) info;
                                ((IShapePageCallback) mCallback).onShapeItemClick(itemInfo, itemInfo.m_data.getData());
                            }
                        }
                    }
                }

                @Override
                public void OnItemDown(AbsAdapter.ItemInfo info, int index)
                {

                }

                @Override
                public void OnItemUp(AbsAdapter.ItemInfo info, int index)
                {

                }
            };
        }

        if (mAdapterScrollCB == null)
        {
            mAdapterScrollCB = new ShapeExAdapter.OnAnimationScrolling()
            {
                @Override
                public void onAnimationScrolling(boolean scrolling)
                {
                    if (mRecyclerView != null)
                    {
                        mRecyclerView.setUiEnable(!scrolling);
                    }
                }

                @Override
                public void onSubRecyclerViewState(boolean open, boolean showSeekBar)
                {
                    if (open && showSeekBar)
                    {
                        showSeekBar(true);
                        showTopBar(false);
                    }
                    else if (!open && !showSeekBar)
                    {
                        showSeekBar(false);
                        showTopBar(true);
                    }

                    if (mCallback != null && mCallback instanceof IShapePageCallback)
                    {
                        if (mAdapter != null)
                        {
                            ((IShapePageCallback) mCallback).onSubLayoutOpen(open, showSeekBar,
                                    mAdapter.GetSelectIndex(), mAdapter.getCurrentItemInfo());
                        }
                    }
                }
            };
        }

        if (mBeautySeekCB == null)
        {
            mBeautySeekCB = new BeautySeekBar.OnSeekBarChangeListener()
            {
                @Override
                public void onProgressChanged(BeautySeekBar seekBar, int progress)
                {
                    updateColorSeekBarProgress(false, seekBar, progress);
                }

                @Override
                public void onStartTrackingTouch(BeautySeekBar seekBar)
                {
                    updateColorSeekBarProgress(false, seekBar, seekBar.getProgress());
                }

                @Override
                public void onStopTrackingTouch(BeautySeekBar seekBar)
                {
                    updateColorSeekBarProgress(true, seekBar, seekBar.getProgress());
                }

                final void updateColorSeekBarProgress(boolean stopTouch, BeautySeekBar seekBar, int progress)
                {
                    if (mAdapter != null)
                    {
                        ShapeExAdapter.ShapeExItemInfo currentItemInfo = mAdapter.updateCurrentItemInfo(progress);
                        if (mCallback != null)
                        {
                            if (mCallback instanceof IShapePageCallback)
                            {
                                ((IShapePageCallback) mCallback).onShapeUpdate(mAdapter.mCurrentSubSel, currentItemInfo, currentItemInfo.m_data.data);
                            }
                            mCallback.onSeekBarSlide(seekBar, progress, stopTouch);
                        }
                    }

                }

            };
        }
        if (mRiseSeekCB == null)
        {
            mRiseSeekCB = new RiseSeekBar.OnSeekBarChangeListener()
            {
                @Override
                public void onProgressChanged(RiseSeekBar seekBar, int progress)
                {
                    updateRiseSeekBarProgress(false, seekBar, progress);
                }

                @Override
                public void onStartTrackingTouch(RiseSeekBar seekBar)
                {
                    updateRiseSeekBarProgress(false, seekBar, seekBar.getProgress());
                }

                @Override
                public void onStopTrackingTouch(RiseSeekBar seekBar)
                {
                    updateRiseSeekBarProgress(true, seekBar, seekBar.getProgress());
                }

                final void updateRiseSeekBarProgress(boolean stopTouch, RiseSeekBar seekBar, int progress)
                {
                    if (mAdapter != null)
                    {
                        int realProgress = BeautyShapeDataUtils.GetBidirectionalRealSize(progress);
                        ShapeExAdapter.ShapeExItemInfo currentItemInfo = mAdapter.updateCurrentItemInfo(realProgress);
                        if (mCallback != null)
                        {
                            if (mCallback instanceof IShapePageCallback)
                            {
                                ((IShapePageCallback) mCallback).onShapeUpdate(mAdapter.mCurrentSubSel, currentItemInfo, currentItemInfo.m_data.data);
                            }
                            mCallback.onSeekBarSlide(seekBar, progress, stopTouch);
                        }
                    }
                }
            };
        }
    }

}
