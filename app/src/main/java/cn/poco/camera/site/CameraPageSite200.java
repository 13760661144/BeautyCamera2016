package cn.poco.camera.site;

import android.content.Context;

import cn.poco.framework.MyFramework;
import cn.poco.framework2.Framework2;
import cn.poco.home.site.HomePageSite;

/**
 * 软件直接打开镜头
 */
public class CameraPageSite200 extends CameraPageSite {
    @Override
    public void onBack(Context context) {
        this.onBack(context, false);
    }

    @Override
    public void onBack(Context context, boolean hasAnim) {
        MyFramework.SITE_Open(context, true, HomePageSite.class, null, Framework2.ANIM_NONE);
    }
}
