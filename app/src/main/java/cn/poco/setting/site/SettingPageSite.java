package cn.poco.setting.site;

import android.content.Context;

import java.util.HashMap;

import cn.poco.about.site.AboutPageSite;
import cn.poco.camera.CameraConfig;
import cn.poco.camera.CameraSetDataKey;
import cn.poco.camera.site.CameraPageSite14;
import cn.poco.camera.site.CameraPageSite3;
import cn.poco.camera3.config.shutter.ShutterConfig;
import cn.poco.framework.BaseSite;
import cn.poco.framework.IPage;
import cn.poco.framework.MyFramework;
import cn.poco.framework.SiteID;
import cn.poco.framework2.Framework2;
import cn.poco.setting.SettingPage;
import cn.poco.water.site.WaterMarkPageSite;

public class SettingPageSite extends BaseSite
{
	private Context m_context;

	public SettingPageSite()
	{
		super(SiteID.SETTING);
	}

	@Override
	public IPage MakePage(Context context)
	{
		m_context = context;
		return new SettingPage(context, this);
	}

	public void OnBack()
	{
		MyFramework.SITE_Back(m_context, null, Framework2.ANIM_TRANSLATION_LEFT);
	}

	public void OnAbout()
	{
		MyFramework.SITE_Popup(m_context, AboutPageSite.class, null, Framework2.ANIM_TRANSLATION_LEFT);
	}

	public void OnFixCamera()
	{
		HashMap<String, Object> params = new HashMap<>();
		params.put(CameraSetDataKey.KEY_PATCH_CAMERA, 1);
		params.put(CameraSetDataKey.KEY_PATCH_FINISH_TO_CLOSE, true);
        params.put(CameraSetDataKey.KEY_CAMERA_SELECT_TAB_TYPE, ShutterConfig.TabType.PHOTO);
        params.put(CameraSetDataKey.KEY_CAMERA_SHOW_TAB_TYPE, ShutterConfig.TabType.PHOTO);
        params.put(CameraSetDataKey.KEY_CAMERA_SELECT_RATIO, CameraConfig.PreviewRatio.Ratio_4_3);
		MyFramework.SITE_Popup(m_context, CameraPageSite3.class, params, Framework2.ANIM_NONE);
	}

	/**
	 * 镜头预览智能美形定制
	 */
	public void OnTailorMadeCamera()
	{
		HashMap<String, Object> params = new HashMap<>();
		params.put(CameraSetDataKey.KEY_TAILOR_MADE_SETTING, true);
		params.put(CameraSetDataKey.KEY_CAMERA_SELECT_TAB_TYPE, ShutterConfig.TabType.CUTE);
        params.put(CameraSetDataKey.KEY_CAMERA_SHOW_TAB_TYPE, ShutterConfig.ALL_TYPE);
		MyFramework.SITE_Open(m_context, CameraPageSite14.class , params, Framework2.ANIM_NONE);
	}

	/**
	 * 照片水印设置
	 *
	 * @param context
	 */
	public void OnPhotoWatermarkSet(Context context)
	{
		MyFramework.SITE_Popup(context, WaterMarkPageSite.class, null, Framework2.ANIM_TRANSLATION_LEFT);
	}
}
