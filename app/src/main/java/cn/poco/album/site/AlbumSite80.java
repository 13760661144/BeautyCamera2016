package cn.poco.album.site;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

import cn.poco.camera.RotationImg2;
import cn.poco.framework.MyFramework;
import cn.poco.framework2.Framework2;
import cn.poco.home.site.HomePageSite;

/**
 * Created by lgh on 2016/12/9.
 * 相册选图到社区页
 */
public class AlbumSite80 extends AlbumSite
{
    @Override
    public void onPhotoSelected(Context context,Map<String, Object> params) {
        RotationImg2[] imgses = MakeRotationImg((String[])params.get("imgs"), true);
        String[] arr=new String[imgses.length];
        for(int i = 0; i < imgses.length; i++)
        {
            arr[i]=imgses[i].m_img.toString();
        }
        HashMap<String, Object> temp = new HashMap<>();
//        temp.put("index", params.get("index"));
//        temp.put("folder_name", params.get("folder_name"));
//        MyFramework.SITE_Popup(PocoCamera.main, Beautify4PageSite7.class, temp, Framework.ANIM_NONE);
        temp.put("imgPath", arr);
        MyFramework.SITE_BackTo(context, HomePageSite.class, temp, Framework2.ANIM_TRANSLATION_BOTTOM);
    }
}
