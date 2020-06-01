package cn.poco.rise.site;

import android.content.Context;

import java.util.HashMap;

import cn.poco.framework.BaseSite;
import cn.poco.framework.IPage;
import cn.poco.framework.MyFramework;
import cn.poco.framework.SiteID;
import cn.poco.framework2.Framework2;
import cn.poco.rise.RisePage;

/**
 * Created by Gxx on 2017/11/29.
 */

public class RisePageSite extends BaseSite
{
    /**
     * 派生类必须实现一个XXXSite()的构造函数
     **/
    public RisePageSite()
    {
        super(SiteID.RISE);
    }

    @Override
    public IPage MakePage(Context context)
    {
        return new RisePage(context, this);
    }

    public void onBack(Context context, HashMap<String, Object> params)
    {
        MyFramework.SITE_Back(context, null, Framework2.ANIM_NONE);
    }

    public void onSave(Context context, HashMap<String, Object> params) {
        MyFramework.SITE_Back(context, null, Framework2.ANIM_NONE);
    }
}
