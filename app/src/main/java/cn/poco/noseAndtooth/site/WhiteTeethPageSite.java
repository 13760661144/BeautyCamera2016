package cn.poco.noseAndtooth.site;

import android.content.Context;

import java.util.HashMap;

import cn.poco.framework.IPage;
import cn.poco.framework.SiteID;
import cn.poco.noseAndtooth.WhiteTeethPage;


public class WhiteTeethPageSite extends AbsNoseAndToothPageSite {
    public WhiteTeethPageSite() {
        super(SiteID.WHITETEETH);
    }

    @Override
    public IPage MakePage(Context context) {
        return new WhiteTeethPage(context,this);
    }

    @Override
    public void onBack(Context context, HashMap<String, Object> params) {

    }

    @Override
    public void onSave(Context context, HashMap<String, Object> params) {

    }
}
