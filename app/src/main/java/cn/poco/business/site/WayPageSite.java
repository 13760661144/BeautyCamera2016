package cn.poco.business.site;

import android.content.Context;

import java.util.HashMap;

import cn.poco.album.site.AlbumSite4;
import cn.poco.business.WayPage;
import cn.poco.camera.CameraSetDataKey;
import cn.poco.camera.site.CameraPageSite4;
import cn.poco.filterBeautify.FilterBeautifyProcessor;
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
public class WayPageSite extends BaseSite
{
	public WayPageSite()
	{
		super(SiteID.BUSINESS_WAY);
	}

	@Override
	public IPage MakePage(Context context)
	{
		return new WayPage(context, this);
	}

	public void OnBack(Context context)
	{
		MyFramework.SITE_Back(context, null, Framework2.ANIM_NONE);
	}

	public void OnCamera(Context context)
	{
		HashMap<String, Object> params = new HashMap<>();
		HomePageSite.CloneBusinessParams(params, m_inParams);
		params.putAll(CameraSetDataKey.GetWayTakePicture(true, true, FilterBeautifyProcessor.ONLY_SHAPE));
		MyFramework.SITE_Open(context, CameraPageSite4.class, params, Framework2.ANIM_NONE);
	}

	public void OnSelPhoto(Context context)
	{
		HashMap<String, Object> params = new HashMap<>();
		HomePageSite.CloneBusinessParams(params, m_inParams);
		params.put("from_camera", true);
		MyFramework.SITE_Open(context, AlbumSite4.class, params, Framework2.ANIM_NONE);
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
