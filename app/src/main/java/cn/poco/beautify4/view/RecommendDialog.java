package cn.poco.beautify4.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.poco.advanced.ImageUtils;
import cn.poco.tianutils.ShareData;
import cn.poco.utils.OnAnimationClickListener;
import my.beautyCamera.R;

/**
 * Created by: fwc
 * Date: 2017/4/7
 */
public class RecommendDialog extends RelativeLayout {

	private Context mContext;

	private ViewGroup mParent;

	private boolean mShow = false;

	private View mBackground;

	private FrameLayout mContent;
	private FrameLayout mPositiveLayout;
	private TextView mPositive;

	private TextView mNegative;

	private LinearLayout mTipLayout;
	private ImageView mTipSelect;

	private int mRadius;

	private boolean mCheck = false;

	public RecommendDialog(@NonNull Context context) {
		super(context);

		mContext = context;
		mRadius = ShareData.PxToDpi_xhdpi(30);

		initViews();
	}

	private void initViews() {

		setClickable(true);

		LayoutParams params;

		mBackground = new View(mContext);
		mBackground.setBackgroundColor(0x99000000);
		params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		addView(mBackground, params);

		mContent = new FrameLayout(mContext);
		mContent.setId(R.id.pintu_content);
		mContent.setClickable(true);
//		DrawableUtils.setBackground(mContent, DrawableUtils.shapeDrawable(0xfff7d4d6, mRadius));
		params = new LayoutParams(ShareData.PxToDpi_xhdpi(568), ShareData.PxToDpi_xhdpi(784));
		params.addRule(RelativeLayout.CENTER_IN_PARENT);
		addView(mContent, params);
		{
			FrameLayout.LayoutParams params1;

			ImageView imageView = new ImageView(mContext);
			imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
			Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.beautify4page_pintu);
			imageView.setImageBitmap(cn.poco.tianutils.ImageUtils.MakeRoundBmp(bitmap, mRadius));
			params1 = new FrameLayout.LayoutParams(ShareData.PxToDpi_xhdpi(568), ShareData.PxToDpi_xhdpi(568));
			mContent.addView(imageView, params1);

			FrameLayout bottom = new FrameLayout(mContext);
			bottom.setBackgroundResource(R.drawable.display_bottom_bg);
			params1 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(253));
			params1.gravity = Gravity.BOTTOM;
			mContent.addView(bottom, params1);
			{
				mPositiveLayout = new FrameLayout(mContext);
				params1 = new FrameLayout.LayoutParams(ShareData.PxToDpi_xhdpi(450), ShareData.PxToDpi_xhdpi(78));
				params1.topMargin = ShareData.PxToDpi_xhdpi(40);
				params1.gravity = Gravity.CENTER_HORIZONTAL;
				bottom.addView(mPositiveLayout, params1);
				{
					ImageView background = new ImageView(mContext);
					background.setImageResource(R.drawable.album_dialog_button_normal);
					ImageUtils.AddSkin(mContext, background);
					params1 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
					mPositiveLayout.addView(background, params1);

					mPositive = new TextView(mContext);
					mPositive.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
					mPositive.setTextColor(Color.WHITE);
					mPositive.getPaint().setFakeBoldText(true);
					params1 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
					params1.gravity = Gravity.CENTER;
					mPositiveLayout.addView(mPositive, params1);
				}

				FrameLayout negativeLayout = new FrameLayout(mContext);
				params1 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(90));
				params1.gravity = Gravity.BOTTOM;
				bottom.addView(negativeLayout, params1);
				{
					mNegative = new TextView(mContext);
					mNegative.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
					mNegative.setTextColor(0xff999999);
					mNegative.setPadding(ShareData.PxToDpi_xhdpi(32), ShareData.PxToDpi_xhdpi(16),
										 ShareData.PxToDpi_xhdpi(32), ShareData.PxToDpi_xhdpi(16));
					mNegative.setGravity(Gravity.CENTER);
					params1 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
					params1.gravity = Gravity.CENTER;
					negativeLayout.addView(mNegative, params1);
				}
			}
		}

		mTipLayout = new LinearLayout(mContext);
		params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.BELOW, mContent.getId());
		params.addRule(RelativeLayout.CENTER_HORIZONTAL);
		params.topMargin = ShareData.PxToDpi_xhdpi(40);
		addView(mTipLayout, params);
		{
			LinearLayout.LayoutParams params1;

			mTipSelect = new ImageView(mContext);
			mTipSelect.setImageResource(R.drawable.tip_unselected);
			params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			params1.gravity = Gravity.CENTER_VERTICAL;
			mTipLayout.addView(mTipSelect, params1);

			TextView tip = new TextView(mContext);
			tip.setIncludeFontPadding(false);
			tip.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
			tip.setTextColor(Color.WHITE);
			tip.getPaint().setFakeBoldText(true);
			tip.setText(R.string.no_longer_tips);
			params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			params1.gravity = Gravity.CENTER_VERTICAL;
			params1.leftMargin = ShareData.PxToDpi_xhdpi(12);
			mTipLayout.addView(tip, params1);
		}
	}

	public void show(ViewGroup parent) {
		if (mParent == null) {
			mShow = true;
			mParent = parent;
			ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
			parent.addView(this, params);

			AnimatorSet set = new AnimatorSet();
			ObjectAnimator animator1 = ObjectAnimator.ofFloat(mContent, "translationY", -ShareData.getScreenH(), 0);
			ObjectAnimator animator2 = ObjectAnimator.ofFloat(mBackground, "alpha", 0, 1);
			ObjectAnimator animator3 = ObjectAnimator.ofFloat(mTipLayout, "translationY", -ShareData.getScreenH(), 0);
			set.playTogether(animator1, animator2, animator3);
			set.setDuration(400);
			set.start();
		}
	}

	public void dismiss() {
		if (mParent != null) {
			mShow = false;

			AnimatorSet set = new AnimatorSet();
			ObjectAnimator animator1 = ObjectAnimator.ofFloat(mContent, "translationY", 0, ShareData.getScreenH());
			ObjectAnimator animator2 = ObjectAnimator.ofFloat(mBackground, "alpha", 1, 0);
			ObjectAnimator animator3 = ObjectAnimator.ofFloat(mTipLayout, "translationY", 0, ShareData.getScreenH());
			set.playTogether(animator1, animator2, animator3);
			set.setDuration(400);
			set.addListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					mParent.removeView(RecommendDialog.this);
					mParent = null;
				}
			});
			set.start();
		}
	}

	public boolean isShow() {
		return mShow;
	}

	public RecommendDialog setPositive(@StringRes int text, final View.OnClickListener listener) {
		mPositive.setText(text);
		mPositiveLayout.setOnTouchListener(new OnAnimationClickListener() {
			@Override
			public void onAnimationClick(View v) {
				if (listener != null) {
					listener.onClick(v);
				}
			}

			@Override
			public void onTouch(View v) {

			}

			@Override
			public void onRelease(View v) {

			}
		});

		return this;
	}

	public RecommendDialog setNegative(@StringRes int text, final View.OnClickListener listener) {
		mNegative.setText(text);
		mNegative.setOnTouchListener(new OnAnimationClickListener() {
			@Override
			public void onAnimationClick(View v) {
				if (listener != null) {
					listener.onClick(v);
				}
			}

			@Override
			public void onTouch(View v) {

			}

			@Override
			public void onRelease(View v) {

			}
		});

		return this;
	}

	public RecommendDialog setDialogBackgroungListener(View.OnClickListener onClickListener) {
		mBackground.setOnClickListener(onClickListener);
		return this;
	}

	public RecommendDialog setOnCheckChangeListener(final OnCheckChangeListener listener) {

		mTipLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mCheck) {
					mCheck = false;
					mTipSelect.setImageResource(R.drawable.tip_unselected);
				} else {
					mCheck = true;
					mTipSelect.setImageResource(R.drawable.tip_selected);
				}

				if (listener != null) {
					listener.onCheck(v, mCheck);
				}
			}
		});

		return this;
	}

	public RecommendDialog showTip(boolean show) {
		if (show) {
			mTipLayout.setVisibility(VISIBLE);
		} else {
			mTipLayout.setVisibility(GONE);
		}

		return this;
	}

	public interface OnCheckChangeListener {
		void onCheck(View v, boolean check);
	}
}
