package cn.poco.business.site;

import android.content.Context;

import java.util.HashMap;

import cn.poco.album.site.AlbumSite83;
import cn.poco.camera.CameraSetDataKey;
import cn.poco.camera.site.CameraPageSite68;
import cn.poco.framework.MyFramework;
import cn.poco.framework2.Framework2;
import cn.poco.home.site.HomePageSite;

/**
 * 只显示"选图/拍照"页
 * 佰草集商业
 */
public class WayPageSite66 extends WayPageSite
{
	@Override
	public void OnCamera(Context context) {
		HashMap<String, Object> params = new HashMap<>();
		HomePageSite.CloneBusinessParams(params, m_inParams);
		params.putAll(CameraSetDataKey.GetBussinessTakePicture(true, false));
		MyFramework.SITE_Open(context, CameraPageSite68.class, params, Framework2.ANIM_NONE);
	}

	@Override
	public void OnSelPhoto(Context context) {
		HashMap<String, Object> params = new HashMap<>();
		HomePageSite.CloneBusinessParams(params, m_inParams);
		MyFramework.SITE_Open(context, AlbumSite83.class, params, Framework2.ANIM_NONE);
	}

	public void OnBack(Context context)
	{
		//MyFramework.SITE_BackTo(context, HomePageSite.class,null, Framework2.ANIM_NONE);
		MyFramework.SITE_Back(context, null, Framework2.ANIM_NONE);
	}
}
