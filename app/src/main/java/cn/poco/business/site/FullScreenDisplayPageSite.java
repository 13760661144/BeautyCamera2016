package cn.poco.business.site;

import android.content.Context;

import com.adnonstop.admasterlibs.AdUtils;

import java.util.HashMap;

import cn.poco.business.FullScreenADPage;
import cn.poco.framework.BaseSite;
import cn.poco.framework.IPage;
import cn.poco.framework.MyFramework;
import cn.poco.framework.SiteID;
import cn.poco.framework2.Framework2;
import cn.poco.tianutils.CommonUtils;
import cn.poco.webview.site.WebViewPageSite;

public class FullScreenDisplayPageSite extends BaseSite
{
	public FullScreenDisplayPageSite()
	{
		super(SiteID.BUSINESS_DISPLAY);
	}

	@Override
	public IPage MakePage(Context context)
	{
		return new FullScreenADPage(context, this);
	}

	public void OnBack(Context context)
	{
		//MyFramework.SITE_BackTo(context, HomePageSite.class, null, Framework2.ANIM_NONE);
		MyFramework.SITE_Back(context, null, Framework2.ANIM_NONE);
	}

	public void OnClickImg(Context context, String url)
	{
		if(url != null)
		{
			url = AdUtils.AdDecodeUrl(context, url);
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
