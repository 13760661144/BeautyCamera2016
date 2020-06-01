package cn.poco.live;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.RectF;
import android.hardware.Camera;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.graphics.ColorUtils;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import cn.poco.beautify.RecomDisplayMgr;
import cn.poco.camera.CameraAllCallback;
import cn.poco.camera.CameraConfig;
import cn.poco.camera2.CameraCallback;
import cn.poco.camera2.CameraErrorTipsDialog;
import cn.poco.camera2.CameraHandler;
import cn.poco.camera2.CameraThread;
import cn.poco.camera3.beauty.BeautySelectorView;
import cn.poco.camera3.beauty.STag;
import cn.poco.camera3.beauty.ShapeSPConfig;
import cn.poco.camera3.beauty.TabUIConfig;
import cn.poco.camera3.beauty.callback.PageCallbackAdapter;
import cn.poco.camera3.beauty.data.BeautyData;
import cn.poco.camera3.beauty.data.BeautyInfo;
import cn.poco.camera3.beauty.data.BeautyResMgr;
import cn.poco.camera3.beauty.data.BeautyShapeDataUtils;
import cn.poco.camera3.beauty.data.ShapeData;
import cn.poco.camera3.beauty.data.SuperShapeData;
import cn.poco.camera3.beauty.recycler.ShapeExAdapter;
import cn.poco.camera3.config.CameraStickerConfig;
import cn.poco.camera3.config.MsgToastConfig;
import cn.poco.camera3.info.sticker.StickerInfo;
import cn.poco.camera3.ui.CameraLightnessSeekBar;
import cn.poco.camera3.ui.ColorFilterToast;
import cn.poco.camera3.ui.MsgToast;
import cn.poco.camera3.ui.RatioBgViewV2;
import cn.poco.camera3.ui.drawable.RoundRectDrawable;
import cn.poco.camera3.ui.drawable.StickerAnimDrawable;
import cn.poco.camera3.util.CameraPercentUtil;
import cn.poco.camera3.util.LanguageUtil;
import cn.poco.camera3.util.RatioBgUtils;
import cn.poco.dynamicSticker.FaceAction;
import cn.poco.dynamicSticker.StickerSound;
import cn.poco.exception.ExceptionData;
import cn.poco.filter4.recycle.FilterAdapter;
import cn.poco.filterBeautify.FilterBeautyParams;
import cn.poco.framework.BaseSite;
import cn.poco.framework.EventCenter;
import cn.poco.framework.EventID;
import cn.poco.framework.IPage;
import cn.poco.framework.SiteID;
import cn.poco.gldraw2.CameraRenderView;
import cn.poco.gldraw2.DetectFaceCallback;
import cn.poco.gldraw2.FaceDataHelper;
import cn.poco.gldraw2.RenderHelper;
import cn.poco.gldraw2.RenderRunnable;
import cn.poco.gldraw2.RenderThread;
import cn.poco.glfilter.shape.FaceShapeType;
import cn.poco.glfilter.sticker.OnDrawStickerResListener;
import cn.poco.glfilter.sticker.StickerDrawHelper;
import cn.poco.image.PocoFace;
import cn.poco.live.dui.DUIConfig;
import cn.poco.live.server.BMTUi;
import cn.poco.live.site.LivePageSite;
import cn.poco.live.sticker.StickerMgr;
import cn.poco.live.sticker.StickerView;
import cn.poco.live.sticker.local.StickerMgrPage;
import cn.poco.login.UserMgr;
import cn.poco.resource.AbsDownloadMgr;
import cn.poco.resource.BaseRes;
import cn.poco.resource.DownloadMgr;
import cn.poco.resource.FilterGroupRes;
import cn.poco.resource.FilterRes;
import cn.poco.resource.IDownload;
import cn.poco.resource.RecommendRes;
import cn.poco.resource.ResType;
import cn.poco.resource.VideoStickerRes;
import cn.poco.statistics.MyBeautyStat;
import cn.poco.tianutils.ShareData;
import cn.poco.utils.OnAnimationClickListener;
import cn.poco.video.encoder.LiveEncoderServer;
import cn.poco.video.encoder.MediaEncoder;
import cn.poco.video.encoder.MediaVideoEncoder;
import cn.poco.video.encoder.RecordState;
import cn.poco.widget.PressedButton;
import my.beautyCamera.R;

/**
 * Created by zwq on 2018/01/15 10:30.<br/><br/>
 */

public class LivePage extends IPage implements CameraCallback, CameraAllCallback, DetectFaceCallback, LivePageListener
{
    // view
    private CameraRenderView mPreviewView;
    private RatioBgViewV2 mRatioBgView;
    private PressedButton mBackBtn; // 返回
    private PressedButton mStickerBtn; // 贴纸
    private PressedButton mBeautyBtn; // 美形
    private PressedButton mSwitchBtn; // 前后置
    private StickerView mStickerView;
    private StickerMgrPage mStickerMgrPage;
    private FocusView mFocusView; // 对焦框
    private CameraLightnessSeekBar mLightnessSeekBar;
    private TextView mLinkPcTipsView;
    private TextView mAutoLockTipsView;

    private int mLinkTipsHeight;
    private int mAutoTipsHeight;
    private int mLinkTipTopMargin;
    private int mAutoTipsTopMargin;

    //美颜、脸型、滤镜控件
    private TabUIConfig mBeautySelectorTabUIConfig;
    private BeautySelectorView mBeautySelectorView;
    private int mBeautySelectorTranslationY;
    private PageCallbackAdapter mBeautySelectorCB;
    private boolean isDoingBeautySelectorAnim;
    private FilterRes mCurrentFilterRes;

    private StickerAnimDrawable mStickerBtnAnim;
    private RatioBgViewV2.OnRatioChangeListener mRatioBgListener;

    private GestureListener mGestureListener;
    private GestureDetector mGestureDetector;
    private OnAnimationClickListener mAnimationClickListener;

    private PCStatusListenerAdapter mPCStatusListenerAdapter;
    private LivePageSite mSite;
    private EventCenter.OnEventListener mEventListener;

    //推荐弹框
    private boolean mShowRecomView = false;
    private RecomDisplayMgr mRecomDisplayMgr;
    private RecomDisplayMgr.ExCallback mRecomCallback;
    private int mRecomMgrViewCloseCount;
    private FrameLayout mPopFrameView;

    //Toast层
    private MsgToast mMsgToast;
    private ColorFilterToast mFilterMsgToast;

    private boolean isClickFilterSensor = true;

    private AbsDownloadMgr.DownloadListener mDownloadListener;

    private boolean mIsInitPage; // 是否在初始化页面
    private String mModel;
    private float mFullScreenRatio;
    private float mCameraViewRatio;
    private int mCameraViewWidth;
    private int mCameraViewHeight;
    private int mCameraSizeType = 0;

    private int mCurrentCameraId = 1;
    private boolean isFront;
    private boolean mIsSwitchCamera;
    private int mDoFocusWithFace;

    private String mCurrentFlashMode = CameraConfig.FlashMode.Off;

    private float mCurrentRatio;
    private int mScreenOrientation;
    private int mFrameTopPadding;

    /**
     * 保持屏幕常亮
     *
     * @param wakeup
     */
    private boolean mIsKeepScreenOn;

    private boolean mDoCameraOpenAnim;
    private Handler mUIHandler;
    private static final int MSG_CANCEL_MASK = 100;
    private static final int MSG_SHOW_ACTION_TIPS = 101;//贴纸动作提示
    private static final int MSG_DETECT_FACE_FINISH = 102;//人脸检测
    private static final int MSG_DO_FOCUS_WITH_FACE = 103;//跟随人脸对焦
    private static final int MSG_CLEAR_FOCUS_AND_METERING = 104; // 取消对焦、测光
    private static final int MSG_OPEN_OR_CLOSE_AUTO_FOCUS = 105;//打开或关闭自动对焦
    private static final int MSG_CAMERA_ERROR = 106;//镜头错误
    private static final int MSG_RESET_GESTURE_STATE = 107;
    private static final int MSG_DO_FOCUS_AND_METERING = 108;//对焦、测光
    private static final int MSG_SHOW_LIGHTNESS_SEEK_BAR = 109;//显示曝光
    private static final int MSG_HIDE_LIGHTNESS_SEEK_BAR = 110;//隐藏曝光
    private static final int MSG_LONG_PRESS_DO_FOCUS_AND_METERING = 111;//长按对焦、测光
    private static final int MSG_PC_CONNECT = 112; // pc 端
    private static final int MSG_PC_DISCONNECT = 113;

    //PC 通讯 贴纸
    private static final int MSG_PC_SELECTED_DECOR = 114;

    //PC 通讯 Tab
    private static final int MSG_PC_CLICK_DECOR_TAB = 115;
    private static final int MSG_PC_CLICK_BEAUTY_TAB = 116;
    private static final int MSG_PC_CLICK_FILTER_TAB = 117;
    private static final int MSG_PC_CLICK_SHAPE_TAB = 118;

    //PC 通讯 美颜
    private static final int MSG_PC_UPDATE_BEAUTY = 119;

    //PC 通讯 滤镜
    private static final int MSG_PC_SELECTED_FILTER = 120;

    //PC 通讯 脸型
    private static final int MSG_PC_SELECTED_SHAPE = 121;
    private static final int MSG_PC_RESET_SHAPE_DATA = 122;
    private static final int MSG_PC_SHAPE_LAYOUT_OPEN = 123;
    private static final int MSG_PC_UPDATE_SHAPE = 124;

    private static final int MSG_GESTURE_CHANGE = 150;//触屏手势


    /**
     * 虚拟键隐藏与显示
     */
    private int mOriginVisibility = -1;
    private int mSystemUiVisibility = -1;

    private CameraErrorTipsDialog mCameraErrorTipsDialog;

    private int mStickerId;
    private int mStickerTongJiId;
    private boolean mStickerDealWithShape;
    private String mActionName;
    private boolean mShowActionTip;
    private int mLastResShapeTypeId;
    private int mShapeTypeId;
    private boolean mHasStickerSound;
    private int mLastResType;

    private boolean mIsInitCamera;
    private boolean mDoingTransYAnim;
    private boolean mTriggerLongPress;
    private boolean mLongPressFocus;
    private int mCurrentExposureValue;

    // 屏幕旋转
    private int mDegree;
    private int mLastDegree;
    private boolean mDoingRotationAnim;
    private int mAnimTargetDegree;
    private boolean isLandscape;
    private int mOrientationShiftType = OrientationShiftType.NULL;
    private CameraLightnessSeekBar.OnSeekBarChangeListener mLightnessBarListener;

    //当前使用的美颜美形参数
    private FilterBeautyParams mFilterBeautyParams;

    @interface OrientationShiftType
    {
        int NULL = 1 << 10; // 没转变过
        int VERTICAL_TO_HORIZONTAL = 1 << 11; // 竖屏 --> 横屏
        int HORIZONTAL_TO_VERTICAL = 1 << 12; // 横屏 --> 竖屏
        int MIRROR = 1 << 13; // 镜像 (横屏 --> 横屏 or 竖屏 --> 竖屏)
    }

    private int mVideoWidth;
    private int mVideoHeight;
    private MediaVideoEncoder mLiveMediaVideoEncoder;
    private LiveEncoderServer mLiveEncoderServer;

    public LivePage(Context context, BaseSite site)
    {
        super(context, site);
        MyBeautyStat.onPageStartByRes(R.string.直播助手_直播页_主页面);

        LanguageUtil.SetLanguage(Locale.CHINA, getContext());

        mSite = (LivePageSite) site;

        mIsInitPage = true;

        mBeautySelectorTranslationY = CameraPercentUtil.HeightPxToPercent(360 + 30 + 80);
        mModel = Build.MODEL.toUpperCase(Locale.CHINA);
        mFullScreenRatio = ShareData.m_screenRealHeight * 1.0f / ShareData.m_screenRealWidth;
        mFilterBeautyParams = new FilterBeautyParams();

        CameraConfig.getInstance().initAll(getContext());
        ShapeSPConfig.getInstance(getContext());
        initHandler();
        initCB();
        initGesture(context);
        initView(context);

        BMTUi.getInstance().setResources(getContext().getApplicationContext().getResources());
        DUIConfig.getInstance().setStatusListener(mPCStatusListenerAdapter);

        if (mLiveEncoderServer == null) {
            mLiveEncoderServer = new LiveEncoderServer();
            mLiveEncoderServer.init();
            mLiveEncoderServer.setCanStopServer(false);
        }
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
                    case MSG_CANCEL_MASK:
                        if (mRatioBgView != null)
                        {
                            mRatioBgView.DoMaskDismissAnim();
                        }
                        break;

                    case MSG_PC_CONNECT:
                    {
                        if (mLinkPcTipsView != null)
                        {
                            mLinkPcTipsView.setVisibility(GONE);
                        }
                        break;
                    }

                    case MSG_PC_DISCONNECT:
                    {
                        if (mLinkPcTipsView != null)
                        {
                            mLinkPcTipsView.setVisibility(VISIBLE);
                        }
                        break;
                    }

                    case MSG_PC_CLICK_DECOR_TAB:
                    {
                        if (isShowStickerMgrPage())
                        {
                            showStickerMgrPage(false);
                            return;
                        }

                        if (!isShowStickerList())
                        {
                            showStickerList(true);
                        }
                        break;
                    }

                    case MSG_PC_CLICK_BEAUTY_TAB:
                    {
                        if (isShowStickerMgrPage())
                        {
                            showStickerMgrPage(false);
                        }

                        boolean delay = false;
                        if (!isShowBeautySelector())
                        {
                            showBeautySelector(true);
                            delay = true;
                        } else
                        {
                            if (isShowStickerList())
                            {
                                showStickerList(false);
                            }
                        }

                        // 选中美颜模块
                        this.postDelayed(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                if (mBeautySelectorView != null) mBeautySelectorView.setTab(TabUIConfig.TAB_TYPE.TAB_BEAUTY);
                            }
                        }, delay ? 300 : 0);
                        break;
                    }

                    case MSG_PC_CLICK_SHAPE_TAB:
                    {
                        if (isShowStickerMgrPage())
                        {
                            showStickerMgrPage(false);
                        }

                        boolean delay = false;
                        if (!isShowBeautySelector())
                        {
                            showBeautySelector(true);
                            delay = true;
                        }
                        else
                        {
                            if (isShowStickerList())
                            {
                                showStickerList(false);
                            }
                        }

                        // 选中脸型模块
                        this.postDelayed(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                if (mBeautySelectorView != null) mBeautySelectorView.setTab(TabUIConfig.TAB_TYPE.TAB_SHAPE);
                            }
                        }, delay ? 300 : 0);
                        break;
                    }

                    case MSG_PC_CLICK_FILTER_TAB:
                    {
                        if (isShowStickerMgrPage())
                        {
                            showStickerMgrPage(false);
                        }

                        boolean delay = false;
                        if (!isShowBeautySelector())
                        {
                            showBeautySelector(true);
                            delay = true;
                        }
                        else
                        {
                            if (isShowStickerList())
                            {
                                showStickerList(false);
                            }
                        }

                        // 选中滤镜模块
                        this.postDelayed(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                if (mBeautySelectorView != null) mBeautySelectorView.setTab(TabUIConfig.TAB_TYPE.TAB_FILTER);
                            }
                        }, delay ? 300 : 0);
                        break;
                    }

                    case MSG_PC_SELECTED_DECOR:
                    {
                        if (mStickerView != null)
                        {
                            mStickerView.onPCSelectedSticker(msg.arg1);
                        }
                        break;
                    }

                    case MSG_PC_UPDATE_BEAUTY:
                    {
                        if (mBeautySelectorView != null)
                        {
                            BeautyData data = mBeautySelectorView.updateBeautyData(msg.arg1, msg.arg2);
                            requestRendererBeauty(data);
                        }
                        break;
                    }

                    case MSG_PC_SELECTED_FILTER:
                    {
                        if (mBeautySelectorView != null)
                        {
                            mBeautySelectorView.setCameraFilterId(msg.arg1);
                        }
                        break;
                    }

                    case MSG_PC_SELECTED_SHAPE:
                    {
                        if (mBeautySelectorView != null) {
                            mBeautySelectorView.setCameraShapeId(msg.arg1);
                        }
                        break;
                    }

                    case MSG_PC_RESET_SHAPE_DATA:
                    {
                        if (mBeautySelectorView != null) {
                            mBeautySelectorView.resetShapeDataByShapeId(msg.arg1);
                        }
                        break;
                    }

                    case MSG_PC_SHAPE_LAYOUT_OPEN:
                    {
                        int shapeId = msg.arg1;
                        boolean open = msg.arg2 == 1;
                        boolean fromUser = (boolean) msg.obj;//判断是否是app用户发起
                        if (mBeautySelectorView != null) {
                            mBeautySelectorView.setCameraShapeSubOpen(shapeId, open);
                        }
                        break;
                    }

                    case MSG_PC_UPDATE_SHAPE:
                    {
                        int type = msg.arg1;
                        int shapeId = msg.arg2;
                        int uiProgress = (int) msg.obj;
                        if (mBeautySelectorView != null) {
                            ShapeData data = mBeautySelectorView.updateShapeData(type, shapeId, uiProgress);
                            requestRendererShape(data);
                        }
                        break;
                    }

                    case MSG_SHOW_ACTION_TIPS:
                    {
                        //素材action提示
                        final String finalActionTip = FaceAction.getActionTips(getContext(), mActionName);
                        if (finalActionTip != null)
                        {
                            showActionMsgToast(finalActionTip);
                        }
                        break;
                    }
                    case MSG_DETECT_FACE_FINISH:{
                        RectF firstFace = null;
                        if(msg.obj != null)
                        {
                            firstFace = (RectF)msg.obj;
                        }
                        //跟随人脸对焦，有人脸时每隔5s对一次焦
                        if(mDoFocusWithFace == 1 && firstFace == null)
                        {
                            mDoFocusWithFace = 0;
                        }
                        else if(mDoFocusWithFace == 0 && firstFace != null && !mLongPressFocus)
                        {
                            mDoFocusWithFace = 1;
                            //sendEmptyMessageDelayed(MSG_DO_FOCUS_WITH_FACE, 5000);
                            float x = firstFace.right;
                            float y = firstFace.bottom;
                            if(mCurrentRatio > CameraConfig.PreviewRatio.Ratio_16_9)
                            {
                                //计算镜头画面的实际位置
                                float r = CameraConfig.PreviewRatio.Ratio_16_9 / mCurrentRatio;
                                x = (mCameraViewWidth * (1.0f - r)) / 2.0f + x * r;
                            }
                            doFocusAndMetering(false, false, x, y, x, y);
                        }
                        break;
                    }
                    case MSG_DO_FOCUS_WITH_FACE:
                    {
                        mDoFocusWithFace = 0;
                        break;
                    }
                    case MSG_CLEAR_FOCUS_AND_METERING: // 取消对焦、测光
                    {
                        if (mFocusView != null && !mLongPressFocus)
                        {
                            mFocusView.showFocus(false);
                        }
                        break;
                    }
                    case MSG_OPEN_OR_CLOSE_AUTO_FOCUS:
                    {
                        boolean autoFocus = (Boolean)msg.obj;
                        setCameraFocusState(autoFocus);
                        break;
                    }
                    case MSG_CAMERA_ERROR:
                    {
                        showCameraPermissionHelper(true);
                        break;
                    }
                    case MSG_RESET_GESTURE_STATE:
                        if(mGestureListener != null)
                        {
                            mGestureListener.resetState();
                        }
                        break;
                    case MSG_DO_FOCUS_AND_METERING: // 对焦、测光
                    {
                        RectF rectF = (RectF)msg.obj;
                        if(rectF != null)
                        {
                            mLongPressFocus = false;
                            doFocusAndMetering(true, false, rectF.left, rectF.top, rectF.right, rectF.bottom);
                            sendEmptyMessageDelayed(MSG_CLEAR_FOCUS_AND_METERING, 5000);
                        }
                        if(mCurrentExposureValue != 0)
                        {
                            if(mLightnessSeekBar != null)
                            {
                                mLightnessSeekBar.setValue(0);
                            }
                            adjustCameraBrightness1(0);//手动对焦重置曝光值
                        }
                        break;
                    }
                    case MSG_LONG_PRESS_DO_FOCUS_AND_METERING:// 长按对焦、测光
                    {
                        if (!mLongPressFocus) {
                            setCameraFocusState(false);
                        }
                        mLongPressFocus = true;
                        RectF rectF = (RectF)msg.obj;
                        if(rectF != null)
                        {
                            doFocusAndMetering(true, true, rectF.left, rectF.top, rectF.right, rectF.bottom);
                        }
                        if(mCurrentExposureValue != 0)
                        {
                            if(mLightnessSeekBar != null)
                            {
                                mLightnessSeekBar.setValue(0);
                            }
                            adjustCameraBrightness1(0);//对焦重置曝光值
                        }
                        break;
                    }
                    case MSG_SHOW_LIGHTNESS_SEEK_BAR:
                    {
                        boolean hide = (boolean)msg.obj;
                        mLightnessSeekBar.setVisibility(VISIBLE);
                        if(hide)
                        {
                            mUIHandler.sendMessageDelayed(Message.obtain(mUIHandler, MSG_HIDE_LIGHTNESS_SEEK_BAR), 3000);
                        }
                        break;
                    }
                    case MSG_HIDE_LIGHTNESS_SEEK_BAR:
                    {
                        mLightnessSeekBar.setVisibility(GONE);
                        break;
                    }
                    case MSG_GESTURE_CHANGE:
                    {
                        int direct = msg.arg1;
                        if(direct == 0)
                        {
                            MyBeautyStat.onClickByRes(R.string.直播助手_拍摄页_拍摄页_左划切换滤镜);
                            //滤镜手势切换
                            setCameraFilterRes(true);//下一个
                        }
                        else if(direct == 2)
                        {
                            MyBeautyStat.onClickByRes(R.string.直播助手_拍摄页_拍摄页_右滑切换滤镜);
                            setCameraFilterRes(false); //上一个
                        }
//                        else if (direct == 1)
//                        {
//                            adjustCameraBrightness(1);
//                        }
//                        else if (direct == 3)
//                        {
//                            adjustCameraBrightness(-1);
//                        }
                        break;
                    }
                    default:
                        break;
                }
            }
        };
    }

    private void showActionMsgToast(String text)
    {
        if (TextUtils.isEmpty(text)) return;

        float ratio = CameraConfig.PreviewRatio.Ratio_Full;

        if(mMsgToast != null)
        {
            if(ratio > CameraConfig.PreviewRatio.Ratio_16_9) {
                ratio = mFullScreenRatio;
            }
            MsgToastConfig config = mMsgToast.getConfig(MsgToastConfig.Key.STICKER_ACTION);

            if(config != null)
            {
                int y = (int)(RatioBgUtils.GetBottomHeightByRation(ratio) + ShareData.getScreenW() * ratio / 2f
                        - CameraPercentUtil.WidthPxToPercent(120) / 2f/*toast 背景图高度*/);

                if(ratio != CameraConfig.PreviewRatio.Ratio_16_9 &&  ratio != CameraConfig.PreviewRatio.Ratio_Full)
                {
                    if(mFullScreenRatio > CameraConfig.PreviewRatio.Ratio_16_9)
                    {
                        int mCameraViewHeight = (int)(ShareData.getScreenW() * CameraConfig.PreviewRatio.Ratio_16_9);

                        if(mCameraViewHeight > ShareData.m_screenRealHeight)
                        {
                            mCameraViewHeight = (int)(ShareData.getScreenW() * CameraConfig.PreviewRatio.Ratio_4_3);
                        }

                        int bottomPadding = 0;
                        if(mFullScreenRatio > CameraConfig.PreviewRatio.Ratio_17$25_9)
                        {
                            bottomPadding = ShareData.m_screenRealHeight - mCameraViewHeight - RatioBgUtils.getTopPaddingHeight(ratio);
                        }
                        else
                        {
                            bottomPadding = ShareData.m_screenRealHeight - mCameraViewHeight;
                        }

                        y -= bottomPadding;
                    }
                }

                config.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, y);
            }

            mMsgToast.show(MsgToastConfig.Key.STICKER_ACTION, text);
        }
    }

    private void showFilterMsgToast(String text)
    {
        if (TextUtils.isEmpty(text))return;

        if (mFilterMsgToast != null)
        {
            mFilterMsgToast.show(text);
        }
    }

    /**
     * 触发按钮发送神策统计
     */
    public final void sendCameraSensorsDataWhenClick()
    {
        MyBeautyStat.CameraBeautyData cameraBeautyData = new MyBeautyStat.CameraBeautyData();
        int shapeId = ShapeSPConfig.getInstance(getContext()).getShapeId();
        MyBeautyStat.AdvFaceType advFaceType = null;
        switch (shapeId) {
            case SuperShapeData.ID_ZIRANXIUSHI://自然修饰
                advFaceType = MyBeautyStat.AdvFaceType._自然修饰;
                break;
            case SuperShapeData.ID_BABIGONGZHU://芭比公主
                advFaceType = MyBeautyStat.AdvFaceType._芭比公主;
                break;
            case SuperShapeData.ID_JINGZHIWANGHONG://精致网红
                advFaceType = MyBeautyStat.AdvFaceType._精致网红;
                break;
            case SuperShapeData.ID_JIMENGSHAONV://激萌少女
                advFaceType = MyBeautyStat.AdvFaceType._激萌少女;
                break;
            case SuperShapeData.ID_MODENGNVWANG://摩登女王
                advFaceType = MyBeautyStat.AdvFaceType._摩登女王;
                break;
            case SuperShapeData.ID_DAIMENGTIANXIN://呆萌甜心
                advFaceType = MyBeautyStat.AdvFaceType._呆萌甜心;
                break;
            case SuperShapeData.ID_DUDUTONGYAN://嘟嘟童颜
                advFaceType = MyBeautyStat.AdvFaceType._嘟嘟童颜;
                break;
            case SuperShapeData.ID_XIAOLIANNVSHEN://小脸女神
                advFaceType = MyBeautyStat.AdvFaceType._小脸女神;
                break;
            case SuperShapeData.ID_MINE_SYNC://我的脸型
                advFaceType = MyBeautyStat.AdvFaceType._我的;
                break;
            case SuperShapeData.ID_NON_SHAPE://无脸型
                advFaceType = MyBeautyStat.AdvFaceType._无;
                break;
        }
        cameraBeautyData.lianxing = advFaceType;
        if (mFilterBeautyParams != null)
        {
            cameraBeautyData.meifu = (int) mFilterBeautyParams.getSkinBeautySize();//美肤数值
            cameraBeautyData.meiya = (int) mFilterBeautyParams.getWhitenTeethSize();//美牙数值
            cameraBeautyData.fuse = (int) mFilterBeautyParams.getSkinTypeSize();//肤色数值
            cameraBeautyData.shoulian = (int) mFilterBeautyParams.getThinFace();//瘦脸数值
            cameraBeautyData.xiaolian = (int) mFilterBeautyParams.getLittleFace();//小脸数值
            cameraBeautyData.xiaolian2 = (int) mFilterBeautyParams.getShavedFace();//削脸数值
            cameraBeautyData.etou = BeautyShapeDataUtils.GetBidirectionalUISize((int) mFilterBeautyParams.getForehead());//额头数值
            cameraBeautyData.quangu = (int) mFilterBeautyParams.getCheekbones();//颧骨数值
            cameraBeautyData.yanjiao = BeautyShapeDataUtils.GetBidirectionalUISize((int) mFilterBeautyParams.getCanthus());//眼角数值
            cameraBeautyData.yanju = BeautyShapeDataUtils.GetBidirectionalUISize((int) mFilterBeautyParams.getEyeSpan());//眼距数值
            cameraBeautyData.shoubi = (int) mFilterBeautyParams.getShrinkNose();//瘦鼻数值
            cameraBeautyData.biyi = BeautyShapeDataUtils.GetBidirectionalUISize((int) mFilterBeautyParams.getNosewing());//鼻翼数值
            cameraBeautyData.bizigaodu = BeautyShapeDataUtils.GetBidirectionalUISize((int) mFilterBeautyParams.getNoseHeight());//鼻子高度数值
            cameraBeautyData.xiaba = BeautyShapeDataUtils.GetBidirectionalUISize((int) mFilterBeautyParams.getChin());//下巴数值
            cameraBeautyData.zuixing = BeautyShapeDataUtils.GetBidirectionalUISize((int) mFilterBeautyParams.getMouth());//嘴型数值
            cameraBeautyData.zuibagaodu = BeautyShapeDataUtils.GetBidirectionalUISize((int) mFilterBeautyParams.getOverallHeight());//嘴巴高度数值
            //大眼
            if (mFilterBeautyParams.getEye_type() == ShapeData.EYE_TYPE.OVAL_EYES) {
                cameraBeautyData.dayan = (int) mFilterBeautyParams.getOvalEye();
            } else if (mFilterBeautyParams.getEye_type() == ShapeData.EYE_TYPE.CIRCLE_EYES) {
                cameraBeautyData.dayan = (int) mFilterBeautyParams.getCircleEye();
            }
        }
        MyBeautyStat.onUseCameraBeauty(cameraBeautyData, true);
    }

    private void updateFilterBeautyParamsByBeautyData(FilterBeautyParams filterBeautyParams, BeautyData beautyData)
    {
        if (filterBeautyParams != null && beautyData != null)
        {
            FilterBeautyParams.SetBeautyData2Params(filterBeautyParams, beautyData);
        }
    }

    private void updateFilterBeautyParamsByShapeData(FilterBeautyParams filterBeautyParams, ShapeData shapeData)
    {
        if (filterBeautyParams != null && shapeData != null)
        {
            FilterBeautyParams.SetShapeData2Params(filterBeautyParams, shapeData);
        }
    }


    private void initCB()
    {
        mRatioBgListener = new RatioBgViewV2.OnRatioChangeListener()
        {
            @Override
            public void onRatioChange(float ratio)
            {

            }

            @Override
            public void onDismissMaskEnd()
            {
                mIsInitPage = false;
            }

            @Override
            public void onSplashMaskEnd()
            {

            }
        };

        mLightnessBarListener = new CameraLightnessSeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(CameraLightnessSeekBar seekBar)
            {
                adjustCameraBrightness1(seekBar.getValue());
            }

            @Override
            public void onStartTrackingTouch(CameraLightnessSeekBar seekBar)
            {
                adjustCameraBrightness1(seekBar.getValue());
                mUIHandler.removeMessages(MSG_HIDE_LIGHTNESS_SEEK_BAR);
            }

            @Override
            public void onStopTrackingTouch(CameraLightnessSeekBar seekBar)
            {
                adjustCameraBrightness1(seekBar.getValue());
                mUIHandler.sendMessageDelayed(Message.obtain(mUIHandler, MSG_HIDE_LIGHTNESS_SEEK_BAR), 3000);
            }
        };

        mAnimationClickListener = new OnAnimationClickListener()
        {
            @Override
            public void onAnimationClick(View v)
            {
                if (v == mBackBtn)
                {
                    onBack();
                }
                else if (v == mStickerBtn)
                {
                    showStickerList(true);
                    /*DUIConfig.getInstance().updateTabSelectStatusToPC(DUIConfig.TabType.TAB_DECO);*/
                }
                else if (v == mBeautyBtn)
                {
                    showBeautySelector(true);
                    /*if (mBeautySelectorView != null && mBeautySelectorView.getCurrentPageTabUI() != null) {
                        switch (mBeautySelectorView.getCurrentPageTabUI().m_type)
                        {
                            case TabUIConfig.TAB_TYPE.TAB_BEAUTY:
                                DUIConfig.getInstance().updateTabSelectStatusToPC(DUIConfig.TabType.TAB_BEAUTY);
                                break;
                            case TabUIConfig.TAB_TYPE.TAB_FILTER:
                                DUIConfig.getInstance().updateTabSelectStatusToPC(DUIConfig.TabType.TAB_FILTER);
                                break;
                            case TabUIConfig.TAB_TYPE.TAB_SHAPE:
                                DUIConfig.getInstance().updateTabSelectStatusToPC(DUIConfig.TabType.TAB_SHAPE);
                                break;
                            case TabUIConfig.TAB_TYPE.UNSET:
                                DUIConfig.getInstance().updateTabSelectStatusToPC(DUIConfig.TabType.UNSET);
                                break;
                        }
                    }*/
                }
                else if (v == mSwitchBtn)
                {
                    MyBeautyStat.onClickByRes(R.string.直播助手_拍摄页_拍摄页_前后置切换);
                    CameraHandler cameraHandler = RenderHelper.getCameraHandler();
                    if(cameraHandler != null && !mIsSwitchCamera){
                        mIsSwitchCamera = true;
                        mCurrentExposureValue = 0;
                        cameraHandler.switchCamera();
                        RenderHelper.sCameraIsChange = true;
                    }
                }
            }
        };

        mBeautySelectorCB = new PageCallbackAdapter()
        {
            @Override
            public void onLogin()
            {
                if (mSite != null) {
                    onPause();
                    mSite.uploadShapeLogin(getContext());
                }
            }

            @Override
            public void onBindPhone()
            {

            }

            @Override
            public void onPageSelected(int position, TabUIConfig.TabUI tabUI)
            {
                /*if (tabUI != null)
                {
                    switch (tabUI.m_type)
                    {
                        case TabUIConfig.TAB_TYPE.TAB_BEAUTY:
                            DUIConfig.getInstance().updateTabSelectStatusToPC(DUIConfig.TabType.TAB_BEAUTY);
                            break;
                        case TabUIConfig.TAB_TYPE.TAB_FILTER:
                            DUIConfig.getInstance().updateTabSelectStatusToPC(DUIConfig.TabType.TAB_FILTER);
                            break;
                        case TabUIConfig.TAB_TYPE.TAB_SHAPE:
                            DUIConfig.getInstance().updateTabSelectStatusToPC(DUIConfig.TabType.TAB_SHAPE);
                            break;
                        case TabUIConfig.TAB_TYPE.UNSET:
                            DUIConfig.getInstance().updateTabSelectStatusToPC(DUIConfig.TabType.UNSET);
                            break;
                    }
                }*/
            }

            @Override
            public void onShapeItemClick(@Nullable ShapeExAdapter.ShapeExItemInfo itemInfo, ShapeData data)
            {
                // 脸型数据选中
                requestRendererShape(data);
                updateFilterBeautyParamsByShapeData(mFilterBeautyParams, data);
                if (itemInfo != null)
                {
                    ShapeSPConfig.getInstance(getContext()).setShapeId(itemInfo.m_uri);
                    DUIConfig.getInstance().updateShapeStatusToPC(itemInfo.m_uri);
                    if (itemInfo.m_uri != SuperShapeData.ID_NON_SHAPE)
                    {
                        for (ShapeExAdapter.ShapeSubInfo m_sub : itemInfo.m_subs) {
                            DUIConfig.getInstance().updateShapeAdjustStatusToPC(m_sub.m_type, (int) itemInfo.getData(m_sub.m_type));
                        }
                    }

                }
            }

            @Override
            public void onShapeUpdate(int subPosition, @Nullable ShapeExAdapter.ShapeExItemInfo itemInfo, ShapeData data)
            {
                // 脸型数据更新
                requestRendererShape(data);
                updateFilterBeautyParamsByShapeData(mFilterBeautyParams, data);

                //通知pc更新
                if (itemInfo != null && itemInfo.m_subs != null) {
                    if (subPosition > -1) {
                        ShapeExAdapter.ShapeSubInfo shapeSubInfo = itemInfo.m_subs.get(subPosition);
                        DUIConfig.getInstance().updateShapeAdjustStatusToPC(shapeSubInfo.m_type, (int) itemInfo.getData(shapeSubInfo.m_type));
                    } else {
                        for (ShapeExAdapter.ShapeSubInfo m_sub : itemInfo.m_subs) {
                            DUIConfig.getInstance().updateShapeAdjustStatusToPC(m_sub.m_type, (int) itemInfo.getData(m_sub.m_type));
                        }
                    }
                }
            }

            @Override
            public void onSubLayoutOpen(boolean isOpen,
                                        boolean showSeekBar,
                                        int position,
                                        ShapeExAdapter.ShapeExItemInfo itemInfo)
            {
                //TODO 脸型打开二级菜单选项
            }

            @Override
            public void onBeautyUpdate(@STag.BeautyTag int type, BeautyData beautyData)
            {
                // 美颜数据更新
                requestRendererBeauty(beautyData);
                updateFilterBeautyParamsByBeautyData(mFilterBeautyParams, beautyData);

                //通知pc更新
                int uiprogress = 0;
                switch (type)
                {
                    case STag.BeautyTag.SKINBEAUTY://美肤
                    {
                        uiprogress = (int) beautyData.getSkinBeauty();
                        break;
                    }
                    case STag.BeautyTag.WHITENTEETH:
                    {
                        uiprogress = (int) beautyData.getWhitenTeeth();
                        break;
                    }
                    case STag.BeautyTag.SKINTYPE:
                    {
                        uiprogress = (int) beautyData.getSkinType();
                        break;
                    }
                }
                DUIConfig.getInstance().updateBeautySlideToPC(type, uiprogress);
            }

            @Override
            public void onFilterItemClick(FilterRes filterRes, boolean showToast)
            {
                if(filterRes != null)
                {
                    setColorFilter(filterRes);
                    if (showToast)
                    {
                        showFilterMsgToast(filterRes.m_id == 0 ? getContext().getString(R.string.filter_type_none) : filterRes.m_name);
                    }
                    //通知pc更新
                    DUIConfig.getInstance().updateFilterStatusToPC(filterRes.m_id);

                    if (mCurrentFilterRes == null) {
                        isClickFilterSensor = false;
                    }

                    //切换滤镜不需要统计id,
                    if (isClickFilterSensor) {
                        if (filterRes.m_id == 0) {
                            MyBeautyStat.onClickByRes(R.string.直播助手_美颜页_滤镜选择_原图);
                        }else {
                            MyBeautyStat.onClickByRes(R.string.直播助手_美颜页_滤镜选择_选择具体滤镜);
                        }
                    }
                }
                setUsedStickerFilter(false);
                mCurrentFilterRes = filterRes;
                isClickFilterSensor = true;
            }

            @Override
            public void onFilterUpdateAdd(FilterRes filterRes, int filter_id)
            {
                //通知pc有新的滤镜添加
                DUIConfig.getInstance().insertFilterToPC(filterRes, filter_id);
            }

            @Override
            public void onFilterUpdateRemove(ArrayList<Integer> ids)
            {
                //通知pc有滤镜删除
                DUIConfig.getInstance().deleteFilterToPC(ids);
            }

            @Override
            public void onFilterItemDownload()
            {
                MyBeautyStat.onClickByRes(R.string.直播助手_美颜页_滤镜选择_滤镜下载更多);
                onPause();
                if (mSite != null)
                {
                    mSite.onFilterDownload(getContext());
                }
            }

            @Override
            public void onFilterItemRecommend(ArrayList<RecommendRes> ress)
            {
                MyBeautyStat.onClickByRes(R.string.直播助手_美颜页_滤镜选择_滤镜推荐位);
                openRecommendView(ress, ResType.FILTER.GetValue());
            }

            @Override
            public FilterRes getCameraFilterRes()
            {
                return mCurrentFilterRes;
            }

            @Override
            public void setShowSelectorView(boolean show)
            {
                showBeautySelector(show);
            }
        };

        mPCStatusListenerAdapter = new PCStatusListenerAdapter()
        {
            @Override
            public void onPCConnected()
            {
                if (mUIHandler != null)
                {
                    mUIHandler.sendEmptyMessage(MSG_PC_CONNECT);
                }
            }

            @Override
            public void onPCDisconnected()
            {
                if (mUIHandler != null)
                {
                    mUIHandler.sendEmptyMessage(MSG_PC_DISCONNECT);
                }
            }

            @Override
            public void onPCSelectedDecor(int id)
            {
                if (mUIHandler != null)
                {
                    mUIHandler.removeMessages(MSG_PC_SELECTED_DECOR);
                    Message msg = mUIHandler.obtainMessage();
                    msg.what = MSG_PC_SELECTED_DECOR;
                    msg.arg1 = id;
                    mUIHandler.sendMessageDelayed(msg, 250);
                }
            }

            @Override
            public void onPCClickDecorTab()
            {
                if (mUIHandler != null)
                {
                    // 确保暂停传输数据时，在pc端不停切换 贴纸、滤镜 列表，会多次发送信息的bug
                    clearPCClickTabMsg();
                    mUIHandler.sendEmptyMessageDelayed(MSG_PC_CLICK_DECOR_TAB, 250);
                }
            }

            @Override
            public void onPCClickBeautyTab()
            {
                if (mUIHandler != null)
                {
                    // 确保暂停传输数据时，在pc端不停切换 贴纸、滤镜 列表，会多次发送信息的bug
                    clearPCClickTabMsg();
                    mUIHandler.sendEmptyMessageDelayed(MSG_PC_CLICK_BEAUTY_TAB, 250);
                }
            }

            @Override
            public void onPCClickShapeTab()
            {
                if (mUIHandler != null)
                {
                    // 确保暂停传输数据时，在pc端不停切换 贴纸、滤镜 列表，会多次发送信息的bug
                    clearPCClickTabMsg();
                    mUIHandler.sendEmptyMessageDelayed(MSG_PC_CLICK_SHAPE_TAB, 250);
                }
            }

            @Override
            public void onPCClickFilterTab()
            {
                if (mUIHandler != null)
                {
                    // 确保暂停传输数据时，在pc端不停切换 贴纸、滤镜 列表，会多次发送信息的bug
                    clearPCClickTabMsg();
                    mUIHandler.sendEmptyMessageDelayed(MSG_PC_CLICK_FILTER_TAB, 250);
                }
            }

            @Override
            public void onPCSliderBeauty(int type, int progress)
            {
                //PC控制更新美颜拉杆
                if (mUIHandler != null) {
                    mUIHandler.obtainMessage(MSG_PC_UPDATE_BEAUTY, type, progress).sendToTarget();
                }
            }

            @Override
            public void onPCSelectedFilter(int filterId)
            {
                //PC控制选中滤镜
                if (mUIHandler != null) {
                    mUIHandler.removeMessages(MSG_PC_SELECTED_FILTER);
                    Message msg = mUIHandler.obtainMessage();
                    msg.what = MSG_PC_SELECTED_FILTER;
                    msg.arg1 = filterId;
                    msg.arg2 = 0;
                    mUIHandler.sendMessageDelayed(msg, 250);
                }
            }

            @Override
            public void onPCSelectedShape(int shapeId)
            {
                //PC控制选中脸型
                if (mUIHandler != null) {
                    mUIHandler.removeMessages(MSG_PC_SELECTED_SHAPE);
                    Message msg = mUIHandler.obtainMessage();
                    msg.what = MSG_PC_SELECTED_SHAPE;
                    msg.arg1 = shapeId;
                    msg.arg2 = 0;
                    mUIHandler.sendMessageDelayed(msg, 250);
                }
            }

            @Override
            public void onPCResetShapeData(int shapeId)
            {
                //PC控制重置脸型数据
                if (mUIHandler != null) {
                    mUIHandler.obtainMessage(MSG_PC_RESET_SHAPE_DATA, shapeId, 0).sendToTarget();
                }
            }

            @Override
            public void onPCShapeSubLayoutOpen(int shapeId, boolean open)
            {
                //PC控制脸型二级选项展开收缩
                if (mUIHandler != null) {
                    mUIHandler.removeMessages(MSG_PC_SHAPE_LAYOUT_OPEN);
                    Message msg = mUIHandler.obtainMessage();
                    msg.what = MSG_PC_SHAPE_LAYOUT_OPEN;
                    msg.arg1 = shapeId;
                    msg.arg2 = open ? 1 : 0;
                    msg.obj = false;
                    mUIHandler.sendMessageDelayed(msg, 250);
                }
            }

            @Override
            public void onPCSlideShapeAdjust(int type, int shapeId, int uiProgress)
            {
                //PC控制脸型二级选项数据更新
                if (mUIHandler != null) {
                    mUIHandler.obtainMessage(MSG_PC_UPDATE_SHAPE, type, shapeId, uiProgress).sendToTarget();
                }
            }

        };

        if (mDownloadListener == null)
        {
            mDownloadListener = new AbsDownloadMgr.DownloadListener()
            {
                @Override
                public void OnDataChange(int resType, int downloadId, IDownload[] resArr)
                {
                    if(resType == ResType.FILTER.GetValue())
                    {
                        //TODO 滤镜下载完成后，更新数据
                        if (mBeautySelectorView != null) {
                        }
                    }
                }
            };
        }

        if(mRecomCallback == null)
        {
            mRecomCallback = new RecomDisplayMgr.ExCallback()
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
                    //退出微信时会调用一次，关闭dialog会调用一次
                    mRecomMgrViewCloseCount++;
                    if(mRecomMgrViewCloseCount == 2)
                    {
                        mSystemUiVisibility = -1;
                        changeSystemUiVisibility(View.GONE);
                    }
                }

                @Override
                public void OnLogin()
                {
                    //TODO 登录
                }

                @Override
                public void onWXCancel()
                {
                    //退出微信时会调用一次，关闭dialog会调用一次
                    mRecomMgrViewCloseCount++;
                    if(mRecomMgrViewCloseCount == 2)
                    {
                        mSystemUiVisibility = -1;
                        changeSystemUiVisibility(View.GONE);
                    }
                }
            };
        }

         mEventListener = new EventCenter.OnEventListener() {

             @Override
             public void onEvent(int eventId, Object[] params) {
                 if (eventId == EventID.HOMEPAGE_UPDATE_MENU_AVATAR) {
                     boolean isUserLogin = UserMgr.IsLogin(getContext(), null);
                     if (!isUserLogin) {
                         if (mUIHandler != null) {
                             mUIHandler.post(new Runnable() {
                                 @Override
                                 public void run() {
                                     onBack(true);
                                 }
                             });
                         }
                     }
                 }
             }
         };
        EventCenter.addListener(mEventListener);
    }

    private void clearPCClickTabMsg()
    {
        mUIHandler.removeMessages(MSG_PC_CLICK_DECOR_TAB);
        mUIHandler.removeMessages(MSG_PC_CLICK_BEAUTY_TAB);
        mUIHandler.removeMessages(MSG_PC_CLICK_SHAPE_TAB);
        mUIHandler.removeMessages(MSG_PC_CLICK_FILTER_TAB);
    }

    private boolean isShowLightnessBar()
    {
        return mLightnessSeekBar != null && mLightnessSeekBar.getVisibility() == VISIBLE;
    }

    private boolean isShowStickerList()
    {
        return mStickerView != null && mStickerView.getTranslationY() == 0;
    }

    private boolean isShowStickerMgrPage()
    {
        return mStickerMgrPage != null && mStickerMgrPage.getTranslationY() == 0;
    }

    private void showStickerList(boolean show)
    {
        if (mDoingTransYAnim) return;
        int dy = CameraPercentUtil.WidthPxToPercent(600);
        mDoingTransYAnim = true;
        if (show)
        {
            MyBeautyStat.onClickByRes(R.string.直播助手_拍摄页_拍摄页_选择贴纸);
            MyBeautyStat.onPageStartByRes(R.string.直播助手_贴纸页_主页面);
            ObjectAnimator animator3 = ObjectAnimator.ofFloat(mStickerView, "translationY", dy, 0);
            AnimatorSet set = new AnimatorSet();
            set.addListener(new AnimatorListenerAdapter()
            {
                @Override
                public void onAnimationEnd(Animator animation)
                {
                    super.onAnimationEnd(animation);
                    mDoingTransYAnim = false;
                }
            });
            if (isShowBeautySelector())
            {
                ObjectAnimator animator4 = ObjectAnimator.ofFloat(mBeautySelectorView, "translationY", 0,mBeautySelectorTranslationY);
                set.playTogether(animator3, animator4);
            }
            else
            {
                ObjectAnimator animator = ObjectAnimator.ofFloat(mStickerBtn, "translationY", dy);
                ObjectAnimator animator1 = ObjectAnimator.ofFloat(mBackBtn, "translationY", dy);
                ObjectAnimator animator2 = ObjectAnimator.ofFloat(mBeautyBtn, "translationY", dy);
                set.playTogether(animator, animator1, animator2, animator3);
            }
            set.setDuration(350);
            set.start();
        }
        else
        {
            MyBeautyStat.onClickByRes(R.string.直播助手_贴纸页_贴纸页_收起);
            MyBeautyStat.onPageEndByRes(R.string.直播助手_贴纸页_主页面);
            ObjectAnimator animator = ObjectAnimator.ofFloat(mStickerBtn, "translationY", dy, 0);
            ObjectAnimator animator1 = ObjectAnimator.ofFloat(mBackBtn, "translationY", dy, 0);
            ObjectAnimator animator2 = ObjectAnimator.ofFloat(mBeautyBtn, "translationY", dy, 0);
            ObjectAnimator animator3 = ObjectAnimator.ofFloat(mStickerView, "translationY", dy);
            AnimatorSet set = new AnimatorSet();
            set.addListener(new AnimatorListenerAdapter()
            {
                @Override
                public void onAnimationEnd(Animator animation)
                {
                    super.onAnimationEnd(animation);
                    mDoingTransYAnim = false;
                }
            });
            set.playTogether(animator, animator1, animator2, animator3);
            set.setDuration(350);
            set.start();
        }
    }

    private boolean isShowBeautySelector()
    {
        return mBeautySelectorView != null && mBeautySelectorView.getTranslationY() == 0;
    }

    private void showBeautySelector(boolean show)
    {
        if ((show && isShowBeautySelector()) || isDoingBeautySelectorAnim)
        {
            return;
        }

        int dy = mBeautySelectorTranslationY;
        if (show)
        {
            MyBeautyStat.onClickByRes(R.string.直播助手_拍摄页_拍摄页_打开美形);
            mBeautySelectorView.sendSensorPageTypeData(mBeautySelectorView.getPageType(), mBeautySelectorView.getCurrentPageTabUI(), true);
            ObjectAnimator animator = ObjectAnimator.ofFloat(mStickerBtn, "translationY", 0, dy);
            ObjectAnimator animator1 = ObjectAnimator.ofFloat(mBackBtn, "translationY", 0, dy);
            ObjectAnimator animator2 = ObjectAnimator.ofFloat(mBeautyBtn, "translationY", 0, dy);
            ObjectAnimator animator3 = ObjectAnimator.ofFloat(mBeautySelectorView, "translationY", dy, 0);
            AnimatorSet set = new AnimatorSet();
            set.addListener(new AnimatorListenerAdapter()
            {
                @Override
                public void onAnimationEnd(Animator animation)
                {
                    super.onAnimationEnd(animation);
                    isDoingBeautySelectorAnim = false;
                }

                @Override
                public void onAnimationStart(Animator animation)
                {
                    isDoingBeautySelectorAnim = true;
                    onShowBeautySelectorView();
                }
            });
            if (isShowStickerList())
            {
                ObjectAnimator animator4 = ObjectAnimator.ofFloat(mStickerView, "translationY", CameraPercentUtil.WidthPxToPercent(600));
                if (isShowStickerMgrPage())
                {
                    ObjectAnimator animator5 = ObjectAnimator.ofFloat(mStickerMgrPage, "translationY", ShareData.m_screenRealHeight);
                    set.playTogether(animator3, animator4, animator5);
                    set.addListener(new AnimatorListenerAdapter()
                    {
                        @Override
                        public void onAnimationEnd(Animator animation)
                        {
                            mUIHandler.postDelayed(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    if(mStickerMgrPage != null)
                                    {
                                        mStickerMgrPage.ClearAll();
                                    }
                                    removeView(mStickerMgrPage);
                                    mStickerMgrPage = null;
                                }
                            }, 100);
                        }
                    });
                }
                else
                {
                    set.playTogether(animator, animator1, animator2, animator3, animator4);
                }
            }
            else
            {
                set.playTogether(animator, animator1, animator2, animator3);
            }
            set.setDuration(350);
            set.start();
        }
        else
        {
            MyBeautyStat.onClickByRes(R.string.直播助手_美颜页_通用_收起调整菜单);
            mBeautySelectorView.sendSensorPageTypeData(mBeautySelectorView.getPageType(), mBeautySelectorView.getCurrentPageTabUI(), false);
            ObjectAnimator animator = ObjectAnimator.ofFloat(mStickerBtn, "translationY", dy, 0);
            ObjectAnimator animator1 = ObjectAnimator.ofFloat(mBackBtn, "translationY", dy, 0);
            ObjectAnimator animator2 = ObjectAnimator.ofFloat(mBeautyBtn, "translationY", dy, 0);
            ObjectAnimator animator3 = ObjectAnimator.ofFloat(mBeautySelectorView, "translationY", 0,dy);
            AnimatorSet set = new AnimatorSet();
            set.addListener(new AnimatorListenerAdapter()
            {
                @Override
                public void onAnimationEnd(Animator animation)
                {
                    super.onAnimationEnd(animation);
                    isDoingBeautySelectorAnim = false;
                }

                @Override
                public void onAnimationStart(Animator animation)
                {
                    isDoingBeautySelectorAnim = true;
                    onCloseBeautySelectorView();
                }
            });
            set.playTogether(animator, animator1, animator2, animator3);
            set.setDuration(350);
            set.start();
        }
        //EventID.HOMEPAGE_UPDATE_MENU_AVATAR
    }

    private void initGesture(Context context)
    {
        mGestureListener = new GestureListener();
        mGestureDetector = new GestureDetector(context, mGestureListener);
    }

    private void initView(Context context)
    {
        this.setBackgroundColor(Color.WHITE);

        mCameraViewRatio = mFullScreenRatio;
        mCameraViewWidth = ShareData.m_screenRealWidth;
        mCameraViewHeight = ShareData.m_screenRealHeight;
        mCurrentRatio = mCameraViewRatio;

        mPreviewView = new CameraRenderView(context);
        FrameLayout.LayoutParams params = new LayoutParams(mCameraViewWidth, mCameraViewHeight);
        addView(mPreviewView, params);

        mLightnessSeekBar = new CameraLightnessSeekBar(getContext());
        mLightnessSeekBar.setVisibility(GONE);
        mLightnessSeekBar.setOnSeekBarChangeListener(mLightnessBarListener);
        params = new LayoutParams(CameraPercentUtil.HeightPxToPercent(200), CameraPercentUtil.HeightPxToPercent(288));
        params.gravity = Gravity.END | Gravity.CENTER_VERTICAL;
        addView(mLightnessSeekBar, params);

        mLinkPcTipsView = new TextView(context);
//        mLinkPcTipsView.setVisibility(GONE);
        mLinkTipsHeight = CameraPercentUtil.WidthPxToPercent(78);
        mLinkTipTopMargin = CameraPercentUtil.WidthPxToPercent(127);
        mLinkPcTipsView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        mLinkPcTipsView.setTextColor(ColorUtils.setAlphaComponent(Color.BLACK, (int) (255 * 0.9f)));
        mLinkPcTipsView.setText(R.string.live_connect_tips);
        mLinkPcTipsView.setGravity(Gravity.CENTER);
        RoundRectDrawable bk = new RoundRectDrawable();
        bk.setColor(ColorUtils.setAlphaComponent(Color.WHITE, (int) (255 * 0.96f)));
        bk.setRoundRectParams(CameraPercentUtil.WidthPxToPercent(50), CameraPercentUtil.WidthPxToPercent(50));
        mLinkPcTipsView.setBackgroundDrawable(bk);
        params = new LayoutParams(CameraPercentUtil.WidthPxToPercent(456), mLinkTipsHeight);
        params.gravity = Gravity.CENTER_HORIZONTAL;
        params.topMargin = mLinkTipTopMargin;
        addView(mLinkPcTipsView, params);

        mAutoLockTipsView = new TextView(context);
        mAutoLockTipsView.setVisibility(GONE);
        mAutoTipsHeight = CameraPercentUtil.WidthPxToPercent(42);
        mAutoTipsTopMargin = CameraPercentUtil.WidthPxToPercent(30);
        mAutoLockTipsView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        mAutoLockTipsView.setTextColor(Color.BLACK);
        mAutoLockTipsView.setText(R.string.live_auto_focus_tips);
        mAutoLockTipsView.setPadding(CameraPercentUtil.WidthPxToPercent(18), 0, CameraPercentUtil.WidthPxToPercent(18), 0);
        mLinkPcTipsView.setGravity(Gravity.CENTER);
        bk = new RoundRectDrawable();
        bk.setColor(0xffffcc00);
        bk.setRoundRectParams(CameraPercentUtil.WidthPxToPercent(3), CameraPercentUtil.WidthPxToPercent(3));
        mAutoLockTipsView.setBackgroundDrawable(bk);
        params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, mAutoTipsHeight);
        params.gravity = Gravity.CENTER_HORIZONTAL;
        params.topMargin = mAutoTipsTopMargin;
        addView(mAutoLockTipsView, params);

        mFocusView = new FocusView(context);
        mFocusView.setCircleParams(CameraPercentUtil.WidthPxToPercent(110) /2f, CameraPercentUtil.WidthPxToPercent(2), 0xfff8f09a);
        params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        addView(mFocusView, params);

        mRatioBgView = new RatioBgViewV2(context);
        mRatioBgView.setRatio(mFullScreenRatio);
        mRatioBgView.SetOnRatioChangeListener(mRatioBgListener);
        params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        addView(mRatioBgView, params);

        mBackBtn = new PressedButton(context);
        mBackBtn.setClickable(true);
        mBackBtn.setOnTouchListener(mAnimationClickListener);
        mBackBtn.setButtonImage(R.drawable.camera_16_9_back, R.drawable.camera_16_9_back);
        params = new LayoutParams(CameraPercentUtil.WidthPxToPercent(70), CameraPercentUtil.WidthPxToPercent(70));
        params.gravity = Gravity.BOTTOM;
        params.leftMargin = CameraPercentUtil.WidthPxToPercent(52);
        params.bottomMargin = CameraPercentUtil.WidthPxToPercent(82);
        addView(mBackBtn, params);

        mStickerBtnAnim = new StickerAnimDrawable(context);
        mStickerBtnAnim.setCurrentPreviewRatio(CameraConfig.PreviewRatio.Ratio_Full);
        mStickerBtnAnim.setBmpColor(Color.WHITE);

        mStickerBtn = new PressedButton(context);
        mStickerBtn.setClickable(true);
        mStickerBtn.setOnTouchListener(mAnimationClickListener);
        mStickerBtn.setBackgroundDrawable(mStickerBtnAnim);
        params = new LayoutParams(CameraPercentUtil.WidthPxToPercent(70), CameraPercentUtil.WidthPxToPercent(70));
        params.gravity = Gravity.END | Gravity.BOTTOM;
        params.rightMargin = CameraPercentUtil.WidthPxToPercent(52);
        params.bottomMargin = CameraPercentUtil.WidthPxToPercent(82);
        addView(mStickerBtn, params);

        mBeautyBtn = new PressedButton(context);
        mBeautyBtn.setClickable(true);
        mBeautyBtn.setOnTouchListener(mAnimationClickListener);
        mBeautyBtn.setButtonImage(R.drawable.live_beauty_logo, R.drawable.live_beauty_logo);
        params = new LayoutParams(CameraPercentUtil.WidthPxToPercent(70), CameraPercentUtil.WidthPxToPercent(70));
        params.gravity = Gravity.END | Gravity.BOTTOM;
        params.rightMargin = CameraPercentUtil.WidthPxToPercent(220);
        params.bottomMargin = CameraPercentUtil.WidthPxToPercent(82);
        addView(mBeautyBtn, params);

        mSwitchBtn = new PressedButton(context);
        mSwitchBtn.setOnTouchListener(mAnimationClickListener);
        mSwitchBtn.setButtonImage(R.drawable.camera_switch, R.drawable.camera_switch);
        params = new LayoutParams(CameraPercentUtil.WidthPxToPercent(50), CameraPercentUtil.WidthPxToPercent(50));
        params.gravity = Gravity.END;
        params.rightMargin = CameraPercentUtil.WidthPxToPercent(68);
        params.topMargin = CameraPercentUtil.WidthPxToPercent(20);
        addView(mSwitchBtn, params);

        mStickerView = new StickerView(context);
        mStickerView.setPageListener(this);
        mStickerView.setTranslationY(CameraPercentUtil.WidthPxToPercent(600));
        params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.BOTTOM;
        addView(mStickerView, params);

        TabUIConfig.Builder builder = new TabUIConfig.Builder();
        builder.addTabType(TabUIConfig.TAB_TYPE_ALL);
        builder.setPageType(TabUIConfig.PAGE_TYPE.PAGE_LIVE);
        builder.setViewType(TabUIConfig.VIEW_TYPE.UNSET);
        mBeautySelectorTabUIConfig = builder.build(getContext());
        mBeautySelectorView = new BeautySelectorView(context, mBeautySelectorTabUIConfig, mBeautySelectorCB);
        mBeautySelectorView.setTranslationY(mBeautySelectorTranslationY);
        params = new LayoutParams(LayoutParams.MATCH_PARENT, mBeautySelectorTranslationY);
        params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
        this.addView(mBeautySelectorView, params);


        mMsgToast = new MsgToast();
        mMsgToast.setParent(this);

        mFilterMsgToast = new ColorFilterToast();
        mFilterMsgToast.setParent(this);

        // 贴纸动作
        MsgToastConfig config = new MsgToastConfig(MsgToastConfig.Key.STICKER_ACTION);
        config.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        config.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 22);
        config.setTextColor(0xffffffff);
        config.setShadow(3f, 2, 2, 0xa7000000);
        mMsgToast.addConfig(config);

        mPopFrameView = new FrameLayout(getContext());
        mPopFrameView.setBackgroundDrawable(null);
        mPopFrameView.setVisibility(View.GONE);
        params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        this.addView(mPopFrameView, params);
    }

    private void changeSystemUiVisibility(int visibility)
    {
        if(visibility != mSystemUiVisibility)
        {
            int vis = ShareData.showOrHideStatusAndNavigation(getContext(), visibility == View.VISIBLE, mOriginVisibility, visibility == View.VISIBLE);
            if(mOriginVisibility == -1 && visibility == View.GONE)
            {
                mOriginVisibility = vis;
            }
            mSystemUiVisibility = visibility;
        }
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility)
    {
        super.onWindowVisibilityChanged(visibility);
        changeSystemUiVisibility(visibility == View.GONE ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus)
    {
        super.onWindowFocusChanged(hasWindowFocus);
        if(hasWindowFocus)
        {
            mSystemUiVisibility = -1;
            changeSystemUiVisibility(View.GONE);
        }
    }

    @Override
    public void SetData(HashMap<String, Object> params)
    {
        openCameraById();
    }

    private void openCameraById()
    {
        if (mPreviewView == null) return;
        CameraHandler cameraHandler = RenderHelper.getCameraHandler();
        if (cameraHandler == null)
        {
            return;
        }
        mDoCameraOpenAnim = true;
        if (mCurrentRatio < CameraConfig.PreviewRatio.Ratio_16_9) {
            cameraHandler.setPreviewSize(mCameraViewWidth, (int)(mCameraViewWidth * CameraConfig.PreviewRatio.Ratio_4_3), mCameraSizeType);
        } else {
            cameraHandler.setPreviewSize(mCameraViewWidth, (int) (mCameraViewWidth * CameraConfig.PreviewRatio.Ratio_16_9), mCameraSizeType);
        }
        CameraThread cameraThread = cameraHandler.getCamera();
        if (cameraThread != null)
        {
            cameraThread.setCameraCallback(this);
            cameraThread.setCameraAllCallback(this);
        }
        mIsInitCamera = true;
        cameraHandler.openCamera(mCurrentCameraId);
    }

    private void initBeautyShapeDUIConfig()
    {
        if (mBeautySelectorView != null)
        {
            //美颜
            ArrayList<BeautyInfo> beautyInfos = mBeautySelectorView.getBeautyList(false);
            if (beautyInfos != null && beautyInfos.size() > 0)
            {
                BeautyInfo beautyInfo = beautyInfos.get(0);
                if (beautyInfo != null)
                {
                    BeautyData data = beautyInfo.getData();
                    DUIConfig.getInstance().updateBeautySlideToPC(STag.BeautyTag.SKINBEAUTY, (int) data.getSkinBeauty());
                    DUIConfig.getInstance().updateBeautySlideToPC(STag.BeautyTag.WHITENTEETH, (int) data.getWhitenTeeth());
                    DUIConfig.getInstance().updateBeautySlideToPC(STag.BeautyTag.SKINTYPE, (int) data.getSkinType());
                }
            }

            //脸型
            ArrayList<ShapeExAdapter.ShapeExItemInfo> shapeList = mBeautySelectorView.getShapeList(false);
            DUIConfig.getInstance().insertShapeToPC(shapeList);

            //滤镜
            ArrayList<FilterAdapter.ItemInfo> filterList = mBeautySelectorView.getFilterList(false);
            if (filterList != null && filterList.size() > 0)
            {
                for (FilterAdapter.ItemInfo itemInfo : filterList)
                {
                    if (itemInfo == null) continue;
                    if (itemInfo.m_uri == FilterAdapter.HeadItemInfo.HEAD_ITEM_URI
                            || itemInfo.m_uri == FilterAdapter.DownloadItemInfo.DOWNLOAD_ITEM_URI
                            || itemInfo.m_uri == FilterAdapter.RecommendItemInfo.RECOMMEND_ITEM_URI)
                    {
                        continue;
                    }

                    if (itemInfo.m_uri == FilterAdapter.OriginalItemInfo.ORIGINAL_ITEM_URI)//原图
                    {
                        DUIConfig.getInstance().insertFilterToPC((FilterRes) itemInfo.m_ex, cn.poco.advanced.ImageUtils.GetSkinColor());
                        continue;
                    }

                    if (itemInfo.m_uris != null
                            && itemInfo.m_uris.length > 1
                            && itemInfo.m_ex instanceof FilterGroupRes
                            && ((FilterGroupRes) itemInfo.m_ex).m_group != null)
                    {
                        FilterGroupRes group = (FilterGroupRes) itemInfo.m_ex;
                        for (FilterRes filterRes : group.m_group)
                        {
                            DUIConfig.getInstance().insertFilterToPC(filterRes, group.m_maskColor);
                        }
                    }
                }
            }
        }
    }

    private void setStickerDrawListener(final OnDrawStickerResListener listener)
    {
        if (mPreviewView != null)
        {
            mPreviewView.queueEvent(new RenderRunnable()
            {
                @Override
                public void run(RenderThread renderThread)
                {
                    if (renderThread != null && renderThread.getFilterManager() != null)
                    {
                        renderThread.getFilterManager().setOnDrawStickerResListener(listener);
                    }
                }
            });
        }
    }

    private boolean adjustCameraBrightness1(int value)
    {
        boolean result = false;
        CameraHandler cameraHandler = RenderHelper.getCameraHandler();
        CameraThread cameraThread = null;
        if(cameraHandler != null)
        {
            cameraThread = cameraHandler.getCamera();
        }
        if(cameraThread != null)
        {
            if(mCurrentExposureValue == 0)
            {
                mCurrentExposureValue = cameraThread.getExposureValue();
            }
            int exposureValue = value;
            if(exposureValue < cameraThread.getMinExposureValue())
            {
                exposureValue = cameraThread.getMinExposureValue();
            }
            else if(exposureValue > cameraThread.getMaxExposureValue())
            {
                exposureValue = cameraThread.getMaxExposureValue();
            }
            if(mCurrentExposureValue != exposureValue)
            {
                mCurrentExposureValue = exposureValue;
                cameraHandler.setExposureValue(mCurrentExposureValue);
                result = true;
            }
        }
        return result;
    }

    /**
     * 滤镜手势切换
     *
     * @param next 下一个
     */
    private void setCameraFilterRes(boolean next)
    {
        if(isShowBeautySelector())
        {
            showBeautySelector(false);
            return;
        }

        if (isShowStickerList())
        {
            showStickerList(false);
            return;
        }

        if (mBeautySelectorView != null)
        {
            mBeautySelectorView.setCameraFilterNext(next);
        }
    }

    private void setCameraFilterRes(int filterId) {
        if (mBeautySelectorView != null)
        {
            mBeautySelectorView.setCameraFilterId(filterId);
        }
    }

    /**
     * 取消滤镜列表的选择（无回调）
     */
    private void cancelCameraFilterSelect()
    {
        if(mBeautySelectorView != null) mBeautySelectorView.cancelFilterUri();
    }

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

    /**
     * 打开美颜脸型调节控件
     */
    private void onShowBeautySelectorView()
    {
        // 清贴纸素材
        // clearStickerWithShapeWhenBeautySetting();
    }

    /**
     * 关闭美颜脸型调节控件
     */
    private void onCloseBeautySelectorView()
    {
        // setStickerEnable(true, 0);
    }

    /**
     * 美形定制过程中，清除贴纸数据以及变形数据
     */
    private void clearStickerWithShapeWhenBeautySetting()
    {
        StickerMgr.getInstance().clearAllSelectedInfo();
        clearStickerWithShape();
        setStickerEnable(false, 0);
        setStickerFilter(null);
    }

    /**
     * 清除变形数据
     */
    private void clearStickerWithShape() {
        StickerDrawHelper.getInstance().setStickerRes(-1, null);
        mStickerDealWithShape = false;
        mShapeTypeId = 0;
    }


    private void setStickerEnable(final boolean enable, int delay)
    {
        if(mPreviewView != null)
        {
            mPreviewView.queueEvent(new RenderRunnable()
            {
                @Override
                public void run(RenderThread renderThread)
                {
                    if(renderThread != null && renderThread.getFilterManager() != null)
                    {
                        renderThread.getFilterManager().setFaceAdjustEnable(true/*enable ? (!mStickerDealWithShape && mShapeTypeId <= 0) : true*/);
                        renderThread.getFilterManager().setStickerEnable(enable);
                        renderThread.getFilterManager().setShapeEnable(false/*enable ? (!mStickerDealWithShape && mShapeTypeId > 0) : false*/);
                    }
                }
            }, delay);
        }
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

            mRecomMgrViewCloseCount = 0;
        }
    }



    /**
     * 贴纸触发监听
     */
    private OnDrawStickerResListener mOnDrawStickerResListener = new OnDrawStickerResListener()
    {
        @Override
        public void onPlayAnimMusic(final int state)
        {
        }

        @Override
        public void onPlayActionMusic(final String action)
        {
        }

        @Override
        public void onPlayActionAnimMusic(String action, int state)
        {
        }

        @Override
        public int getPlayState(int type)
        {
            return 0;
        }

        @Override
        public void onAnimStateChange(int state)
        {
        }

        @Override
        public void onAnimTrigger(int type)
        {
        }
    };

    private void setHasStickerSound(boolean has)
    {
        this.mHasStickerSound = has;
    }

    private void setStickerFilter(VideoStickerRes res)
    {
        // 滤镜处理
        if(res != null
                && res.mStickerRes != null
                && res.mStickerRes.mFilterRes != null)
        {
            mCurrentFilterRes = res.mStickerRes.mFilterRes;
            setColorFilter(mCurrentFilterRes);
            cancelCameraFilterSelect();
        }
        else if(!checkIsStickerFilter())
        {
            setUsedStickerFilter(false);
            setColorFilter(mCurrentFilterRes);
        }
        else
        {
            //使用原图滤镜
            setFilterMsgToastShow(false);
            setCameraFilterRes(0);
        }
    }


    /**
     * 更新脸型数据
     *
     * @param shapeData
     */
    private void requestRendererShape(final ShapeData shapeData)
    {
        if(mPreviewView != null)
        {
            mPreviewView.runOnGLThread(new RenderRunnable()
            {
                @Override
                public void run(RenderThread renderThread)
                {
                    if(renderThread != null && renderThread.getFilterManager() != null)
                    {
                        //脸型
                        renderThread.getFilterManager().setShapeData(shapeData);
                    }
                }
            });
        }
    }

    /**
     * 更新美颜数据
     *
     * @param beautyData
     */
    private void requestRendererBeauty(final BeautyData beautyData)
    {
        if(mPreviewView != null)
        {
            mPreviewView.runOnGLThread(new RenderRunnable()
            {
                @Override
                public void run(RenderThread renderThread)
                {
                    if(renderThread != null && renderThread.getFilterManager() != null)
                    {
                        //美颜
                        renderThread.getFilterManager().setBeautyData(beautyData);
                    }
                }
            });
        }
    }

    private void setColorFilter(final FilterRes filterRes)
    {
        if(mPreviewView != null)
        {
            mPreviewView.runOnGLThread(new RenderRunnable()
            {
                @Override
                public void run(RenderThread renderThread)
                {
                    if(renderThread != null && renderThread.getFilterManager() != null)
                    {
                        renderThread.getFilterManager().changeColorFilter(filterRes);
                    }
                }
            });
        }
    }

    private boolean checkIsStickerFilter()
    {
        return mCurrentFilterRes != null && mCurrentFilterRes.m_isStickerFilter;
    }

    private void setUsedStickerFilter(boolean isUsedStickerFilter)
    {
        if(mBeautySelectorView != null)
        {
            mBeautySelectorView.setUsedStickerFilter(isUsedStickerFilter);
        }
    }


    /**
     * 是否显示滤镜名称toast，在调用callback之前设置
     *
     * @param show
     */
    private void setFilterMsgToastShow(boolean show)
    {
        if (mBeautySelectorView != null)
        {
            mBeautySelectorView.setFilterMsgToastShow(show);
        }
    }

    @Override
    public void onCameraOpen() {
        final boolean finalIsSwitchCamera = mIsSwitchCamera;
        if (mIsSwitchCamera) isClickFilterSensor = false;
        mUIHandler.post(new Runnable()
        {
            @Override
            public void run()
            {
                if (mIsInitCamera) {
                    initBeautyShapeDUIConfig();
                }

                initWidgetStateAfterCameraOpen(finalIsSwitchCamera);
                initStickerData(mIsInitCamera);
                initShape(mIsInitCamera);
                initBeauty(mIsInitCamera);
                mIsInitCamera = false;

                initLiveEncoder();
                startOrStopLiveEncoder(true);
            }
        });
        if(mPreviewView != null)
        {
            mPreviewView.runOnGLThread(new RenderRunnable()
            {
                @Override
                public void run(RenderThread renderThread)
                {
                    renderThread.setRenderMode(1);
                    renderThread.setFrameTopPadding(mFrameTopPadding);
                    renderThread.setWaterMarkHasDate(false);
                    renderThread.setDetectFaceCallback(LivePage.this);
                    //renderThread.setOnCaptureFrameListener(CameraPageV3.this);

                    if (renderThread.getFilterManager() != null)
                    {
                        renderThread.getFilterManager().setBeautyEnable(true);
                        renderThread.getFilterManager().setStickerEnable(true);
                        //renderThread.getFilterManager().changeColorFilter(mCurrentFilterRes);
                        renderThread.getFilterManager().setRatioAndOrientation(1 / mCurrentRatio, mScreenOrientation, mFrameTopPadding);
                    }
                }
            });
        }
        mIsSwitchCamera = false;
        if (mLiveEncoderServer != null)
        {
            if (mLiveEncoderServer.isConnected())
            {
                if (mUIHandler != null) {
                    mUIHandler.sendEmptyMessage(MSG_PC_CONNECT);
                }
            } else {
                if (mUIHandler != null) {
                    mUIHandler.sendEmptyMessage(MSG_PC_DISCONNECT);
                }
            }
        }
    }

    private void initStickerData(boolean isInitCamera)
    {
        if (!isInitCamera) return;

        if (mStickerView != null)
        {
            mStickerView.onStartLoadData();
        }
    }

    private void initShape(boolean isInitCamera)
    {
        if (!isInitCamera) return;
        if (mBeautySelectorView != null) {
            mBeautySelectorView.setClickShapeSensor(false);
            mBeautySelectorView.setCameraShapeId(ShapeSPConfig.getInstance(getContext()).getShapeId());
        }
    }

    private void initBeauty(boolean isInitCamera)
    {
        if (!isInitCamera) return;

        ArrayList<BeautyInfo> beautyInfos = BeautyResMgr.getInstance().GetResArrByInfoFilter(getContext(), null);
        if (beautyInfos != null && beautyInfos.size() > 0) {
            BeautyData beautyData = beautyInfos.get(0).getData();
            requestRendererBeauty(beautyData);
            updateFilterBeautyParamsByBeautyData(mFilterBeautyParams, beautyData);
        }
    }

    @Override
    public void onCameraClose()
    {

    }

    /**
     * 打开镜头完成后，初始化参数
     */
    private void initWidgetStateAfterCameraOpen(boolean isSwitchCamera)
    {
        CameraHandler cameraHandler = RenderHelper.getCameraHandler();
        if(cameraHandler == null)
        {
            return;
        }
        //Log.i(TAG, "initWidgetStateAfterCameraOpen: ");
        CameraThread cameraThread = cameraHandler.getCamera();
        if(cameraThread != null)
        {
            mCurrentCameraId = cameraThread.getCurrentCameraId();
            isFront = cameraThread.isFront();

            if(mLightnessSeekBar != null)
            {
                mLightnessSeekBar.setMax(cameraThread.getMaxExposureValue());
                mLightnessSeekBar.setMin(cameraThread.getMinExposureValue());
            }
        }
        mLongPressFocus = false;
        if (mFocusView != null) {
            mFocusView.showFocus(false);
        }
        if (mAutoLockTipsView != null) {
            mAutoLockTipsView.setVisibility(GONE);
        }

        //Log.i(TAG, "mCurrentCameraId:" + mCurrentCameraId);
        CameraConfig.getInstance().saveData(CameraConfig.ConfigMap.LastCameraId, mCurrentCameraId);
        final int preDegree = getPreviewPatchDegree();
        //Log.i(TAG, "preDegree:"+preDegree);
        cameraHandler.setPreviewOrientation(preDegree);//通过外部获取预览修正角度;

        //镜头矫正判断
//        updatePatchConfig();
//        mCameraTopControl.updateUI();
//        boolean noSound = CameraConfig.getInstance().getBoolean(CameraConfig.ConfigMap.NoSound);
//        if(mCurrentTabType == ShutterConfig.TabType.PHOTO)
//        {
//            mCurrentFlashMode = CameraConfig.getInstance().getString(CameraConfig.ConfigMap.FlashModeStr);
//            mFaceGuideTakePicture = CameraConfig.getInstance().getBoolean(CameraConfig.ConfigMap.FaceGuideTakePicture);
//            if(noSound)
//            {
//                mFaceGuideTakePicture = false;
//            }
//        }
//        else
//        {
//            mCurrentFlashMode = CameraConfig.FlashMode.Off;
//        }
        if(cameraThread != null)
        {
//            if(mCameraPopSetting != null && isFront)
//            {
//                mCameraPopSetting.setShowFrontFlash(cameraThread.isFlashSupported());//vivo x9i支持前置闪光灯
//            }
//            cameraHandler.setFlashMode(mCurrentFlashMode);
        }

        if(mCurrentExposureValue != 0)
        {
            cameraHandler.setExposureValue(mCurrentExposureValue);
        }
        setCameraFocusState(true);
//        cameraHandler.setSilenceOnTaken(noSound);

        //触屏拍照
//        mTouchCapture = CameraConfig.getInstance().getBoolean(CameraConfig.ConfigMap.TouchCapture);

        if(mPreviewView != null)
        {
            mPreviewView.queueEvent(new RenderRunnable()
            {
                @Override
                public void run(RenderThread renderThread)
                {
                    if(renderThread != null && renderThread.getFilterManager() != null)
                    {
                        renderThread.getFilterManager().setPreviewDegree(preDegree, isFront);
                    }
                }
            });
        }

        //默认是原图滤镜
        int filterResId = 0;

        //镜头初始化打开
        if (mIsInitCamera)
        {
            filterResId = CameraConfig.getInstance().getInt(CameraConfig.ConfigMap.CameraFilterId);
            setFilterMsgToastShow(false);
            setCameraFilterRes(filterResId);
        }
        else
        {
            if (isSwitchCamera)
            {
                //切换镜头
                if (mCurrentFilterRes != null)
                {
                    if (checkIsStickerFilter())
                    {
                        setColorFilter(mCurrentFilterRes);
                    }
                    else
                    {
                        setFilterMsgToastShow(false);
                        setCameraFilterRes(mCurrentFilterRes.m_id);
                    }
                }
                else
                {
                    setCameraFilterRes(filterResId);
                }
            }
            else
            {
                //镜头打开
                if (mCurrentFilterRes != null)
                {
                    if (checkIsStickerFilter())
                    {
                        setColorFilter(mCurrentFilterRes);
                    }
                    else
                    {
                        setFilterMsgToastShow(false);
                        setCameraFilterRes(mCurrentFilterRes.m_id);
                    }
                }
                else
                {
                    setCameraFilterRes(filterResId);
                }
            }
        }

//
//        //屏幕亮度
//        if(mBrightnessUtils == null)
//        {
//            mBrightnessUtils = BrightnessUtils.getInstance();
//            mBrightnessUtils.setContext(getContext());
//            mBrightnessUtils.init();
//            mBrightnessUtils.registerBrightnessObserver();
//        }
//        if(mBrightnessUtils != null)
//        {
//            mBrightnessUtils.setBrightnessToMax();
//        }
        keepScreenWakeUp(true);
    }

    public int getPreviewPatchDegree()
    {
        if(mCurrentCameraId == 0)
        {
            if(!CameraConfig.getInstance().getBoolean(CameraConfig.ConfigMap.FixPreviewPatch_0))
            {
                int defaultDegree = CameraConfig.getInstance().getInt(CameraConfig.ConfigMap.PreviewPatch_0, true);
                int degree = CameraConfig.getInstance().getInt(CameraConfig.ConfigMap.PreviewPatch_0);
                if(defaultDegree == 0 && degree == 0)
                {
                    CameraConfig.getInstance().saveData(CameraConfig.ConfigMap.PreviewPatch_0, 90);
                    CameraConfig.getInstance().saveData(CameraConfig.ConfigMap.FixPreviewPatch_0, true);
                }
            }
            return CameraConfig.getInstance().getInt(CameraConfig.ConfigMap.PreviewPatch_0);
        }
        else if(mCurrentCameraId == 1)
        {
            if(!CameraConfig.getInstance().getBoolean(CameraConfig.ConfigMap.FixPreviewPatch_1))
            {
                int defaultDegree = CameraConfig.getInstance().getInt(CameraConfig.ConfigMap.PreviewPatch_1, true);
                int degree = CameraConfig.getInstance().getInt(CameraConfig.ConfigMap.PreviewPatch_1);
                if(defaultDegree == 0 && degree == 0)
                {
                    CameraConfig.getInstance().saveData(CameraConfig.ConfigMap.PreviewPatch_1, 90);
                    CameraConfig.getInstance().saveData(CameraConfig.ConfigMap.FixPreviewPatch_1, true);
                }
            }
            return CameraConfig.getInstance().getInt(CameraConfig.ConfigMap.PreviewPatch_1);
        }
        return 90;
    }

    private void keepScreenWakeUp(boolean wakeup)
    {
        if(wakeup && !mIsKeepScreenOn)
        {
            ((Activity)getContext()).getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            mIsKeepScreenOn = true;
        }
        else if(!wakeup && mIsKeepScreenOn)
        {
            ((Activity)getContext()).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            mIsKeepScreenOn = false;
        }
    }

    private void initLiveEncoder() {
        if (mLiveMediaVideoEncoder == null) {
            changeVideoSize(mCurrentRatio);
            mLiveMediaVideoEncoder = new MediaVideoEncoder(new MediaEncoder.MediaEncoderListener() {
                @Override
                public void onPrepared(final MediaEncoder encoder) {
                    if(mPreviewView != null) {
                        mPreviewView.runOnGLThread(new RenderRunnable() {
                            @Override
                            public void run(RenderThread renderThread) {
                                if(renderThread != null) {
                                    renderThread.setFrameTopPadding(mFrameTopPadding);
                                    renderThread.setDrawEndingEnable(false);
                                    renderThread.setRecordState(RecordState.START);
                                }
                            }
                        });
                    }
                }

                @Override
                public void onStarted(MediaEncoder encoder) {
                }

                @Override
                public void onResumed(MediaEncoder encoder) {
                }

                @Override
                public void onPaused(MediaEncoder encoder) {
                }

                @Override
                public void onStopped(MediaEncoder encoder) {
                }

                @Override
                public void onReleased(MediaEncoder encoder) {
                    if (encoder != null) {
                        encoder.stopRecording();
                        encoder.releaseMediaCodec(true);
                    }
                }

                @Override
                public void onError(MediaEncoder encoder, String msg) {

                }
            }, mVideoWidth, mVideoHeight);
        }
        if (mLiveMediaVideoEncoder != null && mLiveEncoderServer != null) {
            mLiveMediaVideoEncoder.setLiveEncoderServer(mLiveEncoderServer);
        }
    }

    private void startOrStopLiveEncoder(final boolean start) {
        if (mLiveMediaVideoEncoder != null) {
            if (start) {
                if (mLiveMediaVideoEncoder.isEncoding()) {
                    return;
                }
                if(mPreviewView != null) {
                    mPreviewView.runOnGLThread(new RenderRunnable() {
                        @Override
                        public void run(RenderThread renderThread) {
                            if(renderThread != null) {
                                renderThread.setMediaVideoEncoder(mLiveMediaVideoEncoder);
                                renderThread.setRecordState(RecordState.IDLE);
                                renderThread.setRecordState(RecordState.PREPARE);
                            }
                        }
                    });
                }

            } else {
                if (RenderHelper.sRenderThread != null) {
                    RenderHelper.sRenderThread.setDrawEndingEnable(false);
                    RenderHelper.sRenderThread.setRecordState(RecordState.STOP);
                }
                if (mLiveMediaVideoEncoder != null) {
                    mLiveMediaVideoEncoder.stopRecording();
                    mLiveMediaVideoEncoder = null;
                }
            }
        }
    }

    private void changeVideoSize(float ratio)
    {
        int width = ShareData.getScreenW();
        float videoScale = 1.0f;
        if(ratio == CameraConfig.PreviewRatio.Ratio_9_16)
        {//0.5625
            if(width > 1080)
            {
                videoScale = 0.75f;//1440x810 -> 1080x608
            }
            else if(width > 720)
            {
//                    videoScale = 0.66666f;//1080x607.5 -> 720x 405->400
//                    ratio = 0.55555f;
                videoScale = 0.68519f;//1080x607.5 -> 740x416
            }
            else if(width > 600)
            {
                videoScale = 0.75f;//720x405 -> 540x304
                if("1501_M02".equals(mModel))
                {//360 F4
                    videoScale = 0.75555f;//544
                    ratio = 0.55882f;
                }
            }
        }
        else if(ratio == CameraConfig.PreviewRatio.Ratio_1_1)
        {
            if(width > 1080)
            {//(1080, +]    1440x2560 2k屏
                videoScale = 0.88888f;//1440x1440 -> 1280x1280
            }
            else if(width > 720)
            {//(720, 1080]
                videoScale = 0.88888f;//1080x1080 -> 960x960
            }
            else if(width > 600)
            {//(600, 720]
                videoScale = 0.88888f;//720x720 -> 640x640
            }
        }
        else if(ratio == CameraConfig.PreviewRatio.Ratio_4_3)
        {
            if(width > 1080)
            {
                videoScale = 0.75f;//1440x1920 -> 1080x1440
            }
            else if(width > 720)
            {
                videoScale = 0.66666f;//1080x1440 -> 720x960
            }
            else if(width > 600)
            {
                videoScale = 0.66666f;//720x960 -> 480x640
            }
        }
        else if(ratio == CameraConfig.PreviewRatio.Ratio_16_9)
        {
            if(width > 1080)
            {
                videoScale = 0.5f;
            }
            else if(width > 720)
            {
                videoScale = 0.66666f;//1080x1920 -> 720x1280
            }
            else if(width > 600)
            {
                videoScale = 0.75f;//720x1280 -> 540x960

                if("1501_M02".equals(mModel))
                {//360 F4
                    videoScale = 0.75555f;//544
                    ratio = 1.76470f;
                }
            }
        }
        else
        {//full
            ratio = mFullScreenRatio;
            if(ratio == CameraConfig.PreviewRatio.Ratio_17_9)
            {
                if(width > 1080)
                {
                    videoScale = 0.5f;
                }
                else if(width > 720)
                {
                    videoScale = 0.66666f;
                }
            }
            else if(ratio == CameraConfig.PreviewRatio.Ratio_17$25_9)
            {
                if(width > 1080)
                {
                    videoScale = 0.52191f;
                }
                else if(width > 720)
                {
                    videoScale = 0.69588f;
                }
                else if(width > 600)
                {
                    videoScale = 0.8f;
                }
            }
            else if(ratio == CameraConfig.PreviewRatio.Ratio_18_9)
            {
                if(width > 1080)
                {
                    videoScale = 0.5f;
                }
                else if(width > 720)
                {
                    videoScale = 0.66666f;
                }
            }
            else if(ratio == CameraConfig.PreviewRatio.Ratio_18$5_9)
            {
                if(width > 1080)
                {
                    videoScale = 0.58920f;
                }
                else if(width > 720)
                {
                    videoScale = 0.78560f;
                }
                else if(width > 600)
                {
                    videoScale = 0.8f;
                }
            }
        }
        mVideoWidth = Math.round(width * videoScale);
        mVideoHeight = Math.round(mVideoWidth * ratio);
        //Log.i(TAG, "changeVideoSize: "+mVideoWidth+", "+mVideoHeight+", "+ratio);
    }

    @Override
    public void onScreenOrientationChanged(int orientation, int pictureDegree, float fromDegree, float toDegree)
    {
        mScreenOrientation = ((90 - pictureDegree) + 360) % 360 / 90;
        startRotationAnim((int) toDegree);
        updateFilterRatioAndOrientation();
    }

    private void updateFilterRatioAndOrientation()
    {
        if (mPreviewView != null)
        {
            mPreviewView.runOnGLThread(new RenderRunnable()
            {
                @Override
                public void run(RenderThread renderThread)
                {
                    if (renderThread != null && renderThread.getFilterManager() != null)
                    {
                        renderThread.getFilterManager().setRatioAndOrientation(1 / mCurrentRatio, mScreenOrientation, mFrameTopPadding);
                    }
                }
            });
        }
    }

    @Override
    public void onAutoFocus(boolean success, Camera camera)
    {
        if(success && mDoFocusWithFace == -1)
        {
            mDoFocusWithFace = 0;
            mUIHandler.sendMessage(mUIHandler.obtainMessage(MSG_OPEN_OR_CLOSE_AUTO_FOCUS, false));
        }
        mUIHandler.removeMessages(MSG_CLEAR_FOCUS_AND_METERING);
        mUIHandler.sendEmptyMessage(MSG_CLEAR_FOCUS_AND_METERING);
    }

    public void startRotationAnim(final int degree)
    {
        if(mDegree != degree) // 修复有默认滤镜，竖屏进入镜头页，文案从侧边出现的bug
        {
            int lastDegree = mDegree % 360;
            int nextDegree = (degree + 360) % 360;

            if (nextDegree == lastDegree)
            {
                mOrientationShiftType = OrientationShiftType.NULL;
                return;
            }

            mLastDegree = lastDegree;
            mDegree = nextDegree;

            if(mLastDegree == 270 && mDegree == 0)
            {
                mDegree = 360;
            }

            if(mLastDegree == 0 && mDegree == 270)
            {
                mLastDegree = 360;
            }

            if(!mDoingRotationAnim)
            {
                mDoingRotationAnim = true;
                createRotationAnim();
            }
        }
    }

    private void createRotationAnim()
    {
        mAnimTargetDegree = mDegree;

        if (Math.abs(mDegree - mLastDegree) == 90)
        {
            if((mLastDegree == 0 && mDegree == 90) || (mLastDegree == 360 && mDegree == 270) || (mLastDegree == 180 && mDegree == 90) || (mLastDegree == 180 && mDegree == 270))
            {
                mOrientationShiftType = OrientationShiftType.VERTICAL_TO_HORIZONTAL;// 竖屏 变 横屏
                isLandscape = true;
            }
            else if((mLastDegree == 90 && mDegree == 0) || (mLastDegree == 270 && mDegree == 360) || (mLastDegree == 90 && mDegree == 180) || (mLastDegree == 270 && mDegree == 180))
            {
                mOrientationShiftType = OrientationShiftType.HORIZONTAL_TO_VERTICAL;// 横屏 变 竖屏
                isLandscape = false;
            }
        }
        else if (Math.abs(mDegree - mLastDegree) == 180)
        {
            mOrientationShiftType = OrientationShiftType.MIRROR; // 镜像

            isLandscape = (mDegree == 90 || mDegree == 270);
        }

        // 适配 tips 位置
        final float link_start_X = mLinkPcTipsView.getTranslationX();
        final float link_start_Y = mLinkPcTipsView.getTranslationY();

        final float auto_start_X = mAutoLockTipsView.getTranslationX();
        final float auto_start_Y = mAutoLockTipsView.getTranslationY();

        float link_end_X = 0;
        float link_end_Y = 0;

        float auto_end_X = 0;
        float auto_end_Y = 0;

        int screenW = ShareData.m_screenRealWidth;
        int screenH = ShareData.m_screenRealHeight;
        int link_tips_landscape_dy = CameraPercentUtil.WidthPxToPercent(100);/* 横屏距边缘距离 */
        int auto_tips_landscape_dy = CameraPercentUtil.WidthPxToPercent(30);/* 横屏距边缘距离 */

        switch (mOrientationShiftType)
        {
            case OrientationShiftType.VERTICAL_TO_HORIZONTAL:
            {
                boolean tipsToRight = (mLastDegree == 0 && mDegree == 90 || mLastDegree == 180 && mDegree == 90);

                if (tipsToRight)// 竖屏 --> 横屏 tips 往右拐 (左右相对竖屏 tips 初始状态而言)
                {
                    link_end_X = screenW / 2f - link_tips_landscape_dy - mLinkTipsHeight / 2f;
                    link_end_Y = screenH / 2f - mLinkTipsHeight / 2f - mLinkTipTopMargin;

                    auto_end_X = screenW / 2f - auto_tips_landscape_dy - mAutoTipsHeight / 2f;
                    auto_end_Y = screenH / 2f - mAutoTipsHeight / 2f - mAutoTipsTopMargin;
                }
                else // 竖屏 --> 横屏 tips 往左拐
                {
                    link_end_X = -(screenW / 2f - link_tips_landscape_dy - mLinkTipsHeight / 2f);
                    link_end_Y = screenH / 2f - mLinkTipsHeight / 2f - mLinkTipTopMargin;

                    auto_end_X = -(screenW / 2f - auto_tips_landscape_dy - mAutoTipsHeight / 2f);
                    auto_end_Y = screenH / 2f - mAutoTipsHeight / 2f - mAutoTipsTopMargin;
                }
                break;
            }

            case OrientationShiftType.MIRROR:
            {
                // 特殊处理,例如: 平放桌面旋转 0-->90(做动画), 90-->180(没做动画), 180-->0(做动画)
                if (isLandscape)
                {
                    boolean tipsToRight = (mDegree == 90);

                    if (tipsToRight)// 横屏 tips 往右拐 (左右相对竖屏 tips 初始状态而言)
                    {
                        link_end_X = screenW / 2f - link_tips_landscape_dy - mLinkTipsHeight / 2f;
                        link_end_Y = screenH / 2f - mLinkTipsHeight / 2f - mLinkTipTopMargin;

                        auto_end_X = screenW / 2f - auto_tips_landscape_dy - mAutoTipsHeight / 2f;
                        auto_end_Y = screenH / 2f - mAutoTipsHeight / 2f - mAutoTipsTopMargin;
                    }
                    else // 横屏 tips 往左拐
                    {
                        link_end_X = -(screenW / 2f - link_tips_landscape_dy - mLinkTipsHeight / 2f);
                        link_end_Y = screenH / 2f - mLinkTipsHeight / 2f - mLinkTipTopMargin;

                        auto_end_X = -(screenW / 2f - auto_tips_landscape_dy - mAutoTipsHeight / 2f);
                        auto_end_Y = screenH / 2f - mAutoTipsHeight / 2f - mAutoTipsTopMargin;
                    }
                }
                break;
            }

            default:{}
        }

        final float final_link_end_X = link_end_X;
        final float final_link_end_Y = link_end_Y;

        final float final_auto_end_X = auto_end_X;
        final float final_auto_end_Y = auto_end_Y;

        final int mAnimLastDegree = mLastDegree;

        ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                float value = (float)animation.getAnimatedValue();
                int degree = (int)(mAnimLastDegree + (mAnimTargetDegree - mAnimLastDegree) * value);
                setBtnRotate(degree, value);

                float x = link_start_X + (final_link_end_X - link_start_X) * value;
                float y = link_start_Y + (final_link_end_Y - link_start_Y) * value;
                setLinkTipsLoc(x, y);

                x = auto_start_X + (final_auto_end_X - auto_start_X) * value;
                y = auto_start_Y + (final_auto_end_Y - auto_start_Y) * value;
                setAutoTipsLock(x, y);

                if (mFilterMsgToast != null)
                {
                    mFilterMsgToast.updateRotateAnimInfo(isLandscape, degree, value);
                }
            }
        });

        animator.addListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                if(mAnimTargetDegree != mDegree)
                {
                    createRotationAnim();
                }
                else
                {
                    mDoingRotationAnim = false;
                }
            }
        });
        animator.start();
    }

    private void setLinkTipsLoc(float x, float y)
    {
        if (mLinkPcTipsView != null)
        {
            mLinkPcTipsView.setTranslationX(x);
            mLinkPcTipsView.setTranslationY(y);
        }
    }

    private void setAutoTipsLock(float x, float y)
    {
        if (mAutoLockTipsView != null)
        {
            mAutoLockTipsView.setTranslationX(x);
            mAutoLockTipsView.setTranslationY(y);
        }
    }

    private void setBtnRotate(int degree, float value)
    {
        if (mBackBtn != null)
        {
            mBackBtn.setRotate(degree);
        }

        if (mStickerBtnAnim != null)
        {
            mStickerBtnAnim.setRotation(degree);
        }

        if (mBeautyBtn != null)
        {
            mBeautyBtn.setRotate(degree);
        }

        if (mSwitchBtn != null)
        {
            mSwitchBtn.setRotate(degree);
        }

        if (mLinkPcTipsView != null)
        {
            mLinkPcTipsView.setRotation(degree);
        }

        if (mAutoLockTipsView != null)
        {
            mAutoLockTipsView.setRotation(degree);
        }
    }

    private void setCameraFocusState(boolean autoFocus)
    {
        CameraHandler cameraHandler = RenderHelper.getCameraHandler();
        if(cameraHandler == null) return;
        cameraHandler.setAutoFocus(autoFocus);
        cameraHandler.setAutoLoopFocus(autoFocus);
    }

    private void doFocusAndMetering(boolean showRect, boolean longPress, float x1, float y1, float x2, float y2)
    {
        CameraHandler cameraHandler = RenderHelper.getCameraHandler();
        if(cameraHandler != null && cameraHandler.getCamera() != null && ((cameraHandler.getCamera().isFocusAreaSupported() && x1 > 0 && y1 > 0) || (cameraHandler.getCamera().isMeteringSupported() && x2 > 0 && y2 > 0)))
        {
            if(showRect)
            {
                float x = x1;
                float y = y1;
                if(mCurrentRatio <= CameraConfig.PreviewRatio.Ratio_16_9)
                {
                    if(mFullScreenRatio > CameraConfig.PreviewRatio.Ratio_17$25_9)
                    {
                        y += RatioBgUtils.getTopPaddingHeight(mCurrentRatio);
                    }
                }
                else
                {
                    float r = CameraConfig.PreviewRatio.Ratio_16_9 / mCurrentRatio;
                    x = (x1 - (mCameraViewWidth * (1.0f - r)) / 2.0f) / r;
                }

                if (mFocusView != null)
                {
                    mFocusView.setCircleXY(x, y);
                    if (longPress)
                    {
                        mFocusView.doLongPressAnim();
                        if (mAutoLockTipsView != null)
                        {
                            mAutoLockTipsView.setVisibility(VISIBLE);
                        }
                    }
                    else
                    {
                        if (mAutoLockTipsView != null)
                        {
                            mAutoLockTipsView.setVisibility(GONE);
                        }
                        mFocusView.showFocus(true);
                    }
                }
            }
            cameraHandler.setFocusAndMeteringArea(x1, y1, x2, y2, RenderHelper.sCameraFocusRatio);
        }
    }

    @Override
    public void onError(int error, Camera camera) {
        if(mUIHandler != null)
        {
            mUIHandler.obtainMessage(MSG_CAMERA_ERROR, error, error).sendToTarget();
        }
    }

    /**
     * 镜头权限提示
     *
     * @param show
     */
    private void showCameraPermissionHelper(boolean show)
    {
        if(show && mCameraErrorTipsDialog == null)
        {
            mCameraErrorTipsDialog = new CameraErrorTipsDialog(getContext());
            mCameraErrorTipsDialog.setOnClickListener(new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    //0：打开提示 1：关闭tips
                    if(which == 0)
                    {
                        if(mSite != null)
                        {
                            String url = "http://wap.adnonstop.com/beauty_camera/prod/public/index.php?r=Softtext/Guidance&key=";
                            try
                            {
                                url += URLEncoder.encode(ExceptionData.SafeString(Build.FINGERPRINT), "UTF-8");
                            }
                            catch(UnsupportedEncodingException e)
                            {
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
            mCameraErrorTipsDialog.setOnDismissListener(new DialogInterface.OnDismissListener()
            {
                @Override
                public void onDismiss(DialogInterface dialog)
                {
                    if(mCameraErrorTipsDialog != null && mCameraErrorTipsDialog.canClosePage())
                    {
                        onBack();
                    }
                    mCameraErrorTipsDialog = null;
                }
            });
            mCameraErrorTipsDialog.show();
        }
        else if(!show && mCameraErrorTipsDialog != null)
        {
            mCameraErrorTipsDialog.setCanClosePage(false);
            mCameraErrorTipsDialog.dismiss();
        }
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera)
    {

    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera)
    {
        if (mDoCameraOpenAnim)
        {
            mDoCameraOpenAnim = false;
            mUIHandler.sendEmptyMessageDelayed(MSG_CANCEL_MASK, 600);
            if (mIsInitPage && mPreviewView != null) {
                RectF firstFace = new RectF();
                firstFace.left = mPreviewView.getWidth();
                firstFace.top = mPreviewView.getHeight();
                firstFace.right = mPreviewView.getWidth() / 2.0f;
                firstFace.bottom = mPreviewView.getHeight() / 2.0f;
                mDoFocusWithFace = 0;
                mUIHandler.obtainMessage(MSG_DETECT_FACE_FINISH, firstFace).sendToTarget();
            }
        }
    }

    @Override
    public void onShutter()
    {

    }

    @Override
    public void onDetectResult(ArrayList<PocoFace> faces, int viewWidth, int viewHeight)
    {
        if(mIsSwitchCamera)
        {
            FaceDataHelper.getInstance().setFaceData(null);
            return;
        }
        FaceDataHelper.getInstance().setFaceData(faces);

        //检测到人脸才发送文本信息
       if(mShowActionTip &&  mUIHandler != null && faces != null)
       {
           mShowActionTip = false;
           mUIHandler.sendEmptyMessageDelayed(MSG_SHOW_ACTION_TIPS, 100);
       }

        RectF firstFace = null;
        if(faces != null && !faces.isEmpty())
        {
            PocoFace pocoFace = faces.get(0);
            if(pocoFace != null && pocoFace.rect != null)
            {
                firstFace = new RectF();
                firstFace.left = (pocoFace.rect.right - pocoFace.rect.left) * mCameraViewWidth;//w
                firstFace.top = (pocoFace.rect.bottom - pocoFace.rect.top) * mCameraViewHeight;//h

                //中心点
                firstFace.right = pocoFace.rect.left * mCameraViewWidth + firstFace.left / 2;//cpx
                firstFace.bottom = pocoFace.rect.top * mCameraViewHeight + firstFace.top / 2;//cpy
            }
        }
        mUIHandler.obtainMessage(MSG_DETECT_FACE_FINISH, firstFace).sendToTarget();
    }

    @Override
    public void onPageResult(int siteID, HashMap<String, Object> params) {
        super.onPageResult(siteID, params);
        if(siteID == SiteID.LOGIN || siteID == SiteID.REGISTER_DETAIL || siteID == SiteID.RESETPSW)
        {
            onResume();
        }
        else if (siteID == SiteID.REGISTER_HEAD)
        {
            onResume();
        }
        else if(siteID == SiteID.FILTER_DOWNLOAD_MORE)
        {
            onResume();
        }
        else if(siteID == SiteID.FILTER_DETAIL)
        {
            onResume();
        }
        else if(siteID == SiteID.WEBVIEW)
        {
            showCameraPermissionHelper(true);
        }

        if (mBeautySelectorView != null) {
            mBeautySelectorView.onPageResult(siteID, params);
        }
        if(siteID == SiteID.WEBVIEW) {
            showCameraPermissionHelper(true);
        } else if(siteID == SiteID.LOGIN || siteID == SiteID.REGISTER_DETAIL || siteID == SiteID.RESETPSW) {
            onResume();
        }
    }

    public void resumeResource()
    {
        StickerDrawHelper.getInstance().onResume();

//        if(mBrightnessUtils != null)
//        {
//            mBrightnessUtils.setContext(getContext());
//            mBrightnessUtils.init();
//            mBrightnessUtils.setBrightnessToMax();
//            mBrightnessUtils.registerBrightnessObserver();
//        }
        keepScreenWakeUp(true);

        // 重新选中贴纸，修复 先打开美人，再通过第三方调用美人相机，并使用贴纸后，再打开美人，公用贴纸效果的 bug
        if (mStickerView != null && mStickerView.hasLoadData() && !StickerMgr.sInstanceIsNull())
        {
            int sel_sticker_id = StickerMgr.getInstance().getSelectedInfo(StickerMgr.SelectedInfoKey.STICKER);
            StickerInfo info = StickerMgr.getInstance().getStickerInfoByID(sel_sticker_id);
            mStickerView.onSelectedSticker(info);
        }

        CameraHandler cameraHandler = RenderHelper.getCameraHandler();
        if(cameraHandler != null)
        {
            CameraThread cameraThread = cameraHandler.getCamera();
            if(cameraThread != null)
            {
                cameraThread.setCameraCallback(this);
                cameraThread.setCameraAllCallback(this);
            }
            cameraHandler.setFlashMode(mCurrentFlashMode, true);
        }

//        mIsPageResult = false;
//
//        requestRendererParams(true);
    }

    public void recycleResource()
    {
//        mPauseDetectOnTabChange = false;
//
//        pauseStickerSound();
//
//        if(mBrightnessUtils != null)
//        {
//            mBrightnessUtils.unregisterBrightnessObserver();
//            if(!mResetBrightnessDisable)
//            {
//                mBrightnessUtils.resetToDefault();
//            }
//        }
        keepScreenWakeUp(false);
//        if(mBrightnessToast != null)
//        {
//            mBrightnessToast = null;
//        }

        if(isFront)
        {
            if(CameraConfig.getInstance().getString(CameraConfig.ConfigMap.FrontFlashModeStr).equals(CameraConfig.FlashMode.Torch))
            {
                mCurrentFlashMode = CameraConfig.FlashMode.Off;
                CameraConfig.getInstance().saveData(CameraConfig.ConfigMap.FrontFlashModeStr, mCurrentFlashMode);
            }
        }
        else
        {
            // 改了需求，如果是 手电筒 模式，不记住状态，恢复 关闭 模式
            if(CameraConfig.getInstance().getString(CameraConfig.ConfigMap.FlashModeStr).equals(CameraConfig.FlashMode.Torch))
            {
                mCurrentFlashMode = CameraConfig.FlashMode.Off;
                CameraConfig.getInstance().saveData(CameraConfig.ConfigMap.FlashModeStr, mCurrentFlashMode);
            }
        }

        CameraHandler cameraHandler = RenderHelper.getCameraHandler();
        if(cameraHandler != null)
        {
            cameraHandler.setFlashMode(CameraConfig.FlashMode.Off);

            CameraThread cameraThread = cameraHandler.getCamera();
            if(cameraThread != null)
            {
                cameraThread.setCameraAllCallback(null);
            }
        }
//        CameraUtils.setSystemVolume(getContext(), false);
//        AudioControlUtils.resumeOtherMusic(getContext());

        CameraConfig.getInstance().saveAllData();

//        if(mCameraPopSetting != null && mCameraPopSetting.isAlive())
//        {
//            mCameraPopSetting.dismissWithoutAnim();
//        }
        if(mRatioBgView != null)
        {
            mRatioBgView.setFocusFinish();
        }
//        dismissMsgToast();
//        removeAllMsg();
    }

    @Override
    public void onResume()
    {
        super.onResume();

        isClickFilterSensor = false;
        CameraConfig.getInstance().initAll(getContext());

        if (mPreviewView != null) {
            if (mCurrentRatio < CameraConfig.PreviewRatio.Ratio_16_9) {
                mPreviewView.setPreviewSize(mCameraViewWidth, (int)(mCameraViewWidth * CameraConfig.PreviewRatio.Ratio_4_3), mCameraSizeType);
            } else {
                mPreviewView.setPreviewSize(mCameraViewWidth, (int)(mCameraViewWidth * CameraConfig.PreviewRatio.Ratio_16_9), mCameraSizeType);
            }
            mPreviewView.onResume();
        }
        if (mBeautySelectorView != null) {
            mBeautySelectorView.onResume();
        }
        initShape(true);
        initBeauty(true);

        if (mRecomDisplayMgr != null) {
            mRecomDisplayMgr.onResume();
        }

        resumeResource();

        changeSystemUiVisibility(View.GONE);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        startOrStopLiveEncoder(false);
        if (mPreviewView != null)
        {
            mPreviewView.onPause();
        }
        if (mBeautySelectorView != null) {
            mBeautySelectorView.onPause();
        }
        if (mRecomDisplayMgr != null) {
            mRecomDisplayMgr.onPause();
        }
        recycleResource();

        changeSystemUiVisibility(View.VISIBLE);
    }

    @Override
    public void onBack()
    {
        onBack(false);
    }

    private void onBack(boolean backToHome)
    {
        if (mDoingTransYAnim) return;

        if (closeRecommendView())
        {
            return;
        }

        if (isShowStickerMgrPage())
        {
            showStickerMgrPage(false);
            return;
        }
        else if (isShowStickerList())
        {
            showStickerList(false);
            return;
        }

        if (mLiveEncoderServer != null) {
            mLiveEncoderServer.setCanStopServer(true);
        }

        sendCameraSensorsDataWhenClick();
        MyBeautyStat.onClickByRes(R.string.直播助手_拍摄页_拍摄页_退出);
        if (mSite != null)
        {
            if (backToHome) {
                mSite.onBackToHome(getContext());
            } else {
                mSite.onBack(getContext());
            }
        }
    }

    @Override
    public void onClose() {
        super.onClose();
        LanguageUtil.SetDefaultLocaleLanguage(getContext());
        startOrStopLiveEncoder(false);
        if(mPreviewView != null) {
            mPreviewView.onPause();
            mPreviewView.onDestroy();
        }
        if (mLiveEncoderServer != null) {
            if (!mLiveEncoderServer.isCanStopServer()) {
                mLiveEncoderServer.setCanStopServer(true);
            }
            mLiveEncoderServer.stopServer();
            mLiveEncoderServer = null;
        }
        if (mEventListener != null) {
            EventCenter.removeListener(mEventListener);
            mEventListener = null;
        }

        recycleResource();

        DownloadMgr.getInstance().RemoveDownloadListener(mDownloadListener);
        mAnimationClickListener = null;

        if (mStickerView != null) {
            mStickerView.ClearAll();
        }
        if (mBeautySelectorView != null) {
            mBeautySelectorView.onClose();
        }
        if(mMsgToast != null) {
            mMsgToast.ClearAll();
        }
        if(mFilterMsgToast != null) {
            mFilterMsgToast.ClearAll();
        }

        mFilterBeautyParams = null;
        removeAllViews();

        MyBeautyStat.onUseLiveCamera(
                String.valueOf(mStickerTongJiId > 0 ? mStickerTongJiId : 0),
                String.valueOf((mCurrentFilterRes != null && !mCurrentFilterRes.m_isStickerFilter) ? mCurrentFilterRes.m_id : 0));
        ShapeSPConfig.getInstance(getContext()).clearAll();
        StickerMgr.getInstance().ClearAll();
        CameraConfig.getInstance().clearAll();
        StickerDrawHelper.getInstance().clearAll();

        DUIConfig.getInstance().ClearAll();

        changeSystemUiVisibility(View.VISIBLE);

        MyBeautyStat.onPageEndByRes(R.string.直播助手_直播页_主页面);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        boolean upEvent = event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL;

        if(mGestureDetector != null && upEvent)
        {
            if (isShowLightnessBar())
            {
                mLightnessSeekBar.hideValueText();
                mUIHandler.sendMessageDelayed(Message.obtain(mUIHandler, MSG_HIDE_LIGHTNESS_SEEK_BAR), 3000);
            }

            if (mTriggerLongPress)
            {
                if (mFocusView != null)
                {
                    mFocusView.setVisibility(View.VISIBLE);
                    mFocusView.doLongPressFingerUpAnim();
                }
            }
        }
        return mGestureDetector != null ? mGestureDetector.onTouchEvent(event) : super.onTouchEvent(event);
    }

    @Override
    public void OnSelectedSticker(final Object obj)
    {
        //监听人脸动作回调
        setStickerDrawListener(mOnDrawStickerResListener);

        if (obj != null && obj instanceof VideoStickerRes)
        {
            if (((VideoStickerRes) obj).mStickerRes != null)
            {
                MyBeautyStat.onClickByRes(R.string.直播助手_贴纸页_贴纸页_选择贴纸);
                DUIConfig.getInstance().updateDecorStatusToPC(((VideoStickerRes) obj).m_id);
            }
        }
        else
        {
            if (mStickerId != 0)
            {
                MyBeautyStat.onClickByRes(R.string.直播助手_贴纸页_贴纸页_无贴纸);
            }
            DUIConfig.getInstance().updateDecorStatusToPC(-1);
        }

        if (mPreviewView != null)
        {
            mPreviewView.queueEvent(new RenderRunnable()
            {
                @Override
                public void run(RenderThread renderThread)
                {
                    VideoStickerRes videoStickerRes = null;
                    if (obj != null)
                    {
                        videoStickerRes = (VideoStickerRes) obj;
                    }
                    mStickerDealWithShape = false;
                    int stickerResType = -1;//0:素材+变形, 1:素材, 2:变形
                    final int lastStickerId = mStickerId;
                    mActionName = null;
                    mShowActionTip = false;
                    if (videoStickerRes != null)
                    {
                        mStickerId = videoStickerRes.m_id;
                        mStickerTongJiId = videoStickerRes.m_tjId;
                        int shapeId = FaceShapeType.getShapeIdByName(videoStickerRes.m_shape_type);
                        if (mLastResShapeTypeId > 0)
                        {
                            if (shapeId > 0)
                            {
                                mShapeTypeId = shapeId;
                                stickerResType = 0;
                            }
                            else
                            {
                                mShapeTypeId = mLastResShapeTypeId;
                                stickerResType = 1;
                            }
                        }
                        else
                        {
                            mShapeTypeId = shapeId;
                            stickerResType = (mShapeTypeId > 0 ? 0 : 1);
                        }

                        videoStickerRes.m_has_music = false;

                        setHasStickerSound(videoStickerRes.m_has_music);
                        if (videoStickerRes.mStickerRes != null)
                        {
                            if (videoStickerRes.mStickerRes.mOrderStickerRes != null && !videoStickerRes.mStickerRes.mOrderStickerRes.isEmpty())
                            {
                                mStickerDealWithShape = (mShapeTypeId > 0);
                            }
                            mActionName = videoStickerRes.mStickerRes.mAction;
                            if (videoStickerRes.m_has_music && videoStickerRes.mStickerRes.mStickerSoundRes != null && videoStickerRes.mStickerRes.mStickerSoundRes.mStickerSounds != null)
                            {
                                for (StickerSound stickerSound : videoStickerRes.mStickerRes.mStickerSoundRes.mStickerSounds)
                                {
                                    if (stickerSound != null && stickerSound.mSoundType.isEffectAction())
                                    {
                                    }
                                }
                            }
                        }
                        else
                        {
                            mLastResShapeTypeId = mShapeTypeId;
                            stickerResType = 2;
                        }
                    }
                    else
                    {
                        mStickerId = CameraStickerConfig.STICKER_ID_NON;
                        mStickerTongJiId = 0;
                        mShapeTypeId = 0;
                        mLastResShapeTypeId = 0;
                        setHasStickerSound(false);
                    }
                    if (mLastResType == 1 && stickerResType == 2)
                    {
                        //普通无变形素材 + 常用变形（切换变形）
                        // mStickerDealWithShape = true;
                        // StickerDrawHelper.getInstance().setShapeTypeId(mShapeTypeId);
                    }
                    else
                    {
                        if (StickerDrawHelper.getInstance().setStickerRes(mStickerId, videoStickerRes))
                        {
                            //StickerDrawHelper.getInstance().setShapeTypeId(mShapeTypeId);

                            if (renderThread != null && renderThread.getFilterManager() != null)
                            {
                                renderThread.getFilterManager().resetFilterData();
                            }

                            if (mUIHandler != null)
                            {
                                final VideoStickerRes finalVideoStickerRes = videoStickerRes;
                                mUIHandler.post(new Runnable()
                                {
                                    @Override
                                    public void run()
                                    {
                                        setStickerFilter(finalVideoStickerRes);
                                    }
                                });
                            }
                            if (mActionName != null)
                            {
                                mShowActionTip = true;
                            }
                        }
                        else
                        {
                            if (mUIHandler != null)
                            {
                                final VideoStickerRes finalVideoStickerRes = videoStickerRes;
                                mUIHandler.post(new Runnable()
                                {
                                    @Override
                                    public void run()
                                    {
                                        setStickerFilter(finalVideoStickerRes);
                                    }
                                });
                            }
                        }
                    }

                    if (renderThread != null && renderThread.getFilterManager() != null)
                    {
                        renderThread.getFilterManager().setBusinessBeautyEnable(false);// 38559 纪梵希商业定制

                        renderThread.getFilterManager().setFaceAdjustEnable(true/*!mStickerDealWithShape && mShapeTypeId <= 0*/);
                        renderThread.getFilterManager().setShapeEnable(false/*!mStickerDealWithShape && mShapeTypeId > 0*/);
                        renderThread.getFilterManager().changeShapeFilter(mShapeTypeId);
                    }
                }
            });
        }
    }

    @Override
    public void onOpenStickerMgrPage()
    {
        MyBeautyStat.onClickByRes(R.string.直播助手_贴纸页_贴纸页_进入素材管理);
        MyBeautyStat.onPageStartByRes(R.string.直播助手_贴纸页_贴纸管理页);
        initStickerMgrPage();

        showStickerMgrPage(true);
    }

    private void initStickerMgrPage()
    {
        if (mStickerMgrPage == null)
        {
            mStickerMgrPage = new StickerMgrPage(getContext());
            mStickerMgrPage.setPageListener(this);
            LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            mStickerMgrPage.setClickable(true);
            addView(mStickerMgrPage, params);
        }
    }

    @Override
    public void onCloseStickerMgrPage()
    {
//        if (StickerLocalMgr.getInstance().hasDeleted())
//        {
//            StickerMgr.getInstance().notifyReflashAllData();
//        }
        MyBeautyStat.onClickByRes(R.string.直播助手_贴纸页_贴纸管理页_返回);
        MyBeautyStat.onPageEndByRes(R.string.直播助手_贴纸页_贴纸管理页);
        showStickerMgrPage(false);
    }

    @Override
    public void onCloseStickerList()
    {
        showStickerList(false);
    }

    private void showStickerMgrPage(boolean show)
    {
        int dy = ShareData.m_screenRealHeight;
        mDoingTransYAnim = true;
        if (show)
        {
            ObjectAnimator animator = ObjectAnimator.ofFloat(mStickerMgrPage, "translationY", dy, 0);
            animator.addListener(new AnimatorListenerAdapter()
            {
                @Override
                public void onAnimationEnd(Animator animation)
                {
                    super.onAnimationEnd(animation);
                    mDoingTransYAnim = false;
                }
            });
            animator.setDuration(350);
            animator.start();
        }
        else
        {
            ObjectAnimator animator = ObjectAnimator.ofFloat(mStickerMgrPage, "translationY", dy);
            animator.setDuration(350);
            animator.addListener(new AnimatorListenerAdapter()
            {
                @Override
                public void onAnimationEnd(Animator animation)
                {
                    super.onAnimationEnd(animation);

                    mUIHandler.postDelayed(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            if(mStickerMgrPage != null)
                            {
                                mStickerMgrPage.ClearAll();
                            }
                            removeView(mStickerMgrPage);
                            mStickerMgrPage = null;
                        }
                    }, 100);

                    mDoingTransYAnim = false;
                }
            });
            animator.start();
        }
    }

    /**
     * 手势操作监听
     */
    private class GestureListener extends GestureDetector.SimpleOnGestureListener
    {
        private final int MOVE_MIN_DISTANCE = 30;
        private boolean mNeedReset;
        private int mDirection = -1;//0:left-right, 1:up-down

        private void resetState()
        {
            mNeedReset = false;
            mDirection = -1;
        }

        private boolean interceptEvent(float ey, boolean isActionDown)
        {
            float topY = RatioBgUtils.GetTopHeightByRatio(mCurrentRatio);
            float offsetY = 0.0f;
            float ratio = mCurrentRatio;
            if(mCurrentRatio > CameraConfig.PreviewRatio.Ratio_16_9)
            {//全屏
                ratio = CameraConfig.PreviewRatio.Ratio_4_3;
                if(mFullScreenRatio > CameraConfig.PreviewRatio.Ratio_17$25_9)
                {
                    offsetY = CameraPercentUtil.HeightPxToPercent(200);
                }
                else
                {
                    offsetY = CameraPercentUtil.HeightPxToPercent(80);
                }
            }
            else
            {
                if(mCurrentRatio == CameraConfig.PreviewRatio.Ratio_16_9)
                {
                    ratio = CameraConfig.PreviewRatio.Ratio_4_3;
                }
            }

            float bottomY = topY + ShareData.m_screenWidth * ratio + offsetY;

            if(ey < topY || ey > bottomY)
            {
                return true;
            }

            if(isActionDown)
            {
                if(!mNeedReset)
                {
                    mNeedReset = true;
                    mUIHandler.sendEmptyMessageDelayed(MSG_RESET_GESTURE_STATE, 5000);
                }
            }
            else
            {
                if(mNeedReset)
                {
                    mNeedReset = false;
                    mUIHandler.removeMessages(MSG_RESET_GESTURE_STATE);
                }

                if (isShowStickerList())
                {
                    showStickerList(false);
                    return true;
                }

                if (isShowBeautySelector())
                {
                    showBeautySelector(false);
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean onDown(MotionEvent e)
        {
            mTriggerLongPress = false;
            if(interceptEvent(e.getY(), true))
            {
                return true;
            }
            mDirection = -1;
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e)
        {
            mTriggerLongPress = true;
            float x1 = e.getX();
            float y1 = e.getY();
            if(mCurrentRatio <= CameraConfig.PreviewRatio.Ratio_16_9)
            {
                if(mFullScreenRatio > CameraConfig.PreviewRatio.Ratio_17$25_9)
                {
                    y1 -= RatioBgUtils.getTopPaddingHeight(mCurrentRatio);
                }
            }
            else
            {
                //计算镜头画面的实际位置
                float r = CameraConfig.PreviewRatio.Ratio_16_9 / mCurrentRatio;
                x1 = (mCameraViewWidth * (1.0f - r)) / 2.0f + x1 * r;
            }
            mUIHandler.removeMessages(MSG_DO_FOCUS_AND_METERING);
            mUIHandler.removeMessages(MSG_LONG_PRESS_DO_FOCUS_AND_METERING);
            mUIHandler.removeMessages(MSG_SHOW_LIGHTNESS_SEEK_BAR);
            mUIHandler.removeMessages(MSG_HIDE_LIGHTNESS_SEEK_BAR);
            mUIHandler.sendMessageDelayed(Message.obtain(mUIHandler, MSG_LONG_PRESS_DO_FOCUS_AND_METERING, new RectF(x1, y1, x1, y1)), 80);
            mUIHandler.sendMessageDelayed(Message.obtain(mUIHandler, MSG_HIDE_LIGHTNESS_SEEK_BAR, true), 80);
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e)
        {
            if(mTriggerLongPress || interceptEvent(e.getY(), false))
            {
                return true;
            }
            float x1 = e.getX();
            float y1 = e.getY();
            if(mCurrentRatio <= CameraConfig.PreviewRatio.Ratio_16_9)
            {
                if(mFullScreenRatio > CameraConfig.PreviewRatio.Ratio_17$25_9)
                {
                    y1 -= RatioBgUtils.getTopPaddingHeight(mCurrentRatio);
                }
            }
            else
            {
                //计算镜头画面的实际位置
                float r = CameraConfig.PreviewRatio.Ratio_16_9 / mCurrentRatio;
                x1 = (mCameraViewWidth * (1.0f - r)) / 2.0f + x1 * r;
            }
            mUIHandler.removeMessages(MSG_DO_FOCUS_AND_METERING);
            mUIHandler.removeMessages(MSG_SHOW_LIGHTNESS_SEEK_BAR);
            mUIHandler.removeMessages(MSG_HIDE_LIGHTNESS_SEEK_BAR);
            mUIHandler.sendMessageDelayed(Message.obtain(mUIHandler, MSG_DO_FOCUS_AND_METERING, new RectF(x1, y1, x1, y1)), 80);
            mUIHandler.sendMessageDelayed(Message.obtain(mUIHandler, MSG_SHOW_LIGHTNESS_SEEK_BAR, true), 80);
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY)
        {   //e1:onDown
            if(interceptEvent(e2.getY(), false))
            {
                return true;
            }

            handleScrollGesture(e1, e2, distanceY, false);
            return false;
        }

        private void handleScrollGesture(MotionEvent e1, MotionEvent e2, float distanceY, boolean recording)
        {
            if(mDirection == -1)
            {
                int direct = getDirection(MOVE_MIN_DISTANCE, e1.getX(), e1.getY(), e2.getX(), e2.getY());
                if(mUIHandler != null)
                {
                    if(direct == 0 || direct == 2)
                    {
                       if(recording) return;
                       mDirection = 0;
                       mUIHandler.removeMessages(MSG_GESTURE_CHANGE);
                       mUIHandler.sendMessageDelayed(Message.obtain(mUIHandler, MSG_GESTURE_CHANGE, direct, 0), 15);
                    }
                    else if(direct == 1 || direct == 3)
                    {
                        mDirection = 1;
                        mUIHandler.removeMessages(MSG_HIDE_LIGHTNESS_SEEK_BAR);
                        if(mLightnessSeekBar != null && mLightnessSeekBar.getVisibility() == GONE)
                        {
                            mUIHandler.sendMessage(Message.obtain(mUIHandler, MSG_SHOW_LIGHTNESS_SEEK_BAR, false));
                        }
                    }
                }
            }

            if(mDirection == 1)
            {
                if(mLightnessSeekBar != null)
                {
                    mLightnessSeekBar.countCircleCenter(distanceY);
                }
            }
        }

        private int getDirection(float distance, float x1, float y1, float x2, float y2)
        {
            if(x1 - x2 > distance && Math.abs(x1 - x2) * 0.77f > Math.abs(y1 - y2))
            {//0.77 = 1 / 1.3
                return 0;//向左滑
            }
            else if(x2 - x1 > distance && Math.abs(x1 - x2) * 0.77f > Math.abs(y1 - y2))
            {
                return 2;//向右滑
            }
            else if(y1 - y2 > distance && Math.abs(y1 - y2) * 0.77f > Math.abs(x1 - x2))
            {
                return 1;//向上滑
            }
            else if(y2 - y1 > distance && Math.abs(y1 - y2) * 0.77f > Math.abs(x1 - x2))
            {
                return 3;//向下滑
            }
            return -1;
        }
    }
}
