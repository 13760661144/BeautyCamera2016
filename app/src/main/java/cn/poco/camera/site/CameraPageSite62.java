package cn.poco.camera.site;

import android.content.Context;
import android.graphics.Bitmap;

import java.util.HashMap;

import cn.poco.camera.ImageFile2;
import cn.poco.camera.RotationImg2;
import cn.poco.framework.DataKey;
import cn.poco.framework.MyFramework;
import cn.poco.framework2.Framework2;
import cn.poco.imagecore.ImageUtils;
import cn.poco.login.EditHeadIconImgPage;
import cn.poco.login.site.EditHeadIconImgPageSite3;

/**
 * 解锁登陆，注册，选择头像，到拍照
 */
public class CameraPageSite62 extends CameraPageSite {
    @Override
    public void onTakePicture(Context context, HashMap<String, Object> params) {
        RotationImg2[] imgs = ((ImageFile2) params.get("img_file")).SaveImg2(context);
        HashMap<String, Object> datas = new HashMap<>();
        datas.put("filterValue", params.get(DataKey.COLOR_FILTER_ID));
        datas.put("userId", m_inParams.get("poco_id"));
        datas.put("tocken", m_inParams.get("poco_token"));
        datas.put("imgPath", imgs);
        datas.put("mode", EditHeadIconImgPage.REGISTER);
        datas.put("info", m_inParams.get("info"));
        datas.put(EditHeadIconImgPage.BGPATH, m_inParams.get(EditHeadIconImgPage.BGPATH));
        datas.put(DataKey.CAMERA_TAILOR_MADE_PARAMS, params.get(DataKey.CAMERA_TAILOR_MADE_PARAMS));
        MyFramework.SITE_Popup(context, EditHeadIconImgPageSite3.class, datas, Framework2.ANIM_TRANSITION);
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
            //萌装照不需要自动保存
            imgFile.SetAutoSave(false);
            params.put("img_file", imgFile);
            onTakePicture(context, params);
        }
    }

}
