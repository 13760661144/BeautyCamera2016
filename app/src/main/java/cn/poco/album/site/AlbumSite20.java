package cn.poco.album.site;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

import cn.poco.filterBeautify.site.FilterBeautifyPageSite;
import cn.poco.framework.DataKey;
import cn.poco.framework.MyFramework;
import cn.poco.framework2.Framework2;

/**
 * Created by Raining on 2016/12/9.
 * 镜头选图到滤镜页
 */

public class AlbumSite20 extends AlbumSite {
    @Override
    public void onPhotoSelected(Context context, Map<String, Object> params) {
        HashMap<String, Object> temp = new HashMap<String, Object>();
        temp.put("imgs", MakeRotationImg((String[]) params.get("imgs"), true));
        temp.put(DataKey.BEAUTIFY_DEF_SEL_URI, m_inParams.get(DataKey.COLOR_FILTER_ID));
        temp.put("index", params.get("index"));
        temp.put("folder_name", params.get("folder_name"));
        temp.put("from_camera", false);
        temp.put(DataKey.COLOR_FILTER_ID, 0);//使用原图滤镜
        MyFramework.SITE_Open(context, FilterBeautifyPageSite.class, temp, Framework2.ANIM_NONE);
    }
}
