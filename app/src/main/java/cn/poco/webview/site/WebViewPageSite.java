package cn.poco.webview.site;

import android.content.Context;

import java.util.HashMap;

import cn.poco.album.site.AlbumSite3;
import cn.poco.camera.CameraSetDataKey;
import cn.poco.camera.site.CameraPageSite15;
import cn.poco.filterBeautify.FilterBeautifyProcessor;
import cn.poco.framework.BaseSite;
import cn.poco.framework.IPage;
import cn.poco.framework.MyFramework;
import cn.poco.framework.SiteID;
import cn.poco.framework2.Framework2;
import cn.poco.home.site.HomePageSite;
import cn.poco.webview.WebViewPage;
public class WebViewPageSite extends BaseSite
{
	public HomePageSite.CmdProc m_cmdProc;

	public WebViewPageSite()
	{
		super(SiteID.WEBVIEW);

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
		return new WebViewPage(context, this);
	}

	public void OnBack(Context context)
	{
		MyFramework.SITE_Back(context, null, Framework2.ANIM_NONE);
	}

	public void OnClose(Context context)
	{
		MyFramework.SITE_Back(context, null, Framework2.ANIM_NONE);
	}

	public void OnCamera(Context context)
	{
		//只有拍照
		HashMap<String, Object> datas = new HashMap<>();
		datas.put(CameraSetDataKey.KEY_START_MODE, 1);
		datas.putAll(CameraSetDataKey.GetWayTakePicture(false, true, FilterBeautifyProcessor.FILTER_BEAUTY));
		MyFramework.SITE_Popup(context, CameraPageSite15.class, datas, Framework2.ANIM_NONE);
	}

	public void OnSelPhoto(Context context)
	{
		HashMap<String, Object> datas = new HashMap<>();
		datas.put("from_camera", true);
		MyFramework.SITE_Popup(context, AlbumSite3.class, datas, Framework2.ANIM_NONE);
	}
}
