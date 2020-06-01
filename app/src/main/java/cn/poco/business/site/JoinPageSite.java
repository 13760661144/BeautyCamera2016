package cn.poco.business.site;

import android.content.Context;

import com.adnonstop.admasterlibs.data.AbsChannelAdRes;

import org.json.JSONObject;

import java.util.HashMap;

import cn.poco.business.JoinPage;
import cn.poco.framework.BaseSite;
import cn.poco.framework.IPage;
import cn.poco.framework.MyFramework;
import cn.poco.framework.SiteID;
import cn.poco.framework2.Framework2;
import cn.poco.home.site.HomePageSite;
import cn.poco.tianutils.CommonUtils;
import cn.poco.webview.site.WebViewPageSite;

/**
 * 通用通道
 */
public class JoinPageSite extends BaseSite
{
	public JoinPageSite()
	{
		super(SiteID.BUSINESS_JOIN);
	}

	@Override
	public IPage MakePage(Context context)
	{
		return new JoinPage(context, this);
	}

	public void OnBack(Context context)
	{
		//MyFramework.SITE_BackTo(context, HomePageSite.class, null, Framework2.ANIM_NONE);
		MyFramework.SITE_Back(context, null, Framework2.ANIM_NONE);
	}

	public void OnJoin(AbsChannelAdRes res, JSONObject postStr,Context context)
	{
		HashMap<String, Object> params = new HashMap<>();
		params.put(HomePageSite.BUSINESS_KEY, res);
		params.put(HomePageSite.POST_STR_KEY, postStr);
		MyFramework.SITE_Open(context, WayPageSite.class, params, Framework2.ANIM_NONE);
	}

	public void OnClickImg(Context context, String url)
	{
		if(url != null)
		{
			if((url.startsWith("http") || url.startsWith("ftp")) && url.contains(".poco.cn"))
			{
				HashMap<String, Object> params = new HashMap<String, Object>();
				params.put("url", url);
				MyFramework.SITE_Popup(context, WebViewPageSite.class, params, Framework2.ANIM_NONE);
			}
			else
			{
				CommonUtils.OpenBrowser(context, url);
			}
		}
	}
}
