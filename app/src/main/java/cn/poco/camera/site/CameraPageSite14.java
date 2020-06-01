package cn.poco.camera.site;

import android.content.Context;

import java.util.HashMap;

import cn.poco.album.site.AlbumSite24;
import cn.poco.filterBeautify.site.FilterBeautifyPageSite;
import cn.poco.framework.MyFramework;
import cn.poco.framework2.Framework2;

/**
 * 智能美形定制
 *
 * @author lmx
 *         Created by lmx on 2016/12/14.
 */

public class CameraPageSite14 extends CameraPageSite {

    /**
     * 拍照
     *
     * @param params <br/>
     *               color_filter_id 滤镜id(int) <br/>
     *               img_file        数据保存对象(ImageFile2) <br/>
     *               camera_mode     镜头模式(int) <br/>
     *               layout_mode     布局模式(int) <br/>
     */
    public void onTakePicture(Context context, HashMap<String, Object> params) {
        HashMap<String, Object> temp = new HashMap<String, Object>();
        temp.putAll(params);
        temp.put("imgs", params.get("img_file"));
        MyFramework.SITE_Open(context, false, FilterBeautifyPageSite.class, temp, Framework2.ANIM_NONE);
    }

    /**
     * 打开相册
     *
     * @param params color_filter_id 滤镜id(int)
     */
    public void openPhotoPicker(Context context, HashMap<String, Object> params) {
        if (params == null) {
            params = new HashMap<>();
        }
        params.put("from_camera", true);
        MyFramework.SITE_Open(context, false, AlbumSite24.class, params, Framework2.ANIM_NONE);
    }
}
