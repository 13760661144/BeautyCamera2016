package cn.poco.nose.site;

import android.content.Context;

import java.util.HashMap;

import cn.poco.framework.BaseSite;
import cn.poco.framework.IPage;
import cn.poco.framework.MyFramework;
import cn.poco.framework.SiteID;
import cn.poco.framework2.Framework2;
import cn.poco.makeup.site.ChangePointPageSite;
import cn.poco.nose.NosePage;

/**
 * Created by: fwc
 * Date: 2016/12/2
 * 高鼻梁
 */
public class NoseSite extends BaseSite {

	public NoseSite() {
		super(SiteID.HIGH_NOSE);
	}

	@Override
	public IPage MakePage(Context context) {
		return new NosePage(context, this);
	}

	/**
	 * @param params img:Bitmap
	 */
	public void onBack(Context context, HashMap<String, Object> params) {
		MyFramework.SITE_Back(context, null, Framework2.ANIM_NONE);
	}

	/**
	 * @param params
	 * img:Bitmap
	 */
	public void OnSave(Context context, HashMap<String, Object> params) {
		MyFramework.SITE_Back(context, params, Framework2.ANIM_NONE);
	}

	public void openFixPage(Context context, HashMap<String, Object> params) {
		MyFramework.SITE_Popup(context, ChangePointPageSite.class, params, Framework2.ANIM_NONE);
	}
}
