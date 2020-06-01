package cn.poco.camera3.beauty.page;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Size;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.HashMap;

import cn.poco.camera2.CameraFilterConfig;
import cn.poco.camera2.CameraFilterListIndexs;
import cn.poco.camera3.beauty.TabUIConfig;
import cn.poco.camera3.beauty.callback.IFilterPageCallback;
import cn.poco.camera3.util.CameraPercentUtil;
import cn.poco.filter4.recycle.FilterAdapter;
import cn.poco.makeup.MySeekBar;
import cn.poco.recycleview.AbsAdapter;
import cn.poco.recycleview.BaseExAdapter;
import cn.poco.recycleview.ListItemDecoration;
import cn.poco.resource.FilterGroupRes;
import cn.poco.resource.FilterRes;
import cn.poco.resource.FilterResMgr2;
import cn.poco.resource.RecommendRes;
import cn.poco.resource.ResourceUtils;
import cn.poco.tianutils.ShareData;

/**
 * @author lmx
 *         Created by lmx on 2018-01-16.
 */

public class FilterFramePager extends BaseFramePager
{
    public static final String FILTER_FRAME_PAGER_TAG = "FilterFramePager";

    public static final String BUNDLE_KEY_DATA_FILTER_LIST = "bundle_key_data_filter_list";

    private RecyclerView mRecyclerView;
    private FilterAdapter mAdapter;
    private ArrayList<FilterAdapter.ItemInfo> mList;
    private boolean mUiEnable = true;
    protected FilterAdapter.OnItemClickListener mOnItemClickListener;

    protected CameraFilterListIndexs mCameraFilterListIndexs;
    private int mFilterResPosition = -1;
    private int mFilterResUri = -1;
    private boolean isRepeatClickFilter;
    private boolean isUsedStickerFilter;
    private boolean isShowFilterMsgToast = true;
    private boolean isShowFilterMsgToastDef = true;

    public FilterFramePager(@NonNull Context context, TabUIConfig mTabUIConfig)
    {
        super(context, mTabUIConfig);
    }

    @Override
    public void initData()
    {
        initCallback();
    }

    @Override
    public void setData(HashMap<String, Object> data)
    {
        super.setData(data);
        if (data != null)
        {
            if (data.containsKey(BUNDLE_KEY_DATA_FILTER_LIST))
            {
                mList = (ArrayList<FilterAdapter.ItemInfo>) data.get(BUNDLE_KEY_DATA_FILTER_LIST);
            }
        }

        if (mAdapter != null && mList != null)
        {
            mAdapter.SetData(mList);
            mAdapter.notifyDataSetChanged();
        }
        initCameraFilterListIndexs(mList);
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

        if (mCameraFilterListIndexs != null)
        {
            mCameraFilterListIndexs.cleaAll();
        }
        mCameraFilterListIndexs = null;
        mOnItemClickListener = null;
        mList = null;
        mRecyclerView = null;
    }

    @Override
    public void notifyDataChanged()
    {
        if (mCallback != null)
        {
            mList = (ArrayList<FilterAdapter.ItemInfo>) mCallback.getFramePagerData(mPosition, getFramePagerTAG(), true);
            if (mAdapter != null && mList != null)
            {
                mAdapter.SetData(mList);
                mAdapter.notifyDataSetChanged();
            }
            initCameraFilterListIndexs(mList);
        }
    }

    private void initCameraFilterListIndexs(ArrayList<FilterAdapter.ItemInfo> list)
    {
        if (list != null)
        {
            if (mCameraFilterListIndexs == null)
            {
                mCameraFilterListIndexs = new CameraFilterListIndexs();
            }
            else
            {
                mCameraFilterListIndexs.reset();
            }

            mCameraFilterListIndexs.sortIndex(list);
        }
    }

    public void setFilterUri(int uri, boolean isCallback)
    {
        if (mAdapter != null)
        {
            mAdapter.SetSelectByUri(uri, true, true, isCallback);
        }
    }


    /**
     * 取消选择列表滤镜选择
     */
    public void cancelFilterUri()
    {
        isRepeatClickFilter = false;
        mFilterResUri = -1;
        mFilterResPosition = -1;
        if (mAdapter != null)
        {
            mAdapter.CancelSelect();
            mAdapter.setOpenIndex(-1);
            mAdapter.scrollToCenterByIndex(0);
        }
    }

    /**
     * 设置当前是否使用了贴纸自带的滤镜效果
     *
     * @param usedStickerFilter
     */
    public void setUsedStickerFilter(boolean usedStickerFilter)
    {
        isUsedStickerFilter = usedStickerFilter;
    }

    /**
     * 设置是否显示toast
     *
     * @param isShowFilterMsgToast
     */
    public void showFilterMsgToast(boolean isShowFilterMsgToast)
    {
        this.isShowFilterMsgToast = isShowFilterMsgToast;
    }


    /**
     * 镜头手势滑动滤镜
     *
     * @param next
     */
    public void setCameraFilterNext(boolean next)
    {
        int currentPosition = mFilterResPosition;
        if (next)
        {
            currentPosition++;
        }
        else
        {
            currentPosition--;
        }
        int size = getCameraFilterListIndexSize();
        if (currentPosition >= size)
        {
            currentPosition = 0;
        }
        else if (currentPosition < 0)
        {
            currentPosition = size - 1;
        }
        if (size != 0 && currentPosition < size && currentPosition >= 0)
        {
            setFilterIndex(currentPosition);
        }
    }

    private void setFilterIndex(int index)
    {
        if (mCameraFilterListIndexs != null)
        {
            int uri = mCameraFilterListIndexs.setFilterIndex(index);
            if (uri != -1)
            {
                setFilterUri(uri, true, false);
            }
        }
    }

    /**
     * @param uri
     * @param isCallbackClick
     * @param isRepeatCallbackClick 是否再次点击同一个滤镜id
     */
    public void setFilterUri(int uri, boolean isCallbackClick, boolean isRepeatCallbackClick)
    {
        this.isRepeatClickFilter = isRepeatCallbackClick;

        //原图滤镜
        if (uri == 0)
        {
            uri = FilterAdapter.OriginalItemInfo.ORIGINAL_ITEM_URI;
        }

        if (isRepeatCallbackClick)
        {
            isCallbackClick = true;
        }

        if (!isCallbackClick)
        {
            mFilterResUri = uri;
            mFilterResPosition = getPositionByFilterId(mFilterResUri);
            if (mFilterResPosition == -1) {
                mFilterResUri = uri = 0;//原图
                mFilterResPosition = getPositionByFilterId(mFilterResUri);
            }
        }

        setFilterUri(uri, isCallbackClick);
    }

    private int getCameraFilterListIndexSize()
    {
        return mCameraFilterListIndexs != null ? mCameraFilterListIndexs.getSize() : 0;
    }

    private int getPositionByFilterId(int id)
    {
        return mCameraFilterListIndexs != null ? mCameraFilterListIndexs.getPositionByFilterId(id) : -1;
    }

    public void updateAndResetDownload()
    {
        mFilterResPosition = getPositionByFilterId(mFilterResUri);
        setFilterIndex(mFilterResPosition);
    }

    @Size(2)
    public int[] GetSubIndexByUri(int filter_id)
    {
        if (mAdapter != null)
        {
            return mAdapter.GetSubIndexByUri(filter_id);
        }
        return new int[]{-1, -1};
    }

    @Override
    public void onCreateContainerView(@NonNull FrameLayout parentLayout)
    {
        FrameLayout layout = new FrameLayout(getContext())
        {
            @Override
            public boolean dispatchTouchEvent(MotionEvent ev)
            {
                return mUiEnable && super.dispatchTouchEvent(ev);
            }
        };
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, CameraPercentUtil.HeightPxToPercent(272));
        params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
        params.topMargin = CameraPercentUtil.HeightPxToPercent(88);
        parentLayout.addView(layout, params);
        {

            FilterFragmentConfig mConfig = new FilterFragmentConfig();
            mAdapter = new FilterAdapter(mConfig);
            mAdapter.setOnItemClickListener(mOnItemClickListener);
            mConfig.def_parent_top_padding = (CameraPercentUtil.HeightPxToPercent(272) - mConfig.def_item_h) / 2;
            mConfig.def_parent_bottom_padding = mConfig.def_parent_top_padding;

            //内边距
            int topPadding = mAdapter.m_config.def_parent_bottom_padding;
            int bottomPadding = mAdapter.m_config.def_parent_bottom_padding;

            LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
            mRecyclerView = new RecyclerView(getContext());
            mRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
            mRecyclerView.setHorizontalScrollBarEnabled(false);
            mRecyclerView.setLayoutManager(mLayoutManager);
            mRecyclerView.addItemDecoration(new ListItemDecoration(mAdapter.m_config.def_item_l, ListItemDecoration.HORIZONTAL));
            mRecyclerView.setPadding(mAdapter.m_config.def_parent_left_padding, topPadding, mAdapter.m_config.def_parent_right_padding, bottomPadding);
            mRecyclerView.setClipToPadding(false);
            mRecyclerView.setAdapter(mAdapter);

            mAdapter.setRecyclerView(mRecyclerView);
            params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
            params.gravity = Gravity.CENTER_HORIZONTAL;
            layout.addView(mRecyclerView, params);
        }
    }

    public void setUiEnable(boolean mUiEnable)
    {
        this.mUiEnable = mUiEnable;
    }


    @Override
    public String getFramePagerTAG()
    {
        return FILTER_FRAME_PAGER_TAG;
    }

    private void initCallback()
    {
        mOnItemClickListener = new FilterAdapter.OnItemClickListener()
        {

            @Override
            public void OnItemClick(BaseExAdapter.ItemInfo info, int parentIndex, int subIndex)
            {
                if (info == null) return;

                switch (info.m_uris[0])
                {
                    case FilterAdapter.OriginalItemInfo.ORIGINAL_ITEM_URI:
                    {
                        FilterRes filterRes = null;
                        if (info instanceof FilterAdapter.OriginalItemInfo
                                && ((FilterAdapter.OriginalItemInfo) info).m_ex != null
                                && (((FilterAdapter.OriginalItemInfo) info).m_ex) instanceof FilterRes)
                        {
                            filterRes = (FilterRes) ((FilterAdapter.OriginalItemInfo) info).m_ex;
                        }
                        else
                        {
                            filterRes = ResourceUtils.GetItem(FilterResMgr2.getInstance().sync_GetLocalRes(getContext(), null), 0);
                        }
                        if (mAdapter != null)
                        {
                            mAdapter.setOpenIndex(-1);
                        }
                        onItemClick(filterRes);
                        break;
                    }

                    case FilterAdapter.DownloadItemInfo.DOWNLOAD_ITEM_URI:
                    {
                        if (mCallback != null && mCallback instanceof IFilterPageCallback)
                        {
                            ((IFilterPageCallback) mCallback).onFilterItemDownload();
                        }
                        break;
                    }
                    case FilterAdapter.RecommendItemInfo.RECOMMEND_ITEM_URI:
                    {
                        if (mCallback != null && mCallback instanceof IFilterPageCallback)
                        {
                            ((IFilterPageCallback) mCallback).onFilterItemRecommend((ArrayList<RecommendRes>) ((FilterAdapter.RecommendItemInfo) info).m_ex);
                        }
                        break;
                    }
                    default:
                        if (subIndex > 0 && info.m_uris.length > subIndex)
                        {
                            FilterRes filterRes = null;
                            if (info instanceof FilterAdapter.ItemInfo
                                    && ((FilterAdapter.ItemInfo) info).m_ex != null
                                    && ((FilterAdapter.ItemInfo) info).m_ex instanceof FilterGroupRes
                                    && ((FilterGroupRes) ((FilterAdapter.ItemInfo) info).m_ex).m_group != null)
                            {
                                filterRes = ((FilterGroupRes) ((FilterAdapter.ItemInfo) info).m_ex).m_group.get(subIndex - 1);
                            }
                            if (filterRes == null)
                            {
                                filterRes = FilterResMgr2.getInstance().GetRes(info.m_uris[subIndex]);
                            }
                            onItemClick(filterRes);
                        }
                        break;
                }
            }

            void onItemClick(FilterRes filterRes)
            {
                boolean tmpShowToast = isShowFilterMsgToast;
                showFilterMsgToast(isShowFilterMsgToastDef);

                if (filterRes != null
                        && (mFilterResUri != filterRes.m_id || isUsedStickerFilter || isRepeatClickFilter))
                {
                    isRepeatClickFilter = false;
                    isUsedStickerFilter = false;
                    mFilterResUri = filterRes.m_id;
                    mFilterResPosition = getPositionByFilterId(mFilterResUri);
                }

                if (mCallback != null && mCallback instanceof IFilterPageCallback)
                {
                    ((IFilterPageCallback) mCallback).onFilterItemClick(filterRes, tmpShowToast);
                }
            }

            @Override
            public void OnItemDown(BaseExAdapter.ItemInfo info, int parentIndex, int subIndex)
            {

            }

            @Override
            public void OnItemUp(BaseExAdapter.ItemInfo info, int parentIndex, int subIndex)
            {

            }

            @Override
            public void onProgressChanged(MySeekBar seekBar, int progress)
            {

            }

            @Override
            public void onStartTrackingTouch(MySeekBar seekBar)
            {

            }

            @Override
            public void onStopTrackingTouch(MySeekBar seekBar)
            {

            }

            @Override
            public void onSeekBarStartShow(MySeekBar seekBar)
            {

            }

            @Override
            public void onFinishLayoutAlphaFr(MySeekBar seekBar)
            {

            }


            @Override
            public void OnItemDown(AbsAdapter.ItemInfo info, int index)
            {

            }

            @Override
            public void OnItemUp(AbsAdapter.ItemInfo info, int index)
            {

            }

            @Override
            public void OnItemClick(AbsAdapter.ItemInfo info, int index)
            {

            }
        };
    }

    public static class FilterFragmentConfig extends CameraFilterConfig
    {
        @Override
        public void InitData()
        {
            isCamera = true;
            def_original_bk_color = 0xffebebeb;
            def_item_w = ShareData.PxToDpi_xhdpi(146);
            def_item_h = ShareData.PxToDpi_xhdpi(187);
            def_sub_w = ShareData.PxToDpi_xhdpi(127);
            def_sub_h = ShareData.PxToDpi_xhdpi(164);                                            //37
            def_item_l = ShareData.PxToDpi_xhdpi(17);
            def_sub_l = ShareData.PxToDpi_xhdpi(17);

            def_parent_center_x = ShareData.m_screenRealWidth / 2;                                  //recycleView的中心点     用户setSelect直接跳转位置确定
            def_parent_right_padding = ShareData.PxToDpi_xhdpi(22);
            def_parent_left_padding = ShareData.PxToDpi_xhdpi(22);

            def_parent_top_padding = ShareData.PxToDpi_xhdpi(22);
            def_parent_bottom_padding = def_parent_top_padding;

            def_head_w = ShareData.PxToDpi_xhdpi(109) - def_parent_left_padding - def_item_l;
            def_alphafr_leftMargin = ShareData.PxToDpi_xhdpi(20);
        }

        @Override
        public void ClearAll()
        {
            super.ClearAll();
        }
    }
}
