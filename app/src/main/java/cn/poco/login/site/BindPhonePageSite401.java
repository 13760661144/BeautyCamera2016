package cn.poco.login.site;

import android.app.Activity;
import android.content.Context;

import java.util.HashMap;

import cn.poco.framework.MyFramework;
import cn.poco.framework2.Framework2;
import cn.poco.login.LoginAllAnim;
import cn.poco.login.activity.LoginActivity;
import cn.poco.login.site.activity.LoginActivitySite;

/**
 * Created by Raining on 2017/11/16.
 * 独立登录Activity调用
 * {@link BindPhonePageSite}
 */

public class BindPhonePageSite401 extends BindPhonePageSite
{
	@Override
	public void bindSuccess(HashMap<String, Object> datas, Context context)
	{
		MyFramework.SITE_Open(context, ResetPswPageSite401.class, datas, Framework2.ANIM_NONE);
	}

	@Override
	public void toLoginPage(HashMap<String, Object> datas, Context context)
	{
		MyFramework.SITE_Open(context, LoginPageSite401.class, datas, Framework2.ANIM_NONE);
	}

	@Override
	public void onBack(Context context)
	{
		LoginAllAnim.ReSetLoginAnimData();

		if(context instanceof LoginActivity)
		{
			LoginActivitySite site = ((LoginActivity)context).getActivitySite();
			site.onBindBack((Activity)context);
		}
	}
}
