package cn.poco.login.site;

import android.app.Activity;
import android.content.Context;

import java.util.HashMap;

import cn.poco.album.site.AlbumSite401;
import cn.poco.framework.MyFramework;
import cn.poco.framework2.Framework2;
import cn.poco.login.LoginAllAnim;
import cn.poco.login.site.activity.LoginActivitySite;

/**
 * Created by Raining on 2017/11/17.
 * 独立登录Activity调用
 * {@link RegisterLoginInfoPageSite}
 */

public class RegisterLoginInfoPageSite401 extends RegisterLoginInfoPageSite
{
	@Override
	public void registerSuccess(Context context)
	{
		LoginAllAnim.ReSetLoginAnimData();
		if(context instanceof Activity)
		{
			LoginActivitySite.LoginSuccess((Activity)context);
		}
	}

	@Override
	public void uploadHeadImg(HashMap<String, Object> datas, Context context)
	{
		MyFramework.SITE_Popup(context, AlbumSite401.class, datas, Framework2.ANIM_TRANSLATION_TOP);
	}
}
