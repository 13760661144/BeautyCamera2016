package cn.poco.camera.site;

import android.content.Context;
import android.graphics.Bitmap;

import java.util.HashMap;

import cn.poco.camera.ImageFile2;
import cn.poco.framework.MyFramework;
import cn.poco.framework2.Framework2;
import cn.poco.imagecore.ImageUtils;
import cn.poco.makeup.site.MakeupSPageSite;

/**
 * 一键萌妆
 */
public class CameraPageSite65 extends CameraPageSite
{
	@Override
	public void onTakePicture(Context context, HashMap<String, Object> params) {
	    resetDefaultBrightness(context);
		HashMap<String, Object> temp = new HashMap<>();
		temp.put("imgs", params.get("img_file"));
		MyFramework.SITE_Open(context, MakeupSPageSite.class, temp, Framework2.ANIM_NONE);
	}

	@Override
	public void openCutePhotoPreviewPage(Context context, HashMap<String, Object> params)
	{
        if (!params.containsKey("bmp"))
        {
            return;
        }
        Bitmap bitmap = (Bitmap) params.get("bmp");
        ImageFile2 imgFile = new ImageFile2();
        try
        {
            byte[] imgData = ImageUtils.JpgEncode(bitmap, 100);
            imgFile.SetData(context, imgData, 0, 0, -1);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return;
        }
        //萌装照不需要自动保存
        imgFile.SetAutoSave(false);
        params.put("img_file", imgFile);
        onTakePicture(context, params);
    }
}
