package cn.poco.album.site;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

import cn.poco.camera.CameraSetDataKey;
import cn.poco.camera.site.CameraPageSite;
import cn.poco.camera.site.CameraPageSite65;
import cn.poco.face.FaceDataV2;
import cn.poco.filterBeautify.FilterBeautifyProcessor;
import cn.poco.framework.BaseSite;
import cn.poco.framework.MyFramework;
import cn.poco.framework2.Framework2;
import cn.poco.makeup.site.MakeupSPageSite;


/**
 * 一键萌妆 再玩一次流程
 */
public class AlbumSite65 extends AlbumSite {

	public void onOpenCamera(Context context) {
		HashMap<String, Object> params = new HashMap<String, Object>();
		BaseSite baseSite = MyFramework.GetLinkSite(context, CameraPageSite.class);
		if (baseSite != null) {
			params = (HashMap<String, Object>) baseSite.m_inParams.clone();
		} else {
			params.putAll(CameraSetDataKey.GetMakeupAndTakePicture());
			params.put(CameraSetDataKey.KEY_HIDE_PHOTO_PICKER_BTN, true);
			params.put(CameraSetDataKey.KEY_IS_FILTER_BEAUTITY_PROCESS, true);//执行滤镜效果处理
			params.put(CameraSetDataKey.KEY_FILTER_BEAUTITY_PROCESS_MASK, FilterBeautifyProcessor.FILTER_BEAUTY);
		}
		MyFramework.SITE_Open(context, CameraPageSite65.class, params, Framework2.ANIM_NONE);
	}

	@Override
	public void onBack(Context context) {
		FaceDataV2.ResetData();
		MyFramework.SITE_ClosePopup(context,null,Framework2.ANIM_NONE);
	}

	/**
	 * 选择好图片后回调
	 *
	 * @param params 参数
	 *               imgs: String[] 图片的路径
	 */
	public void onPhotoSelected(Context context, Map<String, Object> params) {
		HashMap<String, Object> temp = new HashMap<>();
		temp.put("imgs", MakeRotationImg((String[])params.get("imgs"), true));
		MyFramework.SITE_Open(context, MakeupSPageSite.class, temp, Framework2.ANIM_NONE);
	}

}
