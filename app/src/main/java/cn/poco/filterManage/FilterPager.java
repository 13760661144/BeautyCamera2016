package cn.poco.filterManage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.VelocityTrackerCompat;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.view.animation.Interpolator;
import android.widget.Scroller;

import java.util.ArrayList;
import java.util.List;

import cn.poco.advanced.ImageUtils;
import cn.poco.tianutils.ShareData;

/**
 * Created by: fwc
 * Date: 2017/5/3
 */
public class FilterPager extends View {

	private static final int SIZE = 1000;
	private static final int INTERVAL_TIME = 2000;
	private static final int MSG_WHAT = 0x12;

	private static final int NONE = 0;
	private static final int LEFT = 1;
	private static final int RIGHT = 1 << 2;

	private static final int INVALID_POINTER = -1;

	private static final int MAX_SETTLE_DURATION = 600; // ms
	private static final int MIN_DISTANCE_FOR_FLING = 25; // dips
	private static final int MIN_FLING_VELOCITY = 400; // dips

	private static final Interpolator sInterpolator = new Interpolator() {

		@Override
		public float getInterpolation(float t) {
			t -= 1.0f;
			return t * t * t * t * t + 1.0f;
		}
	};

	private int mWidth;
	private int mHeight;

	private Bitmap mBitmap;
	private Canvas mCanvas;

	private int mCurrentPosition;
	private int mDotPosition;
	private int mNextPosition;

	private List<FilterPage> mItems;

	private int mStartScrollX;
	private int mDirection;

	private float mLastMotionX;
	private float mLastMotionY;
	private float mInitialMotionX;
	private float mInitialMotionY;

	private int mActivePointerId = INVALID_POINTER;

	private int mTouchSlop;

	private VelocityTracker mVelocityTracker;
	private int mMinimumVelocity;
	private int mMaximumVelocity;
	private int mFlingDistance;

	private Scroller mScroller;

	private boolean mIsBeingDragged;

	private OnImageScrollListener mOnImageScrollListener;

	private OnClickListener mOnClickListener;

	private float mRadius;
	private Paint mPaint;
	private BitmapShader mBitmapShader;
	private RectF mRect;

	private float mHasScrollTempX;

	private Paint mTextPaint;
	private Handler mHandler;

	private boolean mScroll = false;

	private int mScrollCurrX;

	public FilterPager(Context context) {
		super(context);

		init();
	}

	public FilterPager(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);

		init();
	}

	private void init() {

		setFocusable(true);
		final Context context = getContext();
		mScroller = new Scroller(context, sInterpolator);
		final ViewConfiguration configuration = ViewConfiguration.get(context);
		final float density = context.getResources().getDisplayMetrics().density;

		mTouchSlop = configuration.getScaledPagingTouchSlop();
		mMinimumVelocity = (int) (MIN_FLING_VELOCITY * density);
		mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();

		mFlingDistance = (int) (MIN_DISTANCE_FOR_FLING * density);

		mRadius = density * 10;

		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);

		mTextPaint = new Paint();
		mTextPaint.setAntiAlias(true);
		mTextPaint.setColor(Color.WHITE);
		mTextPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 22, getResources().getDisplayMetrics()));
		mTextPaint.setShadowLayer(3, 0, 2, 0x1a000000);

		mItems = new ArrayList<>();

		mHandler = new Handler(new Handler.Callback() {
			@Override
			public boolean handleMessage(Message msg) {
				if (msg.what == MSG_WHAT) {
					if (mItems.size() >= 2 && mScroller.isFinished() && !mIsBeingDragged) {
						mStartScrollX = 0;
						mScroll = true;
						mDirection = RIGHT;
						mNextPosition = mCurrentPosition + 1;
						mScroller.startScroll(0, 0, mWidth, 0, 1000);
						ViewCompat.postInvalidateOnAnimation(FilterPager.this);

						mHandler.removeMessages(MSG_WHAT);
						mHandler.sendEmptyMessageDelayed(MSG_WHAT, INTERVAL_TIME);
					}
				}
				return true;
			}
		});
	}

	public void addItem(FilterPage page, boolean select) {
		if (page.bitmap != null) {
			mItems.add(page);

			if (select) {
				mDotPosition = mCurrentPosition = mNextPosition = mItems.size() - 1;
				drawBitmap(mCurrentPosition, 0, NONE);
			}

			ViewCompat.postInvalidateOnAnimation(this);
		}

		if (mItems.size() >= 2) {
			mHandler.removeMessages(MSG_WHAT);
			mHandler.sendEmptyMessageDelayed(MSG_WHAT, 1000);
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(getSize(widthMeasureSpec), getSize(widthMeasureSpec));
	}

	private int getSize(int measureSpec) {

		int result;

		int mode = MeasureSpec.getMode(measureSpec);
		int size = MeasureSpec.getSize(measureSpec);

		if (mode == MeasureSpec.EXACTLY) {
			result = size;
		} else {
			result = SIZE > size ? size : SIZE;
		}

		return result;
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		mWidth = w;
		mHeight = h;

		mBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
		mCanvas = new Canvas(mBitmap);

		mBitmapShader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
		mPaint.setShader(mBitmapShader);
		mRect = new RectF(0, 0, w, h);

		drawBitmap(mCurrentPosition, 0, NONE);
	}

	private Bitmap getBitmap(int position) {
		int index = (position + mItems.size()) % mItems.size();
		return mItems.get(index).bitmap;
	}

	private Matrix getDrawMatrix(Bitmap bitmap) {

		Matrix matrix = new Matrix();
		float scaleW = mWidth * 1f / bitmap.getWidth();
		float scaleH = mHeight * 1f / bitmap.getHeight();
		if (scaleW > scaleH) {
			final float finalH = bitmap.getHeight() * scaleW;
			matrix.setScale(scaleW, scaleW);
			float dy = (mHeight - finalH) / 2f;
			if (dy > 0) {
				dy = -dy;
			}
			matrix.postTranslate(0, dy);
		} else {
			final float finalW = bitmap.getWidth() * scaleH;
			matrix.setScale(scaleH, scaleH);
			float dx = (mWidth - finalW) / 2f;
			if (dx > 0) {
				dx = -dx;
			}
			matrix.postTranslate(dx, 0);
		}

		return matrix;
	}

	private void drawBitmap(int position, float percent, int direction) {

		if (mItems.isEmpty()) {
			return;
		}

		Bitmap bitmap = getBitmap(position);
		Matrix matrix = getDrawMatrix(bitmap);

		int next = position;
		float clipWidth = (1 - percent) * mWidth;

		mCanvas.save();
		if (direction == LEFT) {
			next--;
			mCanvas.clipRect(mWidth - clipWidth, 0, mWidth, mHeight);
		} else if (direction == RIGHT) {
			next++;
			mCanvas.clipRect(0, 0, clipWidth, mHeight);
		}
		mCanvas.drawBitmap(bitmap, matrix, null);
		mCanvas.restore();

		if (direction == NONE) {
			return;
		}

		bitmap = getBitmap(next);
		matrix = getDrawMatrix(bitmap);

		mCanvas.save();
		if (direction == LEFT) {
			mCanvas.clipRect(0, 0, mWidth - clipWidth, mHeight);
		} else if (direction == RIGHT) {
			mCanvas.clipRect(clipWidth, 0, mWidth, mHeight);
		}
		mCanvas.drawBitmap(bitmap, matrix, null);
		mCanvas.restore();
	}

	@Override
	protected void onDraw(Canvas canvas) {

		if (mItems.isEmpty()) {
			return;
		}

		canvas.drawRoundRect(mRect, mRadius, mRadius, mPaint);

		if (mItems.size() > mDotPosition && !TextUtils.isEmpty(mItems.get(mDotPosition).name)) {
			mTextPaint.setColor(Color.WHITE);
			canvas.drawText(mItems.get(mDotPosition).name, ShareData.PxToDpi_xhdpi(20), mHeight - ShareData.PxToDpi_xhdpi(24), mTextPaint);
		}

		if (mItems != null && !mItems.isEmpty()) {

			int bottom, right;
			right = bottom = ShareData.PxToDpi_xhdpi(24);
			float radius = ShareData.PxToDpi_xhdpi(6);
			int delta = ShareData.PxToDpi_xhdpi(20);
			for (int i = mItems.size() - 1; i >= 0; i--) {
				if (i == mDotPosition) {
					mTextPaint.setColor(ImageUtils.GetSkinColor(0xffe75988));
				} else {
					mTextPaint.setColor(Color.WHITE);
				}
				canvas.drawCircle(mWidth - right - radius, mHeight - bottom - radius, radius, mTextPaint);
				right += delta;
			}
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		if (mItems.size() < 2) {
			return super.onTouchEvent(event);
		}

		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
		}
		mVelocityTracker.addMovement(event);

		switch (event.getActionMasked()) {
			case MotionEvent.ACTION_DOWN: {
				if (!mScroller.isFinished()) {
					mScrollCurrX = mScroller.getCurrX();
					mScroller.abortAnimation();
				} else {
					mScrollCurrX = 0;
				}

				mLastMotionX = mInitialMotionX = event.getX();
				mLastMotionY = mInitialMotionY = event.getY();
				mActivePointerId = event.getPointerId(0);

				mHasScrollTempX = 0;

				ViewParent parent = getParent();
				if (parent != null) {
					parent.requestDisallowInterceptTouchEvent(true);
				}
				break;
			}
			case MotionEvent.ACTION_MOVE:
				if (!mIsBeingDragged) {
					int pointerIndex = event.findPointerIndex(mActivePointerId);
                    if (pointerIndex < 0 || pointerIndex >= event.getPointerCount()) {
                        break;
                    }
					final float x = event.getX(pointerIndex);
					final float xDiff = Math.abs(x - mLastMotionX);
					final float y = event.getY(pointerIndex);
					final float yDiff = Math.abs(y - mLastMotionY);

					if (xDiff > mTouchSlop) {
						mIsBeingDragged = true;
						mHandler.removeMessages(MSG_WHAT);
						mLastMotionX = x - mInitialMotionX > 0 ? mInitialMotionX + mTouchSlop :
								mInitialMotionX - mTouchSlop;
						mLastMotionY = y;

//						ViewParent parent = getParent();
//						if (parent != null) {
//							parent.requestDisallowInterceptTouchEvent(true);
//						}
					} else if (yDiff > 2 * mTouchSlop) {
						ViewParent parent = getParent();
						if (parent != null) {
							parent.requestDisallowInterceptTouchEvent(false);
						}
					}
				}

				if (mIsBeingDragged) {
					final int activePointerIndex = event.findPointerIndex(mActivePointerId);
                    if (activePointerIndex < 0 || activePointerIndex >= event.getPointerCount()) {
                        break;
                    }
					final float x = event.getX(activePointerIndex);
					final float delta = x - mLastMotionX;
					mHasScrollTempX += delta;
					final int totalDelta = (int)mHasScrollTempX;
					if (totalDelta > 0) {
						drawBitmap(mCurrentPosition, totalDelta * 1f / mWidth, LEFT);
					} else {
						drawBitmap(mCurrentPosition, -totalDelta * 1f / mWidth, RIGHT);
					}

					mLastMotionX = x;
					ViewCompat.postInvalidateOnAnimation(this);
				}
				break;
			case MotionEvent.ACTION_UP:
				mScrollCurrX = 0;
				if (mIsBeingDragged) {
					final VelocityTracker velocityTracker = mVelocityTracker;
					velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
					int initialVelocity = (int) VelocityTrackerCompat.getXVelocity(velocityTracker, mActivePointerId);
					final int activePointerIndex = event.findPointerIndex(mActivePointerId);
                    if (activePointerIndex < 0 || activePointerIndex >= event.getPointerCount()) {
                        break;
                    }
					final float x = event.getX(activePointerIndex);
					final int totalDelta = (int) (x - mInitialMotionX);

					dispatchScroll(initialVelocity, totalDelta);
				} else {
					if (mOnClickListener != null) {
						mOnClickListener.onClick(this);
					}
				}
				break;
			case MotionEvent.ACTION_CANCEL:

				if (mIsBeingDragged) {
					dispatchScroll(0, 0);
				} else if (mScrollCurrX != 0 && Math.abs(mScrollCurrX) != mWidth) {
					mStartScrollX = mScrollCurrX;
					int dx = 0;
					if (mScrollCurrX > 0) {
						dx = mWidth - mScrollCurrX;
					} else {
						dx = mWidth + mScrollCurrX;
					}

					// 由于调用mScroller.abortAnimation()导致滑动结束
					if (!mScroll) {
						mNextPosition = mCurrentPosition;
						mCurrentPosition = (mCurrentPosition - 1 + mItems.size()) % mItems.size() ;
						mDotPosition = mCurrentPosition;
						mScrollCurrX = 0;
					}

					int duration = (int)(Math.abs(dx) * 2f / mWidth * 300);
					duration = Math.min(duration, MAX_SETTLE_DURATION);

					mScroll = true;
					mScroller.startScroll(0, 0, dx, 0, duration);
					ViewCompat.postInvalidateOnAnimation(this);
				}
				break;
			case MotionEventCompat.ACTION_POINTER_DOWN: {
				final int index = event.getActionIndex();
				final float x = event.getX(index);
				mLastMotionX = x;
				mActivePointerId = event.getPointerId(index);
				break;
			}
			case MotionEventCompat.ACTION_POINTER_UP:
				onSecondaryPointerUp(event);
                int activePointerIndex = event.findPointerIndex(mActivePointerId);
                if (activePointerIndex < 0 || activePointerIndex >= event.getPointerCount()) {
                    break;
                }
				mLastMotionX = event.getX(activePointerIndex);
				break;
		}

		return true;
	}

	private void dispatchScroll(int velocity, int deltaX) {

		int dx;
		if (Math.abs(deltaX) > mFlingDistance && Math.abs(velocity) > mMinimumVelocity) {
			if (velocity > 0) {
				mDirection = LEFT;
				dx = mWidth - deltaX;
				mNextPosition = mCurrentPosition - 1;
			} else {
				mDirection = RIGHT;
				dx = -(mWidth + deltaX);
				mNextPosition = mCurrentPosition + 1;
			}

		} else if (Math.abs(deltaX) > mWidth / 3f) {
			if (deltaX > 0) {
				mDirection = LEFT;
				dx = mWidth - deltaX;
				mNextPosition = mCurrentPosition - 1;
			} else {
				mDirection = RIGHT;
				dx = -(mWidth + deltaX);
				mNextPosition = mCurrentPosition + 1;
			}
		} else {
			mDirection = deltaX > 0 ? LEFT : RIGHT;
			dx = -deltaX;
			mNextPosition = mCurrentPosition;
		}

		if (dx == 0) {
			mIsBeingDragged = false;

			return;
		}

		int duration = (int)(Math.abs(dx) * 2f / mWidth * 300);
		duration = Math.min(duration, MAX_SETTLE_DURATION);

		mIsBeingDragged = false;
		mScroll = true;
		mStartScrollX = deltaX;
		mScroller.startScroll(0, 0, dx, 0, duration);
		ViewCompat.postInvalidateOnAnimation(this);
	}

	@Override
	public void computeScroll() {
		if (!mScroller.isFinished() && mScroller.computeScrollOffset()) {

			mHandler.removeMessages(MSG_WHAT);

			final int x = mStartScrollX + mScroller.getCurrX();

			float percent = x * 1f / mWidth;
			if (percent < 0) {
				percent = -percent;
			}
			drawBitmap(mCurrentPosition, percent, mDirection);

			if (percent > 0.9f) {
				mDotPosition = (mNextPosition + mItems.size()) % mItems.size();
			}

			ViewCompat.postInvalidateOnAnimation(this);
		} else {
			// 滑动结束
			if (mScroll) {
				if (mOnImageScrollListener != null && mCurrentPosition != mNextPosition) {
					mOnImageScrollListener.onImageSelected(mNextPosition);
				}
				mDotPosition = mCurrentPosition = (mNextPosition + mItems.size()) % mItems.size();
				mStartScrollX = 0;
				mScroll = false;

				if (!mIsBeingDragged) {
					mHandler.sendEmptyMessageDelayed(MSG_WHAT, INTERVAL_TIME);
				}
			}
		}
	}

	private void onSecondaryPointerUp(MotionEvent ev) {
		final int pointerIndex = ev.getActionIndex();
		final int pointerId = ev.getPointerId(pointerIndex);
		if (pointerId == mActivePointerId) {
			final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
			mLastMotionX = ev.getX(newPointerIndex);
			mActivePointerId = ev.getPointerId(newPointerIndex);
			if (mVelocityTracker != null) {
				mVelocityTracker.clear();
			}
		}
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		mHandler.removeMessages(MSG_WHAT);
	}

	@Override
	public void setOnClickListener(OnClickListener listener) {
		super.setOnClickListener(listener);
		mOnClickListener = listener;
	}

	public void setOnImageScrollListener(OnImageScrollListener onImageScrollListener) {
		mOnImageScrollListener = onImageScrollListener;
	}

	public void setRadius(float radius) {
		mRadius = radius;
	}

	public interface OnImageScrollListener {
		void onImageSelected(int position);
	}

	public static class FilterPage {
		public Bitmap bitmap;
		public String name;
	}
}
