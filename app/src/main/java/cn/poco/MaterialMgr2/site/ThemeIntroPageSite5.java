package cn.poco.MaterialMgr2.site;

import android.content.Context;

import java.util.HashMap;

import cn.poco.album.site.AlbumSite81;
import cn.poco.beautify4.UiMode;
import cn.poco.filterManage.site.FilterDetailSite5;
import cn.poco.framework.MyFramework;
import cn.poco.framework2.Framework2;
import cn.poco.resource.ResType;

/**
 * Created by admin on 2017/4/20.
 * 社区调用素材中心
 */

public class ThemeIntroPageSite5 extends ThemeIntroPageSite
{
	@Override
	public void OnResourceUse(Context context,HashMap<String, Object> params)
	{
		ResType type = (ResType)params.get("type");
		int id = (Integer)params.get("id");
		HashMap<String, Object> temp;
		switch(type)
		{
			case FRAME:
			case FRAME2:
				temp = new HashMap<>();
				temp.put(TYPE, UiMode.XIANGKUANG.GetValue());
				temp.put(ID, id);
				temp.put("from_camera", true);
				MyFramework.SITE_OpenAndClosePopup(context, AlbumSite81.class, temp, Framework2.ANIM_NONE);
				break;
			case DECORATE:
				temp = new HashMap<>();
				temp.put(TYPE, UiMode.TIETU.GetValue());
				temp.put(ID, id);
				temp.put("from_camera", true);
				MyFramework.SITE_OpenAndClosePopup(context, AlbumSite81.class, temp, Framework2.ANIM_NONE);
				break;
			case GLASS:
				temp = new HashMap<>();
				temp.put(TYPE, UiMode.MAOBOLI.GetValue());
				temp.put(ID, id);
				temp.put("from_camera", true);
				MyFramework.SITE_OpenAndClosePopup(context, AlbumSite81.class, temp, Framework2.ANIM_NONE);
				break;
			case MOSAIC:
				temp = new HashMap<>();
				temp.put(TYPE, UiMode.MASAIKE.GetValue());
				temp.put(ID, id);
				temp.put("from_camera", true);
				MyFramework.SITE_OpenAndClosePopup(context, AlbumSite81.class, temp, Framework2.ANIM_NONE);
				break;
			case BRUSH:
				temp = new HashMap<>();
				temp.put(TYPE, UiMode.ZHIJIANMOFA.GetValue());
				temp.put(ID, id);
				temp.put("from_camera", true);
				MyFramework.SITE_OpenAndClosePopup(context, AlbumSite81.class, temp, Framework2.ANIM_NONE);
				break;
			case MAKEUP_GROUP:
				temp = new HashMap<>();
				temp.put(TYPE, UiMode.CAIZHUANG.GetValue());
				temp.put(ID, id);
				temp.put("from_camera", true);
				MyFramework.SITE_OpenAndClosePopup(context, AlbumSite81.class, temp, Framework2.ANIM_NONE);
				break;
			case FILTER:
				temp = new HashMap<>();
				temp.put(TYPE, UiMode.LVJING.GetValue());
				temp.put(ID, id);
				temp.put("from_camera", true);
				MyFramework.SITE_OpenAndClosePopup(context, AlbumSite81.class, temp, Framework2.ANIM_NONE);
				break;

			default:
				break;
		}
	}

	@Override
	public void onFilterDetails(Context context,HashMap<String, Object> params)
	{
		MyFramework.SITE_Popup(context, FilterDetailSite5.class, params, Framework2.ANIM_TRANSLATION_LEFT);
	}
}
