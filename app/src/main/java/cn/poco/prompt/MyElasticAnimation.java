package cn.poco.prompt;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.BounceInterpolator;
import android.view.animation.TranslateAnimation;

public class MyElasticAnimation extends TranslateAnimation
{
    
	public MyElasticAnimation(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}
	
	public MyElasticAnimation(float fromXDelta, float toXDelta, float fromYDelta, float toYDelta) {
        super(fromXDelta, toXDelta, fromYDelta, toYDelta);
    }
	
	public MyElasticAnimation(int fromXType, float fromXValue, int toXType, float toXValue,
            int fromYType, float fromYValue, int toYType, float toYValue) {

        super(fromXType, fromXValue, toXType, toXValue, fromYType, fromYValue, toYType, toYValue);
    }

	@Override
	public void initialize(int width, int height, int parentWidth, int parentHeight)
	{
		BounceInterpolator interpolator = new BounceInterpolator();
		interpolator.getInterpolation(0.2f);
		setInterpolator(interpolator);
		super.initialize(width, height, parentWidth, parentHeight);
	}

}
