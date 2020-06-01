package cn.poco.camera3.ui.preview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.View;
import android.widget.ImageView;

import cn.poco.advanced.ImageUtils;

/**
 * Created by Gxx on 2017/12/11.
 */

public class SaveAnimView extends ImageView
{
    private Paint mPaint;
    private float mPaintWidth;
    private Path mPath;

    private int mW;
    private int mH;

    private boolean mAnimEnd;

    private SaveViewUIConfig mConfig;

    public SaveAnimView(Context context)
    {
        super(context);
//        mPaint = new Paint();
//        mPath = new Path();
    }

//    public void setConfig(SaveViewUIConfig config)
//    {
//        mConfig = config;
//        if (mConfig != null)
//        {
//            mConfig.init(mW, mH);
//        }
//        invalidate();
//    }

//    public void showAnim()
//    {
//        // 扩大画笔宽度,一半
//        ValueAnimator valueAnimator = ValueAnimator.ofFloat(1, mConfig.mPaintMaxHalfWidth);
//        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
//        {
//            @Override
//            public void onAnimationUpdate(ValueAnimator animation)
//            {
//                float half_width = (float) animation.getAnimatedValue();
//                if (mConfig != null)
//                {
//                    mPaintWidth = half_width * 2f;
//                    mConfig.updateArcRect(mW, mH, half_width);
//                    invalidate();
//                }
//            }
//        });
//        valueAnimator.setDuration(500);
//
//        ValueAnimator valueAnimator1 = ValueAnimator.ofFloat(0, 1);
//        valueAnimator1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
//        {
//            @Override
//            public void onAnimationUpdate(ValueAnimator animation)
//            {
//                float value = (float) animation.getAnimatedValue();
//                mPath.reset();
//
//                if (mConfig != null)
//                {
//                    mConfig.updatePath(value, mPath);
//                    invalidate();
//                }
//            }
//        });
//
//        valueAnimator1.addListener(new AnimatorListenerAdapter()
//        {
//            @Override
//            public void onAnimationEnd(Animator animation)
//            {
//                mAnimEnd = true;
//                mPath.reset();
//                if (mConfig != null)
//                {
//                    mConfig.updatePath(1, mPath);
//                    invalidate();
//                }
//            }
//        });
//        valueAnimator1.setDuration(300);
//
//        AnimatorSet set = new AnimatorSet();
//
//        set.playSequentially(valueAnimator, valueAnimator1);
//
//        set.start();
//    }

//    @Override
//    protected void onSizeChanged(int w, int h, int oldw, int oldh)
//    {
//        super.onSizeChanged(w, h, oldw, oldh);
//        mW = w;
//        mH = h;
//        if (mConfig != null)
//        {
//            mConfig.init(w, h);
//        }
//    }

//    @Override
//    protected void onDraw(Canvas canvas)
//    {
//        super.onDraw(canvas);
//
//        if (mConfig == null || mPaintWidth <= 0) return;
//
//        if (!mAnimEnd)
//        {
//            canvas.save();
//            mPaint.reset();
//            mPaint.setAntiAlias(true);
//            mPaint.setStyle(Paint.Style.STROKE);
//            mPaint.setStrokeWidth(mPaintWidth);
//            mPaint.setColor(ImageUtils.GetSkinColor());
//            canvas.drawArc(mConfig.mArcRect, 0, 360, false, mPaint);
//            canvas.restore();
//
//            canvas.save();
//            mPaint.reset();
//            mPaint.setAntiAlias(true);
//            mPaint.setStyle(Paint.Style.STROKE);
//            mPaint.setStrokeWidth(mConfig.mLineWidth);
//            mPaint.setStrokeJoin(Paint.Join.ROUND);
//            mPaint.setStrokeCap(Paint.Cap.ROUND);
//            mPaint.setColor(Color.WHITE);
//            canvas.drawPath(mPath, mPaint);
//            canvas.restore();
//        }
//        else
//        {
//            canvas.save();
//            mPaint.reset();
//            mPaint.setAntiAlias(true);
//            mPaint.setColor(ImageUtils.GetSkinColor());
//            canvas.drawCircle(mW/2f, mH/2f, mConfig.mCircleRadius, mPaint);
//            mPaint.setStyle(Paint.Style.STROKE);
//            mPaint.setStrokeWidth(mConfig.mLineWidth);
//            mPaint.setStrokeJoin(Paint.Join.ROUND);
//            mPaint.setStrokeCap(Paint.Cap.ROUND);
//            mPaint.setColor(Color.WHITE);
//            canvas.drawPath(mPath, mPaint);
//            canvas.restore();
//        }
//    }
}
