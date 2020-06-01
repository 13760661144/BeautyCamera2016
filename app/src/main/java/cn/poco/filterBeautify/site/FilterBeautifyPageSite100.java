package cn.poco.filterBeautify.site;

import android.content.Context;

import java.util.HashMap;

import cn.poco.beautify4.site.Beautify4PageSite100;
import cn.poco.camera.RotationImg2;
import cn.poco.framework.DataKey;
import cn.poco.framework.MyFramework;
import cn.poco.framework2.Framework2;
import my.beautyCamera.PocoCamera;
import my.beautyCamera.site.activity.MainActivitySite;

/**
 * 第三方调用
 */
public class FilterBeautifyPageSite100 extends FilterBeautifyPageSite
{
	@Override
	public void OnSave(Context context, HashMap<String, Object> params)
	{
		if(context instanceof PocoCamera)
		{
			MainActivitySite site = ((PocoCamera)context).getActivitySite();
			if(site != null)
			{
				site.OnSave(context, m_inParams, params);
			}
		}
	}

	@Override
	public void OnBeauty(Context context, HashMap<String, Object> params, RotationImg2 img, int filterUri, int filterAlpha, boolean hasWaterMark, int waterMarkId)
	{
		if (params == null)
		{
			params = new HashMap<>();
		}
		MyFramework.CopyExternalCallParams(m_inParams, params);
		params.put("imgs", new RotationImg2[]{img});
		params.put(DataKey.BEAUTIFY_DEF_SEL_URI, filterUri);
		params.put("filter_alpha", filterAlpha);
		params.put("do_not_del_filter_cache", true);
		params.put("do_not_reset_data", true);
		params.put("only_one_pic", true);
		params.put("has_water_mark", hasWaterMark);
		params.put("water_mark_id", waterMarkId);
		MyFramework.SITE_Open(context, Beautify4PageSite100.class, params, Framework2.ANIM_NONE);
	}
}
