package cn.poco.makeup.makeup_abs;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import cn.poco.makeup.MySeekBar;
import cn.poco.recycleview.AbsAdapter;
import cn.poco.recycleview.AbsConfig;
import cn.poco.recycleview.AbsDragAdapter;
import cn.poco.recycleview.BaseAdapter;
import cn.poco.widget.recycle.RecommendDragContainer;


public abstract class AbsAlphaFrAdapter extends AbsDragAdapter {
    private final int VIEW_TYPE_NORMAL = 100;
    private boolean m_alphaFrIsShow = false;
    protected BaseAlphaFrItem.MySeekBarAllCallBack mProgressChangeListener;
    protected BaseAlphaFrItem.AnimChangeCallBack m_ChangceCB;
    public AbsAlphaFrAdapter(AbsConfig m_config) {
        super(m_config);
        init();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        BaseAdapter.ViewHolder viewHolder = null;
        if(viewType == VIEW_TYPE_NORMAL)
        {
            BaseAlphaFrItem item = initItem(parent.getContext());
            RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(m_config.def_item_w, m_config.def_item_h);
            item.setLayoutParams(params);
            viewHolder = new BaseAdapter.ViewHolder(item);
        }
        return viewHolder;
    }

    private void init()
    {
        mProgressChangeListener = new BaseAlphaFrItem.MySeekBarAllCallBack() {
            @Override
            public void AlphaFrLayoutStart(MySeekBar mySeekBar) {
                m_parent.setLayoutFrozen(false);
                if(m_parent.getParent() instanceof RecommendDragContainer)
                {
                    RecommendDragContainer recyclerViewContainer = (RecommendDragContainer) m_parent.getParent();
                    recyclerViewContainer.setUIEnable(false);
                }
                ItemAllCallBack itemAllCallBack = (ItemAllCallBack) m_onItemClickListener;
                if(itemAllCallBack != null)
                {
                    itemAllCallBack.onAlphaFrShowStart(mySeekBar);
                }
            }

            @Override
            public void AlphaFrLayoutFinish(MySeekBar mySeekBar) {
                ItemAllCallBack itemAllCallBack = (ItemAllCallBack) m_onItemClickListener;
                if(itemAllCallBack != null)
                {
                    itemAllCallBack.onSeekBarLayoutFinish(mySeekBar);
                }
                if(m_alphaFrIsShow)
                {
                    //弹出透明度ui时，设置recyclerView不可滑动
                    m_parent.setLayoutFrozen(true);
                }
                else
                {
                     //恢复recyclerView可滑动
                    m_parent.setLayoutFrozen(false);
                }

                if(m_parent.getParent() instanceof RecommendDragContainer)
                {
                    RecommendDragContainer recyclerViewContainer = (RecommendDragContainer) m_parent.getParent();
                    recyclerViewContainer.setUIEnable(true);
                }
            }

            @Override
            public void onProgressChanged(MySeekBar seekBar, int progress) {
                ItemAllCallBack itemAllCallBack = (ItemAllCallBack) m_onItemClickListener;
                if(itemAllCallBack != null)
                {
                    itemAllCallBack.onProgressChanged(seekBar,progress);
                }
            }

            @Override
            public void onStartTrackingTouch(MySeekBar seekBar) {
                ItemAllCallBack itemAllCallBack = (ItemAllCallBack) m_onItemClickListener;
                if(itemAllCallBack != null)
                {
                    itemAllCallBack.onStartTrackingTouch(seekBar);
                }
            }

            @Override
            public void onStopTrackingTouch(MySeekBar seekBar) {
                ItemAllCallBack itemAllCallBack = (ItemAllCallBack) m_onItemClickListener;
                if(itemAllCallBack != null)
                {
                    itemAllCallBack.onStopTrackingTouch(seekBar);
                }
            }
        };

        m_ChangceCB = new BaseAlphaFrItem.AnimChangeCallBack() {
            @Override
            public void change(View view, float value, boolean isOpening) {
                if(view != null)
                {
                    if(isOpening)
                    {
                        //弹出调整透明度ui过程中，弹出透明度调节ui的子Item距离屏幕左边的距离的调整。
                        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) view.getLayoutParams();
                        int curLeft = lp.leftMargin;
                        AbsAlphaConfig config = (AbsAlphaConfig) m_config;
                        int animValue = (int) ((config.m_alphaFr_Item_left - curLeft)*value);
                        lp.leftMargin = curLeft + animValue;
                        int curRight = lp.rightMargin;
                        if(curRight > 0)
                        {
                            int rightAnimValue = (int) (curRight*(1 - value));
                            lp.rightMargin = rightAnimValue;
                        }
                        view.setLayoutParams(lp);
                        scrollByLeft2(view,value);//弹出调整透明度ui滑动到左边
                    }
                    else
                    {
                        //收回调整透明度ui过程中，弹出透明度调节ui的子Item恢复原来的状态
                        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) view.getLayoutParams();
                        int curLeft = lp.leftMargin;
                        int animValue = (int) ((curLeft)*value);
                        lp.leftMargin = curLeft - animValue;
                        view.setLayoutParams(lp);
                        scrollByCenter(view);//收回调整透明度ui恢复到中心点
                    }
                }
            }
        };
    }

    protected void scrollByLeft2(View view, float percent)
    {
        if(view != null && m_parent != null)
        {
            float center = m_parent.getLeft();
            int[] dst = new int[2];
            view.getLocationOnScreen(dst);
            float viewCenter = dst[0];
            AbsAlphaConfig config = (AbsAlphaConfig) m_config;
            float offset = viewCenter - center - config.m_alphaFr_Item_left;
            m_parent.smoothScrollBy((int)(offset * percent), 0);
        }
    }

    public boolean alphaIsShow()
    {
        return m_alphaFrIsShow;
    }


    public void onCloseAlphaFr()
    {
        if(alphaIsShow())
        {
            BaseAlphaFrItem container = getVisibleItem(m_currentSel);
            if(container != null)
            {
                container.closeAlphaFr();
                m_alphaFrIsShow = false;
                if(m_currentSel > -1)
                {
                    if(m_currentSel > 0 && container.getChildCount() > 0)
                    {
                        if(container.getChildAt(0) != null)
                        {
                            IAlphaMode alphaMode = (IAlphaMode) container.getChildAt(0);
                            alphaMode.onCloseAlphaFr();
                        }
                    }
                }
            }
        }
    }

    protected BaseAlphaFrItem getVisibleItem(int position)
    {
        BaseAlphaFrItem itemContainer = null;
        LinearLayoutManager layoutManager = (LinearLayoutManager)m_parent.getLayoutManager();
        for(int i = 0; i < layoutManager.getChildCount(); i++)
        {
            if(layoutManager.getChildAt(i) instanceof BaseAlphaFrItem)
            {
                BaseAlphaFrItem item = (BaseAlphaFrItem)layoutManager.getChildAt(i);
                if(item.getTag() != null && (int)item.getTag() == position)
                {
                    itemContainer = item;
                    break;
                }
            }
        }
        return itemContainer;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        int type = getItemViewType(position);
        if(type == VIEW_TYPE_NORMAL)
        {
            BaseAlphaFrItem item = (BaseAlphaFrItem) holder.itemView;
            item.setItemInfo((AbsAdapter.ItemInfo) m_infoList.get(position));
            item.m_Item.setOnTouchListener(mOnClickListener);
            item.m_Item.setTag(position);
            if(m_currentSel == position)
            {
                item.m_Item.onSelected();
            }
            else
            {
                item.m_Item.onUnSelected();
            }
            item.setTag(position);
        }
    }



    @Override
    public int getItemViewType(int position) {
        return VIEW_TYPE_NORMAL;
    }

    protected abstract BaseAlphaFrItem initItem(Context context);

    @Override
    protected void onClick(View v) {
        if(v.getParent() instanceof BaseAlphaFrItem)
        {
            BaseAlphaFrItem item = (BaseAlphaFrItem) v.getParent();
            int position = (int) item.getTag();
            if(position == m_currentSel)
            {
                //选中状态下，假如没有显示调整透明度ui的，就弹出调整透明度ui。
                if(!m_alphaFrIsShow)
                {
                    item.setOnProgressChangeListener(mProgressChangeListener);
                    item.setChangeCB(m_ChangceCB);
                    m_alphaFrIsShow = true;
                    item.openAlphaFr();
                    item.m_Item.onOpenAlphaFr();
                }
                else
                {
                    m_alphaFrIsShow = false;
                    item.closeAlphaFr();
                    item.m_Item.onCloseAlphaFr();
                }
            }
            else
            {
                int temp = m_currentSel;
                m_currentSel = position;
                notifyItemChanged(temp);
                notifyItemChanged(m_currentSel);
                if(m_onItemClickListener != null)
                {
                    m_onItemClickListener.OnItemClick(m_infoList.get(position), position);
                }
                scrollByCenter(v);
            }
        }
        else
        {
            super.onClick(v);
        }
    }


    @Override
    protected void scrollByCenter(final View view) {
        if (view != null && m_parent != null) {
			int[] dst = new int[2];
			view.getLocationOnScreen(dst);
			//当前view移去中间的距离
			float viewCenter = dst[0] + view.getWidth() / 2f;
			float center = m_parent.getWidth() / 2f;
			float offset = viewCenter - center;
			m_parent.smoothScrollBy((int)offset, 0);
        }
    }

    public void SetSelectIndex(int index)
    {
        m_currentSel = index;
        notifyDataSetChanged();

        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) m_parent.getLayoutManager();
        int offset = (int) (m_config.def_parent_center_x - (m_config.def_item_w/2f) - m_config.def_item_l - m_config.def_parent_left_padding);
        if(index > -1)
        {
            linearLayoutManager.scrollToPositionWithOffset(index,offset);
        }
        else
        {
            linearLayoutManager.scrollToPositionWithOffset(0,offset);
        }
    }


    public void scrollToCenterByIndex2(int index)
    {
        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) m_parent.getLayoutManager();
        View view = linearLayoutManager.findViewByPosition(index);
        if(view != null)
        {
            int[] dst = new int[2];
            view.getLocationOnScreen(dst);
            int x = dst[0];
            int targetX = (int) ((m_parent.getWidth() - m_config.def_item_w)/2f);
            int dis = x - targetX;
            m_parent.smoothScrollBy(dis,0);
        }
    }

    public interface ItemAllCallBack extends OnItemClickListener
    {
        public void onAlphaFrShowStart(MySeekBar mySeekBar);

        public void onSeekBarLayoutFinish(MySeekBar mySeekBar);

        public void onProgressChanged(MySeekBar mySeekBar,int progress);

        public void onStartTrackingTouch(MySeekBar mySeekBar);

        public void onStopTrackingTouch(MySeekBar mySeekBar);
    }
}
