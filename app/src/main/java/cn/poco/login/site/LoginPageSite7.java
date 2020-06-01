package cn.poco.login.site;

import android.content.Context;

import cn.poco.framework.MyFramework;
import cn.poco.framework2.Framework2;
import cn.poco.home.site.HomePageSite;
import cn.poco.login.LoginAllAnim;

/**
 * 任务大厅->领任务->登录
 */
public class LoginPageSite7 extends LoginPageSite
{

	@Override
	public void loginSuccess(Context context)
	{
		LoginAllAnim.ReSetLoginAnimData();
		MyFramework.SITE_BackTo(context, HomePageSite.class, null, Framework2.ANIM_NONE);
	}

	@Override
	public void onBack(Context context) {
		LoginAllAnim.ReSetLoginAnimData();
		MyFramework.SITE_BackTo(context, HomePageSite.class,null,Framework2.ANIM_NONE);
	}
}
