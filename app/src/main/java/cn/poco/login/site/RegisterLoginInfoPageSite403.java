package cn.poco.login.site;

import android.content.Context;

import java.util.HashMap;

import cn.poco.album.site.AlbumSite403;
import cn.poco.framework.MyFramework;
import cn.poco.framework2.Framework2;
import cn.poco.live.site.LivePageSite;
import cn.poco.login.LoginAllAnim;

/**
 * Created by pocouser on 2018/1/29.
 */

public class RegisterLoginInfoPageSite403 extends RegisterLoginInfoPageSite
{
	@Override
	public void registerSuccess(Context context)
	{
		LoginAllAnim.ReSetLoginAnimData();
		MyFramework.SITE_OpenAndClosePopup(context, false, LivePageSite.class, null, Framework2.ANIM_NONE);
	}

	@Override
	public void uploadHeadImg(HashMap<String, Object> datas, Context context)
	{
		MyFramework.SITE_Popup(context, AlbumSite403.class, datas, Framework2.ANIM_TRANSLATION_TOP);
	}
}
