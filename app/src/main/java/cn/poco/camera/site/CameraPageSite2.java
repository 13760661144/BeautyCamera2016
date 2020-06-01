package cn.poco.camera.site;

import android.content.Context;

import cn.poco.framework.MyFramework;
import cn.poco.framework2.Framework2;
import cn.poco.home.site.HomePageSite;

/**
 * 拍照到滤镜,分享页调用,后退特殊处理
 */
public class CameraPageSite2 extends CameraPageSite {
    @Override
    public void onBack(Context context) {
        this.onBack(context, false);
    }

    @Override
    public void onBack(Context context, boolean hasAnim) {
        MyFramework.SITE_Open(context, true, HomePageSite.class, null, Framework2.ANIM_NONE);
    }
}
