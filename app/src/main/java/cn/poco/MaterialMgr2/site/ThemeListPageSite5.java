package cn.poco.MaterialMgr2.site;

import android.content.Context;

import java.util.HashMap;

import cn.poco.filterManage.site.FilterDetailSite;
import cn.poco.filterManage.site.FilterDetailSite5;
import cn.poco.framework.MyFramework;
import cn.poco.framework2.Framework2;
import my.beautyCamera.PocoCamera;

/**
 * 社区打开素材商店
 */
public class ThemeListPageSite5 extends ThemeListPageSite
{
	public void OpenThemeIntroPage(Context context,HashMap<String, Object> params)
	{
		MyFramework.SITE_Popup(context, ThemeIntroPageSite5.class, params, Framework2.ANIM_TRANSLATION_LEFT);
	}

	public void onFilterDetails(Context context,HashMap<String, Object> params) {
		MyFramework.SITE_Popup(context, FilterDetailSite5.class, params, Framework2.ANIM_TRANSLATION_LEFT);
	}
}
