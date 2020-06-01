package cn.poco.water.site;

import android.content.Context;

import cn.poco.framework.BaseSite;
import cn.poco.framework.IPage;
import cn.poco.framework.MyFramework;
import cn.poco.framework.SiteID;
import cn.poco.framework2.Framework2;
import cn.poco.water.WaterMarkPage;

/**
 * Created by admin on 2017/12/13.
 */

public class WaterMarkPageSite extends BaseSite
{
	/**
	 * 派生类必须实现一个XXXSite()的构造函数
	 *
	 */
	public WaterMarkPageSite()
	{
		super(SiteID.WATERMARK);
	}

	@Override
	public IPage MakePage(Context context)
	{
		return new WaterMarkPage(context, this);
	}

	public void onBack(Context context)
	{
		MyFramework.SITE_Back(context, null, Framework2.ANIM_NONE);
	}
}
