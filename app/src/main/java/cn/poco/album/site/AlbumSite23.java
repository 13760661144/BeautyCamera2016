package cn.poco.album.site;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

import cn.poco.framework.MyFramework;
import cn.poco.framework2.Framework2;
import cn.poco.login.EditHeadIconImgPage;
import cn.poco.login.UserInfoPage;
import cn.poco.login.site.EditHeadIconImgPageSite2;

/**
 * Created by Raining on 2016/12/9.
 * 积分打开用户信息，选择头像
 */

public class AlbumSite23 extends AlbumSite
{
	@Override
	public void onPhotoSelected(Context context, Map<String, Object> params)
	{
		HashMap<String, Object> temp = new HashMap<>();
		temp.put("mode", EditHeadIconImgPage.OTHER);
		temp.put("imgPath", MakeRotationImg((String[])params.get("imgs"), true));
		temp.put("userId",m_inParams.get("userId"));
		temp.put("tocken",m_inParams.get("tocken"));
		temp.put(EditHeadIconImgPage.BGPATH,m_inParams.get(EditHeadIconImgPage.BGPATH));
		MyFramework.SITE_Popup(context, EditHeadIconImgPageSite2.class, temp, Framework2.ANIM_NONE);
	}

	@Override
	public void onBack(Context context) {
		HashMap<String,Object> params = new HashMap<>();
		params.put("m_mode" , UserInfoPage.NONE);
		params.put("m_opera", UserInfoPage.ISBACK);
		MyFramework.SITE_Back(context, params, Framework2.ANIM_TRANSLATION_TOP);
	}
}
