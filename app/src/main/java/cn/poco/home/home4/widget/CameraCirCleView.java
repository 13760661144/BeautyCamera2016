package cn.poco.home.home4.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import cn.poco.tianutils.ShareData;


/**
 * Created by lgd on 2016/11/22.
 */

public class CameraCirCleView extends View
{
	private Bitmap bitmapCenter;
	private boolean isDrawCenter = true;
	private boolean isDrawRing = true;
	private Rect mDestRect;
	private Paint mPaint;
	private int strokeWidth;

	public CameraCirCleView(Context context)
	{
		super(context);
		init();
	}

	public CameraCirCleView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init();
	}

	public CameraCirCleView(Context context, AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
		init();
	}

	private void init()
	{
		mPaint = new Paint();
		mPaint.setColor(Color.WHITE);
		mPaint.setAlpha(102);
		mPaint.setAntiAlias(true);
		mPaint.setStyle(Paint.Style.STROKE);
		strokeWidth = ShareData.PxToDpi_xhdpi(2);
		mPaint.setStrokeWidth(strokeWidth);
	}
	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		if(isDrawRing)
		{
			canvas.drawCircle(getWidth() / 2, getHeight() / 2, getWidth() / 2 - strokeWidth / 2, mPaint);
		}
		if(isDrawCenter){
			if(bitmapCenter != null)
			{
				int radius = bitmapCenter.getWidth() / 2;
				mDestRect = new Rect(getWidth() / 2 - radius, getHeight() / 2 - radius, getWidth() / 2 + radius, getHeight() / 2 + radius);
				canvas.drawBitmap(bitmapCenter, null, mDestRect, null);
			}
		}
	}
	public void setBitmapCenter(Bitmap bitmapCenter)
	{
		this.bitmapCenter = bitmapCenter;
	}

	public void setDrawCenter(boolean drawCenter)
	{
		isDrawCenter = drawCenter;
		this.invalidate();
	}

	public void setDrawRing(boolean drawRing)
	{
		isDrawRing = drawRing;
		this.invalidate();
	}
}
