package cn.poco.camera.site;

import android.content.Context;

import java.util.HashMap;

import cn.poco.filterBeautify.site.FilterBeautifyPageSite100;
import cn.poco.framework.MyFramework;
import cn.poco.framework2.Framework2;

/**
 * 拍照到滤镜,外部调用
 */
public class CameraPageSite100 extends CameraPageSite
{
	@Override
	public void onTakePicture(Context context, HashMap<String, Object> params)
	{
		HashMap<String, Object> temp = new HashMap<>();
		MyFramework.CopyExternalCallParams(m_inParams, temp);
		temp.putAll(params);
		temp.put("imgs", params.get("img_file"));
		MyFramework.SITE_Open(context, FilterBeautifyPageSite100.class, temp, Framework2.ANIM_NONE);
	}
}
