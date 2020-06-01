package cn.poco.album.site;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

import cn.poco.beautify4.site.Beautify4PageSite8;
import cn.poco.framework.MyFramework;
import cn.poco.framework2.Framework2;

/**
 * Created by Raining on 2016/12/9.
 * 主页 美化入口选图片
 */

public class AlbumSite5 extends AlbumSite
{
	@Override
	public void onPhotoSelected(Context context, Map<String, Object> params)
	{
		HashMap<String, Object> temp = (HashMap<String, Object>) m_inParams.clone();
		temp.put("imgs", MakeRotationImg((String[])params.get("imgs"), true));
		temp.put("index", params.get("index"));
		temp.put("folder_name", params.get("folder_name"));
		temp.put("show_exit_dialog", false);
		MyFramework.SITE_Open(context, Beautify4PageSite8.class, temp, Framework2.ANIM_NONE);
	}

	@Override
	public void onBack(Context context)
	{
		MyFramework.SITE_Back(context, null, Framework2.ANIM_TRANSLATION_TOP);
	}
}
