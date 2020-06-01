package cn.poco.widget.recycle;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.widget.FrameLayout;

import cn.poco.recycleview.AbsConfig;

/**
 * @author lmx
 *         Created by lmx on 2017/7/10.
 */

public abstract class BaseDownMoreRecommendView extends FrameLayout
{
    protected FrameLayout mBaseFr;
    protected AbsConfig mConfig;
    protected Object[] mLogos;
    protected int mNum;

    protected int mW;
    protected int mH;

    public BaseDownMoreRecommendView(Context context, @NonNull AbsConfig mConfig, Object[] logos, int num)
    {
        super(context);
        this.mConfig = mConfig;
        mW = mConfig.def_item_w;
        mH = mConfig.def_item_h;

        mLogos = logos;
        mNum = num;

        initView();
    }

    public void initView()
    {
        mBaseFr = new FrameLayout(getContext());
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        params.gravity = Gravity.CENTER;
        addView(mBaseFr, params);
    }

    public void SetData(Object[] logos, int num, int index)
    {
        this.mLogos = logos;
        this.mNum = num;
    }
}
