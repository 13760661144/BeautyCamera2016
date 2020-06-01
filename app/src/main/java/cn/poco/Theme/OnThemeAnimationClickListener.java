package cn.poco.Theme;

import android.view.View;
import android.view.animation.DecelerateInterpolator;

import cn.poco.utils.OnAnimationClickListener;

/**
 * Created by lgd on 2016/12/8.
 */

public abstract class OnThemeAnimationClickListener extends OnAnimationClickListener
{
	protected float scaleBig = 1.08f;
	protected float[] scales;
	protected int clickDuration = 400;

	public OnThemeAnimationClickListener()
	{
		super();
		init();
	}

	private void init()
	{
		ss_scaleSmall = 0.95f;
		scales = new float[]{ss_scaleSmall, scaleBig,1};
		ss_clickAnimator.setFloatValues(scales);
		ss_clickAnimator.setDuration(clickDuration);
	}

	@Override
	protected void startClickAnimation(View view)
	{
		onAnimationClickStart(view);
		scales[0] = view.getScaleX();
		ss_clickAnimator.setFloatValues(scales);
		ss_clickAnimator.setDuration(clickDuration);
		ss_clickAnimator.setInterpolator(new DecelerateInterpolator());
		ss_clickAnimator.start();
	}

	abstract public void onAnimationClickStart(View v);
}
