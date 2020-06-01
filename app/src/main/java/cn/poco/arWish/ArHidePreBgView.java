package cn.poco.arWish;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.view.View;

import cn.poco.tianutils.MakeBmpV2;
import my.beautyCamera.R;

/**
 * ar 藏祝福 背景
 * Created by Gxx on 2018/2/6.
 */

public class ArHidePreBgView extends View
{
    private Paint mPaint;
    private Matrix mMatrix;
    private Bitmap mOrgBmp; //原图 18.5 : 9
    private Bitmap mTempBmp; //裁剪后的图片

    private int mPaintFlags = Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG;

    public ArHidePreBgView(Context context)
    {
        super(context);
        mPaint = new Paint();
        mMatrix = new Matrix();

        mOrgBmp = MakeBmpV2.DecodeNoDpiResource(getResources(), R.drawable.ar_bg, null);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);
        createFixBmp(w, h);
    }

    private void createFixBmp(int w, int h)
    {
        if (mOrgBmp != null)
        {
            float scale = w * 1f / mOrgBmp.getWidth();
            mMatrix.reset();
            mMatrix.postScale(scale, scale);

            mTempBmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(mTempBmp);
            mPaint.reset();
            mPaint.setFlags(mPaintFlags);
            canvas.save();
            canvas.drawBitmap(mOrgBmp, mMatrix, mPaint);
            canvas.restore();
        }
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        if (mTempBmp != null && !mTempBmp.isRecycled())
        {
            canvas.save();
            mMatrix.reset();
            mPaint.reset();
            mPaint.setFlags(mPaintFlags);
            canvas.drawBitmap(mTempBmp, mMatrix, mPaint);
            canvas.restore();
        }
    }
}
