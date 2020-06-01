package cn.poco.MaterialMgr2.site;

import android.content.Context;

import java.util.HashMap;

import cn.poco.album.site.AlbumSite7;
import cn.poco.beautify4.UiMode;
import cn.poco.framework.MyFramework;
import cn.poco.framework2.Framework2;
import cn.poco.resource.ResType;

/**
 * 美化打开素材中心
 */
public class ThemeIntroPageSite2 extends ThemeIntroPageSite
{
	@Override
	public void OnResourceUse(Context context, HashMap<String, Object> params)
	{
		ResType type = (ResType)params.get("type");
		int id = (Integer)params.get("id");
		HashMap<String, Object> temp;
		switch(type)
		{
			case FRAME:
			case FRAME2:
				temp = new HashMap<>();
				temp.put(TYPE, type);
				temp.put(ID, id);
				temp.put("from_camera", true);
				MyFramework.SITE_ClosePopup2(context, temp, -1, Framework2.ANIM_NONE);
				break;
			case DECORATE:
				temp = new HashMap<>();
				temp.put(TYPE, type);
				temp.put(ID, id);
				temp.put("from_camera", true);
				MyFramework.SITE_ClosePopup2(context, temp, -1, Framework2.ANIM_NONE);
				break;
			case GLASS:
				temp = new HashMap<>();
				temp.put(TYPE, type);
				temp.put(ID, id);
				temp.put("from_camera", true);
				MyFramework.SITE_ClosePopup2(context, temp, -1, Framework2.ANIM_NONE);
				break;
			case MOSAIC:
				temp = new HashMap<>();
				temp.put(TYPE, type);
				temp.put(ID, id);
				temp.put("from_camera", true);
				MyFramework.SITE_ClosePopup2(context, temp, -1, Framework2.ANIM_NONE);
				break;

			case MAKEUP_GROUP:
				temp = new HashMap<>();
				temp.put(TYPE, UiMode.CAIZHUANG.GetValue());
				temp.put(ID, id);
				temp.put("from_camera", true);
				MyFramework.SITE_Popup(context, AlbumSite7.class, temp, Framework2.ANIM_NONE);
				break;

			default:
				break;
		}
	}
}
