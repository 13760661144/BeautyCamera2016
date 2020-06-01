package cn.poco.camera3.ui.sticker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.widget.ImageView;

import cn.poco.advanced.ImageUtils;
import cn.poco.camera3.config.StickerImageViewConfig;

/**
 * 圆角进度条 ImageView
 * Created by Gxx on 2017/10/12.
 */

public class StickerImageView extends ImageView
{
    private float mProgress;
    private int mProgressWidth;
    private int mProgressBKColor;
    private int mProgressColor;

    private Bitmap mWaitBmp;
    private Matrix mWaitMatrix;

    private Path mPath; // 临时路径
    private Paint mPaint;

    private boolean mIsSelected;

    private int mType;

    public StickerImageView(Context context)
    {
        this(context, StickerImageViewConfig.ItemType.NORMAL_STICKER);
    }

    public StickerImageView(Context context, int type)
    {
        super(context);
        mType = type;
        initData();
    }

    private void initData()
    {
        mPath = new Path();
        mPaint = new Paint();
        mProgressBKColor = 0x4DFFFFFF;
        mProgressColor = ImageUtils.GetSkinColor();
    }

    public void ClearAll()
    {
        mWaitBmp = null;
        mWaitMatrix = null;
    }

    public void init()
    {
        if (mWaitBmp == null)
        {
            mWaitBmp = StickerImageViewConfig.getDownloadWaitBmp();
        }

        if (mWaitMatrix == null)
        {
            mWaitMatrix = StickerImageViewConfig.getWaitBmpMatrix();
        }

        mProgressWidth = StickerImageViewConfig.getProgressWidth();
    }

    public void showGrayProgressBKColor(boolean show)
    {
        mProgressBKColor = show ? 0x4D999999 : 0x4DFFFFFF;
    }

    public void setSelected(boolean is)
    {
        this.mIsSelected = is;
    }

    public void setProgress(float progress)
    {
        mProgress = progress;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
        {
            setImageAlpha(mProgress > -1 && mProgress < 1 ? (int) (255 * 0.6f) : 255);
        }
        else
        {
            setAlpha(mProgress > -1 && mProgress < 1 ? (int) (255 * 0.6f) : 255);
        }
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        // 下载
        drawDownLoadUI(canvas);

        // 下载完 or 选中
        drawSelectedUI(canvas);
    }

    private void drawSelectedUI(Canvas canvas)
    {
        if (mIsSelected)
        {
            mPaint.reset();
            mPaint.setAntiAlias(true);
            mPaint.setFilterBitmap(true);
            mPaint.setStrokeWidth(mProgressWidth);
            mPaint.setStyle(Paint.Style.STROKE);

            canvas.save();
            mPaint.setColor(mProgressColor);
            if (mPath != null)
            {
                mPath.reset();
                StickerImageViewConfig.getSelectedPath(mPath, isCircleProgress());
                canvas.drawPath(mPath, mPaint);
            }
            canvas.restore();
        }
    }

    private boolean isCircleProgress()
    {
        return mType == StickerImageViewConfig.ItemType.MAKEUP_STICKER;
    }

    private void drawDownLoadUI(Canvas canvas)
    {
        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setFilterBitmap(true);

        if (mProgress > -1 && mProgress < 1)
        {
            if (mWaitBmp != null && !mWaitBmp.isRecycled())
            {
                canvas.save();
                canvas.drawBitmap(mWaitBmp, mWaitMatrix, mPaint);
                canvas.restore();
                return;
            }
        }

        if (mProgress >= 1 && mProgress <= 100)
        {
            mPaint.setStrokeWidth(mProgressWidth);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeCap(Paint.Cap.ROUND);
            mPaint.setStrokeJoin(Paint.Join.ROUND);

            canvas.save();
            if (!isCircleProgress())
            {
                canvas.rotate(90, getMeasuredWidth() / 2f, getMeasuredHeight() / 2f);
            }
            else
            {
                canvas.rotate(-90, getMeasuredWidth() / 2f, getMeasuredHeight() / 2f);
            }

            // 进度条背景
            mPaint.setColor(mProgressBKColor);
            mPath.reset();
            StickerImageViewConfig.getSelectedPath(mPath, isCircleProgress());
            canvas.drawPath(mPath, mPaint);

            // 进度条
            mPaint.setColor(mProgressColor);
            mPath.reset();
            StickerImageViewConfig.getProgressPath(mProgress, mPath, isCircleProgress());
            canvas.drawPath(mPath, mPaint);
            canvas.restore();
        }
    }
}
