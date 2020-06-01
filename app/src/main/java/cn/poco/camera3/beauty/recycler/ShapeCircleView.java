package cn.poco.camera3.beauty.recycler;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v7.widget.AppCompatImageView;

import cn.poco.advanced.ImageUtils;
import cn.poco.camera3.util.CameraPercentUtil;

/**
 * @author lmx
 *         Created by lmx on 2017-12-11.
 */

public class ShapeCircleView extends AppCompatImageView
{
    private int width;
    private int height;
    private Paint mPaint;
    private boolean isOpenSub = false;
    private boolean isSelect = false;
    private int mStrokeW;
    private boolean isDrawInnerMask = true;

    public ShapeCircleView(Context context)
    {
        super(context);
        mPaint = new Paint();
        mStrokeW = CameraPercentUtil.WidthPxToPercent(4);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        canvas.save();
        if (isSelect || isOpenSub)
        {
            mPaint.reset();
            mPaint.setAntiAlias(true);
            mPaint.setFilterBitmap(true);
            mPaint.setColor(ImageUtils.GetSkinColor());
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeWidth(mStrokeW);
            canvas.drawCircle(width / 2f, height / 2f, (width - mStrokeW) / 2, mPaint);

            if (isDrawInnerMask)
            {
                mPaint.reset();
                mPaint.setAntiAlias(true);
                mPaint.setFilterBitmap(true);
                mPaint.setColor(ImageUtils.GetColorAlpha(ImageUtils.GetSkinColor(), 0.8f));
                mPaint.setStyle(Paint.Style.FILL);
                canvas.drawCircle(width / 2f, height / 2f, width / 2 - mStrokeW, mPaint);
            }
        }
        canvas.restore();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void onSelected()
    {
        isSelect = true;
        invalidate();
    }

    public void onUnSelected()
    {
        isSelect = false;
        invalidate();
    }

    public void setDrawInnerMask(boolean drawInnerMask)
    {
        isDrawInnerMask = drawInnerMask;
    }

    public void onClick()
    {

    }

    public void onOpenSub(boolean isOpenSub)
    {
        this.isOpenSub = isOpenSub;
        invalidate();
    }
}
