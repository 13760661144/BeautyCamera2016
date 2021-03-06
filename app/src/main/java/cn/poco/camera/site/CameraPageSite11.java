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
import cn.poco.login.site.EditHeadIconImgPageSite1;

/**
 * 用户信息页拍头像
 */
public class CameraPageSite11 extends CameraPageSite {

    @Override
    public void onTakePicture(Context context, HashMap<String, Object> params) {
        RotationImg2[] imgs = ((ImageFile2) params.get("img_file")).SaveImg2(context);//自动保存图片
        HashMap<String, Object> datas = new HashMap<>();
        datas.put("imgPath", imgs);
        datas.put("filterValue", params.get(DataKey.COLOR_FILTER_ID));
        datas.put("mode", EditHeadIconImgPage.OTHER);
        datas.put("userId", m_inParams.get("poco_id"));
        datas.put("tocken", m_inParams.get("poco_token"));
        datas.put(EditHeadIconImgPage.BGPATH, m_inParams.get(EditHeadIconImgPage.BGPATH));
        datas.put(DataKey.CAMERA_TAILOR_MADE_PARAMS, params.get(DataKey.CAMERA_TAILOR_MADE_PARAMS));
        MyFramework.SITE_Popup(context, EditHeadIconImgPageSite1.class, datas, Framework2.ANIM_TRANSITION);
    }

    public void openCutePhotoPreviewPage(Context context, HashMap<String, Object> params) {
        if (params != null) {
            if (!params.containsKey("bmp")) {
                return;
            }
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

    @Override
    public void onBack(Context context) {
        //恢复屏幕亮度
        resetDefaultBrightness(context);
        super.onBack(context);
    }
}
