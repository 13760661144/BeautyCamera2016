package cn.poco.beauty.site;

import android.content.Context;

import java.util.HashMap;

import cn.poco.beauty.BeautyPage;
import cn.poco.framework.BaseSite;
import cn.poco.framework.IPage;
import cn.poco.framework.MyFramework;
import cn.poco.framework.SiteID;
import cn.poco.framework2.Framework2;

/**
 * Created by: fwc
 * Date: 2016/12/20
 * 美颜
 */
public class BeautySite extends BaseSite {

	public BeautySite() {
		super(SiteID.BEAUTIFY);
	}

	@Override
	public IPage MakePage(Context context) {
		return new BeautyPage(context, this);
	}

	/**
	 * @param params img:Bitmap
	 */
	public void OnSave(Context context, HashMap<String, Object> params) {
		MyFramework.SITE_Back(context, params, Framework2.ANIM_NONE);
	}

	/**
	 * @param params img:Bitmap
	 */
	public void OnBack(Context context, HashMap<String, Object> params) {
		MyFramework.SITE_Back(context, null, Framework2.ANIM_NONE);
	}
}
