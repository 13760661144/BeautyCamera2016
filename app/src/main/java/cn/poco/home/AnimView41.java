package cn.poco.home;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by lgd on 2017/1/14.
 */

public class AnimView41 extends BaseAnimView
{
	protected ImageView mImageView;
	public AnimView41(Context context)
	{
		super(context);
	}

	public AnimView41(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public AnimView41(Context context, AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
	}

	@Override
	protected void Init()
	{
		mImageView = new ImageView(getContext());
		mImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
		LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		addView(mImageView,params);
	}

	public void SetData(int res)
	{
		mImageView.setImageResource(res);
	}


	@Override
	public void Start()
	{

	}

	@Override
	public void Pause()
	{

	}

	@Override
	public void Stop()
	{

	}

	@Override
	public void ClearAll()
	{

	}
}
