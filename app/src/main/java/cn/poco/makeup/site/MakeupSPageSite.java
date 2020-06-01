package cn.poco.makeup.site;


import android.content.Context;

import java.util.HashMap;

import cn.poco.framework.BaseSite;
import cn.poco.framework.IPage;
import cn.poco.framework.MyFramework;
import cn.poco.framework.SiteID;
import cn.poco.framework2.Framework2;
import cn.poco.makeup.makeup2.MakeupSPage;
import cn.poco.share.site.SharePageSite8;

public class MakeupSPageSite extends BaseSite{
    public MakeupSPageSite() {
        super(SiteID.YIJIANMENGZHUANG);
    }

    @Override
    public IPage MakePage(Context context) {
        return new MakeupSPage(context,this);
    }

    /**
     * @param params img:Bitmap
     */
    public void onBack(Context context, HashMap<String, Object> params)
    {
        MyFramework.SITE_Back(context, null, Framework2.ANIM_NONE);
    }

    /**
     * @param params img:Bitmap
     */
    public void onSave(Context context, HashMap<String, Object> params)
    {
        m_inParams.put(MakeupSPage.BACKFLAG,true);
        m_inParams.put(MakeupSPage.BACKDATA,params.get(MakeupSPage.BACKDATA));
        MyFramework.SITE_Open(context, SharePageSite8.class,params, Framework2.ANIM_NONE);
    }
}
