package cn.poco.login.site;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;

import java.util.HashMap;

import cn.poco.framework.FileCacheMgr;
import cn.poco.framework.MyFramework;
import cn.poco.framework2.Framework2;
import cn.poco.login.LoginAllAnim;
import cn.poco.login.LoginStyle;
import cn.poco.login.activity.LoginActivity;
import cn.poco.login.site.activity.LoginActivitySite;
import cn.poco.loginlibs.info.LoginInfo;
import cn.poco.utils.Utils;

/**
 * Created by Raining on 2017/11/16.
 * 独立登录Activity调用
 * {@link LoginPageSite}
 */

public class LoginPageSite401 extends LoginPageSite
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
		MyFramework.SITE_Open(context, ResetLoginPswPageSite401.class, params, Framework2.ANIM_NONE);
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
		MyFramework.SITE_Open(context, RegisterLoginPageSite401.class, params, Framework2.ANIM_NONE);
	}

	@Override
	public void onBack(Context context)
	{
		LoginAllAnim.ReSetLoginAnimData();

		if(context instanceof LoginActivity)
		{
			LoginActivitySite site = ((LoginActivity)context).getActivitySite();
			site.onBack((Activity)context);
		}
	}

	@Override
	public void loginSuccess(Context context)
	{
		LoginAllAnim.ReSetLoginAnimData();

		if(context instanceof LoginActivity)
		{
			LoginActivitySite site = ((LoginActivity)context).getActivitySite();
			site.onLogin((Activity)context);
		}
	}

	@Override
	public void thirdPartLoginOneStepFinish(Context context, LoginInfo info, LoginStyle.LoginBaseInfo baseInfo)
	{
		HashMap<String, Object> params = new HashMap<>();
		params.put("loginInfo", info);
		params.put("relogininfo", baseInfo);
		MyFramework.SITE_Open(context, BindPhonePageSite401.class, params, Framework2.ANIM_NONE);
	}
}
