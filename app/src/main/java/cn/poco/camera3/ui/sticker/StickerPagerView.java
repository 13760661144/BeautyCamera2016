package cn.poco.camera3.ui.sticker;

import android.content.Context;
import android.os.MessageQueue;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.ArrayList;

import cn.poco.camera3.cb.sticker.StickerInnerListener;
import cn.poco.camera3.cb.sticker.StickerPagerViewHelper;
import cn.poco.camera3.info.sticker.StickerInfo;
import cn.poco.camera3.mgr.StickerResMgr;
import cn.poco.camera3.ui.decoration.StickerItemDecoration;
import my.beautyCamera.R;

import static android.os.Looper.myQueue;

/**
 * 贴纸
 * Created by Gxx on 2017/10/12.
 */

public class StickerPagerView extends FrameLayout
{
    private RecyclerView mContentView;
    private StickerItemAdapter mItemAdapter;
    private int mLabelIndex = -1;

    private StickerPagerViewHelper mStickerHelper;
    private GridLayoutManager mLayoutManager;

    public StickerPagerView(@NonNull Context context, int index)
    {
        this(context);
        mLabelIndex = index;
        initCB(index);
        initView(context);
    }

    private StickerPagerView(@NonNull Context context)
    {
        super(context);
    }

    private void initCB(int index)
    {
        mStickerHelper = new StickerPagerViewHelper(index)
        {
            @Override
            public void OnProgress(int stickerID)
            {
                notifyDataChange(stickerID);
            }

            @Override
            public void OnComplete(int stickerID)
            {
                notifyDataChange(stickerID);
            }

            @Override
            public void OnFail(int stickerID)
            {
                notifyDataChange(stickerID);
            }

            @Override
            public void onDataChange(int stickerID)
            {
                notifyDataChange(stickerID);
            }
        };

        registerStickerResMgrCB();
    }

    public void registerStickerResMgrCB()
    {
        StickerResMgr.getInstance().registerStickerResMgrCB(mStickerHelper);
    }

    private void notifyDataChange(int stickerID)
    {
        int index = StickerResMgr.getInstance().getStickerInfoIndexInPagerView(stickerID, mLabelIndex);
        if (mItemAdapter != null)
        {
            mItemAdapter.notifyItemChanged(index);
        }
    }

    private void initView(Context context)
    {
        mContentView = new RecyclerView(context);
        mContentView.setOverScrollMode(OVER_SCROLL_NEVER);
        mLayoutManager = new GridLayoutManager(context, 5);
        mContentView.setLayoutManager(mLayoutManager);
        mContentView.addItemDecoration(new StickerItemDecoration());
        ((SimpleItemAnimator) mContentView.getItemAnimator()).setSupportsChangeAnimations(false);
        mContentView.getItemAnimator().setChangeDuration(0);
        FrameLayout.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        addView(mContentView, params);

        initData();
    }

    private void initData()
    {
        mItemAdapter = new StickerItemAdapter();
        ArrayList<StickerInfo> data = StickerResMgr.getInstance().getStickerInfoArr(mLabelIndex);
        mItemAdapter.setData(data);
        mContentView.setAdapter(mItemAdapter);
    }

    public void updateAdapterData()
    {
        if (mItemAdapter != null)
        {
            ArrayList<StickerInfo> data = StickerResMgr.getInstance().getStickerInfoArr(mLabelIndex);
            mItemAdapter.setData(data);
        }
    }

    public void setStickerDataHelper(StickerInnerListener helper)
    {
        if (mItemAdapter != null)
        {
            mItemAdapter.setStickerDataHelper(helper);
        }
    }

    public int getLabelIndex()
    {
        return mLabelIndex;
    }

    public void smoothScrollToPosition(final int position)
    {
        if (mLayoutManager != null && mContentView != null)
        {
            View view = mLayoutManager.findViewByPosition(position);
            float center = mContentView.getHeight() / 2f;
            if (view != null)
            {
                float viewCenter = view.getY() + view.getHeight() / 2f;
                mContentView.smoothScrollBy(0 , (int) (viewCenter - center));
            }
            else
            {
                mContentView.smoothScrollToPosition(position);
                myQueue().addIdleHandler(new MessageQueue.IdleHandler()
                {
                    @Override
                    public boolean queueIdle()
                    {
                        myQueue().removeIdleHandler(this);
                        smoothScrollToPosition(position);
                        return false;
                    }
                });
            }
        }
    }

    public void ClearAll()
    {
        StickerResMgr.getInstance().unregisterStickerResMgrCB(mStickerHelper);
        mStickerHelper = null;

        if (mContentView != null)
        {
            mContentView.setLayoutFrozen(true);
            for (int i = 0; i < mContentView.getChildCount(); i++)
            {
                View v = mContentView.getChildAt(i);
                if (v != null && v instanceof StickerItemView)
                {
                    StickerImageView view = v.findViewById(R.id.sticker_image_view);
                    if (view != null)
                    {
                        view.ClearAll();
                    }
                }
            }
            mContentView.removeAllViews();
        }

        if (mItemAdapter != null)
        {
            mItemAdapter.ClearAll();
        }

        removeAllViews();
    }
}
