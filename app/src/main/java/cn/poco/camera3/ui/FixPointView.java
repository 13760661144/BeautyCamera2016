package cn.poco.camera3.ui;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;

import cn.poco.advanced.ImageUtils;
import cn.poco.camera3.util.AnimUtil;
import cn.poco.camera3.util.CameraPercentUtil;
import cn.poco.system.TagMgr;
import cn.poco.system.Tags;
import my.beautyCamera.R;

/**
 * 手动定点view
 * Created by Gxx on 2018/1/4.
 */

public class FixPointView extends FrameLayout
{
	private ObjectAnimator mAnim;
	private boolean mShowJitterAnim;
	private ImageView mItemView;

	public FixPointView(@NonNull Context context)
	{
		super(context);
		mShowJitterAnim = TagMgr.CheckTag(context, Tags.USE_FACE_DETECTION_FIXED_POINT);
		initView(context);
	}

	private void initView(Context context)
	{
		this.setBackgroundResource(R.drawable.beautify_white_circle_bg);

		mItemView = new ImageView(context);
		mItemView.setImageResource(R.drawable.beautify_fix_by_hand);
		FrameLayout.LayoutParams params = new LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.CENTER;
		this.addView(mItemView, params);
		ImageUtils.AddSkin(getContext(), mItemView);
	}

	public void showJitterAnimAccordingStatus()
	{
		if(mShowJitterAnim)
		{
			mAnim = AnimUtil.jitterXAnim(mItemView, CameraPercentUtil.WidthPxToPercent(4), 500, 1500, true);
			mAnim.setRepeatCount(ValueAnimator.INFINITE);
			mAnim.start();
		}
	}

	public void modifyStatus()
	{
		if(mShowJitterAnim)
		{
			mShowJitterAnim = false;
			TagMgr.SetTag(getContext(), Tags.USE_FACE_DETECTION_FIXED_POINT);
			TagMgr.Save(getContext());

			clearAnimator();
		}
	}

	public void clearAll()
	{
		clearAnimator();
		removeAllViews();
	}

	private void clearAnimator()
	{
		if(mAnim != null && mItemView != null)
		{
			mAnim.cancel();
			mAnim.removeAllListeners();
			mAnim.removeAllUpdateListeners();
			mAnim = null;
			mItemView.setTranslationX(0);
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);

		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);

		widthMeasureSpec = getMeasureSize(widthMode, widthSize);
		heightMeasureSpec = getMeasureSize(heightMode, heightSize);

		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	private int getMeasureSize(int mode, int size)
	{
		if(mode == MeasureSpec.AT_MOST)
		{
			int out = CameraPercentUtil.WidthPxToPercent(80);

			if(out > size)
			{
				return size;
			}
			return out;
		}

		return size;
	}
}
