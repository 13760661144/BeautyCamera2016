package cn.poco.login.site;

import android.content.Context;

import java.util.HashMap;

import cn.poco.framework.MyFramework;
import cn.poco.framework2.Framework2;
import cn.poco.live.site.LivePageSite;
import cn.poco.login.LoginAllAnim;

/**
 * Created by pocouser on 2018/1/29.
 */

public class ResetPswPageSite403 extends ResetPswPageSite
{
	@Override
	public void successBind(HashMap<String, Object> datas, Context context)
	{
		LoginAllAnim.ReSetLoginAnimData();
		MyFramework.SITE_OpenAndClosePopup(context, false, LivePageSite.class, null, Framework2.ANIM_NONE);
	}

	@Override
	public void reLogin(HashMap<String, Object> datas, Context context)
	{
		LoginAllAnim.ReSetLoginAnimData();
		MyFramework.SITE_BackTo(context, LoginPageSite403.class, datas, Framework2.ANIM_NONE);
	}

	@Override
	public void resetPswSuccess(Context context)
	{
		LoginAllAnim.ReSetLoginAnimData();
		MyFramework.SITE_OpenAndClosePopup(context, false, LivePageSite.class, null, Framework2.ANIM_NONE);
	}
}
