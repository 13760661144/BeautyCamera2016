package cn.poco.beauty.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.support.annotation.DrawableRes;
import android.view.MotionEvent;
import android.view.View;

import cn.poco.advanced.ImageUtils;
import cn.poco.camera3.util.CameraPercentUtil;
import cn.poco.tianutils.ShareData;

/**
 * Created by: fwc
 * Date: 2017/1/10
 */
public class ColorSeekBar extends View {

	private int mWidth;
	private int mCircleSize;

	private Paint mPaint;
	private int mDefaultPaintWidth;
	private int mProgressPaintWidth;
	private int mCircleRadius;

	private int mMaxProgress = 100;
	private int mMinProgress = 0;
	private int mMaxMoveDistance;

	private int mProgress = 0;

	private int mBackgroundColor = 0x57000000;

	private boolean isDown = false;
	private float mCircleScale = 0.85f;

	private Bitmap mBackground;
	private int mCircleColor;

	private OnSeekBarChangeListener mOnSeekBarChangeListener;

	public ColorSeekBar(Context context) {
		super(context);

		init();
	}

	private void init() {

		mDefaultPaintWidth = CameraPercentUtil.WidthPxToPercent(2);
		mProgressPaintWidth = CameraPercentUtil.WidthPxToPercent(4);

		mCircleSize = CameraPercentUtil.WidthPxToPercent(48);
		mCircleRadius = mCircleSize / 2 - mDefaultPaintWidth;
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setStyle(Paint.Style.STROKE);

		mCircleColor = ImageUtils.GetSkinColor(0xffe75988);
	}

	public void setCircleSize(int size)
	{
		mCircleSize = size;
		mCircleRadius = mCircleSize / 2 - mDefaultPaintWidth;
		mMaxMoveDistance = mWidth - mCircleSize;
		invalidate();
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		mWidth = w;

		if (mBackground != null) {
			// 将背景填满
			Matrix matrix = new Matrix();
			matrix.setScale(mWidth * 1f / mBackground.getWidth(), 1f);
			mBackground = Bitmap.createBitmap(mBackground, 0, 0, mBackground.getWidth(), mBackground.getHeight(), matrix, false);
		}

		mMaxMoveDistance = w - mCircleSize;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		float circleLeft = mProgress * 1f / mMaxProgress * mMaxMoveDistance;

		if (mProgress > mMinProgress) {
			mPaint.setColor(ImageUtils.GetSkinColor(0xffe75988));
			mPaint.setStrokeWidth(mProgressPaintWidth);

			float end = circleLeft;
			if (!isDown) {
				end += mCircleRadius * (1 - mCircleScale);
			}

			if (mBackground != null) {
				Bitmap temp = Bitmap.createBitmap(mBackground, 0, 0, (int)end + 2, mBackground.getHeight());
				canvas.drawBitmap(temp, 0, getHeight() / 2, null);
				if (temp != null) {
					temp.recycle();
					temp = null;
				}
			} else {
				canvas.drawLine(0, getHeight() / 2f, end + 2, getHeight() / 2f, mPaint);
			}
		}

		mPaint.setColor(mCircleColor);
		mPaint.setStrokeWidth(mDefaultPaintWidth);
		float radius = mCircleRadius;
		if (!isDown) {
			radius *= mCircleScale;
		}
		canvas.drawCircle(circleLeft + mCircleSize / 2f, getHeight() / 2, radius, mPaint);

		if (mProgress < mMaxProgress) {
			mPaint.setColor(mBackgroundColor);
			float start = circleLeft;
			if (!isDown) {
				start -= mCircleRadius * (1 - mCircleScale);
			}
			if (mBackground != null) {
				Bitmap temp = Bitmap.createBitmap(mBackground, (int)start + mCircleSize - 2, 0,
										   mBackground.getWidth() - ((int)start + mCircleSize - 2), mBackground.getHeight());
				canvas.drawBitmap(temp, start + mCircleSize - 2, getHeight() / 2, null);
				if (temp != null) {
					temp.recycle();
					temp = null;
				}
			} else {
				canvas.drawLine(start + mCircleSize - 2, getHeight() / 2f, mWidth, getHeight() / 2f, mPaint);
			}
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);

		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);

		setMeasuredDimension(getMeasureSize(widthMode, widthSize), getMeasureSize(heightMode, heightSize));
	}

	private int getMeasureSize(int mode, int size) {
		int result;
		if (mode == MeasureSpec.AT_MOST) {
			result = ShareData.PxToDpi_xhdpi(150);
			if (result > size) {
				result = size;
			}
		} else {
			result = size;
		}

		return result;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		if (!isEnabled()) {
			return false;
		}

		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				isDown = true;
				mProgress = calculateProgress(event.getX());
				if (mOnSeekBarChangeListener != null) {
					mOnSeekBarChangeListener.onStartTrackingTouch(this);
				}
				break;
			case MotionEvent.ACTION_MOVE:
				mProgress = calculateProgress(event.getX());
				if (mOnSeekBarChangeListener != null) {
					mOnSeekBarChangeListener.onProgressChanged(this, mProgress);
				}
				break;
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL:
				isDown = false;
				if (mOnSeekBarChangeListener != null) {
					mOnSeekBarChangeListener.onStopTrackingTouch(this);
				}
				break;
		}

		invalidate();

		return true;
	}

	public void setOnSeekBarChangeListener(OnSeekBarChangeListener onSeekBarChangeListener) {
		mOnSeekBarChangeListener = onSeekBarChangeListener;
	}

	public int getCircleWith()
	{
		return mCircleSize;
	}

	private int calculateProgress(float x) {
		float realX = x - mCircleRadius;
		if (realX < 0) {
			realX = 0;
		}
		if (realX > mMaxMoveDistance) {
			realX = mMaxMoveDistance;
		}
		return (int)(realX / mMaxMoveDistance * mMaxProgress + 0.5f);
	}

	public void setMax(int max) {
		mMaxProgress = max;
	}

	public int getMax() {
		return mMaxProgress;
	}


	public void setMin(int min) {
		mMinProgress = min;
	}

	public void setBackgroundColor(int backgroundColor) {
		mBackgroundColor = backgroundColor;
	}

	public void setCircleScale(float scale) {
		mCircleScale = scale;
	}

	public void setProgress(int progress) {
		if (progress < 0) {
			progress = 0;
		}
		if (progress > 100) {
			progress = 100;
		}
		mProgress = progress;
		invalidate();
	}

	public int getProgress() {
		return mProgress;
	}

	public void setCircleColor(int circleColor) {
		mCircleColor = circleColor;
	}

	public void setBackground(@DrawableRes int resId) {
		mBackground = BitmapFactory.decodeResource(getResources(), resId);
	}

	public interface OnSeekBarChangeListener {

		void onProgressChanged(ColorSeekBar seekBar, int progress);

		void onStartTrackingTouch(ColorSeekBar seekBar);

		void onStopTrackingTouch(ColorSeekBar seekBar);
	}
}
