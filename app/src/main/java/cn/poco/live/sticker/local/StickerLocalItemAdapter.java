package cn.poco.live.sticker.local;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import cn.poco.camera3.info.sticker.StickerInfo;
import cn.poco.camera3.mgr.StatusMgr;
import cn.poco.camera3.ui.sticker.local.StickerLocalItemView;
import cn.poco.camera3.util.CameraPercentUtil;
import cn.poco.campaignCenter.utils.ToastUtil;
import cn.poco.dynamicSticker.newSticker.MyHolder;

/**
 * @author Created by Gxx on 2017/10/30.
 */

public class StickerLocalItemAdapter extends RecyclerView.Adapter implements View.OnClickListener
{
    private ArrayList<StickerInfo> mData;
    private StickerLocalMgr.DataListener mDataListener;
    private final int mImageViewTagKey = -1;

    void setDataListener(StickerLocalMgr.DataListener listener)
    {
        mDataListener = listener;
    }

    public void setData(ArrayList<StickerInfo> data)
    {
        if (data != null)
        {
            mData = data;
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
    public void onViewRecycled(RecyclerView.ViewHolder holder)
    {
        if (holder != null && holder instanceof MyHolder)
        {
            MyHolder mh = (MyHolder) holder;
            StickerLocalItemView itemView = mh.getItemView();
            itemView.ClearAll();
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        StickerLocalItemView itemView = new StickerLocalItemView(parent.getContext());
        itemView.setLayoutParams(new RecyclerView.LayoutParams(CameraPercentUtil.WidthPxToPercent(108), CameraPercentUtil.WidthPxToPercent(108)));
        return new MyHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {
        if (holder != null && holder instanceof MyHolder)
        {
            MyHolder mh = (MyHolder) holder;
            StickerLocalItemView itemView = mh.getItemView();

            StickerInfo info = getData(position);
            if (info != null && itemView != null)
            {
                itemView.setTag(mImageViewTagKey, position);
                itemView.setOnClickListener(this);
                itemView.setAlpha(info.mStatus == StatusMgr.Type.BUILT_IN ? 0.25f : 1f);
                itemView.setSelected(info.mIsInMgrSelected);
                itemView.setThumb(info.mThumb);
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
        int index = (int) v.getTag(mImageViewTagKey);
        StickerInfo info = getData(index);
        if (info != null)
        {
            if (info.mStatus != StatusMgr.Type.BUILT_IN)
            {
                info.mIsInMgrSelected = !info.mIsInMgrSelected;

                StickerLocalMgr.getInstance().notifyItemDataChanged(info);

                boolean isAllSelected = StickerLocalMgr.getInstance().isAllStickerSelected(mData);
                boolean isSelectedNone = StickerLocalMgr.getInstance().isSelectedNone(mData);

                if (mDataListener != null)
                {
                    mDataListener.onChangeSelectedIconStatus(!isAllSelected ? StickerMgrPage.SelectedIconType.CHECK_ALL : StickerMgrPage.SelectedIconType.SELECTED_NONE);

                    mDataListener.onChangeDeleteIconAlpha(isSelectedNone ? StickerMgrPage.DeleteIconType.DO_NOT_CLICK : StickerMgrPage.DeleteIconType.CLICKABLE);

                    mDataListener.onLabelScrollToSelected(StickerLocalMgr.getInstance().getSelectedLabelIndex());
                }
            }
            else
            {
                ToastUtil.showToast(v.getContext(), "内置素材不可删除");
            }
        }
    }

    public void ClearAll()
    {
        mData = null;
        mDataListener = null;
    }
}
