package cn.poco.makeup.site;

import android.content.Context;

import java.util.HashMap;

import cn.poco.MaterialMgr2.site.DownloadMorePageSite;
import cn.poco.framework.BaseSite;
import cn.poco.framework.IPage;
import cn.poco.framework.MyFramework;
import cn.poco.framework.SiteID;
import cn.poco.framework2.Framework2;
import cn.poco.login.site.LoginPageSite1;
import cn.poco.makeup.MakeupPage;
import cn.poco.resource.ResType;

//彩妆
public class MakeupPageSite extends BaseSite {


    public MakeupPageSite() {
        super(SiteID.MAKEUP_PAGE);
    }

    @Override
    public IPage MakePage(Context context) {
        return new MakeupPage(context, this);
    }

    /**
     * @param params img:Bitmap
     */
    public void onBack(Context context, HashMap<String, Object> params) {
    }

    /**
     * @param params img:Bitmap
     */
    public void onSave(Context context, HashMap<String, Object> params) {
    }

    public void OnDownloadMore(Context context, ResType type) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("type", type);
        MyFramework.SITE_Popup(context, DownloadMorePageSite.class, params, Framework2.ANIM_NONE);
    }

    public void onChangePoint(Context context, HashMap<String, Object> params) {
        MyFramework.SITE_Popup(context, ChangePointPageSite.class, params, Framework2.ANIM_NONE);
    }

    public void OnLogin(Context context) {
        MyFramework.SITE_Popup(context, LoginPageSite1.class, null, Framework2.ANIM_NONE);
    }
}
