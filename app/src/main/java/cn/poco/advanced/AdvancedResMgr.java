package cn.poco.advanced;

import java.util.ArrayList;

import cn.poco.beautify.BeautifyResMgr2;
import cn.poco.camera.RotationImg2;
import cn.poco.resource.DecorateGroupRes;
import cn.poco.resource.DecorateRes;
import cn.poco.tianutils.ItemBoxV3;
import cn.poco.tianutils.ItemListV5;
import cn.poco.tsv.FastDynamicListV2;
import cn.poco.tsv.FastItemList;
import cn.poco.tsv100.FastHSV100;

public class AdvancedResMgr
{
	public static int GetResIndex(ArrayList<?> arr, int uri)
	{
		int out = -1;

		int len = arr.size();
		Object temp;
		for(int i = 0; i < len; i++)
		{
			temp = arr.get(i);
			if(temp instanceof ItemBoxV3.ItemInfo)
			{
				if(((ItemBoxV3.ItemInfo)temp).m_uri == uri)
				{
					out = i;
					break;
				}
			}
			else if(temp instanceof FastItemList.ItemInfo)
			{
				if(((FastItemList.ItemInfo)temp).m_uri == uri)
				{
					out = i;
					break;
				}
			}
			else if(temp instanceof ItemListV5.ItemInfo)
			{
				if(((ItemListV5.ItemInfo)temp).m_uri == uri)
				{
					out = i;
					break;
				}
			}
			else if(temp instanceof DecorateRes)
			{
				if(((DecorateRes)temp).m_id == uri)
				{
					out = i;
					break;
				}
			}
		}

		return out;
	}

	/**
	 * 只适用常规样式,包含下载按钮,下载按钮只能在第一个
	 *
	 * @param list FastDynamicListV2
	 * @param dst
	 * @return need reset? SetSelItemByUri(0);
	 */
	public static boolean UpdateList(FastHSV100 list, ArrayList<RecommendItemList.ItemInfo> dst)
	{
		boolean out = false;

		//if(list != null && list.m_view != null && dst != null)
		//{
		//	int len = dst.size();
		//	int[] orders = new int[dst.size() + 1];
		//	orders[0] = FastDynamicListV2.DOWNLOAD_ITEM_URI;
		//	for(int i = 0; i < len; i++)
		//	{
		//		orders[i + 1] = dst.get(i).m_uri;
		//	}
		//
		//	ArrayList<FastDynamicListV2.ItemInfo> src = (ArrayList<FastDynamicListV2.ItemInfo>)list.m_view.GetResData();
		//	//获取当前选中的uri
		//	Integer selUri = null;
		//	int selIndex = list.m_view.GetSelectIndex();
		//	if(selIndex >= 0)
		//	{
		//		selUri = src.get(selIndex).m_uri;
		//	}
		//	//去除下载按钮
		//	int index = FastDynamicListV2.GetIndex(src, FastDynamicListV2.DOWNLOAD_ITEM_URI);
		//	if(index >= 0)
		//	{
		//		src.remove(index);
		//	}
		//	BkResMgr.AddDelArr(dst, src);
		//
		//	len = dst.size();
		//	for(int i = 0; i < len; i++)
		//	{
		//		((FastDynamicListV2)list.m_view).AddItem(0, dst.get(i));
		//	}
		//
		//	len = src.size();
		//	int uri;
		//	for(int i = 0; i < len; i++)
		//	{
		//		uri = src.get(i).m_uri;
		//		if(selUri != null && uri == selUri)
		//		{
		//			out = true;
		//		}
		//		((FastDynamicListV2)list.m_view).DeleteItemByUri(src.get(i).m_uri);
		//	}
		//
		//	list.m_view.UpdateOrder(orders);
		//}
		if(list != null && list.m_view != null && dst != null)
		{
			int len = dst.size();
			int[] orders = new int[dst.size()];
			for(int i = 0; i < len; i++)
			{
				orders[i] = dst.get(i).m_uri;
			}

			ArrayList<RecommendItemList.ItemInfo> src = (ArrayList<RecommendItemList.ItemInfo>)list.m_view.GetResData();
			//获取当前选中的uri
			Integer selUri = null;
			int selIndex = ((RecommendItemList)list.m_view).GetSelectIndex();
			if(selIndex >= 0)
			{
				selUri = src.get(selIndex).m_uri;
			}
			//获取下载按钮的index
			int index = RecommendItemList.GetIndex(src, RecommendItemList.DownloadItemInfo.DOWNLOAD_ITEM_URI);
			//插入新item的插入位置
			index++;

			AddDelArr(dst, src);

			len = dst.size();
			for(int i = 0; i < len; i++)
			{
				((RecommendItemList)list.m_view).AddItem(index, dst.get(i));
			}

			len = src.size();
			int uri;
			for(int i = 0; i < len; i++)
			{
				uri = src.get(i).m_uri;
				if(selUri != null && uri == selUri)
				{
					out = true;
				}
				((RecommendItemList)list.m_view).DeleteItemByUri(src.get(i).m_uri);
			}

			((RecommendItemList)list.m_view).UpdateOrder(orders);
		}

		return out;
	}

	public static void AddDelArr(ArrayList<RecommendItemList.ItemInfo> dst, ArrayList<RecommendItemList.ItemInfo> src)
	{
		RecommendItemList.ItemInfo temp;
		NEXT:
		for(int i = 0; i < src.size(); i++)
		{
			temp = src.get(i);
			for(int j = 0; j < dst.size(); j++)
			{
				if(temp.m_uri == dst.get(j).m_uri)
				{
					src.remove(i);
					dst.remove(j);
					i--;
					continue NEXT;
				}
			}
		}
	}

	public static int GetFirstInsertIndex(ArrayList<?> arr)
	{
		int out = -1;

		out = GetResIndex(arr, 0);
		if(out < 0)
		{
			out = GetResIndex(arr, FastDynamicListV2.DOWNLOAD_ITEM_URI);
			if(out < 0)
			{
				out = -1;
			}
		}
		out++;

		return out;
	}

	public static ArrayList<ItemBoxV3.ItemInfo> GetPendantRes(ArrayList<DecorateGroupRes> arr, int uri)
	{
		ArrayList<ItemBoxV3.ItemInfo> out = new ArrayList<ItemBoxV3.ItemInfo>();
		DecorateGroupRes ress = null;

		int len = arr.size();
		for(int i = 0; i < len; i++)
		{
			if(arr.get(i).m_id == uri)
			{
				ress = arr.get(i);
				break;
			}
		}

		if(ress != null)
		{
			ItemBoxV3.ItemInfo info;
			int resLen = ress.m_group.size();
			for(int i = 0; i < resLen; i++)
			{
				DecorateRes res = ress.m_group.get(i);

				info = new ItemBoxV3.ItemInfo();
				info.m_uri = res.m_id;
				info.m_logo = res.m_thumb;
				info.m_name = res.m_name;
				info.m_ex = res;
				out.add(info);
			}
		}

		return out;
	}

	public static RotationImg2[] CloneRotationImgArr(RotationImg2[] src)
	{
		return BeautifyResMgr2.CloneRotationImgArr(src);
	}

	public static boolean IsNull(Object res)
	{
		boolean out = true;

		if(res != null && !res.equals(""))
		{
			out = false;
		}

		return out;
	}
}
