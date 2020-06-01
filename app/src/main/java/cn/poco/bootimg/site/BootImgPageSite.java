package cn.poco.bootimg.site;

import android.content.Context;

import com.adnonstop.admasterlibs.data.AbsBootAdRes;

import java.util.HashMap;

import cn.poco.bootimg.BootImgPage;
import cn.poco.framework.BaseSite;
import cn.poco.framework.IPage;
import cn.poco.framework.MyFramework;
import cn.poco.framework.MyFramework2App;
import cn.poco.framework.SiteID;
import cn.poco.framework2.Framework2;
import cn.poco.home.site.HomePageSite;
import cn.poco.home.site.IntroPage2Site;
import cn.poco.tianutils.CommonUtils;
import cn.poco.utils.MyNetCore;
import cn.poco.webview.site.WebViewPageSite2;

/**
 * 开机页
 */
public class BootImgPageSite extends BaseSite
{
	public BootImgPageSite()
	{
		super(SiteID.BOOT_IMG);
	}

	@Override
	public IPage MakePage(Context context)
	{
		return new BootImgPage(context, this);
	}

	public void OnHome(Context context,boolean anim, AbsBootAdRes res)
	{
		if(MyFramework2App.getInstance().IsFirstRun())
		{
			MyFramework.SITE_Open(context, true, IntroPage2Site.class, null, Framework2.ANIM_NONE);
		}
		else
		{
			HashMap<String, Object> params = new HashMap<>();
			params.put("boot_img", res);
			if(anim)
			{
				MyFramework.SITE_Open(context, true, HomePageSite.class, params, Framework2.ANIM_TRANSITION);
			}
			else
			{
				MyFramework.SITE_Open(context, true, HomePageSite.class, params, Framework2.ANIM_NONE);
			}
		}
	}

	public void OnBack(Context context)
	{

	}

	public void OnMyWeb(Context context,String url)
	{
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("url", url);
		MyFramework.SITE_Popup(context, WebViewPageSite2.class, params, Framework2.ANIM_NONE);
	}

	public void OnSystemWeb(Context context, String url)
	{
		CommonUtils.OpenBrowser(context, MyNetCore.GetPocoUrl(context, url));
	}


}
