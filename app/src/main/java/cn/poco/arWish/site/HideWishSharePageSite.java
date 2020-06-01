package cn.poco.arWish.site;

import android.content.Context;

import java.util.HashMap;

import cn.poco.arWish.HideWishSharePage;
import cn.poco.framework.BaseSite;
import cn.poco.framework.IPage;
import cn.poco.framework.MyFramework;
import cn.poco.framework.SiteID;
import cn.poco.framework2.Framework2;
import cn.poco.preview.site.PreviewImgPageSite;

/**
 * Created by pocouser on 2018/1/22.
 */

public class HideWishSharePageSite extends BaseSite
{
	public HideWishSharePageSite()
	{
		super(SiteID.HIDE_ARWISH_SHARE);
	}

	@Override
	public IPage MakePage(Context context)
	{
		return new HideWishSharePage(context, this);
	}

	public void onBack(Context context)
	{
		MyFramework.SITE_Back(context, null, Framework2.ANIM_NONE);
	}

	public void backToActivityPage(Context context)
	{
		MyFramework.SITE_BackTo(context, ArIntroIndexSite.class, null, Framework2.ANIM_NONE);
	}

	public void OnPreview(Context context, String picPath)
	{
		HashMap<String, Object> params = new HashMap<>();
		params.put("img", picPath);
		MyFramework.SITE_Popup(context, PreviewImgPageSite.class, params, Framework2.ANIM_TRANSITION);
	}
}
