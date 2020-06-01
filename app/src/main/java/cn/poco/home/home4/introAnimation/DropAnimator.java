package cn.poco.home.home4.introAnimation;//package com.example.lgd.animation.animation;

import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;
import android.view.animation.AccelerateInterpolator;


/**
 * Created by lgd on 2016/11/12.
 *  小圆下落动画
 */
public class DropAnimator extends ValueAnimator {
    public final static String TAG = "DropAnimator";
    public final static int DURATION = Config.DURATION_DROP;
    // 水滴下落距离
    public int distance;
    public int startX;
    public int startY;
    public int radius;
    private View mView;
    private final CirCle circle;

    public DropAnimator(View view) {
        mView = view;
        distance = Config.DISTANCE_DROP;
        startX = Config.START_X;
        startY = Config.START_Y;
        radius = Config.RADIUS_SMALL_CIRCLE;
        circle = new CirCle(startX, startY, radius);
        initAnim();
    }

    /**
     * 初始化动画的配置
     */
    public void initAnim() {
        this.setDuration(DURATION);
        this.setInterpolator(new AccelerateInterpolator());
        this.setIntValues(circle.y,circle.y+distance);
        this.addUpdateListener(new AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int curY = (int) animation.getAnimatedValue();
                circle.y = curY;
                mView.postInvalidate();
            }
        });
    }

    public void draw(Canvas canvas, Paint paint) {
            canvas.drawCircle(circle.x, circle.y,circle.radius,paint);
    }

}
