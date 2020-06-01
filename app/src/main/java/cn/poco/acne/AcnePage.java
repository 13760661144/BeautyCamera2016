package cn.poco.acne;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.poco.acne.site.AcneSite;
import cn.poco.acne.view.ControlPanel;
import cn.poco.acne.view.UndoPanel;
import cn.poco.advanced.ImageUtils;
import cn.poco.beautify.SonWindow;
import cn.poco.beautify4.Beautify4Page;
import cn.poco.camera.RotationImg2;
import cn.poco.cloudalbumlibs.utils.CloudAlbumDialog;
import cn.poco.face.FaceLocalData;
import cn.poco.framework.BaseSite;
import cn.poco.framework.IPage;
import cn.poco.image.filter;
import cn.poco.statistics.MyBeautyStat;
import cn.poco.statistics.TongJi2;
import cn.poco.statistics.TongJiUtils;
import cn.poco.system.SysConfig;
import cn.poco.tianutils.ShareData;
import cn.poco.utils.WaitAnimDialog;
import cn.poco.view.beauty.AcneViewEx;
import my.beautyCamera.R;

import static cn.poco.beautify4.Beautify4Page.PAGE_BACK_ANIM_IMG_H;
import static cn.poco.beautify4.Beautify4Page.PAGE_BACK_ANIM_VIEW_TOP_MARGIN;

/**
 * Created by: fwc
 * Date: 2016/11/29
 */
public class AcnePage extends IPage {

	private static final int REMOVE_ACNE = 0x12;
	private static final int UPDATE_UI = 0x13;

	private AcneSite mSite;
	private Context mContext;

	private int DEF_IMG_SIZE;

	private AcneViewEx mAcneView;

	private ControlPanel mPanel;

	private SonWindow mSonWin;

	private ImageView mCompareView;

	private int mPanelHeight;

	private int mFrameWidth;
	private int mFrameHeight;

	private Bitmap mBitmap;
	private Object mImgs;
	private Bitmap mTempCompareBmp = null;

	private ArrayList<PointData> mAcneDatas = new ArrayList<>();

	/**
	 * Redo的数据
	 * 当撤销时会从mAcneDatas中移除最后一个数据然后添加到mRedoAcneDatas中
	 */
	private ArrayList<PointData> mRedoAcneDatas = new ArrayList<>();

	private UndoPanel mUndoPanel;
	private WaitAnimDialog mWaitDialog;

	private UIHandler mUIHandler;
	private HandlerThread mHandlerThread;
	private RemoveAcneHandler mAcneHandler;

	private boolean mChange = false;
	private CloudAlbumDialog mBackHintDialog;

	private int mImgH;
	private int mViewH;
	private int mViewTopMargin;
	private static final int SHOW_VIEW_ANIM_TIME = 300;

	private int mStartY;
	private float mStartScale;

	private boolean mDoingAnimation = false;

	private boolean mUiEnable = true;

	private boolean mGotoSave = false;

	public AcnePage(Context context, BaseSite site) {
		super(context, site);
		TongJiUtils.onPageStart(context, R.string.祛痘);
		MyBeautyStat.onPageStartByRes(R.string.美颜美图_祛痘页_主页面);

		mContext = context;
		mSite = (AcneSite)site;

		initDatas();
	}

	/**
	 * 设置数据
	 *
	 * @param params 传入参数
	 *               imgs: RotationImg2[]/Bitmap
	 */
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
				mBitmap = (Bitmap)imgs;
			}

			Object o = params.get("goto_save");
			if (o instanceof Boolean) {
				mGotoSave = (boolean) o;
			}
		}

		initViews();

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
				mViewTopMargin = (int)o;
			}

			if (mImgH > 0 && mViewH > 0) {
				mStartY = (int)(mViewTopMargin + (mViewH - mFrameHeight) / 2f);
				float scaleX = (mFrameWidth - 2) * 1f / mBitmap.getWidth();
				float scaleY = mFrameHeight * 1f / mBitmap.getHeight();
				mStartScale = mImgH / (mBitmap.getHeight() * Math.min(scaleX, scaleY));
				showViewAnim();
			} else {
				showTip();
			}
		}
	}

	private void showViewAnim() {

		mDoingAnimation = true;
		AnimatorSet animatorSet = new AnimatorSet();
		ObjectAnimator object1 = ObjectAnimator.ofFloat(mAcneView, "scaleX", mStartScale, 1);
		ObjectAnimator object2 = ObjectAnimator.ofFloat(mAcneView, "scaleY", mStartScale, 1);
		ObjectAnimator object3 = ObjectAnimator.ofFloat(mAcneView, "translationY", mStartY, 0);
		ObjectAnimator yAnimator = ObjectAnimator.ofFloat(mPanel, "translationY", mPanelHeight, 0);
		animatorSet.setDuration(SHOW_VIEW_ANIM_TIME);
		animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
		animatorSet.play(object1).with(object2).with(object3).with(yAnimator);
		animatorSet.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				mDoingAnimation = false;
				showTip();
			}
		});
		animatorSet.start();
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		mFrameHeight = ShareData.m_screenHeight - mPanelHeight;
		mFrameHeight -= mFrameHeight % 2;
	}

	private void initDatas() {
		mPanelHeight = ShareData.PxToDpi_xhdpi(320);
		DEF_IMG_SIZE = SysConfig.GetPhotoSize(getContext());

		mFrameWidth = ShareData.m_screenWidth;
		mFrameWidth -= mFrameWidth % 2;
		mFrameHeight = ShareData.m_screenHeight - mPanelHeight;
		mFrameHeight -= mFrameHeight % 2;

		mFrameWidth += 2;

		mUIHandler = new UIHandler();
		mHandlerThread = new HandlerThread("remove_acne");
		mHandlerThread.start();
		mAcneHandler = new RemoveAcneHandler(mHandlerThread.getLooper(), mUIHandler);
	}

	private void initViews() {

		LayoutParams params;
		mAcneView = new AcneViewEx(mContext, mCallback);
		mAcneView.def_stroke_width = ShareData.PxToDpi_xhdpi(2);
		if (mAcneView.def_stroke_width < 1) {
			mAcneView.def_stroke_width = 1;
		}
		mAcneView.def_color = Color.WHITE;
		mAcneView.setImage(mBitmap);
		params = new LayoutParams(mFrameWidth, ViewGroup.LayoutParams.MATCH_PARENT);
		params.bottomMargin = mPanelHeight;
		params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
		addView(mAcneView, params);
		mAcneView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return !mUiEnable;
			}
		});

		mPanel = new ControlPanel(mContext);
		params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mPanelHeight);
		params.gravity = Gravity.BOTTOM;
		mPanel.setOnSeekBarChangeListener(new ControlPanel.OnSeekBarChangeListener() {
			@Override
			public void onChanged(int progress) {
				updateCircleSize(progress);
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

					TongJi2.AddCountByRes(getContext(), R.integer.修图_美颜美形_祛痘_确认);
					MyBeautyStat.onClickByRes(R.string.美颜美图_祛痘页_主页面_确认);
					MyBeautyStat.onUseAntiAcne(mPanel.getProgress() + 5);

					Bitmap out = mAcneView.getImage();
					HashMap<String, Object> params = new HashMap<>();
					params.put("img", out);
					params.putAll(getBackAnimParam());
					mSite.OnSave(getContext(), params);
				} else {
					cancel();
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
		mPanel.setOnPanelChangeListener(new ControlPanel.OnPanelChangeListener() {
			@Override
			public void onChanged(boolean down, Animator animator) {

				if (mDoingAnimation) {
					return;
				}

				mDoingAnimation = true;

				AnimatorSet set = new AnimatorSet();
				int start, end;

				start = mFrameHeight;
				end = mFrameHeight + ShareData.PxToDpi_xhdpi(232);

				ObjectAnimator animator1;

				if (!down) {
					// 向上
					start = end + start;
					end = start - end;
					start = start - end;

					animator1 = ObjectAnimator.ofFloat(mUndoPanel, "translationY", ShareData.PxToDpi_xhdpi(232), 0);
				} else {
					animator1 = ObjectAnimator.ofFloat(mUndoPanel, "translationY", 0, ShareData.PxToDpi_xhdpi(232));
				}

				mAcneView.InitAnimDate(mFrameWidth, start, mFrameWidth, end);

				ValueAnimator valueAnimator = ValueAnimator.ofInt(start, end);
				valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
					@Override
					public void onAnimationUpdate(ValueAnimator animation) {
						int value = (Integer)animation.getAnimatedValue();
						LayoutParams params = (LayoutParams)mAcneView.getLayoutParams();
						params.bottomMargin = mPanelHeight - (value - mFrameHeight);
						mAcneView.requestLayout();
					}
				});
				set.playTogether(valueAnimator, animator, animator1);
				set.setDuration(300);
				set.addListener(new AnimatorListenerAdapter() {
					@Override
					public void onAnimationEnd(Animator animation) {
						mDoingAnimation = false;
					}
				});
				set.start();
			}
		});

		mSonWin = new SonWindow(getContext());
		params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, ShareData.m_screenWidth / 3);
		params.gravity = Gravity.TOP | Gravity.START;
		mSonWin.setLayoutParams(params);
		addView(mSonWin);

		mCompareView = new ImageView(mContext);
		mCompareView.setPadding(0, ShareData.PxToDpi_xhdpi(10), ShareData.PxToDpi_xhdpi(20), 0);
		mCompareView.setImageResource(R.drawable.beautify_compare);
		params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.END;
		addView(mCompareView, params);
		mCompareView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (mAcneView != null) {
					switch (event.getAction()) {
						case MotionEvent.ACTION_DOWN:
							TongJi2.AddCountByRes(getContext(), R.integer.修图_美颜美形_祛痘_对比按钮);
							MyBeautyStat.onClickByRes(R.string.美颜美图_祛痘页_主页面_对比按钮);
							if (mBitmap != null && mTempCompareBmp == null) {
								mTempCompareBmp = mAcneView.getImage();
								mAcneView.setImage(mBitmap);
								mUiEnable = false;
								mPanel.setUiEnable(false);
							}
							break;

						case MotionEvent.ACTION_UP:
							if (mTempCompareBmp != null) {
								mAcneView.setImage(mTempCompareBmp);
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

		mWaitDialog = new WaitAnimDialog((Activity)mContext);
		mWaitDialog.SetGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, mPanelHeight + ShareData.PxToDpi_xhdpi(38));

		initUndoCtrl();
	}

	/**
	 * 初始化控制按钮
	 */
	private void initUndoCtrl() {
		mUndoPanel = new UndoPanel(getContext());
		mUndoPanel.setCallback(new UndoPanel.Callback() {
			@Override
			public void onUndo() {
				if (!mUiEnable) {
					return;
				}

				int len = mAcneDatas.size();
				if (len > 0 && !mDoingAnimation) {
					MyBeautyStat.onClickByRes(R.string.美颜美图_祛痘页_主页面_撤回);
					mRedoAcneDatas.add(mAcneDatas.remove(len - 1));
					updateUndoCtrl(mAcneDatas, mRedoAcneDatas);

					updateWaitDialog(true, "正在处理");

					removeAcneTask(true);
					if (mAcneDatas.isEmpty()) {
						mChange = false;
					}
				}
			}

			@Override
			public void onRedo() {
				if (!mUiEnable) {
					return;
				}
				int len = mRedoAcneDatas.size();
				if (len > 0 && !mDoingAnimation) {
					MyBeautyStat.onClickByRes(R.string.美颜美图_祛痘页_主页面_重做);
					mAcneDatas.add(mRedoAcneDatas.remove(len - 1));
					updateUndoCtrl(mAcneDatas, mRedoAcneDatas);

					updateWaitDialog(true, "正在处理");

					removeAcneTask(false);
				}
			}
		});

		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.BOTTOM;
		params.leftMargin = ShareData.PxToDpi_xhdpi(28);
		params.bottomMargin = ShareData.PxToDpi_xhdpi(30) + mPanelHeight;
		addView(mUndoPanel, params);
		mUndoPanel.setVisibility(INVISIBLE);
	}

	@Override
	public void onBack() {
		if (!mUiEnable) {
			return;
		}
		onCancel();
	}

	public void onCancel() {
		TongJi2.AddCountByRes(getContext(), R.integer.修图_美颜美形_祛痘_取消);
		MyBeautyStat.onClickByRes(R.string.美颜美图_祛痘页_主页面_取消);
		if (mChange) {
			if (mBackHintDialog == null) {
				mBackHintDialog = new CloudAlbumDialog(mContext, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				ImageUtils.AddSkin(mContext, mBackHintDialog.getOkButtonBg());
				mBackHintDialog.setCancelButtonText(R.string.cancel).setOkButtonText(R.string.ensure).setMessage(R.string.confirm_back).setListener(new CloudAlbumDialog.OnButtonClickListener() {
					@Override
					public void onOkButtonClick() {
						mBackHintDialog.dismiss();
						cancel();
					}

					@Override
					public void onCancelButtonClick() {
						mBackHintDialog.dismiss();
					}
				});
			}
			mBackHintDialog.show();
		} else {
			cancel();
		}
	}

	private Toast mToast;

	private void showTip() {
		mToast = new Toast(mContext);
		TextView textView = new TextView(mContext);
		textView.setBackgroundResource(R.drawable.acne_tip_bg);
		textView.setText(R.string.acne_tip);
		textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
		textView.setTextColor(Color.BLACK);
		textView.setGravity(Gravity.CENTER);
		mToast.setView(textView);
		mToast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, ShareData.PxToDpi_xhdpi(372));
		mToast.setDuration(Toast.LENGTH_SHORT);
		mToast.show();
	}

	private void cancel() {
		HashMap<String, Object> params = new HashMap<>();
		params.put("img", mBitmap);
		params.putAll(getBackAnimParam());
		mSite.onBack(getContext(), params);
	}

	private HashMap<String, Object> getBackAnimParam() {
		HashMap<String, Object> params = new HashMap<>();
		float imgH = mAcneView.getImgHeight();
		params.put(PAGE_BACK_ANIM_IMG_H, imgH);
		float marginTop = (mAcneView.getHeight() - mFrameHeight) / 2;
		params.put(PAGE_BACK_ANIM_VIEW_TOP_MARGIN, marginTop);
		return params;
	}

	@Override
	public void onResume() {
		TongJiUtils.onPageResume(mContext, R.string.祛痘);
	}

	@Override
	public void onPause() {
		TongJiUtils.onPagePause(mContext, R.string.祛痘);
	}

	@Override
	public void onClose() {

		mAcneHandler.removeCallbacksAndMessages(null);
		mHandlerThread.quit();

		mUIHandler.removeCallbacksAndMessages(null);

		if (mBackHintDialog != null)
		{
			mBackHintDialog.dismiss();
			mBackHintDialog.setListener(null);
			mBackHintDialog = null;
		}

		if (mWaitDialog != null) {
			mWaitDialog.dismiss();
			mWaitDialog = null;
		}

		if (mToast != null) {
			mToast.cancel();
			mToast = null;
		}

		FaceLocalData.ClearData();

		TongJiUtils.onPageEnd(mContext, R.string.祛痘);
		MyBeautyStat.onPageEndByRes(R.string.美颜美图_祛痘页_主页面);
	}

	private AcneViewEx.ControlCallback mCallback = new AcneViewEx.ControlCallback() {
		@Override
		public void OnTouchAcne(float x, float y, float rw) {
			if (mDoingAnimation) {
				return;
			}

			mAcneDatas.add(new PointData(x, y, rw));
			mRedoAcneDatas.clear();
			updateUndoCtrl(mAcneDatas, mRedoAcneDatas);

			updateWaitDialog(true, "正在处理");

			removeAcneTask(false);
		}

		@Override
		public void UpdateSonWin(Bitmap bmp, int x, int y) {
			if (mSonWin != null) {
				mSonWin.SetData(bmp, x, y);
			}
		}

		@Override
		public void OnFingerDown(int count) {
			if (count == 1) {
				setCompareViewState(true);
				mUndoPanel.hide();
			} else {
				OnFingerUp();
			}
		}

		@Override
		public void OnFingerUp() {
			setCompareViewState(false);
			if (!mAcneDatas.isEmpty() || !mRedoAcneDatas.isEmpty()) {
				mUndoPanel.show();
			}
		}

		@Override
		public void OnViewSizeChange() {

		}
	};

	/**
	 * 执行祛痘操作
	 */
	private void removeAcneTask(boolean isAll) {

		mChange = true;
		Message message = mAcneHandler.obtainMessage();
		message.what = REMOVE_ACNE;
		HandlerData handlerData = new HandlerData();
		List<PointData> temp = new ArrayList<>();
		if (isAll) {
			handlerData.orgBitmap = mBitmap;
			temp.addAll(mAcneDatas);
		} else {
			handlerData.orgBitmap = mAcneView.getImage();
			int size = mAcneDatas.size();
			temp.addAll(mAcneDatas.subList(size - 1, size));
		}

		handlerData.datas = temp;
		message.obj = handlerData;
		mAcneHandler.sendMessage(message);
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

	private void updateUndoCtrl(ArrayList<?> undoArr, ArrayList<?> redoArr) {
		if (mUndoPanel != null) {
			if (undoArr == null || undoArr.size() <= 0) {
				if (mUndoPanel.isCanUndo()) {
					mUndoPanel.setCanUndo(false);
				}
			} else {
				if (!mUndoPanel.isCanUndo()) {
					mUndoPanel.setCanUndo(true);
				}
			}
			if (redoArr == null || redoArr.size() <= 0) {
				if (mUndoPanel.isCanRedo()) {
					mUndoPanel.setCanRedo(false);
				}
			} else {
				if (!mUndoPanel.isCanRedo()) {
					mUndoPanel.setCanRedo(true);
				}
			}
		}
	}

	/**
	 * 更新圆点大小
	 *
	 * @param value 进度值
	 */
	private void updateCircleSize(int value) {
		if (value < 0 || value > 100) {
			value = 100;
		}

		float scale = value * 1.5f / 100f + 1f;

		if (mAcneView != null) {
			mAcneView.SetAcneToolRScale(scale);
		}
	}

	private class UIHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			HandlerData handlerData = (HandlerData)msg.obj;
			msg.obj = null;
			switch (msg.what) {
				case UPDATE_UI:
					if (handlerData.outBitmap != null) {
						// 为了解决正在对比操作时刚好完成任务时出现的问题
						if (mTempCompareBmp != null) {
							mTempCompareBmp = handlerData.outBitmap;
						} else {
							mAcneView.setImage(handlerData.outBitmap);
						}
					}

					handlerData.outBitmap = null;

					// 隐藏loading
					updateWaitDialog(false, null);

					setCompareViewState(false);

					mUndoPanel.show();

					break;
				default:
					break;
			}
		}
	}

	private void setCompareViewState(boolean forceHide) {

		mCompareView.animate().cancel();

		if ((forceHide || mAcneDatas.isEmpty()) && mCompareView.getVisibility() == VISIBLE) {
			mCompareView.animate().scaleX(0).scaleY(0).setDuration(100).setListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					mCompareView.setVisibility(INVISIBLE);
				}
			});

		} else if (!mAcneDatas.isEmpty()) {

			if (mCompareView.getVisibility() != VISIBLE) {
				mCompareView.setScaleX(0);
				mCompareView.setScaleY(0);
				mCompareView.setVisibility(VISIBLE);
				mCompareView.animate().scaleX(1).scaleY(1).setDuration(100).setListener(null);
			} else {
				mCompareView.setScaleX(1);
				mCompareView.setScaleY(1);
			}
		}
	}

	private static class RemoveAcneHandler extends Handler {

		private Handler mUiHandler;

		private Bitmap mBitmap;

		RemoveAcneHandler(Looper looper, Handler uiHandler) {
			super(looper);

			mUiHandler = uiHandler;
		}

		@Override
		public void handleMessage(Message msg) {

			HandlerData handlerData = (HandlerData)msg.obj;
			msg.obj = null;

			switch (msg.what) {
				case REMOVE_ACNE:
					mBitmap = handlerData.orgBitmap.copy(Bitmap.Config.ARGB_8888, true);
					handlerData.orgBitmap = null;
					if (handlerData.datas != null) {
						PointData temp;
						int len = handlerData.datas.size();
						if (len > 0) {
							float[] ps = new float[len << 1];
							float[] rs = new float[len];
							for (int i = 0; i < len; i++) {
								temp = handlerData.datas.get(i);
								ps[i << 1] = temp.m_x;
								ps[(i << 1) + 1] = temp.m_y;
								rs[i] = temp.m_r;
							}
							mBitmap = filter.remove_blemish_continuous(mBitmap, ps, rs, len);
						}

						handlerData.datas.clear();
						handlerData.datas = null;
					}

					handlerData.outBitmap = mBitmap;
					Message message = mUiHandler.obtainMessage();
					message.what = UPDATE_UI;
					message.obj = handlerData;
					mUiHandler.sendMessage(message);
					break;
				default:
					break;
			}
		}
	}

	private static class HandlerData {
		Bitmap orgBitmap;
		List<PointData> datas;
		Bitmap outBitmap;
	}

	private static class PointData {
		public float m_x;
		public float m_y;
		public float m_r;

		public PointData(float x, float y, float r) {
			m_x = x;
			m_y = y;
			m_r = r;
		}
	}
}
