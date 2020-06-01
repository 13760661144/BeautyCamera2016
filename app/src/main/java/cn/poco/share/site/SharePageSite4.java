package cn.poco.share.site;

import android.content.Context;

import cn.poco.framework.IPage;
import cn.poco.share.SharePage;

/**
 * 通用商业到分享
 */
public class SharePageSite4 extends SharePageSite
{
	@Override
	public IPage MakePage(Context context)
	{
		IPage page = super.MakePage(context);

		((SharePage)page).mHideBanner = true;

		return page;
	}
}
