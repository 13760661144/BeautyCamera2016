package cn.poco.filterManage.site;

import android.content.Context;

import java.util.HashMap;

import cn.poco.album.site.AlbumSite7;
import cn.poco.filterManage.FilterDetailPage;
import cn.poco.framework.BaseSite;
import cn.poco.framework.IPage;
import cn.poco.framework.MyFramework;
import cn.poco.framework.SiteID;
import cn.poco.framework2.Framework2;
import cn.poco.login.site.LoginPageSite1;

/**
 * Created by: fwc
 * Date: 2017/3/27
 * 滤镜详情
 */
public class FilterDetailSite extends BaseSite {

	public FilterDetailSite() {
		super(SiteID.FILTER_DETAIL);
	}

	@Override
	public IPage MakePage(Context context) {
		return new FilterDetailPage(context, this);
	}

	public void onBack(Context context, HashMap<String, Object> params) {
		MyFramework.SITE_Back(context, params, Framework2.ANIM_TRANSLATION_LEFT);
	}

	public void onLogin(Context context) {
		MyFramework.SITE_Popup(context, LoginPageSite1.class, null, Framework2.ANIM_NONE);
	}

	public void onResourceUse(Context context, HashMap<String, Object> params) {
		params.put("from_camera", true);
		MyFramework.SITE_Popup(context, AlbumSite7.class, params, Framework2.ANIM_NONE);
	}
}
