package cn.poco.album.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import cn.poco.tianutils.ShareData;
import my.beautyCamera.R;

/**
 * Created by: fwc
 * Date: 2016/12/16
 */
public class SelectedItemView extends FrameLayout {

	public ImageView image;
	public ImageView delete;

	private OnClickListener mListener;

	private boolean mDown = false;

	public SelectedItemView(Context context) {
		super(context);
		initViews();
	}

	private void initViews() {

		LayoutParams params;

		image = new ImageView(getContext());
		image.setScaleType(ImageView.ScaleType.CENTER_CROP);
		params = new LayoutParams(ShareData.PxToDpi_xhdpi(170), ShareData.PxToDpi_xhdpi(170));
		params.leftMargin = params.topMargin = ShareData.PxToDpi_xhdpi(20);
		addView(image, params);

		delete = new ImageView(getContext());
		delete.setImageResource(R.drawable.album_puzzle_delete);
		params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		addView(delete, params);
	}

	@Override
	public void setOnClickListener(OnClickListener l) {
		setClickable(true);
		mListener = l;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if (mDown) {
					break;
				}
				float x = event.getX();
				float y = event.getY();
				RectF rectF = new RectF(0, 0, delete.getWidth(), delete.getHeight());
				if (rectF.contains(x, y)) {
					mDown = true;
					animate().scaleX(0.95f).scaleY(0.95f).setDuration(50);
				}

				break;
			case MotionEvent.ACTION_UP:
				x = event.getX();
				y = event.getY();
				rectF = new RectF(0, 0, delete.getWidth(), delete.getHeight());
				if (mDown && rectF.contains(x, y)) {
					animate().scaleX(1f).scaleY(1f).setDuration(50).setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							if (mListener != null) {
								mListener.onClick(SelectedItemView.this);
							}
						}
					});
				}
			case MotionEvent.ACTION_CANCEL:
				animate().scaleX(1f).scaleY(1f).setDuration(100);
				mDown = false;
				break;
		}

		return super.onTouchEvent(event);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, widthMeasureSpec);
	}
}
