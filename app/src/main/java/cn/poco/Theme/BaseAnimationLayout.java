package cn.poco.Theme;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;

/**
 * Created by lgd on 2016/11/29.
 */
@Deprecated
public class BaseAnimationLayout extends FrameLayout
{
	protected boolean mUiEnabled = true;
	protected ValueAnimator clickAnimator;     //点击动画
	protected ValueAnimator touchAnimator;     //触摸动画
	protected ValueAnimator restoreAnimator;     //还原动画
	protected float scaleSmall = 0.95f;
	protected float scaleBig = 1.05f;
	private float[] scales;
	protected int clickDuration = 700;
	protected int touchDuration = 400;
	protected Rect clickRect;

	public BaseAnimationLayout(Context context)
	{
		super(context);
		init();
	}

	public BaseAnimationLayout(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init();
	}

	public BaseAnimationLayout(Context context, AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
		init();
	}

	private void init()
	{
		scales = new float[]{scaleSmall, scaleBig, 1f};
		clickAnimator = ValueAnimator.ofFloat(scales);
		clickAnimator.setInterpolator(new DecelerateInterpolator());
		clickAnimator.setDuration(clickDuration);
		clickAnimator.addUpdateListener(updateListener);
		clickAnimator.addListener(animatorListenerAdapter);

		touchAnimator = ValueAnimator.ofFloat(1, scaleSmall);
		touchAnimator.setDuration(touchDuration);
		touchAnimator.addUpdateListener(updateListener);

		restoreAnimator = ValueAnimator.ofFloat(scaleSmall, 1f);
		restoreAnimator.setDuration(touchDuration);
		restoreAnimator.addUpdateListener(updateListener);

	}

	private AnimatorListenerAdapter animatorListenerAdapter = new AnimatorListenerAdapter()
	{
		@Override
		public void onAnimationStart(Animator animation)
		{
			mUiEnabled = false;
		}

		@Override
		public void onAnimationEnd(Animator animation)
		{
			if(onClickListener != null)
			{
				onClickListener.onClick(BaseAnimationLayout.this);
			}
			mUiEnabled = true;
		}
	};

	private ValueAnimator.AnimatorUpdateListener updateListener = new ValueAnimator.AnimatorUpdateListener()
	{

		@Override
		public void onAnimationUpdate(ValueAnimator animation)
		{
			float cVal = (Float)animation.getAnimatedValue();
			BaseAnimationLayout.this.setScaleX(cVal);
			BaseAnimationLayout.this.setScaleY(cVal);
		}
	};

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		if(mUiEnabled)
		{
			switch(event.getAction())
			{
				case MotionEvent.ACTION_DOWN:
					clickRect = new Rect(0, 0, getWidth(), getHeight());
					touchAnimator.start();
					break;
				case MotionEvent.ACTION_MOVE:
					break;
				case MotionEvent.ACTION_UP:
					if(isInClickArea((int)event.getX(), (int)event.getY()))
					{
						touchAnimator.cancel();
						scales[0] = getScaleX();
						clickAnimator.setFloatValues(scales);
						clickAnimator.start();
						break;
					}
				case MotionEvent.ACTION_CANCEL:
					touchAnimator.cancel();
					restoreAnimator.setDuration((int)(touchDuration * (1 - getScaleX()) / (1 - scaleSmall)));
					restoreAnimator.setFloatValues(getScaleX(), 1);
					restoreAnimator.start();
					break;
				default:
					return super.onTouchEvent(event);
			}
			return true;
		}
		return super.onTouchEvent(event);
	}

	protected Boolean isInClickArea(int x, int y)
	{
		return clickRect.contains(x,y);
	}

	private OnClickListener onClickListener;

	@Override
	public void setOnClickListener(OnClickListener l)
	{
		this.onClickListener = l;
	}

	public void setTouchDuration(int touchDuration)
	{
		this.touchDuration = touchDuration;
		touchAnimator.setDuration(touchDuration);
		restoreAnimator.setDuration(touchDuration);
	}

	public void setClickDuration(int clickDuration)
	{
		this.clickDuration = clickDuration;
		clickAnimator.setDuration(clickDuration);
	}

	public void setTouchScale(float scale)
	{
		this.scaleSmall = scale;
		touchAnimator.setFloatValues(1, scale);
		restoreAnimator.setFloatValues(scale, 1);
	}

	/**
	 * 点击缩放变化值，  scale[0] 动态获取
	 *
	 * @param clickScales
	 */
	public void setClickScales(float[] clickScales)
	{
		scales = new float[clickScales.length + 1];
		for(int i = 1; i < clickScales.length; i++)
		{
			scales[i] = clickScales[i];
		}
	}

	public void setClickInterpolator(TimeInterpolator interpolator)
	{
		clickAnimator.setInterpolator(interpolator);
	}

}
