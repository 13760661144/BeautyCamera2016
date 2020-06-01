package cn.poco.camera.site;

import android.content.Context;

import java.util.HashMap;

import cn.poco.camera3.CameraPageV3;
import cn.poco.framework.IPage;
import cn.poco.framework.MyFramework;
import cn.poco.framework2.Framework2;
import cn.poco.webview.site.WebViewPageSite;

/**
 * 趣玩-动态贴纸
 */
public class CameraPageSite1 extends CameraPageSite {

    public CameraPageSite1() {
        super();
    }

    @Override
    public IPage MakePage(Context context) {
        return new CameraPageV3(context, this);
    }

    public void showLimitResInfo(Context context, HashMap<String, Object> params) {
        MyFramework.SITE_Open(context, WebViewPageSite.class, params, Framework2.ANIM_TRANSLATION_LEFT);
    }

}
