package cn.poco.home.home4.introAnimation;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by lgd on 2016/11/13.
 */
public class IntroAnimationView extends View {
    private AnimationController mController;
    private Paint mPaint;

    public IntroAnimationView(Context context) {
        super(context);
        init();
    }

    public IntroAnimationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public IntroAnimationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        // create Controller
        mController = new AnimationController(this);
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.WHITE);
        mPaint.setAntiAlias(true);
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mController.draw(canvas, mPaint);
    }

    public AnimationController getAnimationController()
    {
        return mController;
    }
}
