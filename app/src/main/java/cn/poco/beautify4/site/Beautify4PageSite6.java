package cn.poco.beautify4.site;

import android.content.Context;

import cn.poco.framework.MyFramework;
import cn.poco.framework2.Framework2;
import cn.poco.home.site.HomePageSite;

/**
 * Created by Raining on 2017/1/9.
 * 通用商业跳美化
 */

public class Beautify4PageSite6 extends Beautify4PageSite
{
	@Override
	public void OnBack(Context context)
	{
		MyFramework.SITE_Open(context, true, HomePageSite.class, null, Framework2.ANIM_NONE);
	}
}
