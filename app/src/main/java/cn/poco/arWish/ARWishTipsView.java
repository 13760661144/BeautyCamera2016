package cn.poco.arWish;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.v4.graphics.ColorUtils;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import cn.poco.camera3.util.CameraPercentUtil;
import cn.poco.statistics.MyBeautyStat;
import cn.poco.tianutils.ShareData;
import my.beautyCamera.R;

/**
 * Created by Gxx on 2018/1/23.
 */

public class ARWishTipsView extends View
{
    private final Bitmap mWhiteAimedBG;
    private final Bitmap mHideAimedBG;
    private final Bitmap mPackUpBmp;
    private final String mSmallBmpCircleTips;

    private boolean mUIEnable = true;
    private boolean mDoingAnim;
    private boolean isShow;
    private boolean mCanShow = true;
    private boolean mCanNarrow;
    
    public void setStatusListener(StatusListener listener)
    {
        this.mListener = listener;
    }

    public interface StatusListener
    {
        void onShowWishTips();

        void onHideWishTips();
    }

    private StatusListener mListener;

    private Matrix mMatrix;
    private Bitmap mBitmap;

    private int mPaintFlag;

    private Matrix mBmpShaderMatrix;
    private Paint mBmpPaint;
    private BitmapShader mBmpShader;

    private Paint mPaint;
    private float mRadius;
    private float mWhiteBGRadius;

    private int mBigBmpWH;
    private int mSmallBmpWH;

    // bitmap circle params
    private int mSmallBmpCircleRadius;

    private int mViewW;
    private int mViewH;

    private float mCircleX, mCircleY;

    private RectF mRoundRect;
    private RectF mSmallBmpTouchRect;
    private RectF mBigBmpTouchRect;
    private Rect mTextRectF;

    private float mCNNDegree;

    private final PorterDuffXfermode dst_atop_mode = new PorterDuffXfermode(PorterDuff.Mode.DST_ATOP);
    private final PorterDuffXfermode src_out_mode = new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT);

    public ARWishTipsView(Context context)
    {
        super(context);

        mSmallBmpCircleTips = getContext().getString(R.string.ar_wish_find_clue);

        mWhiteAimedBG = BitmapFactory.decodeResource(getResources(), R.drawable.ar_aimed_bg);
        mHideAimedBG = BitmapFactory.decodeResource(getResources(), R.drawable.ar_hide_aimed_bg);
        mPackUpBmp = BitmapFactory.decodeResource(getResources(), R.drawable.ar_find_wish_pack);

        mTextRectF = new Rect();
        mPaintFlag = Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG;
        mPaint = new Paint(mPaintFlag);
        mBmpPaint = new Paint(mPaintFlag);
        mMatrix = new Matrix();
        mBmpShaderMatrix = new Matrix();
        mRoundRect = new RectF();

        mViewH = ShareData.m_screenRealHeight;
        mViewW = ShareData.m_screenRealWidth;
        mBigBmpWH = CameraPercentUtil.WidthPxToPercent(434);
        mSmallBmpWH = CameraPercentUtil.WidthPxToPercent(118);
        mSmallBmpCircleRadius = CameraPercentUtil.WidthPxToPercent(62);
    }

    public void setBitmap(Object bitmap)
    {
        if (bitmap == null)
        {
            reset();
            return;
        }

        if (bitmap instanceof Integer)
        {
            mBitmap = BitmapFactory.decodeResource(getResources(), (int) bitmap);
        }
        else if (bitmap instanceof String)
        {
            mBitmap = BitmapFactory.decodeFile((String) bitmap);
        }
        else if (bitmap instanceof Bitmap && !((Bitmap) bitmap).isRecycled())
        {
            mBitmap = (Bitmap) bitmap;
        }

        if (mBitmap != null)
        {
            mBmpShader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

            float scale = mSmallBmpWH * 1f / mBitmap.getWidth();
            float x = (mViewW - mSmallBmpWH) / 2f;
            float y = CameraPercentUtil.WidthPxToPercent(100 + 30);
            mBmpShaderMatrix.reset();
            mBmpShaderMatrix.postScale(scale, scale);
            mBmpShaderMatrix.postTranslate(x, y);
            mBmpShader.setLocalMatrix(mBmpShaderMatrix);
            mBmpPaint.setShader(mBmpShader);

            mWhiteBGRadius = mSmallBmpCircleRadius;

            float left = mViewW * 1f / 2f - mSmallBmpCircleRadius;
            float right = mViewW * 1f / 2f + mSmallBmpCircleRadius;
            float top = CameraPercentUtil.WidthPxToPercent(100 + 30);
            float bottom = CameraPercentUtil.WidthPxToPercent(100 + 30 + 124);

            mRoundRect.set(left, top, right, bottom);
            mRadius = mSmallBmpWH *1f/2f;

            mCircleX = mViewW / 2f;
            mCircleY = CameraPercentUtil.WidthPxToPercent(100 + 30 + 62);

            initTouchRect();

            invalidate();
        }
    }

    public boolean isShowTips()
    {
        return isShow;
    }

    public void show()
    {
        if (mBitmap == null || mBitmap.isRecycled()) return;

        MyBeautyStat.onClickByRes(R.string.AR祝福_找祝福_打开ar镜头_查看线索图);

        mUIEnable = false;
        isShow = true;
        mDoingAnim = true;

        final float new_x = (mViewW - mBigBmpWH) / 2f;
        final float new_y = CameraPercentUtil.WidthPxToPercent(240 + 122);

        final float old_x = (mViewW - mSmallBmpWH) / 2f;
        final float old_y = CameraPercentUtil.WidthPxToPercent(100 + 30);

        final float old_scale = mSmallBmpWH * 1f / mBitmap.getWidth();
        final float new_scale = mBigBmpWH * 1f / mBitmap.getWidth();

        final float old_center_x = mViewW / 2f;
        final float old_center_y = CameraPercentUtil.WidthPxToPercent(100 + 30 + 62);

        final float new_center_x = mViewW / 2f;
        final float new_center_y = CameraPercentUtil.WidthPxToPercent(240 + 122 + 217);

        final float old_circle_radius = mSmallBmpWH * 1f / 2f;
        final float new_circle_radius = mBigBmpWH * 1f / 2f;

        final float old_white_bg_round_rect_radius = mSmallBmpCircleRadius;
        final float new_white_bg_round_rect_radius = CameraPercentUtil.WidthPxToPercent(30);

        final float old_left = mViewW * 1f / 2f - mSmallBmpCircleRadius;
        final float old_right = mViewW * 1f / 2f + mSmallBmpCircleRadius;
        final float old_top = CameraPercentUtil.WidthPxToPercent(100 + 30);
        final float old_bottom = CameraPercentUtil.WidthPxToPercent(100 + 30 + 124);

        final float new_left = (mViewW * 1f - CameraPercentUtil.WidthPxToPercent(568)) / 2f;
        final float new_right = (mViewW * 1f + CameraPercentUtil.WidthPxToPercent(568)) / 2f;
        final float new_top = CameraPercentUtil.WidthPxToPercent(240);
        final float new_bottom = CameraPercentUtil.WidthPxToPercent(240 + 680);

        ValueAnimator anim = ValueAnimator.ofFloat(0, 1);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                float value = (float) animation.getAnimatedValue();
                float x = old_x + (new_x - old_x) * value;
                float y = old_y + (new_y - old_y) * value;
                float scale = old_scale + (new_scale - old_scale) * value;

                mBmpShaderMatrix.reset();
                mBmpShaderMatrix.postScale(scale, scale);
                mBmpShaderMatrix.postTranslate(x, y);
                mBmpShader.setLocalMatrix(mBmpShaderMatrix);
                mBmpPaint.reset();
                mBmpPaint.setFlags(mPaintFlag);
                mBmpPaint.setShader(mBmpShader);

                mRadius = old_circle_radius + (new_circle_radius - old_circle_radius) * value;
                mCircleX = old_center_x + (new_center_x - old_center_x) * value;
                mCircleY = old_center_y + (new_center_y - old_center_y) * value;

                float left = old_left + (new_left - old_left) * value;
                float right = old_right + (new_right - old_right) * value;
                float top = old_top + ((new_top - old_top) * value);
                float bottom = old_bottom + (new_bottom - old_bottom) * value;
                mRoundRect.set(left, top, right, bottom);

                mWhiteBGRadius = old_white_bg_round_rect_radius + (new_white_bg_round_rect_radius - old_white_bg_round_rect_radius) * value;

                invalidate();
            }
        });
        anim.addListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                mUIEnable = true;
                mDoingAnim = false;

                if (mListener != null)
                {
                    mListener.onShowWishTips();
                }
            }
        });
        anim.setDuration(400);
        anim.start();
    }

    public void narrow()
    {
        if (mBitmap == null || mBitmap.isRecycled()) return;

        MyBeautyStat.onClickByRes(R.string.AR祝福_找祝福_打开ar镜头_收回线索图);

        mUIEnable = false;
        isShow = false;
        mDoingAnim = true;

        final float new_x = (mViewW - mSmallBmpWH) / 2f;
        final float new_y = CameraPercentUtil.WidthPxToPercent(100 + 30);

        final float old_x = (mViewW - mBigBmpWH) / 2f;
        final float old_y = CameraPercentUtil.WidthPxToPercent(240 + 122);

        final float new_scale = mSmallBmpWH * 1f / mBitmap.getWidth();
        final float old_scale = mBigBmpWH * 1f / mBitmap.getWidth();

        final float old_center_x = mViewW / 2f;
        final float old_center_y = CameraPercentUtil.WidthPxToPercent(240 + 122 + 217);

        final float new_center_x = mViewW / 2f;
        final float new_center_y = CameraPercentUtil.WidthPxToPercent(100 + 30 + 62);

        final float old_circle_radius = mBigBmpWH * 1f / 2f;
        final float new_circle_radius = mSmallBmpWH * 1f / 2f;

        final float new_white_bg_round_rect_radius = mSmallBmpCircleRadius;
        final float old_white_bg_round_rect_radius = CameraPercentUtil.WidthPxToPercent(30);

        final float new_left = mViewW * 1f / 2f - mSmallBmpCircleRadius;
        final float new_right = mViewW * 1f / 2f + mSmallBmpCircleRadius;
        final float new_top = CameraPercentUtil.WidthPxToPercent(100 + 30);
        final float new_bottom = CameraPercentUtil.WidthPxToPercent(100 + 30 + 124);

        final float old_left = (mViewW * 1f - CameraPercentUtil.WidthPxToPercent(568)) / 2f;
        final float old_right = (mViewW * 1f + CameraPercentUtil.WidthPxToPercent(568)) / 2f;
        final float old_top = CameraPercentUtil.WidthPxToPercent(240);
        final float old_bottom = CameraPercentUtil.WidthPxToPercent(240 + 680);

        ValueAnimator anim = ValueAnimator.ofFloat(0, 1);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                float value = (float) animation.getAnimatedValue();
                float x = old_x + (new_x - old_x) * value;
                float y = old_y + (new_y - old_y) * value;
                float scale = old_scale + (new_scale - old_scale) * value;

                mBmpShaderMatrix.reset();
                mBmpShaderMatrix.postScale(scale, scale);
                mBmpShaderMatrix.postTranslate(x, y);
                mBmpShader.setLocalMatrix(mBmpShaderMatrix);
                mBmpPaint.reset();
                mBmpPaint.setFlags(mPaintFlag);
                mBmpPaint.setShader(mBmpShader);

                mRadius = old_circle_radius + (new_circle_radius - old_circle_radius) * value;
                mCircleX = old_center_x + (new_center_x - old_center_x) * value;
                mCircleY = old_center_y + (new_center_y - old_center_y) * value;

                float left = old_left + (new_left - old_left) * value;
                float right = old_right + (new_right - old_right) * value;
                float top = old_top + ((new_top - old_top) * value);
                float bottom = old_bottom + (new_bottom - old_bottom) * value;
                mRoundRect.set(left, top, right, bottom);

                mWhiteBGRadius = old_white_bg_round_rect_radius + (new_white_bg_round_rect_radius - old_white_bg_round_rect_radius) * value;

                invalidate();
            }
        });
        anim.addListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                mUIEnable = true;
                mDoingAnim = false;

                if (mListener != null)
                {
                    mListener.onHideWishTips();
                }
            }
        });
        anim.setDuration(400);
        anim.start();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        setMeasuredDimension(mViewW, mViewH);
    }

    private void initTouchRect()
    {
        int w = getMeasuredWidth() == 0 || getMeasuredHeight() == 0 ? mViewW : getMeasuredWidth();

        if (mBitmap != null)
        {
            mSmallBmpTouchRect = new RectF();
            mBigBmpTouchRect = new RectF();

            float left = (w - mSmallBmpWH) / 2f;
            float right = (w + mSmallBmpWH) / 2f;
            float top = CameraPercentUtil.WidthPxToPercent(100 + 32);
            float bottom = CameraPercentUtil.WidthPxToPercent(100 + 32 + 120);

            mSmallBmpTouchRect.set(left, top, right, bottom);

            left = (w - CameraPercentUtil.WidthPxToPercent(568)) / 2f;
            right = (w + CameraPercentUtil.WidthPxToPercent(568)) / 2f;
            top = CameraPercentUtil.WidthPxToPercent(240);
            bottom = CameraPercentUtil.WidthPxToPercent(240 + 680);

            mBigBmpTouchRect.set(left, top, right, bottom);
        }
    }

    private void reset()
    {
        if (mBitmap != null && !mBitmap.isRecycled())
        {
            mBitmap.recycle();
            mBitmap = null;
        }
        isShow = false;
        mSmallBmpTouchRect = null;
        mBigBmpTouchRect = null;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if (mUIEnable)
        {
            switch (event.getAction())
            {
                case MotionEvent.ACTION_DOWN:
                {
                    if (isShow)
                    {
                        if (mBigBmpTouchRect != null && !mBigBmpTouchRect.contains(event.getX(), event.getY()))
                        {
                            mCanNarrow = true;
                        }
                        return true;
                    }
                    else
                    {
                        if (mSmallBmpTouchRect != null && mSmallBmpTouchRect.contains(event.getX(), event.getY()))
                        {
                            mCanShow = true;
                            return true;
                        }
                        return false;
                    }
                }

                case MotionEvent.ACTION_UP:
                {
                    if (isShow)
                    {
                        if (mCanNarrow && mBigBmpTouchRect != null && !mBigBmpTouchRect.contains(event.getX(), event.getY()))
                        {
                            narrow();
                        }
                    }
                    else
                    {
                        if (mCanShow && mSmallBmpTouchRect != null && mSmallBmpTouchRect.contains(event.getX(), event.getY()))
                        {
                            show();
                        }
                    }
                    mCanShow = false;
                    mCanNarrow = false;
                    return false;
                }
            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        int saved = canvas.saveLayer(null, null, Canvas.ALL_SAVE_FLAG);

        float x = mViewW / 2f;
        float y = CameraPercentUtil.WidthPxToPercent(162 + 80 + 96 + 245);

        if (mHideAimedBG != null && mWhiteAimedBG != null)
        {
            // 透明圆 圆心
            mPaint.reset();
            mPaint.setFlags(mPaintFlag);
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(Color.WHITE);
            canvas.drawCircle(x, y, CameraPercentUtil.WidthPxToPercent(219), mPaint);

            mPaint.setXfermode(src_out_mode);
            mPaint.setColor(ColorUtils.setAlphaComponent(Color.BLACK, (int) (255 * 0.5f)));
            canvas.drawRect(0, 0, mViewW, mViewH, mPaint);

            int bitmapWH = CameraPercentUtil.WidthPxxToPercent(774);
            x = (mViewW - bitmapWH) / 2f;
            y = CameraPercentUtil.WidthPxToPercent(162 + 80 + 96 - 11);
            float scale = bitmapWH * 1f / mHideAimedBG.getWidth();
            mMatrix.reset();
            mMatrix.postScale(scale, scale);
            mMatrix.postTranslate(x, y);
            mPaint.reset();
            mPaint.setFlags(mPaintFlag);
            // 逆时针自转
            canvas.rotate(mCNNDegree, x + bitmapWH /2f, y + bitmapWH /2f );
            canvas.drawBitmap(mHideAimedBG, mMatrix, mPaint);

            bitmapWH = CameraPercentUtil.WidthPxxToPercent(740);
            x = (mViewW - bitmapWH) / 2f;
            y = CameraPercentUtil.WidthPxToPercent(162 + 80 + 96);
            scale = bitmapWH * 1f / mWhiteAimedBG.getWidth();
            mMatrix.reset();
            mMatrix.postScale(scale, scale);
            mMatrix.postTranslate(x, y);
            mPaint.reset();
            mPaint.setFlags(mPaintFlag);
            // 顺时针自转
            canvas.rotate(Math.abs(mCNNDegree) * 2f, x + bitmapWH/2f, y  + bitmapWH/2f);
            canvas.drawBitmap(mWhiteAimedBG, mMatrix, mPaint);

            mCNNDegree -= 1.2f;
            invalidate();
        }

        canvas.restoreToCount(saved);

        if (mBitmap != null)
        {
            if (!mDoingAnim)
            {
                if (isShow)
                {
                    if (mPackUpBmp != null)
                    {
                        mMatrix.reset();
                        int packUpBmpWH = CameraPercentUtil.WidthPxToPercent(80);
                        float scale = packUpBmpWH * 1f / mPackUpBmp.getWidth();
                        mPaint.reset();
                        mPaint.setFlags(mPaintFlag);
                        mMatrix.postScale(scale, scale);
                        mMatrix.postTranslate((mViewW - packUpBmpWH) / 2f, CameraPercentUtil.WidthPxToPercent(140));
                        canvas.drawBitmap(mPackUpBmp, mMatrix, mPaint);
                    }
                }
                else if (!TextUtils.isEmpty(mSmallBmpCircleTips))
                {
                    mPaint.reset();
                    mPaint.setFlags(mPaintFlag);
                    mPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 13, getResources().getDisplayMetrics()));
                    mPaint.setColor(Color.WHITE);
                    Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
                    mPaint.getTextBounds(mSmallBmpCircleTips, 0, mSmallBmpCircleTips.length(), mTextRectF);
                    x = (getMeasuredWidth() - mTextRectF.width()) / 2f;
                    y = CameraPercentUtil.WidthPxToPercent(134 + 120 + 14) - fontMetrics.ascent;
                    canvas.drawText(mSmallBmpCircleTips, x, y, mPaint);
                }
            }

            int save = canvas.saveLayer(null, null, Canvas.ALL_SAVE_FLAG);
            canvas.drawCircle(mCircleX, mCircleY, mRadius, mBmpPaint);

            mPaint.reset();
            mPaint.setFlags(mPaintFlag);
            mPaint.setColor(Color.WHITE);
            mPaint.setXfermode(dst_atop_mode);
            canvas.drawRoundRect(mRoundRect, mWhiteBGRadius, mWhiteBGRadius, mPaint);

            canvas.restoreToCount(save);
        }
    }
}
