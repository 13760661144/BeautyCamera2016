package cn.poco.frame;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import java.util.ArrayList;

import cn.poco.MaterialMgr2.MgrUtils;
import cn.poco.filterPendant.ColorItemInfo;
import cn.poco.resource.BaseRes;
import cn.poco.resource.FrameExRecommendResMgr2;
import cn.poco.resource.FrameExRes;
import cn.poco.resource.FrameExResMgr2;
import cn.poco.resource.FrameGroupRes;
import cn.poco.resource.FrameRecommendResMgr2;
import cn.poco.resource.FrameRes;
import cn.poco.resource.RecommendRes;
import cn.poco.system.TagMgr;
import cn.poco.system.Tags;
import cn.poco.widget.recycle.RecommendAdapter;
import cn.poco.widget.recycle.RecommendExAdapter;

public class FrameResMgr
{
	public static RecommendExAdapter.RecommendItemInfo getThemeRecommendInfo(Context context)
	{
		ArrayList<RecommendRes> arr = null;
		Object[] logos = null;
		String[] names = null;
		RecommendExAdapter.RecommendItemInfo tempInfo = null;
		ArrayList<RecommendRes> tempArr = FrameRecommendResMgr2.getInstance().sync_ar_GetCloudCacheRes(context, null);
		if(tempArr != null && tempArr.size() > 0)
		{
			arr = new ArrayList<>();
			int len = tempArr.size();
			RecommendRes temp;
			for(int i = 0; i < len; i++)
			{
				temp = tempArr.get(i);
				if(MgrUtils.hasDownloadFrame(context, temp.m_id) == 0 && TagMgr.CheckTag(context, Tags.ADV_RECO_FRAME_FLAG + temp.m_id))
				{
					arr.add(temp);
				}
			}
			len = arr.size();
			if(len > 0)
			{
				logos = new Object[len];
				names = new String[len];
				for(int i = 0; i < len; i++)
				{
					temp = arr.get(i);
					Object thumb = temp.m_thumb;
					if (thumb == null && !TextUtils.isEmpty(temp.url_thumb)) {
						thumb = temp.url_thumb;
					}
					logos[i] = thumb;
					names[i] = temp.m_name;
				}
				//推荐位
				tempInfo = new RecommendExAdapter.RecommendItemInfo();
				tempInfo.m_ex = arr;
				tempInfo.setLogo(logos, names);
			}
		}
		return tempInfo;
	}

	public static ArrayList<RecommendExAdapter.ItemInfo> GetThemeFrameRes(Context context)
	{
		ArrayList<RecommendExAdapter.ItemInfo> out = new ArrayList<>();

		RecommendExAdapter.ItemInfo tempInfo;
		ArrayList<RecommendRes> arr = null;
		Object[] logos = null;
		String[] names = null;
		//下载按钮
		tempInfo = new RecommendExAdapter.DownloadItemInfo();
		((RecommendExAdapter.DownloadItemInfo)tempInfo).setNum(cn.poco.resource.FrameResMgr2.getInstance().GetNoDownloadCount());
		out.add(tempInfo);

		RecommendExAdapter.RecommendItemInfo recommendItemInfo = getThemeRecommendInfo(context);
		if (recommendItemInfo != null) {
			((RecommendExAdapter.DownloadItemInfo) tempInfo).setLogos(recommendItemInfo.m_logos);
			((RecommendExAdapter.DownloadItemInfo) tempInfo).setNames(recommendItemInfo.m_names);
		}
		if(recommendItemInfo != null){
			out.add(recommendItemInfo);
		}

		ArrayList<FrameGroupRes> resArr = cn.poco.resource.FrameResMgr2.getInstance().GetGroupResArr();
		for(FrameGroupRes res : resArr)
		{
			if(res != null)
			{
				int len2 = 1;
				if(res.m_group != null)
				{
					len2 = res.m_group.size() + 1;
				}
				int[] uris = new int[len2];
				logos = new Object[len2];
				names = new String[len2];
				uris[0] = res.m_id;
				logos[0] = res.m_thumb;
				names[0] = res.m_name;
				if(res.m_group != null)
				{
					FrameRes temp3;
					for(int j = 1; j < len2; j++)
					{
						temp3 = res.m_group.get(j - 1);
						uris[j] = temp3.m_id;
						logos[j] = temp3.m_thumb;
						names[j] = temp3.m_name;
					}
				}
				tempInfo = new RecommendExAdapter.ItemInfo();
				if(!res.m_name.equals("其他")) {
					tempInfo.m_canDrag = true;
				}
				tempInfo.m_uri = res.m_id;
				tempInfo.setData(uris, logos, names, res);
				switch(res.m_type)
				{
					case BaseRes.TYPE_NETWORK_URL:
						tempInfo.m_style = RecommendExAdapter.ItemInfo.Style.NEED_DOWNLOAD;
						break;
					case BaseRes.TYPE_LOCAL_RES:
					case BaseRes.TYPE_LOCAL_PATH:
					default:
						tempInfo.m_style = RecommendExAdapter.ItemInfo.Style.NORMAL;
						break;
				}
				if(cn.poco.resource.FrameResMgr2.getInstance().IsNewGroup(res.m_id))
				{
					tempInfo.m_style = RecommendExAdapter.ItemInfo.Style.NEW;
				}
				out.add(tempInfo);
			}
		}

		return out;
	}

	public static RecommendAdapter.RecommendItemInfo getSimpleRecommendInfo(Context context)
	{
		ArrayList<RecommendRes> arr = null;
		Object[] logos = null;
		String[] names = null;
		RecommendAdapter.RecommendItemInfo tempInfo = null;
		ArrayList<RecommendRes> tempArr = FrameExRecommendResMgr2.getInstance().sync_ar_GetCloudCacheRes(context, null);
		if(tempArr != null && tempArr.size() > 0)
		{
			arr = new ArrayList<>();
			int len = tempArr.size();
			RecommendRes temp;
			for(int i = 0; i < len; i++)
			{
				temp = tempArr.get(i);
				if(MgrUtils.hasDownloadSimpleFrame(context, temp.m_id) == 0 && TagMgr.CheckTag(context, Tags.ADV_RECO_SIMPLE_FRAME_FLAG + temp.m_id))
				{
					arr.add(temp);
				}
			}
			len = arr.size();
			if(len > 0)
			{
				logos = new Object[len];
				names = new String[len];
				for(int i = 0; i < len; i++)
				{
					temp = arr.get(i);
					logos[i] = temp.m_thumb;
					names[i] = temp.m_name;
				}
				//推荐位
				tempInfo = new RecommendAdapter.RecommendItemInfo();
				tempInfo.m_ex = arr;
				tempInfo.setLogo(logos[0], names[0]);
			}
		}
		return tempInfo;
	}


	public static ArrayList<RecommendAdapter.ItemInfo> GetSimpleFrameRes(Activity ac)
	{
		ArrayList<RecommendAdapter.ItemInfo> out = new ArrayList<>();

		RecommendAdapter.ItemInfo tempInfo;
		ArrayList<RecommendRes> arr = null;
		Object[] logos = null;
		String[] names = null;

		//下载按钮
		tempInfo = new RecommendAdapter.DownloadItemInfo();
		((RecommendAdapter.DownloadItemInfo)tempInfo).setNum(FrameExResMgr2.getInstance().GetNoDownloadCount(ac));
		out.add(tempInfo);

		RecommendAdapter.RecommendItemInfo recommendItemInfo = getSimpleRecommendInfo(ac);
		if (recommendItemInfo != null) {
			((RecommendAdapter.DownloadItemInfo) tempInfo).m_logo = recommendItemInfo.m_logo;
			((RecommendAdapter.DownloadItemInfo) tempInfo).m_name = recommendItemInfo.m_name;
		}
		if(recommendItemInfo != null){
			out.add(recommendItemInfo);
		}

		ArrayList<FrameExRes> resArr = FrameExResMgr2.getInstance().GetResArr();
		resArr.add(0, FrameExResMgr2.GetWhiteFrameRes());
		for (int i = 0; i < resArr.size(); i++) {
			FrameExRes res = resArr.get(i);
			tempInfo = new RecommendAdapter.ItemInfo();
			tempInfo.m_logo = res.m_thumb;
			tempInfo.m_name = res.m_name;
			tempInfo.m_uri = res.m_id;
			tempInfo.m_ex = res;
			if(i != 0) {
				tempInfo.m_canDrag = true;
			}
			switch(res.m_type)
			{
				case BaseRes.TYPE_NETWORK_URL:
					tempInfo.m_style = RecommendAdapter.ItemInfo.Style.NEED_DOWNLOAD;
					break;
				case BaseRes.TYPE_LOCAL_RES:
				case BaseRes.TYPE_LOCAL_PATH:
				default:
					tempInfo.m_style = RecommendAdapter.ItemInfo.Style.NORMAL;
					break;
			}
			if(FrameExResMgr2.getInstance().IsNewRes(res.m_id))
			{
				tempInfo.m_style = RecommendAdapter.ItemInfo.Style.NEW;
			}
			out.add(tempInfo);
		}

		return out;
	}

	public static ArrayList<ColorItemInfo> GetColorPaletteArr()
	{
		int[][] ress = {{0xFFFFFFFF, 0xFFFFFFFF}, {0xffd8ece1, 0xffd8ece1}, {0xfffcf5d9, 0xfffcf5d9}, {0xffcfebef, 0xffcfebef}, {0xfffde9e8, 0xfffde9e8}, {0xfff8c6d2, 0xfff8c6d2}, {0xffe0eef7, 0xffe0eef7}, {0xffa0cbee, 0xffa0cbee}, {0xffeacee1, 0xffeacee1}, {0xfff6bfbc, 0xfff6bfbc}, {0xff8ca7de, 0xff8ca7de}, {0xff7d9fc1, 0xff7d9fc1}, {0xffae6966, 0xffae6966}, {0xffe2ba5e, 0xffe2ba5e}, {0xffaa9083, 0xffaa9083}, {0xff95969c, 0xff95969c}, {0xff000000, 0xff000000}};

		ArrayList<ColorItemInfo> out = new ArrayList<ColorItemInfo>();
		ColorItemInfo item;

		int len = ress.length;
		for(int i = 0; i < len; i++)
		{
			item = new ColorItemInfo();
			item.m_id = i;
			item.m_position = i;
			item.m_color = ress[i][0];
			item.m_pre_Color = ress[i][1];
			out.add(item);
		}

		return out;
	}

	public static int GetThemeFirstUri(ArrayList<RecommendExAdapter.ItemInfo> arr)
	{
		int out = 0;

		if(arr != null)
		{
			for(RecommendExAdapter.ItemInfo info : arr)
			{
				if(info.m_uri != RecommendExAdapter.DownloadItemInfo.DOWNLOAD_ITEM_URI && info.m_uri != RecommendExAdapter.RecommendItemInfo.RECOMMEND_ITEM_URI)
				{
					if(info.m_uris != null && info.m_uris.length > 1)
					{
						out = info.m_uris[1];
						break;
					}
				}
			}
		}

		return out;
	}
}
