package cn.poco.filterManage.site;

import android.content.Context;

import java.util.HashMap;

import cn.poco.framework.MyFramework;
import cn.poco.framework2.Framework2;

/**
 * Created by: fwc
 * Date: 2017/4/5
 * 美颜美图-滤镜-下载更多-滤镜详情
 */
public class FilterDetailSite2 extends FilterDetailSite {

	@Override
	public void onResourceUse(Context context, HashMap<String, Object> params) {
		MyFramework.SITE_ClosePopup2(context, params, 2, Framework2.ANIM_NONE);
	}
}
