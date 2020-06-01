package cn.poco.camera3.ui.sticker.local;

import android.content.Context;
import android.os.MessageQueue;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.ArrayList;

import cn.poco.camera3.cb.sticker.StickerLocalInnerListener;
import cn.poco.camera3.info.sticker.LabelLocalInfo;
import cn.poco.camera3.mgr.StickerLocalMgr;
import cn.poco.camera3.util.CameraPercentUtil;

import static android.os.Looper.myQueue;

/**
 * 素材管理页标题 + 标签 区域
 * Created by Gxx on 2017/10/30.
 */

public class LabelLocalView extends FrameLayout
{
    private RecyclerView mContentView;
    private LabelLocalAdapter mLabelAdapter;
    private LinearLayoutManager mLayoutManager;

    public LabelLocalView(@NonNull Context context)
    {
        super(context);
        initView(context);
    }

    public void ClearAll()
    {
        if (mLabelAdapter != null)
        {
            mLabelAdapter.ClearAll();
            mLabelAdapter = null;
        }
    }

    private void initView(Context context)
    {
        mContentView = new RecyclerView(context);
        mContentView.setOverScrollMode(OVER_SCROLL_NEVER);
        ((SimpleItemAnimator) mContentView.getItemAnimator()).setSupportsChangeAnimations(false);
        mContentView.getItemAnimator().setChangeDuration(0);
        mLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        mContentView.setLayoutManager(mLayoutManager);
        FrameLayout.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, CameraPercentUtil.HeightPxToPercent(72));
        addView(mContentView, params);

        initData();
    }

    private void initData()
    {
        mLabelAdapter = new LabelLocalAdapter();
        ArrayList<LabelLocalInfo> data = StickerLocalMgr.getInstance().getLabelInfoArr();
        mLabelAdapter.setData(data);
        mContentView.setAdapter(mLabelAdapter);
    }

    public void setStickerLocalDataHelper(StickerLocalInnerListener helper)
    {
        if (mLabelAdapter != null)
        {
            mLabelAdapter.setStickerLocalDataHelper(helper);
        }
    }

    public void notifyLabelDataChange(int index)
    {
        if (mLabelAdapter != null)
        {
            if (index != -1)
            {
                mLabelAdapter.notifyItemChanged(index);
            }
            else
            {
                mLabelAdapter.notifyDataSetChanged();
            }
        }
    }

    public void scrollToCenter(final int position)
    {
        if (mLayoutManager != null && mContentView != null)
        {
            View view = mLayoutManager.findViewByPosition(position);
            float center = mContentView.getWidth() / 2f;
            if (view != null)
            {
                float viewCenter = view.getX() + view.getWidth() / 2f;
                mContentView.smoothScrollBy((int) (viewCenter - center), 0);
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
                        scrollToCenter(position);
                        return false;
                    }
                });
            }
        }
    }
}
