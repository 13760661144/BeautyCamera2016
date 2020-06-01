package cn.poco.MaterialMgr2.site;

import android.content.Context;

import cn.poco.framework.MyFramework;
import cn.poco.framework2.Framework2;
import cn.poco.home.site.HomePageSite;

/**
 * 魔窗进入素材中心
 */
public class ThemeListPageSite3 extends ThemeListPageSite
{
	@Override
	public void OnBack(Context context)
	{
		MyFramework.SITE_Open(context, true, HomePageSite.class, null, Framework2.ANIM_NONE);
	}
}
