package cn.poco.camera3.ui.tab;

import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.poco.camera.CameraConfig;
import cn.poco.camera3.config.CameraUIConfig;
import cn.poco.camera3.info.TabInfo;
import cn.poco.camera3.config.TabItemConfig;
import cn.poco.home.home4.utils.PercentUtil;
import my.beautyCamera.R;

public class TabViewAdapter extends TabViewBaseAdapter
{
    private int mSelIndex = 0;
    private float mCurrRatio;

    public TabViewAdapter(CameraUIConfig config)
    {
        super(config);
        mCurrRatio = config.GetPreviewRatio();
    }

    @Override
    public int getItemCount()
    {
        return mData != null ? mData.size() : 0;
    }

    @Override
    public View onCreateView(ViewGroup parent, int viewType, int position)
    {
        FrameLayout itemView = new FrameLayout(parent.getContext());
        itemView.setOnClickListener(this);
        itemView.setOnTouchListener(this);
        itemView.setTag(position);
        LinearLayout.LayoutParams itemParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        itemView.setLayoutParams(itemParams);
        {
            TextView tv = new TextView(parent.getContext());
            tv.setId(R.id.camera_tab_text);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
            tv.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            tv.setGravity(Gravity.CENTER);

            TabInfo itemInfo = getTabInfoByIndex(position);
            if (itemInfo != null)
            {
                Object info = itemInfo.getInfo();
                if (info != null && info instanceof String)
                {
                    tv.setText((String) info);
                }
            }

            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
            params.leftMargin = PercentUtil.WidthPxToPercent(16);
            params.rightMargin = PercentUtil.WidthPxToPercent(16);
            params.bottomMargin = PercentUtil.HeightPxToPercent(40);
            itemView.addView(tv, params);
        }
        return itemView;
    }

    @Override
    public void onBindView(View view, final int position)
    {
        if (view != null && view instanceof FrameLayout)
        {
            FrameLayout itemView = (FrameLayout) view;
            View v = itemView.findViewById(R.id.camera_tab_text);
            if (v != null && v instanceof TextView)
            {
                TextView tv = (TextView) v;

                TabItemConfig config = (mCurrRatio >= CameraConfig.PreviewRatio.Ratio_16_9) ? mItemConfig169 : mItemConfig43;

                tv.setShadowLayer(config.getShadowLayerRadius(), config.getShadowLayerDx(), config.getShadowLayerDy(), config.getShadowLayerColor());

                tv.setTextColor((position == mSelIndex) ? config.getSelTextColor() : config.getNoSelTextColor());
            }
        }
    }

    public void setSelIndex(int index)
    {
        mSelIndex = index;
    }

    /**
     * 设置当前预览比例，用于 UI 适配
     *
     * @param ratio for example {@link cn.poco.camera.CameraConfig.PreviewRatio#Ratio_16_9}
     */
    public void setCurrPreviewRatio(float ratio)
    {
        mCurrRatio = ratio;
    }

    @Override
    public void onClick(View v)
    {
        if (mOnItemClickListener != null)
        {
            mOnItemClickListener.onItemClick((Integer) v.getTag());
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
            {
                v.setAlpha(0.4f);
                break;
            }

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_OUTSIDE:
            {
                v.setAlpha(1f);
                break;
            }
        }
        super.onTouch(v, event);
        return false;
    }
}
