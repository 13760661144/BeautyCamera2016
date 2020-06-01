package cn.poco.widget.recycle;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import cn.poco.recycleview.AbsAdapter;
import cn.poco.recycleview.AbsExConfig;
import cn.poco.recycleview.BaseExAdapter;
import cn.poco.recycleview.BaseGroup;
import cn.poco.recycleview.BaseItem;
import cn.poco.recycleview.BaseItemContainer;


/**
 * Created by lgd on 2017/5/9.
 */

public class RecommendExAdapter extends BaseExAdapter
{
	private static final String TAG = "RecommendAdapter";
	public final static int VIEW_TYPE_DOWNLOAD_MODE = 0x01000000;
	public final static int VIEW_TYPE_RECOMMEND = 0x00100000;

	public RecommendExAdapter(AbsExConfig itemConfig)
	{
		super(itemConfig);
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
	{
		RecyclerView.ViewHolder holder = super.onCreateViewHolder(parent, viewType);
		if(holder == null)
		{
			if(viewType == VIEW_TYPE_DOWNLOAD_MODE)
			{
				DownMorePage page = new DownMorePage(parent.getContext(), m_config);
				RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(m_config.def_item_w, m_config.def_item_h);
				page.setLayoutParams(params);
				holder = new DownViewHolder(page);
			}
			else if(viewType == VIEW_TYPE_RECOMMEND)
			{
				RecommendPage page = new RecommendPage(parent.getContext());
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
		super.onBindViewHolder(holder, position);
		int type = getItemViewType(position);
		if(type == VIEW_TYPE_DOWNLOAD_MODE || type == VIEW_TYPE_RECOMMEND)
		{
			BaseItem item = (BaseItem)holder.itemView;
			item.SetData(m_infoList.get(position), position);
			item.setTag(position);
			item.setOnTouchListener(mOnClickListener);
		}
	}

	@Override
	public int getItemViewType(int position)
	{
		AbsAdapter.ItemInfo info = m_infoList.get(position);
		if(info instanceof DownloadItemInfo)
		{
			return VIEW_TYPE_DOWNLOAD_MODE;
		}
		else if(info instanceof RecommendItemInfo)
		{
			return VIEW_TYPE_RECOMMEND;
		}
		else
		{
			return super.getItemViewType(position);
		}
	}


	@Override
	public void onClick(View v)
	{
		if(v.getParent() instanceof BaseItemContainer)
		{
			super.onClick(v);
		}
		else
		{
			int position = (int)v.getTag();
			BaseItem item = (BaseItem)v;
			if(m_onItemClickListener != null)
			{
				((BaseExAdapter.OnItemClickListener)m_onItemClickListener).OnItemClick((BaseExAdapter.ItemInfo)m_infoList.get(position), position, -1);
			}
			item.onClick();
		}
	}

	/**
	 * 如果资源没下载，回调的 OnItemDown(ItemInfo info, int parentIndex, int subIndex); subIndex为-1即可
	 *
	 * @param group
	 * @param position
	 */
	@Override
	protected void onGroupClick(BaseGroup group, int position)
	{
		if(m_infoList.get(position) instanceof ItemInfo)
		{
			ItemInfo itemInfo = (ItemInfo)m_infoList.get(position);
			if(itemInfo.m_style == ItemInfo.Style.NEED_DOWNLOAD)
			{
				if(m_onItemClickListener != null)
				{
					((OnItemClickListener)m_onItemClickListener).OnItemClick(itemInfo, position, -1);
				}
				return;
			}
		}
		super.onGroupClick(group, position);
	}

	@Override
	protected BaseItemContainer initItem(Context context, AbsExConfig itemConfig)
	{
		return new RecommendExItem(context, itemConfig);
	}


	public int SetItemStyleByIndex(int index, ItemInfo.Style style)
	{
		int out = -1;
		if(m_infoList != null && m_infoList.size() > index)
		{
			ItemInfo info = (ItemInfo)m_infoList.get(index);
			info.m_style = style;
			out = index;
			notifyDataSetChanged();
		}
		return out;
	}

	public int SetItemStyleByUri(int uri, ItemInfo.Style style)
	{
		ArrayList<?> temp = m_infoList;
		int out = GetGroupIndex((ArrayList<BaseExAdapter.ItemInfo>)temp, uri);
		if(out >= 0)
		{
			ItemInfo info = (ItemInfo)m_infoList.get(out);
			info.m_style = style;
			notifyDataSetChanged();
		}

		return out;
	}

	public void Lock2(int uri)
	{
		ItemInfo info = (ItemInfo)GetGroupItemInfoByUri(uri);
		if(info != null)
		{
			info.m_isLock2 = true;
		}
		notifyDataSetChanged();
	}

	public void Unlock2(int uri)
	{
		ItemInfo info = (ItemInfo)GetGroupItemInfoByUri(uri);
		if(info != null)
		{
			info.m_isLock2 = false;
		}
		notifyDataSetChanged();
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

		public Style m_style = Style.NORMAL;
		public boolean m_isLock2 = false;

		public void setData(int[] uris, Object[] logos, String[] names, Object ex)
		{
			m_uris = uris;
			m_logos = logos;
			m_names = names;
			m_ex = ex;
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

		public void setLogo(Object[] arr, String[] names)
		{
			m_logos = arr;
			m_names = names;
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

		public void setLogos(Object[] logo)
		{
			this.m_logos = logo;
		}

		public void setNames(String[] names)
		{
			this.m_names = names;
		}

		public void setNum(int num)
		{
			this.num = num;
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

	public void notifyItemDownLoad(int increaseSize)
	{
		if(m_hasOpen != -1)
		{
			m_hasOpen += increaseSize;
		}
		if(m_currentSel != -1)
		{
			m_currentSel += increaseSize;
		}
	}

}
