package cn.poco.camera3;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import cn.poco.business.ChannelValue;
import cn.poco.camera.BrightnessUtils;
import cn.poco.camera.CameraAllCallback;
import cn.poco.camera.CameraConfig;
import cn.poco.camera.CameraSetDataKey;
import cn.poco.camera.CameraSound;
import cn.poco.camera.CameraUtils;
import cn.poco.camera.CountDownView;
import cn.poco.camera.ImageFile2;
import cn.poco.camera.LayoutMode;
import cn.poco.camera.PatchDialogType;
import cn.poco.camera.PatchDialogV2;
import cn.poco.camera.PatchMsgView;
import cn.poco.camera.StickerSPConfig;
import cn.poco.camera.activity.CameraActivity;
import cn.poco.camera.site.CameraPageSite;
import cn.poco.camera2.AudioControlUtils;
import cn.poco.camera2.CameraCallback;
import cn.poco.camera2.CameraErrorTipsDialog;
import cn.poco.camera2.CameraHandler;
import cn.poco.camera2.CameraThread;
import cn.poco.camera2.ResultCallback;
import cn.poco.camera3.beauty.BeautyGuideView;
import cn.poco.camera3.beauty.STag;
import cn.poco.camera3.beauty.ShapeSPConfig;
import cn.poco.camera3.beauty.callback.PageCallbackAdapter;
import cn.poco.camera3.beauty.data.BeautyData;
import cn.poco.camera3.beauty.data.BeautyInfo;
import cn.poco.camera3.beauty.data.BeautyResMgr;
import cn.poco.camera3.beauty.data.BeautyShapeDataUtils;
import cn.poco.camera3.beauty.data.ShapeData;
import cn.poco.camera3.beauty.data.SuperShapeData;
import cn.poco.camera3.beauty.recycler.ShapeExAdapter;
import cn.poco.camera3.cb.CameraFilterListener;
import cn.poco.camera3.cb.CameraPageListener;
import cn.poco.camera3.cb.UIObservable;
import cn.poco.camera3.cb.UIObserver;
import cn.poco.camera3.config.CameraBtnConfig;
import cn.poco.camera3.config.CameraStickerConfig;
import cn.poco.camera3.config.CameraUIConfig;
import cn.poco.camera3.config.shutter.ShutterConfig;
import cn.poco.camera3.mgr.StickerResMgr;
import cn.poco.camera3.ui.CameraLightnessSeekBar;
import cn.poco.camera3.ui.ClearAllDialogView;
import cn.poco.camera3.ui.RatioBgViewV2;
import cn.poco.camera3.ui.TipsView;
import cn.poco.camera3.util.CameraPercentUtil;
import cn.poco.camera3.util.RatioBgUtils;
import cn.poco.dynamicSticker.FaceAction;
import cn.poco.dynamicSticker.StickerMediaPlayer;
import cn.poco.dynamicSticker.StickerSound;
import cn.poco.dynamicSticker.StickerSoundManager;
import cn.poco.dynamicSticker.TypeValue;
import cn.poco.exception.ExceptionData;
import cn.poco.filter4.recycle.FilterAdapter;
import cn.poco.filterBeautify.FilterBeautifyInfo;
import cn.poco.filterBeautify.FilterBeautifyProcessor;
import cn.poco.filterBeautify.FilterBeautyParams;
import cn.poco.framework.DataKey;
import cn.poco.framework.EventCenter;
import cn.poco.framework.EventID;
import cn.poco.framework.IPage;
import cn.poco.framework.MyFramework;
import cn.poco.framework.SiteID;
import cn.poco.gldraw2.CameraRenderView;
import cn.poco.gldraw2.DetectFaceCallback;
import cn.poco.gldraw2.FaceDataHelper;
import cn.poco.gldraw2.OnCaptureFrameListener;
import cn.poco.gldraw2.RenderHelper;
import cn.poco.gldraw2.RenderRunnable;
import cn.poco.gldraw2.RenderThread;
import cn.poco.glfilter.shape.FaceShapeType;
import cn.poco.glfilter.sticker.OnDrawStickerResListener;
import cn.poco.glfilter.sticker.StickerDrawHelper;
import cn.poco.image.PocoDetector;
import cn.poco.image.PocoFace;
import cn.poco.imagecore.ImageUtils;
import cn.poco.resource.AbsDownloadMgr;
import cn.poco.resource.BaseRes;
import cn.poco.resource.DownloadMgr;
import cn.poco.resource.FilterRes;
import cn.poco.resource.IDownload;
import cn.poco.resource.ResType;
import cn.poco.resource.VideoStickerRes;
import cn.poco.setting.SettingInfoMgr;
import cn.poco.statistics.MyBeautyStat;
import cn.poco.statistics.TongJi2;
import cn.poco.statistics.TongJiUtils;
import cn.poco.system.TagMgr;
import cn.poco.system.Tags;
import cn.poco.tianutils.CommonUtils;
import cn.poco.tianutils.MakeBmpV2;
import cn.poco.tianutils.ShareData;
import cn.poco.utils.DrawableUtils;
import cn.poco.utils.MemoryTipDialog;
import cn.poco.utils.PermissionHelper;
import cn.poco.utils.WaitAnimDialog;
import cn.poco.video.OnRecordListener;
import cn.poco.video.RecordManager;
import cn.poco.video.encoder.MediaMuxerWrapper;
import cn.poco.video.encoder.RecordState;
import cn.poco.widget.AlertDialogV1;
import cn.poco.widget.SdkOutDatedDialog;
import my.beautyCamera.R;

import static cn.poco.imagecore.ImageUtils.JpgEncode;

public class CameraPageV3 extends IPage implements
		CameraPageListener, CameraPopSetting.UIListener,
		CameraCallback, CameraAllCallback,
		DetectFaceCallback, OnCaptureFrameListener,
		CameraFilterListener, UnLockUIListener,
		BeautyGuideView.onBeautyGuideStateListener, KeyBroadCastReceiver.OnKeyStatusListener
{
	private static final String TAG = "CameraPageV3";

	private static final int MSG_CANCEL_MASK = 10; // 清除遮罩
	private static final int MSG_TIMER_COUNT_DOWN = 11;// 拍照计时
	private static final int MSG_CANCEL_TAKE_PICTURE = 12; // 取消拍照
	private static final int MSG_GESTURE_CHANGE = 13;//触屏手势
	private static final int MSG_RESET_GESTURE_STATE = 14;
	private static final int MSG_DO_FOCUS_AND_METERING = 15;//对焦框
	private static final int MSG_CLEAR_FOCUS_AND_METERING = 16;//清除对焦框
	private static final int MSG_PATCH_SAVE_PIC_END = 17;//镜头校正照片方向
	private static final int MSG_CAMERA_ERROR = 19;//打开镜头出错
	private static final int MSG_OPEN_OR_CLOSE_AUTO_FOCUS = 20;//开启或关闭自动对焦
	private static final int MSG_SHOW_ACTION_TIPS = 21;//贴纸动作提示
	private static final int MSG_DELAY_PREPARE_RECORD = 22;
	private static final int MSG_RECORD_TIMER_COUNT_DOWN = 23;// 录制计时
	private static final int MSG_TAKE_PICTURE = 24;// 拍照
	private static final int MSG_FLASH_SCREEN_ON_TAKE_PICTURE = 25;//拍照时闪一下屏
	private static final int MSG_HIDE_NAVIGATION_BAR = 26;//隐藏虚拟按键
	private static final int MSG_SHOW_LIGHTNESS_SEEK_BAR = 27;//显示曝光
	private static final int MSG_HIDE_LIGHTNESS_SEEK_BAR = 28;//隐藏曝光
	private static final int MSG_DETECT_FACE_FINISH = 29;//人脸检测
	private static final int MSG_DO_FOCUS_WITH_FACE = 30;//判断是否可以跟随人脸对焦
	private final int mVersionCode;

	private int mPopHeight; // 设置弹窗 一级界面高度

	private CameraPageSite mSite;
	private Handler mUIHandler;
	private GestureDetector mGestureDetector;
	private GestureListener mGestureListener;

	// UI
	private FrameLayout mRootView;
	private CameraRenderView mPreviewView;
	private RatioBgViewV2 mRatioBgView;
	private FrameLayout mContentRootView;
	private CameraLayoutV3 mContentView;
	private CameraTopControlV3 mCameraTopControl;
	private CameraBottomControlV3 mCameraBottomControl;
	private CameraPopSetting mCameraPopSetting;
	private FrameLayout mPopFrameView;
	private CountDownView mCountDownView;
	// private TailorMadeViewV2 mTailorMadeView;
	private SdkOutDatedDialog mAppUpdateTips;
	private TipsView mTips;

	private BeautyGuideView mBeautyGuideView;
	private PageCallbackAdapter mBeautySelectorCB;

	//当前使用的美颜美形参数
	private FilterBeautyParams mFilterBeautyParams;

	//当前模式类型
	private int mCurrentTabType;

	private int mLastTabType;

	//当前可显示的模式类型
	private int mShowTabType = ShutterConfig.ALL_TYPE;


	// cb
	private RatioBgViewV2.OnRatioChangeListener mRatioChangeListener;

	// page -> ui 之间通讯
	private UIObservable mUIObserverList;

	private boolean mPauseDetectOnTabChange;//切换tab时停止人脸数据更新

	/**
	 * SetData Params
	 */
	private boolean mIsPageResult;
	private boolean mHidePhotoPickerBtn;
	private boolean mHideStickerBtn;
	private boolean mHideBeautySetting;
	private boolean mIsShowStickerSelector;
	private boolean isBusiness;
	private String mChannelValue;
	private boolean mHasOpenAnim;
	private boolean mIsDoingAnim;
	private boolean mIsPauseDetect;
	private boolean mHideFilterSelector;
	private boolean isTailorMadeSetting = false;//设置美形定制
    private boolean mIsHideTailorMadeDialog;
	private boolean mHideSettingBtn;
	private boolean mHideRatioBtn;
	private boolean mHidePatchBtn;

	/**
	 * 第三方调用
	 */
	private boolean mIsThirdPartyVideo;     // 第三方调用录像
	private boolean mIsThirdPartyPicture;   // 第三方调用拍照
	private Uri mVideoExtraUri;             //第三方调用视频指定的uri

	/**
	 * 镜头
	 */
	private boolean mIsInitCamera;           //镜头open初始化
	private boolean isPageBack;             // 是否是返回;
	private boolean isPageClose;            // 是否关闭页面;
	private CameraErrorTipsDialog mCameraErrorTipsDialog;         //镜头提示dialog
	private boolean mVideoIsPause;          //是否在暂停录制视频中
	private WaitAnimDialog mDialog;         //dialog

	/**
	 * 镜头矫正
	 */
	private boolean isPatchMode;            //是否矫正模式
	private boolean isPatchOtherCamera;     //是否矫正另外一个镜头
	private boolean isPatchFinishToClose;   //矫正完成后是否关闭page
	private int picturePatchDegree = 90;    //保存照片时的修正角度
	private int tempPreviewDegree;          // 临时预览角度;
	private int tempPictureDegree;          // 临时图片角度;

	/**
	 * 滤镜美颜处理
	 */
	private boolean mIsFilterBeautifyProcess;                                               //滤镜美颜处理，耗时操作
	private int mFilterBeautifyProcessMask = FilterBeautifyProcessor.FILTER_BEAUTY;         //美形美颜滤镜位运算值


	/**
	 * 动态贴纸
	 */
	private int mStickerCategoryId = CameraStickerConfig.STICKER_CATEGORY_ID_NORMAL;      //贴纸分类id
	private int mStickerId = CameraStickerConfig.STICKER_ID_NORMAL;                       //贴纸id
	private boolean mRememberStickerID = true;
	private int mJustGotoCategoryID; // 初始化时，选中的 贴纸分类id
	private boolean mTempStickerWithFilter;
	private boolean mHideStickerManagerBtn = false;                                         //是否隐藏贴纸素材管理btn入口


	/**
	 * 贴纸变形组合
	 */
	private VideoStickerRes mShapeStickerRes;                                               //常用变形组合素材id
	private boolean mStickerDealWithShape;                                                  //常用组合素材+普通无变形素材
	private boolean isShapeCompose;                                                         //是否是变形组合

	private int mStickerTongJiId;
	private int mShapeTypeId;
	private int mLastResType;
	private int mLastResShapeTypeId;
	private boolean mIsStickerPreviewBack;

	private String mActionName;
	private String mShapeActionName;
	private String mMusicActionName;
	private boolean mShowActionTip;
	private boolean mHideWaterMark;

	/**
	 * 预览大小、宽高比
	 */
	private float mCameraViewRatio;
	private int mCameraViewWidth;
	private int mCameraViewHeight;
	private int mCameraSizeType = 0;
	private boolean mDoCameraOpenAnim;
	private boolean mIsSwitchCamera;
	private float mFullScreenRatio;

	private int mGifWidth = 240;
	private int mGifHeight = 240;
	private int mVideoWidth = 540;
	private int mVideoHeight = 960;
	private long mTargetDuration;
	private long mVideoTimeLong;
	private long mGifTimeLong = 3000;
	private Bitmap mVideoBG;
	private boolean mWaterMarkHasDate;
	private int mVideoOrientation;
	private int mPicOrientation;

	//视频录制时长
	@ShutterConfig.VideoDurationType
	private int mVideoRecordTimeType = ShutterConfig.VideoDurationType.DURATION_TEN_SEC | ShutterConfig.VideoDurationType.DURATION_THREE_MIN;
	private long mVideoRecordMinDuration = 1000L;//最短录制时长
	private boolean mVideoMultiSectionEnable = true;//多段录制
	private boolean mVideoMultiOrientationEnable = true;//支持多个方向录制

	private float mCurrentRatio;
	private int mFrameTopPadding;
	private int mScreenOrientation;

	private boolean mForceTabRatio;//强制所有tab使用指定的比例

	private int mCurrentCameraId;
	private boolean isFront;

	private boolean doTakePicture;
	private boolean mFaceGuideTakePicture;
	private int mTimerCounts = -1;
	private int mDoFocusWithFace = -1;

	//记录 拍照-预览-保存的闪关灯模式、拍照-预览-美颜美图-保存的闪关灯模式 用于继续拍照流程
	private String mCurrentFlashMode = CameraConfig.FlashMode.Off;
	private int mCurrentExposureValue;
	private boolean mTouchCapture;
	private int mCurrentTimerMode = CameraConfig.CaptureMode.Manual;
	private boolean mTickSoundEnable;
	private CameraSound mCameraSound;

	private boolean mResetBrightnessDisable;
	private Toast mBrightnessToast;

	/**
	 * 贴纸音效
	 */
	private StickerSoundManager mStickerSoundManager;
	private boolean mHasStickerSound;//是否有贴纸音效
	private boolean mIsStickerSoundMute;//贴纸是否静音

	private boolean mIsInitPage; // 是否在初始化页面

	private boolean mRecordVideoEnable;
	private boolean mAudioEnable;
	private boolean mDisableAudioPermissionTip;
	private RecordManager mRecordManager;
	private boolean mHasPrepared;
	private boolean mResetRecordUIState;
	private int mRecordState = RecordState.IDLE;
	private boolean mCanCaptureFrameAfterPrepare;
	private VideoMgr mVideoMgr;
	private int mCanStopRecordState = -1;//0:忽略继续录制，1:录满整秒停止，2:可以立即停止, 3最后一秒内点击停止
	private volatile long mCurrentDuration = 0;	// 当前录制走过的时长
	private volatile boolean mLaseSecondPause = false; // 最后一秒倒计时点击了停止

	private boolean mIsShowPhotoUseTips;
	private boolean mIsShowVideoUseTips;
	private boolean mIsShowCuteRatioUseTips;
	private boolean mIsShowCuteBeautyUseTips;

	/**
	 * 滤镜
	 */
	private FilterRes mCurrentFilterRes;
	private boolean mOnPause;
	private long pageOpenTime;

	/**
	 * 此id会查询 {@link cn.poco.filter4.FilterResMgr#GetFilterRes(Context, boolean, boolean)} 下的数据
	 */
	private int mFilterId = -1;

	private boolean mIsHomeKeyDown;// 点击home键的标识

	private static int mPageInitCount;//存在多个CameraPage时，只处理顶层 page 的 resume/pause 回调
	private static int mPagePauseCount;

	private boolean mShowSplashMask;
	private volatile boolean mSaveImageDataSucceed = false;    //线程保存图片成功
	private volatile boolean mSplashMaskAnimEnd = false;       //遮罩动画完成
	private ImageFile2 mImageFile;


	/**
	 * 虚拟键隐藏与显示
	 */
	private int mOriginVisibility = -1;
	private int mSystemUiVisibility = -1;
	private int mUnlockViewCloseCount;

	private String mModel;
	private CameraLightnessSeekBar mLightnessSeekBar;

    private EventCenter.OnEventListener mEventListener;

	public CameraPageV3(Context context, CameraPageSite site)
	{
		super(context, site);
		mSite = site;
		ShareData.InitData(context, true);
		mIsInitPage = true;
		// userRecord 代码4.2.0先注释，下个版本再上
//		UserRecord.init(); // 用户记录
//		UserRecord record = UserRecord.getRecord(UserRecord.RecordType.CAMERA);
//		if (record != null)
//		{
//			mCurrentTimerMode = (int) record.getRecordInfo(UserRecord.CameraRecordInfoType.CAPTURE_MODE);
//		}

		mModel = Build.MODEL.toUpperCase(Locale.CHINA);
		mVersionCode = CommonUtils.GetAppVerCode(context);

		// 检查是否首次安装 或 升级用户
		mIsShowVideoUseTips = TagMgr.CheckTag(getContext(), Tags.CAMERA_VIDEO_USE_TIPS);
//		mIsShowPhotoUseTips = TagMgr.CheckTag(getContext(), Tags.CAMERA_PHOTO_USE_TIPS);
//		mIsShowCuteRatioUseTips = TagMgr.CheckTag(getContext(), Tags.CAMERA_CUTE_USE_TIPS_FOR_RATIO);
		mIsShowCuteBeautyUseTips = TagMgr.CheckTag(getContext(), Tags.CAMERA_CUTE_USE_TIPS_FOR_BEAUTY);
		CameraConfig.getInstance().initAll(getContext());
		StickerSPConfig.getInstance().init(getContext());

		// 旧版本升级到 4.2.0 要显示一次 美形定制 气泡
		int beautyTagValue = TagMgr.GetTagIntValue(getContext(), Tags.CAMERA_CUTE_USE_TIPS_FOR_BEAUTY);
		if (mVersionCode <= 209 && !mIsShowCuteBeautyUseTips && beautyTagValue == 1)
		{
			TagMgr.ResetTag(getContext(), Tags.CAMERA_CUTE_USE_TIPS_FOR_BEAUTY);
			mIsShowCuteBeautyUseTips = true;
		}

		mFullScreenRatio = ShareData.m_screenRealHeight * 1.0f / ShareData.m_screenRealWidth;
		mPopHeight = CameraPercentUtil.HeightPxToPercent(188);

		//NOTE 首次安装或升级到v4.1.3（206）版本，打开镜头，默认“萌妆照模式”，不使用素材。升级后续版本时，则记住使用上一版本的模式和贴纸素材。
		//NOTE 跳转协议不用改，只改正常流程
		//NOTE 首次安装或升级到v4.1.3（206）版本，打开镜头，选择原图滤镜效果
		boolean isNonSticker = false;
		mCurrentTabType = ShutterConfig.TabType.PHOTO;
		mCurrentRatio = CameraConfig.PreviewRatio.Ratio_4_3;
		int tab = CameraConfig.getInstance().getInt(CameraConfig.ConfigMap.SelectTab);
		int cameraVC = TagMgr.GetTagIntValue(getContext(), Tags.CAMERA_PAGE_VERSION);   //camera version code
		if(cameraVC < 206)
		{
		    //比例 画幅
            CameraConfig.getInstance().saveData(CameraConfig.ConfigMap.CuteRatio, CameraConfig.PreviewRatio.Ratio_Full);
            CameraConfig.getInstance().saveData(CameraConfig.ConfigMap.VideoRatio, CameraConfig.PreviewRatio.Ratio_Full);

            //镜头类型模式
			mCurrentTabType = ShutterConfig.TabType.CUTE;
			isNonSticker = true;
			CameraConfig.getInstance().saveData(CameraConfig.ConfigMap.SelectTab, mCurrentTabType);

			//滤镜
			CameraConfig.getInstance().saveData(CameraConfig.ConfigMap.CameraFilterId, 0);

			//美形定制引导
			TagMgr.ResetTag(context, Tags.CAMERA_TAILOR_MADE_GUIDE_FLAG);
			TagMgr.Save(context);
		}
		else
		{
			if(tab != -1)
			{
				mCurrentTabType = tab;
			}
		}
		mLastTabType = mCurrentTabType;
		mRecordVideoEnable = true;
		if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2)
		{
			mRecordVideoEnable = false;// API小于18 录像不可用
		}

		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
		{
			setBackground(null);
		}
		else
		{
			setBackgroundDrawable(null);
		}

		if(DownloadMgr.getInstance() != null)
		{
			DownloadMgr.getInstance().AddDownloadListener(mDownloadListener);
		}

		if(CameraConfig.getInstance().getBoolean(CameraConfig.ConfigMap.NoSound) == SettingInfoMgr.GetSettingInfo(getContext()).GetCameraSoundState())
		{
			CameraConfig.getInstance().saveData(CameraConfig.ConfigMap.NoSound, !SettingInfoMgr.GetSettingInfo(getContext()).GetCameraSoundState());
		}
		mWaterMarkHasDate = SettingInfoMgr.GetSettingInfo(getContext()).GetAddDateState();
		mTickSoundEnable = SettingInfoMgr.GetSettingInfo(getContext()).GetTickSoundState();
		mCurrentFlashMode = CameraConfig.getInstance().getString(CameraConfig.ConfigMap.FlashModeStr);
		if(TextUtils.isEmpty(mCurrentFlashMode))
		{
			int flashMode = CameraConfig.getInstance().getInt(CameraConfig.ConfigMap.FlashMode);// 获取镜头闪光灯的配置;
			mCurrentFlashMode = CameraConfig.FlashMode.getMode(flashMode);
		}

		mTargetDuration = CameraConfig.VideoDuration.TEN_SECOND * 1000L;
		if(!TagMgr.CheckTag(getContext(), Tags.CAMERA_RECORD_DURATION))
		{
			mTargetDuration = TagMgr.GetTagIntValue(getContext(), Tags.CAMERA_RECORD_DURATION) * 1000L;
		}
		mVideoTimeLong = mTargetDuration;
		mFilterBeautyParams = new FilterBeautyParams();
		KeyBroadCastReceiver.registerListener(this);
		initStickerManager(isNonSticker);
		initGesture(context);
		initHandler();
		initCallback();
		initView();

		TongJiUtils.onPageStart(getContext(), R.string.拍照);

		mPageInitCount++;
        mPagePauseCount = 0;
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

	private float changeRatioByType(boolean isSave)
	{
		if(mCurrentTabType == ShutterConfig.TabType.PHOTO)
		{
			if(isSave)
			{
				CameraConfig.getInstance().saveData(CameraConfig.ConfigMap.PhotoRatio, mCurrentRatio);
			}
			else
			{
				mCurrentRatio = CameraConfig.getInstance().getFloat(CameraConfig.ConfigMap.PhotoRatio);
			}
		}
		else if(mCurrentTabType == ShutterConfig.TabType.CUTE)
		{
			if(isSave)
			{
				CameraConfig.getInstance().saveData(CameraConfig.ConfigMap.CuteRatio, mCurrentRatio);
			}
			else
			{
				mCurrentRatio = CameraConfig.getInstance().getFloat(CameraConfig.ConfigMap.CuteRatio);
			}
		}
		else if(mCurrentTabType == ShutterConfig.TabType.VIDEO)
		{
			if(isSave)
			{
				CameraConfig.getInstance().saveData(CameraConfig.ConfigMap.VideoRatio, mCurrentRatio);
			}
			else
			{
				mCurrentRatio = CameraConfig.getInstance().getFloat(CameraConfig.ConfigMap.VideoRatio);
			}
		}
		else if(mCurrentTabType == ShutterConfig.TabType.GIF)
		{
			if(isSave)
			{
				CameraConfig.getInstance().saveData(CameraConfig.ConfigMap.GifRatio, mCurrentRatio);
			}
			else
			{
				mCurrentRatio = CameraConfig.getInstance().getFloat(CameraConfig.ConfigMap.GifRatio);
			}
		}
		if(!isSave)
		{
			mCurrentRatio = checkRatio(mCurrentRatio);
		}
		return mCurrentRatio;
	}

	private float checkRatio(float ratio)
	{
		if(ratio > mFullScreenRatio)
		{
			if(mFullScreenRatio >= CameraConfig.PreviewRatio.Ratio_16_9)
			{
				ratio = mFullScreenRatio;
			}
			else
			{
				ratio = CameraConfig.PreviewRatio.Ratio_4_3;
			}
		}
		return ratio;
	}

	/**
	 * 参数设置
	 *
	 * @param params 详细参看 {@link cn.poco.camera.CameraSetDataKey} 说明
	 */
	@Override
	public void SetData(HashMap<String, Object> params)
	{
		int patchCamera = -1;
		boolean forceRatio = false;
		mCurrentCameraId = -2;
		if(params != null)
		{
			if(params.containsKey(CameraSetDataKey.KEY_HAS_OPEN_ANIM))
			{
				this.mHasOpenAnim = (Boolean)params.get(CameraSetDataKey.KEY_HAS_OPEN_ANIM);
			}
			if(params.containsKey(CameraSetDataKey.KEY_START_MODE))
			{
				this.mCurrentCameraId = (Integer)params.get(CameraSetDataKey.KEY_START_MODE);
			}
			if(params.containsKey(CameraSetDataKey.KEY_PATCH_CAMERA))
			{
				patchCamera = (Integer)params.get(CameraSetDataKey.KEY_PATCH_CAMERA);
			}
			if(params.containsKey(CameraSetDataKey.KEY_PATCH_FINISH_TO_CLOSE))
			{
				this.isPatchFinishToClose = (Boolean)params.get(CameraSetDataKey.KEY_PATCH_FINISH_TO_CLOSE);
			}
			if(params.containsKey(CameraSetDataKey.KEY_HIDE_PHOTO_PICKER_BTN))
			{
				this.mHidePhotoPickerBtn = (Boolean)params.get(CameraSetDataKey.KEY_HIDE_PHOTO_PICKER_BTN);
			}
			if(params.containsKey(CameraSetDataKey.KEY_HIDE_STICKER_BTN))
			{
				this.mHideStickerBtn = (Boolean)params.get(CameraSetDataKey.KEY_HIDE_STICKER_BTN);
			}
			if(params.containsKey(CameraSetDataKey.KEY_HIDE_FILTER_SELECTOR))
			{
				this.mHideFilterSelector = (Boolean)params.get(CameraSetDataKey.KEY_HIDE_FILTER_SELECTOR);
			}
			if(params.containsKey(CameraSetDataKey.KEY_HIDE_BEAUTY_SETTING))
			{
				this.mHideBeautySetting = (Boolean)params.get(CameraSetDataKey.KEY_HIDE_BEAUTY_SETTING);
			}
			if(params.containsKey(CameraSetDataKey.KEY_HIDE_WATER_MARK))
			{
				this.mHideWaterMark = (Boolean)params.get(CameraSetDataKey.KEY_HIDE_WATER_MARK);
			}
			if(params.containsKey(CameraSetDataKey.KEY_HIDE_SETTING_BTN))
			{
				this.mHideSettingBtn = (Boolean)params.get(CameraSetDataKey.KEY_HIDE_SETTING_BTN);
			}
			if(params.containsKey(CameraSetDataKey.KEY_HIDE_RATIO_BTN))
			{
				this.mHideRatioBtn = (Boolean)params.get(CameraSetDataKey.KEY_HIDE_RATIO_BTN);
			}
			if(params.containsKey(CameraSetDataKey.KEY_HIDE_PATCH_BTN))
			{
				this.mHidePatchBtn = (Boolean)params.get(CameraSetDataKey.KEY_HIDE_PATCH_BTN);
			}
			if(params.containsKey(CameraSetDataKey.KEY_HIDE_STICKER_MANAGER_BTN))
			{
				this.mHideStickerManagerBtn = (Boolean)params.get(CameraSetDataKey.KEY_HIDE_STICKER_MANAGER_BTN);
			}
			if(params.containsKey(CameraSetDataKey.KEY_IS_BUSINESS))
			{
				this.isBusiness = (Boolean)params.get(CameraSetDataKey.KEY_IS_BUSINESS);
			}
			if(params.containsKey("channelValue"))
			{
				mChannelValue = (String)params.get("channelValue");
			}
			if(params.containsKey(CameraSetDataKey.KEY_TAILOR_MADE_SETTING))
			{
				this.isTailorMadeSetting = (Boolean)params.get(CameraSetDataKey.KEY_TAILOR_MADE_SETTING);
			}
			if(params.containsKey(CameraSetDataKey.KEY_HIDE_TAILOR_MADE_TIP))
			{
				this.mIsHideTailorMadeDialog = (Boolean)params.get(CameraSetDataKey.KEY_HIDE_TAILOR_MADE_TIP);
			}
			if(params.containsKey(CameraSetDataKey.KEY_IS_FILTER_BEAUTITY_PROCESS))
			{
				this.mIsFilterBeautifyProcess = (Boolean)params.get(CameraSetDataKey.KEY_IS_FILTER_BEAUTITY_PROCESS);
			}
			if(params.containsKey(CameraSetDataKey.KEY_FILTER_BEAUTITY_PROCESS_MASK))
			{
				this.mFilterBeautifyProcessMask = (Integer)params.get(CameraSetDataKey.KEY_FILTER_BEAUTITY_PROCESS_MASK);
			}
			if(params.containsKey(CameraSetDataKey.KEY_IS_THIRD_PARTY_VIDEO))
			{
				this.mIsThirdPartyVideo = (Boolean)params.get(CameraSetDataKey.KEY_IS_THIRD_PARTY_VIDEO);
			}
			if(params.containsKey(CameraSetDataKey.KEY_IS_THIRD_PARTY_PICTURE))
			{
				this.mIsThirdPartyPicture = (Boolean)params.get(CameraSetDataKey.KEY_IS_THIRD_PARTY_PICTURE);
			}
			if(params.containsKey(CameraSetDataKey.KEY_CAMERA_SELECT_TAB_TYPE))
			{
				this.mCurrentTabType = (Integer)params.get(CameraSetDataKey.KEY_CAMERA_SELECT_TAB_TYPE);
				this.mLastTabType = this.mCurrentTabType;
			}
			if(params.containsKey(CameraSetDataKey.KEY_CAMERA_SHOW_TAB_TYPE))
			{
				this.mShowTabType = (Integer)params.get(CameraSetDataKey.KEY_CAMERA_SHOW_TAB_TYPE);
			}
			if(params.containsKey(CameraSetDataKey.KEY_CAMERA_SELECT_RATIO))
			{
				this.mCurrentRatio = (Float)params.get(CameraSetDataKey.KEY_CAMERA_SELECT_RATIO);
				forceRatio = true;
			}
			if(forceRatio && params.containsKey(CameraSetDataKey.KEY_CAMERA_FORCE_TAB_RATIO))
			{
				this.mForceTabRatio = (Boolean) params.get(CameraSetDataKey.KEY_CAMERA_FORCE_TAB_RATIO);
			}
			if(params.containsKey(CameraSetDataKey.KEY_CAMERA_FLASH_MODE))
			{
				this.mCurrentFlashMode = (String)params.get(CameraSetDataKey.KEY_CAMERA_FLASH_MODE);
			}
			if(params.containsKey(CameraSetDataKey.KEY_CAMERA_FRONT_SPLASH_MASK))
			{
				this.mShowSplashMask = (Boolean)params.get(CameraSetDataKey.KEY_CAMERA_FRONT_SPLASH_MASK);
			}
			if(params.containsKey(CameraSetDataKey.KEY_IS_SHOW_STICKER_SELECTOR))
			{
				this.mIsShowStickerSelector = (Boolean)params.get(CameraSetDataKey.KEY_IS_SHOW_STICKER_SELECTOR);
			}
			if(params.containsKey(CameraSetDataKey.KEY_VIDEO_RECORD_TIME_TYPE))
			{
				this.mVideoRecordTimeType = (Integer)params.get(CameraSetDataKey.KEY_VIDEO_RECORD_TIME_TYPE);
			}
			if(params.containsKey(CameraSetDataKey.KEY_VIDEO_RECORD_MIN_DURATION))
			{
				this.mVideoRecordMinDuration = (Long)params.get(CameraSetDataKey.KEY_VIDEO_RECORD_MIN_DURATION);
			}
			if(params.containsKey(CameraSetDataKey.KEY_VIDEO_MULTI_SECTION_ENABLE))
			{
				this.mVideoMultiSectionEnable = (Boolean)params.get(CameraSetDataKey.KEY_VIDEO_MULTI_SECTION_ENABLE);
			}
			if(params.containsKey(CameraSetDataKey.KEY_VIDEO_MULTI_ORIENTATION_ENABLE))
			{
				this.mVideoMultiOrientationEnable = (Boolean)params.get(CameraSetDataKey.KEY_VIDEO_MULTI_ORIENTATION_ENABLE);
			}
			if(params.containsKey(CameraSetDataKey.KEY_CAMERA_STICKER_CATEGORY_ID))
			{
				this.mStickerCategoryId = (Integer)params.get(CameraSetDataKey.KEY_CAMERA_STICKER_CATEGORY_ID);
			}
			if(params.containsKey(CameraSetDataKey.KEY_CAMERA_STICKER_CATEGORY_ID_JUST_GOTO))
			{
				this.mJustGotoCategoryID = (int) params.get(CameraSetDataKey.KEY_CAMERA_STICKER_CATEGORY_ID_JUST_GOTO);
			}
			if(params.containsKey(CameraSetDataKey.KEY_CAMERA_STICKER_STICKER_ID))
			{
				this.mStickerId = (Integer)params.get(CameraSetDataKey.KEY_CAMERA_STICKER_STICKER_ID);
			}
			if (params.containsKey(CameraSetDataKey.KEY_CAMERA_STICKER_REMEMBER_STICKER_ID))
			{
				this.mRememberStickerID = (Boolean)params.get(CameraSetDataKey.KEY_CAMERA_STICKER_REMEMBER_STICKER_ID);
			}
			if(params.containsKey(CameraSetDataKey.KEY_EXTERNAL_CALL_IMG_SAVE_URI))
			{
				this.mVideoExtraUri = (Uri)params.get(CameraSetDataKey.KEY_EXTERNAL_CALL_IMG_SAVE_URI);
			}
			if(params.containsKey(CameraSetDataKey.KEY_CAMERA_FILTER_ID))
			{
				this.mFilterId = (Integer)params.get(CameraSetDataKey.KEY_CAMERA_FILTER_ID);
			}
			if(mCurrentCameraId < 0)
			{
				mCurrentCameraId = 1;
			}
		}
		if(!mRecordVideoEnable && (mCurrentTabType == ShutterConfig.TabType.GIF || mCurrentTabType == ShutterConfig.TabType.VIDEO))
		{
			checkCanRecordVideo(mCurrentTabType, new MemoryTipDialog.OnDialogClick()
			{
				@Override
				public void onClick(AlertDialogV1 dialog)
				{
					if(dialog != null)
					{
						dialog.dismiss();
					}
					closePage();
				}
			}, new DialogInterface.OnDismissListener()
			{
				@Override
				public void onDismiss(DialogInterface dialog)
				{
					mSystemUiVisibility = -1;
				}
			});
			return;
		}

		if(mSite != null && mSite.m_myParams != null)
		{
			if(mSite.m_myParams.containsKey(CameraSetDataKey.KEY_IS_PAGE_BACK))
			{
				this.isPageBack = (Boolean)mSite.m_myParams.get(CameraSetDataKey.KEY_IS_PAGE_BACK);
			}
			if(isPageBack)
			{
				if(mSite.m_myParams.containsKey("timer"))
				{
					mCurrentTimerMode = (int)mSite.m_myParams.get("timer");
				}
				if(mSite.m_myParams.containsKey("record_audio_enable"))
				{
					mAudioEnable = (Boolean)mSite.m_myParams.get("record_audio_enable");
				}
				if(mSite.m_myParams.containsKey(CameraSetDataKey.KEY_CAMERA_SHOW_TAB_TYPE))
				{
					mShowTabType = (Integer)mSite.m_myParams.get(CameraSetDataKey.KEY_CAMERA_SHOW_TAB_TYPE);
				}
				if(mSite.m_myParams.containsKey(CameraSetDataKey.KEY_CAMERA_SELECT_TAB_TYPE))
				{
					mCurrentTabType = (Integer)mSite.m_myParams.get(CameraSetDataKey.KEY_CAMERA_SELECT_TAB_TYPE);
					mLastTabType = mCurrentTabType;
				}
				if(mSite.m_myParams.containsKey(CameraSetDataKey.KEY_CAMERA_SELECT_RATIO))
				{
					mCurrentRatio = (Float)mSite.m_myParams.get(CameraSetDataKey.KEY_CAMERA_SELECT_RATIO);
				}
				if(mSite.m_myParams.containsKey(CameraSetDataKey.KEY_CAMERA_FLASH_MODE))
				{
					this.mCurrentFlashMode = (String)mSite.m_myParams.get(CameraSetDataKey.KEY_CAMERA_FLASH_MODE);
				}
				if(mSite.m_myParams.containsKey(CameraSetDataKey.KEY_CAMERA_FRONT_SPLASH_MASK))
				{
					this.mShowSplashMask = (Boolean)mSite.m_myParams.get(CameraSetDataKey.KEY_CAMERA_FRONT_SPLASH_MASK);
				}
				if(mSite.m_myParams.containsKey(CameraSetDataKey.KEY_IS_STICKER_PREVIEWBACK))
				{
					this.mIsStickerPreviewBack = (Boolean)mSite.m_myParams.get(CameraSetDataKey.KEY_IS_STICKER_PREVIEWBACK);
				}
				if(mSite.m_myParams.containsKey(CameraSetDataKey.KEY_CAMERA_STICKER_CATEGORY_ID))
				{
					this.mStickerCategoryId = (Integer)mSite.m_myParams.get(CameraSetDataKey.KEY_CAMERA_STICKER_CATEGORY_ID);
				}
				if(mSite.m_myParams.containsKey(CameraSetDataKey.KEY_CAMERA_STICKER_STICKER_ID))
				{
					this.mStickerId = (Integer)mSite.m_myParams.get(CameraSetDataKey.KEY_CAMERA_STICKER_STICKER_ID);
				}
				if(mSite.m_myParams.containsKey(CameraSetDataKey.KEY_HIDE_STICKER_MANAGER_BTN))
				{
					this.mHideStickerManagerBtn = (Boolean)mSite.m_myParams.get(CameraSetDataKey.KEY_HIDE_STICKER_MANAGER_BTN);
				}
				if(mCurrentTabType == ShutterConfig.TabType.GIF)
				{
					mIsStickerPreviewBack = false;
				}
				isTailorMadeSetting = false;
				mDisableAudioPermissionTip = true;
				mIsShowStickerSelector = false;
			}
			mTempStickerWithFilter = isPageBack;
		}

		//判断第三方是否有指定录制时长
		if((mVideoRecordTimeType & ShutterConfig.VideoDurationType.DURATION_TEN_SEC) > 0 && (mVideoRecordTimeType & ShutterConfig.VideoDurationType.DURATION_THREE_MIN) <= 0)
		{
			mTargetDuration = CameraConfig.VideoDuration.TEN_SECOND * 1000L;
			mVideoTimeLong = mTargetDuration;

		}
		else if((mVideoRecordTimeType & ShutterConfig.VideoDurationType.DURATION_TEN_SEC) <= 0 && (mVideoRecordTimeType & ShutterConfig.VideoDurationType.DURATION_THREE_MIN) > 0)
		{
			mTargetDuration = CameraConfig.VideoDuration.THREE_MIN * 1000L;
            mVideoTimeLong = mTargetDuration;
        }
        else if((mVideoRecordTimeType & ShutterConfig.VideoDurationType.DURATION_ONE_MIN) > 0) {
            mTargetDuration = CameraConfig.VideoDuration.ONE_MIN * 1000L;
            mVideoTimeLong = mTargetDuration;
        }
        if(mVideoRecordMinDuration < 1000L)
		{
			mVideoRecordMinDuration = 1000L;
		}
		else if(mVideoRecordMinDuration > mTargetDuration)
		{
			mVideoRecordMinDuration = mTargetDuration;
		}

		initPageData(patchCamera, forceRatio);
	}

	private void initPageData(int patchCamera, boolean forceRatio)
	{
		initCameraId();

		initPatchMode(patchCamera);

		initCameraViewConfig(forceRatio);

		initStickerDrawData();

		openCameraById();

		initStickerSound();

		//初始化Record
		changeVideoSize(mCurrentRatio);
		initMyRecord();

		pageOpenTime = System.currentTimeMillis();
	}

	@Override
	public void onBackResult(int siteID, HashMap<String, Object> params)
	{
		if(params != null)
		{
			if(params.containsKey(CameraSetDataKey.KEY_IS_RESUME_VIDEO_PAUSE))
			{
				boolean resumeVideo = (Boolean)params.get(CameraSetDataKey.KEY_IS_RESUME_VIDEO_PAUSE);
				if(resumeVideo && params.containsKey(CameraSetDataKey.KEY_RESUME_VIDEO_PAUSE_MGR))
				{
					this.mVideoMgr = (VideoMgr)params.get(CameraSetDataKey.KEY_RESUME_VIDEO_PAUSE_MGR);
				}
			}

			if(params.containsKey(CameraSetDataKey.KEY_IS_SHOW_STICKER_SELECTOR))
			{
				this.mIsShowStickerSelector = (Boolean)params.get(CameraSetDataKey.KEY_IS_SHOW_STICKER_SELECTOR);
			}
		}
		super.onBackResult(siteID, params);
	}

	private void closePage()
	{
		if(mBrightnessUtils != null && !mResetBrightnessDisable)
		{
			mBrightnessUtils.resetToDefault();
		}
		if(mSite != null)
		{
			TongJi2.AddCountByRes(getContext(), R.integer.拍照_退出到首页);
			mSite.onBack(getContext(), false);
		}
	}

	private void initStickerDrawData()
	{
		if(mCurrentTabType != ShutterConfig.TabType.PHOTO)
		{
			StickerDrawHelper.getInstance().setMode(mCurrentTabType == ShutterConfig.TabType.GIF);
			StickerDrawHelper.getInstance().setPreviewRatio(mCurrentRatio);
			StickerDrawHelper.getInstance().setFrameTopPaddingOnRatio1_1(RatioBgUtils.getMaskRealHeight(CameraConfig.PreviewRatio.Ratio_1_1));
			StickerDrawHelper.getInstance().setFrameTopPadding(mFrameTopPadding);
		}
	}

	private void initCameraId()
	{
		int cameraId = CameraConfig.getInstance().getInt(CameraConfig.ConfigMap.LastCameraId);

		if(mCurrentCameraId > -2 && !isPageBack)
		{//不是页面返回，不需要人脸引导自拍
			CameraConfig.getInstance().saveData(CameraConfig.ConfigMap.FaceGuideTakePicture, false);
		}
		if(mCurrentCameraId < 0 || mCurrentCameraId > 1 || isPageBack)
		{
			mCurrentCameraId = cameraId;
		}

		// 4.2.0先注释
//		UserRecord record = UserRecord.getRecord(UserRecord.RecordType.CAMERA);
//		if (record != null)
//		{
//			Object recordID = record.getRecordInfo(UserRecord.CameraRecordInfoType.FRONT_AND_BACK_LENS);
//			if (recordID != null)
//			{
//				mCurrentCameraId = (int) recordID;
//			}
//		}

		isFront = (mCurrentCameraId == 1);
	}

	private void openCameraById()
	{
		if(mPreviewView == null) return;
		CameraHandler cameraHandler = RenderHelper.getCameraHandler();
		if(cameraHandler == null)
		{
			return;
		}
		if(mCameraViewRatio >= CameraConfig.PreviewRatio.Ratio_16_9)
		{
			cameraHandler.setPreviewSize(mCameraViewWidth, (int)(mCameraViewWidth * CameraConfig.PreviewRatio.Ratio_16_9), mCameraSizeType);
		}
		else
		{
			cameraHandler.setPreviewSize(mCameraViewWidth, mCameraViewHeight, mCameraSizeType);
		}
		if(mCurrentTabType == ShutterConfig.TabType.PHOTO)
		{
			cameraHandler.setPictureSize(mCameraViewWidth, mCameraViewHeight);
		}
		CameraThread cameraThread = cameraHandler.getCamera();
		if(cameraThread != null)
		{
			cameraThread.setCameraCallback(this);
			cameraThread.setCameraAllCallback(this);
		}
		cameraHandler.openCamera(mCurrentCameraId);
		mIsInitCamera = true;
		initWidgetState();
	}

	/**
	 * 校正镜头（在open镜头之前初始化）
	 *
	 * @param patchCamera 默认为-1:不校正镜头，0:后置，1:前置
	 */
	private void initPatchMode(int patchCamera)
	{
		if(patchCamera == 0 || patchCamera == 1)
		{
			isPatchMode = true;
			isPatchOtherCamera = true;
			mCurrentCameraId = patchCamera;
		}
	}

	private void initWidgetState()
	{
		CameraHandler cameraHandler = RenderHelper.getCameraHandler();
		if(cameraHandler == null)
		{
			return;
		}
		CameraThread cameraThread = cameraHandler.getCamera();
		if(cameraThread != null)
		{
			mCameraTopControl.SetCameraNum(cameraThread.getNumberOfCameras());
		}
		mCameraTopControl.updateUI();
	}

	/**
	 * 打开镜头完成后，初始化参数
	 */
	private void initWidgetStateAfterCameraOpen(boolean isSwitchCamera)
	{
		int cameraVC = TagMgr.GetTagIntValue(getContext(), Tags.CAMERA_PAGE_VERSION);
		int appVC = CommonUtils.GetAppVerCode(getContext()); //app version code
		if (cameraVC < appVC)
		{
			TagMgr.SetTagValue(getContext(), Tags.CAMERA_PAGE_VERSION, Integer.toString(appVC));
		}

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
		//Log.i(TAG, "mCurrentCameraId:" + mCurrentCameraId);
		CameraConfig.getInstance().saveData(CameraConfig.ConfigMap.LastCameraId, mCurrentCameraId);
		final int preDegree = getPreviewPatchDegree();
		//Log.i(TAG, "preDegree:"+preDegree);
		cameraHandler.setPreviewOrientation(preDegree);//通过外部获取预览修正角度;

		//镜头矫正判断
		updatePatchConfig();
		mCameraTopControl.updateUI();
		boolean noSound = CameraConfig.getInstance().getBoolean(CameraConfig.ConfigMap.NoSound);
		if(mCurrentTabType == ShutterConfig.TabType.PHOTO)
		{
			mCurrentFlashMode = CameraConfig.getInstance().getString(CameraConfig.ConfigMap.FlashModeStr);
			mFaceGuideTakePicture = CameraConfig.getInstance().getBoolean(CameraConfig.ConfigMap.FaceGuideTakePicture);
			if(noSound)
			{
				mFaceGuideTakePicture = false;
			}
		}
		else
		{
			mCurrentFlashMode = CameraConfig.FlashMode.Off;
		}
		if(cameraThread != null)
		{
			if(mCameraPopSetting != null && isFront)
			{
				mCameraPopSetting.setShowFrontFlash(cameraThread.isFlashSupported());//vivo x9i支持前置闪光灯
			}
			cameraHandler.setFlashMode(mCurrentFlashMode);
		}

		if(mCurrentExposureValue != 0)
		{
			cameraHandler.setExposureValue(mCurrentExposureValue);
		}
		setCameraFocusState(true);
		cameraHandler.setSilenceOnTaken(noSound);

		//触屏拍照
		mTouchCapture = CameraConfig.getInstance().getBoolean(CameraConfig.ConfigMap.TouchCapture);

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

		//矫正镜头模式下，使用原图滤镜
		//隐藏滤镜选择，使用原图滤镜
		if(isPatchMode || mHideFilterSelector)
		{
			filterResId = 0;
			setCameraFilterRes(filterResId);
		}
		else
		{
			//镜头初始化打开
			if(mIsInitCamera)
			{
				if (mFilterId != -1) {
					filterResId = mFilterId;
				} else {
					filterResId = CameraConfig.getInstance().getInt(CameraConfig.ConfigMap.CameraFilterId);
				}
				setFilterMsgToastShow(false);
				setCameraFilterRes(filterResId);

				//NOTE 内置滤镜“推荐”，第一次打开拍照的滤镜列表时，默认展开“推荐”分类，直至用户点开其他分类。
				if (mCameraBottomControl!= null && !mHideFilterSelector)  {
					mCameraBottomControl.setFilterScrollToGroupByUri(1763);
				}
			}
			else
			{
				if(isSwitchCamera)
				{
					//切换镜头
					if(mCurrentFilterRes != null)
					{
						if(checkIsStickerFilter())
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
					if(mCurrentFilterRes != null)
					{
						if(checkIsStickerFilter())
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
		}

		//屏幕亮度
		if(mBrightnessUtils == null)
		{
			mBrightnessUtils = BrightnessUtils.getInstance();
			mBrightnessUtils.setContext(getContext());
			mBrightnessUtils.init();
			mBrightnessUtils.registerBrightnessObserver();
		}
		if(mBrightnessUtils != null)
		{
			mBrightnessUtils.setBrightnessToMax();
		}
		keepScreenWakeUp(true);

		//声音
		initCameraSound();
	}

	private void setCameraFocusState(boolean autoFocus)
	{
		CameraHandler cameraHandler = RenderHelper.getCameraHandler();
		if(cameraHandler == null) return;
		cameraHandler.setAutoFocus(autoFocus);
		cameraHandler.setAutoLoopFocus(autoFocus);
	}

	/**
	 * 初始化声音，平均耗时1850ms
	 * 另开线程完成初始化
	 */
	private void initCameraSound()
	{
		if(mCameraSound == null)
		{
			mCameraSound = new CameraSound();
		}
		if(!mCameraSound.hasInit())
		{
			new Thread(new Runnable()
			{
				@Override
				public void run()
				{
					mCameraSound.initSounds(getContext());
				}
			}, "initSound").start();
		}
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

	public int getPicturePatchDegree()
	{
		if(mCurrentCameraId == 0)
		{
			return CameraConfig.getInstance().getInt(CameraConfig.ConfigMap.PicturePatch_0);
		}
		else if(mCurrentCameraId == 1)
		{
			return CameraConfig.getInstance().getInt(CameraConfig.ConfigMap.PicturePatch_1);
		}
		return 0;
	}

	private void savePatchConfig()
	{
		if(mCurrentCameraId == 0)
		{
			CameraConfig.getInstance().putData(CameraConfig.ConfigMap.PreviewPatch_0, tempPreviewDegree);
			CameraConfig.getInstance().putData(CameraConfig.ConfigMap.PicturePatch_0, tempPictureDegree);
		}
		else if(mCurrentCameraId == 1)
		{
			CameraConfig.getInstance().putData(CameraConfig.ConfigMap.PreviewPatch_1, tempPreviewDegree);
			CameraConfig.getInstance().putData(CameraConfig.ConfigMap.PicturePatch_1, tempPictureDegree);
		}
		CameraConfig.getInstance().saveAllData();
	}

	private void initCameraViewConfig(boolean forceRatio)
	{
		CameraUIConfig.Builder builder = new CameraUIConfig.Builder();
		builder.setTimerMode(mCurrentTimerMode);
		builder.setShowTabType(mShowTabType);
		builder.SelectedTabType(mCurrentTabType);
		if(mVideoMgr != null)
		{
			//// FIXME: 2017/09/26  mVideoMultiSectionEnable == false 不可录制
			mVideoMgr.calculateProgressAngle();
			builder.setShutterMode(ShutterConfig.ShutterType.PAUSE_RECORD);
			builder.resumeVideoPauseStatus(mVideoMgr);
			mTargetDuration = mVideoMgr.getTargetDuration();
			mVideoTimeLong = mTargetDuration - mVideoMgr.getRecordDuration();
		}
		if(forceRatio)
		{
			mCurrentRatio = checkRatio(mCurrentRatio);
		}
		else
		{
			changeRatioByType(false);
		}

		updateLightnessSeekBarLoc(mCurrentRatio);

		if(mHideFilterSelector)
		{
			builder.isShowColorFilter(false);
		}

		//隐藏相册入口按钮
		if(mHidePhotoPickerBtn)
		{
			builder.setIsShowPhoto(false);
		}

		//隐藏贴纸按钮
		if(mHideStickerBtn)
		{
			builder.isShowSticker(false);
		}

		//隐藏美形定制按钮
		if(mHideBeautySetting)
		{
			builder.isShowBeauty(false);
		}

		if(mHideSettingBtn)
		{
			builder.setHideTopType(CameraBtnConfig.BarType.CAMERA_SETTING);
		}
		if(mHideRatioBtn)
		{
			builder.setHideTopType(CameraBtnConfig.BarType.CAMERA_RATIO);
		}
		if(mHidePatchBtn)
		{
			builder.setHideTopType(CameraBtnConfig.BarType.CAMERA_ADJUST);
		}

//		//是否显示矫正镜头按钮
//		int showCameraPatchBtnTimes = CameraConfig.getInstance().getInt(CameraConfig.ConfigMap.ShowCameraPatchBtn);
//		if(showCameraPatchBtnTimes < 3 && mCurrentTabType == ShutterConfig.TabType.PHOTO)
//		{
//			if(!CameraConfig.getInstance().getBoolean(CameraConfig.ConfigMap.UsePatchBtn) && !isPatchMode)
//			{
//				builder.setShowTopType(CameraBtnConfig.BarType.CAMERA_ADJUST);
//			}
//			CameraConfig.getInstance().saveData(CameraConfig.ConfigMap.ShowCameraPatchBtn, showCameraPatchBtnTimes + 1);
//		}
//		else
//		{
//			builder.setHideTopType(CameraBtnConfig.BarType.CAMERA_ADJUST);
//		}
		builder.setHideTopType(CameraBtnConfig.BarType.CAMERA_ADJUST);

		if(mIsShowStickerSelector)
		{
			if (mCurrentTabType == ShutterConfig.TabType.PHOTO)
			{
				mIsShowStickerSelector = false;
			}
			else
			{
				builder.setShutterMode(ShutterConfig.ShutterType.UNFOLD_RES);
			}
		}


		if(!TextUtils.isEmpty(mCurrentFlashMode))
		{
			//如果后置为手电筒模式，就关闭闪关灯
			if(!isFront && CameraConfig.FlashMode.Torch.equals(mCurrentFlashMode))
			{
				mCurrentFlashMode = CameraConfig.FlashMode.Off;
			}

			if(!isFront && !CameraConfig.getInstance().getString(CameraConfig.ConfigMap.FlashModeStr).equals(mCurrentFlashMode))
			{
				CameraConfig.getInstance().saveData(CameraConfig.ConfigMap.FlashModeStr, mCurrentFlashMode);
			}
		}

		if(mShowSplashMask && isFront)
		{
			mCurrentFlashMode = CameraConfig.FlashMode.Torch;
			CameraConfig.getInstance().saveData(CameraConfig.ConfigMap.FrontFlashModeStr, mCurrentFlashMode);
		}
		else if(!mShowSplashMask && isFront)
		{
			mCurrentFlashMode = CameraConfig.FlashMode.Off;
			CameraConfig.getInstance().saveData(CameraConfig.ConfigMap.FrontFlashModeStr, mCurrentFlashMode);
		}


		builder.setVideoDurationType(this.mVideoRecordTimeType);

		builder.isCameraFront(mCurrentCameraId == 1);
		//判断是否是全屏
		builder.setCameraPreviewRatio(mCurrentRatio > CameraConfig.PreviewRatio.Ratio_16_9 ? CameraConfig.PreviewRatio.Ratio_Full : mCurrentRatio);
		CameraUIConfig config = builder.build(getContext());
		if(mContentView != null)
		{
			mContentView.setUIConfig(config);
			if(!isPageBack)
			{
				mUIHandler.postDelayed(new Runnable()
				{
					@Override
					public void run()
					{
						/*初始化tab，相当于打开该tab类型的page*/
						sendTypePageSensorsData(mCurrentTabType, true);

                        /*poco 统计*/
						switch(mCurrentTabType)
						{
							case ShutterConfig.TabType.GIF:
								TongJi2.AddCountByRes(getContext(), R.integer.拍照_切换到表情包);
								break;

							case ShutterConfig.TabType.PHOTO:
								TongJi2.AddCountByRes(getContext(), R.integer.拍照_切换到高清拍照);
								break;

							case ShutterConfig.TabType.CUTE:
								TongJi2.AddCountByRes(getContext(), R.integer.拍照_切换到萌妆照);
								break;

							case ShutterConfig.TabType.VIDEO:
								TongJi2.AddCountByRes(getContext(), R.integer.拍照_切换到视频);
								break;
						}

						mContentView.showTitleToast(mCurrentTabType);
					}
				}, 1500);
			}
		}
		if(mRatioBgView != null)
		{
			mRatioBgView.setRatio(config.GetPreviewRatio());
			if(mCountDownView != null)
			{
				mCountDownView.setTranslationY(mRatioBgView.getDisplayAreaCenterY(mCurrentRatio) - ShareData.PxToDpi_xhdpi(148) / 2f);
			}
		}
		mFrameTopPadding = RatioBgUtils.getMaskRealHeight(mCurrentRatio);
		updateToastMsgHeight(mCurrentRatio);
		resetCameraViewSize(mCurrentRatio);
	}

	/**
	 * 加载贴纸素材资源 + 页面埋点
	 */
	private void initStickerData(boolean isInitCamera)
	{
		if(isInitCamera)
		{
			CameraStickerConfig.Builder builder = new CameraStickerConfig.Builder();
			builder.setBusinessMode(isBusiness);
			builder.setShutterType(mCurrentTabType);
			builder.setPreviewRatio(mCurrentRatio > CameraConfig.PreviewRatio.Ratio_16_9 ? CameraConfig.PreviewRatio.Ratio_Full : mCurrentRatio);
			builder.setSelectedStickerID(mStickerId);
			builder.setTailorMadeSetting(isTailorMadeSetting);
			builder.setShowStickerSelector(mIsShowStickerSelector);
			builder.isRememberUseStickerID(mRememberStickerID);
			builder.setJustGoToLabelID(mJustGotoCategoryID);
			if(mStickerCategoryId != -1)
			{
				builder.setSpecificLabel(mStickerCategoryId);
			}
			StickerResMgr.getInstance().setStickerConfig(builder.build());
		}
	}

	/**
	 * 初始化脸型
	 *
	 * @param isInitCamera 是否初始化
	 */
	private void initShape(boolean isInitCamera)
	{
		if (!isInitCamera) return;

		if (mCameraBottomControl != null && mCameraBottomControl.mBeautySelectorView != null)
		{
			mCameraBottomControl.mBeautySelectorView.setClickShapeSensor(false);
			mCameraBottomControl.mBeautySelectorView.setCameraShapeId(ShapeSPConfig.getInstance(getContext()).getShapeId());
		}
	}

	/**
	 * 初始化美颜
	 *
	 * @param isInitCamera 是否初始化
	 */
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


	/**
	 * 初始化贴纸音效参数
	 */
	private void initStickerSound()
	{
		//阿玛尼商业需要开启音效
		if(mStickerId == 39165)
		{
			mIsStickerSoundMute = false;
		}
		setHasStickerSound(false);
		checkStickerSoundMute();
	}


	/**
	 * 初始化贴纸音效manger
	 *
	 * @param isNonSticker true：选择无贴纸模式，false，默认id
	 */
	private void initStickerManager(boolean isNonSticker)
	{
		mIsStickerSoundMute = StickerSPConfig.getInstance().getStickerMute();

		if(StickerResMgr.sInstanceIsNull() && !StickerResMgr.isInitConfig())
		{
			mStickerId = StickerSPConfig.getInstance().getStickerId();
			if(isNonSticker)
			{
				mStickerId = CameraStickerConfig.STICKER_ID_NON;
			}
		}
		else
		{
			mStickerId = StickerResMgr.getInstance().getSelectedInfo(StickerResMgr.SelectedInfoKey.STICKER);
			mRememberStickerID = StickerResMgr.getInstance().isRememberUseStickerID();
		}

		mStickerSoundManager = new StickerSoundManager();
		mStickerSoundManager.init(getContext());
		mStickerSoundManager.setMediaPlayerListener(new MyStickerSoundListener());
	}

	private void initGesture(Context context)
	{
		mGestureListener = new GestureListener();
		mGestureDetector = new GestureDetector(context, mGestureListener);
		mGestureDetector.setIsLongpressEnabled(false);
	}

	private void initCallback()
	{
		//比例切换callback
		mRatioChangeListener = new RatioBgViewV2.OnRatioChangeListener()
		{
			@Override
			public void onRatioChange(float ratio)
			{
				if(mCountDownView != null)
				{
					mCountDownView.setTranslationY(mRatioBgView.getDisplayAreaCenterY(ratio) - ShareData.PxToDpi_xhdpi(148) / 2f);
				}
			}

			@Override
			public void onDismissMaskEnd()
			{
				if(mIsInitPage)
				{
					if(checkSDKValid())
					{
						showAppUpdateTips();
						mIsInitPage = false;
						return;
					}

					if(isPatchMode)// 设置 - 手动校正
					{
						onClickCameraPatch();
					}
					else
					{
						//如果有权限提示 则关闭提示
						showCameraPermissionHelper(false);

						boolean isShowTailor = checkTailorShowFlow();
						if(!isShowTailor)
						{
							showTipForUse();
						}
					}

					mIsInitPage = false;

					if (getContext() instanceof CameraActivity) {
					    mUIHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (mSite != null) {
                                    mSite.showTip(getContext());
                                }
                            }
                        });
                    }
				}

				if(mOnPause)
				{
					mOnPause = false;
				}
				notifyAllWidgetUnlockUI(100);
			}

			@Override
			public void onSplashMaskEnd()
			{
				mSplashMaskAnimEnd = true;
				if(mSaveImageDataSucceed)
				{
					sendTakePictureOnAfterMask(mImageFile);
				}
			}
		};

		mBeautySelectorCB = new PageCallbackAdapter()
		{
			@Override
			public void onLogin()
			{
				if (mSite != null) {
					mSite.uploadShapeLogin(getContext());
				}
			}

			@Override
			public void onBindPhone()
			{

			}

			@Override
			public void onShapeUpdate(int subPosition, @Nullable ShapeExAdapter.ShapeExItemInfo itemInfo, ShapeData data)
			{
				// 脸型数据更新
                requestRendererShape(data);
				updateFilterBeautyParamsByShapeData(mFilterBeautyParams, data);
			}

			@Override
			public void onShapeItemClick(@Nullable ShapeExAdapter.ShapeExItemInfo itemInfo, ShapeData data)
			{
				// 选中某款脸型数据
                requestRendererShape(data);
                if (itemInfo != null) {
                	ShapeSPConfig.getInstance(getContext()).setShapeId(itemInfo.m_uri);
				}
				updateFilterBeautyParamsByShapeData(mFilterBeautyParams, data);
			}

			@Override
			public void onBeautyUpdate(@STag.BeautyTag int type, BeautyData beautyData)
			{
				// 美颜数据更新
                requestRendererBeauty(beautyData);
                updateFilterBeautyParamsByBeautyData(mFilterBeautyParams, beautyData);
			}

			@Override
			public void setShowSelectorView(boolean show)
			{
				//动画完成后回调
				if (show) {
					onShowBeautySelectorView();
				} else {
					onCloseBeautySelectorView();
				}
			}
		};

        mEventListener = new EventCenter.OnEventListener() {

            @Override
            public void onEvent(int eventId, Object[] params) {
                if (eventId == EventID.SHOW_MULTI_DEVICE_LOGIN_DIALOG) {
                    if (mCurrentTabType == ShutterConfig.TabType.VIDEO && mUIHandler != null) {//下线弹窗 停止录制
                        mUIHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                onPauseVideo();
                            }
                        });
                    }
                }
            }
        };
        EventCenter.addListener(mEventListener);
	}

	private void initHandler()
	{
		mUIHandler = new Handler(Looper.getMainLooper())
		{
			@Override
			public void handleMessage(Message msg)
			{
				switch(msg.what)
				{
					case MSG_CANCEL_MASK: // 镜头黑色遮罩渐变动画
					{
						mRatioBgView.DoMaskDismissAnim();
						break;
					}
					case UIObserver.MSG_UNLOCK_UI: // 解ui
					{
						if(mUIObserverList != null)
						{
							mUIObserverList.notifyObservers(UIObserver.MSG_UNLOCK_UI);
						}
						break;
					}
					case MSG_GESTURE_CHANGE:
					{
						int direct = msg.arg1;
						if(direct == 0)
						{   //滤镜手势切换
							setCameraFilterRes(true);//下一个
						}
						else if(direct == 2)
						{
							setCameraFilterRes(false); //上一个
						}
						break;
					}
					case MSG_RESET_GESTURE_STATE:
					{
						if(mGestureListener != null)
						{
							mGestureListener.resetState();
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
					case MSG_DO_FOCUS_AND_METERING: // 对焦、测光
					{
						RectF rectF = (RectF)msg.obj;
						if(rectF != null)
						{
							doFocusAndMetering(true, rectF.left, rectF.top, rectF.right, rectF.bottom);
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
					case MSG_CLEAR_FOCUS_AND_METERING: // 取消对焦、测光
					{
						if(mRatioBgView != null)
						{
							mRatioBgView.setFocusFinish();
						}
						break;
					}
					case MSG_OPEN_OR_CLOSE_AUTO_FOCUS:
					{
						if(doTakePicture && mCurrentTimerMode <= 0) return;
						boolean autoFocus = (Boolean)msg.obj;
						setCameraFocusState(autoFocus);
						break;
					}
					case MSG_PATCH_SAVE_PIC_END:
					{
						if(isPageClose)
						{// 页面结束的话;
							break;
						}

						//矫正照片
						if(isPatchMode)
						{
							byte[] data = (byte[])msg.obj;
							Bitmap bitmap = null;
							if(data != null)
							{
								bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
							}
							showPatchDialog(PatchDialogType.STEP_3_PATCH_PICTURE, bitmap);
						}
						break;
					}
					case MSG_RECORD_TIMER_COUNT_DOWN:
					{
						if(mTimerCounts == 0)
						{
							notifyAllWidgetLockUI();
							mTimerCounts = -1;
							if(mCountDownView != null)
							{
								mCountDownView.setText("");
							}
							if(mCameraBottomControl != null)
							{
								mCameraBottomControl.handleRecordTimerEndEvent();
							}
						}
						else
						{
							if(mTimerCounts > 0)
							{
								if(mCountDownView != null && mCurrentTimerMode > 0)
								{
									mCountDownView.setText("" + mTimerCounts);
								}
								mTimerCounts--;
								mUIHandler.sendEmptyMessageDelayed(MSG_RECORD_TIMER_COUNT_DOWN, 1000);
							}
						}
						break;
					}
					case MSG_TIMER_COUNT_DOWN:
					{
						if(mTimerCounts == 0)
						{
							notifyAllWidgetLockUI();
							mTimerCounts = -1;
							if(mCountDownView != null)
							{
								mCountDownView.setText("");
							}
							mUIHandler.sendEmptyMessage(MSG_TAKE_PICTURE);
						}
						else
						{
							if(mCountDownView != null && mCurrentTimerMode > 0)
							{
								mCountDownView.setText("" + mTimerCounts);
							}

//                        Log.i(TAG, "timer:" + mTimerCounts+", "+CameraConfig.getInstance().getBoolean(CameraConfig.ConfigMap.NoSound)+", "+mCurrentTimerMode);
							if(mCurrentTimerMode > 1 && mTickSoundEnable/* && !CameraConfig.getInstance().CameraConfig.ConfigMap.NoSound)*/ && mCameraSound != null)
							{
								AudioControlUtils.pauseOtherMusic(getContext());
								if(mTimerCounts == 3)
								{
									mCameraSound.playSound(1, 11, true);
									mCameraSound.soundIsBusy = false;

								}
								else if(mTimerCounts == 2)
								{
									mCameraSound.playSound(mCurrentTimerMode > 2 ? 2 : 1, 11, true);
									mCameraSound.soundIsBusy = false;

								}
								else if(mTimerCounts == 1)
								{
									mCameraSound.playSound(mCurrentTimerMode > 2 ? 3 : 2, 11, true);
									mCameraSound.soundIsBusy = false;
								}
							}
							if(mTimerCounts > 0)
							{
								mTimerCounts--;
								mUIHandler.sendEmptyMessageDelayed(MSG_TIMER_COUNT_DOWN, 1000);
							}
						}
						break;
					}
					case MSG_TAKE_PICTURE:
					{
						if(mCurrentTabType == ShutterConfig.TabType.PHOTO)
						{
							CameraHandler cameraHandler = RenderHelper.getCameraHandler();
							if(cameraHandler != null)
							{
								if((!isPatchMode && !mIsFilterBeautifyProcess) || (isPatchMode && isPatchFinishToClose))
								{
									cameraHandler.takeOnePicture();
								}
								else
								{
									//cameraHandler.takeOnePicture2();//拍完之后镜头会继续预览
									cameraHandler.takePictureByType(1);//拍完之后镜头停止预览，停留最后一帧
								}
							}
							if(mUIHandler != null)
							{
								mUIHandler.removeMessages(MSG_CANCEL_TAKE_PICTURE);
								mUIHandler.sendEmptyMessageDelayed(MSG_CANCEL_TAKE_PICTURE, 5000);
							}
						}
						else
						{
							onCaptureAFrame();
						}
						break;
					}
					case MSG_CANCEL_TAKE_PICTURE:
					{
						doTakePicture = false;
						if(mCountDownView != null)
						{
							mCountDownView.setText("");
						}
						if(mCameraSound != null)
						{
							mCameraSound.stopSound();
						}
						mRecordState = RecordState.IDLE;
						if(!mOnPause && mUIObserverList != null)
						{
							mUIObserverList.notifyObservers(UIObserver.MSG_UNLOCK_UI);
						}
						break;
					}
					case MSG_CAMERA_ERROR:
					{
						showCameraPermissionHelper(true);
						break;
					}
					case MSG_SHOW_ACTION_TIPS:
					{
						//素材action提示
						final String finalActionTip = FaceAction.getActionTips(getContext(), mActionName);
						//音乐action提示（GIF不提示）
						final String finalActionMusicTip = FaceAction.getActionTips(getContext(), mMusicActionName);

						//变形action提示
						final String finalShapeActionName = mShapeActionName;
						mShapeActionName = null;

						if(finalShapeActionName != null)
						{
							showActionMsgToast(finalShapeActionName);
							//showMsgToast(finalShapeActionName, true, Gravity.CENTER);
						}
						else if(finalActionTip != null)
						{
							showActionMsgToast(finalActionTip);
							//showMsgToast(finalActionTip, true, Gravity.CENTER);
						}
						else if(finalActionMusicTip != null && isSupportStickerSound())
						{
							showActionMsgToast(finalActionMusicTip);
							//showMsgToast(finalActionMusicTip, true, Gravity.CENTER);
						}
						break;
					}
					case MSG_DELAY_PREPARE_RECORD:
					{
						initMyRecord();
						break;
					}
					case MSG_FLASH_SCREEN_ON_TAKE_PICTURE:
					{
						if(mRatioBgView != null)
						{
							mRatioBgView.showSplashMask();
						}
						break;
					}
					case MSG_HIDE_NAVIGATION_BAR:
					{
						mSystemUiVisibility = -1;
						changeSystemUiVisibility(View.GONE);
						break;
					}
					case MSG_DETECT_FACE_FINISH:
					{
						RectF firstFace = null;
						if(msg.obj != null)
						{
							firstFace = (RectF)msg.obj;
						}
						if(mCurrentTabType == ShutterConfig.TabType.PHOTO && mCurrentCameraId == 0 && !mOnPause && !doTakePicture && mFaceGuideTakePicture && mCameraSound != null)
						{
							voiceGuide(firstFace);
						}
						if(mCurrentTabType == ShutterConfig.TabType.CUTE)
						{
							//跟随人脸对焦，有人脸时每隔5s对一次焦
							if(mDoFocusWithFace == 1 && firstFace == null)
							{
								mDoFocusWithFace = 0;
							}
							else if(mDoFocusWithFace == 0 && firstFace != null)
							{
								mDoFocusWithFace = 1;
								sendEmptyMessageDelayed(MSG_DO_FOCUS_WITH_FACE, 5000);
								float x = firstFace.right;
								float y = firstFace.bottom;
								if(mCurrentRatio > CameraConfig.PreviewRatio.Ratio_16_9)
								{
									//计算镜头画面的实际位置
									float r = CameraConfig.PreviewRatio.Ratio_16_9 / mCurrentRatio;
									x = (mCameraViewWidth * (1.0f - r)) / 2.0f + x * r;
								}
								doFocusAndMetering(false, x, y, x, y);
							}
						}
						break;
					}
					case MSG_DO_FOCUS_WITH_FACE:
					{
						mDoFocusWithFace = 0;
						break;
					}
				}
			}
		};

		mUIObserverList = new UIObservable();
	}

	private void initView()
	{
		this.setBackgroundColor(Color.WHITE);

		mCameraViewRatio = CameraConfig.PreviewRatio.Ratio_16_9;
		mCameraViewWidth = ShareData.getScreenW();
		mCameraViewHeight = (int)(mCameraViewWidth * mCameraViewRatio);

		if(mCameraViewHeight > ShareData.m_screenRealHeight)
		{
			mCameraViewRatio = CameraConfig.PreviewRatio.Ratio_4_3;
			mCameraViewHeight = (int)(mCameraViewWidth * mCameraViewRatio);
		}

		mRootView = new FrameLayout(getContext());
		int bottomPadding = 0;
		if(mFullScreenRatio > CameraConfig.PreviewRatio.Ratio_16_9)
		{
			if(mFullScreenRatio > CameraConfig.PreviewRatio.Ratio_17$25_9)
			{
				bottomPadding = ShareData.m_screenRealHeight - mCameraViewHeight - CameraPercentUtil.WidthPxToPercent(100);
			}
			else
			{
				bottomPadding = ShareData.m_screenRealHeight - mCameraViewHeight;
			}
		}
		LayoutParams params = new LayoutParams(mCameraViewWidth, ShareData.m_screenRealHeight);
		addView(mRootView, params);
		{
			// 预览层
			mPreviewView = new CameraRenderView(getContext());
			params = new LayoutParams(mCameraViewWidth, mCameraViewHeight);
			params.gravity = Gravity.BOTTOM;
			params.bottomMargin = bottomPadding;
			mRootView.addView(mPreviewView, params);

			// 遮罩层
			mRatioBgView = new RatioBgViewV2(getContext());
			mRatioBgView.setBgColor(Color.WHITE);
			mRatioBgView.SetOnRatioChangeListener(mRatioChangeListener);
			params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
			mRootView.addView(mRatioBgView, params);

			mContentRootView = new FrameLayout(getContext());
			params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
			params.bottomMargin = bottomPadding;
			mRootView.addView(mContentRootView, params);

			//倒计时
			mCountDownView = new CountDownView(getContext());
			mCountDownView.setText("");
			params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			params.gravity = Gravity.CENTER_HORIZONTAL;
			mContentRootView.addView(mCountDownView, params);


			mContentView = new CameraLayoutV3(getContext());
			mContentView.setUIObserver(mUIObserverList);
			mContentView.setCameraPageListener(this);

			params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			mContentRootView.addView(mContentView, params);

			mCameraTopControl = mContentView.mCameraTopControl;
			mCameraBottomControl = mContentView.mCameraBottomControl;
			mCameraPopSetting = mContentView.mCameraPopSetting;

			mCameraBottomControl.setCameraFilterListener(this);
			mCameraBottomControl.setBeautySelectorCB(mBeautySelectorCB);
			mCameraBottomControl.mStickerView.setUnLockUIListener(this);
			mCameraBottomControl.mCameraFilterRecyclerView.setUnLockUIListener(this);
			mCameraPopSetting.setUIListener(this);

		}
		// 弹窗层
		mPopFrameView = new FrameLayout(getContext());
		mPopFrameView.setBackgroundDrawable(null);
		params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
		addView(mPopFrameView, params);
		setPopView(mPopFrameView);

		mLightnessSeekBar = new CameraLightnessSeekBar(getContext());
		mLightnessSeekBar.setVisibility(GONE);
		mLightnessSeekBar.setOnSeekBarChangeListener(new CameraLightnessSeekBar.OnSeekBarChangeListener()
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
		});
		params = new LayoutParams(CameraPercentUtil.HeightPxToPercent(200), CameraPercentUtil.HeightPxToPercent(288));
		params.gravity = Gravity.END;
		addView(mLightnessSeekBar, params);

		notifyAllWidgetLockUI();
	}

	/**
	 * 检查美形定制 流程
	 */
	private boolean checkTailorShowFlow()
	{
		if(!isPatchMode)
		{
			if(mHideBeautySetting) return false;
            if(mIsHideTailorMadeDialog) return false;

			if(BeautyGuideView.IsShowGuide(getContext()))
			{
				if (isTailorMadeSetting) {
					BeautyGuideView.SetGuideTag(getContext(), true);
					checkTailorShowFlow();
					return true;
				}
				if(!mIsThirdPartyPicture && !mIsThirdPartyVideo)
				{ // 不是第三方调用

					if(mIsDoingAnim)
					{
						if(mUIHandler != null)
						{
							mUIHandler.postDelayed(new Runnable()
							{
								@Override
								public void run()
								{
									showTailorMadeTipsView();
								}
							}, 250);
						}
					}
					else
					{
						showTailorMadeTipsView();
					}
					return true;
				}
				else
				{
					return false;
				}
			}
			else if(isTailorMadeSetting)
			{ // 首页 - 设置 - 美形定制
				updateTailorTag();

				if(mIsDoingAnim)
				{
					if(mUIHandler != null)
					{
						mUIHandler.postDelayed(new Runnable()
						{
							@Override
							public void run()
							{
								showBeautySelectorView(false);
							}
						}, 250);
					}
				}
				else
				{
					showBeautySelectorView(false);
				}
				return true;
			}
		}

		return false;
	}


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

	private void showBeautySelectorView(boolean hasAnim)
	{
		if (mCameraBottomControl != null)
		{
			mCameraBottomControl.OpenBeautySelector(hasAnim);
		}
	}

	private void initBeautyGuideView()
	{
		if(mBeautyGuideView == null)
		{
			mBeautyGuideView = new BeautyGuideView(getContext());
			mBeautyGuideView.SetOnStateListener(this);
			PointF pointF = mCameraBottomControl.GetBeautySettingLoc();
			mBeautyGuideView.SetDismissEndLoc(pointF.x, pointF.y);
			LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
			this.addView(mBeautyGuideView, params);
		}
		else
		{
			setStickerEnable(false, 0);
			pauseStickerSound();
		}

		// 是否隐藏镜头矫正
		if(mCameraTopControl.GetCameraPatchState())
		{
			mCameraTopControl.SetCameraPatchState(false);
			mBeautyGuideView.SetPathStateB4Anim(true); // 记录曾经gone过镜头校正btn
			mCameraTopControl.updateUI();
		}
	}

	private void showTailorMadeTipsView()
	{
		initBeautyGuideView();
		if(mBeautyGuideView != null)
		{
			mBeautyGuideView.ShowGuide();
		}
	}

	public boolean isShowTailorMadeView()
	{
		return mBeautyGuideView != null && (mBeautyGuideView.isDoingAnim() || mBeautyGuideView.isBeautyGuideAlive());
	}

	private void initTips()
	{
		int bottomMargin = 0;
		if(mFullScreenRatio > CameraConfig.PreviewRatio.Ratio_16_9)
		{
			if(mCurrentRatio <= CameraConfig.PreviewRatio.Ratio_16_9)
			{
				bottomMargin = ShareData.m_screenRealHeight - Math.round(mCameraViewWidth * CameraConfig.PreviewRatio.Ratio_16_9) - RatioBgUtils.getTopPaddingHeight(mCurrentRatio);
			}
		}
		LayoutParams params = null;
		if(mTips == null)
		{
			mTips = new TipsView(getContext());
			params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			params.bottomMargin = bottomMargin;
			addView(mTips, params);
		}
		else
		{
			params = (LayoutParams)mTips.getLayoutParams();
			if(params != null && bottomMargin != params.bottomMargin)
			{
				params.bottomMargin = bottomMargin;
				mTips.setLayoutParams(params);
			}
		}
	}

	private void showTipForUse()
	{
		if(mIsThirdPartyPicture || mIsThirdPartyVideo || isBusiness) return;
		if(mCurrentTabType == ShutterConfig.TabType.GIF)
		{
			return;
		}
		if(mCameraBottomControl != null && mCameraBottomControl.GetShutterMode() != ShutterConfig.ShutterType.DEF)
		{
			return;
		}
		if((mShowTabType & ShutterConfig.TabType.VIDEO) == 0 || (mShowTabType & ShutterConfig.TabType.CUTE) == 0)
		{
			return;
		}

		switch(mCurrentTabType)
		{
			case ShutterConfig.TabType.VIDEO:
			{
				if(!mIsShowVideoUseTips) return;

				initTips();
				mTips.setTabType(mCurrentTabType);

				mIsShowVideoUseTips = false;
				TagMgr.SetTag(getContext(), Tags.CAMERA_VIDEO_USE_TIPS);
				TagMgr.Save(getContext());
				mTips.setPreviewRatio(mCurrentRatio);
				mUIHandler.postDelayed(new Runnable()
				{
					@Override
					public void run()
					{
						mTips.doShutterTipsZoomAnim(0, 1);
					}
				}, 30);
				break;
			}

			case ShutterConfig.TabType.PHOTO:
			{
//				if(!mIsShowPhotoUseTips) return;
//
//				initTips();
//				mTips.setTabType(mCurrentTabType);
//
//				mIsShowPhotoUseTips = false;
//				TagMgr.SetTag(getContext(), Tags.CAMERA_PHOTO_USE_TIPS);
//				TagMgr.Save(getContext());
//
//				mUIHandler.postDelayed(new Runnable()
//				{
//					@Override
//					public void run()
//					{
//						if(mCameraBottomControl.mScrollView != null)
//						{
//							mTips.setStickerLogoLocation(mCameraBottomControl.mScrollView.getStickerLogoCenter());
//						}
//						mTips.doVideoLogoTipsZoomAnim(0, 1);
//					}
//				}, 30);
				break;
			}

			case ShutterConfig.TabType.CUTE:
			{
				if(!mIsShowCuteBeautyUseTips) return;

				initTips();
				mTips.setTabType(mCurrentTabType);

				mIsShowCuteBeautyUseTips = false;
//				mIsShowCuteRatioUseTips = false;
				TagMgr.SetTagValue(getContext(), Tags.CAMERA_CUTE_USE_TIPS_FOR_BEAUTY, String.valueOf(mVersionCode));
//				TagMgr.SetTag(getContext(), Tags.CAMERA_CUTE_USE_TIPS_FOR_RATIO);
				TagMgr.Save(getContext());

				mUIHandler.postDelayed(new Runnable()
				{
					@Override
					public void run()
					{
						if(mCameraTopControl != null)
						{
//							mTips.setRationBtnLocation(mCameraTopControl.GetRatioLoc());
							mTips.setBeautyBtnLocation(mCameraBottomControl.GetBeautySettingLoc());
						}
//						mTips.doRatioTipsZoomAnim(0, 1);
						mTips.doBeautyTipsZoomAnim(0, 1);
					}
				}, 30);
				break;
			}
		}
	}

	private boolean checkSDKValid()
	{
		return !PocoDetector.detectFaceSdkIsValid(new Date());
	}

	private void showAppUpdateTips()
	{
		if(mAppUpdateTips == null)
		{
			mAppUpdateTips = new SdkOutDatedDialog((Activity)getContext());
			mAppUpdateTips.SetSubText(getContext().getString(R.string.camerapage_sdk_out_of_date_tip_msg));
			mAppUpdateTips.setOnCancelListener(new DialogInterface.OnCancelListener()
			{
				@Override
				public void onCancel(DialogInterface dialog)
				{
					appUpdateCancelFlow();

				}
			});
			mAppUpdateTips.setOnDismissListener(new DialogInterface.OnDismissListener()
			{
				@Override
				public void onDismiss(DialogInterface dialog)
				{
					mSystemUiVisibility = -1;
				}
			});
			mAppUpdateTips.setCallback(new SdkOutDatedDialog.SdkDialogCallback()
			{
				@Override
				public void updateNow()
				{
					try
					{
						Uri uri = Uri.parse("market://details?id=" + getContext().getPackageName());
						Intent intent = new Intent(Intent.ACTION_VIEW, uri);
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						getContext().startActivity(intent);
					}
					catch(Throwable e)
					{
						e.printStackTrace();
						Toast.makeText(getContext(), getResources().getString(R.string.not_installed_app_store), Toast.LENGTH_LONG).show();
					}
				}

				@Override
				public void updateLater()
				{
					appUpdateCancelFlow();
				}
			});
		}

		if(!mAppUpdateTips.isShowingDialog())
		{
			mAppUpdateTips.show();
		}
	}

	/**
	 * app update tips dismiss 的流程
	 */
	private void appUpdateCancelFlow()
	{
		// 取消美形定制 指引
		if(BeautyGuideView.IsShowGuide(getContext()))
		{
			BeautyGuideView.SetGuideTag(getContext(), true);
		}

		// 更新 美形定制logo 带 new
//		if(mCameraTopControl != null && mTailorConfig != null && mTailorConfig.isShowNewLogo())
//		{
//			mCameraTopControl.UpdateBeautyIcon();
//		}

		// 继续 镜头矫正 或者 美形定制 流程
		if(isPatchMode)
		{
			onClickCameraPatch();
			if(mUIHandler != null)
			{
				mUIHandler.sendEmptyMessageDelayed(UIObserver.MSG_UNLOCK_UI, 100);
			}
		}
		else
		{
			boolean isShowTailor = checkTailorShowFlow();
			if(!isShowTailor)
			{
				showTipForUse();
			}
			if(mUIHandler != null)
			{
				mUIHandler.sendEmptyMessageDelayed(UIObserver.MSG_UNLOCK_UI, 100);
			}
		}
	}

	private void notifyAllWidgetLockUI()
	{
		if(mUIObserverList != null)
		{
			mUIObserverList.notifyObservers(UIObserver.MSG_LOCK_UI);
		}
	}

	private void notifyAllWidgetUnlockUI(long daley)
	{
		if(mUIHandler != null)
		{
			mUIHandler.sendEmptyMessageDelayed(UIObserver.MSG_UNLOCK_UI, daley);
		}
	}

	private void setPopView(FrameLayout frameLayout)
	{
		if(mCameraBottomControl != null)
		{
			mCameraBottomControl.setPopView(frameLayout);
		}
	}

	private void updateToastMsgHeight(float ratio)
	{
		if(mContentView != null)
		{
			if(ratio > CameraConfig.PreviewRatio.Ratio_16_9)
			{
				ratio = mFullScreenRatio;
			}
			int height = Math.round(RatioBgUtils.GetTopHeightByRatio(ratio) + (ShareData.m_screenRealWidth * ratio) / 2.0f) * 2;
			mContentView.updateColorFilterToastMsgHeight(height);
		}
	}

	private void resetCameraViewSize(float ratio)
	{
		if(mPreviewView != null)
		{
			if(ratio <= CameraConfig.PreviewRatio.Ratio_4_3)
			{
				if(mCameraViewRatio == CameraConfig.PreviewRatio.Ratio_4_3)
				{
					return;
				}
				else
				{
					ratio = CameraConfig.PreviewRatio.Ratio_4_3;
				}
			}
			else if(ratio == CameraConfig.PreviewRatio.Ratio_Full)
			{
				ratio = mFullScreenRatio;
			}

			int bottomMargin1 = 0;
			int bottomMargin2 = 0;
			FrameLayout.LayoutParams params = null;
			if(mFullScreenRatio > CameraConfig.PreviewRatio.Ratio_16_9)
			{
				if(ratio <= CameraConfig.PreviewRatio.Ratio_16_9)
				{
					bottomMargin1 = ShareData.m_screenRealHeight - Math.round(mCameraViewWidth * CameraConfig.PreviewRatio.Ratio_16_9) - RatioBgUtils.getTopPaddingHeight(ratio);
				}
				bottomMargin2 = bottomMargin1;
				params = (LayoutParams)mContentRootView.getLayoutParams();
				if(params != null && bottomMargin1 != params.bottomMargin)
				{
					params.bottomMargin = bottomMargin1;
					mContentRootView.setLayoutParams(params);
				}
			}
			if(ratio <= CameraConfig.PreviewRatio.Ratio_4_3)
			{
				bottomMargin2 += ShareData.m_screenRealHeight - Math.round(mCameraViewWidth * CameraConfig.PreviewRatio.Ratio_4_3) - RatioBgUtils.getTopPaddingHeight(ratio) - bottomMargin1;
			}
			params = (LayoutParams)mPreviewView.getLayoutParams();
			params.height = (int)(params.width * ratio);
			params.bottomMargin = bottomMargin2;
			mPreviewView.setLayoutParams(params);

			mCameraViewRatio = ratio;
			mCameraViewHeight = (int)(mCameraViewWidth * mCameraViewRatio);
		}
	}

	/**
	 * @param viewRatio
	 * @param targetRatio
	 * @return true 重新了打开了镜头
	 */
	private boolean resetViewAndCameraRatio(float viewRatio, float targetRatio)
	{
		mRatioBgView.DoChangedRatioAnim(targetRatio);
		if(targetRatio == CameraConfig.PreviewRatio.Ratio_Full)
		{
			mCurrentRatio = mFullScreenRatio;
		}
		else
		{
			mCurrentRatio = targetRatio;
		}
		mFrameTopPadding = RatioBgUtils.getMaskRealHeight(targetRatio);
		updateToastMsgHeight(targetRatio);
		if(mCameraViewRatio == CameraConfig.PreviewRatio.Ratio_4_3 && targetRatio <= CameraConfig.PreviewRatio.Ratio_4_3)
		{
			mDoCameraOpenAnim = true;//不重新打开镜头
			return false;
		}
		else
		{
			boolean needReopenCamera = true;
			if(mCameraViewRatio >= CameraConfig.PreviewRatio.Ratio_16_9 && targetRatio >= CameraConfig.PreviewRatio.Ratio_16_9)
			{
				needReopenCamera = false;
			}
			resetCameraViewSize(viewRatio);

			if(!needReopenCamera)
			{
				mDoCameraOpenAnim = true;//不重新打开镜头
				return false;
			}
			CameraHandler cameraHandler = RenderHelper.getCameraHandler();
			if(cameraHandler != null)
			{
				if(mCameraViewRatio >= CameraConfig.PreviewRatio.Ratio_16_9)
				{
					cameraHandler.setPreviewSize(mCameraViewWidth, (int)(mCameraViewWidth * CameraConfig.PreviewRatio.Ratio_16_9), mCameraSizeType);
				}
				else
				{
					cameraHandler.setPreviewSize(mCameraViewWidth, mCameraViewHeight, mCameraSizeType);
				}
				if(mCameraViewRatio == CameraConfig.PreviewRatio.Ratio_4_3)
				{
					cameraHandler.setPictureSize(mCameraViewWidth, mCameraViewHeight);
				}
				cameraHandler.reopenCamera();
				RenderHelper.sCameraIsChange = true;
			}
		}
		return true;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		boolean upEvent = event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL;

		if(mGestureDetector != null)
		{
			if(!mGestureDetector.onTouchEvent(event) && upEvent)
			{
				mLightnessSeekBar.hideValueText();
				mUIHandler.sendMessageDelayed(Message.obtain(mUIHandler, MSG_HIDE_LIGHTNESS_SEEK_BAR), 3000);
			}
			return true;
		}
		return super.onTouchEvent(event);
	}

	@Override
	public void onItemClickFilterRes(FilterRes filterRes, boolean isShowFilterMsgToast)
	{
		if(isBusiness && !TextUtils.isEmpty(mChannelValue) && ChannelValue.AD84.equals(mChannelValue))
		{
			//阿玛尼定制
			filterRes = new FilterRes();
			filterRes.m_id = 2000000;
		}
		if(filterRes != null)
		{
			setColorFilter(filterRes);
			if((mCurrentFilterRes != null
					&& mCurrentFilterRes.m_id == filterRes.m_id) || (mCurrentFilterRes == null && isPageBack) || (mCurrentFilterRes == null && filterRes.m_id == 0))
			{
			}
			else
			{
				if (isShowFilterMsgToast)
				{
					showFilterMsgToast(filterRes.m_id == 0 ? getContext().getString(R.string.filter_type_none) : filterRes.m_name);
				}
			}
		}
		setUsedStickerFilter(false);
		mCurrentFilterRes = filterRes;
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

	@Override
	public void onItemClickFilterDownloadMore()
	{
		if(mSite != null)
		{
			onPause();
			TongJi2.AddCountByRes(getContext(), R.integer.拍照_滤镜下载更多);
			int clickResId = -1;
			switch(mCurrentTabType)
			{
				case ShutterConfig.TabType.GIF:
					clickResId = R.string.拍照_表情包拍摄_滤镜列表_下载更多;
					break;
				case ShutterConfig.TabType.PHOTO:
					clickResId = R.string.拍照_美颜拍照_滤镜列表_下载更多;
					break;
				case ShutterConfig.TabType.CUTE:
					clickResId = R.string.拍照_萌妆照拍摄页_滤镜列表_下载更多;
					break;
				case ShutterConfig.TabType.VIDEO:
					clickResId = R.string.拍照_视频拍摄页_滤镜列表_下载更多;
					break;
			}
			if(clickResId != -1)
			{
				MyBeautyStat.onClickByRes(clickResId);
			}
			if(mSite != null) mSite.openDownloadMoreFilter(getContext(), ResType.FILTER);
		}
	}


	private boolean checkIsStickerFilter()
	{
		return mCurrentFilterRes != null && mCurrentFilterRes.m_isStickerFilter;
	}

	private boolean isUsedStickerFilter()
	{
		return mCameraBottomControl != null && mCameraBottomControl.isUsedStickerFilter();
	}

	private void setUsedStickerFilter(boolean isUsedStickerFilter)
	{
		if(mCameraBottomControl != null)
		{
			mCameraBottomControl.setUsedStickerFilter(isUsedStickerFilter);
		}
	}

	/**
	 * 设置滤镜列表对滤镜id
	 *
	 * @param filterResId
	 */
	private void setCameraFilterRes(int filterResId)
	{
		if(mCameraBottomControl != null)
		{
			mCameraBottomControl.setFilterUri(filterResId, true, true);
		}
	}

	/**
	 * 是否显示滤镜名称toast，在调用callback之前设置
	 * @param show
	 */
	private void setFilterMsgToastShow(boolean show) {
		if (mCameraBottomControl != null) {
			mCameraBottomControl.setShowFilterMsgToast(show);
		}
	}

	/**
	 * 取消滤镜列表的选择（无回调）
	 */
	private void cancelCameraFilterSelect()
	{
		if(mCameraBottomControl != null) mCameraBottomControl.cancelFilterUri();
	}


	/**
	 * 滤镜手势切换
	 *
	 * @param next 下一个
	 */
	private void setCameraFilterRes(boolean next)
	{
		if(mHideFilterSelector) return;

		if(mCameraBottomControl != null && mCameraBottomControl.IsOpenOtherPage())
		{
			mCameraBottomControl.CloseOpenedPage();
		}

		if (mCameraBottomControl != null)
		{
			mCameraBottomControl.setCameraFilterNext(next);
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

	private boolean adjustCameraBrightness(int offset)
	{
		boolean result = false;
		CameraHandler cameraHandler = RenderHelper.getCameraHandler();
		CameraThread cameraThread = null;
		if(cameraHandler != null)
		{
			cameraThread = cameraHandler.getCamera();
		}
		if(cameraThread != null && (offset == -1 || offset == 1))
		{
			if(mCurrentExposureValue == 0)
			{
				mCurrentExposureValue = cameraThread.getExposureValue();
			}
			int exposureValue = mCurrentExposureValue + offset;
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
			if(result)
			{
				if(mBrightnessToast == null)
				{
					mBrightnessToast = new Toast(getContext());
					mBrightnessToast.setDuration(Toast.LENGTH_SHORT);
					mBrightnessToast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, ShareData.getRealPixel_720P(130));

					Drawable drawable = getResources().getDrawable(R.drawable.camera_brightness_tip_icon);
					drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());

					TextView msgView = new TextView(getContext());
					msgView.setGravity(Gravity.CENTER_VERTICAL);
					msgView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14f);
					msgView.setTextColor(0xffffffff);
					msgView.setBackgroundDrawable(DrawableUtils.shapeDrawable(0x3e000000, ShareData.getRealPixel_720P(45)));
					msgView.setPadding(ShareData.getRealPixel_720P(18), ShareData.getRealPixel_720P(5), ShareData.getRealPixel_720P(18), ShareData.getRealPixel_720P(5));
					msgView.setCompoundDrawables(drawable, null, null, null);
					msgView.setCompoundDrawablePadding(ShareData.getRealPixel_720P(8));
					msgView.setMinWidth(ShareData.getRealPixel_720P(170));
					LayoutParams fParams = new LayoutParams(ShareData.getRealPixel_720P(164), LayoutParams.WRAP_CONTENT);
					msgView.setLayoutParams(fParams);

					mBrightnessToast.setView(msgView);
				}

				View toastView = mBrightnessToast.getView();
				if(toastView != null && toastView instanceof TextView)
				{
					if(mCurrentExposureValue < 0)
					{
						((TextView)toastView).setText(getResources().getString(R.string.camerapage_exposure_value, mCurrentExposureValue));
					}
					else
					{
						((TextView)toastView).setText(getResources().getString(R.string.camerapage_exposure_value_up, mCurrentExposureValue));
					}
					mBrightnessToast.show();
				}
			}
		}
		return result;
	}

	private void showFilterMsgToast(String msg)
	{
		if(mContentView != null)
		{
			mContentView.showColorFilterToast(msg);
		}
	}

	private void showActionMsgToast(String msg)
	{
		if(msg == null)
		{
			return;
		}
		if(mContentView != null)
		{
			mContentView.showActionToast(msg);
		}
	}

	private void dismissMsgToast()
	{
		if(mContentView != null)
		{
			mContentView.cancelToast();
		}
	}

	@Override
	public void onBack()
	{
        if (mCameraErrorTipsDialog == null) {
            //矫正dialog 不可返回
            FrameLayout topView = MyFramework.GetTopView(getContext());
            if(isPatchMode && topView != null && topView.getChildCount() > 0)
            {
                View childAt = topView.getChildAt(0);
                if(childAt != null && childAt.getTag() instanceof String && ((String)childAt.getTag()).equals(PatchMsgView.PATCH_VIEW_TAG))
                {
                    return;
                }
            }

            if(isCountDown()) // 取消定时
            {
                onCancelCountDown();
                return;
            }
            if(mCameraBottomControl != null && mCameraBottomControl.CloseUnlockView())
            {
                return;
            }
            if(mCameraBottomControl != null && mCameraBottomControl.IsOpenOtherPage())
            {
                mCameraBottomControl.CloseOpenedPage();
                return;
            }

            if(mCurrentTabType == ShutterConfig.TabType.VIDEO && mCameraBottomControl != null)
            {
                if(mRecordState == RecordState.RECORDING)
                {
                    onPauseVideo();
                    return;
                }

                if(mCameraBottomControl.isPauseRecording())
                {
                    onClearAllVideo();
                    return;
                }
            }
        }

		mFaceGuideTakePicture = false;
		if(mSite != null)
		{
			TongJi2.AddCountByRes(getContext(), R.integer.拍照_关闭);
			MyBeautyStat.onClickByRes(R.string.拍照_拍照_切换模式_退出拍照);
			mSite.onBack(getContext());
		}
	}

	public void resumeResource()
	{
		mPauseDetectOnTabChange = false;
		StickerDrawHelper.getInstance().onResume();
		// 分段录制的时候，没有停止录制，退到后台，要更新时长
		if(mVideoMgr != null && mCameraBottomControl != null && mCurrentTabType == ShutterConfig.TabType.VIDEO && mCameraBottomControl.GetShutterMode() != ShutterConfig.ShutterType.UNFOLD_RES)
		{
			if(!mCameraBottomControl.IsOpenOtherPage())
			{
				int realVideoSize = mVideoMgr.getVideoNum();
				// 当已成功录制的视频个数为零时, 需要刷新底部 ui 样式
				if(realVideoSize == 0)
				{
					mCameraBottomControl.resetUIToVideoDefMode(); // 主要解决刚切换到暂停ui,就回退到后台,切回前台时ui不对的问题
				}
				else
				{
					mCameraBottomControl.checkVideoSize(realVideoSize);
					// 如果小于1000毫秒，则直接显示mTargetDuration时长，否则显示实际录制时长
					if (mVideoMgr.processLaseSecond() && mTargetDuration - mVideoMgr.getRecordDuration() < 1000) {
						mCameraBottomControl.setRecordTimeText(mVideoMgr.getDurationStr(mTargetDuration));
					} else {
						mCameraBottomControl.setRecordTimeText(mVideoMgr.getDurationStr());
					}
				}
				// 解决 刚暂停录制就锁屏，快门ui置灰的问题
				if(isValidVideoTimeLong())
				{
					mResetRecordUIState = true;
					mCameraBottomControl.setShutterEnable(ShutterConfig.RecordStatus.CAN_RECORDED);
				}
			}
		}

		if(mBrightnessUtils != null)
		{
			mBrightnessUtils.setContext(getContext());
			mBrightnessUtils.init();
			mBrightnessUtils.setBrightnessToMax();
			mBrightnessUtils.registerBrightnessObserver();
		}
		keepScreenWakeUp(true);

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

		initCameraSound();
		CameraUtils.setSystemVolume(getContext(), CameraConfig.getInstance().getBoolean(CameraConfig.ConfigMap.NoSound));

		if(mRecordManager != null)
		{
			mVideoIsPause = false;
			mRecordManager.setContext(getContext());
			mRecordManager.initMediaPlayer();

			if(mCurrentTabType == ShutterConfig.TabType.VIDEO)
			{
				checkAudioRecordPermission();
				mRecordManager.setAudioRecordEnable(mAudioEnable);
			}
			prepareRecord(0);
		}
		mIsPageResult = false;
		// 重新显示页面时需要重置状态
		mLaseSecondPause = false;
	}

	public void recycleResource()
	{
		mPauseDetectOnTabChange = false;
		if(isCountDown())
		{
			onCancelCountDown();
		}
		pauseStickerSound();
		if(mCanStopRecordState > -1)
		{
			mCanStopRecordState = 2;
		}
		onPauseVideo();

		if(mRecordManager != null)
		{
			mRecordManager.releaseAll(true);
		}

		if(mBrightnessUtils != null)
		{
			mBrightnessUtils.unregisterBrightnessObserver();
			if(!mResetBrightnessDisable)
			{
				mBrightnessUtils.resetToDefault();
			}
		}
		keepScreenWakeUp(false);
		if(mBrightnessToast != null)
		{
			mBrightnessToast = null;
		}

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
		if(mCameraSound != null)
		{
			mCameraSound.stopSound();
		}
		CameraUtils.setSystemVolume(getContext(), false);
		AudioControlUtils.resumeOtherMusic(getContext());

		CameraConfig.getInstance().saveAllData();

		if(mCameraPopSetting != null && mCameraPopSetting.isAlive())
		{
			mCameraPopSetting.dismissWithoutAnim();
		}
		if(mRatioBgView != null)
		{
			mRatioBgView.setFocusFinish();
		}
		dismissMsgToast();
		removeAllMsg();
	}

	private void removeAllMsg()
	{
		if(mUIHandler != null)
		{
			mUIHandler.removeMessages(MSG_RECORD_TIMER_COUNT_DOWN);
			mUIHandler.removeMessages(MSG_TAKE_PICTURE);
			mUIHandler.removeMessages(MSG_CANCEL_MASK);
			mUIHandler.removeMessages(MSG_TIMER_COUNT_DOWN);
			mUIHandler.removeMessages(MSG_GESTURE_CHANGE);
			mUIHandler.removeMessages(MSG_RESET_GESTURE_STATE);
			mUIHandler.removeMessages(MSG_DO_FOCUS_AND_METERING);
			mUIHandler.removeMessages(MSG_CLEAR_FOCUS_AND_METERING);
			mUIHandler.removeMessages(MSG_PATCH_SAVE_PIC_END);
			mUIHandler.removeMessages(MSG_OPEN_OR_CLOSE_AUTO_FOCUS);
			mUIHandler.removeMessages(MSG_SHOW_ACTION_TIPS);
		}
	}

	@Override
	public void onResume()
	{
		super.onResume();
		KeyBroadCastReceiver.registerListener(this);
		if(mPageInitCount > 1 && mPagePauseCount > 0)
		{
			mPagePauseCount--;
			if(mPagePauseCount != 0)
			{
				return;
			}
		}
		CameraConfig.getInstance().initAll(getContext());

		if(mRatioBgView != null)
		{
			mDoCameraOpenAnim = true;
			mRatioBgView.showBlackMask();
		}
		if(mPreviewView != null)
		{
			if(mCameraViewRatio >= CameraConfig.PreviewRatio.Ratio_16_9)
			{
				mPreviewView.setPreviewSize(mCameraViewWidth, (int)(mCameraViewWidth * CameraConfig.PreviewRatio.Ratio_16_9), mCameraSizeType);
			}
			else
			{
				mPreviewView.setPreviewSize(mCameraViewWidth, mCameraViewHeight, mCameraSizeType);
			}
			mPreviewView.onResume();
		}
		if(mCameraBottomControl != null && mCameraBottomControl.mStickerView != null)
		{
			mCameraBottomControl.mStickerView.onResume();
		}
		if(mCameraBottomControl != null && mCameraBottomControl.mBeautySelectorView != null)
		{
			mCameraBottomControl.mBeautySelectorView.onResume();
		}
		initShape(true);
		initBeauty(true);

		// 多个镜头页时，同步最新素材信息
		if(mCameraBottomControl != null && mCameraBottomControl.mStickerView != null)
		{
			// 由于第三方调用的时候，先执行 onResume(), 再执行 initStickerData(), 导致无参情况下load素材
			if(!StickerResMgr.sInstanceIsNull() && StickerResMgr.isInitConfig())
			{
				StickerResMgr.getInstance().setShutterType(mCurrentTabType);
				StickerResMgr.getInstance().setPreviewRatio(mCurrentRatio);
				mCameraBottomControl.mStickerView.registerStickerResMgrCB();
				mCameraBottomControl.mStickerView.onRefreshAllData();
			}
		}

		setStickerDrawListener(mOnDrawStickerResListener);
		resumeStickerSound();
		resumeResource();

		mIsPauseDetect = false;
		if(mCurrentTabType != ShutterConfig.TabType.PHOTO && mPreviewView != null)
		{
			mPreviewView.queueEvent(new RenderRunnable()
			{
				@Override
				public void run(RenderThread renderThread)
				{
					if(renderThread != null && renderThread.getFilterManager() != null)
					{
						if(mShapeTypeId > 0)
						{
							renderThread.getFilterManager().changeShapeFilter(mShapeTypeId);
						}
					}
				}
			}, 500);
			if(!isShowTailorMadeView())
			{
				setStickerEnable(true, 500);
			}
		}
		TongJiUtils.onPageResume(getContext(), R.string.拍照);
		pageOpenTime = 0;

		changeSystemUiVisibility(View.GONE);
	}

	@Override
	public void onPause()
	{
		super.onPause();
		KeyBroadCastReceiver.unregisterListener(this);
		if (mRememberStickerID && !StickerResMgr.sInstanceIsNull())
		{
			StickerResMgr.getInstance().saveStickerSelectedInfo();
		}

        if(mPageInitCount > 1 && mPagePauseCount <= mPageInitCount)
		{
			mPagePauseCount++;
            if(mPagePauseCount != mPageInitCount)
            {
                return;
            }
        }
		mOnPause = true;
		if(mUIObserverList != null)
		{
			mUIObserverList.notifyObservers(UIObserver.MSG_LOCK_UI);
		}

		if (mCameraBottomControl!= null && mCameraBottomControl.mBeautySelectorView != null)
		{
			mCameraBottomControl.mBeautySelectorView.onPause();
		}

		if(mPreviewView != null)
		{
			mPreviewView.onPause();
		}
		if(mRatioBgView != null)
		{
			mDoCameraOpenAnim = true;
			mRatioBgView.showBlackMask();
		}
		recycleResource();

		if(mCameraBottomControl != null)
		{
			mCameraBottomControl.cancelAnim();
			if(mCameraBottomControl.mStickerView != null)
			{
				mCameraBottomControl.mStickerView.onPause();
			}
		}
		if(mContentView != null)
		{
			mContentView.handlePauseEvent();
		}

		mIsHomeKeyDown = false;
		TongJiUtils.onPagePause(getContext(), R.string.拍照);

		changeSystemUiVisibility(View.VISIBLE);
	}

	@Override
	public void onClose()
	{
	    KeyBroadCastReceiver.unregisterListener(this);
		if(mPageInitCount > 0)
		{
			mPageInitCount--;
		}
		UserRecord record = UserRecord.getRecord(UserRecord.RecordType.CAMERA);
		if (record == null)
		{
			record = new UserRecord();
			record.updateRecordInfo(UserRecord.CameraRecordInfoType.CAPTURE_MODE, mCurrentTimerMode);
			record.updateRecordInfo(UserRecord.CameraRecordInfoType.FRONT_AND_BACK_LENS, mCurrentCameraId);
			UserRecord.addRecord(UserRecord.RecordType.CAMERA, record);
		}
		else
		{
			record.updateRecordInfo(UserRecord.CameraRecordInfoType.CAPTURE_MODE, mCurrentTimerMode);
			record.updateRecordInfo(UserRecord.CameraRecordInfoType.FRONT_AND_BACK_LENS, mCurrentCameraId);
		}
		CameraConfig.getInstance().saveData(CameraConfig.ConfigMap.FaceGuideTakePicture, false);
		if(mSite != null)
		{
			if (mSite.m_myParams != null)
			{
				mSite.m_myParams.put("record_audio_enable", mAudioEnable);
				mSite.m_myParams.put("timer", mCurrentTimerMode);
				mSite.m_myParams.put(CameraSetDataKey.KEY_CAMERA_FLASH_MODE, mCurrentFlashMode);
				mSite.m_myParams.put(CameraSetDataKey.KEY_CAMERA_FRONT_SPLASH_MASK, mShowSplashMask);
				mSite.m_myParams.put(CameraSetDataKey.KEY_CAMERA_SELECT_TAB_TYPE, mCurrentTabType);
				mSite.m_myParams.put(CameraSetDataKey.KEY_CAMERA_SHOW_TAB_TYPE, mShowTabType);
				mSite.m_myParams.put(CameraSetDataKey.KEY_CAMERA_SELECT_RATIO, mCurrentRatio);
				mSite.m_myParams.put(CameraSetDataKey.KEY_IS_STICKER_PREVIEWBACK, mIsStickerPreviewBack);
				mSite.m_myParams.put(CameraSetDataKey.KEY_IS_PAGE_BACK, true);
				mSite.m_myParams.put(CameraSetDataKey.KEY_EXPOSURE_VALUE, mCurrentExposureValue);
				mSite.m_myParams.put(CameraSetDataKey.KEY_CAMERA_STICKER_CATEGORY_ID, mStickerCategoryId);
				mSite.m_myParams.put(CameraSetDataKey.KEY_HIDE_STICKER_MANAGER_BTN, mHideStickerManagerBtn);
			}

			if (mSite.m_inParams != null)
			{
				if (mSite.m_inParams.containsKey(CameraSetDataKey.KEY_CAMERA_SELECT_TAB_TYPE))
				{
					mSite.m_inParams.put(CameraSetDataKey.KEY_CAMERA_SELECT_TAB_TYPE, mCurrentTabType);
				}
				if (mSite.m_inParams.containsKey(CameraSetDataKey.KEY_CAMERA_SELECT_RATIO))
				{
					mSite.m_inParams.put(CameraSetDataKey.KEY_CAMERA_SELECT_RATIO, mCurrentRatio);
				}
				mSite.m_inParams.remove(CameraSetDataKey.KEY_TAILOR_MADE_SETTING);
				mSite.m_inParams.remove(CameraSetDataKey.KEY_CAMERA_STICKER_STICKER_ID);
				mSite.m_inParams.remove(CameraSetDataKey.KEY_CAMERA_FILTER_ID);
			}
		}

		if (mCameraBottomControl != null && mCameraBottomControl.mBeautySelectorView != null)
		{
			mCameraBottomControl.mBeautySelectorView.onClose();
		}

		if(mPreviewView != null)
		{
			mPreviewView.onPause();
			mPreviewView.onDestroy();
		}
        if (mEventListener != null) {
            EventCenter.removeListener(mEventListener);
            mEventListener = null;
        }

		recycleResource();

		if(mCameraSound != null)
		{
			new Thread(new Runnable()
			{
				@Override
				public void run()
				{
					mCameraSound.clearSound();
					mCameraSound = null;
				}
			}, "releaseSound").start();
		}

		//保存闪关灯为off
		mCurrentFlashMode = CameraConfig.FlashMode.Off;
		CameraConfig.getInstance().saveData(CameraConfig.ConfigMap.FrontFlashModeStr, mCurrentFlashMode);
		CameraConfig.getInstance().saveData(CameraConfig.ConfigMap.FlashModeStr, mCurrentFlashMode);

		if(mBrightnessUtils != null) mBrightnessUtils.destroyContext();
		if (mBeautyGuideView != null) mBeautyGuideView.clear();

		//删除page最顶层view
		MyFramework.ClearTopView(getContext());

		showWaitAnimDialog(false);
		ShapeSPConfig.getInstance(getContext()).clearAll();
		StickerSPConfig.getInstance().setStickerMute(mIsStickerSoundMute);

		if(mPageInitCount <= 0)
		{
			// 只有一个镜头页，清掉所有素材
			StickerResMgr.getInstance().clearMemory();
		}
		else
		{
			// 多个镜头页时，只需要清楚监听
			StickerResMgr.getInstance().unregisterAllStickerResMgrCB();
		}

		clearAll();
		StickerSPConfig.getInstance().clearAll();
		CameraConfig.getInstance().clearAll();
		StickerDrawHelper.getInstance().clearAll();
		TongJiUtils.onPageEnd(getContext(), R.string.拍照);

		if(mRootView != null && mPreviewView != null)
		{
			mRootView.removeView(mPreviewView);
			mPreviewView = null;
		}

		mFilterBeautyParams = null;
		mBeautyGuideView = null;
		mBrightnessUtils = null;
		mGestureDetector = null;
		mGestureListener = null;
		isPageClose = true;
		mDialog = null;

		changeSystemUiVisibility(View.VISIBLE);
	}

	private void clearAll()
	{
		//释放录制监听
		if(mRecordManager != null)
		{
			mRecordManager.setMessageHandler(null);
			mRecordManager.setOnRecordListener(null);
			mRecordManager.destroy();
			mRecordManager = null;
		}

		//释放下载监听
		if(mDownloadListener != null)
		{
			if(DownloadMgr.getInstance() != null)
			{
				DownloadMgr.getInstance().RemoveDownloadListener(mDownloadListener);
			}
			mDownloadListener = null;
		}

		//释放贴纸音效监听
		if(mStickerSoundManager != null)
		{
			mStickerSoundManager.setMediaPlayerListener(null);
			mStickerSoundManager.clearAll(getContext());
			mStickerSoundManager = null;
		}

		if(mUIObserverList != null)
		{
			mUIObserverList.deleteObservers();
			mUIObserverList = null;
		}

		if(mContentView != null)
		{
			mContentView.cancelToast();
			mContentView.clearAll();
		}

		if(mRatioBgView != null)
		{
			mRatioBgView.clearMemory();
		}

		if(mLightnessSeekBar != null)
		{
			mLightnessSeekBar.setOnSeekBarChangeListener(null);
		}
		this.removeAllViews();
	}

	@Override
	public void onPageResult(int siteID, HashMap<String, Object> params)
	{
		super.onPageResult(siteID, params);
		mIsPageResult = true;
		isPatchMode = false;
		doTakePicture = false;
		mSaveImageDataSucceed = false;
		if(isFront)
		{
			mShowSplashMask = false;
			mCurrentFlashMode = CameraConfig.FlashMode.Off;
			CameraConfig.getInstance().saveData(CameraConfig.ConfigMap.FrontFlashModeStr, mCurrentFlashMode);
		}

		if (mCameraBottomControl != null && mCameraBottomControl.mBeautySelectorView != null) {
			mCameraBottomControl.mBeautySelectorView.onPageResult(siteID, params);
		}

		if(siteID == SiteID.ALBUM)
		{
			CameraHandler cameraHandler = RenderHelper.getCameraHandler();
			if(cameraHandler != null)
			{
				cameraHandler.setFlashMode(mCurrentFlashMode);
			}
		}
		else if(siteID == SiteID.LOGIN || siteID == SiteID.REGISTER_DETAIL || siteID == SiteID.RESETPSW)
		{
			onResume();
			if(mCameraBottomControl != null)
			{
				mCameraBottomControl.UpdateCredit();
			}
		}
		else if(siteID == SiteID.WEBVIEW)
		{
			showCameraPermissionHelper(true);
		}
		else if(siteID == SiteID.FILTER_DOWNLOAD_MORE)
		{   //滤镜管理
			if(params != null)
			{
				boolean isChange = false;
				Object o = params.get("is_change");
				if(o instanceof Boolean)
				{
					isChange = (Boolean)o;
				}

				if(isChange && mCameraBottomControl != null)
				{
					mTempStickerWithFilter = true;
					mCameraBottomControl.updateCameraFilterItemList();

					if (!checkIsStickerFilter())
					{
						int[] ints = mCameraBottomControl.mCameraFilterRecyclerView.mFilterAdapter.GetSubIndexByUri(mCurrentFilterRes.m_id);
						//滤镜已删除，使用原图滤镜
						if (ints == null || ints[0] < 0 || ints[1] < 0) {
							mCameraBottomControl.mCameraFilterRecyclerView.mFilterAdapter.CancelSelect();
							mCameraBottomControl.mCameraFilterRecyclerView.mFilterAdapter.setOpenIndex(-1);

							setFilterMsgToastShow(false);
							mCameraBottomControl.setFilterUri(FilterAdapter.OriginalItemInfo.ORIGINAL_ITEM_URI, true);
						}
					}
				}
			}
			onResume();
		}
		else if(siteID == SiteID.FILTER_DETAIL)
		{   //滤镜详情页马上使用
			if(params != null && params.get("material_id") != null && params.get("material_id") instanceof Integer)
			{
				mTempStickerWithFilter = true;
				int id = (Integer)params.get("material_id");
				if(id != 0)
				{
					if(mCameraBottomControl != null)
					{
						mCameraBottomControl.updateCameraFilterItemList();
						mCameraBottomControl.setFilterUri(id, true);
					}
				}
			}
			onResume();
		}
		else if(siteID == SiteID.REGISTER_HEAD)
		{
			onResume();
		}
		notifyAllWidgetUnlockUI(200);
	}

	@Override
	public boolean onActivityKeyDown(int keyCode, KeyEvent event)
	{
		//此操作防止close popup 返回时候，推荐弹框无法关闭的问题
		if(keyCode == KeyEvent.KEYCODE_BACK)
		{
			return super.onActivityKeyDown(keyCode, event);
		}

		if(pageOpenTime <= 0 || System.currentTimeMillis() - pageOpenTime < 1000)
		{
			pageOpenTime = System.currentTimeMillis();
			return true;
		}

		if(mUIObserverList != null && mUIObserverList.isLock())
		{
			if(doTakePicture && mCurrentTimerMode > 0)
			{
				//倒计时拍照可以取消
			}
			else
			{
				return true;
			}
		}

		// 美形定制 引导页
		if (mBeautyGuideView != null && (mBeautyGuideView.isBeautyGuideAlive() || mBeautyGuideView.isDoingAnim()))
		{
			return true;
		}

		switch(keyCode)
		{
			case KeyEvent.KEYCODE_HOME:
				mIsHomeKeyDown = true;
				break;
            case KeyEvent.KEYCODE_UNKNOWN:
            case KeyEvent.KEYCODE_ENTER:
            {
                if(mCurrentTabType == ShutterConfig.TabType.GIF)
                {//gif不需要自拍杆控制
                    return false;
                }
            }
            case KeyEvent.KEYCODE_CAMERA:
			{
				if(!isPatchMode)
				{
					//触屏拍照、视频
					if(mCameraBottomControl != null)
					{
						mCameraBottomControl.handleTouchModeEvent();
					}
				}
				return true;
			}
			case KeyEvent.KEYCODE_VOLUME_UP:
			case KeyEvent.KEYCODE_VOLUME_DOWN:
			{
				if((mCurrentTabType == ShutterConfig.TabType.GIF || mCurrentTabType == ShutterConfig.TabType.VIDEO) && mRecordState == RecordState.STOP)
				{
					// 在 STOP -> IDLE 这段时间不处理
					return true;
				}
				if(mHasStickerSound)
				{
					return super.onActivityKeyDown(keyCode, event);
				}

				//素材弹框
				if(mCameraBottomControl != null && mCameraBottomControl.CloseUnlockView())
				{
					return super.onActivityKeyDown(keyCode, event);
				}

				if(mTips != null && mTips.isAlive())
				{
					mTips.showNextTips();
					return true;
				}

				if(!isPatchMode)
				{
					if(isShowTailorMadeView())
					{
						return true;
					}
					//触屏拍照、视频
					if(mCameraBottomControl != null)
					{
						mCameraBottomControl.handleTouchModeEvent();
					}
				}
				return true;
			}
			case KeyEvent.KEYCODE_FOCUS:
				return true;
			case KeyEvent.KEYCODE_ZOOM_IN:
			case KeyEvent.KEYCODE_ZOOM_OUT:
				CameraHandler cameraHandler = RenderHelper.getCameraHandler();
				if(cameraHandler != null)
				{
					cameraHandler.setCameraZoomInOrOut(keyCode == KeyEvent.KEYCODE_ZOOM_IN ? 1 : -1);
				}
				return true;
			default:
				break;
		}
		return super.onActivityKeyDown(keyCode, event);
	}

	@Override
	public boolean onActivityKeyUp(int keyCode, KeyEvent event)
	{
		switch(keyCode)
		{
			case KeyEvent.KEYCODE_CAMERA:
			case KeyEvent.KEYCODE_FOCUS:
				return true;
			default:
				break;
		}
		return super.onActivityKeyUp(keyCode, event);
	}

	@Override
	public void onTabTypeChange(int type)
	{
		switch(type)
		{
			case ShutterConfig.TabType.GIF:
				MyBeautyStat.onClickByRes(R.string.拍照_拍照_切换模式_表情包);
				TongJi2.AddCountByRes(getContext(), R.integer.拍照_切换到表情包);
				break;

			case ShutterConfig.TabType.PHOTO:
				MyBeautyStat.onClickByRes(R.string.拍照_拍照_切换模式_美颜拍照);
				TongJi2.AddCountByRes(getContext(), R.integer.拍照_切换到高清拍照);
				break;

			case ShutterConfig.TabType.CUTE:
				MyBeautyStat.onClickByRes(R.string.拍照_拍照_切换模式_萌妆照);
				TongJi2.AddCountByRes(getContext(), R.integer.拍照_切换到萌妆照);
				break;

			case ShutterConfig.TabType.VIDEO:
				MyBeautyStat.onClickByRes(R.string.拍照_拍照_切换模式_视频);
				TongJi2.AddCountByRes(getContext(), R.integer.拍照_切换到视频);
				break;
		}

		mLastTabType = mCurrentTabType;
		mCurrentTabType = type;
		mUIHandler.sendEmptyMessage(MSG_CLEAR_FOCUS_AND_METERING);
		CameraConfig.getInstance().saveData(CameraConfig.ConfigMap.SelectTab, mCurrentTabType);

		if(mContentView != null)
		{
			mContentView.showTitleToast(type);
		}

		if(mCameraPopSetting != null)
		{
			mCameraPopSetting.setTabType(type);
		}
		boolean hasReopenCamera = false;
		if(mCameraTopControl != null && mCameraBottomControl != null)
		{
			float curRatio = mCameraTopControl.getCurrRatio();
			float targetRatio = mCurrentRatio;
			if (mForceTabRatio) {
			    //强制使用比例情况下 切换tab时 用上一个tab的比例
                targetRatio = checkRatio(mCurrentRatio);
            } else {
                targetRatio = changeRatioByType(false);
            }

			if(type == ShutterConfig.TabType.GIF)
			{
				if(mLastTabType == ShutterConfig.TabType.PHOTO && mPreviewView != null)
				{
					mPreviewView.queueEvent(new RenderRunnable()
					{
						@Override
						public void run(RenderThread renderThread)
						{
							if(renderThread.getFilterManager() != null)
							{
								renderThread.getFilterManager().setStickerEnable(false);
							}
						}
					});
				}
			}
			if(curRatio != targetRatio)
			{
				float viewRatio = CameraConfig.PreviewRatio.Ratio_4_3;
				if(targetRatio >= CameraConfig.PreviewRatio.Ratio_16_9)
				{
					viewRatio = targetRatio;
				}
				hasReopenCamera = resetViewAndCameraRatio(viewRatio, targetRatio);
			}

			if(mCurrentTabType == ShutterConfig.TabType.PHOTO)
			{
				mCurrentFlashMode = CameraConfig.getInstance().getString(isFront ? CameraConfig.ConfigMap.FrontFlashModeStr : CameraConfig.ConfigMap.FlashModeStr);
			}
			if(!CameraConfig.FlashMode.Off.equals(mCurrentFlashMode))
			{
				if(CameraConfig.FlashMode.Torch.equals(mCurrentFlashMode))
				{
					mCurrentFlashMode = CameraConfig.FlashMode.Off;
					CameraConfig.getInstance().saveData(isFront ? CameraConfig.ConfigMap.FrontFlashModeStr : CameraConfig.ConfigMap.FlashModeStr, mCurrentFlashMode);
				}
				CameraHandler cameraHandler = RenderHelper.getCameraHandler();
				if(cameraHandler != null)
				{
					cameraHandler.setFlashMode(mCurrentTabType == ShutterConfig.TabType.PHOTO ? mCurrentFlashMode : CameraConfig.FlashMode.Off);
				}
			}
			changeVideoSize(targetRatio);
			updatePatchConfig();
			updateLightnessSeekBarLoc(targetRatio);

			float temp_ratio = targetRatio > CameraConfig.PreviewRatio.Ratio_16_9 ? CameraConfig.PreviewRatio.Ratio_Full : targetRatio;
			if(mCameraPopSetting != null)
			{
				mCameraPopSetting.updateRatioArr(temp_ratio);
			}

			mCameraBottomControl.setTabType(mCurrentTabType);
			mCameraBottomControl.setShutterMode(ShutterConfig.ShutterType.DEF);
			mCameraBottomControl.setPreviewRatio(temp_ratio);
			mCameraTopControl.setTabTy(mCurrentTabType);
			mCameraTopControl.setCurrRatio(temp_ratio);

			StickerResMgr.getInstance().setPreviewRatio(temp_ratio);
			StickerResMgr.getInstance().setShutterType(mCurrentTabType);
			StickerResMgr.getInstance().notifyShutterTabChange();

			mCameraBottomControl.updateUI();
			mCameraTopControl.updateUI();
		}

		showTipForUse();

		StickerDrawHelper.getInstance().setMode(mCurrentTabType == ShutterConfig.TabType.GIF);
		StickerDrawHelper.getInstance().setPreviewRatio(mCurrentRatio);
		StickerDrawHelper.getInstance().setFrameTopPaddingOnRatio1_1(RatioBgUtils.getMaskRealHeight(CameraConfig.PreviewRatio.Ratio_1_1));
		StickerDrawHelper.getInstance().setFrameTopPadding(mFrameTopPadding);

		if(mLastTabType == ShutterConfig.TabType.PHOTO || mCurrentTabType == ShutterConfig.TabType.PHOTO)
		{
			mPauseDetectOnTabChange = true;
		}
		if(mPreviewView != null)
		{
			mPreviewView.queueEvent(new RenderRunnable()
			{
				@Override
				public void run(RenderThread renderThread)
				{
					int renderMode = getRenderMode();
					renderThread.setRenderMode(renderMode);
					renderThread.setFrameTopPadding(mFrameTopPadding);

					if(renderThread.getFilterManager() != null)
					{
						renderThread.getFilterManager().setFaceRectEnable(renderMode == 0);
						renderThread.getFilterManager().setFaceAdjustEnable(renderMode == 0 ? true : !mStickerDealWithShape && mShapeTypeId <= 0);
						renderThread.getFilterManager().setStickerEnable(renderMode != 0);
						renderThread.getFilterManager().setShapeEnable(renderMode != 0 && !mStickerDealWithShape && mShapeTypeId > 0);
						renderThread.getFilterManager().setRatioAndOrientation(1 / mCurrentRatio, mScreenOrientation, mFrameTopPadding);
					}
				}
			});
		}
		if(mUIHandler != null && (mCurrentTabType == ShutterConfig.TabType.PHOTO || !hasReopenCamera))
		{
			mDoFocusWithFace = -1;
			mUIHandler.obtainMessage(MSG_OPEN_OR_CLOSE_AUTO_FOCUS, true).sendToTarget();
		}

		if(mCurrentTabType == ShutterConfig.TabType.GIF || mCurrentTabType == ShutterConfig.TabType.VIDEO)
		{
			if(mRecordManager == null || !mRecordManager.isSameSize(mVideoWidth, mVideoHeight))
			{
				mUIHandler.removeMessages(MSG_DELAY_PREPARE_RECORD);
				mUIHandler.sendEmptyMessageDelayed(MSG_DELAY_PREPARE_RECORD, 500);
			}
			else if(mRecordManager != null)
			{
				if(mCurrentTabType == ShutterConfig.TabType.GIF)
				{
					mRecordManager.setVideoTimeLong(mGifTimeLong);
					mRecordManager.setAudioRecordEnable(false);

				}
				else if(mCurrentTabType == ShutterConfig.TabType.VIDEO)
				{
					mRecordManager.setVideoTimeLong(mVideoTimeLong);
					mRecordManager.setAudioRecordEnable(mAudioEnable);
				}
			}
		}

		//重新加载贴纸列表素材
		if(mLastTabType != mCurrentTabType)
		{
			//拍照模式关闭贴纸音效
			if(mCurrentTabType == ShutterConfig.TabType.PHOTO)
			{
				releaseStickerSound();
			}

              /*切换tab，相当于关闭上一次tab类型的page*/
			sendTypePageSensorsData(mLastTabType, false);

            /*切换tab，相当于打开该tab类型的page*/
			sendTypePageSensorsData(mCurrentTabType, true);
		}

		if(mPauseDetectOnTabChange && mUIHandler != null)
		{
			mUIHandler.postDelayed(new Runnable()
			{
				@Override
				public void run()
				{
					mPauseDetectOnTabChange = false;
				}
			}, 200);
		}
	}

	@Override
	public void onCloseStickerList()
	{
		showTipForUse();
	}

	@Override
	public void onShowLessThan18SDKTips(int type)
	{
		checkCanRecordVideo(type, new MemoryTipDialog.OnDialogClick()
		{
			@Override
			public void onClick(AlertDialogV1 dialog)
			{
				if(dialog != null)
				{
					dialog.dismiss();
					notifyAllWidgetUnlockUI(50);
				}
			}
		}, new DialogInterface.OnDismissListener()
		{
			@Override
			public void onDismiss(DialogInterface dialog)
			{
				mSystemUiVisibility = -1;
			}
		});
	}


	/**
	 * 当前page模式类型 神策埋点
	 *
	 * @param mType       当前page模式类型 {@link cn.poco.camera3.config.shutter.ShutterConfig.TabType}
	 * @param isPageStart 是否页面start
	 */
	private void sendTypePageSensorsData(@ShutterConfig.TabType int mType, boolean isPageStart)
	{
		int pageResId = -1;
		switch(mType)
		{
			case ShutterConfig.TabType.GIF:
				pageResId = R.string.拍照_表情包拍摄_主页面;
				break;
			case ShutterConfig.TabType.CUTE:
				pageResId = R.string.拍照_萌妆照拍摄页_主页面;
				break;
			case ShutterConfig.TabType.PHOTO:
				pageResId = R.string.拍照_美颜拍照_主页面;
				break;
			case ShutterConfig.TabType.VIDEO:
				pageResId = R.string.拍照_视频拍摄页_主页面;
				break;
		}
		if(pageResId != -1)
		{
			if(isPageStart)
			{
				MyBeautyStat.onPageStartByRes(pageResId);
			}
			else
			{
				MyBeautyStat.onPageEndByRes(pageResId);
			}
		}
	}

	/**
	 * 4.3版本一下动态贴纸切换弹出提示框，false不可使用并且弹出tips
	 *
	 * @return
	 */
	private boolean checkCanRecordVideo(int type, MemoryTipDialog.OnDialogClick callback, DialogInterface.OnDismissListener dismissListener)
	{
		if(!mRecordVideoEnable)
		{
			MemoryTipDialog.Builder builder = new MemoryTipDialog.Builder(getContext());
			int stringID = 0;
			switch(type)
			{
				case ShutterConfig.TabType.GIF:
				{
					stringID = R.string.camerapage_camera_open_gif_record_tips;
					break;
				}

				case ShutterConfig.TabType.VIDEO:
				{
					stringID = R.string.camerapage_camera_open_video_record_tips;
				}
			}
			builder.setMessage(stringID);
			builder.setPositiveButton(R.string.camerapage_camera_open_sticker_record_tips_ensure, callback);
			builder.setDismissListener(dismissListener);
			builder.show();
			return false;
		}
		return true;
	}

	private void changeVideoSize(float ratio)
	{
		if(mCurrentTabType == ShutterConfig.TabType.PHOTO || mCurrentTabType == ShutterConfig.TabType.CUTE)
		{
			return;
		}
		int width = ShareData.getScreenW();
		float videoScale = 1.0f;
		if(mCurrentTabType == ShutterConfig.TabType.GIF)
		{
			/*不压缩 gif 的录制尺寸，让预览页看起来更和谐*/
			/*if (width > 1080)
            {
                videoScale = 0.5f;//1440x1440 -> 720x720
            }
            else if (width > 720)
            {
                videoScale = 0.5f;//1080x1080 -> 540x540
            }
            else if (width > 600)
            {
                videoScale = 0.75f;//720x720 -> 540x540
            }*/
		}
		else
		{
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
					videoScale = 0.75f;//1440x2560 -> 1080x1920
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
					if(width > 720)
					{//(720,?]
						videoScale = 0.8f;
					}
				}
				else if(ratio == CameraConfig.PreviewRatio.Ratio_17$25_9)
				{
					if(width > 1080)
					{
						videoScale = 0.8f;
					}
					else if(width > 720)
					{
						videoScale = 0.88888f;
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
						videoScale = 0.75f;
					}
					else if(width > 720)
					{
						videoScale = 0.8f;
					}
				}
				else if(ratio == CameraConfig.PreviewRatio.Ratio_18$5_9)
				{
					if(width > 600)
					{
						videoScale = 0.8f;
					}
				}
			}
		}
		mVideoWidth = Math.round(width * videoScale);
		mVideoHeight = Math.round(mVideoWidth * ratio);
		//Log.i(TAG, "changeVideoSize: "+mVideoWidth+", "+mVideoHeight+", "+ratio);
	}

	private int getRenderMode()
	{
		if(mCurrentTabType == ShutterConfig.TabType.GIF)
		{
			return 2;
		}
		else if(mCurrentTabType == ShutterConfig.TabType.PHOTO)
		{
			return 0;
		}
		return 1;
	}

	@Override
	public void onSelectRepeatSticker(Object info)
	{
		//当前使用的滤镜非贴纸自带滤镜，设置贴纸滤镜
		if(!checkIsStickerFilter())
		{
			if(info != null && info instanceof VideoStickerRes && ((VideoStickerRes)info).mStickerRes != null && ((VideoStickerRes)info).mStickerRes.mFilterRes != null)
			{
				mCurrentFilterRes = ((VideoStickerRes)info).mStickerRes.mFilterRes;
				setColorFilter(mCurrentFilterRes);
				cancelCameraFilterSelect();
			}
		}
	}

	/**
	 * 贴纸选中回调
	 *
	 * @param info -> {@link VideoStickerRes}
	 */
	@Override
	public void onSelectSticker(final Object info, final boolean isTabChange)
	{
		// 首次安装时，由于线程问题
		if(isShowTailorMadeView())
		{
            StickerResMgr.getInstance().clearAllSelectedInfo();
            clearStickerWithShape();
			if(mCameraBottomControl != null && mCameraBottomControl.mStickerView != null)
			{
				mCameraBottomControl.mStickerView.onShowVolumeBtn(false);
			}
			return;
		}

		//释放所有sticker sound
		releaseStickerSound();

		//监听人脸动作回调
		setStickerDrawListener(mOnDrawStickerResListener);

		if(mPreviewView != null)
		{
			mPreviewView.queueEvent(new RenderRunnable()
			{
				@Override
				public void run(RenderThread renderThread)
				{
					VideoStickerRes videoStickerRes = null;
					if(info != null)
					{
						videoStickerRes = (VideoStickerRes)info;
					}
					mStickerDealWithShape = false;
					int stickerResType = -1;//0:素材+变形, 1:素材, 2:变形
					final int lastStickerId = mStickerId;
					mActionName = null;
					mMusicActionName = null;
					mShapeActionName = null;
					mShowActionTip = false;
					isShapeCompose = false;//是否是变形组合
					if(videoStickerRes != null)
					{
						mStickerId = videoStickerRes.m_id;
						mStickerTongJiId = videoStickerRes.m_tjId;
						int shapeId = FaceShapeType.getShapeIdByName(videoStickerRes.m_shape_type);
						if(mLastResShapeTypeId > 0)
						{
							if(shapeId > 0)
							{
								mShapeTypeId = shapeId;
								mLastResShapeTypeId = 0;
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

						setHasStickerSound(videoStickerRes.m_has_music);
						if(videoStickerRes.mStickerRes != null)
						{
							if(videoStickerRes.mStickerRes.mOrderStickerRes != null && !videoStickerRes.mStickerRes.mOrderStickerRes.isEmpty())
							{
								mStickerDealWithShape = (mShapeTypeId > 0);

								if(mShapeTypeId > 0 && mLastResShapeTypeId > 0)
								{
									isShapeCompose = true;
								}
							}
							mActionName = videoStickerRes.mStickerRes.mAction;
							if(videoStickerRes.m_has_music && videoStickerRes.mStickerRes.mStickerSoundRes != null && videoStickerRes.mStickerRes.mStickerSoundRes.mStickerSounds != null)
							{
								for(StickerSound stickerSound : videoStickerRes.mStickerRes.mStickerSoundRes.mStickerSounds)
								{
									if(stickerSound != null && stickerSound.mSoundType.isEffectAction())
									{
										mMusicActionName = stickerSound.mAction;
									}
								}
							}
						}
						else
						{
							if(mLastResType == 1 && mLastResShapeTypeId == 0 && mShapeTypeId > 0)
							{
								isShapeCompose = true;
							}

							//常用-变形
							//常用变形组合 素材
							mShapeStickerRes = videoStickerRes;
							mShapeActionName = videoStickerRes.m_prompt_text;
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
						mShapeStickerRes = null;
						setHasStickerSound(false);
					}
					if(mLastResType == 1 && stickerResType == 2)
					{
						//普通无变形素材 + 常用变形（切换变形）
						mStickerDealWithShape = true;
						StickerDrawHelper.getInstance().setShapeTypeId(mShapeTypeId);
					}
					else
					{
						if(StickerDrawHelper.getInstance().setStickerRes(mStickerId, videoStickerRes))
						{
							StickerDrawHelper.getInstance().setShapeTypeId(mShapeTypeId);

							if(renderThread != null && renderThread.getFilterManager() != null)
							{
								renderThread.getFilterManager().resetFilterData();
							}

							if(mUIHandler != null)
							{
								final VideoStickerRes finalVideoStickerRes = videoStickerRes;
								mUIHandler.post(new Runnable()
								{
									@Override
									public void run()
									{
										setStickerSound(finalVideoStickerRes);
										setStickerFilter(finalVideoStickerRes, isTabChange);
									}
								});
							}
							if(mActionName != null || mMusicActionName != null)
							{
								mShowActionTip = true;
							}
							if(mShapeActionName != null && mUIHandler != null)
							{
								mUIHandler.sendEmptyMessageDelayed(MSG_SHOW_ACTION_TIPS, 100);
							}
						}
						else
						{
							if(mUIHandler != null)
							{
								final VideoStickerRes finalVideoStickerRes = videoStickerRes;
								mUIHandler.post(new Runnable()
								{
									@Override
									public void run()
									{
										if(mCurrentTabType != ShutterConfig.TabType.GIF)
										{
											setStickerSound(finalVideoStickerRes);
										}
										setStickerFilter(finalVideoStickerRes, isTabChange);
									}
								});
							}
						}
					}
					if(stickerResType != 2)
					{
						mLastResType = stickerResType;
					}
					if(renderThread != null && renderThread.getFilterManager() != null)
					{
						renderThread.getFilterManager().setBusinessBeautyEnable(false);// 38559 纪梵希商业定制

						renderThread.getFilterManager().setFaceAdjustEnable(!mStickerDealWithShape && mShapeTypeId <= 0);
						renderThread.getFilterManager().setShapeEnable(!mStickerDealWithShape && mShapeTypeId > 0);
						renderThread.getFilterManager().changeShapeFilter(mShapeTypeId);
					}
				}
			});
		}
	}

	private AbsDownloadMgr.DownloadListener mDownloadListener = new AbsDownloadMgr.DownloadListener()
	{
		@Override
		public void OnDataChange(int resType, int downloadId, IDownload[] resArr)
		{
			if(resType == ResType.FILTER.GetValue() && resArr != null && ((BaseRes)resArr[0]).m_type == BaseRes.TYPE_LOCAL_PATH)
			{
				//滤镜下载完成后，更新数据
				if(mCameraBottomControl != null)
				{
					mCameraBottomControl.downloadCameraFilterItemList();
				}
			}
		}
	};

	/**
	 * 贴纸触发监听
	 */
	private OnDrawStickerResListener mOnDrawStickerResListener = new OnDrawStickerResListener()
	{
		@Override
		public void onPlayAnimMusic(final int state)
		{
			//Log.d(TAG, "CameraPage --> onPlayAnimMusic: " + state);
			if(state == 1)
			{
				if(mUIHandler != null)
				{
					mUIHandler.post(new Runnable()
					{
						@Override
						public void run()
						{
							startAnimSound();
						}
					});
				}
			}
			else if(state == 0)
			{
				if(mUIHandler != null)
				{
					mUIHandler.post(new Runnable()
					{
						@Override
						public void run()
						{
							stopAnimSound();
						}
					});
				}
			}
		}

		@Override
		public void onPlayActionMusic(final String action)
		{
			//Log.d(TAG, "CameraPage --> onPlayActionMusic: action :" + action);
			if(mUIHandler != null)
			{
				mUIHandler.post(new Runnable()
				{
					@Override
					public void run()
					{
						startFaceActionSound(action);
					}
				});
			}
		}

		@Override
		public void onPlayActionAnimMusic(String action, int state)
		{
//            Log.d(TAG, "CameraPage --> onPlayActionAnimMusic: action :" + action + ", state:" + state);
		}

		@Override
		public int getPlayState(int type)
		{
			return getSoundPlayState(type);
		}

		@Override
		public void onAnimStateChange(int state)
		{
			if(state == 1 && RenderHelper.sRenderThread != null && RenderHelper.sRenderThread.getFilterManager() != null)
			{
				if(mStickerId == 38559)
				{
					// 纪梵希商业定制
					RenderHelper.sRenderThread.getFilterManager().setBusinessBeautyEnable(false);
				}
				else if(mStickerId == 39165)
				{
					//阿玛尼
					RenderHelper.sRenderThread.getFilterManager().changeColorFilterRenderStyle(false);
				} else if (mStickerId == 41744) {
				    //羽西//20180104
                    RenderHelper.sRenderThread.getFilterManager().setBusinessBeautyEnable(false);
                }
			}
		}

		@Override
		public void onAnimTrigger(int type)
		{
			if(type == 1 && RenderHelper.sRenderThread != null && RenderHelper.sRenderThread.getFilterManager() != null)
			{
				if(mStickerId == 38559)
				{
					RenderHelper.sRenderThread.getFilterManager().setBusinessBeautyEnable(true);

                } else if (mStickerId == 41744) {
                    //羽西
                    RenderHelper.sRenderThread.getFilterManager().setBusinessBeautyEnable(true);
                }
			}
		}
	};

	/**
	 * 动画触发音效
	 */
	private void startAnimSound()
	{
		if(mStickerSoundManager != null)
		{
			mStickerSoundManager.startAnimSound();
		}
	}

	/**
	 * 停止动画音效
	 */
	private void stopAnimSound()
	{
		if(mStickerSoundManager != null)
		{
			mStickerSoundManager.stopAnimSound();
		}
	}

	/**
	 * 人脸动作触发音效
	 *
	 * @param faceAction {@link FaceAction}
	 */
	private void startFaceActionSound(String faceAction)
	{
		if(mStickerSoundManager != null)
		{
			mStickerSoundManager.startFaceActionSound(faceAction);
		}
	}

	/**
	 * 返回音效状态
	 *
	 * @param type 0:AnimMusic, 1:ActionMusic, 2:ActionAnimMusic
	 * @return {@link OnDrawStickerResListener#IDLE},  {@link OnDrawStickerResListener#PLAYING},  {@link OnDrawStickerResListener#STOP}
	 */
	private int getSoundPlayState(int type)
	{
		TypeValue.SoundType soundType = null;
		if(type == 0)
		{
			soundType = TypeValue.SoundType.EFFECT_DELAY;
		}
		else if(type == 1)
		{
			soundType = TypeValue.SoundType.EFFECT_ACTION;

		}
		else if(type == 2)
		{
			//// TODO: 2017/7/19 CameraPage --> getSoundPlayState
		}
		if(mStickerSoundManager != null)
		{
			TypeValue.SoundStatus status = mStickerSoundManager.getStickerSoundStatus(soundType);
			if(status != null)
			{
				if(status.isPlaying())
				{
					return OnDrawStickerResListener.PLAYING;
				}
				else if(status.isStop())
				{
					return OnDrawStickerResListener.STOP;
				}
				else
				{
					return OnDrawStickerResListener.IDLE;
				}
			}
		}
		return OnDrawStickerResListener.IDLE;
	}

	/**
	 * 动态贴纸模式下，检查是否静音
	 */
	private void checkStickerSoundMute()
	{
		boolean audioMute = checkAudioMute();
		if(audioMute)
		{
			mIsStickerSoundMute = true;
		}
		setStickerSoundMute(mIsStickerSoundMute, false);
	}

	/**
	 * 贴纸音效开关
	 *
	 * @param mute    true 静音
	 * @param setPlay 设置media play音量
	 */
	private boolean setStickerSoundMute(final boolean mute, boolean setPlay)
	{
		boolean success = false;
		if(mStickerSoundManager != null)
		{
			success = mStickerSoundManager.setStickerMute(mute, setPlay);
		}
		if(success)
		{
			mIsStickerSoundMute = mute;
		}
		return success;
	}

	private void pauseStickerSound()
	{
		setStickerDrawListener(null);
		if(mStickerSoundManager != null)
		{
			mStickerSoundManager.onPause(getContext());
		}
	}

	private void resumeStickerSound()
	{
		//管理素材不resume音效
		if(mCameraBottomControl != null && mCameraBottomControl.isMgrPage())
		{
			return;
		}

		if(isShowTailorMadeView())
		{
			return;
		}

		//非当前page ，不恢复音效
		if(!isCurrentPage())
		{
			return;
		}

		if(mStickerSoundManager != null)
		{
			mStickerSoundManager.onResume(getContext());
		}
	}

	private void setStickerDrawListener(final OnDrawStickerResListener listener)
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
						renderThread.getFilterManager().setOnDrawStickerResListener(listener);
					}
				}
			});
		}
	}

	@Override
	public void onShowGuide()
	{
		TongJi2.AddCountByRes(getContext(), R.integer.拍照_美形定制_指引弹窗);
	}

	@Override
	public void onDismissGuideStart()
	{
		mCameraTopControl.SetBtnAlpha(1f);

		// 是否显示镜头矫正
		if(mBeautyGuideView.GetPatchStateB4Anim())
		{
			mCameraTopControl.SetCameraPatchState(true);
			mBeautyGuideView.SetPathStateB4Anim(false);
			mCameraTopControl.updateUI();
		}

		if(BeautyGuideView.IsShowGuide(getContext()))
		{
			BeautyGuideView.SetGuideTag(getContext(), true);
		}

		notifyAllWidgetUnlockUI(300);
	}

	@Override
	public void onDismissGuideEnd(boolean is2Show)
	{
		if (is2Show) {
			if (TagMgr.CheckTag(getContext(), Tags.CAMERA_TAILOR_MADE_NEW_FLAG)){
				TagMgr.SetTag(getContext(), Tags.CAMERA_TAILOR_MADE_NEW_FLAG);
				TagMgr.Save(getContext());

				if(mCameraBottomControl != null && mCameraBottomControl.mControlLayout != null){
					mCameraBottomControl.mControlLayout.UpdateBeautyIcon();
				}
			}
			showBeautySelectorView(true);
		} else {
			showTipForUse();
		}
	}

	/**
	 * 打开美颜脸型调节控件
	 */
	private void onShowBeautySelectorView()
	{
		//显示美颜%脸型调整
		if(mCameraBottomControl != null && mCameraBottomControl.mStickerView != null)
		{
			mCameraBottomControl.mStickerView.onShowVolumeBtn(false);
		}

		if (mUIObserverList != null)
		{
			mUIObserverList.notifyObservers();
		}

		// 清贴纸素材
		clearStickerWithShapeWhenBeautySetting();
		//清除贴纸音效
		releaseStickerSound();

		updateTailorTag();

		MyBeautyStat.onPageStartByRes(R.string.拍照_拍照_美形定制);
	}

	/**
	 * 关闭美颜脸型调节控件
	 */
	private void onCloseBeautySelectorView()
	{
		setStickerEnable(mCurrentTabType != ShutterConfig.TabType.PHOTO, 0);
		releaseStickerSound();


		// 是否显示镜头矫正
		if(mBeautyGuideView != null)
		{
			if(mBeautyGuideView.GetPatchStateB4Anim())
			{
				mCameraTopControl.SetCameraPatchState(true);
				mBeautyGuideView.SetPathStateB4Anim(false);
				mCameraTopControl.updateUI();
			}
		}

		notifyAllWidgetUnlockUI(300);
		MyBeautyStat.onPageEndByRes(R.string.拍照_拍照_美形定制);
	}

	private void updateTailorTag()
	{
		BeautyGuideView.UpdateTag(getContext());
	}

	@Override
	public void onLongPressPowerKey()
	{
		if (mUIHandler != null)
		{
			removeAllMsg();
		}

		if(isCountDown())
		{
			onCancelCountDown();
		}

		mUIHandler.postDelayed(new Runnable()
		{
			@Override
			public void run()
			{
				if(mCanStopRecordState > -1)
				{
					mCanStopRecordState = 2;
				}
				onPauseVideo();
			}
		}, 200);
	}

	@Override
	public void onClickHomeKey()
	{

	}

	@Override
	public void onLongPressHomeKey()
	{

	}

	private class MyStickerSoundListener extends StickerSoundManager.StickerSoundMPListener
	{
		@Override
		public void onPrepared(StickerMediaPlayer stickerMediaPlayer, MediaPlayer mediaPlayer)
		{
			super.onPrepared(stickerMediaPlayer, mediaPlayer);
		}

		@Override
		public void onPreparedBgmSound(StickerMediaPlayer stickerMediaPlayer, MediaPlayer mediaPlayer)
		{
			if(mStickerSoundManager != null)
			{
				mStickerSoundManager.startBgmSound();
				//Log.d(TAG, "MyStickerSoundListener --> onPreparedBgmSound: ");
			}
		}

		@Override
		public void onVolumeChanged(int streamType, int streamVolume, boolean isMute)
		{
			mIsStickerSoundMute = isMute;
			if(mCameraBottomControl != null && mCameraBottomControl.mStickerView != null)
			{
                mCameraBottomControl.mStickerView.setStickerSoundMute(isMute);//改变图标状态
			}
		}
	}

	/**
	 * 检查是否静音
	 *
	 * @return
	 */
	private boolean checkAudioMute()
	{
		if(mStickerSoundManager != null)
		{
			return mStickerSoundManager.isStickerMute();
		}
		return false;
	}

	/**
	 * 检查是否插入耳机设备
	 *
	 * @return true 已插入
	 */
	private boolean checkHeadSetIn()
	{
		if(mStickerSoundManager != null)
		{
			return mStickerSoundManager.isHeadSetIn();
		}
		return false;
	}

	@Override
	public boolean onStickerSoundMute(boolean mute)
	{
		return setStickerSoundMute(mute, true);
	}

	@Override
	public boolean getAudioMute()
	{
		return checkAudioMute();
	}

	@Override
	public void closeStickerMgrPage()
	{
		setStickerDrawListener(mOnDrawStickerResListener);
		resumeStickerSound();
	}

	@Override
	public void openStickerMgrPage()
	{
		pauseStickerSound();
	}

	/**
	 * 支持贴纸音效
	 *
	 * @return
	 */
	private boolean isSupportStickerSound()
	{
		return mCurrentTabType == ShutterConfig.TabType.VIDEO;
	}

	private void setHasStickerSound(boolean has)
	{
		if(isSupportStickerSound())
		{
			this.mHasStickerSound = has;
		}
		else
		{
			this.mHasStickerSound = false;
		}
	}

	/**
	 * 释放贴纸音效
	 */
	private void releaseStickerSound()
	{
		if(mStickerSoundManager != null)
		{
			mStickerSoundManager.releaseStickerSound();
		}
	}

	private void seekToStickerBgmSound(int semc)
	{
		if(mStickerSoundManager != null)
		{
			mStickerSoundManager.seekToBgmSound(semc);
		}
	}

	private void setStickerFilter(VideoStickerRes res, boolean isTabChange)
	{
		boolean tmpStickerWithFilter = mTempStickerWithFilter;
		mTempStickerWithFilter = false;

		if(isTabChange && checkIsStickerFilter() && mCurrentTabType == ShutterConfig.TabType.PHOTO)
		{
			//如果使用贴纸滤镜，切换到高清照还原成原图滤镜
			setUsedStickerFilter(false);
			setFilterMsgToastShow(false);
			setCameraFilterRes(0);
			return;
		}

		if(isTabChange && !checkIsStickerFilter())
		{
			if(mLastTabType == ShutterConfig.TabType.PHOTO && res != null && res.mStickerRes != null && res.mStickerRes.mFilterRes != null)
			{
				//使用贴纸自带滤镜
				mCurrentFilterRes = res.mStickerRes.mFilterRes;
				setColorFilter(mCurrentFilterRes);
				dismissMsgToast();
				cancelCameraFilterSelect();
			}
			else
			{
				//如果萌装照、gif、视频内切换模式，维持修改过的滤镜
				setUsedStickerFilter(false);
				setColorFilter(mCurrentFilterRes);
			}
			return;
		}

		if(res != null && res.mStickerRes != null)
		{
			// 返回镜头，使用上一次的滤镜效果
			if (tmpStickerWithFilter) {
				if (mCurrentFilterRes != null && !mCurrentFilterRes.m_isStickerFilter)
				{
					setUsedStickerFilter(false);
					setColorFilter(mCurrentFilterRes);
					return;
				}
			}

			if (res.mStickerRes.mFilterRes != null)
			{
				mCurrentFilterRes = res.mStickerRes.mFilterRes;
				setColorFilter(mCurrentFilterRes);
				cancelCameraFilterSelect();
				return;
			}
		}
		if(!checkIsStickerFilter())
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
	 * 贴纸音效
	 *
	 * @param finalVideoStickerRes
	 */
	private void setStickerSound(final VideoStickerRes finalVideoStickerRes)
	{
		if(!isSupportStickerSound())
		{
			return;
		}

		if(mStickerSoundManager != null)
		{
			if(finalVideoStickerRes != null && finalVideoStickerRes.mStickerRes != null && finalVideoStickerRes.mStickerRes.mStickerSoundRes != null)
			{
				mStickerSoundManager.setStickerSoundRes(getContext(), finalVideoStickerRes.mStickerRes.mStickerSoundRes);
			}
			else
			{
				releaseStickerSound();
			}
		}
	}

	/**
	 * 美形定制过程中，清除贴纸数据以及变形数据
	 */
	private void clearStickerWithShapeWhenBeautySetting()
	{
        StickerResMgr.getInstance().clearAllSelectedInfo();
		clearStickerWithShape();
		setStickerEnable(false, 0);
		setStickerFilter(null, false);
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
						renderThread.getFilterManager().setFaceAdjustEnable(enable ? (!mStickerDealWithShape && mShapeTypeId <= 0) : true);
						renderThread.getFilterManager().setStickerEnable(enable);
						renderThread.getFilterManager().setShapeEnable(enable ? (!mStickerDealWithShape && mShapeTypeId > 0) : false);
					}
				}
			}, delay);
		}
	}

	private void onClickShutterTongji()
	{
		if(mCurrentTabType == ShutterConfig.TabType.PHOTO)
		{
			TongJi2.AddCountByRes(getContext(), R.integer.拍照_点击拍照);

			if(CameraConfig.FlashMode.On.equals(mCurrentFlashMode))
			{
				TongJi2.AddCountByRes(getContext(), R.integer.拍照_闪光灯开);
			}
			else if(CameraConfig.FlashMode.Off.equals(mCurrentFlashMode))
			{
				TongJi2.AddCountByRes(getContext(), R.integer.拍照_闪光灯关);
			}
			else if(CameraConfig.FlashMode.Auto.equals(mCurrentFlashMode))
			{
				TongJi2.AddCountByRes(getContext(), R.integer.拍照_闪光灯自动);
			}
			else if(CameraConfig.FlashMode.Torch.equals(mCurrentFlashMode))
			{
				TongJi2.AddCountByRes(getContext(), R.integer.拍照_手电筒);
			}
			if(mCurrentRatio == 1.0f)
			{
				TongJi2.AddCountByRes(getContext(), R.integer.拍照_1_1照片比例);
			}
			else
			{
				TongJi2.AddCountByRes(getContext(), R.integer.拍照_4_3照片比例);
			}
		}
		if(mCurrentTimerMode == 1)
		{
			TongJi2.AddCountByRes(getContext(), R.integer.拍照_设置_手动1秒);
		}
		else if(mCurrentTimerMode == 2)
		{
			TongJi2.AddCountByRes(getContext(), R.integer.拍照_设置_手动2秒);
		}
		else if(mCurrentTimerMode == 10)
		{
			TongJi2.AddCountByRes(getContext(), R.integer.拍照_设置_手动10秒);
		}
		else
		{
			TongJi2.AddCountByRes(getContext(), R.integer.拍照_设置_手动关闭);
		}
		if(mTouchCapture)
		{
			TongJi2.AddCountByRes(getContext(), R.integer.拍照_设置_触屏拍照开);
		}
		else
		{
			TongJi2.AddCountByRes(getContext(), R.integer.拍照_设置_触屏拍照关);
		}
	}

	@Override
	public void onTiming()
	{
		if(mCameraPopSetting.isAlive())
		{
			mCameraPopSetting.dismiss();
		}
		mTimerCounts = mCurrentTimerMode;
		if(mCurrentTabType == ShutterConfig.TabType.PHOTO || mCurrentTabType == ShutterConfig.TabType.CUTE)
		{
			doTakePicture = true;
			shutterTongJi2();
			mUIHandler.sendEmptyMessage(MSG_TIMER_COUNT_DOWN);
		}

		if(mCurrentTabType == ShutterConfig.TabType.GIF || mCurrentTabType == ShutterConfig.TabType.VIDEO)
		{
			mUIHandler.sendEmptyMessage(MSG_RECORD_TIMER_COUNT_DOWN);
		}
	}

	@Override
	public boolean isPatchMode()
	{
		return isPatchMode;
	}

	/**
	 * 快门拍照回调
	 */
	@Override
	public void onShutterClick()
	{
		if(mIsHomeKeyDown)
		{
			if(mUIObserverList != null)
			{
				mUIObserverList.notifyObservers(UIObserver.MSG_UNLOCK_UI);
			}
			return;
		}
		if(mCurrentTabType == ShutterConfig.TabType.PHOTO || mCurrentTabType == ShutterConfig.TabType.CUTE || isPatchMode)
		{
			if (!isPatchMode) {
				//高清拍照、萌装照 点击触发埋点
				sendCameraSensorsDataWhenClick();
			}
			if(mUIHandler != null)
			{
				mUIHandler.sendEmptyMessage(MSG_TAKE_PICTURE);
			}
		}
		else if(mCurrentTabType == ShutterConfig.TabType.GIF || mCurrentTabType == ShutterConfig.TabType.VIDEO)
		{
			if(mRecordState != RecordState.IDLE || !mRecordVideoEnable || !isValidVideoTimeLong())
			{
				return;
			}
			if (mCurrentTabType == ShutterConfig.TabType.GIF) {
				sendCameraSensorsDataWhenClick();
			}
			if(mCurrentTabType == ShutterConfig.TabType.VIDEO)
			{
				if(mVideoIsPause)
				{
					mVideoIsPause = false;
					return;
				}
				if(mVideoMgr == null)
				{
					mVideoMgr = new VideoMgr();
					// 是否处理最后一秒录制时长跟显示的时长不一致的情况
					mVideoMgr.setProcessLastSecond(true);
				}
				mVideoMgr.setTargetDuration(mTargetDuration);
			}
			if(mRecordManager != null)
			{
				int state = RecordState.IDLE;
				if(RenderHelper.sRenderThread != null)
				{
					state = RenderHelper.sRenderThread.getRecordState();
				}
				if(state != RecordState.WAIT)
				{
					mCameraBottomControl.handleRecordingStatusInPause();
					return;
				}
				if(!mRecordManager.isPrepareFinish())
				{
					//解决快速切换tab导致不能录制的问题
					if(!mHasPrepared)
					{
						mRecordManager.prepareAgain(state);
						mHasPrepared = true;
					}
					else
					{
						mHasPrepared = false;
						mCameraBottomControl.handleRecordingStatusInPause();
						return;
					}
				}
				try
				{
					if(mVideoMultiOrientationEnable)
					{
						mVideoOrientation = (4 - mScreenOrientation) * 90 % 360;
					}
					else
					{
						mVideoOrientation = 0;
					}
					if(mCurrentTabType == ShutterConfig.TabType.VIDEO)
					{
						if(mVideoMgr.getVideoNum() == 0)
						{
							//素材统计埋点
							sendCameraSensorsData();
							sendCameraSensorsDataWhenClick();
						}

						mVideoMgr.setVideoOrientation(mVideoOrientation);
						mVideoOrientation = mVideoMgr.getVideoOrientation();
					}
					mCanStopRecordState = -1;
					mRecordManager.setOrientationHint(mVideoOrientation);
					mRecordManager.startRecord();
					mRecordState = RecordState.RECORDING;
				}
				catch(Exception e)
				{
					e.printStackTrace();
					mRecordState = RecordState.IDLE;
				}
			}
		}
	}

	@Override
	public void onVideoProgressFull()
	{
		// TODO: 2017/12/20 CameraPageV3 --> onVideoProgressFull
	}

	@Override
	public boolean canRecord()
	{
		if(mCurrentTabType == ShutterConfig.TabType.GIF || mCurrentTabType == ShutterConfig.TabType.VIDEO)
		{
			if(mRecordState != RecordState.IDLE || !mRecordVideoEnable || !isValidVideoTimeLong())
			{
				return false;
			}
			if(!mVideoMultiSectionEnable && mVideoMgr != null && mVideoMgr.getVideoNum() == 1)
			{
				return false;
			}
            /*if (mCurrentTabType == ShutterConfig.TabType.VIDEO && mVideoIsPause)
            {
                return false;
            }*/
			if(mRecordManager != null)
			{
				int state = RecordState.IDLE;
				if(RenderHelper.sRenderThread != null)
				{
					state = RenderHelper.sRenderThread.getRecordState();
				}
				if(state != RecordState.WAIT)
				{
					return false;
				}
				if(!mRecordManager.isPrepareFinish())
				{
					//解决快速切换tab导致不能录制的问题
					if(mHasPrepared)
					{
						mHasPrepared = false;
						mCameraBottomControl.handleRecordingStatusInPause();
						return false;
					}
				}
			}
		}
		return true;
	}

	@Override
	public boolean isRecording()
	{
		if((mCurrentTabType == ShutterConfig.TabType.GIF || mCurrentTabType == ShutterConfig.TabType.VIDEO) && mRecordState == RecordState.RECORDING)
		{
			return true;
		}
		return false;
	}

	@Override
	public boolean canPauseRecord()
	{
		if((mCurrentTabType == ShutterConfig.TabType.GIF || mCurrentTabType == ShutterConfig.TabType.VIDEO) && mCanStopRecordState == 2)
		{
			return true;
		}
		return false;
	}

	@Override
	public boolean isCountDown()
	{
		if(mCurrentTimerMode > 0 && mTimerCounts > -1)
		{
			return true;
		}
		return false;
	}

	private void shutterTongJi2()
	{
		if(mCurrentTabType != ShutterConfig.TabType.PHOTO) return;

		TongJi2.AddCountByRes(getContext(), R.integer.拍照_点击拍照);

		if(CameraConfig.FlashMode.On.equals(mCurrentFlashMode))
		{
			TongJi2.AddCountByRes(getContext(), R.integer.拍照_闪光灯开);
		}
		else if(CameraConfig.FlashMode.Off.equals(mCurrentFlashMode))
		{
			TongJi2.AddCountByRes(getContext(), R.integer.拍照_闪光灯关);
		}
		else if(CameraConfig.FlashMode.Auto.equals(mCurrentFlashMode))
		{
			TongJi2.AddCountByRes(getContext(), R.integer.拍照_闪光灯自动);
		}
		else if(CameraConfig.FlashMode.Torch.equals(mCurrentFlashMode))
		{
			TongJi2.AddCountByRes(getContext(), R.integer.拍照_手电筒);
		}

		if(mCurrentTimerMode == 1)
		{
			TongJi2.AddCountByRes(getContext(), R.integer.拍照_设置_手动1秒);
		}
		else if(mCurrentTimerMode == 2)
		{
			TongJi2.AddCountByRes(getContext(), R.integer.拍照_设置_手动2秒);
		}
		else if(mCurrentTimerMode == 10)
		{
			TongJi2.AddCountByRes(getContext(), R.integer.拍照_设置_手动10秒);
		}
		else
		{
			TongJi2.AddCountByRes(getContext(), R.integer.拍照_设置_手动关闭);
		}

		int resId = -1;
		if(mCurrentRatio == CameraConfig.PreviewRatio.Ratio_1_1)
		{
			resId = R.integer.拍照_1_1照片比例;
		}
		else if(mCurrentRatio == CameraConfig.PreviewRatio.Ratio_4_3)
		{
			resId = R.integer.拍照_4_3照片比例;
		}
		else if(mCurrentRatio == CameraConfig.PreviewRatio.Ratio_16_9)
		{
			resId = R.integer.拍照_16_9照片比例;
		}
		if(resId != -1)
		{
			TongJi2.AddCountByRes(getContext(), resId);
		}

		TongJi2.AddCountByRes(getContext(), mTouchCapture ? R.integer.拍照_设置_触屏拍照开 : R.integer.拍照_设置_触屏拍照关);
	}

	@Override
	public void onPauseVideo()
	{
		if(mCurrentTabType == ShutterConfig.TabType.GIF || mCurrentTabType == ShutterConfig.TabType.VIDEO)
		{
			if(mRecordState == RecordState.RECORDING && mRecordManager != null)
			{
				// 只有录制视频才判断最后一秒
				boolean videoRecord = mCurrentTabType == ShutterConfig.TabType.VIDEO;
				// 这里的小于一秒时回调是倒计时回调的时长
				boolean lestThanOnSecond = videoRecord
						&& (mTargetDuration - mCurrentDuration) < 1000;
				// 这里的时间是剩余录制时间，跟前面的倒计时回调时长并不一致，存在明显的误差
				long lestTime = videoRecord
						? mTargetDuration - (mVideoMgr.getRecordDuration()
						+ System.currentTimeMillis() - mRecordManager.getRecordTime())
						: mTargetDuration;
				// 如果剩余时间小于1秒(相差一个50毫秒的计时回调)，并且倒计时记录的时间是大于1秒
				// 这属于最后一秒交汇处的地方，计时器还没回调，但已经进入了最后一秒的阶段
				if (lestTime < 1050 && !lestThanOnSecond && !mOnPause
						&& mVideoMgr.processLaseSecond()) {
					mLaseSecondPause = true;
				}
				// 最后300毫秒再点击停止的话，stopRecordByTime路径
				// 防止最后一次的倒计时回调不存在导致UI存在缺口
				else if (lestTime <= 300 && mVideoMgr.processLaseSecond()) {
					mCanStopRecordState = 1;
				}
				// 进入最后一秒时点击回调
				else if (lestThanOnSecond && lestTime < 1000 && !mOnPause
						&& mVideoMgr.processLaseSecond()) {
					mLaseSecondPause = true;
					mCurrentDuration = 0;
				} else
				// 旧逻辑
				if(mCanStopRecordState == 0)
				{
					if(mOnPause)
					{
						mCanStopRecordState = 2;
					}
					else
					{
						notifyAllWidgetUnlockUI(300);
						return;
					}
				}
				mRecordState = RecordState.STOP;
				mVideoIsPause = true;
				// 最后一秒停止倒计时，则停止录制器，但不停止时间
				if (mLaseSecondPause && mVideoMgr.processLaseSecond()) {
					mRecordManager.stopRecordWithoutStopTime();
				} else
				// 旧逻辑
				if(mCanStopRecordState == 1)
				{
					mRecordManager.stopRecordByTime();
				}
				else if(mCanStopRecordState == 2)
				{
					mRecordManager.stopRecord();
				}
				mHasPrepared = false;
			}
		}
	}

	// 防止多重统计和跳转
	private volatile boolean hasPreviewVideo = false;
	@Override
	public void onClickVideoSaveBtn()
	{
		// 如果此时不处于可录制状态/重置可录制状态(Prepared回调更新快门按钮的状态)，
		// MediaCodec的native层会报错并且捕捉不到log，这是IBinder链接的Native层方法的状态不对导致
		final int status = mCameraBottomControl.getShutterEnable();
		if (!(status == ShutterConfig.RecordStatus.CAN_RECORDED
				|| status == ShutterConfig.RecordStatus.RESET_CAN_RECORDED)) {
			// 最后一秒停止不用处理
			if (isValidVideoTimeLong()) {
				return;
			}
		}
		if (hasPreviewVideo) {
			return;
		}
		hasPreviewVideo = true;
		TongJi2.AddCountByRes(getContext(), R.integer.拍照_视频拍摄_确认拍照_打钩);
		MyBeautyStat.onClickByRes(R.string.拍照_视频拍摄页_主页面_完成拍摄);
		mUIHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				previewVideo();
				hasPreviewVideo = false;
			}
		}, 200);
		notifyAllWidgetUnlockUI(50);
	}


	/**
	 * 萌装照预览跳转
	 *
	 * @param bitmap
	 */
	private void previewCutePhoto(Bitmap bitmap)
	{
		boolean hasWaterMark = false;
		if (mCurrentFilterRes != null
				&& !mCurrentFilterRes.m_isStickerFilter
				&& mCurrentFilterRes.m_isHaswatermark)
		{
			hasWaterMark = true;
		}

		//素材统计埋点
		sendCameraSensorsData();

		saveFilterUri();

		HashMap<String, Object> params = new HashMap<>();
		params.put("type", 0);
		params.put("bmp", bitmap);
		params.put("res_id", mStickerId);
		params.put("ratio", mCurrentRatio);
		params.put("orientation", mPicOrientation);
		params.put("has_water_mark", hasWaterMark);
		params.put("cameraId", mCurrentCameraId);
		if (!TextUtils.isEmpty(mChannelValue)) {
			params.put("channelValue", mChannelValue);
		}
		if (mStickerTongJiId > 0) {
			params.put("res_tj_id", mStickerTongJiId + "");
		}
		mSite.openCutePhotoPreviewPage(getContext(), params);
	}

	/**
	 * gif预览跳转
	 *
	 * @param filePath
	 * @param duration
	 */
	private void previewGif(String filePath, long duration)
	{
		mResetBrightnessDisable = true;

		//素材统计埋点
		sendCameraSensorsData();

		saveFilterUri();

		HashMap<String, Object> params = new HashMap<>();
		params.put("mp4Path", filePath);
		params.put("duration", (int)duration);
		params.put("videoWidth", mVideoWidth);
		params.put("videoHeight", mVideoHeight);
		params.put("width", mGifWidth);
		params.put("height", mGifHeight);
		params.put("res_id", mStickerId);
		params.put("orientation", mVideoOrientation);
		if(mStickerTongJiId > 0)
		{
			params.put("res_tj_id", mStickerTongJiId + "");
		}
		mSite.openGifEditPage(getContext(), params);
	}

	/**
	 * 视频预览跳转
	 */
	private void previewVideo()
	{
		if(mRecordState != RecordState.IDLE)
		{
			return;
		}
		if(mVideoMgr == null || mVideoMgr.getVideoNum() <= 0) return;

		notifyAllWidgetLockUI();

//		//素材统计埋点
//		sendCameraSensorsData();

		saveFilterUri();

		//动态贴纸预览
		String filePath = null;
		int orientation = 0;
		if(mVideoMgr != null)
		{
			orientation = mVideoMgr.getVideoOrientation();
			VideoMgr.SubVideo subVideo = mVideoMgr.getSubVideo(0);
			if(subVideo != null) filePath = subVideo.mPath;
		}
		final HashMap<String, Object> params = new HashMap<>();
		params.put("type", 1);
		params.put("bmp", mVideoBG);
		params.put("mp4_path", filePath);
		params.put("width", mVideoWidth);
		params.put("height", mVideoHeight);
		params.put("res_id", mStickerId);
		params.put("ratio", mCurrentRatio);
		params.put("thirdParty", mIsThirdPartyVideo);
		params.put("video_mgr", mVideoMgr);
		params.put("orientation", orientation);
		params.put("record_audio_enable", mAudioEnable);
		if(!TextUtils.isEmpty(mChannelValue))
		{
			params.put("channelValue", mChannelValue);
		}
		if(mStickerTongJiId > 0) params.put("res_tj_id", mStickerTongJiId + "");

		mIsStickerPreviewBack = true;
		mResetBrightnessDisable = true;
        if(mRecordManager != null) {
            mRecordManager.destroy();
        }

		if (mModel != null && mModel.toUpperCase().contains("OPPO")) {//延迟跳转 防止预览页白屏
            mUIHandler.postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    if(mVideoMultiSectionEnable)
                    {
                        mSite.openVideoPreviewPage(getContext(), params);
                    }
                    else
                    {
                        mSite.openVideoPreviewPage(getContext(), params);
                    }
                }
            }, 500);
        } else {
            if(mVideoMultiSectionEnable)
            {
                mSite.openVideoPreviewPage(getContext(), params);
            }
            else
            {
                mSite.openVideoPreviewPage(getContext(), params);
            }
        }
	}

	private int saveFilterUri()
	{
		if(mCameraBottomControl != null)
		{
			int filterUri = mCameraBottomControl.getFilterUri();
			CameraConfig.getInstance().saveData(CameraConfig.ConfigMap.CameraFilterId, filterUri);
			return filterUri;
		}
		else
		{
			return 0;
		}
	}


	/**
	 * 神策拍照素材统计埋点
	 */
	private void sendCameraSensorsData()
	{
		//滤镜统计id
		String filterTjId = String.valueOf(mCurrentFilterRes != null ? mCurrentFilterRes.m_tjId : 0);

		//贴纸素材统计id
		String stickerTjId = String.valueOf(mStickerTongJiId > 0 ? mStickerTongJiId : 0);

		//组合变形素材统计
		boolean shapeCompose = isShapeCompose;
		int shapeComposeTjId = -1;
		if(mShapeStickerRes != null)
		{
			shapeComposeTjId = mShapeStickerRes.m_tjId;
		}

		//延迟拍摄模式
		MyBeautyStat.CameraTimer cameraTimer;
		switch(mCurrentTimerMode)
		{
			case 1:
				cameraTimer = MyBeautyStat.CameraTimer._1S;
				break;
			case 2:
				cameraTimer = MyBeautyStat.CameraTimer._2S;
				break;
			case 10:
				cameraTimer = MyBeautyStat.CameraTimer._10S;
				break;
			default:
				cameraTimer = MyBeautyStat.CameraTimer._关闭;
				break;
		}

		//触屏拍照
		boolean touchScreen = mTouchCapture;

		//镜头比例
		MyBeautyStat.CameraScale cameraScale;
		if(mCurrentRatio == CameraConfig.PreviewRatio.Ratio_4_3)
		{
			cameraScale = MyBeautyStat.CameraScale._4比3;
		}
		else if(mCurrentRatio == CameraConfig.PreviewRatio.Ratio_1_1)
		{
			cameraScale = MyBeautyStat.CameraScale._1比1;
		}
		else if(mCurrentRatio == CameraConfig.PreviewRatio.Ratio_16_9)
		{
			cameraScale = MyBeautyStat.CameraScale._16比9;
		}
		else if(mCurrentRatio == CameraConfig.PreviewRatio.Ratio_9_16)
		{
			cameraScale = MyBeautyStat.CameraScale._9比16;
		}
		else
		{
			cameraScale = MyBeautyStat.CameraScale._full;
		}

		//录制视频时长
		MyBeautyStat.VideoTime videoTime = MyBeautyStat.VideoTime._10秒;
		if(mTargetDuration == CameraConfig.VideoDuration.TEN_SECOND * 1000L)
		{
			videoTime = MyBeautyStat.VideoTime._10秒;
		}
		else if(mTargetDuration == CameraConfig.VideoDuration.THREE_MIN * 1000L)
		{
			videoTime = MyBeautyStat.VideoTime._3分钟;
		}

		//前后置镜头模式
		boolean frontCamera = isFront;

		switch(mCurrentTabType)
		{
			case ShutterConfig.TabType.PHOTO:
				MyBeautyStat.onUseCameraTakePhoto(filterTjId, cameraTimer, touchScreen, cameraScale, frontCamera);
				break;
			case ShutterConfig.TabType.CUTE:
				MyBeautyStat.onUseCameraCosFace(stickerTjId, filterTjId, shapeCompose, String.valueOf(shapeComposeTjId), cameraTimer, touchScreen, cameraScale, frontCamera);
				break;
			case ShutterConfig.TabType.GIF:
				MyBeautyStat.onUseCameraGif(stickerTjId, filterTjId, shapeCompose, String.valueOf(shapeComposeTjId), cameraTimer, touchScreen, frontCamera);
				break;
			case ShutterConfig.TabType.VIDEO:
				MyBeautyStat.onUseCameraVideo(stickerTjId, filterTjId, shapeCompose, String.valueOf(shapeComposeTjId), cameraTimer, touchScreen, cameraScale, frontCamera, videoTime);
				break;
		}
		//Log.d(TAG, "CameraPageV3 --> sendCameraSensorsData: type:" + mCurrentTabType + " filterTjId:" + filterTjId + " stickerTjId:" + stickerTjId + " shapeCompose:" + shapeCompose + " shapeComposeId:" + shapeComposeId);
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
		MyBeautyStat.onUseCameraBeauty(cameraBeautyData, false);
	}

	/**
	 * 动态贴纸拍照
	 */
	private void onCaptureAFrame()
	{
		if(mRecordState == RecordState.IDLE)
		{
			if(mRecordState == RecordState.CAPTURE_A_FRAME) return;
			mRecordState = RecordState.CAPTURE_A_FRAME;
			mPicOrientation = (4 - mScreenOrientation) * 90 % 360;
			if(mPreviewView != null)
			{
				sendCaptureTongjiParams();
				mPreviewView.runOnGLThread(new RenderRunnable()
				{
					@Override
					public void run(RenderThread renderThread)
					{
						if(renderThread != null)
						{
							renderThread.setRecordState(RecordState.CAPTURE_A_FRAME);
						}
						if(mUIHandler != null)
						{
							mUIHandler.removeMessages(MSG_CANCEL_TAKE_PICTURE);
						}
					}
				});
			}
			if(mUIHandler != null)
			{
				mUIHandler.removeMessages(MSG_CANCEL_TAKE_PICTURE);
				mUIHandler.sendEmptyMessageDelayed(MSG_CANCEL_TAKE_PICTURE, 5000);
			}
		}
	}

	//动态贴纸 拍照 统计
	private void sendCaptureTongjiParams()
	{
		if(mStickerTongJiId > 0)
		{
			TongJi2.AddCountByRes(getContext(), R.integer.拍照_动态贴纸_短按拍照, "" + mStickerTongJiId);
		}
		else
		{
			TongJi2.AddCountByRes(getContext(), R.integer.拍照_动态贴纸_短按拍照);
		}
	}

	private void stopTakePicture()
	{
		if(mUIHandler != null)
		{
			mUIHandler.removeMessages(MSG_TIMER_COUNT_DOWN);
			mUIHandler.removeMessages(MSG_CANCEL_TAKE_PICTURE);
			mUIHandler.sendEmptyMessage(MSG_CANCEL_TAKE_PICTURE);
		}
	}

	/**
	 * 时长少于 1s 不可录制
	 *
	 * @return
	 */
	private boolean isValidVideoTimeLong()
	{
		if(mVideoTimeLong < mVideoRecordMinDuration/* || mVideoTimeLong < 1000L*/)
		{
			return false;
		}
		return true;
	}

	private void checkAudioRecordPermission()
	{
		try
		{
			//第一段视频时判断是否有权限
			if(!mAudioEnable && (mVideoMgr == null || mVideoMgr.getVideoNum() == 0))
			{
				mAudioEnable = PermissionHelper.queryAudioRecordPermission();
			}
		}
		catch(Throwable t)
		{
			t.printStackTrace();
			mAudioEnable = false;
		}
	}

	private void initMyRecord()
	{
		if(mCurrentTabType == ShutterConfig.TabType.PHOTO || mCurrentTabType == ShutterConfig.TabType.CUTE)
		{
			return;
		}
		if(!mRecordVideoEnable)
		{
			return;
		}
		if(mRecordManager == null)
		{
			mRecordManager = new RecordManager(getContext());
		}
		if(mRecordManager != null)
		{
			if(mCurrentTabType == ShutterConfig.TabType.VIDEO)
			{
				checkAudioRecordPermission();
				if(!mAudioEnable && !mDisableAudioPermissionTip)
				{
					mDisableAudioPermissionTip = true;
					Toast.makeText(getContext(), getResources().getString(R.string.camerapage_check_record_permission), Toast.LENGTH_SHORT).show();
				}
			}

			//初始化录制视频路径
			mRecordManager.initDefaultPath(mIsThirdPartyVideo ? false : mVideoMgr == null);

			mRecordManager.setMessageHandler(mUIHandler);
			mHasPrepared = false;
			if(mCurrentTabType == ShutterConfig.TabType.GIF)
			{
				mRecordManager.setVideoTimeLong(mGifTimeLong);
				mRecordManager.setVideoSize(mVideoWidth, mVideoHeight);
				mRecordManager.setAudioRecordEnable(false);
			}
			else
			{
//                Log.i(TAG, "initMyRecord: "+mVideoTimeLong);
				mRecordManager.setVideoTimeLong(mVideoTimeLong);
				mRecordManager.setVideoSize(mVideoWidth, mVideoHeight);
				mRecordManager.setAudioRecordEnable(mAudioEnable);
			}
			mRecordManager.setOnRecordListener(new OnRecordListener()
			{
				@Override
				public void onPrepare(final MediaMuxerWrapper mediaMuxerWrapper)
				{
					//Log.d(TAG, "onPrepare: ");
					if(mPreviewView != null)
					{
						mPreviewView.runOnGLThread(new RenderRunnable()
						{
							@Override
							public void run(RenderThread renderThread)
							{
								if(renderThread != null)
								{
									renderThread.setMediaMuxerWrapper(mediaMuxerWrapper);
									renderThread.setRecordState(RecordState.IDLE);
									renderThread.setRecordState(RecordState.PREPARE);
								}
							}
						});
						if(mResetRecordUIState && mCameraBottomControl != null && isValidVideoTimeLong())
						{
							mResetRecordUIState = false;
							mCameraBottomControl.setShutterEnable(ShutterConfig.RecordStatus.RESET_CAN_RECORDED);
							notifyAllWidgetUnlockUI(0);
						}
					}
					if(mCanCaptureFrameAfterPrepare)
					{
						mCanCaptureFrameAfterPrepare = false;
						if(mPreviewView != null)
						{
							mPreviewView.runOnGLThread(new RenderRunnable()
							{
								@Override
								public void run(RenderThread renderThread)
								{
									if(renderThread != null)
									{
										renderThread.setRecordState(RecordState.IDLE);
									}
								}
							});
						}
						if(mUIHandler != null)
						{
							mUIHandler.post(new Runnable()
							{
								@Override
								public void run()
								{
									onCaptureAFrame();
								}
							});
						}
					}
				}

				@Override
				public void onStart(MediaMuxerWrapper mediaMuxerWrapper)
				{
//                Log.d(TAG, "record manager onStart: ");
					if(mCurrentTabType == ShutterConfig.TabType.VIDEO)
					{
						if(mAudioEnable)
						{
							//检查录音功能是否被其它应用占用
							mAudioEnable = PermissionHelper.queryAudioRecordPermission();
							if(!mAudioEnable && mRecordManager != null)
							{
								mRecordManager.setAudioRecordEnable(false);

                            /*if (mUIHandler != null) {
                                mUIHandler.removeMessages(MSG_HIDE_NAVIGATION_BAR);
                                mUIHandler.sendEmptyMessageDelayed(MSG_HIDE_NAVIGATION_BAR, 3000);
                            }*/
							}
						}
						else
						{
							checkAudioRecordPermission();
							if(mAudioEnable && mRecordManager != null)
							{
								mRecordManager.setAudioRecordEnable(true);
							}
						}
					}

					mCanStopRecordState = 0;
					if(mPreviewView != null)
					{
						mPreviewView.runOnGLThread(new RenderRunnable()
						{
							@Override
							public void run(RenderThread renderThread)
							{
								if(renderThread != null)
								{
									renderThread.setFrameTopPadding(mFrameTopPadding);
									renderThread.setDrawEndingEnable(false);
									renderThread.setVideoOrientation(mVideoOrientation);
									renderThread.setRecordState(RecordState.START);

									if(mStickerId == 39165)
									{
										//阿玛尼定制
										renderThread.getFilterManager().changeColorFilterRenderStyle(true);
										StickerDrawHelper.getInstance().mResIsChange = true;
										//重新开始播放阿玛尼音效
										seekToStickerBgmSound(0);
									}
								}
							}
						});
					}
				}

				@Override
				public void onResume()
				{
//                Log.d(TAG, "record manager onResume: ");
					if(mPreviewView != null)
					{
						mPreviewView.runOnGLThread(new RenderRunnable()
						{
							@Override
							public void run(RenderThread renderThread)
							{
								if(renderThread != null)
								{
									renderThread.setRecordState(RecordState.RESUME);
								}
							}
						});
					}
				}

				@Override
				public void onProgressChange(float progress)
				{
					if(mCurrentTabType == ShutterConfig.TabType.GIF)
					{
						int shutterEnable = ShutterConfig.RecordStatus.CAN_RECORDED;
						long time = Math.round(progress * mGifTimeLong / 100);
						if(time < mVideoRecordMinDuration)
						{//500-1000
							mCanStopRecordState = 1;
							shutterEnable = ShutterConfig.RecordStatus.LESS_THAN_ONE_SEC;
						}
						else
						{
							mCanStopRecordState = 2;
						}
						if(mCameraBottomControl != null)
						{
							mCameraBottomControl.setRecordProgressAndText((int)Math.round(progress * 3.6), null, shutterEnable);
						}
					}
					else
					{
						int degree = (int)Math.round(progress * mVideoTimeLong / mTargetDuration * 3.6);
						String timeStr = null;
						int shutterEnable = ShutterConfig.RecordStatus.CAN_RECORDED;
						if(mVideoMgr != null)
						{
							long subTime = Math.round(progress * mVideoTimeLong / 100);
							long time = mVideoMgr.getRecordDuration() + subTime;
							mCurrentDuration = time;
							// 第一段视频至少要1秒
							if(subTime < mVideoRecordMinDuration && (!mVideoMgr.processLaseSecond() || mVideoMgr.getVideoNum() == 0))
							{//500-1000
								mCanStopRecordState = 0;
							} else if (subTime < 250 && mVideoMgr.processLaseSecond()) { // 录制的视频小于200毫秒不可点击
								mCanStopRecordState = 0;
							}
							else
							{
								mCanStopRecordState = 2;
							}

							if(time > mTargetDuration - 1100)
							{
								//剩余可录制时长不足1s继续录制(旧逻辑)
								if (!mVideoMgr.processLaseSecond()) {
									mCanStopRecordState = 0;
								}
								shutterEnable = ShutterConfig.RecordStatus.AT_THE_LAST_SEC;
							}
							timeStr = mVideoMgr.getDurationStr(time);
						}
						//Log.i(TAG, "onProgressChange: "+progress+", "+degree+", "+timeStr);
						if(progress >= 100.0f)
						{
							degree += 10;
						}
						if(mCameraBottomControl != null)
						{
							if(mCanStopRecordState == 2)
							{
								//显示暂停按钮
								mCameraBottomControl.setIsDrawPauseLogo(true);
							}
							mCameraBottomControl.setRecordProgressAndText(degree, timeStr, shutterEnable);
						}
					}
				}

				@Override
				public void onPause()
				{
//                Log.d(TAG, "record manager onPause: ");
					if(mPreviewView != null)
					{
						mPreviewView.runOnGLThread(new RenderRunnable()
						{
							@Override
							public void run(RenderThread renderThread)
							{
								if(renderThread != null)
								{
									renderThread.setRecordState(RecordState.PAUSE);
								}
							}
						});
					}
				}

				@Override
				public void onStop(boolean isValid, final long duration, final String filePath)
				{
					mHasPrepared = false;
					//Log.i(TAG, "record manager onStop: " + isValid + ", duration:" + duration + ", path:" + (filePath == null ? "null" : filePath));
					if(mOnPause)
					{
						//录制时按Home键退出
						return;
					}
					boolean reInitMyRecord = false;
					if(isValid)
					{
						if(filePath == null)
						{
							if(mPreviewView != null)
							{
								mPreviewView.runOnGLThread(new RenderRunnable()
								{
									@Override
									public void run(RenderThread renderThread)
									{
										if(renderThread != null)
										{
											renderThread.setDrawEndingEnable(false);
											renderThread.setRecordState(RecordState.STOP);
										}
									}
								});
							}
							if(mUIHandler != null && mVideoIsPause && mCurrentTabType == ShutterConfig.TabType.VIDEO)
							{
								mVideoIsPause = false;
								if(mVideoMultiSectionEnable)
								{
									mUIHandler.post(new Runnable()
									{
										@Override
										public void run()
										{
											if(mVideoMgr != null)
											{
												long time = mVideoMgr.getRecordDuration() + duration;
												mCameraBottomControl.setRecordTimeText(mVideoMgr.getDurationStr(time));
											}
											// 最后一秒不用停止
											if (mLaseSecondPause && mVideoMgr.processLaseSecond()) {
												notifyAllWidgetUnlockUI(1000);
												return;
											}
											mCameraBottomControl.setShutterEnable(ShutterConfig.RecordStatus.PAUSE_VIDEO);
											mCameraBottomControl.showPauseAnimator();

											notifyAllWidgetUnlockUI(1000);
										}
									});
								}
							}
							return;
						}
						else
						{
							//录制时按Home键退出
							if(mOnPause)
							{
								return;
							}
							if(mCurrentTabType == ShutterConfig.TabType.GIF)
							{
								if(mPreviewView != null)
								{
									mPreviewView.runOnGLThread(new RenderRunnable()
									{
										@Override
										public void run(RenderThread renderThread)
										{
											if(renderThread != null)
											{
												renderThread.setRenderState(true);
											}
										}
									});
								}
							}

							//跳转页面 保存
							if(mSite != null)
							{
								if(mCurrentTabType == ShutterConfig.TabType.GIF)
								{
									AudioControlUtils.setCanResumeMusic(false);
									//gif 表情编辑页
									previewGif(filePath, duration);
									return;

								}
								else if(mVideoMgr != null)
								{
									//视频多段录制
									VideoMgr.SubVideo subVideo = new VideoMgr.SubVideo();
									subVideo.mPath = filePath;
									subVideo.mDuration = duration;
									mVideoMgr.add(subVideo);

									mVideoTimeLong = mTargetDuration - mVideoMgr.getRecordDuration();
									// 如果小于最小录制时间，则直接显示录制完成的时间，否则显示实际录制时长
									if (mVideoMgr.processLaseSecond() && mVideoTimeLong < 1000) {
                                        mCameraBottomControl.setRecordProgressAndText(100,
                                                mVideoMgr.getDurationStr(mTargetDuration));

									} else {
                                        mCameraBottomControl.setRecordTimeText(mVideoMgr.getDurationStr());
									}
									mResetRecordUIState = true;
									//Log.i(TAG, "onStop: " + mTargetDuration + ", " + mVideoMgr.getRecordDuration() + ", " + mVideoTimeLong);
									// 最后一秒点击时不用跳转
									if (mLaseSecondPause && mVideoMgr.processLaseSecond()) {
										mLaseSecondPause = false;
										mCameraBottomControl.setShutterEnable(ShutterConfig.RecordStatus.PAUSE_VIDEO);
										mCameraBottomControl.showPauseAnimator();
										mRecordState = RecordState.IDLE;
									} else
									// 跳转，需要判断点击时是否进入了最后一秒，如果还没进入，则直接开始下一次
									if(mVideoMgr.isRecordFinish() || !isValidVideoTimeLong() || !mVideoMultiSectionEnable)
									{
										AudioControlUtils.setCanResumeMusic(false);
										mRecordState = RecordState.IDLE;
										previewVideo();
										return;
									}
									else
									{
										reInitMyRecord = true;
									}
								}
							}
						}
					}
					else
					{
						if(mUIHandler != null && mVideoIsPause)
						{
							mVideoIsPause = false;
							mUIHandler.post(new Runnable()
							{
								@Override
								public void run()
								{
									mCameraBottomControl.showPauseAnimator();
								}
							});
						}
						//录制时按Home键退出
						if(mOnPause)
						{
							return;
						}
					}
					if(reInitMyRecord)
					{
						initMyRecord();
					}
					else
					{
						prepareRecord(0);
					}
				}
			});
			prepareRecord(0);
		}
	}

	private void prepareRecord(final int delay)
	{
		if(mRecordManager == null)
		{
			return;
		}
		if(delay > 0)
		{
			mRecordState = RecordState.IDLE;
			mUIHandler.postDelayed(new Runnable()
			{
				@Override
				public void run()
				{
					if(mRecordManager != null)
					{
						try
						{
							mRecordManager.prepare();
						}
						catch(Exception e)
						{
							e.printStackTrace();
						}
					}
				}
			}, delay);
		}
		else
		{
			try
			{
				mRecordManager.prepare();
				mRecordState = RecordState.IDLE;
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onBackBtnClick()
	{
		onBack();
	}

	@Override
	public void onUserLogin()
	{
		onPause();
		if(mSite != null)
		{
			mSite.userLogin(getContext());
		}
	}

	@Override
	public void closeUnLockView()
	{
		//退出微信时会调用一次，关闭dialog会调用一次
		mUnlockViewCloseCount++;
		if(mUnlockViewCloseCount == 2)
		{
			mSystemUiVisibility = -1;
			changeSystemUiVisibility(View.GONE);
		}
	}

	@Override
	public void openUnLockView()
	{
		mUnlockViewCloseCount = 0;
	}

	@Override
	public void onCancelCountDown()
	{
//        stopTakePicture();
		stopCountDown();
	}

	private void stopCountDown()
	{
		if(mUIHandler != null && mCurrentTimerMode > 0)
		{
			if(mCurrentTabType == ShutterConfig.TabType.PHOTO || mCurrentTabType == ShutterConfig.TabType.CUTE)
			{
				mUIHandler.removeMessages(MSG_TIMER_COUNT_DOWN);
			}

			if(mCurrentTabType == ShutterConfig.TabType.VIDEO || mCurrentTabType == ShutterConfig.TabType.GIF)
			{
				mUIHandler.removeMessages(MSG_RECORD_TIMER_COUNT_DOWN);
			}
			mUIHandler.removeMessages(MSG_CANCEL_TAKE_PICTURE);
			mUIHandler.sendEmptyMessage(MSG_CANCEL_TAKE_PICTURE);
		}
		mTimerCounts = -1;
	}

	@Override
	public void onClickOpenPhoto()
	{
		CameraHandler cameraHandler = RenderHelper.getCameraHandler();
		if(cameraHandler != null)
		{
			cameraHandler.setFlashMode(CameraConfig.FlashMode.Off);
		}

		if(mBrightnessUtils != null)
		{
			mBrightnessUtils.resetToDefault();
		}
		keepScreenWakeUp(false);
		stopTakePicture();

		TongJi2.AddCountByRes(getContext(), R.integer.拍照_打开相册);
		MyBeautyStat.onClickByRes(R.string.拍照_美颜拍照_主页面_选相册);
		if(mSite != null)
		{
			HashMap<String, Object> params = new HashMap<>();
			params.put(DataKey.COLOR_FILTER_ID, (mCurrentFilterRes != null ? mCurrentFilterRes.m_id : 0));
			mSite.openPhotoPicker(getContext(), params);
		}
	}

	private BrightnessUtils mBrightnessUtils;

	/**
	 * 保持屏幕常亮
	 *
	 * @param wakeup
	 */
	private boolean mIsKeepScreenOn;

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

	@Override
	public void onClickSetting()
	{
		MyBeautyStat.onClickByRes(R.string.拍照_拍照_顶部设置_更多设置);
		MyBeautyStat.onPageStartByRes(R.string.拍照_拍照_顶部设置);
		if(mCameraPopSetting != null && mCameraTopControl != null)
		{
			mCameraTopControl.SetButtonClickable(false);
			mCameraTopControl.SetBtnAlpha(0);
			mCameraPopSetting.resetDataStatus();
			mCameraPopSetting.showRatioLayout(false);
			mCameraPopSetting.doPopTransYAnim(-mPopHeight, 0);
		}
	}

	@Override
	public void onClickCameraSwitch()
	{
		boolean isFront = mCameraTopControl.isFrontMode();
		MyBeautyStat.onClickByRes(isFront ? R.string.拍照_拍照_顶部设置_切换前置镜头 : R.string.拍照_拍照_顶部设置_切换后置镜头);

		if(isFront)
		{
			// 改了需求，切换前置的时候，如果是 手电筒 模式，不记住状态，恢复 关闭 模式
			if(CameraConfig.getInstance().getString(CameraConfig.ConfigMap.FlashModeStr).equals(CameraConfig.FlashMode.Torch))
			{
				mCurrentFlashMode = CameraConfig.FlashMode.Off;
				CameraConfig.getInstance().saveData(CameraConfig.ConfigMap.FlashModeStr, mCurrentFlashMode);
			}
		}
		else
		{
			if(CameraConfig.getInstance().getString(CameraConfig.ConfigMap.FrontFlashModeStr).equals(CameraConfig.FlashMode.Torch))
			{
				mCurrentFlashMode = CameraConfig.FlashMode.Off;
				CameraConfig.getInstance().saveData(CameraConfig.ConfigMap.FrontFlashModeStr, CameraConfig.FlashMode.Off);
			}
			mShowSplashMask = false;
		}

		CameraHandler cameraHandler = RenderHelper.getCameraHandler();
		if(cameraHandler != null)
		{
			mIsSwitchCamera = true;
			cameraHandler.switchCamera();
			RenderHelper.sCameraIsChange = true;
		}

		if(mCameraPopSetting != null)
		{
			mCameraPopSetting.setCameraMode(isFront);
		}
	}

	@Override
	public void onClickCameraPatch()
	{
		//关闭弹出的组件view
		if(mCameraBottomControl != null && mCameraBottomControl.IsOpenOtherPage())
		{
			mCameraBottomControl.CloseOpenedPage();
		}

		isPatchMode = true;

		showOrHideFaceRect(false);

		showPatchDialog(PatchDialogType.STEP_1_PATCH_TIPS, null);
	}

	private void showOrHideFaceRect(final boolean show)
	{
		if(mPreviewView != null)
		{
			mPreviewView.runOnGLThread(new RenderRunnable()
			{
				@Override
				public void run(RenderThread renderThread)
				{
					if(renderThread.getFilterManager() != null)
					{
						renderThread.getFilterManager().setFaceRectEnable(show);
					}
				}
			});
		}
	}

	/**
	 * 镜头矫正判断
	 */
	private void updatePatchConfig()
	{
		if (isTailorMadeSetting) {

			mCameraTopControl.SetCameraPatchState(false);
			return;
		}

		int showCameraPatchBtnTimes = CameraConfig.getInstance().getInt(CameraConfig.ConfigMap.ShowCameraPatchBtn);
		if(showCameraPatchBtnTimes < 3 && mCurrentTabType == ShutterConfig.TabType.PHOTO)
		{
			if(!CameraConfig.getInstance().getBoolean(CameraConfig.ConfigMap.UsePatchBtn) && !isPatchMode)
			{
				mCameraTopControl.SetCameraPatchState(true);
			}
			CameraConfig.getInstance().saveData(CameraConfig.ConfigMap.ShowCameraPatchBtn, showCameraPatchBtnTimes + 1);
		}
		else
		{
			mCameraTopControl.SetCameraPatchState(false);
		}
	}


	/**
	 * @param type 1:开始提示，2:镜头校正，3:照片校正，4:完成 ,{@link cn.poco.camera.PatchDialogType}
	 * @param pic
	 */
	private void showPatchDialog(final int type, Bitmap pic)
	{
		if(!isCurrentPage())
		{
			return;
		}

		MyFramework.ClearTopView(getContext());

		if(type == PatchDialogType.STEP_1_PATCH_TIPS)
		{
			PatchMsgView patchMsgView = PatchMsgView.createPatchView(getContext(), type, new PatchMsgViewClickListener());
			if(patchMsgView != null)
			{
				FrameLayout frameLayout = new FrameLayout(getContext());
				frameLayout.setClickable(true);
				frameLayout.setBackgroundColor(PatchMsgView.PATCH_MSG_VIEW_BACKGROUND_COLOR);
				FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
				params.topMargin = CameraPercentUtil.HeightPxToPercent(396);
				frameLayout.addView(patchMsgView.mParentView, params);
				frameLayout.setTag(PatchMsgView.PATCH_VIEW_TAG);
				MyFramework.AddTopView(getContext(), frameLayout, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			}

		}
		else if(type == PatchDialogType.STEP_2_PATCH_CAMERA)
		{
			PatchMsgView patchMsgView = PatchMsgView.createPatchView(getContext(), type, new PatchMsgViewClickListener());
			if(patchMsgView != null)
			{
				int height = CameraPercentUtil.HeightPxToPercent(320);
				int currentVirtualKeyHeight = ShareData.getCurrentVirtualKeyHeight((Activity)getContext());
				if(currentVirtualKeyHeight > 0)
				{
					height += currentVirtualKeyHeight;
				}
				FrameLayout.LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, height);
				FrameLayout frameLayout = new FrameLayout(getContext());
				frameLayout.addView(patchMsgView.mParentView, params);

				FrameLayout frameLayout2 = new FrameLayout(getContext());
				frameLayout2.setBackgroundColor(Color.TRANSPARENT);

				params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
				frameLayout2.addView(frameLayout, params);
				frameLayout2.setClickable(true);
				frameLayout2.setTag(PatchMsgView.PATCH_VIEW_TAG);
				MyFramework.AddTopView(getContext(), frameLayout2, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

				//初始化矫正角度
				tempPreviewDegree = getPreviewPatchDegree();
				tempPictureDegree = getPicturePatchDegree();
				if(mPreviewView != null)
				{
					mPreviewView.queueEvent(new RenderRunnable()
					{
						@Override
						public void run(RenderThread renderThread)
						{
							if(renderThread != null && renderThread.getFilterManager() != null)
							{
								renderThread.getFilterManager().setPreviewDegree(tempPreviewDegree, isFront);
							}
						}
					});
				}
			}
		}
		else if(type == PatchDialogType.STEP_3_PATCH_PICTURE)
		{
			PatchMsgView patchMsgView = PatchMsgView.createPatchView(getContext(), type, new PatchMsgViewClickListener());
			if(patchMsgView != null)
			{
				FrameLayout frameLayout = new FrameLayout(getContext());
				frameLayout.setBackgroundColor(PatchMsgView.PATCH_MSG_VIEW_BACKGROUND_COLOR);
				FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
				params.topMargin = CameraPercentUtil.HeightPxToPercent(274);
				frameLayout.addView(patchMsgView.mParentView, params);
				frameLayout.setTag(PatchMsgView.PATCH_VIEW_TAG);
				frameLayout.setClickable(false);
				((PatchMsgView.MsgView)patchMsgView.mMsgView).setPicture(pic);
				MyFramework.AddTopView(getContext(), frameLayout, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			}

		}
		else if(type == PatchDialogType.STEP_4_PATCH_FINISH)
		{
			PatchMsgView patchMsgView = PatchMsgView.createPatchView(getContext(), type, new PatchMsgViewClickListener());
			if(patchMsgView != null)
			{
				FrameLayout frameLayout = new FrameLayout(getContext());
				frameLayout.setBackgroundColor(PatchMsgView.PATCH_MSG_VIEW_BACKGROUND_COLOR);
				FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				params.gravity = Gravity.CENTER;
				frameLayout.setClickable(false);
				frameLayout.addView(patchMsgView.mParentView, params);
				frameLayout.setTag(PatchMsgView.PATCH_VIEW_TAG);
				MyFramework.AddTopView(getContext(), frameLayout, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			}
		}
	}


	/**
	 * 是否是当前page
	 *
	 * @return
	 */
	private boolean isCurrentPage()
	{
		IPage iPage = MyFramework.GetTopPage(getContext());
		if(iPage != null && iPage instanceof CameraPageV3)
		{
			return true;
		}
		return false;
	}


	/**
	 * 矫正镜头事件监听
	 */
	private class PatchMsgViewClickListener implements PatchMsgView.ClickListener
	{
		@Override
		public void onClick(View view, @PatchDialogType int type, int which)
		{
			if(type == PatchDialogType.STEP_1_PATCH_TIPS)
			{
				//1：开始镜头矫正
				if(which == 1)
				{
					isPatchMode = true;
					CameraConfig.getInstance().saveData(CameraConfig.ConfigMap.UsePatchBtn, true);
					if(mCameraTopControl != null)
					{
						mCameraTopControl.SetCameraPatchState(false);
						mCameraTopControl.updateUI();
					}

					showPatchDialog(PatchDialogType.STEP_2_PATCH_CAMERA, null);
				}
			}
			else if(type == PatchDialogType.STEP_2_PATCH_CAMERA)
			{
				//0: 保存角度并拍照, 1:旋转角度
				if(which == 0)
				{
					//拍照
					isPatchMode = true;
					mCameraTopControl.SetButtonClickable(true);
					savePatchConfig();
					Toast.makeText(getContext(), R.string.camerapage_to_take_photo, Toast.LENGTH_SHORT).show();
					MyFramework.ClearTopView(getContext());
					onShutterClick();
				}
				else if(which == 1)
				{
					//旋转预览
					onPatchPreviewDegree();
				}
			}
			else if(type == PatchDialogType.STEP_3_PATCH_PICTURE)
			{
				//确定完成图片矫正
				if(which == 1)
				{
					isPatchMode = true;
					tempPictureDegree = (((PatchMsgView.MsgView)view).getRotate() + getPicturePatchDegree()) % 360;
					savePatchConfig();
					showPatchDialog(PatchDialogType.STEP_4_PATCH_FINISH, null);
				}
			}
			else if(type == PatchDialogType.STEP_4_PATCH_FINISH)
			{
				//ok完成镜头矫正
				//修正完成后重新打开镜头
				if(!isPageClose && !isPatchOtherCamera && !isPatchFinishToClose && isPatchMode)
				{

					isPatchMode = false;
					mCameraTopControl.SetCameraPatchState(false);
					mCameraTopControl.updateUI();
					CameraHandler cameraHandler = RenderHelper.getCameraHandler();
					if(cameraHandler != null)
					{
						cameraHandler.reopenCamera();
						cameraHandler.setPreviewOrientation(tempPreviewDegree);
					}
					MyFramework.ClearTopView(getContext());
				}
			}
		}

		@Override
		public void onDismiss(View view, @PatchDialogType int type, int which)
		{
			//“知道了” //移除dialog
			if(type == PatchDialogType.STEP_1_PATCH_TIPS && which == 0)
			{
				MyFramework.ClearTopView(getContext());
				if(isPatchFinishToClose)
				{
					onBackBtnClick();
				}
				else
				{
					isPatchMode = false;
					notifyAllWidgetUnlockUI(0); // 镜头校正结束，解ui
					showOrHideFaceRect(true);
				}
			}

			if(type == PatchDialogType.STEP_4_PATCH_FINISH && view instanceof PatchMsgView.MsgView)
			{
				//修正另外一个镜头
				if(((PatchMsgView.MsgView)view).canQuitPatch())
				{
					if(isPatchOtherCamera)
					{
						isPatchOtherCamera = false;
						CameraHandler cameraHandler = RenderHelper.getCameraHandler();
						if(cameraHandler != null && cameraHandler.getCamera() != null && mCurrentCameraId != cameraHandler.getCamera().getNextCameraId())
						{
							onClickCameraSwitch();
							postDelayed(new Runnable()
							{
								@Override
								public void run()
								{
									showPatchDialog(PatchDialogType.STEP_1_PATCH_TIPS, null);
								}
							}, 100);
						}
					}
					else
					{
						if(isPatchFinishToClose)
						{
							MyFramework.ClearTopView(getContext());
							onBackBtnClick();
						}
						else
						{
							// 镜头校正结束，解ui
							notifyAllWidgetUnlockUI(0);
							showOrHideFaceRect(true);
						}
					}
				}
			}
		}
	}


	/**
	 * 矫正镜头事件监听
	 */
	private class PatchDialogOnClickListener implements DialogInterface.OnClickListener, DialogInterface.OnDismissListener
	{
		private int mType;
		private PatchDialogV2 mDialog;

		/**
		 * @param mType 1:开始提示，2:镜头校正，3:照片校正，4:完成，{@link PatchDialogType}
		 */
		public PatchDialogOnClickListener(@NonNull PatchDialogV2 mDialog, int mType)
		{
			this.mDialog = mDialog;
			this.mType = mType;
		}

		@Override
		public void onClick(DialogInterface dialog, int which)
		{
			if(mType == PatchDialogType.STEP_1_PATCH_TIPS)
			{
				//0：nothing 1：镜头矫正
				if(which == 1)
				{
					isPatchMode = true;
					CameraConfig.getInstance().saveData(CameraConfig.ConfigMap.UsePatchBtn, true);
					if(mCameraTopControl != null)
					{
						mCameraTopControl.SetCameraPatchState(false);
						mCameraTopControl.updateUI();
					}
					showPatchDialog(PatchDialogType.STEP_2_PATCH_CAMERA, null);
				}
			}
			else if(mType == PatchDialogType.STEP_2_PATCH_CAMERA)
			{
				//nothing to do
			}
			else if(mType == PatchDialogType.STEP_3_PATCH_PICTURE)
			{
				if(which == 1)
				{
					isPatchMode = true;
					//完成图片矫正
					tempPictureDegree = (mDialog.getRotate() + getPicturePatchDegree()) % 360;
					savePatchConfig();
					showPatchDialog(PatchDialogType.STEP_4_PATCH_FINISH, null);
				}
			}
			else if(mType == PatchDialogType.STEP_4_PATCH_FINISH)
			{
				if(!isPageClose && !isPatchOtherCamera && !isPatchFinishToClose && isPatchMode)
				{
					isPatchMode = false;
					mCameraTopControl.SetCameraPatchState(false);
					mCameraTopControl.updateUI();
					CameraHandler cameraHandler = RenderHelper.getCameraHandler();
					if(cameraHandler != null)
					{
						cameraHandler.reopenCamera();
						cameraHandler.setPreviewOrientation(tempPreviewDegree);
					}
				}
			}

		}

		@Override
		public void onDismiss(DialogInterface dialog)
		{
			if(mDialog.canQuitPatch())
			{
				if(isPatchOtherCamera && mType == PatchDialogType.STEP_4_PATCH_FINISH)
				{
					isPatchOtherCamera = false;
					CameraHandler cameraHandler = RenderHelper.getCameraHandler();
					if(cameraHandler != null && cameraHandler.getCamera() != null && mCurrentCameraId != cameraHandler.getCamera().getNextCameraId())
					{
						onClickCameraSwitch();
						showPatchDialog(PatchDialogType.STEP_1_PATCH_TIPS, null);
					}
				}
				else
				{
					if(isPatchFinishToClose)
					{
						onBackBtnClick();
					}
					else
					{
						isPatchMode = false;
						// 镜头校正结束，解ui
						notifyAllWidgetUnlockUI(0);
					}
				}
				mSystemUiVisibility = -1;
			}

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
					if(mHasOpenAnim && mIsDoingAnim)
					{
						if(mCameraErrorTipsDialog != null)
						{
							mCameraErrorTipsDialog.setCanClosePage(false);
						}
						return;
					}
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

	private void showWaitAnimDialog(boolean show)
	{
		if(mDialog == null)
		{
			mDialog = new WaitAnimDialog((Activity)getContext());
		}
		if(show)
		{
			mDialog.show();
		}
		else
		{
			mDialog.dismiss();
		}
	}


	/**
	 * 镜头预览实时矫正
	 */
	private void onPatchPreviewDegree()
	{
		CameraHandler cameraHandler = RenderHelper.getCameraHandler();
		if(cameraHandler != null)
		{
			cameraHandler.patchPreviewDegree(new ResultCallback<Integer>()
			{
				@Override
				public void onCallback(Integer r)
				{
					tempPreviewDegree = r;
					if(mPreviewView != null)
					{
						mPreviewView.queueEvent(new RenderRunnable()
						{
							@Override
							public void run(RenderThread renderThread)
							{
								if(renderThread != null && renderThread.getFilterManager() != null)
								{
									renderThread.getFilterManager().setPreviewDegree(tempPreviewDegree, isFront);
								}
							}
						});
					}
				}
			});
		}
	}

	@Override
	public void onClickRatioBtn()
	{
		mCameraPopSetting.showRatioLayout(true);
		mCameraPopSetting.doPopTransYAnim(-mPopHeight, 0);
		mLightnessSeekBar.setVisibility(GONE);
		notifyAllWidgetUnlockUI(300);
	}

	@Override
	public void onClickVideoDurationBtn(int second)
	{
		mTargetDuration = second * 1000L;
		mVideoTimeLong = mTargetDuration;
		if(second == CameraConfig.VideoDuration.TEN_SECOND)
		{
			mContentView.showDurationTips(getContext().getString(R.string.camera_video_duration_ten_second), second);
		}
		else
		{
			mContentView.showDurationTips(getContext().getString(R.string.camera_video_duration_three_minute), second);
		}
		if(mRecordManager != null)
		{
			mRecordManager.setVideoTimeLong(mVideoTimeLong);
		}
	}

	@Override
	public void onConfirmVideoDel()
	{
		TongJi2.AddCountByRes(getContext(), R.integer.拍照_视频拍摄_删除选段);
		MyBeautyStat.onClickByRes(R.string.拍照_视频拍摄页_主页面_删除上一段);
		// 解决第一段录制后快速回删，此时MediaCodec的release可能没回调，
		// 导致录制时长/录制时间UI显示错乱的情况
		mUIHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				deleteSubVideo();
			}
		}, 200);
	}

	private void deleteSubVideo()
	{
		String timeStr = null;
		if(mVideoMgr != null)
		{
			mVideoTimeLong = mTargetDuration - mVideoMgr.remove();
			timeStr = mVideoMgr.getDurationStr();
		}
		if(mRecordManager != null)
		{
			mRecordManager.setVideoTimeLong(mVideoTimeLong);
		}
		if(mCameraBottomControl != null)
		{
//            mCameraBottomControl.setRecordVideoEnable(isValidVideoTimeLong());
			mCameraBottomControl.setRecordTimeText(timeStr);
			// 重置录制按钮的状态
			if(isValidVideoTimeLong()) {
				mLaseSecondPause = false;
				mResetRecordUIState = true;
				mCameraBottomControl.setShutterEnable(ShutterConfig.RecordStatus.CAN_RECORDED);
			}
		}
	}

	@Override
	public void onClearAllVideo()
	{
		ClearAllDialogView dialog = new ClearAllDialogView(getContext());
		dialog.setOnClickListener(new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				switch(which)
				{
					case ClearAllDialogView.VIEW_ABANDON:
					{
						TongJi2.AddCountByRes(getContext(), R.integer.拍照_视频拍摄_拍摄暂停_返回);
						MyBeautyStat.onClickByRes(R.string.拍照_视频拍摄页_主页面_返回_放弃拍摄);
						if(mVideoMgr != null)
						{
							mVideoMgr.clearAll();
						}
						if(mRecordManager != null)
						{
							mVideoTimeLong = mTargetDuration;
							mRecordManager.setVideoTimeLong(mVideoTimeLong);
						}
						if(mCameraBottomControl != null)
						{
							mCameraBottomControl.resetUIToVideoDefMode();
							// 重置录制按钮的状态
							if(isValidVideoTimeLong()) {
								mLaseSecondPause = false;
								mResetRecordUIState = true;
								mCameraBottomControl.setShutterEnable(ShutterConfig.RecordStatus.CAN_RECORDED);
							}
						}
					}
					default:
					{
						dialog.dismiss();
					}
					mSystemUiVisibility = -1;
				}
			}
		});

		dialog.show();
	}


	@Override
	public void onUpdateFrontFlash(boolean isOn)
	{
		String manufacturer = Build.MANUFACTURER.toUpperCase(Locale.CHINA);
		String model = Build.MODEL.toUpperCase(Locale.CHINA);
		mShowSplashMask = false;
		if(isOn && manufacturer != null && "OPPO".equals(manufacturer) && model != null && model.startsWith("OPPO"))
		{
			mShowSplashMask = true;
		}
		mCurrentFlashMode = isOn ? CameraConfig.FlashMode.Torch : CameraConfig.FlashMode.Off;
		CameraHandler cameraHandler = RenderHelper.getCameraHandler();
		if(cameraHandler != null)
		{
			cameraHandler.setFlashMode(mCurrentFlashMode);
		}
	}

	@Override
	public void onUpdateFlash(String mode)
	{
		mCurrentFlashMode = mode;
		CameraHandler cameraHandler = RenderHelper.getCameraHandler();
		if(cameraHandler != null)
		{
			cameraHandler.setFlashMode(mCurrentFlashMode);
		}

		int tongJiID;
		switch(mode)
		{
			case CameraConfig.FlashMode.On:
			{
				tongJiID = R.string.拍照_拍照_更多设置_后置_闪光灯开;
				break;
			}

			case CameraConfig.FlashMode.Off:
			{
				tongJiID = R.string.拍照_拍照_更多设置_后置_闪光灯关;
				break;
			}

			case CameraConfig.FlashMode.Auto:
			{
				tongJiID = R.string.拍照_拍照_更多设置_后置_闪光灯自动;
				break;
			}

			default:
			{
				tongJiID = R.string.拍照_拍照_更多设置_后置_手电筒;
			}
		}

		MyBeautyStat.onClickByRes(tongJiID);
	}

	@Override
	public void onUpdateTimer(int time)
	{
		mCurrentTimerMode = time;
		int tongJiID;
		if(mCurrentTimerMode == 1)
		{
			showSettingToast(getResources().getString(R.string.camerapage_setting_capture_time_1s));
			tongJiID = R.string.拍照_拍照_更多设置_延时_1秒;
		}
		else if(mCurrentTimerMode == 2)
		{
			showSettingToast(getResources().getString(R.string.camerapage_setting_capture_time_2s));
			tongJiID = R.string.拍照_拍照_更多设置_延时_2秒;
		}
		else if(mCurrentTimerMode == 10)
		{
			showSettingToast(getResources().getString(R.string.camerapage_setting_capture_time_10s));
			tongJiID = R.string.拍照_拍照_更多设置_延时_10秒;
		}
		else
		{
			showSettingToast(getResources().getString(R.string.camerapage_setting_capture_time_off));
			tongJiID = R.string.拍照_拍照_更多设置_延时_手动;
		}

		MyBeautyStat.onClickByRes(tongJiID);
	}

	@Override
	public void onUpdateTouchMode(boolean isOn)
	{
		mTouchCapture = isOn;
		if(mTouchCapture)
		{
			showSettingToast(getResources().getString(R.string.camerapage_setting_touch_capture_on));
			MyBeautyStat.onClickByRes(R.string.拍照_拍照_更多设置_触屏开);
		}
		else
		{
			showSettingToast(getResources().getString(R.string.camerapage_setting_touch_capture_off));
			MyBeautyStat.onClickByRes(R.string.拍照_拍照_更多设置_触屏关);
		}
	}

	@Override
	public void onUpdateVoiceMode(boolean isOn)
	{
		mFaceGuideTakePicture = isOn;
//		if(CameraConfig.getInstance().getBoolean(CameraConfig.ConfigMap.NoSound))
//		{
//			mFaceGuideTakePicture = false;
//		}
		showOpenSoundTip(isOn, true);
		MyBeautyStat.onClickByRes(isOn ? R.string.拍照_拍照_更多设置_语言提示开 : R.string.拍照_拍照_更多设置_语音提示关);
	}

	@Override
	public void onUpdateRatio(float ratio)
	{
		notifyAllWidgetLockUI();

		if(ratio == CameraConfig.PreviewRatio.Ratio_1_1)
		{
			MyBeautyStat.onClickByRes(R.string.拍照_拍照_顶部设置_1_1照片比例);
		}
		else if(ratio == CameraConfig.PreviewRatio.Ratio_4_3)
		{
			MyBeautyStat.onClickByRes(R.string.拍照_拍照_顶部设置_4_3照片比例);
		}
		else if(ratio == CameraConfig.PreviewRatio.Ratio_16_9)
		{
			MyBeautyStat.onClickByRes(R.string.拍照_拍照_顶部设置_16_9照片比例);
		}
		else if(ratio == CameraConfig.PreviewRatio.Ratio_9_16)
		{
			MyBeautyStat.onClickByRes(R.string.拍照_拍照_顶部设置_9_16照片比例);
		}
		else
		{
			MyBeautyStat.onClickByRes(R.string.拍照_拍照_顶部设置_全屏照片比例);
		}

		StickerResMgr.getInstance().setPreviewRatio(ratio);
		mCameraBottomControl.setPreviewRatio(ratio);
		mCameraBottomControl.updateUI();

		mCameraTopControl.setCurrRatio(ratio);
		mCameraTopControl.updateUI();

		ratio = checkRatio(ratio);
		updateLightnessSeekBarLoc(ratio);
		float viewRatio = CameraConfig.PreviewRatio.Ratio_4_3;
		if(ratio >= CameraConfig.PreviewRatio.Ratio_16_9)
		{
			viewRatio = ratio;
		}
		resetViewAndCameraRatio(viewRatio, ratio);

		StickerDrawHelper.getInstance().setPreviewRatio(mCurrentRatio);
		StickerDrawHelper.getInstance().setFrameTopPadding(mFrameTopPadding);

		if(mPreviewView != null)
		{
			mPreviewView.queueEvent(new RenderRunnable()
			{
				@Override
				public void run(RenderThread renderThread)
				{
					renderThread.setFrameTopPadding(mFrameTopPadding);
					if(renderThread.getFilterManager() != null)
					{
						renderThread.getFilterManager().setRatioAndOrientation(1 / mCurrentRatio, mScreenOrientation, mFrameTopPadding);
					}
				}
			});
		}

		if(mCurrentTabType == ShutterConfig.TabType.GIF || mCurrentTabType == ShutterConfig.TabType.VIDEO)
		{
			changeVideoSize(ratio);
			if(mRecordManager == null || !mRecordManager.isSameSize(mVideoWidth, mVideoHeight))
			{
				initMyRecord();
			}
		}
		changeRatioByType(true);

		notifyAllWidgetUnlockUI(500);
	}

	private void updateLightnessSeekBarLoc(float ratio)
	{
		int top_height = RatioBgUtils.GetTopHeightByRatio(ratio);
		int bot_height = RatioBgUtils.GetBottomHeightByRation(ratio);
		mLightnessSeekBar.setTranslationY(top_height + (ShareData.m_screenRealHeight - top_height - bot_height - CameraPercentUtil.HeightPxToPercent(288)) / 2f);
	}

	/**
	 * 开启自拍语音
	 *
	 * @param isOn
	 * @param flag 是否显示提示
	 */
	private void showOpenSoundTip(boolean isOn, boolean flag)
	{
		if(isOn)
		{
			boolean show = false;
			if(CameraUtils.sHasSetVolume)
			{
				show = true;
			}
			CameraUtils.setSystemVolume(getContext(), false);

			if(CameraUtils.old_volume1 == 0 && show)
			{
				showSettingToast(getResources().getString(R.string.camerapage_adjust_volume_level));
			}
			else if(flag)
			{
				showSettingToast(getResources().getString(R.string.camerapage_zipai_volume_open));
			}
			int times = CameraConfig.getInstance().getInt(CameraConfig.ConfigMap.UseVoiceGuideTimes);
			CameraConfig.getInstance().saveData(CameraConfig.ConfigMap.UseVoiceGuideTimes, times + 1);

			AudioControlUtils.pauseOtherMusic(getContext());
			mCameraSound.soundPlayDelay = 5500;
			mCameraSound.playSound(0, 6);
		}
		else
		{
			mCameraSound.stopSound();
			CameraUtils.setSystemVolume(getContext(), CameraConfig.getInstance().getBoolean(CameraConfig.ConfigMap.NoSound));
			showSettingToast(getResources().getString(R.string.camerapage_zipai_volume_close));
		}
	}

	private void showSettingToast(String msg)
	{
		if(msg == null)
		{
			return;
		}
		if(mContentView != null)
		{
			mContentView.showSettingToast(msg);
		}
	}

	@Override
	public void onClickPackUpLineCB()
	{
		MyBeautyStat.onPageEndByRes(R.string.拍照_拍照_顶部设置);
		if(mCameraTopControl != null)
		{
			mCameraTopControl.SetBtnAlpha(1f);
			mCameraTopControl.SetButtonClickable(true);
		}

		if(mCameraPopSetting != null)
		{
			mCameraPopSetting.doPopTransYAnim(0, -mPopHeight);
		}
	}

	@Override
	public void onDismissSucceed(boolean is_top_layout)
	{
		if(is_top_layout)
		{
			MyBeautyStat.onPageEndByRes(R.string.拍照_拍照_顶部设置);

			if(mCameraTopControl != null)
			{
				mCameraTopControl.SetBtnAlpha(1f);
				mCameraTopControl.SetButtonClickable(true);
			}
		}
	}

	/**
	 * 打开镜头后回调
	 */
	@Override
	public void onCameraOpen()
	{
		final boolean finalIsSwitchCamera = mIsSwitchCamera;
		mUIHandler.post(new Runnable()
		{
			@Override
			public void run()
			{

				initWidgetStateAfterCameraOpen(finalIsSwitchCamera);

				initStickerData(mIsInitCamera);

				initShape(mIsInitCamera);

				initBeauty(mIsInitCamera);

				mIsInitCamera = false;
			}
		});

		if(mPreviewView != null)
		{
			final boolean sticker_enable = mBeautyGuideView == null || !mBeautyGuideView.isBeautyGuideAlive();
			mPreviewView.runOnGLThread(new RenderRunnable()
			{
				@Override
				public void run(RenderThread renderThread)
				{
					int renderMode = getRenderMode();

					renderThread.setRenderMode(renderMode);
					renderThread.setFrameTopPadding(mFrameTopPadding);
					renderThread.setWaterMarkHasDate(mWaterMarkHasDate);
					renderThread.setDetectFaceCallback(CameraPageV3.this);
					renderThread.setOnCaptureFrameListener(CameraPageV3.this);

					if(renderThread.getFilterManager() != null)
					{
						renderThread.getFilterManager().setFaceRectEnable(renderMode == 0);
						renderThread.getFilterManager().setStickerEnable(renderMode != 0 && sticker_enable);
						//renderThread.getFilterManager().changeColorFilter(mCurrentFilterRes);
						renderThread.getFilterManager().setRatioAndOrientation(1 / mCurrentRatio, mScreenOrientation, mFrameTopPadding);
					}
				}
			});
		}

		if(mIsSwitchCamera)
		{
			mIsSwitchCamera = false;
		}
		else
		{
			mDoCameraOpenAnim = true;
		}
	}

	@Override
	public void onCameraClose()
	{

	}

	@Override
	public void onScreenOrientationChanged(int orientation, int pictureDegree, float fromDegree, float toDegree)
	{
		if(mContentView != null)
		{
			mContentView.startRotationAnim((int)toDegree);
		}
		if(mCountDownView != null)
		{
			mCountDownView.startRotationAnim((int)toDegree);
		}
		picturePatchDegree = pictureDegree;
		mScreenOrientation = ((90 - pictureDegree) + 360) % 360 / 90;
		updateFilterRatioAndOrientation();
		StickerDrawHelper.getInstance().setScreenOrientation(mScreenOrientation);
	}

	private void updateFilterRatioAndOrientation()
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
						renderThread.getFilterManager().setRatioAndOrientation(1 / mCurrentRatio, mScreenOrientation, mFrameTopPadding);
					}
				}
			});
		}
	}

	@Override
	public void onAutoFocus(boolean success, Camera camera)
	{
		if(success && mCurrentTabType != ShutterConfig.TabType.PHOTO && mDoFocusWithFace == -1)
		{
			mDoFocusWithFace = 0;
			mUIHandler.sendMessage(mUIHandler.obtainMessage(MSG_OPEN_OR_CLOSE_AUTO_FOCUS, false));
		}
		mUIHandler.removeMessages(MSG_CLEAR_FOCUS_AND_METERING);
		mUIHandler.sendEmptyMessage(MSG_CLEAR_FOCUS_AND_METERING);
	}

	private void doFocusAndMetering(boolean showRect, float x1, float y1, float x2, float y2)
	{
		CameraHandler cameraHandler = RenderHelper.getCameraHandler();
		if(cameraHandler != null && cameraHandler.getCamera() != null && ((cameraHandler.getCamera().isFocusAreaSupported() && x1 > 0 && y1 > 0) || (cameraHandler.getCamera().isMeteringSupported() && x2 > 0 && y2 > 0)))
		{
			if(showRect && mRatioBgView != null)
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
				mRatioBgView.setFocusLocation(x, y);
			}
			cameraHandler.setFocusAndMeteringArea(x1, y1, x2, y2, RenderHelper.sCameraFocusRatio);
		}
	}

	@Override
	public void onError(int error, Camera camera)
	{
		if(mUIHandler != null)
		{
			mUIHandler.obtainMessage(MSG_CAMERA_ERROR, error, error).sendToTarget();
		}
	}

	@Override
	public void onPictureTaken(final byte[] previewData, Camera camera)
	{
		if(mUIHandler != null)
		{
			mUIHandler.removeMessages(MSG_CANCEL_TAKE_PICTURE);
			if(mShowSplashMask)
			{
				mUIHandler.obtainMessage(MSG_FLASH_SCREEN_ON_TAKE_PICTURE, 1, 1).sendToTarget();
			}
		}
		byte[] data = previewData;
		final int degree = getPictureDegree();

		if(getMachineMode().indexOf("kftt") != -1)
		{// 亚马逊的kindle fire
			// 要做垂直翻转
			data = kfttFixJpgOrientation(data);
		}

        /*if (CameraConfig.getInstance().getBoolean(CameraConfig.ConfigMap.DebugMode) && mCurrentPreviewSize != null) {
            String size = mCurrentPreviewSize.width + " X " + mCurrentPreviewSize.height;
            Toast.makeText(mContext, getContext().getString(R.string.camerapage_preview_size) + size, Toast.LENGTH_LONG).show();
        }*/
		if(isPatchMode)
		{
			doTakePicture = false;
			if(mUIHandler != null)
			{
				byte[] pic = rotateAndCropPicture(data, isFront, degree, mCurrentRatio, 0, 1024);
				mUIHandler.obtainMessage(MSG_PATCH_SAVE_PIC_END, pic).sendToTarget();
			}
			return;
		}
		final SaveImageThread saveImageThread = new SaveImageThread(data, degree);
		new Thread(saveImageThread, "SaveImageThread").start();
	}

	private int getPictureDegree()
	{
		int degree = picturePatchDegree;
		int patchDegree = getPicturePatchDegree();
//        Log.i(TAG, mCurrentCameraId+"-->degree[0]:"+item.degree[0]+", degree[1]:"+item.degree[1]+", degree[2]:"+item.degree[2]);
		if(isFront)
		{
			degree = (360 - degree) % 360;
			if(patchDegree != 0)
			{
				degree = (degree - patchDegree + 360) % 360;
			}
		}
		else
		{
			if(patchDegree != 0)
			{
				degree = (degree + patchDegree) % 360;
			}
		}
		return degree;
	}

	public String getMachineMode()
	{
		String res = Build.MODEL.replace(" ", "").replace("-", "") + ",";
		res += Build.ID + ",";
		res += Build.VERSION.RELEASE + ",";
		res += Build.VERSION.SDK;
		return res.toLowerCase();
	}

	public byte[] kfttFixJpgOrientation(byte[] data)
	{
		Bitmap b1 = BitmapFactory.decodeByteArray(data, 0, data.length);
		Bitmap b2 = Bitmap.createBitmap(b1.getWidth(), b1.getHeight(), Bitmap.Config.ARGB_8888);
		int xx, yy;
		for(yy = 0; yy < b1.getHeight(); yy++)
		{
			for(xx = 0; xx < b1.getWidth(); xx++)
			{
				int color = b1.getPixel(xx, yy);
				b2.setPixel(b1.getWidth() - xx - 1, yy, color);
			}
		}
		b1.recycle();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		b2.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		return baos.toByteArray();
	}

	/**
	 * 旋转、裁剪图片
	 *
	 * @param data
	 * @param hMirror 水平镜像
	 * @param degree
	 * @param ratio
	 * @param top
	 * @param maxSize 最大不能超过maxSize
	 * @return
	 */
	private byte[] rotateAndCropPicture(byte[] data, boolean hMirror, int degree, float ratio, int top, int maxSize)
	{
		float maxMem = 0.25f;
//        Log.i(TAG, "--rotateAndCropPicture--top:"+top);
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inJustDecodeBounds = true;
		opt.inPreferredConfig = Bitmap.Config.ARGB_8888;
		BitmapFactory.decodeByteArray(data, 0, data.length, opt);
		opt.inJustDecodeBounds = false;

		int bigOne = opt.outWidth > opt.outHeight ? opt.outWidth : opt.outHeight;

		float srcRatio = opt.outHeight * 1.0f / opt.outWidth;
		if(srcRatio < 1)
		{
			srcRatio = 1 / srcRatio;
		}
		if(ratio > srcRatio)
		{
			bigOne = opt.outWidth > opt.outHeight ? opt.outWidth : opt.outHeight;

			int sampleSize = bigOne / maxSize;
			if(sampleSize <= 0)
			{
				sampleSize = 1;
			}
			int cw = opt.outWidth / sampleSize;
			int ch = opt.outHeight / sampleSize;
			int memUse = cw * ch * 4;
			if(memUse > Runtime.getRuntime().maxMemory() * maxMem)
			{
				bigOne = opt.outWidth > opt.outHeight ? opt.outWidth : opt.outHeight;
			}
		}
		if(bigOne > maxSize)
		{
			opt.inSampleSize = bigOne / maxSize;
		}
		if(opt.inSampleSize < 1)
		{
			opt.inSampleSize = 1;
		}

		Bitmap bitmap = ImageUtils.DecodeJpg(data, opt.inSampleSize);
		if(bitmap == null || bitmap.isRecycled())
		{
			return null;
		}

		Matrix matrix = new Matrix();
		matrix.postRotate(degree);
		if(hMirror)
		{
			matrix.postScale(-1, 1);
		}
		bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		if(bitmap == null || bitmap.isRecycled())
		{
			return null;
		}

		//裁剪
		srcRatio = 1.0f;
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		float topScale = top * 1.0f / mCameraViewHeight;
		int x = 0, y = 0;

		if(height > width)
		{
			srcRatio = height * 1.0f / width;
			x = 0;
			y = (int)(height * topScale);
			height = (int)(width * ratio);

		}
		else if(height < width)
		{
			srcRatio = width * 1.0f / height;
			x = (int)(width * topScale);
			y = 0;
			width = (int)(height * ratio);
		}
		if(srcRatio == ratio)
		{
			return JpgEncode(bitmap, 100);
		}
		if(x > bitmap.getWidth())
		{
			x = 0;
		}
		if(y > bitmap.getHeight())
		{
			y = 0;
		}
		if(x + width > bitmap.getWidth())
		{
			width = bitmap.getWidth() - x;
		}
		if(y + height > bitmap.getHeight())
		{
			height = bitmap.getHeight() - y;
		}
		Bitmap target = Bitmap.createBitmap(bitmap, x, y, width, height);
		if(bitmap != null && !bitmap.isRecycled())
		{
			bitmap.recycle();
			bitmap = null;
		}
		if(target == null || target.isRecycled())
		{
			return null;
		}
		return JpgEncode(target, 100);
	}

	private class SaveImageThread implements Runnable
	{
		private byte[] mImgData;
		private int mDegree;

		public SaveImageThread(byte[] data, int degree)
		{
			mImgData = data;
			mDegree = degree;
		}

		@Override
		public void run()
		{
			if(mCurrentRatio != CameraConfig.PreviewRatio.Ratio_4_3)
			{   //mUpCutHeight
				mImgData = CameraUtils.rotateAndCropPicture2byteArr(mImgData, isFront, mDegree, mCurrentRatio, mFrameTopPadding * 1.0f / (ShareData.getScreenW() * 4.0f / 3), CameraConfig.getInstance().getInt(CameraConfig.ConfigMap.PhotoSize), 100);
				//mImgData = CameraUtils.cutBitmap(mImgData, mCurrentRatio, 0, false, CameraConfig.getInstance().getInt(CameraConfig.ConfigMap.PhotoSize));
			}

			ImageFile2 imgFile = new ImageFile2();
			try
			{
				if(mCurrentRatio != CameraConfig.PreviewRatio.Ratio_4_3)
				{
					imgFile.SetData(getContext(), mImgData, 0, 0, -1);
				}
				else
				{
					imgFile.SetData(getContext(), mImgData, mDegree, isFront ? MakeBmpV2.FLIP_H : MakeBmpV2.FLIP_NONE, -1);
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}

			if(mIsFilterBeautifyProcess)
			{
				processFilterBeautify(imgFile);
			}
			else
			{
				processOriginal(imgFile);
			}
			doTakePicture = false;
		}

		private void processOriginal(ImageFile2 imageFile)
		{
			mImageFile = imageFile;
			mSaveImageDataSucceed = true;
			if(!mShowSplashMask || mSplashMaskAnimEnd)
			{
				sendTakePicture(imageFile);
			}
		}

		private void processFilterBeautify(ImageFile2 imageFile)
		{
			if(mUIHandler != null)
			{
				mUIHandler.post(new Runnable()
				{
					@Override
					public void run()
					{
						showWaitAnimDialog(true);
					}
				});
			}

			if(imageFile != null)
			{
				try
				{
					boolean hasBeauty = FilterBeautifyProcessor.HasBeauty(mFilterBeautifyProcessMask);
					boolean hasShape = FilterBeautifyProcessor.HasShape(mFilterBeautifyProcessMask);
					boolean hasFilter = FilterBeautifyProcessor.HasFilter(mFilterBeautifyProcessMask);

					FilterBeautifyInfo info = new FilterBeautifyInfo();
					info.imgs = imageFile;
					info.m_filter = hasFilter;
					info.m_shape = hasShape;
					info.m_beauty = hasBeauty;
					info.m_blur = false;
					info.m_dark = mCurrentFilterRes != null && mCurrentFilterRes.m_isHasvignette;
					info.m_filter_uri = mCurrentFilterRes != null ? mCurrentFilterRes.m_id : 0;
					info.m_filter_alpha = mCurrentFilterRes != null ? mCurrentFilterRes.m_filterAlpha : 80;
					info.mFilterBeautyParams = mFilterBeautyParams;

					Bitmap out = FilterBeautifyProcessor.ProcessFilterBeautify(getContext(), info);

					if(out != null && !out.isRecycled())
					{
						imageFile.ClearAll();//清除原来数据

						mImgData = CameraUtils.bitmapToByteArray(out, true);
						mImageFile = new ImageFile2();
						mImageFile.SetData(getContext(), mImgData, 0, MakeBmpV2.FLIP_NONE, -1);//重新拆解byte[]封装(已矫正)
					}
				}
				catch(Throwable e)
				{
					e.printStackTrace();
				}

				mSaveImageDataSucceed = true;
				if(!mShowSplashMask || mSplashMaskAnimEnd)
				{
					sendTakePicture(mImageFile);
				}
			}
		}

		private void sendTakePicture(ImageFile2 imageFile)
		{
			if(mUIHandler != null)
			{
				mUIHandler.post(new Runnable()
				{
					@Override
					public void run()
					{
						showWaitAnimDialog(false);
					}
				});
			}

			if(imageFile != null)
			{
				int filterUri = saveFilterUri();
				if(filterUri == 0 || filterUri == FilterAdapter.OriginalItemInfo.ORIGINAL_ITEM_URI)
				{
					TongJi2.AddCountByRes(getContext(), R.integer.拍照_滤镜原图);
				}

				sendCameraSensorsData();

				final HashMap<String, Object> params = new HashMap<String, Object>();
				params.put(DataKey.COLOR_FILTER_ID, (mCurrentFilterRes != null ? mCurrentFilterRes.m_id : 0));
				params.put("img_file", imageFile);
				params.put("ratio", mCurrentRatio);
				params.put("orientation", mPicOrientation = (4 - mScreenOrientation) * 90 % 360);
				params.put("from_camera", true);
				params.put(DataKey.CAMERA_TAILOR_MADE_PARAMS, mFilterBeautyParams);
				params.put(CameraSetDataKey.KEY_CAMERA_FLASH_MODE, mCurrentFlashMode);
				params.put(CameraSetDataKey.KEY_CAMERA_FRONT_SPLASH_MASK, mShowSplashMask);


				params.put("cameraId", mCurrentCameraId);
                if(mUIHandler != null)
				{
					mUIHandler.post(new Runnable()
					{
						@Override
						public void run()
						{
							mResetBrightnessDisable = true;
							if(mSite != null)
							{
								mSite.onTakePicture(getContext(), params);
							}
						}
					});
				}
			}
		}
	}

	private void sendTakePictureOnAfterMask(ImageFile2 imageFile)
	{
		showWaitAnimDialog(false);

		if(imageFile != null)
		{
			int layout_mode = LayoutMode.LAYOUT_4_3;
			if(mCurrentRatio == CameraConfig.PreviewRatio.Ratio_16_9)
			{
				layout_mode = LayoutMode.LAYOUT_16_9;
			}
			else if(mCurrentRatio == CameraConfig.PreviewRatio.Ratio_1_1)
			{
				layout_mode = LayoutMode.LAYOUT_1_1;
			}

			int filterUri = saveFilterUri();
			if(filterUri == 0 || filterUri == FilterAdapter.OriginalItemInfo.ORIGINAL_ITEM_URI)
			{
				TongJi2.AddCountByRes(getContext(), R.integer.拍照_滤镜原图);
			}

			sendCameraSensorsData();

			final HashMap<String, Object> params = new HashMap<String, Object>();
			params.put(DataKey.COLOR_FILTER_ID, (mCurrentFilterRes != null ? mCurrentFilterRes.m_id : 0));
			params.put("img_file", imageFile);
			params.put("layout_mode", layout_mode);
			params.put("from_camera", true);
			params.put("ratio", mCurrentRatio);
			params.put(DataKey.CAMERA_TAILOR_MADE_PARAMS, mFilterBeautyParams);
			params.put(CameraSetDataKey.KEY_CAMERA_FLASH_MODE, mCurrentFlashMode);
			params.put(CameraSetDataKey.KEY_CAMERA_FRONT_SPLASH_MASK, mShowSplashMask);

            params.put("cameraId", mCurrentCameraId);
			mResetBrightnessDisable = true;
			if(mSite != null)
			{
				mSite.onTakePicture(getContext(), params);
			}
		}
	}

	@Override
	public void onPreviewFrame(byte[] data, Camera camera)
	{
		if(mDoCameraOpenAnim)
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
	public void onDetectResult(final ArrayList<PocoFace> faces, final int viewWidth, final int viewHeight)
	{
		if(mIsSwitchCamera || mPauseDetectOnTabChange)
		{
			FaceDataHelper.getInstance().setFaceData(null);
			return;
		}
		FaceDataHelper.getInstance().setFaceData(faces);

		if(mShowActionTip && mCurrentTabType != ShutterConfig.TabType.PHOTO && mUIHandler != null && faces != null)
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

	private void voiceGuide(RectF firstFace)
	{
		// 后置语音引导
		if(firstFace != null && firstFace.left != 0 && firstFace.top != 0)
		{
			AudioControlUtils.pauseOtherMusic(getContext());
			if(firstFace.right < mCameraViewWidth / 3.0f)
			{
				mCameraSound.playSound(0, 4);//向左
			}
			else if(firstFace.right > mCameraViewWidth / 3.0f * 2)
			{
				mCameraSound.playSound(0, 2);//向右
			}
			else if(firstFace.bottom < mCameraViewHeight / 3.0f)
			{
				mCameraSound.playSound(0, 3);//向上
			}
			else if(firstFace.bottom > mCameraViewHeight / 3.0f * 2)
			{
				mCameraSound.playSound(0, 5);//向下
			}
			else if(!mCameraSound.soundIsBusy)
			{
				takePictureAfterFaceDetectSuccess();
			}
		}
	}

	/**
	 * 自拍引导检测到人脸后拍照
	 */
	private void takePictureAfterFaceDetectSuccess()
	{
		if(mOnPause || doTakePicture)
		{
			return;
		}
		notifyAllWidgetLockUI();
		// 高级检查
		TongJi2.AddCountByRes(getContext(), R.integer.拍照_语音导航_自动拍);
		if(mCameraSound != null)
		{
			mCameraSound.soundPlayDelay = 10000;
			mCameraSound.playSound(0, 1);
		}
		if(mUIHandler != null)
		{
			mUIHandler.postDelayed(new Runnable()
			{
				@Override
				public void run()
				{
					CameraHandler cameraHandler = RenderHelper.getCameraHandler();
					if(cameraHandler != null)
					{
						if(!mIsFilterBeautifyProcess)
						{
							cameraHandler.takeOnePicture();
						}
						else
						{
							cameraHandler.takeOnePicture2();
						}
					}
				}
			}, 3200);
		}
	}

	private int[] calculatePhotoSize()
	{
		int width = ShareData.getScreenW();
		float scale = 1.0f;
		int level = 1;//1:super-high, 2:high, 3:normal
		if(mCurrentRatio == CameraConfig.PreviewRatio.Ratio_1_1)
		{
			if(width > 1080)
			{
				level = 2;
				scale = 0.88888f;//1440x1440 -> 1280x1280

				level = 3;
				scale = 0.75f;//1440x1440 -> 1080x1080

			}
			else if(width > 720)
			{
				level = 2;
				scale = 0.88888f;//1080x1080 -> 960x960

				level = 3;
				scale = 0.66666f;//1080x1080 -> 720x720

			}
			else if(width > 600)
			{
//                scale = 0.88888f;//720x720 -> 640x640
			}
		}
		else if(mCurrentRatio == CameraConfig.PreviewRatio.Ratio_4_3)
		{
			if(width > 1080)
			{
				level = 2;
				scale = 0.88888f;//1440x1920 -> 1280x1707(1706.64)
//                scale = 0.75f;//1440x1920 -> 1080x1440

				level = 3;
				scale = 0.75f;//1440x1920 -> 1080x1440

			}
			else if(width > 720)
			{
				level = 2;
				scale = 0.88888f;//1080x1440 -> 960x1280
//                scale = 0.66666f;//1080x1440 -> 720x960

				level = 3;
				scale = 0.66666f;//1080x1440 -> 720x960

			}
			else if(width > 600)
			{
//                scale = 0.66666f;//720x960 -> 480x640
			}
		}
		else if(mCurrentRatio == CameraConfig.PreviewRatio.Ratio_16_9)
		{
			if(width > 1080)
			{//(1080, +]    1440x2560 2k屏
				level = 2;
//                scale = 0.88888f;//s -> 1280x2276
				scale = 0.75f;//s -> 1080x1920

				level = 3;
				scale = 0.75f;//s -> 1080x1920

			}
			else if(width > 720)
			{//(720, 1080]
				level = 2;
//                scale = 0.88888f;//s -> 960x1707
				scale = 0.66666f;//s -> 720x1280

				level = 3;
				scale = 0.66666f;//s -> 720x1280

			}
			else if(width > 600)
			{//(600, 720]
//                scale = 0.75f;//720x1280 -> 540x960
			}
		}
		else
		{
			mCurrentRatio = mFullScreenRatio;
		}
		int pWidth = Math.round(width * scale);
		int pHeight = Math.round(pWidth * mCurrentRatio);

		return new int[]{pWidth, pHeight};
	}

	@Override
	public void onCaptureFrame(final int frameType, final IntBuffer data, final int width, final int height)
	{
		if(data == null || width == 0 || height == 0)
		{
			return;
		}
		if(frameType == 1)
		{
			mUIHandler.post(new Runnable()
			{
				@Override
				public void run()
				{
					System.gc();
					Bitmap temp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
					data.rewind();
					temp.copyPixelsFromBuffer(data);
					Bitmap bmp = null;
					if(temp != null && !temp.isRecycled())
					{
						int y = 0;
						int bmpHeight = height;
						if(mCurrentRatio == CameraConfig.PreviewRatio.Ratio_1_1 || mCurrentRatio == CameraConfig.PreviewRatio.Ratio_9_16)
						{
							y = height - Math.round(width * mCurrentRatio) - mFrameTopPadding;
							bmpHeight = Math.round(width * mCurrentRatio);
						}

						Matrix mt = new Matrix();
						if(mPicOrientation == 90)
						{
							mt.postScale(1, -1);
							mt.postRotate(mPicOrientation);

						}
						else if(mPicOrientation == 180)
						{
							mt.postScale(-1, 1);

						}
						else if(mPicOrientation == 270)
						{
							mt.postScale(1, -1);
							mt.postRotate(mPicOrientation);

						}
						else
						{
							mt.postScale(1, -1);
						}
						bmp = Bitmap.createBitmap(temp, 0, y, width, bmpHeight, mt, true);
						temp.recycle();
						temp = null;
					}
					if(bmp != null && !bmp.isRecycled())
					{
						if(mCameraBottomControl != null)
						{
							mCameraBottomControl.CloseOpenedPage();
						}
						if(mSite != null)
						{
							if(mSite.m_myParams != null)
							{
								mSite.m_myParams.put("lastPageId", SiteID.DYNAMIC_STICKER_VIDEO_PREVIEW);
							}

							mIsStickerPreviewBack = true;
							mResetBrightnessDisable = true;
							previewCutePhoto(bmp);

						}
					}
					mRecordState = RecordState.IDLE;
				}
			});
		}
		else if(frameType == 2)
		{

		}
		else
		{
			mUIHandler.post(new Runnable()
			{
				@Override
				public void run()
				{
					System.gc();
					Bitmap temp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
					temp.copyPixelsFromBuffer(data);

					Bitmap bmp = null;
					if(temp != null && !temp.isRecycled())
					{
						if(temp.getWidth() != mVideoWidth || temp.getHeight() != mVideoHeight)
						{
							bmp = Bitmap.createScaledBitmap(temp, mVideoWidth, mVideoHeight, true);
							temp.recycle();
							temp = bmp;
							bmp = null;
						}
					}
					if(temp != null && !temp.isRecycled())
					{
						Matrix mt = new Matrix();
						mt.postScale(1, -1);
						bmp = Bitmap.createBitmap(temp, 0, 0, temp.getWidth(), temp.getHeight(), mt, true);
						temp.recycle();
						temp = null;
					}
					mVideoBG = bmp;
				}
			});
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

			if(mUIObserverList != null && mUIObserverList.isLock())
			{
				if((doTakePicture && mCurrentTimerMode > 0))
				{
					//倒计时拍照可以取消
				}
				else
				{
					return true;
				}
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
				if (mBeautyGuideView != null && mBeautyGuideView.isBeautyGuideAlive()) {
					return true;
				}
				if(mCameraBottomControl != null && mCameraBottomControl.IsOpenOtherPage())
				{
					return mCameraBottomControl.CloseOpenedPage();
				}
			}
			return false;
		}

		@Override
		public boolean onDown(MotionEvent e)
		{
			if(interceptEvent(e.getY(), true))
			{
				return true;
			}
			mDirection = -1;
			return true;
		}

		@Override
		public boolean onSingleTapUp(MotionEvent e)
		{
			if(interceptEvent(e.getY(), false))
			{
				return true;
			}
			if(mTouchCapture && (mCurrentTabType == ShutterConfig.TabType.GIF || mCurrentTabType == ShutterConfig.TabType.VIDEO) && mRecordState == RecordState.STOP)
			{
				// 在 STOP -> IDLE 这段时间不处理
				return true;
			}

			if(mTouchCapture && (mBeautyGuideView == null || !mBeautyGuideView.isBeautyGuideAlive()))// 触屏拍照、视频
			{
				return mCameraBottomControl != null && mCameraBottomControl.handleTouchModeEvent();
			}
			else if(!doTakePicture || mCurrentTimerMode > 0 || mRecordState != RecordState.RECORDING)// 对焦、测光
			{
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
			}

			return true;
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY)
		{   //e1:onDown
			if(interceptEvent(e2.getY(), false))
			{
				return true;
			}
			boolean isRecording = (mCurrentTabType == ShutterConfig.TabType.GIF || mCurrentTabType == ShutterConfig.TabType.VIDEO) && mRecordState == RecordState.RECORDING;

			if(!isRecording && isCountDown())
			{
				onCancelCountDown();
			}

			handleScrollGesture(e1, e2, distanceY, isRecording);
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
