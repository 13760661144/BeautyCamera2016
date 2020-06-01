package cn.poco.arWish.site;

import android.content.Context;

import java.util.HashMap;

import cn.poco.arWish.FindWishPage;
import cn.poco.framework.BaseSite;
import cn.poco.framework.IPage;
import cn.poco.framework.MyFramework;
import cn.poco.framework.SiteID;
import cn.poco.framework2.Framework2;

/**
 * Created by Anson on 2018/1/19.
 */

public class FindWishPageSite extends BaseSite {
    public FindWishPageSite() {
        super(SiteID.FIND_ARWISH);
    }

    @Override
    public IPage MakePage(Context context) {
        return new FindWishPage(context, this);
    }

    public void OnBack(Context context) {
        MyFramework.SITE_Back(context, null, Framework2.ANIM_TRANSLATION_RIGHT);
    }

    public void openARCamera(Context context, HashMap<String, Object> params) {
        MyFramework.SITE_Open(context, ARWishesCameraPageSite.class, params, Framework2.ANIM_NONE);
    }
}
