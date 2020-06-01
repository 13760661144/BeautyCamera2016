package cn.poco.camera3.ui.tab;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.view.View;

import cn.poco.camera3.util.CameraPercentUtil;

public class PointView extends View
{
	protected Paint mPaint;
	protected int mPointColor;
	protected int mViewW, mViewH;
	protected boolean mIsDrawPoint;
	protected float mRadius;
	protected boolean mIsDrawShadow;
	protected float mShadowRadius;
	protected RadialGradient mGradient;

	public PointView(Context context)
	{
		super(context);

		mPaint = new Paint();
		mPointColor = Color.WHITE;
		mRadius = CameraPercentUtil.WidthPxToPercent(6);
		mShadowRadius = CameraPercentUtil.WidthPxToPercent(9);

		if (mShadowRadius == 0)
		{
			mShadowRadius = 9;
		}

		mGradient = new RadialGradient(0, 0, mShadowRadius,
				new int[]{0x4D000000, 0x4D000000, 0x00000000},
				new float[]{0f, 0.4f, 1f}, Shader.TileMode.CLAMP);
	}

	public void setPointColor(int color, boolean isDrawShadow)
	{
		mPointColor = color;
		mIsDrawShadow = isDrawShadow;
		invalidate();
	}

	public void ShowPoint(boolean show, boolean updateUI)
	{
		mIsDrawPoint = show;
		if(updateUI)
			invalidate();
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		super.onSizeChanged(w, h, oldw, oldh);
		mViewW = w;
		mViewH = h;
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		if (mIsDrawShadow)
		{
			canvas.save();
			mPaint.reset();
			mPaint.setAntiAlias(true);
			mPaint.setShader(mGradient);
			canvas.translate(mViewW/2f, mViewH/2f);
			canvas.drawCircle(0, 0, mShadowRadius, mPaint);
			canvas.restore();
		}

		if(mIsDrawPoint)
		{
			canvas.save();
			mPaint.reset();
			mPaint.setColor(mPointColor);
			mPaint.setAntiAlias(true);
			mPaint.setStyle(Paint.Style.FILL);
			canvas.drawCircle(mViewW/2f, mViewH/2f, mRadius, mPaint);
			canvas.restore();
		}
	}
}
