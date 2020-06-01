package cn.poco.camera3.info;

import android.animation.ValueAnimator;

/**
 * Created by Gxx on 2017/11/10.
 */

public class StickerDownloadAnim extends ValueAnimator
{
    private Object mRes;

    public Object getRes()
    {
        return mRes;
    }

    public void setRes(Object mRes)
    {
        this.mRes = mRes;
    }
}
