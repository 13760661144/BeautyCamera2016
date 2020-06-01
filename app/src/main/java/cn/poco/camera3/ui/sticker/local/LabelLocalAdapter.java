package cn.poco.camera3.ui.sticker.local;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.ArrayList;

import cn.poco.advanced.ImageUtils;
import cn.poco.camera3.cb.sticker.StickerLocalInnerListener;
import cn.poco.camera3.info.sticker.LabelLocalInfo;
import cn.poco.dynamicSticker.newSticker.MyHolder;
import my.beautyCamera.R;

/**
 * 贴纸素材管理页
 * Created by Gxx on 2017/10/30.
 */

public class LabelLocalAdapter extends RecyclerView.Adapter implements View.OnClickListener
{
    private ArrayList<LabelLocalInfo> mData;
    private StickerLocalInnerListener mHelper;

    public void ClearAll()
    {
        mData = null;
        mHelper = null;
    }

    public void setData(ArrayList<LabelLocalInfo> data)
    {
        if (data != null)
        {
            mData = data;
            notifyDataSetChanged();
        }
    }

    public LabelLocalInfo getData(int index)
    {
        if (mData != null)
        {
            return mData.get(index);
        }
        return null;
    }

    void setStickerLocalDataHelper(StickerLocalInnerListener helper)
    {
        mHelper = helper;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        LabelLocalItemView itemView = new LabelLocalItemView(parent.getContext());
        ViewGroup.LayoutParams vl = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        itemView.setLayoutParams(vl);
        return new MyHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {
        if (holder instanceof MyHolder)
        {
            LabelLocalInfo info = getData(position);

            MyHolder mh = (MyHolder) holder;
            FrameLayout itemView = ((MyHolder) holder).getItemView();
            if (itemView != null)
            {
                itemView.setTag(position);
                itemView.setOnClickListener(this);
            }

            TextView tv = mh.getViewById(R.id.sticker_local_label_text);
            View bottomLine = mh.getViewById(R.id.sticker_local_label_bottom_line);

            if (info != null)
            {
                tv.setText(info.mLabelName);
                tv.setTextColor(info.isSelected ? ImageUtils.GetSkinColor() : Color.BLACK);
                bottomLine.setAlpha(info.isSelected ? 1 : 0);
            }
        }
    }

    @Override
    public int getItemCount()
    {
        return mData != null ? mData.size() : 0;
    }

    @Override
    public void onClick(View v)
    {
        int index = (int) v.getTag();
        LabelLocalInfo info = getData(index);
        if (info != null && info.isSelected)
        {
            return;
        }

        if (mHelper != null)
        {
            mHelper.onSelectedLabel(index);
        }
    }
}
