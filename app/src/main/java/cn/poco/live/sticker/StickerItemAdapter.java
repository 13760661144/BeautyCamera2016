package cn.poco.live.sticker;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import cn.poco.camera3.info.sticker.StickerInfo;
import cn.poco.camera3.mgr.StatusMgr;
import cn.poco.camera3.util.CameraPercentUtil;
import cn.poco.dynamicSticker.newSticker.MyHolder;
import cn.poco.resource.LiveVideoStickerResRedDotMgr2;

/**
 * 直播 贴纸
 * Created by Gxx on 2017/10/12.
 */

class StickerItemAdapter extends RecyclerView.Adapter implements View.OnClickListener
{
    private ArrayList<StickerInfo> mData;
    private StickerMgr.DataListener mDataListener;

    StickerItemAdapter()
    {
        mData = new ArrayList<>();
    }

    public void ClearAll()
    {
        mDataListener = null;
        mData = null;
    }

    void setStickerDataListener(StickerMgr.DataListener listener)
    {
        mDataListener = listener;
    }

    @Override
    public int getItemCount()
    {
        return mData != null ? mData.size() : 0;
    }

    public void setData(ArrayList<StickerInfo> data)
    {
        if (mData != null && data != null)
        {
            mData.clear();
            mData.addAll(data);
            notifyDataSetChanged();
        }
    }

    private StickerInfo getData(int index)
    {
        if (mData != null)
        {
            int size = mData.size();
            if (size > 0 && index >= 0 && index < size)
            {
                return mData.get(index);
            }
        }
        return null;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        StickerItemView itemView = new StickerItemView(parent.getContext());
        itemView.setLayoutParams(new RecyclerView.LayoutParams(CameraPercentUtil.WidthPxToPercent(108), CameraPercentUtil.WidthPxToPercent(108)));
        return new MyHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {
        if (holder != null && holder instanceof MyHolder)
        {
            MyHolder mh = (MyHolder) holder;
            StickerItemView itemView = mh.getItemView();
            if (itemView != null)
            {
                itemView.setTag(position);
                itemView.setOnClickListener(this);
                itemView.initImageConfig();
                StickerInfo info = getData(position);
                if (info != null)
                {
                    if(info.mAutoSelected && info.mStatus == StatusMgr.Type.LOCAL)
                    {
                        info.mIsSelected = true;
                        itemView.performClick();
                    }

                    // 是否被选中
                    itemView.setIsSelected(info.mIsSelected);

                    // 状态
                    itemView.setStickerStatus(info);

                    // 底图
                    itemView.setThumb(info.mThumb, info.mStatus == StatusMgr.Type.LOCAL);
                }
            }
        }
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder)
    {
        if (holder instanceof MyHolder)
        {
            StickerItemView itemView = (StickerItemView) ((MyHolder) holder).mItemView;
            itemView.ClearAll();
        }
    }

    @Override
    public void onClick(View v)
    {
        if(mDataListener != null)
        {
            final int index = (int) v.getTag();
            final StickerInfo info = getData(index);
            if (info != null)
            {
                switch (info.mStatus)
                {
                    case StatusMgr.Type.LIMIT:
                    case StatusMgr.Type.LOCK:
                    case StatusMgr.Type.NEW:
                    case StatusMgr.Type.NEED_DOWN_LOAD:
                    {
                        if(info.mStatus == StatusMgr.Type.NEW)
                        {
                            LiveVideoStickerResRedDotMgr2.getInstance().markResFlag(v.getContext(), info.id);
                        }

                        info.mStatus = StatusMgr.Type.DOWN_LOADING;
                        info.mProgress = 0;
                        StickerMgr.getInstance().notifyPagerViewDataChange(info);
                        StickerMgr.getInstance().DownloadRes(info);
                        break;
                    }

                    case StatusMgr.Type.BUILT_IN:
                    case StatusMgr.Type.LOCAL:
                    {
                        if (info.mIsSelected)
                        {
                            return;
                        }

                        int oldID = StickerMgr.getInstance().getSelectedInfo(StickerMgr.SelectedInfoKey.STICKER);
                        info.mIsSelected = true;
                        int newID = info.id;
                        StickerInfo oldInfo = StickerMgr.getInstance().getStickerInfoByID(oldID);
                        if (oldInfo != null)
                        {
                            oldInfo.mIsSelected = false;
                            StickerMgr.getInstance().notifyPagerViewDataChange(oldInfo);
                        }
                        StickerMgr.getInstance().updateSelectedInfo(StickerMgr.SelectedInfoKey.STICKER, newID);
                        StickerMgr.getInstance().notifyPagerViewDataChange(info);
                        mDataListener.onSelectedSticker(info);
                    }
                }
            }
        }
    }
}
