package cn.poco.filterManage.site;

import android.content.Context;

import java.util.HashMap;

import cn.poco.filterManage.FilterMorePage;
import cn.poco.framework.BaseSite;
import cn.poco.framework.IPage;
import cn.poco.framework.MyFramework;
import cn.poco.framework.SiteID;
import cn.poco.framework2.Framework2;

/**
 * Created by: fwc
 * Date: 2017/3/29
 * 滤镜下载更多
 */
public class FilterMoreSite extends BaseSite {

	public FilterMoreSite() {
		super(SiteID.FILTER_DOWNLOAD_MORE);
	}

	@Override
	public IPage MakePage(Context context) {
		return new FilterMorePage(context, this);
	}

	public void onBack(Context context, HashMap<String, Object> params) {
		MyFramework.SITE_Back(context, params, Framework2.ANIM_NONE);
	}

	public void onFilterManagePage(Context context, HashMap<String, Object> params) {
		MyFramework.SITE_Popup(context, FilterManageSite.class, params, Framework2.ANIM_TRANSLATION_LEFT);
	}

	public void onFilterDetails(Context context, HashMap<String, Object> params) {
		MyFramework.SITE_Popup(context, FilterDetailSite2.class, params, Framework2.ANIM_TRANSLATION_LEFT);
	}
}
