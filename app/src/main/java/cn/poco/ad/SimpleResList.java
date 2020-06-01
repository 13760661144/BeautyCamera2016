package cn.poco.ad;

import java.util.ArrayList;

import cn.poco.tianutils.ItemListV5;
import cn.poco.tsv.FastDynamicListV2;

public class SimpleResList
{
	public static ArrayList<ItemListV5.ItemInfo> getItemInfo(ArrayList<MyItemInfo> infos)
	{
		ArrayList<ItemListV5.ItemInfo> out = new ArrayList<ItemListV5.ItemInfo>();
		for(MyItemInfo info : infos)
		{
			ItemListV5.ItemInfo itemInfo = new ItemListV5.ItemInfo();
			itemInfo.m_name = info.m_name;
			itemInfo.m_uri = info.m_uri;
			itemInfo.m_logo = info.m_thumb;
			itemInfo.m_ex = info;
			out.add(itemInfo);
		}
		return out;
	}

	public static ArrayList<FastDynamicListV2.ItemInfo> getFastItemInfo(ArrayList<MyItemInfo> infos)
	{
		ArrayList<FastDynamicListV2.ItemInfo> out = new ArrayList<FastDynamicListV2.ItemInfo>();
		for(MyItemInfo info : infos)
		{
			FastDynamicListV2.ItemInfo itemInfo = new FastDynamicListV2.ItemInfo();
			itemInfo.m_name = info.m_name;
			itemInfo.m_uri = info.m_uri;
			itemInfo.m_logo = info.m_thumb;
			itemInfo.m_ex = info;
			out.add(itemInfo);
		}
		return out;
	}

	public static class MyItemInfo
	{
		public int m_uri;
		public String m_name;
		public Object m_thumb;
		public Object m_res;
		public Object m_ex;
	}
}
