package cn.poco.widget.recycle;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.widget.FrameLayout;

import cn.poco.recycleview.AbsDragAdapter;
import cn.poco.recycleview.ListItemDecoration;

/**
 * 高度自适应
 *
 * @author lmx
 *         Created by lmx on 2017/6/21.
 */

public class RecommendBaseView extends FrameLayout
{


    //base
    protected int topPadding;
    protected int bottomPadding;

    //adapter
    protected AbsDragAdapter mAdapter;
    protected RecyclerView mRecyclerView;
    protected LinearLayoutManager mLayoutManager;

    public RecommendBaseView(@NonNull Context context, @NonNull AbsDragAdapter adapter)
    {
        super(context);
        mAdapter = adapter;
        init(context);
    }

    protected void init(Context context)
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
}
