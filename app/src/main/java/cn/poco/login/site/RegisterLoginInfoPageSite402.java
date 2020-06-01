package cn.poco.login.site;

import android.content.Context;

import java.util.HashMap;

import cn.poco.album.site.AlbumSite402;
import cn.poco.framework.MyFramework;
import cn.poco.framework2.Framework2;
import cn.poco.home.site.HomePageSite;
import cn.poco.login.LoginAllAnim;

/**
 * 社区到注册，到注册流程验证完手机进入
 */

public class RegisterLoginInfoPageSite402 extends RegisterLoginInfoPageSite
{
	public void registerSuccess(Context context)
	{
		LoginAllAnim.ReSetLoginAnimData();
		MyFramework.SITE_ClosePopup(context, null, Framework2.ANIM_NONE);
	}

	public void uploadHeadImg(HashMap<String,Object> datas, Context context)
	{
		MyFramework.SITE_Popup(context, AlbumSite402.class, datas, Framework2.ANIM_TRANSLATION_TOP);
	}
}
