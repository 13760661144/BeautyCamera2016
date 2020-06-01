package cn.poco.camera3.beauty;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import cn.poco.acne.view.CirclePanel;
import cn.poco.advanced.ImageUtils;
import cn.poco.camera3.beauty.callback.IBeautyPageCallback;
import cn.poco.camera3.beauty.callback.IFilterPageCallback;
import cn.poco.camera3.beauty.callback.IPageCallback;
import cn.poco.camera3.beauty.callback.IShapePageCallback;
import cn.poco.camera3.beauty.callback.PageCallbackAdapter;
import cn.poco.camera3.beauty.data.BaseShapeResMgr;
import cn.poco.camera3.beauty.data.BeautyData;
import cn.poco.camera3.beauty.data.BeautyInfo;
import cn.poco.camera3.beauty.data.BeautyResMgr;
import cn.poco.camera3.beauty.data.BeautyShapeSyncInfo;
import cn.poco.camera3.beauty.data.ShapeData;
import cn.poco.camera3.beauty.data.ShapeDataType;
import cn.poco.camera3.beauty.data.ShapeInfo;
import cn.poco.camera3.beauty.data.ShapeResMgr;
import cn.poco.camera3.beauty.data.ShapeSyncResMgr;
import cn.poco.camera3.beauty.data.SuperShapeData;
import cn.poco.camera3.beauty.page.BaseFramePager;
import cn.poco.camera3.beauty.page.BeautyFramePager;
import cn.poco.camera3.beauty.page.BeautyFramePagerAdapter;
import cn.poco.camera3.beauty.page.FilterFramePager;
import cn.poco.camera3.beauty.page.IFramePager;
import cn.poco.camera3.beauty.page.ShapeFramePager;
import cn.poco.camera3.beauty.recycler.ShapeExAdapter;
import cn.poco.camera3.beauty.viewpagerIndicator.TabPageIndicator;
import cn.poco.camera3.ui.drawable.RoundRectDrawable;
import cn.poco.camera3.util.CameraPercentUtil;
import cn.poco.cloudalbumlibs.utils.CloudAlbumDialog;
import cn.poco.filter4.FilterResMgr;
import cn.poco.filter4.recycle.FilterAdapter;
import cn.poco.framework.EventCenter;
import cn.poco.framework.EventID;
import cn.poco.framework.SiteID;
import cn.poco.login.LoginOtherUtil;
import cn.poco.login.UserMgr;
import cn.poco.resource.AbsDownloadMgr;
import cn.poco.resource.BaseRes;
import cn.poco.resource.DownloadMgr;
import cn.poco.resource.FilterRes;
import cn.poco.resource.IDownload;
import cn.poco.resource.RecommendRes;
import cn.poco.resource.ResType;
import cn.poco.rise.RiseSeekBar;
import cn.poco.statistics.MyBeautyStat;
import cn.poco.system.SysConfig;
import cn.poco.utils.OnAnimationClickListener;
import my.beautyCamera.BuildConfig;
import my.beautyCamera.R;

/**
 * @author lmx
 *         Created by lmx on 2018-01-15.
 */

public class BeautySelectorView extends FrameLayout
{
    private static final String TAG = "bbb";

    private TabUIConfig mTabUIConfig;

    private TabUIConfig.TabUI mCurrentTabUI;

    private int mIndicatorH;
    private int mContainerH;
    private int mPageCount;
    private int mCirclePanelRadius;

    private boolean mShowTopBar = true;
    private boolean mTopBarAnim;

    private boolean isClose;

    private boolean hasFilter;
    private boolean hasShape;
    private boolean hasBeauty;

    private boolean isClickShapeSensor = true;

    private LinearLayout mTopBarView;
    private RelativeLayout mBarLayout;

    private BeautyFramePagerAdapter mAdapter;
    private TabPageIndicator mTabIndicator;
    private ViewPager mViewPager;

    private FrameLayout mResetView;
    private SaveBtn mSaveView;
    private FoldBtn mFoldView;
    private CirclePanel mCirclePanel;

    private boolean isResetViewAnima;
    private boolean isSaveViewAnima;

    private EventCenter.OnEventListener mEventListener;

    // callback
    protected TabPageIndicator.OnTabSelectListener mTabReselectedListener;
    protected TabPageIndicator.OnPageChangeListenerEx mPagerChangeListener;
    protected OnAnimationClickListener mOnAnimationClickListener;
    protected IPageCallback mCallback;
    private PageCallbackAdapter mInnerCallback;
    private AbsDownloadMgr.DownloadListener mFilterDownloadListener;


    //data
    protected ArrayList<FilterAdapter.ItemInfo> mFilterList;
    protected ArrayList<ShapeExAdapter.ShapeExItemInfo> mShapeList;
    protected ArrayList<BeautyInfo> mBeautyList;


    protected boolean isPagerCanScroll = false;

    private final Object RES_OBJ_LOCK = new Object();

    public BeautySelectorView(@NonNull Context context,
                              @NonNull TabUIConfig tabUIConfig,
                              IPageCallback callback)
    {
        super(context);
        mTabUIConfig = tabUIConfig;
        mCallback = callback;
        initData();
        initView();
    }

    private void initData()
    {
        if (mTabUIConfig == null || mTabUIConfig.mBuilder == null)
        {
            throw new IllegalStateException("config is null or builder not instantiation");
        }
        mContainerH = CameraPercentUtil.HeightPxToPercent(360);
        mIndicatorH = CameraPercentUtil.HeightPxToPercent(88);
        mPageCount = mTabUIConfig.getTabCount();
        mCirclePanelRadius = CameraPercentUtil.WidthPxToPercent(55 / 2);

        hasFilter = mTabUIConfig.hasTabUI(TabUIConfig.TAB_TYPE.TAB_FILTER);
        hasBeauty = mTabUIConfig.hasTabUI(TabUIConfig.TAB_TYPE.TAB_BEAUTY);
        hasShape = mTabUIConfig.hasTabUI(TabUIConfig.TAB_TYPE.TAB_SHAPE);

        initCallback();
    }


    public ArrayList<FilterAdapter.ItemInfo> getFilterList(boolean update)
    {
        synchronized (RES_OBJ_LOCK)
        {
            if (update || mFilterList == null)
            {
                mFilterList = FilterResMgr.GetLiveFilterRes(getContext(), true);
            }
            return mFilterList;
        }
    }

    public ArrayList<ShapeExAdapter.ShapeExItemInfo> getShapeList(boolean update)
    {
        synchronized (RES_OBJ_LOCK)
        {
            if (update || mShapeList == null)
            {
                mShapeList = BeautyShapeInfoResMgr.GetShapeInfoList(getContext());
            }
            return mShapeList;
        }
    }

    public ArrayList<BeautyInfo> getBeautyList(boolean update)
    {
        synchronized (RES_OBJ_LOCK)
        {
            if (update || mBeautyList == null)
            {
                mBeautyList = BeautyResMgr.getInstance().GetResArrByInfoFilter(getContext(), null);
            }
            return mBeautyList;
        }
    }

    private BeautyInfo getBeautyInfo(@TabUIConfig.PAGE_TYPE int pageType, boolean update)
    {
        BeautyInfo out = null;
        ArrayList<BeautyInfo> beautyList = getBeautyList(update);
        if (beautyList != null && beautyList.size() > 0)
        {
            //目前 镜头社区 & 直播助手共用
            out = beautyList.get(0);
        }
        return out;
    }

    public final @TabUIConfig.PAGE_TYPE int getPageType()
    {
        return mTabUIConfig == null ? TabUIConfig.PAGE_TYPE.UNSET : mTabUIConfig.getPageType();
    }

    public TabUIConfig.TabUI getCurrentPageTabUI()
    {
        return mCurrentTabUI;
    }

    /**
     * 设置是否触发脸型统计，需在回调前设置
     *
     * @param clickShapeSensor
     */
    public void setClickShapeSensor(boolean clickShapeSensor)
    {
        this.isClickShapeSensor = clickShapeSensor;
    }

    private void initView()
    {
        LayoutParams params;

        mBarLayout = new RelativeLayout(getContext());
        mBarLayout.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mOnAnimationClickListener != null) {
                    mOnAnimationClickListener.onAnimationClick(mBarLayout);
                }
            }
        });
        mBarLayout.setPadding(0, 0, 0, CameraPercentUtil.HeightPxToPercent(30));
        params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        params.bottomMargin = mContainerH;
        this.addView(mBarLayout, params);

        if (hasShape)
        {
            //重置
            mResetView = new FrameLayout(getContext());
            mResetView.setId(R.id.beauty_selector_view_reset_btn);
            RoundRectDrawable drawable = new RoundRectDrawable();
            drawable.setRoundRectParams(CameraPercentUtil.WidthPxToPercent(28), CameraPercentUtil.WidthPxToPercent(28));
            drawable.setColor(ImageUtils.GetColorAlpha(Color.WHITE, 0.96f));
            drawable.setShadowLayer(CameraPercentUtil.WidthPxToPercent(5), 0, CameraPercentUtil.WidthPxToPercent(2), ImageUtils.GetColorAlpha(Color.BLACK, 0.6f));
            mResetView.setBackgroundDrawable(drawable);
            mResetView.setOnTouchListener(mOnAnimationClickListener);
            mResetView.setVisibility(View.GONE);
            mResetView.setTag(false);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(CameraPercentUtil.WidthPxToPercent(103), CameraPercentUtil.WidthPxToPercent(56));
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_START);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            layoutParams.leftMargin = CameraPercentUtil.WidthPxToPercent(14);
            mBarLayout.addView(mResetView, layoutParams);
            {
                TextView resetV = new TextView(getContext());
                resetV.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
                resetV.setTextColor(0xff333333);
                resetV.setText(R.string.beauty_selector_view_shape_reset_params);
                resetV.setGravity(Gravity.CENTER);
                params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                params.gravity = Gravity.CENTER;
                mResetView.addView(resetV, params);
            }

            //“我的”脸型展开子列表才显示
            mSaveView = new SaveBtn(getContext());
            mSaveView.setId(R.id.beauty_selector_view_save_btn);
            mSaveView.setVisibility(View.GONE);
            mSaveView.setTag(false);
            mSaveView.setOnTouchListener(mOnAnimationClickListener);
            layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, CameraPercentUtil.WidthPxToPercent(56));
            layoutParams.addRule(RelativeLayout.RIGHT_OF, mResetView.getId());
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            layoutParams.leftMargin = CameraPercentUtil.WidthPxToPercent(31);
            mBarLayout.addView(mSaveView, layoutParams);

            //判断是否已修改过
            boolean isModify = ShapeSyncResMgr.getInstance().checkSyncDataIsModify(getContext());
            mSaveView.setSaved(!isModify);
        }

        //收缩
        mFoldView = new FoldBtn(getContext());
        mFoldView.setOnTouchListener(mOnAnimationClickListener);
        RoundRectDrawable drawable = new RoundRectDrawable();
        drawable.setRoundRectParams(CameraPercentUtil.WidthPxToPercent(28), CameraPercentUtil.WidthPxToPercent(28));
        drawable.setColor(ImageUtils.GetColorAlpha(Color.WHITE, 0.96f));
        drawable.setShadowLayer(CameraPercentUtil.WidthPxToPercent(5), 0, CameraPercentUtil.WidthPxToPercent(2), ImageUtils.GetColorAlpha(Color.BLACK, 0.6f));
        mFoldView.setBackgroundDrawable(drawable);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(CameraPercentUtil.WidthPxToPercent(116), CameraPercentUtil.WidthPxToPercent(56));
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_END);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        layoutParams.rightMargin = CameraPercentUtil.WidthPxToPercent(16);
        mBarLayout.addView(mFoldView, layoutParams);

        FrameLayout mContainer = new FrameLayout(getContext());
        mContainer.setBackgroundColor(Color.WHITE);
        params = new LayoutParams(LayoutParams.MATCH_PARENT, mContainerH);
        params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
        this.addView(mContainer, params);
        {
            mViewPager = new ViewPager(getContext())
            {
                @Override
                public boolean onInterceptTouchEvent(MotionEvent ev)
                {
                    return isPagerCanScroll && super.onInterceptTouchEvent(ev);
                }

                @Override
                public boolean onTouchEvent(MotionEvent ev)
                {
                    return isPagerCanScroll && super.onTouchEvent(ev);
                }
            };
            mViewPager.setId(R.id.beauty_selector_view_pager);
            mViewPager.setOffscreenPageLimit(2);//预加载2页面
            params = new LayoutParams(LayoutParams.MATCH_PARENT, mContainerH);
            params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
            mContainer.addView(mViewPager, params);

            mTopBarView = new LinearLayout(getContext());
            mTopBarView.setOrientation(LinearLayout.VERTICAL);
            mTopBarView.setGravity(Gravity.CENTER_HORIZONTAL);
            mTopBarView.setTag(true);
            params = new LayoutParams(LayoutParams.MATCH_PARENT, mIndicatorH);
            params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
            mContainer.addView(mTopBarView, params);
            {
                mTabIndicator = new TabPageIndicator(getContext());
                mTabIndicator.setId(R.id.beauty_selector_view_indicator);
                mTabIndicator.setBackgroundColor(ImageUtils.GetColorAlpha(Color.WHITE, 0.92f));
                mTabIndicator.setOnPageChangeExListener(mPagerChangeListener);
                mTabIndicator.setOnTabSelectListener(mTabReselectedListener);
                LinearLayout.LayoutParams linparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, mIndicatorH - CameraPercentUtil.HeightPxToPercent(1));
                linparams.gravity = Gravity.CENTER_HORIZONTAL;
                mTopBarView.addView(mTabIndicator, linparams);

                View line = new View(getContext());
                line.setBackgroundColor(ImageUtils.GetColorAlpha(0xff000000, 0.06f));
                linparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, CameraPercentUtil.HeightPxToPercent(1));
                linparams.gravity = Gravity.CENTER_HORIZONTAL;
                mTopBarView.addView(line, linparams);
            }
        }

        mAdapter = new BeautyFramePagerAdapter(getContext(), mInnerCallback);
        mAdapter.setTabUIConfig(mTabUIConfig);
        mViewPager.setAdapter(mAdapter);
        mTabIndicator.setViewPager(mViewPager);

        mCirclePanel = new CirclePanel(getContext());
        params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        this.addView(mCirclePanel, params);
    }


    /**
     * 更新美颜数据，更新seek bar，
     *
     * @param type
     * @param uiProgress
     * @return
     */
    public BeautyData updateBeautyData(@STag.BeautyTag int type, int uiProgress)
    {
        if (!hasBeauty) return null;
        if (mViewPager != null)
        {
            View viewWithTag = mViewPager.findViewWithTag(BeautyFramePager.BEAUTY_FRAME_PAGER_TAG);
            if (viewWithTag != null && viewWithTag instanceof BeautyFramePager)
            {
                return ((BeautyFramePager) viewWithTag).updateBeautyData(type, uiProgress, true);
            }
        }
        return null;
    }

    /**
     * 更新脸型数据，更新seek bar
     *
     * @param type
     * @param shapeId
     * @param uiProgress
     * @return
     */
    public ShapeData updateShapeData(@ShapeDataType int type, int shapeId, int uiProgress)
    {
        if (!hasShape) return null;
        if (mViewPager != null)
        {
            View viewWithTag = mViewPager.findViewWithTag(ShapeFramePager.SHAPE_FRAME_PAGER_TAG);
            if (viewWithTag != null && viewWithTag instanceof ShapeFramePager)
            {
                showResetViewAnim(true, 0);
                return ((ShapeFramePager) viewWithTag).updateShapeData(type, shapeId, uiProgress);
            }
        }
        return null;
    }

    public void setCameraShapeId(int shapeId)
    {
        if (!hasShape) return;
        if (mViewPager != null)
        {
            View viewWithTag = mViewPager.findViewWithTag(ShapeFramePager.SHAPE_FRAME_PAGER_TAG);
            if (viewWithTag != null && viewWithTag instanceof ShapeFramePager)
            {
                ((ShapeFramePager) viewWithTag).setSelectShapeId(shapeId);
            }
        }
    }

    public void setCameraShapeSubOpen(int shapeId, boolean open)
    {
        if (!hasShape) return;
        if (mViewPager != null)
        {
            View viewWithTag = mViewPager.findViewWithTag(ShapeFramePager.SHAPE_FRAME_PAGER_TAG);
            if (viewWithTag != null && viewWithTag instanceof ShapeFramePager)
            {
                ((ShapeFramePager) viewWithTag).setShapeSubOpen(shapeId, open);
            }
        }
    }

    /**
     * 镜头手势滑动滤镜
     *
     * @param next
     */
    public void setCameraFilterNext(boolean next)
    {
        if (!hasFilter) return;
        if (mViewPager != null)
        {
            View viewWithTag = mViewPager.findViewWithTag(FilterFramePager.FILTER_FRAME_PAGER_TAG);
            if (viewWithTag != null && viewWithTag instanceof FilterFramePager)
            {
                ((FilterFramePager) viewWithTag).setCameraFilterNext(next);
            }
        }
    }

    public void setCameraFilterId(int uri)
    {
        if (!hasFilter) return;
        if (mViewPager != null)
        {
            View viewWithTag = mViewPager.findViewWithTag(FilterFramePager.FILTER_FRAME_PAGER_TAG);
            if (viewWithTag != null && viewWithTag instanceof FilterFramePager)
            {
                ((FilterFramePager) viewWithTag).setFilterUri(uri, true, true);
            }
        }
    }

    /**
     * 取消选择列表滤镜选择
     */
    public void cancelFilterUri()
    {
        if (!hasFilter) return;
        if (mViewPager != null)
        {
            View viewWithTag = mViewPager.findViewWithTag(FilterFramePager.FILTER_FRAME_PAGER_TAG);
            if (viewWithTag != null && viewWithTag instanceof FilterFramePager)
            {
                ((FilterFramePager) viewWithTag).cancelFilterUri();
            }
        }
    }

    /**
     * 设置当前是否使用了贴纸自带的滤镜效果
     *
     * @param usedStickerFilter
     */
    public void setUsedStickerFilter(boolean usedStickerFilter)
    {
        if (!hasFilter) return;
        if (mViewPager != null)
        {
            View viewWithTag = mViewPager.findViewWithTag(FilterFramePager.FILTER_FRAME_PAGER_TAG);
            if (viewWithTag != null && viewWithTag instanceof FilterFramePager)
            {
                ((FilterFramePager) viewWithTag).setUsedStickerFilter(usedStickerFilter);
            }
        }
    }

    /**
     * 是否显示滤镜名称toast，在调用callback之前设置
     *
     * @param show
     */
    public void setFilterMsgToastShow(boolean show)
    {
        if (!hasFilter) return;
        if (mViewPager != null)
        {
            View viewWithTag = mViewPager.findViewWithTag(FilterFramePager.FILTER_FRAME_PAGER_TAG);
            if (viewWithTag != null && viewWithTag instanceof FilterFramePager)
            {
                ((FilterFramePager) viewWithTag).showFilterMsgToast(show);
            }
        }
    }

    public void setTab(@TabUIConfig.TAB_TYPE int type)
    {
        if (type != TabUIConfig.TAB_TYPE.UNSET && mTabIndicator != null)
        {
            int index = mTabUIConfig.getTabUIType(type);
            if (index != -1)
            {
                int oldPosition = mViewPager.getCurrentItem();
                mTabIndicator.setCurrentItem(index);
                this.onTabSelected(oldPosition, index);
            }
        }
    }

    private void showResetViewAnim(final boolean show, long postDelayRun)
    {
        showResetViewAnim(show, true, postDelayRun);
    }

    private void showResetViewAnim(final boolean show, boolean setTag, long postDelayRun)
    {
        if (isResetViewAnima) return;
        if (mResetView != null)
        {
            final boolean isShowing = mResetView.getVisibility() == View.VISIBLE;
            if (isShowing == show) return;
            if (setTag) { mResetView.setTag(show);}
            ObjectAnimator animator1;
            ObjectAnimator animator2;
            if (show)
            {
                animator1 = ObjectAnimator.ofFloat(mResetView, "scaleX", 0, 1);
                animator2 = ObjectAnimator.ofFloat(mResetView, "scaleY", 0, 1);
            }
            else
            {
                animator1 = ObjectAnimator.ofFloat(mResetView, "scaleX", 1, 0);
                animator2 = ObjectAnimator.ofFloat(mResetView, "scaleY", 1, 0);
            }

            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(animator1, animator2);
            animatorSet.setDuration(120);
            animatorSet.setStartDelay(postDelayRun);
            animatorSet.addListener(new AnimatorListenerAdapter()
            {
                @Override
                public void onAnimationEnd(Animator animation)
                {
                    isResetViewAnima = false;
                    if (!show)
                    {
                        mResetView.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onAnimationStart(Animator animation)
                {
                    isResetViewAnima = true;
                    if (show)
                    {
                        mResetView.setVisibility(View.VISIBLE);
                    }
                }
            });
            animatorSet.start();
        }
    }

    private void showSaveViewAnim(final boolean show, long postDelayRun)
    {
        showSaveViewAnim(show, true, postDelayRun);
    }

    private void showSaveViewAnim(final boolean show, boolean setTag, long postDelayRun)
    {
        if (isSaveViewAnima) return;
        if (mSaveView != null)
        {
            final boolean isShowing = mSaveView.getVisibility() == View.VISIBLE;
            if (isShowing == show) return;
            if (setTag) {mSaveView.setTag(show);}
            ObjectAnimator animator1;
            ObjectAnimator animator2;
            if (show)
            {
                animator1 = ObjectAnimator.ofFloat(mSaveView, "scaleX", 0, 1);
                animator2 = ObjectAnimator.ofFloat(mSaveView, "scaleY", 0, 1);
            }
            else
            {
                animator1 = ObjectAnimator.ofFloat(mSaveView, "scaleX", 1, 0);
                animator2 = ObjectAnimator.ofFloat(mSaveView, "scaleY", 1, 0);
            }

            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(animator1, animator2);
            animatorSet.setDuration(120);
            animatorSet.setStartDelay(postDelayRun);
            animatorSet.addListener(new AnimatorListenerAdapter()
            {
                @Override
                public void onAnimationEnd(Animator animation)
                {
                    isSaveViewAnima = false;
                    if (!show)
                    {
                        mSaveView.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onAnimationStart(Animator animation)
                {
                    isSaveViewAnima = true;
                    if (show)
                    {
                        mSaveView.setVisibility(View.VISIBLE);
                    }
                }
            });
            animatorSet.start();
        }
    }

    public void setPagerCanScroll(boolean isPagerCanScroll)
    {
        this.isPagerCanScroll = isPagerCanScroll;
    }

    public void setCallback(IPageCallback callback)
    {
        this.mCallback = callback;
    }

    private void initCallback()
    {
        //event 事件监听
        mEventListener = new EventCenter.OnEventListener()
        {
            @Override
            public void onEvent(int eventId, Object[] params)
            {
                if (isClose) return;

                if (eventId == EventID.NOTIFY_SYNC_SHAPE_UPDATE && hasShape)
                {
                    ShapeSyncResMgr.onPostResponseInfo responseInfo = null;
                    if (params != null && params.length > 0)
                    {
                        if (params[0] != null && params[0] instanceof ShapeSyncResMgr.onPostResponseInfo)
                        {
                            responseInfo = (ShapeSyncResMgr.onPostResponseInfo) params[0];
                        }
                    }

                    //event 事件回调更新数据
                    if (responseInfo != null && responseInfo.error == 0 && !isClose) {
                        if (mViewPager != null) {
                            View viewWithTag = mViewPager.findViewWithTag(ShapeFramePager.SHAPE_FRAME_PAGER_TAG);
                            if (viewWithTag != null && viewWithTag instanceof ShapeFramePager) {
                                ((ShapeFramePager) viewWithTag).updateSeekBarShapeData();
                            }
                        }
                    }

                    if (BuildConfig.DEBUG && responseInfo != null)
                    {
                        Log.d(TAG, "BeautySelectorView --> onEvent: NOTIFY_SYNC_SHAPE_UPDATE " + responseInfo.error);
                    }
                }
                else if (eventId == EventID.NOTIFY_FILTERRES_DELETE && hasFilter)
                {
                    if (params != null && params[0] != null && params[0] instanceof ArrayList)
                    {
                        try
                        {
                            if (mCallback != null && mCallback instanceof IFilterPageCallback)
                            {
                                ((IFilterPageCallback) mCallback).onFilterUpdateRemove((ArrayList<Integer>) params[0]);
                            }
                        }
                        catch (Throwable t)
                        {

                        }
                    }
                }
            }
        };
        EventCenter.addListener(mEventListener);

        if (mTabReselectedListener == null)
        {
            mTabReselectedListener = new TabPageIndicator.OnTabSelectListener()
            {
                @Override
                public void onTabSelected(int oldPosition, int newPosition)
                {
                    //tab点击事件
                    BeautySelectorView.this.onTabSelected(oldPosition, newPosition);
                }
            };
        }

        //pager changed listener
        if (mPagerChangeListener == null)
        {
            mPagerChangeListener = new TabPageIndicator.OnPageChangeListenerEx()
            {
                @Override
                public void onInitialPage(int position)
                {
                    //当前初始化的page
                    if (mTabUIConfig != null)
                    {
                        mCurrentTabUI = mTabUIConfig.getTabUI(position);
                    }
                }

                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
                {

                }

                @Override
                public void onPageSelected(int position)
                {
                    if (mViewPager != null && mTabUIConfig != null)
                    {
                        TabUIConfig.TabUI tabUI = mTabUIConfig.getTabUI(position);
                        TabUIConfig.TabUI lastTabUI = mCurrentTabUI;
                        mCurrentTabUI = tabUI;
                        if (tabUI != null)
                        {
                            View framePageView = null;
                            Object tag = null;
                            if (tabUI.m_type == TabUIConfig.TAB_TYPE.TAB_BEAUTY)
                            {
                                tag = BeautyFramePager.BEAUTY_FRAME_PAGER_TAG;
                            }
                            else if (tabUI.m_type == TabUIConfig.TAB_TYPE.TAB_SHAPE)
                            {
                                tag = ShapeFramePager.SHAPE_FRAME_PAGER_TAG;
                            }
                            else if (tabUI.m_type == TabUIConfig.TAB_TYPE.TAB_FILTER)
                            {
                                tag = FilterFramePager.FILTER_FRAME_PAGER_TAG;
                            }

                            if (tag != null)
                            {
                                framePageView = mViewPager.findViewWithTag(tag);
                                if (framePageView != null && framePageView instanceof BaseFramePager)
                                {
                                    BeautySelectorView.this.onPageSelected(position, tabUI, (BaseFramePager) framePageView);
                                    ((BaseFramePager) framePageView).onPageSelected(position, tag);
                                }
                                if (mInnerCallback != null)
                                {
                                    mInnerCallback.onPageSelected(position, tabUI);
                                }
                            }
                        }
                        //切换tab统计
                        sendSensorPageTypeData(mTabUIConfig.getPageType(), lastTabUI, false);
                        sendSensorPageTypeData(mTabUIConfig.getPageType(), tabUI, true);
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state)
                {
                }
            };
        }

        if (mOnAnimationClickListener == null)
        {
            mOnAnimationClickListener = new OnAnimationClickListener()
            {
                @Override
                public void onAnimationClick(View v)
                {
                    if (v == mFoldView || v == mBarLayout)
                    {
                        //收缩列表
                        if (mInnerCallback != null) mInnerCallback.setShowSelectorView(false);
                        int pageType = getPageType();
                        if (pageType == TabUIConfig.PAGE_TYPE.PAGE_CAMERA) {
                            MyBeautyStat.onClickByRes(R.string.拍照_拍照_美形定制_收回按钮);
                        } else if (pageType == TabUIConfig.PAGE_TYPE.PAGE_LIVE){
                        }
                    }
                    else if (v == mResetView)
                    {
                        //重置脸型数据
                        resetCurrentShapeData();
                        int pageType = getPageType();
                        if (pageType == TabUIConfig.PAGE_TYPE.PAGE_CAMERA) {
                            MyBeautyStat.onClickByRes(R.string.拍照_拍照_美形定制_重置脸型);
                        } else if (pageType == TabUIConfig.PAGE_TYPE.PAGE_LIVE) {
                            MyBeautyStat.onClickByRes(R.string.直播助手_美颜页_脸型调整_重置脸型参数);
                        }
                    }
                    else if (v == mSaveView)
                    {
                        //保存同步
                        saveMineSyncShapeDataUpload();
                        int pageType = getPageType();
                        if (pageType == TabUIConfig.PAGE_TYPE.PAGE_CAMERA) {
                            MyBeautyStat.onClickByRes(R.string.拍照_拍照_美形定制_保存);
                        } else if (pageType == TabUIConfig.PAGE_TYPE.PAGE_LIVE) {
                        }
                    }
                }
            };
        }

        if (mInnerCallback == null)
        {
            mInnerCallback = new PageCallbackAdapter()
            {
                @Override
                public void onShowTopBar(boolean show)
                {
                    showTopBar(show);
                }

                @Override
                public void setShowSelectorView(boolean show)
                {
                    if (mCallback != null) mCallback.setShowSelectorView(show);
                }

                @Override
                public void onSeekBarSlide(View seek, int progress, boolean isStop)
                {
                    updateCircle(seek, !isStop, progress);
                }

                @Override
                public void onFilterItemClick(FilterRes filterRes, boolean showToast)
                {
                    if (mCallback != null && mCallback instanceof IFilterPageCallback)
                    {
                        ((IFilterPageCallback) mCallback).onFilterItemClick(filterRes, showToast);
                    }
                }

                @Override
                public void onFilterItemDownload()
                {
                    if (mCallback != null && mCallback instanceof IFilterPageCallback)
                    {
                        ((IFilterPageCallback) mCallback).onFilterItemDownload();
                    }
                }

                @Override
                public void onFilterItemRecommend(ArrayList<RecommendRes> ress)
                {
                    if (mCallback != null && mCallback instanceof IFilterPageCallback)
                    {
                        ((IFilterPageCallback) mCallback).onFilterItemRecommend(ress);
                    }
                }

                @Override
                public void onShapeItemClick(@Nullable ShapeExAdapter.ShapeExItemInfo itemInfo, ShapeData data)
                {
                    if (itemInfo != null)
                    {
                        if (itemInfo.m_uri == SuperShapeData.ID_NON_SHAPE) {
                            if (mResetView != null)
                            {
                                mResetView.setVisibility(View.GONE);
                                mResetView.setTag(false);
                            }
                            if (mSaveView != null)
                            {
                                mSaveView.setTag(false);
                                mSaveView.setVisibility(View.GONE);
                            }
                        }
                        if (isClickShapeSensor) {
                            sendSensorClickShapeData(itemInfo.m_uri, false);
                        }
                        isClickShapeSensor = true;
                    }
                    if (mCallback != null && mCallback instanceof IShapePageCallback)
                    {
                        ((IShapePageCallback) mCallback).onShapeItemClick(itemInfo, data);
                    }
                }

                //滑动seek更新
                @Override
                public void onShapeUpdate(int subPosition, @Nullable ShapeExAdapter.ShapeExItemInfo itemInfo, ShapeData data)
                {
                    if (itemInfo != null)
                    {
                        itemInfo.m_data.setDefaultData(false);
                        showResetViewAnim(true, 0);
                        if (itemInfo.m_uri == SuperShapeData.ID_MINE_SYNC)
                        {
                            itemInfo.m_data.setModify(true);
                            if (mSaveView != null)
                            {
                                mSaveView.setSaved(false);
                            }
                        }
                    }

                    if (mCallback != null && mCallback instanceof IShapePageCallback)
                    {
                        ((IShapePageCallback) mCallback).onShapeUpdate(subPosition, itemInfo, data);
                    }
                }

                //重置按钮更新
                @Override
                public void onResetShapeData(int subPosition, @Nullable ShapeExAdapter.ShapeExItemInfo itemInfo, ShapeData data)
                {
                    if (itemInfo != null)
                    {
                        if (itemInfo.m_uri == SuperShapeData.ID_MINE_SYNC && itemInfo.m_data != null)
                        {
                            itemInfo.m_data.setModify(true);
                            if (mSaveView != null) mSaveView.setSaved(false);
                        }

                        if (itemInfo.m_data != null)
                        {
                            itemInfo.m_data.setDefaultData(true);
                        }
                    }


                    if (mCallback != null && mCallback instanceof IShapePageCallback)
                    {
                        ((IShapePageCallback) mCallback).onShapeUpdate(subPosition, itemInfo, data);
                    }

                    //toast
                    if (getContext() != null) {
                        Toast.makeText(getContext(), getContext().getString(R.string.beauty_selector_view_shape_reset_params_success), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onSubLayoutOpen(boolean isOpen,
                                            boolean showSeekBar,
                                            int position,
                                            ShapeExAdapter.ShapeExItemInfo itemInfo)
                {
                    if (itemInfo != null)
                    {
                        if (itemInfo.m_uri == SuperShapeData.ID_MINE_SYNC)
                        {
                            //"我的"脸型

                            //显示 保存按钮 和 重置按钮
                            showResetViewAnim(isOpen, 0);
                            showSaveViewAnim(isOpen, 0);
                        }
                        else if (itemInfo.m_uri != SuperShapeData.ID_NON_SHAPE)
                        {
                            showResetViewAnim(isOpen, 0);
                            showSaveViewAnim(false, 0);
                        }

                        if (isOpen) {
                            sendSensorClickShapeData(itemInfo.m_uri, true);
                        }
                    }

                    if (mCallback != null && mCallback instanceof IShapePageCallback)
                    {
                        ((IShapePageCallback) mCallback).onSubLayoutOpen(isOpen, showSeekBar, position, itemInfo);
                    }
                }

                @Override
                public void onBeautyUpdate(@STag.BeautyTag int type, BeautyData beautyData)
                {
                    if (mCallback != null && mCallback instanceof IBeautyPageCallback)
                    {
                        ((IBeautyPageCallback) mCallback).onBeautyUpdate(type, beautyData);
                    }
                }

                @Override
                public Object getFramePagerData(int position, String frameTag, boolean isUpdate)
                {
                    if (frameTag != null)
                    {
                        switch (frameTag)
                        {
                            case BeautyFramePager.BEAUTY_FRAME_PAGER_TAG:
                                return getBeautyInfo(mTabUIConfig.getPageType(), isUpdate);
                            case ShapeFramePager.SHAPE_FRAME_PAGER_TAG:
                                return getShapeList(isUpdate);
                            case FilterFramePager.FILTER_FRAME_PAGER_TAG:
                                return getFilterList(isUpdate);
                        }
                    }
                    return null;
                }

                @Override
                public void onLogin()
                {
                    if (mCallback != null) mCallback.onLogin();
                }

                @Override
                public void onBindPhone()
                {
                    if (mCallback != null) mCallback.onBindPhone();
                }

                @Override
                public void onPageSelected(int position, TabUIConfig.TabUI tabUI)
                {
                    if (mCallback != null)
                    {
                        mCallback.onPageSelected(position, tabUI);
                    }
                }
            };
        }

        if (hasFilter && mFilterDownloadListener == null)
        {
            mFilterDownloadListener = new AbsDownloadMgr.DownloadListener()
            {
                @Override
                public void OnDataChange(int resType, int downloadId, IDownload[] resArr)
                {
                    if (hasFilter
                            && resType == ResType.FILTER.GetValue()
                            && resArr != null && ((BaseRes) resArr[0]).m_type == BaseRes.TYPE_LOCAL_PATH)
                    {
                        //滤镜下载完成后，更新数据
                        if (resArr.length > 0)
                        {
                            for (IDownload iDownload : resArr)
                            {
                                if (iDownload != null && iDownload instanceof FilterRes)
                                {
                                    if (mCallback != null && mCallback instanceof IFilterPageCallback)
                                    {
                                        ((IFilterPageCallback) mCallback).onFilterUpdateAdd((FilterRes) iDownload, ((FilterRes) iDownload).m_id);
                                    }
                                }
                            }
                        }

                        if (mViewPager != null)
                        {
                            View viewWithTag = mViewPager.findViewWithTag(FilterFramePager.FILTER_FRAME_PAGER_TAG);
                            if (viewWithTag != null && viewWithTag instanceof FilterFramePager)
                            {
                                ((FilterFramePager) viewWithTag).notifyDataChanged();
                                ((FilterFramePager) viewWithTag).updateAndResetDownload();
                            }
                        }
                    }
                }
            };
            DownloadMgr.getInstance().AddDownloadListener(mFilterDownloadListener);
        }
    }

    /**
     * 更新circle
     *
     * @param seek
     * @param show
     * @param uiProgress
     */
    private void updateCircle(View seek, boolean show, int uiProgress)
    {
        if (mCirclePanel != null && seek != null)
        {
            if (show)
            {
                int[] panelLoc = new int[2];
                mCirclePanel.getLocationOnScreen(panelLoc);
                int[] seekbarLoc = new int[2];
                seek.getLocationOnScreen(seekbarLoc);
                float circleY = (seekbarLoc[1] - panelLoc[1]) * 0.4f;
                if (seek instanceof BeautySeekBar)
                {
                    float circleX = mCirclePanelRadius + seek.getLeft() + ((BeautySeekBar) seek).getValidWidth() * uiProgress / 100f;
                    mCirclePanel.change(circleX, circleY, mCirclePanelRadius * 2f);
                    mCirclePanel.setText(uiProgress + "");
                }
                else if (seek instanceof RiseSeekBar)
                {
                    int seekBarWidth = seek.getWidth();
                    int validWidth = ((RiseSeekBar) seek).getValidWidth();
                    float circleX = seek.getLeft() + seekBarWidth / 2f + uiProgress / 100f * (validWidth / 2f);
                    mCirclePanel.change(circleX, circleY, mCirclePanelRadius * 2f);
                    mCirclePanel.setText(uiProgress + "");
                }
                mCirclePanel.show();
            }
            else
            {
                mCirclePanel.hide();
            }
        }
    }

    /**
     * 脸型点解触发统计
     *
     * @param shapeId
     * @param isEditShapeData
     */
    public final void sendSensorClickShapeData(int shapeId, boolean isEditShapeData) {
        int resId = -1;
        int pageType = getPageType();
        switch (shapeId) {
            case SuperShapeData.ID_ZIRANXIUSHI://自然修饰
            {
                if (pageType == TabUIConfig.PAGE_TYPE.PAGE_LIVE)
                {
                    resId = R.string.直播助手_美颜页_脸型调整_脸型_自然修饰;
                    if (isEditShapeData) {
                        resId = R.string.直播助手_美颜页_脸型调整_脸型_自然修饰_进入编辑;
                    }
                }
                else if (pageType == TabUIConfig.PAGE_TYPE.PAGE_CAMERA)
                {
                    resId = R.string.拍照_拍照_美形定制_脸型_自然修饰;
                    if (isEditShapeData) {
                        resId = R.string.拍照_拍照_美形定制_脸型_自然修饰_进入编辑;
                    }
                }
                break;
            }
            case SuperShapeData.ID_BABIGONGZHU://芭比公主
            {
                if (pageType == TabUIConfig.PAGE_TYPE.PAGE_LIVE)
                {
                    resId = R.string.直播助手_美颜页_脸型调整_脸型_芭比公主;
                    if (isEditShapeData) {
                        resId = R.string.直播助手_美颜页_脸型调整_脸型_芭比公主_进入编辑;
                    }
                }
                else if (pageType == TabUIConfig.PAGE_TYPE.PAGE_CAMERA)
                {
                    resId = R.string.拍照_拍照_美形定制_脸型_芭比公主;
                    if (isEditShapeData) {
                        resId = R.string.拍照_拍照_美形定制_脸型_芭比公主_进入编辑;
                    }
                }
                break;
            }
            case SuperShapeData.ID_JINGZHIWANGHONG://精致网红
            {
                if (pageType == TabUIConfig.PAGE_TYPE.PAGE_LIVE)
                {
                    resId = R.string.直播助手_美颜页_脸型调整_脸型_精致网红;
                    if (isEditShapeData) {
                        resId = R.string.直播助手_美颜页_脸型调整_脸型_精致网红_进入编辑;
                    }
                }
                else if (pageType == TabUIConfig.PAGE_TYPE.PAGE_CAMERA)
                {
                    resId = R.string.拍照_拍照_美形定制_脸型_精致网红;
                    if (isEditShapeData) {
                        resId = R.string.拍照_拍照_美形定制_脸型_精致网红_进入编辑;
                    }
                }
                break;
            }
            case SuperShapeData.ID_JIMENGSHAONV://激萌少女
            {
                if (pageType == TabUIConfig.PAGE_TYPE.PAGE_LIVE)
                {
                    resId = R.string.直播助手_美颜页_脸型调整_脸型_激萌少女;
                    if (isEditShapeData) {
                        resId = R.string.直播助手_美颜页_脸型调整_脸型_激萌少女_进入编辑;
                    }
                }
                else if (pageType == TabUIConfig.PAGE_TYPE.PAGE_CAMERA)
                {
                    resId = R.string.拍照_拍照_美形定制_脸型_激萌少女;
                    if (isEditShapeData) {
                        resId = R.string.拍照_拍照_美形定制_脸型_激萌少女_进入编辑;
                    }
                }
                break;
            }
            case SuperShapeData.ID_MODENGNVWANG://摩登女王
            {
                if (pageType == TabUIConfig.PAGE_TYPE.PAGE_LIVE)
                {
                    resId = R.string.直播助手_美颜页_脸型调整_脸型_摩登女王;
                    if (isEditShapeData) {
                        resId = R.string.直播助手_美颜页_脸型调整_脸型_摩登女王_进入编辑;
                    }
                }
                else if (pageType == TabUIConfig.PAGE_TYPE.PAGE_CAMERA)
                {
                    resId = R.string.拍照_拍照_美形定制_脸型_摩登女王;
                    if (isEditShapeData) {
                        resId = R.string.拍照_拍照_美形定制_脸型_摩登女王_进入编辑;
                    }
                }
                break;
            }
            case SuperShapeData.ID_DAIMENGTIANXIN://呆萌甜心
            {
                if (pageType == TabUIConfig.PAGE_TYPE.PAGE_LIVE)
                {
                    resId = R.string.直播助手_美颜页_脸型调整_脸型_呆萌甜心;
                    if (isEditShapeData) {
                        resId = R.string.直播助手_美颜页_脸型调整_脸型_呆萌甜心_进入编辑;
                    }
                }
                else if (pageType == TabUIConfig.PAGE_TYPE.PAGE_CAMERA)
                {
                    resId = R.string.拍照_拍照_美形定制_脸型_呆萌甜心;
                    if (isEditShapeData) {
                        resId = R.string.拍照_拍照_美形定制_脸型_呆萌甜心_进入编辑;
                    }
                }
                break;
            }
            case SuperShapeData.ID_DUDUTONGYAN://嘟嘟童颜
            {
                if (pageType == TabUIConfig.PAGE_TYPE.PAGE_LIVE)
                {
                    resId = R.string.直播助手_美颜页_脸型调整_脸型_嘟嘟童颜;
                    if (isEditShapeData) {
                        resId = R.string.直播助手_美颜页_脸型调整_脸型_嘟嘟童颜_进入编辑;
                    }
                }
                else if (pageType == TabUIConfig.PAGE_TYPE.PAGE_CAMERA)
                {
                    resId = R.string.拍照_拍照_美形定制_脸型_嘟嘟童颜;
                    if (isEditShapeData) {
                        resId = R.string.拍照_拍照_美形定制_脸型_嘟嘟童颜_进入编辑;
                    }
                }
                break;
            }
            case SuperShapeData.ID_XIAOLIANNVSHEN://小脸女神
            {
                if (pageType == TabUIConfig.PAGE_TYPE.PAGE_LIVE)
                {
                    resId = R.string.直播助手_美颜页_脸型调整_脸型_小脸女神;
                    if (isEditShapeData) {
                        resId = R.string.直播助手_美颜页_脸型调整_脸型_小脸女神_进入编辑;
                    }
                }
                else if (pageType == TabUIConfig.PAGE_TYPE.PAGE_CAMERA)
                {
                    resId = R.string.拍照_拍照_美形定制_脸型_小脸女神;
                    if (isEditShapeData) {
                        resId = R.string.拍照_拍照_美形定制_脸型_小脸女神_进入编辑;
                    }
                }
                break;
            }
            case SuperShapeData.ID_MINE_SYNC://我的脸型
            {
                if (pageType == TabUIConfig.PAGE_TYPE.PAGE_LIVE)
                {
                    resId = R.string.直播助手_美颜页_脸型调整_脸型_我的;
                    if (isEditShapeData) {
                        resId = R.string.直播助手_美颜页_脸型调整_脸型_我的_进入编辑;
                    }
                }
                else if (pageType == TabUIConfig.PAGE_TYPE.PAGE_CAMERA)
                {
                    resId = R.string.拍照_拍照_美形定制_脸型_我的;
                    if (isEditShapeData) {
                        resId = R.string.拍照_拍照_美形定制_脸型_我的_进入编辑;
                    }
                }
                break;
            }
            case SuperShapeData.ID_NON_SHAPE://无脸型
            {
                if (pageType == TabUIConfig.PAGE_TYPE.PAGE_LIVE)
                {
                    resId = R.string.直播助手_美颜页_脸型调整_脸型_无;
                }
                else if (pageType == TabUIConfig.PAGE_TYPE.PAGE_CAMERA)
                {
                    resId = R.string.拍照_拍照_美形定制_脸型_无;
                }
                break;
            }
        }
        if (resId != -1) {
            MyBeautyStat.onClickByRes(resId);
        }
    }

    public final void sendSensorPageTypeData(@TabUIConfig.PAGE_TYPE int pageType, TabUIConfig.TabUI tabUI, boolean isPageStart)
    {
        if (tabUI != null)
        {
            int resId = -1;
            if (pageType == TabUIConfig.PAGE_TYPE.PAGE_CAMERA)
            {
                switch (tabUI.m_type) {
                    case TabUIConfig.TAB_TYPE.TAB_BEAUTY:
                        break;
                    case TabUIConfig.TAB_TYPE.TAB_FILTER:
                        break;
                    case TabUIConfig.TAB_TYPE.TAB_SHAPE:
                        break;
                    case TabUIConfig.TAB_TYPE.UNSET:
                        break;
                }
            }
            else if (pageType == TabUIConfig.PAGE_TYPE.PAGE_LIVE)
            {
                switch (tabUI.m_type) {
                    case TabUIConfig.TAB_TYPE.TAB_BEAUTY:
                        resId = R.string.直播助手_美颜美型页_美颜页;
                        break;
                    case TabUIConfig.TAB_TYPE.TAB_FILTER:
                        resId = R.string.直播助手_美颜美型页_滤镜页;
                        break;
                    case TabUIConfig.TAB_TYPE.TAB_SHAPE:
                        resId = R.string.直播助手_美颜美型页_脸型页;
                        break;
                    case TabUIConfig.TAB_TYPE.UNSET:
                        break;
                }
            }

            if (resId != -1) {
                if (isPageStart) {
                    MyBeautyStat.onPageStartByRes(resId);
                }else {
                    MyBeautyStat.onPageEndByRes(resId);
                }
            }
        }
    }

    private void onPageSelected(int position, TabUIConfig.TabUI tabUI, BaseFramePager baseFramePager)
    {
        if (tabUI != null && position >= 0)
        {
            if (tabUI.m_type != TabUIConfig.TAB_TYPE.TAB_SHAPE)
            {
                //非脸型page隐藏保存&重置按钮
                showResetViewAnim(false, false, 0);
                showSaveViewAnim(false, false, 0);
                showTopBar(true);
            }
            else
            {
                //恢复按钮显示，根据tag状态判断是否恢复
                if (mResetView != null)
                {
                    showResetViewAnim((boolean) mResetView.getTag(), 0);
                }
                if (mSaveView != null)
                {
                    showSaveViewAnim((boolean) mSaveView.getTag(), 0);
                }

                //topbar和seekbar显示状态判断
                if (baseFramePager != null
                        && baseFramePager instanceof ShapeFramePager
                        && ((ShapeFramePager) baseFramePager).isShowSeekBar())
                {
                    showTopBar(false);
                }
                else {
                    showTopBar(true);
                }
            }
        }
    }

    private void onTabSelected(int oldPosition, int newPosition)
    {
        int pageType = getPageType();
        if (pageType == TabUIConfig.PAGE_TYPE.PAGE_CAMERA) {
            TabUIConfig.TabUI tabUI = mTabUIConfig.getTabUI(newPosition);
            if (tabUI != null && oldPosition != newPosition) {
                int resId = -1;
                switch (tabUI.m_type) {
                    case TabUIConfig.TAB_TYPE.TAB_BEAUTY:
                        resId = R.string.拍照_拍照_美形定制_切换美颜Tab;
                        break;
                    case TabUIConfig.TAB_TYPE.TAB_SHAPE:
                        resId = R.string.拍照_拍照_美形定制_切换脸型Tab;
                        break;
                    case TabUIConfig.TAB_TYPE.TAB_FILTER:
                        break;
                    case TabUIConfig.TAB_TYPE.UNSET:
                        break;
                }
                if (resId != -1) MyBeautyStat.onClickByRes(resId);
            }
        } else if (pageType == TabUIConfig.PAGE_TYPE.PAGE_LIVE) {
            TabUIConfig.TabUI tabUI = mTabUIConfig.getTabUI(newPosition);
            if (tabUI != null && oldPosition != newPosition) {
                int resId = -1;
                switch (tabUI.m_type) {
                    case TabUIConfig.TAB_TYPE.TAB_BEAUTY:
                        resId = R.string.直播助手_美颜页_美颜调整_美颜按钮;
                        break;
                    case TabUIConfig.TAB_TYPE.TAB_SHAPE:
                        resId = R.string.直播助手_美颜页_脸型调整_脸型按钮;
                        break;
                    case TabUIConfig.TAB_TYPE.TAB_FILTER:
                        break;
                    case TabUIConfig.TAB_TYPE.UNSET:
                        break;
                }
                if (resId != -1) MyBeautyStat.onClickByRes(resId);
            }
        }
    }


    private boolean isShowTopBar()
    {
        return mShowTopBar;
    }

    private void showTopBar(boolean show)
    {
        if (this.mShowTopBar == show || mTopBarAnim)
        {
            return;
        }
        this.mShowTopBar = show;
        mTopBarView.setTag(show);
        ObjectAnimator topAnimator = ObjectAnimator.ofFloat(mTopBarView, "translationY",
                show ? -CameraPercentUtil.HeightPxToPercent(88) : 0,
                show ? 0 : -CameraPercentUtil.HeightPxToPercent(88));
        topAnimator.setDuration(200);
        topAnimator.addListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                mTopBarAnim = false;
                if (!mShowTopBar)
                {
                    mTopBarView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationStart(Animator animation)
            {
                mTopBarAnim = true;
                if (mShowTopBar)
                {
                    mTopBarView.setVisibility(View.VISIBLE);
                }
            }
        });
        topAnimator.start();
    }

    public void resetCurrentShapeData()
    {
        if (!hasShape) return;
        if (mViewPager != null)
        {
            View viewWithTag = mViewPager.findViewWithTag(ShapeFramePager.SHAPE_FRAME_PAGER_TAG);
            if (viewWithTag != null && viewWithTag instanceof ShapeFramePager)
            {
               ((ShapeFramePager) viewWithTag).resetCurrentShapeData();
            }
        }
    }

    public void resetShapeDataByShapeId(int shapeId)
    {
        if (!hasShape) return;

        if (mViewPager != null)
        {
            View viewWithTag = mViewPager.findViewWithTag(ShapeFramePager.SHAPE_FRAME_PAGER_TAG);
            if (viewWithTag != null && viewWithTag instanceof ShapeFramePager)
            {
                boolean result = ((ShapeFramePager) viewWithTag).resetShapeIdItemInfo(shapeId);

                if (SysConfig.IsDebug())
                {
                    Toast.makeText(getContext(), result ? "已重置" : "", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    /**
     * 保存数据到网络
     */
    private synchronized void saveMineSyncShapeDataUpload()
    {
        ArrayList<ShapeInfo> arr = ShapeSyncResMgr.getInstance().SyncGetSdcardArr(getContext());
        ShapeInfo shapeInfo = BaseShapeResMgr.HasItem(arr, SuperShapeData.ID_MINE_SYNC);
        if (shapeInfo == null) return;

        boolean isLogin = UserMgr.IsLogin(getContext(), null);

        //没有修改过
        if (!shapeInfo.isModify())
        {
            //判断是否登录，未登录再次弹出引导
            if (isLogin) return;
        }


        //更新时间戳
        shapeInfo.setUpdate_time();
        shapeInfo.setModify(false);
        shapeInfo.setDefaultData(false);
        shapeInfo.setNeedSynchronize(true);

        //状态变更
        if (mSaveView != null)
        {
            mSaveView.setSaved(true);
        }

        if (!isLogin)
        {
            //未登录，跳转登录后同步数据
            showLoginDialog();
            return;
        }

        boolean isNetConnected = LoginOtherUtil.isNetConnected(getContext());
        if (!isNetConnected)
        {
            // 先保存，下次有网络同步上传
            Toast.makeText(getContext(), getContext().getString(R.string.network_disconnected), Toast.LENGTH_SHORT).show();
            return;
        }

        //开启线程上传
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                ShapeSyncResMgr.onPostResponseInfo responseInfo = new ShapeSyncResMgr.onPostResponseInfo();
                Context context = getContext();
                if (context == null)
                {
                    responseInfo.error = ShapeSyncResMgr.onPostResponseInfo.ERROR.context_null;
                    return;
                }
                ArrayList<ShapeInfo> arr = ShapeSyncResMgr.getInstance().SyncGetSdcardArr(getContext());
                final ShapeInfo shapeInfo = BaseShapeResMgr.HasItem(arr, SuperShapeData.ID_MINE_SYNC);
                BeautyShapeSyncInfo resultInfo = ShapeSyncResMgr.getInstance().UploadShapeData(getContext(), shapeInfo, null);
                if (resultInfo != null)
                {
                    if (resultInfo.mProtocolCode == 200 && resultInfo.mCode == 0)
                    {
                        if (shapeInfo.isNeedSynchronize())
                        {
                            shapeInfo.setNeedSynchronize(false);
                        }
                        if (!shapeInfo.isModify())
                        {
                            shapeInfo.setModify(false);
                        }
                        shapeInfo.setDefaultData(false);
                        shapeInfo.setUpdate_time();
                        ShapeSyncResMgr.getInstance().SyncSaveSdcardArr(getContext(), arr);
                    }
                    else if (resultInfo.mProtocolCode == 205)
                    {
                        responseInfo.error = ShapeSyncResMgr.onPostResponseInfo.ERROR.access_token_invalid;
                    }
                }
                else
                {
                    responseInfo.error = ShapeSyncResMgr.onPostResponseInfo.ERROR.other;
                }

                if (responseInfo.error != 0)
                {
                    //同步失败，下次上传
                    shapeInfo.setNeedSynchronize(true);
                    ShapeSyncResMgr.getInstance().SyncSaveSdcardArr(getContext(), arr);

                }
                EventCenter.sendEvent(EventID.NOTIFY_SYNC_SHAPE_UPDATE, responseInfo);

            }
        }).start();
    }

    /**
     * 先保存数据到本地，下次登录后上传数据
     */
    private synchronized void saveMineSyncShapeDataNext()
    {
        ArrayList<ShapeInfo> shapeInfos = ShapeSyncResMgr.getInstance().SyncGetSdcardArr(getContext());
        ShapeInfo shapeInfo = BaseShapeResMgr.HasItem(shapeInfos, SuperShapeData.ID_MINE_SYNC);
        if (shapeInfo != null)
        {
            //更新时间戳
            shapeInfo.setUpdate_time();

            //下次需要同步
            shapeInfo.setNeedSynchronize(true);

            //完成已修改的
            shapeInfo.setModify(false);
        }
        ShapeSyncResMgr.getInstance().SyncSaveSdcardArr(getContext(), shapeInfos);

        if (mSaveView != null)
        {
            mSaveView.setSaved(true);
        }
    }


    private void showLoginDialog()
    {
        final CloudAlbumDialog loginDialog = new CloudAlbumDialog(getContext(), ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ImageUtils.AddSkin(getContext(), loginDialog.getOkButtonBg());
        loginDialog.setCancelButtonText(R.string.beauty_selector_view_shape_save_next_time)
                .setOkButtonText(R.string.beauty_selector_view_shape_save_login)
                .setMessage(R.string.beauty_selector_view_shape_save_login_tips)
                .setListener(new CloudAlbumDialog.OnButtonClickListener()
                {
                    @Override
                    public void onOkButtonClick()
                    {
                        if (loginDialog != null)
                        {
                            loginDialog.dismiss();
                        }
                        if (mCallback != null)
                        {
                            mCallback.onLogin();
                        }
                    }

                    @Override
                    public void onCancelButtonClick()
                    {
                        if (loginDialog != null)
                        {
                            loginDialog.dismiss();
                        }
                        //下次上传
                        saveMineSyncShapeDataNext();
                    }
                });
        loginDialog.show();
    }

    public void onPause()
    {
        if (mViewPager != null)
        {
            int childCount = mViewPager.getChildCount();
            for (int i = 0; i < childCount; i++)
            {
                View childAt = mViewPager.getChildAt(i);
                if (childAt != null && childAt instanceof IFramePager)
                {
                    ((IFramePager) childAt).onPause();
                }
            }
        }
    }

    public void onResume()
    {
        if (mViewPager != null)
        {
            int childCount = mViewPager.getChildCount();
            for (int i = 0; i < childCount; i++)
            {
                View childAt = mViewPager.getChildAt(i);
                if (childAt != null && childAt instanceof IFramePager)
                {
                    ((IFramePager) childAt).onResume();
                }
            }
        }
    }


    public void onClose()
    {
        isClose = true;
        if (mViewPager != null)
        {
            int childCount = mViewPager.getChildCount();
            for (int i = 0; i < childCount; i++)
            {
                View childAt = mViewPager.getChildAt(i);
                if (childAt != null && childAt instanceof IFramePager)
                {
                    ((IFramePager) childAt).onClose();
                }
            }
        }
        sendSensorPageTypeData(mTabUIConfig.getPageType(), mCurrentTabUI, false);
        if (mFilterDownloadListener != null)
        {
            DownloadMgr.getInstance().RemoveDownloadListener(mFilterDownloadListener);
        }
        if (mEventListener != null)
        {
            EventCenter.removeListener(mEventListener);
        }

        saveInfoData();
        mResetView = null;
        mSaveView = null;
        mFilterDownloadListener = null;
        mOnAnimationClickListener = null;
        mEventListener = null;
        mBeautyList = null;
        mShapeList = null;
        mFilterList = null;

    }


    public void onPageResult(int siteID, HashMap<String, Object> params)
    {
        switch (siteID)
        {
            case SiteID.LOGIN:
            case SiteID.REGISTER_DETAIL:
            case SiteID.RESETPSW:
            {
                if (!hasShape) break;
                break;
            }
            case SiteID.FILTER_DETAIL:
            {
                if (hasFilter)
                {
                    //滤镜详情页马上使用
                    if (params != null
                            && params.get("material_id") != null
                            && params.get("material_id") instanceof Integer)
                    {
                        int id = (Integer) params.get("material_id");
                        if (id != 0)
                        {
                            if (mViewPager != null)
                            {
                                View viewWithTag = mViewPager.findViewWithTag(FilterFramePager.FILTER_FRAME_PAGER_TAG);
                                if (viewWithTag != null && viewWithTag instanceof FilterFramePager)
                                {
                                    //通知更新数据，设置指定的滤镜
                                    ((FilterFramePager) viewWithTag).notifyDataChanged();
                                    ((FilterFramePager) viewWithTag).setFilterUri(id, true);

                                }
                            }
                        }
                    }
                }
                break;
            }
            case SiteID.FILTER_DOWNLOAD_MORE:
            {
                if (hasFilter)
                {
                    //滤镜管理
                    if (params != null)
                    {
                        boolean isChange = false;
                        Object o = params.get("is_change");
                        if (o instanceof Boolean)
                        {
                            isChange = (Boolean) o;
                        }

                        if (isChange && mViewPager != null)
                        {
                            View viewWithTag = mViewPager.findViewWithTag(FilterFramePager.FILTER_FRAME_PAGER_TAG);
                            if (viewWithTag != null && viewWithTag instanceof FilterFramePager)
                            {
                                //更新滤镜资源
                                ((FilterFramePager) viewWithTag).notifyDataChanged();

                                if (mCallback != null && mCallback instanceof IFilterPageCallback)
                                {
                                    FilterRes cameraFilterRes = ((IFilterPageCallback) mCallback).getCameraFilterRes();
                                    if (cameraFilterRes != null && !cameraFilterRes.m_isStickerFilter)
                                    {
                                        int[] ints = ((FilterFramePager) viewWithTag).GetSubIndexByUri(cameraFilterRes.m_id);
                                        //滤镜已删除，使用原图滤镜
                                        if (ints == null || ints[0] < 0 || ints[1] < 0)
                                        {
                                            ((FilterFramePager) viewWithTag).cancelFilterUri();
                                            ((FilterFramePager) viewWithTag).showFilterMsgToast(false);
                                            ((FilterFramePager) viewWithTag).setFilterUri(FilterAdapter.OriginalItemInfo.ORIGINAL_ITEM_URI, true);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    break;
                }
            }
        }
    }


    void saveInfoData()
    {
        if (hasBeauty)
        {
            //美颜
            BeautyResMgr.getInstance().SyncSaveSdcardArr(getContext(),
                    BeautyResMgr.getInstance().SyncGetSdcardArr(getContext()));
        }

        if (hasShape)
        {
            //预设脸型数据
            ShapeResMgr.getInstance().SyncSaveSdcardArr(getContext(),
                    ShapeResMgr.getInstance().SyncGetSdcardArr(getContext()));

            //同步脸型数据
            ShapeSyncResMgr.getInstance().SyncSaveSdcardArr(getContext(),
                    ShapeSyncResMgr.getInstance().SyncGetSdcardArr(getContext()));
        }
    }
}
