package cn.poco.business.site;

import android.content.Context;

import java.util.HashMap;

import cn.poco.framework.MyFramework;
import cn.poco.framework2.Framework2;
import cn.poco.home.site.HomePageSite;

/**
 * 通用通道 - 全屏广告
 */
public class DownloadBusinessPageSite3 extends DownloadBusinessPageSite
{
	@Override
	public void OnNext(Context context)
	{
		HashMap<String, Object> params = new HashMap<>();
		HomePageSite.CloneBusinessParams(params, m_inParams);
		//MyFramework.SITE_Open(context, FullScreenDisplayPageSite.class, params, Framework2.ANIM_NONE);
		MyFramework.SITE_BackAndOpen(context, null, FullScreenDisplayPageSite.class, params, Framework2.ANIM_NONE);
	}
}
