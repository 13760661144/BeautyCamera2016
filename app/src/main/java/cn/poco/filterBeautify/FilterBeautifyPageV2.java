package cn.poco.filterBeautify;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.circle.ctrls.SharedTipsView;

import java.util.ArrayList;
import java.util.HashMap;

import cn.poco.acne.view.CirclePanel;
import cn.poco.advanced.ImageUtils;
import cn.poco.beautify.RecomDisplayMgr;
import cn.poco.camera.CameraSetDataKey;
import cn.poco.camera.ImageFile2;
import cn.poco.camera.RotationImg2;
import cn.poco.camera2.CameraFilterConfig;
import cn.poco.camera3.ui.PreviewBackMsgToast;
import cn.poco.camera3.util.CameraPercentUtil;
import cn.poco.cloudalbumlibs.utils.CloudAlbumDialog;
import cn.poco.face.FaceDataV2;
import cn.poco.filter4.FilterResMgr;
import cn.poco.filter4.WatermarkAdapter;
import cn.poco.filter4.WatermarkItem;
import cn.poco.filter4.recycle.FilterAdapter;
import cn.poco.filter4.recycle.FilterBaseView;
import cn.poco.filterBeautify.site.FilterBeautifyPageSite;
import cn.poco.filterPendant.MyStatusButton;
import cn.poco.framework.BaseSite;
import cn.poco.framework.DataKey;
import cn.poco.framework.IPage;
import cn.poco.framework.SiteID;
import cn.poco.image.filter;
import cn.poco.login.UserMgr;
import cn.poco.loginlibs.info.UserInfo;
import cn.poco.makeup.MySeekBar;
import cn.poco.recycleview.AbsAdapter;
import cn.poco.recycleview.BaseExAdapter;
import cn.poco.resource.AbsDownloadMgr;
import cn.poco.resource.BaseRes;
import cn.poco.resource.DownloadMgr;
import cn.poco.resource.FilterGroupRes;
import cn.poco.resource.FilterRes;
import cn.poco.resource.FilterResMgr2;
import cn.poco.resource.IDownload;
import cn.poco.resource.RecommendRes;
import cn.poco.resource.ResType;
import cn.poco.resource.ResourceUtils;
import cn.poco.resource.WatermarkResMgr2;
import cn.poco.setting.SettingInfo;
import cn.poco.setting.SettingInfoMgr;
import cn.poco.share.SharePage;
import cn.poco.share.ShareSendUtil;
import cn.poco.share.SimpleSharePage;
import cn.poco.statisticlibs.PhotoStat;
import cn.poco.statistics.MyBeautyStat;
import cn.poco.statistics.TongJi2;
import cn.poco.statistics.TongJiUtils;
import cn.poco.system.SysConfig;
import cn.poco.tianutils.AnimationView;
import cn.poco.tianutils.CommonUtils;
import cn.poco.tianutils.MakeBmp;
import cn.poco.tianutils.MakeBmpV2;
import cn.poco.tianutils.NetState;
import cn.poco.tianutils.ShareData;
import cn.poco.utils.FileUtil;
import cn.poco.utils.OnAnimationClickListener;
import cn.poco.utils.Utils;
import cn.poco.utils.WaitAnimDialog;
import cn.poco.view.PictureView;
import cn.poco.view.material.VerFilterViewEx;
import my.beautyCamera.R;

/**
 * @author lmx
 *         Created by lmx on 2017-12-04.
 */

public class FilterBeautifyPageV2 extends IPage implements VerFilterViewEx.ControlCallback
{
    private FilterBeautifyPageSite mSite;

    private int mViewW;
    private int mViewH;
    private int mBarH;
    private int mCirCleRadius;

    private CirclePanel mBeautyCircle;
    private CirclePanel mAlphaCircle;
    private MyStatusButton mFCenterBtn;
    private MyStatusButton mWCenterBtn;
    private ImageView mCompareBtn;
    private FrameLayout mSaveBtn;
    private FrameLayout mBarFr;
    private BtnFr mBackBtn;
    private BtnFr mFilterBtn;
    private BtnFr mBeautyBtn;
    private BtnFr mShareBtn;
    private MySeekBar mSeekBar;
    private PictureView mView;
    private WaitAnimDialog mWaitDialog;
    private RecomDisplayMgr mRecomView;


    private int mWaterMarkId = -1;
    private boolean mFromCamera;
    private boolean isShowShareBtn = true;
    private boolean isRestore;
    private boolean hasWaterMark;
    private FilterBeautyParams mFilterBeautyParams;

    //统计id
    private int mFilterTjId = -1;
    private FilterRes mFilterRes;
    private int mFilterUri;
    private int mAdjustValue;//微调数值（磨皮美颜）
    private int mFilterAlpha;//滤镜透明度
    private float mRatio = -1;
    private int mOrientation;
    private boolean isBlured;
    private boolean isDarked;
    private boolean isDoSaved;//是否保存过

    private Object mImgs;       //原图框架
    private Bitmap mOrgBmp;     //原图框架缓存
    private Bitmap mTmpBmp;     //对比缓存图
    private String mSavePath;

    private UIHandler mUiHandler;
    private BeautyHandler mImageHandler;
    private HandlerThread mHandlerThread;

    private boolean mUiEnable = true;

    public static final String FILTER_BEAUTY_PARAMS = "filter_beauty_params";
    public static final String RESTORE_IMGS = "restore_imgs";
    public static final String ORIENTATION = "orientation";
    public static final String IS_BACK = "is_back";
    public static final String ADJUST_VALUE = "adjust_value";
    public static final String FILTER_URI = "filter_uri";
    public static final String FILTER_ALPHA = "filter_alpha";
    public static final String RATIO = "ratio";
    public static final String IS_BLUR = "is_blur";
    public static final String IS_DARK = "is_dark";
    public static final String IS_SHAPE = "is_shape";
    public static final String WATERMARKID = "water_mark_id";

    //水印recycler view
    private boolean isShowWaterMarkView;
    private boolean isDoingWVAnim;
    private int mWatermarkFrTranslationY;
    private FrameLayout mWatermarkFr;
    private ArrayList<WatermarkItem> mWatermarkResArr;
    private RecyclerView mWatermarkRecyclerView;
    private WatermarkAdapter mWatermarkAdapter;
    private boolean isDoWaterAlphaAnim = true;    //水印标识alpha动画

    //滤镜recycler view
    private boolean isShowFilterView;
    private boolean isDoingFVAnim;
    private int mFilterFrTranslationY;
    private FrameLayout mFilterFr;
    private CameraFilterConfig mConfig;
    private ArrayList<FilterAdapter.ItemInfo> mFilterItemInfos;
    private FilterBaseView mFilterView;
    private FilterAdapter mFilterAdapter;

    //过度动画
    private ArrayList<AnimationView.AnimFrameData> mOverAnimDatas;
    private AnimationView mOverAnimView;
    private boolean isShowOverAnim = true;

    //社交分享
    private SimpleSharePage mSharePage;
    private boolean mShareToCommunity = false;

    public FilterBeautifyPageV2(Context context, BaseSite site)
    {
        super(context, site);
        mSite = (FilterBeautifyPageSite) site;
        initData();
        initView();
        MyBeautyStat.onPageStartByRes(R.string.拍照_拍照预览页_主页面);
        TongJiUtils.onPageStart(getContext(), R.string.拍照_滤镜);
    }

    private void initData()
    {
        filter.deleteAllCacheFilterFile();

        DownloadMgr.getInstance().AddDownloadListener(mDownloadListener);

        mViewW = ShareData.m_screenRealWidth;
        mViewH = ShareData.m_screenHeight - ShareData.PxToDpi_xhdpi(320);
        mBarH = ShareData.m_screenHeight - mViewH;
        mFilterFrTranslationY = ShareData.PxToDpi_xhdpi(320);
        mWatermarkFrTranslationY = ShareData.PxToDpi_xhdpi(320);
        mCirCleRadius = CameraPercentUtil.WidthPxToPercent(55);

        mWaterMarkId = SettingInfoMgr.GetSettingInfo(getContext()).GetPhotoWatermarkId(WatermarkResMgr2.getInstance().GetDefaultWatermarkId(getContext()));
        mFilterUri = FilterAdapter.OriginalItemInfo.ORIGINAL_ITEM_URI;
        mAdjustValue = 80;
        mFilterAlpha = 100;

        mConfig = new CameraFilterConfig();
        mFilterItemInfos = FilterResMgr.GetFilterRes(getContext(), false);

        mUiHandler = new UIHandler();
        mHandlerThread = new HandlerThread("filter_beautify_handler_thread");
        mHandlerThread.start();
        mImageHandler = new BeautyHandler(mHandlerThread.getLooper(), getContext(), mUiHandler);

        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                mOverAnimDatas = initOverAnimRes();
            }
        }).start();
    }

    private void initView()
    {
        boolean hasVirtual = false;
        if (ShareData.getCurrentVirtualKeyHeight((Activity) getContext()) > 0)
        {
            hasVirtual = true;
        }
        setBackgroundColor(Color.WHITE);

        mView = new PictureView(getContext());
        mView.setVerFilterCB(this);
        LayoutParams params = new LayoutParams(mViewW, mViewH);
        params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        this.addView(mView, params);

        mCompareBtn = new ImageView(getContext());
        mCompareBtn.setImageResource(R.drawable.beautify_compare);
        mCompareBtn.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        mCompareBtn.setOnTouchListener(new OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                    {
                        hideCircle(mBeautyCircle);
                        MyBeautyStat.onClickByRes(R.string.拍照_拍照预览页_主页面_对比按钮);
                        TongJi2.AddCountByRes(getContext(), R.integer.拍照_预览_对比按钮);
                        if (mView.getOrgImage() != null && mOrgBmp != null && mTmpBmp == null && !mView.getIsCompare())
                        {
                            mTmpBmp = mView.getOrgImage();
                            mView.setCompare(mOrgBmp, true);
                            mView.SetUIEnabled(true);
                        }
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_OUTSIDE:
                    {
                        if (mTmpBmp != null && mView.getOrgImage() != null && mView.getIsCompare())
                        {
                            mView.setCompare(mTmpBmp, false);
                            mTmpBmp = null;
                            mView.SetUIEnabled(true);
                        }
                        break;
                    }
                    default:
                        break;
                }
                return true;
            }
        });
        params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.TOP | Gravity.END;
        params.setMargins(0, ShareData.PxToDpi_xhdpi(18), ShareData.PxToDpi_xhdpi(18), 0);
        this.addView(mCompareBtn, params);

        mBarFr = new FrameLayout(getContext());
        mBarFr.setBackgroundColor(Color.WHITE);
        params = new LayoutParams(LayoutParams.MATCH_PARENT, mBarH);
        params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        this.addView(mBarFr, params);
        {
            mSeekBar = new MySeekBar(getContext());
            mSeekBar.setMax(100);
            mSeekBar.setProgress(mAdjustValue);
            mSeekBar.setBackgroundColor(ImageUtils.GetColorAlpha(Color.BLACK, 0.2f));
            mSeekBar.setOnProgressChangeListener(mOnSeekBarChangeListener);

            params = new LayoutParams(ShareData.PxToDpi_xhdpi(616), ShareData.PxToDpi_xhdpi(88));
            params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
            params.bottomMargin = ShareData.PxToDpi_xhdpi(170 + 30 + 10);
//            params.bottomMargin += hasVirtual ? ShareData.PxToDpi_xhdpi(10) : ShareData.PxToDpi_xhdpi(30);
//            params.bottomMargin += hasVirtual ? ShareData.PxToDpi_xhdpi(3) : ShareData.PxToDpi_xhdpi(10);
            mBarFr.addView(mSeekBar, params);

            RelativeLayout btnFr = new RelativeLayout(getContext());
            btnFr.setGravity(Gravity.CENTER_VERTICAL);
            params = new LayoutParams(LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(170));
            params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
            params.bottomMargin = ShareData.PxToDpi_xhdpi(30);
//            params.bottomMargin = hasVirtual ? ShareData.PxToDpi_xhdpi(10) : ShareData.PxToDpi_xhdpi(30);
            mBarFr.addView(btnFr, params);
            {
                RelativeLayout.LayoutParams lp;
                mSaveBtn = new FrameLayout(getContext());
                mSaveBtn.setId(R.id.filter_beautify_btn_save);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                {
                    mSaveBtn.setBackground(new BitmapDrawable(getResources(),
                            ImageUtils.AddSkin(getContext(), BitmapFactory.decodeResource(getResources(), R.drawable.camera_pre_save_icon_bg))));
                }
                else
                {
                    mSaveBtn.setBackgroundDrawable(new BitmapDrawable(getResources(),
                            ImageUtils.AddSkin(getContext(), BitmapFactory.decodeResource(getResources(), R.drawable.camera_pre_save_icon_bg))));
                }
                mSaveBtn.setOnTouchListener(mOnAnimationClickListener);
                ImageView icon = new ImageView(getContext());
                icon.setImageResource(R.drawable.camera_pre_save_icon);
                params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                params.gravity = Gravity.CENTER;
                mSaveBtn.addView(icon, params);
                lp = new RelativeLayout.LayoutParams(ShareData.PxToDpi_xhdpi(170), ShareData.PxToDpi_xhdpi(170));
                lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
                lp.addRule(RelativeLayout.CENTER_VERTICAL);
                btnFr.addView(mSaveBtn, lp);

                mFilterBtn = new BtnFr(getContext(),
                        R.drawable.camera_pre_filter_icon, R.string.filterpage_filter, mOnAnimationClickListener, true);
                mFilterBtn.setId(R.id.filter_beautify_btn_filter);
                lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                lp.addRule(RelativeLayout.CENTER_VERTICAL);
                lp.addRule(RelativeLayout.LEFT_OF, R.id.filter_beautify_btn_save);
                lp.rightMargin = ShareData.PxToDpi_xhdpi(48);
                lp.rightMargin = mFilterBtn.hasOffset() > 0 ? lp.rightMargin - mFilterBtn.hasOffset() : lp.rightMargin;
                btnFr.addView(mFilterBtn, lp);

                mBackBtn = new BtnFr(getContext(),
                        R.drawable.camera_pre_back_gray, R.string.back, mOnAnimationClickListener, false);
                mBackBtn.setId(R.id.filter_beautify_btn_back);
                lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                lp.addRule(RelativeLayout.CENTER_VERTICAL);
                lp.addRule(RelativeLayout.LEFT_OF, R.id.filter_beautify_btn_filter);
                lp.rightMargin = ShareData.PxToDpi_xhdpi(48);
                lp.rightMargin = mBackBtn.hasOffset() > 0 ? lp.rightMargin - mBackBtn.hasOffset() : lp.rightMargin;
                btnFr.addView(mBackBtn, lp);

                mBeautyBtn = new BtnFr(getContext(),
                        R.drawable.camera_pre_beauty_icon, R.string.filterbeautify_page_advanced_tip, mOnAnimationClickListener, true);
                mBeautyBtn.setId(R.id.filter_beautify_btn_beauty);
                lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                lp.addRule(RelativeLayout.CENTER_VERTICAL);
                lp.addRule(RelativeLayout.RIGHT_OF, R.id.filter_beautify_btn_save);
                lp.leftMargin = ShareData.PxToDpi_xhdpi(48);
                lp.leftMargin = mBeautyBtn.hasOffset() > 0 ? lp.leftMargin - mBeautyBtn.hasOffset() : lp.leftMargin;
                btnFr.addView(mBeautyBtn, lp);

                mShareBtn = new BtnFr(getContext(),
                        R.drawable.camera_pre_share, R.string.share, mOnAnimationClickListener, true);
                mShareBtn.setId(R.id.filter_beautify_btn_share);
                lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                lp.addRule(RelativeLayout.CENTER_VERTICAL);
                lp.addRule(RelativeLayout.RIGHT_OF, R.id.filter_beautify_btn_beauty);
                lp.leftMargin = ShareData.PxToDpi_xhdpi(48);
                lp.leftMargin = mShareBtn.hasOffset() > 0 ? lp.leftMargin - mShareBtn.hasOffset() : lp.leftMargin;
                btnFr.addView(mShareBtn, lp);
            }
        }

        mFilterFr = new FrameLayout(getContext());
        mFilterFr.setClickable(true);
        mFilterFr.setBackgroundColor(0xfff0f0f0);
        mFilterFr.setTranslationY(mFilterFrTranslationY);
        params = new LayoutParams(LayoutParams.MATCH_PARENT, mFilterFrTranslationY);
        params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
        this.addView(mFilterFr, params);
        {
            FrameLayout topBar = new FrameLayout(getContext());
            topBar.setBackgroundColor(Color.WHITE);
            params = new LayoutParams(LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(88));
            params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
            mFilterFr.addView(topBar, params);
            {
                mFCenterBtn = new MyStatusButton(getContext());
                mFCenterBtn.setData(R.drawable.filterbeautify_color_icon, getContext().getString(R.string.filterpage_filter));
                mFCenterBtn.setBtnStatus(true, true);
                mFCenterBtn.setOnClickListener(mOnClickListener);
                params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
                params.gravity = Gravity.CENTER;
                topBar.addView(mFCenterBtn, params);
            }

            mFilterAdapter = new FilterAdapter(mConfig);
            /*mConfig.def_parent_top_padding = (CameraPercentUtil.HeightPxToPercent(232) - mConfig.def_item_h) / 2;
            mConfig.def_parent_bottom_padding = mConfig.def_parent_top_padding;*/

            mFilterAdapter.SetData(mFilterItemInfos);
            mFilterAdapter.setOnItemClickListener(mOnItemClickListener);

            mFilterView = new FilterBaseView(getContext(), mFilterAdapter);
            params = new LayoutParams(LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(232));
            params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
            mFilterFr.addView(mFilterView, params);
        }

        mBeautyCircle = new CirclePanel(getContext());
        mBeautyCircle.hide();
        params = new LayoutParams(LayoutParams.MATCH_PARENT, CameraPercentUtil.HeightPxToPercent(120));
        params.bottomMargin = mBarH + ShareData.PxToDpi_xhdpi(20);
        params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        this.addView(mBeautyCircle, params);

        mAlphaCircle = new CirclePanel(getContext());
        mAlphaCircle.hide();
        params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, CameraPercentUtil.HeightPxToPercent(120));
        params.bottomMargin = CameraPercentUtil.HeightPxToPercent(196);
        params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        this.addView(mAlphaCircle, params);

        mWaitDialog = new WaitAnimDialog((Activity) getContext());
        mWaitDialog.SetGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, CameraPercentUtil.HeightPxToPercent(320 + 60));
    }

    private void initRecomView()
    {
        if (mRecomView == null)
        {
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
                    if (mSite != null) mSite.OnLogin(getContext());
                }
            });
            mRecomView.Create(this);
        }
    }

    private static final int S_SHOW_OVER_TIME = 1000;

    private ArrayList<AnimationView.AnimFrameData> initOverAnimRes()
    {
        ArrayList<Integer> resInt = new ArrayList<>();
        resInt.add(R.drawable.c6pz310000);
        resInt.add(R.drawable.c6pz310001);
        resInt.add(R.drawable.c6pz310002);
        resInt.add(R.drawable.c6pz310003);
        resInt.add(R.drawable.c6pz310004);
        resInt.add(R.drawable.c6pz310005);
        resInt.add(R.drawable.c6pz310006);
        resInt.add(R.drawable.c6pz310007);
        resInt.add(R.drawable.c6pz310008);
        resInt.add(R.drawable.c6pz310009);
        resInt.add(R.drawable.c6pz310010);
        resInt.add(R.drawable.c6pz310011);
        resInt.add(R.drawable.c6pz310012);
        resInt.add(R.drawable.c6pz310013);
        resInt.add(R.drawable.c6pz310014);
        resInt.add(R.drawable.c6pz310015);
        resInt.add(R.drawable.c6pz310016);
        resInt.add(R.drawable.c6pz310017);
        resInt.add(R.drawable.c6pz310018);
        resInt.add(R.drawable.c6pz310019);
        resInt.add(R.drawable.c6pz310020);
        resInt.add(R.drawable.c6pz310021);
        resInt.add(R.drawable.c6pz310022);
        resInt.add(R.drawable.c6pz310023);
        resInt.add(R.drawable.c6pz310024);
        resInt.add(R.drawable.c6pz310025);
        resInt.add(R.drawable.c6pz310026);
        resInt.add(R.drawable.c6pz310027);
        resInt.add(R.drawable.c6pz310028);
        resInt.add(R.drawable.c6pz310029);

        ArrayList<AnimationView.AnimFrameData> datas = new ArrayList<>();
        int duration = S_SHOW_OVER_TIME / resInt.size();
        for (Integer res : resInt)
        {
            datas.add(new AnimationView.AnimFrameData(res, duration, false));
        }
        return datas;
    }

    private void showOverAnimView()
    {
        if (isShowOverAnim)
        {
            if (mOverAnimView == null)
            {
                mOverAnimView = new AnimationView(getContext());
                mOverAnimView.setClickable(false);
                mOverAnimView.SetGravity(Gravity.FILL_HORIZONTAL | Gravity.FILL_VERTICAL);
                if (mOverAnimDatas != null && mOverAnimDatas.size() > 1)
                {
                    mOverAnimView.SetData_nodpi(mOverAnimDatas, new AnimationView.Callback()
                    {
                        @Override
                        public void OnClick()
                        {

                        }

                        @Override
                        public void OnAnimationEnd()
                        {
                            post(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    if (mOverAnimView != null)
                                    {
                                        mOverAnimView.setVisibility(GONE);
                                        FilterBeautifyPageV2.this.removeView(mOverAnimView);
                                    }
                                    mOverAnimView = null;
                                }
                            });
                        }
                    });
                }
                LayoutParams fp = new LayoutParams(mViewW, mViewH);
                this.addView(mOverAnimView, fp);
            }

            isShowOverAnim = false;
            mOverAnimView.Start();
        }
    }

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
                    sendCancelMsg();
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

    private void showWaitDialog(boolean show)
    {
        if (mWaitDialog != null)
        {
            if (show)
            {
                mWaitDialog.show();
            }
            else
            {
                mWaitDialog.hide();
            }
        }
    }

    private void showToast(@StringRes int res)
    {
        PreviewBackMsgToast toast = new PreviewBackMsgToast();
        toast.setMsg(getResources().getString(res)).show(getContext());
//        Toast toast = Toast.makeText(getContext(), res, Toast.LENGTH_SHORT);
//        int topPaddingHeight = RatioBgUtils.getTopPaddingHeight(1f);
//        topPaddingHeight += ShareData.m_screenRealWidth * 1f / 1f + CameraPercentUtil.HeightPxToPercent(60);
//        toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, topPaddingHeight);
//        toast.show();
    }

    private FilterAdapter.OnItemClickListener mOnItemClickListener = new FilterAdapter.OnItemClickListener()
    {
        @Override
        public void OnItemClick(BaseExAdapter.ItemInfo info, int parentIndex, int subIndex)
        {
            if (info == null) return;

            switch (info.m_uris[0])
            {
                case FilterAdapter.HeadItemInfo.HEAD_ITEM_URI:
                {
                    if (mView != null && !mView.getIsTouch() && !mView.getIsCompare())
                    {
                        boolean isSelectBlur = ((FilterAdapter.HeadItemInfo) info).isSelectBlur;
                        boolean isSelectDark = ((FilterAdapter.HeadItemInfo) info).isSelectDark;

                        if (FilterBeautifyPageV2.this.isBlured != isSelectBlur)
                        {
                            MyBeautyStat.onClickByRes(R.string.拍照_拍照预览页_滤镜列表_虚化);
                        }
                        if (FilterBeautifyPageV2.this.isDarked != isSelectDark)
                        {
                            MyBeautyStat.onClickByRes(R.string.拍照_拍照预览页_滤镜列表_暗角);
                        }

                        FilterBeautifyPageV2.this.isBlured = isSelectBlur;
                        FilterBeautifyPageV2.this.isDarked = isSelectDark;
                        sendBlurDarkMsg(mFilterRes);
                    }
                }
                break;
                case FilterAdapter.DownloadItemInfo.DOWNLOAD_ITEM_URI:
                {
                    TongJi2.AddCountByRes(getContext(), R.integer.拍照_预览_滤镜_预览_滤镜下载更多);
                    MyBeautyStat.onClickByRes(R.string.拍照_拍照预览页_滤镜列表_滤镜下载更多);
                    if (mSite != null) mSite.OpenDownloadMore(getContext(), ResType.FILTER);
                }
                break;
                case FilterAdapter.RecommendItemInfo.RECOMMEND_ITEM_URI:
                {
                    MyBeautyStat.onClickByRes(R.string.拍照_拍照预览页_滤镜列表_滤镜推广位);
                    ArrayList<RecommendRes> ress = (ArrayList<RecommendRes>) ((FilterAdapter.ItemInfo) info).m_ex;
                    //推荐位
                    RecommendRes recommendRes = null;
                    if (ress != null && ress.size() > 0)
                    {
                        recommendRes = ress.get(0);
                    }
                    if (recommendRes != null)
                    {
                        initRecomView();
                        mRecomView.SetBk(CommonUtils.GetScreenBmp((Activity) getContext(), ShareData.m_screenRealWidth / 8, ShareData.m_screenRealHeight / 8), true);
                        mRecomView.SetDatas(recommendRes, ResType.FILTER.GetValue());
                        mRecomView.Show();
                    }
                }
                break;
                case FilterAdapter.OriginalItemInfo.ORIGINAL_ITEM_URI:
                {
                    MyBeautyStat.onClickByRes(R.string.拍照_拍照预览页_滤镜列表_滤镜_原图);
                    if (mFilterUri != info.m_uri)
                    {
                        mFilterUri = 0;
                        if (info instanceof FilterAdapter.OriginalItemInfo && ((FilterAdapter.OriginalItemInfo) info).m_ex != null && (((FilterAdapter.OriginalItemInfo) info).m_ex) instanceof FilterRes)
                        {
                            mFilterRes = (FilterRes) ((FilterAdapter.OriginalItemInfo) info).m_ex;
                        }
                        else
                        {
                            mFilterRes = ResourceUtils.GetItem(FilterResMgr2.getInstance().sync_GetLocalRes(getContext(), null), 0);
                        }
                        mFilterAdapter.setOpenIndex(-1);
                        mFilterTjId = R.integer.拍照_预览_滤镜_预览_滤镜_原图;
                        mFilterAlpha = mFilterRes != null ? mFilterRes.m_filterAlpha : 100;
                        sendInitMsg(mFilterRes);
                    }
                }
                break;
                default:
                {
                    if (subIndex > 0)
                    {
                        if (mFilterUri != info.m_uris[subIndex])
                        {
                            mFilterUri = info.m_uris[subIndex];
                            if (info instanceof FilterAdapter.ItemInfo
                                    && ((FilterAdapter.ItemInfo) info).m_ex != null
                                    && ((FilterAdapter.ItemInfo) info).m_ex instanceof FilterGroupRes
                                    && ((FilterGroupRes) ((FilterAdapter.ItemInfo) info).m_ex).m_group != null)
                            {
                                mFilterRes = ((FilterGroupRes) ((FilterAdapter.ItemInfo) info).m_ex).m_group.get(subIndex - 1);
                            }
                            if (mFilterRes == null)
                            {
                                mFilterRes = FilterResMgr2.getInstance().GetRes(mFilterUri);
                            }
                            if (mFilterRes != null)
                            {
                                isDarked = mFilterRes.m_isHasvignette;
                                mFilterTjId = mFilterRes.m_tjId;
                                sendFilterMsg(mFilterRes);
                            }
                            if (mFilterItemInfos != null && mFilterItemInfos.size() > 0)
                            {
                                ((FilterAdapter.HeadItemInfo) mFilterItemInfos.get(0)).isSelectBlur = isBlured;
                                ((FilterAdapter.HeadItemInfo) mFilterItemInfos.get(0)).isSelectDark = isDarked;
                                if (mFilterAdapter != null)
                                {
                                    mFilterAdapter.notifyItemChanged(0);
                                }
                            }
                        }
                    }
                }
                break;
            }
        }

        @Override
        public void onProgressChanged(MySeekBar seekBar, int progress)
        {
            mSavePath = null;
            //滤镜alpha 变化
            float circleX = mConfig.def_sub_w + mConfig.def_sub_l * 2 + CameraPercentUtil.WidthPxToPercent(30 / 2) + seekBar.getLeft() + seekBar.getMaxDistans() * progress / 100f;
            float circleY = mAlphaCircle.getHeight() / 2f - CameraPercentUtil.HeightPxToPercent(3);
            showCircle(mAlphaCircle, progress, circleX, circleY);
            setViewFilterAlpha(progress);
        }

        @Override
        public void onStartTrackingTouch(MySeekBar seekBar)
        {
            float circleX = mConfig.def_sub_w + mConfig.def_sub_l * 2 + CameraPercentUtil.WidthPxToPercent(30 / 2) + seekBar.getLeft() + seekBar.getMaxDistans() * seekBar.getProgress() / 100f;
            float circleY = mAlphaCircle.getHeight() / 2f - CameraPercentUtil.HeightPxToPercent(3);
            showCircle(mAlphaCircle, seekBar.getProgress(), circleX, circleY);
            setViewFilterAlpha(seekBar.getProgress());
            mView.SetUIEnabled(false);
        }

        @Override
        public void onStopTrackingTouch(MySeekBar seekBar)
        {
            hideCircle(mAlphaCircle);
            setViewFilterAlpha(seekBar.getProgress());
            setFilterSeekBarProgress(seekBar.getProgress());
            mView.SetUIEnabled(true);
        }

        @Override
        public void onSeekBarStartShow(MySeekBar seekBar)
        {
            mFilterView.setUiEnable(false);
        }

        @Override
        public void onFinishLayoutAlphaFr(MySeekBar seekBar)
        {
            mFilterView.setUiEnable(true);
            hideCircle(mAlphaCircle);
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

    private MySeekBar.OnProgressChangeListener mOnSeekBarChangeListener = new MySeekBar.OnProgressChangeListener()
    {

        @Override
        public void onProgressChanged(MySeekBar seekBar, int progress)
        {
            float circleX = CameraPercentUtil.WidthPxToPercent(55 / 2) + seekBar.getLeft() + seekBar.getMaxDistans() * seekBar.getProgress() / 100f;
            float circleY = mBeautyCircle.getHeight() / 2f - CameraPercentUtil.HeightPxToPercent(3);
            showCircle(mBeautyCircle, progress, circleX, circleY);
        }

        @Override
        public void onStartTrackingTouch(MySeekBar seekBar)
        {
            MyBeautyStat.onClickByRes(R.string.拍照_拍照预览页_主页面_调节美颜滑动杆);
            TongJi2.AddCountByRes(getContext(), R.integer.拍照_预览_滤镜_调节美颜滑动杆);
            float circleX = CameraPercentUtil.WidthPxToPercent(55 / 2) + seekBar.getLeft() + seekBar.getMaxDistans() * seekBar.getProgress() / 100f;
            float circleY = mBeautyCircle.getHeight() / 2f - CameraPercentUtil.HeightPxToPercent(3);
            showCircle(mBeautyCircle, seekBar.getProgress(), circleX, circleY);
            mView.SetUIEnabled(false);
        }

        @Override
        public void onStopTrackingTouch(MySeekBar seekBar)
        {
            hideCircle(mBeautyCircle);
            mFilterBeautyParams.setSkinBeautySize(mAdjustValue = seekBar.getProgress());
            sendAdjustMsg(mFilterRes);
            mView.SetUIEnabled(true);
        }
    };

    private void showCircle(CirclePanel circlePanel, int progress, float circleX, float circleY)
    {
        if (circlePanel != null)
        {
            circlePanel.change(circleX, circleY, mCirCleRadius);
            circlePanel.setText(String.valueOf(progress));
            circlePanel.show();
        }
    }

    private void hideCircle(CirclePanel circlePanel)
    {
        if (circlePanel != null)
        {
            circlePanel.hide();
        }
    }

    private OnClickListener mOnClickListener = new OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            if (v == mFCenterBtn)
            {
                boolean isShow = !isShowFilterView;
                mFCenterBtn.setNewStatus(false);
                mFCenterBtn.setBtnStatus(true, !mFCenterBtn.isDown());
                showFilterView(isShow);
            }
            else if (v == mWCenterBtn)
            {
                showWaterMarkView(!isShowWaterMarkView);
            }

        }
    };

    private OnAnimationClickListener mOnAnimationClickListener = new OnAnimationClickListener()
    {
        @Override
        public void onAnimationClick(View v)
        {
            if (!mUiEnable) return;

            if (v == mBackBtn)
            {
                back();
            }
            else if (v == mFilterBtn)
            {
                mOnClickListener.onClick(mFCenterBtn);
            }
            else if (v == mShareBtn)
            {
                MyBeautyStat.onClickByRes(R.string.拍照_拍照预览页_主页面_分享);
                if (FileUtil.isFileExists(mSavePath))
                {
                    showSharePage();
                }
                else
                {
                    sendShareMsg(mFilterRes);
                }
            }
            else if (v == mBeautyBtn)
            {
                MyBeautyStat.onClickByRes(R.string.拍照_拍照预览页_主页面_美颜美图按钮);
                TongJi2.AddCountByRes(getContext(), R.integer.拍照_预览_美颜美化按钮);
                sendAdvancedMsg();
            }
            else if (v == mSaveBtn)
            {
                MyBeautyStat.onClickByRes(R.string.拍照_拍照预览页_主页面_确认保存);
                TongJi2.AddCountByRes(getContext(), R.integer.拍照_预览_确认);

                if (!FileUtil.isFileExists(mSavePath))
                {
                    sendSaveMsg(mFilterRes);
                }
                else
                {
                    //直接保存
                    msgSave(mSavePath);
                }
            }
        }
    };

    private DownloadMgr.DownloadListener mDownloadListener = new AbsDownloadMgr.DownloadListener()
    {
        @Override
        public void OnDataChange(int resType, int downloadId, IDownload[] resArr)
        {
            //下载完成后的回调
            if (resArr != null && resType == ResType.FILTER.GetValue() && ((BaseRes) resArr[0]).m_type == BaseRes.TYPE_LOCAL_PATH)
            {
                if (mFilterAdapter != null)
                {
                    ArrayList<FilterAdapter.ItemInfo> dst = FilterResMgr.GetFilterRes(getContext(), false);
                    if (mFilterItemInfos != null && dst.size() > mFilterItemInfos.size())
                    {
                        int selectIndex = mFilterAdapter.GetSelectIndex();
                        mFilterAdapter.notifyItemDownLoad(dst.size() - mFilterItemInfos.size());
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
                    mFilterItemInfos = dst;
                    mFilterAdapter.SetData(mFilterItemInfos);
                    mFilterAdapter.notifyDataSetChanged();
                }
            }
        }
    };

    private void removeAllMessage()
    {
        if (mHandlerThread != null)
        {
            mHandlerThread.quit();
            mHandlerThread = null;
        }
        if (mImageHandler != null)
        {
            mImageHandler.removeMessages(BeautyHandler.MSG_INIT);
            mImageHandler.removeMessages(BeautyHandler.MSG_SAVE);
            mImageHandler.removeMessages(BeautyHandler.MSG_CANCEL);
            mImageHandler.removeMessages(BeautyHandler.MSG_FILTER);
            mImageHandler.removeMessages(BeautyHandler.MSG_RESTORE);
            mImageHandler.removeMessages(BeautyHandler.MSG_ADJUST);
            mImageHandler.removeMessages(BeautyHandler.MSG_BLUR_DARK);
            mImageHandler.removeMessages(BeautyHandler.MSG_ADVANCED);

            mImageHandler.clear();
            mImageHandler = null;
        }
    }

    private void back()
    {
        TongJi2.AddCountByRes(getContext(), R.integer.拍照_预览_取消返回);
        MyBeautyStat.onClickByRes(R.string.拍照_拍照预览页_主页面_取消返回);
        if (mFromCamera && !isDoSaved)
        {
            sendCancelMsg();
            //showExitDialog();
        }
        else
        {
            sendCancelMsg();
        }
    }

    @Override
    public void onClose()
    {
        MyBeautyStat.onPageEndByRes(R.string.拍照_拍照预览页_主页面);
        TongJiUtils.onPageEnd(getContext(), R.string.拍照_滤镜);
        FaceDataV2.ResetData();
        if (DownloadMgr.getInstance() != null)
        {
            DownloadMgr.getInstance().RemoveDownloadListener(mDownloadListener);
        }
        if (mRecomView != null)
        {
            mRecomView.ClearAllaa();
        }
        if (mView != null)
        {
            this.removeView(mView);
            mView.setVerFilterCB(null);
            mView.ReleaseMem();
        }
        if (mOverAnimDatas != null)
        {
            mOverAnimDatas.clear();
            mOverAnimDatas = null;
        }

        if (mOverAnimView != null)
        {
            this.removeView(mOverAnimView);
            mOverAnimView.ClearAll();
            mOverAnimView = null;
        }

        if (mFilterAdapter != null)
        {
            mFilterAdapter.ClearAll();
        }

        if (mWatermarkAdapter != null)
        {
            mWatermarkAdapter.clear();
        }

        if (mWaitDialog != null)
        {
            mWaitDialog.dismiss();
        }
        if (mShareSendUtil != null)
        {
            mShareSendUtil.clear();
        }

        removeAllMessage();
        closeSharePage();
        clearExitDialog();

        SettingInfoMgr.Save(getContext());
        mScreenshots = null;
        mTmpBmp = null;
        mWaitDialog = null;
        mFilterAdapter = null;
        mWatermarkAdapter = null;
        mFilterFr = null;
        mView = null;
        mRecomView = null;
        mDownloadListener = null;
        mShareSendUtil = null;
        mFilterBeautyParams = null;
        super.onClose();
    }


    @Override
    public void SetData(HashMap<String, Object> params)
    {
        FaceDataV2.ResetData();
        initParams(params);
    }

    private void initParams(HashMap<String, Object> params)
    {
        if (params != null)
        {
            if (params.containsKey("from_camera"))
            {
                mFromCamera = (Boolean) params.get("from_camera");
            }

            if (params.containsKey("show_share_btn"))
            {
                isShowShareBtn = (Boolean) params.get("show_share_btn");
            }

            if (params.containsKey("imgs"))
            {
                mImgs = params.get("imgs");
            }

            if (params.containsKey("ratio"))
            {
                mRatio = (float) params.get("ratio");
            }

            if (params.containsKey("orientation"))
            {
                mOrientation = (Integer) params.get("orientation");
            }

            if (params.containsKey(DataKey.COLOR_FILTER_ID))
            {
                mFilterUri = (Integer) params.get(DataKey.COLOR_FILTER_ID);
            }

            if (params.containsKey(DataKey.CAMERA_TAILOR_MADE_PARAMS))
            {
                Object o = params.get(DataKey.CAMERA_TAILOR_MADE_PARAMS);
                if (o != null && o instanceof FilterBeautyParams)
                {
                    mFilterBeautyParams = (FilterBeautyParams) o;
                }
            }

            if (mFilterBeautyParams == null)
            {
                // 构造默认
                mFilterBeautyParams = new FilterBeautyParams();
                mFilterBeautyParams.getCamera(getContext());
            }

            Bitmap temp = null;
            int rotation;
            int flip;
            float scale = -1;
            if (mImgs instanceof ImageFile2)
            {
                RotationImg2[] img = ((ImageFile2) mImgs).GetRawImg();
                Object data = img[0].m_img;
                rotation = img[0].m_degree;
                flip = img[0].m_flip;
                temp = cn.poco.imagecore.Utils.DecodeShowImage((Activity) getContext(), data, rotation, scale, flip);
            }
            else if (mImgs instanceof RotationImg2[])
            {
                Object data = ((RotationImg2[]) mImgs)[0].m_img;
                rotation = ((RotationImg2[]) mImgs)[0].m_degree;
                flip = ((RotationImg2[]) mImgs)[0].m_flip;
                temp = cn.poco.imagecore.Utils.DecodeShowImage((Activity) getContext(), data, rotation, scale, flip);
            }
            else if (mImgs instanceof Bitmap)
            {
                temp = (Bitmap) mImgs;
            }
            mOrgBmp = temp;
        }

        //是否back
        if (mSite.m_myParams != null &&
                mSite.m_myParams.containsKey(IS_BACK))
        {
            isRestore = true;
            isDoWaterAlphaAnim = false;
            isShowOverAnim = false;
            getOnRestoreParams();
        }

        mAdjustValue = (int) mFilterBeautyParams.getSkinBeautySize();

        if (isRestore && mSite.m_myParams != null && mSite.m_myParams.containsKey(RESTORE_IMGS))
        {
            Object obj = mSite.m_myParams.get(RESTORE_IMGS);
            if (obj != null && obj instanceof String)
            {
                Bitmap showBmp = cn.poco.imagecore.Utils.DecodeShowImage((Activity) getContext(), obj, 0, -1, MakeBmpV2.FLIP_NONE);
                if (showBmp != null && !showBmp.isRecycled())
                {
                    setDatas(showBmp, true);
                }
                else
                {
                    setDatas(mOrgBmp, false);
                }
            }
        }
        else
        {
            setDatas(mOrgBmp, false);
        }
    }

    private void setDatas(Bitmap showBmp, boolean isRestoreValid)
    {
        if (showBmp != null && !showBmp.isRecycled())
        {
            mView.setBackColor(Color.WHITE);
            mView.setOrgImage(showBmp);
        }

        mShareBtn.setVisibility(this.isShowShareBtn ? VISIBLE : GONE);
        mSeekBar.setProgress(mAdjustValue);

        if (isRestore)
        {
            mFilterRes = FilterResMgr2.getInstance().GetRes(mFilterUri);
            if (isRestoreValid)
            {
                sendRestoreMsg(mFilterRes);
            }
            else
            {
                sendRestoreMsg(mFilterRes);
            }
        }
        else
        {
            initStartFilterRes();
        }

//        if (!isRestore)
//        {
//            autoSaveOriginImageFile(mImgs);
//        }

        if (!isRestore)
        {
            //照片统计
            SettingInfo settingInfo = SettingInfoMgr.GetSettingInfo(getContext());
            if (settingInfo != null)
            {
                PhotoStat.Stat(getContext(), mOrgBmp, CommonUtils.GetAppVer(getContext()), settingInfo.GetPoco2Id(true));
            }
        }
    }

    private void autoSaveOriginImageFile(Object obj)
    {
        if (obj != null && obj instanceof ImageFile2)
        {
            final Object finalObj = obj;
            new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    if (finalObj instanceof ImageFile2)
                    {
                        ((ImageFile2) finalObj).SaveImg2(getContext());
                    }
                }
            }).start();
        }
    }

    private void getOnRestoreParams()
    {
        if (mSite.m_myParams.containsKey(ADJUST_VALUE))
        {
            mAdjustValue = (Integer) mSite.m_myParams.get(ADJUST_VALUE);
        }

        if (mSite.m_myParams.containsKey(FILTER_URI))
        {
            mFilterUri = (Integer) mSite.m_myParams.get(FILTER_URI);
        }

        if (mSite.m_myParams.containsKey(FILTER_ALPHA))
        {
            mFilterAlpha = (Integer) mSite.m_myParams.get(FILTER_ALPHA);
        }
        if (mSite.m_myParams.containsKey(IS_BLUR))
        {
            isBlured = (Boolean) mSite.m_myParams.get(IS_BLUR);
        }
        if (mSite.m_myParams.containsKey(IS_DARK))
        {
            isDarked = (Boolean) mSite.m_myParams.get(IS_DARK);
        }
        if (mSite.m_myParams.containsKey(RATIO))
        {
            mRatio = (float) mSite.m_myParams.get(RATIO);
        }
        if (mSite.m_myParams.containsKey(ORIENTATION))
        {
            mOrientation = (int) mSite.m_myParams.get(ORIENTATION);
        }
        if (mSite.m_myParams.containsKey(FILTER_BEAUTY_PARAMS))
        {
            mFilterBeautyParams = (FilterBeautyParams) mSite.m_myParams.get(FILTER_BEAUTY_PARAMS);
        }
    }


    public void setOnRestoreParams()
    {
        mSite.m_myParams.put(RATIO, mRatio);
        mSite.m_myParams.put(ORIENTATION, mOrientation);
        mSite.m_myParams.put(IS_BACK, true);
        mSite.m_myParams.put(ADJUST_VALUE, mAdjustValue);
        mSite.m_myParams.put(FILTER_URI, mFilterUri);
        mSite.m_myParams.put(FILTER_ALPHA, mFilterAlpha);
        mSite.m_myParams.put(IS_BLUR, isBlured);
        mSite.m_myParams.put(IS_DARK, isDarked);
        mSite.m_myParams.put(IS_SHAPE, true);
        mSite.m_myParams.put(WATERMARKID, mWaterMarkId);
        mSite.m_myParams.put(FILTER_BEAUTY_PARAMS, mFilterBeautyParams);
    }


    /**
     * 统计 、 神策埋点
     */
    private void sendTongJiParams()
    {
        //TODO 水印统计
        WatermarkItem item = WatermarkResMgr2.getInstance().GetWaterMarkById(mWatermarkResArr, mWaterMarkId);
        if (item != null)
        {
            TongJi2.AddCountByRes(getContext(), item.mTongJiId);
        }

        //神策埋点
        String filterId = "0";
        if (mFilterTjId > 0)
        {
            filterId = mFilterTjId == R.integer.拍照_预览_滤镜_预览_滤镜_原图 ? "0" : String.valueOf(mFilterTjId);
        }
        MyBeautyStat.onUseFilter(isBlured, isDarked, filterId, mFilterAlpha);
    }

    private void initStartFilterRes()
    {
        if (mFilterUri > 0)
        {
            int tempUri = mFilterUri;
            mFilterUri = 0;
            int[] outs = mFilterAdapter.GetSubIndexByUri(tempUri);
            if (outs != null && outs[0] >= 0 && outs[1] >= 0)
            {
                mFilterAdapter.SetSelectByIndex(outs[0], outs[1], true, true, true);
            }
            else
            {
                mFilterAdapter.SetSelectByUri(FilterAdapter.OriginalItemInfo.ORIGINAL_ITEM_URI);
            }
        }
        else
        {   //原图滤镜
            mFilterUri = 0;
            mFilterAdapter.SetSelectByUri(FilterAdapter.OriginalItemInfo.ORIGINAL_ITEM_URI);
        }
    }

    /**
     * 对原图框架一次操作
     *
     * @param filterRes
     */
    private void sendRestoreMsg(FilterRes filterRes)
    {
        showWaitDialog(true);
        BeautyHandler.RestoreMsg msg = new BeautyHandler.RestoreMsg();
        msg.isRestoreValid = false;
        msg.mOrgBmp = mOrgBmp;
        msg.mAdjustSize = mAdjustValue;
        msg.mFilterUri = mFilterUri;
        msg.mFilterAlpha = mFilterAlpha;
        msg.isBlur = isBlured;
        msg.isDark = isDarked;
        msg.mFilterRes = filterRes;
        setTailorMadeParams(msg);
        sendHandlerMessage(BeautyHandler.MSG_RESTORE, msg);
    }

    /**
     * 执行人脸检测+脸型bmp
     *
     * @param filterRes
     */
    private void sendRestoreMsg2(FilterRes filterRes)
    {
        //showWaitDialog(true);
        BeautyHandler.RestoreMsg msg = new BeautyHandler.RestoreMsg();
        msg.isRestoreValid = true;
        msg.mOrgBmp = mOrgBmp;
        msg.mAdjustSize = mAdjustValue;
        msg.mFilterUri = mFilterUri;
        msg.mFilterAlpha = mFilterAlpha;
        msg.isBlur = isBlured;
        msg.isDark = isDarked;
        msg.mFilterRes = filterRes;
        setTailorMadeParams(msg);
        sendHandlerMessage(BeautyHandler.MSG_RESTORE, msg);

        if (filterRes != null)
        {
            setWaterMark(filterRes.m_isHaswatermark);
        }
    }

    public void sendBlurDarkMsg(FilterRes filterRes)
    {
        showWaitDialog(true);
        BeautyHandler.BeautyMsg msg = new BeautyHandler.BeautyMsg();
        msg.mOrgBmp = mOrgBmp;
        msg.mFilterUri = mFilterUri;
        msg.mFilterAlpha = mFilterAlpha;
        msg.mAdjustSize = mAdjustValue;
        msg.isBlur = isBlured;
        msg.isDark = isDarked;
        msg.mFilterRes = filterRes;

        setTailorMadeParams(msg);
        sendHandlerMessage(BeautyHandler.MSG_BLUR_DARK, msg);
    }

    public void sendFilterMsg(FilterRes filterRes)
    {
        showWaitDialog(true);
        BeautyHandler.BeautyMsg msg = new BeautyHandler.BeautyMsg();
        msg.mOrgBmp = mOrgBmp;
        msg.mImgs = mImgs;

        msg.mAdjustSize = mAdjustValue;
        msg.isBlur = isBlured;
        msg.isDark = filterRes.m_isHasvignette;
        msg.mFilterUri = mFilterUri;
        msg.mFilterAlpha = mFilterAlpha = filterRes.m_filterAlpha;
        msg.mFilterRes = filterRes;

        setTailorMadeParams(msg);
        sendHandlerMessage(BeautyHandler.MSG_FILTER, msg);
    }

    private void sendInitMsg(FilterRes filterRes)
    {
        showWaitDialog(true);
        BeautyHandler.BeautyMsg msg = new BeautyHandler.BeautyMsg();
        msg.mFrW = mViewW;
        msg.mFrH = mViewH;
        msg.mImgs = mImgs;
        msg.mFilterRes = filterRes;
        msg.mOrgBmp = mOrgBmp;
        msg.mAdjustSize = mAdjustValue;

        msg.mFilterUri = mFilterUri;
        msg.mFilterAlpha = mFilterAlpha = filterRes != null ? filterRes.m_filterAlpha : mFilterAlpha;
        msg.isBlur = isBlured;
        msg.isDark = isDarked;

        setTailorMadeParams(msg);
        sendHandlerMessage(BeautyHandler.MSG_INIT, msg);
    }

    private void sendAdjustMsg(FilterRes filterRes)
    {
        showWaitDialog(true);

        BeautyHandler.BeautyMsg msg = new BeautyHandler.BeautyMsg();
        msg.mOrgBmp = mOrgBmp;

        msg.mAdjustSize = mAdjustValue;
        msg.mFilterUri = mFilterUri;
        msg.mFilterAlpha = mFilterAlpha;
        msg.isBlur = isBlured;
        msg.isDark = isDarked;
        msg.mFilterRes = filterRes;
        setTailorMadeParams(msg);
        sendHandlerMessage(BeautyHandler.MSG_ADJUST, msg);
    }

    private void sendSaveMsg(FilterRes filterRes)
    {
        showWaitDialog(true);
        sendTongJiParams();

        BeautyHandler.SaveMsg msg = new BeautyHandler.SaveMsg();
        msg.mImgs = mImgs;
        msg.mOutSize = SysConfig.GetPhotoSize(getContext());
        msg.mAdjustSize = mAdjustValue;
        msg.mFilterUri = mFilterUri;
        msg.mFilterAlpha = mFilterAlpha;
        msg.isBlur = isBlured;
        msg.isDark = isDarked;
        msg.saveToFile = true;
        msg.mFilterRes = filterRes;
        if (filterRes != null)
        {
            msg.hasWaterMark = filterRes.m_isHaswatermark;
            if (filterRes.m_isHaswatermark && mWaterMarkId != WatermarkResMgr2.getInstance().GetNonWatermarkId(getContext()))
            {
                msg.waterMarkId = mWaterMarkId;
                msg.hasDateMark = SettingInfoMgr.GetSettingInfo(getContext()).GetAddDateState();
            }
        }

        setTailorMadeParams(msg);
        sendHandlerMessage(BeautyHandler.MSG_SAVE, msg);
    }

    private void sendShareMsg(FilterRes filterRes)
    {
        showWaitDialog(true);

        BeautyHandler.SaveMsg msg = new BeautyHandler.SaveMsg();
        msg.isShare = true;
        msg.saveToFile = true;
        msg.mImgs = mImgs;
        msg.mOutSize = SysConfig.GetPhotoSize(getContext());
        msg.mAdjustSize = mAdjustValue;
        msg.mFilterUri = mFilterUri;
        msg.mFilterAlpha = mFilterAlpha;
        msg.isBlur = isBlured;
        msg.isDark = isDarked;
        msg.mFilterRes = filterRes;
        if (filterRes != null)
        {
            msg.hasWaterMark = filterRes.m_isHaswatermark;
            if (filterRes.m_isHaswatermark && mWaterMarkId != WatermarkResMgr2.getInstance().GetNonWatermarkId(getContext()))
            {
                msg.waterMarkId = mWaterMarkId;
                msg.hasDateMark = SettingInfoMgr.GetSettingInfo(getContext()).GetAddDateState();
            }
        }

        setTailorMadeParams(msg);
        sendHandlerMessage(BeautyHandler.MSG_SAVE, msg);
    }

    private void sendAdvancedMsg()
    {
        showWaitDialog(true);
        setOnRestoreParams();

        BeautyHandler.AdvancedMsg msg = new BeautyHandler.AdvancedMsg();
        msg.mImgs = mImgs;
        msg.mOutSize = SysConfig.GetPhotoSize(getContext());
        msg.mAdjustSize = mAdjustValue;
        msg.mFilterUri = mFilterUri;
        msg.mFilterAlpha = mFilterAlpha;
        msg.isBlur = isBlured;
        msg.isDark = isDarked;
        msg.mFilterRes = mFilterRes;
        msg.hasWaterMark = false;//跳转到美颜美图不画水印，在保存时画水印
        msg.waterMarkId = mWaterMarkId;

        setTailorMadeParams(msg);
        sendHandlerMessage(BeautyHandler.MSG_ADVANCED, msg);
    }

    private void sendCancelMsg()
    {
        sendHandlerMessage(BeautyHandler.MSG_CANCEL, null);
    }

    private void sendHandlerMessage(int what, Object obj)
    {
        mUiEnable = false;
        //每次发消息 清除保存路径
        mSavePath = null;
        if (mImageHandler != null) {
            mImageHandler.obtainMessage(what, obj).sendToTarget();
        }
    }

    private void setTailorMadeParams(BeautyHandler.BeautyMsg msg)
    {
        if (msg != null && mFilterBeautyParams != null)
        {
            msg.mFilterBeautyParams = mFilterBeautyParams;
        }
    }

    private void msgInit(Message msg)
    {
        showOverAnimView();

        BeautyHandler.BeautyMsg params = (BeautyHandler.BeautyMsg) msg.obj;
        msg.obj = null;

        setViewBottomBmp(params.mBottomBmp);
        setViewTopBmp(params.mTopBmp);
        setViewFilterAlpha(params.mFilterAlpha);
        setFilterSeekBarProgress(params.mFilterAlpha);
        setWaterMark(false);
        params.mTopBmp = null;
        params.mBottomBmp = null;
    }

    private void msgFilter(Message msg)
    {
        showOverAnimView();

        BeautyHandler.BeautyMsg params = (BeautyHandler.BeautyMsg) msg.obj;
        msg.obj = null;

        setViewBottomBmp(params.mBottomBmp);
        setViewTopBmp(params.mTopBmp);
        setViewFilterAlpha(params.mFilterAlpha);
        setFilterSeekBarProgress(params.mFilterAlpha);

        if (params.mFilterRes != null)
        {
            setWaterMark(params.mFilterRes.m_isHaswatermark);
        }

        params.mTopBmp = null;
        params.mBottomBmp = null;
    }

    private void msgAdjust(Message msg)
    {
        BeautyHandler.BeautyMsg params = (BeautyHandler.BeautyMsg) msg.obj;
        msg.obj = null;

        if (mTmpBmp != null)
        {
            mTmpBmp = params.mBottomBmp;
        }
        else
        {
            setViewBottomBmp(params.mBottomBmp);
        }
        setViewTopBmp(params.mTopBmp);
        setViewFilterAlpha(params.mFilterAlpha);
        setFilterSeekBarProgress(params.mFilterAlpha);

        params.mTopBmp = null;
        params.mBottomBmp = null;
    }

    private void msgBlurDark(Message msg)
    {
        BeautyHandler.BeautyMsg params = (BeautyHandler.BeautyMsg) msg.obj;
        msg.obj = null;

        if (mTmpBmp != null)
        {
            mTmpBmp = params.mBottomBmp;
        }
        else
        {
            setViewBottomBmp(params.mBottomBmp);
        }
        setViewTopBmp(params.mTopBmp);
        setViewFilterAlpha(params.mFilterAlpha);
        setFilterSeekBarProgress(params.mFilterAlpha);

        params.mTopBmp = null;
        params.mBottomBmp = null;
    }

    private void msgCancel()
    {
        if (!isRestore && !isDoSaved && mFromCamera)
        {
            showToast(R.string.cancel_save);
        }

        if (mSite != null)
        {
            mSite.OnBack(getContext());
        }
    }

    private void msgSave(Message msg)
    {
        if (msg.obj instanceof String)
        {
            mSavePath = (String) msg.obj;
        }
        else if (msg.obj instanceof Bitmap)
        {
            Bitmap bitmap = (Bitmap) msg.obj;
            if (bitmap != null && !bitmap.isRecycled())
            {
                mSavePath = Utils.SaveImg(getContext(), bitmap, Utils.MakeSavePhotoPath(getContext(), (float) bitmap.getWidth() / bitmap.getHeight()), 100, true);
            }
        }
        if (!FileUtil.isFileExists(mSavePath))
        {
            showToast(R.string.saving_picture_failed);
        }
        else
        {
            isDoSaved = true;
            if (msg.arg1 == 1)
            {
                //分享
                showSharePage();
            }
            else
            {
                //保存
                msgSave(mSavePath);
            }
        }
    }

    private void msgSave(String mSavePath)
    {
        this.mSavePath = mSavePath;
        showToast(R.string.succeed_save2);
        HashMap<String, Object> params = new HashMap<>();
        params.put("img", this.mSavePath);
        mSite.OnSave(getContext(), params);
    }

    private void msgRestore(Message msg)
    {
        BeautyHandler.RestoreMsg params = (BeautyHandler.RestoreMsg) msg.obj;
        msg.obj = null;

        if (!params.isRestoreValid)
        {
            setViewBottomBmp(params.mBottomBmp);
            setViewTopBmp(params.mTopBmp);

            params.mTopBmp = null;
            params.mBottomBmp = null;
            setViewFilterAlpha(params.mFilterAlpha);

            if (params.mFilterRes != null)
            {
                setWaterMark(params.mFilterRes.m_isHaswatermark);
            }
        }

        setFilterSeekBarProgress(params.mFilterAlpha);

        if (mFilterItemInfos != null && mFilterItemInfos.size() > 0)
        {
            FilterAdapter.ItemInfo itemInfo = mFilterItemInfos.get(0);
            if (itemInfo instanceof FilterAdapter.HeadItemInfo)
            {
                ((FilterAdapter.HeadItemInfo) itemInfo).isSelectBlur = isBlured;
                ((FilterAdapter.HeadItemInfo) itemInfo).isSelectDark = isDarked;
                mFilterAdapter.notifyItemChanged(0);
            }
        }

        if (mFilterUri == 0)
        {
            postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    if (mFilterAdapter != null)
                    {
                        mFilterAdapter.SetSelectByUri(FilterAdapter.OriginalItemInfo.ORIGINAL_ITEM_URI, true, true, false);
                    }
                }
            }, 500);
        }
        else
        {
            postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    if (mFilterAdapter != null)
                    {
                        mFilterAdapter.SetSelectByUri(mFilterUri, true, true, false);
                    }
                }
            }, 500);
        }
    }


    private void msgAdvanced(Message msg)
    {
        WatermarkItem item = WatermarkResMgr2.getInstance().GetWaterMarkById(mWaterMarkId);
        if (item != null)
        {
            TongJi2.AddCountByRes(getContext(), item.mTongJiId);
        }

        BeautyHandler.AdvancedMsg param = (BeautyHandler.AdvancedMsg) msg.obj;
        msg.obj = null;

        //缓存操作后的图片路径
        if (param.mTempPath != null)
        {
            mSite.m_myParams.put(RESTORE_IMGS, param.mTempPath);
        }

        RotationImg2 info = null;
        if (param.mImgs != null && param.mImgs instanceof RotationImg2)
        {
            info = (RotationImg2) param.mImgs;
        }
        else
        {
            if (mImgs instanceof ImageFile2)
            {
                RotationImg2[] img = ((ImageFile2) mImgs).SaveImg2(getContext());
                info = img[0];

            }
            else if (mImgs instanceof RotationImg2[])
            {
                RotationImg2[] img = (RotationImg2[]) mImgs;
                info = img[0];
            }
        }

        if (mSite != null)
        {
            HashMap<String, Object> params = new HashMap<>();
            //记录 拍照-预览-美颜美化-保存的闪关灯模式，用于继续拍照流程
            if (mSite.m_inParams != null)
            {
                if (mSite.m_inParams.containsKey(CameraSetDataKey.KEY_CAMERA_FLASH_MODE))
                {
                    params.put(CameraSetDataKey.KEY_CAMERA_FLASH_MODE, mSite.m_inParams.get(CameraSetDataKey.KEY_CAMERA_FLASH_MODE));
                }

                if (mSite.m_inParams.containsKey(CameraSetDataKey.KEY_CAMERA_FRONT_SPLASH_MASK))
                {
                    params.put(CameraSetDataKey.KEY_CAMERA_FRONT_SPLASH_MASK, mSite.m_inParams.get(CameraSetDataKey.KEY_CAMERA_FRONT_SPLASH_MASK));
                }
            }

            if (!mFromCamera) {
                params.put("show_exit_dialog", false);
            }
            mSite.OnBeauty(getContext(), params, info, mFilterUri, mFilterAlpha, hasWaterMark, mWaterMarkId);
        }
    }

    private void setViewBottomBmp(Bitmap bitmap)
    {
        if (bitmap != null && mView != null)
        {
            mView.setOrgImage(bitmap);
        }
    }

    private void setViewTopBmp(Bitmap topBmp)
    {
        if (topBmp != null && mView != null)
        {
            mView.setMaskImage(topBmp);
        }
    }

    private void setViewFilterAlpha(int alpha)
    {
        mFilterAlpha = alpha;
        if (mView != null)
        {
            mView.setFilterAlpha(mFilterAlpha);
        }
    }

    private void updateView()
    {
        if (mView != null)
        {
            mView.invalidate();
        }
    }

    private void setFilterSeekBarProgress(int progress)
    {
        if (mFilterAdapter != null)
        {
            mFilterAdapter.setCurrentAlphaProgress(progress);
        }
    }

    private void setWaterMark(boolean hasWaterMark)
    {
        this.hasWaterMark = hasWaterMark;
        if (mView != null)
        {
            mView.setDrawWaterMark(hasWaterMark);
        }
        if (hasWaterMark)
        {
            if (mWatermarkResArr == null)
            {
                mWatermarkResArr = WatermarkResMgr2.getInstance().sync_GetLocalRes(getContext(), null);
            }
            WatermarkItem watermarkItem = WatermarkResMgr2.getInstance().GetWaterMarkById(mWatermarkResArr, mWaterMarkId);
            if (watermarkItem != null && mView != null)
            {
                mWaterMarkId = watermarkItem.mID;
                if (isDoWaterAlphaAnim)
                {
                    mView.AddWaterMarkWithAnim(MakeBmpV2.DecodeImage(getContext(),
                            watermarkItem.res, 0, -1, -1, -1, Bitmap.Config.ARGB_8888), mWaterMarkId == WatermarkResMgr2.getInstance().GetNonWatermarkId(getContext()));
                }
                else
                {
                    mView.AddWaterMark(MakeBmpV2.DecodeImage(getContext(),
                            watermarkItem.res, 0, -1, -1, -1, Bitmap.Config.ARGB_8888), mWaterMarkId == WatermarkResMgr2.getInstance().GetNonWatermarkId(getContext()));
                }
                isDoWaterAlphaAnim = false;
            }
        }
    }

    private void showWaterMarkView(final boolean show)
    {
        if (isShowWaterMarkView == show || isDoingWVAnim) return;
        this.isShowWaterMarkView = show;
        if (mWCenterBtn != null)
        {
            mWCenterBtn.setBtnStatus(true, !show);
        }
        if (show)
        {
            initWaterMarkUI();
            this.post(new Runnable()
            {
                @Override
                public void run()
                {
                    mWatermarkRecyclerView.smoothScrollToPosition(mWatermarkAdapter.GetPosition());
                }
            });
        }

        float start = show ? mWatermarkFrTranslationY : 0;
        float end = show ? 0 : mWatermarkFrTranslationY;
        ObjectAnimator object = ObjectAnimator.ofFloat(mWatermarkFr, "translationY", start, end);
        object.setDuration(300);
        object.addListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationStart(Animator animation)
            {
                isDoingWVAnim = true;
                if (show)
                {
                    mWatermarkFr.setVisibility(VISIBLE);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation)
            {
                isDoingWVAnim = false;
                if (!show)
                {
                    mWatermarkFr.setVisibility(GONE);
                }
                else
                {
                    scroll2Center(mWatermarkAdapter.GetPosition());
                }
            }
        });
        object.start();
    }

    private void initWaterMarkUI()
    {
        if (mWatermarkRecyclerView == null || mWatermarkFr == null || mWatermarkAdapter == null)
        {
            mWatermarkFr = new FrameLayout(getContext());
            mWatermarkFr.setClickable(true);
            LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, mWatermarkFrTranslationY);
            params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
            this.addView(mWatermarkFr, params);
            {
                FrameLayout topBar = new FrameLayout(getContext());
                topBar.setBackgroundColor(Color.WHITE);
                params = new LayoutParams(LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(88));
                params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
                mWatermarkFr.addView(topBar, params);

                mWCenterBtn = new MyStatusButton(getContext());
                mWCenterBtn.setData(R.drawable.filterbeautify_watermark_icon, getContext().getString(R.string.filterpage_watermark));
                mWCenterBtn.setBtnStatus(true, !isShowWaterMarkView);
                mWCenterBtn.setOnClickListener(mOnClickListener);
                params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
                params.gravity = Gravity.CENTER;
                topBar.addView(mWCenterBtn, params);
            }

            mWatermarkRecyclerView = new RecyclerView(getContext());
            ((SimpleItemAnimator) mWatermarkRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
            mWatermarkRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            mWatermarkRecyclerView.setHasFixedSize(true);
            params = new LayoutParams(LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(232));
            params.gravity = Gravity.CENTER_VERTICAL | Gravity.BOTTOM;
            mWatermarkFr.addView(mWatermarkRecyclerView, params);

            if (mWatermarkResArr == null)
            {
                mWatermarkResArr = WatermarkResMgr2.getInstance().sync_GetLocalRes(getContext(), null);
            }
            mWatermarkAdapter = new WatermarkAdapter(getContext());
            mWatermarkAdapter.SetData(mWatermarkResArr);//水印数据集
            mWatermarkAdapter.SetSelectedId(mWaterMarkId);
            mWatermarkAdapter.setListener(new WatermarkAdapter.OnItemClickListener()
            {
                @Override
                public void onItemClick(int position, WatermarkItem item)
                {
                    //水印素材改变，重新置空保存过得照片
                    mSavePath = null;
                    mView.setDrawWaterMark(true);
                    mWaterMarkId = item.mID;
                    SettingInfoMgr.GetSettingInfo(getContext()).SetPhotoWatermarkId(mWaterMarkId);
                    if (isDoWaterAlphaAnim)
                    {
                        mView.AddWaterMarkWithAnim(MakeBmpV2.DecodeImage(getContext(), item.res, 0, -1, -1, -1, Bitmap.Config.ARGB_8888), item.mID == WatermarkResMgr2.getInstance().GetNonWatermarkId(getContext()));
                    }
                    else
                    {
                        mView.AddWaterMark(MakeBmpV2.DecodeImage(getContext(), item.res, 0, -1, -1, -1, Bitmap.Config.ARGB_8888), item.mID == WatermarkResMgr2.getInstance().GetNonWatermarkId(getContext()));
                    }
                    isDoWaterAlphaAnim = false;
                    scroll2Center(position);
                }
            });
            mWatermarkRecyclerView.setAdapter(mWatermarkAdapter);

            if (mWatermarkRecyclerView.getBackground() == null)
            {
                //水印素材区域毛玻璃处理
                Bitmap temp = CommonUtils.GetScreenBmp((Activity) getContext(), ShareData.m_screenWidth, ShareData.m_screenHeight);
                Bitmap out = MakeBmp.CreateFixBitmap(temp, temp.getWidth(), ShareData.PxToDpi_xhdpi(320), MakeBmp.POS_END, 0, Bitmap.Config.ARGB_8888);
                Bitmap mask = filter.fakeGlassBeauty(out, 0x99000000);//60%黑色
                mWatermarkRecyclerView.setBackgroundDrawable(new BitmapDrawable(getResources(), mask));
            }

        }
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

    private Bitmap mScreenshots;

    private Bitmap getScreenshots()
    {
        if (mScreenshots == null || mScreenshots.isRecycled())
        {
            mScreenshots = filter.fakeGlassBeauty(SharePage.screenCapture(getContext(), ShareData.m_screenWidth, ShareData.m_screenHeight), 0xdcf5f5f5);
        }
        return mScreenshots;
    }

    private void showSharePage()
    {
        closeSharePage();
        mSharePage = new SimpleSharePage(getContext());
        mSharePage.needAnime();
        mSharePage.setScreenshots(getScreenshots(), ShareData.PxToDpi_xhdpi(550));
        LayoutParams fl = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        fl.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
        this.addView(mSharePage, fl);
        ArrayList<SimpleSharePage.ShareType> shareTypeArrayList = new ArrayList<>();
        shareTypeArrayList.add(SimpleSharePage.ShareType.wechat);
        shareTypeArrayList.add(SimpleSharePage.ShareType.wechat_friends_circle);
        shareTypeArrayList.add(SimpleSharePage.ShareType.qq);
        shareTypeArrayList.add(SimpleSharePage.ShareType.qzone);
        shareTypeArrayList.add(SimpleSharePage.ShareType.sina);
        shareTypeArrayList.add(SimpleSharePage.ShareType.facebook);
        shareTypeArrayList.add(SimpleSharePage.ShareType.instagram);
        shareTypeArrayList.add(SimpleSharePage.ShareType.twitter);
        mSharePage.init(shareTypeArrayList, new SimpleSharePage.SimpleSharePageClickListener()
        {
            @Override
            public void onClick(SimpleSharePage.ShareType type)
            {
                if (type == null)
                {
                    closeSharePage();
                    return;
                }

                initShareSendUtil();

                if (!NetState.IsConnectNet(getContext()))
                {
                    Toast.makeText(getContext(), getContext().getResources().getText(R.string.net_weak_tip), Toast.LENGTH_LONG).show();
                    return;
                }

                switch (type)
                {
                    case wechat:
                        sendPicToWeiXin(mSavePath, true);
                        break;
                    case wechat_friends_circle:
                        sendPicToWeiXin(mSavePath, false);
                        break;
                    case sina:
                        sendPicToSina(mSavePath);
                        break;
                    case qq:
                        sendPicToQQ(mSavePath);
                        break;
                    case qzone:
                        sendPicToQzone(mSavePath);
                        break;
                    case facebook:
                        sendPicToFacebook(mSavePath);
                        break;
                    case twitter:
                        sendPicToTwitter(mSavePath, null);
                        break;
                    case instagram:
                        sendPicToInstagram(mSavePath);
                        break;
                    case community:
                        shareToCommunity();
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void closeSharePage()
    {
        if (mSharePage != null)
        {
            this.removeView(mSharePage);
            mSharePage.close();
            mSharePage = null;
        }
    }

    @Override
    public boolean onActivityKeyDown(int keyCode, KeyEvent event)
    {
        switch (keyCode)
        {
            case KeyEvent.KEYCODE_UNKNOWN:
            case KeyEvent.KEYCODE_CAMERA:
            case KeyEvent.KEYCODE_VOLUME_UP:
            case KeyEvent.KEYCODE_ENTER:
            {
                if (mSharePage != null
                        || !mUiEnable
                        || isShowFilterView
                        || (mRecomView != null && mRecomView.IsShow()))
                {
                    return super.onActivityKeyDown(keyCode, event);
                }
                //保存
                if (mSaveBtn != null && mOnAnimationClickListener != null)
                {
                    mOnAnimationClickListener.onAnimationClick(mSaveBtn);
                }
                return true;
            }
        }
        return super.onActivityKeyDown(keyCode, event);
    }

    @Override
    public void onBack()
    {
        if (mSharePage != null)
        {
            mSharePage.onBack();
            return;
        }

        if (mOnAnimationClickListener != null)
        {
            mOnAnimationClickListener.onAnimationClick(mBackBtn);
        }
    }

    @Override
    public void onPause()
    {
        TongJiUtils.onPagePause(getContext(), R.string.拍照_滤镜);
        hideCircle(mAlphaCircle);
        hideCircle(mBeautyCircle);
        super.onPause();
    }

    @Override
    public void onResume()
    {
        TongJiUtils.onPageResume(getContext(), R.string.拍照_滤镜);
        super.onResume();
    }


    @Override
    public void OnFingerDown(int fingerCount)
    {
        if (mSeekBar != null) mSeekBar.setEnabled(false);

        if (isShowWaterMarkView)
        {
            mOnClickListener.onClick(mWCenterBtn);
            return;
        }

        if (isShowFilterView)
        {
            mOnClickListener.onClick(mFCenterBtn);
        }
    }

    @Override
    public void OnFingerUp(int fingerCount)
    {
        if (mSeekBar != null) mSeekBar.setEnabled(true);
    }

    @Override
    public void OnClickWaterMask()
    {
        showWaterMarkView(!isShowWaterMarkView);
    }

    private void showFilterView(final boolean isShowFilterView)
    {
        if (this.isShowFilterView == isShowFilterView || isDoingFVAnim)
        {
            return;
        }
        this.isShowFilterView = isShowFilterView;
        float startY = 0, endY = 0;
        if (this.isShowFilterView)
        {
            MyBeautyStat.onClickByRes(R.string.拍照_拍照预览页_主页面_展开滤镜列表);
            MyBeautyStat.onPageStartByRes(R.string.拍照_拍照预览页_滤镜列表);
            TongJi2.AddCountByRes(getContext(), R.integer.拍照_预览_滤镜_预览_展开滤镜bar);

            startY = mFilterFrTranslationY;
            endY = 0;
        }
        else
        {
            MyBeautyStat.onClickByRes(R.string.拍照_拍照预览页_滤镜列表_收起滤镜列表);
            MyBeautyStat.onPageEndByRes(R.string.拍照_拍照预览页_滤镜列表);
            TongJi2.AddCountByRes(getContext(), R.integer.拍照_预览_滤镜_预览_收起滤镜bar);

            startY = 0;
            endY = mFilterFrTranslationY;
        }
        ObjectAnimator obj = ObjectAnimator.ofFloat(mFilterFr, "translationY", startY, endY);
        ObjectAnimator obj2 = ObjectAnimator.ofFloat(mSeekBar, "alpha", isShowFilterView ? 1 : 0, isShowFilterView ? 0 : 1);
        AnimatorSet set = new AnimatorSet();
        set.setDuration(300);
        set.playTogether(obj, obj2);
        set.addListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                isDoingFVAnim = false;
                if (!isShowFilterView)
                {
                    mFilterFr.setVisibility(GONE);
                }
                else
                {
                    mSeekBar.setVisibility(GONE);
                }
            }

            @Override
            public void onAnimationStart(Animator animation)
            {
                isDoingFVAnim = true;
                if (isShowFilterView)
                {
                    mFilterFr.setVisibility(VISIBLE);
                }
                else
                {
                    mSeekBar.setVisibility(VISIBLE);
                }
            }
        });
        set.start();
    }

    @Override
    public void OnSelFaceIndex(int index)
    {

    }

    @Override
    public void OnAnimFinish()
    {

    }

    private class UIHandler extends Handler
    {
        @Override
        public void handleMessage(Message msg)
        {
            showWaitDialog(false);
            mUiEnable = true;
            switch (msg.what)
            {
                case BeautyHandler.MSG_INIT:
                {
                    msgInit(msg);
                    break;
                }
                case BeautyHandler.MSG_FILTER:
                {
                    msgFilter(msg);
                    break;
                }
                case BeautyHandler.MSG_ADJUST:
                {
                    msgAdjust(msg);
                    break;
                }
                case BeautyHandler.MSG_BLUR_DARK:
                {
                    msgBlurDark(msg);
                    break;
                }
                case BeautyHandler.MSG_CANCEL:
                {
                    msgCancel();
                    break;
                }
                case BeautyHandler.MSG_SAVE:
                {
                    msgSave(msg);
                    break;
                }
                case BeautyHandler.MSG_RESTORE:
                {
                    msgRestore(msg);
                    break;
                }
                case BeautyHandler.MSG_ADVANCED:
                {
                    msgAdvanced(msg);
                    break;
                }
                default:
                    break;
            }
        }
    }

    @Override
    public void onPageResult(int siteID, HashMap<String, Object> params)
    {
        switch (siteID)
        {
            case SiteID.PUBLISH_OPUS_PAGE:
                if (params != null && params.containsKey("isSuccess"))
                {
                    boolean b = (boolean) params.get("isSuccess");
                    if (b)
                    {
                        SharedTipsView view = new SharedTipsView(getContext());
                        final Dialog dialog = new Dialog(getContext(), R.style.fullDialog1);
                        view.setJump2AppClickListener(new OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                if (mSite != null)
                                {
                                    mSite.onHome(getContext());
                                }
                                dialog.dismiss();
                            }
                        });
                        view.setStayClickListener(new OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                        dialog.setContentView(view);
                    }
                }
                break;

            case SiteID.LOGIN:
            case SiteID.REGISTER_DETAIL:
            case SiteID.RESETPSW:
                if (mRecomView != null)
                {
                    mRecomView.UpdateCredit();
                }
                if (mShareToCommunity)
                {
                    mShareToCommunity = false;
                    if(!UserMgr.IsLogin(getContext(), null)) return;
                    UserInfo userInfo = UserMgr.ReadCache(getContext());
                    if(userInfo == null) return;
                    if(TextUtils.isEmpty(userInfo.mMobile)) return ;
                    shareToCommunity();
                }
                break;
            case SiteID.FILTER_DOWNLOAD_MORE:
                //滤镜管理
                boolean isChange = false;
                Object o = params.get("is_change");
                if (o != null && o instanceof Boolean)
                {
                    isChange = (Boolean) o;
                }

                if (isChange)
                {
                    mFilterItemInfos = FilterResMgr.GetFilterRes(getContext(), false);
                    if (mFilterAdapter != null)
                    {
                        mFilterAdapter.SetData(mFilterItemInfos);
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
                break;
            case SiteID.FILTER_DETAIL:
                //滤镜详情页马上使用
                Object obj = params.get("material_id");
                int id = 0;
                if (obj != null && obj instanceof Integer)
                {
                    id = (Integer) obj;
                }

                if (id != 0)
                {
                    mFilterItemInfos = FilterResMgr.GetFilterRes(getContext(), false);
                    final int finalId = id;
                    this.post(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            if (mFilterAdapter != null)
                            {
                                mFilterUri = 0;
                                mFilterAdapter.SetData(mFilterItemInfos);
                                mFilterAdapter.SetSelectByUri(finalId);
                            }
                        }
                    });
                }
                break;
        }
        super.onPageResult(siteID, params);
    }

    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (mShareSendUtil != null)
        {
            mShareSendUtil.onActivityResult(requestCode, resultCode, data);
        }
        return super.onActivityResult(requestCode, resultCode, data);
    }

    private static class BtnFr extends FrameLayout
    {
        ImageView icon;
        TextView title;
        int titleW;
        int defW;

        public BtnFr(@NonNull Context context,
                     @DrawableRes int resId,
                     @StringRes int txtId,
                     OnAnimationClickListener listener,
                     boolean sysColor)
        {
            super(context);
            setOnTouchListener(listener);
            icon = new ImageView(context);
            icon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            icon.setImageResource(resId);
            if (sysColor)
            {
                ImageUtils.AddSkin(context, icon);
            }
            LayoutParams params = new LayoutParams(defW = ShareData.PxToDpi_xhdpi(70), ShareData.PxToDpi_xhdpi(70));
            params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
            this.addView(icon, params);

            title = new TextView(context);
            title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 11);
            title.setTextColor(sysColor ? ImageUtils.GetSkinColor() : 0xff999999);
            title.setGravity(Gravity.CENTER);
            title.setText(txtId);
            params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
            params.topMargin = ShareData.PxToDpi_xhdpi(70 + 4);
            titleW = (int) title.getPaint().measureText(getResources().getString(txtId));
            this.addView(title, params);
        }

        int hasOffset()
        {
            return (titleW - defW) / 2;
        }
    }


    // -----------------  社交分享模块 ---------------
    private ShareSendUtil mShareSendUtil;

    private void initShareSendUtil()
    {
        if (mShareSendUtil == null)
        {
            mShareSendUtil = new ShareSendUtil();
        }
    }

    private void sendPicToWeiXin(String pic, final boolean WXSceneSession)
    {
        if (WXSceneSession)
        {
            TongJi2.AddCountByRes(getContext(), R.integer.拍照_预览_保存_分享到微信好友);
        }
        else
        {
            TongJi2.AddCountByRes(getContext(), R.integer.拍照_预览_保存_分享到朋友圈);
        }

        if (mShareSendUtil != null)
        {
            mShareSendUtil.sendPicToWeiXin(getContext(), pic, WXSceneSession, new ShareSendUtil.OnShareSendCallback()
            {
                @Override
                public void onSendCallback(int type)
                {
                    switch (type)
                    {
                        case ShareSendUtil.SendCBType.succeed:
                            if (WXSceneSession)
                            {
                                MyBeautyStat.onShareCompleteByRes(MyBeautyStat.BlogType.微信好友, R.string.拍照_拍照保存页_主页面);
                            }
                            else
                            {
                                MyBeautyStat.onShareCompleteByRes(MyBeautyStat.BlogType.朋友圈, R.string.拍照_拍照保存页_主页面);
                            }
                            break;
                        default:
                            break;
                    }
                }
            });
        }
    }

    private void sendPicToSina(String pic)
    {
        TongJi2.AddCountByRes(getContext(), R.integer.拍照_预览_保存_分享到新浪);

        if (mShareSendUtil != null)
        {
            mShareSendUtil.sendPicToSina(getContext(), pic, new ShareSendUtil.OnShareSendCallback()
            {
                @Override
                public void onSendCallback(int type)
                {
                    switch (type)
                    {
                        case ShareSendUtil.SendCBType.succeed:
                            MyBeautyStat.onShareCompleteByRes(MyBeautyStat.BlogType.微博, R.string.拍照_拍照保存页_主页面);
                            break;
                        default:
                            break;
                    }
                }
            });
        }
    }

    private void sendPicToQQ(String pic)
    {
        TongJi2.AddCountByRes(getContext(), R.integer.拍照_预览_保存_分享到QQ好友);

        if (mShareSendUtil != null)
        {
            mShareSendUtil.sendPicToQQ(getContext(), pic, new ShareSendUtil.OnShareSendCallback()
            {
                @Override
                public void onSendCallback(int type)
                {
                    switch (type)
                    {
                        case ShareSendUtil.SendCBType.succeed:
                        {
                            MyBeautyStat.onShareCompleteByRes(MyBeautyStat.BlogType.QQ好友, R.string.拍照_拍照保存页_主页面);
                        }
                        break;
                    }
                }
            });
        }
    }

    private void sendPicToQzone(String pic)
    {
        TongJi2.AddCountByRes(getContext(), R.integer.拍照_预览_保存_分享到QQ空间);

        if (mShareSendUtil != null)
        {
            mShareSendUtil.sendPicToQzone(getContext(), pic, new ShareSendUtil.OnShareSendCallback()
            {
                @Override
                public void onSendCallback(int type)
                {
                    switch (type)
                    {
                        case ShareSendUtil.SendCBType.succeed:
                        {
                            MyBeautyStat.onShareCompleteByRes(MyBeautyStat.BlogType.QQ空间, R.string.拍照_拍照保存页_主页面);
                        }
                        break;
                        default:
                            break;
                    }
                }
            });
        }
    }

    private void sendPicToFacebook(String pic)
    {
        TongJi2.AddCountByRes(getContext(), R.integer.拍照_预览_保存_分享到facebook);

        if (mShareSendUtil != null)
        {
            mShareSendUtil.sendPicToFacebook(getContext(), pic, new ShareSendUtil.OnShareSendCallback()
            {
                @Override
                public void onSendCallback(int type)
                {
                    switch (type)
                    {
                        case ShareSendUtil.SendCBType.succeed:
                        {
                            MyBeautyStat.onShareCompleteByRes(MyBeautyStat.BlogType.Facebook, R.string.拍照_拍照保存页_主页面);
                        }
                        break;
                        default:
                            break;
                    }
                }
            });
        }
    }

    /**
     * 发送图片和文字到Twitter，两者至少有一种
     *
     * @param pic     图片路径
     * @param content 文字内容
     */
    private void sendPicToTwitter(String pic, String content)
    {
        TongJi2.AddCountByRes(getContext(), R.integer.拍照_预览_保存_分享到twitter);

        if (mShareSendUtil != null)
        {
            mShareSendUtil.sendPicToTwitter(getContext(), pic, content, new ShareSendUtil.OnShareSendCallback()
            {
                @Override
                public void onSendCallback(int type)
                {
                    switch (type)
                    {
                        case ShareSendUtil.SendCBType.succeed:
                        {
                            MyBeautyStat.onShareCompleteByRes(MyBeautyStat.BlogType.Twitter, R.string.拍照_拍照保存页_主页面);
                        }
                        break;
                        default:
                            break;
                    }
                }
            });
        }
    }

    private void sendPicToInstagram(String pic)
    {
        TongJi2.AddCountByRes(getContext(), R.integer.拍照_预览_保存_分享到ins);

        if (mShareSendUtil != null)
        {
            mShareSendUtil.sendPicToInstagram(getContext(), pic, new ShareSendUtil.OnShareSendCallback()
            {
                @Override
                public void onSendCallback(int type)
                {
                    switch (type)
                    {
                        case ShareSendUtil.SendCBType.succeed:
                        {
                        }
                        break;
                        default:
                            break;
                    }
                }
            });
        }
    }

    private void shareToCommunity()
    {
        if(mSite == null) return;
        mShareToCommunity = true;
        if(!UserMgr.IsLogin(getContext(), null))
        {
            //未登录去登录
            mSite.onLoginCommunity(getContext());
            return;
        }
        UserInfo userInfo = UserMgr.ReadCache(getContext());
        if(userInfo != null && TextUtils.isEmpty(userInfo.mMobile))
        {
            //未完善资料去完善资料
            mSite.onBindPhone(getContext());
            return;
        }
        mShareToCommunity = false;
        if(mSavePath == null || TextUtils.isEmpty(mSavePath) || !FileUtil.isFileExists(mSavePath))
        {
            Toast.makeText(getContext(), R.string.share_error_image_is_null, Toast.LENGTH_SHORT).show();
            return;
        }
        mSite.onCommunity(getContext(), mSavePath, 1);
    }
}
