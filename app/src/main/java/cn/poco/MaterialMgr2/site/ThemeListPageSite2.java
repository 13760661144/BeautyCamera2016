package cn.poco.MaterialMgr2.site;

import android.content.Context;

import java.util.HashMap;

import cn.poco.framework.MyFramework;
import cn.poco.framework2.Framework2;

/**
 * 美化打开素材中心
 */
public class ThemeListPageSite2 extends ThemeListPageSite
{
	@Override
	public void OpenThemeIntroPage(Context context, HashMap<String, Object> params)
	{
		MyFramework.SITE_Popup(context, ThemeIntroPageSite2.class, params, Framework2.ANIM_TRANSLATION_LEFT);
	}
}
