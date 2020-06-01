package cn.poco.business.site;

import android.content.Context;

import java.util.HashMap;

import cn.poco.album.site.AlbumSite84;
import cn.poco.business.WayPage2;
import cn.poco.camera.CameraSetDataKey;
import cn.poco.camera.site.CameraPageSite69;
import cn.poco.framework.IPage;
import cn.poco.framework.MyFramework;
import cn.poco.framework2.Framework2;
import cn.poco.home.site.HomePageSite;

/**
 * 只显示"选图/拍照"页
 * 兰蔻商业2
 */
public class WayPageSite65 extends WayPageSite
{

	@Override
	public IPage MakePage(Context context)
	{
		return new WayPage2(context, this);
	}

	@Override
	public void OnCamera(Context context) {
		HashMap<String, Object> params = new HashMap<>();
		HomePageSite.CloneBusinessParams(params, m_inParams);
		params.putAll(CameraSetDataKey.GetBussinessTakePicture(true, false));
		MyFramework.SITE_Open(context, CameraPageSite69.class, params, Framework2.ANIM_NONE);
	}

	@Override
	public void OnSelPhoto(Context context) {
		HashMap<String, Object> params = new HashMap<>();
		HomePageSite.CloneBusinessParams(params, m_inParams);
		MyFramework.SITE_Open(context, AlbumSite84.class, params, Framework2.ANIM_NONE);
	}

	public void OnBack(Context context)
	{
		//MyFramework.SITE_BackTo(context, HomePageSite.class,null, Framework2.ANIM_NONE);
		MyFramework.SITE_Back(context, null, Framework2.ANIM_NONE);
	}
}
