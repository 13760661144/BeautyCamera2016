package cn.poco.utils.pullToRefresh;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import cn.poco.utils.WaitAnimDialog;

public class HeaderView extends LinearLayout
{
	private State m_preState = State.NONE;
	private State m_curState = State.NONE;
	private WaitAnimDialog.WaitAnimView mLoadingView;
	int m_topMargin;
	int m_bottomMargin;

	public HeaderView(Context context)
	{
		this(context, null);
	}

	public HeaderView(Context context, int topMargin, int bottomMargin)
	{
		super(context);
		m_bottomMargin = bottomMargin;
		m_topMargin = topMargin;
		init(context);
	}

	public HeaderView(Context context, AttributeSet attrs)
	{
		this(context, attrs, 0);
	}

	public HeaderView(Context context, AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
		init(context);
	}

	private void init(Context context)
	{
		setGravity(Gravity.CENTER);
		mLoadingView = new WaitAnimDialog.WaitAnimView(context);
		LinearLayout.LayoutParams ll = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		ll.bottomMargin = m_bottomMargin;
		ll.topMargin = m_topMargin;
		ll.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
		mLoadingView.setLayoutParams(ll);
		this.addView(mLoadingView);

		mLoadingView.stop();
		mLoadingView.invalidate();
	}

	public void setState(State state)
	{
		// TODO Auto-generated method stub
		if(m_curState != state)
		{
			m_preState = m_curState;
			m_curState = state;
			onStateChanged(m_curState);
		}
	}

	public State getState()
	{
		// TODO Auto-generated method stub
		return m_curState;
	}

	public int getContentSize()
	{
		// TODO Auto-generated method stub
		return getHeight();
	}

	public void onPull(int scale)
	{
		// TODO Auto-generated method stub

	}

	private void onStateChanged(State state)
	{
		switch(state)
		{
			case PULL_TO_REFRESH:
			{
				onPullToRefresh();
				break;
			}
			case RELEASE_TO_REFRESH:
			{
				onReleaseToRefresh();
				break;
			}
			case REFRESHING:
			{
				onRefreshing();
				break;
			}
			case RESET:
			{
				onReset();
				break;
			}
			default:
				break;
		}
	}

	protected void onPullToRefresh()
	{
		stopAnim();
	}

	protected void onReleaseToRefresh()
	{
		stopAnim();
	}

	protected void onRefreshing()
	{
		if (mLoadingView != null) {
			mLoadingView.start();
			mLoadingView.invalidate();
		}
	}

	protected void onReset()
	{
		stopAnim();
	}

	private void stopAnim()
	{
		if (mLoadingView != null) {
			mLoadingView.stop();
			mLoadingView.invalidate();
		}
	}

	//下拉刷新的四种状态
	public static enum State
	{
		REFRESHING,
		RELEASE_TO_REFRESH,
		NONE,
		PULL_TO_REFRESH,
		State,
		RESET
	};

}
