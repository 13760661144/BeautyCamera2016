package cn.poco.camera.site;

import android.content.Context;

import java.util.HashMap;

import cn.poco.beautify4.site.Beautify4PageSite2;
import cn.poco.camera.CameraSetDataKey;
import cn.poco.camera.ImageFile2;
import cn.poco.camera.RotationImg2;
import cn.poco.framework.DataKey;
import cn.poco.framework.MyFramework;
import cn.poco.framework2.Framework2;

/**
 * Created by POCO on 2017/4/21.
 * 素材中心马上使用-拍照-美化
 */

public class CameraPageSite16 extends CameraPageSite
{
	@Override
	public void onTakePicture(Context context, HashMap<String, Object> params)
	{
		RotationImg2[] imgs = ((ImageFile2)params.get("img_file")).SaveImg2(context);

		HashMap<String, Object> temp = new HashMap<>();
		temp.put("imgs", imgs);
		temp.put(DataKey.BEAUTIFY_DEF_OPEN_PAGE, m_inParams.get(DataKey.BEAUTIFY_DEF_OPEN_PAGE));
		temp.put(DataKey.BEAUTIFY_DEF_SEL_URI, m_inParams.get(DataKey.BEAUTIFY_DEF_SEL_URI));
		temp.put("only_one_pic", true);
		temp.put(CameraSetDataKey.KEY_CAMERA_FLASH_MODE, params.get(CameraSetDataKey.KEY_CAMERA_FLASH_MODE));
		temp.put(CameraSetDataKey.KEY_CAMERA_FRONT_SPLASH_MASK, params.get(CameraSetDataKey.KEY_CAMERA_FRONT_SPLASH_MASK));

		if (MyFramework.GetCurrentIndex(context) > 0) {
			MyFramework.SITE_OpenAndClosePopup(context, false, -1, Beautify4PageSite2.class, temp, Framework2.ANIM_NONE);
		} else {
			MyFramework.SITE_Open(context, Beautify4PageSite2.class, temp, Framework2.ANIM_NONE);
		}
	}
}
