package cn.poco.home.site;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.view.View;

import java.util.HashMap;

import cn.poco.framework.AnimatorHolder;
import cn.poco.framework.BaseSite;
import cn.poco.framework.IPage;
import cn.poco.framework.MyFramework;
import cn.poco.framework.SiteID;
import cn.poco.home.IntroPage2;

/**
 * 正常第一次启动
 */
public class IntroPage2Site extends BaseSite
{
	private static final String TAG = "IntroPage2Site";
	public IntroPage2Site()
	{
		super(SiteID.INTRO_PAGE);
	}

	@Override
	public IPage MakePage(Context context)
	{
		return new IntroPage2(context, this);
	}

	public void OnNext(Context context)
	{
//		MyFramework.SITE_Open(PocoCamera.main, true, HomePageSite.class, null, Framework2.ANIM_NONE);
		HashMap<String,Object> data = new HashMap<>();
		data.put("delay",true);
		MyFramework.SITE_Open(context, true, HomePageSite.class, data, new AnimatorHolder()
		{
			@Override
			public void doAnimation(final View oldView, final View newView, final AnimatorListener lst)
			{
				ValueAnimator valueAnimator = ValueAnimator.ofFloat(1f, 0f);
				valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
				{
					@Override
					public void onAnimationUpdate(ValueAnimator animation)
					{
						float f = (float)animation.getAnimatedValue();
						oldView.setAlpha(f);
						oldView.setScaleX(1.5f-f*0.5f);
						oldView.setScaleY(1.5f-f*0.5f);
						newView.setAlpha(1-f);
					}
				});
				valueAnimator.setDuration(800);
				valueAnimator.addListener(new AnimatorListenerAdapter()
				{
					@Override
					public void onAnimationEnd(Animator animation)
					{
						lst.OnAnimationEnd();
					}
				});
				valueAnimator.start();
			}
		});
	}
}
