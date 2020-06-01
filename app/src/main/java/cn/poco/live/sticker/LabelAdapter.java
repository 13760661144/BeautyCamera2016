package cn.poco.live.sticker;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import cn.poco.camera3.info.sticker.LabelInfo;
import cn.poco.camera3.mgr.TypeMgr;
import cn.poco.camera3.ui.sticker.LabelItemView;
import cn.poco.dynamicSticker.newSticker.MyHolder;
import cn.poco.dynamicSticker.newSticker.PointCircle;
import cn.poco.resource.LiveVideoStickerGroupResRedDotMrg2;
import my.beautyCamera.R;

/**
 * 直播 贴纸标签
 * Created by Gxx on 2018/1/16.
 */

class LabelAdapter extends RecyclerView.Adapter implements View.OnClickListener
{
    private ArrayList<LabelInfo> mData;
    private StickerMgr.DataListener mDataListener;

    LabelAdapter()
    {
        mData = new ArrayList<>();
    }

    public void ClearAll()
    {
        mData = null;
        mDataListener = null;
    }

    void setDataListener(StickerMgr.DataListener listener)
    {
        mDataListener = listener;
    }

    public void setData(ArrayList<LabelInfo> data)
    {
        if (mData != null && data != null)
        {
            mData.clear();
            mData.addAll(data);
            notifyDataSetChanged();
        }
    }

    private LabelInfo getDataByIndex(int index)
    {
        if (mData != null && mData.size() > 0 && index >= 0 && index < mData.size())
        {
            return mData.get(index);
        }
        return null;
    }

    @Override
    public int getItemViewType(int position)
    {
        if (mData != null)
        {
            LabelInfo itemInfo = mData.get(position);
            if (itemInfo != null)
            {
                if (itemInfo.mType == TypeMgr.StickerLabelType.HOT)
                {
                    return LabelItemView.Type.TYPE_IMAGE;
                }
                else if (itemInfo.mType == TypeMgr.StickerLabelType.MANAGER)
                {
                    return LabelItemView.Type.TYPE_IMAGE_MGR;
                }
                return LabelItemView.Type.TYPE_TEXT;
            }
            else
            {
                return LabelItemView.Type.TYPE_TEXT;
            }
        }
        return LabelItemView.Type.TYPE_TEXT;
    }

    @Override
    public int getItemCount()
    {
        return mData != null ? mData.size() : 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        LabelItemView itemView = new LabelItemView(parent.getContext(), viewType);
        ViewGroup.LayoutParams vl = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        itemView.setLayoutParams(vl);
        return new MyHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {
        if (holder instanceof MyHolder)
        {
            LabelInfo info = mData.get(position);

            MyHolder mh = (MyHolder) holder;
            FrameLayout itemView = ((MyHolder) holder).getItemView();
            if (itemView != null)
            {
                itemView.setTag(position);
                itemView.setOnClickListener(this);
            }

            TextView tv = mh.getViewById(R.id.sticker_label_text);
            View bottomLine = mh.getViewById(R.id.sticker_label_bottom_line);
            PointCircle redPoint = mh.getViewById(R.id.sticker_label_point_right_top);
            ImageView logo = mh.getViewById(R.id.sticker_label_logo);

            if (info != null)
            {
                bottomLine.setAlpha(info.isSelected ? 1: 0);
                bottomLine.setBackgroundColor(0xFFFFFFFF);

                if (tv != null)
                {
                    tv.setText(info.mLabelName);
                    tv.setTextColor(0xFFFFFFFF);
                }

                if (logo != null)
                {
                    if (info.mType == TypeMgr.StickerLabelType.HOT)
                    {
                        logo.setImageResource(R.drawable.sticker_label_hot);
                    }
                    else if (info.mType == TypeMgr.StickerLabelType.MANAGER)
                    {
                        logo.setImageResource(R.drawable.sticker_manger_white);
                    }
                }

                if (redPoint != null)
                {
                    redPoint.setAlpha(info.isShowRedPoint ? 1 : 0);
                }
            }
        }
    }

    @Override
    public void onClick(View v)
    {
        if (v instanceof LabelItemView)
        {
            LabelItemView itemView = (LabelItemView) v;
            int index = (int) itemView.getTag();
            LabelInfo info = getDataByIndex(index);
            if (info != null)
            {
                if (info.isSelected)
                {
                    return;
                }
                if (info.isShowRedPoint)
                {
                    info.isShowRedPoint = false;
                    LiveVideoStickerGroupResRedDotMrg2.getInstance().markResFlag(v.getContext(), info.ID);
                }
            }
            if (mDataListener != null)
            {
                mDataListener.onSelectedLabel(index);
            }
        }
    }
}
