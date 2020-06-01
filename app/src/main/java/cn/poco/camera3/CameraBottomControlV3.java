package cn.poco.camera3;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.Region;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.Observable;

import cn.poco.advanced.ImageUtils;
import cn.poco.camera.CameraConfig;
import cn.poco.camera2.CameraFilterListIndexs;
import cn.poco.camera2.CameraFilterRecyclerView;
import cn.poco.camera3.beauty.BeautySelectorView;
import cn.poco.camera3.beauty.STag;
import cn.poco.camera3.beauty.TabUIConfig;
import cn.poco.camera3.beauty.callback.PageCallbackAdapter;
import cn.poco.camera3.beauty.data.BeautyData;
import cn.poco.camera3.beauty.data.ShapeData;
import cn.poco.camera3.beauty.recycler.ShapeExAdapter;
import cn.poco.camera3.cb.CameraFilterListener;
import cn.poco.camera3.cb.CameraPageListener;
import cn.poco.camera3.cb.ControlUIListener;
import cn.poco.camera3.cb.ShutterAnimListener;
import cn.poco.camera3.cb.UIObservable;
import cn.poco.camera3.cb.UIObserver;
import cn.poco.camera3.cb.sticker.StickerUIListener;
import cn.poco.camera3.config.CameraUIConfig;
import cn.poco.camera3.config.shutter.ShutterConfig;
import cn.poco.camera3.mgr.CameraSettingMgr;
import cn.poco.camera3.mgr.StickerLocalMgr;
import cn.poco.camera3.mgr.StickerResMgr;
import cn.poco.camera3.ui.shutter.RecordTextView;
import cn.poco.camera3.ui.shutter.ShutterView;
import cn.poco.camera3.ui.sticker.StickerLayout;
import cn.poco.camera3.ui.sticker.local.StickerMgrPage;
import cn.poco.camera3.ui.tab.HorizontalScrollLayout;
import cn.poco.camera3.ui.tab.TabView;
import cn.poco.camera3.ui.tab.TabViewAdapter;
import cn.poco.camera3.ui.tab.TabViewBaseAdapter;
import cn.poco.camera3.util.CameraPercentUtil;
import cn.poco.camera3.util.RatioBgUtils;
import cn.poco.filter4.FilterResMgr;
import cn.poco.filter4.recycle.FilterAdapter;
import cn.poco.home.home4.utils.PercentUtil;
import cn.poco.resource.FilterRes;
import cn.poco.resource.VideoStickerRes;
import cn.poco.statistics.MyBeautyStat;
import cn.poco.statistics.TongJi2;
import cn.poco.tianutils.ShareData;
import my.beautyCamera.R;

public class CameraBottomControlV3 extends FrameLayout implements ControlUIListener, UIObserver
{
    private final float mFullScreenRatio;
    // 控制btn
    public CameraControlBtnLayout mControlLayout;
    // 素材
    public StickerLayout mStickerView;
    // 快门
    public ShutterView mShutterBtn;
    private RecordTextView mRecordTextBtn; // 录制时长 btn

    public HorizontalScrollLayout mScrollView;
    private TabViewAdapter mScrollViewAdapter;

    // 滤镜
    public CameraFilterRecyclerView mCameraFilterRecyclerView;
    protected ArrayList<FilterAdapter.ItemInfo> mCameraFilterItemArr;
    protected CameraFilterRecyclerView.OnItemClick mCameraFilterCB;
    public CameraFilterListIndexs mCameraFilterListIndexs;
    protected int mFilterResUri = -1;
    public int mFilterResPosition = -1;
    private boolean mIsRepeatClickFilter = false;
    private boolean isUsedStickerFilter;
    protected CameraFilterListener mCameraFilterListener;

    // Pop区域
    public FrameLayout mPopFrameView;
    public StickerMgrPage mStickerManagerPage;

    //美颜、脸型
    public BeautySelectorView mBeautySelectorView;
    private PageCallbackAdapter mInnerBeautySelectorCB;
    private PageCallbackAdapter mBeautySelectorCB;

    @ShutterConfig.TabType
    public int mType;
    private int mLastShutterType;
    public int mShutterType;
    private float mCurrentRatio;

    private long mAnimDuration = 300; // 动画统一持续时间

    private boolean mDoingAnim = false;
    private boolean isMgrPage = false;
    private boolean mIsInitPage = false;
    private boolean mIsIntercept = false;

    private int mControlTransY;
    private int mStickerTransY;
    private int mFilterTransY;
    private int mBeautyTransY;
    private int mScrollTransY;
    private int mDurationBtnDefTransY;

    // 通讯
    private Handler mUIHandler;
    private UIObservable mUIObserverList;

    private AnimatorSet mSet;

    public CameraBottomControlV3(@NonNull Context context)
    {
        super(context);
        mIsInitPage = true;
        mFullScreenRatio = ShareData.m_screenRealHeight * 1.0f / ShareData.m_screenRealWidth;
        mControlTransY = ShareData.m_screenRealHeight;
        mScrollTransY = ShareData.m_screenRealHeight;
        mStickerTransY = CameraPercentUtil.HeightPxToPercent(600);
        mFilterTransY = CameraPercentUtil.HeightPxToPercent(380);
        mDurationBtnDefTransY = CameraPercentUtil.WidthPxToPercent(296);
        mBeautyTransY = CameraPercentUtil.HeightPxToPercent(360 + 30 + 80);

        initHandler();
        initData();
        initView();
    }

    private void initHandler()
    {
        mUIHandler = new Handler(Looper.getMainLooper())
        {
            @Override
            public void handleMessage(Message msg)
            {
                switch (msg.what)
                {
                    case UIObserver.MSG_UNLOCK_UI:
                    {
                        if (mUIObserverList != null)
                        {
                            mUIObserverList.notifyObservers(UIObserver.MSG_UNLOCK_UI);
                        }
                    }
                }
            }
        };
    }

    private void initData()
    {
        initFilterData();
    }

    public void initFilterData()
    {
        mCameraFilterItemArr = FilterResMgr.GetFilterRes(getContext(), true, false);
        mCameraFilterCB = initCameraFilterCallback();
        initFilterPositionLists(mCameraFilterItemArr);
    }

    private void initView()
    {
        mControlLayout = new CameraControlBtnLayout(getContext());
        mControlLayout.setControlUIListener(this);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.gravity = Gravity.BOTTOM;
        this.addView(mControlLayout, params);

        // 素材列表
        mStickerView = new StickerLayout(getContext());
        mStickerView.setTranslationY(mStickerTransY);
        mStickerView.setClickable(true);
        mStickerView.setStickerUIListener(mStickerUIController);
        params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.BOTTOM;
        addView(mStickerView, params);

        // 快门
        mShutterBtn = new ShutterView(getContext());
        mShutterBtn.setOnClickListener(mClickListener);
        mShutterBtn.SetShutterAnimListener(mShutterAnimListener);
        params = new LayoutParams(PercentUtil.WidthPxToPercent(220), PercentUtil.HeightPxToPercent(380));
        params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        this.addView(mShutterBtn, params);

        // 底部滑动标签
        mScrollView = new HorizontalScrollLayout(getContext());
        mScrollView.SetOnItemScrollStateListener(mScrollViewItemScrollStateListener);
        params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.BOTTOM;
        this.addView(mScrollView, params);

        mRecordTextBtn = new RecordTextView(getContext());
        mRecordTextBtn.setVisibility(GONE);
        params = new LayoutParams(CameraPercentUtil.WidthPxToPercent(300), CameraPercentUtil.HeightPxToPercent(70));
        params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        params.bottomMargin = mDurationBtnDefTransY;
        addView(mRecordTextBtn, params);

        //滤镜
        mCameraFilterRecyclerView = new CameraFilterRecyclerView(getContext());
        mCameraFilterRecyclerView.setMaskFrameViewBGColor(0xfff0f0f0);
        mCameraFilterRecyclerView.setItemClickCallback(mCameraFilterCB);
        mCameraFilterRecyclerView.InitData(mCameraFilterItemArr);
        mCameraFilterRecyclerView.setTranslationY(mFilterTransY);
        mCameraFilterRecyclerView.SetPopFrameView(mPopFrameView);
        params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, CameraPercentUtil.HeightPxToPercent(320));//232 + 88
        params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
        this.addView(mCameraFilterRecyclerView, params);

        TabUIConfig.Builder builder = new TabUIConfig.Builder();
        builder.addTabType(TabUIConfig.TAB_TYPE.TAB_BEAUTY | TabUIConfig.TAB_TYPE.TAB_SHAPE);
        builder.setPageType(TabUIConfig.PAGE_TYPE.PAGE_CAMERA);
        builder.setViewType(TabUIConfig.VIEW_TYPE_ALL);
        builder.setCurrentTabType(TabUIConfig.TAB_TYPE.TAB_SHAPE);
        TabUIConfig config = builder.build(getContext());
        mBeautySelectorView = new BeautySelectorView(getContext(), config, initBeautySelectorCB());
        mBeautySelectorView.setClickable(true);
        mBeautySelectorView.setTranslationY(mBeautyTransY);
        params = new LayoutParams(LayoutParams.MATCH_PARENT, mBeautyTransY);
        params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
        this.addView(mBeautySelectorView, params);
    }

    private PageCallbackAdapter initBeautySelectorCB() {
        if (mInnerBeautySelectorCB == null)
        {
            mInnerBeautySelectorCB = new PageCallbackAdapter()
            {
                @Override
                public void onLogin()
                {
                    if (mBeautySelectorCB != null) mBeautySelectorCB.onLogin();
                }

                @Override
                public void onBindPhone()
                {
                    if (mBeautySelectorCB != null) mBeautySelectorCB.onBindPhone();
                }

                @Override
                public void setShowSelectorView(boolean show)
                {
                    //通过监听动画事件，回传callback
                    if (show && !isBeautySelectorShowed()) {
                        OpenBeautySelector(true);
                    } else if (!show && isBeautySelectorShowed()) {
                        CloseBeautySelector();
                    }
                }

                //脸型数据回调
                @Override
                public void onShapeUpdate(int subPosition, @Nullable ShapeExAdapter.ShapeExItemInfo itemInfo, ShapeData data)
                {
                    if (mBeautySelectorCB != null) mBeautySelectorCB.onShapeUpdate(subPosition, itemInfo, data);
                }
                @Override
                public void onShapeItemClick(@Nullable ShapeExAdapter.ShapeExItemInfo itemInfo, ShapeData data)
                {
                    if (mBeautySelectorCB != null) mBeautySelectorCB.onShapeItemClick(itemInfo, data);
                }
                @Override
                public void onBeautyUpdate(@STag.BeautyTag int type,  BeautyData beautyData)
                {
                    if (mBeautySelectorCB != null) mBeautySelectorCB.onBeautyUpdate(type, beautyData);
                }


            };
        }
        return mInnerBeautySelectorCB;
    }

    public void setPopView(FrameLayout popView)
    {
        if (popView != null)
        {
            mPopFrameView = popView;

            if (mCameraFilterRecyclerView != null)
            {
                mCameraFilterRecyclerView.SetPopFrameView(mPopFrameView);
            }
            if (mStickerView != null)
            {
                mStickerView.SetPopFrameView(mPopFrameView);
            }
        }
    }

    /**
     * 更新镜头滤镜数据
     */
    public void updateCameraFilterItemList()
    {
        initFilterData();
        if (mCameraFilterRecyclerView != null)
        {
            mCameraFilterRecyclerView.setItemClickCallback(mCameraFilterCB);
            mCameraFilterRecyclerView.InitData(mCameraFilterItemArr);
        }
    }

    public void downloadCameraFilterItemList()
    {
        initFilterData();

        if (mCameraFilterRecyclerView != null)
        {
            mCameraFilterRecyclerView.setItemClickCallback(mCameraFilterCB);
            mCameraFilterRecyclerView.InitData(mCameraFilterItemArr);

            //重新选择上次的uri
            mFilterResPosition = GetPositionByFilterId(mFilterResUri);
            setFilterIndex(mFilterResPosition);
        }
    }

    //构造滤镜数据下标集
    private void initFilterPositionLists(ArrayList<FilterAdapter.ItemInfo> list)
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

    public void setTabType(@ShutterConfig.TabType int type)
    {
        mType = type;

        if (mShutterBtn != null)
        {
            mShutterBtn.setTabType(type);
        }
        if (mControlLayout != null)
        {
            mControlLayout.setTabType(type);
        }
    }

    public float getRatio()
    {
        return mCurrentRatio;
    }

    public void setPreviewRatio(float ratio)
    {
        mCurrentRatio = ratio;

        if (mShutterBtn != null)
        {
            mShutterBtn.setPreviewRatio(ratio);
        }
        if (mControlLayout != null)
        {
            mControlLayout.setPreviewRatio(ratio);
        }
    }

    public void setShutterMode(int mode)
    {
        mShutterType = mode;

        if (mShutterBtn != null)
        {
            mShutterBtn.setMode(mode);
        }
        if (mControlLayout != null)
        {
            mControlLayout.setShutterMode(mode);
        }
    }

    /**
     * 将父布局的事件直接传到快门处理
     *
     * @param event 父布局的事件
     */
    public void passParentEventToShutter(MotionEvent event)
    {
        if (mShutterBtn == null) return;

        float x = event.getX() - getLeft() - mShutterBtn.getLeft();
        float y = event.getY() - getTop() - mShutterBtn.getTop();
        MotionEvent shutterEvent = MotionEvent.obtain(event.getDownTime(), event.getEventTime(), event.getAction(), x, y, event.getMetaState());
        mShutterBtn.dispatchTouchEvent(shutterEvent);
    }

    public boolean isTouchShutter(MotionEvent event)
    {
        if (mShutterBtn != null)
        {
            Region region = mShutterBtn.getCircleLocationInShutter();
            int x = (int) (event.getX() - getLeft() - mShutterBtn.getLeft());
            int y = (int) (event.getY() - getTop() - mShutterBtn.getTop());
            if (region.contains(x, y))
            {
                return true;
            }
        }

        return false;
    }

    public boolean isPauseRecording()
    {
        return mType == ShutterConfig.TabType.VIDEO && mShutterType == ShutterConfig.ShutterType.PAUSE_RECORD;
    }

    /**
     * 根据 tab type ，镜头预览比例 设置 ui 样式 <br/>
     * 如果 tab type 、预览比例ratio 有改变, 需要先调用<br/>
     * {@link CameraBottomControlV3#setTabType(int)}<br/>
     * {@link CameraBottomControlV3#setPreviewRatio(float)}
     */
    public void updateUI()
    {
        float ratio = mCurrentRatio;

        if (mControlLayout != null)
        {
            mControlLayout.updateUI();
        }

        if (!mIsInitPage)
        {
            mShutterBtn.updateShutter();
        }

        if (mScrollViewAdapter != null)
        {
            mScrollViewAdapter.setCurrPreviewRatio(ratio);
            mScrollViewAdapter.notifyDateChange();
        }

        updateRecordTextBtnLoc();
    }

    private void updateRecordTextBtnLoc()
    {
        if (mRecordTextBtn != null)
        {
            if (mCurrentRatio == CameraConfig.PreviewRatio.Ratio_4_3)
            {
                mRecordTextBtn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13, true);
                mRecordTextBtn.setTextColor(Color.WHITE);
                mRecordTextBtn.setShadowLayer(CameraPercentUtil.WidthPxToPercent(2), 0, CameraPercentUtil.WidthPxToPercent(2), 0x0D000000);

                int transY = mDurationBtnDefTransY - CameraPercentUtil.WidthPxToPercent(3) - RatioBgUtils.GetBottomHeightByRation(mCurrentRatio);

                int bottomPadding = 0;
                if (mFullScreenRatio > CameraConfig.PreviewRatio.Ratio_16_9)
                {
                    int mCameraViewHeight = (int) (ShareData.getScreenW() * CameraConfig.PreviewRatio.Ratio_16_9);

                    if (mCameraViewHeight > ShareData.m_screenRealHeight)
                    {
                        mCameraViewHeight = (int) (ShareData.getScreenW() * CameraConfig.PreviewRatio.Ratio_4_3);
                    }

                    if (mFullScreenRatio > CameraConfig.PreviewRatio.Ratio_17$25_9)
                    {
                        bottomPadding = ShareData.m_screenRealHeight - mCameraViewHeight - RatioBgUtils.getTopPaddingHeight(mCurrentRatio);
                    }
                    else
                    {
                        bottomPadding = ShareData.m_screenRealHeight - mCameraViewHeight;
                    }
                }
                transY += bottomPadding;
                mRecordTextBtn.setTranslationY(transY);
            }
            else
            {
                if (mCurrentRatio == CameraConfig.PreviewRatio.Ratio_1_1 || mCurrentRatio == CameraConfig.PreviewRatio.Ratio_9_16)
                {
                    mRecordTextBtn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13, false);
                    mRecordTextBtn.setTextColor(ImageUtils.GetSkinColor());
                    mRecordTextBtn.setShadowLayer(0, 0, 0, Color.TRANSPARENT);
                }
                else
                {
                    mRecordTextBtn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13, true);
                    mRecordTextBtn.setTextColor(Color.WHITE);
                    mRecordTextBtn.setShadowLayer(CameraPercentUtil.WidthPxToPercent(2), 0, CameraPercentUtil.WidthPxToPercent(2), 0x0D000000);
                }
                mRecordTextBtn.setTranslationY(0);
            }
            mRecordTextBtn.updateUI();
        }
    }

    public void SetUIObserver(@NonNull UIObservable uiObserver)
    {
        mUIObserverList = uiObserver;
        mUIObserverList.addObserver(this);

        if (mControlLayout != null)
        {
            mControlLayout.setUIObserver(uiObserver);
        }
    }

    public PointF GetBeautySettingLoc()
    {
        int[] loc = new int[2];
        if (mControlLayout != null && mControlLayout.mBeautyBtn != null)
        {
            mControlLayout.mBeautyBtn.getLocationInWindow(loc);
            loc[0] += mControlLayout.mBeautyBtn.getMeasuredWidth() / 2f;
            loc[1] += mControlLayout.mBeautyBtn.getMeasuredHeight() / 2f;
        }
        return new PointF(loc[0], loc[1]);
    }

    public boolean isColorFilterShowed()
    {
        return mCameraFilterRecyclerView != null && mCameraFilterRecyclerView.getVisibility() == VISIBLE && mCameraFilterRecyclerView.getTranslationY() == 0;
    }

    public boolean isStickerListShowed()
    {
        return mStickerView != null && mStickerView.getVisibility() == VISIBLE && mStickerView.getTranslationY() == 0;
    }

    private boolean isShowStickerMgrPage()
    {
        return mStickerManagerPage != null;
    }

    private boolean isBeautySelectorShowed()
    {
        return mBeautySelectorView != null && mBeautySelectorView.getVisibility() == VISIBLE && mBeautySelectorView.getTranslationY() == 0;
    }

    public boolean IsOpenOtherPage()
    {
        return isShowStickerMgrPage() || isColorFilterShowed() || isStickerListShowed() || isBeautySelectorShowed();
    }

    public void handleRecordingStatusInPause()
    {
        if (!IsDoingAnim() && mShutterBtn != null && !mShutterBtn.isCancelAnim() && mShutterType != ShutterConfig.ShutterType.RECORDING)
        {
            return;
        }

        int mode = ShutterConfig.ShutterType.DEF;

        if (mShutterBtn != null)
        {
            if (mShutterBtn.getVideoSize() > 0)
            {
                mode = ShutterConfig.ShutterType.PAUSE_RECORD;
                if (mUIObserverList != null)
                {
                    mUIObserverList.notifyObservers(UIObserver.MSG_ONLY_SHOW_CAMERA_SWITCH);
                }
            }
            else
            {
                if (mUIObserverList != null)
                {
                    mUIObserverList.notifyObservers(UIObserver.MSG_SHOW_TOP_ALL_UI);
                }

                if (mRecordTextBtn != null)
                {
                    mRecordTextBtn.setVisibility(GONE);
                }
            }
            mShutterBtn.clearCurrentProgress();
            setShutterMode(mode);
        }

        if (mControlLayout != null)
        {
            mControlLayout.setTranslationY(0);
            mControlLayout.setVisibility(VISIBLE);
            mControlLayout.setAlpha(1);
            mControlLayout.setBtnClickable(true);
        }
        if (mScrollView != null && mode != ShutterConfig.ShutterType.PAUSE_RECORD)
        {
            mScrollView.setTranslationY(0);
            mScrollView.setVisibility(VISIBLE);
            mScrollView.setAlpha(1);
        }
        if (mStickerView != null)
        {
            mStickerView.setTranslationY(mStickerTransY);
        }
        updateUI();
    }

    public void setIsDrawPauseLogo(boolean isDraw)
    {
        if (mShutterBtn != null)
        {
            mShutterBtn.setIsDrawPauseLogo(isDraw);
        }
    }

    /**
     * 绘制录制进度 和 进度文本
     *
     * @param progress 0~360°
     * @param text     文本
     */
    public void setRecordProgressAndText(int progress, String text, int uiEnable)
    {
        if (mShutterBtn != null)
        {
            mShutterBtn.setUIEnable(uiEnable);
            mShutterBtn.setProgress(progress);
        }
//        mShutterBtn.setRecordTimeText(text);
        if (mRecordTextBtn != null)
        {
            mRecordTextBtn.setVisibility(VISIBLE);
            mRecordTextBtn.setText(text);
            mRecordTextBtn.updateUI();
        }
    }

    /**
     * 设置录制进入和进度文本，保持上一个状态，用于最后一秒录制的设定
     * @param progress
     * @param text
     */
    public void setRecordProgressAndText(int progress, String text) {
        if (mShutterBtn != null) {
            mShutterBtn.setProgress(progress);
        }
        if (mRecordTextBtn != null)
        {
            if (!mDoingAnim && !isColorFilterShowed() && !isStickerListShowed())
            {
                mRecordTextBtn.setVisibility(VISIBLE);
            }
            mRecordTextBtn.setText(text);
            mRecordTextBtn.updateUI();
        }
    }

    public void setShutterEnable(int uiEnable)
    {
        mShutterBtn.setUIEnable(uiEnable);
        mShutterBtn.UpDateUI();
    }

    public int getShutterEnable() {
        return mShutterBtn.getUIEnable();
    }

    public void checkVideoSize(int size)
    {
        if (mShutterBtn != null)
        {
            mShutterBtn.checkVideoSize(size);
        }
    }

    public void setRecordTimeText(String text)
    {
        if (mRecordTextBtn != null)
        {
            if(!mDoingAnim && !isColorFilterShowed() && !isStickerListShowed())
            {
                mRecordTextBtn.setVisibility(VISIBLE);
            }
            mRecordTextBtn.setText(text);
            mRecordTextBtn.updateUI();
        }
    }

    protected void initStickerMgrPage()
    {
        if (mStickerManagerPage == null)
        {
            mStickerManagerPage = new StickerMgrPage(getContext());
            mStickerManagerPage.setStickerUIListener(mStickerUIController);
            LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            mStickerManagerPage.setClickable(true);
            if (mPopFrameView != null) mPopFrameView.addView(mStickerManagerPage, params);

            /*素材管理创建 埋点*/
            int clickResId = -1;
            switch (mType)
            {
                case ShutterConfig.TabType.GIF:
                    clickResId = R.string.拍照_表情包拍摄_素材管理;
                    break;
                case ShutterConfig.TabType.CUTE:
                    clickResId = R.string.拍照_萌妆照拍摄页_素材管理;
                    break;
                case ShutterConfig.TabType.VIDEO:
                    clickResId = R.string.拍照_视频拍摄页_素材管理;
                    break;
            }
            if (clickResId != -1)
            {
                MyBeautyStat.onPageStartByRes(clickResId);
            }
        }
    }

    public void UpdateCredit()
    {
        if (mStickerView != null)
        {
            mStickerView.UpdateCredit();
        }

        if (mCameraFilterRecyclerView != null)
        {
            mCameraFilterRecyclerView.updateCredit();
        }
    }

    public boolean isShowUnlockView()
    {
        if (mCameraFilterRecyclerView != null)
        {
            return mCameraFilterRecyclerView.isShowRecomView();
        }

        return mStickerView != null && mStickerView.isShowUnlockView();
    }

    public void updateVideoDuration(int value)
    {
        if (mShutterBtn != null)
        {
            mShutterBtn.updateVideoDuration(value);
        }
    }

    /**
     * 初始化UI时的 config
     *
     * @param config {@link CameraUIConfig}
     */
    public void setUIConfig(CameraUIConfig config)
    {
        setTabType(config.GetSelectedType());
        setShutterMode(config.GetShutterMode());
        setPreviewRatio(config.GetPreviewRatio());

        initScrollViewAdapter(config);

        if (mShutterBtn != null)
        {
            mShutterBtn.setUIConfig(config);
        }

        if (mControlLayout != null)
        {
            mControlLayout.updateUI(config);
        }

        if (mStickerView != null)
        {
            mStickerView.setUIConfig(config);
        }

        if (mShutterType == ShutterConfig.ShutterType.UNFOLD_RES)
        {
            ShowStickerListWithoutAnim();
        }

        if (mShutterType == ShutterConfig.ShutterType.PAUSE_RECORD && mRecordTextBtn != null)
        {
            Object obj = config.GetResetVideoPauseStatusInfo();
            if (obj != null && obj instanceof VideoMgr)
            {
                mRecordTextBtn.setVisibility(VISIBLE);
                // 这里是预览预览页面回来时更新数据
                // 如果需要处理最后一秒的状态并且剩余录制时间小于1秒，直接设置为目标时长
                if (((VideoMgr) obj).processLaseSecond()
                        && ((VideoMgr) obj).getTargetDuration() - ((VideoMgr) obj).getRecordDuration() < 1000)
                {
                    mRecordTextBtn.setText(((VideoMgr) obj).getDurationStr(
                            ((VideoMgr) obj).getTargetDuration()));
                } else // 如果剩余录制时长还大于1秒，则设置为实际录制的时长，并且设置为可录制状态
                {
                    mRecordTextBtn.setText(((VideoMgr) obj).getDurationStr());
                    setShutterEnable(ShutterConfig.RecordStatus.CAN_RECORDED);
                }
            }
        }

        updateRecordTextBtnLoc();

        mIsInitPage = false;
    }

    public int GetShutterMode()
    {
        return mShutterBtn != null ? mShutterBtn.GetMode() : -1;
    }

    private void ShowStickerListWithoutAnim()
    {
        if (mStickerView != null)
        {
            /*素材列表创建 页面埋点*/
            int clickResId = -1;
            switch (mType)
            {
                case ShutterConfig.TabType.GIF:
                    clickResId = R.string.拍照_表情包拍摄_素材列表;
                    break;
                case ShutterConfig.TabType.CUTE:
                    clickResId = R.string.拍照_萌妆照拍摄页_素材列表;
                    break;
                case ShutterConfig.TabType.VIDEO:
                    clickResId = R.string.拍照_视频拍摄页_素材列表;
                    break;
            }
            if (clickResId != -1)
            {
                MyBeautyStat.onPageStartByRes(clickResId);
            }

            mStickerView.setTranslationY(0);
            mStickerView.setVisibility(VISIBLE);
        }
        if (mControlLayout != null)
        {
            mControlLayout.setTranslationY(mControlTransY);
        }
        if (mScrollView != null)
        {
            mScrollView.setTranslationY(mScrollTransY);
        }
    }

    /**
     * 初始化底部滑动标签
     */
    private void initScrollViewAdapter(CameraUIConfig config)
    {
        int size = config.GetTabSize();

        if (size > 1)
        {
            mScrollViewAdapter = new TabViewAdapter(config);
            mScrollViewAdapter.setItemClickListener(mScrollViewItemClickListener);
            mScrollViewAdapter.setSelIndex(config.GetSelectedIndex());

            mScrollView.SetOriginallySelectedIndex(config.GetSelectedIndex());
            mScrollView.SelectCurrentIndex(config.GetSelectedIndex());
            mScrollView.SetAdapter(mScrollViewAdapter);
            mScrollView.setVisibility(mShutterType != ShutterConfig.ShutterType.PAUSE_RECORD ? View.VISIBLE : GONE);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev)
    {
        return mIsIntercept || super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        return mIsIntercept || super.onTouchEvent(event);
    }

    public boolean handleRecordTimerEndEvent()
    {
        if (IsDoingAnim()) return true;

        switch (mType)
        {
            case ShutterConfig.TabType.GIF:
            {
                TongJi2.AddCountByRes(getContext(), R.integer.拍照_表情包拍摄_拍摄按钮);
                MyBeautyStat.onClickByRes(R.string.拍照_表情包拍摄_主页面_开始拍摄);
                break;
            }

            case ShutterConfig.TabType.VIDEO:
            {
                if (mShutterBtn != null)
                {
                    int video_size = mShutterBtn.getVideoSize();
                    if (video_size > 0)
                    {
                        MyBeautyStat.onClickByRes(R.string.拍照_视频拍摄页_主页面_继续拍摄);
                    }
                    else
                    {
                        TongJi2.AddCountByRes(getContext(), R.integer.拍照_视频拍摄_拍摄开始);
                        MyBeautyStat.onClickByRes(R.string.拍照_视频拍摄页_主页面_开始拍摄);
                    }
                    break;
                }
            }
        }

        showRecordAnimator();
        return true;
    }

    public boolean handleTouchModeEvent()
    {
        if (isBeautySelectorShowed() || isShowStickerMgrPage() || IsDoingAnim()) return true;

        lockUI();
        boolean canContinue = true;
        if(mType == ShutterConfig.TabType.VIDEO || mType == ShutterConfig.TabType.GIF)
        {
            if(mCameraPageController != null)
            {
                switch(mShutterType)
                {
                    case ShutterConfig.ShutterType.RECORDING:
                    {
                        canContinue = mCameraPageController.canPauseRecord();
                        break;
                    }

                    default:
                    {
                        canContinue = mCameraPageController.canRecord();
                        break;
                    }
                }
            }
        }
        if(canContinue)
        {
            checkTypeBe4ShutterClick();
        }
        else
        {
            unlockUI();
        }
        return true;
    }

    public int GetPositionByFilterId(int id)
    {
        if (mCameraFilterListIndexs != null)
        {
            return mCameraFilterListIndexs.getPositionByFilterId(id);
        }
        return -1;
    }

    public int GetFilterResPosition()
    {
        return mFilterResPosition;
    }

    public int GetCameraFilterResListSize()
    {
        return mCameraFilterListIndexs != null ? mCameraFilterListIndexs.getSize() : 0;
    }

    /**
     * 镜头手势滑动滤镜
     *
     * @param next
     */
    public void setCameraFilterNext(boolean next)
    {
        int currentPosition = GetFilterResPosition();
        if (next)
        {
            currentPosition++;
        }
        else
        {
            currentPosition--;
        }
        int size = GetCameraFilterResListSize();
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
            int clickResId = -1;
            switch (mType)
            {
                case ShutterConfig.TabType.GIF:
                    clickResId = next ? R.string.拍照_表情包拍摄_主页面_左滑切换滤镜 : R.string.拍照_表情包拍摄_主页面_右滑切换滤镜;
                    break;
                case ShutterConfig.TabType.PHOTO:
                    clickResId = next ? R.string.拍照_美颜拍照_主页面_左滑切换滤镜 : R.string.拍照_美颜拍照_主页面_右滑切换滤镜;
                    break;
                case ShutterConfig.TabType.CUTE:
                    clickResId = next ? R.string.拍照_萌妆照拍摄页_主页面_左滑切换滤镜 : R.string.拍照_萌妆照拍摄页_主页面_右滑切换滤镜;
                    break;
                case ShutterConfig.TabType.VIDEO:
                    clickResId = next ? R.string.拍照_视频拍摄页_主页面_左滑切换滤镜 : R.string.拍照_视频拍摄页_主页面_右滑切换滤镜;
                    break;
            }
            if (clickResId != -1)
            {
                MyBeautyStat.onClickByRes(clickResId);
            }
        }
    }


    public void setFilterIndex(int index)
    {
        if (mCameraFilterListIndexs != null)
        {
            int uri = mCameraFilterListIndexs.setFilterIndex(index);
            if (uri != -1)
            {
                setFilterUri(uri);
            }
        }
    }

    public int getFilterUri()
    {
        return mFilterResUri;
    }

    public void setFilterUri(int uri)
    {
        setFilterUri(uri, true);
    }

    public void setFilterUri(int uri, boolean isCallbackClick)
    {
        setFilterUri(uri, isCallbackClick, false);
    }

    public void setFilterScrollToGroupByUri(int groupUri) {
        if (mCameraFilterRecyclerView != null) {
            mCameraFilterRecyclerView.scrollToGroupByUri(groupUri);
        }
    }

    /**
     * @param uri
     * @param isCallbackClick
     * @param isRepeatCallbackClick 是否再次点击同一个滤镜id
     */
    public void setFilterUri(int uri, boolean isCallbackClick, boolean isRepeatCallbackClick)
    {
        this.mIsRepeatClickFilter = isRepeatCallbackClick;

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
            mFilterResPosition = GetPositionByFilterId(mFilterResUri);
        }

        if (mCameraFilterRecyclerView != null)
        {
            mCameraFilterRecyclerView.SetSelUri(uri, isCallbackClick);
        }
    }

    public void setShowFilterMsgToast(boolean show) {
        if (mCameraFilterRecyclerView != null) {
            mCameraFilterRecyclerView.showFilterMsgToast(show);
        }
    }

    //取消选择列表滤镜选择
    public void cancelFilterUri()
    {
        mIsRepeatClickFilter = false;
        mFilterResUri = -1;
        mFilterResPosition = -1;
        mCameraFilterRecyclerView.mFilterAdapter.CancelSelect();
        mCameraFilterRecyclerView.mFilterAdapter.setOpenIndex(-1);
        mCameraFilterRecyclerView.mFilterAdapter.scrollToCenterByIndex(0);
    }

    //************************************** 监听 *********************************************//

    //构造滤镜控件操作回调监听
    private CameraFilterRecyclerView.OnItemClick initCameraFilterCallback()
    {
        if (mCameraFilterCB == null)
        {
            mCameraFilterCB = new CameraFilterRecyclerView.OnItemClick()
            {
                @Override
                public void onItemClick(FilterRes filterRes, boolean isShowFilterMsgToast)
                {
                    if (filterRes != null && (mFilterResUri != filterRes.m_id || isUsedStickerFilter || mIsRepeatClickFilter))
                    {
                        mIsRepeatClickFilter = false;
                        isUsedStickerFilter = false;
                        mFilterResUri = filterRes.m_id;
                        mFilterResPosition = GetPositionByFilterId(mFilterResUri);

                        if (mCameraFilterListener != null)
                        {
                            mCameraFilterListener.onItemClickFilterRes(filterRes, isShowFilterMsgToast);
                        }
                    }
                }

                @Override
                public void onItemDownload()
                {
                    if (mCameraFilterListener != null)
                    {
                        mCameraFilterListener.onItemClickFilterDownloadMore();
                    }
                }

                @Override
                public void foldItemList(boolean isDown)
                {
                    if (isDown) CloseFilterList();
                }
            };
        }
        return mCameraFilterCB;
    }

    private TabViewBaseAdapter.OnItemClickListener mScrollViewItemClickListener = new TabViewBaseAdapter.OnItemClickListener()
    {
        @Override
        public void onItemClick(int position)
        {
            if (mScrollView != null && mScrollView.GetCurrentSelectedIndex() != position)
            {
                if (mCameraPageController != null && mCameraPageController.isCountDown())
                {
                    mCameraPageController.onCancelCountDown();
                }

                lockUI();
                mScrollView.SmoothToPosition(position);
            }
        }
    };

    // 底部滚动标签的监听
    private TabView.OnItemScrollStateListener mScrollViewItemScrollStateListener = new TabView.OnItemScrollStateListener()
    {
        @Override
        public void onFingerMove()
        {
            if (mCameraPageController != null && mCameraPageController.isCountDown())
            {
                mCameraPageController.onCancelCountDown();
            }
        }

        @Override
        public void onItemSelected(int currSelIndex, int type)
        {
            lockUI();
            updateTabSelIndex(currSelIndex);

            if (mCameraPageController != null)
            {
                mCameraPageController.onTabTypeChange(type);
            }
            unlockUI(1000);
        }

        @Override
        public void onScrollStart()
        {
            lockUI();
        }

        @Override
        public void onScrollEnd()
        {
            unlockUI();
        }

        @Override
        public void onLessThan18SDK(int type)
        {
            if (mCameraPageController != null)
            {
                mCameraPageController.onShowLessThan18SDKTips(type);
            }
        }
    };

    private void updateTabSelIndex(int currSelIndex)
    {
        if (mScrollViewAdapter != null)
        {
            mScrollViewAdapter.setSelIndex(currSelIndex);
        }
    }

    private void unlockUI()
    {
        unlockUI(0);
    }

    private void unlockUI(long delay)
    {
        if (mUIHandler != null)
        {
            if (delay == 0)
            {
                mUIHandler.sendEmptyMessage(UIObserver.MSG_UNLOCK_UI);
            }
            else
            {
                mUIHandler.sendEmptyMessageDelayed(UIObserver.MSG_UNLOCK_UI, delay);
            }
        }
    }

    private void lockUI()
    {
        if (mUIObserverList != null)
        {
            mUIObserverList.notifyObservers(UIObserver.MSG_LOCK_UI);
        }
    }

    // 单击处理
    protected OnClickListener mClickListener = new OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            if (mScrollView.isScrollStated() || IsDoingAnim())
            {
                return;
            }

            lockUI();
            if (v == mShutterBtn)
            {
                checkTypeBe4ShutterClick();
            }
        }
    };

    private void checkTypeBe4ShutterClick()
    {
        if (mCameraPageController != null && mCameraPageController.isCountDown())
        {
            mCameraPageController.onCancelCountDown();
            unlockUI(300);
            return;
        }

        int timer = CameraSettingMgr.getCurrentTimerMode();
        int mode = mShutterType;
        switch (mType)
        {
            case ShutterConfig.TabType.GIF:
            {
                if (mode == ShutterConfig.ShutterType.DEF || mode == ShutterConfig.ShutterType.UNFOLD_RES)
                {
                    if (mCameraPageController != null)
                    {
                        if (mCameraPageController.canRecord())
                        {
                            if (timer == CameraConfig.CaptureMode.Manual)
                            {
                                TongJi2.AddCountByRes(getContext(), R.integer.拍照_表情包拍摄_拍摄按钮);
                                MyBeautyStat.onClickByRes(R.string.拍照_表情包拍摄_主页面_开始拍摄);
                                showRecordAnimator();
                            }
                            else
                            {
                                mCameraPageController.onTiming();
                                unlockUI(300);
                            }
                        }
                        else
                        {
                            unlockUI(100);
                        }
                    }
                }
                else if (mode == ShutterConfig.ShutterType.RECORDING)
                {
                    if (mCameraPageController != null)
                    {
                        MyBeautyStat.onClickByRes(R.string.拍照_表情包拍摄_主页面_停止拍摄);
                        mCameraPageController.onPauseVideo();
                    }
                }
                break;
            }

            case ShutterConfig.TabType.VIDEO:
            {
                if (mode == ShutterConfig.ShutterType.RECORDING)
                {
                    TongJi2.AddCountByRes(getContext(), R.integer.拍照_视频拍摄_拍摄暂停);
                    MyBeautyStat.onClickByRes(R.string.拍照_视频拍摄页_主页面_拍摄暂停);
                    if (mCameraPageController != null)
                    {
                        mCameraPageController.onPauseVideo();
                    }
                }
                else if (mode == ShutterConfig.ShutterType.DEF || mode == ShutterConfig.ShutterType.PAUSE_RECORD
                        || mode == ShutterConfig.ShutterType.UNFOLD_RES)
                {
                    if (mShutterBtn != null)
                    {
                        if (timer == CameraConfig.CaptureMode.Manual)
                        {
                            int video_size = mShutterBtn.getVideoSize();
                            if (video_size > 0)
                            {
                                MyBeautyStat.onClickByRes(R.string.拍照_视频拍摄页_主页面_继续拍摄);
                            }
                            else
                            {
                                TongJi2.AddCountByRes(getContext(), R.integer.拍照_视频拍摄_拍摄开始);
                                MyBeautyStat.onClickByRes(R.string.拍照_视频拍摄页_主页面_开始拍摄);
                            }
                            showRecordAnimator();
                        }
                        else
                        {
                            mCameraPageController.onTiming();
                            onCancelSelectedVideo();
                            unlockUI(300);
                        }
                    }
                }
                break;
            }

            case ShutterConfig.TabType.CUTE:
            case ShutterConfig.TabType.PHOTO:
            {
                if (timer == CameraConfig.CaptureMode.Manual)
                {
                    if (mType == ShutterConfig.TabType.PHOTO)
                    {
                        MyBeautyStat.onClickByRes(R.string.拍照_美颜拍照_主页面_拍照按钮);
                    }
                    else
                    {
                        MyBeautyStat.onClickByRes(R.string.拍照_萌妆照拍摄页_主页面_拍照快门);
                    }

                    if (mCameraPageController != null)
                    {
                        mCameraPageController.onShutterClick();
                    }
                }
                else
                {
                    if (mCameraPageController != null)
                    {
                        if (mCameraPageController.isPatchMode() && mType == ShutterConfig.TabType.PHOTO)
                        {
                            mCameraPageController.onShutterClick();
                        }
                        else
                        {
                            mCameraPageController.onTiming();
                            unlockUI(300);
                        }
                    }
                }
            }
        }
    }

    /**
     * 改变音效开关图标
     *
     * @param mute
     */
    public void setStickerSoundViewMute(boolean mute)
    {
        if (mStickerView != null)
        {
            mStickerView.setStickerSoundMute(mute);
        }
    }

    protected ShutterAnimListener mShutterAnimListener = new ShutterAnimListener()
    {
        @Override
        public void onShutterAnimStart(int mode)
        {
            mLastShutterType = mode;
            mDoingAnim = true;
            if (mScrollView != null)
            {
                mScrollView.SetUIEnable(false);
            }
        }

        @Override
        public void onShutterAnimEnd(int mode)
        {
            mShutterType = mode;
            mControlLayout.setShutterMode(mode);

            if (mScrollView != null)
            {
                mScrollView.SetUIEnable(true);
            }
            switch (mode)
            {
                case ShutterConfig.ShutterType.DEF:
                {
                    if (mStickerView != null)
                    {
                        mStickerView.setVisibility(GONE);
                    }
                    if (mCameraPageController != null)
                    {
                        mCameraPageController.onCloseStickerList();
                    }
                    break;
                }

                case ShutterConfig.ShutterType.UNFOLD_RES:
                {
                    if (mControlLayout != null)
                    {
                        mControlLayout.setVisibility(GONE);
                    }

                    if (mScrollView != null)
                    {
                        mScrollView.setVisibility(GONE);
                    }
                    if (mStickerView != null)
                    {
                        mStickerView.onScrollStickerToCenter();
                    }

                    break;
                }

                case ShutterConfig.ShutterType.RECORDING:
                {
                    if (mStickerView != null)
                    {
                        mStickerView.setVisibility(GONE);
                    }

                    if (mScrollView != null)
                    {
                        mScrollView.setTranslationY(mScrollTransY);
                        mScrollView.setVisibility(GONE);
                    }

                    if (mCameraFilterRecyclerView != null)
                    {
                        mCameraFilterRecyclerView.setVisibility(GONE);
                    }

                    if (mCameraPageController != null)
                    {
                        mCameraPageController.onShutterClick();
                    }

                    mDoingAnim = false;
                    unlockUI();
                    return;
                }

                case ShutterConfig.ShutterType.PAUSE_RECORD:
                {
                    if (mControlLayout != null)
                    {
                        mControlLayout.setBtnClickable(true);
                    }
                    if (mShutterBtn != null)
                    {
                        mShutterBtn.setIsDrawPauseLogo(false);
                    }
                    if (mUIObserverList != null)
                    {
                        mUIObserverList.notifyObservers(UIObserver.MSG_ONLY_SHOW_CAMERA_SWITCH);
                    }
                    mDoingAnim = false;
                    unlockUI();
                    return;
                }
            }
            mDoingAnim = false;
            unlockUI(200);
        }

        @Override
        public void onShutterAnimCancel()
        {
            mDoingAnim = false;
            unlockUI();
        }
    };

    // CameraStickerUI 对子view的监听
    protected StickerUIListener mStickerUIController = new StickerUIListener()
    {
        @Override
        public void onStickerSoundMute(boolean mute)
        {
            if (mCameraPageController != null)
            {
                //设置贴纸音效开关，成功后改变图标
                boolean success = mCameraPageController.onStickerSoundMute(mute);
                if (success) setStickerSoundViewMute(mute);
            }
        }

        @Override
        public boolean getAudioMute()
        {
            return mCameraPageController != null && mCameraPageController.getAudioMute();
        }

        @Override
        public void onCloseStickerList()
        {
            if (isStickerListShowed())
            {
                if (mLastShutterType != ShutterConfig.ShutterType.PAUSE_RECORD)
                {
                    CloseStickerList();
                }
                else
                {
                    CloseStickerToPauseMode();
                }
            }
        }

        @Override
        public void onOpenStickerMgrPage()
        {
            OpenStickerMgrPage();// 打开素材管理页面
        }

        @Override
        public void onCloseStickerMgrPage()
        {
            CloseStickerMgrPage();// 关闭素材管理页面
        }

        @Override
        public void onSelectSticker(VideoStickerRes stickerRes, boolean repeat) // 选中、使用素材
        {
            onSelectSticker(stickerRes, repeat, false);
        }

        @Override
        public void onSelectSticker(VideoStickerRes stickerRes, boolean repeat, boolean isTabChange)
        {
            if (mCameraPageController != null)
            {
                isUsedStickerFilter = false;

                if (stickerRes != null && stickerRes.mStickerRes != null && stickerRes.mStickerRes.mFilterRes != null)
                {
                    isUsedStickerFilter = true;
                }

                if (repeat)
                {
                    mCameraPageController.onSelectRepeatSticker(stickerRes);
                }
                else
                {
                    mCameraPageController.onSelectSticker(stickerRes, isTabChange);
                }
            }
        }
    };

    public boolean isUsedStickerFilter()
    {
        return isUsedStickerFilter;
    }

    public void setUsedStickerFilter(boolean usedStickerFilter)
    {
        isUsedStickerFilter = usedStickerFilter;
    }

    protected CameraPageListener mCameraPageController;


    public void setBeautySelectorCB(PageCallbackAdapter cb)
    {
        mBeautySelectorCB = cb;
    }

    public void SetCameraPageListener(CameraPageListener listener)
    {
        mCameraPageController = listener;
        if (mControlLayout != null)
        {
            mControlLayout.setCameraPageListener(listener);
        }

        if (mShutterBtn != null)
        {
            mShutterBtn.setCameraPageListener(listener);
        }
    }

    /**
     * 滤镜组件监听
     *
     * @param cameraFilterListener
     */
    public void setCameraFilterListener(CameraFilterListener cameraFilterListener)
    {
        this.mCameraFilterListener = cameraFilterListener;
    }

    // ************************************** 动画 ************************************//

    private Animator InitTransYAnimator(View item, int dy, long duration)
    {
        ObjectAnimator obj = ObjectAnimator.ofFloat(item, "translationY", dy);
        obj.setDuration(duration);
        return obj;
    }

    private Animator InitTransYAnimator(View item, int startY, int endY, long duration)
    {
        ObjectAnimator obj = ObjectAnimator.ofFloat(item, "translationY", startY, endY);
        obj.setDuration(duration);
        return obj;
    }

    private Animator InitAlphaAnimator(View item, float startA, float endA, long duration)
    {
        ObjectAnimator obj = ObjectAnimator.ofFloat(item, "alpha", startA, endA);
        obj.setDuration(duration);
        return obj;
    }

    /**
     * 同一个 tab 类型下，做不同模式的转换
     *
     * @param nextMode     模式
     * @param animDuration 时长
     * @return 属性动画
     */
    private ValueAnimator GetShutterAnimByMode(@ShutterConfig.ShutterType int nextMode, long animDuration)
    {
        if (mShutterBtn == null || mShutterBtn.isDoingTransAnim()) return null;

        return (ValueAnimator) mShutterBtn.InitTransformAnim(nextMode, animDuration);
    }

    private ObjectAnimator GetShowStickerMgrTransYAnim()
    {
        int dy = ShareData.m_screenRealHeight;
        mStickerManagerPage.setVisibility(VISIBLE);
        return (ObjectAnimator) InitTransYAnimator(mStickerManagerPage, dy, 0, mAnimDuration);
    }

    private ObjectAnimator GetCloseStickerMgrTransYAnim()
    {
        int dy = ShareData.m_screenRealHeight;
        return (ObjectAnimator) InitTransYAnimator(mStickerManagerPage, dy, mAnimDuration);
    }

    private ObjectAnimator GetShowFilterTransYAnim()
    {
        int dy = mFilterTransY;
        if (mCameraFilterRecyclerView != null)
        {
            mCameraFilterRecyclerView.setVisibility(VISIBLE);
        }
        return (ObjectAnimator) InitTransYAnimator(mCameraFilterRecyclerView, dy, 0, mAnimDuration);
    }

    private ObjectAnimator GetShowBeautySelectorTransYAnim()
    {
        int dy = mBeautyTransY;
        if (mBeautySelectorView != null)
        {
            mBeautySelectorView.setVisibility(VISIBLE);
        }
        return (ObjectAnimator) InitTransYAnimator(mBeautySelectorView, dy, 0, mAnimDuration);
    }

    private Animator GetHideBeautySelectorTransYAnim()
    {
        int dy = mBeautyTransY;
        return InitTransYAnimator(mBeautySelectorView, 0, dy, mAnimDuration);
    }

    private ObjectAnimator GetHideFilterTransYAnim()
    {
        int dy = mFilterTransY;
        return (ObjectAnimator) InitTransYAnimator(mCameraFilterRecyclerView, dy, mAnimDuration);
    }


    /**
     * @return 关闭素材列表的动画
     */
    private Animator GetHideStickerTransYAnim()
    {
        int dy = mStickerTransY;
        return InitTransYAnimator(mStickerView, 0, dy, mAnimDuration);
    }

    /**
     * @return 显示素材列表的动画
     */
    private Animator GetShowStickerTransYAnim()
    {
        mStickerView.setVisibility(VISIBLE);
        int dy = mStickerTransY;
        return InitTransYAnimator(mStickerView, dy, 0, mAnimDuration);
    }

    private Animator GetShowScrollerTransAnim()
    {
        if (mScrollView == null || mShutterType == ShutterConfig.ShutterType.PAUSE_RECORD)
        {
            return null;
        }
        int dy = mScrollTransY;
        mScrollView.setVisibility(VISIBLE);
        return InitTransYAnimator(mScrollView, dy, 0, mAnimDuration);
    }

    private Animator GetHideScrollerTransAnim()
    {
        int dy = mScrollTransY;
        return InitTransYAnimator(mScrollView, 0, dy, mAnimDuration);
    }

    /**
     * @return 显示底部控制的动画
     */
    private Animator GetShowControlTransYAnim()
    {
        int dy = mControlTransY;
        mControlLayout.setVisibility(VISIBLE);
        return InitTransYAnimator(mControlLayout, dy, 0, mAnimDuration);
    }

    /**
     * @return 关闭底部控制的动画
     */
    private Animator GetHideControlTransYAnim()
    {
        int dy = mControlTransY;
        return InitTransYAnimator(mControlLayout, 0, dy, mAnimDuration);
    }

    public void cancelAnim()
    {
        if (mSet != null)
        {
            mSet.cancel();
        }
    }

    public void showFullProgressAnimator()
    {
        if (mShutterBtn != null)
        {
            mShutterBtn.pauseProgress();
        }

        int mode = ShutterConfig.ShutterType.PAUSE_RECORD;

        Animator shutterAnim = GetShutterAnimByMode(mode, mAnimDuration);
        if (shutterAnim == null) return;

        Animator controlAnim = null;
        if (mControlLayout != null)
        {
            mControlLayout.setShutterMode(mode);
            mControlLayout.updateUI();
            mControlLayout.updateDelLogo(mShutterBtn.isAlreadySelLastVideo());
            mControlLayout.setTranslationY(0);
            mControlLayout.setVisibility(VISIBLE);
            controlAnim = InitAlphaAnimator(mControlLayout, 0f, 1f, mAnimDuration);
        }

        mSet = new AnimatorSet();
        mSet.playTogether(shutterAnim, controlAnim);
        mSet.start();
    }

    public void showPauseAnimator()
    {
        if (mShutterBtn != null)
        {
            mShutterBtn.pauseProgress();
        }

        int mode = ShutterConfig.ShutterType.PAUSE_RECORD;

        Animator shutterAnim = GetShutterAnimByMode(mode, mAnimDuration);
        if (shutterAnim == null) return;

        Animator controlAnim = null;
        if (mControlLayout != null)
        {
            mControlLayout.setShutterMode(mode);
            mControlLayout.updateUI();
            mControlLayout.updateDelLogo(mShutterBtn.isAlreadySelLastVideo());
            mControlLayout.setTranslationY(0);
            mControlLayout.setVisibility(VISIBLE);
            controlAnim = InitAlphaAnimator(mControlLayout, 0f, 1f, mAnimDuration);
        }

        mSet = new AnimatorSet();
        mSet.playTogether(shutterAnim, controlAnim);
        mSet.start();
    }

    private void showRecordAnimator()
    {
        if (mShutterBtn != null)
        {
            mShutterBtn.resumeProgress();
            mShutterBtn.resetSelectedStatus();
        }

        if (mControlLayout != null)
        {
            mControlLayout.setBtnClickable(false);
        }

        if (mUIObserverList != null)
        {
            mUIObserverList.notifyObservers(UIObserver.MSG_GONE_TOP_ALL_UI);
        }

        Animator shutterAnim = GetShutterAnimByMode(ShutterConfig.ShutterType.RECORDING, mAnimDuration);

        if (shutterAnim == null) return;

        mSet = new AnimatorSet();

        switch (mShutterType)
        {
            case ShutterConfig.ShutterType.PAUSE_RECORD:
            case ShutterConfig.ShutterType.DEF:
            {
                Animator controlAnim = null;
                if (mControlLayout != null)
                {
                    controlAnim = InitAlphaAnimator(mControlLayout, 1f, 0f, mAnimDuration);
                }

                Animator scrollAnim = null;
                if (mScrollView != null)
                {
                    scrollAnim = InitAlphaAnimator(mScrollView, 1f, 0f, mAnimDuration);
                }

                mSet.playTogether(shutterAnim, controlAnim, scrollAnim);
                break;
            }

            case ShutterConfig.ShutterType.UNFOLD_RES:
            {
                Animator stickerAnim = GetHideStickerTransYAnim();
                mSet.playTogether(shutterAnim, stickerAnim);
            }
        }

        mSet.start();
    }

    /**
     * 关闭滤镜列表
     */
    public void CloseFilterList()
    {
        if (this.getVisibility() != VISIBLE) return;

        if (mType == ShutterConfig.TabType.VIDEO)
        {
            if (mRecordTextBtn != null && mShutterType == ShutterConfig.ShutterType.PAUSE_RECORD)
            {
                mRecordTextBtn.setVisibility(VISIBLE);
            }

            if (mControlLayout != null && mShutterType == ShutterConfig.ShutterType.DEF)
            {
                mControlLayout.updateDurationBtnVis(true);
            }
        }

        Animator filterAnim = GetHideFilterTransYAnim();

        filterAnim.addListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                unlockUI(100);
            }
        });
        filterAnim.start();

        /*埋点*/
        int clickResId = -1;
        int pocoResId = -1;
        switch (mType)
        {
            case ShutterConfig.TabType.GIF:
                clickResId = R.string.拍照_表情包拍摄_滤镜列表_收起滤镜列表;
                pocoResId = R.integer.拍照_表情包拍摄_收起滤镜;
                break;
            case ShutterConfig.TabType.PHOTO:
                clickResId = R.string.拍照_美颜拍照_滤镜列表_收起滤镜列表;
                pocoResId = R.integer.拍照_收起滤镜列表;
                break;
            case ShutterConfig.TabType.CUTE:
                clickResId = R.string.拍照_萌妆照拍摄页_滤镜列表_收起滤镜列表;
                pocoResId = R.integer.拍照_萌装照_收起滤镜;
                break;
            case ShutterConfig.TabType.VIDEO:
                clickResId = R.string.拍照_视频拍摄页_滤镜列表_收起滤镜列表;
                pocoResId = R.integer.拍照_视频拍摄_收起滤镜;
                break;
        }
        if (clickResId != -1)
        {
            MyBeautyStat.onClickByRes(clickResId);
        }
        if (pocoResId != -1)
        {
            TongJi2.AddCountByRes(getContext(), pocoResId);
        }

        /*滤镜页面 关闭 埋点*/
        clickResId = -1;
        switch (mType)
        {
            case ShutterConfig.TabType.GIF:
                clickResId = R.string.拍照_表情包拍摄_滤镜列表;
                break;
            case ShutterConfig.TabType.CUTE:
                clickResId = R.string.拍照_萌妆照拍摄页_滤镜列表;
                break;
            case ShutterConfig.TabType.VIDEO:
                clickResId = R.string.拍照_视频拍摄页_滤镜列表;
                break;
            case ShutterConfig.TabType.PHOTO:
                clickResId = R.string.拍照_美颜拍照_滤镜列表;
                break;
        }
        if (clickResId != -1)
        {
            MyBeautyStat.onPageEndByRes(clickResId);
        }
    }

    /**
     * 打开滤镜列表
     */
    private void OpenFilterList()
    {
        if (this.getVisibility() != VISIBLE || mDoingAnim) return;

        if (mType == ShutterConfig.TabType.VIDEO)
        {
            if (mRecordTextBtn != null /*&& mShutterType == ShutterConfig.ShutterType.PAUSE_RECORD*/)
            {
                mRecordTextBtn.setVisibility(GONE);
            }

            if (mControlLayout != null /*&& mShutterType == ShutterConfig.ShutterType.DEF*/)
            {
                mControlLayout.updateDurationBtnVis(false);
            }
        }

        if (mShutterBtn != null)
        {
            if (mShutterBtn.isAlreadySelLastVideo())
            {
                mShutterBtn.resetSelectedStatus();
                mControlLayout.updateDelLogo(false);
            }
        }

        /*滤镜页面 展开 埋点*/
        int clickResId = -1;
        int pocoResId = -1;
        switch (mType)
        {
            case ShutterConfig.TabType.GIF:
                clickResId = R.string.拍照_表情包拍摄_滤镜列表;
                pocoResId = R.integer.拍照_表情包拍摄_展开滤镜;
                break;
            case ShutterConfig.TabType.CUTE:
                clickResId = R.string.拍照_萌妆照拍摄页_滤镜列表;
                pocoResId = R.integer.拍照_萌装照_展开滤镜;
                break;
            case ShutterConfig.TabType.VIDEO:
                clickResId = R.string.拍照_视频拍摄页_滤镜列表;
                pocoResId = R.integer.拍照_视频拍摄_展开滤镜;
                break;
            case ShutterConfig.TabType.PHOTO:
                clickResId = R.string.拍照_美颜拍照_滤镜列表;
                pocoResId = R.integer.拍照_展开滤镜列表;
                break;
        }
        if (clickResId != -1)
        {
            MyBeautyStat.onPageStartByRes(clickResId);
        }
        if (pocoResId != -1)
        {
            TongJi2.AddCountByRes(getContext(), pocoResId);
        }

        Animator filterAnim = GetShowFilterTransYAnim();
        filterAnim.addListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                unlockUI(300);
            }

            @Override
            public void onAnimationCancel(Animator animation)
            {
                unlockUI();
            }
        });
        filterAnim.start();
    }


    /**
     * 打开素材列表
     */
    private void OpenStickerList()
    {
        if (this.getVisibility() != VISIBLE || mDoingAnim) return;

        if (mRecordTextBtn != null)
        {
            mRecordTextBtn.setVisibility(GONE);
        }

        if (isAlreadySelLastVideo())
        {
            resetSelectedStatus();
        }

        /*贴纸素材列表展开 页面埋点*/
        int clickResId = -1;
        int pocoResId = -1;
        switch (mType)
        {
            case ShutterConfig.TabType.GIF:
                clickResId = R.string.拍照_表情包拍摄_素材列表;
                pocoResId = R.integer.拍照_表情包拍摄_展开素材列表;
                break;
            case ShutterConfig.TabType.CUTE:
                clickResId = R.string.拍照_萌妆照拍摄页_素材列表;
                pocoResId = R.integer.拍照_萌装照_展开素材列表;
                break;
            case ShutterConfig.TabType.VIDEO:
                clickResId = R.string.拍照_视频拍摄页_素材列表;
                pocoResId = R.integer.拍照_视频拍摄_展开素材列表;
                break;
        }
        if (clickResId != -1)
        {
            MyBeautyStat.onPageStartByRes(clickResId);
        }
        if (pocoResId != -1)
        {
            TongJi2.AddCountByRes(getContext(), pocoResId);
        }

        Animator stickerAnim = GetShowStickerTransYAnim();
        Animator controlAnim = GetHideControlTransYAnim();
        Animator scrollerAnim = GetHideScrollerTransAnim();

        Animator shutterAnim = GetShutterAnimByMode(ShutterConfig.ShutterType.UNFOLD_RES, mAnimDuration);

        if (shutterAnim == null) return;

        AnimatorSet set = new AnimatorSet();
        set.playTogether(shutterAnim, stickerAnim, controlAnim, scrollerAnim);
        set.start();
    }

    /**
     * 关闭美颜脸型列表
     */
    public void CloseBeautySelector()
    {
        if (this.getVisibility() != VISIBLE) return;

        if (mType == ShutterConfig.TabType.VIDEO)
        {
            if (mRecordTextBtn != null && mShutterType == ShutterConfig.ShutterType.PAUSE_RECORD)
            {
                mRecordTextBtn.setVisibility(VISIBLE);
            }

            if (mControlLayout != null && mShutterType == ShutterConfig.ShutterType.DEF)
            {
                mControlLayout.updateDurationBtnVis(true);
            }
        }

        Animator beautyAnim = GetHideBeautySelectorTransYAnim();

        beautyAnim.addListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                unlockUI(100);
                if (mBeautySelectorCB != null) mBeautySelectorCB.setShowSelectorView(false);
            }
        });
        beautyAnim.start();
    }


    /**
     * 打开美颜脸型列表
     */
    public void OpenBeautySelector(boolean hasAnim)
    {
        if (this.getVisibility() != VISIBLE || mDoingAnim) return;

        if (mType == ShutterConfig.TabType.VIDEO)
        {
            if (mRecordTextBtn != null)
            {
                mRecordTextBtn.setVisibility(GONE);
            }

            if (mControlLayout != null)
            {
                mControlLayout.updateDurationBtnVis(false);
            }
        }

        if (mShutterBtn != null)
        {
            if (mShutterBtn.isAlreadySelLastVideo())
            {
                mShutterBtn.resetSelectedStatus();
                mControlLayout.updateDelLogo(false);
            }
        }

        if (!hasAnim)
        {
            if (mBeautySelectorView != null)
            {
                mBeautySelectorView.setTranslationY(0);
                mBeautySelectorView.setVisibility(VISIBLE);
            }
            if (mBeautySelectorCB != null)
            {
                mBeautySelectorCB.setShowSelectorView(true);
            }
            return;
        }
        Animator beautyAnim = GetShowBeautySelectorTransYAnim();
        beautyAnim.addListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                unlockUI(300);
                if (mBeautySelectorCB != null) mBeautySelectorCB.setShowSelectorView(true);
            }

            @Override
            public void onAnimationCancel(Animator animation)
            {
                unlockUI();
            }
        });
        beautyAnim.start();
    }


    /**
     * 关闭素材列表
     */
    private void CloseStickerList()
    {
        if (this.getVisibility() != VISIBLE) return;

        /*素材列表关闭 页面埋点*/
        int clickResId = -1;
        int pocoResId = -1;
        switch (mType)
        {
            case ShutterConfig.TabType.GIF:
                clickResId = R.string.拍照_表情包拍摄_素材列表;
                pocoResId = R.integer.拍照_表情包拍摄_收回素材列表;
                break;
            case ShutterConfig.TabType.CUTE:
                clickResId = R.string.拍照_萌妆照拍摄页_素材列表;
                pocoResId = R.integer.拍照_萌装照_收回素材列表;
                break;
            case ShutterConfig.TabType.VIDEO:
                clickResId = R.string.拍照_视频拍摄页_素材列表;
                pocoResId = R.integer.拍照_视频拍摄_收回素材列表;
                break;
        }
        if (clickResId != -1)
        {
            MyBeautyStat.onPageEndByRes(clickResId);
        }
        if (pocoResId != -1)
        {
            TongJi2.AddCountByRes(getContext(), pocoResId);
        }

        Animator controlAnim = GetShowControlTransYAnim();
        Animator scrollerAnim = GetShowScrollerTransAnim();
        Animator stickerAnim = GetHideStickerTransYAnim();

        Animator shutterAnim = GetShutterAnimByMode(ShutterConfig.ShutterType.DEF, mAnimDuration);

        if (shutterAnim == null) return;

        AnimatorSet set = new AnimatorSet();
        set.playTogether(shutterAnim, stickerAnim, controlAnim, scrollerAnim);
        set.start();
    }

    public boolean IsDoingAnim()
    {
        return mDoingAnim;
    }

    /**
     * @return true 开始关闭页面动画 false 没有可关的页面
     */
    public boolean CloseOpenedPage()
    {
        if (IsDoingAnim()) return true;

        if (isShowStickerMgrPage())
        {
            CloseStickerMgrPage();
            return true;
        }

        if (isColorFilterShowed())
        {
            CloseFilterList();
            return true;
        }

        if (isBeautySelectorShowed())
        {
            CloseBeautySelector();
            return true;
        }

        if (isStickerListShowed())
        {
            if (mLastShutterType != ShutterConfig.ShutterType.PAUSE_RECORD)
            {
                CloseStickerList();
            }
            else
            {
                CloseStickerToPauseMode();
            }
            return true;
        }
        return false;
    }

    private void CloseStickerToPauseMode()
    {
        Animator controlAnim = GetShowControlTransYAnim();
        Animator stickerAnim = GetHideStickerTransYAnim();
        Animator shutterAnim = GetShutterAnimByMode(ShutterConfig.ShutterType.PAUSE_RECORD, mAnimDuration);

        if (shutterAnim == null) return;

        if (mRecordTextBtn != null)
        {
            mRecordTextBtn.setVisibility(VISIBLE);
        }

        AnimatorSet set = new AnimatorSet();
        set.playTogether(shutterAnim, stickerAnim, controlAnim);
        set.start();
    }

    public boolean CloseUnlockView()
    {
        return mStickerView != null && mStickerView.CloseUnlockView() ||
                mCameraFilterRecyclerView != null && mCameraFilterRecyclerView.closeRecommendView();
    }

    private void CloseStickerMgrPage()
    {
        if (StickerLocalMgr.getInstance().hasDeleted())
        {
            StickerResMgr.getInstance().notifyReflashAllData();
        }

        Animator closeLocalStickerAnim = GetCloseStickerMgrTransYAnim();
        closeLocalStickerAnim.addListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationStart(Animator animation)
            {
                mDoingAnim = true;
                if (mScrollView != null)
                {
                    mScrollView.SetUIEnable(false);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation)
            {
                animation.removeAllListeners();
                mDoingAnim = false;
                isMgrPage = false;
                if (mScrollView != null)
                {
                    mScrollView.SetUIEnable(true);
                }

                //动画完成后才更新数据
                CameraBottomControlV3.this.postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if (mCameraPageController != null)
                        {
                            mCameraPageController.closeStickerMgrPage();
                        }

                        if(mStickerManagerPage != null)
                        {
                            mStickerManagerPage.ClearAll();
                        }
                        removeView(mStickerManagerPage);
                        mStickerManagerPage = null;
                    }
                }, 100);
            }
        });
        closeLocalStickerAnim.start();

         /*埋点*/
        int clickResId = -1;
        switch (mType)
        {
            case ShutterConfig.TabType.GIF:
                clickResId = R.string.拍照_表情包拍摄_素材管理_返回;
                break;
            case ShutterConfig.TabType.CUTE:
                clickResId = R.string.拍照_萌妆照拍摄页_素材管理_返回;
                break;
            case ShutterConfig.TabType.VIDEO:
                clickResId = R.string.拍照_视频拍摄页_素材管理_返回;
                break;
        }
        if (clickResId != -1)
        {
            MyBeautyStat.onClickByRes(clickResId);
        }
    }

    protected void OpenStickerMgrPage()
    {
        if(mDoingAnim) return;

        initStickerMgrPage();
        Animator anim = GetShowStickerMgrTransYAnim();
        anim.addListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationStart(Animator animation)
            {
                mDoingAnim = true;
                if (mScrollView != null)
                {
                    mScrollView.SetUIEnable(false);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation)
            {
                animation.removeAllListeners();
                isMgrPage = true;
                mDoingAnim = false;
                if (mScrollView != null)
                {
                    mScrollView.SetUIEnable(true);
                }

                if (mCameraPageController != null)
                {
                    mCameraPageController.openStickerMgrPage();
                }

                /*素材管理关闭 埋点*/
                int clickResId = -1;
                switch (mType)
                {
                    case ShutterConfig.TabType.GIF:
                        clickResId = R.string.拍照_表情包拍摄_素材管理;
                        break;
                    case ShutterConfig.TabType.CUTE:
                        clickResId = R.string.拍照_萌妆照拍摄页_素材管理;
                        break;
                    case ShutterConfig.TabType.VIDEO:
                        clickResId = R.string.拍照_视频拍摄页_素材管理;
                        break;
                }
                if (clickResId != -1)
                {
                    MyBeautyStat.onPageEndByRes(clickResId);
                }

            }
        });
        anim.start();

         /*埋点*/
        int clickResId = -1;
        switch (mType)
        {
            case ShutterConfig.TabType.GIF:
                clickResId = R.string.拍照_表情包拍摄_素材列表_素材管理;
                break;
            case ShutterConfig.TabType.CUTE:
                clickResId = R.string.拍照_萌妆照拍摄页_素材列表_素材管理;
                break;
            case ShutterConfig.TabType.VIDEO:
                clickResId = R.string.拍照_视频拍摄页_素材列表_素材管理;
                break;
        }
        if (clickResId != -1)
        {
            MyBeautyStat.onClickByRes(clickResId);
        }

    }

    public boolean isMgrPage()
    {
        return isMgrPage;
    }

    public void ClearMemory()
    {
        if (mUIObserverList != null)
        {
            mUIObserverList.deleteObserver(this);
            mUIObserverList = null;
        }
        mClickListener = null;
        mCameraFilterCB = null;

        mStickerUIController = null;

        mScrollViewItemClickListener = null;
        mScrollViewItemScrollStateListener = null;

        if (mCameraFilterListIndexs != null)
        {
            mCameraFilterListIndexs.cleaAll();
            mCameraFilterListIndexs = null;
        }

        if (mCameraFilterRecyclerView != null)
        {
            mCameraFilterRecyclerView.ClearAll();
            mCameraFilterRecyclerView = null;
        }

        if (mStickerManagerPage != null)
        {
            mStickerManagerPage.ClearAll();
            mStickerManagerPage = null;
        }

        if (mStickerView != null)
        {
            mStickerView.setStickerUIListener(null);
            mStickerView.ClearAll();
        }

        mShutterBtn.ClearMemory();
        mShutterBtn.setOnClickListener(null);
        mShutterBtn.setOnLongClickListener(null);

        mScrollView.ClearMemory();
    }

    public boolean isAlreadySelLastVideo()
    {
        return mShutterBtn != null && mShutterBtn.isAlreadySelLastVideo();
    }

    @Override
    public void onClickColorFilterBtn()
    {
        if (mCameraPageController != null && mCameraPageController.isPatchMode()) {
            return;
        }
        OpenFilterList();
    }

    @Override
    public void onClickStickerBtn()
    {
        OpenStickerList();
    }

    @Override
    public void onClickVideoDelBtn()
    {
        boolean isSel = mShutterBtn.isAlreadySelLastVideo();

        mControlLayout.updateDelLogo(!isSel);
        if (!isSel)
        {
            mShutterBtn.selectLastVideo();
        }
        else
        {
            int size = mShutterBtn.deleteVideo();
            if (size == 0)
            {
                resetUIToVideoDefMode(false);
            }
        }

        if (isSel && mCameraPageController != null)
        {
            mCameraPageController.onConfirmVideoDel();
        }
    }

    public void setControlBtnRotate(int degree)
    {
        if (mControlLayout != null)
        {
            mControlLayout.setBtnRotation(degree);
        }

        if (mShutterBtn != null)
        {
            mShutterBtn.setGIFRotation(degree);
        }

        if (mStickerView != null)
        {
            mStickerView.setBtnRotation(degree);
        }
    }

    @Override
    public void onCancelSelectedVideo()
    {
        resetSelectedStatus();
    }

    @Override
    public void onClickBeautyBtn()
    {
        boolean isClickEnable = true;
        if (mCameraPageController != null && mCameraPageController.isPatchMode()) {
            isClickEnable = false;
        }
        if (mInnerBeautySelectorCB != null && isClickEnable)
        {
            mInnerBeautySelectorCB.setShowSelectorView(true);
        }

        /*this.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                if (mBeautySelectorView != null)
                {
                    int currentSelShapeId = mBeautySelectorView.getCurrentSelShapeId();
                    if (currentSelShapeId != -1) {
                        mBeautySelectorView.setCameraShapeSubOpen(currentSelShapeId, true);
                    }
                }
            }
        }, mAnimDuration);*/
    }

    @Override
    public boolean isShowFilterList()
    {
        return isColorFilterShowed();
    }

    @Override
    public boolean isShowBeautyList()
    {
        return isBeautySelectorShowed();
    }

    public void resetSelectedStatus()
    {
        if (mShutterBtn != null)
        {
            mShutterBtn.resetSelectedStatus();
        }

        if (mControlLayout != null)
        {
            mControlLayout.updateDelLogo(false);
        }
    }

    public void resetUIToVideoDefMode()
    {
        resetUIToVideoDefMode(true);
    }

    private void resetUIToVideoDefMode(boolean clearProgress)
    {
        if (clearProgress)
        {
            mShutterBtn.ResetProgress();
        }
        mScrollView.setTranslationY(0);
        if (mScrollView.getAlpha() == 0)
        {
            mScrollView.setAlpha(1);
        }
        if (mScrollView.getVisibility() == GONE)
        {
            mScrollView.setVisibility(VISIBLE);
        }
        if (mRecordTextBtn != null)
        {
            mRecordTextBtn.setText(null);
            mRecordTextBtn.setVisibility(GONE);
        }

        if (mControlLayout != null)
        {
            mControlLayout.setTranslationY(0);
            mControlLayout.setAlpha(1);
            mControlLayout.setVisibility(VISIBLE);
        }
        setShutterMode(ShutterConfig.ShutterType.DEF);
        updateUI();
        if (mUIObserverList != null)
        {
            mUIObserverList.notifyObservers(UIObserver.MSG_SHOW_TOP_ALL_UI);
        }
    }

    @Override
    public void update(Observable o, Object arg)
    {
        if (arg instanceof Integer)
        {
            int msg = (int) arg;
            switch (msg)
            {
                case UIObserver.MSG_LOCK_UI:
                {
                    if (mType == ShutterConfig.TabType.VIDEO && mShutterType != ShutterConfig.ShutterType.UNFOLD_RES)
                    {
                        if (mScrollView != null)
                        {
                            mScrollView.SetUIEnable(false);
                        }

                        if (mControlLayout != null)
                        {
                            mControlLayout.setUIEnable(false);
                        }
                    }
                    else
                    {
                        mIsIntercept = true;
                    }
                    break;
                }

                case UIObserver.MSG_UNLOCK_UI:
                {
                    mIsIntercept = false;

                    if (mScrollView != null)
                    {
                        mScrollView.SetUIEnable(true);
                    }

                    if (mControlLayout != null)
                    {
                        mControlLayout.setUIEnable(true);
                    }
                    break;
                }
            }
        }
    }
}
