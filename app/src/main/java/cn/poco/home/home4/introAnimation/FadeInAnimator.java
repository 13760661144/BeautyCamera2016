package cn.poco.home.home4.introAnimation;

import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;


/**
 * Created by lgd on 2016/11/12.
 */

public class FadeInAnimator extends ValueAnimator {
    public final static String TAG = "FadeInAnimator";
    public final static int DURATION = Config.DURATION_FADE_IN;
    private final View mView;
    private int centerX;
    private int centerY;
    private int radius;
    private CirCle topCircle;
    private CirCle BottomCircle;
    private Paint mPaint;
    public FadeInAnimator(View view) {
        mView = view;
        centerX = Config.CENTER_X;
        centerY = Config.CENTER_Y;
        int distance = Config.DISTANCE_SEGREGATE;
        radius = Config.RADIUS_BIG_CIRCLE;
        topCircle = new CirCle(centerX, centerY-distance, radius);
        BottomCircle = new CirCle(centerX, centerY+distance, radius);
        initAnim();
    }

    private void initAnim() {
        this.setDuration(DURATION);
        this.setIntValues(255,0);
        this.addUpdateListener(new AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int alpha = (int) animation.getAnimatedValue();
                BottomCircle.alpha =alpha;
                topCircle.alpha =alpha;
                if(fadeInCallBack!=null){
                    fadeInCallBack.fadeIn(alpha*1.0f/255);
                }
                mView.postInvalidate();
            }
        });
    }

    public void draw(Canvas canvas, Paint paint) {
            if(mPaint ==null) {
                mPaint = new Paint(paint);
            }
            mPaint.setAlpha(topCircle.alpha);
            canvas.drawCircle(topCircle.x, topCircle.y, topCircle.radius, mPaint);
            canvas.drawCircle(BottomCircle.x, BottomCircle.y, topCircle.radius, mPaint);
    }
    private FadeInCallBack fadeInCallBack;
    interface FadeInCallBack{
        void fadeIn(float alpha);
    }

    public void setFadeInCallBack(FadeInCallBack fadeInCallBack)
    {
        this.fadeInCallBack = fadeInCallBack;
    }
}
