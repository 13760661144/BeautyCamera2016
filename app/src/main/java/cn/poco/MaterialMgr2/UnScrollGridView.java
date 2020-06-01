package cn.poco.MaterialMgr2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.GridView;

import cn.poco.transitions.TweenLite;

public class UnScrollGridView extends GridView
{
	private int m_curHeight = -1;
	private int m_endH;
	private int m_startH;
	private TweenLite m_anim = new TweenLite();
	private OnAnimCompleteListener m_cb;
	private boolean m_refreshable;
	public UnScrollGridView(Context context)
	{
		this(context, null);
	}

	public UnScrollGridView(Context context, AttributeSet attrs)
	{
		this(context, attrs, 0);
	}

	@SuppressLint("NewApi")
	public UnScrollGridView(Context context, AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
		
//		//禁用硬件加速
//		if(android.os.Build.VERSION.SDK_INT >= 14)
//		{
//			this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
//			if(isHardwareAccelerated())
//			{
//				System.out.println("硬件加速");
//			}
//		}
		this.setWillNotDraw(false);
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev)
	{
		return super.dispatchTouchEvent(ev);
	}
	
	public void setHeight(int height, boolean hasAnim)
	{
		stopAnim();
		m_startH = getHeight();
		m_endH = height;
		if(hasAnim)
		{
			m_curHeight = m_startH;
			initAnim();
		}
		else
		{
			m_curHeight = m_endH;
			this.invalidate();
			this.requestLayout();
		}
	}
	
	private void stopAnim()
	{
		m_anim.M1End();
		this.invalidate();
		this.requestLayout();
	}
	
	public void setOnAnimCompleteListener(OnAnimCompleteListener listener)
	{
		m_cb = listener;
	}
	
	public void setRefreshable(boolean refreshable)
	{
		m_refreshable = refreshable;
	}
	
	@Override
	protected void onDraw(Canvas canvas)
	{
		if(!m_anim.M1IsFinish())
		{
			m_curHeight = (int)m_anim.M1GetPos();

			this.postInvalidate();
			this.requestLayout();
		}
		else
		{
			if(null != m_cb && m_refreshable)
			{
				m_refreshable = false;
				m_cb.onComplete();
			}
		}
		
	}
	
	private void initAnim()
	{
		m_anim.M1End();
		m_anim.Init(m_startH, m_endH, 500);
		m_anim.M1Start(TweenLite.EASE_IN_OUT | TweenLite.EASING_QUART);
		m_curHeight = (int)m_anim.M1GetPos();
		while(m_curHeight < 2)
		{
			m_curHeight = (int)m_anim.M1GetPos();
		}
		this.invalidate();
		this.requestLayout();
	}
	
	@Override
	protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec)
	{
		// TODO Auto-generated method stub
		if(m_curHeight < 0)
		{
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		}
		else
		{
			int heightSpec = MeasureSpec.makeMeasureSpec(m_curHeight, MeasureSpec.EXACTLY);
			super.onMeasure(widthMeasureSpec, heightSpec);
		}
	}

	public void clear()
	{
		m_cb = null;
	}
	
	public interface OnAnimCompleteListener{
		public void onComplete();
	}

}
