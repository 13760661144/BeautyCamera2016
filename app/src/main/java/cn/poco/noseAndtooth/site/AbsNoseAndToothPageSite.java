package cn.poco.noseAndtooth.site;


import android.content.Context;

import java.util.HashMap;

import cn.poco.framework.BaseSite;
import cn.poco.framework.IPage;

public abstract class AbsNoseAndToothPageSite extends BaseSite{

    public AbsNoseAndToothPageSite(int id) {
        super(id);
    }

    @Override
    public IPage MakePage(Context context) {
        return null;
    }

    public abstract void onBack(Context context, HashMap<String,Object> params);

    public abstract void onSave(Context context, HashMap<String,Object> params);

}
