package cn.poco.camera3.beauty.data;

import android.support.annotation.FloatRange;

/**
 * 美颜参数[0f, 100f]
 *
 * @author lmx
 *         Created by lmx on 2017-12-15.
 */

public class BeautyData
{
    public float skinBeauty;       //肤质、美肤（磨皮）
    public float whitenTeeth;      //美牙
    public float skinType;        //肤色

    public float getSkinBeauty()
    {
        return skinBeauty;
    }

    public void setSkinBeauty(@FloatRange(from = 0.0f, to = 100.0f) float skinBeauty)
    {
        this.skinBeauty = skinBeauty;
    }

    public float getWhitenTeeth()
    {
        return whitenTeeth;
    }

    public void setWhitenTeeth(@FloatRange(from = 0.0f, to = 100.0f) float whitenTeeth)
    {
        this.whitenTeeth = whitenTeeth;
    }

    public float getSkinType()
    {
        return skinType;
    }

    public void setSkinType(@FloatRange(from = 0.0f, to = 100.0f) float skinType)
    {
        this.skinType = skinType;
    }
}
