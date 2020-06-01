package cn.poco.MaterialMgr2.site;

import android.content.Context;

import java.util.HashMap;

import cn.poco.MaterialMgr2.ManageIntroPage;
import cn.poco.framework.BaseSite;
import cn.poco.framework.IPage;
import cn.poco.framework.MyFramework;
import cn.poco.framework.SiteID;
import cn.poco.framework2.Framework2;

/**
 * 素材管理分类详情
 */
public class ManagePageIntroSite extends BaseSite
{
	public ManagePageIntroSite()
	{
		super(SiteID.RES_MANAGE_INTRO);
	}

	@Override
	public IPage MakePage(Context context)
	{
		return new ManageIntroPage(context, this);
	}

	public void OnBack(Context context, HashMap<String, Object> params)
	{
		MyFramework.SITE_ClosePopup(context, params, Framework2.ANIM_TRANSLATION_LEFT);
	}
}
