package cn.poco.MaterialMgr2;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.RectF;
import android.view.View;

import cn.poco.tianutils.ShareData;

public class RoundProgressBar extends View {
    public static final int FILL = 0;
    public static final int STROKE = 1;

    private Paint m_paint;
    private PaintFlagsDrawFilter mDrawFilter;
    private int m_progressColor = 0xffffffff;
    private int m_progressBgColor = 0;

    public int m_roundWidth;

    private int m_max = 100;
    private int m_progress;

    public int m_style = STROKE;

    private int m_width;
    private int m_height;

    public RoundProgressBar(Context context, int width, int height) {
        super(context);

        ShareData.InitData(context);
        m_roundWidth = ShareData.PxToDpi_xhdpi(2);
        m_width = width;
        m_height = height;

        m_paint = new Paint();
    }

    protected void SetProgressColor(int color)
    {
        m_progressColor = color;
    }

    public void SetProgressBgColor(int color)
    {
        m_progressBgColor = color;
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
        canvas.restore();
    }

    private void drawRoundProgress(Canvas canvas) {
        int center = m_width / 2;
        int radius = center - m_roundWidth / 2;
        m_paint.reset();
        m_paint.setColor(m_progressBgColor);
        m_paint.setAntiAlias(true);
        m_paint.setStyle(Style.STROKE);
        m_paint.setStrokeWidth(m_roundWidth);
        canvas.drawCircle(center, center, radius, m_paint);

        m_paint.setStrokeWidth(m_roundWidth);
        m_paint.setColor(m_progressColor);
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
//        m_progress = 50;
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
        setMeasuredDimension(m_width, m_height);
    }

}
