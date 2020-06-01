package cn.poco.album.site;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

import cn.poco.ad.site.ADPageSite;
import cn.poco.framework.MyFramework;
import cn.poco.framework2.Framework2;
import cn.poco.home.site.HomePageSite;

/**
 * Created by Raining on 2016/12/9.
 * 通用商业"选一张"
 */

public class AlbumSite4 extends AlbumSite
{
	@Override
	public void onPhotoSelected(Context context, Map<String, Object> params)
	{
		HashMap<String, Object> temp = new HashMap<String, Object>();
		HomePageSite.CloneBusinessParams(temp, m_inParams);
		temp.put("imgs", MakeRotationImg((String[])params.get("imgs"), true));
		temp.put("index", params.get("index"));
		temp.put("folder_name", params.get("folder_name"));
		MyFramework.SITE_Open(context, ADPageSite.class, temp, Framework2.ANIM_NONE);
	}
}
