package cn.poco.camera.site;

import android.content.Context;

import cn.poco.framework.BaseSite;
import cn.poco.framework.MyFramework;
import cn.poco.framework2.Framework2;
import cn.poco.home.site.HomePageSite;

/**
 * 美型美化入口->相机->（返回回到首页或首页打开的模块）
 */
public class CameraPageSite17 extends CameraPageSite10 {
    @Override
    public void onBack(Context context) {
        this.onBack(context, false);
    }

    @Override
    public void onBack(Context context, boolean hasAnim) {
        BaseSite baseSite = MyFramework.GetLinkSite(context, HomePageSite.class);
        if (baseSite == null) {
            MyFramework.SITE_Open(context, true, HomePageSite.class, null, Framework2.ANIM_NONE);
        } else {
            MyFramework.SITE_BackTo(context, HomePageSite.class, null, Framework2.ANIM_NONE);
        }
    }
}
