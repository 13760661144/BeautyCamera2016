package cn.poco.album.site;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

import cn.poco.framework.MyFramework;
import cn.poco.framework2.Framework2;

/**
 * Created by Raining on 2016/12/9.
 * 弹出,不复制图片,返回String[]
 */

public class AlbumSite19 extends AlbumSite
{
	@Override
	public void onPhotoSelected(Context context, Map<String, Object> params)
	{
		MyFramework.SITE_ClosePopup(context, (HashMap<String, Object>)params, Framework2.ANIM_NONE);
	}
}
