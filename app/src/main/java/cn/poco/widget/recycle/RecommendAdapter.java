package cn.poco.widget.recycle;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import cn.poco.recycleview.AbsAdapter;
import cn.poco.recycleview.AbsConfig;
import cn.poco.recycleview.BaseAdapter;
import cn.poco.recycleview.BaseItem;


/**
 * Created by lgd on 2017/5/9.
 */

public class RecommendAdapter extends BaseAdapter
{
	private static final String TAG = "RecommendAdapter";
	public final static int VIEW_TYPE_DOWNLOAD_MODE = 0x01000000;
	public final static int VIEW_TYPE_RECOMMEND = 0x00100000;

	public RecommendAdapter(AbsConfig itemConfig)
	{
		super(itemConfig);
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
	{
		RecyclerView.ViewHolder holder = null;
		if(viewType == VIEW_TYPE_DOWNLOAD_MODE)
		{
			DownMorePage page = new DownMorePage(parent.getContext(),m_config);
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
		if(holder == null){
			holder = super.onCreateViewHolder(parent, viewType);
		}
		return holder;
	}

	@Override
	protected BaseItem initItem(Context context)
	{
		return new RecommendItem(context);
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
	{
		super.onBindViewHolder(holder, position);
		int type = getItemViewType(position);
		if(type == VIEW_TYPE_DOWNLOAD_MODE)
		{
			DownViewHolder viewHolder = (DownViewHolder)holder;
			BaseItem page = (BaseItem) viewHolder.itemView;
			page.SetData(m_infoList.get(position), position);
			page.setTag(position);
			page.setOnTouchListener(mOnClickListener);
//			viewHolder.itemView.setOnTouchListener(mOnClickListener);
		}
		else if(type == VIEW_TYPE_RECOMMEND)
		{
			RemViewHolder viewHolder = (RemViewHolder)holder;
			BaseItem page = (BaseItem)viewHolder.itemView;
			page.SetData(m_infoList.get(position), position);
			page.setTag(position);
			page.setOnTouchListener(mOnClickListener);
		}
	}

	@Override
	public int getItemViewType(int position)
	{
		if (m_infoList.size() <= position) {
			return super.getItemViewType(position);
		}
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
		int position = (int)v.getTag();
		int type = getItemViewType(position);
		if (type == VIEW_TYPE_DOWNLOAD_MODE || type == VIEW_TYPE_RECOMMEND) {
			BaseItem item = (BaseItem) v;
			item.onClick();
			if (m_onItemClickListener != null) {
				m_onItemClickListener.OnItemClick(m_infoList.get(position),position);
			}
		}else{
			super.onClick(v);
		}
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
		int out = GetIndex((ArrayList<BaseAdapter.ItemInfo>)temp, uri);
		if(out >= 0)
		{
			ItemInfo info = (ItemInfo)m_infoList.get(out);
			info.m_style = style;
			notifyDataSetChanged();
		}

		return out;
	}

	public void Lock(int uri)
	{
		int index= GetIndex(uri);
		ItemInfo info = (ItemInfo)GetItemInfoByIndex(uri);
		if(info != null)
		{
			info.m_isLock = true;
		}
		notifyItemChanged(index);
	}

	public void Unlock(int uri)
	{
		int index= GetIndex(uri);
		ItemInfo info = (ItemInfo)GetItemInfoByIndex(uri);
		if(info != null)
		{
			info.m_isLock = false;
		}
		notifyItemChanged(index);
	}

	public static class ItemInfo extends BaseAdapter.ItemInfo
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

		public Object m_logo;
		public String m_name;
		public int m_id;

		public Object m_ex;

		public Style m_style = Style.NORMAL;
		public boolean m_isLock = false;

		public void setData(int uri, Object logo, String name, Object ex)
		{
			m_uri = uri;
			m_logo = logo;
			m_name = name;
			m_ex = ex;
		}
	}

	public static class RecommendItemInfo extends ItemInfo
	{
		public static final int RECOMMEND_ITEM_URI = 0xFFFFFFF1;

		public RecommendItemInfo()
		{
			m_uri = RECOMMEND_ITEM_URI;
			m_id = MY_ID;
			m_canDrag = false;
		}

		public void setLogo(Object arr, String name)
		{
			m_logo = arr;
			m_name = name;
		}
	}

	public static class DownloadItemInfo extends ItemInfo
	{
		public int num;
		public static final int DOWNLOAD_ITEM_URI = 0xFFFFFFF2;

		public Object[] m_logos;

		public DownloadItemInfo()
		{
			m_uri = DOWNLOAD_ITEM_URI;
			m_id = MY_ID;
			m_canDrag = false;
		}

		public void setLogos(Object[] m_logos)
		{
			this.m_logos = m_logos;
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
		if(m_currentSel != -1)
		{
			m_currentSel += increaseSize;
		}
	}
}
