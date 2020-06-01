package cn.poco.login.site;

import android.content.Context;

import java.util.HashMap;

import cn.poco.cloudAlbum.site.CloudAlbumPageSite;
import cn.poco.framework.MyFramework;
import cn.poco.framework2.Framework2;
import cn.poco.home.site.HomePageSite;
import cn.poco.setting.SettingInfo;
import cn.poco.setting.SettingInfoMgr;

/**
 * 云相册账号信息过期,重新登陆<br/>
 * 弹出窗口,返回信息给下次view
 */
public class LoginPageSite4 extends LoginPageSite
{
	@Override
	public void onBack(Context context)
	{
		MyFramework.SITE_BackTo(context, HomePageSite.class, null, Framework2.ANIM_NONE);
	}

	@Override
	public void loginSuccess(Context context)
	{
		SettingInfo info = SettingInfoMgr.GetSettingInfo(context);
		String id = info.GetPoco2Id(true);
		String token = info.GetPoco2Token(true);
		if(id != null && id.length() > 0 && token != null && token.length() > 0)
		{
			HashMap<String, Object> params = new HashMap<>();
			params.put("id", id);
			params.put("token", token);
			MyFramework.SITE_BackAndOpen(context, HomePageSite.class, CloudAlbumPageSite.class, params, Framework2.ANIM_NONE);
		}
	}
}
