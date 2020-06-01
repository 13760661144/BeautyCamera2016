package cn.poco.live.sticker;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.ArrayList;

import cn.poco.camera3.info.sticker.StickerInfo;
import cn.poco.camera3.ui.decoration.StickerItemDecoration;

/**
 * Created by admin on 2018/1/15.
 */

public class StickerPagerView extends FrameLayout implements StickerMgr.StickerStatusListener
{
    private RecyclerView mSticker;
    private StickerItemAdapter mAdapter;
    private int mIndex;

    public StickerPagerView(@NonNull Context context, int index)
    {
        super(context);
        mIndex = index;
        StickerMgr.getInstance().registerStickerStatusListener(this);
        init(context);
    }

    private void init(Context context)
    {
        mSticker = new RecyclerView(context);
        mSticker.setOverScrollMode(OVER_SCROLL_NEVER);
        mSticker.setLayoutManager(new GridLayoutManager(context, 5));
        mSticker.addItemDecoration(new StickerItemDecoration());
        ((SimpleItemAnimator) mSticker.getItemAnimator()).setSupportsChangeAnimations(false);
        mSticker.getItemAnimator().setChangeDuration(0);
        FrameLayout.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        addView(mSticker, params);

        mAdapter = new StickerItemAdapter();
        ArrayList<StickerInfo> data = StickerMgr.getInstance().getStickerInfoArr(mIndex);
        mAdapter.setData(data);
        mSticker.setAdapter(mAdapter);
    }

    public void setStickerDataHelper(StickerMgr.DataListener listener)
    {
        mAdapter.setStickerDataListener(listener);
    }

    public void updateData()
    {
        ArrayList<StickerInfo> data = StickerMgr.getInstance().getStickerInfoArr(mIndex);
        mAdapter.setData(data);
    }

    public void ClearAll()
    {
        mAdapter.setStickerDataListener(null);
        StickerMgr.getInstance().unregisterStickerStatusListener(this);
    }

    @Override
    public int getIndex()
    {
        return mIndex;
    }

    @Override
    public void OnStatusChange(int id)
    {
        notifyDataChange(id);
    }

    @Override
    public void OnProgress(int id)
    {
        notifyDataChange(id);
    }

    @Override
    public void OnComplete(int id)
    {
        notifyDataChange(id);
    }

    @Override
    public void OnFail(int id)
    {
        notifyDataChange(id);
    }

    private void notifyDataChange(int stickerID)
    {
        int index = StickerMgr.getInstance().getStickerIndexInView(stickerID, mIndex);
        if (mAdapter != null)
        {
            mAdapter.notifyItemChanged(index);
        }
    }
}
