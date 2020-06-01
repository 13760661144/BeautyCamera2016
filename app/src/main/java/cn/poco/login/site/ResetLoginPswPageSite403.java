package cn.poco.login.site;

import android.content.Context;
import android.graphics.Bitmap;

import java.util.HashMap;

import cn.poco.framework.FileCacheMgr;
import cn.poco.framework.MyFramework;
import cn.poco.framework2.Framework2;
import cn.poco.utils.Utils;

/**
 * Created by pocouser on 2018/1/29.
 */

public class ResetLoginPswPageSite403 extends ResetLoginPswPageSite
{
	@Override
	public void pre_reset(HashMap<String, Object> dates, Context context)
	{
		HashMap<String, Object> params = new HashMap<>();
		String path = FileCacheMgr.GetLinePath();
		Bitmap bmp = (Bitmap)dates.get("img");
		if(Utils.SaveTempImg(bmp, path))
		{
			params.put("img", path);
		}
		params.put("info", dates.get("info"));
		params.put("mode", dates.get("mode"));
		MyFramework.SITE_Open(context, ResetPswPageSite403.class, params, Framework2.ANIM_NONE);
	}
}
