package cn.poco.camera3.ui.bgm;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

public class MaskView extends View
{
    private int mW, mH;
    private Paint mPaint;
    private float mRadius;

    public MaskView(Context context)
    {
        super(context);

        mPaint = new Paint();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        mW = w;
        mH = h;
        mRadius = Math.min(w, h) / 2f;
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        canvas.save();
        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setColor(0x1E000000);
        canvas.drawCircle(mW / 2f, mH / 2f, mRadius, mPaint);
        canvas.restore();
    }
}
