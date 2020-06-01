package cn.poco.home.home4.introAnimation;

import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

/**
 * Created by lgd on 2016/11/12.
 */

public class FadeOutAnimator extends ValueAnimator {
    public final static String TAG = "FadeOutAnimator";
    public final static int DURATION = Config.DURATION_FADE_OUT;
    private final View mView;
    private int centerX;
    private int centerY;
    private int radius;
    private CirCle circle;
    private Paint mPaint;

    public FadeOutAnimator(View view) {
        mView = view;
        centerX = Config.CENTER_X;
        centerY = Config.CENTER_Y;
        radius = Config.RADIUS_BIG_CIRCLE;
        circle = new CirCle(centerX, centerY, radius);
        initAnim();
    }

    private void initAnim() {
        final int toRadius = circle.radius;
        this.setDuration(DURATION);
        this.setIntValues(0,toRadius);
        this.addUpdateListener(new AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int radius = (int) animation.getAnimatedValue();
                circle.radius = radius;
                circle.alpha = radius*255/toRadius;
                mView.postInvalidate();
            }
        });
    }

    public void draw(Canvas canvas, Paint paint) {
            if(mPaint ==null) {
                mPaint = new Paint(paint);
            }
            mPaint.setAlpha(circle.alpha);
            canvas.drawCircle(circle.x, circle.y,circle.radius,mPaint);
//            Log.d(TAG,"radius:"+radius+" alpha:"+circle.radius+" Radius"+circle.radius);
    }
}
