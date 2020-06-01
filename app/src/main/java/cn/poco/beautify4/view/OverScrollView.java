package cn.poco.beautify4.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.HorizontalScrollView;

import cn.poco.tianutils.ShareData;

/**
 * Created by: fwc
 * Date: 2017/1/18
 */
public class OverScrollView extends HorizontalScrollView {

	private float mMaxOverScrollX;

	private static final float SCROLL_RATIO = 0.5f;
	private static final int TIME = 200;

	private boolean mOverScroll = false;

	private boolean mDoingAnimation = false;

	private View mContentView;

	private float mLastX;

	private int flag = 0;

	public OverScrollView(Context context) {
		super(context);

		mMaxOverScrollX = 50 * ShareData.m_resScale + 0.5f;
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		mContentView = getChildAt(0);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {

		switch (ev.getAction()) {
			case MotionEvent.ACTION_MOVE:

				float x = ev.getX();
				float delta = x - mLastX;
				if (((flag == 1 && delta > 0) || (flag == 2 && delta < 0)) &&
				mOverScroll && !mDoingAnimation) {

					float moveDistance = delta * SCROLL_RATIO;

					if (flag == 1) {
						if (moveDistance > mMaxOverScrollX) {
							moveDistance = mMaxOverScrollX;
						}
					} else {
						if (moveDistance < -mMaxOverScrollX) {
							moveDistance = -mMaxOverScrollX;
						}
					}

					if (mContentView == null) {
						mContentView = getChildAt(0);
					}
					mContentView.setTranslationX(moveDistance);

				} else if ((getScrollX() <= 0 || getScrollX() >= getScrollRange()) &&
						!mOverScroll && !mDoingAnimation) {

					if (getScrollX() <= 0) {
						flag = 1;
					} else if (getScrollX() >= getScrollRange()){
						flag = 2;
					}
					mOverScroll = true;
					mLastX = ev.getX();
				} else {
					mOverScroll = false;
					flag = 0;
				}
				break;
			case MotionEvent.ACTION_UP:
				if (mOverScroll && (flag == 1 || flag == 2) && !mDoingAnimation) {
					mDoingAnimation = true;
					if (mContentView == null) {
						mContentView = getChildAt(0);
					}
					float translationX = mContentView.getTranslationX();
					ObjectAnimator animator = ObjectAnimator.ofFloat(mContentView, "translationX", translationX, 0);
					animator.setDuration((int)(Math.abs(translationX) / mMaxOverScrollX * TIME + 0.5f));
					animator.setInterpolator(new AccelerateInterpolator());
					animator.addListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mDoingAnimation = false;
							mOverScroll = false;
						}
					});
					animator.start();
					flag = 0;
				}
				break;
		}

		return mOverScroll || super.onTouchEvent(ev);
	}

	private int getScrollRange() {
		int scrollRange = 0;
		if (getChildCount() > 0) {
			View child = getChildAt(0);
			scrollRange = Math.max(0,
								   child.getWidth() - (getWidth() - getPaddingLeft() - getPaddingRight()));
		}
		return scrollRange;
	}
}
