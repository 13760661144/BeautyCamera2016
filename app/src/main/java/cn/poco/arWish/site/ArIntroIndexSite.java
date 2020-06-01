package cn.poco.arWish.site;

import android.content.Context;

import java.util.HashMap;

import cn.poco.arWish.ArIntroIndexPage;
import cn.poco.arWish.ArVideoPage;
import cn.poco.framework.BaseSite;
import cn.poco.framework.IPage;
import cn.poco.framework.MyFramework;
import cn.poco.framework.SiteID;
import cn.poco.framework2.Framework2;

/**
 * Created by admin on 2018/1/18.
 */

public class ArIntroIndexSite extends BaseSite
{
    public ArIntroIndexSite()
    {
        super(SiteID.AR_INTRO_INDEX);
    }

    @Override
    public IPage MakePage(Context context)
    {
        return new ArIntroIndexPage(context, this);
    }

    public void onBack(Context context)
    {
        MyFramework.SITE_Back(context, null, Framework2.ANIM_NONE);
    }

    public void goToCreateAr(Context context)
    {
        MyFramework.SITE_Open(context, ARHideWishPrePageSite.class, null, Framework2.ANIM_NONE);
    }

    public void goToFindAr(Context context)
    {
        HashMap<String, Object> params = new HashMap<>();
        params.put("pageType", 1);
        MyFramework.SITE_Open(context, ARWishesCameraPageSite.class, params, Framework2.ANIM_NONE);
    }

    public void goToPlayIntro(Context context)
    {
        HashMap<String, Object> params = new HashMap<>();
        params.put(ArVideoPage.KEY_VIDEO_PATH, ArIntroIndexPage.INTRO_VIDEO_PATH);
//        params.put(ArVideoPage.KEY_VIDEO_THUMB, ArIntroIndexPage.INTRO_THUMB_PATH);
        MyFramework.SITE_Open(context, ArVideoPageSite.class, params, Framework2.ANIM_TRANSLATION_LEFT);
    }
}
