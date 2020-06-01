package cn.poco.arWish.site;

import android.content.Context;

import java.util.HashMap;

import cn.poco.arWish.ARWishesCameraPage;
import cn.poco.arWish.ArVideoPage;
import cn.poco.framework.BaseSite;
import cn.poco.framework.IPage;
import cn.poco.framework.MyFramework;
import cn.poco.framework.SiteID;
import cn.poco.framework2.Framework2;
import cn.poco.webview.site.WebViewPageSite;

/**
 * Created by zwq on 2018/01/22 13:46.<br/><br/>
 */

public class ARWishesCameraPageSite extends BaseSite {

    public ARWishesCameraPageSite() {
        super(SiteID.AR_WISHES_CAMERA);
    }

    @Override
    public IPage MakePage(Context context) {
        return new ARWishesCameraPage(context, this);
    }

    public void onBack(Context context) {
        MyFramework.SITE_Back(context, null, Framework2.ANIM_NONE);
    }

    public void openCameraPermissionsHelper(Context context, HashMap<String, Object> params) {
        MyFramework.SITE_Popup(context, WebViewPageSite.class, params, Framework2.ANIM_TRANSLATION_TOP);
    }

    public void openVideo(Context context, String url)
    {
        HashMap<String, Object> params = new HashMap<>();
        params.put(ArVideoPage.KEY_VIDEO_PATH, url);
        MyFramework.SITE_Popup(context, ArVideoPageSite.class, params, Framework2.ANIM_NONE);
    }

    /**
     * 藏视频
     * @param context
     * @param params
     */
    public void hideVideo(Context context, HashMap<String, Object> params) {
        MyFramework.SITE_ClosePopup(context, params, Framework2.ANIM_NONE);
    }
}
