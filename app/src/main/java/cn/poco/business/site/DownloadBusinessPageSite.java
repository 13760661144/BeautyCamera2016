package cn.poco.business.site;

import android.content.Context;

import java.util.HashMap;

import cn.poco.business.DownloadBusinessPage;
import cn.poco.framework.BaseSite;
import cn.poco.framework.IPage;
import cn.poco.framework.MyFramework;
import cn.poco.framework.SiteID;
import cn.poco.framework2.Framework2;
import cn.poco.home.site.HomePageSite;

/**
 * 通用通道 - 正常"马上参加"-"选图/拍照"
 */
public class DownloadBusinessPageSite extends BaseSite
{
	public DownloadBusinessPageSite()
	{
		super(SiteID.BUSINESS_DOWNLOAD_RES);
	}

	@Override
	public IPage MakePage(Context context)
	{
		return new DownloadBusinessPage(context, this);
	}

	public void OnBack(Context context)
	{
		MyFramework.SITE_Back(context, null, Framework2.ANIM_NONE);
	}

	public void OnNext(Context context)
	{
		HashMap<String, Object> params = new HashMap<>();
		HomePageSite.CloneBusinessParams(params, m_inParams);
		//MyFramework.SITE_Open(context, JoinPageSite.class, params, Framework2.ANIM_NONE);
		MyFramework.SITE_BackAndOpen(context, null, JoinPageSite.class, params, Framework2.ANIM_NONE);
	}
}
