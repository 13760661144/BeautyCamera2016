package cn.poco.ad.abs;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.widget.FrameLayout;

import cn.poco.recycleview.AbsConfig;

public abstract class ADAbsBottomFrWithRY extends BottomFr
{
    protected RecyclerView m_recyclerView;
    protected ADAbsAdapter m_adAdapter;
    protected AbsConfig m_config;
    public ADAbsBottomFrWithRY(@NonNull Context context) {
        super(context);
        initData();
        addUI();
    }

    private void initData()
    {
        m_config = getConfig();
        m_adAdapter = getAdapter();
    }

    public abstract AbsConfig getConfig();

    public abstract ADAbsAdapter getAdapter();

    public abstract RecyclerView.ItemDecoration getItemDecoration();

    protected void addUI()
    {
        m_recyclerView = new RecyclerView(getContext());
        FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,m_config.def_item_h);
        fl.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
        m_recyclerView.setLayoutParams(fl);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        m_recyclerView.setLayoutManager(linearLayoutManager);
        m_adAdapter.setRecyclerView(m_recyclerView);
        m_recyclerView.setAdapter(m_adAdapter);
        m_recyclerView.addItemDecoration(getItemDecoration());
        m_bottomList.addView(m_recyclerView);
    }

    public interface ClickCallBack extends BottomFr.ClickCallBack
    {
        public void onItemClick(Object object,int index);
    }
}
