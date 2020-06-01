package cn.poco.bootimg.site;

import android.content.Context;

import cn.poco.framework.MyFramework;
import cn.poco.framework2.Framework2;

/**
 * Created by lgd on 2016/12/27.
 * 主页->开机广告页
 */

public class BootImgPageSite1 extends BootImgPageSite
{
	@Override
	public void OnBack(Context context)
	{
		MyFramework.SITE_Back(context, null, Framework2.ANIM_TRANSLATION_RIGHT);
	}
}
