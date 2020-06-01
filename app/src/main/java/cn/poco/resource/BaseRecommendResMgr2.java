package cn.poco.resource;

import android.content.Context;

import com.adnonstop.resourcelibs.DataFilter;

import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

import cn.poco.framework.EventID;

/**
 * Created by Raining on 2017/10/8.
 */

public abstract class BaseRecommendResMgr2 extends BaseResMgr<RecommendRes, ArrayList<RecommendRes>>
{
	@Override
	public int GetResArrSize(ArrayList<RecommendRes> arr)
	{
		return arr.size();
	}

	@Override
	public ArrayList<RecommendRes> MakeResArrObj()
	{
		return new ArrayList<>();
	}

	@Override
	public boolean ResArrAddItem(ArrayList<RecommendRes> arr, RecommendRes item)
	{
		return arr.add(item);
	}

	@Override
	protected ArrayList<RecommendRes> sync_raw_GetLocalRes(Context context, DataFilter filter)
	{
		return null;
	}

	@Override
	protected void sync_raw_SaveSdcardRes(Context context, ArrayList<RecommendRes> arr)
	{
	}

	@Override
	protected int GetCloudEventId()
	{
		return EventID.RECOMMEND_CLOUD_OK;
	}

	@Override
	protected RecommendRes ReadResItem(JSONObject jsonObj, boolean isPath)
	{
		RecommendRes out = null;

		if(jsonObj != null)
		{
			try
			{
				out = new RecommendRes();
				String temp = jsonObj.getString("theme_id");
				if(temp != null && temp.length() > 0)
				{
					out.m_id = Integer.parseInt(temp);
				}
				out.m_showContent = jsonObj.getString("content");
				out.url_thumb = jsonObj.getString("pic");
				out.url_showImg = jsonObj.getString("preview_pic");
			}
			catch(Throwable e)
			{
				e.printStackTrace();
				out = null;
			}
		}

		return out;
	}

	@Override
	protected String GetSdcardPath(Context context)
	{
		return null;
	}

	@Override
	public RecommendRes GetItem(ArrayList<RecommendRes> arr, int id)
	{
		return ResourceUtils.GetItem(arr, id);
	}

	@Override
	public ArrayList<RecommendRes> sync_GetCloudCacheRes(Context context, DataFilter filter)
	{
		ArrayList<RecommendRes> arr = mCloudResArr;
		ArrayList<RecommendRes> arr2 = super.sync_GetCloudCacheRes(context, filter);

		synchronized(CLOUD_MEM_LOCK)
		{
			if(arr != arr2 && arr2 != null)
			{
				ArrayList<ThemeRes> themes = ThemeResMgr2.getInstance().GetAllResArr();
				BuildArr(arr2, themes);
				for(RecommendRes res : arr2)
				{
					DownloadMgr.FastDownloadRes(res, false);
					if(res.m_type == BaseRes.TYPE_NETWORK_URL)
					{
						DownloadMgr.getInstance().DownloadRes(res, null);
					}
				}
			}
		}

		return arr2;
	}

	@Override
	protected void sync_last_GetCloudRes(Context context, DataFilter filter, boolean justSave, ArrayList<RecommendRes> result)
	{
		super.sync_last_GetCloudRes(context, filter, justSave, result);

		if(result != null && result.size() > 0)
		{
			ArrayList<ThemeRes> themes = ThemeResMgr2.getInstance().GetAllResArr();
			BuildArr(result, themes);
			for(RecommendRes res : result)
			{
				DownloadMgr.getInstance().SyncDownloadRes(res, false);
			}
		}
	}

	@Override
	protected int GetNewJsonVer()
	{
		return 0;
	}

	@Override
	protected int GetNewOrderJsonVer()
	{
		return 0;
	}

	protected abstract void BuildArr(ArrayList<RecommendRes> dst, ArrayList<ThemeRes> src);

	@Override
	protected void RebuildNetResArr(ArrayList<RecommendRes> dst, ArrayList<RecommendRes> src)
	{
		if(dst != null && src != null)
		{
			RecommendRes srcTemp;
			RecommendRes dstTemp;
			Class cls = RecommendRes.class;
			Field[] fields = cls.getDeclaredFields();
			int index;
			int len = dst.size();
			for(int i = 0; i < len; i++)
			{
				dstTemp = dst.get(i);
				index = ResourceUtils.HasItem(src, dstTemp.m_id);
				if(index >= 0)
				{
					srcTemp = src.get(index);
					dstTemp.m_type = srcTemp.m_type;
					dstTemp.m_thumb = srcTemp.m_thumb;
					dstTemp.m_showImg = srcTemp.m_showImg;

					for(Field field : fields)
					{
						try
						{
							if(!Modifier.isFinal(field.getModifiers()))
							{
								Object value = field.get(dstTemp);
								field.set(srcTemp, value);
							}
						}
						catch(Throwable e2)
						{
							e2.printStackTrace();
						}
					}
					dst.set(i, srcTemp);
				}
			}
		}
	}
}
