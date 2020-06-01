package cn.poco.about.site;

import android.content.Context;

import cn.poco.about.AboutPage;
import cn.poco.framework.BaseSite;
import cn.poco.framework.IPage;
import cn.poco.framework.MyFramework;
import cn.poco.framework.SiteID;
import cn.poco.framework2.Framework2;

public class AboutPageSite extends BaseSite
{
	private Context m_context;

	public AboutPageSite()
	{
		super(SiteID.ABOUT);
	}

	@Override
	public IPage MakePage(Context context)
	{
		m_context = context;
		return new AboutPage(context, this);
	}

	public void onBack()
	{
		MyFramework.SITE_Back(m_context, null, Framework2.ANIM_TRANSLATION_LEFT);
	}
}
