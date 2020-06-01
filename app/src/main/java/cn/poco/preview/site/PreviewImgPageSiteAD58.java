package cn.poco.preview.site;

import android.content.Context;

import cn.poco.framework.IPage;
import cn.poco.preview.PreviewImgPage;
import cn.poco.tianutils.CommonUtils;

/**
 * adidas去掉硬件加速
 */
public class PreviewImgPageSiteAD58 extends PreviewImgPageSite
{
	@Override
	public IPage MakePage(Context context)
	{
		IPage out = new PreviewImgPage(context, this);
		CommonUtils.CancelViewGPU(out);
		return out;
	}
}
