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

public class RegisterLoginPageSite403 extends RegisterLoginPageSite
{
	@Override
	public void verify_Code(HashMap<String, Object> datas, Context context)
	{
		HashMap<String ,Object> params = new HashMap<>();
		Bitmap bmp = (Bitmap) datas.get("img");
		String path = FileCacheMgr.GetLinePath();
		if(Utils.SaveTempImg(bmp, path))
		{
			params.put("img",path);
		}
		params.put("info",datas.get("info"));
		MyFramework.SITE_Open(context, RegisterLoginInfoPageSite403.class, params, Framework2.ANIM_NONE);
	}
}
