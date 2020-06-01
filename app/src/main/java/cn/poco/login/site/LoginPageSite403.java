package cn.poco.login.site;

import android.content.Context;
import android.graphics.Bitmap;

import java.util.HashMap;

import cn.poco.framework.FileCacheMgr;
import cn.poco.framework.MyFramework;
import cn.poco.framework2.Framework2;
import cn.poco.live.site.LiveIntroPageSite;
import cn.poco.live.site.LivePageSite;
import cn.poco.login.LoginStyle;
import cn.poco.loginlibs.info.LoginInfo;
import cn.poco.utils.Utils;

/**
 * Created by Raining on 2018/1/29.
 * 登陆到直播
 */

public class LoginPageSite403 extends LoginPageSite
{
	@Override
	public void LosePsw(HashMap<String, Object> datas, Context context)
	{
		HashMap<String, Object> params = new HashMap<>();
		String path = FileCacheMgr.GetLinePath();
		Bitmap bmp = (Bitmap)datas.get("img");
		if(Utils.SaveTempImg(bmp, path))
		{
			params.put("img", path);
		}
		params.put("info", datas.get("info"));
		MyFramework.SITE_Open(context, ResetLoginPswPageSite403.class, params, Framework2.ANIM_NONE);
	}

	@Override
	public void creatAccount(HashMap<String, Object> datas, Context context)
	{
		HashMap<String, Object> params = new HashMap<>();
		String path = FileCacheMgr.GetLinePath();
		Bitmap bmp = (Bitmap)datas.get("img");
		if(Utils.SaveTempImg(bmp, path))
		{
			params.put("img", path);
		}
		MyFramework.SITE_Open(context, RegisterLoginPageSite403.class, params, Framework2.ANIM_NONE);
	}

	@Override
	public void onBack(Context context)
	{
		MyFramework.SITE_Back(context, null, Framework2.ANIM_NONE);
	}

	@Override
	public void loginSuccess(Context context)
	{
//		MyFramework.SITE_OpenAndClosePopup(context, false, LivePageSite.class, null, Framework2.ANIM_NONE);
		MyFramework.SITE_OpenAndClosePopup(context, false, LiveIntroPageSite.class, null, Framework2.ANIM_NONE);
	}

	@Override
	public void thirdPartLoginOneStepFinish(Context context, LoginInfo info, LoginStyle.LoginBaseInfo baseInfo)
	{
		HashMap<String, Object> params = new HashMap<>();
		params.put("loginInfo", info);
		params.put("relogininfo", baseInfo);
		MyFramework.SITE_Open(context, BindPhonePageSite403.class, params, Framework2.ANIM_NONE);
	}
}
