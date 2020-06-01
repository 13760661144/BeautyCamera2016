package cn.poco.login.site;

import android.content.Context;

import java.util.HashMap;

import cn.poco.cloudAlbum.site.CloudAlbumPageSite2;
import cn.poco.framework.MyFramework;
import cn.poco.framework2.Framework2;
import cn.poco.setting.SettingInfo;
import cn.poco.setting.SettingInfoMgr;

/**
 * 弹窗到云相册,登陆
 */
public class LoginPageSite2 extends LoginPageSite
{
	@Override
	public void onBack(Context context)
	{
		MyFramework.SITE_Back(context, null, Framework2.ANIM_NONE);
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
			MyFramework.SITE_OpenAndClosePopup(context, false, CloudAlbumPageSite2.class, params, Framework2.ANIM_NONE);
		}
	}
}
