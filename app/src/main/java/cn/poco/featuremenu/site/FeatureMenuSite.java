package cn.poco.featuremenu.site;

import android.content.Context;

import cn.poco.featuremenu.page.FeatureMenuPage;
import cn.poco.framework.BaseSite;
import cn.poco.framework.IPage;
import cn.poco.framework.SiteID;

/**
 * Created by Shine on 2017/9/6.
 */

public class FeatureMenuSite extends BaseSite{

    public FeatureMenuSite() {
        super(SiteID.FEATUREMENU);
    }

    @Override
    public IPage MakePage(Context context) {
        return new FeatureMenuPage(context, this);
    }

    public void OnBack(Context context)
    {
    }

}
