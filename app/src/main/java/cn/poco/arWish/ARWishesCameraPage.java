package cn.poco.arWish;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.ColorUtils;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.NumberKeyListener;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import cn.easyar.CameraDeviceFocusMode;
import cn.easyar.CameraDeviceType;
import cn.easyar.Engine;
import cn.easyar.StorageType;
import cn.poco.advanced.ImageUtils;
import cn.poco.arWish.site.ARWishesCameraPageSite;
import cn.poco.arsdk.EasyARNative;
import cn.poco.arsdk.GLView;
import cn.poco.arsdk.TargetsEffectData;
import cn.poco.camera.CameraAllCallback;
import cn.poco.camera.CameraConfig;
import cn.poco.camera2.CameraCallback;
import cn.poco.camera2.CameraErrorTipsDialog;
import cn.poco.camera2.CameraHandler;
import cn.poco.camera2.CameraThread;
import cn.poco.camera3.ui.RatioBgViewV2;
import cn.poco.camera3.ui.drawable.RoundRectDrawable;
import cn.poco.camera3.util.CameraPercentUtil;
import cn.poco.camera3.util.LanguageUtil;
import cn.poco.exception.ExceptionData;
import cn.poco.framework.BaseSite;
import cn.poco.framework.FileCacheMgr;
import cn.poco.framework.IPage;
import cn.poco.framework.SiteID;
import cn.poco.gldraw2.CameraRenderView;
import cn.poco.gldraw2.OnCaptureFrameListener;
import cn.poco.gldraw2.RenderHelper;
import cn.poco.gldraw2.RenderRunnable;
import cn.poco.gldraw2.RenderThread;
import cn.poco.resource.ArRes;
import cn.poco.statistics.MyBeautyStat;
import cn.poco.tianutils.CommonUtils;
import cn.poco.tianutils.KeyboardMgr;
import cn.poco.tianutils.ShareData;
import cn.poco.utils.OnAnimationClickListener;
import cn.poco.utils.PermissionHelper;
import cn.poco.utils.Utils;
import cn.poco.utils.WaitAnimDialog;
import cn.poco.video.encoder.RecordState;
import my.beautyCamera.R;

/**
 * Created by zwq on 2018/01/22 13:46.<br/><br/>
 */

public class ARWishesCameraPage extends IPage implements CameraCallback, CameraAllCallback,
        OnCaptureFrameListener, GLView.DetectCallback, View.OnClickListener
{

    private final boolean mIsChinese;
    private ARWishesCameraPageSite mSite;
    private boolean mIsInitPage;

    private static final int MSG_CAMERA_ERROR = 0x1;
    private static final int MSG_CANCEL_MASK = 0x2;
    private static final int MSG_SHOW_INPUT = 0x3;

    private Handler mUIHandler;
    private RatioBgViewV2.OnRatioChangeListener mRatioBgListener;

    private String mModel;
    private float mFullScreenRatio;
    private float mCameraViewRatio;
    private int mCameraViewWidth;
    private int mCameraViewHeight;
    private int mCameraSizeType;
    private float mCurrentRatio;
    private int mCurrentCameraId;
    private boolean isFront;
    private boolean mIsSwitchCamera;

    private boolean mIsHideWish;

    private FrameLayout mCameraLayout;
    private CameraRenderView mPreviewView;
    private RatioBgViewV2 mRatioBgView;
    private TextView mTitleView;
    private ImageView mBackView;
    private ImageView mFlashView;
    private RelativeLayout mScanTipsTextLayout;

    // password
    private FrameLayout mPasswordBG;
    private RelativeLayout mPasswordLayout;
    private EditText mPasswordET;
    private TextView mPasswordCancel;
    private TextView mPasswordConfirm;
    private boolean mIsLoadingPassword;
    private TextWatcher mTextWatcher;
    private TextView.OnEditorActionListener mOnEdActionListener;

    private boolean mUIEnable = true;
    private TextView mAimedTv;
    private TextView mPasswordErrorTips;
    private ImageView mPasswordErrorIcon;

    private ImageView mScanView;
    private ARWishTipsView mARTipsView;

    private WaitAnimDialog.WaitAnimView mWaitDialog;

    private boolean mDoCameraOpenAnim;

    private int mSystemUiVisibility;
    private int mOriginVisibility;

    private boolean mIsKeepScreenOn;
    private String mCurrentFlashMode;

    private CameraErrorTipsDialog mCameraErrorTipsDialog;
    private ARWishTipsView.StatusListener mFindWishTipsStatusListener;
    private OnAnimationClickListener mAnimClickListener;

    private int mPageType;//0:藏祝福，1:找祝福
    private int mPicWidth;
    private int mPicCropTopHeight;//照片的顶部裁剪高度
    private int mPicCropLeft;
    private boolean mIsTackingPic;

    private GLView mArGLView;
    private boolean mFlashON;

    private TargetsEffectData.ImageTargets mImageTargets;  //识别图像目标
    private boolean mDealResult;

    private KeyboardMgr keyboardMgr;

    // AR 作品信息
    private String mVideoPath;
    private String mImgPath;
    private String mShowID;
    private String mPassword;
    private ARWorksMgr mArWorkMgr;
    private ARWorksMgr.CallBack mArWorkListener;
    private boolean mIsShowPasswordErrorTips;

    public ARWishesCameraPage(Context context, BaseSite site) {
        super(context, site);
        mSite = (ARWishesCameraPageSite) site;
        mIsInitPage = true;

        mArWorkMgr = new ARWorksMgr();

        mIsChinese = LanguageUtil.checkSystemLanguageIsChinese(context);
        mModel = Build.MODEL.toUpperCase(Locale.CHINA);
        mFullScreenRatio = ShareData.m_screenRealHeight * 1.0f / ShareData.m_screenRealWidth;
        CameraConfig.getInstance().initAll(getContext());

        initHandler();
        initCB(context);
        initView(context);
    }

    private void initHandler() {
        mUIHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case MSG_CAMERA_ERROR:
                        showCameraPermissionHelper(true);
                        break;
                    case MSG_CANCEL_MASK:
                        if (mRatioBgView != null) {
                            mRatioBgView.DoMaskDismissAnim();
                        }
                        break;
                    case MSG_SHOW_INPUT:
                        ShowInput();
                        break;
                    default:
                        break;
                }
            }
        };
    }

    private void initCB(Context context) {
        mOnEdActionListener = new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                if (actionId == EditorInfo.IME_ACTION_DONE) // 软键盘 确定
                {
                    checkPassword(false);
                }
                return false;
            }
        };

        keyboardMgr = new KeyboardMgr((Activity) context, ARWishesCameraPage.this, new KeyboardMgr.Callback()
        {
            @Override
            public void OnShowKeyboard(KeyboardMgr mgr)
            {
                float transY = mgr.GetKeyboardHeight() / 2f;
                if (mPasswordLayout != null)
                {
                    mPasswordLayout.setTranslationY(-transY);
                }
            }

            @Override
            public void OnHideKeyboard(KeyboardMgr mgr)
            {
                if (mPasswordLayout != null)
                {
                    mPasswordLayout.setTranslationY(0);
                }
            }
        });

        mRatioBgListener = new RatioBgViewV2.OnRatioChangeListener() {

            @Override
            public void onRatioChange(float ratio) {

            }

            @Override
            public void onDismissMaskEnd() {
                mIsInitPage = false;
            }

            @Override
            public void onSplashMaskEnd() {

            }
        };

        mTextWatcher = new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {

            }

            @Override
            public void afterTextChanged(Editable s)
            {
                if (mIsShowPasswordErrorTips)
                {
                    showPasswordErrorMsg(false, "");
                }

                if (s.length() > 0)
                {
                    if (mPasswordConfirm != null)
                    {
                        mPasswordConfirm.setAlpha(1f);
                    }
                }
                else
                {
                    if (mPasswordConfirm != null)
                    {
                        mPasswordConfirm.setAlpha(0.4f);
                    }
                }
            }
        };

        mArWorkListener = new ARWorksMgr.CallBack()
        {
            @Override
            public void onLoadSucceed(ArRes res)
            {
                mVideoPath = res.m_video_url;
                mImgPath = (String) res.m_thumb;
                mShowID = res.m_show_id;

                if (mImgPath != null)
                {
                    Bitmap img = BitmapFactory.decodeFile(mImgPath);
                    if (img != null)
                    {
                        if (mARTipsView != null)
                        {
                            mARTipsView.setBitmap(img);
                        }

                        if (mPasswordBG != null)
                        {
                            mPasswordBG.setVisibility(GONE);
                        }

                        if (mScanView != null)
                        {
                            mScanView.setVisibility(GONE);
                        }

                        MyBeautyStat.onPageStartByRes(R.string.ar祝福_找祝福_打开ar镜头);

                        setDetectTarget(mShowID, mImgPath);

                        CloseInput();

                        if (mWaitDialog != null)
                        {
                            mWaitDialog.setVisibility(GONE);
                        }

                        if (mScanTipsTextLayout != null)
                        {
                            mScanTipsTextLayout.setVisibility(VISIBLE);
                        }
                    }
                }

                mIsLoadingPassword = false;
                showPasswordErrorMsg(false, "");
                setBtnEnable(true);
            }

            @Override
            public void onLoadFailed(String error_zh, String error_en)
            {
                if (mWaitDialog != null)
                {
                    mWaitDialog.setVisibility(GONE);
                }
                mIsLoadingPassword = false;
                showPasswordErrorMsg(true, mIsChinese ? error_zh : error_en);
                setBtnEnable(true);
            }

            @Override
            public void onDownloadFailed()
            {
                if (mWaitDialog != null)
                {
                    mWaitDialog.setVisibility(GONE);
                }
                mIsLoadingPassword = false;
                showPasswordErrorMsg(true, getContext().getString(R.string.ar_wish_find_net_error));
                setBtnEnable(true);
            }

            @Override
            public void onNetError()
            {
                if (mWaitDialog != null)
                {
                    mWaitDialog.setVisibility(GONE);
                }
                mIsLoadingPassword = false;
                showPasswordErrorMsg(true, getContext().getString(R.string.ar_wish_find_net_error));
                setBtnEnable(true);
            }
        };

        mAnimClickListener = new OnAnimationClickListener()
        {
            @Override
            public void onAnimationClick(View v)
            {
                if (v == mBackView)
                {
                    if (!mUIEnable) return;
                    onBack();
                }
                else if (v == mFlashView)
                {
                    if (!mUIEnable) return;

                    if (mIsHideWish) {
                        if (mPreviewView != null) {
                            CameraHandler cameraHandler = RenderHelper.getCameraHandler();
                            if(cameraHandler != null)
                            {
                                mFlashON = !mFlashON;
                                if (mFlashView != null)
                                {
                                    if (mFlashON)
                                    {
                                        MyBeautyStat.onClickByRes(R.string.AR祝福_送祝福_打开镜头拍照_打开手电筒);
                                    }
                                    else
                                    {
                                        MyBeautyStat.onClickByRes(R.string.AR祝福_送祝福_打开镜头拍照_关闭手电筒);
                                    }
                                    mFlashView.setImageResource(mFlashON ? R.drawable.ar_wish_flash_open : R.drawable.ar_wish_flash_close);
                                }
                                cameraHandler.setFlashMode(mFlashON ? CameraConfig.FlashMode.Torch : CameraConfig.FlashMode.Off);
                            }
                        }
                    } else {
                        if (mArGLView != null) {
                            mFlashON = !mFlashON;
                            if (mFlashView != null)
                            {
                                if (mFlashON)
                                {
                                    MyBeautyStat.onClickByRes(R.string.AR祝福_找祝福_打开ar镜头_打开手电筒);
                                }
                                else
                                {
                                    MyBeautyStat.onClickByRes(R.string.AR祝福_找祝福_打开ar镜头_关闭手电筒);
                                }
                                mFlashView.setImageResource(mFlashON ? R.drawable.ar_wish_flash_open : R.drawable.ar_wish_flash_close);
                            }
                            EasyARNative.nativeFlashLightMode(mArGLView.mCameraHandle, mFlashON);
                        }
                    }
                }
                else if (v == mScanView)
                {
                    if (!mUIEnable) return;

                    if (mIsHideWish)
                    {
                        MyBeautyStat.onClickByRes(R.string.AR祝福_送祝福_打开镜头拍照_ok);
                        takePicture();
                    }
                    else
                    {
                        MyBeautyStat.onClickByRes(R.string.AR祝福_找祝福_打开镜头准备扫描_开始扫描);
                        showPasswordArea(true);
                        if (mUIHandler != null)
                        {
                            mUIHandler.sendEmptyMessageDelayed(MSG_SHOW_INPUT, 300);
                        }
                    }
                }
                else if (v == mPasswordConfirm)
                {
                    if (mUIHandler != null)
                    {
                        mUIHandler.removeMessages(MSG_SHOW_INPUT);
                    }

                    checkPassword(true);
                }
                else if (v == mPasswordCancel)
                {
                    if (mIsLoadingPassword)
                    {
                        return;
                    }

                    MyBeautyStat.onClickByRes(R.string.AR祝福_找祝福_输入口令_取消);

                    if (mUIHandler != null)
                    {
                        mUIHandler.removeMessages(MSG_SHOW_INPUT);
                    }

                    CloseInput();

                    showPasswordArea(false);

                    if (mPasswordET != null)
                    {
                        mPasswordET.setText("");
                    }

                    mSystemUiVisibility = -1;
                    changeSystemUiVisibility(View.GONE);
                }
            }
        };

        mFindWishTipsStatusListener = new ARWishTipsView.StatusListener()
        {
            @Override
            public void onShowWishTips()
            {
                setBtnEnable(false);

                if (mScanTipsTextLayout != null)
                {
                    mScanTipsTextLayout.setVisibility(GONE);
                }

                if (mBackView != null)
                {
                    mBackView.setAlpha(0.2f);
                }

                if (mTitleView != null)
                {
                    mTitleView.setAlpha(0.2f);
                }

                if (mFlashView != null)
                {
                    mFlashView.setAlpha(0.2f);
                }
            }

            @Override
            public void onHideWishTips()
            {
                if (mScanTipsTextLayout != null)
                {
                    mScanTipsTextLayout.setVisibility(VISIBLE);
                }

                if (mBackView != null)
                {
                    mBackView.setAlpha(1f);
                }

                if (mTitleView != null)
                {
                    mTitleView.setAlpha(1f);
                }

                if (mFlashView != null)
                {
                    mFlashView.setAlpha(1f);
                }

                setBtnEnable(true);
            }
        };
    }

    private void checkPassword(boolean closeInput)
    {
        if (mIsLoadingPassword)
        {
            return;
        }

        String password = null;

        if (mPasswordET != null)
        {
            password = mPasswordET.getText().toString();
        }

        if (TextUtils.isEmpty(password))
        {
            return;
        }

        MyBeautyStat.onClickByRes(R.string.AR祝福_找祝福_输入口令_确认);

        mPassword = password;

        mIsLoadingPassword = true;

        setBtnEnable(false);

        if (closeInput)
        {
            CloseInput();

            mSystemUiVisibility = -1;
            changeSystemUiVisibility(View.GONE);
        }

        if (mWaitDialog != null)
        {
            mWaitDialog.setVisibility(VISIBLE);
        }

        getWorkByPassword(mPassword);
    }

    private void showPasswordErrorMsg(boolean show, String error)
    {
        mIsShowPasswordErrorTips = show;
        if (mPasswordErrorIcon != null)
        {
            mPasswordErrorIcon.setVisibility(show ? VISIBLE : INVISIBLE);
        }

        if (mPasswordErrorTips != null)
        {
            mPasswordErrorTips.setVisibility(show ? VISIBLE : INVISIBLE);
            mPasswordErrorTips.setText(show ? error : "");
        }
    }

    private void showPasswordArea(boolean show)
    {
        if (show)
        {
            MyBeautyStat.onPageStartByRes(R.string.ar祝福_找祝福_输入口令);
        }
        else
        {
            MyBeautyStat.onPageEndByRes(R.string.ar祝福_找祝福_输入口令);
        }

        setBtnEnable(!show);

        if (mPasswordBG != null)
        {
            mPasswordBG.setVisibility(show ? VISIBLE : GONE);
        }

        if (mPasswordLayout != null)
        {
            mPasswordLayout.setVisibility(show ? VISIBLE : GONE);
        }
    }

    private void getWorkByPassword(String password)
    {
        if (mArWorkMgr != null)
        {
            mArWorkMgr.getWorkByPassword(getContext(), password, mArWorkListener);
        }
    }

    private void initView(Context context) {
        mCameraViewRatio = mFullScreenRatio;
        mCameraViewWidth = ShareData.m_screenRealWidth;
        mCameraViewHeight = ShareData.m_screenRealHeight;
        mCurrentRatio = mCameraViewRatio;

        FrameLayout.LayoutParams params = null;

        mCameraLayout = new FrameLayout(context);
        mCameraLayout.setBackgroundColor(0xff000000);
        params = new LayoutParams(mCameraViewWidth, mCameraViewHeight);
        addView(mCameraLayout, params);

        mARTipsView = new ARWishTipsView(context);
        mARTipsView.setStatusListener(mFindWishTipsStatusListener);
        params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        addView(mARTipsView, params);

        mPicWidth = CameraPercentUtil.WidthPxToPercent(250) * 2;
        mPicCropTopHeight = CameraPercentUtil.WidthPxToPercent(333);//583-250
        mPicCropLeft = (ShareData.m_screenRealWidth - mPicWidth) / 2;

        mAimedTv = new TextView(context);
        mAimedTv.setText(R.string.ar_camera_page_aimed_tips);
        mAimedTv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        mAimedTv.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        mAimedTv.setTextColor(Color.WHITE);
        mAimedTv.setShadowLayer(CameraPercentUtil.WidthPxToPercent(4), 0, CameraPercentUtil.WidthPxToPercent(2), ColorUtils.setAlphaComponent(Color.BLACK, (int) (255 * 0.14f)));
        params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER_HORIZONTAL;
        params.topMargin = CameraPercentUtil.WidthPxToPercent(900);
        addView(mAimedTv, params);

        mScanTipsTextLayout = new RelativeLayout(context);
        params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        params.bottomMargin = CameraPercentUtil.WidthPxToPercent(278);
        addView(mScanTipsTextLayout, params);
        {
            TextView tips = new TextView(context);
            tips.setText(R.string.ar_wish_scan_tips_two);
            tips.setId(R.id.ar_camera_page_scan_tips);
            tips.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
            tips.setTextColor(Color.WHITE);
            RelativeLayout.LayoutParams rl = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            rl.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            rl.addRule(RelativeLayout.CENTER_HORIZONTAL);
            mScanTipsTextLayout.addView(tips, rl);

            TextView tips2 = new TextView(context);
            tips2.setText(R.string.ar_wish_scan_tips_one);
            tips2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
            tips2.setTextColor(Color.WHITE);
            rl = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            rl.addRule(RelativeLayout.ABOVE, tips.getId());
            rl.addRule(RelativeLayout.CENTER_HORIZONTAL);
            rl.bottomMargin = CameraPercentUtil.WidthPxToPercent(16);
            mScanTipsTextLayout.addView(tips2, rl);
        }

        mScanView = new ImageView(context);
        mScanView.setOnTouchListener(mAnimClickListener);
        mScanView.setImageResource(R.drawable.ar_camera_hide_logo);
        params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        params.bottomMargin = CameraPercentUtil.WidthPxToPercent(102);
        addView(mScanView, params);

        mRatioBgView = new RatioBgViewV2(context);
        mRatioBgView.setRatio(mFullScreenRatio);
        mRatioBgView.SetOnRatioChangeListener(mRatioBgListener);
        mRatioBgView.setVisibility(View.GONE);
        params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        addView(mRatioBgView, params);

        mBackView = new ImageView(context);
        mBackView.setOnTouchListener(mAnimClickListener);
        mBackView.setImageResource(R.drawable.ar_back_logo);
        params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topMargin = CameraPercentUtil.WidthPxToPercent(6);
        addView(mBackView, params);

        mTitleView = new TextView(context);
        mTitleView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        mTitleView.setTextColor(Color.WHITE);
        params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER_HORIZONTAL;
        params.topMargin = CameraPercentUtil.WidthPxToPercent(24);
        addView(mTitleView, params);

        mFlashView = new ImageView(context);
        mFlashView.setOnTouchListener(mAnimClickListener);
        mFlashView.setImageResource(R.drawable.ar_wish_flash_close);
        params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.END;
        params.topMargin = CameraPercentUtil.WidthPxToPercent(6);
        addView(mFlashView, params);

        // password
        mPasswordBG = new FrameLayout(context);
        mPasswordBG.setVisibility(GONE);
        mPasswordBG.setBackgroundColor(ColorUtils.setAlphaComponent(Color.BLACK, (int) (255 * 0.5f)));
        params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        addView(mPasswordBG, params);
        {
            RoundRectDrawable bg = new RoundRectDrawable();
            bg.setRoundRectParams(CameraPercentUtil.WidthPxToPercent(30), CameraPercentUtil.WidthPxToPercent(30));
            bg.setColor(Color.WHITE);
            mPasswordLayout = new RelativeLayout(context);
            mPasswordLayout.setBackgroundDrawable(bg);
            mPasswordLayout.setVisibility(GONE);
            params = new LayoutParams(CameraPercentUtil.WidthPxToPercent(570), CameraPercentUtil.WidthPxToPercent(524));
            params.gravity = Gravity.CENTER;
            mPasswordBG.addView(mPasswordLayout, params);
            {
                TextView tips = new TextView(context);
                tips.setId(R.id.ar_camera_page_password_tips);
                tips.setText(R.string.ar_wish_password_tips_one);
                tips.setTextColor(0xff333333);
                tips.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
                RelativeLayout.LayoutParams rl = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                rl.addRule(RelativeLayout.CENTER_HORIZONTAL);
                rl.topMargin = CameraPercentUtil.WidthPxToPercent(50);
                mPasswordLayout.addView(tips, rl);

                TextView tips1 = new TextView(context);
                tips1.setText(R.string.ar_wish_password_tips_two);
                tips1.setTextColor(0xff333333);
                tips1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
                rl = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                rl.addRule(RelativeLayout.CENTER_HORIZONTAL);
                rl.addRule(RelativeLayout.BELOW, tips.getId());
                rl.topMargin = CameraPercentUtil.WidthPxToPercent(14);
                mPasswordLayout.addView(tips1, rl);

                bg = new RoundRectDrawable();
                bg.setRoundRectParams(CameraPercentUtil.WidthPxToPercent(38), CameraPercentUtil.WidthPxToPercent(38));
                bg.setColor(0xfff0f0f0);
                mPasswordET = new EditText(context);
                mPasswordET.setOnClickListener(this);
                mPasswordET.setOnEditorActionListener(mOnEdActionListener);
                mPasswordET.addTextChangedListener(mTextWatcher);
                mPasswordET.setId(R.id.ar_camera_page_password_et);
                mPasswordET.setBackgroundDrawable(bg);
                mPasswordET.setGravity(Gravity.CENTER_VERTICAL);
                mPasswordET.setHint(R.string.ar_find_password_hint);
                mPasswordET.setSingleLine(true);
                mPasswordET.setTextColor(0xff333333);
                mPasswordET.setPadding(CameraPercentUtil.WidthPxToPercent(30), 0, CameraPercentUtil.WidthPxToPercent(30), 0);
                mPasswordET.setHintTextColor(ColorUtils.setAlphaComponent(0xff333333, (int) (255 * 0.3f)));
                mPasswordET.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
                mPasswordET.setKeyListener(new NumberKeyListener() {
                    @Override
                    protected char[] getAcceptedChars() {
                        String passwordRuleStr = getContext().getString(R.string.ar_command_rule);
                        return passwordRuleStr.toCharArray();
                    }

                    @Override
                    public int getInputType() {
                        return InputType.TYPE_CLASS_TEXT;
                    }
                });
                mPasswordET.setFilters(new InputFilter[]{new InputFilter.LengthFilter(18)});
                setCursorDrawableColor(mPasswordET, ImageUtils.GetSkinColor()); // 修改光标颜色
                rl = new RelativeLayout.LayoutParams(CameraPercentUtil.WidthPxToPercent(492), CameraPercentUtil.WidthPxToPercent(76));
                rl.addRule(RelativeLayout.CENTER_HORIZONTAL);
                rl.topMargin = CameraPercentUtil.WidthPxToPercent(180);
                mPasswordLayout.addView(mPasswordET, rl);

                mPasswordErrorIcon = new ImageView(context);
                mPasswordErrorIcon.setVisibility(INVISIBLE);
                mPasswordErrorIcon.setId(R.id.ar_camera_page_password_error);
                mPasswordErrorIcon.setImageResource(R.drawable.ar_wish_password_error);
                rl = new RelativeLayout.LayoutParams(CameraPercentUtil.WidthPxToPercent(30), CameraPercentUtil.WidthPxToPercent(30));
                rl.addRule(RelativeLayout.BELOW, mPasswordET.getId());
                rl.addRule(RelativeLayout.ALIGN_LEFT, mPasswordET.getId());
                rl.topMargin = CameraPercentUtil.WidthPxToPercent(20);
                mPasswordLayout.addView(mPasswordErrorIcon, rl);

                mPasswordErrorTips = new TextView(context);
                mPasswordErrorTips.setVisibility(INVISIBLE);
                mPasswordErrorTips.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
                mPasswordErrorTips.setTextColor(0xffff4b4b);
                rl = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                rl.addRule(RelativeLayout.END_OF, mPasswordErrorIcon.getId());
                rl.addRule(RelativeLayout.ALIGN_BOTTOM, mPasswordErrorIcon.getId());
                rl.leftMargin = CameraPercentUtil.WidthPxToPercent(10);
                mPasswordLayout.addView(mPasswordErrorTips, rl);

                bg = new RoundRectDrawable();
                bg.setRoundRectParams(CameraPercentUtil.WidthPxToPercent(38), CameraPercentUtil.WidthPxToPercent(38));
                bg.setColor(ImageUtils.GetSkinColor());
                mPasswordConfirm = new TextView(context);
                mPasswordConfirm.setId(R.id.ar_camera_page_password_confirm);
                mPasswordConfirm.setOnTouchListener(mAnimClickListener);
                mPasswordConfirm.setBackgroundDrawable(bg);
                mPasswordConfirm.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                mPasswordConfirm.setTextColor(Color.WHITE);
                mPasswordConfirm.setAlpha(0.4f);
                mPasswordConfirm.setText(R.string.ar_find_password_ok);
                mPasswordConfirm.setGravity(Gravity.CENTER);
                rl = new RelativeLayout.LayoutParams(CameraPercentUtil.WidthPxToPercent(417), CameraPercentUtil.WidthPxToPercent(78));
                rl.addRule(RelativeLayout.CENTER_HORIZONTAL);
                rl.addRule(RelativeLayout.BELOW, mPasswordErrorIcon.getId());
                rl.topMargin = CameraPercentUtil.WidthPxToPercent(20);
                mPasswordLayout.addView(mPasswordConfirm,rl);

                mPasswordCancel = new TextView(context);
                mPasswordCancel.setOnTouchListener(mAnimClickListener);
                mPasswordCancel.setText(R.string.ar_find_password_cancel);
                mPasswordCancel.setTextColor(0xff999999);
                mPasswordCancel.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
                mPasswordCancel.setGravity(Gravity.CENTER);
                rl = new RelativeLayout.LayoutParams(CameraPercentUtil.WidthPxToPercent(417), CameraPercentUtil.WidthPxToPercent(80));
                rl.addRule(RelativeLayout.CENTER_HORIZONTAL);
                rl.addRule(RelativeLayout.BELOW, mPasswordConfirm.getId());
                rl.topMargin = CameraPercentUtil.WidthPxToPercent(20);
                mPasswordLayout.addView(mPasswordCancel, rl);
            }
        }

        mWaitDialog = new WaitAnimDialog.WaitAnimView(context);
        mWaitDialog.setVisibility(GONE);
        params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.gravity = Gravity.CENTER;
        addView(mWaitDialog, params);
    }

    /**
     * 反射修改 EditText 光标颜色
     */
    private void setCursorDrawableColor(EditText editText, int color)
    {
        try
        {
            Field fCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
            fCursorDrawableRes.setAccessible(true);
            int mCursorDrawableRes = fCursorDrawableRes.getInt(editText);
            Field fEditor = TextView.class.getDeclaredField("mEditor");
            fEditor.setAccessible(true);
            Object editor = fEditor.get(editText);
            Class<?> clazz = editor.getClass();
            Field fCursorDrawable = clazz.getDeclaredField("mCursorDrawable");
            fCursorDrawable.setAccessible(true);
            Drawable[] drawables = new Drawable[2];
            drawables[0] = ContextCompat.getDrawable(editText.getContext(), mCursorDrawableRes);
            drawables[1] = ContextCompat.getDrawable(editText.getContext(), mCursorDrawableRes);
            drawables[0].setColorFilter(color, PorterDuff.Mode.SRC_IN);
            drawables[1].setColorFilter(color, PorterDuff.Mode.SRC_IN);
            fCursorDrawable.set(editor, drawables);
        }
        catch (Throwable ignored)
        {

        }
    }

    private void setBtnEnable(boolean enable)
    {
        mUIEnable = enable;
    }

    private void CloseInput()
    {
        if (mUIHandler != null)
        {
            mUIHandler.removeMessages(MSG_SHOW_INPUT);
        }

        if (mPasswordET != null)
        {
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null)
            {
                imm.hideSoftInputFromWindow(mPasswordET.getWindowToken(), 0);
            }
        }
    }

    public void ShowInput()
    {
        if (mPasswordET != null)
        {
            mPasswordET.requestFocus();
            mPasswordET.setCursorVisible(true);
            InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputMethodManager != null)
            {
                // 接受软键盘输入的编辑文本或其它视图
                inputMethodManager.showSoftInput(mPasswordET, InputMethodManager.SHOW_FORCED);
            }
        }
    }

    /**
     * 加载检测目标图像
     * @param showId
     * @param imgPath
     */
    private void setDetectTarget(String showId, String imgPath) {
        if (TextUtils.isEmpty(showId) || TextUtils.isEmpty(imgPath)) return;

        //示例：装载图像目标
        TargetsEffectData.ImageTargets imageTargets = new TargetsEffectData.ImageTargets();
        imageTargets.mDataStorageType = StorageType.Absolute;  //设置图像目标存储方式（绝对路径存储方式）

        ArrayList<TargetsEffectData.ImageTargetsDataSet> dataSets = new ArrayList<>();//识别目标图片的数据集
        //示例：装载两个图像目标进如数据集
        TargetsEffectData.ImageTargetsDataSet target = new TargetsEffectData.ImageTargetsDataSet();
        target.mImageName = showId;
        target.mImagePath = imgPath;
        dataSets.add(target);
        imageTargets.mImageTargetsDataSet = dataSets;
        if (mArGLView != null && imageTargets != null) {
            if (mImageTargets != null) {
               EasyARNative.nativeUnLoadImageTargets(mArGLView.mCameraHandle, mImageTargets); //卸载图像目标
            }

            mImageTargets = imageTargets;
            EasyARNative.nativeLoadImageTargets(mArGLView.mCameraHandle, mImageTargets); //加载图像目标
        }
    }

    private void initCameraViewByType() {
        FrameLayout.LayoutParams params = null;
        if (mPageType == 1) {
            int version = TargetsEffectData.VersionID.APP_NORMAL_VERSION;
            String packageName = getContext().getPackageName();
            if ("com.adnonstop.beautyCamera".equals(packageName)) {
                version =  TargetsEffectData.VersionID.APP_GOOGLE_VERSION;
            }

            if (Engine.initialize((Activity) getContext(), EasyARNative.nativeGetEnginekey(version))) {   //初始化验证EasyAR秘钥
                boolean hasPermission = PermissionHelper.queryCameraPermission(getContext());
                if (hasPermission) {
                    mArGLView = new GLView(getContext());
                    mArGLView.SetmCameraType(CameraDeviceType.Default);
                    mArGLView.SetPreViewSize(1280, 720);
                    mArGLView.SetmCameraFocusMode(CameraDeviceFocusMode.Continousauto);
                    mArGLView.setDetectCallback(this);
                    params = new LayoutParams(mCameraViewWidth, mCameraViewHeight);
                    mCameraLayout.addView(mArGLView, params);

                    mArGLView.onResume();// 解决切换语言后打开无法预览的问题

                    keepScreenWakeUp(true);

                } else {
                    if (mUIHandler != null) {
                        mUIHandler.sendEmptyMessage(MSG_CAMERA_ERROR);
                    }
                }
            } else {
                //"Initialization Failed."
            }

        } else {
            mPreviewView = new CameraRenderView(getContext());
            params = new LayoutParams(mCameraViewWidth, mCameraViewHeight);
            mCameraLayout.addView(mPreviewView, params);

            if (mRatioBgView != null) {
                mRatioBgView.setVisibility(View.VISIBLE);
            }

            openCameraById();
        }
    }

    private void changeSystemUiVisibility(int visibility) {
        if (visibility != mSystemUiVisibility) {
            int vis = ShareData.showOrHideStatusAndNavigation(getContext(), visibility == View.VISIBLE, mOriginVisibility, visibility == View.VISIBLE);
            if (mOriginVisibility == -1 && visibility == View.GONE) {
                mOriginVisibility = vis;
            }
            mSystemUiVisibility = visibility;
        }
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        changeSystemUiVisibility(visibility == View.GONE ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (hasWindowFocus) {
            mSystemUiVisibility = -1;
            changeSystemUiVisibility(View.GONE);
        }
    }

    @Override
    public void SetData(HashMap<String, Object> params) {
        if (params != null) {
            if (params.containsKey("pageType")) {
                mPageType = (Integer) params.get("pageType");
            }
        }

        mIsHideWish = mPageType == 0;

        if (!mIsHideWish)
        {
            MyBeautyStat.onPageStartByRes(R.string.ar祝福_找祝福_打开镜头准备扫描);
        }

        initUIStatus();
        initCameraViewByType();
    }

    private void initUIStatus()
    {
        if (mTitleView != null)
        {
            mTitleView.setText(mIsHideWish ? R.string.ar_camera_page_hide_title : R.string.ar_camera_page_find_title);
        }

        if (mIsHideWish)
        {
            if (mScanTipsTextLayout != null)
            {
                mScanTipsTextLayout.setVisibility(GONE);
            }
        }
        else
        {
            if (mAimedTv != null)
            {
                mAimedTv.setVisibility(GONE);
            }
            if (mScanTipsTextLayout != null)
            {
                mScanTipsTextLayout.setVisibility(GONE);
            }
        }

        if (mScanView != null)
        {
            int res_id = mIsHideWish ? R.drawable.ar_hide_ok : R.drawable.ar_find_scan;
            mScanView.setImageResource(res_id);
        }
    }

    private void openCameraById() {
        if (mPreviewView == null) return;
        CameraHandler cameraHandler = RenderHelper.getCameraHandler();
        if (cameraHandler == null) {
            return;
        }
        mDoCameraOpenAnim = true;
        if (mCurrentRatio < CameraConfig.PreviewRatio.Ratio_16_9) {
            cameraHandler.setPreviewSize(mCameraViewWidth, (int) (mCameraViewWidth * CameraConfig.PreviewRatio.Ratio_4_3), mCameraSizeType);
        } else {
            cameraHandler.setPreviewSize(mCameraViewWidth, (int) (mCameraViewWidth * CameraConfig.PreviewRatio.Ratio_16_9), mCameraSizeType);
        }
        CameraThread cameraThread = cameraHandler.getCamera();
        if (cameraThread != null) {
            cameraThread.setCameraCallback(this);
            cameraThread.setCameraAllCallback(this);
        }
        //mIsInitCamera = true;
        mCurrentCameraId = 0;
        cameraHandler.openCamera(mCurrentCameraId);
    }

    @Override
    public void onCameraOpen() {
        final boolean finalIsSwitchCamera = mIsSwitchCamera;
        mUIHandler.post(new Runnable() {
            @Override
            public void run() {
                initWidgetStateAfterCameraOpen(finalIsSwitchCamera);

//                mIsInitCamera = false;
            }
        });
        if (mPreviewView != null) {
            mPreviewView.runOnGLThread(new RenderRunnable() {
                @Override
                public void run(RenderThread renderThread) {
                    renderThread.setRenderMode(1);
                    renderThread.setFrameTopPadding(0);
                    renderThread.setWaterMarkHasDate(false);
//                    renderThread.setDetectFaceCallback(ARWishesCameraPage.this);
                    renderThread.setOnCaptureFrameListener(ARWishesCameraPage.this);

                    if (renderThread.getFilterManager() != null) {
                        renderThread.getFilterManager().setBeautyEnable(true);
                        renderThread.getFilterManager().setStickerEnable(true);
                        //renderThread.getFilterManager().changeColorFilter(mCurrentFilterRes);
//                        renderThread.getFilterManager().setRatioAndOrientation(1 / mCurrentRatio, mScreenOrientation, mFrameTopPadding);
                    }
                    renderThread.getFilterManager().setBeautyEnable(true);
                }
            });
        }
        mIsSwitchCamera = false;
    }

    @Override
    public void onCameraClose() {

    }

    private void initWidgetStateAfterCameraOpen(boolean isSwitchCamera) {
        CameraHandler cameraHandler = RenderHelper.getCameraHandler();
        if (cameraHandler == null) {
            return;
        }
        //Log.i(TAG, "initWidgetStateAfterCameraOpen: ");
        CameraThread cameraThread = cameraHandler.getCamera();
        if (cameraThread != null) {
            mCurrentCameraId = cameraThread.getCurrentCameraId();
            isFront = cameraThread.isFront();
        }

        //Log.i(TAG, "mCurrentCameraId:" + mCurrentCameraId);
        CameraConfig.getInstance().saveData(CameraConfig.ConfigMap.LastCameraId, mCurrentCameraId);
        final int preDegree = getPreviewPatchDegree();
        //Log.i(TAG, "preDegree:"+preDegree);
        cameraHandler.setPreviewOrientation(preDegree);//通过外部获取预览修正角度;

        setCameraFocusState(true);

        if (mPreviewView != null) {
            mPreviewView.queueEvent(new RenderRunnable() {
                @Override
                public void run(RenderThread renderThread) {
                    if (renderThread != null && renderThread.getFilterManager() != null) {
                        renderThread.getFilterManager().setPreviewDegree(preDegree, isFront);
                    }
                }
            });
        }
        keepScreenWakeUp(true);
    }

    public int getPreviewPatchDegree() {
        if (mCurrentCameraId == 0) {
            if (!CameraConfig.getInstance().getBoolean(CameraConfig.ConfigMap.FixPreviewPatch_0)) {
                int defaultDegree = CameraConfig.getInstance().getInt(CameraConfig.ConfigMap.PreviewPatch_0, true);
                int degree = CameraConfig.getInstance().getInt(CameraConfig.ConfigMap.PreviewPatch_0);
                if (defaultDegree == 0 && degree == 0) {
                    CameraConfig.getInstance().saveData(CameraConfig.ConfigMap.PreviewPatch_0, 90);
                    CameraConfig.getInstance().saveData(CameraConfig.ConfigMap.FixPreviewPatch_0, true);
                }
            }
            return CameraConfig.getInstance().getInt(CameraConfig.ConfigMap.PreviewPatch_0);
        } else if (mCurrentCameraId == 1) {
            if (!CameraConfig.getInstance().getBoolean(CameraConfig.ConfigMap.FixPreviewPatch_1)) {
                int defaultDegree = CameraConfig.getInstance().getInt(CameraConfig.ConfigMap.PreviewPatch_1, true);
                int degree = CameraConfig.getInstance().getInt(CameraConfig.ConfigMap.PreviewPatch_1);
                if (defaultDegree == 0 && degree == 0) {
                    CameraConfig.getInstance().saveData(CameraConfig.ConfigMap.PreviewPatch_1, 90);
                    CameraConfig.getInstance().saveData(CameraConfig.ConfigMap.FixPreviewPatch_1, true);
                }
            }
            return CameraConfig.getInstance().getInt(CameraConfig.ConfigMap.PreviewPatch_1);
        }
        return 90;
    }

    private void setCameraFocusState(boolean autoFocus) {
        CameraHandler cameraHandler = RenderHelper.getCameraHandler();
        if (cameraHandler == null) return;
        cameraHandler.setAutoFocus(autoFocus);
        cameraHandler.setAutoLoopFocus(autoFocus);
    }

    private void showCameraPermissionHelper(boolean show) {
        if (show && mCameraErrorTipsDialog == null) {
            mCameraErrorTipsDialog = new CameraErrorTipsDialog(getContext());
            mCameraErrorTipsDialog.setOnClickListener(new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //0：打开提示 1：关闭tips
                    if (which == 0) {
                        if (mSite != null) {
                            String url = "http://wap.adnonstop.com/beauty_camera/prod/public/index.php?r=Softtext/Guidance&key=";
                            try {
                                url += URLEncoder.encode(ExceptionData.SafeString(Build.FINGERPRINT), "UTF-8");
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                            HashMap<String, Object> params = new HashMap<String, Object>();
                            params.put("url", url);
                            mSite.openCameraPermissionsHelper(getContext(), params);
                        }
                    }
                    dialog.dismiss();
                }
            });
            mCameraErrorTipsDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    if (mCameraErrorTipsDialog != null && mCameraErrorTipsDialog.canClosePage()) {
                        onBack();
                    }
                    mCameraErrorTipsDialog = null;
                }
            });
            mCameraErrorTipsDialog.show();
        } else if (!show && mCameraErrorTipsDialog != null) {
            mCameraErrorTipsDialog.setCanClosePage(false);
            mCameraErrorTipsDialog.dismiss();
        }
    }

    @Override
    public void onScreenOrientationChanged(int orientation, int pictureDegree, float fromDegree, float toDegree) {

    }

    @Override
    public void onAutoFocus(boolean success, Camera camera) {

    }

    @Override
    public void onError(int error, Camera camera) {
        if (mUIHandler != null) {
            mUIHandler.obtainMessage(MSG_CAMERA_ERROR, error, error).sendToTarget();
        }
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {

    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        if (mDoCameraOpenAnim) {
            mDoCameraOpenAnim = false;
            mUIHandler.sendEmptyMessageDelayed(MSG_CANCEL_MASK, 600);
        }
    }

    @Override
    public void onShutter() {

    }

    private void keepScreenWakeUp(boolean wakeup) {
        if (wakeup && !mIsKeepScreenOn) {
            ((Activity) getContext()).getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            mIsKeepScreenOn = true;
        } else if (!wakeup && mIsKeepScreenOn) {
            ((Activity) getContext()).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            mIsKeepScreenOn = false;
        }
    }

    public void resumeResource() {
        keepScreenWakeUp(true);

        if (mIsHideWish) {
            CameraHandler cameraHandler = RenderHelper.getCameraHandler();
            if (cameraHandler != null) {
                CameraThread cameraThread = cameraHandler.getCamera();
                if (cameraThread != null) {
                    cameraThread.setCameraCallback(this);
                    cameraThread.setCameraAllCallback(this);
                }
                cameraHandler.setFlashMode(mFlashON ? CameraConfig.FlashMode.Torch : CameraConfig.FlashMode.Off, true);
            }
        } else {
            if (mArGLView != null) {
                mArGLView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        EasyARNative.nativeFlashLightMode(mArGLView.mCameraHandle, mFlashON);
                    }
                }, 500);
            }
        }
        if (mFlashON && mFlashView != null) {
            mFlashView.setImageResource(R.drawable.ar_wish_flash_open);
        }
    }

    public void recycleResource() {
        keepScreenWakeUp(false);

        if (mFlashView != null) {
            mFlashView.setImageResource(R.drawable.ar_wish_flash_close);
        }

        if (mIsHideWish) {
            CameraHandler cameraHandler = RenderHelper.getCameraHandler();
            if (cameraHandler != null) {
                cameraHandler.setFlashMode(CameraConfig.FlashMode.Off);

                CameraThread cameraThread = cameraHandler.getCamera();
                if (cameraThread != null) {
                    cameraThread.setCameraAllCallback(null);
                }
            }
        } else {
           /* if (mArGLView != null) {

            }*/
        }
    }

    @Override
    public void onPageResult(int siteID, HashMap<String, Object> params) {
        super.onPageResult(siteID, params);
        if (siteID == SiteID.WEBVIEW) {
            showCameraPermissionHelper(true);
        }
        else if (siteID == SiteID.AR_VIDEO)
        {
            mFlashON = false;
            if (mFlashView != null) {
                mFlashView.setImageResource(R.drawable.ar_wish_flash_close);
            }
            setBtnEnable(true);

            if (mARTipsView != null){
                mARTipsView.setBitmap(null);
            }

            if (!TextUtils.isEmpty(mPassword))
            {
                mPassword = null;
            }

            if (mBackView != null)
            {
                mBackView.setAlpha(1f);
            }

            if (mTitleView != null)
            {
                mTitleView.setAlpha(1f);
            }

            if (mFlashView != null)
            {
                mFlashView.setAlpha(1f);
            }

            if (mPasswordET != null)
            {
                mPasswordET.setText("");
            }

            if (mPasswordConfirm != null)
            {
                mPasswordConfirm.setAlpha(0.4f);
            }

            if (mScanTipsTextLayout != null)
            {
                mScanTipsTextLayout.setVisibility(GONE);
            }

            showPasswordErrorMsg(false, "");

            if (mArGLView != null && mImageTargets != null) {
                EasyARNative.nativeUnLoadImageTargets(mArGLView.mCameraHandle, mImageTargets); //卸载图像目标
                mImageTargets = null;
            }

            mDealResult = false;

            if (mScanView != null){
                mScanView.setVisibility(VISIBLE);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mArGLView != null) {
            mArGLView.onResume();
        }
        if (mPreviewView != null) {
            if (mCurrentRatio < CameraConfig.PreviewRatio.Ratio_16_9) {
                mPreviewView.setPreviewSize(mCameraViewWidth, (int) (mCameraViewWidth * CameraConfig.PreviewRatio.Ratio_4_3), mCameraSizeType);
            } else {
                mPreviewView.setPreviewSize(mCameraViewWidth, (int) (mCameraViewWidth * CameraConfig.PreviewRatio.Ratio_16_9), mCameraSizeType);
            }
            mPreviewView.onResume();
        }
        resumeResource();

        changeSystemUiVisibility(View.GONE);
    }

    private boolean isShowPasswordArea()
    {
        return mPasswordBG != null && mPasswordBG.getVisibility() == View.VISIBLE;
    }

    @Override
    public void onBack() {
        if (mARTipsView != null && mARTipsView.isShowTips()){
            mARTipsView.narrow();
            return;
        }

        if (isShowPasswordArea()){
            showPasswordArea(false);
            return;
        }

        if (mIsHideWish)
        {
            MyBeautyStat.onClickByRes(R.string.AR祝福_送祝福_打开镜头拍照_返回);
        }
        else
        {
            if (mDealResult)
            {
                MyBeautyStat.onClickByRes(R.string.AR祝福_找祝福_打开ar镜头_返回);
            }
            else
            {
                MyBeautyStat.onClickByRes(R.string.AR祝福_找祝福_打开镜头准备扫描_返回);
            }
        }

        CloseInput();

        if (mSite != null) {
            mSite.onBack(getContext());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mArGLView != null) {
            mArGLView.onPause();
        }
        if (mPreviewView != null) {
            mPreviewView.onPause();
        }
        recycleResource();

        changeSystemUiVisibility(View.VISIBLE);
    }

    @Override
    public void onClose() {
        super.onClose();
        if (mArGLView != null) {
            mArGLView.setDetectCallback(null);
            mArGLView.onDestroy();
            mArGLView = null;
        }
        if (mPreviewView != null) {
            mPreviewView.onPause();
            mPreviewView.onDestroy();
        }
        recycleResource();

        if (mArWorkMgr != null)
        {
            mArWorkMgr.clearAll();
            mArWorkMgr = null;
        }
        if (mWaitDialog != null)
        {
            mWaitDialog.stop();
            mWaitDialog = null;
        }
        keyboardMgr.ClearAll();
        keyboardMgr = null;
        CameraConfig.getInstance().clearAll();

        changeSystemUiVisibility(View.VISIBLE);

        clearViewStatus();
        removeAllViews();

        if (!mIsHideWish && mImageTargets == null)
        {
            MyBeautyStat.onPageEndByRes(R.string.ar祝福_找祝福_打开镜头准备扫描);
        }

        if ( mImageTargets != null)
        {
            MyBeautyStat.onPageEndByRes(R.string.ar祝福_找祝福_打开ar镜头);
        }
    }

    private void clearViewStatus()
    {
        if (mPasswordLayout != null)
        {
            mPasswordLayout.removeAllViews();
            mPasswordLayout = null;
        }

        if (mARTipsView != null)
        {
            mARTipsView.setStatusListener(null);
            mFindWishTipsStatusListener = null;
            mARTipsView = null;
        }

        if (mTitleView != null)
        {
            mTitleView = null;
        }

        if (mCameraLayout != null)
        {
            mCameraLayout.removeAllViews();
            mCameraLayout = null;
        }

        if (mRatioBgView != null)
        {
            mRatioBgView.SetOnRatioChangeListener(null);
            mRatioBgView.clearMemory();
        }

        if (mPasswordET != null)
        {
            mPasswordET.setOnEditorActionListener(null);
            mPasswordET.removeTextChangedListener(mTextWatcher);
            mTextWatcher = null;
            mOnEdActionListener = null;
        }

        if (mArGLView != null)
        {
            mArGLView = null;
        }

        mVideoPath = null;
        mSite = null;
        mUIHandler = null;
        mRatioBgListener = null;
        mAnimClickListener = null;
    }

    private void takePicture() {
        if (mIsTackingPic) {
            return;
        }
        mIsTackingPic = true;
        if(mPreviewView != null) {
            mPreviewView.runOnGLThread(new RenderRunnable() {
                @Override
                public void run(RenderThread renderThread) {
                    if(renderThread != null) {
                        renderThread.setRecordState(RecordState.CAPTURE_A_FRAME);
                    }
                }
            });
        }
    }

    @Override
    public void onCaptureFrame(int frameType, final IntBuffer data, final int width, final int height) {
        if(data == null || width == 0 || height == 0) {
            mIsTackingPic = false;
            return;
        }
        if(frameType == 1) {
            mUIHandler.post(new Runnable() {
                @Override
                public void run() {
                    System.gc();
                    Bitmap temp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                    data.rewind();
                    temp.copyPixelsFromBuffer(data);
                    Bitmap bmp = null;
                    if(temp != null && !temp.isRecycled()) {
                        int x = mPicCropLeft;
                        int bmpWidth = mPicWidth;
                        int y = height - bmpWidth - mPicCropTopHeight;
                        int bmpHeight = mPicWidth;

                        Matrix mt = new Matrix();
                        mt.postScale(1, -1);
                        bmp = Bitmap.createBitmap(temp, x, y, bmpWidth, bmpHeight, mt, true);
                        temp.recycle();
                        temp = null;
                    }
                    if(bmp != null && !bmp.isRecycled()) {
                        String imagePath = FileCacheMgr.GetLinePath() + ".img";
                        CommonUtils.MakeParentFolder(imagePath);
                        Utils.SaveImg(getContext(), bmp, imagePath, 100, false);

                        if(mSite != null) {
                            HashMap<String, Object> params = new HashMap<String, Object>();
                            params.put(ARHideWishPrePage.KEY_IMAGE_PATH, imagePath);
                            params.put(ARHideWishPrePage.KEY_IMAGE_THUMB, bmp);
                            mSite.hideVideo(getContext(), params);
                        }
                    }
                    mIsTackingPic = false;
                }
            });
        }
    }

    @Override
    public void onResultCallback(boolean isSuccess, String name) {
//        Log.i("vvv", "onResultCallback: "+isSuccess+", "+name);
        if (!mDealResult && isSuccess) {
            mDealResult = true;
            //判断name是否与showId相同，如果不相同mDealResult置为false
            mUIHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mSite != null)
                    {
                        mSite.openVideo(getContext(), mVideoPath);
                    }
                }
            });
        }
    }

    @Override
    public void onClick(View v)
    {
        if (v == mPasswordET)
        {
            if (mUIHandler != null)
            {
                mUIHandler.removeMessages(MSG_SHOW_INPUT);
            }
        }
    }
}
