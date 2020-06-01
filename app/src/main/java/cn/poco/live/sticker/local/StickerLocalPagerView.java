package cn.poco.live.sticker.local;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.ArrayList;

import cn.poco.camera3.info.sticker.StickerInfo;
import cn.poco.camera3.ui.decoration.StickerItemDecoration;
import cn.poco.camera3.ui.drawable.StickerMgrNonDrawable;

/**
 * @author Created by Gxx on 2017/10/30.
 */

public class StickerLocalPagerView extends FrameLayout
{
    private StickerMgrNonDrawable mNonDrawable;
    private RecyclerView mContentView;
    private StickerLocalItemAdapter mItemAdapter;
    private int mLabelIndex = -1;
    private StickerLocalMgr.StickerStatusListener mStickerStatusListener;
    private StickerLocalMgr.DataListener mDataListener;

    public StickerLocalPagerView(@NonNull Context context, int index)
    {
        this(context);
        mLabelIndex = index;
        mNonDrawable = StickerLocalMgr.getInstance().getStickerMgrNonDrawable();
        initCB();
        initView(context);
    }

    private StickerLocalPagerView(@NonNull Context context)
    {
        super(context);
    }

    private void initCB()
    {
        mStickerStatusListener = new StickerLocalMgr.StickerStatusListener()
        {

            @Override
            public int getIndex()
            {
                return mLabelIndex;
            }

            @Override
            public void OnStatusChange(int id)
            {
                if (mItemAdapter != null)
                {
                    int index = StickerLocalMgr.getInstance().getStickerInfoIndexInPagerView(id, mLabelIndex);
                    mItemAdapter.notifyItemChanged(index);
                }
            }

            @Override
            public void OnDeleted()
            {
                ArrayList<StickerInfo> data = StickerLocalMgr.getInstance().getStickerInfoArr(mLabelIndex);

                if (data != null && data.size() > 0)
                {
                    if (mItemAdapter != null)
                    {
                        mItemAdapter.notifyDataSetChanged();
                    }
                }
                else
                {
                    if (mItemAdapter != null)
                    {
                        mItemAdapter = null;
                    }
                    mContentView.setBackgroundDrawable(mNonDrawable);
                }
            }
        };

        StickerLocalMgr.getInstance().registerStatusListener(mStickerStatusListener);
    }

    public void setDataListener(StickerLocalMgr.DataListener listener)
    {
        mDataListener = listener;
        if (mItemAdapter != null)
        {
            mItemAdapter.setDataListener(listener);
        }
    }

    public void ClearAll()
    {
        StickerLocalMgr.getInstance().unregisterStatusListener(mStickerStatusListener);

        mStickerStatusListener = null;
        mDataListener = null;
        mNonDrawable = null;

        if (mItemAdapter != null)
        {
            mItemAdapter.ClearAll();
        }

        if (mContentView != null)
        {
            mContentView.setBackgroundDrawable(null);
            mContentView.removeAllViews();
        }

        removeAllViews();
    }

    private void initView(Context context)
    {
        mContentView = new RecyclerView(context);
        mContentView.setOverScrollMode(OVER_SCROLL_NEVER);
        mContentView.setLayoutManager(new GridLayoutManager(context, 5));
        mContentView.addItemDecoration(new StickerItemDecoration());
        ((SimpleItemAnimator) mContentView.getItemAnimator()).setSupportsChangeAnimations(false);
        mContentView.getItemAnimator().setChangeDuration(0);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        addView(mContentView, params);

        initData();
    }

    private void initData()
    {
        int selected_icon_status = StickerMgrPage.SelectedIconType.DO_NOT_SHOW;

        ArrayList<StickerInfo> data = StickerLocalMgr.getInstance().getStickerInfoArr(mLabelIndex);

        if (data != null && data.size() > 0)
        {
            if (StickerLocalMgr.getInstance().isHadLocalRes(data))
            {
                selected_icon_status = StickerMgrPage.SelectedIconType.CHECK_ALL;
            }
            mItemAdapter = new StickerLocalItemAdapter();
            mItemAdapter.setData(data);
            mContentView.setAdapter(mItemAdapter);
        }
        else
        {
            mContentView.setBackgroundDrawable(mNonDrawable);
        }

        if (StickerLocalMgr.getInstance().getSelectedLabelIndex() == mLabelIndex && mDataListener != null)
        {
            mDataListener.onChangeSelectedIconStatus(selected_icon_status);
        }
    }
}
