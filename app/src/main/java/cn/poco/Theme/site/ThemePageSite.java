package cn.poco.Theme.site;

import android.content.Context;

import java.util.HashMap;

import cn.poco.Theme.ThemePage;
import cn.poco.framework.BaseSite;
import cn.poco.framework.IPage;
import cn.poco.framework.MyFramework;
import cn.poco.framework.SiteID;
import cn.poco.framework2.Framework2;

/**
 * Created by lgd on 2016/12/14.
 */

// 主页到换肤
public class ThemePageSite extends BaseSite
{
	/**
	 * 派生类必须实现一个XXXSite()的构造函数
	 *
	 */
	public ThemePageSite()
	{
		super(SiteID.THEME);
	}

	@Override
	public IPage MakePage(Context context)
	{
		return new ThemePage(context, this);
	}


	public void OnBack(Context context)
	{
		MyFramework.SITE_Back(context, null, Framework2.ANIM_TRANSLATION_LEFT);
	}


	public void OnSave(Context context,HashMap<String ,Object> datas)
	{
		MyFramework.SITE_Back(context, datas, Framework2.ANIM_TRANSLATION_LEFT);
	}
}
