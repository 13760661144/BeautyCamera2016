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
 * {@link ResetPswPageSite}
 */

public class ResetPswPageSite401 extends ResetPswPageSite
{
	@Override
	public void successBind(HashMap<String, Object> datas, Context context)
	{
		LoginAllAnim.ReSetLoginAnimData();

		if(context instanceof LoginActivity)
		{
			LoginActivitySite site = ((LoginActivity)context).getActivitySite();
			site.BindSuccess((Activity)context, datas);
		}
	}

	@Override
	public void reLogin(HashMap<String, Object> datas, Context context)
	{
		LoginAllAnim.ReSetLoginAnimData();
		MyFramework.SITE_BackTo(context, LoginPageSite401.class, datas, Framework2.ANIM_NONE);
	}

	@Override
	public void resetPswSuccess(Context context)
	{
		LoginAllAnim.ReSetLoginAnimData();
		if(context instanceof Activity)
		{
			LoginActivitySite.LoginSuccess((Activity)context);
		}
	}
}
