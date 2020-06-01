package cn.poco.camera3.ui.sticker.local;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.ArrayList;

import cn.poco.camera3.cb.sticker.StickerLocalInnerListener;
import cn.poco.camera3.cb.sticker.StickerLocalPagerViewHelper;
import cn.poco.camera3.info.sticker.StickerInfo;
import cn.poco.camera3.mgr.StickerLocalMgr;
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
    private StickerLocalPagerViewHelper mPagerViewHelper;
    private StickerLocalInnerListener mHelper;

    public StickerLocalPagerView(@NonNull Context context, int index)
    {
        this(context);
        mLabelIndex = index;
        mNonDrawable = StickerLocalMgr.getInstance().getStickerMgrNonDrawable();
        initCB(index);
        initView(context);
    }

    private StickerLocalPagerView(@NonNull Context context)
    {
        super(context);
    }

    private void initCB(int index)
    {
        mPagerViewHelper = new StickerLocalPagerViewHelper(index)
        {
            @Override
            public void OnAllDataChange()
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

            @Override
            public void OnDataChange(int sticker_id)
            {
                if (mItemAdapter != null)
                {
                    int index = StickerLocalMgr.getInstance().getStickerInfoIndexInPagerView(sticker_id, mLabelIndex);
                    mItemAdapter.notifyItemChanged(index);
                }
            }
        };

        StickerLocalMgr.getInstance().registerPagerViewHelper(mPagerViewHelper);
    }

    public void setStickerHelper(StickerLocalInnerListener helper)
    {
        mHelper = helper;
        if (mItemAdapter != null)
        {
            mItemAdapter.setStickerHelper(helper);
        }
    }

    public void ClearAll()
    {
        StickerLocalMgr.getInstance().unregisterPagerViewHelper(mPagerViewHelper);

        mPagerViewHelper = null;
        mHelper = null;
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
        FrameLayout.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
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

        if (StickerLocalMgr.getInstance().getSelectedLabelIndex() == mLabelIndex && mHelper != null)
        {
            mHelper.onChangeSelectedIconStatus(selected_icon_status);
        }
    }
}
