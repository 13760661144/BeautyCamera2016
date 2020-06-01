package cn.poco.album.site;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

import cn.poco.ad66.site.AD66PageSite;
import cn.poco.camera.CameraSetDataKey;
import cn.poco.camera.site.CameraPageSite69;
import cn.poco.framework.MyFramework;
import cn.poco.framework2.Framework2;
import cn.poco.home.site.HomePageSite;


/**
 * 兰蔻商业2
 */
public class AlbumSite84 extends AlbumSite {

	public void onOpenCamera(Context context) {
		HashMap<String, Object> params = new HashMap<String, Object>();
		/** 参数的设置请查看cn.poco.camera.CameraPage类的SetData方法的参数说明 */
		params.putAll(CameraSetDataKey.GetBussinessTakePicture(true, false));
		HomePageSite.CloneBusinessParams(params, m_inParams);
		MyFramework.SITE_Open(context, CameraPageSite69.class, params, Framework2.ANIM_NONE);
	}

	/**
	 * 选择好图片后回调
	 *
	 * @param params 参数
	 *               imgs: String[] 图片的路径
	 */
	public void onPhotoSelected(Context context,Map<String, Object> params) {
		HashMap<String, Object> temp = new HashMap<>();
		temp.put("imgs", MakeRotationImg((String[])params.get("imgs"), true));
		HomePageSite.CloneBusinessParams(temp, m_inParams);
		MyFramework.SITE_Open(context, AD66PageSite.class, temp, Framework2.ANIM_NONE);
	}

}
