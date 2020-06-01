package cn.poco.camera3.ui.customization;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.view.View;

import cn.poco.camera3.util.CameraPercentUtil;
import my.beautyCamera.R;

public class BesselView extends View
{
    private Bitmap mBmp;
    private Bitmap mResBmp;
    private Matrix mMatrix;
    private Paint mPaint;
    private Path mPath;

    private int mArcHeight;
    private int mArcPoint;

    public BesselView(Context context)
    {
        super(context);

        mResBmp = BitmapFactory.decodeResource(getResources(), R.drawable.camera_tailor_bk);

        mMatrix = new Matrix();
        mPaint = new Paint();
        mPath = new Path();

        mArcHeight = CameraPercentUtil.HeightPxToPercent(34);
        mArcPoint = CameraPercentUtil.HeightPxToPercent(324);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);
        initBmp(w, h);
    }

    private void initBmp(int w, int h)
    {
        mBmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(mBmp);

        canvas.save();
        mMatrix.reset();
        mPaint.reset();

        mPaint.setFilterBitmap(true);
        mPaint.setAntiAlias(true);

        RectF r = new RectF(0, 0, w, h);
        canvas.drawRoundRect(r, CameraPercentUtil.WidthPxToPercent(20), CameraPercentUtil.HeightPxToPercent(20), mPaint);

        float targetW = CameraPercentUtil.WidthPxxToPercent(852);
        float targetH = CameraPercentUtil.HeightPxxToPercent(597);

        float resW = mResBmp.getWidth();
        float resH = mResBmp.getHeight();

        float scale = Math.min((targetW / resW),(targetH / resH));
        mMatrix.postScale(scale, scale);

        mPaint.reset();
        mPaint.setFilterBitmap(true);
        mPaint.setAntiAlias(true);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(mResBmp, mMatrix, mPaint);

        mPath.moveTo(0, mArcPoint);
        mPath.lineTo(0, h);
        mPath.lineTo(w, h);
        mPath.lineTo(w, mArcPoint);
        mPath.quadTo(w /2f, mArcPoint + mArcHeight *2f, 0, mArcPoint);

        mPaint.setColor(Color.WHITE);
        canvas.drawPath(mPath, mPaint);

        canvas.restore();
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        mMatrix.reset();
        canvas.drawBitmap(mBmp, mMatrix, null);
    }

    public void ClearMemory()
    {
        mBmp = null;
        mResBmp = null;
    }
}
