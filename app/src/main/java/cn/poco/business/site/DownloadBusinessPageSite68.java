package cn.poco.business.site;

import android.content.Context;

import java.util.HashMap;

import cn.poco.framework.MyFramework;
import cn.poco.framework2.Framework2;
import cn.poco.home.site.HomePageSite;

/**
 * 佰草集商业
 */
public class DownloadBusinessPageSite68 extends DownloadBusinessPageSite
{
	@Override
	public void OnNext(Context context)
	{
		HashMap<String, Object> params = new HashMap<>();
		HomePageSite.CloneBusinessParams(params, m_inParams);
		params.put("channelValue",m_inParams.get("channelValue"));
		//MyFramework.SITE_Open(context, WayPageSite64.class, params, Framework2.ANIM_NONE);
		MyFramework.SITE_BackAndOpen(context, null, WayPageSite66.class, params, Framework2.ANIM_NONE);
	}
}
