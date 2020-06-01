package cn.poco.utils.pullToRefresh;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.AbsListView;
import android.widget.ListView;

/**
 * Created by admin on 2017/2/14.
 */

public class PullToRefreshListView1 extends ListView implements AbsListView.OnScrollListener
{
	protected static final float OFFSET_RADIO = 2.5F;
	protected static final int SCROLL_DURATION = 150;
	private OnRefreshListener m_refreshListener;
	private boolean m_isReadyToPullDown = false;	//listview是否在顶部
	private int m_downY;

	private HeaderView m_headerView;
	private int m_headerViewHeight = 0;
	private int m_lastTopPadding = 0;
	private HeaderView.State m_state = HeaderView.State.NONE;
	private int m_topMargin = 0;
	private int m_bottomMargin = 0;

	private boolean isRecored;
	public PullToRefreshListView1(Context context, AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
		Init(context);
	}

	public PullToRefreshListView1(Context context)
	{
		super(context);
		Init(context);
	}

	public PullToRefreshListView1(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		Init(context);
	}

	public PullToRefreshListView1(Context context, int topMargin, int bottomMargin)
	{
		super(context);
		m_topMargin = topMargin;
		m_bottomMargin = bottomMargin;
		Init(context);
	}

	private void Init(Context context)
	{
		setCacheColorHint(0);

		m_headerView = new HeaderView(context, m_topMargin, m_bottomMargin);
		addHeaderView(m_headerView, null, false);

		// 设置滚动监听事件
		setOnScrollListener(this);

		// 一开始的状态就是下拉刷新完的状态，所以为DONE
		m_state = HeaderView.State.NONE;

		// 得到Header的高度，这个高度需要用这种方式得到，在onLayout方法里面得到的高度始终是0
		getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
		{

			@Override
			public void onGlobalLayout()
			{
				// TODO Auto-generated method stub
				refreshLoadingViewSize();
				getViewTreeObserver().removeGlobalOnLayoutListener(this);
			}
		});
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev)
	{
		if (m_isReadyToPullDown && !isRefreshing()) {
			switch (ev.getAction()) {
				case MotionEvent.ACTION_DOWN:
					if (!isRecored)
					{
						isRecored = true;
						m_downY = (int) ev.getY();// 手指按下时记录当前位置
						m_lastTopPadding = m_headerView.getPaddingTop();
					}
					break;
				case MotionEvent.ACTION_MOVE:
					int tempY = (int) ev.getY();
					if (!isRecored) {
						isRecored = true;
						m_downY = tempY;
						m_lastTopPadding = m_headerView.getPaddingTop();
					}
					int deltaY = tempY - m_downY;
					if(deltaY > 1)
					{
						pullHeaderLayout((int)(deltaY / OFFSET_RADIO));
					}
					break;

				case MotionEvent.ACTION_UP:
				case MotionEvent.ACTION_CANCEL:
					if(m_state == HeaderView.State.RELEASE_TO_REFRESH)
					{
						startRefreshing();
					}
					resetHeaderLayout();
					isRecored = false;
					break;
				default:
					break;
			}
		}
		return super.onTouchEvent(ev);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		// TODO Auto-generated method stub
		super.onSizeChanged(w, h, oldw, oldh);

		refreshLoadingViewSize();

		post(new Runnable()
		{

			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				requestLayout();
			}
		});
	}

	private void refreshLoadingViewSize()
	{
		int height = (null != m_headerView)? m_headerView.getContentSize(): 0;

		if(height < 0)
		{
			height = 0;
		}

		m_headerViewHeight = height;

		// 这里得到Header的高度，设置的padding的top和bottom就应该是header的高度
		// 因为header和footer是完全看不见的
		height = (null != m_headerView) ? m_headerView.getMeasuredHeight() : 0;

		int pLeft = getPaddingLeft();
		int pRight = getPaddingRight();
		int pBottom = getPaddingBottom();

		int pTop = -height;

		if(m_headerView != null)
		{
			m_headerView.setPadding(pLeft, pTop, pRight, pBottom);
			m_headerView.invalidate();
		}
	}

	protected void pullHeaderLayout(int delta)
	{
		if(m_state == HeaderView.State.RELEASE_TO_REFRESH || m_state == HeaderView.State.PULL_TO_REFRESH)
		{
			setSelection(0);
		}
		if(delta < 0 && (m_lastTopPadding + delta) <= -m_headerViewHeight)
		{
			m_headerView.setPadding(0, -m_headerViewHeight, 0, 0);
			return;
		}
		m_headerView.setPadding(0, m_lastTopPadding + delta, 0, 0);
		if(!isRefreshing())
		{
			if(m_headerView.getPaddingTop() >= 0)
			{
				m_state = HeaderView.State.RELEASE_TO_REFRESH;
			}
			else
			{
				m_state = HeaderView.State.PULL_TO_REFRESH;
			}
			m_headerView.setState(m_state);
		}
	}

	protected void startRefreshing()
	{
		if(isRefreshing())
		{
			return;
		}
		m_state = HeaderView.State.REFRESHING;

		if(null != m_headerView)
		{
			m_headerView.setState(m_state);
		}


		postDelayed(new Runnable()
		{

			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				if(null != m_refreshListener)
				{
					m_refreshListener.onRefresh();
				}
			}
		}, SCROLL_DURATION);
	}

	public void onPullDownRefreshComplete()
	{
		if(isRefreshing())
		{
			m_state = HeaderView.State.RESET;

			postDelayed(new Runnable()
			{

				@Override
				public void run()
				{
					// TODO Auto-generated method stub
					m_headerView.setState(m_state);
				}
			}, SCROLL_DURATION);

			resetHeaderLayout();
		}
	}

	protected void resetHeaderLayout()
	{
		int scrollY = m_headerView.getPaddingTop();
		boolean refreshing = isRefreshing();
		if(refreshing && scrollY <= 0 && scrollY > -m_headerViewHeight)
		{
			setSelection(0);
			smoothScrollTo(0);
		}
		else if(refreshing)
		{
			smoothScrollTo(0);
		}
		else
		{
			smoothScrollTo(-m_headerViewHeight);
		}
	}

	protected void smoothScrollTo(int delta)
	{
		smoothScrollTo(delta, SCROLL_DURATION, 0);
	}

	/**
	 * 平滑滚动
	 *
	 * @param newScrollValue 滚动的值
	 * @param duration 滚动时候
	 * @param delayMillis 延迟时间，0代表不延迟
	 */
	private void smoothScrollTo(int newScrollValue, long duration, long delayMillis)
	{
		if(null != m_smoothScrollRunnable)
		{
			m_smoothScrollRunnable.stop();
		}

		int oldScrollValue = m_headerView.getPaddingTop();
		boolean post = (oldScrollValue != newScrollValue);
		if (post) {
			m_smoothScrollRunnable = new SmoothScrollRunnable(oldScrollValue, newScrollValue, duration);
		}

		if (post) {
			if (delayMillis > 0) {
				postDelayed(m_smoothScrollRunnable, delayMillis);
			} else {
				post(m_smoothScrollRunnable);
			}
		}
	}

	private SmoothScrollRunnable m_smoothScrollRunnable;

	class SmoothScrollRunnable implements Runnable
	{
		/**动画效果*/
		private final Interpolator mInterpolator;
		/**结束Y*/
		private final int mScrollToY;
		/**开始Y*/
		private final int mScrollFromY;
		/**滑动时间*/
		private final long mDuration;
		/**是否继续运行*/
		private boolean mContinueRunning = true;
		/**开始时刻*/
		private long mStartTime = -1;
		/**当前Y*/
		private int mCurrentY = -1;

		/**
		 * 构造方法
		 *
		 * @param fromY 开始Y
		 * @param toY 结束Y
		 * @param duration 动画时间
		 */
		public SmoothScrollRunnable(int fromY, int toY, long duration) {
			mScrollFromY = fromY;
			mScrollToY = toY;
			mDuration = duration;
			mInterpolator = new DecelerateInterpolator();
		}

		@Override
		public void run() {
			/**
			 * If the duration is 0, we scroll the view to target y directly.
			 */
			if (mDuration <= 0) {
				m_headerView.setPadding(0, mScrollToY, 0, 0);
				return;
			}

			/**
			 * Only set mStartTime if this is the first time we're starting,
			 * else actually calculate the Y delta
			 */
			if (mStartTime == -1) {
				mStartTime = System.currentTimeMillis();
			} else {

				/**
				 * We do all calculations in long to reduce software float
				 * calculations. We use 1000 as it gives us good accuracy and
				 * small rounding errors
				 */
				final long oneSecond = 1000;    // SUPPRESS CHECKSTYLE
				long normalizedTime = (oneSecond * (System.currentTimeMillis() - mStartTime)) / mDuration;
				normalizedTime = Math.max(Math.min(normalizedTime, oneSecond), 0);

				final int deltaY = Math.round((mScrollFromY - mScrollToY)
													  * mInterpolator.getInterpolation(normalizedTime / (float) oneSecond));
				mCurrentY = mScrollFromY - deltaY;

				m_headerView.setPadding(0, mCurrentY, 0, 0);
			}

			// If we're not at the target Y, keep going...
			if (mContinueRunning && mScrollToY != mCurrentY) {
				PullToRefreshListView1.this.postDelayed(this, 16);// SUPPRESS CHECKSTYLE
			}
		}

		/**
		 * 停止滑动
		 */
		public void stop() {
			mContinueRunning = false;
			removeCallbacks(this);
		}

	}

	@Override
	public void smoothScrollToPosition(int position)
	{
		super.smoothScrollToPosition(position);
		resetHeaderLayout();
	}

	public void releaseMem()
	{
		m_refreshListener = null;
	}

	public void setOnRefreshListener(OnRefreshListener refreshListener)
	{
		m_refreshListener = refreshListener;
	}

	public boolean isRefreshing()
	{
		return m_state == HeaderView.State.REFRESHING;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState)
	{

	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
	{
		m_isReadyToPullDown = firstVisibleItem == 0;
		/*if(firstVisibleItem == 0)
		{
			final View topChildView = getChildAt(firstVisibleItem);
			if(topChildView != null)
			{
				m_isReadyToPullDown = topChildView.getTop() == 0;
			}
		}*/
	}

	public interface OnRefreshListener
	{
		public void onRefresh();
	}
}
