package cn.poco.campaignCenter.widget.component;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.Log;
import android.view.View;


/**
 * Created by Shine on 2017/1/16.
 */

public abstract class BaseLoadingView extends View {
    enum Style {
        VERTICAL,
        DIAGONAL_TOP_RIGHT,
        DIAGONAL_TOP_LEFT,
        DIAGONAL_BOTTOM_LEFT,
        DIAGONAL_BOTTOM_RIGHT
    }

    private final static int SWEEP_ANGLE = 180;
    private final static int DEFAULT_RATE_ANGLE = 45;

    protected Paint mPaint;
    protected Paint mAbovePaint;
    protected Paint mBottomPaint;
    protected Paint mHelperPaint;

    private Path mPath;
    private Path mAbovePath;
    private Path mBottomPath;
    private Path mHelpPath;

    private RectF mRect;
    private RectF mHelperRect;

    private int mStartAngle = 0;
    private int mStartAngle2 = 180;

    protected double mRate = 0;
    protected Integer mAboveColor = Color.RED;
    protected Integer mBottomColor = Color.WHITE;
    protected Integer mCircleColor = Color.WHITE;
    protected Style mCurrentStyle = Style.VERTICAL;

    private int mRotateAngle;

    public BaseLoadingView(Context context) {
        super(context);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(mCircleColor);
        mPaint.setStyle(Paint.Style.FILL);

        mAbovePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mAbovePaint.setColor(mCircleColor);
        mAbovePaint.setStyle(Paint.Style.FILL);

        mBottomPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBottomPaint.setColor(mBottomColor);
        mBottomPaint.setStyle(Paint.Style.FILL);

        mHelperPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mHelperPaint.setColor(mBottomColor);
        mHelperPaint.setStyle(Paint.Style.FILL);

        mPath = new Path();
        mAbovePath = new Path();
        mBottomPath = new Path();
        mHelpPath = new Path();

        mRect = new RectF();
        mHelperRect = new RectF();

        initialData(mAboveColor, mBottomColor, mCircleColor,mCurrentStyle);

//        final Handler handler = new Handler() {
//            @Override
//            public void handleMessage(Message msg) {
//                if (mRate <= 1.0) {
//                    if (mRate < 1) {
//                        mRate += 0.01;
//                    }
//                    invalidate();
//                }
//                Message msg2 = Message.obtain();
//                sendMessage(msg2);
//            }
//        };
//
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                Message msg = Message.obtain();
//                handler.sendMessage(msg);
//            }
//        }, 3000);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY));
        if (mCurrentStyle == Style.DIAGONAL_TOP_RIGHT) {
            mRotateAngle = DEFAULT_RATE_ANGLE;
        } else if (mCurrentStyle == Style.DIAGONAL_TOP_LEFT) {
            mRotateAngle = -DEFAULT_RATE_ANGLE;
        } else if (mCurrentStyle == Style.DIAGONAL_BOTTOM_LEFT) {
            mRotateAngle = -(DEFAULT_RATE_ANGLE + 90);
        } else if (mCurrentStyle == Style.DIAGONAL_BOTTOM_RIGHT) {
            mRotateAngle = DEFAULT_RATE_ANGLE + 90;
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.i("drawMe", "drawMe");
        mRect.left = 0;
        mRect.top = 0;
        mRect.right = this.getWidth();
        mRect.bottom = this.getHeight();
        canvas.drawArc(mRect, 0, 360, true, mPaint);

        canvas.save();
        canvas.rotate(mRotateAngle, this.getWidth() / 2, this.getHeight() / 2);

        if (mRate > 0) {
            mAbovePaint.setColor(mAboveColor);
        } else {
            mAbovePaint.setColor(mCircleColor);
        }

        mAbovePath.addArc(mRect, mStartAngle2, SWEEP_ANGLE);
        canvas.drawPath(mAbovePath, mAbovePaint);

        mBottomPath.addArc(mRect, mStartAngle, SWEEP_ANGLE);
        canvas.drawPath(mBottomPath, mBottomPaint);

        double rectLeft = 0;
        double rectTop = 0;
        double rectRight = 0;
        double rectBottom = 0;
        mRate = mRate <= 1 && mRate >= 0? mRate : mRate > 1 ? 1 : 0;

        if (mRate <= 0.50) {

            rectLeft = 0;
            rectTop = (this.getHeight() * mRate);
            rectRight = this.getWidth();
            rectBottom = this.getHeight() * (1 - mRate);
        } else if (mRate <= 1.0) {
            rectLeft = 0;
            rectTop = (this.getHeight() * (1 - mRate));
            rectRight = this.getWidth();
            rectBottom = (this.getHeight() * mRate);
        }

        mHelperRect.left = (float)rectLeft;
        mHelperRect.top= (float)Math.ceil(rectTop);
        mHelperRect.right = (float)rectRight;
        mHelperRect.bottom = (float)Math.floor(rectBottom);

        if (mRate <= 0.50) {
            mHelperPaint.setColor(mBottomColor);
            mHelpPath.addArc(mHelperRect, mStartAngle2, SWEEP_ANGLE);
        } else if (mRate <= 1.0) {
            mHelperPaint.setColor(mAboveColor);
            mHelpPath.addArc(mHelperRect, mStartAngle, SWEEP_ANGLE);
        } else {
            mHelperPaint.setColor(mAboveColor);
            mHelpPath.addArc(mHelperRect, mStartAngle, SWEEP_ANGLE);
        }

        canvas.clipPath(mBottomPath, Region.Op.UNION);
        canvas.drawPath(mHelpPath, mHelperPaint);
        canvas.restore();

        if (mRate < 1.0) {
            mHelpPath.reset();
        }
    }

    protected abstract void initialData(Integer boveColor, Integer bottomColor, Integer circleColor, Style style);

}
