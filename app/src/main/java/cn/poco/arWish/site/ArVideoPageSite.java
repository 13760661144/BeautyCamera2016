package cn.poco.arWish.site;

import android.content.Context;

import cn.poco.arWish.ArVideoPage;
import cn.poco.framework.BaseSite;
import cn.poco.framework.IPage;
import cn.poco.framework.MyFramework;
import cn.poco.framework.SiteID;
import cn.poco.framework2.Framework2;

/**
 * Created by admin on 2018/1/18.
 */

public class ArVideoPageSite extends BaseSite {
    public ArVideoPageSite()
    {
        super(SiteID.AR_VIDEO);
    }

    @Override
    public IPage MakePage(Context context)
    {
        return new ArVideoPage(context, this);
    }

    public void onBack(Context context)
    {
        MyFramework.SITE_Back(context, null, Framework2.ANIM_TRANSLATION_LEFT);
    }

    public void goToCreateAr(Context context) {
        MyFramework.SITE_Open(context, false, ArIntroCreateSite.class, null, Framework2.ANIM_TRANSLATION_LEFT);
    }

    public void goToFindAr(Context context) {
        MyFramework.SITE_Open(context, FindIntroPageSite.class, null, Framework2.ANIM_TRANSLATION_LEFT);
    }
}
