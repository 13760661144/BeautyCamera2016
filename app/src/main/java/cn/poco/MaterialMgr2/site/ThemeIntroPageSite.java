package cn.poco.MaterialMgr2.site;

import android.content.Context;

import java.util.HashMap;

import cn.poco.MaterialMgr2.ThemeIntroPage;
import cn.poco.album.site.AlbumSite7;
import cn.poco.beautify4.UiMode;
import cn.poco.filterManage.site.FilterDetailSite;
import cn.poco.framework.BaseSite;
import cn.poco.framework.IPage;
import cn.poco.framework.MyFramework;
import cn.poco.framework.SiteID;
import cn.poco.framework2.Framework2;
import cn.poco.login.site.LoginPageSite1;
import cn.poco.resource.ResType;

/**
 * 主题详情
 */
public class ThemeIntroPageSite extends BaseSite
{
	public ThemeIntroPageSite()
	{
		super(SiteID.THEME_INTRO);
	}

	@Override
	public IPage MakePage(Context context)
	{
		return new ThemeIntroPage(context, this);
	}

	public void OnBack(Context context, HashMap<String, Object> params)
	{
		MyFramework.SITE_ClosePopup(context, params, Framework2.ANIM_TRANSLATION_LEFT);
	}

	public static final String TYPE = "material_type"; //打开哪个页面(int)
	public static final String ID = "material_id"; //打开哪个id的素材(int)

	/**
	 * @param params type ResType
	 *               id int
	 */
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
				temp.put(TYPE, UiMode.XIANGKUANG.GetValue());
				temp.put(ID, id);
				temp.put("from_camera", true);
				MyFramework.SITE_Popup(context, AlbumSite7.class, temp, Framework2.ANIM_NONE);
				break;
			case DECORATE:
				temp = new HashMap<>();
				temp.put(TYPE, UiMode.TIETU.GetValue());
				temp.put(ID, id);
				temp.put("from_camera", true);
				MyFramework.SITE_Popup(context, AlbumSite7.class, temp, Framework2.ANIM_NONE);
				break;
			case GLASS:
				temp = new HashMap<>();
				temp.put(TYPE, UiMode.MAOBOLI.GetValue());
				temp.put(ID, id);
				temp.put("from_camera", true);
				MyFramework.SITE_Popup(context, AlbumSite7.class, temp, Framework2.ANIM_NONE);
				break;
			case MOSAIC:
				temp = new HashMap<>();
				temp.put(TYPE, UiMode.MASAIKE.GetValue());
				temp.put(ID, id);
				temp.put("from_camera", true);
				MyFramework.SITE_Popup(context, AlbumSite7.class, temp, Framework2.ANIM_NONE);
				break;
			case BRUSH:
				temp = new HashMap<>();
				temp.put(TYPE, UiMode.ZHIJIANMOFA.GetValue());
				temp.put(ID, id);
				temp.put("from_camera", true);
				MyFramework.SITE_Popup(context, AlbumSite7.class, temp, Framework2.ANIM_NONE);
				break;
			case MAKEUP_GROUP:
				temp = new HashMap<>();
				temp.put(TYPE, UiMode.CAIZHUANG.GetValue());
				temp.put(ID, id);
				temp.put("from_camera", true);
				MyFramework.SITE_Popup(context, AlbumSite7.class, temp, Framework2.ANIM_NONE);
				break;
			case FILTER:
				temp = new HashMap<>();
				temp.put(TYPE, UiMode.LVJING.GetValue());
				temp.put(ID, id);
				temp.put("from_camera", true);
				MyFramework.SITE_Popup(context, AlbumSite7.class, temp, Framework2.ANIM_NONE);
				break;

			default:
				break;
		}
	}

	public void OnLogin(Context context)
	{
		MyFramework.SITE_Popup(context, LoginPageSite1.class, null, Framework2.ANIM_NONE);
	}

	public void onFilterDetails(Context context, HashMap<String, Object> params) {
		MyFramework.SITE_Popup(context, FilterDetailSite.class, params, Framework2.ANIM_TRANSLATION_LEFT);
	}
}
