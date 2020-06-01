package cn.poco.video.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.view.View;

import cn.poco.advanced.ImageUtils;
import cn.poco.home.home4.utils.PercentUtil;

/**
 * Created by lgd on 2017/7/13.
 */

public class LineView extends View
{
    private int[] lineHeights;
    private int span;
    private int strokeW;
    private Paint paint;

    private int mLineRound;

    public LineView(Context context)
    {
        super(context);
        //最长38px， 最短12px，粗4px，间隔8px
        lineHeights = new int[]{
                PercentUtil.HeightPxToPercent(12),
                PercentUtil.HeightPxToPercent(24),
                PercentUtil.HeightPxToPercent(32),
                PercentUtil.HeightPxToPercent(38),
                PercentUtil.HeightPxToPercent(38),
                PercentUtil.HeightPxToPercent(32),
                PercentUtil.HeightPxToPercent(24)};

        span = PercentUtil.WidthPxToPercent(12);
        strokeW = PercentUtil.WidthPxToPercent(4);
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(ImageUtils.GetSkinColor());
        paint.setStyle(Paint.Style.FILL);
        mLineRound = PercentUtil.HeightPxToPercent(6);
        //paint.setStrokeWidth(strokeW);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        int startX = 0;
        int lineH;
        int lineIndex = 0;
        while (strokeW * 2 + startX <= getWidth())
        {
            int i = lineIndex % lineHeights.length;
            startX = span * lineIndex;
            lineH = lineHeights[i];
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            {
                canvas.drawRoundRect(startX, (getHeight() - lineH) / 2, startX + strokeW, ((getHeight() + lineH) / 2), mLineRound, mLineRound, paint);
            }
            else
            {
                canvas.drawRect(startX, (getHeight() - lineH) / 2, startX + strokeW, ((getHeight() + lineH) / 2), paint);
            }
            lineIndex++;
        }

    }
}
