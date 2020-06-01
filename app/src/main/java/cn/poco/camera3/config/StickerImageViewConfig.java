package cn.poco.camera3.config;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

import cn.poco.camera3.util.CameraPercentUtil;
import my.beautyCamera.R;

/**
 * Created by Gxx on 2017/11/1.
 */

public class StickerImageViewConfig
{
    public @interface ItemType
    {
        int MAKEUP_STICKER = 10000;
        int NORMAL_STICKER = 20000;
    }

    private static int mProgressWidth;
    private static int mProgressRoundRadius;
    private static int mImageViewWH;

    private static PathMeasure mRectRoundPathMeasure;
    private static PathMeasure mCirclePathMeasure;
    private static Matrix mWaitMatrix;

    private static Bitmap mDownloadWaitBmp;
    private static Drawable mLoadingDrawable;

    private StickerImageViewConfig()
    {

    }

    public static void init(Context context)
    {
        mWaitMatrix = new Matrix();
        mRectRoundPathMeasure = new PathMeasure();
        mCirclePathMeasure = new PathMeasure();
        mProgressWidth = CameraPercentUtil.WidthPxToPercent(4);
        mProgressRoundRadius = CameraPercentUtil.WidthPxToPercent(10);
        mImageViewWH = CameraPercentUtil.WidthPxToPercent(108);
        int mWaitWH = CameraPercentUtil.WidthPxxToPercent(102);
        mDownloadWaitBmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.sticker_download_wait_nodpi);
        mLoadingDrawable = context.getResources().getDrawable(R.drawable.sticker_progress_bar);

        if (mDownloadWaitBmp != null && !mDownloadWaitBmp.isRecycled())
        {
            float scale = mWaitWH * 1f / mDownloadWaitBmp.getWidth();
            mWaitMatrix.postScale(scale, scale);
            mWaitMatrix.postTranslate((mImageViewWH - mWaitWH) / 2f, (mImageViewWH - mWaitWH) / 2f);
        }

        initCirclePath();
        initRectRoundPath();
    }

    private static void initCirclePath()
    {
        float radius = mImageViewWH / 2f - mProgressWidth;
        Path path = new Path();
        path.addCircle(mImageViewWH / 2f, mImageViewWH / 2f, radius, Path.Direction.CW);

        mCirclePathMeasure.setPath(path, true);
    }

    private static void initRectRoundPath()
    {
        RectF rectF = new RectF();
        rectF.left = mProgressWidth / 2f;
        rectF.top = mProgressWidth / 2f;
        rectF.right = mImageViewWH - mProgressWidth / 2f;
        rectF.bottom = mImageViewWH - mProgressWidth / 2f;
        Path path = new Path();
        path.addRoundRect(rectF, mProgressRoundRadius, mProgressRoundRadius, Path.Direction.CW);

        mRectRoundPathMeasure.setPath(path, true);

        Path temp_path = new Path();
        // 改变 path 起点
        float start_point = mImageViewWH / 2f - mProgressRoundRadius;
        mRectRoundPathMeasure.getSegment(start_point, mRectRoundPathMeasure.getLength(), temp_path, true);
        temp_path.close();
        mRectRoundPathMeasure.setPath(temp_path, true);
    }

    public static int getProgressWidth()
    {
        return mProgressWidth;
    }

    public static Matrix getWaitBmpMatrix()
    {
        return mWaitMatrix;
    }

    public static Drawable getLoadingDrawable()
    {
        return mLoadingDrawable;
    }

    public static Bitmap getDownloadWaitBmp()
    {
        return mDownloadWaitBmp;
    }

    public static void ClearAll()
    {
        mLoadingDrawable = null;

        if (mDownloadWaitBmp != null && !mDownloadWaitBmp.isRecycled())
        {
            mDownloadWaitBmp.recycle();
            mDownloadWaitBmp = null;
        }

        mRectRoundPathMeasure = null;
        mCirclePathMeasure = null;
    }

    public static void getSelectedPath(Path dst, boolean isCircle)
    {
        getProgressPath(100, dst, isCircle);
    }

    public static void getProgressPath(float progress, Path dst, boolean isCircle)
    {
        PathMeasure temp = isCircle ? mCirclePathMeasure : mRectRoundPathMeasure;

        if (temp != null)
        {
            temp.getSegment(0, progress / 100f * temp.getLength(), dst, true);
        }
    }
}
