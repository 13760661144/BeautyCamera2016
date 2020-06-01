package cn.poco.filter4;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import cn.poco.acne.view.CirclePanel;
import cn.poco.advanced.ImageUtils;
import cn.poco.beautify.FilterType;
import cn.poco.beautify.RecomDisplayMgr;
import cn.poco.beautify4.Beautify4Page;
import cn.poco.camera.RotationImg2;
import cn.poco.camera2.CameraFilterConfig;
import cn.poco.cloudalbumlibs.utils.CloudAlbumDialog;
import cn.poco.filter4.recycle.FilterAdapter;
import cn.poco.filter4.site.Filter4PageSite;
import cn.poco.filterPendant.MyStatusButton;
import cn.poco.framework.BaseSite;
import cn.poco.framework.DataKey;
import cn.poco.framework.IPage;
import cn.poco.framework.SiteID;
import cn.poco.image.filter;
import cn.poco.makeup.MySeekBar;
import cn.poco.recycleview.AbsAdapter;
import cn.poco.recycleview.AbsDragAdapter;
import cn.poco.recycleview.BaseExAdapter;
import cn.poco.resource.AbsDownloadMgr;
import cn.poco.resource.BaseRes;
import cn.poco.resource.DownloadMgr;
import cn.poco.resource.FilterGroupRes;
import cn.poco.resource.FilterRes;
import cn.poco.resource.FilterResMgr2;
import cn.poco.resource.GroupRes;
import cn.poco.resource.IDownload;
import cn.poco.resource.RecommendRes;
import cn.poco.resource.ResType;
import cn.poco.resource.ResourceUtils;
import cn.poco.resource.ThemeRes;
import cn.poco.resource.ThemeResMgr2;
import cn.poco.resource.WatermarkResMgr2;
import cn.poco.setting.SettingInfoMgr;
import cn.poco.statistics.MyBeautyStat;
import cn.poco.statistics.TongJi2;
import cn.poco.statistics.TongJiUtils;
import cn.poco.system.SysConfig;
import cn.poco.tianutils.CommonUtils;
import cn.poco.tianutils.MakeBmp;
import cn.poco.tianutils.MakeBmpV2;
import cn.poco.tianutils.ShareData;
import cn.poco.utils.OnAnimationClickListener;
import cn.poco.utils.Utils;
import cn.poco.utils.WaitAnimDialog;
import cn.poco.view.material.VerFilterViewEx;
import cn.poco.widget.PressedButton;
import cn.poco.widget.recycle.RecommendDragContainer;
import my.beautyCamera.R;

import static cn.poco.tianutils.ShareData.PxToDpi_xhdpi;

/**
 * 素材美化流程<br>
 * <p>滤镜框</p>
 * </br>
 *
 * @author lmx
 *         Created by lmx on 2016/11/30.
 */

public class FilterPage extends IPage
{
    private final Filter4PageSite m_site;
    private Context mContext;

    private boolean mUiEnabled = true;
    private boolean mQuit;
    private Object mImgs; //RotationImg2[] / Bitmap
    private Bitmap mOrgBmp;//原始图/在onclose释放
    private Bitmap mShowBmp;//显示view
    private Bitmap mTempCompareBmp;

    private boolean mAddDataMark;
    private int mFilterUri;//滤镜类型
    private int mFilterAlpha;//滤镜透明度
    protected int DEF_IMG_SIZE;

    private boolean isBlured;
    private boolean isDarked;

    private int mBottomBarHeight;
    private int mBottomLayoutHeight;

    private boolean isCompareBtnTouch = false;
    private boolean isViewTouch = false;
    private boolean isFold = false;

    private VerFilterViewEx mView;

    private PressedButton mCancelBtn, mOkBtn;
    private FrameLayout mBottomFr;
    private LinearLayout mWaterMarkBottomFr;
    private CirclePanel mCirclePanel;
    private int mCirclePanelRadius;
    private MyStatusButton mCenterBtn;
    private MyStatusButton mWaterMarkCenterBtn;
    private FrameLayout mBottomBar;


    private WaitAnimDialog mWaitAnimDialog;

    private UIHandler mUIHandler;
    private HandlerThread mImageThread;
    private FilterHandler mImageHandler;
    private int mTongJiId;

    //ui
    private int mViewFrW;
    private int mViewFrH;
    private int m_imgH = 0;
    private int m_viewH = 0;
    private int m_viewTopMargin;
    private static final int SHOW_VIEW_ANIM = 300;
    private static final int SHOW_CLOSE_VIEW_ANIM = 200;

    private static float SEEKBAR_SCALE = 0.6f;

    private RecomDisplayMgr mRecomView;

    ImageView mCompareBtn;

    private ArrayList<FilterAdapter.ItemInfo> mListInfos;
    private CameraFilterConfig mConfig;
    private RecommendDragContainer mFilterFr;
    private FilterAdapter mFilterAdapter;

    //water ui
    private FrameLayout mWaterMarkFr;
    private boolean isWatermarkState = false;
    private boolean hasWatermark = false;
    private int mWaterMarkId;
    private RecyclerView mWatermarkRecyclerView;
    private WatermarkAdapter mWatermarkAdapter;
    private ArrayList<WatermarkItem> mWatermarkResArr;

    //business
    protected boolean isBusiness = false;
    protected FrameLayout mBannerView;
    protected ImageView mBannerImageView;
    private CountDownTimer mCountDownTimer;
    private boolean isShowBanner = true;

    private boolean isEdited = false;

    //水印标识alpha动画
    private boolean isDoWaterAlphaAnim = true;

    public FilterPage(Context context, BaseSite site)
    {
        super(context, site);
        mContext = context;
        m_site = (Filter4PageSite) site;
        InitData();
        InitUI();

        MyBeautyStat.onPageStartByRes(R.string.美颜美图_滤镜页面_主页面);
        TongJiUtils.onPageStart(getContext(), R.string.滤镜);
    }

    private void InitData()
    {
        ShareData.InitData(mContext);
        filter.deleteAllCacheFilterFile();

        mBottomLayoutHeight = PxToDpi_xhdpi(232);
        mBottomBarHeight = PxToDpi_xhdpi(88);

        mViewFrW = ShareData.m_screenWidth;
        mViewFrH = ShareData.m_screenHeight - ShareData.PxToDpi_xhdpi(320); //view底部不可见边距 88+232

        mCirclePanelRadius = PxToDpi_xhdpi(55);


        // 获取图片的最大边长
        DEF_IMG_SIZE = SysConfig.GetPhotoSize(getContext());
        mFilterAlpha = 100;

        mUIHandler = new FilterPage.UIHandler();
        mImageThread = new HandlerThread("filter_handler_thread");
        mImageThread.start();
        mImageHandler = new FilterHandler(mImageThread.getLooper(), mUIHandler, getContext());

        mConfig = new CameraFilterConfig();


        //默认使用原图
        mFilterUri = FilterAdapter.OriginalItemInfo.ORIGINAL_ITEM_URI;
//		mFilterUri = 140;

        if (DownloadMgr.getInstance() != null)
        {
            DownloadMgr.getInstance().AddDownloadListener(mDownloadListener);
        }

        mWaterMarkId = SettingInfoMgr.GetSettingInfo(getContext()).GetPhotoWatermarkId(WatermarkResMgr2.getInstance().GetDefaultWatermarkId(getContext()));
    }

    private void InitUI()
    {
        LayoutParams fl;

        mView = new VerFilterViewEx(getContext());
        mView.setVerFilterCB(mControlCallback);
        fl = new LayoutParams(mViewFrW, mViewFrH);
        fl.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        mView.setLayoutParams(fl);
        this.addView(mView, 0);

        mBottomFr = new FrameLayout(mContext);
        fl = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        fl.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        mBottomFr.setLayoutParams(fl);
        this.addView(mBottomFr);
        {
            // 广告banner
            //mBannerView = new ImageView(getContext());
            //mBannerView.setScaleType(ImageView.ScaleType.CENTER);
            //mBannerView.setOnClickListener(mBtnOnClickListener);
            //mBannerView.setVisibility(GONE);
            //fl = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            //fl.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
            //fl.bottomMargin = ShareData.PxToDpi_xhdpi(320);
            //mBottomFr.addView(mBannerView, fl);

            mBottomBar = new FrameLayout(mContext);
            mBottomBar.setBackgroundColor(0xe6ffffff);
            fl = new LayoutParams(LayoutParams.MATCH_PARENT, mBottomBarHeight); //88px
            fl.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
            fl.bottomMargin = mBottomLayoutHeight;
            mBottomBar.setLayoutParams(fl);
            mBottomFr.addView(mBottomBar);
            {
                //叉
                mCancelBtn = new PressedButton(mContext, R.drawable.beautify_cancel, R.drawable.beautify_cancel);
                mCancelBtn.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                mCancelBtn.setPadding(ShareData.PxToDpi_xhdpi(22), 0, ShareData.PxToDpi_xhdpi(22), 0);
                fl = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
                fl.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
                mCancelBtn.setLayoutParams(fl);
                mCancelBtn.setOnTouchListener(mOnAnimationClickListener);
                mBottomBar.addView(mCancelBtn);

                mCenterBtn = new MyStatusButton(getContext());
                mCenterBtn.setData(R.drawable.filterbeautify_color_icon, getResources().getString(R.string.filterpage_filter));
                mCenterBtn.setBtnStatus(true, false);
                mCenterBtn.setOnClickListener(mBtnOnClickListener);
                fl = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
                fl.gravity = Gravity.CENTER;
                mCenterBtn.setLayoutParams(fl);
                mBottomBar.addView(mCenterBtn);

                //勾
                mOkBtn = new PressedButton(mContext, R.drawable.beautify_ok, R.drawable.beautify_ok);
                mOkBtn.setScaleType(ImageView.ScaleType.CENTER);
                mOkBtn.setPadding(ShareData.PxToDpi_xhdpi(22), 0, ShareData.PxToDpi_xhdpi(22), 0);
                ImageUtils.AddSkin(getContext(), mOkBtn);
                fl = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
                fl.gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
                mOkBtn.setLayoutParams(fl);
                mOkBtn.setOnTouchListener(mOnAnimationClickListener);
                mBottomBar.addView(mOkBtn);
            }

            mListInfos = FilterResMgr.GetFilterRes(getContext(), false);
            mFilterAdapter = new FilterAdapter(mConfig);
            mFilterAdapter.SetData(mListInfos);
            mFilterAdapter.setOnDragCallBack(mOnDragCallBack);
            mFilterAdapter.setOnItemClickListener(mOnItemClickListener);
            //滤镜fr
            mFilterFr = new RecommendDragContainer(getContext(), mFilterAdapter);
            fl = new LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            fl.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
            mBottomFr.addView(mFilterFr, fl);
        }

        //水印layout
        mWaterMarkBottomFr = new LinearLayout(mContext);
        mWaterMarkBottomFr.setOrientation(LinearLayout.VERTICAL);
        mWaterMarkBottomFr.setVisibility(GONE);
        mWaterMarkBottomFr.setClickable(true);
        fl = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        fl.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        mWaterMarkBottomFr.setLayoutParams(fl);
        this.addView(mWaterMarkBottomFr);
        {
            FrameLayout mWaterMarkCenterFr = new FrameLayout(mContext);
            mWaterMarkCenterFr.setBackgroundColor(0xffffffff);
            fl = new LayoutParams(LayoutParams.MATCH_PARENT, mBottomBarHeight); //88px
            fl.gravity = Gravity.CENTER_HORIZONTAL;
            mWaterMarkCenterFr.setLayoutParams(fl);
            mWaterMarkBottomFr.addView(mWaterMarkCenterFr);

            mWaterMarkCenterBtn = new MyStatusButton(getContext());
            mWaterMarkCenterBtn.setData(R.drawable.filterbeautify_watermark_icon, getContext().getString(R.string.filterpage_watermark));
            mWaterMarkCenterBtn.setBtnStatus(true, false);
            mWaterMarkCenterBtn.setOnClickListener(mBtnOnClickListener);
            fl = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
            fl.gravity = Gravity.CENTER;
            mWaterMarkCenterBtn.setLayoutParams(fl);
            mWaterMarkCenterFr.addView(mWaterMarkCenterBtn);

            //水印fr
            mWaterMarkFr = new FrameLayout(mContext);
            fl = new LayoutParams(LayoutParams.MATCH_PARENT, mBottomLayoutHeight); //232px
            fl.gravity = Gravity.CENTER_HORIZONTAL;
            mWaterMarkBottomFr.addView(mWaterMarkFr, fl);
        }

        //拖动条tips
        mCirclePanel = new CirclePanel(mContext);
        fl = new LayoutParams(LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(120));
        fl.gravity = Gravity.BOTTOM | Gravity.CENTER_VERTICAL;
        fl.bottomMargin = ShareData.PxToDpi_xhdpi(196);
        this.addView(mCirclePanel, fl);

        //对比
        mCompareBtn = new ImageView(getContext());
        mCompareBtn.setImageResource(R.drawable.beautify_compare);
        mCompareBtn.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        fl = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        fl.gravity = Gravity.RIGHT | Gravity.TOP;
        fl.setMargins(0, PxToDpi_xhdpi(18), PxToDpi_xhdpi(18), 0);
        mCompareBtn.setLayoutParams(fl);
        this.addView(mCompareBtn);
        mCompareBtn.setOnTouchListener(new OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if (mUiEnabled && !mQuit)
                {
                    switch (event.getActionMasked())
                    {
                        case MotionEvent.ACTION_DOWN:
                            hideCircle(mCirclePanel);
                            MyBeautyStat.onClickByRes(R.string.美颜美图_滤镜页面_主页面_对比按钮);
                            isCompareBtnTouch = true;
                            if (mView.getOrgImage() != null && mShowBmp != null && mTempCompareBmp == null && !mView.getIsCompare())
                            {
                                mTempCompareBmp = mView.getOrgImage();
                                mView.setCompare(mShowBmp, true);
                                setViewUIEnabled(true);
                            }
                            break;
                        case MotionEvent.ACTION_POINTER_DOWN:
                        case MotionEvent.ACTION_UP:
                        case MotionEvent.ACTION_CANCEL:
                            isCompareBtnTouch = false;
                            if (mTempCompareBmp != null && mView.getOrgImage() != null && mView.getIsCompare())
                            {
                                mView.setCompare(mTempCompareBmp, false);
                                mTempCompareBmp = null;
                                setViewUIEnabled(true);
                            }
                            break;

                        default:
                            break;
                    }
                }
                return true;
            }
        });
        mCompareBtn.setVisibility(INVISIBLE);

        mWaitAnimDialog = new WaitAnimDialog((Activity) mContext);
        mWaitAnimDialog.SetGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, PxToDpi_xhdpi(320 + 38));

        mRecomView = new RecomDisplayMgr(getContext(), new RecomDisplayMgr.Callback()
        {
            @Override
            public void UnlockSuccess(BaseRes res)
            {

            }

            @Override
            public void OnCloseBtn()
            {

            }

            @Override
            public void OnBtn(int state)
            {

            }

            @Override
            public void OnClose()
            {

            }

            @Override
            public void OnLogin()
            {
                if (m_site != null) m_site.OnLogin(getContext());
            }
        });
        mRecomView.Create(this);
    }


    /**
     * @param params imgs :RotationImg[] / Bitmap
     */
    @Override
    public void SetData(HashMap<String, Object> params)
    {
        TongJi2.AddCountByRes(getContext(), R.integer.滤镜页);
        initParams(params);
    }

    private void initParams(HashMap<String, Object> params)
    {

        Object o = params.get(DataKey.BEAUTIFY_DEF_SEL_URI);
        if (o != null && o instanceof Integer)
        {
            mFilterUri = (int) o;
        }

        o = params.get(Beautify4Page.PAGE_ANIM_IMG_H);
        if (o != null && o instanceof Integer)
        {
            m_imgH = (int) o;
        }

        o = params.get(Beautify4Page.PAGE_ANIM_VIEW_H);
        if (o != null && o instanceof Integer)
        {
            m_viewH = (int) o;
        }

        o = params.get(Beautify4Page.PAGE_ANIM_VIEW_TOP_MARGIN);
        if (o != null && o instanceof Integer)
        {
            m_viewTopMargin = (int) o;
        }

        o = params.get("isBusiness");
        if (o != null && o instanceof Boolean)
        {
            isBusiness = (Boolean) o;
        }

        o = params.get("add_date");
        if (o != null && o instanceof Boolean)
        {
            mAddDataMark = (Boolean) o;
        }

        o = params.get("has_water_mark");
        if (o != null && o instanceof Boolean)
        {
            hasWatermark = (Boolean) o;
        }

        mOrgBmp = decodeImgs(params);

        if (mOrgBmp != null && !mOrgBmp.isRecycled())
        {
            mShowBmp = MakeBmpV2.CreateBitmapV2(mOrgBmp, 0, MakeBmpV2.FLIP_NONE, -1, (int) (mOrgBmp.getWidth() * 3f / 5f), (int) (mOrgBmp.getHeight() * 3f / 5f), Bitmap.Config.ARGB_8888);
            mView.setImage(mShowBmp);
        }
        ShowStarAnim();
    }


    private void initStartFilterRes()
    {
        if (mFilterUri > 0)
        {
            int tempUri = mFilterUri;
            mFilterUri = 0;
            mFilterAdapter.SetSelectByUri(tempUri, true, true);
        }
        else
        {//原图滤镜
            mFilterUri = 0;
            mFilterAdapter.SetSelectByUri(FilterAdapter.OriginalItemInfo.ORIGINAL_ITEM_URI, true, true, false);
        }
    }

    private void sendInitMsg()
    {
        mUiEnabled = false;
        setWaitUI(true, mContext.getResources().getString(R.string.processing));
        FilterHandler.FilterMsg initMsg = new FilterHandler.FilterMsg();
        initMsg.filterAlpha = mFilterAlpha;
        initMsg.filterUri = mFilterUri;
        initMsg.mOrgBmp = mShowBmp;
        initMsg.hasBlur = isBlured;
        initMsg.hasDark = isDarked;
        initMsg.res = mFilterRes;

        Message mMsg = mImageHandler.obtainMessage();
        mMsg.what = FilterHandler.MSG_INIT;
        mMsg.obj = initMsg;
        mImageHandler.sendMessage(mMsg);

        mUiEnabled = false;
        setSeekBarProgress(mFilterAlpha);
    }

    private Bitmap decodeImgs(HashMap<String, Object> params)
    {
        mImgs = params.get("imgs");
        Bitmap temp = null;
        int rotation;
        int flip;
        float scale = -1;
        if (mImgs instanceof RotationImg2[])
        {
            rotation = ((RotationImg2[]) mImgs)[0].m_degree;
            flip = ((RotationImg2[]) mImgs)[0].m_flip;
            temp = cn.poco.imagecore.Utils.DecodeShowImage((Activity) mContext, ((RotationImg2[]) mImgs)[0].m_img, rotation, scale, flip);
        }
        else if (mImgs instanceof Bitmap)
        {
            temp = (Bitmap) mImgs;
        }
        if (temp == null)
        {
            onBack();
        }
        return temp;
    }

    private void ShowStarAnim()
    {
        if (m_viewH > 0 && m_imgH > 0)
        {
            int tempStartY = (int) (ShareData.PxToDpi_xhdpi(90) + (m_viewH - mViewFrH) / 2f);
            float scaleX = mViewFrW * 1f / (float) mShowBmp.getWidth();
            float scaleY = mViewFrH * 1f / (float) mShowBmp.getHeight();
            float m_currImgH = mShowBmp.getHeight() * Math.min(scaleX, scaleY);
            float scaleH = Math.min(m_imgH * 1f / m_currImgH, 2.0f);
            ShowViewAnim(mView, tempStartY, 0, scaleH, 1f, SHOW_VIEW_ANIM);
        }
        else
        {
            initStartFilterRes();
        }
    }

    private void ShowViewAnim(final View view, int startY, int endY, float startScale, float endScale, int duration)
    {
        if (view != null)
        {

            AnimatorSet animatorSet = new AnimatorSet();
            ObjectAnimator object1 = ObjectAnimator.ofFloat(view, "scaleX", startScale, endScale);
            ObjectAnimator object2 = ObjectAnimator.ofFloat(view, "scaleY", startScale, endScale);
            ObjectAnimator object3 = ObjectAnimator.ofFloat(view, "translationY", startY, endY);
            ObjectAnimator object4 = ObjectAnimator.ofFloat(mBottomFr, "translationY", mBottomBarHeight + mBottomLayoutHeight, 0);
            animatorSet.setDuration(duration);
            animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
            animatorSet.playTogether(object1, object2, object3, object4);
            animatorSet.addListener(new AnimatorListenerAdapter()
            {
                @Override
                public void onAnimationStart(Animator animation)
                {
                    mUiEnabled = false;
                }

                @Override
                public void onAnimationEnd(Animator animation)
                {
                    mUiEnabled = true;

                    view.clearAnimation();
                    mBottomFr.clearAnimation();

                    initStartFilterRes();
                }
            });
            animatorSet.start();
        }
    }

    private void InitWaterMarkUI()
    {
        if (mWatermarkRecyclerView == null)
        {
            mWatermarkRecyclerView = new RecyclerView(mContext);
            ((SimpleItemAnimator) mWatermarkRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
            mWatermarkRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
            mWatermarkRecyclerView.setHasFixedSize(true);
            mWatermarkRecyclerView.setBackgroundColor(Color.TRANSPARENT);
            LayoutParams fp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            fp.gravity = Gravity.CENTER_VERTICAL;
            mWaterMarkFr.addView(mWatermarkRecyclerView, fp);
        }
    }

    @Override
    public void onBack()
    {
        if (mOnAnimationClickListener != null)
        {
            mOnAnimationClickListener.onAnimationClick(mCancelBtn);
        }
    }

    @Override
    public void onClose()
    {
        mUiEnabled = false;
        mQuit = true;

        removeAllMessage();
        clearExitDialog();

        if (DownloadMgr.getInstance() != null)
        {
            DownloadMgr.getInstance().RemoveDownloadListener(mDownloadListener);
        }
        mDownloadListener = null;

        cancelCountDownTimer();

        if (mRecomView != null)
        {
            mRecomView.ClearAllaa();
            mRecomView = null;
        }

        if (mView != null)
        {
            this.removeView(mView);
            mView.ReleaseMem();
            mView = null;
        }

        if (mTempCompareBmp != null && !mTempCompareBmp.isRecycled())
        {
            mTempCompareBmp.recycle();
            mTempCompareBmp = null;
        }

        if (mWaitAnimDialog != null)
        {
            mWaitAnimDialog.dismiss();
            mWaitAnimDialog = null;
        }

        if (mWatermarkAdapter != null)
        {
            mWatermarkAdapter.clear();
            mWatermarkAdapter = null;
        }

        SettingInfoMgr.Save(getContext());

        MyBeautyStat.onPageEndByRes(R.string.美颜美图_滤镜页面_主页面);
        TongJiUtils.onPageEnd(getContext(), R.string.滤镜);

        super.onClose();
    }

    @Override
    public void onPause()
    {
        TongJiUtils.onPagePause(getContext(), R.string.滤镜);
        super.onPause();
    }

    @Override
    public void onResume()
    {
        TongJiUtils.onPageResume(getContext(), R.string.滤镜);
        super.onResume();
    }


    public void setWaitUI(boolean flag, String str)
    {
        if (flag)
        {
            if (mWaitAnimDialog != null)
            {
                if (str != null && !str.equals("") && str.trim().length() > 0)
                {
                    mWaitAnimDialog.SetText(str);
                }
                mWaitAnimDialog.show();
            }
        }
        else
        {
            if (mWaitAnimDialog != null)
            {
                mWaitAnimDialog.hide();
            }
        }
    }

    private OnClickListener mBtnOnClickListener = new OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            if (mUiEnabled && !mQuit)
            {
                if (v == mCenterBtn)
                {
                    isFold = !isFold;
                    SetViewState(isFold);
                    SetBottomFrState(isFold);
                    mCenterBtn.setNewStatus(false);
                    mCenterBtn.setBtnStatus(true, isFold);
                    MyBeautyStat.onClickByRes(isFold ? R.string.美颜美图_滤镜页面_主页面_收回bar : R.string.美颜美图_滤镜页面_主页面_展开bar);
                }
                else if (v == mWaterMarkCenterBtn)
                {
                    if (isWatermarkState)
                    {
                        showWaterMarkFr(false);
                    }
                }
                else if (v == mBannerView)
                {
                    if (mFilterUri >= 247 && mFilterUri <= 250)
                    {
                        isShowBanner = false;
                        // 阿玛尼201708商业内置滤镜banner
                        CommonUtils.OpenBrowser(getContext(), "http://cav.adnonstop.com/cav/f53576787a/0065503008/?url=http://www.giorgioarmanibeauty.cn/landing-pages/170626acqua.html");
                    }
                }
            }
        }
    };

    private OnAnimationClickListener mOnAnimationClickListener = new OnAnimationClickListener()
    {
        @Override
        public void onAnimationClick(View v)
        {
            if (mUiEnabled && !mQuit)
            {
                if (v == mCancelBtn)
                {

                    if (mRecomView != null && mRecomView.IsShow())
                    {
                        mRecomView.OnCancel(true);
                        return;
                    }
                    MyBeautyStat.onClickByRes(R.string.美颜美图_滤镜页面_主页面_取消);
                    TongJi2.AddCountByRes(getContext(), R.integer.修图_素材美化_滤镜_取消);
                    if ((mFilterUri != FilterAdapter.OriginalItemInfo.ORIGINAL_ITEM_URI && mFilterUri != 0) || isBlured || isDarked)
                    {
                        showExitDialog();
                    }
                    else
                    {
                        sendCancelMessage();
                    }
                }
                else if (v == mOkBtn)
                {
                    MyBeautyStat.onClickByRes(R.string.美颜美图_滤镜页面_主页面_确认);
                    TongJi2.AddCountByRes(getContext(), R.integer.修图_素材美化_滤镜_确认);
                    sendSaveMessage();
                }
            }
        }

        @Override
        public void onTouch(View v)
        {

        }

        @Override
        public void onRelease(View v)
        {

        }
    };

    public VerFilterViewEx.ControlCallback mControlCallback = new VerFilterViewEx.ControlCallback()
    {
        @Override
        public void OnSelFaceIndex(int index)
        {

        }

        @Override
        public void OnAnimFinish()
        {

        }

        @Override
        public void OnFingerDown(int fingerCount)
        {
            //收起水印列表
            if (isWatermarkState)
            {
                showWaterMarkFr(false);
                return;
            }

            if (!isFold)
            {
                mCenterBtn.performClick();
            }
        }

        @Override
        public void OnFingerUp(int fingerCount)
        {
        }

        @Override
        public void OnClickWaterMask()
        {
            if (!isWatermarkState)
            {
                if (isFold)
                {
                    isFold = !isFold;
                    SetViewState(isFold);
                    SetBottomFrState(isFold);
                    mCenterBtn.setBtnStatus(true, isFold);
                }
            }

            showWaterMarkFr(!isWatermarkState);
        }
    };

    private void showCircle(MySeekBar seekBar, int progress)
    {
        float circleX = mConfig.def_sub_w + mConfig.def_sub_l * 2 + ShareData.PxToDpi_xhdpi(30 / 2) + seekBar.getLeft() + seekBar.getMaxDistans() * progress / 100f;
        float circleY = mCirclePanel.getHeight() / 2f - ShareData.PxToDpi_xhdpi(3);
        mCirclePanel.change(circleX, circleY, mCirclePanelRadius);
        mCirclePanel.setText(String.valueOf(progress));
        mCirclePanel.show();
    }

    private void hideCircle(CirclePanel circlePanel)
    {
        if (circlePanel != null)
        {
            circlePanel.hide();
        }
    }

    private void setViewFilterAlpha(int alpha)
    {
        mFilterAlpha = alpha;
        if (mView != null)
        {
            mView.setFilterAlpha(alpha);
        }
    }

    private void setViewUIEnabled(boolean enabled)
    {
        if (mView != null)
        {
            mView.SetUIEnabled(enabled);
        }
    }

    private void setSeekBarProgress(int progress)
    {
        if (mFilterAdapter != null)
        {
            mFilterAdapter.setCurrentAlphaProgress(progress);
        }
    }

    private void SetViewState(boolean isFold)
    {
        if (mView == null) return;

        mView.clearAnimation();
        int start, end;
        if (isFold)
        {
            start = mViewFrH;
            end = start + mBottomLayoutHeight;
        }
        else
        {
            start = ShareData.m_screenHeight - mBottomBarHeight;
            end = mViewFrH;
        }
        mUiEnabled = false;
        ValueAnimator valueAnimator = ValueAnimator.ofInt(start, end);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                if (mView != null)
                {
                    LayoutParams fp = (LayoutParams) mView.getLayoutParams();
                    fp.height = (int) animation.getAnimatedValue();
                    mView.setLayoutParams(fp);
                }
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                mUiEnabled = true;
            }
        });
        valueAnimator.setDuration(300);
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator.start();
    }

    private void SetBottomFrState(boolean isFold)
    {
        if (mBottomFr == null) return;

        mBottomFr.clearAnimation();
        int start;
        int end;
        start = isFold ? 0 : mBottomLayoutHeight;
        end = isFold ? mBottomLayoutHeight : 0;
        ObjectAnimator object = ObjectAnimator.ofFloat(mBottomFr, "translationY", start, end);
        object.setDuration(300);
        object.setInterpolator(new AccelerateDecelerateInterpolator());
        object.addListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationStart(Animator animation)
            {
                mUiEnabled = false;
            }

            @Override
            public void onAnimationEnd(Animator animation)
            {
                mUiEnabled = true;
            }
        });
        object.start();
    }

    private void setViewBottomBmp(Bitmap bottomBmp, boolean invalidate)
    {
        if (bottomBmp != null && mView != null)
        {
            mView.setOrgImage(bottomBmp, invalidate);
        }
    }

    private void setViewTopBmp(Bitmap topBmp, boolean invalidate)
    {
        if (topBmp != null && mView != null)
        {
            mView.setMaskImage(topBmp, invalidate);
        }
    }

    //模糊光影处理
    public void sendBlurDarkMessage()
    {
        mUiEnabled = false;
        setWaitUI(true, mContext.getResources().getString(R.string.processing));

        FilterHandler.BlurDarkMsg blurdarkMsg = new FilterHandler.BlurDarkMsg();
        blurdarkMsg.mOrgBmp = mShowBmp;
        blurdarkMsg.filterUri = mFilterUri;
        blurdarkMsg.filterAlpha = mFilterAlpha;
        blurdarkMsg.hasBlur = isBlured;
        blurdarkMsg.hasDark = isDarked;
        blurdarkMsg.res = mFilterRes;

        Message mMsg = mImageHandler.obtainMessage();
        mMsg.what = FilterHandler.MSG_BLUR_DARK;
        mMsg.obj = blurdarkMsg;
        mImageHandler.sendMessage(mMsg);
    }

    //取消滤镜调整
    public void sendCancelMessage()
    {
        if (mUiEnabled && !mQuit && mView != null && !mView.getIsTouch())
        {
            mUiEnabled = false;
            mQuit = true;
            Message msg = mImageHandler.obtainMessage();
            msg.what = FilterHandler.MSG_CANCEL;
            mImageHandler.sendMessage(msg);
        }
    }

    //保存当前滤镜调整
    public void sendSaveMessage()
    {

        if (mUiEnabled && !mQuit && mView != null && !mView.getIsTouch())
        {
            mUiEnabled = false;
            mQuit = true;
            setWaitUI(true, mContext.getString(R.string.saving));

            FilterHandler.SaveMsg saveMsg = new FilterHandler.SaveMsg();
            saveMsg.mImgs = mImgs;
            saveMsg.outSize = DEF_IMG_SIZE;
            saveMsg.filterAlpha = mFilterAlpha;
            saveMsg.filterUri = mFilterUri;
            saveMsg.res = mFilterRes;
            saveMsg.hasDark = isDarked;
            saveMsg.hasBlur = isBlured;
            saveMsg.hasDateMark = mAddDataMark;
            if (mFilterRes != null)
            {
                if (mFilterRes.m_isHaswatermark
                        && mWaterMarkId != WatermarkResMgr2.getInstance().GetNonWatermarkId(getContext()))
                {
                    saveMsg.hasWaterMark = true;
                    saveMsg.waterMarkId = mWaterMarkId;
                }
                else
                {
                    saveMsg.hasWaterMark = false;
                }
            }
            else
            {
                saveMsg.hasWaterMark = hasWatermark;
            }

            Message msg = mImageHandler.obtainMessage();
            msg.what = FilterHandler.MSG_SAVE;
            msg.obj = saveMsg;
            mImageHandler.sendMessage(msg);
        }
    }

    private FilterAdapter.OnDragCallBack mOnDragCallBack = new AbsDragAdapter.OnDragCallBack()
    {
        @Override
        public void onItemDelete(AbsDragAdapter.ItemInfo info, int position)
        {
            FilterAdapter.ItemInfo itemInfo = (FilterAdapter.ItemInfo) info;
            ThemeRes themeRes = ThemeResMgr2.getInstance().GetRes(itemInfo.m_uri);
            GroupRes res = new GroupRes();
            res.m_themeRes = themeRes;
            res.m_ress = new ArrayList<>();
            for (int i = 1; i < itemInfo.m_uris.length; i++)
            {
                res.m_ress.add(cn.poco.resource.FilterResMgr2.getInstance().GetRes(itemInfo.m_uris[i]));
            }
            cn.poco.resource.FilterResMgr2.getInstance().DeleteGroupRes(getContext(), res);

            int downIndex = mFilterAdapter.GetIndex((FilterAdapter.DownloadItemInfo.DOWNLOAD_ITEM_URI));
            ((FilterAdapter.DownloadItemInfo) mListInfos.get(downIndex)).setNum(cn.poco.resource.FilterResMgr2.getInstance().GetNoDownloadCount());
            mFilterAdapter.notifyItemChanged(downIndex);

            FilterAdapter.RecommendItemInfo recommendInfo = FilterResMgr.getRecommendInfo(getContext());
            int recommendIndex = mFilterAdapter.GetIndex((FilterAdapter.RecommendItemInfo.RECOMMEND_ITEM_URI));
            if (recommendInfo != null && recommendIndex >= 0)
            {
                mListInfos.set(recommendIndex, recommendInfo);
                mFilterAdapter.notifyItemChanged(recommendIndex);
            }
        }

        @Override
        public void onItemMove(AbsDragAdapter.ItemInfo info, int fromPosition, int toPosition)
        {
            AbsAdapter.ItemInfo itemInfo = mListInfos.get(fromPosition);
            int fromPos = fromPosition;
            int toPos = toPosition;
            if (itemInfo != null)
            {
                fromPos = ResourceUtils.HasId(FilterResMgr2.getInstance().GetOrderArr(), itemInfo.m_uri);
            }
            itemInfo = mListInfos.get(toPosition);
            if (itemInfo != null)
            {
                toPos = ResourceUtils.HasId(FilterResMgr2.getInstance().GetOrderArr(), itemInfo.m_uri);
            }
            if (fromPos >= 0 && toPos >= 0)
            {
                ResourceUtils.ChangeOrderPosition(FilterResMgr2.getInstance().GetOrderArr(), fromPos, toPos);
                FilterResMgr2.getInstance().SaveOrderArr();
            }
        }

        @Override
        public void onDragStart(AbsDragAdapter.ItemInfo itemInfo, int position)
        {
            int out = ResourceUtils.HasItem(cn.poco.resource.ThemeResMgr2.getInstance().sync_GetLocalRes(getContext(), null), itemInfo.m_uri);
            if (out >= 0)
            {
                mFilterFr.setCanDelete(false);
            }
            else
            {
                mFilterFr.setCanDelete(true);
            }
        }

        @Override
        public void onDragEnd(AbsDragAdapter.ItemInfo itemInfo, int position)
        {

        }

        @Override
        public void onLongClick(AbsDragAdapter.ItemInfo itemInfo, int position)
        {

        }
    };

    private FilterAdapter.OnItemClickListener mOnItemClickListener = new FilterAdapter.OnItemClickListener()
    {
        @Override
        public void onProgressChanged(MySeekBar seekBar, int progress)
        {
            showCircle(seekBar, progress);
            setViewFilterAlpha(progress);
        }

        @Override
        public void onStartTrackingTouch(MySeekBar seekBar)
        {
            showCircle(seekBar, seekBar.getProgress());
            setViewFilterAlpha(seekBar.getProgress());
            setViewUIEnabled(false);
        }

        @Override
        public void onStopTrackingTouch(MySeekBar seekBar)
        {
            if (mUiEnabled && !mQuit)
            {
                hideCircle(mCirclePanel);
                setViewFilterAlpha(seekBar.getProgress());
            }
            setViewUIEnabled(true);
            setSeekBarProgress(seekBar.getProgress());
        }

        @Override
        public void onSeekBarStartShow(MySeekBar seekBar)
        {
            if (mFilterFr != null)
            {
                mFilterFr.setUIEnable(false);
            }
        }

        @Override
        public void onFinishLayoutAlphaFr(MySeekBar seekBar)
        {
            if (mFilterFr != null)
            {
                mFilterFr.setUIEnable(true);
            }
            hideCircle(mCirclePanel);
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
        public void OnItemClick(BaseExAdapter.ItemInfo info, int parentIndex, int subIndex)
        {
            switch (info.m_uris[0])
            {
                case FilterAdapter.HeadItemInfo.HEAD_ITEM_URI:
                    if (mUiEnabled && mView != null && !mView.getIsTouch() && !mView.getIsCompare())
                    {
                        isBlured = ((FilterAdapter.HeadItemInfo) info).isSelectBlur;
                        isDarked = ((FilterAdapter.HeadItemInfo) info).isSelectDark;
                        sendBlurDarkMessage();
                    }
                    break;
                case FilterAdapter.DownloadItemInfo.DOWNLOAD_ITEM_URI:// 下载更多
                    cancelCountDownTimer();
                    removeBannerView();
                    TongJi2.AddCountByRes(getContext(), R.integer.修图_素材美化_滤镜_下载更多);
                    MyBeautyStat.onClickByRes(R.string.美颜美图_滤镜页面_主页面_滤镜下载更多);
                    m_site.OpenDownloadMore(getContext(), ResType.FILTER);
                    break;
                case FilterAdapter.OriginalItemInfo.ORIGINAL_ITEM_URI:
                    if (mFilterUri != info.m_uri)
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
                        mFilterUri = 0;
                        mFilterRes = filterRes;
                        mFilterAlpha = mFilterRes != null ? mFilterRes.m_filterAlpha : 100;
                        mTongJiId = R.integer.修图_素材美化_滤镜_Original;
                        sendInitMsg();
                        setWaterMark(false);
                    }
                    cancelCountDownTimer();
                    removeBannerView();
                    break;
                case FilterAdapter.RecommendItemInfo.RECOMMEND_ITEM_URI:
                    // 额外更多素材
                    cancelCountDownTimer();
                    removeBannerView();
                    TongJi2.AddCountByRes(getContext(), R.integer.修图_素材美化_滤镜_推荐位);
                    MyBeautyStat.onClickByRes(R.string.美颜美图_滤镜页面_主页面_滤镜推荐位);
                    openRecommendView((ArrayList<RecommendRes>) ((FilterAdapter.ItemInfo) info).m_ex, ResType.FILTER.GetValue());
                    break;
                default:

                    if (subIndex > 0)
                    {
                        if (mFilterUri != info.m_uris[subIndex])
                        {
                            mFilterUri = info.m_uris[subIndex];

                            mFilterRes = null;
                            if (info instanceof FilterAdapter.ItemInfo
                                    && ((FilterAdapter.ItemInfo) info).m_ex != null
                                    && ((FilterAdapter.ItemInfo) info).m_ex instanceof FilterGroupRes
                                    && ((FilterGroupRes) ((FilterAdapter.ItemInfo) info).m_ex).m_group != null)
                            {
                                mFilterRes = ((FilterGroupRes) ((FilterAdapter.ItemInfo) info).m_ex).m_group.get(subIndex - 1);
                            }
                            if (mFilterRes == null)
                            {
                                mFilterRes = cn.poco.resource.FilterResMgr2.getInstance().GetRes(mFilterUri);
                            }

                            if (mFilterRes != null)
                            {
                                isDarked = mFilterRes.m_isHasvignette;
                                mTongJiId = mFilterRes.m_tjId;
                                sendFilterRes(mFilterRes);
                            }
                            ((FilterAdapter.HeadItemInfo) mListInfos.get(0)).isSelectBlur = isBlured;
                            ((FilterAdapter.HeadItemInfo) mListInfos.get(0)).isSelectDark = isDarked;
                            mFilterAdapter.notifyItemChanged(0);


                            //阿玛尼商业201708
                            if (mFilterUri == 247 || mFilterUri == 248 || mFilterUri == 249 || mFilterUri == 250)
                            {
                                if (mBannerView == null && isShowBanner)
                                {
                                    mBannerView = new FrameLayout(getContext());
                                    mBannerView.setOnClickListener(mBtnOnClickListener);
                                    LayoutParams fl = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                                    fl.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
                                    fl.bottomMargin = ShareData.PxToDpi_xhdpi(320 - 2);//误差
                                    mBottomFr.addView(mBannerView, fl);
                                    {

                                        mBannerImageView = new ImageView(getContext());
                                        fl = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                                        fl.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
                                        mBannerView.addView(mBannerImageView, fl);

                                        TextView textView = new TextView(getContext());
                                        textView.setOnClickListener(new OnClickListener()
                                        {
                                            @Override
                                            public void onClick(View v)
                                            {
                                                isShowBanner = false;
                                                removeBannerView();
                                                cancelCountDownTimer();
                                            }
                                        });
                                        fl = new LayoutParams(ShareData.PxToDpi_xhdpi(60), ShareData.PxToDpi_xhdpi(60));
                                        fl.gravity = Gravity.TOP | Gravity.RIGHT;
                                        fl.topMargin = ShareData.PxToDpi_xhdpi(16);
                                        mBannerView.addView(textView, fl);
                                    }
                                }
                                String trackStr = null;
                                int resId = 0;
                                if (mFilterUri == 247)
                                {
                                    trackStr = "http://cav.adnonstop.com/cav/fe0a01a3d9/0065503130/?url=https://a-m-s-ios.poco.cn/images/blank.gif";
                                    resId = R.drawable.filter_banner_giorgio_armani_247;
                                }
                                else if (mFilterUri == 248)
                                {
                                    trackStr = "http://cav.adnonstop.com/cav/fe0a01a3d9/0065503131/?url=https://a-m-s-ios.poco.cn/images/blank.gif";
                                    resId = R.drawable.filter_banner_giorgio_armani_248;
                                }
                                else if (mFilterUri == 249)
                                {
                                    trackStr = "http://cav.adnonstop.com/cav/fe0a01a3d9/0065503132/?url=https://a-m-s-ios.poco.cn/images/blank.gif";
                                    resId = R.drawable.filter_banner_giorgio_armani_249;
                                }
                                else if (mFilterUri == 250)
                                {
                                    trackStr = "http://cav.adnonstop.com/cav/fe0a01a3d9/0065503133/?url=https://a-m-s-ios.poco.cn/images/blank.gif";
                                    resId = R.drawable.filter_banner_giorgio_armani_250;
                                }
                                if (isShowBanner && mBannerImageView != null)
                                {
                                    BitmapFactory.Options options = new BitmapFactory.Options();
                                    options.inJustDecodeBounds = true;
                                    BitmapFactory.decodeResource(getResources(), resId, options);
                                    float scale = options.outWidth / options.outHeight * 1f;
                                    options.inJustDecodeBounds = false;
                                    ViewGroup.LayoutParams layoutParams = mBannerImageView.getLayoutParams();
                                    layoutParams.height = (int) (ShareData.getScreenW() / scale);
                                    layoutParams.width = ShareData.getScreenW();
                                    mBannerImageView.requestLayout();
                                    mBannerImageView.setImageResource(resId);
                                }
                                if (!TextUtils.isEmpty(trackStr))
                                {
                                    Utils.UrlTrigger(getContext(), trackStr);
                                }

                                cancelCountDownTimer();
                            }
                            else
                            {
                                removeBannerView();
                                cancelCountDownTimer();
                            }
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

        @Override
        public void OnItemClick(AbsAdapter.ItemInfo info, int index)
        {

        }
    };

    private void removeBannerView()
    {
        if (mBannerView != null && mBottomFr != null)
        {
            mBottomFr.removeView(mBannerView);
            mBannerImageView = null;
            mBannerView = null;
        }
    }

    private void cancelCountDownTimer()
    {
        if (mCountDownTimer != null)
        {
            mCountDownTimer.cancel();
            mCountDownTimer = null;
        }
    }

    private FilterRes mFilterRes = null;

    private void sendFilterRes(FilterRes filterRes)
    {
        if (filterRes != null && filterRes.m_datas != null && filterRes.m_datas.length > 0)
        {
            mUiEnabled = false;
            setWaitUI(true, "");
            FilterHandler.FilterResMsg msg = new FilterHandler.FilterResMsg();
            msg.mOrgBmp = mShowBmp;
            msg.filterAlpha = mFilterAlpha = filterRes.m_filterAlpha;
            msg.filterUri = mFilterUri;
            msg.hasBlur = isBlured;
            msg.hasDark = filterRes.m_isHasvignette;
            msg.res = filterRes;

            Message mMsg = mImageHandler.obtainMessage();
            mMsg.what = FilterHandler.MSG_FILTER_RES;
            mMsg.obj = msg;
            mImageHandler.sendMessage(mMsg);
        }
    }

    private void setWaterMark(boolean hasWatermark)
    {
        if (mView != null)
        {
            this.hasWatermark = hasWatermark;
            mView.setDrawWaterMark(hasWatermark);
            if (mWatermarkResArr == null)
            {
                mWatermarkResArr = WatermarkResMgr2.getInstance().sync_GetLocalRes(getContext(), null);
            }
            if (hasWatermark)
            {
                WatermarkItem item = getWaterMarkById(mWaterMarkId);
                if (item != null)
                {
                    mWaterMarkId = item.mID;
                    if (isDoWaterAlphaAnim)
                    {
                        mView.AddWaterMarkWithAnim(MakeBmpV2.DecodeImage(getContext(), item.res, 0, -1, -1, -1, Bitmap.Config.ARGB_8888), mWaterMarkId == WatermarkResMgr2.getInstance().GetNonWatermarkId(getContext()));
                    }
                    else
                    {
                        mView.AddWaterMark(MakeBmpV2.DecodeImage(getContext(), item.res, 0, -1, -1, -1, Bitmap.Config.ARGB_8888), mWaterMarkId == WatermarkResMgr2.getInstance().GetNonWatermarkId(getContext()));
                    }
                    isDoWaterAlphaAnim = false;
                }
            }
        }
    }

    private WatermarkItem getWaterMarkById(int id)
    {
        if (mWatermarkResArr == null)
        {
            mWatermarkResArr = WatermarkResMgr2.getInstance().sync_GetLocalRes(getContext(), null);
        }
        for (WatermarkItem item : mWatermarkResArr)
        {
            if (item != null && item.mID == id) return item;
        }
        return null;
    }

    private void showWaterMarkFr(final boolean show)
    {
        if (show)
        {
            mWaterMarkBottomFr.setVisibility(VISIBLE);
        }
        isWatermarkState = show;
        mWaterMarkCenterBtn.setBtnStatus(true, !show);

        if (show)
        {
            InitWaterMarkUI();

            if (mWaterMarkFr.getBackground() == null)
            {
                //水印素材区域毛玻璃处理
                Bitmap temp = CommonUtils.GetScreenBmp((Activity) getContext(), ShareData.m_screenWidth, ShareData.m_screenHeight);
                Bitmap out = MakeBmp.CreateFixBitmap(temp, temp.getWidth(), ShareData.PxToDpi_xhdpi(232), MakeBmp.POS_END, 0, Bitmap.Config.ARGB_8888);
                if (out != temp)
                {
                    temp.recycle();
                    temp = null;
                }
                Bitmap mWaterMaskBmp = filter.fakeGlassBeauty(out, 0x99000000);//60%黑色
                mWaterMarkFr.setBackgroundDrawable(new BitmapDrawable(getResources(), mWaterMaskBmp));
            }

            if (mWatermarkAdapter == null)
            {
                mWatermarkAdapter = new WatermarkAdapter(getContext());
                if (mWatermarkResArr == null)
                {
                    mWatermarkResArr = WatermarkResMgr2.getInstance().sync_GetLocalRes(getContext(), null);
                }
                mWatermarkAdapter.SetData(mWatermarkResArr);//水印数据集
                mWatermarkAdapter.SetSelectedId(mWaterMarkId);
                mWatermarkAdapter.setListener(new WatermarkAdapter.OnItemClickListener()
                {
                    @Override
                    public void onItemClick(int position, WatermarkItem item)
                    {
                        mView.setDrawWaterMark(true);
                        mWaterMarkId = item.mID;
                        if (isDoWaterAlphaAnim)
                        {
                            mView.AddWaterMarkWithAnim(MakeBmpV2.DecodeImage(getContext(), item.res, 0, -1, -1, -1, Bitmap.Config.ARGB_8888), item.mID == WatermarkResMgr2.getInstance().GetNonWatermarkId(getContext()));
                        }
                        else
                        {
                            mView.AddWaterMark(MakeBmpV2.DecodeImage(getContext(), item.res, 0, -1, -1, -1, Bitmap.Config.ARGB_8888), item.mID == WatermarkResMgr2.getInstance().GetNonWatermarkId(getContext()));
                        }
                        SettingInfoMgr.GetSettingInfo(getContext()).SetPhotoWatermarkId(mWaterMarkId);
                        isDoWaterAlphaAnim = false;
                        scroll2Center(position);
                    }
                });
                mWatermarkRecyclerView.setAdapter(mWatermarkAdapter);
            }
        }

        float start = show ? ShareData.PxToDpi_xhdpi(320) : 0;
        float end = show ? 0 : ShareData.PxToDpi_xhdpi(320);
        final ObjectAnimator object = ObjectAnimator.ofFloat(mWaterMarkBottomFr, "translationY", start, end);
        object.setDuration(300);
        object.setInterpolator(new AccelerateDecelerateInterpolator());
        object.addListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationStart(Animator animation)
            {
                mUiEnabled = false;
            }

            @Override
            public void onAnimationEnd(Animator animation)
            {
                mUiEnabled = true;
                if (!show)
                {
                    mWaterMarkBottomFr.setVisibility(GONE);
                }
                else
                {
                    mWatermarkRecyclerView.smoothScrollToPosition(mWatermarkAdapter.GetPosition());
                    mWaterMarkBottomFr.postDelayed(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            scroll2Center(mWatermarkAdapter.GetPosition());
                        }
                    }, 150);
                }
            }
        });
        object.start();
    }

    private void scroll2Center(int position)
    {
        if (mWatermarkRecyclerView != null)
        {
            View view = mWatermarkRecyclerView.getLayoutManager().findViewByPosition(position);
            if (view != null)
            {
                float center = mWatermarkRecyclerView.getWidth() / 2f;
                float viewCenter = view.getX() + view.getWidth() / 2f;
                mWatermarkRecyclerView.smoothScrollBy((int) (viewCenter - center), 0);
            }
        }
    }

    /**
     * 推荐位view
     *
     * @param ress
     * @param type
     */
    protected void openRecommendView(ArrayList<RecommendRes> ress, int type)
    {
        //推荐位
        RecommendRes recommendRes = null;
        if (ress != null && ress.size() > 0)
        {
            recommendRes = ress.get(0);
        }
        if (recommendRes != null && mRecomView != null)
        {
            mRecomView.SetBk(CommonUtils.GetScreenBmp((Activity) getContext(), ShareData.m_screenWidth / 8, ShareData.m_screenHeight / 8), true);
            mRecomView.SetDatas(recommendRes, type);
            mRecomView.Show();
        }
    }

    private class UIHandler extends Handler
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case FilterHandler.MSG_INIT:
                    MsgInit(msg);
                    setCompareBtnState(false);
                    break;
                case FilterHandler.MSG_BLUR_DARK:
                {
                    MsgBlurDark(msg);
                    setCompareBtnState(false);
                    break;
                }
                case FilterHandler.MSG_SAVE:
                {
                    MsgSave(msg);
                    break;
                }
                case FilterHandler.MSG_CANCEL:
                {
                    MsgCancel(msg);
                    break;
                }
                case FilterHandler.MSG_FILTER_RES:
                {
                    setCompareBtnState(false);
                    MsgFilterRes(msg);
                    break;
                }
            }
        }

    }

    private void setCompareBtnState(boolean forceHide)
    {
        if ((forceHide || (mFilterUri == FilterType.NONE && !isBlured && !isDarked)) && mCompareBtn.getVisibility() == VISIBLE)
        {
            mCompareBtn.animate().scaleX(0).scaleY(0).setDuration(100).setListener(new AnimatorListenerAdapter()
            {
                @Override
                public void onAnimationEnd(Animator animation)
                {
                    mCompareBtn.setVisibility(INVISIBLE);
                }
            });
        }
        else if ((mFilterUri != FilterType.NONE || isBlured || isDarked) && mCompareBtn.getVisibility() != VISIBLE)
        {
            mCompareBtn.setScaleX(0);
            mCompareBtn.setScaleY(0);
            mCompareBtn.setVisibility(VISIBLE);
            mCompareBtn.animate().scaleX(1).scaleY(1).setDuration(100).setListener(null);
        }
    }

    private void MsgFilterRes(Message msg)
    {
        FilterHandler.FilterResMsg params = (FilterHandler.FilterResMsg) msg.obj;
        msg.obj = null;

        setViewBottomBmp(params.mBottomBmp, false);
        setViewTopBmp(params.mTopBmp, true);
        setViewFilterAlpha(params.filterAlpha);
        setSeekBarProgress(params.filterAlpha);

        if (params.res != null && params.res instanceof FilterRes)
        {
            setWaterMark(((FilterRes) params.res).m_isHaswatermark);
        }

        params.mTopBmp = null;
        params.mBottomBmp = null;

        setWaitUI(false, "");
        mUiEnabled = true;

        if (mFilterUri >= 247 && mFilterUri <= 250)
        {
            cancelCountDownTimer();
            //2秒后消失
            mCountDownTimer = new CountDownTimer(2000L, 1000L)
            {
                @Override
                public void onTick(long millisUntilFinished)
                {

                }

                @Override
                public void onFinish()
                {
                    removeBannerView();
                    mCountDownTimer = null;
                }
            };
            mCountDownTimer.start();
        }
    }

    /*滤镜init*/
    private void MsgInit(Message msg)
    {
        FilterHandler.FilterMsg params = (FilterHandler.FilterMsg) msg.obj;
        msg.obj = null;

        setViewBottomBmp(params.mBottomBmp, false);
        setViewTopBmp(params.mTopBmp, true);
        setViewFilterAlpha(params.filterAlpha);
        setSeekBarProgress(params.filterAlpha);


        params.mTopBmp = null;
        params.mBottomBmp = null;

        setWaitUI(false, "");
        mUiEnabled = true;
    }

    /*模糊阴影*/
    private void MsgBlurDark(Message msg)
    {
        FilterHandler.BlurDarkMsg params = (FilterHandler.BlurDarkMsg) msg.obj;
        msg.obj = null;

        setViewBottomBmp(params.mBottomBmp, false);
        setViewTopBmp(params.mTopBmp, true);
        setViewFilterAlpha(params.filterAlpha);
        setSeekBarProgress(params.filterAlpha);

        params.mBottomBmp = null;
        params.mTopBmp = null;

        setWaitUI(false, "");
        mUiEnabled = true;
    }

    /*取消，返回上级编辑*/
    private void MsgCancel(Message msg)
    {
        if (m_site != null)
        {
            HashMap<String, Object> params = new HashMap<>();
            params.put("img", mOrgBmp);
            params.putAll(getBackAnimParam());
            m_site.OnBack(getContext(), params);
        }
    }

    private HashMap<String, Object> getBackAnimParam()
    {
        HashMap<String, Object> params = new HashMap<>();
        params.put(Beautify4Page.PAGE_BACK_ANIM_VIEW_TOP_MARGIN, (mView.getHeight() - mViewFrH) / 2f);
        params.put(Beautify4Page.PAGE_BACK_ANIM_IMG_H, mView.getImgHeight());
        return params;
    }

    /*保存，返回上级编辑*/
    private void MsgSave(Message msg)
    {
        setWaitUI(false, "");
        if (m_site != null)
        {
            HashMap<String, Object> params = new HashMap<>();
            params.put("img", (Bitmap) msg.obj);
            params.putAll(getBackAnimParam());

            // 阿玛尼商业201707 [247, 248, 249, 250]
            if (mFilterUri >= 247 && mFilterUri <= 250)
            {
                //params.put("business_channel_value", ChannelValue.AD77);
            }

            //当选择的滤镜无水印功能，返回默认，若选择的滤镜处理了水印功能，返回取消水印记
            if (mFilterUri != 0)
            {
                params.put("has_water_mark", hasWatermark = false);
                params.put("water_mark_id", WatermarkResMgr2.getInstance().GetNonWatermarkId(getContext()));
            }
            WatermarkItem item = getWaterMarkById(mWaterMarkId);
            if (item != null)
            {
                TongJi2.AddCountByRes(getContext(), item.mTongJiId);//水印统计
            }
            if (mTongJiId > 0)
            {
                if (mTongJiId == R.integer.修图_素材美化_滤镜_Original)
                {
                    TongJi2.AddCountByRes(getContext(), mTongJiId);
                }
                else
                {
                    TongJi2.AddCountById(mTongJiId + "");//统计滤镜
                }
            }
            if (isBlured) TongJi2.AddCountByRes(getContext(), R.integer.修图_素材美化_滤镜_虚化);
            if (isDarked) TongJi2.AddCountByRes(getContext(), R.integer.修图_素材美化_滤镜_暗角);

            sendSensorsData();
            msg.obj = null;
            m_site.OnSave(params);
        }
    }

    /**
     * 神策埋点
     */
    private void sendSensorsData()
    {
        String filterId = "0";
        if (mTongJiId > 0)
        {
            if (mTongJiId == R.integer.修图_素材美化_滤镜_Original)
            {
                filterId = "0";
            }
            else
            {
                filterId = String.valueOf(mTongJiId);//统计滤镜
            }
        }
        MyBeautyStat.onUseFilter(isBlured, isDarked, filterId, mFilterAlpha);
    }

    private void removeAllMessage()
    {

        if (mImageThread != null)
        {
            mImageThread.quit();
            mImageThread = null;
        }

        if (mUIHandler != null)
        {
            mUIHandler.removeMessages(FilterHandler.MSG_SAVE);
            mUIHandler.removeMessages(FilterHandler.MSG_INIT);
            mUIHandler.removeMessages(FilterHandler.MSG_BLUR_DARK);
            mUIHandler.removeMessages(FilterHandler.MSG_CANCEL);
            mUIHandler.removeMessages(FilterHandler.MSG_FILTER_RES);
            mUIHandler = null;
        }
        if (mImageHandler != null)
        {
            mImageHandler.removeMessages(FilterHandler.MSG_SAVE);
            mImageHandler.removeMessages(FilterHandler.MSG_INIT);
            mImageHandler.removeMessages(FilterHandler.MSG_BLUR_DARK);
            mImageHandler.removeMessages(FilterHandler.MSG_CANCEL);
            mImageHandler.removeMessages(FilterHandler.MSG_FILTER_RES);
            mImageHandler.ReleaseMem();
            mImageHandler = null;
        }
    }

    @Override
    public void onPageResult(int siteID, HashMap<String, Object> params)
    {
        if (siteID == SiteID.FILTER_DOWNLOAD_MORE)
        {   //滤镜管理
            if (params != null)
            {
                boolean isChange = false;
                Object o = params.get("is_change");
                if (o instanceof Boolean)
                {
                    isChange = (Boolean) o;
                }

                if (isChange)
                {
                    mListInfos = FilterResMgr.GetFilterRes(getContext(), false);
                    mFilterAdapter.SetData(mListInfos);
                    mFilterAdapter.CancelSelect();
                    mFilterAdapter.setOpenIndex(-1);
                    mFilterAdapter.notifyDataSetChanged();

                    boolean needCallback = false;
                    int uri = mFilterUri;
                    int[] ints = mFilterAdapter.GetSubIndexByUri(mFilterUri);
                    //滤镜已删除，使用原图滤镜
                    if (ints == null || ints[0] < 0 || ints[1] < 0) {
                        uri = FilterAdapter.OriginalItemInfo.ORIGINAL_ITEM_URI;
                        needCallback = true;
                    } else {
                        if (mFilterUri == 0) {
                            uri = FilterAdapter.OriginalItemInfo.ORIGINAL_ITEM_URI;
                            needCallback = true;
                        }
                    }
                    mFilterUri = 0;
                    mFilterAdapter.SetSelectByUri(uri, true, true, needCallback);
                }
            }
        }

        if (siteID == SiteID.FILTER_DETAIL)
        { //滤镜详情页马上使用
            if (params != null && params.get("material_id") != null && params.get("material_id") instanceof Integer)
            {
                final int id = (Integer) params.get("material_id");
                if (id != 0)
                {
                    mFilterUri = 0;
                    mListInfos = FilterResMgr.GetFilterRes(getContext(), false);
                    mFilterAdapter.SetData(mListInfos);
                    mFilterAdapter.SetSelectByUri(id);
                }
            }
        }

        if (siteID == SiteID.LOGIN || siteID == SiteID.REGISTER_DETAIL || siteID == SiteID.RESETPSW)
        {
            if (mRecomView != null)
            {
                mRecomView.UpdateCredit();
            }
        }
    }


    public DownloadMgr.DownloadListener mDownloadListener = new AbsDownloadMgr.DownloadListener()
    {
        @Override
        public void OnDataChange(int resType, int downloadId, IDownload[] resArr)
        {
            if (resArr != null && ((BaseRes) resArr[0]).m_type == BaseRes.TYPE_LOCAL_PATH)
            {
                if (resType == ResType.FILTER.GetValue())
                {
                    if (mFilterAdapter != null)
                    {
                        ArrayList<FilterAdapter.ItemInfo> dst = FilterResMgr.GetFilterRes(getContext(), false);
                        if (mListInfos != null && dst.size() > mListInfos.size())
                        {
                            int selectIndex = mFilterAdapter.GetSelectIndex();
                            mFilterAdapter.notifyItemDownLoad(dst.size() - mListInfos.size());
                            int newSelectIndex = mFilterAdapter.GetSelectIndex();

                            if (newSelectIndex < dst.size() && newSelectIndex >= 0) {
                                FilterAdapter.ItemInfo itemInfo = dst.get(newSelectIndex);
                                //判断新选择的是否是推荐素材，如果是则重新选择之前的
                                if (itemInfo != null && (itemInfo instanceof FilterAdapter.RecommendItemInfo
                                        || itemInfo.m_uris[0] == FilterAdapter.RecommendItemInfo.RECOMMEND_ITEM_URI)) {
                                    mFilterAdapter.notifyItemDownLoad(selectIndex - newSelectIndex);
                                }
                            }
                        }
                        mListInfos = dst;
                        mFilterAdapter.SetData(mListInfos);
                        mFilterAdapter.notifyDataSetChanged();

                    }
                }
            }
        }
    };

    private CloudAlbumDialog mExitDialog;

    private void showExitDialog()
    {
        if (mExitDialog == null)
        {
            mExitDialog = new CloudAlbumDialog(getContext(), ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            ImageUtils.AddSkin(getContext(), mExitDialog.getOkButtonBg());
            mExitDialog.setCancelable(true).setCancelButtonText(R.string.cancel).setOkButtonText(R.string.ensure).setMessage(R.string.confirm_back).setListener(new CloudAlbumDialog.OnButtonClickListener()
            {
                @Override
                public void onOkButtonClick()
                {
                    if (mExitDialog != null)
                    {
                        mExitDialog.dismiss();
                    }
                    sendCancelMessage();
                }

                @Override
                public void onCancelButtonClick()
                {
                    if (mExitDialog != null)
                    {
                        mExitDialog.dismiss();
                    }
                }
            });
        }
        mExitDialog.show();
    }

    private void clearExitDialog()
    {
        if (mExitDialog != null)
        {
            mExitDialog.dismiss();
            mExitDialog.setListener(null);
            mExitDialog = null;
        }
    }
}
