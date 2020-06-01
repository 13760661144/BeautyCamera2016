package cn.poco.campaignCenter.ui.cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

/**
 * Created by admin on 2016/11/22.
 */

public class DividerCell extends View {
    private static Paint mPaint;

    public DividerCell(Context context) {
        super(context);
        if (mPaint == null) {
            mPaint = new Paint();
        }
        mPaint.setColor(Color.parseColor("#000000"));
        mPaint.setAlpha(20);
    }

    public DividerCell(Context context, String color, int alpha) {
        super(context);
        if (mPaint == null) {
            mPaint = new Paint();
        }
        mPaint.setColor(Color.parseColor(color));
        mPaint.setAlpha(alpha);
    }



    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawLine(getPaddingLeft(), getHeight() / 2, getWidth() - getPaddingRight(), getHeight() / 2, mPaint);
    }
}
