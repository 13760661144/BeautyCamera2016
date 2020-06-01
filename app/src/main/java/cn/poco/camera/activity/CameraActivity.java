package cn.poco.camera.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;

import cn.poco.album.site.AlbumSite500;
import cn.poco.camera.CameraConfig;
import cn.poco.camera.CameraSetDataKey;
import cn.poco.camera.site.CameraPageSite600;
import cn.poco.camera.site.activity.CameraActivitySite;
import cn.poco.camera3.config.shutter.ShutterConfig;
import cn.poco.filterBeautify.FilterBeautifyProcessor;
import cn.poco.framework.BaseFwActivity;
import cn.poco.framework.BaseSite;

/**
 * Created by Raining on 2018/1/24.
 */
public class CameraActivity extends BaseFwActivity<CameraActivitySite> {

    @Override
    protected void InitData(@Nullable Bundle savedInstanceState) {
        super.InitData(savedInstanceState);

        if (mSite == null) {
            mSite = new CameraActivitySite();
        }
    }

    @Override
    protected void onAppMapGate(final Context context, Bundle savedInstanceState, boolean newActivity) {
        ArrayList<BaseSite> arr = mFramework.GetCurrentSiteList();
        if (arr != null && arr.size() > 0) {
            mFramework.onCreate(context, savedInstanceState);
        } else {
            int openType = 0;//0:镜头，1:相册
            int cameraId = -1;

            HashMap<String, Object> params = new HashMap<>();
            Intent intent = getIntent();
            if (intent != null) {
                openType = intent.getIntExtra("openType", 0);
                cameraId = intent.getIntExtra("cameraId", -1);
            }
            if (openType == 1) {
                openPhotoPicker(context, params);
            } else {
                params.put(CameraSetDataKey.KEY_START_MODE, cameraId);
                openCamera(context, params);
            }
        }
    }

    private void openCamera(Context context, HashMap<String, Object> params) {
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
        SITE_Open(context, true, CameraPageSite600.class, params, null);
    }

    public void openPhotoPicker(Context context, HashMap<String, Object> params) {
        params.put("from_camera", true);
        SITE_Open(context, true, AlbumSite500.class, params, null);
    }
}
