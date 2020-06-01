package cn.poco.camera2;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import java.util.ArrayList;

import cn.poco.beautify.RecomDisplayMgr;
import cn.poco.camera3.UnLockUIListener;
import cn.poco.camera3.util.CameraPercentUtil;
import cn.poco.filter4.recycle.FilterAdapter;
import cn.poco.filter4.recycle.FilterBaseView;
import cn.poco.filterPendant.MyStatusButton;
import cn.poco.makeup.MySeekBar;
import cn.poco.recycleview.AbsAdapter;
import cn.poco.recycleview.BaseExAdapter;
import cn.poco.resource.BaseRes;
import cn.poco.resource.FilterGroupRes;
import cn.poco.resource.FilterRes;
import cn.poco.resource.FilterResMgr2;
import cn.poco.resource.RecommendRes;
import cn.poco.resource.ResType;
import cn.poco.resource.ResourceUtils;
import cn.poco.system.TagMgr;
import cn.poco.system.Tags;
import cn.poco.tianutils.CommonUtils;
import cn.poco.utils.Utils;
import my.beautyCamera.R;

/**
 * @author lmx
 *         Created by lmx on 2017/6/14.
 */

public class CameraFilterRecyclerView extends FrameLayout
{
    //base
    protected Context mContext;

    //callback
    protected OnItemClick mCb;
    protected UnLockUIListener mUnLockUIListener;

    //ui
    protected MyStatusButton mCenterBtn;
    protected FrameLayout mPopFrameView;
    protected FrameLayout mMaskFrameView;
    protected RecomDisplayMgr mRecomDisplayMgr;
    protected boolean mShowRecomView = false;
    protected boolean mUiEnable = true;


    //recycler view
    protected CameraFilterConfig mConfig;
    protected FilterBaseView mFilterFr;
    public FilterAdapter mFilterAdapter;

    protected boolean isUpdateVersion = false;

    protected boolean isShowFilterMsgToast = true;
    protected boolean isShowFilterMsgToastDef = true;

    public CameraFilterRecyclerView(@NonNull Context context)
    {
        super(context);
        mContext = context;
        InitUI(context);

        int vc = TagMgr.GetTagIntValue(context, Tags.CAMERA_FILTER_VIEW_VERSION);
        int appvc = CommonUtils.GetAppVerCode(context);
        TagMgr.SetTagValue(context, Tags.CAMERA_FILTER_VIEW_VERSION, String.valueOf(appvc));
        TagMgr.Save(context);
        isUpdateVersion = appvc > vc;
    }


    public void InitUI(Context context)
    {
        //mask背景遮罩
        mMaskFrameView = new FrameLayout(context);
        LayoutParams fp = new LayoutParams(LayoutParams.MATCH_PARENT, CameraPercentUtil.HeightPxToPercent(232));
        fp.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        addView(mMaskFrameView, fp);

        FrameLayout topLayout = new FrameLayout(context);
        topLayout.setBackgroundColor(0xffffffff);
        fp = new LayoutParams(LayoutParams.MATCH_PARENT, CameraPercentUtil.HeightPxToPercent(88));
        fp.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        fp.bottomMargin = CameraPercentUtil.HeightPxToPercent(232);
        topLayout.setClickable(true);
        this.addView(topLayout, fp);
        {
            mCenterBtn = new MyStatusButton(getContext());
            mCenterBtn.setData(R.drawable.filterbeautify_color_icon, getResources().getString(R.string.filterpage_filter));
            mCenterBtn.setBtnStatus(true, false);
            mCenterBtn.setOnClickListener(mOnClickListener);
            fp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
            fp.gravity = Gravity.CENTER;
            mCenterBtn.setLayoutParams(fp);
            topLayout.addView(mCenterBtn);
        }

        mConfig = new CameraFilterConfig();
        mFilterAdapter = new FilterAdapter(mConfig);
        mConfig.isCamera = true;
        //适配虚拟导航栏
        mConfig.def_parent_top_padding = (CameraPercentUtil.HeightPxToPercent(232) - mConfig.def_item_h) / 2;
        mConfig.def_parent_bottom_padding = mConfig.def_parent_top_padding;
        mFilterAdapter.setOnItemClickListener(mOnItemClickListener);

        //高度固定232px
        mFilterFr = new FilterBaseView(context, mFilterAdapter);
        fp = new LayoutParams(LayoutParams.MATCH_PARENT, CameraPercentUtil.HeightPxToPercent(232));
        fp.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
        mFilterFr.setBackgroundColor(Color.TRANSPARENT);
        addView(mFilterFr, fp);
    }

    public void InitData(ArrayList<FilterAdapter.ItemInfo> list)
    {
        if (mFilterAdapter != null && list != null)
        {
            mFilterAdapter.SetData(list);
            mFilterAdapter.notifyDataSetChanged();
        }
    }

    public void setRecyclerViewCanTouch(boolean uiEnable)
    {
        this.mUiEnable = uiEnable;
        if (mFilterFr != null)
        {
            mFilterFr.setUiEnable(uiEnable);
        }
    }

    /**
     * 滚动到指定滤镜组
     *
     * @param groupUri 滤镜组主题id
     */
    public void scrollToGroupByUri(int groupUri)
    {
        boolean isScrollTO;
        if (isUpdateVersion) {
            isScrollTO = true;
        } else {
            //NOTE 内置滤镜“推荐”，第一次打开拍照的滤镜列表时，默认展开“推荐”分类，直至用户点开其他分类。
            isScrollTO = groupUri != 1763;
        }
        if (isScrollTO && mFilterAdapter != null) {
            mFilterAdapter.scrollToGroupByUri(groupUri);
        }
    }

    /**
     * 设置是否显示toast
     * @param isShowFilterMsgToast
     */
    public void showFilterMsgToast(boolean isShowFilterMsgToast) {
        this.isShowFilterMsgToast = isShowFilterMsgToast;
    }

    /**
     * 设置默认是否显示toast
     * @param showFilterMsgToastDef
     */
    public void showFilterMsgToastDef(boolean showFilterMsgToastDef)
    {
        isShowFilterMsgToastDef = showFilterMsgToastDef;
        isShowFilterMsgToast = showFilterMsgToastDef;
    }

    //recycler view item click callback
    protected FilterAdapter.OnItemClickListener mOnItemClickListener = new FilterAdapter.OnItemClickListener()
    {
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
                    mFilterAdapter.setOpenIndex(-1);
                    onItemClick(filterRes);
                    break;
                }

                case FilterAdapter.DownloadItemInfo.DOWNLOAD_ITEM_URI:
                {
                    if (mCb != null)
                    {
                        mCb.onItemDownload();
                    }
                    break;
                }
                case FilterAdapter.RecommendItemInfo.RECOMMEND_ITEM_URI:
                {
                    openRecommendView((ArrayList<RecommendRes>) ((FilterAdapter.RecommendItemInfo) info).m_ex, ResType.FILTER.GetValue());
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

                        //阿玛尼商业201708
                        int m_uri = info.m_uris[subIndex];
                        if (m_uri == 247 || m_uri == 248 || m_uri == 249 || m_uri == 250)
                        {
                            String trackStr = null;
                            if (m_uri == 247)
                            {
                                trackStr = "http://cav.adnonstop.com/cav/fe0a01a3d9/0065503130/?url=https://a-m-s-ios.poco.cn/images/blank.gif";
                            }
                            else if (m_uri == 248)
                            {
                                trackStr = "http://cav.adnonstop.com/cav/fe0a01a3d9/0065503131/?url=https://a-m-s-ios.poco.cn/images/blank.gif";
                            }
                            else if (m_uri == 249)
                            {
                                trackStr = "http://cav.adnonstop.com/cav/fe0a01a3d9/0065503132/?url=https://a-m-s-ios.poco.cn/images/blank.gif";
                            }
                            else if (m_uri == 250)
                            {
                                trackStr = "http://cav.adnonstop.com/cav/fe0a01a3d9/0065503133/?url=https://a-m-s-ios.poco.cn/images/blank.gif";
                            }

                            if (!TextUtils.isEmpty(trackStr))
                            {
                                Utils.UrlTrigger(getContext(), trackStr);
                            }
                        }
                    }
                    break;
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

    protected void onItemClick(FilterRes filterRes)
    {
        if (mCb != null) {
            boolean tmpShow = isShowFilterMsgToast;
            showFilterMsgToast(isShowFilterMsgToastDef);
            mCb.onItemClick(filterRes, tmpShow);
        }
    }

    protected View.OnClickListener mOnClickListener = new OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            if (v == mCenterBtn)
            {
                if (mCb != null)
                {
                    mCb.foldItemList(true);
                }
            }
        }
    };

    protected RecomDisplayMgr.ExCallback mRecomCallback = new RecomDisplayMgr.ExCallback()
    {
        @Override
        public void UnlockSuccess(BaseRes res)
        {
        }

        @Override
        public void OnCloseBtn()
        {
            mShowRecomView = false;
        }

        @Override
        public void OnBtn(int state)
        {
        }

        @Override
        public void OnClose()
        {
            mShowRecomView = false;
            if (mUnLockUIListener != null)
            {
                mUnLockUIListener.closeUnLockView();
            }
        }

        @Override
        public void OnLogin()
        {
            if (mUnLockUIListener != null)
            {
                mUnLockUIListener.onUserLogin();
            }
        }

        @Override
        public void onWXCancel()
        {
            if (mUnLockUIListener != null)
            {
                mUnLockUIListener.closeUnLockView();
            }
        }
    };

    public boolean closeRecommendView()
    {
        if (mShowRecomView)
        {
            if (mRecomDisplayMgr != null && mRecomDisplayMgr.IsShow())
            {
                mRecomDisplayMgr.OnCancel(true);
                mRecomDisplayMgr = null;
            }
            mShowRecomView = false;
            return true;
        }
        return false;
    }


    public boolean isShowRecomView()
    {
        return mShowRecomView;
    }

    public void updateCredit()
    {
        if (mRecomDisplayMgr != null)
        {
            mRecomDisplayMgr.UpdateCredit();
        }
    }

    /**
     * 推荐位view
     *
     * @param ress
     * @param type
     */
    public void openRecommendView(ArrayList<RecommendRes> ress, int type)
    {
        //推荐位
        RecommendRes recommendRes = null;
        if (ress != null && ress.size() > 0)
        {
            recommendRes = ress.get(0);
        }

        if (mPopFrameView != null && mRecomDisplayMgr == null)
        {
            mPopFrameView.setVisibility(VISIBLE);

            mRecomDisplayMgr = new RecomDisplayMgr(getContext(), mRecomCallback);
            mRecomDisplayMgr.Create(mPopFrameView);
        }

        if (recommendRes != null && mRecomDisplayMgr != null)
        {
            mShowRecomView = true;
            mRecomDisplayMgr.SetBk(0xcc000000);
            mRecomDisplayMgr.SetDatas(recommendRes, type);
            mRecomDisplayMgr.Show();

            if (mUnLockUIListener != null)
            {
                mUnLockUIListener.openUnLockView();
            }
        }
    }

    public void setItemClickCallback(OnItemClick callback)
    {
        this.mCb = callback;
    }

    public void setUnLockUIListener(UnLockUIListener listener)
    {
        this.mUnLockUIListener = listener;
    }

    public void SetPopFrameView(FrameLayout popframe)
    {
        this.mPopFrameView = popframe;
    }

    public void setMaskFrameViewBGColor(@ColorInt int color)
    {
        if (mMaskFrameView != null)
        {
            mMaskFrameView.setBackgroundColor(color);
        }
    }

    public void SetSelUri(int uri, boolean isCallbackClick)
    {
        if (mFilterAdapter != null)
        {
            mFilterAdapter.SetSelectByUri(uri, true, true, isCallbackClick);
        }
    }

    public void ClearAll()
    {
        if (mRecomDisplayMgr != null)
        {
            mRecomDisplayMgr.ClearAllaa();
            mRecomDisplayMgr = null;
        }

        if (mPopFrameView != null)
        {
            mPopFrameView.removeAllViews();
            mPopFrameView = null;
        }


        if (mCenterBtn != null)
        {
            mCenterBtn.setOnClickListener(null);
        }

        if (mFilterAdapter != null)
        {
            mFilterAdapter.setOnItemClickListener(null);
        }

        mRecomCallback = null;
        mOnItemClickListener = null;
        mOnClickListener = null;
    }

    public interface OnItemClick
    {
        void onItemClick(FilterRes filterRes, boolean isShowFilterMsgToast);

        void onItemDownload();

        void foldItemList(boolean isDown);
    }
}
