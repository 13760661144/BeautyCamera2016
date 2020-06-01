package cn.poco.album.site;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

import cn.poco.camera.CameraSetDataKey;
import cn.poco.camera.site.CameraPageSite402;
import cn.poco.framework.MyFramework;
import cn.poco.framework2.Framework2;
import cn.poco.login.EditHeadIconImgPage;
import cn.poco.login.site.EditHeadIconImgPageSite402;

/**
 * Created by pocouser on 2017/12/27.
 */

public class AlbumSite402 extends AlbumSite
{
	@Override
	public void onPhotoSelected(Context context, Map<String, Object> params)
	{
		HashMap<String, Object> temp = new HashMap<>();
		temp.put("mode", EditHeadIconImgPage.REGISTER);
		temp.put("imgPath", MakeRotationImg((String[])params.get("imgs"), true));
		temp.put("info", m_inParams.get("info"));
		Object userId = m_inParams.get("userId");
		if(userId != null && !userId.equals("")) temp.put("userId", userId);
		Object token = m_inParams.get("tocken");
		if(token != null && !token.equals("")) temp.put("tocken", token);
		temp.put(EditHeadIconImgPage.BGPATH,m_inParams.get(EditHeadIconImgPage.BGPATH));
		MyFramework.SITE_Popup(context, EditHeadIconImgPageSite402.class, temp, Framework2.ANIM_NONE);
	}

	@Override
	public void onBack(Context context) {
		MyFramework.SITE_Back(context, null, Framework2.ANIM_TRANSLATION_TOP);
	}

	/**
	 * 打开Camera
	 */
	public void onOpenCamera(Context context) {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.putAll(CameraSetDataKey.GetRegisterTakePicture(false));
		params.put("info", m_inParams.get("info"));
		Object userId = m_inParams.get("userId");
		if(userId != null && !userId.equals("")) params.put("poco_id", userId);
		Object token = m_inParams.get("tocken");
		if(token != null && !token.equals("")) params.put("poco_token", token);
		params.put(EditHeadIconImgPage.BGPATH,m_inParams.get(EditHeadIconImgPage.BGPATH));
		MyFramework.SITE_Open(context, CameraPageSite402.class, params, Framework2.ANIM_NONE);
	}
}
