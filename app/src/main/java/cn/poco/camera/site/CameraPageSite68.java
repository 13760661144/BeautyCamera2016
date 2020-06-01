package cn.poco.camera.site;

import android.content.Context;
import android.graphics.Bitmap;

import java.util.HashMap;

import cn.poco.ad65.site.AD65PageSite;
import cn.poco.camera.ImageFile2;
import cn.poco.framework.MyFramework;
import cn.poco.framework2.Framework2;
import cn.poco.home.site.HomePageSite;
import cn.poco.imagecore.ImageUtils;

/**
 * 佰草集商业
 */
public class CameraPageSite68 extends CameraPageSite {
    @Override
    public void onTakePicture(Context context, HashMap<String, Object> params) {
        HashMap<String, Object> temp = new HashMap<String, Object>();
        HomePageSite.CloneBusinessParams(temp, m_inParams);
        temp.put("imgs", params.get("img_file"));
        MyFramework.SITE_Open(context, AD65PageSite.class, temp, Framework2.ANIM_NONE);
    }

    public void openCutePhotoPreviewPage(Context context, HashMap<String, Object> params) {
        if (params != null) {
            Bitmap bitmap = (Bitmap) params.get("bmp");
            ImageFile2 imgFile = new ImageFile2();
            try {
                byte[] imgData = ImageUtils.JpgEncode(bitmap, 100);
                imgFile.SetData(context, imgData, 0, 0, -1);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
            params.put("img_file", imgFile);
            onTakePicture(context, params);
        }
    }
}
