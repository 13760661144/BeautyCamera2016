package cn.poco.arWish.site;

import android.content.Context;

import java.util.HashMap;

import cn.poco.album.site.AlbumSite19;
import cn.poco.arWish.ArCreateIntroPage;
import cn.poco.camera.CameraSetDataKey;
import cn.poco.camera.site.CameraPageSite500;
import cn.poco.camera3.config.shutter.ShutterConfig;
import cn.poco.framework.BaseSite;
import cn.poco.framework.IPage;
import cn.poco.framework.MyFramework;
import cn.poco.framework.SiteID;
import cn.poco.framework2.Framework2;

/**
 * Created by admin on 2018/1/18.
 */

public class ArIntroCreateSite extends BaseSite {
    /**
     * 派生类必须实现一个XXXSite()的构造函数
     *
     * @param id
     */
    private Context m_context;

    public ArIntroCreateSite()
    {
        super(SiteID.AR_INTRO_CREATE);
    }

    @Override
    public IPage MakePage(Context context)
    {
        m_context = context;
        return new ArCreateIntroPage(context, this);
    }

    public void onBack()
    {
        MyFramework.SITE_Back(m_context, null, Framework2.ANIM_TRANSLATION_LEFT);
    }

    public void goToRecordVideo() {
        HashMap<String, Object> params = new HashMap<>();
        params.put(CameraSetDataKey.KEY_START_MODE, 0);
        params.put(CameraSetDataKey.KEY_CAMERA_SELECT_TAB_TYPE, ShutterConfig.TabType.VIDEO);
        params.put(CameraSetDataKey.KEY_CAMERA_SHOW_TAB_TYPE, ShutterConfig.TabType.VIDEO);
        params.put(CameraSetDataKey.KEY_VIDEO_RECORD_TIME_TYPE, ShutterConfig.VideoDurationType.DURATION_ONE_MIN);
        params.put(CameraSetDataKey.KEY_VIDEO_RECORD_MIN_DURATION, 1000L);
        params.put(CameraSetDataKey.KEY_VIDEO_MULTI_SECTION_ENABLE, true);
        MyFramework.SITE_Open(m_context, CameraPageSite500.class, params, Framework2.ANIM_NONE);
    }

    public void goToChooseImage() {
        HashMap<String, Object> params = new HashMap<>();
        params.put("from_camera", true);
        MyFramework.SITE_Popup(m_context, AlbumSite19.class, params, Framework2.ANIM_TRANSLATION_TOP);
    }
}
