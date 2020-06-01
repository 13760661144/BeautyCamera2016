package cn.poco.album.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.View;

import cn.poco.advanced.ImageUtils;
import cn.poco.tianutils.ShareData;

/**
 * Created by: fwc
 * Date: 2016/12/20
 */
public class BorderView extends View {

	private Paint mPaint;
	private Path mPath;

	public BorderView(Context context) {
		super(context);

		mPaint = new Paint();
		mPaint.setColor(ImageUtils.GetSkinColor(0xffe75988));
		mPaint.setAntiAlias(true);
		mPaint.setStrokeWidth(ShareData.PxToDpi_xhdpi(6));
		mPaint.setStyle(Paint.Style.STROKE);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {

		float stroke = ShareData.PxToDpi_xhdpi(6) / 2f;

		mPath = new Path();
		mPath.moveTo(stroke, stroke);
		mPath.lineTo(stroke, h - stroke);
		mPath.lineTo(w - stroke, h - stroke);
		mPath.lineTo(w - stroke, stroke);
		mPath.close();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawPath(mPath, mPaint);
	}
}
