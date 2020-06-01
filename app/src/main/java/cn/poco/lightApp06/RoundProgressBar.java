package cn.poco.lightApp06;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.view.View;
import android.widget.ProgressBar;

import cn.poco.tianutils.ShareData;

public class RoundProgressBar extends View {
    public static final int FILL = 0;
    public static final int STROKE = 1;

    private Paint m_paint;
    private Paint m_paint1;
    private PaintFlagsDrawFilter mDrawFilter;

    public int m_roundColor = -1;
    public int m_roundProgressColor = -1;

    public int m_roundRes = 0;
    public int m_roundProgressRes = 0;
    protected Bitmap m_roundBmp;
    protected Bitmap m_roundProgressBmp;

    public int m_textColor;
    public int m_textSize;
    public boolean m_isShowText = true;

    public int m_roundWidth;

    private int m_max;
    private int m_progress;

    public int m_style = STROKE;

    private int m_maxW;
    private int m_maxH;
    private int m_minW;
    private int m_minH;
    private Typeface m_typeFace;

    public RoundProgressBar(Context context) {
        super(context);
        ShareData.InitData(context);
        m_roundWidth = ShareData.PxToDpi_xhdpi(2);
        m_roundColor = 0x26ffffff;
        m_roundProgressColor = 0xffffffff;
        m_maxW = ShareData.PxToDpi_xhdpi(200);
        m_maxH = m_maxW;
        m_minW = ShareData.PxToDpi_xhdpi(100);
        m_minH = m_minW;
        m_textColor = 0xffffffff;
        m_textSize = ShareData.PxToDpi_xhdpi(100);

        m_paint = new Paint();
        m_paint1 = new Paint();
        m_typeFace = Typeface.createFromAsset(getContext().getAssets(), "fonts/code_light.otf");

        ProgressBar p = new ProgressBar(context);
        p.setMax(m_max);
    }

    public void initData() {
        if (m_roundRes != 0) {
            m_roundBmp = BitmapFactory.decodeResource(getResources(), m_roundRes);
        }
        if (m_roundProgressRes != 0) {
            m_roundProgressBmp = BitmapFactory.decodeResource(getResources(), m_roundProgressRes);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mDrawFilter == null) {
            mDrawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        }
        canvas.save();
        canvas.setDrawFilter(mDrawFilter);
        drawRoundProgress(canvas);
        drawProgressText(canvas);
        canvas.restore();
    }

    private void drawRoundProgress(Canvas canvas) {
        int center = m_maxW / 2;
        int radius = center - m_roundWidth / 2;
        m_paint.reset();
        m_paint.setColor(m_roundColor);
        m_paint.setAntiAlias(true);
        m_paint.setStyle(Style.STROKE);
        m_paint.setStrokeWidth(m_roundWidth);
        if (m_roundBmp != null) {
            m_paint.setColor(0xff000000);
            center = m_roundBmp.getWidth() / 2;
            radius = center - m_roundWidth / 2;
            canvas.drawBitmap(m_roundBmp, new Matrix(), m_paint);
        } else {
            canvas.drawCircle(center, center, radius, m_paint);
        }

        m_paint.setStrokeWidth(0);
        m_paint.setStrokeWidth(m_roundWidth);
        m_paint.setColor(m_roundProgressColor);
        RectF rect = new RectF(center - radius, center - radius, center + radius, center + radius);
        switch (m_style) {
            case STROKE:
                m_paint.setStyle(Style.STROKE);
                canvas.drawArc(rect, -90, 360f * m_progress / m_max, false, m_paint);
                break;

            case FILL:
                m_paint.setStyle(Style.FILL_AND_STROKE);
                canvas.drawArc(rect, -90, 360f * m_progress / m_max, true, m_paint);
                break;
        }
    }

    private void drawProgressText(Canvas canvas) {
        int center = m_maxW / 2;
        m_paint.reset();
        m_paint.setStrokeWidth(0);
        m_paint.setColor(m_textColor);
        m_paint.setTextSize(m_textSize);
        m_paint.setTypeface(m_typeFace); //设置字体
        //中间的进度百分比，先转换成float在进行除法运算，不然都为0
        int percent = (int) (((float) m_progress / (float) m_max) * 100);
        //测量字体宽度，我们需要根据字体的宽度设置在圆环中间
        float textWidth = m_paint.measureText(String.valueOf(percent));

        m_paint1.reset();
        m_paint1.setStrokeWidth(0);
        m_paint1.setColor(m_textColor);
        m_paint1.setTextSize(ShareData.PxToDpi_xhdpi(14));
        m_paint1.setTypeface(m_typeFace);

        float percentWidth = m_paint1.measureText("%");
        float width = textWidth + percentWidth;
        String text = String.valueOf(percent);
        if (m_isShowText && m_style == STROKE) {
            canvas.drawText(text, center - width / 2, center + m_textSize / 3, m_paint);
            canvas.drawText("%", center - width / 2 + textWidth + 5, center + m_textSize / 3, m_paint1);
        }
    }

    public synchronized void setMax(int max) {
        if (max < 0) {
            max = 0;
        }
        if (max != m_max) {
            m_max = max;
            postInvalidate();

            if (m_progress > max) {
                m_progress = max;
            }
            this.invalidate();
        }
    }

    public synchronized void setProgress(int progress) {
        setProgress(progress, false);
    }

    public synchronized void setProgress(int progress, boolean fromUser) {
        if (progress < 0) {
            progress = 0;
        }

        if (progress > m_max) {
            progress = m_max;
        }

        if (progress != m_progress) {
            m_progress = progress;
            this.invalidate();
        }
    }

    public synchronized int getProgress() {
        return m_progress;
    }

    public synchronized int getMax() {
        return m_max;
    }

    public synchronized boolean isFinished() {
        return m_max == m_progress;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int dw = 0;
        int dh = 0;
        if (m_roundBmp != null) {
            dw = Math.max(m_minW, Math.min(m_maxW, m_roundBmp.getWidth()));
            dh = Math.max(m_minH, Math.min(m_maxH, m_roundBmp.getHeight()));
        } else {
            dw = m_maxW;
            dh = m_maxH;
        }
        setMeasuredDimension(dw, dh);
    }

}
