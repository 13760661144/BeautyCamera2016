package cn.poco.camera3.ui.preview;

import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;

/**
 * Created by Gxx on 2017/12/11.
 */

public abstract class SaveViewUIConfig
{
    public float mCircleRadius;

    public float mCircleWidth;

    public RectF mArcRect;

    public PathMeasure mPathMeasure;

    public float mLineWidth;

    public float mPaintMaxHalfWidth;

    public SaveViewUIConfig()
    {
        mArcRect = new RectF();
        mPathMeasure = new PathMeasure();
    }

    public abstract void init(int w, int h);

    public void updatePath(float percent, Path dst)
    {
        mPathMeasure.getSegment(0, mPathMeasure.getLength() * percent, dst, true);
    }

    public void updateArcRect(int w, int h, float half_paint_width)
    {
        mArcRect.set(w / 2f - mCircleRadius + half_paint_width, h / 2f - mCircleRadius + half_paint_width,
                w / 2f + mCircleRadius - half_paint_width, h / 2f + mCircleRadius - half_paint_width);
    }
}
