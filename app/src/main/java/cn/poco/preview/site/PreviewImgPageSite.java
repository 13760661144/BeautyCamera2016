package cn.poco.preview.site;

import android.content.Context;

import cn.poco.framework.BaseSite;
import cn.poco.framework.IPage;
import cn.poco.framework.MyFramework;
import cn.poco.framework.SiteID;
import cn.poco.framework2.Framework2;
import cn.poco.preview.PreviewImgPage;

/**
 * 图片预览
 */
public class PreviewImgPageSite extends BaseSite
{
	public PreviewImgPageSite()
	{
		super(SiteID.PREVIEW_IMG);
	}

	@Override
	public IPage MakePage(Context context)
	{
		return new PreviewImgPage(context, this);
	}

	public void OnBack(Context context)
	{
		MyFramework.SITE_Back(context, null, Framework2.ANIM_NONE);
	}
}
