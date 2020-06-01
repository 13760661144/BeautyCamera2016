package cn.poco.login.site;

import android.content.Context;
import android.graphics.Bitmap;

import java.util.HashMap;

import cn.poco.framework.FileCacheMgr;
import cn.poco.framework.MyFramework;
import cn.poco.framework2.Framework2;
import cn.poco.utils.Utils;

/**
 * 社区直接进入注册的验证手机
 */
public class RegisterLoginPageSite402 extends RegisterLoginPageSite
{
	public void verify_Code(HashMap<String,Object> datas,Context context)
	{
		HashMap<String ,Object> params = new HashMap<>();
		Bitmap bmp = (Bitmap) datas.get("img");
		String path = FileCacheMgr.GetLinePath();
		if(Utils.SaveTempImg(bmp, path))
		{
			params.put("img",path);
		}
		params.put("info",datas.get("info"));
		MyFramework.SITE_Open(context, RegisterLoginInfoPageSite402.class, params, Framework2.ANIM_NONE);
	}

	//返回上一步
	public void toLoginPage(Context context)
	{
		MyFramework.SITE_Open(context, LoginPageSite402.class, null, Framework2.ANIM_NONE);
	}

	//返回上一步
	public void onBackToLastPage(Context context)
	{
		MyFramework.SITE_ClosePopup(context, null, Framework2.ANIM_NONE);
	}

	//返回上一步
	public void onBack(Context context)
	{
		MyFramework.SITE_ClosePopup(context, null, Framework2.ANIM_NONE);
	}
}
