package cn.poco.MaterialMgr2.site;

import android.content.Context;

import java.util.HashMap;

import cn.poco.MaterialMgr2.ThemeListPage;
import cn.poco.filterManage.site.FilterDetailSite;
import cn.poco.framework.BaseSite;
import cn.poco.framework.IPage;
import cn.poco.framework.MyFramework;
import cn.poco.framework.SiteID;
import cn.poco.framework2.Framework2;

/**
 * 首页打开素材中心
 */
public class ThemeListPageSite extends BaseSite
{
	public ThemeListPageSite()
	{
		super(SiteID.THEME_LIST);
	}

	@Override
	public IPage MakePage(Context context)
	{
		return new ThemeListPage(context, this);
	}

	public void OnBack(Context context)
	{
		MyFramework.SITE_Back(context, null, Framework2.ANIM_NONE);
	}

	public void OnManagePageOpen(Context context)
	{
		MyFramework.SITE_Popup(context, ManagePageSite.class, null, Framework2.ANIM_TRANSLATION_LEFT);
	}

	public void OpenThemeIntroPage(Context context, HashMap<String, Object> params)
	{
		MyFramework.SITE_Popup(context, ThemeIntroPageSite.class, params, Framework2.ANIM_TRANSLATION_LEFT);
	}

	public void onFilterDetails(Context context, HashMap<String, Object> params) {
		MyFramework.SITE_Popup(context, FilterDetailSite.class, params, Framework2.ANIM_TRANSLATION_LEFT);
	}
}
