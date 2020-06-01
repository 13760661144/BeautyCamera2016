package cn.poco.album.site;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

import cn.poco.framework.MyFramework;
import cn.poco.framework2.Framework2;

/**
 * Created by Raining on 2016/12/9.
 * 弹出,不复制图片
 */

public class AlbumSite3 extends AlbumSite
{
	@Override
	public void onPhotoSelected(Context context, Map<String, Object> params)
	{
		HashMap<String, Object> temp = new HashMap<String, Object>();
		temp.put("imgs", MakeRotationImg((String[])params.get("imgs"), false));
		temp.put("index", params.get("index"));
		temp.put("folder_name", params.get("folder_name"));
		MyFramework.SITE_ClosePopup(context, temp, Framework2.ANIM_NONE);
	}
}
