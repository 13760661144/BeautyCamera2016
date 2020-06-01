package cn.poco.webview.site;

import android.content.Context;

import cn.poco.framework.MyFramework;
import cn.poco.framework2.Framework2;

/**
 * 无动画
 */
public class WebViewPageSite2 extends WebViewPageSite
{
	@Override
	public void OnBack(Context context)
	{
		MyFramework.SITE_Back(context, null, Framework2.ANIM_NONE);
	}

	@Override
	public void OnClose(Context context)
	{
		MyFramework.SITE_ClosePopup(context, null, Framework2.ANIM_NONE);
	}




}
