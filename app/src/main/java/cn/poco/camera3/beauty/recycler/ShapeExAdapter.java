package cn.poco.camera3.beauty.recycler;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import cn.poco.camera3.beauty.data.BeautyShapeDataUtils;
import cn.poco.camera3.beauty.data.ShapeDataType;
import cn.poco.camera3.beauty.data.ShapeInfo;
import cn.poco.camera3.beauty.data.SuperShapeData;
import cn.poco.dynamicSticker.newSticker.CropCircleTransformation;
import cn.poco.recycleview.AbsAdapter;
import cn.poco.recycleview.BaseAdapter;
import cn.poco.recycleview.BaseItem;

/**
 * @author lmx
 *         Created by lmx on 2017-12-08.
 */

public class ShapeExAdapter extends BaseAdapter
{
    private static final String TAG = "ShapeExAdapter";
    public final static int VIEW_TYPE_NON = 0x01000000;
    public final static int VIEW_TYPE_SHAPE = 0x01000001;

    protected boolean isShowingSubFr = false;
    protected CropCircleTransformation mTransformation;

    //当前展开选中的子item 选项下标索引
    public int mCurrentSubSel = -1;

    protected OnExItemClickListener mExItemClickListener;
    protected OnAnimationScrolling mScrollingListener;

    public interface OnAnimationScrolling
    {
        void onAnimationScrolling(boolean scrolling);

        void onSubRecyclerViewState(boolean open, boolean showSeekBar);
    }

    public interface OnExItemClickListener extends OnItemClickListener
    {
        void onSubItemClick(int parentPosition, int subPosition, @ShapeDataType int type, ShapeExItemInfo itemInfo);
        void onResetShapeData (int parentPosition, int subPosition, ShapeExItemInfo itemInfo);
    }

    public ShapeExAdapter(ShapeExAdapterConfig m_config)
    {
        super(m_config);
    }

    public void setScrollingListener(OnAnimationScrolling mScrollingListener)
    {
        this.mScrollingListener = mScrollingListener;
    }

    public void setExOnItemClickListener(OnExItemClickListener onItemClickListener)
    {
        mExItemClickListener = onItemClickListener;
        setOnItemClickListener(onItemClickListener);
    }

    public void setTransformation(CropCircleTransformation mTransformation)
    {
        this.mTransformation = mTransformation;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        RecyclerView.ViewHolder holder = null;
        if (viewType == VIEW_TYPE_NON)
        {
            ShapeNonView item = new ShapeNonView(parent.getContext(), (ShapeExAdapterConfig) m_config);
            RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(m_config.def_item_w, m_config.def_item_h);
            item.setLayoutParams(params);
            holder = new ShapeNonViewHolder(item);
        }
        else if (viewType == VIEW_TYPE_SHAPE)
        {
            BaseItem item = initItem(parent.getContext());
            RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(m_config.def_item_w, m_config.def_item_h);
            item.setLayoutParams(params);
            holder = new ViewHolder(item);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {
        int type = getItemViewType(position);
        if (type == VIEW_TYPE_NON)
        {
            ShapeNonViewHolder viewHolder = (ShapeNonViewHolder) holder;
            ShapeNonView nonView = (ShapeNonView) viewHolder.itemView;
            nonView.SetData(m_infoList.get(position), position);
            nonView.setTag(position);
            nonView.setOnTouchListener(mOnClickListener);
            if (m_currentSel == position)
            {
                nonView.onSelected();
            } else
            {
                nonView.onUnSelected();
            }
        }
        else if (type == VIEW_TYPE_SHAPE)
        {
            ShapeItemView item = (ShapeItemView) holder.itemView;
            item.setTransformation(mTransformation);
            item.SetData(m_infoList.get(position), position);
            item.setTag(position);
            //设置选择状态
            if (m_currentSel == position)
            {
                item.onSelected();
            }
            else
            {
                item.onUnSelected();
            }
            item.setSubItemClickCB(mOnSubItemClickCB);
            item.setOnTouchListener(mOnClickListener);
        }
    }

    @Override
    public int getItemViewType(int position)
    {
        AbsAdapter.ItemInfo info = m_infoList.get(position);
        if (info instanceof ShapeExItemInfo)
        {
            if (info.m_uri == SuperShapeData.ID_NON_SHAPE)
            {
                return VIEW_TYPE_NON;
            }
            else
            {
                return VIEW_TYPE_SHAPE;
            }
        }
        else
        {
            return super.getItemViewType(position);
        }
    }

    public void performClick(View view)
    {
        mOnClickListener.onAnimationClick(view);
    }

    private ShapeItemView.OnSubItemClickCB mOnSubItemClickCB = new ShapeItemView.OnSubItemClickCB()
    {
        @Override
        public void onSubItemClick(int parentPosition, int subPosition, ShapeSubInfo subInfo)
        {
            mCurrentSubSel = subPosition;

            ShapeExItemInfo itemInfo = (ShapeExItemInfo) m_infoList.get(parentPosition);

            if (mExItemClickListener != null)
            {
                mExItemClickListener.onSubItemClick(parentPosition, subPosition, subInfo.m_type, itemInfo);
            }
        }
    };

    @Override
    protected void onClick(View v)
    {
        int position = (Integer) v.getTag();
        int type = getItemViewType(position);
        if (type == VIEW_TYPE_NON)
        {
            mCurrentSubSel = -1;
        }

        if (v.getParent() instanceof ShapeItemView)
        {
            ShapeItemView itemView = (ShapeItemView) v.getParent();
            //autoOpenSubFr(itemView, position);
            openSubFr(itemView, position);
        }
        else
        {
            super.onClick(v);
        }
    }

    private void openSubFr(ShapeItemView itemView, int position)
    {
        if (itemView.isSelect())
        {
            openSubRCFr(itemView, position);
        }
        else
        {
            mCurrentSubSel = -1;
            super.onClick(itemView);
        }

    }

    private void autoOpenSubFr(ShapeItemView itemView, int position)
    {
        if (!itemView.isSelect())
        {
            if (position != m_currentSel)
            {
                mCurrentSubSel = -1;

                //选中并打开
                int originPosition = m_currentSel;
                m_currentSel = position;

                if (originPosition != -1)
                {
                    notifyItemChanged(originPosition);
                }

                //更新视图数据
                itemView.setTransformation(mTransformation);
                itemView.SetData(m_infoList.get(position), position);
                itemView.setTag(position);
                itemView.onSelected();
                itemView.setSubItemClickCB(mOnSubItemClickCB);
                itemView.setOnTouchListener(mOnClickListener);

                if (m_onItemClickListener != null)
                {
                    m_onItemClickListener.OnItemClick(m_infoList.get(position), position);
                }
                openSubRCFr(itemView, m_currentSel);
            }
        }
        else
        {
            openSubRCFr(itemView, position);
        }
    }

    private void openSubRCFr(ShapeItemView itemView, int position)
    {
        if (itemView.isAnimation())
        {
            return;
        }

        //展开二级列表
        if (!isShowingSubFr)
        {
            itemView.setSubControlCB(new ShapeItemView.OnSubControlCB()
            {
                @Override
                public void onStartSubFrAnimation(ShapeItemView iv)
                {
                    if (m_parent.getLayoutManager() instanceof ShapeExAdapterConfig.MLinearLayoutManager)
                    {
                        ((ShapeExAdapterConfig.MLinearLayoutManager) m_parent.getLayoutManager()).setCanScroll(true);
                    }

                    if (mScrollingListener != null)
                    {
                        mScrollingListener.onAnimationScrolling(true);
                    }
                }

                @Override
                public void onFinishSubFrAnimation(ShapeItemView iv)
                {
                    iv.setChangeSubFrCB(null);

                    if (m_parent.getLayoutManager() instanceof ShapeExAdapterConfig.MLinearLayoutManager)
                    {
                        ((ShapeExAdapterConfig.MLinearLayoutManager) m_parent.getLayoutManager()).setCanScroll(!isShowingSubFr);
                    }

                    if (mScrollingListener != null)
                    {
                        mScrollingListener.onAnimationScrolling(false);
                    }

                    if (mCurrentSubSel != -1 && iv.mSubAdapter != null)
                    {
                        iv.mSubAdapter.setSelectPosition(mCurrentSubSel);
                    }
                }
            });
            itemView.openSubFr(position, ((ShapeExItemInfo) m_infoList.get(position)).m_subs, new ShapeItemView.OnChangeSubFrCB()
            {
                @Override
                public void change(ShapeItemView iv, float value)
                {
                    scrollByLeft2(iv, value);
                }
            });
            isShowingSubFr = true;
            itemView.openSubMode();
            if (mScrollingListener != null)
            {
                mScrollingListener.onSubRecyclerViewState(true, mCurrentSubSel != -1);
            }
        }
        else
        {
            isShowingSubFr = false;
            itemView.setSubControlCB(new ShapeItemView.OnSubControlCB()
            {
                @Override
                public void onStartSubFrAnimation(ShapeItemView iv)
                {
                    if (m_parent.getLayoutManager() instanceof ShapeExAdapterConfig.MLinearLayoutManager)
                    {
                        ((ShapeExAdapterConfig.MLinearLayoutManager) m_parent.getLayoutManager()).setCanScroll(true);
                    }

                    if (mScrollingListener != null)
                    {
                        mScrollingListener.onAnimationScrolling(true);
                    }
                }

                @Override
                public void onFinishSubFrAnimation(ShapeItemView iv)
                {
                    iv.setChangeSubFrCB(null);

                    if (m_parent.getLayoutManager() instanceof ShapeExAdapterConfig.MLinearLayoutManager)
                    {
                        ((ShapeExAdapterConfig.MLinearLayoutManager) m_parent.getLayoutManager()).setCanScroll(!isShowingSubFr);
                    }

                    if (mScrollingListener != null)
                    {
                        mScrollingListener.onAnimationScrolling(false);
                    }
                }
            });
            itemView.closeSubFr(new ShapeItemView.OnChangeSubFrCB()
            {
                @Override
                public void change(ShapeItemView iv, float value)
                {
                    scrollByCenter(iv);
                }
            });
            itemView.closeSubMode();
            if (mScrollingListener != null)
            {
                mScrollingListener.onSubRecyclerViewState(false, false);
            }
        }

    }

    void scrollByLeft2(ShapeItemView view, float percent)
    {
        if (view != null && m_parent != null)
        {
            float center = m_parent.getLeft();
            int[] dst = new int[2];
            view.getLocationOnScreen(dst);
            float viewCenter = dst[0];
            ShapeExAdapterConfig config = (ShapeExAdapterConfig) m_config;
            float offset = viewCenter - center - config.def_open_sub_parent_offset_left;
            m_parent.smoothScrollBy((int) (offset * percent), 0);
        }
    }

    @Override
    protected BaseItem initItem(Context context)
    {
        return new ShapeItemView(context, (ShapeExAdapterConfig) m_config);
    }


    @Override
    public void ClearAll()
    {
        if (m_parent != null)
        {
            m_parent.clearOnScrollListeners();
            for (int i = 0, size = m_parent.getChildCount(); i < size; i++)
            {
                View childAt = m_parent.getChildAt(i);
                if (childAt != null)
                {
                    RecyclerView.ViewHolder viewHolder = m_parent.getChildViewHolder(childAt);
                    if (viewHolder != null && viewHolder.itemView != null && viewHolder.itemView instanceof ShapeItemView)
                    {
                        ((ShapeItemView) viewHolder.itemView).clearAll();
                    }
                }
            }
        }
        mTransformation = null;
        mScrollingListener = null;
        mExItemClickListener = null;
        super.ClearAll();

    }

    public boolean isShowingSubFr()
    {
        return isShowingSubFr;
    }


    public ShapeExItemInfo updateCurrentItemInfo(int progress)
    {
        if (m_currentSel != -1 && mCurrentSubSel != -1)
        {
            ShapeExItemInfo itemInfo = (ShapeExItemInfo) m_infoList.get(m_currentSel);
            if (itemInfo.m_uri != SuperShapeData.ID_NON_SHAPE)
            {
                itemInfo.updateData(itemInfo.m_subs.get(mCurrentSubSel).m_type, progress);
                return itemInfo;
            }
        }
        return null;
    }

    public ShapeExItemInfo getCurrentItemInfo()
    {
        if (m_currentSel != -1)
        {
            return (ShapeExItemInfo) m_infoList.get(m_currentSel);
        }
        return null;
    }

    /**
     * 重置当前选中脸型的定制参数
     * @return true 重置成功
     */
    public boolean resetCurrentItemInfo()
    {
        if (m_currentSel != -1)
        {
            ShapeExItemInfo itemInfo = (ShapeExItemInfo) m_infoList.get(m_currentSel);
            if (itemInfo.m_uri != SuperShapeData.ID_NON_SHAPE)
            {
                itemInfo.m_data.setDefData();

                if (mExItemClickListener != null)
                {
                    mExItemClickListener.onResetShapeData(m_currentSel, mCurrentSubSel, itemInfo);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 重置指定脸型的定制参数
     * @return true 指定的shape和当前选中一致
     */
    public boolean resetShapeIdItemInfo(int shapeId)
    {
        int index = GetIndex(shapeId);
        if (index > -1)
        {
            ShapeExAdapter.ShapeExItemInfo itemInfo = (ShapeExItemInfo) GetItemInfoByIndex(index);
            if (itemInfo != null) {
                itemInfo.m_data.setDefData();
                if (mExItemClickListener != null)
                {
                    mExItemClickListener.onResetShapeData(m_currentSel, mCurrentSubSel, itemInfo);
                }
            }
        }
        return index == m_currentSel;
    }

    public static class ShapeExItemInfo extends ItemInfo
    {
        public Object m_logo;
        public String m_name;
        public ShapeInfo m_data = new ShapeInfo();//临时
        public ArrayList<ShapeSubInfo> m_subs;

        public float getData(@ShapeDataType int type)
        {
            return BeautyShapeDataUtils.GetData(m_data, type);
        }

        public void updateData(@ShapeDataType int type, float data)
        {
            BeautyShapeDataUtils.UpdateData(m_data, type, data);
        }
    }

    public static class ShapeSubInfo
    {
        public String m_sub_name;
        public Object m_sub_logo;

        @ShapeDataType
        public int m_type;
    }

    public static class ShapeNonViewHolder extends RecyclerView.ViewHolder
    {
        public ShapeNonViewHolder(ShapeNonView view)
        {
            super(view);
        }
    }
}
