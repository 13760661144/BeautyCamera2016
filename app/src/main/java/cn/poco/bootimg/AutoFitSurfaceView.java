package cn.poco.bootimg;

import android.content.Context;
import android.view.SurfaceView;

/**
 * Created by lgd on 2017/9/21.
 */

public class AutoFitSurfaceView extends SurfaceView
{

    private int mRatioWidth;
    private int mRatioHeight;

    public AutoFitSurfaceView(Context context)
    {
        super(context);
    }

    public void setAspectRatio(int width, int height)
    {
        mRatioWidth = width;
        mRatioHeight = height;

        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        if (mRatioWidth == 0 || mRatioHeight == 0)
        {
            setMeasuredDimension(width, height);
        } else {
            float ratio = mRatioWidth * 1f / mRatioHeight;
            if (width < ratio * height)
            {
                setMeasuredDimension(width, (int)(width / ratio));
            } else {
                setMeasuredDimension((int)(height * ratio), height);
            }
        }
    }
}
