package cn.poco.ad65.site;

import android.content.Context;

import java.util.HashMap;

import cn.poco.ad65.AD65Page;
import cn.poco.business.ChannelValue;
import cn.poco.framework.BaseSite;
import cn.poco.framework.IPage;
import cn.poco.framework.MyFramework;
import cn.poco.framework2.Framework2;
import cn.poco.home.site.HomePageSite;
import cn.poco.share.site.SharePageSiteAD65;

import static cn.poco.framework.SiteID.AD65;


public class AD65PageSite extends BaseSite {
    public AD65PageSite() {
        super(AD65);
    }

    @Override
    public IPage MakePage(Context context) {
        return new AD65Page(context, this);
    }

    public void onback(Context context)
    {
        MyFramework.SITE_Back(context,null, Framework2.ANIM_NONE);
    }

    public void onSave(Context context, HashMap<String ,Object> params)
    {
        m_inParams.put(AD65Page.KEYVALUE_FRAMEINDEX,params.get(AD65Page.KEYVALUE_FRAMEINDEX));
        m_inParams.put(AD65Page.KEYVALUE_BACK,true);
        m_inParams.put(AD65Page.KEYVALUE_PROGRESS,params.get(AD65Page.KEYVALUE_PROGRESS));
        HomePageSite.CloneBusinessParams(params, m_inParams);
        params.put("channelValue", ChannelValue.AD83);
        MyFramework.SITE_Open(context, SharePageSiteAD65.class,params, Framework2.ANIM_NONE);
    }
}
