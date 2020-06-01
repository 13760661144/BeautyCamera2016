package cn.poco.home.home4.utils;

import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import cn.poco.utils.OnAnimationClickListener;

/**
 * Created by lgd on 2016/12/8.
 */

public abstract class OnHomeAnimationClickListener extends OnAnimationClickListener
{
	protected float scaleBig = 1.06f;
	protected float[] scales;
	protected int clickDuration = 700;

	public OnHomeAnimationClickListener()
	{
		super();
		init();
	}

	private void init()
	{
		scales = new float[]{ss_scaleSmall, scaleBig, 1 + (scaleBig - 1) / 3, 1 + (scaleBig - 1) * 2 / 3, 1f};
		ss_clickAnimator.setFloatValues(scales);
		ss_clickAnimator.setDuration(clickDuration);
		ss_updateListener = new ValueAnimator.AnimatorUpdateListener()
		{
			@Override
			public void onAnimationUpdate(ValueAnimator animation)
			{
				float scale = (Float)animation.getAnimatedValue();
				ss_view.setScaleX(scale);
				ss_view.setScaleY(scale);
				//之前乐视机的bug
//				View parent = (View)ss_view.getParent();
//				if(parent != null){
//					parent.postInvalidate();
//				}
			}
		};
	}

	@Override
	protected void startClickAnimation(View view)
	{
		scales[0] = view.getScaleX();
		ss_clickAnimator.setFloatValues(scales);
		ss_clickAnimator.setDuration(clickDuration);
		ss_clickAnimator.setInterpolator(new DecelerateInterpolator());
		ss_clickAnimator.start();
		onAnimationClickStart(view);
	}

	abstract public void onAnimationClickStart(View v);
}
