package cn.poco.filterPendant;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import cn.poco.MaterialMgr2.site.DownloadMorePageSite;
import cn.poco.advanced.ImageUtils;
import cn.poco.advanced.RecommendItemConfig2;
import cn.poco.beautify.RecomDisplayMgr;
import cn.poco.beautify4.Beautify4Page;
import cn.poco.camera.RotationImg2;
import cn.poco.cloudalbumlibs.utils.CloudAlbumDialog;
import cn.poco.filterPendant.site.FilterPendantPageSite;
import cn.poco.framework.BaseSite;
import cn.poco.framework.DataKey;
import cn.poco.framework.IPage;
import cn.poco.framework.SiteID;
import cn.poco.recycleview.AbsAdapter;
import cn.poco.recycleview.AbsDragAdapter;
import cn.poco.resource.BaseRes;
import cn.poco.resource.DownloadMgr;
import cn.poco.resource.GlassRes;
import cn.poco.resource.GlassResMgr2;
import cn.poco.resource.IDownload;
import cn.poco.resource.LockRes;
import cn.poco.resource.LockResMgr2;
import cn.poco.resource.RecommendRes;
import cn.poco.resource.ResType;
import cn.poco.resource.ResourceUtils;
import cn.poco.statistics.MyBeautyStat;
import cn.poco.statistics.TongJi2;
import cn.poco.statistics.TongJiUtils;
import cn.poco.system.SysConfig;
import cn.poco.system.TagMgr;
import cn.poco.system.Tags;
import cn.poco.tianutils.CommonUtils;
import cn.poco.tianutils.MakeBmp;
import cn.poco.tianutils.MakeBmpV2;
import cn.poco.tianutils.ShareData;
import cn.poco.utils.OnAnimationClickListener;
import cn.poco.view.BaseView;
import cn.poco.view.material.FilterPendantViewEx;
import cn.poco.widget.recycle.RecommendAdapter;
import cn.poco.widget.recycle.RecommendConfig;
import cn.poco.widget.recycle.RecommendDragContainer;
import my.beautyCamera.R;

import static cn.poco.filterPendant.FilterPendantResMgr.isPendantNewFlag;
import static cn.poco.filterPendant.FilterPendantResMgr.isShapeNewFlag;

/**
 * 毛玻璃主界面
 */
public class FilterPendantPage extends IPage {
    //private static final String TAG = "毛玻璃";

    protected FilterPendantPageSite m_site;

    public boolean mUiEnabled;
    private Bitmap mOrgBmp; //原图
    private Bitmap mShowBmp;

    //    private PendantViewEx mView;
    private FilterPendantViewEx mView;
    private Object mImgs;

    private int m_viewWidth, m_viewHeight;
    private int mBottomBarHeight;
    private int mBottomLayoutHeight;

    private int mPendantDefWhiteColor = 0x00FFFFFF;
    private int mPendantColor = mPendantDefWhiteColor;
    private int mShapeDefWhiteColor = 0x00FFFFFF;
    private int mShapeColor = mShapeDefWhiteColor;

    private FrameLayout mColorIcon;
    private FrameLayout mReverseIcon;
    private FrameLayout mBottomFr;

    private FrameLayout mBottomBar;
    private ImageView mCancelBtn;
    private ImageView mOkBtn;
    private LinearLayout mCenterBtn;
    private MyStatusButton mPendantTypeBtn;
    private MyStatusButton mShapeTypeBtn;
    private boolean isPendantType;
    private boolean isReverseMode;
    private boolean isColorMode;

    private FrameLayout mColorChooseBar;
    private RecommendDragContainer mPendantList;
    private RecommendDragContainer mShapeList;
    private RecommendAdapter mPendantAdapter;
    private RecommendAdapter mShapeAdapter;
    private int mPendantTypeUri;
    private int mShapeTypeUri;
    private ArrayList<RecommendAdapter.ItemInfo> mPendantListInfos;
    private ArrayList<RecommendAdapter.ItemInfo> mShapeListInfos;

    private ColorChangeLayoutV2 colorPendantChangeLayout;
    private ColorChangeLayoutV2 colorShapeChangeLayout;


    private ArrayList<GlassRes> m_allResArr;

    protected RecomDisplayMgr m_recomView;

    private RecommendItemConfig2 m_config;
    private int DEF_IMG_SIZE;

    //ui anim
    private float m_currImgH = 0f;
    private int m_imgH = 0;
    private int m_viewH = 0;
    private int m_viewTopMargin;
    private static final int SHOW_VIEW_ANIM = 300;


    public FilterPendantPage(Context context, BaseSite site) {
        super(context, site);

        m_site = (FilterPendantPageSite) site;

        initDate(getContext());
        initUI(getContext());

        MyBeautyStat.onPageStartByRes(R.string.美颜美图_毛玻璃页面_主页面);
        TongJiUtils.onPageStart(getContext(), R.string.毛玻璃);
    }

    public void initDate(Context context) {
        ShareData.InitData(context);
        DEF_IMG_SIZE = SysConfig.GetPhotoSize(context);

        m_config = new RecommendItemConfig2(getContext(), true);
        mBottomLayoutHeight = ShareData.PxToDpi_xhdpi(232);
        mBottomBarHeight = ShareData.PxToDpi_xhdpi(88);
        m_viewWidth = ShareData.m_screenWidth;
        m_viewHeight = ShareData.m_screenHeight - ShareData.PxToDpi_xhdpi(320);
        mUiEnabled = true;

        if (DownloadMgr.getInstance() != null) {
            DownloadMgr.getInstance().AddDownloadListener(m_downloadLst);
        }

        mPendantListInfos = FilterPendantResMgr.getRess(context,GlassRes.GLASS_TYPE_PENDANT);
        mShapeListInfos = FilterPendantResMgr.getRess(getContext(),GlassRes.GLASS_TYPE_SHAPE);
        m_allResArr = GlassResMgr2.getInstance().GetResArr();
        isPendantType = true;
        isReverseMode = false;
    }

    public DownloadMgr.DownloadListener m_downloadLst = new DownloadMgr.DownloadListener() {
        @Override
        public void OnDataChange(int resType, int downloadId, IDownload[] resArr) {
            if (resArr != null && ((BaseRes) resArr[0]).m_type == BaseRes.TYPE_LOCAL_PATH && resType == ResType.GLASS.GetValue()) {
                ArrayList<RecommendAdapter.ItemInfo> pendant_arr = FilterPendantResMgr.getRess(getContext(), GlassRes.GLASS_TYPE_PENDANT);
                if (pendant_arr.size() > mPendantListInfos.size())
                {
                    mPendantAdapter.notifyItemDownLoad(pendant_arr.size() - mPendantListInfos.size());
                }
                mPendantListInfos = pendant_arr ;
                mPendantAdapter.SetData(mPendantListInfos);
                mPendantAdapter.notifyDataSetChanged();

                ArrayList<RecommendAdapter.ItemInfo> shape_arr = FilterPendantResMgr.getRess(getContext(), GlassRes.GLASS_TYPE_SHAPE);
                if (shape_arr.size() > mShapeListInfos.size())
                {
                    mShapeAdapter.notifyItemDownLoad(shape_arr.size() - mShapeListInfos.size());
                }
                mShapeListInfos = shape_arr;
                mShapeAdapter.SetData(mShapeListInfos);
                mShapeAdapter.notifyDataSetChanged();
            }
        }
    };

    public void initUI(Context context) {
        LayoutParams fl_lp;
        LinearLayout.LayoutParams ll_lp;

        m_viewWidth += 2;
        m_viewHeight += 2;
        mView = new FilterPendantViewEx(getContext());
        mView.setControlCallback(m_controlCallback);
        fl_lp = new LayoutParams(m_viewWidth, m_viewHeight);
        fl_lp.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        fl_lp.topMargin = -1;
        mView.setLayoutParams(fl_lp);
        this.addView(mView, 0);

        mBottomFr = new FrameLayout(context);
        fl_lp = new LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        fl_lp.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        mBottomFr.setLayoutParams(fl_lp);
        this.addView(mBottomFr);

        //颜色模板 方向编辑
        LinearLayout adjustContainer = new LinearLayout(context);
        fl_lp = new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        fl_lp.gravity = Gravity.RIGHT | Gravity.BOTTOM;
        fl_lp.bottomMargin = mBottomBarHeight + mBottomLayoutHeight;
        adjustContainer.setPadding(0, 0, ShareData.PxToDpi_xhdpi(28), ShareData.PxToDpi_xhdpi(28));
        adjustContainer.setLayoutParams(fl_lp);
        mBottomFr.addView(adjustContainer);
        {
            mReverseIcon = new FrameLayout(context);
            mReverseIcon.setBackgroundResource(R.drawable.beautify_white_circle_bg);
            mReverseIcon.setOnTouchListener(mOnAnimationClickListener);
            mReverseIcon.setVisibility(View.GONE);
            {
                ImageView reverseIcon = new ImageView(context);
                reverseIcon.setImageResource(R.drawable.advanced_beautify_glass_reverse);
                ImageUtils.AddSkin(context, reverseIcon);
                fl_lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                fl_lp.gravity = Gravity.CENTER;
                mReverseIcon.addView(reverseIcon, fl_lp);
            }

            ll_lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            ll_lp.setMargins(0, 0, 0, 0);
            mReverseIcon.setLayoutParams(ll_lp);
            adjustContainer.addView(mReverseIcon);

            mColorIcon = new FrameLayout(context);
            mColorIcon.setBackgroundResource(R.drawable.beautify_white_circle_bg);
            mColorIcon.setOnTouchListener(mOnAnimationClickListener);
            mColorIcon.setVisibility(View.GONE);
            {
                ImageView colorIcon = new ImageView(context);
                colorIcon.setImageResource(R.drawable.advanced_beautify_frame_color_palette2);
                ImageUtils.AddSkin(context, colorIcon);
                fl_lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                fl_lp.gravity = Gravity.CENTER;
                mColorIcon.addView(colorIcon, fl_lp);
            }
            ll_lp.setMargins(ShareData.PxToDpi_xhdpi(18), 0, 0, 0);
            ll_lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            mColorIcon.setLayoutParams(ll_lp);
            adjustContainer.addView(mColorIcon);
        }

        mBottomBar = new FrameLayout(getContext());
        mBottomBar.setBackgroundColor(0xe6ffffff);
        fl_lp = new LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, mBottomBarHeight);
        fl_lp.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
        fl_lp.bottomMargin = mBottomLayoutHeight;
        mBottomBar.setLayoutParams(fl_lp);
        mBottomFr.addView(mBottomBar);
        {
            mCancelBtn = new ImageView(context);
            mCancelBtn.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            mCancelBtn.setImageResource(R.drawable.beautify_cancel);
            mCancelBtn.setPadding(ShareData.PxToDpi_xhdpi(22), 0, ShareData.PxToDpi_xhdpi(22), 0);
            fl_lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
            fl_lp.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
            mCancelBtn.setLayoutParams(fl_lp);
            mBottomBar.addView(mCancelBtn);
            mCancelBtn.setOnTouchListener(mOnAnimationClickListener);

            mOkBtn = new ImageView(context);
            mOkBtn.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            mOkBtn.setImageResource(R.drawable.beautify_ok);
            mOkBtn.setPadding(ShareData.PxToDpi_xhdpi(22), 0, ShareData.PxToDpi_xhdpi(22), 0);
            ImageUtils.AddSkin(getContext(), mOkBtn);
            fl_lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
            fl_lp.gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
            mOkBtn.setLayoutParams(fl_lp);
            mBottomBar.addView(mOkBtn);
            mOkBtn.setOnTouchListener(mOnAnimationClickListener);

            mCenterBtn = new LinearLayout(context);
            mCenterBtn.setOrientation(LinearLayout.HORIZONTAL);
            fl_lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
            fl_lp.gravity = Gravity.CENTER;
            mCenterBtn.setLayoutParams(fl_lp);
            mBottomBar.addView(mCenterBtn);
            mCenterBtn.setOnClickListener(mOnClickListener);
            {
                mPendantTypeBtn = new MyStatusButton(context);
                mPendantTypeBtn.setData(ImageUtils.AddSkin(context, BitmapFactory.decodeResource(getResources(), R.drawable.filterpendant_chartlet_def_icon)), getContext().getString(R.string.filterpendantpage_pendant));
                mPendantTypeBtn.setBtnStatus(isPendantType, isFold);
                mPendantTypeBtn.setOnClickListener(mOnClickListener);
                ll_lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
                ll_lp.gravity = Gravity.CENTER_VERTICAL;
                ll_lp.rightMargin = ShareData.PxToDpi_xhdpi(14);
                mPendantTypeBtn.setLayoutParams(ll_lp);
                mCenterBtn.addView(mPendantTypeBtn);

                mShapeTypeBtn = new MyStatusButton(context);
                mShapeTypeBtn.setData(ImageUtils.AddSkin(getContext(), BitmapFactory.decodeResource(getResources(), R.drawable.filterpendant_shape_def_icon)), getContext().getString(R.string.filterpendantpage_shape));
                mShapeTypeBtn.setBtnStatus(!isPendantType, isFold);
                mShapeTypeBtn.setOnClickListener(mOnClickListener);
                ll_lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
                ll_lp.gravity = Gravity.CENTER_VERTICAL;
                ll_lp.setMargins(ShareData.PxToDpi_xhdpi(40 + 14), 0, 0, 0);
                mShapeTypeBtn.setLayoutParams(ll_lp);
                mCenterBtn.addView(mShapeTypeBtn);
            }

        }
        //素材区域
        mColorChooseBar = new FrameLayout(context);
        fl_lp = new LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        fl_lp.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
        mColorChooseBar.setLayoutParams(ll_lp);
        mBottomFr.addView(mColorChooseBar);
        {
            RecommendConfig config = new RecommendConfig();
            mPendantAdapter = new RecommendAdapter(config);
            mPendantAdapter.setOnItemClickListener(mPendantListCallback);
            mPendantAdapter.setOnDragCallBack(mPendantListDragCB);
            mPendantAdapter.SetData(mPendantListInfos);
            mShapeAdapter = new RecommendAdapter(config);
            mShapeAdapter.SetData(mShapeListInfos);
            mShapeAdapter.setOnDragCallBack(mShapeListDragCB);
            mShapeAdapter.setOnItemClickListener(mShapeListCallback);

            mPendantList = new RecommendDragContainer(getContext(),mPendantAdapter);
            mShapeList = new RecommendDragContainer(getContext(),mShapeAdapter);
            mShapeList.setVisibility(View.GONE);
            fl_lp = new LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            fl_lp.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
            mBottomFr.addView(mPendantList, fl_lp);
            mBottomFr.addView(mShapeList, fl_lp);
        }


        m_recomView = new RecomDisplayMgr(getContext(), m_unlockListener);
        m_recomView.Create(this);
    }

    private void showColorIconAnim() {
        if (mColorIcon.getVisibility() == GONE) {
//            Log.d(TAG, "showColorIconAnim: ");
            mColorIcon.setScaleX(0);
            mColorIcon.setScaleY(0);
            mColorIcon.animate().scaleX(1).scaleY(1).setDuration(100).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    mColorIcon.setVisibility(VISIBLE);
                }
            }).start();
        }
    }

    private void hideColorIconAnim() {
        if (mColorIcon.getVisibility() == VISIBLE) {
//            Log.d(TAG, "hideColorIconAnim: ");
            mColorIcon.setScaleX(1);
            mColorIcon.setScaleY(1);
            mColorIcon.animate().scaleX(0).scaleY(0).setDuration(100).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mColorIcon.setVisibility(GONE);
                }
            }).start();
        }
    }

    private void showReverseIconAnim() {
        if (mReverseIcon.getVisibility() == GONE) {
//            Log.d(TAG, "showReverseIconAnim: ");
            mReverseIcon.setScaleX(0);
            mReverseIcon.setScaleY(0);
            mReverseIcon.animate().scaleX(1).scaleY(1).setDuration(100).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    mReverseIcon.setVisibility(VISIBLE);
                }
            }).start();
        }
    }

    private void hideReverseIconAnim() {
        if (mReverseIcon.getVisibility() == VISIBLE) {
//            Log.d(TAG, "hideReverseIconAnim: ");
            mReverseIcon.setScaleX(1);
            mReverseIcon.setScaleY(1);
            mReverseIcon.animate().scaleX(0).scaleY(0).setDuration(100).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mReverseIcon.setVisibility(GONE);
                }
            }).start();
        }
    }

    private boolean isFold = false;
    private OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mUiEnabled) {
                if (v == mCenterBtn) {
                    if (isPendantType) {
                        mOnClickListener.onClick(mPendantTypeBtn);
                    } else {
                        mOnClickListener.onClick(mShapeTypeBtn);
                    }
                } else if (v == mPendantTypeBtn) {
                    mPendantTypeBtn.setNewStatus(false);
                    if (isPendantType) {
                        isFold = !isFold;
                        mPendantTypeBtn.setBtnStatus(true, !mPendantTypeBtn.isDown());
                        mShapeTypeBtn.setBtnStatus(false, !mShapeTypeBtn.isDown());
                        SetViewFrState(isFold);
                        SetBottomFrState(isFold);
                    } else {
                        isPendantType = true;
                        mPendantTypeBtn.setBtnStatus(true, false);
                        mShapeTypeBtn.setBtnStatus(false, false);
                        boolean oldIsFold = isFold;
                        isFold = false;
                        if (oldIsFold) {
                            SetViewFrState(isFold);
                            SetBottomFrState(isFold);
                        }
                        mPendantList.setVisibility(View.VISIBLE);
                        mShapeList.setVisibility(View.INVISIBLE);
                        MyBeautyStat.onClickByRes(R.string.美颜美图_毛玻璃页面_主页面_贴纸tab);
                        TongJi2.AddCountByRes(getContext(), R.integer.修图_素材美化_毛玻璃_贴纸);
                    }
                } else if (v == mShapeTypeBtn) {
                    mShapeTypeBtn.setNewStatus(false);
                    if (!isPendantType) {
                        isFold = !isFold;
                        mPendantTypeBtn.setBtnStatus(false, !mPendantTypeBtn.isDown());
                        mShapeTypeBtn.setBtnStatus(true, !mShapeTypeBtn.isDown());
                        SetViewFrState(isFold);
                        SetBottomFrState(isFold);
                    } else {
                        isPendantType = false;
                        mPendantTypeBtn.setBtnStatus(false, false);
                        mShapeTypeBtn.setBtnStatus(true, false);
                        boolean oldIsFold = isFold;
                        isFold = false;
                        if (oldIsFold) {
                            SetViewFrState(isFold);
                            SetBottomFrState(isFold);
                        }
                        mShapeList.setVisibility(View.VISIBLE);
                        mPendantList.setVisibility(View.INVISIBLE);
                        MyBeautyStat.onClickByRes(R.string.美颜美图_毛玻璃页面_主页面_形状tab);
                        TongJi2.AddCountByRes(getContext(), R.integer.修图_素材美化_毛玻璃_形状);
                    }
                }
            }
        }
    };

    private OnAnimationClickListener mOnAnimationClickListener = new OnAnimationClickListener() {
        @Override
        public void onAnimationClick(View v) {
            if (mUiEnabled) {
                if (v == mCancelBtn) {
                    if (m_recomView != null && m_recomView.IsShow()) {
                        m_recomView.OnCancel(true);
                    } else {
                        MyBeautyStat.onClickByRes(R.string.美颜美图_毛玻璃页面_主页面_取消);
                        TongJi2.AddCountByRes(getContext(), R.integer.修图_素材美化_毛玻璃_取消);
                        if (mPendantTypeUri != 0 || mShapeTypeUri != 0) {
                            showExitDialog();
                        } else {
                            cancel();
                        }
                    }
                } else if (v == mOkBtn) {
                    MyBeautyStat.onClickByRes(R.string.美颜美图_毛玻璃页面_主页面_确认);
                    TongJi2.AddCountByRes(getContext(), R.integer.修图_素材美化_毛玻璃_确认);
                    int uri = isPendantType ? mPendantTypeUri : mShapeTypeUri;
                    for (GlassRes glass : m_allResArr) {
                        if (glass.m_id == uri) {
                            TongJi2.AddCountById(Integer.toString(glass.m_tjId));
                            break;
                        }
                    }
                    sendSensorsData();
                    if (m_site != null) {
                        HashMap<String, Object> params = new HashMap<>();
                        params.put("img", mView.getOutPutBmp());
                        params.putAll(getBackAnimParam());
                        m_site.OnSave(getContext(), params);
                    }
                } else if (v == mReverseIcon) {
                    isReverseMode = !isReverseMode;
                    mView.setReverseMode(isReverseMode);
                    if (isReverseMode)
                    {
                        MyBeautyStat.onClickByRes(R.string.美颜美图_毛玻璃页面_主页面_反向);
                    }
                } else if (v == mColorIcon) {
                    //选中才会显示颜色按钮的
                    if (mPendantSelectedIndex >= 0) {
                        FilterPendantViewEx.BlurShapeEx info = (FilterPendantViewEx.BlurShapeEx) mView.getPendantArrByIndex(mPendantSelectedIndex);
                        if (info != null && info.m_type == FilterPendantViewEx.TYPE_PENDANT) {
                            showPendantColorPalette(!isColorMode);
                        } else if (info != null && info.m_type == FilterPendantViewEx.TYPE_SHAPE) {
                            showShapeColorPalette(!isColorMode);
                            if (isColorMode)
                            {
                                MyBeautyStat.onClickByRes(R.string.美颜美图_毛玻璃页面_主页面_背景颜色);
                            }
                        }
                    }
                }
            }
        }

        @Override
        public void onTouch(View v) {

        }

        @Override
        public void onRelease(View v) {

        }
    };

//    private CloudAlbumDialog mBackHintDialog;
//
//    private void showExitDialog() {
//        if (mBackHintDialog == null) {
//            mBackHintDialog = new CloudAlbumDialog(getContext(),
//                    ViewGroup.LayoutParams.WRAP_CONTENT,
//                    ViewGroup.LayoutParams.WRAP_CONTENT);
//            ImageUtils.AddSkin(getContext(), mBackHintDialog.getOkButtonBg());
//            mBackHintDialog.setCancelButtonText(R.string.cancel)
//                    .setOkButtonText(R.string.ensure)
//                    .setMessage(R.string.confirm_back)
//                    .setListener(new CloudAlbumDialog.OnButtonClickListener() {
//                        @Override
//                        public void onOkButtonClick() {
//                            mBackHintDialog.dismiss();
//                            cancel();
//                        }
//
//                        @Override
//                        public void onCancelButtonClick() {
//                            mBackHintDialog.dismiss();
//                        }
//                    });
//        }
//        mBackHintDialog.show();
//    }

    /**
     * 神策素材使用埋点
     */
    private void sendSensorsData()
    {
        if (mView != null && mView.getPendantArray() != null && mView.getPendantArray().size() > 0)
        {
            String[] tongjiIds = new String[mView.getPendantArray().size()];
            for (int i = 0, size = mView.getPendantArray().size(); i < size; i++)
            {
                BaseView.Shape shape = mView.getPendantArray().get(i);
                if (shape != null && shape.m_info != null && shape.m_info instanceof GlassRes)
                {
                    tongjiIds[i] = String.valueOf(((GlassRes) shape.m_info).m_tjId);
                }
            }
            MyBeautyStat.onUseGlass(tongjiIds);
        }
    }

    private void cancel() {
        if (m_site != null) {
            HashMap<String, Object> params = new HashMap<>();
            params.put("img", mOrgBmp);
            params.putAll(getBackAnimParam());
            removeShowView();
            m_site.OnBack(getContext(), params);
        }
        releaseMem();
    }

    private HashMap<String, Object> getBackAnimParam() {
        HashMap<String, Object> params = new HashMap<>();
        float viewHeight = ShareData.m_screenHeight - mBottomBarHeight - mBottomLayoutHeight;
        float topMargin = (mView.getHeight() - viewHeight) / 2f;
        params.put(Beautify4Page.PAGE_BACK_ANIM_IMG_H, mView.getImgHeight());
        params.put(Beautify4Page.PAGE_BACK_ANIM_VIEW_TOP_MARGIN, topMargin);
        return params;
    }

//    protected TweenLite m_tween = new TweenLite();
//
//    private void UpdateViewHeight() {
//        if (mView != null && m_tween != null) {
//            LayoutParams fl = new LayoutParams(m_viewWidth, (int) m_tween.M1GetPos());
//            fl.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
//            mView.setLayoutParams(fl);
//            if (!m_tween.M1IsFinish()) {
//                this.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        UpdateViewHeight();
//                    }
//                }, 1);
//            }
//        }
//    }

    private void SetViewFrState(boolean isFold) {
        mView.clearAnimation();
        int start, end;
        if (isFold) {
//            start = 0;
//            end = mBottomLayoutHeight / 2;

            start = m_viewHeight;
            end = start + mBottomLayoutHeight;
        } else {
//            start = mBottomLayoutHeight / 2;
//            end = 0;

            start = ShareData.m_screenHeight - mBottomBarHeight;
            end = m_viewHeight;
        }
//        mUiEnabled = false;
//        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(mView, "translationY", start, end);
//        objectAnimator.setDuration(300);
//        objectAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
//        objectAnimator.addListener(new AnimatorListenerAdapter() {
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                mUiEnabled = true;
//            }
//        });
//        objectAnimator.start();

        mUiEnabled = false;
        ValueAnimator valueAnimator = ValueAnimator.ofInt(start, end);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (mView != null)
                {
                    LayoutParams fp = (LayoutParams) mView.getLayoutParams();
                    fp.height = (int) animation.getAnimatedValue();
                    mView.setLayoutParams(fp);
                }
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mUiEnabled = true;
            }
        });
        valueAnimator.setDuration(300);
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator.start();
    }

    private void SetBottomFrState(boolean isFold) {
        mBottomFr.clearAnimation();
        int start;
        int end;
        start = isFold ? 0 : mBottomLayoutHeight;
        end = isFold ? mBottomLayoutHeight : 0;
        ObjectAnimator object = ObjectAnimator.ofFloat(mBottomFr, "translationY", start, end);
        object.setDuration(300);
        object.setInterpolator(new AccelerateDecelerateInterpolator());
        object.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                mUiEnabled = false;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mUiEnabled = true;
            }
        });
        object.start();
    }

    /**
     * 平移显示消失动画
     *
     * @param show true:显示 false:隐藏
     */
    public void showWidgetAnim(View view, boolean show) {
        if (view == null) return;
        float fromX, toX, fromY, toY;
        if (show) {
            fromX = 0;
            toX = 0;
            fromY = 1.0f;
            toY = 0;
        } else {
            fromX = 0;
            toX = 0;
            fromY = 0;
            toY = 1.0f;
        }
        view.setVisibility(show ? VISIBLE : GONE);
        TranslateAnimation ta = new TranslateAnimation(Animation.RELATIVE_TO_SELF, fromX, Animation.RELATIVE_TO_SELF, toX, Animation.RELATIVE_TO_SELF, fromY, Animation.RELATIVE_TO_SELF, toY);
        ta.setDuration(300);
        ta.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                mUiEnabled = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mUiEnabled = true;
            }
        });
        view.startAnimation(ta);
    }

    private int mPendantDownloadingId = -1;

    private AbsDragAdapter.OnDragCallBack mPendantListDragCB = new AbsDragAdapter.OnDragCallBack() {
        @Override
        public void onItemDelete(AbsDragAdapter.ItemInfo info, int position) {

        }

        @Override
        public void onItemMove(AbsDragAdapter.ItemInfo info, int fromPosition, int toPosition) {
            AbsAdapter.ItemInfo itemInfo = mPendantListInfos.get(fromPosition);
            int fromPos = fromPosition;
            int toPos = toPosition;
            if (itemInfo != null) {
                fromPos = ResourceUtils.HasId(GlassResMgr2.getInstance().GetOrderArr(), itemInfo.m_uri);
            }
            itemInfo = mPendantListInfos.get(toPosition);
            if (itemInfo != null) {
                toPos = ResourceUtils.HasId(GlassResMgr2.getInstance().GetOrderArr(), itemInfo.m_uri);
            }
            if(fromPos >=0 && toPos >=0){
                ResourceUtils.ChangeOrderPosition(GlassResMgr2.getInstance().GetOrderArr(), fromPos, toPos);
                GlassResMgr2.getInstance().SaveOrderArr();
            }
        }

        @Override
        public void onDragStart(AbsDragAdapter.ItemInfo itemInfo, int position) {
            mPendantList.setCanDelete(false);
//            int out = BaseResMgr.HasItem(ThemeResMgr.GetLocalResArr(),itemInfo.m_uri);
//            if(out >=0 ){
//                mPendantList.setCanDelete(false);
//            }else{
//                mPendantList.setCanDelete(true);
//            }
        }

        @Override
        public void onDragEnd(AbsDragAdapter.ItemInfo itemInfo, int position) {

        }

        @Override
        public void onLongClick(AbsDragAdapter.ItemInfo itemInfo, int position) {

        }
    };

    private AbsDragAdapter.OnDragCallBack mShapeListDragCB = new AbsDragAdapter.OnDragCallBack() {
        @Override
        public void onItemDelete(AbsDragAdapter.ItemInfo info, int position) {

        }

        @Override
        public void onItemMove(AbsDragAdapter.ItemInfo info, int fromPosition, int toPosition) {
            AbsAdapter.ItemInfo itemInfo = mShapeListInfos.get(fromPosition);
            int fromPos = fromPosition;
            int toPos = toPosition;
            if (itemInfo != null) {
                fromPos = ResourceUtils.HasId(GlassResMgr2.getInstance().GetOrderArr(), itemInfo.m_uri);
            }
            itemInfo = mShapeListInfos.get(toPosition);
            if (itemInfo != null) {
                toPos = ResourceUtils.HasId(GlassResMgr2.getInstance().GetOrderArr(), itemInfo.m_uri);
            }
            if(fromPos >=0 && toPos >=0){
                ResourceUtils.ChangeOrderPosition(GlassResMgr2.getInstance().GetOrderArr(), fromPos, toPos);
                GlassResMgr2.getInstance().SaveOrderArr();
            }
        }

        @Override
        public void onDragStart(AbsDragAdapter.ItemInfo itemInfo, int position) {
            mShapeList.setCanDelete(false);
//            int out = BaseResMgr.HasItem(ThemeResMgr.GetLocalResArr(),itemInfo.m_uri);
//            if(out >=0 ){
//                mShapeList.setCanDelete(false);
//            }else{
//                mShapeList.setCanDelete(true);
//            }
        }

        @Override
        public void onDragEnd(AbsDragAdapter.ItemInfo itemInfo, int position) {

        }

        @Override
        public void onLongClick(AbsDragAdapter.ItemInfo itemInfo, int position) {

        }
    };


    private RecommendAdapter.OnItemClickListener mPendantListCallback = new AbsAdapter.OnItemClickListener() {
        @Override
        public void OnItemDown(AbsAdapter.ItemInfo info, int index) {

        }

        @Override
        public void OnItemUp(AbsAdapter.ItemInfo info, int index) {

        }

        @Override
        public void OnItemClick(AbsAdapter.ItemInfo info, int index) {
            if (mUiEnabled && info.m_uri == RecommendAdapter.DownloadItemInfo.DOWNLOAD_ITEM_URI) {
                TongJi2.AddCountByRes(getContext(), R.integer.修图_素材美化_毛玻璃_下载更多);//贴纸
                openDownloadMorePage(ResType.GLASS);
                return;
            }
            if (mUiEnabled && info.m_uri == RecommendAdapter.RecommendItemInfo.RECOMMEND_ITEM_URI) {
                //推荐位
                ArrayList<RecommendRes> ress = (ArrayList<RecommendRes>) ((RecommendAdapter.RecommendItemInfo) info).m_ex;
                RecommendRes recommendRes = null;
                if (ress != null && ress.size() > 0) {
                    recommendRes = ress.get(0);
                }
                if (recommendRes != null && m_recomView != null) {
                    showRecommendView(m_recomView, recommendRes, ResType.GLASS.GetValue());
                }
                return;
            }
            if (mUiEnabled && mPendantTypeUri != info.m_uri) {
                switch (((RecommendAdapter.ItemInfo) info).m_style) {
                    case NEW:
                    case NORMAL: {
                        mPendantTypeUri =  info.m_uri;
                        mPendantDownloadingId = mPendantTypeUri;
                        if (mPendantTypeUri == 0) {
                            mView.DelGlassItem(PendantViewEx.TYPE_PENDANT);
                            mPendantTypeBtn.setCircleIcon(null);
                            hideReverseIconAnim();
                            if (mPendantTypeUri <= 0 && mShapeTypeUri <= 0) {
                                hideColorIconAnim();
                            }
                        } else {
                            mPendantSelectedIndex = -1;
                            hideReverseIconAnim();

                            if (colorPendantChangeLayout != null) {
                                colorPendantChangeLayout.setSelectedPosition(0);
                            }
                            mPendantDefWhiteColor = ((GlassRes) ((RecommendAdapter.ItemInfo) info).m_ex).m_color;
                            mPendantColor = mPendantDefWhiteColor;
                            mView.SetGlassColor(mShapeColor, mPendantColor);

                            int selectIndex = mView.myAddPendant((GlassRes) ((RecommendAdapter.ItemInfo) info).m_ex);
                            mView.SetSelPendant(selectIndex);
                            m_controlCallback.selectPendant(selectIndex);

                            Bitmap tempBmp = cn.poco.imagecore.Utils.DecodeImage(getContext(), ((GlassRes) ((RecommendAdapter.ItemInfo) info).m_ex).m_icon, 0, -1, -1, -1);
                            if (tempBmp != null) {
                                mPendantTypeBtn.setCircleIcon(MakeBmp.CreateFixBitmap(tempBmp, ShareData.PxToDpi_xhdpi(34), ShareData.PxToDpi_xhdpi(34), MakeBmp.POS_CENTER, 0, Bitmap.Config.ARGB_8888));
                                recycleBitmap(tempBmp, true);
                            }
                        }
                        break;
                    }

                    case NEED_DOWNLOAD: {
                        RecommendAdapter.ItemInfo itemInfo = (RecommendAdapter.ItemInfo) info;
                        if (itemInfo.m_isLock && TagMgr.CheckTag(getContext(), Tags.GLASS_UNLOCK_ID_FLAG + itemInfo.m_uri)) {
                            ArrayList<LockRes> lockArr = LockResMgr2.getInstance().getGlassLockArr();
                            if(lockArr != null)
                            {
                                for (LockRes lockRes : lockArr) {
                                    if (lockRes.m_id == itemInfo.m_uri && lockRes.m_shareType != LockRes.SHARE_TYPE_NONE && TagMgr.CheckTag(getContext(), Tags.GLASS_UNLOCK_ID_FLAG + lockRes.m_id)) {
                                        if (m_recomView != null) {
                                            showRecommendView(m_recomView, lockRes, 0);
                                        }
                                        break;
                                    }
                                }
                            }
                        } else {
                            mPendantDownloadingId = itemInfo.m_uri;
                            DownloadMgr.getInstance().DownloadRes((GlassRes) itemInfo.m_ex, new DownloadMgr.Callback() {

                                @Override
                                public void OnProgress(int downloadId, IDownload res, int progress) {

                                }

                                @Override
                                public void OnFail(int downloadId, IDownload res) {
                                    Toast.makeText(getContext(), getResources().getString(R.string.material_download_failed), Toast.LENGTH_SHORT).show();
                                    if (mPendantAdapter != null) {
                                        mPendantAdapter.SetItemStyleByUri(((BaseRes) res).m_id, RecommendAdapter.ItemInfo.Style.NEED_DOWNLOAD);
                                    }
                                }

                                @Override
                                public void OnComplete(int downloadId, IDownload res) {
                                    if (mPendantAdapter != null) {
                                        mPendantAdapter.SetItemStyleByUri(((BaseRes) res).m_id, RecommendAdapter.ItemInfo.Style.NEW);
                                        mPendantAdapter.SetSelectByUri(((BaseRes) res).m_id);
                                    }
                                }
                            });
                            mPendantAdapter.SetItemStyleByUri(((RecommendAdapter.ItemInfo) info).m_uri, RecommendAdapter.ItemInfo.Style.LOADING);
                        }
                        break;
                    }
                    default:
                        break;
                }
            }
        }
    };

    private int mShapeDownloadingId = -1;
    private RecommendAdapter.OnItemClickListener mShapeListCallback = new AbsAdapter.OnItemClickListener() {
        @Override
        public void OnItemDown(AbsAdapter.ItemInfo info, int index) {

        }

        @Override
        public void OnItemUp(AbsAdapter.ItemInfo info, int index) {

        }

        @Override
        public void OnItemClick(AbsAdapter.ItemInfo info, int index) {
            if (mUiEnabled && info.m_uri == RecommendAdapter.DownloadItemInfo.DOWNLOAD_ITEM_URI) {
                TongJi2.AddCountByRes(getContext(), R.integer.修图_素材美化_毛玻璃_下载更多);//形状
                openDownloadMorePage(ResType.GLASS);
                return;
            }
            if (mUiEnabled && info.m_uri == RecommendAdapter.RecommendItemInfo.RECOMMEND_ITEM_URI) {
                //推荐位
                ArrayList<RecommendRes> ress = (ArrayList<RecommendRes>) (((RecommendAdapter.ItemInfo) info).m_ex);
                RecommendRes recommendRes = null;
                if (ress != null && ress.size() > 0) {
                    recommendRes = ress.get(0);
                }
                if (recommendRes != null && m_recomView != null) {
                    showRecommendView(m_recomView, recommendRes, ResType.GLASS.GetValue());
                }
                return;
            }
            if (mUiEnabled && mShapeTypeUri != info.m_uri) {
                switch (((RecommendAdapter.ItemInfo) info).m_style) {
                    case NEW:
                    case NORMAL: {
                        mShapeTypeUri = info.m_uri;
                        mShapeDownloadingId = mShapeTypeUri;
                        if (mShapeTypeUri == 0) {
                            mView.DelGlassItem(PendantViewEx.TYPE_SHAPE);
                            mShapeTypeBtn.setCircleIcon(null);
                        } else {
                            mPendantSelectedIndex = -1;

                            if (colorShapeChangeLayout != null) {
                                colorShapeChangeLayout.setSelectedPosition(0);
                            }
                            mShapeDefWhiteColor = ((GlassRes) ((RecommendAdapter.ItemInfo) info).m_ex).m_color;
                            mShapeColor = mShapeDefWhiteColor;
                            mView.SetGlassColor(mShapeColor, mPendantColor);

                            int selectIndex = mView.myAddPendant((GlassRes) ((RecommendAdapter.ItemInfo) info).m_ex);

                            isReverseMode = false;
                            mView.SetSelPendant(selectIndex);
                            m_controlCallback.selectPendant(selectIndex);
                            mView.setReverseMode(isReverseMode);

                            Bitmap tempBmp = cn.poco.imagecore.Utils.DecodeImage(getContext(), ((GlassRes) ((RecommendAdapter.ItemInfo) info).m_ex).m_icon, 0, -1, -1, -1);
                            if (tempBmp != null) {
                                mShapeTypeBtn.setCircleIcon(MakeBmp.CreateFixBitmap(tempBmp, ShareData.PxToDpi_xhdpi(34), ShareData.PxToDpi_xhdpi(34), MakeBmp.POS_CENTER, 0, Bitmap.Config.ARGB_8888));
                                recycleBitmap(tempBmp, true);
                            }
                        }
                        break;
                    }

                    case NEED_DOWNLOAD: {
                        RecommendAdapter.ItemInfo itemInfo = (RecommendAdapter.ItemInfo) info;
                        if (itemInfo.m_isLock && TagMgr.CheckTag(getContext(), Tags.GLASS_UNLOCK_ID_FLAG + itemInfo.m_uri)) {
                            ArrayList<LockRes> lockArr = LockResMgr2.getInstance().getGlassLockArr();
                            if(lockArr != null)
                            {
                                for (LockRes lockRes : lockArr) {
                                    if (lockRes.m_id == ((RecommendAdapter.ItemInfo) info).m_uri && lockRes.m_shareType != LockRes.SHARE_TYPE_NONE && TagMgr.CheckTag(getContext(), Tags.GLASS_UNLOCK_ID_FLAG + lockRes.m_id)) {
                                        showRecommendView(m_recomView, lockRes, 0);
                                        break;
                                    }
                                }
                            }
                        } else {
                            mShapeDownloadingId = ((RecommendAdapter.ItemInfo) info).m_uri;
                            DownloadMgr.getInstance().DownloadRes((GlassRes) ((RecommendAdapter.ItemInfo) info).m_ex, new DownloadMgr.Callback() {

                                @Override
                                public void OnProgress(int downloadId, IDownload res, int progress) {

                                }

                                @Override
                                public void OnFail(int downloadId, IDownload res) {
                                    Toast.makeText(getContext(), getResources().getString(R.string.material_download_failed), Toast.LENGTH_SHORT).show();
                                    if (mShapeAdapter != null) {
                                        mShapeAdapter.SetItemStyleByUri(((BaseRes) res).m_id, RecommendAdapter.ItemInfo.Style.NEED_DOWNLOAD);

                                    }
                                }

                                @Override
                                public void OnComplete(int downloadId, IDownload res) {
                                    if (mShapeAdapter != null) {
                                        mShapeAdapter.SetItemStyleByUri(((BaseRes) res).m_id, RecommendAdapter.ItemInfo.Style.NEW);
                                        if (mShapeDownloadingId == ((BaseRes) res).m_id) {
                                            mShapeAdapter.SetSelectByUri(((BaseRes) res).m_id);
                                        }
                                    }
                                }
                            });
                            mShapeAdapter.SetItemStyleByUri(((RecommendAdapter.ItemInfo) info).m_uri, RecommendAdapter.ItemInfo.Style.LOADING);
                        }
                        break;
                    }
                    default:
                        break;
                }
            }
        }
    };

    private RecomDisplayMgr.Callback m_unlockListener = new RecomDisplayMgr.Callback() {
        @Override
        public void UnlockSuccess(BaseRes res) {
            if (res != null) {
                GlassRes tempGlassRes = null;
                for (GlassRes glass : m_allResArr) {
                    if (glass.m_id == res.m_id) {
                        tempGlassRes = glass;
                        break;
                    }
                }
                if (tempGlassRes != null) {
                    if (tempGlassRes.m_glassType == GlassRes.GLASS_TYPE_PENDANT) {
                        int index = mPendantAdapter.GetIndex(res.m_id);
                        if (index >=0 ) {
                            mPendantAdapter.Unlock(res.m_id);
                            TagMgr.SetTag(getContext(), Tags.THEME_UNLOCK + res.m_id);
                            mPendantAdapter.SetSelectByIndex(index);
                        }
                    } else if (tempGlassRes.m_glassType == GlassRes.GLASS_TYPE_SHAPE) {
                        int index = mShapeAdapter.GetIndex(res.m_id);
                        if (index >= 0) {
                            mPendantAdapter.Unlock(res.m_id);
                            TagMgr.SetTag(getContext(), Tags.THEME_UNLOCK + res.m_id);
                            mShapeAdapter.SetSelectByIndex(index);
                        }
                    }
                }
            }
        }

        @Override
        public void OnClose() {
        }

        @Override
        public void OnLogin() {
            if (m_site != null) {
                m_site.OnLogin(getContext());
            }
        }

        @Override
        public void OnBtn(int state) {
        }

        @Override
        public void OnCloseBtn() {
        }
    };

    public static ArrayList<ColorItemInfo> getColorPaletteRess() {
        int[][] ress = {
                {0xFFFFFFFF, 0xFFFFFFFF},
                {0x7AFFF9F4, 0xFFFFF9F4},
                {0x57FFFDD4, 0xFFFFFDD4},
                {0x5CFFF3FF, 0xFFFFF3FF},
                {0x4DFFE2E2, 0xFFFFE2E2},
                {0x3DFFEBCB, 0xFFFFEBCB},
                {0x4DE0FFD4, 0xFFE0FFD4},
                {0x4DE8FEFF, 0xFFE8FEFF},
                {0x5CFBF3BA, 0xFFFBF3BA},
                {0x47FFDABF, 0xFFFFDABF},
                {0x3DFEC1D8, 0xFFFEC1D8},
                {0x57ABC5A8, 0xFFABC5A8},
                {0x52ADD8EA, 0xFFADD8EA},
                {0x3DDEE6FF, 0xFFDEE6FF},
                {0x57F2B7AB, 0xFFF2B7AB},
                {0x5CF3C4F2, 0xFFF3C4F2},
                {0x7A000000, 0xFF000000}};
        ArrayList<ColorItemInfo> out = new ArrayList<>();
        ColorItemInfo item;

        int len = ress.length;
        for (int i = 0; i < len; i++) {
            item = new ColorItemInfo();
            item.m_id = i;
            item.m_position = i;
            item.m_color = ress[i][0];
            item.m_pre_Color = ress[i][1];
            out.add(item);
        }

        return out;
    }

    private void initPendantColorPalette() {
        if (colorPendantChangeLayout == null) {
            colorPendantChangeLayout = new ColorChangeLayoutV2(getContext());
            colorPendantChangeLayout.SetData(getColorPaletteRess());
            colorPendantChangeLayout.setmItemOnClickListener(colorPendantItemOnClickListener);
            LayoutParams fl = new LayoutParams(LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(320));
            fl.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
            this.addView(colorPendantChangeLayout, fl);
        }
    }

    private void initShapeColorPalette() {
        if (colorShapeChangeLayout == null) {
            colorShapeChangeLayout = new ColorChangeLayoutV2(getContext());
            colorShapeChangeLayout.SetData(getColorPaletteRess());
            colorShapeChangeLayout.setmItemOnClickListener(colorShapeOnItemClickListener);
            LayoutParams fl = new LayoutParams(LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(320));
            fl.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
            this.addView(colorShapeChangeLayout, fl);
        }
    }

    public void showPendantColorPalette(boolean show) {
        initPendantColorPalette();

        if (mUiEnabled) {
            isColorMode = show;
            showWidgetAnim(colorPendantChangeLayout, show);
            if (show) {
                colorPendantChangeLayout.setSelectedColor(mPendantColor);
            }
        }
    }

    public void showShapeColorPalette(boolean show) {
        initShapeColorPalette();

        if (mUiEnabled) {
            isColorMode = show;
            showWidgetAnim(colorShapeChangeLayout, show);
        }
    }

    ColorChangeLayoutV2.OnColorItemClickListener colorPendantItemOnClickListener = new ColorChangeLayoutV2.OnColorItemClickListener() {
        @Override
        public void onDownClick() {
            if (mUiEnabled) {
                isColorMode = false;
                mBottomFr.setVisibility(View.VISIBLE);
                if (colorPendantChangeLayout != null && colorPendantChangeLayout.getVisibility() == VISIBLE) {
                    showWidgetAnim(colorPendantChangeLayout, false);
                }
                if (colorShapeChangeLayout != null && colorShapeChangeLayout.getVisibility() == VISIBLE) {
                    showWidgetAnim(colorShapeChangeLayout, false);
                }
            }
            /*if (!isFold && isPendantType) {
                mPendantTypeBtn.setBtnStatus(isPendantType, isFold);
                mShapeTypeBtn.setBtnStatus(!isPendantType, isFold);

                LayoutParams fl_lp;
                fl_lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                fl_lp.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
                fl_lp.setMargins(0, 0, 0, -mColorChooseBarHeight);
                mBottomFr.setLayoutParams(fl_lp);
                isFold = true;
                mBottomFr.setVisibility(View.VISIBLE);
                showWidgetAnim(colorPendantChangeLayout, false);
                colorPendantChangeLayout.setVisibility(View.GONE);
            } else if (!isFold && !isPendantType) {
                mPendantTypeBtn.setBtnStatus(isPendantType, isFold);
                mShapeTypeBtn.setBtnStatus(!isPendantType, isFold);
                LayoutParams fl_lp;

                fl_lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                fl_lp.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
                fl_lp.setMargins(0, 0, 0, -mColorChooseBarHeight);
                mBottomFr.setLayoutParams(fl_lp);
                isFold = true;
                mBottomFr.setVisibility(View.VISIBLE);
                showWidgetAnim(colorPendantChangeLayout, false);
                colorPendantChangeLayout.setVisibility(View.GONE);
            } else if (isFold) {
                mBottomFr.setVisibility(View.VISIBLE);
                showWidgetAnim(colorPendantChangeLayout, false);
                colorPendantChangeLayout.setVisibility(View.GONE);
            }*/
        }

        @Override
        public void onColorItemClick(int position, Object result) {
            mPendantColor = ((ColorItemInfo) result).m_color;
//            if (mPendantColor == 0xFFFFFFFF) {
//                mPendantColor = mPendantDefWhiteColor;
//            }
            mView.SetGlassColor(mShapeColor, mPendantColor);
            if (colorPendantChangeLayout != null) {
                colorPendantChangeLayout.setSelectedPosition(((ColorItemInfo) result).m_position);
            }
        }
    };


    ColorChangeLayoutV2.OnColorItemClickListener colorShapeOnItemClickListener = new ColorChangeLayoutV2.OnColorItemClickListener() {
        @Override
        public void onDownClick() {
            if (mUiEnabled) {
                isColorMode = false;
                mBottomFr.setVisibility(View.VISIBLE);
                if (colorPendantChangeLayout != null && colorPendantChangeLayout.getVisibility() == VISIBLE) {
                    showWidgetAnim(colorPendantChangeLayout, false);
                }
                if (colorShapeChangeLayout != null && colorShapeChangeLayout.getVisibility() == VISIBLE) {
                    showWidgetAnim(colorShapeChangeLayout, false);
                }
            }
        }

        @Override
        public void onColorItemClick(int position, Object result) {
            mShapeColor = ((ColorItemInfo) result).m_color;
            if (mShapeColor == 0xFFFFFFFF) {
                mShapeColor = mShapeDefWhiteColor;
            }
            mView.SetGlassColor(mShapeColor, mPendantColor);
            if (colorShapeChangeLayout != null) {
                colorShapeChangeLayout.setSelectedPosition(((ColorItemInfo) result).m_position);
            }
        }
    };

    public void SetImg(Object params) {
        if (params != null) {
            mImgs = params;
            if (params instanceof RotationImg2[]) {
                mOrgBmp = ImageUtils.MakeBmp(getContext(), params, DEF_IMG_SIZE, DEF_IMG_SIZE);
            } else if (params instanceof Bitmap) {
                mOrgBmp = (Bitmap) params;
            }

            if (mOrgBmp != null) {
                mShowBmp = cutOrgBmp(mOrgBmp);
                setImageBmp(mShowBmp);
            }

            if (mPendantTypeUri != 0) {
                postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        isShapeNewFlag = false;
                        isPendantNewFlag = false;
                        SetSelUri(mPendantTypeUri);
                    }
                }, 50);
            }
        }
    }

    private Bitmap cutOrgBmp(Bitmap src) {
        if (src == null) return null;
        int screenMax = Math.max(ShareData.m_screenWidth, ShareData.m_screenHeight);
        int srcMax = Math.max(src.getWidth(), src.getHeight());

        if (screenMax >= 1920) {
            if (srcMax > 1600) {
                srcMax = 1600;
            } else {
                return src;
            }
        } else if (screenMax >= 1280) {
            if (srcMax > 1024) {
                srcMax = 1024;
            } else {
                return src;
            }
        } else {
            if (srcMax > 1024) {
                srcMax = 1024;
            } else {
                return src;
            }
        }

//        int outW = 0, outH = 0;
//        if (srcMax == 1024) {
//            if (src.getWidth() > src.getHeight()) {
//                outW = 1024;
//                outH = (int) ((float) outW / (float) src.getWidth() * (float) src.getHeight());
//            } else {
//                outH = 1024;
//                outW = (int) ((float) outH / (float) src.getHeight() * (float) src.getWidth());
//            }
//        } else if (srcMax == 1600) {
//            if (src.getWidth() > src.getHeight()) {
//                outW = 1600;
//                outH = (int) ((float) outW / (float) src.getWidth() * (float) src.getHeight());
//            } else {
//                outH = 1600;
//                outW = (int) ((float) outH / (float) src.getHeight() * (float) src.getWidth());
//            }
//        } else {
//            outW = src.getWidth();
//            outH = src.getHeight();
//        }
//        Log.d("bbb", "cutOrgBmp: srcMax:" + srcMax + ", screenMax:" + screenMax + ", outW:" + outW + ", outH:" + outH);
        Bitmap temp = MakeBmpV2.CreateBitmapV2(src, 0, MakeBmpV2.FLIP_NONE, -1, srcMax, srcMax, Bitmap.Config.ARGB_8888);
        return temp;
    }

    private void setImageBmp(Bitmap orgBmp) {
        mView.setImage(orgBmp);

        ShowStartAnim();
    }

    private void ShowStartAnim() {
        if (m_viewH > 0 && m_imgH > 0) {
            int tempStartY = (int) (ShareData.PxToDpi_xhdpi(90) + (m_viewH - m_viewHeight) / 2f);
            float scaleX = (m_viewWidth) * 1f / (float) mShowBmp.getWidth();
            float scaleY = (m_viewHeight) * 1f / (float) mShowBmp.getHeight();
            m_currImgH = mShowBmp.getHeight() * Math.min(scaleX, scaleY);
            float scaleH = m_imgH / m_currImgH;
            ShowViewAnim(mView, tempStartY, 0, scaleH, 1f, SHOW_VIEW_ANIM);
        }
    }

    private void ShowViewAnim(final View view, int startY, int endY, float startScale, float endScale, int duration) {
        if (view != null) {
            AnimatorSet animatorSet = new AnimatorSet();
            ObjectAnimator object1 = ObjectAnimator.ofFloat(view, "scaleX", startScale, endScale);
            ObjectAnimator object2 = ObjectAnimator.ofFloat(view, "scaleY", startScale, endScale);
            ObjectAnimator object3 = ObjectAnimator.ofFloat(view, "translationY", startY, endY);
            ObjectAnimator object4 = ObjectAnimator.ofFloat(mBottomFr, "translationY", mBottomBarHeight + mBottomLayoutHeight, 0);
            animatorSet.setDuration(duration);
            animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
            animatorSet.playTogether(object1, object2, object3, object4);
            animatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mUiEnabled = true;
                    view.clearAnimation();
                    mBottomFr.clearAnimation();
                }
            });
            animatorSet.start();
        }
    }

    public void SetSelUri(int uri) {
        if (mPendantAdapter != null) {
            int index = mPendantAdapter.GetIndex(uri);
            RecommendAdapter.ItemInfo tempInfo = (RecommendAdapter.ItemInfo) mPendantAdapter.GetItemInfoByIndex(index);
            if(index != -1 && tempInfo != null){
                mPendantTypeUri = 0;
                mPendantAdapter.SetSelectByUri(uri);
                isPendantType = false;
                mOnClickListener.onClick(mPendantTypeBtn);

                if (isShapeNewFlag && mShapeTypeBtn != null) {
                    mShapeTypeBtn.setNewStatus(true);
                }

                isShapeNewFlag = false;
                isPendantNewFlag = false;
                return;
            }
        }
        if (mShapeAdapter != null) {

            int index = mShapeAdapter.GetIndex(uri);
            RecommendAdapter.ItemInfo tempInfo = (RecommendAdapter.ItemInfo) mShapeAdapter.GetItemInfoByIndex(index);
            if(index != -1 && tempInfo != null){
                mShapeTypeUri = 0;
                mShapeAdapter.SetSelectByUri(uri);
                isPendantType = true;
                mOnClickListener.onClick(mShapeTypeBtn);

                if (isPendantNewFlag && mPendantTypeBtn != null) { //显示另外一个为new
                    mPendantTypeBtn.setNewStatus(true);
                }

                isShapeNewFlag = false;
                isPendantNewFlag = false;
            }
        }
    }

    private FilterPendantViewEx.ControlCallback m_controlCallback = new FilterPendantViewEx.ControlCallback() {
        @Override
        public void selectPendant(int index) {
            mPendantSelectedIndex = index;
            if (mPendantSelectedIndex >= 0) {
                FilterPendantViewEx.BlurShapeEx info = ((FilterPendantViewEx.BlurShapeEx) mView.getPendantArrByIndex(mPendantSelectedIndex));
                showColorIconAnim();
                if (info != null && info.m_type == FilterPendantViewEx.TYPE_SHAPE) {
                    showReverseIconAnim();
                } else {
                    hideReverseIconAnim();
                }
            } else {
                if (mColorIcon != null) {
                    hideColorIconAnim();
                }
                if (mReverseIcon != null) {
                    hideReverseIconAnim();
                }
            }
        }

        @Override
        public void fingerDown() {
            if ((colorPendantChangeLayout != null && colorPendantChangeLayout.getVisibility() == View.VISIBLE) ||
                    (colorShapeChangeLayout != null && colorShapeChangeLayout.getVisibility() == View.VISIBLE)) {
                if (mPendantSelectedIndex >= 0) {
                    showColorIconAnim();
                    if (colorShapeChangeLayout != null && colorShapeChangeLayout.getVisibility() == VISIBLE) {
                        showWidgetAnim(colorShapeChangeLayout, false);
                    } else if (colorPendantChangeLayout != null && colorPendantChangeLayout.getVisibility() == VISIBLE) {
                        showWidgetAnim(colorPendantChangeLayout, false);
                    }
                    isColorMode = false;
                } else {
                    if (colorPendantChangeLayout != null && colorPendantChangeLayout.getVisibility() == View.VISIBLE) {
                        showWidgetAnim(colorPendantChangeLayout, false);
                    } else if (colorShapeChangeLayout != null && colorShapeChangeLayout.getVisibility() == VISIBLE) {
                        showWidgetAnim(colorShapeChangeLayout, false);
                    }
                    isColorMode = false;
                }
            }
        }

        @Override
        public void deletePendantType(int type, int index) {
            switch (type) {
                case FilterPendantViewEx.TYPE_PENDANT: {
                    mPendantTypeUri = 0;
                    mPendantAdapter.CancelSelect();
                    mPendantTypeBtn.setCircleIcon(null);

                    mPendantSelectedIndex = index;
                    if (mPendantSelectedIndex >= 0) {
                        FilterPendantViewEx.BlurShapeEx info = ((FilterPendantViewEx.BlurShapeEx) mView.getPendantArrByIndex(mPendantSelectedIndex));
                        showColorIconAnim();
                        if (info != null && info.m_type == FilterPendantViewEx.TYPE_SHAPE) {
                            showReverseIconAnim();
                        } else {
                            hideReverseIconAnim();
                        }
                    } else {
                        hideColorIconAnim();
                        hideReverseIconAnim();
                    }

                    if (mPendantTypeUri <= 0 && mShapeTypeUri <= 0) {
                        hideColorIconAnim();
                    }
                    if (colorPendantChangeLayout != null) {
                        mBottomFr.setVisibility(View.VISIBLE);
                        colorPendantChangeLayout.setVisibility(GONE);
                        if (isColorMode) {
                            showWidgetAnim(colorPendantChangeLayout, false);
                            isColorMode = false;
                        }
                    }
                    break;
                }
                case FilterPendantViewEx.TYPE_SHAPE: {
                    mShapeTypeUri = 0;
                    mShapeAdapter.CancelSelect();
                    mShapeTypeBtn.setCircleIcon(null);

                    mPendantSelectedIndex = index;
                    if (mPendantSelectedIndex >= 0) {
                        FilterPendantViewEx.BlurShapeEx info = (FilterPendantViewEx.BlurShapeEx) mView.getPendantArrByIndex(mPendantSelectedIndex);
                        showColorIconAnim();
                        if (info != null && info.m_type == FilterPendantViewEx.TYPE_SHAPE) {
                            showReverseIconAnim();
                        } else {
                            hideReverseIconAnim();
                        }
                    } else {
                        hideColorIconAnim();
                        hideReverseIconAnim();
                    }

                    if (mPendantTypeUri <= 0 && mShapeTypeUri <= 0) {
                        hideColorIconAnim();
                    }
                    if (colorShapeChangeLayout != null) {
                        mBottomFr.setVisibility(View.VISIBLE);
                        colorShapeChangeLayout.setVisibility(GONE);
                        if (isColorMode) {
                            showWidgetAnim(colorShapeChangeLayout, false);
                            isColorMode = false;
                        }
                    }
                    break;
                }
            }
        }
    };

    private int mPendantSelectedIndex = -1;

    private void removeShowView() {
        if (mView != null) {
            removeView(mView);
            mView = null;
        }
    }

    public void releaseMem() {
        if (mView != null) {
            this.removeView(mView);
            mView.ReleaseMem();
            mView = null;
        }
        this.setBackgroundDrawable(null);
        if (mPendantTypeBtn != null) {
            mPendantTypeBtn.releaseMem();
        }
        if (mShapeTypeBtn != null) {
            mShapeTypeBtn.releaseMem();
        }

        if (DownloadMgr.getInstance() != null) {
            DownloadMgr.getInstance().RemoveDownloadListener(m_downloadLst);
        }
    }

    /**
     * @param params imgs : RotationImg[] / Bitmap
     */
    @Override
    public void SetData(HashMap<String, Object> params) {
        if (params != null) {
            Object o;
            o = params.get(DataKey.BEAUTIFY_DEF_SEL_URI);
            if (o != null && o instanceof Integer) {
                mPendantTypeUri = (int) params.get(DataKey.BEAUTIFY_DEF_SEL_URI);
            }

            o = params.get(Beautify4Page.PAGE_ANIM_IMG_H);
            if (o != null && o instanceof Integer) {
                m_imgH = (int) o;
            }

            o = params.get(Beautify4Page.PAGE_ANIM_VIEW_H);
            if (o != null && o instanceof Integer) {
                m_viewH = (int) o;
            }

            o = params.get(Beautify4Page.PAGE_ANIM_VIEW_TOP_MARGIN);
            if (o != null && o instanceof Integer) {
                m_viewTopMargin = (int) o;
            }

            if (params.get("imgs") != null) {
                SetImg(params.get("imgs"));
            }
        }
    }

    @Override
    public void onBack() {
        if (mOnAnimationClickListener != null) {
            mOnAnimationClickListener.onAnimationClick(mCancelBtn);
        }
    }

    @Override
    public void onClose() {
        clearExitDialog();
        if (m_recomView != null) {
            m_recomView.ClearAllaa();
            m_recomView = null;
        }
        if (mPendantAdapter != null) {
            mPendantAdapter.ClearAll();
            mPendantAdapter = null;
        }
        if (mShapeAdapter != null) {
            mShapeAdapter.ClearAll();
            mShapeAdapter = null;
        }
        releaseMem();
        this.removeAllViews();

        MyBeautyStat.onPageEndByRes(R.string.美颜美图_毛玻璃页面_主页面);
        TongJiUtils.onPageEnd(getContext(), R.string.毛玻璃);
        isShapeNewFlag = false;
        isPendantNewFlag = false;

        super.onClose();
    }

    @Override
    public void onPause() {
        TongJiUtils.onPagePause(getContext(), R.string.毛玻璃);
        super.onPause();
    }

    @Override
    public void onResume() {
        TongJiUtils.onPageResume(getContext(), R.string.毛玻璃);
        super.onResume();
    }

    @Override
    public void onPageResult(int siteID, HashMap<String, Object> params) {
        if (siteID == SiteID.DOWNLOAD_MORE || siteID == SiteID.THEME_INTRO) {
            if (params != null) {
                ResType type = (ResType) params.get(DownloadMorePageSite.DOWNLOAD_MORE_TYPE);
                boolean del = false;
                int uri = -1;
                {
                    Object obj = params.get(DownloadMorePageSite.DOWNLOAD_MORE_DEL);
                    if (obj instanceof Boolean) {
                        del = (Boolean) obj;
                    }
                }

                if (params.get(DataKey.BEAUTIFY_DEF_SEL_URI) != null) {
                    uri = (int) params.get(DataKey.BEAUTIFY_DEF_SEL_URI);
                }

                switch (type) {
                    case GLASS:
                        if (del && uri <= 0) {
                            //删除
                            mPendantListInfos = FilterPendantResMgr.getRess(getContext(),GlassRes.GLASS_TYPE_PENDANT);
                            mPendantAdapter.SetData(mPendantListInfos);
                            mPendantAdapter.notifyDataSetChanged();
                            mPendantAdapter.CancelSelect();
                            mShapeListInfos = FilterPendantResMgr.getRess(getContext(),GlassRes.GLASS_TYPE_SHAPE);
                            mShapeAdapter.SetData(mShapeListInfos);
                            mShapeAdapter.notifyDataSetChanged();
                            mShapeAdapter.CancelSelect();
                            isShapeNewFlag = false;
                            isPendantNewFlag = false;
                        } else {
                            //使用
                            final int finalUri = uri;
                            postDelayed(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    SetSelUri(finalUri);
                                }
                            }, 50);
                        }
                        break;
                    default:
                        break;
                }
            }
        }
        if (siteID == SiteID.LOGIN || siteID == SiteID.REGISTER_DETAIL || siteID == SiteID.RESETPSW) {
            if (m_recomView != null) {
                m_recomView.UpdateCredit();
            }
        }

        super.onPageResult(siteID, params);
    }

    private void recycleBitmap(Bitmap bmp, boolean recycle) {
        if (bmp != null && !bmp.isRecycled() && recycle) {
            bmp.recycle();
            bmp = null;
        }
    }

    private void openDownloadMorePage(ResType resType)
    {
        MyBeautyStat.onClickByRes(R.string.美颜美图_毛玻璃页面_主页面_下载更多);
        if (m_site != null)
        {
            m_site.OpenDownloadMore(getContext(), resType);
        }
    }

    private void showRecommendView(RecomDisplayMgr recomDisplayMgr, BaseRes baseRes, int resType) {
        //推荐位统计
        MyBeautyStat.onClickByRes(R.string.美颜美图_毛玻璃页面_主页面_毛玻璃推荐位);
        TongJi2.AddCountByRes(getContext(), R.integer.修图_素材美化_毛玻璃_推荐位);
        if (recomDisplayMgr != null) {
            recomDisplayMgr.SetBk(CommonUtils.GetScreenBmp((Activity) getContext(), ShareData.m_screenWidth / 8, ShareData.m_screenHeight / 8), true);
            recomDisplayMgr.SetDatas(baseRes, resType);
            recomDisplayMgr.Show();
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
                    cancel();
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
