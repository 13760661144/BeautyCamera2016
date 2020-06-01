package cn.poco.nose;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;

import cn.poco.advanced.ImageUtils;
import cn.poco.beautify4.Beautify4Page;
import cn.poco.camera.RotationImg2;
import cn.poco.camera3.ui.FixPointView;
import cn.poco.cloudalbumlibs.utils.CloudAlbumDialog;
import cn.poco.face.FaceDataV2;
import cn.poco.face.FaceLocalData;
import cn.poco.framework.BaseSite;
import cn.poco.framework.IPage;
import cn.poco.framework.SiteID;
import cn.poco.image.PocoBeautyFilter;
import cn.poco.makeup.ChangePointPage;
import cn.poco.nose.view.ControlPanel;
import cn.poco.statistics.MyBeautyStat;
import cn.poco.statistics.TongJi2;
import cn.poco.system.SysConfig;
import cn.poco.tianutils.ShareData;
import cn.poco.utils.CommonUI;
import cn.poco.utils.WaitAnimDialog;
import cn.poco.view.beauty.BeautyCommonViewEx;
import my.beautyCamera.R;

import static cn.poco.beautify4.Beautify4Page.PAGE_BACK_ANIM_IMG_H;
import static cn.poco.beautify4.Beautify4Page.PAGE_BACK_ANIM_VIEW_TOP_MARGIN;
import static cn.poco.view.beauty.BeautyCommonViewEx.MODE_SEL_FACE;

/**
 * Created by: fwc
 * Date: 2016/12/2
 */
public abstract class AbsShapePage extends IPage {

	private static final int UPDATE_UI = 0x1;
	private static final int INIT = 0x2;

	protected static final int HIGH_NOSE = 0x11;
	protected static final int SMILE = 0x12;

	private Context mContext;

	private int DEF_IMG_SIZE;

	private int mFrameWidth;
	private int mFrameHeight;

	private BeautyCommonViewEx mShapeView;

	private ControlPanel mPanel;
	private int mPanelHeight;

	protected Bitmap mBitmap;
	private Object mImgs;
	private Bitmap mTempCompareBmp = null;

	private UIHandler mUIHandler;
	private HandlerThread mHandlerThread;
	private TaskHandler mTaskHandler;

	private WaitAnimDialog mWaitDialog;

	/**
	 * 对比
	 */
	private ImageView mCompareView;

	/**
	 * 定点
	 */
//	private FrameLayout mFixView;
	private FixPointView mFixView;

	private TextView mMutipleFaceDetect;

	private FrameLayout mChangeFaceView;

	private ITaskInfo mTaskInfo;

	protected Bitmap mResult;

	private boolean mChange = false;
	private Dialog mNoFaceDialog;

	private int mImgH;
	private int mViewH;
	private int mViewTopMargin;
	private static final int SHOW_VIEW_ANIM_TIME = 300;

	private int mStartY;
	private float mStartScale;

	private boolean mDoingAnimation = false;

	private boolean mCompareViewEnable = true;

	private boolean mUiEnable = true;

	private boolean mInit = false;

	private boolean mGotoSave = false;

	protected int mValue = 0;

	private CloudAlbumDialog mBackHintDialog;

	public AbsShapePage(Context context, BaseSite site, ITaskInfo taskInfo) {
		super(context, site);
		mContext = context;
		mTaskInfo = taskInfo;

		initDatas();
		initViews();
	}

	@Override
	public void SetData(HashMap<String, Object> params) {
		if (params != null) {
			Object imgs = params.get("imgs");
			if (imgs != null && imgs instanceof RotationImg2[]) {
				mImgs = imgs;
				//mBitmap = ImageUtils.MakeBmp(getContext(), imgs, mFrameWidth, mFrameHeight);
				mBitmap = ImageUtils.MakeBmp(getContext(), imgs, DEF_IMG_SIZE, DEF_IMG_SIZE);
			}
			if (imgs != null && imgs instanceof Bitmap) {
				mBitmap = (Bitmap) imgs;
			}

			Object o = params.get("goto_save");
			if (o instanceof Boolean) {
				mGotoSave = (boolean) o;
			}
		}

		mShapeView.setImage(mBitmap);

		if (params != null) {
			Object o = params.get(Beautify4Page.PAGE_ANIM_IMG_H);

			if (o instanceof Integer) {
				mImgH = (int)o;
			}

			o = params.get(Beautify4Page.PAGE_ANIM_VIEW_H);
			if (o instanceof Integer) {
				mViewH = (int)o;
			}

			o = params.get(Beautify4Page.PAGE_ANIM_VIEW_TOP_MARGIN);
			if (o instanceof Integer) {
				mViewTopMargin = (int) o;
			}

			if (mImgH > 0 && mViewH > 0) {
				mStartY = (int)(mViewTopMargin + (mViewH - mFrameHeight) / 2f);
				float scaleX = (mFrameWidth-2) * 1f / mBitmap.getWidth();
				float scaleY = mFrameHeight * 1f / mBitmap.getHeight();
				mStartScale = mImgH / (mBitmap.getHeight() * Math.min(scaleX, scaleY));
				showViewAnim();
			} else {
				init();
			}
		}
	}

	private void showViewAnim() {

		mDoingAnimation = true;
		AnimatorSet animatorSet = new AnimatorSet();
		ObjectAnimator object1 = ObjectAnimator.ofFloat(mShapeView, "scaleX", mStartScale, 1);
		ObjectAnimator object2 = ObjectAnimator.ofFloat(mShapeView, "scaleY", mStartScale, 1);
		ObjectAnimator object3 = ObjectAnimator.ofFloat(mShapeView, "translationY", mStartY, 0);
		ObjectAnimator yAnimator = ObjectAnimator.ofFloat(mPanel, "translationY", mPanelHeight, 0);
		animatorSet.setDuration(SHOW_VIEW_ANIM_TIME);
		animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
		animatorSet.play(object1).with(object2).with(object3).with(yAnimator);
		animatorSet.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				mDoingAnimation = false;

				init();
			}
		});
		animatorSet.start();

	}

	private void init() {
		setScaleAnim(mFixView, false);

		updateWaitDialog(true, null);

		Message message = mTaskHandler.obtainMessage();
		HandlerData data = new HandlerData();
		data.orgBitmap = mBitmap;
		message.obj = data;
		message.what = INIT;
		mTaskHandler.sendMessage(message);
	}

	private void initDatas() {
		mPanelHeight = ShareData.PxToDpi_xhdpi(320);
		DEF_IMG_SIZE = SysConfig.GetPhotoSize(getContext());

		mFrameWidth = ShareData.m_screenWidth;
		mFrameWidth -= mFrameWidth % 2;
		mFrameWidth += 2;

		mFrameHeight = ShareData.m_screenHeight - mPanelHeight;
		mFrameHeight -= mFrameHeight % 2;

		mUIHandler = new UIHandler();
		mHandlerThread = new HandlerThread("beautify_task");
		mHandlerThread.start();
		mTaskHandler = new TaskHandler(getContext(), mHandlerThread.getLooper(), mUIHandler);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		mFrameHeight = ShareData.m_screenHeight - mPanelHeight;
		mFrameHeight -= mFrameHeight % 2;
	}

	private void initViews() {
		LayoutParams params;

		mShapeView = new BeautyCommonViewEx(mContext);
		mShapeView.SetOnControlListener(mCallback);
		params = new LayoutParams(mFrameWidth, ViewGroup.LayoutParams.MATCH_PARENT);
		params.bottomMargin = mPanelHeight;
		params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
		addView(mShapeView, params);
//		mShapeView.setOnTouchListener(new OnTouchListener() {
//			@Override
//			public boolean onTouch(View v, MotionEvent event) {
//				switch (event.getActionMasked()) {
//					case MotionEvent.ACTION_DOWN:
//						if (mShapeView.m_img != null && mBitmap != null && mTempCompareBmp == null && mViewEnable) {
//							mTempCompareBmp = mShapeView.m_img.m_bmp;
//							mShapeView.m_img.m_bmp = mBitmap;
//							mCompareViewEnable = false;
//						}
//						break;
//					case MotionEvent.ACTION_POINTER_DOWN:
//					case MotionEvent.ACTION_UP:
//					case MotionEvent.ACTION_CANCEL:
//						if (mTempCompareBmp != null && mShapeView.m_img != null && mViewEnable) {
//							mShapeView.m_img.m_bmp = mTempCompareBmp;
//							mTempCompareBmp = null;
//						}
//						mCompareViewEnable = true;
//						break;
//
//					default:
//						break;
//				}
//
//				mShapeView.UpdateUI();
//				return false;
//			}
//		});

		mFixView = new FixPointView(getContext());
//		mFixView.setBackgroundResource(R.drawable.beautify_white_circle_bg);
		params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.END | Gravity.BOTTOM;
		params.rightMargin = ShareData.PxToDpi_xhdpi(24);
		params.bottomMargin = mPanelHeight + ShareData.PxToDpi_xhdpi(24);
		addView(mFixView, params);
//		{
//			ImageView imageView = new ImageView(mContext);
//			imageView.setImageResource(R.drawable.beautify_fix_by_hand);
//			ImageUtils.AddSkin(mContext, imageView);
//			LayoutParams params1 = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//			params1.gravity = Gravity.CENTER;
//			mFixView.addView(imageView, params1);
//		}
		mFixView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!mUiEnable) {
					return;
				}
				mFixView.modifyStatus();
				openFixPage(FaceDataV2.sFaceIndex);
			}
		});
		mFixView.setVisibility(INVISIBLE);

		mChangeFaceView = new FrameLayout(mContext);
		mChangeFaceView.setBackgroundResource(R.drawable.beautify_white_circle_bg);
		params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.END | Gravity.BOTTOM;
		params.rightMargin = ShareData.PxToDpi_xhdpi(120);
		params.bottomMargin = mPanelHeight + ShareData.PxToDpi_xhdpi(24);
		addView(mChangeFaceView, params);
		{
			ImageView imageView = new ImageView(mContext);
			imageView.setImageResource(R.drawable.beautify_makeup_multiface_icon);
			ImageUtils.AddSkin(mContext, imageView);
			LayoutParams params1 = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			params1.gravity = Gravity.CENTER;
			mChangeFaceView.addView(imageView, params1);
		}

		mPanel = new ControlPanel(mContext, mFixView, mChangeFaceView) {
			@Override
			protected String getTitle() {
				return getResources().getString(mTaskInfo.getTitle());
			}

			@Override
			protected int getIcon() {
				return mTaskInfo.getIcon();
			}
		};
		params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mPanelHeight);
		params.gravity = Gravity.BOTTOM;
		mPanel.setOnSeekBarChangeListener(new ControlPanel.OnSeekBarChangeListener() {
			@Override
			public void onChanged(int progress) {
				if (progress == 0) {
					if (mTempCompareBmp != null) {
						mTempCompareBmp = mBitmap;
					} else {
						mShapeView.setImage(mBitmap);
					}
					setScaleAnim(mCompareView, true);
					mChange = false;
				} else {
					onProgressChanged(progress);
				}
			}

			@Override
			public void onStart() {
				if (mTaskInfo.getMessageWhat() == HIGH_NOSE) {
					TongJi2.AddCountByRes(mContext, R.integer.修图_美颜美形_高鼻梁_滑动杆);
				} else if (mTaskInfo.getMessageWhat() == SMILE) {
					TongJi2.AddCountByRes(mContext, R.integer.修图_美颜美形_微笑_滑动杆);
				}
			}
		});
		addView(mPanel, params);
		mPanel.setOnOkClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if (!mUiEnable) {
					return;
				}

				if (mGotoSave || mChange) {
					mResult = mShapeView.getImage();
					save(getBackAnimParam());
				} else {
					cancel(getBackAnimParam());
				}
			}
		});
		mPanel.setOnCancelClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!mUiEnable) {
					return;
				}

				onCancel();
			}
		});
		mPanel.setOnStatusChangeListener(new ControlPanel.OnStatusChangeListener() {
			@Override
			public void onChange(boolean down, Animator animator) {
				onChangeAnim(animator, down);
			}
		});
		mPanel.mSeekBar.setEnabled(false);

		mCompareView = new ImageView(mContext);
		mCompareView.setPadding(0, ShareData.PxToDpi_xhdpi(10), ShareData.PxToDpi_xhdpi(20), 0);
		mCompareView.setImageResource(R.drawable.beautify_compare);
		params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.END;
		addView(mCompareView, params);
		mCompareView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (mShapeView != null && mCompareViewEnable) {
					switch (event.getAction()) {
						case MotionEvent.ACTION_DOWN:
							if (mShapeView.getImage() != null && mBitmap != null && mTempCompareBmp == null) {
								if (mTaskInfo.getMessageWhat() == HIGH_NOSE) {
									MyBeautyStat.onClickByRes(R.string.美颜美图_高鼻梁页面_主页面_对比按钮);
								} else if (mTaskInfo.getMessageWhat() == SMILE) {
									MyBeautyStat.onClickByRes(R.string.美颜美图_微笑页面_主页面_对比按钮);
								}
								mTempCompareBmp = mShapeView.getImage();
								mShapeView.setImage(mBitmap);
								mUiEnable = false;
								mPanel.setUiEnable(false);
							}
							break;

						case MotionEvent.ACTION_UP:
							if (mTempCompareBmp != null) {
								mShapeView.setImage(mTempCompareBmp);
								mTempCompareBmp = null;
							}
							mUiEnable = true;
							mPanel.setUiEnable(true);
							break;

						default:
							break;
					}
				}
				return true;
			}
		});
		mCompareView.setVisibility(INVISIBLE);

		mChangeFaceView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if (!mUiEnable) {
					return;
				}

				if (FaceLocalData.getInstance().m_faceNum > 1) {
					mPanel.setVisibility(GONE);
					setScaleAnim(mFixView, true);
					setScaleAnim(mCompareView, true);
					setScaleAnim(mChangeFaceView, true);
					mMutipleFaceDetect.setVisibility(View.VISIBLE);
					mShapeView.Restore();
				}
			}
		});

		mChangeFaceView.setVisibility(INVISIBLE);

		mMutipleFaceDetect = new TextView(mContext);
		mMutipleFaceDetect.setText(R.string.bigeyes_multiple_face_detect);
		mMutipleFaceDetect.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
		mMutipleFaceDetect.setTextColor(Color.parseColor("#000000"));
		mMutipleFaceDetect.setGravity(Gravity.CENTER);
		mMutipleFaceDetect.setBackgroundDrawable(getResources().getDrawable(R.drawable.beautifyeyes_multiple_indication));
		params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
		params.bottomMargin = mPanelHeight + ShareData.PxToDpi_xhdpi(54);
		addView(mMutipleFaceDetect, params);
		mMutipleFaceDetect.setVisibility(View.GONE);

		mWaitDialog = new WaitAnimDialog((Activity)mContext);
		mWaitDialog.SetGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, mPanelHeight + ShareData.PxToDpi_xhdpi(38));
	}

	private BeautyCommonViewEx.ControlCallback mCallback = new BeautyCommonViewEx.ControlCallback() {

		@Override
		public void OnAnimFinish() {
			if (!mInit) {

				mPanel.mSeekBar.setEnabled(true);

				// 因为显示Dialog时会卡动画，因为放在这里执行操作
				onProgressChanged(ControlPanel.DEFAULT_PROGRESS);
			}
		}

		@Override
		public void OnSelFaceIndex(int index) {
			mPanel.setVisibility(VISIBLE);
			setScaleAnim(mFixView, false);
			setScaleAnim(mChangeFaceView, false);
			mMutipleFaceDetect.setVisibility(View.GONE);
			int progress = getProgress(FaceLocalData.getInstance(), index);
			if (progress > 0) {
				setScaleAnim(mCompareView, false);
			}

			mPanel.mSeekBar.setProgress(progress);

			mShapeView.m_faceIndex = index;
			FaceDataV2.sFaceIndex = mShapeView.m_faceIndex;
			mShapeView.DoSelFaceAnim();
		}
	};

	private void onProgressChanged(int progress) {
		mInit = true;
		updateWaitDialog(true, null);
		if (FaceDataV2.sFaceIndex != -1) {
			progressChanged(FaceLocalData.getInstance(), FaceDataV2.sFaceIndex, progress);
			executeTask();
		}
	}

	@Override
	public void onClose() {

		mTaskHandler.removeCallbacksAndMessages(null);
		mTaskHandler.clear();
		mHandlerThread.quit();

		mUIHandler.removeCallbacksAndMessages(null);

		if(mFixView != null)
		{
			mFixView.clearAll();
		}

		if (mWaitDialog != null) {
			mWaitDialog.dismiss();
			mWaitDialog = null;
		}

		if (mNoFaceDialog != null) {
			mNoFaceDialog.dismiss();
			mNoFaceDialog = null;
		}

		FaceLocalData.ClearData();
	}

	private void onChangeAnim(Animator animator, boolean down) {
		AnimatorSet set = new AnimatorSet();
		int start, end;

		start = mFrameHeight;
		end = mFrameHeight + ShareData.PxToDpi_xhdpi(232);

		if (!down) {
			// 向上
			start = end + start;
			end = start - end;
			start = start - end;

			if (mTaskInfo.getMessageWhat() == HIGH_NOSE) {
				TongJi2.AddCountByRes(mContext, R.integer.修图_美颜美形_高鼻梁_展开bar);
				MyBeautyStat.onClickByRes(R.string.美颜美图_高鼻梁页面_主页面_bar展开);
			} else if (mTaskInfo.getMessageWhat() == SMILE) {
				TongJi2.AddCountByRes(mContext, R.integer.修图_美颜美形_微笑_展开bar);
				MyBeautyStat.onClickByRes(R.string.美颜美图_微笑页面_主页面_bar展开);
			}
		} else {
			if (mTaskInfo.getMessageWhat() == HIGH_NOSE) {
				TongJi2.AddCountByRes(mContext, R.integer.修图_美颜美形_高鼻梁_收回bar);
				MyBeautyStat.onClickByRes(R.string.美颜美图_高鼻梁页面_主页面_bar收回);
			} else if (mTaskInfo.getMessageWhat() == SMILE) {
				TongJi2.AddCountByRes(mContext, R.integer.修图_美颜美形_微笑_收回bar);
				MyBeautyStat.onClickByRes(R.string.美颜美图_微笑页面_主页面_bar收回);
			}
		}

		mShapeView.InitAnimDate(mFrameWidth, start, mFrameWidth, end);

		ValueAnimator valueAnimator = ValueAnimator.ofInt(start, end);
		valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				int value = (Integer) animation.getAnimatedValue();
				LayoutParams params = (LayoutParams) mShapeView.getLayoutParams();
				params.bottomMargin = mPanelHeight - (value - mFrameHeight);
				mShapeView.requestLayout();
			}
		});
		set.playTogether(valueAnimator, animator);
		set.setDuration(300);
		set.start();
	}

	private void executeTask() {
		mChange = true;
		Message message = mTaskHandler.obtainMessage();
		message.what = mTaskInfo.getMessageWhat();
		HandlerData handlerData = new HandlerData();
		handlerData.orgBitmap = mBitmap;
		handlerData.data = FaceLocalData.getInstance().Clone();
		message.obj = handlerData;
		mTaskHandler.sendMessage(message);
	}

	/**
	 * 更新WaitDialog状态
	 *
	 * @param flag 是否显示
	 */
	private void updateWaitDialog(boolean flag, String title) {

		if (flag) {
			if (mWaitDialog != null) {
				mWaitDialog.show();
			}
		} else {
			if (mWaitDialog != null) {
				mWaitDialog.hide();
			}
		}
	}

	public void onCancel() {
		if (mChange) {
			if (mBackHintDialog == null) {
				mBackHintDialog = new CloudAlbumDialog(getContext(),
													   ViewGroup.LayoutParams.WRAP_CONTENT,
													   ViewGroup.LayoutParams.WRAP_CONTENT);
				ImageUtils.AddSkin(getContext(), mBackHintDialog.getOkButtonBg());
				mBackHintDialog.setCancelButtonText(R.string.cancel)
						.setOkButtonText(R.string.ensure)
						.setMessage(R.string.confirm_back)
						.setListener(new CloudAlbumDialog.OnButtonClickListener() {
							@Override
							public void onOkButtonClick() {
								mBackHintDialog.dismiss();
								cancel(getBackAnimParam());
							}

							@Override
							public void onCancelButtonClick() {
								mBackHintDialog.dismiss();
							}
						});
			}
			mBackHintDialog.show();
		} else {
			cancel(getBackAnimParam());
		}
	}

	@Override
	public void onBack() {
		if (!mUiEnable) {
			return;
		}

		mUiEnable = false;
	}

	private HashMap<String, Object> getBackAnimParam() {
		HashMap<String, Object> params = new HashMap<>();
		float imgH = mShapeView.getImgHeight();
		params.put(PAGE_BACK_ANIM_IMG_H, imgH);
		float marginTop = (mShapeView.getHeight() - mFrameHeight) / 2;
		params.put(PAGE_BACK_ANIM_VIEW_TOP_MARGIN, marginTop);
		return params;
	}

	@Override
	public void onPageResult(int siteID, HashMap<String, Object> params) {
		boolean change = false;
		if (params != null) {
			Object o = params.get(ChangePointPage.ISCHANGE);
			if (o != null && o instanceof Boolean) {
				change = (Boolean) o;
			}

			if (siteID == SiteID.CHANGEPOINT_PAGE) {
				if (!mInit) {
					onProgressChanged(ControlPanel.DEFAULT_PROGRESS);
				} else if (change && mChange) {
					executeTask();
				}
			}
		}
	}

	private class UIHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			HandlerData handlerData = (HandlerData)msg.obj;
			msg.obj = null;
			switch (msg.what) {
				case INIT:
					updateWaitDialog(false, null);
					mPanel.mSeekBar.setEnabled(true);
					if (!FaceDataV2.CHECK_FACE_SUCCESS) {
						// 人脸检测失败
						if (mNoFaceDialog == null) {
							mNoFaceDialog = CommonUI.MakeNoFaceHelpDlg((Activity)mContext, new OnClickListener() {
								@Override
								public void onClick(View v) {
									if(mFixView != null)
									{
										mFixView.modifyStatus();
									}
									mNoFaceDialog.dismiss();
									openFixPage(0);
								}
							});
						}
						if (mUiEnable) {
							mNoFaceDialog.show();
						}
					} else {
						if (FaceLocalData.getInstance().m_faceNum > 1) {

							for (int i = 0; i < FaceLocalData.getInstance().m_faceNum; i++) {
								progressChanged(FaceLocalData.getInstance(), i, ControlPanel.DEFAULT_PROGRESS);
							}

							mPanel.mSeekBar.setEnabled(false);

							if (FaceDataV2.sFaceIndex != -1) {
								mShapeView.m_faceIndex = FaceDataV2.sFaceIndex;

								mShapeView.DoSelFaceAnim();

								setScaleAnim(mChangeFaceView, false);
							} else {
								// 多人检测
								mPanel.setVisibility(GONE);
								setScaleAnim(mFixView, true);
								setScaleAnim(mCompareView, true);
								setScaleAnim(mChangeFaceView, true);
								mMutipleFaceDetect.setVisibility(View.VISIBLE);
								mShapeView.m_faceIndex = -1;
								mShapeView.setMode(MODE_SEL_FACE);
							}
						} else {
							onProgressChanged(ControlPanel.DEFAULT_PROGRESS);
						}

						if(mFixView != null)
						{
							mFixView.showJitterAnimAccordingStatus();
						}
					}
					break;
				case UPDATE_UI:
					if (handlerData.outBitmap != null) {
						// 为了解决正在对比操作时刚好完成任务时出现的问题
						if (mTempCompareBmp != null) {
							mTempCompareBmp = handlerData.outBitmap;
						} else {
							mShapeView.setImage(handlerData.outBitmap);
						}
					}
					handlerData.outBitmap = null;

					// 隐藏loading
					updateWaitDialog(false, null);

					setScaleAnim(mCompareView, false);
					break;
				default:
					break;
			}
		}
	}

	private static class TaskHandler extends Handler {

		private Handler mUiHandler;

		private Bitmap mBitmap;

		private Context mContext;

		TaskHandler(Context context, Looper looper, Handler uiHandler) {
			super(looper);
			mContext = context;
			mUiHandler = uiHandler;
		}

		@Override
		public void handleMessage(Message msg) {
			HandlerData handlerData = (HandlerData)msg.obj;
			msg.obj = null;

			mBitmap = handlerData.orgBitmap.copy(Bitmap.Config.ARGB_8888, true);
			handlerData.orgBitmap = null;

			if (msg.what == INIT) {
				if (!FaceDataV2.CHECK_FACE_SUCCESS) {
					//人脸检测
					if (mContext != null) {
						FaceDataV2.CheckFace(mContext, mBitmap);
						//初始化人脸识别
						FaceLocalData.getNewInstance(FaceDataV2.FACE_POS_MULTI.length);
					}
				} else if (FaceLocalData.getInstance() == null) {
					//初始化人脸识别
					FaceLocalData.getNewInstance(FaceDataV2.FACE_POS_MULTI.length);
				}

				if (mUiHandler != null) {
					Message message = mUiHandler.obtainMessage();
					message.what = INIT;
					mUiHandler.sendMessage(message);
				}
				return;
			}

			switch (msg.what) {
				case HIGH_NOSE:
					for (int i = 0; i < handlerData.data.m_faceNum; i++) {
						if (FaceDataV2.RAW_POS_MULTI != null &&
								FaceDataV2.RAW_POS_MULTI.length > 0 &&
								handlerData.data.m_highNoseLevel_multi[i] > 0) {
							mBitmap = PocoBeautyFilter.BaseHno(mBitmap, FaceDataV2.RAW_POS_MULTI[i], handlerData.data.m_highNoseLevel_multi[i]);
						}
					}
					break;
				case SMILE:
					for (int i = 0; i < handlerData.data.m_faceNum; i++) {
						if (FaceDataV2.RAW_POS_MULTI != null &&
								FaceDataV2.RAW_POS_MULTI.length > 0 &&
								handlerData.data.m_smileLevel_multi[i] > 0) {
							mBitmap = PocoBeautyFilter.smile(mBitmap, FaceDataV2.RAW_POS_MULTI[i], handlerData.data.m_smileLevel_multi[i]);
						}
					}
					break;
				default:
					break;
			}

			if (mUiHandler != null) {
				handlerData.outBitmap = mBitmap;
				Message message = mUiHandler.obtainMessage();
				message.what = UPDATE_UI;
				message.obj = handlerData;
				mUiHandler.sendMessage(message);
			}
		}

		public void clear()
		{
			mUiHandler = null;
			mContext = null;
		}
	}

	/**
	 * 进度变化回调
	 *
	 * @param progress 进度
	 */
	protected abstract void progressChanged(FaceLocalData data, int faceIndex, int progress);

	protected abstract int getProgress(FaceLocalData data, int faceIndex);

	/**
	 * 打开定点界面
	 */
	protected abstract void openFixPage(int faceIndex);

	protected abstract void cancel(HashMap<String, Object> temp);

	protected abstract void save(HashMap<String, Object> temp);

	private static class HandlerData {
		Bitmap orgBitmap;
		FaceLocalData data;
		Bitmap outBitmap;
	}

	protected interface ITaskInfo {
		int getMessageWhat();

		int getTitle();

		int getIcon();
	}

	private void setScaleAnim(final View view, boolean hide) {
		if (hide && view.getVisibility() == VISIBLE) {
			view.animate().scaleX(0).scaleY(0).setDuration(100).setListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					view.setVisibility(INVISIBLE);
				}
			});
		} else if (!hide && view.getVisibility() != VISIBLE) {
			view.setScaleX(0);
			view.setScaleY(0);
			view.setVisibility(VISIBLE);
			view.animate().scaleX(1).scaleY(1).setDuration(100).setListener(null);
		}
	}
}
