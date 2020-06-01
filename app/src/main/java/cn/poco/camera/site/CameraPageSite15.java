package cn.poco.camera.site;

import android.content.Context;

import java.util.HashMap;

import cn.poco.camera.ImageFile2;
import cn.poco.camera.RotationImg2;
import cn.poco.framework.MyFramework;
import cn.poco.framework2.Framework2;

/**
 * 网页调用拍照
 */
public class CameraPageSite15 extends CameraPageSite
{
	@Override
	public void onTakePicture(Context context, HashMap<String, Object> params)
	{
		RotationImg2[] imgs = ((ImageFile2)params.get("img_file")).SaveImg2(context);
		HashMap<String,Object> datas = new HashMap<>();
		datas.put("imgs", imgs);
		MyFramework.SITE_ClosePopup(context, datas, Framework2.ANIM_NONE);
	}
}
