package cn.poco.camera.site;

import android.content.Context;
import android.graphics.Bitmap;

import java.util.HashMap;

import cn.poco.album.PhotoStore;
import cn.poco.beautify4.site.Beautify4PageSite7;
import cn.poco.camera.ImageFile2;
import cn.poco.camera.RotationImg2;
import cn.poco.framework.DataKey;
import cn.poco.framework.MyFramework;
import cn.poco.framework2.Framework2;
import cn.poco.imagecore.ImageUtils;

/**
 * 社区->素材中心->选图-打开镜头
 *
 * @author lmx
 *         Created by lmx on 2017/11/2.
 */

public class CameraPageSite305 extends CameraPageSite
{
    @Override
    public void onTakePicture(Context context, HashMap<String, Object> params)
    {
        //拍照 修图 保存后直接跳转到社区，取消后退到镜头页
        resetDefaultBrightness(context);
        RotationImg2[] imgs = ((ImageFile2) params.get("img_file")).SaveImg2(context);
        HashMap<String, Object> temp = new HashMap<>();
        temp.put("imgs", imgs);
        temp.put(DataKey.BEAUTIFY_DEF_OPEN_PAGE, m_inParams.get(DataKey.BEAUTIFY_DEF_OPEN_PAGE));
        if (params.containsKey(DataKey.COLOR_FILTER_ID))
        {
            int id = (Integer) params.get(DataKey.COLOR_FILTER_ID);
            if (id != 0)
            {
                temp.put(DataKey.BEAUTIFY_DEF_SEL_URI, id);
            }
            else
            {
                temp.put(DataKey.BEAUTIFY_DEF_SEL_URI, m_inParams.get(DataKey.BEAUTIFY_DEF_SEL_URI));
            }
        }
        else
        {
            temp.put(DataKey.BEAUTIFY_DEF_SEL_URI, m_inParams.get(DataKey.BEAUTIFY_DEF_SEL_URI));
        }
        if (imgs != null && imgs.length > 0)
        {
            temp.put("index", PhotoStore.getInstance(context).getPhotoInfoIndex(null, imgs[0].m_orgPath));
        }
        temp.put("only_one_pic", true);
        temp.put("goto_save", true);
        MyFramework.SITE_Open(context, Beautify4PageSite7.class, temp, Framework2.ANIM_NONE);
    }

    @Override
    public void openCutePhotoPreviewPage(Context context, HashMap<String, Object> params)
    {
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
    public void onBack(Context context)
    {
        resetDefaultBrightness(context);
        super.onBack(context);
    }
}
