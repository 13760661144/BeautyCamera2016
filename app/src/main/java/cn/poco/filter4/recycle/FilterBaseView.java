package cn.poco.filter4.recycle;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import cn.poco.recycleview.AbsDragAdapter;
import cn.poco.recycleview.ListItemDecoration;

/**
 * 高度自适应
 *
 * @author lmx
 *         Created by lmx on 2017/6/14.
 */

public class FilterBaseView extends FrameLayout
{
    private static final String TAG = "bbb";

    //adapter
    protected AbsDragAdapter mAdapter;
    protected RecyclerView mRecyclerView;
    protected LinearLayoutManager mLayoutManager;

    //base
    protected int topPadding;
    protected int bottomPadding;
    protected boolean mUiEnable = true;

    public FilterBaseView(@NonNull Context context, @NonNull AbsDragAdapter adapter)
    {
        super(context);
        mAdapter = adapter;
        init(context);
    }

    private void init(Context context)
    {
        LayoutParams params;

        //内边距
        topPadding = mAdapter.m_config.def_parent_bottom_padding;
        bottomPadding = mAdapter.m_config.def_parent_bottom_padding;

        mLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView = new RecyclerView(context);
        mRecyclerView.setOverScrollMode(OVER_SCROLL_NEVER);
        mRecyclerView.setHorizontalScrollBarEnabled(false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new ListItemDecoration(mAdapter.m_config.def_item_l, ListItemDecoration.HORIZONTAL));
        mRecyclerView.setPadding(mAdapter.m_config.def_parent_left_padding, topPadding, mAdapter.m_config.def_parent_right_padding, bottomPadding);
        mRecyclerView.setClipToPadding(false);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setRecyclerView(mRecyclerView);
        params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        params.gravity = Gravity.CENTER_HORIZONTAL;
        addView(mRecyclerView, params);
    }

    public void setUiEnable(boolean uiEnable)
    {
        this.mUiEnable = uiEnable;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev)
    {
        if (!mUiEnable)
        {
            return false;
        }
        return super.dispatchTouchEvent(ev);
    }
}
