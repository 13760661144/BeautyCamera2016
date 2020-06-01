package cn.poco.featuremenu.widget;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by Simon Meng on 2017/10/10.
 * Guangzhou Beauty Information Technology Co.,Ltd
 */

public class TopbarDefaultDrawable extends Drawable{
    private Paint mGradientPaint, mLayerPaint;
    private float mStartX, mStartY, mEndX, mEndY;
    private int mColorFrom, mColorTo;

    public TopbarDefaultDrawable(float startX, float startY, float endX, float endY, int colorFrom, int colorTo) {
        mStartX = startX;
        mStartY = startY;
        mEndX = endX;
        mEndY = endY;
        mColorFrom = colorFrom;
        mColorTo = colorTo;

        mGradientPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mGradientPaint.setStyle(Paint.Style.FILL);

        mLayerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLayerPaint.setStyle(Paint.Style.FILL);
        mLayerPaint.setColor(0x1A000000);
    }

    public void initShader() {
        Shader shader = new LinearGradient(mStartX, mStartY, mEndX, mEndY, new int[]{mColorFrom, mColorTo}, null, Shader.TileMode.CLAMP);
        mGradientPaint.setShader(shader);
    }

    public void setGradientCoordinate(float startX, float startY, float endX, float endY) {
        if (startX != -1) {
            mStartX = startX;
        }

        if (startY != -1) {
            mStartY = startY;
        }

        if (endX != -1) {
            mEndX = endX;
        }

        if (endY != -1) {
            mEndY = endY;
        }
    }

    public void setGradientColor(int colorFrom, int colorTo) {
        if (colorFrom != -1) {
            this.mColorFrom = colorFrom;
        }

        if (colorTo != -1) {
            this.mColorTo = colorTo;
        }
    }


    @Override
    public void draw(@NonNull Canvas canvas) {
        initShader();
        Rect bounds = this.getBounds();
        canvas.drawRect(bounds.left, bounds.top, bounds.right, bounds.bottom, mGradientPaint);
        canvas.drawRect(bounds, mLayerPaint);
    }

    @Override
    public void setAlpha(@IntRange(from = 0, to = 255) int alpha) {

    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return PixelFormat.OPAQUE;
    }
}
