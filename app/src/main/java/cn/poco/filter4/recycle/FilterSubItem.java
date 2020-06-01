package cn.poco.filter4.recycle;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import cn.poco.recycleview.AbsAdapter;
import cn.poco.recycleview.BaseItem;
import cn.poco.tianutils.ShareData;
import my.beautyCamera.R;

/**
 * Created by lgd on 2017/5/26.
 */

public class FilterSubItem extends BaseItem
{
    private ImageView mLogo;
    private TextView mBottomText;
    private View mBackColor;
    private ImageView mSelectLogo;
    private FilterConfig config;
    private boolean mIsSelect;

    public FilterSubItem(@NonNull Context context)
    {
        super(context);
        init();
    }

    public FilterSubItem(@NonNull Context context, FilterConfig config)
    {
        super(context);
        this.config = config;
        init();
    }

    private void init()
    {
        LayoutParams params;
        mLogo = new ImageView(getContext());
        params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(127));
        params.gravity = Gravity.TOP;
        addView(mLogo, params);

        mBackColor = new View(getContext());
        mBackColor.setVisibility(View.GONE);
        params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(127));
        params.gravity = Gravity.TOP;
        params.bottomMargin = ShareData.PxToDpi_xhdpi(37); //不盖住文字
        addView(mBackColor, params);

        mBottomText = new TextView(getContext());
        mBottomText.setGravity(Gravity.CENTER);
        mBottomText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 9f);
        mBottomText.setTextColor(Color.WHITE);
        params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(37));   //164-127
        params.gravity = Gravity.BOTTOM;
        addView(mBottomText, params);

        mSelectLogo = new ImageView(getContext());
        if (config != null && config.isCamera)
        {
            mSelectLogo.setImageResource(R.drawable.filter_selected_tips_icon_none);
        }
        else
        {
            mSelectLogo.setImageResource(R.drawable.filter_selected_tips_icon);
        }
        mSelectLogo.setVisibility(View.GONE);
        params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        params.bottomMargin = ShareData.PxToDpi_xhdpi(20);
        addView(mSelectLogo, params);
    }

    @Override
    public void onSelected()
    {
        mIsSelect = true;
        mBackColor.setVisibility(View.VISIBLE);
        mSelectLogo.setVisibility(View.VISIBLE);
    }

    @Override
    public void onUnSelected()
    {
        mIsSelect = false;
        mBackColor.setVisibility(View.GONE);
        mSelectLogo.setVisibility(View.GONE);
    }

    public boolean isSelect()
    {
        return mIsSelect;
    }

    @Override
    public void onClick()
    {

    }

    public void onAlphaMode()
    {
        if (mSelectLogo != null)
        {
            mSelectLogo.setImageResource(R.drawable.filter_selected_back_icon);
        }
    }

    public void closeAlphaMode()
    {
        if (mSelectLogo != null)
        {
            if (config != null && config.isCamera)
            {
                mSelectLogo.setImageResource(R.drawable.filter_selected_tips_icon_none);
            }
            else
            {
                mSelectLogo.setImageResource(R.drawable.filter_selected_tips_icon);
            }
        }
    }

    @Override
    public void SetData(AbsAdapter.ItemInfo info, int index)
    {
        if (info instanceof FilterAdapter.ItemInfo)
        {
            FilterAdapter.ItemInfo itemInfo = (FilterAdapter.ItemInfo) info;
            mBottomText.setText(itemInfo.m_names[index]);
            mBottomText.setBackgroundColor(itemInfo.m_bkColor);
            mBottomText.getBackground().setAlpha(178);   //70
            mBackColor.setBackgroundColor(itemInfo.m_bkColor);
            mBackColor.getBackground().setAlpha(240);   //94
            Glide.with(getContext()).load(itemInfo.m_logos[index]).into(mLogo);
        }
    }
}
