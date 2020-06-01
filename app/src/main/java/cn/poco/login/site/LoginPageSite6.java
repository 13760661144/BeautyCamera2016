package cn.poco.login.site;

import android.content.Context;

import cn.poco.framework.MyFramework;
import cn.poco.framework2.Framework2;
import cn.poco.home.site.HomePageSite;
import cn.poco.login.LoginAllAnim;

/**
 * 云相册到绑定手机页面，输入已注册的手机号，点去登陆用到
 */
public class LoginPageSite6 extends LoginPageSite
{

	@Override
	public void loginSuccess(Context context)
	{
		LoginAllAnim.ReSetLoginAnimData();
		MyFramework.SITE_BackTo(context, HomePageSite.class, null, Framework2.ANIM_NONE);
	}
}
