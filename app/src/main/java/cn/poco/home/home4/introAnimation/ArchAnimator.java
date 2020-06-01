package cn.poco.home.home4.introAnimation;

import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;
import android.view.animation.DecelerateInterpolator;


/**
 * Created by lgd on 2016/11/12.
 */

public class ArchAnimator extends ValueAnimator
{
	public final static int DURATION = Config.DURATION_ARCH;
	private final int archMaxHeight;
	private View mView;
	private int archHeight;
	private int archWidth;
	private float ratio;
	private int centerX;
	private int centerY;
	private int alpha;
	private Paint mPaint;
	private int mCurRadius;
	private final int mOriginRadius;

	public ArchAnimator(View view)
	{
		mView = view;
		alpha = 255;
		archHeight = Config.ARCH_HEIGHT;
		archMaxHeight = Config.ARCH_MAX_HEIGHT;
		archWidth = Config.ARCH_WIDTH;
		//（x-B长度）平方 +（A长度的一半）平方 = x平方
		int radius = (archWidth * archWidth + archHeight * archHeight) / (archHeight*2);
		mOriginRadius = radius - archHeight;
		centerY = Config.ARCH_CENTER_Y + mOriginRadius;
		centerX = Config.ARCH_CENTER_X;
		ratio = archWidth / archHeight;
		initAnim();
	}

	boolean isReBound = false;

	private void initAnim()
	{
		this.setDuration(DURATION);
		this.setInterpolator(new DecelerateInterpolator());
		mCurRadius = mOriginRadius;
		this.setIntValues(mOriginRadius, mOriginRadius + archMaxHeight, mOriginRadius + archHeight);
		this.addUpdateListener(new AnimatorUpdateListener()
		{
			int curRadius;
			int lastRadius;

			float scale;

			@Override
			public void onAnimationUpdate(ValueAnimator animation)
			{
				lastRadius = curRadius;
				curRadius = (int)animation.getAnimatedValue();
				mCurRadius = curRadius;
				if(curRadius < lastRadius || curRadius == mOriginRadius + archMaxHeight)
				{
					//回弹
					alpha = (curRadius - mOriginRadius - archHeight) * 255 / (archMaxHeight - archHeight);
					scale = ((curRadius-mOriginRadius)*1.0f / archHeight);
					if(fadeInCallBack != null)
					{
						fadeInCallBack.fadeIn(alpha*1.0f/255,scale);
					}
				}
				mView.postInvalidate();
			}
		});
	}

	public void draw(Canvas canvas, Paint paint)
	{
		if(mPaint == null)
		{
			mPaint = new Paint(paint);
		}
		mPaint.setAlpha(alpha);
		canvas.drawCircle(centerX, centerY, mCurRadius, mPaint);
	}

	private FadeInCallBack fadeInCallBack;

	interface FadeInCallBack
	{
		void fadeIn(float alpha, float scale);
	}

	public void setFadeInCallBack(FadeInCallBack fadeInCallBack)
	{
		this.fadeInCallBack = fadeInCallBack;
	}
}
