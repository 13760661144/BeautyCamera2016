package cn.poco.filter4.recycle;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;

import cn.poco.makeup.MySeekBar;
import cn.poco.recycleview.AbsAdapter;
import cn.poco.recycleview.AbsExConfig;
import cn.poco.recycleview.BaseExAdapter;
import cn.poco.recycleview.BaseItem;
import cn.poco.recycleview.BaseItemContainer;

/**
 * Created by lgd on 2017/5/26.
 */

public class FilterAdapter extends BaseExAdapter
{
    public final static int VIEW_TYPE_DOWNLOAD_MODE = 0x1;
    public final static int VIEW_TYPE_RECOMMEND = 0x10;
    public final static int VIEW_TYPE_HEAD = 0x100;
    public final static int VIEW_TYPE_ORIGINAL = 0x1000;

    protected boolean mIsShowAlphaFr = false;
    protected int mCurrentAlphaProgress = 100;

    public FilterAdapter(AbsExConfig itemConfig)
    {
        super(itemConfig);
    }

    public void setCurrentAlphaProgress(int mCurrentAlphaProgress)
    {
        this.mCurrentAlphaProgress = mCurrentAlphaProgress;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        RecyclerView.ViewHolder holder = super.onCreateViewHolder(parent, viewType);
        if (holder == null)
        {
            if (viewType == VIEW_TYPE_HEAD)
            {
                FilterHead page = new FilterHead(parent.getContext());
                RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(((FilterConfig) m_config).def_head_w, m_config.def_item_h);
                page.setLayoutParams(params);
                holder = new DownViewHolder(page);
            }
            else if (viewType == VIEW_TYPE_DOWNLOAD_MODE)
            {
                FilterDownMore page = new FilterDownMore(parent.getContext());
                RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(m_config.def_item_w, m_config.def_item_h);
                page.setLayoutParams(params);
                holder = new DownViewHolder(page);
            }
            else if (viewType == VIEW_TYPE_ORIGINAL)
            {
                FilterOriginal page = new FilterOriginal(parent.getContext());
                RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(m_config.def_item_w, m_config.def_item_h);
                page.setLayoutParams(params);
                holder = new DownViewHolder(page);
            }
            else if (viewType == VIEW_TYPE_RECOMMEND)
            {
                FilterRecommend page = new FilterRecommend(parent.getContext());
                RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(m_config.def_item_w, m_config.def_item_h);
                page.setLayoutParams(params);
                holder = new RemViewHolder(page);
            }
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {
        int type = getItemViewType(position);
        if (type == VIEW_TYPE_HEAD)
        {
//			BaseItem item = (BaseItem)holder.itemView;
//			item.SetData(m_infoList.get(position), position);
//			item.setTag(position);
//			item.setOnTouchListener(mOnClickListener);
            FilterHead item = (FilterHead) holder.itemView;
            item.SetData(m_infoList.get(position), position);
            item.setTag(position);
            item.mBlur.setOnTouchListener(mOnClickListener);
            item.mDark.setOnTouchListener(mOnClickListener);
            if (m_currentSel == position)
            {
                item.onSelected();
            }
            else
            {
                item.onUnSelected();
            }
        }
        else if (type == VIEW_TYPE_ORIGINAL || type == VIEW_TYPE_RECOMMEND || type == VIEW_TYPE_DOWNLOAD_MODE)
        {
            BaseItem item = (BaseItem) holder.itemView;
            item.SetData(m_infoList.get(position), position);
            item.setTag(position);
            item.setOnTouchListener(mOnClickListener);
            if (type == VIEW_TYPE_ORIGINAL
                    && m_config instanceof FilterConfig
                    && ((FilterConfig) m_config).def_original_bk_color != -1)
            {
                if (item instanceof FilterOriginal)
                {
                    ((FilterOriginal) item).setDefBKCoverColor(((FilterConfig) m_config).def_original_bk_color);
                }
            }
            if (m_currentSel == position)
            {
                item.onSelected();
            }
            else
            {
                item.onUnSelected();
            }
        }
        else
        {
            super.onBindViewHolder(holder, position);
        }
    }

    @Override
    public int getItemViewType(int position)
    {
        AbsAdapter.ItemInfo info = m_infoList.get(position);
        if (info instanceof HeadItemInfo)
        {
            return VIEW_TYPE_HEAD;
        }
        else if (info instanceof OriginalItemInfo)
        {
            return VIEW_TYPE_ORIGINAL;
        }
        else if (info instanceof DownloadItemInfo)
        {
            return VIEW_TYPE_DOWNLOAD_MODE;
        }
        else if (info instanceof RecommendItemInfo)
        {
            return VIEW_TYPE_RECOMMEND;
        }
        else
        {
            return super.getItemViewType(position);
        }
    }

    public boolean isShowAlphaFr()
    {
        return mIsShowAlphaFr;
    }

    @Override
    public void onClick(View v)
    {
        if (v.getParent() instanceof BaseItemContainer)
        {
            super.onClick(v);
        }
        else
        {
            BaseItem item;
            if (v.getParent() instanceof FilterHead)
            {
                FilterHead parent = (FilterHead) v.getParent();
                int position = (int) parent.getTag();
                HeadItemInfo itemInfo = (HeadItemInfo) m_infoList.get(position);
                if (v == parent.mBlur)
                {
                    itemInfo.isSelectBlur = !itemInfo.isSelectBlur;
                }
                else if (v == parent.mDark)
                {
                    itemInfo.isSelectDark = !itemInfo.isSelectDark;
                }
                item = parent;
            }
            else
            {
                item = (BaseItem) v;
            }
            int position = (int) item.getTag();
            if (item instanceof FilterOriginal)
            {
                item.onSelected();
                if (m_currentSel != position)
                {
                    notifyItemChanged(m_currentSel);
                    m_currentSel = position;
                }
                scrollByCenter(item);
            }
            item.onClick();
            if (m_onItemClickListener != null)
            {
                ((BaseExAdapter.OnItemClickListener) m_onItemClickListener).OnItemClick((BaseExAdapter.ItemInfo) m_infoList.get(position), position, -1);
            }
        }
    }

    @Override
    protected void onSubClick(final BaseItem subItem, int position)
    {
        if (((FilterConfig) m_config).isCamera)
        {
            super.onSubClick(subItem, position);
        }
        else
        {
            if (subItem instanceof FilterSubItem)
            {
                FilterSubItem tempItem = (FilterSubItem) subItem;
                if (tempItem.isSelect())
                {
                    if (!mIsShowAlphaFr)
                    {
                        FilterItem item = (FilterItem) subItem.getParent();
                        item.setProgressChangeCB(new FilterItem.OnProgressChangeCB()
                        {
                            @Override
                            public void onSeekBarStartShow(MySeekBar seekBar)
                            {
                                m_parent.setLayoutFrozen(false);
                                if (m_onItemClickListener != null)
                                {
                                    OnItemClickListener listener = (OnItemClickListener) m_onItemClickListener;
                                    listener.onSeekBarStartShow(seekBar);
                                }
                            }

                            @Override
                            public void onFinishLayoutAlphaFr(MySeekBar seekBar)
                            {
                                if (m_onItemClickListener != null)
                                {
                                    OnItemClickListener listener = (OnItemClickListener) m_onItemClickListener;
                                    listener.onFinishLayoutAlphaFr(seekBar);
                                }

                                if (isShowAlphaFr())
                                {
                                    m_parent.setLayoutFrozen(true);
                                }
                                else
                                {
                                    m_parent.setLayoutFrozen(false);
                                }
                            }

                            @Override
                            public void onProgressChanged(MySeekBar seekBar, int progress)
                            {
                                if (m_onItemClickListener != null)
                                {
                                    OnItemClickListener listener = (OnItemClickListener) m_onItemClickListener;
                                    listener.onProgressChanged(seekBar, progress);
                                }
                            }

                            @Override
                            public void onStartTrackingTouch(MySeekBar seekBar)
                            {
                                if (m_onItemClickListener != null)
                                {
                                    OnItemClickListener listener = (OnItemClickListener) m_onItemClickListener;
                                    listener.onStartTrackingTouch(seekBar);
                                }
                            }

                            @Override
                            public void onStopTrackingTouch(MySeekBar seekBar)
                            {
                                if (m_onItemClickListener != null)
                                {
                                    OnItemClickListener listener = (OnItemClickListener) m_onItemClickListener;
                                    listener.onStopTrackingTouch(seekBar);
                                }
                            }
                        });
                        item.openAlphaFr(m_currentSubSel, mCurrentAlphaProgress, new FilterItem.OnChangeAlphaFrCB()
                        {
                            @Override
                            public void change(float value)
                            {
                                if (subItem != null)
                                {
                                    LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) subItem.getLayoutParams();
                                    int curLeft = lp.leftMargin;
                                    FilterConfig config = (FilterConfig) m_config;
                                    int animValue = (int) ((config.def_alphafr_leftMargin - curLeft) * value);
                                    lp.leftMargin = curLeft + animValue;
                                    int curRight = lp.rightMargin;
                                    if (curRight > 0)
                                    {
                                        lp.rightMargin = (int) (curRight * (1 - value));
                                    }
                                    subItem.setLayoutParams(lp);
                                }
                                scrollByLeft2(subItem, value);
                            }
                        });
                        ((FilterSubItem) subItem).onAlphaMode();
                        mIsShowAlphaFr = true;
                    }
                    else
                    {
                        ((FilterSubItem) subItem).closeAlphaMode();
                        mIsShowAlphaFr = false;
                        FilterItem item = (FilterItem) subItem.getParent();
                        item.closeAlphaFr(new FilterItem.OnChangeAlphaFrCB()
                        {
                            @Override
                            public void change(float value)
                            {
                                if (subItem != null)
                                {
                                    LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) subItem.getLayoutParams();
                                    FilterConfig config = (FilterConfig) m_config;
                                    int curLeft = lp.leftMargin;
                                    int normolSub_l = config.def_sub_l;
                                    if (m_currentSubSel == 1)
                                    {
                                        normolSub_l = config.def_sub_l + config.def_sub_padding_l;
                                    }
                                    int animValue = (int) ((curLeft - normolSub_l) * value);
                                    lp.leftMargin = curLeft - animValue;

                                    if (subItem.getParent() != null)
                                    {
                                        int finishSubIndex = ((FilterItem) subItem.getParent()).getChildCount() - 2;
                                        if (m_currentSubSel == finishSubIndex)
                                        {
                                            lp.rightMargin = (int) (lp.rightMargin + (config.def_sub_padding_r - lp.rightMargin) * value);
                                        }
                                        subItem.setLayoutParams(lp);
                                    }
                                    scrollByCenter(subItem);
                                }
                            }
                        });
                    }
                }
                else
                {
                    super.onSubClick(subItem, position);
                }
            }
            else
            {
                super.onSubClick(subItem, position);
            }
        }
    }

    protected void scrollByLeft2(View view, float percent)
    {
        if (view != null && m_parent != null)
        {
            float center = m_parent.getLeft();
            int[] dst = new int[2];
            view.getLocationOnScreen(dst);
            float viewCenter = dst[0];
            FilterConfig config = (FilterConfig) m_config;
            float offset = viewCenter - center - config.def_alphafr_leftMargin;
            m_parent.smoothScrollBy((int) (offset * percent), 0);
        }
    }

    //	@Override
//	public void onDown(View v)
//	{
//		if(v.getTag() != null)
//		{
//			int position = (int)v.getTag();
//			if(getItemViewType(position) == VIEW_TYPE_HEAD)
//			{
//				//模糊暗影
//			}
//			else if(getItemViewType(position) == VIEW_TYPE_ORIGINAL)
//			{
//				//原图
//			}
//			else if(getItemViewType(position) == VIEW_TYPE_DOWNLOAD_MODE)
//			{
//				//下载位
//
//			}
//			else if(getItemViewType(position) == VIEW_TYPE_RECOMMEND)
//			{
//				//推荐位   可能没有
//
//			}
//			else
//			{
//				super.onDown(v);
//			}
//		}
//		else
//		{
//			super.onDown(v);
//		}
//	}

//	@Override
//	public void onUp(View v)
//	{
//		if(v.getTag() != null)
//		{
//			int position = (int)v.getTag();
//			if(getItemViewType(position) == VIEW_TYPE_HEAD)
//			{
//				//模糊暗影
//			}
//			else if(getItemViewType(position) == VIEW_TYPE_ORIGINAL)
//			{
//				//原图
//			}
//			else if(getItemViewType(position) == VIEW_TYPE_DOWNLOAD_MODE)
//			{
//				//下载位
//
//			}
//			else if(getItemViewType(position) == VIEW_TYPE_RECOMMEND)
//			{
//				//推荐位   可能没有
//
//			}
//			else
//			{
//				super.onUp(v);
//			}
//		}
//		else
//		{
//			super.onUp(v);
//		}
//	}


    @Override
    protected BaseItemContainer initItem(Context context, AbsExConfig itemConfig)
    {
        return new FilterItem(context, itemConfig);
    }

    public int SetItemStyleByIndex(int index, ItemInfo.Style style)
    {
        int out = -1;
        if (m_infoList != null && m_infoList.size() > index)
        {
            ItemInfo info = (ItemInfo) m_infoList.get(index);
            info.m_style = style;
            out = index;
            notifyDataSetChanged();
        }
        return out;
    }

    public int SetItemStyleByUri(int uri, ItemInfo.Style style)
    {
        ArrayList<?> temp = m_infoList;
        int out = GetGroupIndex((ArrayList<BaseExAdapter.ItemInfo>) temp, uri);
        if (out >= 0)
        {
            ItemInfo info = (ItemInfo) m_infoList.get(out);
            info.m_style = style;
            notifyDataSetChanged();
        }

        return out;
    }

    public void Lock2(int uri)
    {
        ItemInfo info = (ItemInfo) GetGroupItemInfoByUri(uri);
        if (info != null)
        {
            info.m_isLock2 = true;
        }
        notifyDataSetChanged();
    }

    public void Unlock2(int uri)
    {
        ItemInfo info = (ItemInfo) GetGroupItemInfoByUri(uri);
        if (info != null)
        {
            info.m_isLock2 = false;
        }
        notifyDataSetChanged();
    }

    public static class HeadViewHolder extends RecyclerView.ViewHolder
    {
        public HeadViewHolder(View itemView)
        {
            super(itemView);
        }
    }

    public static class OriginalViewHolder extends RecyclerView.ViewHolder
    {
        public OriginalViewHolder(View itemView)
        {
            super(itemView);
        }
    }


    public static class DownViewHolder extends RecyclerView.ViewHolder
    {
        public DownViewHolder(View itemView)
        {
            super(itemView);
        }
    }

    public static class RemViewHolder extends RecyclerView.ViewHolder
    {
        public RemViewHolder(View itemView)
        {
            super(itemView);
        }
    }

    public static class ItemInfo extends BaseExAdapter.ItemInfo
    {
        public static int MY_ID = 1;

        public enum Style
        {
            //正常
            NORMAL(0),
            //需要下载
            NEED_DOWNLOAD(1),
            //下载中
            LOADING(2),
            //等待下载
            WAIT(3),
            //下载失败
            FAIL(4),
            //新下载
            NEW(5);

            private final int m_value;

            Style(int value)
            {
                m_value = value;
            }

            public int GetValue()
            {
                return m_value;
            }
        }

        public Object[] m_logos;
        public String[] m_names;
        public int[] m_ids;

        public Object m_ex;
        public int m_bkColor;

        public Style m_style = Style.NORMAL;
        public boolean m_isLock2 = false;

        public boolean isDrawLine = true;

        public void setData(int[] uris, Object[] logos, String[] names, Object ex, int bkColor)
        {
            m_uris = uris;
            m_logos = logos;
            m_names = names;
            m_ex = ex;
            m_bkColor = bkColor;
        }
    }


    public static class RecommendItemInfo extends ItemInfo
    {
        public static final int RECOMMEND_ITEM_URI = 0xFFFFFFF1;

        public RecommendItemInfo()
        {
            m_uri = RECOMMEND_ITEM_URI;
            m_uris = new int[]{RECOMMEND_ITEM_URI};
            m_ids = new int[]{MY_ID++};
            m_canDrag = false;
        }

        public void setLogo(Object[] arr, String[] names, int bkColor)
        {
            m_logos = arr;
            m_names = names;
            m_bkColor = bkColor;
        }
    }

    public static class DownloadItemInfo extends ItemInfo
    {
        public int num;
        public static final int DOWNLOAD_ITEM_URI = 0xFFFFFFF2;

        public DownloadItemInfo()
        {
            m_uri = DOWNLOAD_ITEM_URI;
            m_uris = new int[]{DOWNLOAD_ITEM_URI};
            m_ids = new int[]{MY_ID++};
            m_canDrag = false;
        }

        public void setNum(int num)
        {
            this.num = num;
        }
    }

    public static class HeadItemInfo extends ItemInfo
    {
        public static final int HEAD_ITEM_URI = 0xFFFFFFF3;
        public boolean isSelectBlur;
        public boolean isSelectDark;

        public HeadItemInfo()
        {
            m_uri = HEAD_ITEM_URI;
            m_uris = new int[]{HEAD_ITEM_URI};
            m_ids = new int[]{MY_ID++};
            isSelectBlur = false;
            isSelectDark = false;
            m_canDrag = false;
        }
    }

    public static class OriginalItemInfo extends ItemInfo
    {
        public static final int ORIGINAL_ITEM_URI = 0xFFFFFFF4;

        public OriginalItemInfo()
        {
            m_uri = ORIGINAL_ITEM_URI;
            m_uris = new int[]{ORIGINAL_ITEM_URI};
            m_ids = new int[]{MY_ID++};
            m_canDrag = false;
        }
    }


    public void notifyItemDownLoad(int increaseSize)
    {
        if (m_hasOpen != -1)
        {
            m_hasOpen += increaseSize;
        }
        if (m_currentSel != -1)
        {
            m_currentSel += increaseSize;
        }
    }

    @Override
    public int SetSelectByIndex(int groupIndex, int subIndex, boolean isOpen, boolean isScrollToCenter, boolean isCallBackClick)
    {
        int out = -1;
        if (m_infoList != null && m_infoList.size() > groupIndex && groupIndex >= 0)
        {
            if (m_infoList.get(groupIndex) instanceof OriginalItemInfo)
            {
                m_currentSel = groupIndex;
                out = groupIndex;
                if (isScrollToCenter)
                {
                    int parentCenter = m_config.def_parent_center_x;
                    int viewLeft = m_config.def_item_l + m_config.def_item_w / 2 + m_config.def_parent_left_padding;
                    int offset = parentCenter - viewLeft;
                    ((LinearLayoutManager) m_parent.getLayoutManager()).scrollToPositionWithOffset(m_currentSel, offset);
                }
                if (m_onItemClickListener != null && isCallBackClick)
                {
                    ((BaseExAdapter.OnItemClickListener) m_onItemClickListener).OnItemClick((BaseExAdapter.ItemInfo) m_infoList.get(groupIndex), groupIndex, -1);
                }
                notifyDataSetChanged();
            }
        }
        if (out != -1)
        {
            return out;
        }
        else
        {
            return super.SetSelectByIndex(groupIndex, subIndex, isOpen, isScrollToCenter, isCallBackClick);
        }
    }

    public interface OnItemClickListener extends BaseExAdapter.OnItemClickListener
    {
        public void onProgressChanged(MySeekBar seekBar, int progress);

        public void onStartTrackingTouch(MySeekBar seekBar);

        public void onStopTrackingTouch(MySeekBar seekBar);

        public void onSeekBarStartShow(MySeekBar seekBar);

        public void onFinishLayoutAlphaFr(MySeekBar seekBar);

    }
}