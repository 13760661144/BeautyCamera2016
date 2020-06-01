package cn.poco.beautify;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;

import java.util.ArrayList;

import cn.poco.MaterialMgr2.MgrUtils;
import cn.poco.advanced.RecommendItemList;
import cn.poco.camera.RotationImg2;
import cn.poco.image.filter;
import cn.poco.makeup.makeup1.Makeup1ExAdapter;
import cn.poco.makeup.makeup_abs.AbsAlphaFrExAdapter;
import cn.poco.makeup.makeup_rl.MakeupRLAdapter;
import cn.poco.resource.BaseRes;
import cn.poco.resource.DecorateGroupRes;
import cn.poco.resource.LockRes;
import cn.poco.resource.LockResMgr2;
import cn.poco.resource.MakeupComboRecommendResMgr2;
import cn.poco.resource.MakeupComboResMgr2;
import cn.poco.resource.MakeupGroupRes;
import cn.poco.resource.MakeupRes;
import cn.poco.resource.MakeupResMgr2;
import cn.poco.resource.MakeupType;
import cn.poco.resource.RecommendRes;
import cn.poco.resource.ResourceUtils;
import cn.poco.setting.SettingInfoMgr;
import cn.poco.system.TagMgr;
import cn.poco.system.Tags;
import cn.poco.tianutils.MakeBmp;
import cn.poco.tianutils.SimpleBtnList;
import cn.poco.tsv.FastDynamicListV2;
import cn.poco.tsv.FastItemList;
import cn.poco.widget.recycle.RecommendExAdapter;
import my.beautyCamera.R;

public class BeautifyResMgr2
{
	public static final float[] EYELASH_UP_POS = new float[]{213, 112, 121, 66, 49, 104};
	public static final float[] EYELASH_DOWN_POS = new float[]{213, 112, 122, 127, 49, 104};
	public static final float[] EYELASH_ALL_POS = new float[]{213, 112, 121, 66, 49, 104, 122, 127};
	public static final float[] EYEBROW_POS = new float[]{204, 65, 90, 39, 37, 55};

	public static int GetDefColor(String uri)
	{
		int out;

		if(uri != null && uri.length() > 0)
		{
			out = GetDefColor(Integer.parseInt(uri));
		}
		else
		{
			out = GetDefColor(EffectType.EFFECT_DEFAULT);
		}

		return out;
	}

	public static int GetDefColor(int uri)
	{
		int out = EffectType.EFFECT_NEWBEE;

		switch(uri)
		{
			case EffectType.EFFECT_DEFAULT:
			case EffectType.EFFECT_NATURE:
				//case EffectType.EFFECT_CLEAR:
			case EffectType.EFFECT_MIDDLE:
			case EffectType.EFFECT_LITTLE:
				//case EffectType.EFFECT_ABS:
			case EffectType.EFFECT_MOON:
				//case EffectType.EFFECT_WB:
				//case EffectType.EFFECT_CANDY:
				//case EffectType.EFFECT_SEXY:
			case EffectType.EFFECT_USER:
			case EffectType.EFFECT_NONE:
				//case EffectType.EFFECT_COUNTRY:
				//case EffectType.EFFECT_SWEET:
			case EffectType.EFFECT_MOONLIGHT:
			case EffectType.EFFECT_NEWBEE:
				//case EffectType.EFFECT_AD55:
				//case EffectType.EFFECT_AD61:
			case EffectType.EFFECT_CLEAR:
			{
				out = uri;
				break;
			}
			default:
			{
				break;
			}
		}

		return out;
	}

	public static RotationImg2[] CloneRotationImgArr(RotationImg2[] src)
	{
		RotationImg2[] out = null;

		if(src != null)
		{
			out = new RotationImg2[src.length];
			for(int i = 0; i < src.length; i++)
			{
				out[i] = src[i].Clone();
			}
		}

		return out;
	}

	public static int GetBkBlurValue(Bitmap bmp)
	{
		int out = 1;

		if(bmp != null)
		{
			out = bmp.getWidth() / 10;
			if(out < 1)
			{
				out = 1;
			}
		}

		return out;
	}

	public static Bitmap MakeBkBmp(Bitmap bmp, int outW, int outH)
	{
		return MakeBkBmp(bmp, outW, outH, 0xB2f0f0f0);
	}

	public static Bitmap MakeBkBmp(Bitmap bmp, int outW, int outH, int fillColor)
	{
		Bitmap out = null;

		if(bmp != null)
		{
			out = MakeBmp.CreateBitmap(bmp, outW / 2, outH / 2, (float)outW / (float)outH, 0, Config.ARGB_8888);
			//out = MakeBmp.CreateFixBitmap(bmp, outW, outH, MakeBmp.POS_CENTER, 0, Config.ARGB_8888);
			//filter.largeRblurOpacity(out, 100, 0);
			//filter.fakeGlassBeauty(out, 0x90FFFFFF);
			filter.fakeGlassBeauty(out, fillColor);
			/*Canvas canvas = new Canvas(out);
			canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
			canvas.drawColor(fillColor);*/
		}

		return out;
	}

	public static int CompareASetSelOld(ArrayList<MakeupRes.MakeupData> datas, ArrayList<MakeupRes> src)
	{
		int out = -1;

		if(datas != null)
		{
			if(datas.size() <= 0)
			{
				out = 0;
			}
			else
			{
				MakeupRes temp;
				ArrayList<MakeupRes.MakeupData> list;
				int len = src.size();
				NEXT:
				for(int i = 0; i < len; i++)
				{
					list = (ArrayList<MakeupRes.MakeupData>)datas.clone();
					temp = src.get(i);
					if(temp == null)
					{
						continue NEXT;
					}

					for(int j = 0; j < temp.m_groupRes.length; j++)
					{
						if(!DeleteOneItem(list, temp.m_groupRes[j].m_id))
						{
							continue NEXT;
						}
					}

					if(list.size() <= 0)
					{
						out = i;
						break;
					}
				}
			}
		}

		return out;
	}

	/**
	 * @param datas
	 * @param aset
	 * @return [0]:存在于aset的index
	 * [1]:按照m_uris的索引输出
	 */
	public static int[] CompareASetSel(ArrayList<MakeupRes.MakeupData> datas, ArrayList<RecommendMakeupItemList.ItemInfo> aset)
	{
		int[] out = new int[]{-1, -1};
		int len2;
		if(datas != null && datas.size() > 0 && aset != null && (len2 = aset.size()) > 0)
		{
			Object temp;
			int index;
			for(int i = 0; i < len2; i++)
			{
				temp = aset.get(i).m_ex;
				if(temp != null && temp instanceof MakeupGroupRes)
				{
					index = CompareASetSelOld(datas, ((MakeupGroupRes)temp).m_group);
					if(index >= 0)
					{
						out[0] = i;
						out[1] = index + 1; //按照m_uris的索引输出
						break;
					}
				}
			}
		}

		return out;
	}

	protected static boolean DeleteOneItem(ArrayList<MakeupRes.MakeupData> list, int uri)
	{
		boolean out = false;

		MakeupRes.MakeupData item;
		int len = list.size();
		for(int i = 0; i < len; i++)
		{
			item = list.get(i);
			if(item.m_id == uri)
			{
				list.remove(i);
				i--;
				len--;
				out = true;
			}
		}

		return out;
	}

	public static int GetInsertIndex(ArrayList<MakeupRes.MakeupData> src, MakeupType type)
	{
		int out = -1;

		if(src != null && type != null)
		{
			int len = src.size();
			out = len;
			int typeValue = type.GetValue();
			for(int i = 0; i < len; i++)
			{
				if(src.get(i).m_makeupType > typeValue)
				{
					out = i;
					break;
				}
			}
		}

		return out;
	}

	public static ArrayList<FastItemList.ItemInfo> GetUIRes(MakeupType type)
	{
		ArrayList<FastItemList.ItemInfo> out = new ArrayList<FastItemList.ItemInfo>();

		ArrayList<MakeupRes> arr = GetRes(type,false);
		if(arr != null)
		{
			int len = arr.size();
			FastItemList.ItemInfo temp;
			MakeupRes temp2;
			for(int i = 0; i < len; i++)
			{
				temp2 = arr.get(i);

				temp = new FastItemList.ItemInfo();
				temp.m_ex = temp2;
				temp.m_logo = temp2.m_thumb;
				temp.m_uri = temp2.m_id;
				temp.m_name = temp2.m_name;
				out.add(temp);
			}
		}

		return out;
	}

	public static ArrayList<RecommendMakeupItemList.ItemInfo> GetAsetRes(Context context, RecommendMakeupItemConfig config)
	{
		ArrayList<RecommendMakeupItemList.ItemInfo> out = new ArrayList<RecommendMakeupItemList.ItemInfo>();

		RecommendMakeupItemList.ItemInfo tempInfo;
		Object[] RecomendLogos = null;
		String[] RecomendNames = null;
		ArrayList<RecommendRes> recommendResArr = null;
		//推荐位
		boolean hasRecommend = false;
		int recommendColor = 0;
		ArrayList<RecommendRes> tempArr = MakeupComboRecommendResMgr2.getInstance().sync_ar_GetCloudCacheRes(context, null);
		if(tempArr != null && tempArr.size() > 0)
		{
			recommendResArr = new ArrayList<>();
			int len = tempArr.size();
			RecommendRes temp;
			for(int i = 0; i < len; i++)
			{
				temp = tempArr.get(i);
				if(MgrUtils.hasDownloadMakeup(context, temp.m_id) == 0 && TagMgr.CheckTag(context, Tags.BEAUTY_RECOMMEND_MAKEUPCOMBO + temp.m_id))
				{
					recommendResArr.add(temp);
				}
			}
			len = recommendResArr.size();
			if(len > 0)
			{
				RecomendLogos = new Object[len];
				RecomendNames = new String[len];
				int[] bkColors = new int[len];
				for(int i = 0; i < len; i++)
				{
					temp = recommendResArr.get(i);
					RecomendLogos[i] = temp.m_thumb;
					RecomendNames[i] = temp.m_name;
					bkColors[i] = temp.m_bkColor;
				}
				recommendColor = bkColors[0];
//				tempInfo = new RecommendMakeupItemList.RecommendItemInfo(config);
//				tempInfo.m_ex = recommendResArr;
//				((RecommendMakeupItemList.RecommendItemInfo)tempInfo).SetLogos(RecomendLogos, RecomendNames, bkColors);
//				out.add(tempInfo);
				hasRecommend = true;
			}
		}

//		//下载
//		tempInfo = new RecommendMakeupItemList.DownloadItemInfo(config);
//		((RecommendMakeupItemList.DownloadItemInfo)tempInfo).SetRecommend(hasRecommend);
//		out.add(tempInfo);

		//下载&推荐按钮
		tempInfo = new RecommendMakeupItemList.DownloadAndRecommendItemInfo((Activity) context, config);
		if(hasRecommend)
		{
			((RecommendMakeupItemList.DownloadAndRecommendItemInfo)tempInfo).SetLogos(RecomendLogos, RecomendNames, 0xffffffff);
			((RecommendMakeupItemList.DownloadAndRecommendItemInfo)tempInfo).m_ex = recommendResArr;
		}
		((RecommendMakeupItemList.DownloadAndRecommendItemInfo)tempInfo).SetNum(MakeupComboResMgr2.getInstance().GetNoDownloadCount(context));
		out.add(tempInfo);

		//无
		tempInfo = new RecommendMakeupItemList.NullItemInfo10(config);
		out.add(tempInfo);

		//额外推荐位
		if(hasRecommend)
		{
			tempInfo = new RecommendMakeupItemList.RecommendExItemInfo(config);
			((RecommendMakeupItemList.RecommendExItemInfo)tempInfo).SetLogos(RecomendLogos, RecomendNames, recommendColor);
			((RecommendMakeupItemList.RecommendExItemInfo)tempInfo).m_ex = recommendResArr;
			out.add(tempInfo);
		}

		ArrayList<MakeupGroupRes> arr = MakeupComboResMgr2.getInstance().GetGroupResArr();
		if(arr != null)
		{
			int len = arr.size();
			MakeupGroupRes temp2;
			for(int i = 0; i < len; i++)
			{
				temp2 = arr.get(i);

				int len2 = 1;
				if(temp2.m_group != null)
				{
					len2 = temp2.m_group.size() + 1;
				}
				int[] uris = new int[len2];
				Object[] logos = new Object[len2];
				String[] names = new String[len2];
				uris[0] = temp2.m_id;
				logos[0] = temp2.m_thumb;
				names[0] = temp2.m_name;
				if(temp2.m_group != null)
				{
					MakeupRes temp3;
					for(int j = 1; j < len2; j++)
					{
						temp3 = temp2.m_group.get(j - 1);
						uris[j] = temp3.m_id;
						logos[j] = temp3.m_thumb2;
						names[j] = temp3.m_name;
					}
				}
				tempInfo = new RecommendMakeupItemList.ItemInfo10(config);
				tempInfo.SetData(uris, logos, names, temp2.m_maskColor, temp2);
				switch(temp2.m_type)
				{
					case BaseRes.TYPE_NETWORK_URL:
						tempInfo.m_style = RecommendMakeupItemList.ItemInfo.Style.NEED_DOWNLOAD;
						break;
					case BaseRes.TYPE_LOCAL_RES:
					case BaseRes.TYPE_LOCAL_PATH:
					default:
						tempInfo.m_style = RecommendMakeupItemList.ItemInfo.Style.NORMAL;
						break;
				}
				if(MakeupComboResMgr2.getInstance().IsNewRes(temp2.m_id))
				{
					tempInfo.m_style = RecommendMakeupItemList.ItemInfo.Style.NEW;
				}
				//判断主题id对应的彩妆组合是否被锁
				if(LockResMgr2.getInstance().m_unlockCaiZhuang != LockRes.SHARE_TYPE_NONE && ((temp2.m_id == AsetUnlock.ASET_LOCK_URI1 && TagMgr.CheckTag(context, Tags.BEAUTY_ASET_LOCK1)) || (temp2.m_id == AsetUnlock.ASET_LOCK_URI2 && TagMgr.CheckTag(context, Tags.BEAUTY_ASET_LOCK2))))
				{
					tempInfo.m_isLock2 = true;
				}
				out.add(tempInfo);
			}
		}

		/*long time = System.currentTimeMillis();
		if(!(BeautifyPage2.s_ad43STime < time && time < BeautifyPage2.s_ad43ETime))
		{
			int len = out.size();
			for(int i = 0; i < len; i++)
			{
				tempInfo = out.get(i);
				if(tempInfo.m_ids != null && tempInfo.m_ids.length > 1)
				{
					if(BaseResMgr.HasId(BeautifyPage2.s_ad43AsetIDs, tempInfo.m_uris[1]) > -1)
					{
						out.remove(i);
						i--;
						len--;
					}
				}
			}
		}*/

		return out;
	}


	public static ArrayList<AbsAlphaFrExAdapter.ItemInfo> GetAsetRes1(Context context)
	{
		ArrayList<AbsAlphaFrExAdapter.ItemInfo> out = new ArrayList<>();

		AbsAlphaFrExAdapter.ItemInfo tempInfo;
		int[] RecomendUris = null;
		Object[] RecomendLogos = null;
		String[] RecomendNames = null;
		ArrayList<RecommendRes> recommendResArr = null;
		//推荐位
		boolean hasRecommend = false;
		int recommendColor = 0;
		ArrayList<RecommendRes> tempArr = MakeupComboRecommendResMgr2.getInstance().sync_ar_GetCloudCacheRes(context, null);
		if(tempArr != null && tempArr.size() > 0)
		{
			recommendResArr = new ArrayList<>();
			int len = tempArr.size();
			RecommendRes temp;
			for(int i = 0; i < len; i++)
			{
				temp = tempArr.get(i);
				if(MgrUtils.hasDownloadMakeup(context, temp.m_id) == 0 && TagMgr.CheckTag(context, Tags.BEAUTY_RECOMMEND_MAKEUPCOMBO + temp.m_id))
				{
					recommendResArr.add(temp);
				}
			}
			len = recommendResArr.size();
			if(len > 0)
			{
				RecomendUris = new int[len];
				RecomendLogos = new Object[len];
				RecomendNames = new String[len];
				int[] bkColors = new int[len];
				for(int i = 0; i < len; i++)
				{
					temp = recommendResArr.get(i);
					RecomendLogos[i] = temp.m_thumb;
					RecomendNames[i] = temp.m_name;
					bkColors[i] = temp.m_bkColor;
					RecomendUris[i] = temp.m_id;
				}
				recommendColor = bkColors[0];
//				tempInfo = new RecommendMakeupItemList.RecommendItemInfo(config);
//				tempInfo.m_ex = recommendResArr;
//				((RecommendMakeupItemList.RecommendItemInfo)tempInfo).SetLogos(RecomendLogos, RecomendNames, bkColors);
//				out.add(tempInfo);
				hasRecommend = true;
			}
		}

//		//下载
//		tempInfo = new RecommendMakeupItemList.DownloadItemInfo(config);
//		((RecommendMakeupItemList.DownloadItemInfo)tempInfo).SetRecommend(hasRecommend);
//		out.add(tempInfo);

		//下载&推荐按钮
		tempInfo = new Makeup1ExAdapter.DownloadItemInfo2();
		if(hasRecommend)
		{
			((Makeup1ExAdapter.DownloadItemInfo2)tempInfo).setData(RecomendLogos,RecomendNames,RecomendLogos.length);
			((Makeup1ExAdapter.DownloadItemInfo2)tempInfo).m_ex = recommendResArr;
		}
		((Makeup1ExAdapter.DownloadItemInfo2)tempInfo).setNum(MakeupComboResMgr2.getInstance().GetNoDownloadCount(context));
		out.add(tempInfo);

		//无
		tempInfo = new Makeup1ExAdapter.NullItemInfo();
		out.add(tempInfo);

		//额外推荐位
		if(hasRecommend)
		{
			tempInfo = new Makeup1ExAdapter.RecommendItemInfo();
			((Makeup1ExAdapter.RecommendItemInfo)tempInfo).setData(RecomendUris,RecomendLogos, RecomendNames, recommendColor);
			((Makeup1ExAdapter.RecommendItemInfo)tempInfo).m_ex = recommendResArr;
			out.add(tempInfo);
		}

		ArrayList<MakeupGroupRes> arr = MakeupComboResMgr2.getInstance().GetGroupResArr();
		if(arr != null)
		{
			int len = arr.size();
			MakeupGroupRes temp2;
			for(int i = 0; i < len; i++)
			{
				temp2 = arr.get(i);

				int len2 = 1;
				if(temp2.m_group != null)
				{
					len2 = temp2.m_group.size() + 1;
				}
				int[] uris = new int[len2];
				Object[] logos = new Object[len2];
				String[] names = new String[len2];
				uris[0] = temp2.m_id;
				logos[0] = temp2.m_thumb;
				names[0] = temp2.m_name;
				if(temp2.m_group != null)
				{
					MakeupRes temp3;
					for(int j = 1; j < len2; j++)
					{
						temp3 = temp2.m_group.get(j - 1);
						uris[j] = temp3.m_id;
						logos[j] = temp3.m_thumb2;
						names[j] = temp3.m_name;
					}
				}
				Makeup1ExAdapter.ItemInfo tempInfo1 = new Makeup1ExAdapter.ItemInfo();
				tempInfo1.m_maskColor = temp2.m_maskColor;
				tempInfo1.setData(uris, logos, names, temp2);
				switch(temp2.m_type)
				{
					case BaseRes.TYPE_NETWORK_URL:
						tempInfo1.m_style = RecommendExAdapter.ItemInfo.Style.NEED_DOWNLOAD;
						break;
					case BaseRes.TYPE_LOCAL_RES:
					case BaseRes.TYPE_LOCAL_PATH:
					default:
						tempInfo1.m_style = RecommendExAdapter.ItemInfo.Style.NORMAL;
						break;
				}
				if(MakeupComboResMgr2.getInstance().IsNewRes(temp2.m_id))
				{
					tempInfo1.m_style = RecommendExAdapter.ItemInfo.Style.NEW;
				}
				//判断主题id对应的彩妆组合是否被锁
				if(LockResMgr2.getInstance().m_unlockCaiZhuang != LockRes.SHARE_TYPE_NONE && ((temp2.m_id == AsetUnlock.ASET_LOCK_URI1 && TagMgr.CheckTag(context, Tags.BEAUTY_ASET_LOCK1)) || (temp2.m_id == AsetUnlock.ASET_LOCK_URI2 && TagMgr.CheckTag(context, Tags.BEAUTY_ASET_LOCK2))))
				{
					tempInfo1.m_isLock2 = true;
				}
				out.add(tempInfo1);
			}
		}

		/*long time = System.currentTimeMillis();
		if(!(BeautifyPage2.s_ad43STime < time && time < BeautifyPage2.s_ad43ETime))
		{
			int len = out.size();
			for(int i = 0; i < len; i++)
			{
				tempInfo = out.get(i);
				if(tempInfo.m_ids != null && tempInfo.m_ids.length > 1)
				{
					if(BaseResMgr.HasId(BeautifyPage2.s_ad43AsetIDs, tempInfo.m_uris[1]) > -1)
					{
						out.remove(i);
						i--;
						len--;
					}
				}
			}
		}*/

		return out;
	}

	public static ArrayList<FastDynamicListV2.ItemInfo> GetFilterRes()
	{
		ArrayList<FastDynamicListV2.ItemInfo> out = new ArrayList<FastDynamicListV2.ItemInfo>();

		FastDynamicListV2.ItemInfo temp;

		temp = new FastDynamicListV2.ItemInfo();
		temp.m_uri = FilterType.NONE;
		temp.m_name = "Original";
		temp.m_logo = R.drawable.filter_logo_original;
		temp.m_ex = 80;
		out.add(temp);

		temp = new FastDynamicListV2.ItemInfo();
		temp.m_uri = FilterType.JASMINE;
		temp.m_name = "Jasmine";
		temp.m_logo = R.drawable.filter_logo_jasmine;
		temp.m_ex = 80;
		out.add(temp);

		temp = new FastDynamicListV2.ItemInfo();
		temp.m_uri = FilterType.CAMILLIA;
		temp.m_name = "Camillia";
		temp.m_logo = R.drawable.filter_logo_camillia;
		temp.m_ex = 80;
		out.add(temp);

		temp = new FastDynamicListV2.ItemInfo();
		temp.m_uri = FilterType.ROSA;
		temp.m_name = "Rosa";
		temp.m_logo = R.drawable.filter_logo_rosa;
		temp.m_ex = 80;
		out.add(temp);

		temp = new FastDynamicListV2.ItemInfo();
		temp.m_uri = FilterType.LAVENDER;
		temp.m_name = "Lavender";
		temp.m_logo = R.drawable.filter_logo_lavender;
		temp.m_ex = 80;
		out.add(temp);

		temp = new FastDynamicListV2.ItemInfo();
		temp.m_uri = FilterType.SUNFLOWER;
		temp.m_name = "Sunflower";
		temp.m_logo = R.drawable.filter_logo_sunflower;
		temp.m_ex = 80;
		out.add(temp);

		temp = new FastDynamicListV2.ItemInfo();
		temp.m_uri = FilterType.CLOVER;
		temp.m_name = "Clover";
		temp.m_logo = R.drawable.filter_logo_clover;
		temp.m_ex = 80;
		out.add(temp);

		temp = new FastDynamicListV2.ItemInfo();
		temp.m_uri = FilterType.PEACH;
		temp.m_name = "Peach";
		temp.m_logo = R.drawable.filter_logo_peach;
		temp.m_ex = 80;
		out.add(temp);

		temp = new FastDynamicListV2.ItemInfo();
		temp.m_uri = FilterType.DANDELION;
		temp.m_name = "Dandelion";
		temp.m_logo = R.drawable.filter_logo_dandelion;
		temp.m_ex = 80;
		out.add(temp);

		temp = new FastDynamicListV2.ItemInfo();
		temp.m_uri = FilterType.LILAC;
		temp.m_name = "Lilac";
		temp.m_logo = R.drawable.filter_logo_lilac;
		temp.m_ex = 80;
		out.add(temp);

		temp = new FastDynamicListV2.ItemInfo();
		temp.m_uri = FilterType.TULIP;
		temp.m_name = "Tulip";
		temp.m_logo = R.drawable.filter_logo_tulip;
		temp.m_ex = 100;
		out.add(temp);

		return out;
	}

	/**
	 * @param list
	 * @param dst
	 * @return need reset? SetSelItemByUri(0);
	 */
	public static boolean UpdateList(RecommendMakeupItemList list, ArrayList<RecommendMakeupItemList.ItemInfo> dst)
	{
		boolean out = false;

		if(list != null && dst != null)
		{
			//重构排序
			//int[] orders = new int[dst.size() + 2];
			//orders[0] = MakeupItemList.DOWNLOAD_ITEM_URI;
			//orders[1] = MakeupItemList.NULL_ITEM_URI;
			int[] orders = new int[dst.size()];
			int len = dst.size();
			for(int i = 0; i < len; i++)
			{
				//orders[i + 2] = dst.get(i).m_uris[0];
				orders[i] = dst.get(i).m_uris[0];
			}

			ArrayList<RecommendMakeupItemList.ItemInfo> src = (ArrayList<RecommendMakeupItemList.ItemInfo>)list.GetResData();

			int insertIndex = RecommendMakeupItemList.GetSubIndexByUri(src, RecommendMakeupItemList.NullItemInfo.NULL_ITEM_URI)[0];
			insertIndex++;

			RecommendMakeupItemList.ItemInfo temp;
			NEXT:
			for(int i = 0; i < src.size(); i++)
			{
				temp = src.get(i);
				for(int j = 0; j < dst.size(); j++)
				{
					if(temp.m_uris[0] == dst.get(j).m_uris[0])
					{
						src.remove(i);
						dst.remove(j);
						i--;
						continue NEXT;
					}
				}
			}

			//删除多余
			int oldSel = list.GetSelectIndex();
			int delIndex;
			len = src.size();
			for(int i = 0; i < len; i++)
			{
				delIndex = list.DeleteItemByUri(src.get(i).m_uris[0]);
				if(oldSel >= 0 && oldSel == delIndex)
				{
					out = true;
				}
			}

			//添加新增
			len = dst.size();
			for(int i = 0; i < len; i++)
			{
				list.AddGroupItem(insertIndex, dst.get(i));
			}

			list.UpdateOrder(orders);
		}

		return out;
	}

	public static int GetSelIndex(ArrayList<MakeupRes.MakeupData> dst, ArrayList<FastItemList.ItemInfo> src)
	{
		int out = -1;

		if(dst != null && src != null && src.size() > 0)
		{
			out = 0;
			int count = 0; //取匹配个数最多的
			MakeupRes temp;
			int srcLen = src.size();
			NEXT:
			for(int i = 0; i < srcLen; i++)
			{
				temp = (MakeupRes)src.get(i).m_ex;
				if(temp != null && temp.m_groupRes != null)
				{
					for(int j = 0; j < temp.m_groupRes.length; j++)
					{
						if(ResourceUtils.HasItem(dst, temp.m_groupRes[j].m_id) < 0)
						{
							continue NEXT;
						}
					}
					if(temp.m_groupRes.length > count)
					{
						count = temp.m_groupRes.length;
						out = i;
					}
				}
			}
		}

		return out;
	}


	public static int GetSelIndexForMakeup(ArrayList<MakeupRes.MakeupData> dst, ArrayList<RecommendItemList.ItemInfo> src)
	{
		int out = -1;

		if(dst != null && src != null && src.size() > 0)
		{
			out = 0;
			int count = 0; //取匹配个数最多的
			MakeupRes temp;
			int srcLen = src.size();
			NEXT:
			for(int i = 0; i < srcLen; i++)
			{
				temp = (MakeupRes)src.get(i).m_ex;
				if(temp != null && temp.m_groupRes != null)
				{
					for(int j = 0; j < temp.m_groupRes.length; j++)
					{
						if(ResourceUtils.HasItem(dst, temp.m_groupRes[j].m_id) < 0)
						{
							continue NEXT;
						}
					}
					if(temp.m_groupRes.length > count)
					{
						count = temp.m_groupRes.length;
						out = i;
					}
				}
			}
		}

		return out;
	}


	public static int GetSelIndexForMakeup1(ArrayList<MakeupRes.MakeupData> dst, ArrayList<MakeupRLAdapter.ItemInfo> src)
	{
		int out = -1;

		if(dst != null && src != null && src.size() > 0)
		{
			int count = 0; //取匹配个数最多的
			MakeupRes temp;
			int srcLen = src.size();
			NEXT:
			for(int i = 0; i < srcLen; i++)
			{
				temp = (MakeupRes)src.get(i).m_ex;
				if(temp != null && temp.m_groupRes != null)
				{
					for(int j = 0; j < temp.m_groupRes.length; j++)
					{
						if(ResourceUtils.HasItem(dst, temp.m_groupRes[j].m_id) < 0)
						{
							continue NEXT;
						}
					}
					if(temp.m_groupRes.length > count)
					{
						count = temp.m_groupRes.length;
						out = i;
					}
				}
			}
		}

		return out;
	}


	public static ArrayList<MakeupRes> GetRes(MakeupType type,boolean hasBussiness)
	{
		ArrayList<MakeupRes> out = new ArrayList<MakeupRes>();

		switch(type)
		{
			case CHEEK_L:
				out.addAll(GetCheekRes(hasBussiness));
				break;

			case EYE_L:
				out.addAll(GetEyeRes(hasBussiness));
				break;
			case EYEBROW_L:
				out.addAll(GetEyebrowRes(hasBussiness));
				break;
			case EYELASH_UP_L:
			case EYELASH_DOWN_L:
				out.addAll(GetEyelashRes(hasBussiness));
				break;
			case LIP:
				out.addAll(GetLipRes(hasBussiness));
				break;
			case EYELINER_UP_L:
				out.addAll(GetEyelineRes(hasBussiness));
				break;
			case KOHL_L:
				out.addAll(GetKohlRes(hasBussiness));
				break;
			case FOUNDATION:
				out.addAll(GetFoundationRes(hasBussiness));
				break;

			//case ASET:
			//	out.addAll(GetMakeupComboRes());
			//	break;

			default:
				break;
		}

		return out;
	}

	public static int DelType(ArrayList<MakeupRes.MakeupData> src, MakeupType type)
	{
		int out = -1;

		if(src != null && type != null)
		{
			int typeValue = type.GetValue();
			int len = src.size();
			for(int i = 0; i < len; i++)
			{
				if(src.get(i).m_makeupType == typeValue)
				{
					out = i;
					src.remove(i);
					i--;
					len--;
				}
			}
		}

		return out;
	}

	public static ArrayList<MakeupRes> GetEyeRes(boolean hasBussiness)
	{
		ArrayList<MakeupRes> out = new ArrayList<>();

		MakeupRes item;
		MakeupRes.MakeupData res;

		item = new MakeupRes();
		{
			item.m_id = 0;
			item.m_thumb = R.drawable.photofactory_sp_null;
			item.m_groupRes = new MakeupRes.MakeupData[1];

			res = new MakeupRes.MakeupData();
			res.m_id = 0;
			res.m_thumb = R.drawable.photofactory_sp_null;
			res.m_makeupType = MakeupType.EYE_L.GetValue();
			item.m_groupRes[0] = res;
		}
		out.add(item);

		out.addAll(MakeupResMgr2.getInstance().GetLocalResArr(MakeupType.EYE_L.GetValue(), hasBussiness));

		/*long time = System.currentTimeMillis();
		if(!(BeautifyPage2.s_ad43STime < time && time < BeautifyPage2.s_ad43ETime))
		{
			int len = out.size();
			for(int i = 0; i < len; i++)
			{
				item = out.get(i);
				if(item.m_groupRes != null && item.m_groupRes.length > 0)
				{
					if(BaseResMgr.HasId(BeautifyPage2.s_ad43EyeIDs, item.m_groupRes[0].m_id) > -1)
					{
						out.remove(i);
						i--;
						len--;
					}
				}
			}
		}*/

		return out;
	}

	public static ArrayList<MakeupRes> GetEyelashRes(boolean isHasBussiness)
	{
		ArrayList<MakeupRes> out = new ArrayList<MakeupRes>();

		MakeupRes item;
		MakeupRes.MakeupData res;

		item = new MakeupRes();
		{
			item.m_id = 0;
			item.m_thumb = R.drawable.photofactory_sp_null;
			item.m_groupRes = new MakeupRes.MakeupData[1];

			res = new MakeupRes.MakeupData();
			res.m_id = 0;
			res.m_thumb = R.drawable.photofactory_sp_null;
			res.m_makeupType = MakeupType.EYELASH_UP_L.GetValue();
			res.m_defAlpha = 60;
			res.m_pos = EYELASH_UP_POS;
			res.m_ex = 1;
			item.m_groupRes[0] = res;
		}
		out.add(item);

		out.addAll(MakeupResMgr2.getInstance().GetLocalResArr(MakeupType.EYELASH_DOWN_L.GetValue() | MakeupType.EYELASH_UP_L.GetValue(), isHasBussiness));

		return out;
	}

	public static ArrayList<MakeupRes> GetKohlRes(boolean isHasBussiness)
	{
		ArrayList<MakeupRes> out = new ArrayList<MakeupRes>();

		MakeupRes item;
		MakeupRes.MakeupData res;

		item = new MakeupRes();
		{
			item.m_id = 0;
			item.m_thumb = R.drawable.photofactory_sp_null;
			item.m_groupRes = new MakeupRes.MakeupData[1];

			res = new MakeupRes.MakeupData();
			res.m_id = 0;
			res.m_thumb = R.drawable.photofactory_sp_null;
			res.m_makeupType = MakeupType.KOHL_L.GetValue();
			res.m_defAlpha = 60;
			res.m_ex = 38;
			res.m_pos = EYELASH_ALL_POS;
			item.m_groupRes[0] = res;
		}
		out.add(item);

		out.addAll(MakeupResMgr2.getInstance().GetLocalResArr(MakeupType.KOHL_L.GetValue(), isHasBussiness));

		return out;
	}

	public static ArrayList<MakeupRes> GetEyelineRes(boolean isHasBussiness)
	{
		ArrayList<MakeupRes> out = new ArrayList<MakeupRes>();

		MakeupRes item;
		MakeupRes.MakeupData res;

		item = new MakeupRes();
		{
			item.m_id = 0;
			item.m_thumb = R.drawable.photofactory_sp_null;
			item.m_groupRes = new MakeupRes.MakeupData[1];

			res = new MakeupRes.MakeupData();
			res.m_id = 0;
			res.m_thumb = R.drawable.photofactory_sp_null;
			res.m_makeupType = MakeupType.EYELINER_UP_L.GetValue();
			res.m_defAlpha = 60;
			res.m_pos = EYELASH_UP_POS;
			item.m_groupRes[0] = res;
		}
		out.add(item);

		out.addAll(MakeupResMgr2.getInstance().GetLocalResArr(MakeupType.EYELINER_DOWN_L.GetValue() | MakeupType.EYELINER_UP_L.GetValue(), isHasBussiness));

		return out;
	}

	public static ArrayList<MakeupRes> GetEyebrowRes(boolean isHasBussiness)
	{
		ArrayList<MakeupRes> out = new ArrayList<MakeupRes>();

		MakeupRes item;
		MakeupRes.MakeupData res;

		item = new MakeupRes();
		{
			item.m_id = 0;
			item.m_thumb = R.drawable.photofactory_sp_null;
			item.m_groupRes = new MakeupRes.MakeupData[1];

			res = new MakeupRes.MakeupData();
			res.m_id = 0;
			res.m_thumb = R.drawable.photofactory_sp_null;
			res.m_makeupType = MakeupType.EYEBROW_L.GetValue();
			res.m_defAlpha = 60;
			res.m_pos = EYEBROW_POS;
			item.m_groupRes[0] = res;
		}
		out.add(item);

		out.addAll(MakeupResMgr2.getInstance().GetLocalResArr(MakeupType.EYEBROW_L.GetValue(), isHasBussiness));

		return out;
	}

	public static ArrayList<MakeupRes> GetFoundationRes(boolean isHasBussiness)
	{
		ArrayList<MakeupRes> out = new ArrayList<MakeupRes>();

		MakeupRes item;
		MakeupRes.MakeupData res;

		item = new MakeupRes();
		{
			item.m_id = 0;
			item.m_thumb = R.drawable.photofactory_sp_null;
			item.m_groupRes = new MakeupRes.MakeupData[1];

			res = new MakeupRes.MakeupData();
			res.m_id = 0;
			res.m_thumb = R.drawable.photofactory_sp_null;
			res.m_makeupType = MakeupType.FOUNDATION.GetValue();
			res.m_defAlpha = 60;
			item.m_groupRes[0] = res;
		}
		out.add(item);

		out.addAll(MakeupResMgr2.getInstance().GetLocalResArr(MakeupType.FOUNDATION.GetValue(), isHasBussiness));

		return out;
	}

	public static ArrayList<MakeupRes> GetLipRes(boolean isHasBussiness)
	{
		ArrayList<MakeupRes> out = new ArrayList<MakeupRes>();

		MakeupRes item;
		MakeupRes.MakeupData res;

		item = new MakeupRes();
		{
			item.m_id = 0;
			item.m_thumb = R.drawable.photofactory_sp_null;
			item.m_groupRes = new MakeupRes.MakeupData[1];

			res = new MakeupRes.MakeupData();
			res.m_id = 0;
			res.m_thumb = R.drawable.photofactory_sp_null;
			res.m_makeupType = MakeupType.LIP.GetValue();
			res.m_defAlpha = 60;
			item.m_groupRes[0] = res;
		}
		out.add(item);

		out.addAll(MakeupResMgr2.getInstance().GetLocalResArr(MakeupType.LIP.GetValue(), isHasBussiness));

		return out;
	}

	public static ArrayList<MakeupRes> GetCheekRes(boolean isHasBussiness)
	{
		ArrayList<MakeupRes> out = new ArrayList<MakeupRes>();

		MakeupRes item;
		MakeupRes.MakeupData res;

		item = new MakeupRes();
		{
			item.m_id = 0;
			item.m_thumb = R.drawable.photofactory_sp_null;
			item.m_groupRes = new MakeupRes.MakeupData[1];

			res = new MakeupRes.MakeupData();
			res.m_id = 0;
			res.m_thumb = R.drawable.photofactory_sp_null;
			res.m_makeupType = MakeupType.CHEEK_L.GetValue();
			res.m_defAlpha = 100;
			item.m_groupRes[0] = res;
		}
		out.add(item);

		out.addAll(MakeupResMgr2.getInstance().GetLocalResArr(MakeupType.CHEEK_L.GetValue(), isHasBussiness));

		return out;
	}

	public static ArrayList<SimpleBtnList.ItemInfo> GetDecorateTagInfoList(ArrayList<DecorateGroupRes> arr)
	{
		ArrayList<SimpleBtnList.ItemInfo> out = new ArrayList<>();

		if(arr != null)
		{
			DecorateGroupRes res;
			int len = arr.size();
			for(int i = 0; i < len; i++)
			{
				res = arr.get(i);
				out.add(new SimpleBtnList.ItemInfo(res.m_id, res.m_name));
			}
		}

		return out;
	}

	public static int getColorTrans(Context context, int id)
	{
		if(SettingInfoMgr.GetSettingInfo(context).GetLastSaveColor())
		{
			String alpha = TagMgr.GetTagValue(context, Tags.BEAUTY_COLOR_ALPHA + id);
			if(alpha != null && alpha.length() > 0)
			{
				return Integer.parseInt(alpha);
			}
		}

		if(id == EffectType.EFFECT_LITTLE || id == EffectType.EFFECT_USER || id == EffectType.EFFECT_NONE)
		{
			return 100;
		}
		else if(id == EffectType.EFFECT_NEWBEE)
		{
			return 90;
		} else if (id == EffectType.EFFECT_CLEAR) {
			return 55;
		}
		/*else if(id == EffectType.EFFECT_AD55 || id == EffectType.EFFECT_AD61)
		{
			return 60;
		}*/
		else {
			return 70;
		}
	}
}
