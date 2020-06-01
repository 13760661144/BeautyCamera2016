package cn.poco.camera.site;

import android.content.Context;

import java.util.HashMap;

import cn.poco.framework.MyFramework;
import cn.poco.framework2.Framework2;
import cn.poco.lightApp06.site.BeautyVideoPageSite500;

/**
 * Created by zwq on 2018/01/24 13:48.<br/><br/>
 * AR送祝福
 */
public class CameraPageSite500 extends CameraPageSite {

    @Override
    public void openVideoPreviewPage(Context context, HashMap<String, Object> params) {
        MyFramework.SITE_Open(context, BeautyVideoPageSite500.class, params, Framework2.ANIM_NONE);
    }
}
