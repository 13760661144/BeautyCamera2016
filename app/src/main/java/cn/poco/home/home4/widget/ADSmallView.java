package cn.poco.home.home4.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.view.View;

import static cn.poco.home.home4.utils.PercentUtil.HeightPxToPercent;

/**
 * Created by lgd on 2016/12/21.
 */
public class ADSmallView extends View
{
	private Paint mPaint;
	private int mStrokeWidth;
	private int mBitmapRadius;
	private Bitmap mBitmap;
	private Rect mDst;
	public ADSmallView(Context context)
	{
		super(context);
		init();
	}

	private void init()
	{
		mPaint = new Paint();
		mPaint.setColor(0x87ffffff);
		mPaint.setAntiAlias(true);
		mPaint.setStyle(Paint.Style.STROKE);
		mStrokeWidth = HeightPxToPercent(2);
		mPaint.setStrokeWidth(mStrokeWidth);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		int radius;
		if(getWidth() <= getHeight())
		{
			radius = getWidth() / 2;
		}
		else
		{
			radius = getHeight() / 2;
		}
		mBitmapRadius = radius - mStrokeWidth;
		mDst = new Rect(getWidth()/2 - mBitmapRadius, getHeight()/2 - mBitmapRadius, getWidth()/2 + mBitmapRadius, getHeight()/2 + mBitmapRadius);
		super.onSizeChanged(w, h, oldw, oldh);
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		if(getWidth() == 0 || getHeight() == 0 || mBitmapRadius == 0)
		{
			return;
		}
		canvas.drawCircle(getWidth() / 2, getHeight() / 2, getWidth() / 2 - mStrokeWidth / 2, mPaint);
		if(mBitmap != null)
		{
			canvas.drawBitmap(createCircleImage(mBitmap, mBitmapRadius * 2), null, mDst, null);
		}
	}

	/**
	 * 根据原图和变长绘制圆形图片
	 *
	 * @param source
	 * @param min
	 * @return
	 */
	private Bitmap createCircleImage(Bitmap source, int min)
	{
		final Paint paint = new Paint();
		paint.setAntiAlias(true);
		Bitmap target = Bitmap.createBitmap(min, min, Bitmap.Config.ARGB_8888);
		/**
		 * 产生一个同样大小的画布
		 */
		Canvas canvas = new Canvas(target);
		canvas.drawCircle(min / 2, min / 2, min / 2, paint);
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		/**
		 * 绘制图片
		 */
		source = Bitmap.createScaledBitmap(source, min, min, true);
		canvas.drawBitmap(source, 0, 0, paint);
//		canvas.drawBitmap(source, new Rect(0, 0, getWidth(), getHeight()),  new RectF(0, 0, getWidth(), getHeight()), paint);
		return target;
	}

	public void setStrokeColor(int color)
	{
		mPaint.setColor(color);
		invalidate();
	}

	public void setStrokeWidth(int strokeWidth)
	{
		this.mStrokeWidth = strokeWidth;
		mBitmapRadius = getWidth()/2 - mStrokeWidth;
		invalidate();
	}

	public void setBitmap(Bitmap bitmap)
	{
		this.mBitmap = bitmap;
		invalidate();
	}
}
