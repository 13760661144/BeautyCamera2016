package cn.poco.MaterialMgr2.site;

import android.content.Context;

import java.util.HashMap;

import cn.poco.framework.MyFramework;
import cn.poco.framework2.Framework2;

/**
 * 运营中心打开素材商店
 */

public class ThemeListPageSite4 extends ThemeListPageSite
{
	public void OpenThemeIntroPage(Context context, HashMap<String, Object> params)
	{
		MyFramework.SITE_Popup(context, ThemeIntroPageSite4.class, params, Framework2.ANIM_TRANSLATION_LEFT);
	}
}
