package cn.poco.ad66.site;

import android.content.Context;

import java.util.HashMap;

import cn.poco.ad66.AD66Page;
import cn.poco.business.ChannelValue;
import cn.poco.framework.BaseSite;
import cn.poco.framework.IPage;
import cn.poco.framework.MyFramework;
import cn.poco.framework.SiteID;
import cn.poco.framework2.Framework2;
import cn.poco.home.site.HomePageSite;
import cn.poco.share.site.SharePageSiteAD66;


public class AD66PageSite extends BaseSite {
    public AD66PageSite() {
        super(SiteID.AD66);
    }

    @Override
    public IPage MakePage(Context context) {
        return new AD66Page(context,this);
    }

    public void onBack(Context context)
    {
        MyFramework.SITE_Back(context,null, Framework2.ANIM_NONE);
    }

    public void onSave(Context context, HashMap<String,Object> params)
    {
        HomePageSite.CloneBusinessParams(params, m_inParams);
        params.put("channelValue", ChannelValue.AD82_1);
        MyFramework.SITE_Open(context, SharePageSiteAD66.class,params, Framework2.ANIM_NONE);
    }
}
