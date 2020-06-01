package cn.poco.utils.pullToRefresh;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

/**
 * Created by admin on 2016/5/31.
 */
public class PullToRefreshListView extends PullToRefreshBase
{
	private ListView m_refreshView;
	public PullToRefreshListView(Context context)
	{
		super(context);
		setPullToRefreshEnabled(true);
	}

	public PullToRefreshListView(Context context, int height)
	{
		super(context, null, height);
		setPullToRefreshEnabled(true);
	}

	@Override
	protected View createRefreshableView(Context context, AttributeSet attrs)
	{
		m_refreshView = new ListView(context);

		LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		m_refreshView.setLayoutParams(ll);

		return m_refreshView;
	}

	@Override
	protected boolean isReadyToPullDown()
	{
		boolean result=false;
		if(m_refreshView != null)
		{
			if(m_refreshView.getFirstVisiblePosition() == 0)
			{
				final View topChildView = m_refreshView.getChildAt(0);
				result = topChildView.getTop() == 0;
			}
		}
		return result;
	}

	public ListView getListView()
	{
		return m_refreshView;
	}
}
