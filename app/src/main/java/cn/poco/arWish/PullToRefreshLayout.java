package cn.poco.arWish;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Scroller;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import cn.poco.camera3.util.CameraPercentUtil;
import my.beautyCamera.R;

/**
 * 应用场景 ListView GridView ScrollView
 * 
 * @author pzf
 */
public class PullToRefreshLayout extends RelativeLayout
{
	public static final int BELOW = 0; // 底部刷新
	public static final int ABOVE = 1; // 顶部刷新
	public static final int ABOVE_BELOW = 2; // 上下都可以刷新
	private static final int DEFAULT_TIME = 300; // 默认时间

	private float x;
	private float y;
	private int offsetY;
	private float preY;
	private float downY; // 按下时y轴坐标
	private float downX; // 按下时x轴坐标
	private int mScrollY; // Y轴偏移量
	private float mFactor = 0.6f; // 滑动因子
	private int mOutLine = 4; // 上下拉界线
	private int refreshMode = -1; // 刷新模式
	private boolean lock = false; // 锁
	private boolean scrollable = true; // 外部控制允许滑动
	private boolean isTop = false; // 顶部标记
	private volatile boolean isRefreshing = false; // 正在刷新
	private long lastTime = 0; // 完成刷新时间
	private int moveUpTime; // 上下滑动时间
	private long upTime; // 进入刷新时间
	private volatile boolean ShouldIntercept = false; // 刷新中断
	private boolean bottomEnable = true;// 启用底部
	private View mChildView = null;
	private Scroller mScroller; // 用于完成滑动滚动操作的实例
	private OnRefreshListener mOnRefreshListener; // 刷新监听
	private int mCurrentState = RefreshState.PULL; // 当前的状态,

	private int mHeaderViewHeight;// 头布局高度
	private int mFooterViewHeight;// 脚布局高度

	private StyleTemplate mHeadStyle;// 头部样式

	private StyleTemplate mFootStyle;// 尾部样式

	public PullToRefreshLayout(Context context)
	{
		this(context, null);
	}

	public PullToRefreshLayout(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		mScroller = new Scroller(context, new OvershootInterpolator());
	}

	private void initFooter()
	{

		measureView(mFootStyle.container);
		mFooterViewHeight = mFootStyle.container.getMeasuredHeight();
		LayoutParams rparams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mFooterViewHeight);
		rparams.addRule(ALIGN_PARENT_BOTTOM);
		rparams.bottomMargin = -mFooterViewHeight;
		addView(mFootStyle.container, rparams);
	}

	private void initHeader()
	{
		measureView(mHeadStyle.container);
		mHeaderViewHeight = mHeadStyle.container.getMeasuredHeight();
		LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mHeaderViewHeight);
		layoutParams.topMargin = -mHeaderViewHeight;
		addView(mHeadStyle.container, layoutParams);
	}

	/**
	 * 测量给定的View的宽和高, 测量之后, 可以得到view的宽和高
	 * 
	 * @param child
	 */
	public void measureView(View child)
	{
		ViewGroup.LayoutParams lp = child.getLayoutParams();
		if (lp == null)
		{
			lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		}

		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0, lp.width);

		int lpHeight = lp.height;
		int childHeightSpec;
		if (lpHeight > 0)
		{
			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);
		} else
		{
			childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
		}
		child.measure(childWidthSpec, childHeightSpec);//
	}

	private void findView()
	{
		if (mChildView == null && (!(mChildView instanceof AbsListView) || !(mChildView instanceof ScrollView)))
		{
			for (int i = 0; i < getChildCount(); i++)
			{
				mChildView = getChildAt(i);
				if (mChildView instanceof AbsListView || mChildView instanceof ScrollView)
					break;
			}

		}
	}

	/***
	 * 刷新时状态
	 */
	public static class RefreshState
	{
		public static final int PULL = 100; // 下拉刷新
		public static final int RELEASE = 200; // 松开刷新
		public static final int INTERRUP = 300; // 中断刷新
		public static final int REFRESHING = 400; // 正在刷新
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev)
	{
		if (ev.getAction() == MotionEvent.ACTION_DOWN)
		{
			preY = downY = ev.getY();
			downX = ev.getX();
			findView();
		}
		return super.dispatchTouchEvent(ev);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev)
	{

		if (lock || !scrollable)
		{
			return false;
		} else
		{
			if (isRefreshing)
			{
				return true;
			}
			if (ev.getAction() == MotionEvent.ACTION_MOVE)
			{

				float y = ev.getY();
				float offsetY = y - downY;
				if (Math.abs(((y - downY) * 0.40)) * 1.5 < Math.abs((ev.getX() - downX) * 0.40))
				{
					return false;
				}
				if (mChildView instanceof AbsListView)
				{
					AbsListView absListView = ((AbsListView) mChildView);
					if (absListView.getChildAt(0) == null)
					{
						return false;
					}
					if (offsetY / 2 > mOutLine && refreshMode != BELOW)
					{
						if (absListView.getFirstVisiblePosition() == 0 && absListView.getChildAt(0).getTop() == 0)
						{
							isTop = true;
							mHeadStyle.onStart();
							return true;
						}
					} else if (offsetY / 2 < -mOutLine && refreshMode != ABOVE)
					{
						if (absListView.getChildAt(absListView.getChildCount() - 1).getBottom() <= absListView.getHeight() && absListView.getAdapter().getCount() - 1 == absListView.getLastVisiblePosition())
						{
							isTop = false;
							if (bottomEnable)
							{
								mFootStyle.onStart();
								return true;
							}
						}

					}
				} else if (mChildView instanceof ScrollView)
				{

					ScrollView scrollView = (ScrollView) mChildView;
					int scrollY = scrollView.getScrollY();
					View child = scrollView.getChildAt(0);
					if (child == null)
					{
						return false;
					}
					if (offsetY > 0 && scrollY == 0 && refreshMode != BELOW)
					{
						isTop = true;
						mHeadStyle.onStart();
						return true;
					}
					if (refreshMode == ABOVE_BELOW || refreshMode == BELOW)
					{
						if (child.getMeasuredHeight() <= scrollView.getHeight() + scrollY && offsetY < 0)
						{
							isTop = false;
							if (bottomEnable)
							{
								mFootStyle.onStart();
								return true;
							}
						}
					}
				}
			}
			return super.onInterceptTouchEvent(ev);
		}

	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		y = event.getY();
		x = event.getX();
		switch (event.getAction())
			{
			case MotionEvent.ACTION_MOVE:
				// 移动中的y轴的阻尼偏移量
				offsetY = (int) (-(y - downY) * 0.425);
				if (Math.abs(Math.abs(y - preY) * 0.40) >= 120 || Math.abs(((y - downY) * 0.40)) * 1.2 < Math.abs((x - downX) * 0.40) || lock)
				{
					return true;
				}
				preY = y;
				if (isRefreshing && (mCurrentState == RefreshState.REFRESHING || mCurrentState == RefreshState.INTERRUP))
				{
					// 正在刷行中,中断刷新状态
					float offsetY = y - downY;
					if (isTop)
					{
						if (offsetY > 0 && isRefreshing)
						{
							scrollTo(0, (int) (-mHeaderViewHeight - offsetY / 2.8));
						}

					} else
					{
						if (offsetY < 0 && !isTop && isRefreshing)
						{
							scrollTo(0, (int) (mFooterViewHeight - offsetY / 2.8));
						}
					}
					if (mCurrentState != RefreshState.INTERRUP && isRefreshing)
					{
						ShouldIntercept = true;
						mCurrentState = RefreshState.INTERRUP;
						if (refreshMode == ABOVE || refreshMode == ABOVE_BELOW)
						{
							mHeadStyle.onHeadStateChange(mCurrentState);
						}
						if (refreshMode == ABOVE_BELOW || refreshMode == BELOW)
						{
							mFootStyle.onFootStateChange(mCurrentState);
						}
					}

				} else
				{
					if (offsetY < 0)
					{// 下拉刷新
						if (isTop && (refreshMode == ABOVE || refreshMode == ABOVE_BELOW))
						{
							if (offsetY >= -mHeaderViewHeight && mCurrentState == RefreshState.RELEASE)
							{
								// 没有完全显示, 进入到下拉状态
								if (mCurrentState != RefreshState.PULL)
								{
									mCurrentState = RefreshState.PULL;
									mHeadStyle.onHeadStateChange(mCurrentState);
								}
							} else if (offsetY < -mHeaderViewHeight && mCurrentState == RefreshState.PULL)
							{ // 完全显示,进入到刷新状态
								if (mCurrentState != RefreshState.RELEASE)
								{
									mCurrentState = RefreshState.RELEASE;
									mHeadStyle.onHeadStateChange(mCurrentState);
								}
							}
							scrollTo(0, offsetY);
							return true;
						}
					} else
					{// 上拉加载
						if (!isTop && (refreshMode == ABOVE_BELOW || refreshMode == BELOW))
						{
							if (offsetY < mFooterViewHeight && mCurrentState == RefreshState.RELEASE)
							{
								if (mCurrentState != RefreshState.PULL)
								{
									mCurrentState = RefreshState.PULL;
									mFootStyle.onFootStateChange(mCurrentState);
								}
							} else if (offsetY > mFooterViewHeight && mCurrentState == RefreshState.PULL)
							{
								if (mCurrentState != RefreshState.RELEASE)
								{
									mCurrentState = RefreshState.RELEASE;
									mFootStyle.onFootStateChange(mCurrentState);
								}
							}
							scrollTo(0, offsetY);
							return true;
						}
					}

				}
				break;
			case MotionEvent.ACTION_UP:
				if (mCurrentState == RefreshState.INTERRUP)
				{
					// 松开时,往回弹
					if (ShouldIntercept)
					{
						interceptRefreshing();
					}
				} else if (mCurrentState == RefreshState.PULL)
				{
					// 松开时, 如果当前显示的状态为下拉状态, 执行隐藏View的操作
					mScroller.startScroll(0, getScrollY(), 0, -getScrollY());
					invalidate();
				} else if (mCurrentState == RefreshState.RELEASE)
				{
					// 松开时,当前显示的状态为松开刷新状态,执行刷新的操作
					mScrollY = getScrollY();
					moveUpTime = (int) (Math.abs(mScrollY) * mFactor);
					upTime = System.currentTimeMillis();

//					PLog.out("TAG", "\nscrollY: " + mScrollY + "\nmoveUpTime:" + moveUpTime);
					if (isTop || refreshMode == ABOVE)
					{
						mScroller.startScroll(0, mScrollY, 0, -(mScrollY + mHeaderViewHeight), moveUpTime);
					} else
					{
						mScroller.startScroll(0, mScrollY, 0, -(mScrollY - mFooterViewHeight), moveUpTime);
					}
					invalidate();
					isRefreshing = true;
					mCurrentState = RefreshState.REFRESHING;
					if (isTop || refreshMode == ABOVE)
					{
						mHeadStyle.onHeadStateChange(mCurrentState);
						if (mOnRefreshListener != null)
							mOnRefreshListener.onRefresh();

					} else
					{
						mFootStyle.onFootStateChange(mCurrentState);
						if (mOnRefreshListener != null)
							mOnRefreshListener.onLoadMore();
					}
				}
				break;
			}

		return (mChildView == null || mChildView.getVisibility() == GONE) ? super.onTouchEvent(event) : true;
	}

	public void setRefreshMode(int refreshMode)
	{
		setRefreshMode(refreshMode, false);
	}

	/**
	 * 设置刷新模式
	 *
	 * @param refreshMode
	 * @param isBeauty true 定制美人进度条样式
	 */
	public void setRefreshMode(int refreshMode, boolean isBeauty)
	{
		this.refreshMode = refreshMode;
		if (refreshMode == BELOW || refreshMode == ABOVE_BELOW)
		{
			if (mFootStyle == null)
			{
				mFootStyle = new DefaultStyle(getContext(), DefaultStyle.LOCATION_BOTTOM);
				initFooter();
			}
		}
		if (refreshMode == ABOVE || refreshMode == ABOVE_BELOW)
		{
			if (mHeadStyle == null)
			{
				if(isBeauty)
				{
					mHeadStyle = new BeautyRefreshStyle(getContext());
				}else
				{
					mHeadStyle = new DefaultStyle(getContext(), DefaultStyle.LOCATION_TOP);
				}


				initHeader();
			}
		}
	}

	public boolean isBottomEnable()
	{
		return bottomEnable;
	}

	public void setBottomEnable(boolean enable)
	{
		this.bottomEnable = enable;
	}

	public void onRefreshFinish()
	{
		if (!ShouldIntercept)
		{
			if (isTop)
			{
				refreshFinish();
			} else
			{
				loadMoreFinish();
			}
		}
	}

	public void setOnRefreshListener(OnRefreshListener mOnRefreshListener)
	{
		this.mOnRefreshListener = mOnRefreshListener;
	}

	public void postRefreshing(Runnable runnable)
	{
		if (runnable != null)
		{
			isTop = true;
			upTime = System.currentTimeMillis();
			scrollTo(0, -mHeaderViewHeight);
			mCurrentState = RefreshState.REFRESHING;
			mHeadStyle.onHeadStateChange(mCurrentState);
			runnable.run();
		}
	}

	/***
	 * 重写computeScroll()方法，并在其内部完成平滑滚动的逻辑
	 */
	@Override
	public void computeScroll()
	{
		if (mScroller.computeScrollOffset())
		{
			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			invalidate();
			if (Math.abs(getScrollY()) <= 1 && lock)
			{
				if (mOnRefreshListener != null)
				{
					lock = false;
					isRefreshing = false;
					ShouldIntercept = false;
					mOnRefreshListener.onSlidingFinish();
					if (refreshMode != ABOVE)
					{
						if (mFootStyle != null)
						{
							mFootStyle.onFinish();
						}
						if (mHeadStyle != null)
						{
							mHeadStyle.onFinish();
						}
					} else
					{
						if (mHeadStyle != null)
						{
							mHeadStyle.onFinish();
						}
					}

				}
			}

		}
	}

	private void refreshFinish()
	{
		if (!ShouldIntercept)
		{
			if (isRefreshing)
			{
				int finishtime = getFinishTime();
//				PLog.out("TAG", "finishTime:" + finishtime);
				postDelayed(new Runnable()
				{
					@Override
					public void run()
					{
						if (!ShouldIntercept)
						{
							lock = true;
							isRefreshing = false;
							mHeadStyle.onRefresh();
							hideLayout();
						}

					}
				}, finishtime + 300);
			} else
			{
				if (mOnRefreshListener != null && getScrollY() == 0)
					mOnRefreshListener.onSlidingFinish();
			}

		}

	}

	private void loadMoreFinish()
	{
		if (!ShouldIntercept)
		{
			if (isRefreshing)
			{
				int finishtime = getFinishTime();
//				PLog.out("TAG", "finishTime:" + finishtime);
				postDelayed(new Runnable()
				{

					@Override
					public void run()

					{

						if (!ShouldIntercept)
						{
							lock = true;
							isRefreshing = false;
							mFootStyle.onRefresh();
							hideLayout();
						}
					}
				}, finishtime + 300);
			} else
			{
				if (mOnRefreshListener != null && getScrollY() == 0)
					mOnRefreshListener.onSlidingFinish();
			}

		}

	}

	private void interceptRefreshing()
	{

		if (ShouldIntercept && isRefreshing)
		{
			lock = true;
			isRefreshing = false;
			lastTime = System.currentTimeMillis();
			if (isTop)
			{
				if (mHeadStyle != null)
				{
					mHeadStyle.startUpAnimation();
				}
			} else
			{
				if (mFootStyle != null)
				{
					mFootStyle.startUpAnimation();
				}
			}
			postDelayed(new Runnable()
			{
				@Override
				public void run()
				{
					lock = true;
					mCurrentState = RefreshState.PULL;
					mScroller.forceFinished(true);
					mScrollY = getScrollY();
					mScroller.startScroll(0, mScrollY, 0, -mScrollY, DEFAULT_TIME);
					postInvalidate();
				}
			}, 200);
		}
	}

	private void hideLayout()
	{
		// 隐藏布局
		postDelayed(new Runnable()
		{
			@Override
			public void run()
			{
				mScrollY = getScrollY();
				mCurrentState = RefreshState.PULL;
				mScroller.startScroll(0, mScrollY, 0, -mScrollY, DEFAULT_TIME);
				invalidate();
			}
		}, 200);

	}

	private int getFinishTime()
	{
		lastTime = System.currentTimeMillis();
		if (mHeadStyle != null)
		{
			mHeadStyle.setLastTime(lastTime);
		}
		if (mFootStyle != null)
		{
			mFootStyle.setLastTime(lastTime);
		}
		int finishtime = 0;
		if ((int) (lastTime - upTime) < moveUpTime)
		{
			finishtime = (int) (moveUpTime - (lastTime - upTime));
		}
		return finishtime;
	}

	public void setScrollable(boolean scrollable)
	{
		this.scrollable = scrollable;
	}

	public void setHeadStyle(StyleTemplate headStyle)
	{
		if (mHeadStyle != null)
		{
			removeView(mHeadStyle.container);
		}
		mHeadStyle = headStyle;
		initHeader();
	}

	public void setFootStyle(StyleTemplate footStyle)
	{
		if (mFootStyle != null)
		{
			removeView(mFootStyle.container);
		}
		mFootStyle = footStyle;
		initFooter();
	}

	public interface OnRefreshListener
	{

		// 当下拉
		void onRefresh();

		// 滑动结束
		void onSlidingFinish();

		// 当上拉
		void onLoadMore();

	}

	/**
	 * 模板样式可继承自定义刷新样式可参考默认样式。
	 * 
	 * @author admin
	 * 
	 */
	public static abstract class StyleTemplate
	{

		public FrameLayout container;// 布局
		protected long mLastTime = 0; // 完成刷新时间

		public StyleTemplate(Context context)
		{
			container = new FrameLayout(context);

			initAnimation();

			addChildView(context, container);

		}

		public void setLastTime(long lastTime)
		{
			this.mLastTime = lastTime;
		}

		public void startUpAnimation()
		{

		}

		/**
		 * 初始化动画
		 */
		public void initAnimation()
		{

		}

		/**
		 * 添加自定义布局
		 * 
		 * @param context
		 *            上下文
		 * @param container
		 *            布局容器
		 */
		public abstract void addChildView(Context context, FrameLayout container);

		/**
		 * 头部状态监听
		 * 
		 * @param state
		 */
		public abstract void onHeadStateChange(int state);

		/**
		 * 尾部状态监听
		 * 
		 * @param state
		 *            see {@link RefreshState}
		 */
		public abstract void onFootStateChange(int state);

		/**
		 * 刷新成功
		 */
		public abstract void onRefresh();

		/**
		 * 滑动结束
		 */
		public abstract void onFinish();

		/**
		 * 滑动开始
		 */
		public abstract void onStart();
	}

	/**
	 * 默认头部样式
	 * 
	 * @author admin
	 * 
	 */
	private static class DefaultStyle extends StyleTemplate
	{
		public static final int LOCATION_TOP = 10;
		public static final int LOCATION_BOTTOM = 20;
		private ImageView mArrow; // 头布局的箭头
		private TextView mState;  // 头布局刷新状态
		private ImageView mProgressBar;   // 头布局的进度条
		private TextView mLastUpdateTime; // 头布局的最后刷新时间
		private Animation upAnim; 	// 向上旋转的动画
		private Animation downAnim; // 向下旋转的动画
		private Animation loadAnim; // 加载中动画
		private int mlocation = LOCATION_TOP;
		private Handler mHandler;

		public DefaultStyle(Context context, int location)
		{
			super(context);
			mHandler = new Handler();
			mlocation = location;
			mArrow.setImageResource(mlocation == LOCATION_TOP ? R.drawable.refresh_arrow_down : R.drawable.refresh_arrow_up);
			mState.setText(mlocation == LOCATION_TOP ? "下拉刷新" : "上拉加载");
			mLastUpdateTime.setText(mlocation == LOCATION_TOP ? "刚刚刷新" : "刚刚加载");
		}

		@Override
		public void addChildView(Context context, FrameLayout container)
		{
			FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

			LinearLayout headerView = new LinearLayout(context);
			headerView.setPadding(0, CameraPercentUtil.HeightPxToPercent(20), 0, CameraPercentUtil.HeightPxToPercent(20));
			headerView.setOrientation(LinearLayout.HORIZONTAL);
			headerView.setVisibility(View.VISIBLE);
			headerView.setGravity(Gravity.CENTER);
			params.gravity = Gravity.CENTER;
			container.addView(headerView, params);

			RelativeLayout frame = new RelativeLayout(context);
			LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(60, LayoutParams.WRAP_CONTENT);
			lparams.gravity = Gravity.CENTER_VERTICAL;
			headerView.addView(frame, lparams);

			mArrow = new ImageView(context);
			mArrow.setScaleType(ScaleType.CENTER_INSIDE);
			params = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			params.gravity = Gravity.CENTER;
			frame.addView(mArrow, params);

			mProgressBar = new ImageView(context);
			mProgressBar.setImageResource(R.drawable.custom_progressbar);
			mProgressBar.setVisibility(View.GONE);
			mProgressBar.setScaleType(ScaleType.CENTER_INSIDE);
			params = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			params.gravity = Gravity.CENTER;
			frame.addView(mProgressBar, params);

			LinearLayout mContentLayout = new LinearLayout(context);
			mContentLayout.setOrientation(LinearLayout.VERTICAL);
			lparams = new LinearLayout.LayoutParams(CameraPercentUtil.HeightPxToPercent(250), LayoutParams.WRAP_CONTENT);
			lparams.gravity = Gravity.CENTER_HORIZONTAL;
			headerView.addView(mContentLayout, lparams);

			mState = new TextView(context);
			mState.setTextColor(Color.GRAY);
			mState.setTextSize(15);
			lparams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			lparams.gravity = Gravity.CENTER_HORIZONTAL;
			mContentLayout.addView(mState, lparams);

			mLastUpdateTime = new TextView(context);
			mLastUpdateTime.setTextColor(Color.GRAY);
			mLastUpdateTime.setTextSize(12);
			lparams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			lparams.gravity = Gravity.CENTER_HORIZONTAL;
			lparams.topMargin = CameraPercentUtil.HeightPxToPercent(6);
			mContentLayout.addView(mLastUpdateTime, lparams);
		}

		@Override
		public void initAnimation()
		{
			upAnim = new RotateAnimation(0, -180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
			upAnim.setDuration(80);
			upAnim.setFillAfter(true);
			downAnim = new RotateAnimation(-180, -360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
			downAnim.setDuration(80);
			downAnim.setFillAfter(true);
			loadAnim = new RotateAnimation(0f, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
			loadAnim.setRepeatCount(Animation.INFINITE);
			loadAnim.setRepeatMode(Animation.RESTART);
            loadAnim.setInterpolator(new LinearInterpolator());
			loadAnim.setDuration(600);
		}

		@Override
		public void onFootStateChange(int state)
		{
			String refreshTime = getLastRefreshTime();
			if (refreshTime != null)
			{
				mLastUpdateTime.setText("上次加载:" + refreshTime);
			}
			if (state == RefreshState.PULL)
			{ // 当前进入上拉
				mArrow.setVisibility(View.VISIBLE);
				mArrow.startAnimation(downAnim);
				mState.setText("加载更多");
			} else if (state == RefreshState.RELEASE)
			{ // 当前松开加载
				mArrow.startAnimation(upAnim);
				mState.setText("松开加载");
			} else if (state == RefreshState.REFRESHING)
			{ // 当前正在加载中
				mHandler.postDelayed(new Runnable()
				{

					@Override
					public void run()
					{
						mArrow.clearAnimation();
						mArrow.setVisibility(View.GONE);
						mProgressBar.setVisibility(View.VISIBLE);
						mProgressBar.startAnimation(loadAnim);
						mState.setText("正在加载中...");
					}
				}, 120);
			} else
			{ // 手动中断加载
				mProgressBar.clearAnimation();
				mProgressBar.setVisibility(View.GONE);
				mState.setText("加载成功");
				mArrow.setVisibility(View.VISIBLE);
			}
		}

		@Override
		public void onHeadStateChange(int state)
		{
			String refreshTime = getLastRefreshTime();
			if (refreshTime != null)
			{
				mLastUpdateTime.setText("上次刷新:" + refreshTime);
			}
			if (state == RefreshState.PULL)
			{ // 当前进入下拉
				if (mArrow.getAnimation() != null)
				{
					mArrow.clearAnimation();
					mArrow.startAnimation(downAnim);
					mState.setText("下拉刷新");
				}
			} else if (state == RefreshState.RELEASE)
			{ // 当前松开刷新
				if (mArrow.getAnimation() != null)
					mArrow.clearAnimation();
				mArrow.startAnimation(upAnim);
				mState.setText("松开刷新");

			} else if (state == RefreshState.REFRESHING)
			{ // 当前正在刷新中
				mHandler.postDelayed(new Runnable()
				{

					@Override
					public void run()
					{
						mArrow.clearAnimation();
						mArrow.setVisibility(View.GONE);
						mProgressBar.setVisibility(View.VISIBLE);
						mProgressBar.startAnimation(loadAnim);
						mState.setText("正在刷新中...");
					}
				}, 120);
			} else
			{ // 手动中断刷新
				mProgressBar.clearAnimation();
				mProgressBar.setVisibility(View.GONE);
				mState.setText("刷新成功");
				mArrow.setVisibility(View.VISIBLE);
			}
		}

		@SuppressLint("SimpleDateFormat")
		private String getLastRefreshTime()
		{
			if (mLastTime == 0)
			{
				return null;
			} else
			{
				long l = System.currentTimeMillis();
				l = l - mLastTime;
				long minutes = l / 60000;
				if (minutes == 0)
				{
					return String.valueOf("小于1分钟");
				} else if (minutes <= 30)
				{
					return String.valueOf(minutes + "分钟前");
				} else
				{
					return String.valueOf(new SimpleDateFormat("hh:mm").format(new Date(mLastTime)));
				}
			}
		}

		@Override
		public void startUpAnimation()
		{
			mArrow.startAnimation(upAnim);

		}

		@Override
		public void onFinish()
		{
			mState.setText(mlocation == LOCATION_TOP ? "下拉刷新" : "上拉加载");
			mArrow.clearAnimation();
		}

		@Override
		public void onRefresh()
		{
			mState.setText(mlocation == LOCATION_TOP ? "刷新成功" : "加载成功");
			mProgressBar.clearAnimation();
			mProgressBar.setVisibility(View.GONE);
			mArrow.setVisibility(View.VISIBLE);
			mArrow.startAnimation(upAnim);
		}

		@Override
		public void onStart()
		{

		}

	}
}
