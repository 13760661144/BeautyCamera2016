package cn.poco.home;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;

import cn.poco.home.home4.introAnimation.Config;
import cn.poco.utils.OnAnimationClickListener;
import my.beautyCamera.R;

import static cn.poco.home.home4.utils.PercentUtil.HeightPxToPercent;
import static cn.poco.home.home4.utils.PercentUtil.HeightPxxToPercent;
import static cn.poco.home.home4.utils.PercentUtil.WidthPxxToPercent;

/**
 * Created by lgd on 2017/1/14.
 */

public class AnimView42 extends BaseAnimView
{
	protected ImageView button;
	protected Runnable m_completeLst;
	private ImageView mLogo1;
	private ImageView mLogo2;

	public AnimView42(Context context)
	{
		super(context);
	}

	public AnimView42(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public AnimView42(Context context, AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
	}

	@Override
	protected void Init()
	{
		LayoutParams params;
		params = new LayoutParams(WidthPxxToPercent(417), HeightPxxToPercent(179));
		params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
		params.topMargin = HeightPxToPercent(289);               //与首页位置一样
		mLogo1 = new ImageView(getContext());
		mLogo1.setImageResource(R.drawable.home4_intro_logo1);
		mLogo1.setScaleType(ImageView.ScaleType.FIT_CENTER);
		addView(mLogo1, params);

		params = new LayoutParams(WidthPxxToPercent(627), HeightPxxToPercent(198));
		params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
		params.topMargin = HeightPxToPercent(289)+HeightPxxToPercent(+179+93);
		mLogo2 = new ImageView(getContext());
		mLogo2.setImageResource(R.drawable.home4_intro_logo2);
		mLogo2.setScaleType(ImageView.ScaleType.FIT_CENTER);
		addView(mLogo2, params);

		button = new ImageView(getContext());
		button.setScaleType(ImageView.ScaleType.FIT_CENTER);
		button.setImageResource(R.drawable.home4_intro_btn);
		params = new LayoutParams(WidthPxxToPercent(372),WidthPxxToPercent(372));
		params.gravity = Gravity.CENTER;
		params.topMargin = Config.CAMERA_CENTER_TOP_MARGIN;             //与首页位置一样
		addView(button, params);
		button.setOnTouchListener(new OnAnimationClickListener()
		{
			@Override
			public void onAnimationClick(View v)
			{
				if(m_completeLst != null){
					m_completeLst.run();
				}
			}

			@Override
			public void onTouch(View v)
			{
			}

			@Override
			public void onRelease(View v)
			{
			}
		});
//		button.setOnClickListener(new OnClickListener()
//		{
//			@Override
//			public void onClick(View v)
//			{
//				if(m_completeLst != null){
//					m_completeLst.run();
//				}
//			}
//		});
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

	@Override
	public void setCompleteListener(Runnable listener)
	{
	 	m_completeLst = listener;
	}

}
