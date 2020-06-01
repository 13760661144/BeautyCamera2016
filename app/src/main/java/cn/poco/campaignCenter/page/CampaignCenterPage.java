package cn.poco.campaignCenter.page;

import android.content.Context;
import android.graphics.Color;
import android.os.Parcelable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseIntArray;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.poco.advanced.ImageUtils;
import cn.poco.beautifyEyes.util.StatisticHelper;
import cn.poco.campaignCenter.api.CampaignApi;
import cn.poco.campaignCenter.manager.ConnectionsManager;
import cn.poco.campaignCenter.manager.FileManager;
import cn.poco.campaignCenter.model.CampaignInfo;
import cn.poco.campaignCenter.site.centerSite.CampaignCenterSite;
import static cn.poco.campaignCenter.site.centerSite.CampaignCenterSite.ITEM_OPEN_INDEX;
import cn.poco.campaignCenter.ui.adapter.CampaignInfoAdapter;
import cn.poco.campaignCenter.ui.cells.AutoDisplayCell;
import cn.poco.campaignCenter.ui.cells.CampaignBgView;
import cn.poco.campaignCenter.ui.cells.CampaignCell;
import cn.poco.campaignCenter.utils.ImageLoaderUtil;
import cn.poco.campaignCenter.widget.Actionbar.ActionBar;
import cn.poco.campaignCenter.widget.CampaignDataState;
import cn.poco.campaignCenter.widget.CampaignListMonitor;
import cn.poco.campaignCenter.widget.CustomViewToast;
import cn.poco.campaignCenter.widget.MultiStateCampaignView;
import cn.poco.campaignCenter.widget.PullDownRefreshRecyclerView.DividerItemDecoration;
import cn.poco.campaignCenter.widget.PullDownRefreshRecyclerView.IRecyclerView;
import cn.poco.campaignCenter.widget.PullDownRefreshRecyclerView.LoadMoreFooterView;
import cn.poco.campaignCenter.widget.component.RefreshHeaderView;
import cn.poco.campaignCenter.widget.view.EmptyHolderView;
import cn.poco.campaignCenter.widget.view.PoorNetWorkNotification;
import cn.poco.framework.BaseSite;
import cn.poco.framework.IPage;
import cn.poco.framework.SiteID;
import cn.poco.statistics.MyBeautyStat;
import cn.poco.statistics.TongJiUtils;
import cn.poco.system.AppInterface;
import cn.poco.system.SysConfig;
import cn.poco.tianutils.ShareData;
import cn.poco.utils.OnAnimationClickListener;
import cn.poco.utils.Utils;
import my.beautyCamera.R;

/**
 * Created by admin on 2016/10/14.
 */

public class CampaignCenterPage extends IPage implements IRecyclerView.OnRefreshListener, IRecyclerView.OnLoadMoreListener{
    private static final String LOG_TAG = "CampaignCenterPage";

    private static final int INITIAL_PAGE = 1;

    private Context mContext;

    private LinearLayout mViewContainer;
    private MultiStateCampaignView mDataLayer;
    private ActionBar mActonBar;

    private IRecyclerView iRecyclerView;
    private LoadMoreFooterView loadMoreFooterView;
    private CampaignInfoAdapter mCampaignInfoAdapter;

    private Map<Integer, List<CampaignInfo>> mCampaignInfoMap = new HashMap();
    private List<CampaignInfo> mAutoSlidePageDataList = new ArrayList<>();
    private List<CampaignInfo> mDisplayPageDataList = new ArrayList<>();
    private List<CampaignInfo> mCampaignItemDataList = new ArrayList<>();

    private AppInterface mAppInterface;

    private int mCurrentPageIndex = INITIAL_PAGE;

    private boolean mStartEffect;
    private boolean mShouldLayout;

    private CampaignCenterSite mSite;

    private int mActionBarHeight;

    private SparseIntArray mLocationDictionary = new SparseIntArray();
    private CustomViewToast mCustomViewToast;
    private boolean mFullyDisplay;
    private boolean mHasLayout = false;


    public CampaignCenterPage(Context context, BaseSite site) {
        super(context, site);
        mSite = (CampaignCenterSite) site;
        mContext = context;
        mAppInterface = AppInterface.GetInstance(context);
        // 初始化UI界面
        initView(context);

    }

    private void onRestoreToCorrectPage() {
        mSite.onRestoreState(mContext);
    }

    private void onRestoreListPosition() {
        if (mSite.m_myParams.containsKey(mSite.SCROLL_POSITION)) {
            Object object = mSite.m_myParams.get(mSite.SCROLL_POSITION);
            if (object instanceof Parcelable) {
                iRecyclerView.getLayoutManager().onRestoreInstanceState((Parcelable) object);
            }
            mSite.m_myParams.remove(mSite.SCROLL_POSITION);
        }
    }


    private void initView(Context context) {
        mViewContainer = new LinearLayout(context);
        mViewContainer.setOrientation(LinearLayout.VERTICAL);
        LayoutParams paramsContainer = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mViewContainer.setLayoutParams(paramsContainer);
        this.addView(mViewContainer);

        // actionbar
        mActonBar = new ActionBar(context);
        mActonBar.setUpActionbarTitle(R.string.campaigncenter_title, 0xff333333, 18);
        mActonBar.setUpLeftImageBtn(R.drawable.framework_back_btn);
        mActonBar.setBackgroundColor(0xf5ffffff);
        mActionBarHeight = ShareData.PxToDpi_xhdpi(90);
        mActonBar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                iRecyclerView.stopScroll();
                iRecyclerView.smoothScrollToPosition(0);
            }
        });

        // 96%的白色透明
        LinearLayout.LayoutParams paramsActionbar = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mActionBarHeight);
        mActonBar.setLayoutParams(paramsActionbar);
        mViewContainer.addView(mActonBar);
        mActonBar.getLeftImageBtn().setOnTouchListener(onAnimationClickListener);

        mDataLayer = new MultiStateCampaignView(context);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mDataLayer.setLayoutParams(params);
        mViewContainer.addView(mDataLayer);
        {
            LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, Gravity.LEFT | Gravity.TOP);
            iRecyclerView = new IRecyclerView(context);
            final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context) {
                @Override
                public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
                    super.onLayoutChildren(recycler, state);
                    for (int i = 0; i < this.getChildCount(); i++) {
                        View currentView = this.getChildAt(i);
                        if (currentView instanceof CampaignCell) {
                            CampaignCell campaignCell = (CampaignCell) currentView;
                            int viewIndex = this.getPosition(campaignCell);

//                                记录每个View最开始的顶部位置
                            if ((mLocationDictionary.indexOfKey(viewIndex) < 0)) {
                                mLocationDictionary.put(viewIndex, campaignCell.getTop());
                                mStartEffect = true;
                            }
                        }
                    }
                }

                @Override
                public void onLayoutCompleted(RecyclerView.State state) {
                    super.onLayoutCompleted(state);
                    if (!mHasLayout) {
                        onRestoreListPosition();
                        mHasLayout = true;
                    }
                }
            };

            iRecyclerView.setLayoutManager(linearLayoutManager);
            iRecyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL_LIST));
            iRecyclerView.setRefreshEnabled(true);
            iRecyclerView.setLoadMoreEnabled(true);
            iRecyclerView.setOnRefreshListener(this);
            iRecyclerView.setOnLoadMoreListener(this);


            iRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    if (!mShouldLayout) {
                        mShouldLayout = true;
                    }
                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    // 只有在整个RecyclerView布好局之后，才开始视觉上的变化
                    if (mStartEffect && mShouldLayout) {
                        for (int i = 0; i < recyclerView.getChildCount(); i++) {
                            View currentView = recyclerView.getChildAt(i);
                            if (currentView instanceof CampaignCell) {
                                CampaignCell campaignCell = (CampaignCell) currentView;
                                int viewIndex = recyclerView.getLayoutManager().getPosition(campaignCell);

                                // 修正linearLayoutManager没有完全回调，造成的某些view的位置数据缺失
                                if ((mLocationDictionary.indexOfKey(viewIndex) < 0)) {
                                    int lastVisibleItemPosition = ((LinearLayoutManager)(recyclerView.getLayoutManager())).findLastVisibleItemPosition();
                                    boolean isLastsOne = lastVisibleItemPosition == viewIndex;
                                    if (isLastsOne) {
                                        mLocationDictionary.put(viewIndex, campaignCell.getTop());
                                    }
                                }

                                int viewLocation = mLocationDictionary.get(viewIndex);

                                float rate = 0;
                                if (viewLocation == 0) {
                                    if (campaignCell.mBackground.mType == CampaignBgView.HEAD) {
                                        rate = 1;
                                    } else if (campaignCell.mBackground.mType == CampaignBgView.NORMAL){
                                        rate = 0;
                                    }
                                } else{
                                    rate = (viewLocation - (currentView.getTop())) / (viewLocation * 1.0f);
                                }

                                if (rate > 1) {
                                    rate = 1;
                                }

                                if (rate < 0) {
                                    rate = 0;
                                }

                                campaignCell.mBackground.mRate = rate;
                                campaignCell.mBackground.invalidate();
                            }
                        }
                    }
                }
            });

            RefreshHeaderView refreshHeaderView = new RefreshHeaderView(context);
            refreshHeaderView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(90)));
            iRecyclerView.setRefreshHeaderView(refreshHeaderView);

            loadMoreFooterView = new LoadMoreFooterView(context);
            float rate = (90 * 1.0f / 720);
            int viewHeight = (int) (ShareData.m_screenWidth * rate);

            loadMoreFooterView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, viewHeight));
            iRecyclerView.setLoadMoreFooterView(loadMoreFooterView);

            mCampaignInfoMap.put(0, mAutoSlidePageDataList);
            mCampaignInfoMap.put(1, mDisplayPageDataList);
            mCampaignInfoMap.put(2, mCampaignItemDataList);

            mCampaignInfoAdapter = new CampaignInfoAdapter(mContext, mCampaignInfoMap);
            iRecyclerView.setIAdapter(mCampaignInfoAdapter);

            mCampaignInfoAdapter.setOnItemClickListener(onClickListener);

            CampaignListMonitor.getInstance().notifyObservers(CampaignDataState.STATE_LOADING, 1, CampaignDataState.BEGIN_WITH_THEME_SKIN);

            //先从缓存的文件中获取数据
            getDataFromCacheFile();

            // 再从服务器获取数据
            getDataFromServer(false);
            iRecyclerView.setLayoutParams(params1);

            mDataLayer.addDataView(iRecyclerView);
            mDataLayer.setOnClickNetWorkError(onClickListener);

            addSkin();
            initAnimationClickListener();
        }
    }

    private void addSkin() {
        ImageUtils.AddSkin(mContext, mActonBar.getLeftImageBtn());
        View headerView = iRecyclerView.getRefreshHeaderView();
        if (headerView != null && headerView instanceof RefreshHeaderView) {
            RefreshHeaderView refreshHeaderView = (RefreshHeaderView) headerView;
            refreshHeaderView.setSkin();
        }
        mDataLayer.setThemeSkin(ImageUtils.GetSkinColor1(Color.WHITE), ImageUtils.GetSkinColor2(Color.WHITE),SysConfig.s_skinColorType);
    }

    private void initAnimationClickListener() {
        mActonBar.getLeftImageBtn().setOnTouchListener(onAnimationClickListener);
        mCampaignInfoAdapter.setOnAnimationClickListener(onAnimationClickListener);
    }


    @Override
    public void SetData(HashMap<String, Object> params) {
        onRestoreToCorrectPage();
    }

    @Override
    public void onBack() {
        reset();
        mSite.onBack(mContext);
    }

    @Override
    public void onStart() {
        super.onStart();
        TongJiUtils.onPageStart(getContext(), R.string.运营专区);
        MyBeautyStat.onPageStartByRes(R.string.社区_首页_主页面);
    }

    @Override
    public void onStop() {
        super.onStop();
        TongJiUtils.onPageEnd(getContext(), R.string.运营专区);
        MyBeautyStat.onPageEndByRes(R.string.社区_首页_主页面);
    }

    @Override
    public void onPause() {
        StatisticHelper.countPagePause(mContext, mContext.getString(R.string.运营专区));
    }

    @Override
    public void onResume() {
        StatisticHelper.countPageResume(mContext, mContext.getString(R.string.运营专区));
    }


    @Override
    public void onClose() {
        clearResource();
    }

    @Override
    public void onPageResult(int siteID, HashMap<String, Object> params) {
        if (siteID != SiteID.CAMPAIGN_WEBVIEW_PAGE) {
            mSite.onRestoreState(mContext);
        } else {
            if (mSite != null && mSite.m_myParams != null && mSite.m_myParams.containsKey(ITEM_OPEN_INDEX)) {
                mSite.m_myParams.remove(ITEM_OPEN_INDEX);
            }
        }
    }

    @Override
    public void onRefresh() {
        loadMoreFooterView.setStatus(LoadMoreFooterView.Status.GONE);
        getDataFromServer(true);
    }

    @Override
    public void onLoadMore() {
        if (loadMoreFooterView.canLoadMore() && mCampaignInfoAdapter.getItemCount() > 0) {
            if (iRecyclerView != null) {
                iRecyclerView.setRefreshing(false);
            }
            loadMoreFooterView.setStatus(LoadMoreFooterView.Status.LOADING);
            loadMore();
        }
    }

    private boolean mRefresh;
    private void getDataFromServer(final boolean refresh) {
        mRefresh = refresh;
        ConnectionsManager.getInstacne().getCampaignInfo("1,2,3", String.valueOf(INITIAL_PAGE), mAppInterface, mGetDataDelagete);
    }

    private void loadMore() {
        ConnectionsManager.getInstacne().getCampaignInfo("3", String.valueOf(++mCurrentPageIndex), mAppInterface, mLoadMoreDelagate);
    }

    private void getDataFromCacheFile() {
        FileManager.getInstacne().getDataFromCacheFile(mContext, FileManager.getInstacne().CACHE_FILE, mCampaignInfoMap, new FileManager.FileManagerDelegate() {
            @Override
            public void onSuccessFetch(Map<Integer, List<CampaignInfo>> result) {
                mCampaignInfoMap = result;
                if (mCampaignInfoAdapter != null) {
                    if (mCampaignInfoMap.size() > 0) {
                    CampaignListMonitor.getInstance().notifyObservers(CampaignDataState.STATE_FINISH_LOADED, 1, CampaignDataState.BEGIN_WITH_THEME_SKIN);
                    }
                    mCampaignInfoAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void failToFetch(String message) {

            }
        });
    }


    public void clearResource() {
        reset();
        ImageLoaderUtil.releaseMemory(mContext);
        CampaignListMonitor.getInstance().clear();
        iRecyclerView.setOnLoadMoreListener(null);
        iRecyclerView.setOnRefreshListener(null);
        mCampaignInfoAdapter.clear();
        mLoadMoreDelagate = null;
        mGetDataDelagete = null;
        onClickListener = null;
        onAnimationClickListener = null;
        FileManager.getInstacne().clear();
        ConnectionsManager.getInstacne().clear();
    }


    public int getActionbarHeight() {
        return mActionBarHeight;
    }


    public void startSerialActions(final boolean refresh) {
        // 从服务器获取数据
        iRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (refresh) {
                    iRecyclerView.setRefreshing(true);
                }
            }
        }, 0);
    }

    public void changeThemeSkin() {
        addSkin();
        mCampaignInfoAdapter.notifyDataSetChanged();
    }

    private void reset() {
        if (mCustomViewToast != null) {
            mCustomViewToast.clear();
        }

        ((LoadMoreFooterView)iRecyclerView.getLoadMoreFooterView()).setStatus(LoadMoreFooterView.Status.GONE);
        iRecyclerView.setRefreshing(false);
        RefreshHeaderView refreshHeader = (RefreshHeaderView) iRecyclerView.getRefreshHeaderView();
        refreshHeader.onReset();
        iRecyclerView.stopScroll();
        iRecyclerView.scrollToPosition(0);
    }

    public void guaranteeShowCorrectPosition() {
        if (iRecyclerView != null) {
            iRecyclerView.setRefreshing(false);
        }
    }

    private void showNotification() {
        if (mCustomViewToast == null) {
            mCustomViewToast = new CustomViewToast(mContext);
            PoorNetWorkNotification notification = new PoorNetWorkNotification(mContext);
            notification.setIndicationText(getResources().getString(R.string.poor_network));
            LayoutParams params = new LayoutParams(ShareData.PxToDpi_xhdpi(688), ShareData.PxToDpi_xhdpi(110));
            notification.setLayoutParams(params);
            mCustomViewToast.setCustomView(notification);
        }
        mCustomViewToast.show();
    }


    public void startBackgroundGradientColor(double progress) {
        int state = CampaignListMonitor.getInstance().mCurrentState;
        CampaignListMonitor.getInstance().notifyObservers(state, progress, CampaignDataState.BEGIN_WITH_THEME_SKIN);
        mFullyDisplay = progress == 1 ? true : false;
    }


    private ConnectionsManager.RequestDelegate mLoadMoreDelagate = new ConnectionsManager.RequestDelegate() {
        @Override
        public void run(Object response, ConnectionsManager.NetWorkError error) {
            if (error == null) {
                CampaignApi campaignApi = (CampaignApi) response;
                if (!(campaignApi.mCampaignListData.size() > 0)) {
                    --mCurrentPageIndex;
                    loadMoreFooterView.setStatus(LoadMoreFooterView.Status.THE_END);
                    return;
                }
                mCampaignItemDataList.addAll(campaignApi.mCampaignListData);
                if (mCampaignInfoAdapter != null) {
                    mCampaignInfoAdapter.notifyDataSetChanged();
                }
                loadMoreFooterView.setStatus(LoadMoreFooterView.Status.GONE);
            } else {
                --mCurrentPageIndex;
                loadMoreFooterView.setStatus(LoadMoreFooterView.Status.GONE);
                showNotification();
            }
        }
    } ;

    private ConnectionsManager.RequestDelegate mGetDataDelagete = new ConnectionsManager.RequestDelegate() {
        @Override
        public void run(Object response, ConnectionsManager.NetWorkError error) {
            if (mRefresh) {
                iRecyclerView.setRefreshing(false);
            }
            mCurrentPageIndex = INITIAL_PAGE;

            // 成功获取服务器数据
            if (error == null) {
                CampaignListMonitor.getInstance().notifyObservers(CampaignDataState.STATE_FINISH_LOADED, 1, CampaignDataState.BEGIN_WITH_OTHER);

                CampaignApi campaignApi = (CampaignApi) response;
                // 更新HashMap里面的数据
                if ((campaignApi.flags & CampaignApi.Has_Position1)!= 0) {
                    mAutoSlidePageDataList.clear();
                    mAutoSlidePageDataList.addAll(campaignApi.mAutoSlideListData);
                }

                if ((campaignApi.flags & CampaignApi.Has_Position2)!= 0) {
                    mDisplayPageDataList.clear();
                    mDisplayPageDataList.addAll(campaignApi.mDisplayListData);
                }

                if ((campaignApi.flags & CampaignApi.Has_Position3)!= 0) {
                    mCampaignItemDataList.clear();
                    mCampaignItemDataList.addAll(campaignApi.mCampaignListData);
                }

                if (mCampaignInfoAdapter != null) {
                    mCampaignInfoAdapter.notifyDataSetChanged();
                    mCampaignInfoAdapter.cacheInfoToFile(mCampaignInfoMap);
                } else {
                    mCampaignInfoAdapter = new CampaignInfoAdapter(mContext, mCampaignInfoMap);
                    iRecyclerView.setIAdapter(mCampaignInfoAdapter);
                }

                // 网络原因，无法获取数据
            } else {
                if (mCampaignInfoAdapter.isAdapterRealEmpty()) {
                    if (mRefresh) {
                        CampaignListMonitor.getInstance().notifyObservers(CampaignDataState.STATE_LOED_FAIL, 1, CampaignDataState.BEGIN_WITH_OTHER);
                    } else {
                        if (mFullyDisplay) {
                            CampaignListMonitor.getInstance().notifyObservers(CampaignDataState.STATE_LOED_FAIL, 0, CampaignDataState.BEGIN_WITH_OTHER);
                        } else {
                            CampaignListMonitor.getInstance().notifyObservers(CampaignDataState.STATE_LOED_FAIL, 0, CampaignDataState.BEGIN_WITH_THEME_SKIN);
                        }
                    }
                }

                if (mRefresh && !mDataLayer.isNetWorkErrorViewShowing()) {
                    showNotification();
                }
            }

        }
    } ;




    private OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v instanceof CampaignCell) {
                CampaignCell cell = (CampaignCell) v;
                CampaignInfo info = cell.getData();
                if (info != null) {
                    // 触发统计
                    Utils.UrlTrigger(mContext, info.getBannerTjUrl());

//                    final String prefix = "item";
//                    final String suffix = ".img";
//                    final String type = "twitter";
//                    final String typeNormal = "other";
//                    int position = iRecyclerView.getLayoutManager().getPosition(v);
//                    String filePathOther = FolderMgr.getInstance().CAMPAIGN_CENTER_CACHE_IMG_PATH + File.separator + prefix + position +  typeNormal + suffix;
//                    String filePathTwitter = FolderMgr.getInstance().CAMPAIGN_CENTER_CACHE_IMG_PATH + File.separator + prefix + position + type + suffix;
//                    info.setCacheImgPath(filePathOther);
//                    info.setCacheImgForTwiter(filePathTwitter);
                    // 保存状态相应数据，以便恢复;
                    Parcelable state = iRecyclerView.getLayoutManager().onSaveInstanceState();
                    mSite.m_myParams.put(mSite.SCROLL_POSITION, state);

                    mAppInterface.onClickBanner(cell.getData().getStatisticId());
                    mSite.onClickCampaignItem(mContext, info);
                }
            } else if (v instanceof AutoDisplayCell) {
                AutoDisplayCell cell = (AutoDisplayCell) v;
                CampaignInfo info = cell.getData();
                mSite.onClickCampaignItem(mContext, info);
            } else if (v instanceof EmptyHolderView) {
                getDataFromServer(false);
            }
        }
    };

    public OnAnimationClickListener onAnimationClickListener = new OnAnimationClickListener() {
        @Override
        public void onAnimationClick(View v) {

        }

        @Override
        public void onTouch(View v) {
            if (v == mActonBar.getLeftImageBtn()) {
                reset();

                mSite.onBack(mContext);
            }
        }

        @Override
        public void onRelease(View v) {

        }
    };

}

