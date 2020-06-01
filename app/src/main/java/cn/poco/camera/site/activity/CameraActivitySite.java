package cn.poco.camera.site.activity;

import android.app.Activity;

import com.adnonstop.changeface.Helper;
import com.poco.changeface_v.FaceManager;
import com.poco.changeface_v.confirm.output.ConfirmManager;

import cn.poco.camera.activity.CameraActivity;
import cn.poco.framework.MyFramework;
import cn.poco.framework2.BaseActivitySite;

/**
 * Created by Raining on 2018/1/24.
 */

public class CameraActivitySite extends BaseActivitySite {

    @Override
    public Class<? extends Activity> getActivityClass() {
        return CameraActivity.class;
    }

    public static void openFaceFromCamera(Activity activity, String filePath, int cameraId) {
        Helper.sTime = System.currentTimeMillis();
        Helper.sCameraId = cameraId;
        FaceManager.getInstance().openFaceConfirm(activity, filePath, ConfirmManager.PHOTO_TYPE, false);
        MyFramework.SITE_Finish(activity, Activity.RESULT_OK, null);
        Helper.clearAll();
    }

    public static void openFaceFromAlbum(Activity activity, String filePath) {
        FaceManager.getInstance().openFaceConfirm(activity, filePath, ConfirmManager.ALBUM_TYPE, false);
        MyFramework.SITE_Finish(activity, Activity.RESULT_OK, null);
        Helper.clearAll();
    }

    public void onBack(Activity activity) {
        Helper.sTime = 0;
        Helper.sCameraId = -1;
        MyFramework.SITE_Finish(activity, Activity.RESULT_CANCELED, null);
        Helper.clearAll();
    }

    public static void showTip(Activity activity) {
        Helper.showTip(activity);
    }
}
