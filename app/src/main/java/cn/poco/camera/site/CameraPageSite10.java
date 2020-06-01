package cn.poco.camera.site;

import android.content.Context;

import java.util.HashMap;

import cn.poco.camera.CameraSetDataKey;
import cn.poco.framework.MyFramework;
import cn.poco.framework2.Framework2;
import cn.poco.home.site.HomePageSite;

/**
 * 弹窗协议到动态贴纸（返回回到首页）
 */
public class CameraPageSite10 extends CameraPageSite1 {
    @Override
    public void onBack(Context context) {
        this.onBack(context, false);
    }

    @Override
    public void onBack(Context context, boolean hasAnim) {
        MyFramework.SITE_Open(context, true, HomePageSite.class, null, Framework2.ANIM_NONE);
    }

    public void openCutePhotoPreviewPage(Context context, HashMap<String, Object> params) {
        if (params != null && params.containsKey("res_id")) {
            if (m_inParams != null && m_inParams.containsKey(CameraSetDataKey.KEY_CAMERA_STICKER_STICKER_ID)) {
                m_inParams.put(CameraSetDataKey.KEY_CAMERA_STICKER_STICKER_ID, params.get("res_id"));
            }
        }
        super.openCutePhotoPreviewPage(context, params);
    }

    /**
     * 动态贴纸图片/视频预览
     *
     * @param params <br/>
     *               type            类型(int), 0:图片，1:OpenGL录制的视频，2:普通方式录制的视频
     *               bmp             拍照的图片/视频的第一帧(Bitmap) <br/>
     *               mp4_path        mp4文件路径(String) <br/>
     *               color_filter_id 滤镜id(int) <br/>
     *               width           视频宽(int) <br/>
     *               height          视频高(int) <br/>
     *               record_obj      视频录制对象(MyRecordVideo) <br/>
     *               res_id          素材id <br/>
     */
    public void openVideoPreviewPage(Context context, HashMap<String, Object> params) {
        if (params != null && params.containsKey("res_id")) {
            if (m_inParams != null && m_inParams.containsKey(CameraSetDataKey.KEY_CAMERA_STICKER_STICKER_ID)) {
                m_inParams.put(CameraSetDataKey.KEY_CAMERA_STICKER_STICKER_ID, params.get("res_id"));
            }
        }
        super.openVideoPreviewPage(context, params);
    }
}
