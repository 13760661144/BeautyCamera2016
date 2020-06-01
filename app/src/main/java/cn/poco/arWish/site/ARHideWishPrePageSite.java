package cn.poco.arWish.site;

import android.content.Context;

import java.util.HashMap;

import cn.poco.album.site.AlbumSite100;
import cn.poco.arWish.ARHideWishPrePage;
import cn.poco.camera.CameraSetDataKey;
import cn.poco.camera.site.CameraPageSite500;
import cn.poco.camera3.config.shutter.ShutterConfig;
import cn.poco.framework.BaseSite;
import cn.poco.framework.IPage;
import cn.poco.framework.MyFramework;
import cn.poco.framework.SiteID;
import cn.poco.framework2.Framework2;

/**
 * 藏祝福 前置页site
 * Created by Gxx on 2018/1/26.
 */

public class ARHideWishPrePageSite extends BaseSite
{
    public ARHideWishPrePageSite()
    {
        super(SiteID.AR_HIDE_WISH_PRE);
    }

    @Override
    public IPage MakePage(Context context)
    {
        return new ARHideWishPrePage(context, this);
    }

    // 藏祝福
    public void onStep1ToTakePicture(Context context)
    {
        HashMap<String, Object> params = new HashMap<>();
        params.put("pageType", 0);
        MyFramework.SITE_Popup(context, ARWishesCameraPageSite.class, params, Framework2.ANIM_NONE);
    }

    // 录制视频
    public void onStep2ToRecordVideo(Context context)
    {
        HashMap<String, Object> params = new HashMap<>();
        params.putAll(CameraSetDataKey.GetOnlyVideoStep());
        params.put(CameraSetDataKey.KEY_CAMERA_STICKER_STICKER_ID, -1);
        params.put(CameraSetDataKey.KEY_CAMERA_STICKER_REMEMBER_STICKER_ID, false);
//        params.put(CameraSetDataKey.KEY_CAMERA_STICKER_CATEGORY_ID_JUST_GOTO, 38479); // test 指定选中 水果 分类
        params.put(CameraSetDataKey.KEY_CAMERA_STICKER_CATEGORY_ID_JUST_GOTO, 42865); // 指定选中 新春 分类
        params.put(CameraSetDataKey.KEY_VIDEO_RECORD_TIME_TYPE, ShutterConfig.VideoDurationType.DURATION_ONE_MIN);
        params.put(CameraSetDataKey.KEY_VIDEO_RECORD_MIN_DURATION, 1000L);
        params.put(CameraSetDataKey.KEY_VIDEO_MULTI_SECTION_ENABLE, true);
        params.put(CameraSetDataKey.KEY_HIDE_TAILOR_MADE_TIP, true);
        MyFramework.SITE_Popup(context, CameraPageSite500.class, params, Framework2.ANIM_NONE);
    }

    public void onStep2ToOpenAlbumPage(Context context)
    {
        MyFramework.SITE_Popup(context, AlbumSite100.class, null, Framework2.ANIM_NONE);
    }

    public void onSave(Context context, HashMap<String, Object> params)
    {
        MyFramework.SITE_Popup(context, HideWishSharePageSite.class, params, Framework2.ANIM_NONE);
    }

    public void onBack(Context context)
    {
        MyFramework.SITE_Back(context, null, Framework2.ANIM_NONE);
    }
}
