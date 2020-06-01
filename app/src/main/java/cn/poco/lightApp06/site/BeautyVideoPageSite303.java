package cn.poco.lightApp06.site;

import android.content.Context;

import java.util.HashMap;

import cn.poco.framework.MyFramework;
import cn.poco.framework2.Framework2;
import cn.poco.preview.site.PreviewImgPageSite;

/**
 * 201710阿玛尼商业
 *
 * @author lmx
 *         Created by lmx on 2017-12-26.
 */

public class BeautyVideoPageSite303 extends BeautyVideoPageSite
{
    /**
     * 打开图片、视频预览
     *
     * @param path    文件本地路径
     * @param isVideo 是否为视频文件
     */
    public void OnPreview(Context context, String path, boolean isVideo)
    {
        HashMap<String, Object> params = new HashMap<>();
        params.put("img", path);
        params.put("isVideo", isVideo);
        params.put("isFullVideoScreen", true);
        MyFramework.SITE_Popup(context, PreviewImgPageSite.class, params, isVideo ? Framework2.ANIM_NONE : Framework2.ANIM_TRANSITION);
    }

    public void onBack(Context context)
    {
        this.OnHome(context);
    }

    public void onBack(Context context, HashMap<String, Object> params)
    {
        this.OnHome(context);
    }
}
