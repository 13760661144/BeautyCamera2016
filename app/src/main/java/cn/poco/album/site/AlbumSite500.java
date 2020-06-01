package cn.poco.album.site;

import android.app.Activity;
import android.content.Context;

import java.util.HashMap;
import java.util.Map;

import cn.poco.camera.CameraConfig;
import cn.poco.camera.CameraSetDataKey;
import cn.poco.camera.site.CameraPageSite600;
import cn.poco.camera.site.activity.CameraActivitySite;
import cn.poco.camera3.config.shutter.ShutterConfig;
import cn.poco.filterBeautify.FilterBeautifyProcessor;
import cn.poco.framework.MyFramework;
import cn.poco.framework.MyFramework2App;
import cn.poco.framework2.Framework2;

/**
 * Created by zwq on 2018/01/24.
 * 换脸轻应用
 */
public class AlbumSite500 extends AlbumSite {

    @Override
    public void onBack(Context context) {
        int size = 0;
        if (MyFramework2App.getInstance().getFramework() != null
                && MyFramework2App.getInstance().getFramework().GetSiteList() != null
                && MyFramework2App.getInstance().getFramework().GetSiteList().length > 0) {
            size = MyFramework2App.getInstance().getFramework().GetSiteList()[0].size();
        }
        if (size == 1) {
            onOpenCamera(context);
        } else {
            super.onBack(context);
        }
    }

    @Override
    public void onOpenCamera(Context context) {
        HashMap<String, Object> params = new HashMap<>();
        params.put(CameraSetDataKey.KEY_CAMERA_SELECT_RATIO, CameraConfig.PreviewRatio.Ratio_4_3);
        params.put(CameraSetDataKey.KEY_CAMERA_SELECT_TAB_TYPE, ShutterConfig.TabType.PHOTO);
        params.put(CameraSetDataKey.KEY_CAMERA_SHOW_TAB_TYPE, ShutterConfig.TabType.PHOTO);
        params.put(CameraSetDataKey.KEY_CAMERA_FORCE_TAB_RATIO, true);
        params.put(CameraSetDataKey.KEY_HIDE_WATER_MARK, true);
        params.put(CameraSetDataKey.KEY_HIDE_RATIO_BTN, true);
        params.put(CameraSetDataKey.KEY_HIDE_PATCH_BTN, true);
        params.put(CameraSetDataKey.KEY_HIDE_TAILOR_MADE_TIP, true);
        // params.put(CameraSetDataKey.KEY_CAMERA_FILTER_ID, 0);
        // params.put(CameraSetDataKey.KEY_HIDE_FILTER_SELECTOR, true);
        params.put(CameraSetDataKey.KEY_IS_FILTER_BEAUTITY_PROCESS ,true);
        params.put(CameraSetDataKey.KEY_FILTER_BEAUTITY_PROCESS_MASK, FilterBeautifyProcessor.FILTER_BEAUTY);
        MyFramework.SITE_Open(context, CameraPageSite600.class, params, Framework2.ANIM_NONE);
    }

    @Override
    public void onPhotoSelected(Context context, Map<String, Object> params) {
        if (params != null) {
            String[] pathArr = (String[]) params.get("imgs");
            if (pathArr != null && pathArr.length > 0) {
                String path = pathArr[0];
                if (path != null && context instanceof Activity) {
                    CameraActivitySite.openFaceFromAlbum((Activity) context, path);
                }
            }
        }
    }
}
