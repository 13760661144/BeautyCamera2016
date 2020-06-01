package cn.poco.home.home4;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.GestureDetectorCompat;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Scroller;

import cn.poco.framework.AnimatorHolder;
import cn.poco.home.home4.utils.PercentUtil;
import cn.poco.tianutils.ImageUtils;
import cn.poco.tianutils.ShareData;


/**
 * Created by lgd on 2016/12/11.
 */

public class HomeEventController implements GestureDetector.OnGestureListener
{
	private static final String TAG = "HomeEventController";
	protected static int TOUCH_SIZE;

	protected boolean mUiEnabled = true;
	protected GestureDetectorCompat mDetector;
	protected Scroller mScroller;
	protected Callback mCB;

	public final static int HORIZONTAL_DURATION = 720;
	public final static int VERTICAL_DURATION = 720;
	public final static int DEFAULT_DURATION = 250;
	public final static int VERTICAL_SPEED = 1500;             //垂直手势速度
	public final static int HORIZONTAL_SPEED = 1000;             //水平手势速度
	public final static float DISTANCE_PRESENT = 0.3f;           //屏幕百分比，超过位移
	private ValueAnimator logoRestoreAnimator;
	private static final int DURATION_RESTORE = 600;
	private static final int MAX_REBOUND = PercentUtil.HeightPxToPercent(20);

	public HomeEventController(Context context, Callback cb)
	{
		ShareData.InitData(context);
		TOUCH_SIZE = ShareData.PxToDpi_xhdpi(35);

		mCB = cb;

		mDetector = new GestureDetectorCompat(context, this);
		mScroller = new Scroller(context, new DecelerateInterpolator());

		logoRestoreAnimator = new ValueAnimator();
		logoRestoreAnimator.setDuration(DURATION_RESTORE);
		logoRestoreAnimator.setInterpolator(new DecelerateInterpolator());
		logoRestoreAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
		{
			@Override
			public void onAnimationUpdate(ValueAnimator animation)
			{
				int val = (int)animation.getAnimatedValue();
				mCB.SetTop((int)mCurrentX, val, (val - mMimValue) / (mMaxValue - mMimValue));
			}
		});
	}


	protected float mDownX;
	protected float mDownY;
	protected float mCurrentX;
	protected float mCurrentY;
	protected float mVelocityX;
	protected float mVelocityY;

	protected int mOldValue;
	protected int mMimValue;
	protected int mMaxValue;

//	private boolean isBootOrMenu = false;                  //左拉有两种情况 ，  暂时用宽度判断
	public static final int MOVE_TYPE_NONE = 0x0;
	public static final int MOVE_TYPE_LEFT = 0x1;
	public static final int MOVE_TYPE_TOP = 0x2;
	public static final int MOVE_TYPE_RIGHT = 0x4;
	public static final int MOVE_TYPE_BOTTOM = 0x8;
	protected int mCurrentMoveType = MOVE_TYPE_NONE;          //  手势方向时判断 当前切换哪个两个页面切换， 除了MOVE_TYPE_NONE 的4个值(主页到-A 与 A - 主页 一样)   避免2x4种情况
	protected int mFinishMoveType = MOVE_TYPE_NONE;          // onDown 判断将去哪个页面  onUp 时判断，回弹还是切换页面
	protected int mPageType = MOVE_TYPE_NONE;          //   当前是在哪一个 位置/页面 模式

	private boolean isOnGesture = false;    //是否在手势阶段
	private boolean isIntercept = false;              // 手势阶段ondown中途拦截 true ,onup false;，防止进入ScrollOk();

	public boolean dispatchTouchEvent(MotionEvent event)
	{
		if(mCB.CanControl())
		{
			mDetector.onTouchEvent(event);
		}
		return true;
	}

	public boolean onInterceptTouchEvent(MotionEvent event)
	{
		if(mCB.CanControl())
		{
			if(mUiEnabled)
			{
				if(event.getPointerCount() == 1)
				{
					mCurrentX = event.getX();
					mCurrentY = event.getY();
					switch(event.getAction())
					{
						case MotionEvent.ACTION_DOWN:
						{
							mDownX = mCurrentX;
							mDownY = mCurrentY;
							//在手势阶段直接拦截
							if(isOnGesture)
							{
								return true;
							}
//							else if(mPageType == MOVE_TYPE_LEFT && !isBootOrMenu && mDownX >= mCB.GetLeftW())
//							{
//								//左菜单模式，且点击在右区域
//								return true;
//							}
							break;
						}
						case MotionEvent.ACTION_MOVE:
						{
							//已经在手势阶段没必要拦截
							if(!isOnGesture)
							{
								//如果return true 在onTouchEvent再调用一次进入onStart();
								return checkIsStartGesture();
							}
							break;
						}
						default:
							break;
					}
				}
				return false;
			}
			else
			{
				return false;
			}
		}
		else
		{
			return false;

		}
	}


	public boolean onTouchEvent(MotionEvent event)
	{
		if(mCB.CanControl())
		{
			if(mUiEnabled)
			{
				if(event.getPointerCount() == 1)
				{
					mCurrentX = event.getX();
					mCurrentY = event.getY();
					switch(event.getAction())
					{
						case MotionEvent.ACTION_DOWN:
						{
							if(isOnGesture)
							{
								OnDown(mCurrentX, mCurrentY);
							}
							else
							{
								mDownX = mCurrentX;
								mDownY = mCurrentY;
							}
							return true;
						}
						case MotionEvent.ACTION_MOVE:
						{
							if(isOnGesture)
							{
								OnMove(mCurrentX, mCurrentY);
							}
							else
							{
								if(checkIsStartGesture())
								{
									onStart();
								}
							}
							return true;
						}

						case MotionEvent.ACTION_OUTSIDE:
						case MotionEvent.ACTION_UP:
						{
							if(isOnGesture)
							{
								OnUp(mCurrentX, mCurrentY);
							}else {
//								float a = mDownX - mCurrentX;
//								float b = mDownY - mCurrentY;
//								if(mPageType == MOVE_TYPE_LEFT && !isBootOrMenu && mDownX >= mCB.GetLeftW() && mCurrentX >= mCB.GetLeftW()  && ImageUtils.Spacing(a, b) < TOUCH_SIZE)
//								{
//									//左菜单模式，且点击在右区域 30px范围
//									closePage(MOVE_TYPE_LEFT);
//								}
							}
							break;
						}
						default:
							break;
					}
				}
				else
				{
					switch(event.getAction() & MotionEvent.ACTION_MASK)
					{
						case MotionEvent.ACTION_POINTER_UP:
						{
							if(isOnGesture)
							{
								if(event.getActionIndex() == 0)
								{
									OnDown(event.getX(1), event.getY(1));
								}
								else
								{
									OnDown(event.getX(), event.getY());
								}
							}
							break;
						}
					}
				}
				return true;
			}
			else
			{
				return false;
			}
		}
		else
		{
			return false;

		}
	}

	/**
	 * 是否拦截 或是否移动页面 开始进入手势阶段
	 *
	 * @return
	 */
	public boolean checkIsStartGesture()
	{
//		Log.i(TAG, "checkIsStartGesture: "+mPageType);
		boolean isStart = false;
		float a = mDownX - mCurrentX;
		float b = mDownY - mCurrentY;
		switch(mPageType)
		{
			case MOVE_TYPE_NONE:
				if(ImageUtils.Spacing(a, b) > TOUCH_SIZE)
				{
					int modeType;
					if(Math.abs(a) > Math.abs(b))
					{
						//水平方向
						if(a > 0)
						{
							modeType = MOVE_TYPE_RIGHT;
						}
						else
						{
							modeType = MOVE_TYPE_LEFT;
						}
					}
					else
					{
						//垂直方向
						if(b > 0)
						{
							if(!mCB.CanBottomIntercept()){
								return false;
							}
							modeType = MOVE_TYPE_BOTTOM;

						}
						else
						{
							modeType = MOVE_TYPE_TOP;
						}
					}
					mFinishMoveType = modeType;
					mCurrentMoveType = modeType;
					isStart = true;
				}
				break;
			case MOVE_TYPE_LEFT:
//				if(a > TOUCH_SIZE && Math.abs(a) > TOUCH_SIZE)
				if(a > TOUCH_SIZE && Math.abs(a) > TOUCH_SIZE && Math.abs(a) > 2 * Math.abs(b))
				{
//					//左菜单只能拦截右边区域
//					if(!isBootOrMenu && mDownX <= mCB.GetLeftW())
//					{
//						break;
//					}
					mFinishMoveType = MOVE_TYPE_NONE;
					mCurrentMoveType = MOVE_TYPE_LEFT;
					isStart = true;
				}
				break;
			case MOVE_TYPE_RIGHT:
				//Math.abs(a) > 3 * Math.abs(b)  避免快速上下滚动时 触发
				if(a < TOUCH_SIZE && Math.abs(a) > TOUCH_SIZE && Math.abs(a) > 2 * Math.abs(b))
				{
					mFinishMoveType = MOVE_TYPE_NONE;
					mCurrentMoveType = MOVE_TYPE_RIGHT;
					isStart = true;
				}
				break;
			case MOVE_TYPE_BOTTOM:
				if(b < TOUCH_SIZE && mCB.CanBottomIntercept() && Math.abs(b) > TOUCH_SIZE)
				{
					mFinishMoveType = MOVE_TYPE_NONE;
					mCurrentMoveType = MOVE_TYPE_BOTTOM;
					isStart = true;
				}
				break;
			case MOVE_TYPE_TOP:
				break;
			default:
				break;
		}
		return isStart;
	}

	/**
	 * 手势开始准备工作， 然后进入手势阶段
	 *
	 * @return
	 */
	private void onStart()
	{
//		Log.i(TAG, "onStart: "+mPageType+" : "+mFinishMoveType);
		mCB.onStart(mPageType, mFinishMoveType,true);
		InitView(mFinishMoveType);
		isOnGesture = true;
		mDownX = mCurrentX;
		mDownY = mCurrentY;

		//其他页面去主页  与主页去其界面的mCurrentMoveType值一样， mCurrentMoveType 两个页面相互切换的值相同
		switch(mCurrentMoveType)
		{
			case MOVE_TYPE_LEFT:
				mOldValue = mCB.GetCurrentLeftX();
				mMaxValue = 0;
				mMimValue = -mCB.GetLeftW();
				break;
			case MOVE_TYPE_RIGHT:
				mOldValue = mCB.GetCurrentRightX();
				mMimValue = 0;
				mMaxValue = mCB.GetRightW();
				break;
			case MOVE_TYPE_TOP:
				mOldValue = mCB.GetCurrentTopY();
				mMimValue = 0;
				mMaxValue = mCB.GetTopH();

				break;
			case MOVE_TYPE_BOTTOM:
				mOldValue = mCB.GetCurrentBottomY();
				mMimValue = 0;
				mMaxValue = mCB.GetBottomH();
				break;
			default:
				break;
		}
	}

	private void OnDown(float x, float y)
	{
		mDownX = x;
		mDownY = y;
		//如果是手势中途拦截重设数据
//		Log.i(TAG, "OnDown: isOnGesture "+isOnGesture);
//		Log.i(TAG, "OnDown: isIntercept "+isIntercept);
		if(isOnGesture)
		{
			mScroller.abortAnimation();
			isIntercept = true;
			logoRestoreAnimator.removeAllListeners();
			logoRestoreAnimator.cancel();
		}

		switch(mCurrentMoveType)
		{
			case MOVE_TYPE_BOTTOM:
				mOldValue = mCB.GetCurrentBottomY();
				mMimValue = 0;
				mMaxValue = mCB.GetBottomH();
				break;

			case MOVE_TYPE_LEFT:
				mOldValue = mCB.GetCurrentLeftX();
				mMaxValue = 0;
				mMimValue = -mCB.GetLeftW();
				break;

			case MOVE_TYPE_RIGHT:
				mOldValue = mCB.GetCurrentRightX();
				mMimValue = 0;
				mMaxValue = mCB.GetRightW();
				break;

			case MOVE_TYPE_TOP:
				mOldValue = mCB.GetCurrentTopY();
				mMimValue = 0;
				mMaxValue = mCB.GetTopH();
				break;

			default:
				break;
		}
	}

	private void OnMove(float x, float y)
	{
		mCurrentX = x;
		mCurrentY = y;
		switch(mCurrentMoveType)
		{
			case MOVE_TYPE_LEFT:
			{
				float tx = mCurrentX - mDownX + mOldValue;
				if(tx > mMaxValue)
				{
					tx = mMaxValue;
					mDownX = mCurrentX + mOldValue - tx;
				}
				else if(tx < mMimValue)
				{
					tx = mMimValue;
					mDownX = mCurrentX + mOldValue - tx;
				}
				tx = (int)(tx + 0.5f);
				mCB.SetLeft((int)tx, (int)mCurrentY, (tx - mMimValue) / (mMaxValue - mMimValue));
				break;
			}

			case MOVE_TYPE_TOP:
			{
				float ty = mCurrentY - mDownY + mOldValue;
				if(ty > mMaxValue)
				{
					ty = mMaxValue;
					mDownY = mCurrentY + mOldValue - ty;
				}
				else if(ty < mMimValue)
				{
					ty = mMimValue;
					mDownY = mCurrentY + mOldValue - ty;
				}
				ty = (int)(ty + 0.5f);
				mCB.SetTop((int)mCurrentX, (int)ty, (ty - mMimValue) / (mMaxValue - mMimValue));
				break;
			}

			case MOVE_TYPE_RIGHT:
			{
				float tx = mCurrentX - mDownX + mOldValue;
				if(tx > mMaxValue)
				{
					tx = mMaxValue;
					mDownX = mCurrentX + mOldValue - tx;
				}
				else if(tx < mMimValue)
				{
					tx = mMimValue;
					mDownX = mCurrentX + mOldValue - tx;
				}
				tx = (int)(tx + 0.5f);
//				mCB.SetRight((int)tx, (int)mCurrentY, (tx - mMimValue) / (mMaxValue - mMimValue));
				mCB.SetRight((int)tx, (int)mCurrentY, (mMaxValue - tx) / (mMaxValue - mMimValue));
				break;
			}

			case MOVE_TYPE_BOTTOM:
			{

				float ty = mCurrentY - mDownY + mOldValue;
				if(ty > mMaxValue)
				{
					ty = mMaxValue;
					mDownY = mCurrentY + mOldValue - ty;
				}
				else if(ty < mMimValue)
				{
					ty = mMimValue;
					mDownY = mCurrentY + mOldValue - ty;
				}
				ty = (int)(ty + 0.5f);
//				mCB.SetBottom((int)mCurrentX, (int)ty, (ty - mMimValue) / (mMaxValue - mMimValue));
				mCB.SetBottom((int)mCurrentX, (int)ty, (mMaxValue - ty) / (mMaxValue - mMimValue));
				break;
			}
			default:
				break;
		}
	}

	private void OnUp(float x, float y)
	{
//		Log.i(TAG, "OnUp: "+mCurrentMoveType+":"+mFinishMoveType+":"+mPageType);
		isIntercept = false;
		//考虑动画情况
		int src = 0;
		boolean hasAnim = true;
		switch(mCurrentMoveType)
		{
			case MOVE_TYPE_BOTTOM:
				src = mCB.GetCurrentBottomY();
				break;

			case MOVE_TYPE_LEFT:
				src = mCB.GetCurrentLeftX();
				break;

			case MOVE_TYPE_RIGHT:
				src = mCB.GetCurrentRightX();
				break;

			case MOVE_TYPE_TOP:
				src = mCB.GetCurrentTopY();
				break;

			default:
				hasAnim = false;
				break;
		}
		if(hasAnim)
		{
			//src  当前距离， dst  目标距离

			//1. 根据距离判断 mFinishMoveType，回弹还是切换
			int dst = 0;
			float present = DISTANCE_PRESENT;
			if(mPageType != MOVE_TYPE_NONE)
			{
				//其他页面切换到主页为0.7
				present = 1 - present;
			}
			switch(mCurrentMoveType)
			{
				//上和下 为 mMimValue为起点
				case MOVE_TYPE_TOP:
					if(Math.abs(src - mMimValue) < ((mMaxValue - mMimValue) * present))
					{
						dst = mMimValue;
						mFinishMoveType = MOVE_TYPE_NONE;
					}
					else
					{
						dst = mMaxValue;
						mFinishMoveType = mCurrentMoveType;
					}
					break;
				case MOVE_TYPE_LEFT:
//					if(!isBootOrMenu)
//					{
//						present = 0.5f;
//					}
					if(Math.abs(src - mMimValue) < ((mMaxValue - mMimValue) * present))
					{
						dst = mMimValue;
						mFinishMoveType = MOVE_TYPE_NONE;
					}
					else
					{
						dst = mMaxValue;
						mFinishMoveType = mCurrentMoveType;
					}
					break;
				//下和右 为  大坐标为起点
				case MOVE_TYPE_BOTTOM:
				case MOVE_TYPE_RIGHT:
					present = 1 - present;
					if(Math.abs(src) - mMimValue > ((mMaxValue - mMimValue) * present))
					{
						dst = mMaxValue;
						mFinishMoveType = MOVE_TYPE_NONE;
					}
					else
					{
						dst = mMimValue;
						mFinishMoveType = mCurrentMoveType;
					}
					break;
			}
			//2. 根据手势速度判断 mFinishMoveType，回弹还是切换
			int duration = 0;
			switch(mCurrentMoveType)
			{
				case MOVE_TYPE_TOP:
					if(Math.abs(mVelocityY) > VERTICAL_SPEED)
					{
						if(mVelocityY > VERTICAL_SPEED)
						{
							dst = mMaxValue;
							mFinishMoveType = MOVE_TYPE_TOP;
						}
						else if(mVelocityY < -VERTICAL_SPEED)
						{
							//手势向上回弹
							dst = mMimValue;
							mFinishMoveType = MOVE_TYPE_NONE;
						}
						duration = (int)Math.abs((dst - src) * 1000 * 2 / mVelocityY);
					}

					break;
				case MOVE_TYPE_LEFT:
					if(Math.abs(mVelocityX) > HORIZONTAL_SPEED)
					{
						if(mVelocityX > HORIZONTAL_SPEED)
						{
							dst = mMaxValue;
							mFinishMoveType = MOVE_TYPE_LEFT;
						}
						else if(mVelocityX < -HORIZONTAL_SPEED)
						{
							//手势向左回弹
							dst = mMimValue;
							mFinishMoveType = MOVE_TYPE_NONE;
						}
						duration = (int)Math.abs((dst - src) * 1000 * 2 / mVelocityX);
					}
					break;
				case MOVE_TYPE_BOTTOM:
					if(Math.abs(mVelocityY) > VERTICAL_SPEED)
					{
						if(mVelocityY > VERTICAL_SPEED)
						{
							//手势向下回弹
							dst = mMaxValue;
							mFinishMoveType = MOVE_TYPE_NONE;
						}
						else if(mVelocityY < -VERTICAL_SPEED)
						{
							dst = mMimValue;
							mFinishMoveType = MOVE_TYPE_BOTTOM;
						}
						duration = (int)Math.abs((dst - src) * 1000 * 2 / mVelocityY);
					}
					break;
				case MOVE_TYPE_RIGHT:
					if(Math.abs(mVelocityX) > HORIZONTAL_SPEED)
					{
						if(mVelocityX > HORIZONTAL_SPEED)
						{
							//手势向右回弹
							dst = mMaxValue;
							mFinishMoveType = MOVE_TYPE_NONE;
						}
						else if(mVelocityX < -HORIZONTAL_SPEED)
						{
							dst = mMimValue;
							mFinishMoveType = MOVE_TYPE_RIGHT;
						}
						duration = (int)Math.abs((dst - src) * 1000 * 2 / mVelocityX);
					}
					break;
			}
//			Log.i(TAG, "OnUp+finsh: "+mCurrentMoveType+":"+mFinishMoveType+":"+mPageType);
			switch(mCurrentMoveType)
			{
				case MOVE_TYPE_TOP:
					if(Math.abs(dst - src) > 10)
					{
						if(duration == 0)
						{
							duration = Math.abs(dst - src) * VERTICAL_DURATION / ShareData.m_screenHeight;
						}
						if(mFinishMoveType == MOVE_TYPE_TOP)
						{
							if(duration < DEFAULT_DURATION)
							{
								duration = DEFAULT_DURATION;
							}
							if(duration > VERTICAL_DURATION / 2)
							{
								duration = VERTICAL_DURATION / 2;
							}
							mScroller.startScroll(0, src, 0, dst - src, duration);
							invalidate();
						}
						else
						{
							//回弹效果，不使用mScroller
							startTopAnimation(src, new AnimatorHolder.AnimatorListener()
							{
								@Override
								public void OnAnimationStart()
								{

								}

								@Override
								public void OnAnimationEnd()
								{
									ScrollOk();
								}
							}, false);

//							duration = Math.abs(dst - src) * VERTICAL_DURATION * 2 / ShareData.m_screenHeight;
//							if(duration < DURATION_RESTORE)
//							{
//								duration = DURATION_RESTORE;
//							}
//							int rebound = Math.abs(src / 4);
//							if(rebound > MAX_REBOUND)
//							{
//								rebound = MAX_REBOUND;
//							}
//							mCB.TopReStore(rebound*1.0f/MAX_REBOUND);
//							logoRestoreAnimator.removeAllListeners();
//							logoRestoreAnimator.setIntValues(src, -rebound, 0);
//							logoRestoreAnimator.setDuration(duration);
//							logoRestoreAnimator.addListener(new AnimatorListenerAdapter()
//							{
//								@Override
//								public void onAnimationStart(Animator animation)
//								{
////				mUiEnabled = false;
//								}
//
//								@Override
//								public void onAnimationEnd(Animator animation)
//								{
//									ScrollOk();
//								}
//							});
//							logoRestoreAnimator.start();
						}
					}
					else
					{
						SetScroll(mCurrentMoveType, (int)mCurrentX, dst);
						ScrollOk();
					}
					break;
				case MOVE_TYPE_BOTTOM:
					if(Math.abs(dst - src) > 10)
					{
						if(duration == 0)
						{
							duration = Math.abs(dst - src) * VERTICAL_DURATION / ShareData.m_screenHeight;
						}
						if(duration < DEFAULT_DURATION)
						{
							duration = DEFAULT_DURATION;
						}
						if(duration > VERTICAL_DURATION / 2)
						{
							duration = VERTICAL_DURATION / 2;
						}
						mScroller.startScroll(0, src, 0, dst - src, duration);
						invalidate();
					}
					else
					{
						SetScroll(mCurrentMoveType, (int)mCurrentX, dst);
						ScrollOk();
					}
					break;

				case MOVE_TYPE_LEFT:
				case MOVE_TYPE_RIGHT:
					if(Math.abs(dst - src) > 10)
					{
						if(duration == 0)
						{
							duration = Math.abs(dst - src) * HORIZONTAL_DURATION / ShareData.m_screenWidth;
						}
						if(duration < DEFAULT_DURATION)
						{
							duration = DEFAULT_DURATION;
						}
						if(duration > HORIZONTAL_DURATION / 2)
						{
							duration = HORIZONTAL_DURATION / 2;
						}
						mScroller.startScroll(src, 0, dst - src, 0, duration);
						invalidate();
					}
					else
					{
						SetScroll(mCurrentMoveType, dst, (int)mCurrentY);
						ScrollOk();
					}
					break;

				default:
					ScrollOk();
					break;
			}
		}
	}

	private void InitView(int type)
	{
		switch(type)
		{
			case MOVE_TYPE_BOTTOM:
				mCB.InitBottom();
				break;

			case MOVE_TYPE_LEFT:
//				isBootOrMenu = mCB.InitLeft();
				mCB.InitLeft();
				break;

			case MOVE_TYPE_RIGHT:
				mCB.InitRight();
				break;

			case MOVE_TYPE_TOP:
				mCB.InitTop();
				break;
			case MOVE_TYPE_NONE:
				mCB.InitMain();
			default:
				break;
		}
	}

	protected void SetScroll(int type, int x, int y)
	{
		switch(type)
		{
			case MOVE_TYPE_BOTTOM:
//				mCB.SetBottom(x, y, Math.abs((float)(y - mMimValue) / (float)(mMaxValue - mMimValue)));
				mCB.SetBottom(x, y, Math.abs((float)(mMaxValue - y) / (float)(mMaxValue - mMimValue)));
				break;

			case MOVE_TYPE_RIGHT:
//				mCB.SetRight(x, y, Math.abs((float)(x - mMimValue) / (float)(mMaxValue - mMimValue)));
				mCB.SetRight(x, y, Math.abs((float)(mMaxValue - x) / (float)(mMaxValue - mMimValue)));
				break;

			case MOVE_TYPE_LEFT:
				mCB.SetLeft(x, y, Math.abs((float)(x - mMimValue) / (float)(mMaxValue - mMimValue)));
				break;

			case MOVE_TYPE_TOP:
				mCB.SetTop(x, y, Math.abs((float)(y - mMimValue) / (float)(mMaxValue - mMimValue)));
				break;
		}
	}

	protected static final int INVALIDATE_MSG = 100;
	protected Handler mHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			if(msg.what == INVALIDATE_MSG)
			{
				if(mScroller.computeScrollOffset())
				{
					switch(mCurrentMoveType)
					{
						case MOVE_TYPE_BOTTOM:
						case MOVE_TYPE_TOP:
							SetScroll(mCurrentMoveType, (int)mCurrentX, mScroller.getCurrY());
							break;

						case MOVE_TYPE_LEFT:
						case MOVE_TYPE_RIGHT:
							SetScroll(mCurrentMoveType, mScroller.getCurrX(), (int)mCurrentY);
							break;

						default:
							break;
					}
					invalidate();
				}
				else
				{
//					Log.i(TAG, "handleMessage: isIntercept"+isIntercept);
					//是否被拦截
//					if(!isIntercept)
//					{
//						ScrollOk();
//					}
					//卡顿的时候   onDown 中途拦截isIntercept  true，信息还没到  onUp已经设置为false
					if(!isIntercept && isOnGesture)
					{
						ScrollOk();
					}
				}
			}
		}
	};


	public void onClose()
	{
		isIntercept = true;
		mScroller.abortAnimation();
	}


	public void openPage(int mode,boolean isHasAmn)
	{
		if(mUiEnabled)
		{
			mCB.onStart(MOVE_TYPE_NONE, mode, false);
			InitView(mode);
			mCurrentMoveType = mode;
			mFinishMoveType = mode;
			int dst = 0;
			int src = 0;
			mUiEnabled = false;
			isIntercept = false;
			isOnGesture = true;                 //SetScrollOk 需要判断
			mScroller.abortAnimation();
			switch (mode)
			{
				case MOVE_TYPE_LEFT:
					src = -mCB.GetLeftW();
					dst = 0;
					mMimValue = src;
					mMaxValue = dst;
					if (isHasAmn)
					{
						mScroller.startScroll(src, 0, dst - src, 0, HORIZONTAL_DURATION / 2);
						invalidate();
					} else
					{
						SetScroll(mode, dst, 0);
						ScrollOk();
					}
					break;
				case MOVE_TYPE_TOP:
					src = 0;
					dst = mCB.GetTopH();
					mMimValue = src;
					mMaxValue = dst;
					if (isHasAmn)
					{
						mScroller.startScroll(0, src, 0, dst - src, VERTICAL_DURATION / 2);
						invalidate();
					} else
					{
						SetScroll(mode, 0, dst);
						ScrollOk();
					}
					break;
				case MOVE_TYPE_RIGHT:
					src = mCB.GetRightW();
					dst = 0;
					mMimValue = dst;
					mMaxValue = src;
					if (isHasAmn)
					{
						mScroller.startScroll(src, 0, dst - src, 0, HORIZONTAL_DURATION / 2);
						invalidate();
					} else
					{
						SetScroll(mode, dst, 0);
						ScrollOk();
					}
					break;
				case MOVE_TYPE_BOTTOM:
					src = mCB.GetBottomH();
					dst = 0;
					mMimValue = dst;
					mMaxValue = src;
					if (isHasAmn)
					{
						mScroller.startScroll(0, src, 0, dst - src, VERTICAL_DURATION / 2);
						invalidate();
					} else
					{
						SetScroll(mode, 0, dst);
						ScrollOk();
					}
					break;
				case MOVE_TYPE_NONE:
					ScrollOk();
				default:
					break;
			}
		}
	}

	/**
	 * 主页到其他页面
	 *
	 * @param mode
	 */
	public void openPage(int mode)
	{
		openPage(mode,true);
	}

	public void closePage(int mode ,boolean isHasAmn)
	{
		if(mUiEnabled)
		{
			mCB.onStart(mode, MOVE_TYPE_NONE, false);
			InitView(MOVE_TYPE_NONE);
			mCurrentMoveType = mode;
			int dst = 0;
			int src = 0;
			mUiEnabled = false;
			isIntercept = false;
			isOnGesture = true;            //SetScrollOk 需要判断
			mScroller.abortAnimation();
			switch (mode)
			{
				case MOVE_TYPE_LEFT:
					src = 0;
					dst = -mCB.GetLeftW();
					mMimValue = dst;
					mMaxValue = src;
					if (isHasAmn)
					{
						mScroller.startScroll(src, 0, dst - src, 0, HORIZONTAL_DURATION / 2);
						invalidate();
					} else
					{
						SetScroll(mode, dst, 0);
						ScrollOk();
					}
					break;
				case MOVE_TYPE_TOP:
					src = mCB.GetTopH();
					dst = 0;
					mMimValue = dst;
					mMaxValue = src;
					if (isHasAmn)
					{
						mScroller.startScroll(0, src, 0, dst - src, VERTICAL_DURATION / 2);
						invalidate();
					} else
					{
						SetScroll(mode, 0, dst);
						ScrollOk();
					}
					break;
				case MOVE_TYPE_RIGHT:
					src = 0;
					dst = mCB.GetRightW();
					mMimValue = src;
					mMaxValue = dst;
					if (isHasAmn)
					{
						mScroller.startScroll(src, 0, dst - src, 0, HORIZONTAL_DURATION / 2);
						invalidate();
					} else
					{
						SetScroll(mode, dst, 0);
						ScrollOk();
					}
					break;
				case MOVE_TYPE_BOTTOM:
					src = 0;
					dst = mCB.GetBottomH();
					mMimValue = src;
					mMaxValue = dst;
					if (isHasAmn)
					{
						mScroller.startScroll(0, src, 0, dst - src, VERTICAL_DURATION / 2);
						invalidate();
					} else
					{
						SetScroll(mode, 0, dst);
						ScrollOk();
					}
					break;
				default:
					break;
			}
		}
	}

	/**
	 * 其他页面到主页
	 *
	 * @param mode
	 */
	public void closePage(int mode)
	{
		closePage(mode,true);
	}
	public void startTopAnimation(int h, final AnimatorHolder.AnimatorListener listener, boolean isIntroAnimation)
	{
//		int[] values = new int[]{h,-rebound,0,-rebound/2,0,-rebound/4,0};
		final int rebound;
		if(Math.abs(h / 6) > MAX_REBOUND)
		{
			rebound = MAX_REBOUND;
		}else{
			rebound =  Math.abs(h / 6);
		}
		int[] values = new int[]{h, -rebound, 0};
		logoRestoreAnimator.setIntValues(values);
		int delayTime = 0;
		if(isIntroAnimation){
			delayTime = 250;
			logoRestoreAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
		}else{
			delayTime = 60;
			logoRestoreAnimator.setInterpolator(new DecelerateInterpolator());
		}
		mHandler.postDelayed(new Runnable()
		{
			@Override
			public void run()
			{
				mCB.TopReStore(rebound * 1.0f / MAX_REBOUND);
			}
		}, delayTime);
		logoRestoreAnimator.addListener(new AnimatorListenerAdapter()
		{
			@Override
			public void onAnimationStart(Animator animation)
			{
				if(listener != null)
				{
					listener.OnAnimationStart();
				}
			}

			@Override
			public void onAnimationEnd(Animator animation)
			{
				if(listener != null)
				{
					listener.OnAnimationEnd();
				}
				logoRestoreAnimator.removeListener(this);
			}
		});
		logoRestoreAnimator.start();
	}

	public void introAnimation(final AnimatorHolder.AnimatorListener listener)
	{
		mMimValue = 0;
		mMaxValue = mCB.GetTopH();
		startTopAnimation(mCB.GetCurrentTopY(),listener,true);
	}

	public void setPageType(int mPageType)
	{
		this.mPageType = mPageType;
	}

	public int getPageType()
	{
		return mPageType;
	}

	public boolean isOnGesture()
	{
		return isOnGesture;
	}

	protected void ScrollOk()
	{
//		if(mFinishMoveType == MOVE_TYPE_NONE){
//			isBootOrMenu = false;
//		}
//		Log.i(TAG, "ScrollOk: "+mPageType+":"+mFinishMoveType);
		mCB.onEnd(mPageType, mFinishMoveType,mUiEnabled);
		mPageType = mFinishMoveType;
		mFinishMoveType = MOVE_TYPE_NONE;
		mCurrentMoveType = MOVE_TYPE_NONE;
		mVelocityY = 0;
		mVelocityX = 0;
		mUiEnabled = true;
		isOnGesture = false;
		isIntercept = false;
	}

	protected void invalidate()
	{
		Message msg = mHandler.obtainMessage();
		msg.what = INVALIDATE_MSG;
		mHandler.sendMessage(msg);
	}


//	public boolean onBack()
//	{
//		if(mPageType == MOVE_TYPE_NONE)
//		{
//			return false;
//		}
//		else
//		{
//			closePage(mPageType);
//			return true;
//		}
//	}


	@Override
	public boolean onDown(MotionEvent e)
	{
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e)
	{

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e)
	{
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY)
	{
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e)
	{

	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
	{
		if(e1 != null && e2 != null)
		{
			if(Math.abs(e1.getX() - e2.getX()) > TOUCH_SIZE || Math.abs(e1.getY() - e2.getY()) > TOUCH_SIZE)
			{
				mVelocityX = velocityX;
				mVelocityY = velocityY;

			}
		}
		return true;
	}

	public interface Callback
	{
		void InitMain();

		boolean InitLeft();


		int GetLeftW();

		int GetCurrentLeftX();

		void SetLeft(int x, int y, float s);


		void InitTop();

		int GetTopH();

		int GetCurrentTopY();

		void SetTop(int x, int y, float s);

		boolean InitRight();


		int GetRightW();

		int GetCurrentRightX();

		void SetRight(int x, int y, float s);

		boolean InitBottom();

		int GetBottomH();

		int GetCurrentBottomY();

		void SetBottom(int x, int y, float s);

		/**
		 * down的时候获取外部状态，判断是否可操作
		 */
		boolean CanControl();

		/**
		 * 相册上下滚动冲突，判断是否到顶部拦截
		 */
		boolean CanBottomIntercept();

		void onStart(int fromType, int endType,boolean isSlide);

		void onEnd(int fromType, int endType, boolean isSlide);

		/**
		 *
		 * @param reBoundPercent    MAX_REBOUND 的比例,1 为最大
		 */
		void TopReStore(float reBoundPercent);
	}
}
