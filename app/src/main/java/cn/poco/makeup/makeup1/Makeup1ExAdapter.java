package cn.poco.makeup.makeup1;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import cn.poco.makeup.makeup_abs.AbsAlphaFrExAdapter;
import cn.poco.recycleview.AbsExConfig;
import cn.poco.recycleview.BaseGroup;
import cn.poco.recycleview.BaseItem;
import cn.poco.recycleview.BaseItemContainer;
import cn.poco.widget.recycle.RecommendAdapter;
import cn.poco.widget.recycle.RecommendExAdapter;

public class Makeup1ExAdapter extends AbsAlphaFrExAdapter {

    public static final int DOWNLOAD_ITEM_TYPE = 0x100011;
    public static final int NULL_ITEM_TYPE = 0x100012;
    public static final int RECOM_ITEM_TYPE = 0x100013;
    public Context m_context;
    public int m_nullIndex = -1;

    public Makeup1ExAdapter(Context context, AbsExConfig itemConfig) {
        super(itemConfig);
        m_context = context;
    }


    @Override
    protected BaseItemContainer initItem(Context context, AbsExConfig itemConfig) {
        return new Makeup1ListItem(context, itemConfig);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = super.onCreateViewHolder(parent, viewType);
        if (viewHolder == null) {
            if (viewType == DOWNLOAD_ITEM_TYPE) {
                Makeup1DownItem item = new Makeup1DownItem(m_context, m_config);
                RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(m_config.def_item_w, m_config.def_item_h);
                item.setLayoutParams(params);
                viewHolder = new RecommendExAdapter.DownViewHolder(item);
            } else if (viewType == NULL_ITEM_TYPE) {
                Makeup1NullItem item = new Makeup1NullItem(m_context, m_config);
                RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(m_config.def_item_w, m_config.def_item_h);
                item.setLayoutParams(params);
                viewHolder = new NullViewHolder(item);
            } else if (viewType == RECOM_ITEM_TYPE) {
                Makeup1RecomItem item = new Makeup1RecomItem(m_context, m_config);
                RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(m_config.def_item_w, m_config.def_item_h);
                item.setLayoutParams(params);
                viewHolder = new RecommendAdapter.RemViewHolder(item);
            }
        }
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        int viewtype = getItemViewType(position);
        if (viewtype == DOWNLOAD_ITEM_TYPE || viewtype == NULL_ITEM_TYPE || viewtype == RECOM_ITEM_TYPE) {
            BaseItem item = (BaseItem) holder.itemView;
            item.SetData(m_infoList.get(position), position);
            item.setTag(position);
            item.setOnTouchListener(mOnClickListener);
            if (viewtype == NULL_ITEM_TYPE) {
                m_nullIndex = position;
                if (m_currentSel == position) {
                    item.onSelected();
                } else {
                    item.onUnSelected();
                }
            }
        }
    }


    @Override
    public int getItemViewType(int position) {
        if (m_infoList.get(position) instanceof DownloadItemInfo2) {
            return DOWNLOAD_ITEM_TYPE;
        } else if (m_infoList.get(position) instanceof NullItemInfo) {
            return NULL_ITEM_TYPE;
        } else if (m_infoList.get(position) instanceof RecommendItemInfo) {
            return RECOM_ITEM_TYPE;
        }
        return super.getItemViewType(position);
    }

    @Override
    public void onClick(View v) {
        if (v instanceof Makeup1NullItem) {
            if (m_currentSel != m_nullIndex) {
//                notifyItemChanged(m_currentSel);
                Makeup1NullItem item = (Makeup1NullItem) v;
                item.onClick();
                item.onSelected();
                final int temp = m_currentSel;
                m_currentSel = (int) v.getTag();
                if (m_hasOpen > -1)
                {
                    closeItem(m_hasOpen);
                }
                m_hasOpen = -1;
                m_parent.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        notifyItemChanged(temp);
                    }
                }, 300);

                if (m_onItemClickListener != null)
                {
                    ((OnItemClickListener) m_onItemClickListener).onItemClickNullItem();
                }
            }
        }
        else if (v instanceof Makeup1DownItem)
        {
            if (m_onItemClickListener != null) {
                ((OnItemClickListener) m_onItemClickListener).onItemClickDownloadItem((AbsAlphaFrExAdapter.ItemInfo) m_infoList.get((Integer) v.getTag()), m_currentSel);
            }
        }
        else if (v instanceof Makeup1RecomItem)
        {
            if (m_onItemClickListener != null)
            {
                ((OnItemClickListener) m_onItemClickListener).onItemClickRecomItem((AbsAlphaFrExAdapter.ItemInfo) m_infoList.get((Integer) v.getTag()),m_currentSel);
            }
        }
        else
        {
            super.onClick(v);
        }
    }

    @Override
    protected void onGroupClick(BaseGroup group, int position) {
        super.onGroupClick(group, position);
//        if(group.getParent() instanceof BaseItemContainer)
//        {
//            BaseItemContainer parent = (BaseItemContainer) group.getParent();
//            m_onItemClickListener.OnItemClick(m_infoList.get((Integer) parent.getTag()),(Integer) parent.getTag());
//        }
    }

    @Override
    protected void onSubClick(BaseItem subItem, int position) {
        super.onSubClick(subItem, position);

        if (m_currentSubSel != -1) {
            notifyItemChanged(1);
        }
    }

    @Override
    public void CancelSelect() {
        m_hasOpen = -1;
        super.CancelSelect();
    }

    public void SetSelectNullItem()
    {
        if(m_currentSel != 1)
        {
            m_hasOpen = -1;
            if(m_currentSel != 1)
            {
                int temp = m_currentSel;
                m_currentSel = 1;
                notifyItemChanged(1);
                if(temp > -1)
                {
                    notifyItemChanged(temp);
                }
            }
        }
        else
        {
            if(m_hasOpen > -1)
            {
                notifyItemChanged(m_hasOpen);
                m_hasOpen = -1;
            }
        }
        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) m_parent.getLayoutManager();
        linearLayoutManager.scrollToPositionWithOffset(0,0);
    }

    public interface OnItemClickListener extends AbsAlphaFrExAdapter.ItemAllCallBack
    {
        public void onItemClickNullItem();

        public void onItemClickDownloadItem(ItemInfo itemInfo,int index);

        public void onItemClickRecomItem(ItemInfo itemInfo,int index);
    }



}
