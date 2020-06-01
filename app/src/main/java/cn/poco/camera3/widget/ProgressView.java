package cn.poco.camera3.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextUtils;
import android.view.View;

import cn.poco.camera3.util.CameraPercentUtil;

/**
 * 进度
 * Created by Gxx on 2018/2/2.
 */

public class ProgressView extends View
{
    private int mProgressStyle = ProgressStyle.CIRCLE;
    private int mProgress;
    private int mViewW;
    private int mViewH;
    private Paint mProgressPaint;

    // circle
    private int mRadius;
    private RectF mArcRectF;

    // text
    private String mText;
    private Paint mTextPain;

    // location
    private int mTopMargin;
    private int mLeftMargin;
    private int mBottomMargin;
    private int mRightMargin;

    private int mPaintFlags = Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG;

    private int mProgressBGColor;
    private int mProgressColor;
    private int mProgressWidth;

    public interface ProgressStyle
    {
        int CIRCLE = 1 << 1;
    }

    public ProgressView(Context context)
    {
        super(context);

        mTextPain = new Paint();
        mProgressPaint = new Paint();

        mTopMargin = CameraPercentUtil.WidthPxToPercent(50);
    }

    public void setProgressStyle(int style)
    {
        mProgressStyle = style;
    }

    public void setRadius(int radius)
    {
        mRadius = radius;
        if (mArcRectF == null)
        {
            mArcRectF = new RectF();
        }
        mArcRectF.set(-radius, -radius, radius, radius);
    }

    public void setProgressColor(int bg_color, int progress_color)
    {
        mProgressBGColor = bg_color;
        mProgressColor = progress_color;
    }

    public void setProgressWidth(int width)
    {
        mProgressWidth = width;
    }

    public void setText(String text)
    {
        mText = text;
    }

    public void setTextParams(float size, int color)
    {
        mTextPain.reset();
        mTextPain.setFlags(mPaintFlags);
        mTextPain.setTextSize(size);
        mTextPain.setColor(color);
    }

    public void setProgress(int progress)
    {
        mProgress = progress;
        update();
    }

    public void update()
    {
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
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
        // progress
        switch (mProgressStyle)
        {
            case ProgressStyle.CIRCLE:
            {
                drawCircleProgressUI(canvas);
                break;
            }
        }

        // text
        if (!TextUtils.isEmpty(mText))
        {
            drawText(canvas);
        }
    }

    private void drawText(Canvas canvas)
    {
        canvas.save();

        Paint.FontMetrics fontMetrics = mTextPain.getFontMetrics();
        float text_width = mTextPain.measureText(mText);
        float y = CameraPercentUtil.WidthPxToPercent(204) + fontMetrics.descent - fontMetrics.ascent;
        float x = (mViewW - text_width) / 2f;
        canvas.drawText(mText, x, y, mTextPain);

        canvas.restore();
    }

    private void drawCircleProgressUI(Canvas canvas)
    {
        float x = mViewW * 1f / 2f;
        float y = mTopMargin + mProgressWidth + mRadius;

        canvas.save();
        canvas.translate(x, y);
        mProgressPaint.reset();
        mProgressPaint.setFlags(mPaintFlags);
        mProgressPaint.setColor(mProgressColor);
        mProgressPaint.setStyle(Paint.Style.STROKE);
        mProgressPaint.setStrokeWidth(mProgressWidth);
        int startProgress = -90;
        canvas.drawArc(mArcRectF, startProgress, mProgress, false, mProgressPaint);

        mProgressPaint.setColor(mProgressBGColor);
        startProgress += mProgress;
        canvas.drawArc(mArcRectF, startProgress, 360 - startProgress, false, mProgressPaint);
        canvas.restore();
    }
}
