package cn.poco.noseAndtooth.site;

import android.content.Context;

import java.util.HashMap;

import cn.poco.framework.IPage;
import cn.poco.framework.SiteID;
import cn.poco.noseAndtooth.ShrinkNosePage;


public class ShrinkNoseSite extends AbsNoseAndToothPageSite {
    public ShrinkNoseSite() {
        super(SiteID.SHRINKNOSE);
    }

    @Override
    public IPage MakePage(Context context) {
        return new ShrinkNosePage(context,this);
    }

    @Override
    public void onBack(Context context, HashMap<String, Object> params) {

    }

    @Override
    public void onSave(Context context, HashMap<String, Object> params) {

    }
}
