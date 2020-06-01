package cn.poco.camera3;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import cn.poco.camera.CameraConfig;
import cn.poco.camera3.cb.CameraPageListener;
import cn.poco.camera3.cb.UIObservable;
import cn.poco.camera3.config.CameraUIConfig;
import cn.poco.camera3.config.MsgToastConfig;
import cn.poco.camera3.config.shutter.ShutterConfig;
import cn.poco.camera3.mgr.CameraSettingMgr;
import cn.poco.camera3.ui.ColorFilterToast;
import cn.poco.camera3.ui.MsgToast;
import cn.poco.camera3.util.CameraPercentUtil;
import cn.poco.camera3.util.RatioBgUtils;
import cn.poco.tianutils.ShareData;
import cn.poco.utils.DrawableUtils;
import my.beautyCamera.R;

/**
 * @author Gxx
 *         Created by Gxx on 2017/8/29.
 */

public class CameraLayoutV3 extends FrameLayout
{
	public CameraTopControlV3 mCameraTopControl;
	public CameraBottomControlV3 mCameraBottomControl;
	public CameraPopSetting mCameraPopSetting;

	private boolean mIsFingerDown = false;
	private MsgToast mToast;
	private ColorFilterToast mColorFilterToast;

	private boolean mDoingRotationAnim = false;
	private int mLastDegree;
	private int mDegree;
	private int mAnimTargetDegree;
	private boolean isLandScape = false;
	private float mFullScreenRatio;

	public CameraLayoutV3(@NonNull Context context)
	{
		super(context);
		mFullScreenRatio = ShareData.m_screenRealHeight * 1.0f / ShareData.m_screenRealWidth;
		setBackgroundDrawable(null);
		initView();
		initToastConfig();
	}

	private void initToastConfig()
	{
		mToast = new MsgToast();
		mToast.setParent(this);

		mColorFilterToast = new ColorFilterToast();
		mColorFilterToast.setParent(this);

		// 标题
		MsgToastConfig config = new MsgToastConfig(MsgToastConfig.Key.TAB_TITLE);
		config.setGravity(Gravity.CENTER_HORIZONTAL, 0, CameraPercentUtil.HeightPxToPercent(130));
		config.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
		config.setTextColor(Color.WHITE);
		config.setTextBG(R.drawable.sticker_gif_title_bk);
		config.setTextBGAlpha(0.8f);
		mToast.addConfig(config);

		// 视频时长
		config = new MsgToastConfig(MsgToastConfig.Key.VIDEO_DURATION);
		config.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
		config.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
		config.setTextColor(0xE6000000);
		config.setTextBG(R.drawable.camera_duration_tips_bk);
		mToast.addConfig(config);

		// 设置
		config = new MsgToastConfig(MsgToastConfig.Key.SETTING);
		config.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
		config.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
		config.setTextColor(0xcc000000);
		config.setTextPadding(CameraPercentUtil.WidthPxToPercent(24), CameraPercentUtil.WidthPxToPercent(15), CameraPercentUtil.WidthPxToPercent(24), CameraPercentUtil.WidthPxToPercent(15));
		config.setTextBGDrawable(DrawableUtils.shapeDrawable(0xffffffff, CameraPercentUtil.WidthPxToPercent(45)));
		mToast.addConfig(config);

		// 贴纸动作
		config = new MsgToastConfig(MsgToastConfig.Key.STICKER_ACTION);
		config.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
		config.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 22);
		config.setTextColor(0xffffffff);
		config.setShadow(3f, 2, 2, 0xa7000000);
		mToast.addConfig(config);
	}

	public void clearAll()
	{
		cancelToast();

		if(mToast != null)
		{
			mToast.ClearAll();
		}

		if(mColorFilterToast != null)
		{
			mColorFilterToast.ClearAll();
		}

		if(mCameraBottomControl != null)
		{
			mCameraBottomControl.ClearMemory();
		}

		if(mCameraTopControl != null)
		{
			mCameraTopControl.clearAll();
		}

		if(mCameraPopSetting != null)
		{
			mCameraPopSetting.setUIListener(null);
			mCameraPopSetting.clearAll();
		}

		removeAllViews();
	}

	private void initView()
	{
		// bar层
		mCameraTopControl = new CameraTopControlV3(getContext());
		mCameraTopControl.setClickable(true);
		mCameraTopControl.setLongClickable(true);
		mCameraTopControl.setId(R.id.camera_layout_top_control);
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
		addView(mCameraTopControl, params);

		// bottom层
		mCameraBottomControl = new CameraBottomControlV3(getContext());
		params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		params.gravity = Gravity.BOTTOM;
		addView(mCameraBottomControl, params);

		// setting 弹窗
		mCameraPopSetting = new CameraPopSetting(getContext());
		mCameraPopSetting.setTranslationY(-CameraPercentUtil.HeightPxToPercent(188));
		params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, CameraPercentUtil.HeightPxToPercent(188));
		params.gravity = Gravity.TOP;
		addView(mCameraPopSetting, params);
	}

	public void setUIConfig(CameraUIConfig config)
	{
		if(mCameraBottomControl != null)
		{
			mCameraBottomControl.setUIConfig(config);
		}

		if(mCameraTopControl != null)
		{
			mCameraTopControl.setUIConfig(config);
		}

		if(mCameraPopSetting != null)
		{
			mCameraPopSetting.setUiConfig(config);
		}
	}

	public void setUIObserver(@NonNull UIObservable UIObserver)
	{
		mCameraTopControl.SetUIObserver(UIObserver);
		mCameraBottomControl.SetUIObserver(UIObserver);
	}

	public void setCameraPageListener(CameraPageListener listener)
	{
		mCameraBottomControl.SetCameraPageListener(listener);
		mCameraTopControl.setCameraPageListener(listener);
	}

	public void updateColorFilterToastMsgHeight(int height)
	{
		if(mColorFilterToast != null)
		{
			mColorFilterToast.updateToastMsgHeight(height);
		}
	}

	public void showColorFilterToast(String msg)
	{
		if(mColorFilterToast != null)
		{
			mColorFilterToast.show(msg);
		}
	}

	public void showTitleToast(int type)
	{
		if(mToast == null) return;

		String title;

		switch(type)
		{
			case ShutterConfig.TabType.GIF:
			{
				title = getContext().getString(R.string.camera_gif_title);
				break;
			}

			case ShutterConfig.TabType.CUTE:
			{
				title = getContext().getString(R.string.camera_meng_zhuang_title);
				break;
			}

			case ShutterConfig.TabType.PHOTO:
			{
				title = getContext().getString(R.string.camera_photo_title);
				break;
			}

			case ShutterConfig.TabType.VIDEO:
			{
				title = getContext().getString(R.string.camera_video_title);
				break;
			}

			default:
			{
				title = "";
			}
		}

		mToast.show(MsgToastConfig.Key.TAB_TITLE, title);
	}

	public void showDurationTips(String text, int value)
	{
		float ratio = CameraConfig.PreviewRatio.Ratio_4_3;

		if(mCameraBottomControl != null)
		{
			mCameraBottomControl.updateVideoDuration(value);
			ratio = mCameraBottomControl.getRatio();
		}

		if(mToast != null)
		{
			MsgToastConfig config = mToast.getConfig(MsgToastConfig.Key.VIDEO_DURATION);

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

			mToast.show(MsgToastConfig.Key.VIDEO_DURATION, text);
		}
	}

	public void showSettingToast(String text)
	{
		float ratio = CameraConfig.PreviewRatio.Ratio_4_3;

		if(mCameraBottomControl != null)
		{
			ratio = mCameraBottomControl.getRatio();
            if(ratio > CameraConfig.PreviewRatio.Ratio_16_9) {
                ratio = mFullScreenRatio;
            }
		}

		if(mToast != null)
		{
			MsgToastConfig config = mToast.getConfig(MsgToastConfig.Key.SETTING);

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

			mToast.show(MsgToastConfig.Key.SETTING, text);
		}
	}

	public void showActionToast(String text)
	{

		float ratio = CameraConfig.PreviewRatio.Ratio_4_3;

		if(mCameraBottomControl != null)
		{
			ratio = mCameraBottomControl.getRatio();
            if(ratio > CameraConfig.PreviewRatio.Ratio_16_9) {
                ratio = mFullScreenRatio;
            }
		}

		if(mToast != null)
		{
			MsgToastConfig config = mToast.getConfig(MsgToastConfig.Key.STICKER_ACTION);

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

			mToast.show(MsgToastConfig.Key.STICKER_ACTION, text);
		}
	}

	public void handlePauseEvent()
	{
		cancelToast();

		if(mCameraBottomControl != null)
		{
			mCameraBottomControl.handleRecordingStatusInPause();
		}

		if(mCameraPopSetting != null && mCameraPopSetting.isAlive())
		{
			mCameraPopSetting.dismissWithoutAnim();
		}
	}

	public void cancelToast()
	{
		if(mToast != null)
		{
			mToast.cancel();
		}

		if(mColorFilterToast != null)
		{
			mColorFilterToast.cancel();
		}
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event)
	{
		if(event.getActionMasked() == MotionEvent.ACTION_DOWN)
		{
			mIsFingerDown = true;
		}
		else if(event.getActionMasked() == MotionEvent.ACTION_POINTER_DOWN)
		{
			mIsFingerDown = false;
		}

		if(mCameraPopSetting != null && mCameraPopSetting.isAlive())
		{
			if(mIsFingerDown)
			{
				if(event.getAction() == MotionEvent.ACTION_UP)
				{
					if(event.getY() > mCameraPopSetting.getBottom() && !mCameraPopSetting.isDoingAnim())
					{
						if(mCameraBottomControl != null && mCameraBottomControl.isTouchShutter(event))
						{
							mIsFingerDown = false;
							int timer = CameraSettingMgr.getCurrentTimerMode();
							if(timer == CameraConfig.CaptureMode.Manual)
							{
								mCameraPopSetting.dismissWithoutAnim();
							}
							else
							{
								mCameraPopSetting.dismiss();
							}
						}
						else
						{
							mCameraPopSetting.dismiss();
						}
					}
				}

				if(mCameraBottomControl != null)
				{
					mCameraBottomControl.passParentEventToShutter(event);
				}

				if(event.getY() <= mCameraPopSetting.getBottom() && !mCameraPopSetting.isDoingAnim())
				{
					passEventToChild(mCameraPopSetting, event);
				}
			}

			return true;
		}
		else
		{
			// 清除选中效果
			if(mCameraBottomControl != null && mCameraBottomControl.GetShutterMode() == ShutterConfig.ShutterType.PAUSE_RECORD)
			{
				if(mCameraBottomControl.isAlreadySelLastVideo())
				{
					if(mCameraBottomControl.mControlLayout != null && event.getY() < (mCameraBottomControl.getTop() + mCameraBottomControl.mControlLayout.getTop()))
					{
						mCameraBottomControl.resetSelectedStatus();
						return true;
					}
				}
			}
		}

		return super.dispatchTouchEvent(event);
	}

	private void passEventToChild(View child, MotionEvent event)
	{
		if(child == null) return;

		float x = event.getX() - child.getLeft();
		float y = event.getY() - child.getTop();
		MotionEvent ev = MotionEvent.obtain(event.getDownTime(), event.getEventTime(), event.getAction(), x, y, event.getMetaState());
		child.dispatchTouchEvent(ev);
	}

	public void startRotationAnim(final int degree)
	{
		if(mDegree != degree) // 修复有默认滤镜，竖屏进入镜头页，文案从侧边出现的bug
		{
			mLastDegree = mDegree % 360;
			mDegree = (degree + 360) % 360;

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

		if(mLastDegree == 0 && mDegree == 90 || mLastDegree == 360 && mDegree == 270 || mLastDegree == 180 && mDegree == 90 || mLastDegree == 180 && mDegree == 270)
		{
			isLandScape = true;// 竖屏 变 横屏
		}

		if(mLastDegree == 90 && mDegree == 0 || mLastDegree == 270 && mDegree == 360 || mLastDegree == 90 && mDegree == 180 || mLastDegree == 270 && mDegree == 180)
		{
			isLandScape = false; // 横屏 变 竖屏
		}

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

	private void setBtnRotate(int degree, float animValue)
	{
		if(mCameraBottomControl != null)
		{
			mCameraBottomControl.setControlBtnRotate(degree);
		}

		if(mCameraTopControl != null)
		{
			mCameraTopControl.setBtnRotation(degree);
		}

		if(mToast != null)
		{
			mToast.setRotation(degree);
		}

		if(mColorFilterToast != null)
		{
			mColorFilterToast.updateRotateAnimInfo(isLandScape, degree, animValue);
		}

		if(mCameraPopSetting != null)
		{
			mCameraPopSetting.setRotation(degree);
		}
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event)
	{
		return event.getPointerCount() > 1 || super.onInterceptTouchEvent(event);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		return event.getPointerCount() > 1 || super.onTouchEvent(event);
	}
}
