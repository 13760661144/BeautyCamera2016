package cn.poco.rise;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
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

import java.util.HashMap;

import cn.poco.acne.view.CirclePanel;
import cn.poco.acne.view.UndoPanel;
import cn.poco.advanced.ImageUtils;
import cn.poco.beautify4.Beautify4Page;
import cn.poco.camera.RotationImg2;
import cn.poco.camera3.util.CameraPercentUtil;
import cn.poco.cloudalbumlibs.utils.CloudAlbumDialog;
import cn.poco.face.FaceDataV2;
import cn.poco.filterPendant.MyStatusButton;
import cn.poco.framework.IPage;
import cn.poco.rise.site.RisePageSite;
import cn.poco.statistics.MyBeautyStat;
import cn.poco.tianutils.MakeBmpV2;
import cn.poco.tianutils.ShareData;
import cn.poco.utils.OnAnimationClickListener;
import cn.poco.widget.PressedButton;
import my.beautyCamera.R;

/**
 * 增高
 * Created by Gxx on 2017/11/28.
 */

public class RisePage extends IPage
{
	private static final int SHOW_VIEW_ANIM = 300;

	private int mMinRadius;
	private float mCircleMarginTop;
	private int mBottomHeight;
	private int mPreHeight;
	private int mSeekBarMargin;
	private int mCompareRightMargin;
	private int mCompareTopMargin;

	private Toast mToast;
	private RisePreView mItemView;
	private FrameLayout mBottomLayout;
	private PressedButton mCancelBtn;
	private MyStatusButton mCenterBtn;
	private PressedButton mOkBtn;
	private CirclePanel mCirclePanel;
	private RiseSeekBar mSeekBar;
	private ImageView mCompareBtn;
	private UndoPanel mUndoCtrl;

	private RiseSeekBar.OnSeekBarChangeListener mSeekBarListener;
	private OnAnimationClickListener mOnAnimationClickListener;
	private OnClickListener mBtnOnClickListener;

	private RisePageSite mSite;

	private Bitmap mOrgBmp;
	private Bitmap mBmp;
	private UndoPanel.Callback mUndoCB;
	private RisePreView.CallBack mItemViewCB;

	private OnTouchListener mAnimTouchListener;

	// 进入增高页时的动画参数
	private int m_imgH;
	private int m_viewH;
	private CloudAlbumDialog mExitDialog;

	public RisePage(Context context, RisePageSite site)
	{
		super(context, site);
		MyBeautyStat.onPageStartByRes(R.string.美颜美图_增高页_主页面);
		mSite = site;
		initData();
		initCB();
		initView(context);
	}

	private void initData()
	{
		mSeekBarMargin = CameraPercentUtil.WidthPxToPercent(80);
		mBottomHeight = CameraPercentUtil.HeightPxToPercent(320);
		mCompareRightMargin = CameraPercentUtil.WidthPxToPercent(20);
		mCompareTopMargin = CameraPercentUtil.WidthPxToPercent(12);
		mPreHeight = ShareData.m_screenHeight - mBottomHeight;
		mCircleMarginTop = ShareData.m_screenHeight - CameraPercentUtil.WidthPxToPercent(264);
		mMinRadius = CameraPercentUtil.WidthPxToPercent(55);
	}

	private void initCB()
	{
		mItemViewCB = new RisePreView.CallBack()
		{
			@Override
			public void onTouchDashedAreaEnd()
			{
				mSeekBar.setProgress(0);
			}

			@Override
			public void onCanNotUnDo()
			{
				if(mUndoCtrl != null)
				{
					mUndoCtrl.setVisibility(VISIBLE);
					mUndoCtrl.setCanUndo(false);
				}
			}

			@Override
			public void onCanUnDo()
			{
				if(mUndoCtrl != null)
				{
					mUndoCtrl.setVisibility(VISIBLE);
					mUndoCtrl.setCanUndo(true);
				}
			}

			@Override
			public void onCanNotReDo()
			{
				if(mUndoCtrl != null)
				{
					mUndoCtrl.setVisibility(VISIBLE);
					mUndoCtrl.setCanRedo(false);
				}
			}

			@Override
			public void onCanReDo()
			{
				if(mUndoCtrl != null)
				{
					mUndoCtrl.setVisibility(VISIBLE);
					mUndoCtrl.setCanRedo(true);
				}
			}

			@Override
			public void onShowUnDoCtrl(boolean show)
			{
				if(mUndoCtrl != null)
				{
					mUndoCtrl.setVisibility(show ? VISIBLE : GONE);
				}
			}

			@Override
			public void onShowCompare(boolean show)
			{
				if(mCompareBtn != null)
				{
					mCompareBtn.setVisibility(show ? VISIBLE : GONE);
				}
			}
		};

		mUndoCB = new UndoPanel.Callback()
		{
			@Override
			public void onUndo()
			{
				// 回撤
				if(mUndoCtrl != null && mUndoCtrl.isCanUndo())
				{
					MyBeautyStat.onClickByRes(R.string.美颜美图_增高页_主页面_撤销);
					if(mSeekBar != null)
					{
						mSeekBar.setProgress(0);
					}

					if(mItemView != null)
					{
						mItemView.unDo();
					}
				}
			}

			@Override
			public void onRedo()
			{
				// 重做
				if(mUndoCtrl != null && mUndoCtrl.isCanRedo())
				{
					MyBeautyStat.onClickByRes(R.string.美颜美图_增高页_主页面_重做);
					if(mSeekBar != null)
					{
						mSeekBar.setProgress(0);
					}

					if(mItemView != null)
					{
						mItemView.reDo();
					}
				}
			}
		};

		mSeekBarListener = new RiseSeekBar.OnSeekBarChangeListener()
		{
			@Override
			public void onProgressChanged(RiseSeekBar seekBar, int progress)
			{
				showCircle(seekBar, progress);
				mItemView.setStretchDegree(seekBar.getProgress() / 100f, false);
			}

			@Override
			public void onStartTrackingTouch(RiseSeekBar seekBar)
			{
				MyBeautyStat.onClickByRes(R.string.美颜美图_增高页_主页面_点击bar);
				showCircle(seekBar, seekBar.getProgress());
				mItemView.setStretchDegree(seekBar.getProgress() / 100f, false);
			}

			@Override
			public void onStopTrackingTouch(RiseSeekBar seekBar)
			{
				mCirclePanel.hide();
				mCompareBtn.setVisibility(VISIBLE);
				mItemView.setStretchDegree(seekBar.getProgress() / 100f, true);
			}
		};

		mOnAnimationClickListener = new OnAnimationClickListener()
		{
			@Override
			public void onAnimationClick(View v)
			{
				if(v == mCancelBtn)
				{
					MyBeautyStat.onClickByRes(R.string.美颜美图_增高页_主页面_返回);
					if (mItemView != null && mItemView.isHasChange()) {
						showExitDialog(new CloudAlbumDialog.OnButtonClickListener()
						{
							@Override
							public void onOkButtonClick()
							{
								if (mExitDialog != null) mExitDialog.dismiss();
								onBack();
							}

							@Override
							public void onCancelButtonClick()
							{
								if (mExitDialog != null) mExitDialog.dismiss();
							}
						});
					} else {
						onBack();
					}
				}
				else if(v == mOkBtn)
				{
					MyBeautyStat.onClickByRes(R.string.美颜美图_增高页_主页面_确认);
					onSave();
				}
			}
		};

		mAnimTouchListener = new OnTouchListener()
		{
			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				if(v == mCompareBtn)
				{
					switch(event.getAction())
					{
						case MotionEvent.ACTION_DOWN:
						{
							v.setScaleX(0.9f);
							v.setScaleY(0.9f);

							if(mItemView != null)
							{
								mItemView.compare(true);
							}
							break;
						}

						case MotionEvent.ACTION_CANCEL:
						case MotionEvent.ACTION_OUTSIDE:
						case MotionEvent.ACTION_UP:
						{
							v.setScaleX(1f);
							v.setScaleY(1f);

							MyBeautyStat.onClickByRes(R.string.美颜美图_增高页_主页面_对比按钮);
							if(mItemView != null)
							{
								mItemView.compare(false);
							}
							break;
						}
					}
				}
				return true;
			}
		};

		mBtnOnClickListener = new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if(v == mCenterBtn)
				{
					showTip();
				}
			}
		};
	}

	private void showExitDialog(final CloudAlbumDialog.OnButtonClickListener listener)
	{
		if (mExitDialog == null) {
			mExitDialog = new CloudAlbumDialog(getContext(),
											   ViewGroup.LayoutParams.WRAP_CONTENT,
											   ViewGroup.LayoutParams.WRAP_CONTENT);
			ImageUtils.AddSkin(getContext(), mExitDialog.getOkButtonBg());
			mExitDialog.setCancelButtonText(R.string.cancel)
					.setOkButtonText(R.string.ensure)
					.setMessage(R.string.confirm_back);
		}
		mExitDialog.setListener(null).setListener(listener);
		mExitDialog.show();
	}

	private void showTip()
	{
		if(mToast == null)
		{
			mToast = new Toast(getContext());
			TextView textView = new TextView(getContext());
			textView.setBackgroundResource(R.drawable.rise_page_tip_bg);
			textView.setText(R.string.rise_page_no_scale_tip);
			textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
			textView.setTextColor(Color.BLACK);
			textView.setGravity(Gravity.CENTER);
			mToast.setView(textView);
			mToast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, ShareData.PxToDpi_xhdpi(372));
			mToast.setDuration(Toast.LENGTH_SHORT);
		}
		mToast.show();
	}

	private void initView(Context context)
	{
		mItemView = new RisePreView(context);
		mItemView.setOnListener(mItemViewCB);
		FrameLayout.LayoutParams params = new LayoutParams(ShareData.m_screenWidth, mPreHeight);
		params.gravity = Gravity.CENTER_HORIZONTAL;
		addView(mItemView, params);

		mBottomLayout = new FrameLayout(context);
		params = new LayoutParams(ShareData.m_screenWidth, mBottomHeight);
		params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
		addView(mBottomLayout, params);
		{
			FrameLayout bar = new FrameLayout(context);
			bar.setBackgroundColor(0xe6ffffff);
			params = new LayoutParams(LayoutParams.MATCH_PARENT, CameraPercentUtil.HeightPxToPercent(88)); //88px
			params.gravity = Gravity.CENTER_HORIZONTAL;
			mBottomLayout.addView(bar, params);
			{
				//叉
				mCancelBtn = new PressedButton(context, R.drawable.beautify_cancel, R.drawable.beautify_cancel);
				mCancelBtn.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
				mCancelBtn.setPadding(ShareData.PxToDpi_xhdpi(22), 0, ShareData.PxToDpi_xhdpi(22), 0);
				params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
				params.gravity = Gravity.CENTER_VERTICAL;
				mCancelBtn.setOnTouchListener(mOnAnimationClickListener);
				bar.addView(mCancelBtn, params);

				mCenterBtn = new MyStatusButton(getContext());
				mCenterBtn.setData(R.drawable.rise_center_logo, getContext().getString(R.string.rise_page_title));
				mCenterBtn.setBtnStatus(true, false);
				mCenterBtn.setOnClickListener(mBtnOnClickListener);
				params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
				params.gravity = Gravity.CENTER;
				bar.addView(mCenterBtn, params);

				//勾
				mOkBtn = new PressedButton(context, R.drawable.beautify_ok, R.drawable.beautify_ok);
				mOkBtn.setScaleType(ImageView.ScaleType.CENTER);
				mOkBtn.setPadding(ShareData.PxToDpi_xhdpi(22), 0, ShareData.PxToDpi_xhdpi(22), 0);
				ImageUtils.AddSkin(getContext(), mOkBtn);
				params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
				params.gravity = Gravity.END | Gravity.CENTER_VERTICAL;
				mOkBtn.setOnTouchListener(mOnAnimationClickListener);
				bar.addView(mOkBtn, params);
			}

			mSeekBar = new RiseSeekBar(context);
			mSeekBar.setOnSeekBarChangeListener(mSeekBarListener);
			params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			params.topMargin = CameraPercentUtil.HeightPxToPercent(130);
			params.leftMargin = params.rightMargin = mSeekBarMargin;
			mBottomLayout.addView(mSeekBar, params);

			mCirclePanel = new CirclePanel(context);
			params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
			addView(mCirclePanel, params);
		}

		mUndoCtrl = new UndoPanel(getContext());
		mUndoCtrl.setVisibility(GONE);
		mUndoCtrl.setCallback(mUndoCB);
		params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.BOTTOM;
		params.leftMargin = CameraPercentUtil.WidthPxToPercent(20);
		params.bottomMargin = CameraPercentUtil.WidthPxToPercent(18) + mBottomHeight;
		addView(mUndoCtrl, params);

		mCompareBtn = new ImageView(context);
		mCompareBtn.setVisibility(View.GONE);
		mCompareBtn.setScaleType(ImageView.ScaleType.CENTER_CROP);
		mCompareBtn.setImageResource(R.drawable.beautify_compare);
		mCompareBtn.setOnTouchListener(mAnimTouchListener);
		params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.END | Gravity.TOP;
		params.rightMargin = mCompareRightMargin;
		params.topMargin = mCompareTopMargin;
		addView(mCompareBtn, params);
	}

	private void showCircle(RiseSeekBar seekBar, int progress)
	{
		float radius = mMinRadius;
		int seekBarWidth = seekBar.getWidth();
		int validWidth = seekBar.getValidWidth();
		float circleX = mSeekBarMargin + seekBarWidth / 2f + progress / 100f * (validWidth / 2f);
		float circleY = mCircleMarginTop;
		mCirclePanel.change(circleX, circleY, radius);
		int value = seekBar.getProgress();
		mCirclePanel.setText(value > 0 ? "+" + value : String.valueOf(value));
		mCirclePanel.show();
	}

	@Override
	public void SetData(HashMap<String, Object> params)
	{
		if(params != null)
		{
			Object o = params.get(Beautify4Page.PAGE_ANIM_IMG_H);
			if(o != null && o instanceof Integer)
			{
				m_imgH = (int)o;
			}

			o = params.get(Beautify4Page.PAGE_ANIM_VIEW_H);
			if(o != null && o instanceof Integer)
			{
				m_viewH = (int)o;
			}

			if(params.containsKey("imgs"))
			{
				Object object = params.get("imgs");
				if(object != null)
				{
					if(object instanceof Bitmap)
					{
						mOrgBmp = (Bitmap)object;
					}
					else if(object instanceof RotationImg2[])
					{
						RotationImg2[] imgArr = (RotationImg2[])object;
						if(imgArr.length > 0)
						{
							RotationImg2 obj = imgArr[0];
							int rotation = obj.m_degree;
							int flip = obj.m_flip;
							float scale = -1;
							mOrgBmp = cn.poco.imagecore.Utils.DecodeShowImage((Activity)getContext(), obj.m_img, rotation, scale, flip);
						}
					}

					if(mOrgBmp != null)
					{
						int max_size = 4032;
						mBmp = MakeBmpV2.CreateBitmapV2(mOrgBmp, 0, MakeBmpV2.FLIP_NONE, -1, max_size, max_size, Bitmap.Config.ARGB_8888);
						mItemView.setBitmap(mBmp);
					}
				}
			}

			ShowStarAnim();
		}
	}

	private void onCancel()
	{
		HashMap<String, Object> params = new HashMap<>();
		params.put("img", mOrgBmp);
		params.putAll(getBackAnimParam());
		mSite.onBack(getContext(), params);
	}

	private void onSave()
	{
		HashMap<String, Object> params = new HashMap<>();
		if(mItemView != null)
		{
			params.put("img", mItemView.getOutPutBmp());
		}
		else
		{
			params.put("img", mBmp);
		}
		params.putAll(getBackAnimParam());
		mSite.onSave(getContext(), params);
	}

	private HashMap<String, Object> getBackAnimParam()
	{
		HashMap<String, Object> params = new HashMap<>();
		params.put(Beautify4Page.PAGE_BACK_ANIM_VIEW_TOP_MARGIN, (mItemView.getHeight() - mPreHeight) / 2f);
		params.put(Beautify4Page.PAGE_BACK_ANIM_IMG_H, mItemView.getImgHeight());
		return params;
	}

	private void ShowStarAnim()
	{
		if(m_viewH > 0 && m_imgH > 0)
		{
			int tempStartY = (int)(ShareData.PxToDpi_xhdpi(90) + (m_viewH - mPreHeight) / 2f);
			float scaleX = (ShareData.m_screenWidth - 2) * 1f / (float)mBmp.getWidth();
			float scaleY = (mPreHeight - 2) * 1f / (float)mBmp.getHeight();
			int m_currImgH = (int)(mBmp.getHeight() * Math.min(scaleX, scaleY));
			float scaleH = m_imgH / m_currImgH;
			ShowViewAnim(mItemView, tempStartY, 0, scaleH, 1f, SHOW_VIEW_ANIM);
		}
		else
		{
			postDelayed(new Runnable()
			{
				@Override
				public void run()
				{
					mItemView.HideSelAreaTipText();
				}
			}, 1000);
		}
	}

	private void ShowViewAnim(final View view, int startY, int endY, float startScale, float endScale, int duration)
	{
		if(view != null)
		{

			AnimatorSet animatorSet = new AnimatorSet();
			ObjectAnimator object1 = ObjectAnimator.ofFloat(view, "scaleX", startScale, endScale);
			ObjectAnimator object2 = ObjectAnimator.ofFloat(view, "scaleY", startScale, endScale);
			ObjectAnimator object3 = ObjectAnimator.ofFloat(view, "translationY", startY, endY);
			ObjectAnimator object4 = ObjectAnimator.ofFloat(mBottomLayout, "translationY", mBottomHeight, 0);
			animatorSet.setDuration(duration);
			animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
			animatorSet.playTogether(object1, object2, object3, object4);
			animatorSet.addListener(new AnimatorListenerAdapter()
			{
				@Override
				public void onAnimationEnd(Animator animation)
				{
					postDelayed(new Runnable()
					{
						@Override
						public void run()
						{
							mItemView.HideSelAreaTipText();
						}
					}, 1000);
				}
			});
			animatorSet.start();
		}
	}

	@Override
	public void onBack()
	{
		onCancel();
	}

	@Override
	public void onClose()
	{
		super.onClose();

		MyBeautyStat.onPageEndByRes(R.string.美颜美图_增高页_主页面);
		if(mItemView != null)
		{
			mItemView.ClearAll();
		}

		if(mItemView != null)
		{
			mItemView.clearAll();
		}

		// 清除人脸检测数据
		FaceDataV2.ResetData();

		mBmp = null;
		mOrgBmp = null;
	}
}
