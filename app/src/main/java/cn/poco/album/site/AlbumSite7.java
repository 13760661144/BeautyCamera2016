package cn.poco.album.site;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

import cn.poco.MaterialMgr2.site.ThemeIntroPageSite;
import cn.poco.beautify4.site.Beautify4PageSite2;
import cn.poco.camera.CameraSetDataKey;
import cn.poco.camera.site.CameraPageSite16;
import cn.poco.camera3.config.shutter.ShutterConfig;
import cn.poco.framework.DataKey;
import cn.poco.framework.MyFramework;
import cn.poco.framework2.Framework2;

/**
 * Created by Raining on 2016/12/9.
 * 素材中心马上使用到美化
 */

public class AlbumSite7 extends AlbumSite
{
	@Override
	public void onBack(Context context)
	{
		MyFramework.SITE_Back(context, null, Framework2.ANIM_NONE);
	}

	@Override
	public void onPhotoSelected(Context context, Map<String, Object> params)
	{
		HashMap<String, Object> temp = new HashMap<>();
		temp.put("imgs", MakeRotationImg((String[])params.get("imgs"), true));
		temp.put(DataKey.BEAUTIFY_DEF_OPEN_PAGE, m_inParams.get(ThemeIntroPageSite.TYPE));
		temp.put(DataKey.BEAUTIFY_DEF_SEL_URI, m_inParams.get(ThemeIntroPageSite.ID));
		temp.put("index", params.get("index"));
		temp.put("folder_name", params.get("folder_name"));
		temp.put("show_exit_dialog", false);
		if (MyFramework.GetCurrentIndex(context) > 0) {
			MyFramework.SITE_OpenAndClosePopup(context, false, -1, Beautify4PageSite2.class, temp, Framework2.ANIM_NONE);
		} else {
			MyFramework.SITE_Open(context, Beautify4PageSite2.class, temp, Framework2.ANIM_NONE);
		}
	}

	/**
	 * 打开Camera
	 */
	@Override
	public void onOpenCamera(Context context) {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put(CameraSetDataKey.KEY_START_MODE, 1);
		params.put(CameraSetDataKey.KEY_HIDE_PHOTO_PICKER_BTN, true);
		params.put(CameraSetDataKey.KEY_IS_FILTER_BEAUTITY_PROCESS, true);//拍照处理美颜滤镜效果
		params.put(CameraSetDataKey.KEY_CAMERA_SHOW_TAB_TYPE, ShutterConfig.TabType.PHOTO);
		params.put(CameraSetDataKey.KEY_CAMERA_SELECT_TAB_TYPE, ShutterConfig.TabType.PHOTO);
		params.put(CameraSetDataKey.KEY_HIDE_STICKER_BTN, true);
		params.put(DataKey.BEAUTIFY_DEF_OPEN_PAGE, m_inParams.get(ThemeIntroPageSite.TYPE));
		params.put(DataKey.BEAUTIFY_DEF_SEL_URI, m_inParams.get(ThemeIntroPageSite.ID));
		/** 参数的设置请查看cn.poco.camera.CameraPage类的SetData方法的参数说明 */
		MyFramework.SITE_OpenAndClosePopup(context, false, -1, CameraPageSite16.class, params, Framework2.ANIM_NONE);
	}
}
