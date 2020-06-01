package cn.poco.camera3.ui.drawable;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;

import cn.poco.advanced.ImageUtils;
import cn.poco.camera.CameraConfig;
import cn.poco.camera3.util.CameraPercentUtil;
import my.beautyCamera.R;

/**
 * 动态贴纸 按钮动画
 * Created by Gxx on 2017/11/21.
 */

public class StickerAnimDrawable extends Drawable
{
    private ArrayList<Bitmap> mData; // 粗素材，只用在 9:16 + full 比例
    private ArrayList<Bitmap> mTinyData;
    private Matrix mMatrix;
    private Paint mPaint;
    private int mBmpColor;
    private int mBmpWH;
    private float mBmpScale;

    private Bitmap mRes;

    private long mAnimStartTime;

    private long mAnimIntervalTime;

    private Context mContext;

    private int mDegree;

    private int mViewW;
    private int mViewH;
    private float mCurrentPreviewRatio;

    public StickerAnimDrawable(Context context)
    {
        mContext = context;
        initData(context);
    }

    private void initData(final Context context)
    {
        mBmpColor = Color.WHITE;

        mAnimIntervalTime = 54;

        mBmpWH = CameraPercentUtil.WidthPxToPercent(70);

        int[] dataIDArr = new int[]{
                R.drawable._sti_00, R.drawable._sti_01, R.drawable._sti_02, R.drawable._sti_03, R.drawable._sti_04,
                R.drawable._sti_05, R.drawable._sti_06, R.drawable._sti_07, R.drawable._sti_08, R.drawable._sti_09,
                R.drawable._sti_10, R.drawable._sti_11, R.drawable._sti_12, R.drawable._sti_13, R.drawable._sti_14,
                R.drawable._sti_15, R.drawable._sti_16, R.drawable._sti_17, R.drawable._sti_18, R.drawable._sti_19,
                R.drawable._sti_20, R.drawable._sti_21, R.drawable._sti_22, R.drawable._sti_23, R.drawable._sti_24,
                R.drawable._sti_25, R.drawable._sti_26, R.drawable._sti_27, R.drawable._sti_28, R.drawable._sti_29,
                R.drawable._sti_30, R.drawable._sti_31, R.drawable._sti_32, R.drawable._sti_33, R.drawable._sti_34,
                R.drawable._sti_35, R.drawable._sti_36, R.drawable._sti_37, R.drawable._sti_38, R.drawable._sti_39,
                R.drawable._sti_40, R.drawable._sti_41, R.drawable._sti_42, R.drawable._sti_43, R.drawable._sti_44,
                R.drawable._sti_45, R.drawable._sti_46, R.drawable._sti_47, R.drawable._sti_48, R.drawable._sti_49,
                R.drawable._sti_50, R.drawable._sti_51, R.drawable._sti_52, R.drawable._sti_53
        };

        int[] tinyDataIDArr = new int[]{
                R.drawable._sti_tiny_01, R.drawable._sti_tiny_02, R.drawable._sti_tiny_03, R.drawable._sti_tiny_04,
                R.drawable._sti_tiny_05, R.drawable._sti_tiny_06, R.drawable._sti_tiny_07, R.drawable._sti_tiny_08,
                R.drawable._sti_tiny_09, R.drawable._sti_tiny_10, R.drawable._sti_tiny_11, R.drawable._sti_tiny_12,
                R.drawable._sti_tiny_13, R.drawable._sti_tiny_14, R.drawable._sti_tiny_15, R.drawable._sti_tiny_16,
                R.drawable._sti_tiny_17, R.drawable._sti_tiny_18, R.drawable._sti_tiny_19, R.drawable._sti_tiny_20,
                R.drawable._sti_tiny_21, R.drawable._sti_tiny_22, R.drawable._sti_tiny_23, R.drawable._sti_tiny_24,
                R.drawable._sti_tiny_25, R.drawable._sti_tiny_26, R.drawable._sti_tiny_27, R.drawable._sti_tiny_28,
                R.drawable._sti_tiny_29, R.drawable._sti_tiny_30, R.drawable._sti_tiny_31, R.drawable._sti_tiny_32,
                R.drawable._sti_tiny_33, R.drawable._sti_tiny_34, R.drawable._sti_tiny_35, R.drawable._sti_tiny_36,
                R.drawable._sti_tiny_37, R.drawable._sti_tiny_38, R.drawable._sti_tiny_39, R.drawable._sti_tiny_40,
                R.drawable._sti_tiny_41, R.drawable._sti_tiny_42, R.drawable._sti_tiny_43, R.drawable._sti_tiny_44,
                R.drawable._sti_tiny_45, R.drawable._sti_tiny_46, R.drawable._sti_tiny_47, R.drawable._sti_tiny_48,
                R.drawable._sti_tiny_49, R.drawable._sti_tiny_50, R.drawable._sti_tiny_51, R.drawable._sti_tiny_52,
                R.drawable._sti_tiny_53, R.drawable._sti_tiny_54
        };

        mData = new ArrayList<>();
        mTinyData = new ArrayList<>();

        int length = dataIDArr.length;
        for (int index = 0; index < length; index++)
        {
            int id = dataIDArr[index];
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), id);
            if (bitmap != null)
            {
                if (mBmpScale == 0)
                {
                    mBmpScale = mBmpWH * 1f / bitmap.getWidth();
                }
                mData.add(bitmap);
            }

            id = tinyDataIDArr[index];
            bitmap = BitmapFactory.decodeResource(context.getResources(), id);
            if (bitmap != null)
            {
                mTinyData.add(bitmap);
            }
        }

        mPaint = new Paint();
        mMatrix = new Matrix();
    }

    public void setCurrentPreviewRatio(float ratio)
    {
        mCurrentPreviewRatio = ratio;
    }

    public void ClearAll()
    {
        mContext = null;

        if (mData != null)
        {
            for (Bitmap bitmap : mData)
            {
                if (bitmap != null && !bitmap.isRecycled())
                {
                    bitmap.recycle();
                }
            }
            mData.clear();
            mData = null;
        }

        if (mTinyData != null)
        {
            for (Bitmap bitmap : mTinyData)
            {
                if (bitmap != null && !bitmap.isRecycled())
                {
                    bitmap.recycle();
                }
            }
            mTinyData.clear();
            mTinyData = null;
        }
    }

    public void setBmpColor(int color)
    {
        mBmpColor = color;
        invalidateSelf();
    }

    public void setRotation(int degree)
    {
        mDegree = degree;
        invalidateSelf();
    }

    @Override
    public void setBounds(int left, int top, int right, int bottom)
    {
        super.setBounds(left, top, right, bottom);

        mViewW = right - left;
        mViewH = bottom - top;

        mMatrix.reset();
        mMatrix.postScale(mBmpScale, mBmpScale);
        mMatrix.postTranslate((mViewW - mBmpWH) / 2f, (mViewH - mBmpWH) / 2f);
    }

    @Override
    public void draw(@NonNull Canvas canvas)
    {
        if (mAnimStartTime == 0)
        {
            mAnimStartTime = System.currentTimeMillis();
        }

        countNextFrame();

        canvas.save();
        canvas.rotate(mDegree, mViewW / 2f, mViewH / 2f);
        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setFilterBitmap(true);
        if (mRes != null && !mRes.isRecycled())
        {
            canvas.drawBitmap(mRes, mMatrix, mPaint);
        }
        canvas.restore();
    }

    private void countNextFrame()
    {
        int result = (int) Math.floor((System.currentTimeMillis() - mAnimStartTime) * 1f / mAnimIntervalTime) - 1;

        if (result < 0)
        {
            result = 0;
        }
        if (mData != null && mData.size() > 0)
        {
            int index = result % mData.size();
            if (index >= 0 && index < mData.size())
            {
                if(mCurrentPreviewRatio == CameraConfig.PreviewRatio.Ratio_16_9 || mCurrentPreviewRatio == CameraConfig.PreviewRatio.Ratio_Full)
                {
                    mRes = mData.get(index);
                }
                else
                {
                    mRes = mTinyData.get(index);
                }

                if (mBmpColor != Color.WHITE && mRes != null && !mRes.isRecycled())
                {
                    mRes = ImageUtils.AddSkin(mContext, mRes);
                }
            }
        }
        invalidateSelf();
    }

    @Override
    public void setAlpha(int alpha)
    {

    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter)
    {

    }

    @Override
    public int getOpacity()
    {
        return PixelFormat.TRANSPARENT;
    }
}
