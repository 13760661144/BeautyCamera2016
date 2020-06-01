package cn.poco.business.site;

import android.content.Context;

import cn.poco.framework.MyFramework;
import cn.poco.framework2.Framework2;

/**
 * 只显示"选图/拍照"页
 */
	public class WayPageSite6 extends WayPageSite
{
	@Override
	public void OnBack(Context context)
	{
		//MyFramework.SITE_BackTo(context, HomePageSite.class, null, Framework2.ANIM_NONE);
		MyFramework.SITE_Back(context, null, Framework2.ANIM_NONE);
	}
}
