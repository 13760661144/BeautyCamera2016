package cn.poco.album.site;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

import cn.poco.framework.MyFramework;
import cn.poco.framework2.Framework2;

/**
 * Created by: fwc
 * Date: 2017/1/4
 * 云相册选图
 */
public class AlbumSite2 extends AlbumSite {

	@Override
	public void onPhotoSelected(Context context, Map<String, Object> params) {
//		params.put("imgs", MakeRotationImg((String[])params.get("imgs"), true));
		MyFramework.SITE_ClosePopup(context, (HashMap<String, Object>)params, Framework2.ANIM_NONE);
	}
}
