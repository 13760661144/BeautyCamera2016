package cn.poco.camera3.ui.preview;

import android.graphics.Path;
import android.graphics.RectF;

import cn.poco.camera3.util.CameraPercentUtil;

/**
 * Created by Gxx on 2017/12/11.
 */

public class PhotoSaveUIConfig extends SaveViewUIConfig
{
    @Override
    public void init(int w, int h)
    {
        mLineWidth = CameraPercentUtil.WidthPxToPercent(6);
        mCircleWidth = CameraPercentUtil.WidthPxToPercent(138);
        mCircleRadius = mCircleWidth / 2f;
        mPaintMaxHalfWidth = mCircleRadius - 1;//确保弧形矩形成立

        Path path = new Path();
        path.moveTo(w / 2f - CameraPercentUtil.WidthPxToPercent(28), h / 2f + CameraPercentUtil.WidthPxToPercent(2));
        path.lineTo(w / 2f - CameraPercentUtil.WidthPxToPercent(9), h / 2f + CameraPercentUtil.WidthPxToPercent(24));
        path.lineTo(w / 2f + CameraPercentUtil.WidthPxToPercent(29), h / 2f - CameraPercentUtil.WidthPxToPercent(17));
        mPathMeasure.setPath(path, false);

        mArcRect = new RectF(w / 2f - mCircleRadius, h / 2f - mCircleRadius, w / 2f + mCircleRadius, h / 2f + mCircleRadius);
    }
}
