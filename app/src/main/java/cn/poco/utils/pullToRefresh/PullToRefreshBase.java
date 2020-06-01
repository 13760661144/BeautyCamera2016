package cn.poco.utils.pullToRefresh;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

/**
 * 下拉刷新的基类，可支持ScrollView和ListView
 * @author pocouser
 *
 */
public abstract class PullToRefreshBase extends LinearLayout
{
	protected static final float OFFSET_RADIO = 2.5F;
	protected static final int SCROLL_DURATION = 150;

	private float m_downY = -1;
	private float m_touchSlop;
	private boolean m_pullToRefreshEnabled;
	private HeaderView.State m_pullDownState = HeaderView.State.NONE;

	private HeaderView m_headerView;
	private int m_headerViewHeight = 0;
	private FrameLayout m_refreshableViewWapper;
	private View m_refreshableView;
	private OnRefreshListener m_refreshListener;

	private SmoothScrollRunnable m_smoothScrollRunnable;
	private OnTouchListener m_refreshViewTouchListener;

	/**是否截断touch事件*/
	private boolean m_interceptEventEnable = true;
	/**表示是否消费了touch事件，如果是，则不调用父类的onTouchEvent方法*/
	private boolean m_isHandledTouchEvent = false;

	private boolean m_needAdjust = false;
	private int m_topHeight;

	// 滑动距离及坐标 
//	private float x_distance, y_distance, x_last, y_last;

	public PullToRefreshBase(Context context)
	{
		this(context, null);
	}

	public PullToRefreshBase(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		// TODO Auto-generated constructor stub 
		init(context, attrs);
	}

	public PullToRefreshBase(Context context, AttributeSet attrs, int topHeight)
	{
		super(context, attrs);
		// TODO Auto-generated constructor stub
		m_topHeight = topHeight;
		init(context, attrs);
	}

	private void init(Context context, AttributeSet attrs)
	{
		setOrientation(LinearLayout.VERTICAL);

		m_touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

		m_headerView = createHeaderView(context, attrs);
		m_refreshableView = createRefreshableView(context, attrs);

		if(null == m_refreshableView)
		{
			throw new NullPointerException("Refreshable view can not be null.");
		}

		addHeaderView(context, m_headerView);

		addRefreshableView(context, m_refreshableView);
		m_refreshViewTouchListener = new OnTouchListener()
		{
			private float downY;
			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				boolean handle = false;
				switch(event.getAction())
				{
					case MotionEvent.ACTION_DOWN:
					{
						m_needAdjust = false;
						downY = event.getY();
						break;
					}
					case MotionEvent.ACTION_MOVE:
					{
						float deltaY = event.getY() - downY;
						downY = event.getY();
						if(deltaY < -0.5f && isRefreshing() && PullToRefreshBase.this.getScrollY() < 0)
						{
							m_needAdjust = true;
							pullHeaderLayout(deltaY / OFFSET_RADIO);
							handle = true;
						}

						if(PullToRefreshBase.this.getScrollY() == 0 && isRefreshing() && m_needAdjust)
						{
							m_needAdjust = false;
							int height = PullToRefreshBase.this.getHeight();
							refreshRefreshableViewSize(0, height);
						}
						break;
					}
					case MotionEvent.ACTION_CANCEL:
					case MotionEvent.ACTION_UP:
					{
						break;
					}
				}
				return handle;
			}
		};

		m_refreshableView.setOnTouchListener(m_refreshViewTouchListener);

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

	protected HeaderView createHeaderView(Context context, AttributeSet attrs)
	{
		HeaderView out = new HeaderView(context);
		return out;
	}

	protected abstract View createRefreshableView(Context context, AttributeSet attrs);

	protected void addHeaderView(Context context, View headerView)
	{
		LayoutParams params = new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

		if(null != headerView)
		{
			if(this == headerView.getParent())
			{
				removeView(headerView);
			}
			addView(headerView, 0, params);
		}
	}

	protected void addRefreshableView(Context context, View refreshView)
	{
		int width = LayoutParams.MATCH_PARENT;
		int height = LayoutParams.MATCH_PARENT;

		m_refreshableViewWapper = new FrameLayout(context);
		m_refreshableViewWapper.addView(refreshView, width, height);
		LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		ll.topMargin = m_topHeight;
		m_refreshableViewWapper.setLayoutParams(ll);

		height = 10;
		this.addView(m_refreshableViewWapper, width, height);
	}

	@Override
	public void setOrientation(int orientation)
	{
		// TODO Auto-generated method stub
		if(LinearLayout.VERTICAL != orientation)
		{
			throw new IllegalArgumentException("this class only support VERTICAL orientation.");
		}
		super.setOrientation(orientation);
	}

	public void setPullToRefreshEnabled(boolean enabled)
	{
		m_pullToRefreshEnabled = enabled;
	}

	public void setOnRefreshListener(OnRefreshListener refreshListener)
	{
		m_refreshListener = refreshListener;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev)
	{
		// TODO Auto-generated method stub
		if(!m_pullToRefreshEnabled)
			return false;
		if(!m_interceptEventEnable)
			return false;
		int action = ev.getAction();
		if(action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP)
		{
			m_isHandledTouchEvent = false;
			return false;
		}
		if(action != MotionEvent.ACTION_DOWN && m_isHandledTouchEvent)
		{
			return true;
		}
		switch(ev.getAction())
		{
			case MotionEvent.ACTION_DOWN:
			{
				m_downY = ev.getY();
				m_isHandledTouchEvent = false;
				break;
			}
			case MotionEvent.ACTION_MOVE:
			{
				float deltaY = ev.getY() - m_downY;
				if(Math.abs(deltaY) > m_touchSlop || isRefreshing())
				{
					m_downY = ev.getY();
					if(m_pullToRefreshEnabled && isReadyToPullDown())
					{
						m_isHandledTouchEvent = deltaY > 0.5f;
					}
				}

				break;
			}
		}
		return m_isHandledTouchEvent;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		// TODO Auto-generated method stub
		boolean handled = false;
		switch(event.getAction())
		{
			case MotionEvent.ACTION_DOWN:
			{
				m_downY = event.getY();
				m_isHandledTouchEvent = false;
				break;
			}
			case MotionEvent.ACTION_MOVE:
			{
				float deltaY = event.getY() - m_downY;
				m_downY = event.getY();
				if(m_pullToRefreshEnabled && isReadyToPullDown())
				{
					pullHeaderLayout(deltaY / OFFSET_RADIO);
					handled = true;
				}
				else
				{
					m_isHandledTouchEvent = false;
				}
				break;
			}
			case MotionEvent.ACTION_CANCEL:
			case MotionEvent.ACTION_UP:
			{
				if(m_isHandledTouchEvent)
				{
					m_isHandledTouchEvent = false;

					if(isReadyToPullDown())
					{
						if(m_pullToRefreshEnabled && m_pullDownState == HeaderView.State.RELEASE_TO_REFRESH)
						{
							startRefreshing();
							handled = true;
						}
						resetHeaderLayout();
					}
				}
				break;
			}
		}
		return handled;
	}

	protected void pullHeaderLayout(float delta)
	{
		int oldScrollY = getScrollY();
		if(delta < 0 && (oldScrollY - delta) >= 0)
		{
			scrollTo(0, 0);
			return;
		}
		scrollBy(0, -(int)delta);
		if(m_pullToRefreshEnabled && !isRefreshing())
		{
			if(Math.abs(oldScrollY) >= m_headerViewHeight)
			{
				m_pullDownState = HeaderView.State.RELEASE_TO_REFRESH;
			}
			else
			{
				m_pullDownState = HeaderView.State.PULL_TO_REFRESH;
			}
			m_headerView.setState(m_pullDownState);
		}
	}

	protected void startRefreshing()
	{
		if(isRefreshing())
		{
			return;
		}
		m_pullDownState = HeaderView.State.REFRESHING;

		if(null != m_headerView)
		{
			m_headerView.setState(m_pullDownState);
		}

		int height = getHeight() - m_headerViewHeight;
		refreshRefreshableViewSize(0, height);


		postDelayed(new Runnable()
		{

			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				if(null != m_refreshListener)
				{
					m_refreshListener.onRefresh(PullToRefreshBase.this);
				}
			}
		}, SCROLL_DURATION);
	}

	protected void resetHeaderLayout()
	{
		int scrollY = Math.abs(getScrollY());
		boolean refreshing = isRefreshing();
		if(refreshing && scrollY <= m_headerViewHeight)
		{
			smoothScrollTo(0);
		}
		if(refreshing)
		{
			smoothScrollTo(-m_headerViewHeight);
		}
		else
		{
			smoothScrollTo(0);
		}
	}

	public void onStateChanged(HeaderView.State state)
	{
		m_pullDownState = state;
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

		int oldScrollValue = this.getScrollY();
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

	public boolean isRefreshing()
	{
		return m_pullDownState == HeaderView.State.REFRESHING;
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		// TODO Auto-generated method stub
		super.onSizeChanged(w, h, oldw, oldh);

		refreshLoadingViewSize();

		refreshRefreshableViewSize(w, h);

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
		int pTop = getPaddingTop();
		int pRight = getPaddingRight();
		int pBottom = getPaddingBottom();

		pTop = -height;

		setPadding(pLeft, pTop, pRight, pBottom);
	}

	public void onPullDownRefreshComplete()
	{
		if(isRefreshing())
		{
			m_pullDownState = HeaderView.State.RESET;

			postDelayed(new Runnable()
			{

				@Override
				public void run()
				{
					// TODO Auto-generated method stub
					m_interceptEventEnable = true;
					m_headerView.setState(m_pullDownState);
				}
			}, SCROLL_DURATION);

			int height = getHeight();
			refreshRefreshableViewSize(0, height);

			resetHeaderLayout();
			m_interceptEventEnable = false;
		}
	}

	/**
	 * 计算刷新View的大小
	 *
	 * @param width 当前容器的宽度
	 * @param height 当前容器的宽度
	 */
	protected void refreshRefreshableViewSize(int width, int height) {
		if (null != m_refreshableViewWapper) {
			LayoutParams lp = (LayoutParams) m_refreshableViewWapper.getLayoutParams();
			if (lp.height != height) {
				lp.height = height;
				m_refreshableViewWapper.requestLayout();
			}
		}
	}

	public void releaseMem()
	{
		m_refreshListener = null;
	}

	/**
	 * refreshableView处于最顶部，此时headerView在屏幕上不显示
	 * @return
	 */
	protected abstract boolean isReadyToPullDown();

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
				scrollTo(0, mScrollToY);
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

				scrollTo(0, mCurrentY);
			}

			// If we're not at the target Y, keep going...
			if (mContinueRunning && mScrollToY != mCurrentY) {
				PullToRefreshBase.this.postDelayed(this, 16);// SUPPRESS CHECKSTYLE
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

	public interface OnRefreshListener
	{
		public void onRefresh(PullToRefreshBase refreshView);
	}
} 
