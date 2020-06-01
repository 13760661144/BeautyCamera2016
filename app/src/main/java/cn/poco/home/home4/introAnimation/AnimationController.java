package cn.poco.home.home4.introAnimation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;


/**
 * Created by lgd on 2016/11/12.
 */

public class AnimationController
{
	private final View mView;
	private  AnimatorSet mAnimSet;
	private FadeOutAnimator fadeOutAnimator;
	private SegregateAnimator segregateAnimator;
	private DropAnimator dropAnimator;
	private ArchAnimator archAnimator;
	private FadeInAnimator fadeInAnimator;
	private final static int ADVANCE_DROP = 100;
	private final static int DELAY_FADE_IN = 200;

	private boolean isDrawFadeOut = false;
	private boolean isDrawFadeIn = false;
	private boolean isDrawSegregate = false;
	private boolean isDrawDrop = false;
	private boolean isDrawArc = false;

	public AnimationController(View view)
	{
		mView = view;
//		initConfig();
		initAnimation();
	}

	private void initAnimation()
	{
		mAnimSet = new AnimatorSet();
		fadeOutAnimator = new FadeOutAnimator(mView);
		fadeOutAnimator.addListener(new AnimatorListenerAdapter()
		{
			@Override
			public void onAnimationStart(Animator animation)
			{
				isDrawFadeOut = true;
			}

			@Override
			public void onAnimationEnd(Animator animation)
			{

			}
		});
		segregateAnimator = new SegregateAnimator(mView);
		segregateAnimator.setStartDelay(FadeOutAnimator.DURATION );
		segregateAnimator.addListener(new AnimatorListenerAdapter()
		{

			@Override
			public void onAnimationStart(Animator animation)
			{
				isDrawFadeOut = false;
				isDrawSegregate = true;
				isDrawDrop = true;
			}
			@Override
			public void onAnimationEnd(Animator animation)
			{

			}
		});

		fadeInAnimator = new FadeInAnimator(mView);
		fadeInAnimator.setStartDelay(FadeOutAnimator.DURATION + SegregateAnimator.DURATION +DELAY_FADE_IN);
		fadeInAnimator.addListener(new AnimatorListenerAdapter()
		{
			@Override
			public void onAnimationStart(Animator animation)
			{
				isDrawFadeIn = true;
				isDrawSegregate = false;
				if(animationCallBack!=null){
					animationCallBack.onCirCleFadeInStart();
				}
			}
			@Override
			public void onAnimationEnd(Animator animation)
			{
				isDrawFadeIn = false;
			}
		});
		dropAnimator = new DropAnimator(mView);
		dropAnimator.setStartDelay(FadeOutAnimator.DURATION + SegregateAnimator.DURATION -ADVANCE_DROP);
		dropAnimator.addListener(new AnimatorListenerAdapter()
		{
			@Override
			public void onAnimationStart(Animator animation)
			{
				isDrawDrop = true;
			}

			@Override
			public void onAnimationEnd(Animator animation)
			{

			}
		});

		archAnimator = new ArchAnimator(mView);
		archAnimator.setStartDelay(FadeOutAnimator.DURATION + SegregateAnimator.DURATION + DropAnimator.DURATION);
		archAnimator.addListener(new AnimatorListenerAdapter()
		{
			@Override
			public void onAnimationStart(Animator animation)
			{
				isDrawDrop = false;
				isDrawArc = true;
				if(animationCallBack!=null){
					animationCallBack.onArchFadeStart();
				}
			}
			@Override
			public void onAnimationEnd(Animator animation)
			{
				isDrawArc = false;
			}
		});

		fadeInAnimator.setFadeInCallBack(new FadeInAnimator.FadeInCallBack()
		{
			@Override
			public void fadeIn(float alpha)
			{
				if(animationCallBack!=null){
					animationCallBack.onCirCleFadeIn(alpha);
				}
			}
		});
		archAnimator.setFadeInCallBack(new ArchAnimator.FadeInCallBack()
		{
			@Override
			public void fadeIn(float alpha, float scale)
			{
				if(animationCallBack!=null){
					animationCallBack.onArchFadeIn(alpha,scale);
				}
			}
		});
		mAnimSet.addListener(new AnimatorListenerAdapter()
		{
			@Override
			public void onAnimationStart(Animator animation)
			{
				if(animationCallBack!=null){
					animationCallBack.onAnimationStart();
				}
			}

			@Override
			public void onAnimationEnd(Animator animation)
			{
				if(animationCallBack!=null){
					animationCallBack.onAnimationEnd();
				}
			}
		});
		mAnimSet.playTogether(fadeOutAnimator, segregateAnimator, dropAnimator, fadeInAnimator, archAnimator);
	}
	public void draw(Canvas canvas, Paint mPaint)
	{
		if(isDrawFadeOut)
		{
			fadeOutAnimator.draw(canvas, mPaint);
		}
		if(isDrawFadeIn)
		{
			fadeInAnimator.draw(canvas, mPaint);
		}
		if(isDrawSegregate)
		{
			segregateAnimator.draw(canvas, mPaint);
		}
		if(isDrawDrop)
		{
			dropAnimator.draw(canvas, mPaint);
		}
		if(isDrawArc)
		{
			archAnimator.draw(canvas, mPaint);
		}
	}
	private AnimationCallBack animationCallBack;
	public interface AnimationCallBack{
		void onAnimationStart();
		void onAnimationEnd();
		void onCirCleFadeInStart();
		void onCirCleFadeIn(float alpha);
		void onArchFadeIn(float alpha, float scale);
		void onArchFadeStart();
	}
	public void setAnimationCallBack(AnimationCallBack animationCallBack)
	{
		this.animationCallBack = animationCallBack;
	}
	public void startAnimation()
	{
		mAnimSet.start();
	}
}
