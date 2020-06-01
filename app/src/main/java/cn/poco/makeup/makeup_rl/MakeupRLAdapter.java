package cn.poco.makeup.makeup_rl;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import cn.poco.makeup.makeup_abs.AbsAlphaFrAdapter;
import cn.poco.makeup.makeup_abs.BaseAlphaFrItem;
import cn.poco.recycleview.AbsAdapter;
import cn.poco.recycleview.AbsConfig;

public class MakeupRLAdapter extends AbsAlphaFrAdapter {
    public final int NULL_TYPE = 0x123123;
    public MakeupRLAdapter(AbsConfig m_config) {
        super(m_config);
    }

    @Override
    protected BaseAlphaFrItem initItem(Context context) {
        return new MakeupRLContainer(context,m_config);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = super.onCreateViewHolder(parent,viewType);
        if(viewHolder == null)
        {
            if(viewType == NULL_TYPE)
            {
//                MakeupRLNullContainer container = new MakeupRLNullContainer(parent.getContext(),m_config);
//                RecyclerView.LayoutParams rl = new RecyclerView.LayoutParams(m_config.def_item_w,m_config.def_item_h);
//                container.setLayoutParams(rl);
//                viewHolder = new NullViewHolder(container);

                MakeupRLNullItem item = new MakeupRLNullItem(parent.getContext(),m_config);
                RecyclerView.LayoutParams rl = new RecyclerView.LayoutParams(m_config.def_item_w,m_config.def_item_h);
                item.setLayoutParams(rl);
                viewHolder = new NullViewHolder(item);
            }
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        int type = getItemViewType(position);
        if(type == NULL_TYPE)
        {
//            MakeupRLNullContainer container = (MakeupRLNullContainer) holder.itemView;
//            if(container != null)
//            {
//                container.setTag(position);
//                container.m_Item.setOnTouchListener(mOnClickListener);
//                container.m_Item.SetData(m_infoList.get(position),-1);
//                container.m_Item.setTag(position);
//                if(m_currentSel == position)
//                {
//                    container.m_Item.onSelected();
//                }
//                else
//                {
//                    container.m_Item.onUnSelected();
//                }
//            }
            MakeupRLNullItem item = (MakeupRLNullItem) holder.itemView;
            if(item != null)
            {
                item.setTag(position);
                item.setOnTouchListener(mOnClickListener);
                item.SetData(m_infoList.get(position),-1);
                if(m_currentSel == position)
                {
                    item.onSelected();
                }
                else
                {
                    item.onUnSelected();
                }
            }
        }
    }



    @Override
    public int getItemViewType(int position) {
        if(m_infoList.get(position) instanceof NullItemInfo)
        {
            return NULL_TYPE;
        }
        return super.getItemViewType(position);
    }

    public static final class NullViewHolder extends RecyclerView.ViewHolder
    {

        public NullViewHolder(View itemView) {
            super(itemView);
        }
    }

    public static class ItemInfo extends AbsAdapter.ItemInfo
    {
        public Object m_logo;
        public String m_name;
        public Object m_ex;
        public int m_maskColor;
        public ItemInfo(int uri,Object logo,String name)
        {
            m_uri = uri;
            m_logo = logo;
            m_name = name;
        }

        public void setEx(Object obj)
        {
            m_ex = obj;
        }

        public void setMaskColor(int color)
        {
            m_maskColor = color;
        }
    }

    public static class NullItemInfo extends ItemInfo
    {
        public NullItemInfo(Object logo,int color)
        {
            super(0,logo,"");
            m_maskColor = color;
        }
        public int m_maskColor;
    }


}
