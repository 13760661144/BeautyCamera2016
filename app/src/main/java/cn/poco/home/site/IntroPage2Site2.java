package cn.poco.home.site;

import android.content.Context;

import cn.poco.framework.MyFramework;
import cn.poco.framework2.Framework2;

/**
 * 浏览欢迎页
 */
public class IntroPage2Site2 extends IntroPage2Site
{
	@Override
	public void OnNext(Context context)
	{
		MyFramework.SITE_ClosePopup(context, null, Framework2.ANIM_NONE);
	}
}
