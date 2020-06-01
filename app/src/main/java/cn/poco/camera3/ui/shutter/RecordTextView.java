package cn.poco.camera3.ui.shutter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.View;

import cn.poco.camera3.util.CameraPercentUtil;

/**
 * 录制时长
 * Created by Gxx on 2017/12/12.
 */

public class RecordTextView extends View
{
	private Paint mPaint;
	private Paint mTextPaint;
	private String mText;

	private int mW;
	private int mH;

	public RecordTextView(Context context)
	{
		super(context);
		mTextPaint = new Paint();
		mTextPaint.setAntiAlias(true);
		mTextPaint.setDither(true);
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		mPaint.setColor(0xfffc3745);
	}

	public void setText(String text)
	{
		mText = text;
	}

	public void setTextSize(int unit, float size, boolean bold)
	{
		mTextPaint.setTextSize(TypedValue.applyDimension(unit, size, getResources().getDisplayMetrics()));
		mTextPaint.setTypeface(bold ? Typeface.DEFAULT_BOLD : Typeface.DEFAULT);
	}

	public void setTextColor(int color)
	{
		mTextPaint.setColor(color);
	}

	public void setShadowLayer(float radius, float dx, float dy, int shadowColor)
	{
		mTextPaint.setShadowLayer(radius, dx, dy, shadowColor);
		mPaint.setShadowLayer(radius, dx, dy, shadowColor);
	}

	public void updateUI()
	{
		invalidate();
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		super.onSizeChanged(w, h, oldw, oldh);

		mW = w;
		mH = h;
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		if(mText == null || mText.equals("")) return;

		float text_width = mTextPaint.measureText(mText);
		Paint.FontMetrics metrics = mTextPaint.getFontMetrics();

		float x = (mW - text_width) / 2f;
		float y = mH / 2f + (metrics.bottom - metrics.top) / 2f - metrics.bottom;
		canvas.save();
		canvas.drawText(mText, x, y, mTextPaint);
		canvas.restore();

		canvas.save();
		canvas.drawCircle(x - CameraPercentUtil.WidthPxToPercent(13), mH / 2f, CameraPercentUtil.WidthPxToPercent(5), mPaint);
		canvas.restore();
	}
}
