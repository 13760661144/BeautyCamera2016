package cn.poco.live.site;

import android.content.Context;

import java.util.HashMap;

import cn.poco.filterManage.site.FilterMoreSite;
import cn.poco.framework.BaseSite;
import cn.poco.framework.IPage;
import cn.poco.framework.MyFramework;
import cn.poco.framework.SiteID;
import cn.poco.framework2.Framework2;
import cn.poco.home.site.HomePageSite;
import cn.poco.live.LivePage;
import cn.poco.login.site.LoginPageSite11;
import cn.poco.webview.site.WebViewPageSite;

/**
 * Created by zwq on 2018/01/15 10:30.<br/><br/>
 */

public class LivePageSite extends BaseSite {

    public LivePageSite() {
        super(SiteID.LIVE_CAMERA);
    }

    @Override
    public IPage MakePage(Context context) {
        return new LivePage(context, this);
    }

    public void onBack(Context context) {
        MyFramework.SITE_Back(context, null, Framework2.ANIM_NONE);
    }

    public void onBackToHome(Context context) {
        MyFramework.SITE_BackTo(context, HomePageSite.class, null, Framework2.ANIM_NONE);
    }

    public void openCameraPermissionsHelper(Context context, HashMap<String, Object> params) {
        MyFramework.SITE_Popup(context, WebViewPageSite.class, params, Framework2.ANIM_TRANSLATION_TOP);
    }

    public void uploadShapeLogin(Context context) {
        MyFramework.SITE_Popup(context, LoginPageSite11.class, null, Framework2.ANIM_NONE);
    }

    public void onFilterDownload(Context context)
    {
        HashMap<String, Object> params = new HashMap<>();
        params.put("from_live_page", true);
        MyFramework.SITE_Popup(context, FilterMoreSite.class, params, Framework2.ANIM_NONE);
    }
}
