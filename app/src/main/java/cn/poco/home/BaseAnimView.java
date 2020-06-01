package cn.poco.home;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public abstract class BaseAnimView extends FrameLayout
{

	public BaseAnimView(Context context)
	{
		super(context);

		Init();
	}

	public BaseAnimView(Context context, AttributeSet attrs)
	{
		super(context, attrs);

		Init();
	}

	public BaseAnimView(Context context, AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);

		Init();
	}

	protected abstract void Init();
	
	public void setCompleteListener(Runnable listener)
	{
	}
	
	public abstract void Start();
	
	public abstract void Pause();
	
	public abstract void Stop();
	
	public abstract void ClearAll();
}
