package cn.poco.pendant.site;

import android.content.Context;

import java.util.HashMap;

import cn.poco.MaterialMgr2.site.DownloadMorePageSite;
import cn.poco.framework.BaseSite;
import cn.poco.framework.IPage;
import cn.poco.framework.MyFramework;
import cn.poco.framework.SiteID;
import cn.poco.framework2.Framework2;
import cn.poco.login.site.LoginPageSite1;
import cn.poco.pendant.PendantPage;
import cn.poco.resource.ResType;

/**
 * Created by: fwc
 * Date: 2016/11/18
 * 贴图
 */
public class PendantSite extends BaseSite {

    public PendantSite() {
        super(SiteID.PENDANT);
    }

    @Override
    public IPage MakePage(Context context) {
        return new PendantPage(context, this);
    }

    /**
     * @param params img:Bitmap
     */
    public void onBack(Context context, HashMap<String, Object> params) {
        MyFramework.SITE_ClosePopup(context, null, Framework2.ANIM_NONE);
    }

    /**
     * @param params img:Bitmap
     */
    public void onSave(Context context, HashMap<String, Object> params) {
        MyFramework.SITE_Back(context, params, Framework2.ANIM_NONE);
    }


    public void openDownloadMore(Context context, ResType type) {

        HashMap<String, Object> params = new HashMap<>();
        params.put("type", type);
        MyFramework.SITE_Popup(context, DownloadMorePageSite.class, params, Framework2.ANIM_NONE);
    }

    public void onLogin(Context context) {
        MyFramework.SITE_Popup(context, LoginPageSite1.class, null, Framework2.ANIM_NONE);
    }
}
