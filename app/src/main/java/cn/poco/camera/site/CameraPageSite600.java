package cn.poco.camera.site;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;

import java.util.HashMap;

import cn.poco.album.site.AlbumSite500;
import cn.poco.camera.ImageFile2;
import cn.poco.camera.RotationImg2;
import cn.poco.camera.activity.CameraActivity;
import cn.poco.camera.site.activity.CameraActivitySite;
import cn.poco.framework.FileCacheMgr;
import cn.poco.framework.MyFramework;
import cn.poco.framework2.Framework2;
import cn.poco.tianutils.CommonUtils;
import cn.poco.utils.Utils;

/**
 * Created by zwq on 2018/01/24 13:48.<br/><br/>
 * 换脸轻应用
 */
public class CameraPageSite600 extends CameraPageSite {

    @Override
    public void onBack(Context context, boolean hasAnim) {
        //super.onBack(context, hasAnim);
        if (context instanceof CameraActivity) {
            CameraActivitySite site = ((CameraActivity) context).getActivitySite();
            site.onBack((Activity) context);
        }
    }

    @Override
    public void openPhotoPicker(Context context, HashMap<String, Object> params) {
        if (params == null) {
            params = new HashMap<String, Object>();
        }
        params.put("from_camera", true);
        MyFramework.SITE_Open(context, AlbumSite500.class, params, Framework2.ANIM_NONE);
    }

    @Override
    public void onTakePicture(Context context, HashMap<String, Object> params) {
        // super.onTakePicture(context, params);
        if (params != null) {
            int cameraId = (Integer) params.get("cameraId");
            String path = null;

            ImageFile2 imageFile2 = (ImageFile2) params.get("img_file");
            if (imageFile2 != null) {
                RotationImg2[] img2s = imageFile2.SaveTemp(context);
                if (img2s != null && img2s.length > 0) {
                    path = (String) img2s[0].m_img;
                }
            }
            if (path != null && context instanceof Activity) {
                CameraActivitySite.openFaceFromCamera((Activity) context, path, cameraId);
            }
        }
    }

    @Override
    public void openCutePhotoPreviewPage(Context context, HashMap<String, Object> params) {
        //super.openCutePhotoPreviewPage(context, params);
        if (params != null) {
            Bitmap bmp = (Bitmap) params.get("bmp");
            if (bmp == null) {
                return;
            }
            int cameraId = (Integer) params.get("cameraId");

            String path = FileCacheMgr.GetLinePath() + ".img";
            CommonUtils.MakeParentFolder(path);
            Utils.SaveImg(context, bmp, path, 100, false);

            if (path != null && context instanceof Activity) {
                CameraActivitySite.openFaceFromCamera((Activity) context, path, cameraId);
            }
        }
    }

    @Override
    public void showTip(Context context) {
        CameraActivitySite.showTip((Activity) context);
    }
}
