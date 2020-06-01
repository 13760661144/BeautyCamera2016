package cn.poco.camera3;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import java.io.File;

import cn.poco.advanced.ImageUtils;
import cn.poco.album.PhotoStore;
import cn.poco.camera.CameraConfig;
import cn.poco.camera3.cb.CameraPageListener;
import cn.poco.camera3.cb.ControlUIListener;
import cn.poco.camera3.cb.UIObservable;
import cn.poco.camera3.cb.UIObserver;
import cn.poco.camera3.config.CameraBtnConfig;
import cn.poco.camera3.config.CameraUIConfig;
import cn.poco.camera3.config.shutter.ShutterConfig;
import cn.poco.camera3.ui.drawable.StickerAnimDrawable;
import cn.poco.camera3.ui.tab.PointView;
import cn.poco.camera3.util.CameraPercentUtil;
import cn.poco.camera3.util.LanguageUtil;
import cn.poco.camera3.util.RatioBgUtils;
import cn.poco.statistics.MyBeautyStat;
import cn.poco.system.TagMgr;
import cn.poco.system.Tags;
import cn.poco.tianutils.ShareData;
import cn.poco.widget.PressedButton;
import my.beautyCamera.R;

/**
 * @author Gxx
 *         Created by Gxx on 2017/8/24.
 */

public class CameraControlBtnLayout extends FrameLayout implements View.OnClickListener
{
	private PressedButton mFilterBtn; // 滤镜
	private PressedButton mBackBtn; // 返回
	private PressedButton mStickerBtn; // 贴纸素材
	private PointView mPointView;
	private PressedButton mMixBtn; // 照片btn
	private PressedButton mDurationBtn; // video 时间 btn
	protected PressedButton mBeautyBtn; // 美形定制
	private FrameLayout mDelLayout;
	private PressedButton mVideoDelBtn; // 删除录像

	private StickerAnimDrawable mStickerAnimDrawable;

	private int mTabType = -1;
	private int mShutterMode = -1;
	private float mCurrentRatio;

	// ui 部件间通讯
	private Handler mUIHandler;
	private UIObservable mUIObserverList;
	private CameraPageListener mCameraPageListener;
	private ControlUIListener mControlUIListener;
	private boolean mIsBtnClickable = true;

	private int mBtnStatus = 0;
	private float mBtnTouchAlpha = 0.5f;

	private boolean mIsShowBackBtn;
	private boolean mIsShowColorFilterBtn;
	private boolean mIsShowStickerBtn;
	private boolean mIsShowBeautyBtn;
	private boolean mIsShowPhotoBtn;
	private boolean mIsShowDurationBtn;
	private boolean mIsShowPointSel;
	private boolean mIsSelectedVideo;
	private boolean mIsChinese;
	private boolean mUIEnable = true;

	private int mDurationBtnDefTransY;
	private float mFullScreenRatio;

	public CameraControlBtnLayout(@NonNull Context context)
	{
		super(context);
		if(Build.VERSION.SDK_INT >= 16)
		{
			setBackground(null);
		}
		else
		{
			setBackgroundDrawable(null);
		}
		mIsChinese = LanguageUtil.checkSystemLanguageIsChinese(context);
		mDurationBtnDefTransY = CameraPercentUtil.WidthPxToPercent(296);
		mFullScreenRatio = ShareData.m_screenRealHeight * 1.0f / ShareData.m_screenRealWidth;
		initHandler();
		initView(context);
	}

	public void clearAll()
	{
		if(mFilterBtn != null)
		{
			mFilterBtn.setOnClickListener(null);
		}

		if(mBackBtn != null)
		{
			mBackBtn.setOnClickListener(null);
		}

		if(mStickerBtn != null)
		{
			mStickerBtn.setOnClickListener(null);
		}

		if(mMixBtn != null)
		{
			mMixBtn.setOnClickListener(null);
		}

		if(mUIHandler != null)
		{
			mUIHandler.removeMessages(UIObserver.MSG_UNLOCK_UI);
		}

		if(mUIObserverList != null)
		{
			mUIObserverList = null;
		}

		if(mStickerAnimDrawable != null)
		{
			mStickerAnimDrawable.ClearAll();
			mStickerAnimDrawable = null;
		}

		mCameraPageListener = null;
		mControlUIListener = null;
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
					case UIObserver.MSG_UNLOCK_UI:
					{
						if(mUIObserverList != null)
						{
							mUIObserverList.notifyObservers(UIObserver.MSG_UNLOCK_UI);
						}
					}
				}
			}
		};
	}

	private void initView(Context context)
	{
		// 返回
		mBackBtn = new PressedButton(context);
		mBackBtn.setOnClickListener(this);
		mBackBtn.setVisibility(GONE);
		FrameLayout.LayoutParams params = new LayoutParams(CameraPercentUtil.WidthPxToPercent(70), CameraPercentUtil.HeightPxToPercent(70));
		params.leftMargin = CameraPercentUtil.HeightPxToPercent(39);
		params.bottomMargin = CameraPercentUtil.HeightPxToPercent(153);
		params.gravity = Gravity.BOTTOM;
		addView(mBackBtn, params);

		// 滤镜
		mFilterBtn = new PressedButton(context);
		mFilterBtn.setOnClickListener(this);
		mFilterBtn.setVisibility(GONE);
		params = new LayoutParams(CameraPercentUtil.WidthPxToPercent(70), CameraPercentUtil.HeightPxToPercent(70));
		params.gravity = Gravity.BOTTOM;
		params.leftMargin = CameraPercentUtil.WidthPxToPercent(156);
		params.bottomMargin = CameraPercentUtil.HeightPxToPercent(153);
		addView(mFilterBtn, params);

        // 美形定制
        mBeautyBtn = new PressedButton(context);
        mBeautyBtn.setOnClickListener(this);
        mBeautyBtn.setVisibility(GONE);
        params = new LayoutParams(CameraPercentUtil.WidthPxToPercent(70), CameraPercentUtil.HeightPxToPercent(70));
        params.gravity = Gravity.BOTTOM | Gravity.END;
		params.rightMargin = CameraPercentUtil.WidthPxToPercent(156);
		params.bottomMargin = CameraPercentUtil.HeightPxToPercent(155);
        addView(mBeautyBtn, params);

		// 素材
		mStickerBtn = new PressedButton(context);
		mStickerBtn.setOnClickListener(this);
		mStickerBtn.setVisibility(GONE);
		params = new LayoutParams(CameraPercentUtil.WidthPxToPercent(70), CameraPercentUtil.HeightPxToPercent(70));
		params.gravity = Gravity.BOTTOM | Gravity.END;
		params.rightMargin = CameraPercentUtil.WidthPxToPercent(46);
		params.bottomMargin = CameraPercentUtil.HeightPxToPercent(153);
		addView(mStickerBtn, params);

		// 相册、跳转预览
		mMixBtn = new PressedButton(context);
		mMixBtn.setOnClickListener(this);
		mMixBtn.setVisibility(GONE);
		params = new LayoutParams(CameraPercentUtil.WidthPxToPercent(70), CameraPercentUtil.HeightPxToPercent(70));
		params.gravity = Gravity.BOTTOM | Gravity.END;
		params.rightMargin = CameraPercentUtil.WidthPxToPercent(46);
		params.bottomMargin = CameraPercentUtil.HeightPxToPercent(153);
		addView(mMixBtn, params);

		// 底部圆点
		mPointView = new PointView(context);
		mPointView.setVisibility(GONE);
		mPointView.ShowPoint(true, false);
		params = new LayoutParams(CameraPercentUtil.WidthPxToPercent(42), CameraPercentUtil.HeightPxToPercent(42));
		params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
		addView(mPointView, params);

		mDelLayout = new FrameLayout(getContext())
		{
			@Override
			public boolean onInterceptTouchEvent(MotionEvent ev)
			{
				return true;
			}

			@Override
			public boolean onTouchEvent(MotionEvent event)
			{
				if(mVideoDelBtn != null)
				{
					mVideoDelBtn.onTouchEvent(event);
				}
				super.onTouchEvent(event);
				return true;
			}
		};
		mDelLayout.setOnClickListener(this);
		mDelLayout.setBackgroundDrawable(null);
		params = new LayoutParams(CameraPercentUtil.WidthPxToPercent(140), CameraPercentUtil.HeightPxToPercent(100));
		params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
		addView(mDelLayout, params);
		{
			// 删视频
			mVideoDelBtn = new PressedButton(getContext());
			mVideoDelBtn.setVisibility(GONE);
			params = new LayoutParams(CameraPercentUtil.WidthPxToPercent(54), CameraPercentUtil.HeightPxToPercent(54));
			params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
			params.bottomMargin = CameraPercentUtil.HeightPxToPercent(30);
			mDelLayout.addView(mVideoDelBtn, params);
		}

		// 视频时长
		mDurationBtn = new PressedButton(context);
		mDurationBtn.setOnClickListener(this);
		mDurationBtn.setVisibility(GONE);
		params = new LayoutParams(CameraPercentUtil.WidthPxToPercent(70), CameraPercentUtil.HeightPxToPercent(70));
		params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
		params.bottomMargin = mDurationBtnDefTransY;
		addView(mDurationBtn, params);
	}

	// =================================== lock、unlock ui part ======================================== //

	public void setBtnClickable(boolean clickable)
	{
		mIsBtnClickable = clickable;
	}

	public void setUIObserver(@NonNull UIObservable UIObserver)
	{
		mUIObserverList = UIObserver;
	}

	private void unlockUI(long delay)
	{
		if(mUIHandler != null)
		{
			if(delay == 0)
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
		if(mUIObserverList != null)
		{
			mUIObserverList.notifyObservers(UIObserver.MSG_LOCK_UI);
		}
	}

	public void setBtnRotation(int degree)
	{
		if(mFilterBtn != null)
		{
			mFilterBtn.setRotate(degree);
		}

		if(mStickerBtn != null)
		{
			mStickerBtn.setRotate(degree);
		}

		if(mMixBtn != null)
		{
			mMixBtn.setRotate(degree);
		}

		if(mStickerAnimDrawable != null)
		{
			mStickerAnimDrawable.setRotation(degree);
		}

		if(mDurationBtn != null)
		{
			mDurationBtn.setRotate(degree);
		}

        if (mBeautyBtn != null)
        {
            mBeautyBtn.setRotate(degree);
        }
	}

	public void setUIEnable(boolean enable)
	{
		mUIEnable = enable;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev)
	{
		return !mUIEnable || super.onInterceptTouchEvent(ev);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		if(!mUIEnable)
		{
			return super.onTouchEvent(event);
		}

		if(mIsSelectedVideo && mControlUIListener != null)
		{
			mControlUIListener.onCancelSelectedVideo();
			return true;
		}
		return super.onTouchEvent(event);
	}

	@Override
	public void onClick(View v)
	{
		if(!mIsBtnClickable) return;

		if(mCameraPageListener != null && mCameraPageListener.isCountDown())
		{
			mCameraPageListener.onCancelCountDown();
		}

		lockUI();

		if(mControlUIListener != null)
		{
			if(v == mStickerBtn)// 素材
			{
				switch(mTabType)
				{
					case ShutterConfig.TabType.GIF:
						MyBeautyStat.onClickByRes(R.string.拍照_表情包拍摄_主页面_素材按钮);
						break;
					case ShutterConfig.TabType.CUTE:
						MyBeautyStat.onClickByRes(R.string.拍照_萌妆照拍摄页_主页面_素材按钮);
						break;
					case ShutterConfig.TabType.VIDEO:
						MyBeautyStat.onClickByRes(R.string.拍照_视频拍摄页_主页面_素材按钮);
				}
				mControlUIListener.onClickStickerBtn();
			}
			else if(v == mMixBtn)
			{
				checkMixBtnInType(mTabType, mShutterMode);
			}
            else if (v == mBeautyBtn)
            {
            	if (TagMgr.CheckTag(getContext(), Tags.CAMERA_TAILOR_MADE_NEW_FLAG))
				{
					TagMgr.SetTag(getContext(), Tags.CAMERA_TAILOR_MADE_NEW_FLAG);
					TagMgr.Save(getContext());
					UpdateBeautyIcon();
				}
				MyBeautyStat.onClickByRes(R.string.拍照_拍照_底部设置_美形定制);
                mControlUIListener.onClickBeautyBtn();
                unlockUI(300);
            }
			else if(v == mDurationBtn)
			{
				int duration = changeDuration(mCurrentRatio);

				// 记录新的时长
				TagMgr.SetTagValue(getContext(), Tags.CAMERA_RECORD_DURATION, String.valueOf(duration));
				TagMgr.Save(getContext());

				if(mCameraPageListener != null)
				{
					mCameraPageListener.onClickVideoDurationBtn(duration);
				}
				unlockUI(300);
			}
			else if(v == mFilterBtn)// 滤镜
			{
				switch(mTabType)
				{
					case ShutterConfig.TabType.GIF:
						MyBeautyStat.onClickByRes(R.string.拍照_表情包拍摄_主页面_滤镜按钮);
						break;
					case ShutterConfig.TabType.CUTE:
						MyBeautyStat.onClickByRes(R.string.拍照_萌妆照拍摄页_主页面_滤镜按钮);
						break;
					case ShutterConfig.TabType.VIDEO:
						MyBeautyStat.onClickByRes(R.string.拍照_视频拍摄页_主页面_滤镜按钮);
						break;
					case ShutterConfig.TabType.PHOTO:
						MyBeautyStat.onClickByRes(R.string.拍照_美颜拍照_主页面_滤镜按钮);
				}
				mControlUIListener.onClickColorFilterBtn();
			}
			else if(v == mDelLayout)
			{
				checkDelBtnStatus();
				unlockUI(200);
			}
		}

		if(mCameraPageListener != null)
		{
			if(v == mBackBtn)// 返回
			{
				if(mTabType == ShutterConfig.TabType.VIDEO && mShutterMode == ShutterConfig.ShutterType.PAUSE_RECORD)
				{
					mCameraPageListener.onClearAllVideo();
					unlockUI(300);
				}
				else
				{
					mCameraPageListener.onBackBtnClick();
				}
			}
		}
	}

	private void checkDelBtnStatus()
	{
		if(mControlUIListener != null)
		{
			mControlUIListener.onClickVideoDelBtn();
		}
	}

	// ================================== change type、mode、ratio, update ui flow start ==================================== //

	public void setPreviewRatio(float ratio)
	{
		mCurrentRatio = ratio;
		if(mStickerAnimDrawable != null)
		{
			mStickerAnimDrawable.setCurrentPreviewRatio(ratio);
		}
	}

	public void setTabType(@ShutterConfig.TabType int type)
	{
		mTabType = type;
	}

	public void setShutterMode(@ShutterConfig.ShutterType int mode)
	{
		mShutterMode = mode;
	}

	public void updateUI()
	{
		updateUI(null);
	}

	public void updateUI(CameraUIConfig config)
	{
		float ratio = mCurrentRatio;
		int tabType = mTabType;
		int shutterMode = mShutterMode;

		initParams(config);
		updateBtnVisibility(tabType, shutterMode);
		updateBtnLogo(tabType, shutterMode, ratio);
	}

	private void initParams(CameraUIConfig config)
	{
		if(config == null) return;
		mIsShowBackBtn = config.isShowBackBtn();
		mIsShowBeautyBtn = config.isShowBeautyBtn();
		mIsShowColorFilterBtn = config.isShowFilterBtn();
		mIsShowStickerBtn = config.isShowStickerBtn();
		mIsShowDurationBtn = config.isShowVideoDuration();
		mIsShowPhotoBtn = config.isShowPhoto();
		mIsShowPointSel = config.GetTabSize() > 1;

		if(mIsShowStickerBtn)
		{
			mStickerAnimDrawable = new StickerAnimDrawable(getContext());
			mStickerAnimDrawable.setCurrentPreviewRatio(config.GetPreviewRatio());
			boolean isShowWhite = config.GetPreviewRatio() == CameraConfig.PreviewRatio.Ratio_16_9 || config.GetPreviewRatio() == CameraConfig.PreviewRatio.Ratio_Full;
			mStickerAnimDrawable.setBmpColor(isShowWhite ? Color.WHITE : ImageUtils.GetSkinColor());
			mStickerBtn.setBackgroundDrawable(mStickerAnimDrawable);
		}
	}

	private void updateBtnVisibility(int tabType, int shutterMode)
	{
		resetBtnType();

		if(mIsShowBackBtn)
		{
			setBtnType(CameraBtnConfig.ControlType.CLOSE_PAGE);
		}

		if(mIsShowColorFilterBtn)
		{
			setBtnType(CameraBtnConfig.ControlType.COLOR_FILTER);
		}

		if(mIsShowStickerBtn)
		{
			setBtnType(CameraBtnConfig.ControlType.STICKERS);
		}

		if(mIsShowBeautyBtn && (shutterMode != ShutterConfig.ShutterType.PAUSE_RECORD && shutterMode != ShutterConfig.ShutterType.RECORDING))
		{
			setBtnType(CameraBtnConfig.ControlType.BEAUTY);
		}

		if(mIsShowPointSel)
		{
			setBtnType(CameraBtnConfig.ControlType.TAB_SEL_POINT);
		}

		switch(tabType)
		{
			case ShutterConfig.TabType.PHOTO:
			{
				if(mIsShowPhotoBtn)
				{
					setBtnType(CameraBtnConfig.ControlType.PHOTO_ALBUM);
				}

				clearType(CameraBtnConfig.ControlType.STICKERS);
				break;
			}
			case ShutterConfig.TabType.VIDEO:
			{
				switch(shutterMode)
				{
					case ShutterConfig.ShutterType.PAUSE_RECORD:
					{
						clearType(CameraBtnConfig.ControlType.CLOSE_PAGE);
						clearType(CameraBtnConfig.ControlType.TAB_SEL_POINT);

						setBtnType(CameraBtnConfig.ControlType.VIDEO_NO_SAVE | CameraBtnConfig.ControlType.COLOR_FILTER | CameraBtnConfig.ControlType.STICKERS | CameraBtnConfig.ControlType.VIDEO_SAVE | CameraBtnConfig.ControlType.VIDEO_DELETE);
						break;
					}

					case ShutterConfig.ShutterType.UNFOLD_RES:
					case ShutterConfig.ShutterType.DEF:
					{
						boolean show = true;
						if (mControlUIListener != null)
						{
							show = !(mControlUIListener.isShowBeautyList() || mControlUIListener.isShowFilterList());
						}
						if(show && mIsShowDurationBtn)
						{
							setBtnType(CameraBtnConfig.ControlType.VIDEO_DURATION);
						}
						break;
					}
				}
				break;
			}
		}
		setBtnVisibilityByStatus();
	}

	private void setBtnVisibilityByStatus()
	{
		boolean isShow;

		if(mPointView != null)
		{
			isShow = containTabType(CameraBtnConfig.ControlType.TAB_SEL_POINT);
			mPointView.setVisibility(isShow ? VISIBLE : GONE);
		}

		if(mBackBtn != null)
		{
			isShow = containTabType(CameraBtnConfig.ControlType.CLOSE_PAGE) || containTabType(CameraBtnConfig.ControlType.VIDEO_NO_SAVE);
			mBackBtn.setVisibility(isShow ? VISIBLE : GONE);
		}

		if(mFilterBtn != null)
		{
			isShow = containTabType(CameraBtnConfig.ControlType.COLOR_FILTER);
			mFilterBtn.setVisibility(isShow ? VISIBLE : GONE);
		}

        if (mBeautyBtn != null)
        {
            isShow = containTabType(CameraBtnConfig.ControlType.BEAUTY);
            mBeautyBtn.setVisibility(isShow ? VISIBLE : GONE);
//            mBeautyBtn.setTranslationX(mTabType != ShutterConfig.TabType.PHOTO ? 0 : -CameraPercentUtil.WidthPxToPercent(110 - 4));
        }

		if(mStickerBtn != null)
		{
			isShow = containTabType(CameraBtnConfig.ControlType.STICKERS);
			mStickerBtn.setVisibility(isShow ? VISIBLE : GONE);
			mStickerBtn.setTranslationX(mShutterMode != ShutterConfig.ShutterType.PAUSE_RECORD ? 0 : -CameraPercentUtil.WidthPxToPercent(110));

		}

		if(mDurationBtn != null)
		{
			isShow = containTabType(CameraBtnConfig.ControlType.VIDEO_DURATION);
			mDurationBtn.setVisibility(isShow ? VISIBLE : GONE);

			if(mCurrentRatio == CameraConfig.PreviewRatio.Ratio_4_3)
			{
				int transY = mDurationBtnDefTransY - CameraPercentUtil.WidthPxToPercent(3) - RatioBgUtils.GetBottomHeightByRation(mCurrentRatio);

				int bottomPadding = 0;
				if(mFullScreenRatio > CameraConfig.PreviewRatio.Ratio_16_9)
				{
					int mCameraViewHeight = (int)(ShareData.getScreenW() * CameraConfig.PreviewRatio.Ratio_16_9);

					if(mCameraViewHeight > ShareData.m_screenRealHeight)
					{
						mCameraViewHeight = (int)(ShareData.getScreenW() * CameraConfig.PreviewRatio.Ratio_4_3);
					}

					if(mFullScreenRatio > CameraConfig.PreviewRatio.Ratio_17$25_9)
					{
						bottomPadding = ShareData.m_screenRealHeight - mCameraViewHeight - RatioBgUtils.getTopPaddingHeight(mCurrentRatio);
					}
					else
					{
						bottomPadding = ShareData.m_screenRealHeight - mCameraViewHeight;
					}
				}
				transY += bottomPadding;
				mDurationBtn.setTranslationY(transY);
			}
			else
			{
				mDurationBtn.setTranslationY(0);
			}
		}

		if(mVideoDelBtn != null)
		{
			isShow = containTabType(CameraBtnConfig.ControlType.VIDEO_DELETE);
			mVideoDelBtn.setVisibility(isShow ? VISIBLE : GONE);
		}

		if(mMixBtn != null)
		{
			isShow = containTabType(CameraBtnConfig.ControlType.PHOTO_ALBUM) || containTabType(CameraBtnConfig.ControlType.VIDEO_SAVE);
			mMixBtn.setVisibility(isShow ? VISIBLE : GONE);
		}
	}

	private boolean containTabType(@CameraBtnConfig.ControlType int type)
	{
		return (mBtnStatus & type) != 0;
	}

	private void resetBtnType()
	{
		mBtnStatus &= CameraBtnConfig.ALL_BTN_GONE;
	}

	private void clearType(@CameraBtnConfig.ControlType int status)
	{
		mBtnStatus &= ~status;
	}

	private void setBtnType(@CameraBtnConfig.ControlType int status)
	{
		mBtnStatus |= status;
	}

	private void updateBtnLogo(int tabType, int shutterMode, float ratio)
	{
		// 所有type共用的view
		boolean is16_9 = (ratio == CameraConfig.PreviewRatio.Ratio_16_9 || ratio == CameraConfig.PreviewRatio.Ratio_Full);
		float alpha = mBtnTouchAlpha;
		int color = is16_9 ? 0 : ImageUtils.GetSkinColor();
		int resID;

		mPointView.setPointColor(is16_9 ? Color.WHITE : Color.BLACK, is16_9);

		resID = is16_9 ? R.drawable.camera_color_filter_16_9 : R.drawable.camera_color_filter_4_3;
		setBtnLogo(mFilterBtn, resID, color, alpha);

		if(mStickerAnimDrawable != null)
		{
			mStickerAnimDrawable.setBmpColor(is16_9 ? Color.WHITE : ImageUtils.GetSkinColor());
		}

		UpdateBeautyIcon();

//		 部分type、mode 才用到的view
		updatePartBtnLogo(tabType, shutterMode, ratio);
	}

	public void UpdateBeautyIcon()
	{
		boolean is16_9 = (mCurrentRatio == CameraConfig.PreviewRatio.Ratio_16_9 || mCurrentRatio == CameraConfig.PreviewRatio.Ratio_Full);
		int resID = is16_9 ? R.drawable.camera_beauty_9_16 : R.drawable.camera_beauty_4_3;

		boolean isNew = TagMgr.CheckTag(getContext(), Tags.CAMERA_TAILOR_MADE_NEW_FLAG);
		if (isNew) {
			Bitmap icon = BitmapFactory.decodeResource(getResources(), resID);
			if (!is16_9) {
				icon = ImageUtils.AddSkin(getContext(), icon);
			}

			if (icon != null && !icon.isMutable()) {
				icon = icon.copy(Bitmap.Config.ARGB_8888, true);
			}
			Bitmap bitmap = drawBeautyNewIcon(icon);
			setBtnLogo(mBeautyBtn, bitmap, -1, mBtnTouchAlpha);
			return;
		}
		int color = is16_9 ? 0 : ImageUtils.GetSkinColor();
		setBtnLogo(mBeautyBtn, resID, color, mBtnTouchAlpha);
	}

	private void updatePartBtnLogo(int type, int shutterMode, float ratio)
	{
		boolean is16_9 = (ratio == CameraConfig.PreviewRatio.Ratio_16_9 || ratio == CameraConfig.PreviewRatio.Ratio_Full);
		int resID;
		float alpha = mBtnTouchAlpha;

		switch(shutterMode)
		{
			case ShutterConfig.ShutterType.PAUSE_RECORD:
			{
				if(type == ShutterConfig.TabType.VIDEO)
				{
					resID = is16_9 ? R.drawable.camera_video_pause_back_16_9 : R.drawable.camera_video_pause_back_4_3;
					setBtnLogo(mBackBtn, resID, 0, alpha);

					resID = is16_9 ? R.drawable.camera_video_save_16_9 : R.drawable.camera_video_save_4_3;
					setBtnLogo(mMixBtn, resID, is16_9 ? 0 : ImageUtils.GetSkinColor(), alpha);

					resID = is16_9 ? R.drawable.camera_16_9_del : R.drawable.camera_4_3_del;
					setBtnLogo(mVideoDelBtn, resID, 0, alpha);
				}
				break;
			}

			default:
			{
				resID = is16_9 ? R.drawable.camera_16_9_back : R.drawable.camera_4_3_back;
				setBtnLogo(mBackBtn, resID, 0, alpha);

				switch(type)
				{
					case ShutterConfig.TabType.VIDEO:
					{
						boolean hasTag = !TagMgr.CheckTag(getContext(), Tags.CAMERA_RECORD_DURATION);
						int duration = CameraConfig.VideoDuration.TEN_SECOND;
						if(hasTag)
						{
							duration = TagMgr.GetTagIntValue(getContext(), Tags.CAMERA_RECORD_DURATION);
						}
						else // 首次
						{
							TagMgr.SetTagValue(getContext(), Tags.CAMERA_RECORD_DURATION, String.valueOf(CameraConfig.VideoDuration.TEN_SECOND));
							TagMgr.Save(getContext());
						}

						boolean is_10s = duration == CameraConfig.VideoDuration.TEN_SECOND;
						if(mIsChinese)
						{
							if(ratio == CameraConfig.PreviewRatio.Ratio_4_3 || is16_9)
							{
								resID = is_10s ? R.drawable.camera_16_9_10_sec : R.drawable.camera_16_9_3_min;
							}
							else
							{
								resID = is_10s ? R.drawable.camera_4_3_10_sec : R.drawable.camera_4_3_3_min;
							}
						}
						else
						{
							if(ratio == CameraConfig.PreviewRatio.Ratio_4_3 || is16_9)
							{
								resID = is_10s ? R.drawable.camera_16_9_10_sec_en : R.drawable.camera_16_9_3_min_en;
							}
							else
							{
								resID = is_10s ? R.drawable.camera_4_3_10_sec_en : R.drawable.camera_4_3_3_min_en;
							}
						}

						setBtnLogo(mDurationBtn, resID, (ratio == CameraConfig.PreviewRatio.Ratio_4_3 || is16_9) ? 0 : ImageUtils.GetSkinColor(), alpha);
						break;
					}

					case ShutterConfig.TabType.PHOTO:
					{
						checkThumbInPhotoStore(getContext());

					}
				}
				break;
			}
		}
	}

	private void setBtnLogo(PressedButton view, Object logo, int color, float alpha)
	{
		if(view == null || logo == null) return;

		if(logo instanceof Integer)
		{
			view.setButtonImage((int)logo, (int)logo, color, alpha);
		}
		else if(logo instanceof Bitmap)
		{
			view.setButtonImage((Bitmap)logo, (Bitmap)logo, color, alpha);
		}
		else if(logo instanceof String)
		{
			String path = (String)logo;
			File file = new File(path);
			if(file.exists())
			{
				Bitmap bmp = BitmapFactory.decodeFile(path);
				view.setButtonImage(bmp, bmp, color, alpha);
			}
		}
	}

	private void checkThumbInPhotoStore(Context context)
	{
		Object logo = R.drawable.camera_photo_picker; // 默认图
		try
		{
			// 查找相册图片,可能有控指针 or 越界异常
			Bitmap thumb = PhotoStore.loadThumb(PhotoStore.getInstance(context).getFirstPhotoInfo(), 150);
			if(thumb != null && !thumb.isRecycled())
			{
				logo = thumb;
			}
			setBtnLogo(mMixBtn, logo, 0, mBtnTouchAlpha);
		}
		catch(Throwable e)
		{
			setBtnLogo(mMixBtn, logo, 0, mBtnTouchAlpha);
			e.printStackTrace();
		}
	}

	public void updateDurationBtnVis(boolean show)
	{
		if(mDurationBtn != null && mIsShowDurationBtn)
		{
			mDurationBtn.setVisibility(show ? VISIBLE : GONE);
		}
	}

	// ================================== change type、mode、ratio, update ui flow end ==================================== //

	public void updateDelLogo(boolean isSel)
	{
		if(mTabType == ShutterConfig.TabType.VIDEO)
		{
			mIsSelectedVideo = isSel;
			float alpha = mBtnTouchAlpha;
			boolean is16_9 = (mCurrentRatio == CameraConfig.PreviewRatio.Ratio_16_9 || mCurrentRatio == CameraConfig.PreviewRatio.Ratio_Full);
			int resID = isSel ? (is16_9 ? R.drawable.camera_16_9_confirm_del : R.drawable.camera_4_3_confirm_del) : (is16_9 ? R.drawable.camera_16_9_del : R.drawable.camera_4_3_del);

			setBtnLogo(mVideoDelBtn, resID, 0, alpha);
		}
	}

	private void checkMixBtnInType(int type, int shutterMode)
	{
		switch(type)
		{
			case ShutterConfig.TabType.PHOTO:
			{
				if(mCameraPageListener != null)
				{
					mCameraPageListener.onClickOpenPhoto();
				}
				break;
			}

			case ShutterConfig.TabType.VIDEO:
			{
				switch(shutterMode)
				{
					case ShutterConfig.ShutterType.PAUSE_RECORD:
					{
						if(mCameraPageListener != null)
						{
							mCameraPageListener.onClickVideoSaveBtn();
						}
						break;
					}
				}
			}
		}
	}

	/**
	 * @return 返回下一个 时长
	 */
	private int changeDuration(float ratio)
	{
		int out;
		int duration = TagMgr.GetTagIntValue(getContext(), Tags.CAMERA_RECORD_DURATION);
		boolean is16_9 = (ratio == CameraConfig.PreviewRatio.Ratio_16_9 || ratio == CameraConfig.PreviewRatio.Ratio_Full);
		float alpha = mBtnTouchAlpha;
		int resID;

		out = (duration == CameraConfig.VideoDuration.TEN_SECOND) ? //点击时是不是10s
				CameraConfig.VideoDuration.THREE_MIN //改成 3min
				: CameraConfig.VideoDuration.TEN_SECOND; //改成 10s

		boolean is_10s = out == CameraConfig.VideoDuration.TEN_SECOND;

		MyBeautyStat.onClickByRes(is_10s ? R.string.拍照_视频拍摄页_主页面_切换10秒 : R.string.拍照_视频拍摄页_主页面_切换3分钟);

		if(mIsChinese)
		{
			if(ratio == CameraConfig.PreviewRatio.Ratio_4_3 || is16_9)
			{
				resID = is_10s ? R.drawable.camera_16_9_10_sec : R.drawable.camera_16_9_3_min;
			}
			else
			{
				resID = is_10s ? R.drawable.camera_4_3_10_sec : R.drawable.camera_4_3_3_min;
			}
		}
		else
		{
			if(ratio == CameraConfig.PreviewRatio.Ratio_4_3 || is16_9)
			{
				resID = is_10s ? R.drawable.camera_16_9_10_sec_en : R.drawable.camera_16_9_3_min_en;
			}
			else
			{
				resID = is_10s ? R.drawable.camera_4_3_10_sec_en : R.drawable.camera_4_3_3_min_en;
			}
		}

		setBtnLogo(mDurationBtn, resID, (ratio == CameraConfig.PreviewRatio.Ratio_4_3 || is16_9) ? 0 : ImageUtils.GetSkinColor(), alpha);

		return out;
	}

	private Bitmap drawBeautyNewIcon(Bitmap icon)
	{
		Bitmap out = icon;
		if (out != null && !out.isRecycled()) {
			boolean isChinese = LanguageUtil.checkSystemLanguageIsChinese(getContext());
			Bitmap dst = Bitmap.createBitmap(out.getWidth(), out.getHeight(), Bitmap.Config.ARGB_8888);

			Paint paint = new Paint();
			Bitmap flag = BitmapFactory.decodeResource(getResources(), R.drawable.photofactory_download_num_bk);
			if (flag != null && !flag.isMutable()) {
				flag = flag.copy(Bitmap.Config.ARGB_8888, true);
			}
			if (flag != null)
			{
				Canvas flagCanvas = new Canvas(flag);
				flagCanvas.drawColor(Color.RED, PorterDuff.Mode.SRC_IN);
				paint.setAntiAlias(true);
				paint.setFilterBitmap(true);
				paint.setColor(Color.WHITE);
				int text_size = ShareData.PxToDpi_xhdpi(14);
				paint.setTextSize(text_size);
				Paint.FontMetrics fontMetrics = paint.getFontMetrics();
				float text_w = paint.measureText(isChinese ? "我" : "Me");
				float text_x = (flag.getWidth() - text_w) / 2f;
				float text_y = (flag.getHeight() + text_size) / 2f - (fontMetrics.ascent - fontMetrics.top);
				flagCanvas.drawText(isChinese ? "我" : "Me", text_x, text_y, paint);

				Canvas canvas = new Canvas(dst);
				paint.reset();
				paint.setAntiAlias(true);
				paint.setFilterBitmap(true);
				canvas.drawBitmap(out, 0, 0, paint);

				float x = out.getWidth() - flag.getWidth();
				paint.reset();
				paint.setAntiAlias(true);
				paint.setFilterBitmap(true);
				canvas.drawBitmap(flag, x, 0, paint);
				out = dst;
			}
		}
		return out;
	}

	// ========================================== 监听 ===================================== //

	public void setCameraPageListener(CameraPageListener listener)
	{
		mCameraPageListener = listener;
	}

	public void setControlUIListener(ControlUIListener listener)
	{
		mControlUIListener = listener;
	}
}