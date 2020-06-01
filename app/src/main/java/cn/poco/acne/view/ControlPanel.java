package cn.poco.acne.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import cn.poco.advanced.ImageUtils;
import cn.poco.beauty.view.ColorSeekBar;
import cn.poco.filterPendant.MyStatusButton;
import cn.poco.home.home4.utils.PercentUtil;
import cn.poco.statistics.MyBeautyStat;
import cn.poco.statistics.TongJi2;
import cn.poco.tianutils.ShareData;
import cn.poco.utils.OnAnimationClickListener;
import my.beautyCamera.R;

/**
 * Created by: fwc
 * Date: 2016/11/30
 */
public class ControlPanel extends FrameLayout {

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
	private ColorSeekBar mSeekBar;

	private CirclePanel mCirclePanel;
	private int mCircleMarginTop;

	private float mMinRadius;
	private float mMaxRadius;

	private MyStatusButton mStatusButton;

	private OnSeekBarChangeListener mListener;

	private OnPanelChangeListener mOnPanelChangeListener;

	private boolean mDown = false;
	private Animator mDownAnimator;
	private Animator mUpAnimator;

	private boolean mUiEnable = true;

	private static final int DEFAULT_PROGRESS = 25;

	public ControlPanel(Context context) {
		super(context);

		mContext = context;

		initDatas();
		initViews();
	}

	private void initDatas() {
		mTopBarHeight = ShareData.PxToDpi_xhdpi(88);
		mContentHeight = ShareData.PxToDpi_xhdpi(234);

		mSeekBarHeight = ShareData.PxToDpi_xhdpi(50);
//		mSeekBarMargin = ShareData.PxToDpi_xhdpi(80);
		mSeekBarMargin = PercentUtil.WidthPxToPercent(80);

//		mMinRadius = ShareData.PxToDpi_xhdpi(30);
		mMinRadius = mMaxRadius = ShareData.PxToDpi_xhdpi(55);

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
			mStatusButton.setData(R.drawable.beautify_remove_acne, getResources().getString(R.string.remove_acne));
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

					if (mDown) {
						TongJi2.AddCountByRes(mContext, R.integer.修图_美颜美形_祛痘_展开bar);
						MyBeautyStat.onClickByRes(R.string.美颜美图_祛痘页_主页面_展开bar);
						if (mOnPanelChangeListener != null) {
							mOnPanelChangeListener.onChanged(false, mUpAnimator);
						}
					} else {
						TongJi2.AddCountByRes(mContext, R.integer.修图_美颜美形_祛痘_收回bar);
						MyBeautyStat.onClickByRes(R.string.美颜美图_祛痘页_主页面_收回bar);
						if (mOnPanelChangeListener != null) {
							mOnPanelChangeListener.onChanged(true, mDownAnimator);
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
			mSeekBar.setMax(95);
			mSeekBar.setProgress(DEFAULT_PROGRESS);
			if (mListener != null) {
				mListener.onChanged(DEFAULT_PROGRESS);
			}
			mSeekBar.setOnSeekBarChangeListener(mSeekBarListener);
			params1 = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			params1.gravity = Gravity.CENTER_VERTICAL;
			params1.leftMargin = params1.rightMargin = mSeekBarMargin;

			mContentLayout.addView(mSeekBar, params1);
		}

		mCirclePanel = new CirclePanel(mContext);
		params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		addView(mCirclePanel, params);

		mDownAnimator = ObjectAnimator.ofFloat(this, "translationY", 0, ShareData.PxToDpi_xhdpi(232));
		mDownAnimator.setDuration(300);
		mDownAnimator.addListener(new AnimatorListenerAdapter() {

			@Override
			public void onAnimationStart(Animator animation) {
				mStatusButton.setBtnStatus(true, true);
				mDown = true;
			}
		});

		mUpAnimator = ObjectAnimator.ofFloat(this, "translationY", ShareData.PxToDpi_xhdpi(232), 0);
		mUpAnimator.setDuration(300);
		mUpAnimator.addListener(new AnimatorListenerAdapter() {

			@Override
			public void onAnimationStart(Animator animation) {
				mStatusButton.setBtnStatus(true, false);
				mDown = false;
			}
		});
	}

	/**
	 * 获取当前进度
	 */
	public int getProgress() {
		int progress = 0;
		if (mSeekBar != null) {
			progress = mSeekBar.getProgress();
		}

		return progress;
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

	public void setOnPanelChangeListener(OnPanelChangeListener onPanelChangeListener) {
		mOnPanelChangeListener = onPanelChangeListener;
	}

	public void setOnSeekBarChangeListener(OnSeekBarChangeListener listener) {
		mListener = listener;
	}

	private ColorSeekBar.OnSeekBarChangeListener mSeekBarListener = new ColorSeekBar.OnSeekBarChangeListener() {

		@Override
		public void onProgressChanged(ColorSeekBar seekBar, int progress) {
			showCircle(seekBar, progress);
			if (mListener != null) {
				mListener.onChanged(progress);
			}
		}

		@Override
		public void onStartTrackingTouch(ColorSeekBar seekBar) {

			TongJi2.AddCountByRes(mContext, R.integer.修图_美颜美形_祛痘_滑动杆);
			showCircle(seekBar, seekBar.getProgress());
		}

		@Override
		public void onStopTrackingTouch(ColorSeekBar seekBar) {
			mCirclePanel.hide();
		}
	};

	public interface OnSeekBarChangeListener {
		void onChanged(int progress);
	}

	public interface OnPanelChangeListener {
		void onChanged(boolean down, Animator animator);
	}

	private void showCircle(ColorSeekBar seekBar, int progress) {
		float radius = progress / 95f * (mMaxRadius - mMinRadius) + mMinRadius;
		int seekBarWidth = seekBar.getWidth();
		float circleX = mSeekBarHeight / 2 + mSeekBarMargin + progress / 95f * (seekBarWidth - mSeekBarHeight);
		float circleY = mCircleMarginTop;
		mCirclePanel.change(circleX, circleY, radius);
		mCirclePanel.setText(String.valueOf(progress+5));
		mCirclePanel.show();
	}

	public void setUiEnable(boolean enable) {
		mUiEnable = enable;
		mSeekBar.setEnabled(enable);
	}
}
