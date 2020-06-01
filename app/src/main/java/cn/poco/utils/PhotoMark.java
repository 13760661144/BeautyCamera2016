package cn.poco.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Raining on 2017/3/27.
 * 图片日期水印位置
 */

public class PhotoMark {
    /**
     * 获取图片的参照size
     */
    public static int getReferenceSize(Bitmap bmp) {
        return Math.min(bmp.getWidth(), bmp.getHeight());
    }

    /**
     * 获取水印的宽度
     */
    public static float getLogoW(float s) {
        return s * 470f / 2048f;
    }

    /**
     * 获取水印的底部间距
     */
    public static float getLogoBottom(float s, boolean hasDate) {
        if (hasDate) {
            return getLogoRight(s) + getTextSize(s) + s * 26f / 2048f;
        } else {
            return getLogoRight(s);
        }
    }

    /**
     * 获取水印的右边间距
     */
    public static float getLogoRight(float s) {
        return s * 68f / 2048f;
    }

    /**
     * 获取日期的文子size
     */
    public static int getTextSize(float s) {
        int out = (int) (s * 50f / 2048f);
        if (out < 10) {
            out = 10;
        }
        out = ((out + 1) / 2) * 2;
        //System.out.println(out);
        return out;
    }

    public static void drawDate(Bitmap bmp) {
        if (bmp != null && bmp.getWidth() > 0 && bmp.getHeight() > 0) {
            int rs = getReferenceSize(bmp);
            float ls = getLogoW(rs);
            float lr = getLogoRight(rs);
            float ts = getTextSize(rs);
            float cx = bmp.getWidth() - lr - 48 / 2048f * rs;
            float cy = bmp.getHeight() - lr - ts * 0.1f;

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            Paint paint = new Paint();
            paint.setTextSize(ts);
            paint.setAntiAlias(true);
            paint.setFilterBitmap(true);
            paint.setColor(0xFFFFFFFF);
            paint.setTextAlign(Paint.Align.RIGHT);
            paint.setShadowLayer(0.1f * ts, 0.1f * ts, 0.05f * ts, 0x30000000);

            Canvas canvas = new Canvas(bmp);
            /*Paint paint2 = new Paint();
            paint2.setColor(0xff0000ff);
			paint2.setStyle(Paint.Style.FILL);
			canvas.drawRect(bmp.getWidth() - lr - ls, bmp.getHeight() - lr - ts, bmp.getWidth() - lr, bmp.getHeight() - lr, paint2);*/
            canvas.drawText(dateFormat.format(new Date()), cx, cy, paint);
        }
    }

    public static void drawDataLeft(Bitmap bmp) {
        if (bmp != null && bmp.getWidth() > 0 && bmp.getHeight() > 0) {
            int rs = getReferenceSize(bmp);
            float ls = getLogoW(rs);
            float lr = getLogoRight(rs);
            float ts = getTextSize(rs);
            float cx = lr + 48 / 2048f * rs;
            float cy = bmp.getHeight() - lr - ts * 0.1f;

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            Paint paint = new Paint();
            paint.setTextSize(ts);
            paint.setAntiAlias(true);
            paint.setFilterBitmap(true);
            paint.setColor(0xFFFFFFFF);
            paint.setTextAlign(Paint.Align.LEFT);
            paint.setShadowLayer(0.1f * ts, 0.1f * ts, 0.05f * ts, 0x30000000);

            Canvas canvas = new Canvas(bmp);
            canvas.drawText(dateFormat.format(new Date()), cx, cy, paint);
        }
    }

    public static void drawWaterMark(Bitmap bmp, Bitmap markBmp, boolean hasDate) {
        if (bmp != null && bmp.getWidth() > 0 && bmp.getHeight() > 0 && markBmp != null) {
            int rs = getReferenceSize(bmp);
            float ls = getLogoW(rs);
            float lr = getLogoRight(rs);
            float lb = getLogoBottom(rs, hasDate);
            float cx = bmp.getWidth() - lr - ls / 2f;
            float by = bmp.getHeight() - lb;
            float sx = ls / markBmp.getWidth();
            float sy = ls / markBmp.getHeight();
            float s = sx > sy ? sy : sx;

            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setFilterBitmap(true);

            Matrix matrix = new Matrix();
            matrix.postTranslate(cx - markBmp.getWidth() / 2f, by - markBmp.getHeight());
            matrix.postScale(s, s, cx, by);

            Canvas canvas = new Canvas(bmp);
            canvas.drawBitmap(markBmp, matrix, paint);
        }
    }

    public static void drawWaterMarkLeft(Bitmap bmp, Bitmap markBmp, boolean hasDate) {
        if (bmp != null && bmp.getWidth() > 0 && bmp.getHeight() > 0 && markBmp != null) {
            int rs = getReferenceSize(bmp);
            float ls = getLogoW(rs);
            float lr = getLogoRight(rs);
            float lb = getLogoBottom(rs, hasDate);
            float cx = lr + ls / 2f;
            float by = bmp.getHeight() - lb;
            float sx = ls / markBmp.getWidth();
            float sy = ls / markBmp.getHeight();
            float s = sx > sy ? sy : sx;

            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setFilterBitmap(true);

            Matrix matrix = new Matrix();
            matrix.postTranslate(cx - markBmp.getWidth() / 2f, by - markBmp.getHeight());
            matrix.postScale(s, s, cx, by);

            Canvas canvas = new Canvas(bmp);
            canvas.drawBitmap(markBmp, matrix, paint);
        }
    }

    public static Bitmap drawVideoWaterMark(Bitmap dest, Bitmap markBmp, boolean isLeft) {
        return drawVideoWaterMark(dest, markBmp, isLeft, false);
    }

    public static Bitmap drawVideoWaterMark(Bitmap dest, Bitmap markBmp, boolean isLeft, boolean hasDate) {
        if (dest != null && !dest.isRecycled() && markBmp != null && !markBmp.isRecycled()) {
            float sr = dest.getWidth() / 1080.0f;
            Matrix matrix = new Matrix();
            Paint paint = new Paint();

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String text = sdf.format(new Date());

            float textSize = 34.0f;
            float textWidth = 0.0f;
            float textHeight = 0.0f;

            if (!TextUtils.isEmpty(text)) {
                if (textSize <= 0) {
                    textSize = 34.0f;
                }
                textSize = Math.round(textSize * sr);

                paint.reset();
                paint.setTextSize(textSize);

                Rect bound = new Rect();
                paint.getTextBounds(text, 0, text.length(), bound);

                Paint.FontMetrics fontMetrics = paint.getFontMetrics();
                bound.bottom = Math.round(-fontMetrics.top);
                bound.top = Math.round(fontMetrics.bottom);

                textWidth = bound.width();
                textHeight = bound.height();
            }

            float left = 42.0f * sr;
            if (!isLeft) {
                left = dest.getWidth() - left - markBmp.getWidth();
            }

            float bottom = 21.0f * sr + textHeight + 40.0f * sr;
            if (!hasDate) {
                bottom = 40.0f * sr;
            }
            float scale = 178.0f * sr / markBmp.getWidth();
            float top = dest.getHeight() - bottom - markBmp.getHeight() * scale;

            matrix.reset();
            matrix.postScale(scale, scale);
            matrix.postTranslate(left, top);

            paint.reset();
            paint.setAntiAlias(true);
            paint.setFilterBitmap(true);
            Canvas canvas = new Canvas(dest);
            canvas.drawBitmap(markBmp, matrix, paint);

            if (hasDate && textWidth > 0 && textHeight > 0) {
                float xOffset = -2.0f * sr;

                paint.reset();
                paint.setAntiAlias(true);
                paint.setTextSize(textSize);
                paint.setColor(0xffffffff);
                paint.setShadowLayer(2, 1, 1, 0x26000000);
                /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    paint.setLetterSpacing(0.2f);
                }*/
                canvas.drawText(text, left + (markBmp.getWidth() * scale - textWidth) / 2 + xOffset, dest.getHeight() - (40.0f * sr), paint);
            }
        }
        return dest;
    }
}
