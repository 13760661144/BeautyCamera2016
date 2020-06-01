package cn.poco.appmarket.site;

import android.content.Context;

import cn.poco.appmarket.MarketWebviewPage;
import cn.poco.framework.BaseSite;
import cn.poco.framework.IPage;
import cn.poco.framework.MyFramework;
import cn.poco.framework.SiteID;
import cn.poco.framework2.Framework2;
import cn.poco.home.site.HomePageSite;

public class MarketPageSite extends BaseSite
{
	public HomePageSite.CmdProc m_cmdProc;
	private Context m_context;

	public MarketPageSite()
	{
		super(SiteID.APP_MARKET);

		MakeCmdProc();
	}

	/**
	 * 注意构造函数调用
	 */
	protected void MakeCmdProc()
	{
		m_cmdProc = new HomePageSite.CmdProc();
	}

	@Override
	public IPage MakePage(Context context)
	{
		m_context = context;
		return new MarketWebviewPage(context, this);
	}

	public void onBack()
	{
		MyFramework.SITE_Back(m_context, null, Framework2.ANIM_TRANSLATION_LEFT);
	}
}
