package cn.poco.business.site;

import android.content.Context;

import java.util.HashMap;

import cn.poco.camera.CameraConfig;
import cn.poco.camera.CameraSetDataKey;
import cn.poco.camera.site.CameraPageSite303;
import cn.poco.camera3.config.CameraStickerConfig;
import cn.poco.camera3.config.shutter.ShutterConfig;
import cn.poco.framework.MyFramework;
import cn.poco.framework2.Framework2;
import cn.poco.home.site.HomePageSite;

/**
 * 阿玛尼商业
 */
public class DownloadBusinessPageSite69 extends DownloadBusinessPageSite
{
	@Override
	public void OnNext(Context context)
	{
		HashMap<String, Object> params = new HashMap<>();
        params.put(CameraSetDataKey.KEY_CAMERA_SHOW_TAB_TYPE, ShutterConfig.TabType.VIDEO);
        params.put(CameraSetDataKey.KEY_CAMERA_SELECT_TAB_TYPE, ShutterConfig.TabType.VIDEO);
        params.put(CameraSetDataKey.KEY_CAMERA_SELECT_RATIO, CameraConfig.PreviewRatio.Ratio_16_9);
        params.put(CameraSetDataKey.KEY_VIDEO_RECORD_TIME_TYPE, ShutterConfig.VideoDurationType.DURATION_TEN_SEC);
        params.put(CameraSetDataKey.KEY_VIDEO_MULTI_SECTION_ENABLE, false);
        params.put(CameraSetDataKey.KEY_VIDEO_MULTI_ORIENTATION_ENABLE, false);
        params.put(CameraSetDataKey.KEY_HIDE_BEAUTY_SETTING, true);
        params.put(CameraSetDataKey.KEY_HIDE_FILTER_SELECTOR, true);
        params.put(CameraSetDataKey.KEY_HIDE_PHOTO_PICKER_BTN, true);
        params.put(CameraSetDataKey.KEY_HIDE_STICKER_BTN, true);
        params.put(CameraSetDataKey.KEY_IS_BUSINESS, true);
        params.put(CameraSetDataKey.KEY_HIDE_SETTING_BTN, true);
        params.put(CameraSetDataKey.KEY_HIDE_RATIO_BTN, true);
        params.put(CameraSetDataKey.KEY_HIDE_PATCH_BTN, true);

		HomePageSite.CloneBusinessParams(params, m_inParams);
		params.put("channelValue",m_inParams.get("channelValue"));
		//MyFramework.SITE_Open(context, WayPageSite64.class, params, Framework2.ANIM_NONE);
        params.put(CameraSetDataKey.KEY_CAMERA_STICKER_STICKER_ID, 39165);
        params.put(CameraSetDataKey.KEY_CAMERA_STICKER_CATEGORY_ID, CameraStickerConfig.STICKER_CATEGORY_ID_NORMAL);
		MyFramework.SITE_BackAndOpen(context, null, CameraPageSite303.class, params, Framework2.ANIM_NONE);
	}
}
