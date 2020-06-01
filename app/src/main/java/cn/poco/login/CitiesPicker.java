package cn.poco.login;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.HeaderViewListAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;

import cn.poco.login.AreaList.AreaInfo;
import cn.poco.login.AreaList.AreaInfo2;
import cn.poco.tianutils.ShareData;

public class CitiesPicker extends FrameLayout
{
	protected ArrayList<ListView> m_lists;
	protected int m_curIndex = 0;
	protected int m_totalLen = 3;
	protected HashMap<Integer, ArrayList<ListInfo>> m_datas;
	protected OnChooseCallback m_cb;
//	protected String m_country = "";
//	protected String m_province = "";
//	protected String m_city = "";
	protected AreaInfo2[] m_allAreaInfos;
	protected long m_selectAreaId = -1;
	public CitiesPicker(Context context, OnChooseCallback cb)
	{
		this(context, null, cb);
	}

	public CitiesPicker(Context context, AttributeSet attrs, OnChooseCallback cb)
	{
		super(context, attrs);
		initUI();
		m_cb = cb;
	}

	public void initData(AreaInfo2[] areaInfo)
	{
		m_datas = new HashMap<Integer, ArrayList<ListInfo>>();
		m_allAreaInfos = areaInfo;
		m_datas.put(0, CastToListInfo(m_allAreaInfos));

		if(m_datas.get(m_curIndex) != null)
		{
			m_lists.get(m_curIndex).setVisibility(VISIBLE);
			ListAdapter adapter = new ListAdapter();
			adapter.setDatas(m_datas.get(m_curIndex));
			adapter.notifyDataSetChanged();
			m_lists.get(m_curIndex).setAdapter(adapter);
		}
	}

	public ArrayList<ListInfo> CastToListInfo(AreaInfo2[] src)
	{
		ArrayList<ListInfo> out = new ArrayList<ListInfo>();
		ListInfo info = null;
		if(src != null)
		{
			for(AreaInfo2 area : src)
			{
				info = new ListInfo();
				info.id = area.m_id;
				info.m_ex = area;
				if(info.id == m_selectAreaId)
				{
					info.choose = true;
				}
				else
				{
					info.choose = false;
				}
				out.add(info);
			}
		}
		return out;
	}


	public void setSelectAreaId(long id)
	{
		m_selectAreaId = id;
	}

	protected void initUI()
	{
		m_lists = new ArrayList<ListView>();
		LayoutParams fl;
		for(int i = 0; i < m_totalLen; i ++)
		{
			ListView list = new ListView(getContext());
			list.setVisibility(View.GONE);
			list.setOnItemClickListener(m_itemClickListener);
			list.setDivider(new ColorDrawable(0x00000000));
			list.setDividerHeight(0);
			list.setCacheColorHint(0x00000000);
			list.setSelector(new ColorDrawable(Color.TRANSPARENT));
			fl = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			list.setLayoutParams(fl);
			ImageView m_headerView = new ImageView(getContext());
			AbsListView.LayoutParams al = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT,ShareData.PxToDpi_xhdpi(40));
			m_headerView.setLayoutParams(al);
			list.addHeaderView(m_headerView,null,false);
			this.addView(list);
			m_lists.add(list);
		}
	}

	protected AdapterView.OnItemClickListener m_itemClickListener = new AdapterView.OnItemClickListener()
	{

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id)
		{
			AreaInfo2 areaInfo = null;
			if(m_datas.get(m_curIndex) != null)
			{
				int len = m_datas.get(m_curIndex).size();

				ArrayList<ListInfo> infos = m_datas.get(m_curIndex);
				ListInfo info = null;
				for(int i = 0; i < len; i ++)
				{
					info = infos.get(i);
					if(info != null)
					{
						if(i == (position - 1))
						{
							info.choose = true;
							areaInfo = (AreaInfo2)info.m_ex;
							if(areaInfo.m_child == null)
							{
								((ChooseItem)view).onChoose(info.choose);
							}
							continue;
						}
						info.choose = false;
					}
				}
//				((ListAdapter)m_lists.get(m_curIndex).getAdapter()).notifyDataSetChanged();
				HeaderViewListAdapter temp = (HeaderViewListAdapter) m_lists.get(m_curIndex).getAdapter();
				ListAdapter listAdapter = (ListAdapter) temp.getWrappedAdapter();
				listAdapter.notifyDataSetChanged();
			}
			switch(m_curIndex)
			{
				case 0:
				{
//					m_country = areaInfo.m_name;
					AreaInfo2[] info = areaInfo.m_child;
					if(info != null && info.length > 0)
					{
						m_datas.put(m_curIndex + 1, CastToListInfo(info));
						moveToNextView(true);
					}
					else
					{
						if(m_cb != null)
						{
							m_cb.onChoose(areaInfo.m_id);
						}
					}
					break;
				}
				case 1:
				{
//					m_province = " " + areaInfo.m_name;
					AreaInfo2[] info = areaInfo.m_child;
					if(info != null && info.length > 0)
					{
						m_datas.put(m_curIndex + 1, CastToListInfo(info));
						moveToNextView(true);
					}
					else
					{
						if(m_cb != null)
						{
							m_cb.onChoose(areaInfo.m_id);
						}
					}
					break;
				}
				case 2:
				{
//					m_city = " " + areaInfo.m_name;
					if(m_cb != null)
					{
						m_cb.onChoose(areaInfo.m_id);
					}
					break;
				}
			}

		}
	};

	public boolean isFirst()
	{
		if(m_curIndex == 0)
			return true;
		return false;
	}

	public boolean onBack()
	{
		if(isFirst())
		{
			return false;
		}
		moveToPreView(true);
		return true;
	}

	public void moveToNextView(boolean hasAnim)
	{
		ListView view1 = m_lists.get(m_curIndex);
		m_curIndex ++;

		ListView view2 = m_lists.get(m_curIndex);
		ListAdapter adapter = new ListAdapter();
		adapter.setDatas(m_datas.get(m_curIndex));
		adapter.notifyDataSetChanged();
		view2.setAdapter(adapter);
		DoLeftAnim(view1, false);
		DoLeftAnim(view2, true);
//		m_curIndex ++;
	}

	public void moveToPreView(boolean hasAnim)
	{
		ListView view1 = m_lists.get(m_curIndex);
		m_curIndex --;
		ListView view2 = m_lists.get(m_curIndex);
		DoRightAnim(view1, false);
		DoRightAnim(view2, true);
	}

	protected void DoRightAnim(View v, boolean show)
	{
		if(v == null)
			return;
		v.clearAnimation();
		int start;
		int end;
		if(show)
		{
			start = -1;
			end = 0;
			v.setVisibility(View.VISIBLE);
		}
		else
		{
			start = 0;
			end = 1;
			v.setVisibility(View.GONE);
		}
		AnimationSet as = new AnimationSet(true);
		TranslateAnimation ta = new TranslateAnimation(Animation.RELATIVE_TO_SELF, start, Animation.RELATIVE_TO_SELF, end, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
		ta.setDuration(350);
		as.addAnimation(ta);
		v.startAnimation(as);
	}

	protected void DoLeftAnim(View v, boolean show)
	{
		if(v == null)
			return;
		int start;
		int end;
		if(show)
		{
			start = 1;
			end = 0;
			v.setVisibility(View.VISIBLE);
		}
		else
		{
			start = 0;
			end = -1;
			v.setVisibility(View.GONE);
		}
		AnimationSet as = new AnimationSet(true);
		TranslateAnimation ta = new TranslateAnimation(Animation.RELATIVE_TO_SELF, start, Animation.RELATIVE_TO_SELF, end, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
		ta.setDuration(350);
		as.addAnimation(ta);
		v.startAnimation(as);
	}

	public class ListAdapter extends BaseAdapter
	{
		protected ArrayList<ListInfo> m_datas;

		public void setDatas(ArrayList<ListInfo> datas)
		{
			m_datas = datas;
		}

		@Override
		public int getCount()
		{
			if(m_datas != null)
			{
				return m_datas.size();
			}
			return 0;
		}

		@Override
		public Object getItem(int position)
		{
			if(m_datas != null)
			{
				return m_datas.get(position);
			}
			return null;
		}

		@Override
		public long getItemId(int position)
		{
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			if(convertView == null)
			{
				AbsListView.LayoutParams al = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(88));
				convertView = new ChooseItem(getContext());
				convertView.setLayoutParams(al);
			}
			Object ex = m_datas.get(position).m_ex;
			((ChooseItem)convertView).setText(((AreaInfo)ex).m_name);
			AreaInfo2 info = AreaList.GetLocation(m_allAreaInfos, m_datas.get(position).id);
			if(info != null && info.m_child == null)
			{
				((ChooseItem)convertView).showArrow(false);
			}
			else
			{
				((ChooseItem)convertView).showArrow(true);
			}

			if(info != null)
			{
				if(info.m_child == null)
				{
					((ChooseItem)convertView).onChoose(m_datas.get(position).choose);
				}
				else
				{
					((ChooseItem)convertView).onChoose(false);
				}
			}
			return convertView;
		}

	}

	public class ListInfo
	{
		long id;
		Object m_ex;
		boolean choose = false;
	}

	protected void clearAll()
	{
		this.removeAllViews();
		m_lists.clear();
	}

	public static interface OnChooseCallback{
		public void onChoose(long id);
	}
}
