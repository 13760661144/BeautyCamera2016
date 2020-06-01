package cn.poco.album.site;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

import cn.poco.beautify4.site.Beautify4PageSite4;
import cn.poco.framework.MyFramework;
import cn.poco.framework2.Framework2;
import cn.poco.home.site.HomePageSite;

/**
 * Created by Raining on 2016/12/29.
 * 分享美化下一张
 */

public class AlbumSite25 extends AlbumSite
{
	@Override
	public void onBack(Context context)
	{
		MyFramework.SITE_Open(context, true, HomePageSite.class, null, Framework2.ANIM_NONE);
	}

	@Override
	public void onPhotoSelected(Context context, Map<String, Object> params)
	{
		HashMap<String, Object> temp = new HashMap<>();
		temp.put("imgs", MakeRotationImg((String[])params.get("imgs"), true));
		temp.put("index", params.get("index"));
		temp.put("folder_name", params.get("folder_name"));
		MyFramework.SITE_OpenAndClosePopup(context, Beautify4PageSite4.class, temp, Framework2.ANIM_NONE);
	}
}
