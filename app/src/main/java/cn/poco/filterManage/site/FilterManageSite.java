package cn.poco.filterManage.site;

import android.content.Context;

import java.util.HashMap;

import cn.poco.filterManage.FilterManagePage;
import cn.poco.framework.BaseSite;
import cn.poco.framework.IPage;
import cn.poco.framework.MyFramework;
import cn.poco.framework.SiteID;
import cn.poco.framework2.Framework2;

/**
 * Created by: fwc
 * Date: 2017/3/29
 * 滤镜素材管理
 */
public class FilterManageSite extends BaseSite {

	public FilterManageSite() {
		super(SiteID.FILTER_MANAGE);
	}

	@Override
	public IPage MakePage(Context context) {
		return new FilterManagePage(context, this);
	}

	public void onBack(Context context, HashMap<String, Object> params) {
		MyFramework.SITE_Back(context, params, Framework2.ANIM_TRANSLATION_LEFT);
	}
}
