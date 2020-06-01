package cn.poco.album.site;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

import cn.poco.MaterialMgr2.site.ThemeIntroPageSite;
import cn.poco.beautify4.site.Beautify4PageSite7;
import cn.poco.framework.DataKey;
import cn.poco.framework.MyFramework;
import cn.poco.framework2.Framework2;

/**
 * Created by lgh on 2016/12/9.
 * 社区->素材中心->选图
 */
public class AlbumSite81 extends AlbumSite
{
    @Override
    public void onPhotoSelected(Context context,Map<String, Object> params) {
        HashMap<String, Object> temp = new HashMap<>();
        temp.put("imgs", MakeRotationImg((String[])params.get("imgs"), true));
        temp.put(DataKey.BEAUTIFY_DEF_OPEN_PAGE, m_inParams.get(ThemeIntroPageSite.TYPE));
        temp.put(DataKey.BEAUTIFY_DEF_SEL_URI, m_inParams.get(ThemeIntroPageSite.ID));
        temp.put("index", params.get("index"));
        temp.put("folder_name", params.get("folder_name"));
        temp.put("show_exit_dialog", false);
        MyFramework.SITE_Open(context, Beautify4PageSite7.class, temp, Framework2.ANIM_NONE);
    }

}
