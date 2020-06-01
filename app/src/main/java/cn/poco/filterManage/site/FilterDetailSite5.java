package cn.poco.filterManage.site;

import android.content.Context;

import java.util.HashMap;

import cn.poco.album.site.AlbumSite81;
import cn.poco.framework.MyFramework;
import cn.poco.framework2.Framework2;

/**
 * 社区-素材中心-素材详情页-滤镜详情页
 */
public class FilterDetailSite5 extends FilterDetailSite
{
	@Override
	public void onResourceUse(Context context,HashMap<String, Object> params)
	{
		MyFramework.SITE_Back(context, params, Framework2.ANIM_NONE);
		params.put("from_camera", true);//隐藏拍照入口
		MyFramework.SITE_OpenAndClosePopup(context, AlbumSite81.class, params, Framework2.ANIM_NONE);
	}
}
