package cn.poco.beautifyEyes.Component.Widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;

/**
 * Created by Shine on 2016/12/6.
 */

public class LetterCenterView extends View {
    private Paint mPaint;
    private TextPaint mTextPaint;
    private String mDrawText;

    public LetterCenterView(Context context) {
        super(context);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.WHITE);
        mPaint.setShadowLayer(3, 0, 5, 0x50888888);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            setLayerType(LAYER_TYPE_SOFTWARE, mPaint);
        }

        mTextPaint = new TextPaint();
        mTextPaint.setTextSize(convertDpToPixel(14));
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(Color.parseColor("#000000"));
        mTextPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(heightMeasureSpec), MeasureSpec.EXACTLY));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float textHeight = mTextPaint.descent() - mTextPaint.ascent();
        float textOffset = (textHeight / 2) - mTextPaint.descent();
        RectF bounds = new RectF((float)2.5, 0, getWidth() - (float)2.5, getHeight() - 5);
        canvas.drawOval(bounds, mPaint);
        if (!TextUtils.isEmpty(mDrawText)) {
            canvas.drawText(mDrawText, bounds.centerX(), bounds.centerY() + textOffset, mTextPaint);
        }
    }

    public void setDrawText(String text) {
        mDrawText = text;
        invalidate();
    }

    private int convertDpToPixel(float dp) {
        int pixel= (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dp, getResources().getDisplayMetrics());
        return pixel;
    }
}
