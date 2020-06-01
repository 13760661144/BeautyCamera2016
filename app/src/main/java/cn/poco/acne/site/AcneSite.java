package cn.poco.acne.site;

import android.content.Context;

import java.util.HashMap;

import cn.poco.acne.AcnePage;
import cn.poco.framework.BaseSite;
import cn.poco.framework.IPage;
import cn.poco.framework.MyFramework;
import cn.poco.framework.SiteID;
import cn.poco.framework2.Framework2;

/**
 * Created by: fwc
 * Date: 2016/11/29
 * 祛痘
 */
public class AcneSite extends BaseSite {

	public AcneSite() {
		super(SiteID.ACNE);
	}

	@Override
	public IPage MakePage(Context context) {
		return new AcnePage(context, this);
	}

	/**
	 * @param params img:Bitmap
	 */
	public void onBack(Context context, HashMap<String, Object> params) {
		MyFramework.SITE_Back(context, null, Framework2.ANIM_NONE);
	}

	/**
	 * @param params img:Bitmap
	 */
	public void OnSave(Context context, HashMap<String, Object> params) {
		MyFramework.SITE_Back(context, params, Framework2.ANIM_NONE);
	}
}
