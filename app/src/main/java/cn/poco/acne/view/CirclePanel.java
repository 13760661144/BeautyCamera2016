package cn.poco.acne.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.TypedValue;
import android.view.View;

/**
 * Created by: fwc
 * Date: 2016/11/30
 */
public class CirclePanel extends View {

	private boolean mShow = false;
	private Paint mPaint;
	private float mCircleX;
	private float mCircleY;
	private float mRadius;

	private String mText;
	private float mTextSize;
	private int mTextColor;
	private boolean mShowText = false;
	private Paint mTextPaint;

	public CirclePanel(Context context) {
		super(context);

		init();
	}

	private void init() {

		setLayerType(LAYER_TYPE_SOFTWARE, null);

		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setColor(Color.WHITE);
		// 不支持硬件加速
		mPaint.setShadowLayer(5, 0, 5, 0x50888888);

		mTextPaint = new Paint();
		mTextPaint.setAntiAlias(true);
		mTextPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
								14, getContext().getResources().getDisplayMetrics()));
		mTextPaint.setColor(Color.BLACK);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (mShow) {
			canvas.drawCircle(mCircleX, mCircleY, mRadius, mPaint);

			if (mShowText && mText != null) {
				Paint.FontMetrics metrics = mTextPaint.getFontMetrics();
				float textWidth = mTextPaint.measureText(mText);
				float x = mCircleX - textWidth / 2;
				float y = mCircleY + (metrics.bottom - metrics.top) / 2 - metrics.bottom;
				canvas.drawText(mText, x, y, mTextPaint);
			}
		}
	}

	public void change(float circleX, float circleY, float radius) {
		mCircleX = circleX;
		mCircleY = circleY;
		mRadius = radius;
	}

	public void show() {
		mShow = true;
		invalidate();
	}

	public void hide() {
		mShow = false;
		invalidate();
	}

	public void setText(String text) {
		mShowText = true;
		mText = text;
	}

	public void setTextSize(float textSize) {
		mTextSize = textSize;
		mTextPaint.setTextSize(mTextSize);
	}

	public void setTextColor(int color) {
		mTextColor = color;
		mTextPaint.setColor(mTextColor);
	}
}
