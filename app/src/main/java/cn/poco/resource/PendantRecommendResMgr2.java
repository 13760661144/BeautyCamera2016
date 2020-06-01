package cn.poco.resource;

import android.content.Context;

import java.util.ArrayList;

/**
 * Created by Raining on 2017/10/8.
 */

public class PendantRecommendResMgr2 extends BaseRecommendResMgr2
{
	protected final String CLOUD_CACHE_PATH = DownloadMgr.getInstance().RECOMMEND_PATH + "/pendant_cache.xxxx";
	protected final String CLOUD_URL = "http://beauty-material.adnonstop.com/API/beauty_camera/recommend/android.php?type=1&version=美人相机v4.0.0";

	private static PendantRecommendResMgr2 sInstance;

	private PendantRecommendResMgr2()
	{
	}

	public synchronized static PendantRecommendResMgr2 getInstance()
	{
		if(sInstance == null)
		{
			sInstance = new PendantRecommendResMgr2();
		}
		return sInstance;
	}

	@Override
	protected void BuildArr(ArrayList<RecommendRes> dst, ArrayList<ThemeRes> src)
	{
		if(dst != null && src != null)
		{
			RecommendRes res;
			ThemeRes theme;
			int len = dst.size();
			for(int i = 0; i < len; i++)
			{
				res = dst.get(i);
				theme = ResourceUtils.GetItem(src, res.m_id);
				if(theme != null)
				{
					res.m_name = theme.m_name;
				}
			}
		}
	}

	@Override
	protected String GetCloudUrl(Context context)
	{
		return CLOUD_URL;
	}

	@Override
	protected String GetCloudCachePath(Context context)
	{
		return CLOUD_CACHE_PATH;
	}
}
