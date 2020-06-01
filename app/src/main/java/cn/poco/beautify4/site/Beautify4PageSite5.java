package cn.poco.beautify4.site;

import android.content.Context;

/**
 * Created by Raining on 2017/1/3.
 * 弹窗协议到美化指定摸个功能，功能退出返回到上一层，保存跳转到分享页
 */

public class Beautify4PageSite5 extends Beautify4PageSite
{
	@Override
	public void OnBack(Context context)
	{
//		MyFramework.SITE_Open(context, false, HomePageSite.class, null, Framework2.ANIM_NONE);
//      MyFramework.SITE_BackTo(context, HomePageSite.class, null, Framework2.ANIM_NONE);
        super.OnBack(context);
	}
}
