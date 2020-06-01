package cn.poco.camera3.config;

import android.graphics.Color;

/**
 * @author Gxx
 *      Created by Gxx on 2017/8/18.
 */

public class TabItemConfig
{
    private int mShadowLayerRadius;
    private int mShadowLayerDx;
    private int mShadowLayerDy;
    private int mShadowLayerColor;
    private int mSelTextColor;
    private int mNoSelTextColor;

    public TabItemConfig()
    {
        mSelTextColor = Color.BLACK;
        mNoSelTextColor = Color.BLACK;
        mShadowLayerColor = Color.WHITE;
    }

    public int getShadowLayerRadius()
    {
        return mShadowLayerRadius;
    }

    public int getShadowLayerDx()
    {
        return mShadowLayerDx;
    }

    public int getShadowLayerDy()
    {
        return mShadowLayerDy;
    }

    public int getShadowLayerColor()
    {
        return mShadowLayerColor;
    }

    public int getSelTextColor()
    {
        return mSelTextColor;
    }

    public int getNoSelTextColor()
    {
        return mNoSelTextColor;
    }

    public void setShadowLayer(int radius, int dx, int dy, int color)
    {
        mShadowLayerColor = color;
        mShadowLayerDx = dx;
        mShadowLayerDy = dy;
        mShadowLayerRadius = radius;
    }

    public void setTextColor(int selColor, int noSelColor)
    {
        mSelTextColor = selColor;
        mNoSelTextColor = noSelColor;
    }
}
