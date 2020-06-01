package cn.poco.MaterialMgr2.site;

import android.content.Context;

import java.util.HashMap;

import cn.poco.MaterialMgr2.ManagePage;
import cn.poco.filterManage.site.FilterManageSite;
import cn.poco.framework.BaseSite;
import cn.poco.framework.IPage;
import cn.poco.framework.MyFramework;
import cn.poco.framework.SiteID;
import cn.poco.framework2.Framework2;

/**
 * 管理页
 */
public class ManagePageSite extends BaseSite
{
	public ManagePageSite()
	{
		super(SiteID.RES_MANAGE);
	}

	@Override
	public IPage MakePage(Context context)
	{
		return new ManagePage(context, this);
	}

	public void OnBack(Context context, HashMap<String, Object> params)
	{
		MyFramework.SITE_ClosePopup(context, params, Framework2.ANIM_TRANSLATION_LEFT);
	}

	public void OnManageIntroPage(Context context, HashMap<String, Object> params)
	{
		boolean hasAnim = true;
		if(params != null && params.get("hasAnim") != null)
		{
			hasAnim = (Boolean)params.get("hasAnim");
		}
		if(hasAnim)
		{
			MyFramework.SITE_Popup(context, ManagePageIntroSite.class, params, Framework2.ANIM_TRANSLATION_LEFT);
		}
		else {
			MyFramework.SITE_Popup(context, ManagePageIntroSite.class, params, Framework2.ANIM_NONE);
		}
	}

	public void onFilterManagePage(Context context, HashMap<String, Object> params) {
		MyFramework.SITE_Popup(context, FilterManageSite.class, params, Framework2.ANIM_TRANSLATION_LEFT);
	}
}
