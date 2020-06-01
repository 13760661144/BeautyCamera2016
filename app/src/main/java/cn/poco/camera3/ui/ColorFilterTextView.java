package cn.poco.camera3.ui;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.TypedValue;
import android.view.View;

import cn.poco.camera3.util.CameraPercentUtil;

/**
 * 镜头切换滤镜 的文案view
 * Created by Gxx on 2017/11/6.
 */

public class ColorFilterTextView extends View {
    private int mXOffset;
    private int mYOffset;
    private int mViewH;

    private String mText;
    private float mTextSize;
    private Paint mPaint;
    private Rect mTextRect;
    private int mTextW;
    private int mTextH;

    private float mAnimValue = 1f;
    private int mDegree;
    private boolean isLandScape = false;

    public ColorFilterTextView(Context context) {
        super(context);
        mPaint = new Paint();
        mTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 22, getResources().getDisplayMetrics());
        mTextRect = new Rect();
        mXOffset = CameraPercentUtil.WidthPxToPercent(32);
        mYOffset = CameraPercentUtil.WidthPxToPercent(0);
    }

    public void setText(String text) {
        mText = text;
        mPaint.reset();
        mPaint.setTextSize(mTextSize);
        mPaint.getTextBounds(mText, 0, mText.length(), mTextRect);
        mTextW = mTextRect.width();
        mTextH = mTextRect.height();
        invalidate();
    }

    public void updateRotateAnimInfo(boolean isLandScape, int degree, float value) {
        this.isLandScape = isLandScape;
        mDegree = degree;
        mAnimValue = value;
        invalidate();
    }

    public void startScaleAnim() {
        this.setPivotX(mXOffset * 1f + (isLandScape ? mTextH / 2f : mTextW / 2f));
        this.setPivotY(mYOffset * 1f + mViewH * 1f / 2f);
        ObjectAnimator animator = ObjectAnimator.ofFloat(this, "scaleX", 1.3f, 1f);
        ObjectAnimator animator1 = ObjectAnimator.ofFloat(this, "scaleY", 1.3f, 1f);
        AnimatorSet set = new AnimatorSet();
        set.setDuration(150);
        set.playTogether(animator, animator1);
        set.start();
    }

    public void ClearAll() {
        mText = null;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mViewH == 0) {
            mViewH = h;
        }
    }

    public void updateToastMsgHeight(int height) {
        mViewH = height;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mText != null) {
            canvas.save();

            float startX = mXOffset * 1f + (isLandScape ?
                    (mTextW / 2f + (mTextH - mTextW) * mAnimValue / 2f)
                    : (mTextH / 2f + (mTextW - mTextH) * mAnimValue / 2f));
            float startY = mYOffset * 1f + mViewH / 2f;

            canvas.translate(startX, startY);
            canvas.rotate(mDegree, 0, 0);
            mPaint.reset();
            mPaint.setAntiAlias(true);
            mPaint.setTextSize(mTextSize);
            mPaint.setTextAlign(Paint.Align.CENTER);
            mPaint.setColor(Color.WHITE);
            canvas.drawText(mText, 0, mTextH * (isLandScape ? mAnimValue : 1f - mAnimValue) / 3f, mPaint);
            canvas.restore();
        }
    }
}
