package cn.poco.beauty.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import cn.poco.advanced.ImageUtils;
import cn.poco.tianutils.ShareData;
import my.beautyCamera.R;

/**
 * Created by: fwc
 * Date: 2016/12/21
 */
public class UserItemView extends FrameLayout {

	private Paint mPaint;
	private Path mPath;

	public UserItemView(Context context) {
		super(context);

		setWillNotDraw(false);

		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setStrokeWidth(ShareData.PxToDpi_xhdpi(4));
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setColor(0xff9e999a);

		initViews();
	}

	private void initViews() {
		FrameLayout centerLayout = new FrameLayout(getContext());
		LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.CENTER;
		addView(centerLayout, params);
		{
			ImageView imageView = new ImageView(getContext());
			imageView.setImageResource(R.drawable.beauty_color_logo_def);
			params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			params.gravity = Gravity.CENTER_HORIZONTAL;
			centerLayout.addView(imageView, params);

			TextView textView = new TextView(getContext());
			textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 9);
			textView.setTextColor(0xe6000000);
			textView.setText(getResources().getString(R.string.beauty_effect_user));
			params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			params.topMargin = ShareData.PxToDpi_xhdpi(82);
			params.gravity = Gravity.CENTER_HORIZONTAL;
			centerLayout.addView(textView, params);
		}
	}

	public void setUserSelected(boolean selected) {
		float paintWidth;
		if (selected) {
			paintWidth = ShareData.PxToDpi_xhdpi(8);
			mPaint.setStrokeWidth(paintWidth);
			mPaint.setColor(ImageUtils.GetSkinColor(0xffe75988));
		} else {
			paintWidth = ShareData.PxToDpi_xhdpi(4);
			mPaint.setStrokeWidth(paintWidth);
			mPaint.setColor(0xff9e999a);
		}

		invalidate();
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);

		mPath = new Path();
		mPath.moveTo(0, 0);
		mPath.lineTo(0, getHeight());
		mPath.lineTo(getWidth(), getHeight());
		mPath.lineTo(getWidth(), 0);
		mPath.close();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawPath(mPath, mPaint);
	}
}
