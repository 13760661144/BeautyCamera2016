package cn.poco.camera.site;

import android.content.Context;

import java.util.HashMap;

import cn.poco.framework.MyFramework;
import cn.poco.framework2.Framework2;
import cn.poco.lightApp06.site.BeautyVideoPageSite;

/**
 * 外部调用,拍视频
 * Created by Gxx on 2017/8/7.
 */

public class CameraPageSite102 extends CameraPageSite {


    @Override
    public void openVideoPreviewPage(Context context, HashMap<String, Object> params) {
        if (params != null) {
            MyFramework.SITE_Open(context, BeautyVideoPageSite.class, params, Framework2.ANIM_NONE);
        }
    }
}
