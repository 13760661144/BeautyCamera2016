package cn.poco.camera.site;

import android.content.Context;

import java.util.HashMap;

import cn.poco.ad.site.ADPageSite;
import cn.poco.framework.MyFramework;
import cn.poco.framework2.Framework2;
import cn.poco.home.site.HomePageSite;

/**
 * 通用商业"拍一张"
 */
public class CameraPageSite4 extends CameraPageSite
{
	@Override
	public void onTakePicture(Context context, HashMap<String, Object> params)
	{
		HashMap<String, Object> temp = new HashMap<>();
		HomePageSite.CloneBusinessParams(temp, m_inParams);
		temp.put("imgs", params.get("img_file"));
		MyFramework.SITE_Open(context, ADPageSite.class, temp, Framework2.ANIM_TRANSITION);
	}
}
