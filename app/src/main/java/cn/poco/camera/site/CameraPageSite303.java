package cn.poco.camera.site;


import android.content.Context;

import java.util.HashMap;

import cn.poco.framework.MyFramework;
import cn.poco.framework2.Framework2;
import cn.poco.lightApp06.site.BeautyVideoPageSite303;

/**
 * 阿玛尼商业
 */
public class CameraPageSite303 extends CameraPageSite
{

    /**
     * 动态贴纸视频预览
     *
     * @param params <br/>
     *               color_filter_id 滤镜id(int) <br/>
     *               width           视频宽(int) <br/>
     *               height          视频高(int) <br/>
     *               mp4_path        mp4文件路径(String) <br/>
     *               record_obj      视频录制对象(MyRecordVideo) <br/>
     */
    public void openVideoPreviewPage(Context context, HashMap<String, Object> params)
    {
        MyFramework.SITE_Open(context, BeautyVideoPageSite303.class, params, Framework2.ANIM_NONE);
    }
}
