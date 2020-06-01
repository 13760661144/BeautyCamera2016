package cn.poco.filterBeautify.site;

import android.content.Context;

import java.util.HashMap;

import cn.poco.beautify4.site.Beautify4PageSite7;
import cn.poco.camera.RotationImg2;
import cn.poco.framework.DataKey;
import cn.poco.framework.MyFramework;
import cn.poco.framework2.Framework2;
import cn.poco.home.site.HomePageSite;

/**
 * 社区调用
 */
public class FilterBeautifyPageSite200 extends FilterBeautifyPageSite
{
    @Override
    public void OnSave(Context context, HashMap<String, Object> params)
    {
        String[] arr = new String[]{params.get("img").toString()};
        HashMap<String, Object> temp = new HashMap<>();
        temp.put("imgPath", arr);
        MyFramework.SITE_BackTo(context, HomePageSite.class, temp, Framework2.ANIM_NONE);
    }


    @Override
    public void OnBeauty(Context context, HashMap<String, Object> params, RotationImg2 img, int filterUri, int filterAlpha, boolean hasWaterMark, int waterMarkId)
    {
        if (params == null)
        {
            params = new HashMap<>();
        }
        MyFramework.CopyExternalCallParams(m_inParams, params);
        params.put("imgs", new RotationImg2[]{img});
        params.put(DataKey.BEAUTIFY_DEF_SEL_URI, filterUri);
        params.put("filter_alpha", filterAlpha);
        params.put("do_not_del_filter_cache", true);
        params.put("do_not_reset_data", true);
        params.put("only_one_pic", true);
        params.put("has_water_mark", hasWaterMark);
        params.put("water_mark_id", waterMarkId);
        MyFramework.SITE_Open(context, Beautify4PageSite7.class, params, Framework2.ANIM_NONE);
    }
}
