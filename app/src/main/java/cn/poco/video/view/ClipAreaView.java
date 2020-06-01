package cn.poco.video.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.view.View;

import cn.poco.advanced.ImageUtils;
import cn.poco.camera3.util.CameraPercentUtil;

/**
 *
 * Created by lgd on 2017/6/19.
 */

public class ClipAreaView extends View
{
    public final static int horizontalLineH = CameraPercentUtil.HeightPxToPercent(5);   //边框水平线高度
    public final static int verticalLineW = CameraPercentUtil.WidthPxToPercent(20);    //边框垂直线宽度
    public final static int radius = CameraPercentUtil.WidthPxToPercent(12);

    private int width;
    private int height;

    private RectF topRectF;
    private RectF bottomRectF;

    private Paint bkPaint;
    private Paint framePaint;

    private float[] leftFloat;
    private float[] rightFloat;

    public ClipAreaView(Context context)
    {
        super(context);
        init();
    }

    private void init()
    {
        bkPaint = new Paint();
        bkPaint.setColor(ImageUtils.GetSkinColor());
        bkPaint.setAntiAlias(true);
        bkPaint.setFilterBitmap(true);

        framePaint = new Paint();
        framePaint.setAntiAlias(true);
        framePaint.setFilterBitmap(true);

        //左上角；右上角；右下角；左下角；
        leftFloat = new float[]{
                radius, radius,
                0, 0,
                0, 0,
                radius, radius
        };
        rightFloat = new float[]{
                0, 0,
                radius, radius,
                radius, radius,
                0, 0
        };
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;

        if (w < verticalLineW * 2 || h < horizontalLineH * 2)
        {
            throw new RuntimeException("size wrong");
        }

        //横向两条线
        topRectF = new RectF(verticalLineW, 0, w - verticalLineW, horizontalLineH);
        bottomRectF = new RectF(verticalLineW, h - horizontalLineH, w - verticalLineW, h);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        //垂直两条线
        canvas.drawBitmap(roundFrame(leftFloat), 0, 0, framePaint);
        canvas.drawBitmap(roundFrame(rightFloat), width - verticalLineW, 0, framePaint);

        //横向两条线
        canvas.drawRect(topRectF, bkPaint);
        canvas.drawRect(bottomRectF, bkPaint);
    }


    private Bitmap roundFrame(float[] radius)
    {
        Bitmap bm = Bitmap.createBitmap(verticalLineW, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bm);
        Path path = new Path();
        path.addRoundRect(new RectF(0, 0, verticalLineW, height), radius, Path.Direction.CW);
        Paint bitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bitmapPaint.setColor(ImageUtils.GetSkinColor());
        c.drawPath(path, bitmapPaint);
        return bm;
    }
}
