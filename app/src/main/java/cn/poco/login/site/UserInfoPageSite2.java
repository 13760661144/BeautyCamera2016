package cn.poco.login.site;

import android.content.Context;

import java.util.HashMap;

import cn.poco.album.site.AlbumSite23;
import cn.poco.camera.CameraSetDataKey;
import cn.poco.camera.site.CameraPageSite13;
import cn.poco.framework.MyFramework;
import cn.poco.framework2.Framework2;
import cn.poco.login.EditHeadIconImgPage;

/**
 * 积分打开用户信息
 */
public class UserInfoPageSite2 extends UserInfoPageSite
{
    @Override
	public void onCamera(String id, String token,String bgPath,Context context)
	{
		//只有拍照
		HashMap<String, Object> datas = new HashMap<String, Object>();
		datas.putAll(CameraSetDataKey.GetRegisterTakePicture(false));
		datas.put("poco_id", id);
		datas.put("poco_token", token);
		if(bgPath != null && bgPath.length() > 0)
		{
			datas.put(EditHeadIconImgPage.BGPATH,bgPath);
		}
		MyFramework.SITE_Popup(context, CameraPageSite13.class, datas, Framework2.ANIM_NONE);
	}

	@Override
	public void onChooseHeadBmp(String id, String tocken,String bgPath,Context context)
	{
		HashMap<String, Object> datas = new HashMap<>();
		datas.put("userId",id);
		datas.put("tocken",tocken);
		if(bgPath != null && bgPath.length() > 0)
		{
			datas.put(EditHeadIconImgPage.BGPATH,bgPath);
		}
		datas.put("from_camera",true);
		MyFramework.SITE_Popup(context, AlbumSite23.class, datas, Framework2.ANIM_TRANSLATION_TOP);
	}
	@Override
	public void onBack(Context context)
	{
		MyFramework.SITE_Back(context, null, Framework2.ANIM_NONE);
	}
}
