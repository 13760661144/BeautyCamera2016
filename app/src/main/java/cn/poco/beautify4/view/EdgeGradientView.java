package cn.poco.beautify4.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Shader;
import android.view.View;

/**
 * Created by: fwc
 * Date: 2017/2/15
 */
public class EdgeGradientView extends View {

	private Paint mTopPaint;
	private Paint mBottomPaint;

	private float mTopPosition;
	private float mBottomPosition;

	public EdgeGradientView(Context context) {
		super(context);

		mTopPaint = new Paint();
		mTopPaint.setAntiAlias(true);
		mTopPaint.setDither(true);

		mBottomPaint = new Paint();
		mBottomPaint.setAntiAlias(true);
		mBottomPaint.setDither(true);

		mTopPosition = 0.19f;
		mBottomPosition = 0.56f;
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		LinearGradient linearGradient = new LinearGradient(0, 0, 0, h * mTopPosition, 0xffffffff, 0x00ffffff, Shader.TileMode.CLAMP);
		mTopPaint.setShader(linearGradient);

		linearGradient = new LinearGradient(0, h * mBottomPosition, 0, h, 0x00ffffff, 0xffffffff, Shader.TileMode.CLAMP);
		mBottomPaint.setShader(linearGradient);
	}

	@Override
	protected void onDraw(Canvas canvas) {

		canvas.save();
		canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG));


		canvas.drawRect(0, 0, getWidth(), getHeight() * mTopPosition, mTopPaint);

		canvas.drawRect(0, getHeight() * mBottomPosition, getWidth(), getHeight(), mBottomPaint);
		canvas.restore();
	}

	public void setBottomAlpha(float alpha) {
		mBottomPaint.setAlpha((int)(alpha * 255f));
		invalidate();
	}

	public float getBottomAlpha() {
		return mBottomPaint.getAlpha() / 255f;
	}
}
