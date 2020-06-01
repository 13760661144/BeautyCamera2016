package cn.poco.camera3.beauty.recycler;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.TextView;

import cn.poco.recycleview.AbsAdapter;
import cn.poco.recycleview.BaseItem;
import my.beautyCamera.R;

/**
 * @author lmx
 *         Created by lmx on 2017-12-08.
 */

public class ShapeNonView extends BaseItem
{
    protected ShapeExAdapterConfig mConfig;

    protected TextView mTVTitle;

    protected ShapeCircleView mIconView;

    public ShapeNonView(@NonNull Context context, ShapeExAdapterConfig mConfig)
    {
        super(context);
        this.mConfig = mConfig;
        init();
    }

    private void init()
    {
        mIconView = new ShapeCircleView(getContext());
        mIconView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        mIconView.setImageResource(R.drawable.ic_shape_non);
        LayoutParams params = new LayoutParams(mConfig.def_item_w, mConfig.def_item_w);
        params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
        addView(mIconView, params);

        mTVTitle = new TextView(getContext());
        mTVTitle.setTextColor(0xff000000);
        mTVTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
        mTVTitle.setGravity(Gravity.CENTER);
        params = new LayoutParams(mConfig.def_item_w, mConfig.def_item_title_h);
        params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
        params.topMargin = mConfig.def_item_w + mConfig.def_title_top_margin;
        addView(mTVTitle, params);
    }

    @Override
    public void onSelected()
    {
        mIconView.setDrawInnerMask(false);
        mIconView.onSelected();
    }

    @Override
    public void onUnSelected()
    {
        mIconView.setDrawInnerMask(false);
        mIconView.onUnSelected();
    }

    @Override
    public void onClick()
    {

    }

    @Override
    public void SetData(AbsAdapter.ItemInfo info, int index)
    {
        if (info != null && info instanceof ShapeExAdapter.ShapeExItemInfo && mTVTitle != null)
        {
            mTVTitle.setText(((ShapeExAdapter.ShapeExItemInfo) info).m_name);
        }
    }

    @Override
    public boolean performClick()
    {
        return super.performClick();
    }
}
