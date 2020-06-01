package cn.poco.live.site;

import android.content.Context;

import java.util.HashMap;

import cn.poco.framework.BaseSite;
import cn.poco.framework.IPage;
import cn.poco.framework.MyFramework;
import cn.poco.framework.SiteID;
import cn.poco.framework2.Framework2;
import cn.poco.live.LiveIntroPage;
import cn.poco.webview.site.WebViewPageSite;

/**
 * Created by zwq on 2018/01/30 11:30.<br/><br/>
 */

public class LiveIntroPageSite extends BaseSite {

    public LiveIntroPageSite() {
        super(SiteID.LIVE_INTRO);
    }

    @Override
    public IPage MakePage(Context context) {
        return new LiveIntroPage(context, this);
    }

    public void onBack(Context context) {
        MyFramework.SITE_Back(context, null, Framework2.ANIM_NONE);
    }

    public void openLiveHelper(Context context, HashMap<String, Object> params) {
        MyFramework.SITE_Popup(context, WebViewPageSite.class, params, Framework2.ANIM_NONE);
    }

    public void openLiveCamera(Context context, HashMap<String, Object> params) {
        MyFramework.SITE_Open(context, LivePageSite.class, params, Framework2.ANIM_NONE);
    }

}
