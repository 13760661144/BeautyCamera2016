package cn.poco.nose.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import cn.poco.acne.view.CirclePanel;
import cn.poco.advanced.ImageUtils;
import cn.poco.beauty.view.ColorSeekBar;
import cn.poco.filterPendant.MyStatusButton;
import cn.poco.home.home4.utils.PercentUtil;
import cn.poco.tianutils.ShareData;
import cn.poco.utils.OnAnimationClickListener;
import my.beautyCamera.R;

/**
 * Created by: fwc
 * Date: 2016/12/1
 */
public abstract class ControlPanel extends FrameLayout {

	private Context mContext;

	private int mTopBarHeight;
	private int mContentHeight;
	private int mSeekBarHeight;
	private int mSeekBarMargin;
	private int mImagePadding;

	private RelativeLayout mTopBarLayout;
	private ImageView mCancelView;
	private ImageView mOkView;

	private FrameLayout mContentLayout;
	public ColorSeekBar mSeekBar;

	private CirclePanel mCirclePanel;
	private int mCircleMarginTop;

	private float mCircleRadius;

	private OnSeekBarChangeListener mListener;

	private MyStatusButton mStatusButton;

	private boolean mDown = false;
	private AnimatorSet mDownAnimator;
	private AnimatorSet mUpAnimator;

	private View mFixView;
	private View mChangeFaceView;

	private OnStatusChangeListener mOnStatusChangeListener;

	private boolean mUiEnable = true;

	public static final int DEFAULT_PROGRESS = 30;

	public ControlPanel(Context context, View fixView, View changeFaceView) {
		super(context);

		mContext = context;

		mFixView = fixView;
		mChangeFaceView = changeFaceView;

		initDatas();
		initViews();
	}

	private void initDatas() {
		mTopBarHeight = ShareData.PxToDpi_xhdpi(88);
		mContentHeight = ShareData.PxToDpi_xhdpi(234);

		mSeekBarHeight = ShareData.PxToDpi_xhdpi(50);
//		mSeekBarMargin = ShareData.PxToDpi_xhdpi(80);
		mSeekBarMargin = PercentUtil.WidthPxToPercent(80);

		mCircleRadius = ShareData.PxToDpi_xhdpi(55);

		mCircleMarginTop = ShareData.PxToDpi_xhdpi(79);

		mImagePadding = ShareData.PxToDpi_xhdpi(22);
	}

	private void initViews() {
		setClickable(true);
		LayoutParams params;

		mTopBarLayout = new RelativeLayout(mContext);
		mTopBarLayout.setBackgroundColor(0xe6ffffff);
		params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mTopBarHeight);
		addView(mTopBarLayout, params);
		{
			RelativeLayout.LayoutParams params1;
			mCancelView = new ImageView(mContext);
			mCancelView.setImageResource(R.drawable.beautify_cancel);
			mCancelView.setPadding(mImagePadding, 0, mImagePadding, 0);
			params1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
			params1.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			mTopBarLayout.addView(mCancelView, params1);

			mOkView = new ImageView(mContext);
			mOkView.setImageResource(R.drawable.beautify_ok);
			mOkView.setPadding(mImagePadding, 0, mImagePadding, 0);
			ImageUtils.AddSkin(mContext, mOkView);
			params1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
			params1.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			mTopBarLayout.addView(mOkView, params1);

			mStatusButton = new MyStatusButton(mContext);
			mStatusButton.setData(getIcon(), getTitle());
			mStatusButton.setBtnStatus(true, false);
			params1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
			params1.addRule(RelativeLayout.CENTER_IN_PARENT);
			mTopBarLayout.addView(mStatusButton, params1);
			mStatusButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (mUpAnimator.isRunning() || mDownAnimator.isRunning() || !mUiEnable) {
						return;
					}

					if (mOnStatusChangeListener != null) {
						if (mDown) {
							mOnStatusChangeListener.onChange(false, mUpAnimator);
						} else {
							mOnStatusChangeListener.onChange(true, mDownAnimator);
						}
					}
				}
			});
		}

		mContentLayout = new FrameLayout(mContext);
		mContentLayout.setClickable(true);
		params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mContentHeight);
		params.gravity = Gravity.BOTTOM;
		addView(mContentLayout, params);
		{
			LayoutParams params1;
			mSeekBar = new ColorSeekBar(mContext);
			mSeekBar.setMax(100);
			mSeekBar.setProgress(DEFAULT_PROGRESS);
			mSeekBar.setOnSeekBarChangeListener(mSeekBarListener);
			params1 = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			params1.gravity = Gravity.CENTER_VERTICAL;
			params1.leftMargin = params1.rightMargin = mSeekBarMargin;

			mContentLayout.addView(mSeekBar, params1);
		}

		mCirclePanel = new CirclePanel(mContext);
		params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		addView(mCirclePanel, params);

		mDownAnimator = new AnimatorSet();
		ObjectAnimator y1 = ObjectAnimator.ofFloat(this, "translationY", 0, ShareData.PxToDpi_xhdpi(232));
		ObjectAnimator y2 = ObjectAnimator.ofFloat(mFixView, "translationY", 0, ShareData.PxToDpi_xhdpi(232));
		ObjectAnimator y3 = ObjectAnimator.ofFloat(mChangeFaceView, "translationY", 0, ShareData.PxToDpi_xhdpi(232));
		mDownAnimator.play(y1).with(y2).with(y3);
		mDownAnimator.setDuration(300);
		mDownAnimator.addListener(new AnimatorListenerAdapter() {

			@Override
			public void onAnimationStart(Animator animation) {
				mStatusButton.setBtnStatus(true, true);
				mDown = true;
			}
		});

		mUpAnimator = new AnimatorSet();
		y1 = ObjectAnimator.ofFloat(this, "translationY", ShareData.PxToDpi_xhdpi(232), 0);
		y2 = ObjectAnimator.ofFloat(mFixView, "translationY", ShareData.PxToDpi_xhdpi(232), 0);
		y3 = ObjectAnimator.ofFloat(mChangeFaceView, "translationY", ShareData.PxToDpi_xhdpi(232), 0);
		mUpAnimator.play(y1).with(y2).with(y3);
		mUpAnimator.setDuration(300);
		mUpAnimator.addListener(new AnimatorListenerAdapter() {

			@Override
			public void onAnimationStart(Animator animation) {
				mStatusButton.setBtnStatus(true, false);
				mDown = false;
			}
		});
	}

	public void setOnOkClickListener(final OnClickListener onOkClickListener) {
		if (onOkClickListener != null) {
			mOkView.setOnTouchListener(new OnAnimationClickListener() {
				@Override
				public void onAnimationClick(View v) {
					onOkClickListener.onClick(v);
				}

				@Override
				public void onTouch(View v) {

				}

				@Override
				public void onRelease(View v) {

				}
			});
		}
	}

	public void setOnCancelClickListener(final OnClickListener onCancelClickListener) {
		mCancelView.setOnClickListener(onCancelClickListener);
		if (onCancelClickListener != null) {
			mCancelView.setOnTouchListener(new OnAnimationClickListener() {
				@Override
				public void onAnimationClick(View v) {
					onCancelClickListener.onClick(v);
				}

				@Override
				public void onTouch(View v) {

				}

				@Override
				public void onRelease(View v) {

				}
			});
		}
	}

	public void setOnSeekBarChangeListener(OnSeekBarChangeListener listener) {
		mListener = listener;
	}

	private ColorSeekBar.OnSeekBarChangeListener mSeekBarListener = new ColorSeekBar.OnSeekBarChangeListener() {

		@Override
		public void onProgressChanged(ColorSeekBar seekBar, int progress) {
			showCircle(seekBar, progress);
		}

		@Override
		public void onStartTrackingTouch(ColorSeekBar seekBar) {
			showCircle(seekBar, seekBar.getProgress());
			mListener.onStart();
		}

		@Override
		public void onStopTrackingTouch(ColorSeekBar seekBar) {
			mCirclePanel.hide();
			if (mListener != null) {
				mListener.onChanged(seekBar.getProgress());
			}
		}
	};

	public interface OnSeekBarChangeListener {
		void onChanged(int progress);

		void onStart();
	}

	protected abstract String getTitle();
	protected abstract int getIcon();

	private void showCircle(ColorSeekBar seekBar, int progress) {
		int seekBarWidth = seekBar.getWidth();
		float circleX = mSeekBarHeight / 2 + mSeekBarMargin + progress / 100f * (seekBarWidth - mSeekBarHeight);
		float circleY = mCircleMarginTop;
		mCirclePanel.change(circleX, circleY, mCircleRadius);
		mCirclePanel.setText(String.valueOf(progress));
		mCirclePanel.show();
	}

	public void setOnStatusChangeListener(OnStatusChangeListener listener) {
		mOnStatusChangeListener = listener;
	}

	public interface OnStatusChangeListener {
		void onChange(boolean down, Animator animator);
	}

	public void setUiEnable(boolean uiEnable) {
		mUiEnable = uiEnable;
		mSeekBar.setEnabled(uiEnable);
	}
}
