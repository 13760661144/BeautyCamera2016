package cn.poco.camera3;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import cn.poco.advanced.ImageUtils;
import cn.poco.camera.CameraConfig;
import cn.poco.camera3.config.CameraUIConfig;
import cn.poco.camera3.config.shutter.ShutterConfig;
import cn.poco.camera3.info.CameraRatioInfo;
import cn.poco.camera3.info.CameraSettingInfo;
import cn.poco.camera3.mgr.CameraRatioMgr;
import cn.poco.camera3.mgr.CameraSettingMgr;
import cn.poco.camera3.util.CameraPercentUtil;
import cn.poco.camera3.util.CameraTimer;
import cn.poco.dynamicSticker.newSticker.MyHolder;
import cn.poco.statistics.MyBeautyStat;
import cn.poco.tianutils.ShareData;
import cn.poco.widget.PressedButton;
import my.beautyCamera.R;

/**
 * @author Gxx
 *         Created by Gxx on 2017/8/21.
 */

public class CameraPopSetting extends FrameLayout implements View.OnClickListener
{

	// UI
	private FrameLayout mTopLayout;
	private SettingItem mFrontFlashLogo; // 前置闪光灯 logo
	private SettingItem mRearFlashLogo; // 后置闪光灯 logo
	private SettingItem mTimingLogo; // 定时 logo
	private SettingItem mVoiceLogo; // 语音 logo
	private SettingItem mTouchLogo; // 触屏拍照 logo
	private FrameLayout mPackUpLine; // 收起的线
	private View mGrayLine;
	private RecyclerView mSecLevelView;
	private RecyclerView.LayoutManager mLayoutMgr;

	private RecyclerView mRatioLayout;
	private CameraRatioAdapter mRatioAdapter;
	private RatioDecoration mRatioDecoration;
	private PhotoRatioDecoration mPhotoRatioDecoration;
	private CameraRatioAdapter.ItemClickListener mRatioItemListener;

	// 二级 icon 数据
	private GroupIconAdapter mGroupIconAdapter;
	private GroupIconAdapter.ItemClickListener mGroupItemListener;

	// Anim
	private final int mAnimDuration = 300;
	private boolean mDoingAnim = false;
	private AnimatorListenerAdapter mAnimListener;
	private ValueAnimator.AnimatorUpdateListener mGroupTransYAnimListener;

	// UI Status
	private boolean mIsFrontMode = true; // 前置镜头
	private boolean mIsClickPickUp = false; // 是否点击收起线
	private boolean mIsShowFrontFlash = false; // 前置闪光灯
	private boolean mUIEnable = true;
	private boolean mFrontFlashOn = false;

	private int mTabType;
	private int mIconW;
	private int mPopHeight; // 设置弹窗 一级界面高度
	private int mPopGroupHeight; // 设置弹窗 二级界面高度
	private int mTopIconSize; // icon 数量
	private boolean[] mTopIconStatusArr;

	private final int MSG_RESET = 10000;
	private final int MSG_DISMISS = 10001;

	private Handler mUIHandler;
	private UIListener mUIListener;
	private CameraTimer mTimer;
	private CameraTimer.TimerEventListener mTimerListener;

	public CameraPopSetting(@NonNull Context context)
	{
		super(context);
		setBackgroundColor(Color.WHITE);

		mIconW = CameraPercentUtil.WidthPxToPercent(90);
		mPopHeight = CameraPercentUtil.HeightPxToPercent(188);
		mPopGroupHeight = CameraPercentUtil.HeightPxToPercent(352);

		setClickable(true);
		setLongClickable(true);
		initCB();
		initView();
	}

	public void setUIListener(UIListener listener)
	{
		mUIListener = listener;
	}

	private void startTimer()
	{
		if(mTimer == null)
		{
			mTimer = new CameraTimer(2500, 2500, mTimerListener);
		}
		mTimer.start();
	}

	private void cancelTimer()
	{
		if(mTimer != null)
		{
			mTimer.cancel();
		}
	}

	private void initCB()
	{
		mTimerListener = new CameraTimer.TimerEventListener()
		{
			@Override
			public void onFinish()
			{
				// onFinish 在线程里回调
				if(mUIHandler != null)
				{
					mUIHandler.sendEmptyMessage(MSG_DISMISS);
				}
			}
		};

		mRatioItemListener = new CameraRatioAdapter.ItemClickListener()
		{
			@Override
			public void onItemClick(float ratio)
			{
				cancelTimer();
				if(mUIListener != null)
				{
					mUIListener.onUpdateRatio(ratio);
				}
				dismiss();
			}
		};

		mGroupItemListener = new GroupIconAdapter.ItemClickListener()
		{
			@Override
			public void onItemClick(CameraSettingInfo info)
			{
				cancelTimer();
				if(mUIEnable)
				{
					switch(CameraSettingMgr.getGroupMode())
					{
						case CameraSettingMgr.GROUP_MODE_REAR_FLASH: // 后置
						{
							if(mRearFlashLogo != null && info != null)
							{
								mRearFlashLogo.setLogo(info.getLogo(), info.getLogo(), ImageUtils.GetSkinColor());
								mRearFlashLogo.setText(info.getText());

								String mode = (String)info.getTag();
								CameraConfig.getInstance().saveData(CameraConfig.ConfigMap.FlashModeStr, mode);
								if(mUIListener != null)
								{
									mUIListener.onUpdateFlash(mode);
								}
							}
							break;
						}

						case CameraSettingMgr.GROUP_MODE_TIMING: // 定时
						{
							if(mTimingLogo != null && info != null)
							{
								mTimingLogo.setLogo(info.getLogo(), info.getLogo(), ImageUtils.GetSkinColor());
								mTimingLogo.setText(info.getText());

								int mode = (Integer)info.getTag();
								CameraSettingMgr.setCurrentTimerMode(mode);
								if(mUIListener != null)
								{
									mUIListener.onUpdateTimer(mode);
								}
							}
							break;
						}
					}

				}
				startTimer();
			}
		};

		// 二级界面 动画监听
		mGroupTransYAnimListener = new ValueAnimator.AnimatorUpdateListener()
		{
			@Override
			public void onAnimationUpdate(ValueAnimator animation)
			{
				int value = (int)animation.getAnimatedValue();
				LayoutParams params = (LayoutParams)getLayoutParams();
				params.height = value;
				setLayoutParams(params);
			}
		};

		mAnimListener = new AnimatorListenerAdapter()
		{
			@Override
			public void onAnimationCancel(Animator animation)
			{
				mDoingAnim = false;
			}

			@Override
			public void onAnimationEnd(Animator animation)
			{
				if(!mIsClickPickUp)
				{
					startTimer();
				}
				mIsClickPickUp = false;
				mDoingAnim = false;
			}
		};

		mUIHandler = new Handler(Looper.getMainLooper())
		{
			@Override
			public void handleMessage(Message msg)
			{
				switch(msg.what)
				{
					case MSG_RESET:
					{
						resetUIStatus();
						break;
					}

					case MSG_DISMISS:
					{
						setUIEnable(false);
						dismiss();
					}
				}
			}
		};
	}

	public void clearAll()
	{
		CameraSettingMgr.clearData();
		CameraRatioMgr.clearAll();

		if(mTimer != null)
		{
			cancelTimer();
			mTimer.clearAll();
			mTimer = null;
		}

		if(mRearFlashLogo != null)
		{
			mRearFlashLogo.setOnClickListener(null);
			mRearFlashLogo.clearAll();
			mRearFlashLogo = null;
		}

		if(mTimingLogo != null)
		{
			mTimingLogo.setOnClickListener(null);
			mTimingLogo.clearAll();
			mTimingLogo = null;
		}

		if(mTouchLogo != null)
		{
			mTouchLogo.setOnClickListener(null);
			mTouchLogo.clearAll();
			mTouchLogo = null;
		}

		if(mPackUpLine != null)
		{
			mPackUpLine.setOnClickListener(null);
			mPackUpLine = null;
		}

		if(mGroupIconAdapter != null)
		{
			mGroupIconAdapter.clearAll();
		}

		if(mSecLevelView != null)
		{
			int size = mLayoutMgr.getChildCount();
			for(int i = 0; i < size; i++)
			{
				View view = mLayoutMgr.getChildAt(i);
				if(view != null)
				{
					view.setOnClickListener(null);
				}
			}
		}

		mAnimListener = null;
		mUIListener = null;
		mGroupTransYAnimListener = null;
		removeAllViews();
	}

	private void initView()
	{
		// 一级界面
		mTopLayout = new FrameLayout(getContext());
		if(Build.VERSION.SDK_INT >= 16)
		{
			mTopLayout.setBackground(null);
		}
		else
		{
			mTopLayout.setBackgroundDrawable(null);
		}
		FrameLayout.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, CameraPercentUtil.HeightPxToPercent(188));
		this.addView(mTopLayout, params);
		{
			mFrontFlashLogo = new SettingItem(getContext());// 前置闪光灯
			mFrontFlashLogo.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
			mFrontFlashLogo.setTextColor(ImageUtils.GetSkinColor());
			mFrontFlashLogo.setText(getContext().getString(R.string.camera_setting_front_flash_text));
			mFrontFlashLogo.setLogo(R.drawable.camera_flash_close, R.drawable.camera_flash_close, ImageUtils.GetSkinColor());
			mFrontFlashLogo.setOnClickListener(this);
			mFrontFlashLogo.setVisibility(GONE);
			params = new LayoutParams(mIconW, ViewGroup.LayoutParams.WRAP_CONTENT);
			params.topMargin = CameraPercentUtil.HeightPxToPercent(40);
			mTopLayout.addView(mFrontFlashLogo, params);

			mRearFlashLogo = new SettingItem(getContext());// 后置闪光灯
			mRearFlashLogo.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
			mRearFlashLogo.setTextColor(ImageUtils.GetSkinColor());
			mRearFlashLogo.setOnClickListener(this);
			mRearFlashLogo.setVisibility(GONE);
			params = new LayoutParams(mIconW, ViewGroup.LayoutParams.WRAP_CONTENT);
			params.topMargin = CameraPercentUtil.HeightPxToPercent(40);
			mTopLayout.addView(mRearFlashLogo, params);

			mTimingLogo = new SettingItem(getContext());// 定时
			mTimingLogo.setOnClickListener(this);
			mTimingLogo.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
			mTimingLogo.setTextColor(ImageUtils.GetSkinColor());
			params = new LayoutParams(mIconW, ViewGroup.LayoutParams.WRAP_CONTENT);
			params.topMargin = CameraPercentUtil.HeightPxToPercent(40);
			mTopLayout.addView(mTimingLogo, params);

			mVoiceLogo = new SettingItem(getContext());// 语音提示
			mVoiceLogo.setTextSingleLine(false);
			mVoiceLogo.setOnClickListener(this);
			mVoiceLogo.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
			mVoiceLogo.setTextColor(ImageUtils.GetSkinColor());
			params = new LayoutParams(mIconW, ViewGroup.LayoutParams.WRAP_CONTENT);
			params.topMargin = CameraPercentUtil.HeightPxToPercent(40);
			mTopLayout.addView(mVoiceLogo, params);

			mTouchLogo = new SettingItem(getContext());// 触屏拍照
			mTouchLogo.setOnClickListener(this);
			mTouchLogo.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
			mTouchLogo.setTextColor(ImageUtils.GetSkinColor());
			params = new LayoutParams(mIconW, ViewGroup.LayoutParams.WRAP_CONTENT);
			params.topMargin = CameraPercentUtil.HeightPxToPercent(40);
			mTopLayout.addView(mTouchLogo, params);
		}

		mRatioLayout = new RecyclerView(getContext());
		mRatioLayout.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
		params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, CameraPercentUtil.HeightPxToPercent(188));
		addView(mRatioLayout, params);

		mPackUpLine = new FrameLayout(getContext());
		mPackUpLine.setOnClickListener(this);
		params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, CameraPercentUtil.HeightPxToPercent(30));
		params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
		this.addView(mPackUpLine, params);
		{
			PressedButton line = new PressedButton(getContext());
			line.setButtonImage(R.drawable.camera_setting_more, R.drawable.camera_setting_more, ImageUtils.GetSkinColor());
			params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
			params.bottomMargin = CameraPercentUtil.HeightPxToPercent(14);
			mPackUpLine.addView(line, params);
		}

		// 二级界面
		mGrayLine = new View(getContext());
		mGrayLine.setBackgroundColor(0x14000000);
		mGrayLine.setVisibility(GONE);
		params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, CameraPercentUtil.HeightPxToPercent(1));
		params.topMargin = CameraPercentUtil.HeightPxToPercent(176);
		params.leftMargin = CameraPercentUtil.WidthPxToPercent(28);
		params.rightMargin = CameraPercentUtil.WidthPxToPercent(28);
		this.addView(mGrayLine, params);

		mSecLevelView = new RecyclerView(getContext());
		mSecLevelView.setVisibility(GONE);
		mSecLevelView.setOverScrollMode(OVER_SCROLL_NEVER);
		mSecLevelView.setHorizontalScrollBarEnabled(false);
		mSecLevelView.setVerticalScrollBarEnabled(false);
		mLayoutMgr = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
		mSecLevelView.setLayoutManager(mLayoutMgr);
		mSecLevelView.addItemDecoration(new GroupIconDecoration());
		mSecLevelView.addOnScrollListener(new RecyclerView.OnScrollListener()
		{
			@Override
			public void onScrollStateChanged(RecyclerView recyclerView, int newState)
			{
				if(newState == RecyclerView.SCROLL_STATE_IDLE)
				{
					startTimer();
				}
				else
				{
					cancelTimer();
				}
			}
		});
		params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, CameraPercentUtil.HeightPxToPercent(175));
		params.topMargin = CameraPercentUtil.HeightPxToPercent(177);
		this.addView(mSecLevelView, params);
	}

	public void setRotation(int degree)
	{
		if(mFrontFlashLogo != null)
		{
			mFrontFlashLogo.setRotation(degree);
		}

		if(mRearFlashLogo != null)
		{
			mRearFlashLogo.setRotation(degree);
		}

		if(mTimingLogo != null)
		{
			mTimingLogo.setRotation(degree);
		}

		if(mVoiceLogo != null)
		{
			mVoiceLogo.setRotation(degree);
		}

		if(mTouchLogo != null)
		{
			mTouchLogo.setRotation(degree);
		}

		if(mGroupIconAdapter != null)
		{
			mGroupIconAdapter.setRotation(degree);
		}

		if(mRatioAdapter != null)
		{
			mRatioAdapter.setRotation(degree);
		}
	}

	private void initData(CameraUIConfig config)
	{
		CameraSettingMgr.InitData(getContext());

		if(config != null)
		{
			updateRatioArr(config.GetPreviewRatio());
			CameraSettingMgr.setCurrentTimerMode(config.getTimerMode());
		}

		mGroupIconAdapter = new GroupIconAdapter();
		mGroupIconAdapter.setOnItemClickListener(mGroupItemListener);
		mSecLevelView.setAdapter(mGroupIconAdapter);

		// 初始化 一级界面 logo
		setTouchInitStatus();
		setFlashInitStatus();
		setTimingInitStatus();
		setVoiceInitStatus();
	}

	public void updateRatioArr(float ratio)
	{
		if(mRatioAdapter == null)
		{
			mRatioAdapter = new CameraRatioAdapter();
			mRatioAdapter.setOnItemClickListener(mRatioItemListener);
		}

		if(mPhotoRatioDecoration == null)
		{
			mPhotoRatioDecoration = new PhotoRatioDecoration();
		}

		if(mRatioDecoration == null)
		{
			mRatioDecoration = new RatioDecoration();
		}

		ArrayList<CameraRatioInfo> mRatioArr = CameraRatioMgr.getResArr(mTabType);

		CameraRatioMgr.updateSelectedStatusByRatio(ratio);

		mRatioLayout.removeItemDecoration(mPhotoRatioDecoration);
		mRatioLayout.removeItemDecoration(mRatioDecoration);

		mRatioLayout.addItemDecoration(mTabType == ShutterConfig.TabType.PHOTO ? mPhotoRatioDecoration : mRatioDecoration);
		mRatioAdapter.setData(mRatioArr);
		mRatioLayout.setAdapter(mRatioAdapter);
	}

	public void resetDataStatus()
	{
		setTouchStatus();
		setTimingStatus();
		setVoiceStatus();
		setFlashStatus();
		setFrontFlashStatus();
	}

	private void setTouchStatus()
	{
		if(mTouchLogo != null)
		{
			boolean isOn = CameraConfig.getInstance().getBoolean(CameraConfig.ConfigMap.TouchCapture);
			if(isOn)
			{
				mTouchLogo.setLogo(R.drawable.camera_back_setting_touch_capture_on, R.drawable.camera_back_setting_touch_capture_on, ImageUtils.GetSkinColor());
			}
			else
			{
				mTouchLogo.setLogo(R.drawable.camera_back_setting_touch_capture_off, R.drawable.camera_back_setting_touch_capture_off, ImageUtils.GetSkinColor());
			}
		}
	}

	private void setTimingStatus()
	{
		int index = CameraSettingInfo.TimingIndex.OFF;
		int currentTimerMode = CameraSettingMgr.getCurrentTimerMode();
		switch(currentTimerMode)
		{
			case CameraConfig.CaptureMode.Timer_1s:
			{
				index = CameraSettingInfo.TimingIndex.ONE_SEC;
				break;
			}

			case CameraConfig.CaptureMode.Timer_2s:
			{
				index = CameraSettingInfo.TimingIndex.TWO_SEC;
				break;
			}

			case CameraConfig.CaptureMode.Timer_10s:
			{
				index = CameraSettingInfo.TimingIndex.TEN_SEC;
				break;
			}
		}

		CameraSettingInfo info = CameraSettingMgr.getSelectedInfo(CameraSettingMgr.GROUP_MODE_TIMING);
		if(info != null)
		{
			info.setIsSelected(false);
		}

		info = CameraSettingMgr.GetTimingInfoByIndex(index);
		CameraSettingMgr.updateSelectedInfo(CameraSettingMgr.GROUP_MODE_TIMING, index);
		if(info != null)
		{
			info.setIsSelected(true);
			mTimingLogo.setLogo(info.getLogo(), info.getLogo(), ImageUtils.GetSkinColor());
			mTimingLogo.setText(info.getText());
		}
	}

	private void setVoiceStatus()
	{
		if(mVoiceLogo != null)
		{
			boolean isOn = CameraConfig.getInstance().getBoolean(CameraConfig.ConfigMap.FaceGuideTakePicture);
			if(isOn)
			{
				mVoiceLogo.setLogo(R.drawable.camera_back_setting_voice_on, R.drawable.camera_back_setting_voice_on, ImageUtils.GetSkinColor());
			}
			else
			{
				mVoiceLogo.setLogo(R.drawable.camera_back_setting_voice_off, R.drawable.camera_back_setting_voice_off, ImageUtils.GetSkinColor());
			}
		}
	}

	private void setFlashStatus()
	{
		if(mRearFlashLogo != null)
		{
			String flash_mode = CameraConfig.getInstance().getString(CameraConfig.ConfigMap.FlashModeStr);
			int index = CameraSettingInfo.FlashIndex.OFF;
			switch(flash_mode)
			{
				case CameraConfig.FlashMode.Torch:
				{
					index = CameraSettingInfo.FlashIndex.TORCH;
					break;
				}

				case CameraConfig.FlashMode.Auto:
				{
					index = CameraSettingInfo.FlashIndex.AUTO;
					break;
				}

				case CameraConfig.FlashMode.On:
				{
					index = CameraSettingInfo.FlashIndex.ON;
					break;
				}
			}

			CameraSettingInfo info = CameraSettingMgr.getSelectedInfo(CameraSettingMgr.GROUP_MODE_REAR_FLASH);
			if(info != null)
			{
				info.setIsSelected(false);
			}
			info = CameraSettingMgr.GetFlashInfoByIndex(index);
			CameraSettingMgr.updateSelectedInfo(CameraSettingMgr.GROUP_MODE_REAR_FLASH, index);
			if(info != null)
			{
				info.setIsSelected(true);
				mRearFlashLogo.setLogo(info.getLogo(), info.getLogo(), ImageUtils.GetSkinColor());
				mRearFlashLogo.setText(info.getText());
			}
		}
	}

	private void setFrontFlashStatus()
	{
		if(mFrontFlashLogo != null)
		{
			String front_flash_mode = CameraConfig.getInstance().getString(CameraConfig.ConfigMap.FrontFlashModeStr);
			switch(front_flash_mode)
			{
				case CameraConfig.FlashMode.Off:
				{
					mFrontFlashLogo.setLogo(R.drawable.camera_flash_close, R.drawable.camera_flash_close, ImageUtils.GetSkinColor());
					mFrontFlashOn = false;
					break;
				}

				case CameraConfig.FlashMode.Torch:
				{
					mFrontFlashLogo.setLogo(R.drawable.camera_flash_open, R.drawable.camera_flash_open, ImageUtils.GetSkinColor());
					mFrontFlashOn = true;
					break;
				}
			}
		}
	}

	public void setUiConfig(CameraUIConfig config)
	{
		if(config != null)
		{
			mTabType = config.GetSelectedType();
			mIsFrontMode = config.isCameraFrontMode();
		}
		initData(config);
		resetTopIconMargin();
	}

	public void setTabType(@ShutterConfig.TabType int type)
	{
		mTabType = type;
		resetTopIconMargin();
	}

	/**
	 * 设置镜头模式
	 *
	 * @param isFrontMode 前置 or 后置
	 */
	public void setCameraMode(boolean isFrontMode)
	{
		mIsFrontMode = isFrontMode;
		resetTopIconMargin();
	}

	private void resetTopIconMargin()
	{
		updateIconSize();
		setIconMargin(getShowIconSize());
	}

	private void setIconMargin(int showIconSize)
	{
		int leftMargin = 0;
		int midMargin = 0;

		switch(showIconSize)
		{
			case 1:
			{
				leftMargin = CameraPercentUtil.WidthPxToPercent(315);
				break;
			}
			case 2:
			{
				leftMargin = CameraPercentUtil.WidthPxToPercent(150);
				midMargin = CameraPercentUtil.WidthPxToPercent(240);
				break;
			}

			case 3:
			{
				leftMargin = CameraPercentUtil.WidthPxToPercent(92);
				midMargin = CameraPercentUtil.WidthPxToPercent(133);
				break;
			}

			case 4:
			{
				leftMargin = CameraPercentUtil.WidthPxToPercent(72);
				midMargin = CameraPercentUtil.WidthPxToPercent(72);
			}
		}

		int iconSize = mTopIconSize;
		int recordSize = 0;
		for(int i = 0; i < iconSize; i++)
		{
			boolean showBtn = mTopIconStatusArr[i];

			if(showBtn && mTopLayout != null)
			{
				View view = mTopLayout.getChildAt(i);

				if(view == null) continue;

				view.setTranslationX(leftMargin + (mIconW + midMargin) * recordSize);
				recordSize += 1;
			}
		}
	}

	private void updateIconSize()
	{
		if(mTopLayout != null)
		{
			mTopIconSize = mTopLayout.getChildCount();
		}
		if(mTopIconStatusArr == null)
		{
			mTopIconStatusArr = new boolean[mTopIconSize];
		}
	}

	private int getShowIconSize()
	{
		int out = 2;
		boolean isShow;

		if(mTimingLogo != null)
		{
			mTimingLogo.setVisibility(VISIBLE);
		}

		if(mFrontFlashLogo != null)
		{
			isShow = mIsShowFrontFlash && mIsFrontMode && (mTabType == ShutterConfig.TabType.PHOTO);
			mFrontFlashLogo.setVisibility(isShow ? VISIBLE : GONE);
			out += isShow ? 1 : 0;
		}

		isShow = !mIsFrontMode && mTabType == ShutterConfig.TabType.PHOTO;

		out += isShow ? 2 : 0;

		if(mRearFlashLogo != null)
		{
			mRearFlashLogo.setVisibility(isShow ? VISIBLE : GONE);
		}
		if(mVoiceLogo != null)
		{
			mVoiceLogo.setVisibility(isShow ? VISIBLE : GONE);
		}

		updateIconStatusArr();
		return out;
	}

	private void updateIconStatusArr()
	{
		mTopIconStatusArr[0] = mIsShowFrontFlash && mIsFrontMode && (mTabType == ShutterConfig.TabType.PHOTO);// 前置闪光灯
		mTopIconStatusArr[1] = !mIsFrontMode && mTabType == ShutterConfig.TabType.PHOTO; // 后置闪光灯
		mTopIconStatusArr[2] = true;
		mTopIconStatusArr[3] = !mIsFrontMode && mTabType == ShutterConfig.TabType.PHOTO; // 语音
		mTopIconStatusArr[4] = true;
	}

	private void setVoiceInitStatus()
	{
		if(mVoiceLogo != null)
		{
			boolean isOn = CameraConfig.getInstance().getBoolean(CameraConfig.ConfigMap.FaceGuideTakePicture);
			if(isOn)
			{
				mVoiceLogo.setLogo(R.drawable.camera_back_setting_voice_on, R.drawable.camera_back_setting_voice_on, ImageUtils.GetSkinColor());
			}
			else
			{
				mVoiceLogo.setLogo(R.drawable.camera_back_setting_voice_off, R.drawable.camera_back_setting_voice_off, ImageUtils.GetSkinColor());
			}
			mVoiceLogo.setText(getContext().getString(R.string.camerapage_camera_voice_tips));
		}
	}

	private void setTouchInitStatus()
	{
		if(mTouchLogo != null)
		{
			boolean isOn = CameraConfig.getInstance().getBoolean(CameraConfig.ConfigMap.TouchCapture);
			if(isOn)
			{
				mTouchLogo.setLogo(R.drawable.camera_back_setting_touch_capture_on, R.drawable.camera_back_setting_touch_capture_on, ImageUtils.GetSkinColor());
			}
			else
			{
				mTouchLogo.setLogo(R.drawable.camera_back_setting_touch_capture_off, R.drawable.camera_back_setting_touch_capture_off, ImageUtils.GetSkinColor());
			}
			mTouchLogo.setText(getContext().getString(R.string.camerapage_camera_touch_take));
		}
	}

	private void setFlashInitStatus()
	{
		CameraSettingInfo info = CameraSettingMgr.getSelectedInfo(CameraSettingMgr.GROUP_MODE_REAR_FLASH);
		if(info != null)
		{
			mRearFlashLogo.setLogo(info.getLogo(), info.getLogo(), ImageUtils.GetSkinColor());
			mRearFlashLogo.setText(info.getText());
		}
	}

	private void setTimingInitStatus()
	{
		CameraSettingInfo info = CameraSettingMgr.getSelectedInfo(CameraSettingMgr.GROUP_MODE_TIMING);
		if(info != null)
		{
			mTimingLogo.setLogo(info.getLogo(), info.getLogo(), ImageUtils.GetSkinColor());
			mTimingLogo.setText(info.getText());
		}
	}

	public boolean isDoingAnim()
	{
		return mDoingAnim;
	}

	public boolean isAlive()
	{
		return this.getVisibility() == View.VISIBLE && this.getAlpha() == 1 && this.getTranslationY() == 0;
	}

	/**
	 * 重置二级界面属性
	 */
	private void resetGroupStatus()
	{
		CameraSettingMgr.updateGroupMode(CameraSettingMgr.GROUP_MODE_IDLE);
	}

	public boolean isShowingFlashGroup()
	{
		return CameraSettingMgr.getGroupMode() == CameraSettingMgr.GROUP_MODE_REAR_FLASH;
	}

	public boolean isShowingTimingGroup()
	{
		return CameraSettingMgr.getGroupMode() == CameraSettingMgr.GROUP_MODE_TIMING;
	}

	public boolean isShowingGroup()
	{
		return CameraSettingMgr.getGroupMode() == CameraSettingMgr.GROUP_MODE_REAR_FLASH || CameraSettingMgr.getGroupMode() == CameraSettingMgr.GROUP_MODE_TIMING;
	}

	public void setShowGroup(boolean show)
	{
		if(mGrayLine != null)
		{
			mGrayLine.setVisibility(show ? VISIBLE : GONE);
		}

		if(mPackUpLine != null)
		{
			mPackUpLine.setVisibility(show ? GONE : VISIBLE);
		}

		if(mSecLevelView != null)
		{
			mSecLevelView.setVisibility(show ? VISIBLE : GONE);
		}
	}

	public void showRatioLayout(boolean show)
	{
		mTopLayout.setVisibility(show ? GONE : VISIBLE);
		mRatioLayout.setVisibility(show ? VISIBLE : GONE);
	}

	/**
	 * 改变 view 高度，做二级界面展开、收起动画
	 */
	public void doGroupTransYAnim(int startHeight, int endHeight)
	{
		mDoingAnim = true;
		ValueAnimator anim = ValueAnimator.ofInt(startHeight, endHeight);
		anim.setDuration(mAnimDuration);
		anim.addUpdateListener(mGroupTransYAnimListener);
		anim.addListener(mAnimListener);
		anim.start();
	}

	/**
	 * 一级界面的平移动画
	 */
	public void doPopTransYAnim(int startY, int endY)
	{
		mDoingAnim = true;
		ObjectAnimator anim = ObjectAnimator.ofFloat(this, "translationY", startY, endY);
		anim.setDuration(mAnimDuration);
		anim.addListener(mAnimListener);
		anim.start();
	}

	public void dismiss()
	{
		mDoingAnim = true;
		cancelTimer();
		int dy = isShowingGroup() ? -mPopGroupHeight : -mPopHeight;
		ObjectAnimator anim = ObjectAnimator.ofFloat(this, "translationY", 0, dy);
		anim.setDuration(mAnimDuration);
		anim.addListener(new AnimatorListenerAdapter()
		{
			@Override
			public void onAnimationEnd(Animator animation)
			{
				if(mUIHandler != null)
				{
					mUIHandler.sendEmptyMessageDelayed(MSG_RESET, 10);
				}

				if(mUIListener != null)
				{
					mUIListener.onDismissSucceed(mTopLayout != null && mTopLayout.getVisibility() == VISIBLE);
				}
			}
		});
		anim.start();
	}

	public void dismissWithoutAnim()
	{
		setAlpha(0);

		cancelTimer();

		int dy = isShowingGroup() ? -mPopGroupHeight : -mPopHeight;
		this.setTranslationY(dy);

		if(mUIHandler != null)
		{
			mUIHandler.sendEmptyMessageDelayed(MSG_RESET, 10);
		}

		if(mUIListener != null)
		{
			mUIListener.onDismissSucceed(mTopLayout != null && mTopLayout.getVisibility() == VISIBLE);
		}
	}

	private void resetUIStatus()
	{
		resetGroupStatus();
		setShowGroup(false);
		setAlpha(1);
		LayoutParams params = (LayoutParams)getLayoutParams();
		if(params != null && params.height != mPopHeight)
		{
			params.height = mPopHeight;
			setLayoutParams(params);
		}
		mDoingAnim = false;
		setUIEnable(true);
	}

	private void setUIEnable(boolean enable)
	{
		mUIEnable = enable;
	}

	@Override
	public void onClick(View v)
	{
		if(!mUIEnable || mDoingAnim) return;

		cancelTimer();
		if(v == mFrontFlashLogo)
		{
			mFrontFlashOn = !mFrontFlashOn;

			int res = mFrontFlashOn ? R.drawable.camera_flash_open : R.drawable.camera_flash_close;
			mFrontFlashLogo.setLogo(res, res, ImageUtils.GetSkinColor());

			CameraConfig.getInstance().saveData(CameraConfig.ConfigMap.FrontFlashModeStr, mFrontFlashOn ? CameraConfig.FlashMode.Torch : CameraConfig.FlashMode.Off);
			if(mUIListener != null)
			{
				mUIListener.onUpdateFrontFlash(mFrontFlashOn);
			}

			startTimer();
		}
		else if(v == mRearFlashLogo)
		{
			MyBeautyStat.onClickByRes(R.string.拍照_拍照_更多设置_后置_灯光按钮);
			mGroupIconAdapter.setData(CameraSettingMgr.GetRearFlashGroupData());
			mGroupIconAdapter.notifyDataSetChanged();
			checkGroupStatus(CameraSettingMgr.GROUP_MODE_REAR_FLASH);
		}
		else if(v == mTimingLogo)
		{
			MyBeautyStat.onClickByRes(R.string.拍照_拍照_更多设置_延时按钮);
			mGroupIconAdapter.setData(CameraSettingMgr.GetTimingGroupData());
			mGroupIconAdapter.notifyDataSetChanged();
			checkGroupStatus(CameraSettingMgr.GROUP_MODE_TIMING);
		}
		else if(v == mVoiceLogo) // 语音
		{
			boolean isOn = CameraConfig.getInstance().getBoolean(CameraConfig.ConfigMap.FaceGuideTakePicture);

			if(isShowingGroup()) // 有二级
			{
				setShowGroup(false);
				doGroupTransYAnim(mPopGroupHeight, mPopHeight);
			}

			if(!isOn)
			{
				mVoiceLogo.setLogo(R.drawable.camera_back_setting_voice_on, R.drawable.camera_back_setting_voice_on, ImageUtils.GetSkinColor());
			}
			else
			{
				mVoiceLogo.setLogo(R.drawable.camera_back_setting_voice_off, R.drawable.camera_back_setting_voice_off, ImageUtils.GetSkinColor());
			}
			CameraConfig.getInstance().saveData(CameraConfig.ConfigMap.FaceGuideTakePicture, !isOn);

			if(mUIListener != null)
			{
				mUIListener.onUpdateVoiceMode(!isOn);
			}

			if(isShowingGroup())
			{
				resetGroupStatus();
			}

			startTimer();
		}
		else if(v == mTouchLogo)
		{
			boolean isOn = CameraConfig.getInstance().getBoolean(CameraConfig.ConfigMap.TouchCapture);

			if(isShowingGroup()) // 有二级
			{
				setShowGroup(false);
				doGroupTransYAnim(mPopGroupHeight, mPopHeight);
			}

			if(isOn)
			{
				mTouchLogo.setLogo(R.drawable.camera_back_setting_touch_capture_off, R.drawable.camera_back_setting_touch_capture_off, ImageUtils.GetSkinColor());
			}
			else
			{
				mTouchLogo.setLogo(R.drawable.camera_back_setting_touch_capture_on, R.drawable.camera_back_setting_touch_capture_on, ImageUtils.GetSkinColor());
			}
			CameraConfig.getInstance().saveData(CameraConfig.ConfigMap.TouchCapture, !isOn);

			if(mUIListener != null)
			{
				mUIListener.onUpdateTouchMode(!isOn);
			}

			if(isShowingGroup())
			{
				resetGroupStatus();
			}

			startTimer();
		}
		else if(v == mPackUpLine)
		{
			mIsClickPickUp = true;
			if(isShowingGroup())
			{
				resetGroupStatus();
			}
			if(mUIListener != null)
			{
				mUIListener.onClickPackUpLineCB();
			}
		}
	}

	private void checkGroupStatus(int be_click_group_mode)
	{
		if(!isShowingGroup()) // 没有二级
		{
			setShowGroup(true);
			CameraSettingMgr.updateGroupMode(be_click_group_mode);
			doGroupTransYAnim(mPopHeight, mPopGroupHeight);
		}
		else // 有二级
		{
			if(be_click_group_mode == CameraSettingMgr.GROUP_MODE_REAR_FLASH ? isShowingFlashGroup() : isShowingTimingGroup())
			{
				setShowGroup(false);
				resetGroupStatus();
				doGroupTransYAnim(mPopGroupHeight, mPopHeight);
			}
			else
			{
				CameraSettingMgr.updateGroupMode(be_click_group_mode);
				startTimer();
			}
		}
	}

	public void setShowFrontFlash(boolean show)
	{
		mIsShowFrontFlash = show;
		resetTopIconMargin();
	}

	public static class CameraRatioAdapter extends RecyclerView.Adapter implements OnClickListener
	{
		private ArrayList<CameraRatioInfo> mData;
		private int mDegree;

		public void setData(ArrayList<CameraRatioInfo> data)
		{
			mData = data;
			notifyDataSetChanged();
		}

		public void setRotation(int degree)
		{
			mDegree = degree;
			notifyDataSetChanged();
		}

		@Override
		public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
		{
			SettingItem itemView = new SettingItem(parent.getContext());
			itemView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
			itemView.setOnClickListener(this);
			RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			itemView.setLayoutParams(params);
			return new MyHolder(itemView);
		}

		@Override
		public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
		{
			if(holder != null && holder instanceof MyHolder)
			{
				CameraRatioInfo info = mData.get(position);
				SettingItem itemView = ((MyHolder)holder).getItemView();
				if(itemView != null)
				{
					itemView.setRotation(mDegree);
					itemView.setTag(info.getValue());
					itemView.setLogo(info.getThumb(), info.getThumb(), info.isSelected() ? ImageUtils.GetSkinColor() : 0xff808080);
					itemView.setTextColor(info.isSelected() ? ImageUtils.GetSkinColor() : 0xff808080);
					itemView.setText(info.getText());
				}
			}
		}

		@Override
		public int getItemCount()
		{
			return mData == null ? 0 : mData.size();
		}

		@Override
		public void onClick(View v)
		{
			float value = (float)v.getTag();
			boolean succeed = CameraRatioMgr.updateSelectedStatusByRatio(value);

			if(succeed && mItemClickListener != null)
			{
				notifyDataSetChanged();
				mItemClickListener.onItemClick(value);
			}
		}

		private ItemClickListener mItemClickListener;

		public void setOnItemClickListener(ItemClickListener listener)
		{
			mItemClickListener = listener;
		}

		private interface ItemClickListener
		{
			void onItemClick(float ratio);
		}
	}

	private static class PhotoRatioDecoration extends RecyclerView.ItemDecoration
	{
		@Override
		public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state)
		{
			// 必须要用adapter get size
			// layoutManager \ parent get 到的 item count 是动态的
			int position = parent.getChildAdapterPosition(view);

			// 没有全屏比例的间距
			outRect.top = CameraPercentUtil.HeightPxToPercent(40);
			outRect.left = CameraPercentUtil.WidthPxToPercent(250);
			if(position == 0)
			{
				outRect.left = CameraPercentUtil.WidthPxToPercent(160);
			}
		}
	}

	private static class RatioDecoration extends RecyclerView.ItemDecoration
	{
		private boolean mHasFullScreenRatio;
		private boolean mLessThan16To9;

		public RatioDecoration()
		{
			mHasFullScreenRatio = ShareData.m_screenRealHeight * 1.0f / ShareData.m_screenRealWidth > CameraConfig.PreviewRatio.Ratio_16_9;
			mLessThan16To9 = ShareData.m_screenRealHeight * 1.0f / ShareData.m_screenRealWidth < CameraConfig.PreviewRatio.Ratio_16_9;
		}

		@Override
		public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state)
		{
			// 必须要用adapter get size
			// layoutManager \ parent get 到的 item count 是动态的
			int position = parent.getChildAdapterPosition(view);

			// 没有全屏比例的间距
			outRect.top = CameraPercentUtil.HeightPxToPercent(40);
			outRect.left = CameraPercentUtil.WidthPxToPercent(mLessThan16To9 ? 152 : mHasFullScreenRatio ? 67 : 113);
			if(position == 0)
			{
				outRect.left = CameraPercentUtil.WidthPxToPercent(mLessThan16To9 ? 102 : mHasFullScreenRatio ? 50 : 50);
			}
		}
	}

	private static class GroupIconDecoration extends RecyclerView.ItemDecoration
	{
		@Override
		public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state)
		{
			// 必须要用adapter get size
			// layoutManager \ parent get 到的 item count 是动态的
			int size = parent.getAdapter().getItemCount();
			int position = parent.getChildAdapterPosition(view);

			outRect.top = CameraPercentUtil.HeightPxToPercent(42);
			outRect.left = CameraPercentUtil.WidthPxToPercent(113);
			if(position == 0)
			{
				outRect.left = CameraPercentUtil.WidthPxToPercent(50);
			}
			else if(position == size - 1)
			{
				outRect.left = CameraPercentUtil.WidthPxToPercent(108);
			}
		}
	}

	private static class SettingItem extends RelativeLayout
	{
		private TextView mTextView;
		private PressedButton mLogo;

		public SettingItem(@NonNull Context context)
		{
			super(context);
			initUI();
		}

		private void initUI()
		{
			mLogo = new PressedButton(getContext());
			mLogo.setId(R.id.camera_pop_setting_logo);
			RelativeLayout.LayoutParams params = new LayoutParams(CameraPercentUtil.WidthPxToPercent(70), CameraPercentUtil.HeightPxToPercent(70));
			params.addRule(CENTER_HORIZONTAL);
			this.addView(mLogo, params);

			mTextView = new TextView(getContext());
			mTextView.setId(R.id.camera_pop_setting_text);
			mTextView.setSingleLine();
			mTextView.setEllipsize(TextUtils.TruncateAt.END);
			mTextView.setGravity(Gravity.CENTER);
			params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			params.addRule(BELOW, R.id.camera_pop_setting_logo);
			params.addRule(CENTER_HORIZONTAL);
			params.topMargin = CameraPercentUtil.HeightPxToPercent(12);
			this.addView(mTextView, params);
		}

		public void setLogo(int onRes, int offRes, int color)
		{
			mLogo.setButtonImage(onRes, offRes, color);
		}

		public void setTextSingleLine(boolean is)
		{
			if(mTextView != null)
			{
				mTextView.setSingleLine(is);
			}
		}

		public void setText(String text)
		{
			mTextView.setText(text);
		}

		public void setTextSize(int unit, float size)
		{
			mTextView.setTextSize(unit, size);
		}

		public void setTextColor(int color)
		{
			mTextView.setTextColor(color);
		}

		public void clearAll()
		{
			if(mLogo != null)
			{
				mLogo = null;
			}

			if(mTextView != null)
			{
				mTextView = null;
			}

			removeAllViews();
		}
	}

	private static class GroupIconAdapter extends RecyclerView.Adapter implements OnClickListener
	{
		private ArrayList<CameraSettingInfo> mData;
		private int mDegree;

		public void setData(ArrayList<CameraSettingInfo> data)
		{
			mData = data;
		}

		private CameraSettingInfo getDataByIndex(int index)
		{
			if(mData != null && mData.size() > 0 && index >= 0 && index < mData.size())
			{
				return mData.get(index);
			}
			return null;
		}

		public void clearAll()
		{
			if(mData != null)
			{
				mData.clear();
				mData = null;
			}

			mItemClickListener = null;
		}

		public void setRotation(int degree)
		{
			mDegree = degree;
			notifyDataSetChanged();
		}

		@Override
		public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
		{
			SettingItem itemView = new SettingItem(parent.getContext());
			itemView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
			RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			params.topMargin = CameraPercentUtil.HeightPxToPercent(-12);
			itemView.setLayoutParams(params);
			return new MyHolder(itemView);
		}

		@Override
		public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
		{
			View view = holder.itemView;
			view.setTag(position);

			if(view instanceof SettingItem)
			{
				SettingItem itemView = (SettingItem)view;
				itemView.setRotation(mDegree);
				CameraSettingInfo info = getInfo(position);

				if(info != null)
				{
					int color = info.isSelected() ? ImageUtils.GetSkinColor() : 0xff808080;

					itemView.setLogo(info.getLogo(), info.getLogo(), color);

					itemView.setText(info.getText());

					itemView.setTextColor(color);
				}

				itemView.setOnClickListener(this);
			}
		}

		private CameraSettingInfo getInfo(int index)
		{
			return mData != null ? mData.get(index) : null;
		}

		private ItemClickListener mItemClickListener;

		public void setOnItemClickListener(ItemClickListener listener)
		{
			mItemClickListener = listener;
		}

		@Override
		public int getItemCount()
		{
			return mData != null ? mData.size() : 0;
		}

		@Override
		public void onClick(View v)
		{
			int index = (int)v.getTag();

			CameraSettingInfo info = getDataByIndex(index);
			if(info != null)
			{
				if(info.isSelected()) return;

				info.setIsSelected(true);
			}

			int current_group_mode = CameraSettingMgr.getGroupMode();
			CameraSettingInfo last_info = CameraSettingMgr.getSelectedInfo(current_group_mode);
			if(last_info != null)
			{
				last_info.setIsSelected(false);
			}

			notifyItemChanged(index);
			notifyItemChanged(CameraSettingMgr.getSelectedIndex(current_group_mode));
			CameraSettingMgr.updateSelectedInfo(current_group_mode, index);

			if(mItemClickListener != null)
			{
				mItemClickListener.onItemClick(info);
			}
		}

		private interface ItemClickListener
		{
			void onItemClick(CameraSettingInfo info); // tag 和 index 一般会相同
		}
	}

	interface UIListener
	{
		void onUpdateFrontFlash(boolean isOn);//前置闪光灯

		void onUpdateFlash(String mode); // 闪光灯

		void onUpdateTimer(int time); // 定时

		void onUpdateTouchMode(boolean isOn); // 触屏拍照

		void onUpdateVoiceMode(boolean isOn); // 语音提示

		void onUpdateRatio(float ratio); // 比例

		void onClickPackUpLineCB(); // 收起线

		void onDismissSucceed(boolean is_top_layout);
	}
}
