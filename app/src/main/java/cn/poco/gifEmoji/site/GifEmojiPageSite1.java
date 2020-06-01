package cn.poco.gifEmoji.site;

import android.content.Context;

import java.util.HashMap;

import cn.poco.framework.MyFramework;
import cn.poco.framework2.Framework2;
import cn.poco.home.site.HomePageSite;
import cn.poco.preview.site.PreviewImgPageSite;

/**
 * 社区参与活动调用gif
 * Created by zwq on 2017/05/27 10:16.<br/><br/>
 */
public class GifEmojiPageSite1 extends GifEmojiPageSite {


    public void OnHome(Context context)
    {
        MyFramework.SITE_Open(context, true, HomePageSite.class, null, Framework2.ANIM_NONE);
    }

    /**
     * 打开图片、视频预览
     *
     * @param path 文件本地路径
     * @param isVideo 是否为视频文件
     */
    public void OnPreview(Context context,String path, boolean isVideo)
    {
        HashMap<String, Object> params = new HashMap<>();
        params.put("img", path);
        params.put("isVideo", isVideo);
        MyFramework.SITE_Popup(context, PreviewImgPageSite.class, params, Framework2.ANIM_NONE);
    }


    public void save(Context context,String path) {
        HashMap<String, Object> temp = new HashMap<String, Object>();
        temp.put("gifPath", path);
        MyFramework.SITE_BackTo(context, HomePageSite.class, temp, Framework2.ANIM_NONE);
    }
}
