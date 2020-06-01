package cn.poco.camera3.ui.customization;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.View;

import cn.poco.advanced.ImageUtils;
import cn.poco.camera3.util.CameraPercentUtil;

public class SwitchView extends View
{
    private int mBKOnColor = ImageUtils.GetSkinColor();
    private int mBKOffColor = 0x33000000;
    private float mBKRx, mBKRy;
    private int mBKHeight;

    private float mSwitchRadius;
    private float mSwitchMargin;
    private boolean mSwitchOn = true;

    private Bitmap mOnSwitchBmp;
    private Bitmap mOffSwitchBmp;
    private Matrix mMatrix;

    private int mViewHeight;

    private RectF mTouchArea;

    private OnClickListener mClickListener;

    public SwitchView(Context context)
    {
        super(context);

        mSwitchRadius = CameraPercentUtil.HeightPxToPercent(29) / 2f;
        mSwitchMargin = CameraPercentUtil.WidthPxToPercent(3);

        mBKRx = CameraPercentUtil.WidthPxToPercent(24);
        mBKRy = CameraPercentUtil.HeightPxToPercent(24);

        mBKHeight = CameraPercentUtil.HeightPxToPercent(36);

        mMatrix = new Matrix();
    }

    public void UpdateUI()
    {
        invalidate();
    }

    public void setSwitchOn(boolean switchOn)
    {
        mSwitchOn = switchOn;
    }

    public boolean isSwitchOn()
    {
        return mSwitchOn;
    }

    public void SetBKColor(int onColor, int offColor)
    {
        mBKOnColor = onColor;
        mBKOffColor = offColor;
    }

    public void SetBKParams(int rx, int ry)
    {
        mBKRx = rx;
        mBKRy = ry;
    }

    public void SetSwitchParams(int radius, float margin)
    {
        mSwitchRadius = radius;
        mSwitchMargin = margin;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);

        mViewHeight = h;

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);

        mOnSwitchBmp = Bitmap.createBitmap(w, mBKHeight, Bitmap.Config.ARGB_8888);
        mOffSwitchBmp = Bitmap.createBitmap(w, mBKHeight, Bitmap.Config.ARGB_8888);
        RectF rectF = new RectF(0, 0, w, mBKHeight);

        Canvas canvas = new Canvas(mOnSwitchBmp);
        paint.setColor(mBKOnColor);
        canvas.drawRoundRect(rectF, mBKRx, mBKRy, paint);
        paint.setColor(Color.WHITE);
        canvas.drawCircle(w - mSwitchMargin - mSwitchRadius, mBKHeight / 2f, mSwitchRadius, paint);

        canvas.setBitmap(mOffSwitchBmp);
        paint.setColor(mBKOffColor);
        canvas.drawRoundRect(rectF, mBKRx, mBKRy, paint);
        paint.setColor(Color.WHITE);
        canvas.drawCircle(mSwitchMargin + mSwitchRadius, mBKHeight / 2f, mSwitchRadius, paint);

        mTouchArea = new RectF(0, 0, w, h);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        canvas.save();
        canvas.translate(0, (mViewHeight - mBKHeight) / 2f);
        canvas.drawBitmap(mSwitchOn ? mOnSwitchBmp : mOffSwitchBmp, mMatrix, null);
        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if (mTouchArea != null && !mTouchArea.contains(event.getX(), event.getY()))
        {
            return false;
        }

        switch (event.getAction())
        {
            case MotionEvent.ACTION_UP:
            {
                mSwitchOn = !mSwitchOn;
                if (mClickListener != null)
                {
                    mClickListener.onClick(this);
                }
            }
        }
        UpdateUI();
        return true;
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l)
    {
        mClickListener = l;
    }

    public void ClearMemory()
    {
        mClickListener = null;

        mOnSwitchBmp = null;
        mOffSwitchBmp = null;
    }
}
