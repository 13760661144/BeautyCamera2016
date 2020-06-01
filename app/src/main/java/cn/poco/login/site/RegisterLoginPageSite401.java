package cn.poco.login.site;

import android.content.Context;
import android.graphics.Bitmap;

import java.util.HashMap;

import cn.poco.framework.FileCacheMgr;
import cn.poco.framework.MyFramework;
import cn.poco.framework2.Framework2;
import cn.poco.utils.Utils;

/**
 * Created by Raining on 2017/11/16.
 * 独立登录Activity调用
 * {@link RegisterLoginPageSite}
 */

public class RegisterLoginPageSite401 extends RegisterLoginPageSite
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
		MyFramework.SITE_Open(context, RegisterLoginInfoPageSite401.class, params, Framework2.ANIM_NONE);
	}
}
