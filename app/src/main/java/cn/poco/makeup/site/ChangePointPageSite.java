package cn.poco.makeup.site;

import android.content.Context;

import java.util.HashMap;

import cn.poco.framework.BaseSite;
import cn.poco.framework.IPage;
import cn.poco.framework.MyFramework;
import cn.poco.framework.SiteID;
import cn.poco.framework2.Framework2;
import cn.poco.makeup.ChangePointPage;

//定点
public class ChangePointPageSite extends BaseSite {
    public ChangePointPageSite() {
        super(SiteID.CHANGEPOINT_PAGE);
    }

    @Override
    public IPage MakePage(Context context) {
        return new ChangePointPage(context,this);
    }

    public void onBack(HashMap<String,Object> params,Context context)
    {
        MyFramework.SITE_Back(context, params, Framework2.ANIM_NONE);
    }
}
