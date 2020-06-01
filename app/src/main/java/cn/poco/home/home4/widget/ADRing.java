package cn.poco.home.home4.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import cn.poco.home.home4.utils.PercentUtil;


/**
 * Created by lgd on 2016/11/23.
 * 外圈
 */

public class ADRing extends View
{
	private Paint mPaint;
	private int strokeWidth;
	public ADRing(Context context)
	{
		super(context);
		init();
	}

	private void init()
	{
		mPaint = new Paint();
		mPaint.setColor(Color.WHITE);
		mPaint.setAlpha(102);
		mPaint.setAntiAlias(true);
		mPaint.setStyle(Paint.Style.STROKE);
		strokeWidth = PercentUtil.HeightPxToPercent(2);
		mPaint.setStrokeWidth(strokeWidth);
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		canvas.drawCircle(getWidth()/2,getHeight()/2,getWidth()/2-strokeWidth/2,mPaint);
	}
}
