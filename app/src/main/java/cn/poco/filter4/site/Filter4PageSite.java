package cn.poco.filter4.site;

import android.content.Context;

import java.util.HashMap;

import cn.poco.filter4.FilterPage;
import cn.poco.filterManage.site.FilterMoreSite;
import cn.poco.framework.BaseSite;
import cn.poco.framework.IPage;
import cn.poco.framework.MyFramework;
import cn.poco.framework.SiteID;
import cn.poco.framework2.Framework2;
import cn.poco.login.site.LoginPageSite1;
import cn.poco.resource.ResType;

/**
 * 素材美化-滤镜
 *
 * @author lmx
 *         Created by lmx on 2016/11/9.
 * @since v4.0
 */

public class Filter4PageSite extends BaseSite {


    public Filter4PageSite() {
        super(SiteID.FILTER);
    }

    @Override
    public IPage MakePage(Context context) {
        return new FilterPage(context, this);
    }

    /**
     * @param params img:Bitmap
     */
    public void OnBack(Context context, HashMap<String, Object> params) {
        MyFramework.SITE_Back(context, null, Framework2.ANIM_NONE);
    }

    /**
     * @param params img:Bitmap
     */
    public void OnSave(HashMap<String, Object> params) {
    }

    /**
     * 推荐位下载更多
     *
     * @param resType
     */
    public void OpenDownloadMore(Context context, ResType resType) {
        MyFramework.SITE_Popup(context, FilterMoreSite.class, null, Framework2.ANIM_NONE);
    }

    public void OnLogin(Context context) {
        MyFramework.SITE_Popup(context, LoginPageSite1.class, null, Framework2.ANIM_NONE);
    }
}
