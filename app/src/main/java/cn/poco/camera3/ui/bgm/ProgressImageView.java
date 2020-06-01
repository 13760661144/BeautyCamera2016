package cn.poco.camera3.ui.bgm;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v7.widget.AppCompatImageView;

import cn.poco.camera3.mgr.BgmResWrapper;
import cn.poco.camera3.util.CameraPercentUtil;
import my.beautyCamera.R;

/**
 * 有圆形下载进度，自动旋转内容
 */
public class ProgressImageView extends AppCompatImageView
{
    private final Bitmap mClipBmp;
    private Matrix mClipMatrix;
    private int mClipBmpWH;
    private int mW, mH;
    private Paint mPaint;
    private RectF mRectF;
    private float mRadius;

    private int mProgress;
    private boolean mDrawProgress = false;
    private int mProgressColor = Color.TRANSPARENT;

    private float mDegree = 0;

    private boolean mAutoUpdate = false;

    private boolean mDrawMask = false;

    // 数据管理，目前只是更新缩略图旋转角度
    private BgmResWrapper mBgmResWrapper;

    public ProgressImageView(Context context)
    {
        super(context);
        mPaint = new Paint();
        mBgmResWrapper = BgmResWrapper.getInstance();
        mClipBmp = BitmapFactory.decodeResource(getResources(), R.drawable.bgm_music_clip);
        mClipBmpWH = CameraPercentUtil.WidthPxToPercent(54);
        mClipMatrix = new Matrix();
    }

    public void setImageDegree(float degree)
    {
        mDegree = degree;
    }

    // 是否自动旋转
    public void setAutoUpdate(boolean autoUpdate)
    {
        mAutoUpdate = autoUpdate;
    }

    public void setProgress(int progress)
    {
        mProgress = progress;
    }

    // 是否重置进度UI效果
    public void resetProgressState(boolean reset)
    {
        mDrawProgress = !reset;
    }

    public void setProgressColor(int color)
    {
        mProgressColor = color;
    }

    public void updateUI()
    {
        invalidate();
    }

    public void setDrawMask(boolean drawMask)
    {
        this.mDrawMask = drawMask;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);
        mW = w;
        mH = h;
        mRadius = w / 2f - CameraPercentUtil.WidthPxToPercent(2) + 0.5f;
        mRectF = new RectF(-mRadius, -mRadius, mRadius, mRadius);

        mClipMatrix.reset();
        float scale = mClipBmpWH * 1f / mClipBmp.getWidth();
        mClipMatrix.postScale(scale, scale);
        mClipMatrix.postTranslate((w - mClipBmpWH) / 2f, (h - mClipBmpWH) / 2f);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        canvas.save();
        canvas.rotate(mDegree, mW / 2f, mH / 2f);
        super.onDraw(canvas);
        canvas.restore();

        DrawProgress(canvas);

        DrawClipBmp(canvas);

        if (getVisibility() == VISIBLE && mAutoUpdate)
        {
            mDegree += 0.64f; // 1s 转 40°
            if (mBgmResWrapper != null)
            {
                mBgmResWrapper.SetThumbRotationDegree(getContext(), mDegree);
            }
            invalidate();
        }
    }

    private void DrawClipBmp(Canvas canvas)
    {
        if (mDrawMask)
        {
            canvas.save();
            mPaint.reset();
            mPaint.setAntiAlias(true);
            mPaint.setColor(0x80000000);
            mPaint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(mW / 2f, mH / 2f, mRadius - 0.5f, mPaint);

            mPaint.reset();
            mPaint.setAntiAlias(true);
            mPaint.setFilterBitmap(true);
            canvas.drawBitmap(mClipBmp, mClipMatrix, mPaint);
            canvas.restore();
        }
    }

    private void DrawProgress(Canvas canvas)
    {
        if (!mDrawProgress) return;

        canvas.save();
        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setFilterBitmap(true);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(CameraPercentUtil.WidthPxToPercent(4));
        mPaint.setColor(mProgressColor);
        canvas.translate(mW / 2f, mH / 2f);
        canvas.drawArc(mRectF, -90, mProgress * 360f / 100f, false, mPaint);
        canvas.restore();
    }

    public void ClearMemory()
    {
        mBgmResWrapper = null;
    }
}
