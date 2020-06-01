package cn.poco.login.site;

import android.content.Context;

import java.util.HashMap;

import cn.poco.framework.MyFramework;
import cn.poco.framework2.Framework2;
import cn.poco.home.site.HomePageSite;
import cn.poco.setting.SettingInfo;
import cn.poco.setting.SettingInfoMgr;
import cn.poco.taskCenter.TaskCenterPage;
import cn.poco.taskCenter.site.TaskCenterPageSite;

/**
 * 积分页面账号过期,重新登陆<br/>
 * 弹出窗口,返回信息给下次view
 */
public class LoginPageSite5 extends LoginPageSite
{

	@Override
	public void loginSuccess(Context context)
	{
		SettingInfo info = SettingInfoMgr.GetSettingInfo(context);
		String id = info.GetPoco2Id(true);
		String token = info.GetPoco2Token(true);
		if(id != null && id.length() > 0 && token != null && token.length() > 0)
		{
			HashMap<String, Object> params = new HashMap<>();
			params.put(TaskCenterPage.EXTRA_USER_ID, id);
			params.put(TaskCenterPage.EXTRA_ACCESS_TOKEN, token);
//            MyFramework.SITE_Back(PocoCamera.main, null, Framework2.ANIM_NONE);
//            MyFramework.SITE_Popup(PocoCamera.main, CloudAlbumPageSite.class, params, Framework2.ANIM_NONE);
		 MyFramework.SITE_BackTo(context, HomePageSite.class, null, Framework2.ANIM_NONE);
			MyFramework.SITE_Open(context, TaskCenterPageSite.class,params,Framework2.ANIM_NONE);
		}
	}
}
