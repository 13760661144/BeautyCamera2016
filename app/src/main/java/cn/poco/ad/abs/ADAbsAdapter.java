package cn.poco.ad.abs;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import cn.poco.recycleview.AbsAdapter;
import cn.poco.recycleview.AbsConfig;
import cn.poco.recycleview.BaseAdapter;
import cn.poco.recycleview.BaseItem;


public abstract class ADAbsAdapter extends BaseAdapter {
    public final int NULL_TYPE = 10000;
    public ADAbsAdapter(AbsConfig m_config) {
        super(m_config);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = super.onCreateViewHolder(parent, viewType);
        if(viewHolder == null)
        {
            if(viewType == NULL_TYPE)
            {
                BaseItem baseItem = initNullItem();
                RecyclerView.LayoutParams rl = new RecyclerView.LayoutParams(m_config.def_item_w,m_config.def_item_h);
                baseItem.setLayoutParams(rl);
                viewHolder = new NullViewHolder2(baseItem);
            }
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        if(getItemViewType(position) == NULL_TYPE)
        {
            BaseItem item = (BaseItem)holder.itemView;
            item.SetData(m_infoList.get(position), position);
            item.setTag(position);
            //设置选择状态
            if(m_currentSel == position)
            {
                item.onSelected();
            }
            else
            {
                item.onUnSelected();
            }
            item.setOnTouchListener(mOnDragClickListener);
        }
    }

    public abstract BaseItem initNullItem();

    @Override
    public int getItemViewType(int position) {
        if(m_infoList.get(position) instanceof ADNullItem)
        {
            return NULL_TYPE;
        }
        return super.getItemViewType(position);
    }

    public static class ADItemInfo extends AbsAdapter.ItemInfo
    {
        public int m_res;
        public Object m_ex;
    }

    public static class ADNullItem extends ADItemInfo
    {
        public int m_selectRes;
    }


    public class NullViewHolder2 extends RecyclerView.ViewHolder
    {
        public NullViewHolder2(View itemView) {
            super(itemView);
        }
    }
}
