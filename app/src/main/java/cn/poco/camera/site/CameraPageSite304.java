package cn.poco.camera.site;


import android.content.Context;

import java.util.HashMap;

import cn.poco.camera.ImageFile2;
import cn.poco.camera.RotationImg2;
import cn.poco.community.site.PublishOpusSite;
import cn.poco.framework.MyFramework;
import cn.poco.framework2.Framework2;

/**
 * 分享到社区-合并资料-修改头像-打开镜头
 */
public class CameraPageSite304 extends CameraPageSite {

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

        ImageFile2 img_file = (ImageFile2) params.get("img_file");
        RotationImg2[] rotationImg2s = img_file.SaveImg2(context);
        if (rotationImg2s == null || rotationImg2s.length < 1) {
            return;
        }
        String[] arr = new String[]{rotationImg2s[0].m_img.toString()};
        HashMap<String, Object> temp = new HashMap<>();
        temp.put("imgPath", arr);
        MyFramework.SITE_BackTo(context, PublishOpusSite.class, temp, Framework2.ANIM_NONE);
    }

    @Override
    public void openCutePhotoPreviewPage(Context context, HashMap<String, Object> params) {

    }

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
    public void openVideoPreviewPage(Context context, HashMap<String, Object> params) {
    }


}
