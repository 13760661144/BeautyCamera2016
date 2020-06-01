package cn.poco.pendant.view;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.FloatRange;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import cn.poco.tianutils.ShareData;
import my.beautyCamera.R;

/**
 * Created by: fwc
 * Date: 2017/2/6
 */
public class GarbageView extends FrameLayout {

	private static final int MAX_ROTATE = 45;
	private static final int ANIM_TIME = 200;

	private Context mContext;

	private ImageView mGarbageCanBung;

	private float mProgress;

	public GarbageView(Context context) {
		super(context);

		mContext = context;

		init();
	}

	private void init() {
		setBackgroundResource(R.drawable.delete_button_white_circle);

		LayoutParams params;

		mGarbageCanBung = new ImageView(mContext);
		mGarbageCanBung.setImageResource(R.drawable.garbage_can_bung);
		params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.CENTER_HORIZONTAL;
		params.topMargin = ShareData.PxToDpi_xhdpi(19);
		addView(mGarbageCanBung, params);

		ImageView imageView = new ImageView(mContext);
		imageView.setImageResource(R.drawable.garbage_can);
		params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.CENTER_HORIZONTAL;
		params.topMargin = ShareData.PxToDpi_xhdpi(72);
		addView(imageView, params);
	}

	public void setProgress(@FloatRange(from = 0, to = 100) float progress) {
		if (progress >= 0 && progress <= 100) {
			mProgress = progress;
			float rotate = mProgress / 100f * MAX_ROTATE;
			// 设置旋转中心
			mGarbageCanBung.setPivotX(mGarbageCanBung.getWidth() - ShareData.PxToDpi_xhdpi(8));
			mGarbageCanBung.setPivotY(mGarbageCanBung.getHeight());
			mGarbageCanBung.setRotation(rotate);
		}
	}

	public Animator getCloseAnimator() {
		Animator animator = ObjectAnimator.ofFloat(mGarbageCanBung, "rotation", mGarbageCanBung.getRotation(), 0);
		int duration = (int)(mGarbageCanBung.getRotation() / MAX_ROTATE) * ANIM_TIME;
		animator.setDuration(duration);
		return animator;
	}
}
