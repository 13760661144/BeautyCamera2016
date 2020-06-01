package cn.poco.video.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

import cn.poco.advanced.ImageUtils;
import cn.poco.video.music.WaveBitmapFactory;

/**
 * Created by lgd on 2017/7/18.
 */

public class WaveLineView extends View
{
    private int waveLineSpan = 10;
    private int paintStroke = 5;
    private Paint mPaint;
    private WaveBitmapFactory.WaveInfo data;
    private double mZoom = 1;

    public WaveLineView(Context context, WaveBitmapFactory.WaveInfo data)
    {
        super(context);
        this.data = data;
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(ImageUtils.GetSkinColor());
        mPaint.setStrokeWidth(paintStroke);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        int ctr = getHeight() / 2;


        for (int i = 0; i < data.max; i += waveLineSpan)
        {
            if (i < data.mHeightsAtThisZoomLevel.length)
            {
                drawWaveformLine(
                        canvas, i,
                        (int) (ctr - data.mHeightsAtThisZoomLevel[i] * mZoom),
                        (int) (ctr + 1 + data.mHeightsAtThisZoomLevel[i] * mZoom),
                        mPaint);
            }
        }
    }

    protected void drawWaveformLine(Canvas canvas, int x, int y0, int y1, Paint paint)
    {
        int pos = data.max;
        float rat = ((float) getWidth() / pos);
        canvas.drawLine((int) (x * rat), y0, (int) (x * rat), y1, paint);
    }

    public void setZoom(double zoom)
    {
        mZoom = zoom;
    }
}
