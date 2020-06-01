package cn.poco.acne.view;

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
import android.widget.LinearLayout;

import cn.poco.tianutils.ShareData;
import cn.poco.utils.OnAnimationClickListener;
import my.beautyCamera.R;

/**
 * Created by: fwc
 * Date: 2016/12/5
 */
public class UndoPanel extends LinearLayout {

	private FrameLayout mUndoLayout;
	private FrameLayout mRedoLayout;

	private ImageView mUndoView;
	private ImageView mRedoView;

	private Callback mCallback;

	private boolean mCanUndo;
	private boolean mCanRedo;

	private boolean mShow = false;

	private AnimatorSet mShowAnimator;
	private AnimatorSet mHideAnimator;

	public UndoPanel(Context context) {
		super(context);

		initViews();
	}

	public void setCallback(Callback callback) {
		mCallback = callback;
	}

	private void initViews() {

		setOrientation(HORIZONTAL);

		setClickable(true);

		LayoutParams params;

		mUndoLayout = new FrameLayout(getContext());
		mUndoLayout.setBackgroundResource(R.drawable.beautify_white_circle_bg);
		params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		addView(mUndoLayout, params);
		{
			FrameLayout.LayoutParams params1 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			params1.gravity = Gravity.CENTER;
			mUndoView = new ImageView(getContext());
			mUndoView.setAlpha(0.4f);
			mUndoView.setImageResource(R.drawable.beautify_undo);
			mUndoLayout.addView(mUndoView, params1);
		}

		mRedoLayout = new FrameLayout(getContext());
		mRedoLayout.setBackgroundResource(R.drawable.beautify_white_circle_bg);
		params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		params.leftMargin = ShareData.PxToDpi_xhdpi(6);
		addView(mRedoLayout, params);
		{
			FrameLayout.LayoutParams params1 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			params1.gravity = Gravity.CENTER;
			mRedoView = new ImageView(getContext());
			mRedoView.setAlpha(0.4f);
			mRedoView.setImageResource(R.drawable.beautify_redo);
			mRedoLayout.addView(mRedoView, params1);
		}

		mUndoLayout.setOnTouchListener(new OnAnimationClickListener() {
			@Override
			public void onAnimationClick(View v) {
				if (mCallback != null) {
					mCallback.onUndo();
				}
			}

			@Override
			public void onTouch(View v) {

			}

			@Override
			public void onRelease(View v) {

			}
		});

		mRedoLayout.setOnTouchListener(new OnAnimationClickListener() {
			@Override
			public void onAnimationClick(View v) {
				if (mCallback != null) {
					mCallback.onRedo();
				}
			}

			@Override
			public void onTouch(View v) {

			}

			@Override
			public void onRelease(View v) {

			}
		});

		mUndoView.setEnabled(false);
		mRedoView.setEnabled(false);

		initAnimators();
	}

	private void initAnimators() {
		mShowAnimator = new AnimatorSet();
		ObjectAnimator animator1 = ObjectAnimator.ofFloat(mUndoLayout, "scaleX", 0, 1);
		ObjectAnimator animator2 = ObjectAnimator.ofFloat(mUndoLayout, "scaleY", 0, 1);
		ObjectAnimator animator3 = ObjectAnimator.ofFloat(mRedoLayout, "scaleX", 0, 1);
		ObjectAnimator animator4 = ObjectAnimator.ofFloat(mRedoLayout, "scaleY", 0, 1);
		mShowAnimator.play(animator1).with(animator2).with(animator3).with(animator4);
		mShowAnimator.setDuration(100);
		mShowAnimator.addListener(new AnimatorListenerAdapter() {

			@Override
			public void onAnimationStart(Animator animation) {
				setVisibility(VISIBLE);
			}
		});

		mHideAnimator = new AnimatorSet();
		animator1 = ObjectAnimator.ofFloat(mUndoLayout, "scaleX", 1, 0);
		animator2 = ObjectAnimator.ofFloat(mUndoLayout, "scaleY", 1, 0);
		animator3 = ObjectAnimator.ofFloat(mRedoLayout, "scaleX", 1, 0);
		animator4 = ObjectAnimator.ofFloat(mRedoLayout, "scaleY", 1, 0);
		mHideAnimator.play(animator1).with(animator2).with(animator3).with(animator4);
		mHideAnimator.setDuration(100);
		mHideAnimator.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				setVisibility(INVISIBLE);
			}
		});
	}

	public boolean isCanUndo() {
		return mCanUndo;
	}

	public void setCanUndo(boolean canUndo) {
		mCanUndo = canUndo;
		mUndoView.setEnabled(canUndo);
		if (canUndo) {
			mUndoView.setAlpha(1f);
		} else {
			mUndoView.setAlpha(0.4f);
		}
	}

	public void show() {
		if (mHideAnimator.isRunning()) {
			mHideAnimator.end();
		}
		if (mShowAnimator.isRunning()) {
			return;
		}

		if (!mShow) {
			mShow = true;
			mShowAnimator.start();
		} else {
			mUndoLayout.setScaleX(1);
			mUndoLayout.setScaleY(1);
			mRedoLayout.setScaleX(1);
			mRedoLayout.setScaleY(1);
			setVisibility(VISIBLE);
		}
	}

	public void hide() {
		if (mShowAnimator.isRunning()) {
			mShowAnimator.end();
		}
		if (mHideAnimator.isRunning()) {
			return;
		}

		if (mShow) {
			mShow = false;
			mHideAnimator.start();
		} else {
			mUndoLayout.setScaleX(0);
			mUndoLayout.setScaleY(0);
			mRedoLayout.setScaleX(0);
			mRedoLayout.setScaleY(0);
			setVisibility(INVISIBLE);
		}
	}

	public boolean isCanRedo() {
		return mCanRedo;
	}

	public void setCanRedo(boolean canRedo) {
		mCanRedo = canRedo;
		mRedoView.setEnabled(canRedo);
		if (canRedo) {
			mRedoView.setAlpha(1f);
		} else {
			mRedoView.setAlpha(0.4f);
		}
	}

	public interface Callback {
		void onUndo();
		void onRedo();
	}
}
