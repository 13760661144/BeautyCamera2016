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
 * Created by pocouser on 2018/1/29.
 */

public class BindPhonePageSite403 extends BindPhonePageSite
{
	@Override
	public void bindSuccess(HashMap<String, Object> datas, Context context)
	{
		MyFramework.SITE_Open(context, ResetPswPageSite403.class, datas, Framework2.ANIM_NONE);
	}

	@Override
	public void toLoginPage(HashMap<String, Object> datas, Context context)
	{
		MyFramework.SITE_Open(context, LoginPageSite403.class, datas, Framework2.ANIM_NONE);
	}
}
