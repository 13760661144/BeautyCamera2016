package cn.poco.video.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.support.v7.widget.AppCompatImageView;

import cn.poco.tianutils.ShareData;

/**
 * @author lmx
 *         Created by lmx on 2017/8/21.
 */

public class MusicCoverView extends AppCompatImageView
{
    protected Paint paint;
    protected PorterDuffXfermode porterDuffXfermode;
    protected int mWidth;
    protected int mHeight;
    protected boolean isDrawRoundInCircle = true;

    public MusicCoverView(Context context)
    {
        super(context);
        paint = new Paint();
        porterDuffXfermode = new PorterDuffXfermode(PorterDuff.Mode.XOR);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);


        if (isDrawRoundInCircle)
        {
            canvas.save();
            float size = Math.min(mWidth, mHeight);
            paint.reset();
            paint.setAntiAlias(true);
            paint.setFilterBitmap(true);
            paint.setColor(0x26ffffff);
            canvas.drawCircle(size / 2, size / 2, ShareData.PxToDpi_xhdpi(32) / 2, paint);

            paint.reset();
            paint.setAntiAlias(true);
            paint.setFilterBitmap(true);
            paint.setColor(0xCCFFFFFF); //80%
            //paint.setXfermode(porterDuffXfermode);
            canvas.drawCircle(size / 2, size / 2, ShareData.PxToDpi_xhdpi(22) / 2, paint);
            canvas.restore();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);
    }

    public void setDrawRoundInCircle(boolean drawRoundInCircle)
    {
        isDrawRoundInCircle = drawRoundInCircle;
        invalidate();
    }
}
