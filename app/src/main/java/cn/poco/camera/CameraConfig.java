package cn.poco.camera;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

/**
 * Created by zwq on 2016/05/09 16:58.<br/><br/>
 * 镜头相关配置
 */
public class CameraConfig extends BaseConfig {

    private static CameraConfig instance;
    private final String configName = "camera_config";

    /** 预览比例 高比宽 */
    public interface PreviewRatio {
        float Ratio_4_3 = 4.0f / 3;
        float Ratio_1_1 = 1.0f;
        float Ratio_16_9 = 16.0f / 9;
        float Ratio_9_16 = 9.0f / 16;

        float Ratio_Full = 10.0f;//计算时根据实际分辨率计算

        float Ratio_17_9 = 17.0f / 9;
        float Ratio_17$25_9 = 17.25f / 9;
        float Ratio_18_9 = 2.0f;
        float Ratio_18$5_9 = 18.5f / 9;
    }

    public interface VideoDuration{
        int TEN_SECOND = 10;
        int ONE_MIN = 60;
        int THREE_MIN = 180;
    }

    /** 闪关灯模式 */
    public static class FlashMode {
        public static final String Off = "off";//0
        public static final String On = "on";//1
        public static final String Auto = "auto";//2
        public static final String RedEye = "red-eye";//3
        public static final String Torch = "torch";//4

        private FlashMode() {}

        /**
         * 不同机型对应的值可能不一样
         * @param value
         * @return
         */
        public static String getMode(int value) {
            if (value == 1) {
                return On;
            } else if (value == 2) {
                return Auto;
            } else if (value == 3) {
                return RedEye;
            } else if (value == 4) {
                return Torch;
            } else {//if (value == 0)
                return Off;
            }
        }

        /**
         * 不同机型对应的值可能不一样
         * @param mode
         * @return
         */
        public static int getValue(String mode) {
            if (On.equals(mode)) {
                return 1;
            } else if (Auto.equals(mode)) {
                return 2;
            } else if (RedEye.equals(mode)) {
                return 3;
            } else if (Torch.equals(mode)) {
                return 4;
            } else { //if (Off.equals(mode))
                return 0;
            }
        }
    }

    /** 拍照模式 */
    public interface CaptureMode {
        public final int Manual = 0;//手动拍照
        public final int Timer_1s = 1;
        public final int Timer_2s = 2;
        public final int Timer_3s = 3;
        public final int Timer_10s = 10;
    }

    /** 镜头模式 */
    public interface CameraMode {
        public final int Mode_Normal = 1;//手动拍照
        public final int Mode_ZiPai = 6;
        public final int Mode_Business = 9;
    }

    /** 配置集合 */
    public interface ConfigMap {
        @Deprecated
        public final String FlashMode = "flashMode";//闪光灯模式
        public final String FlashModeStr = "flashModeStr";//闪光灯模式
        public final String FrontFlashModeStr = "frontFlashModeStr";//前置闪光灯模式
        public final String TimerMode = "timerMode";//定时拍照模式
        public final String VoiceGuide = "voiceGuide";//语音提示
        public final String UseVoiceGuideTimes = "useVoiceGuideTimes";//使用语音提示次数
        public final String LastCameraId = "lastCameraId";
        public final String PreviewRatio = "previewRatio";
        public final String PreviewPatch_0 = "previewPatch_0";
        public final String PreviewPatch_1 = "previewPatch_1";
        public final String PicturePatch_0 = "picturePatch_0";
        public final String PicturePatch_1 = "picturePatch_1";
        public final String FixPreviewPatch_0 = "fixPreviewPatch_0";
        public final String FixPreviewPatch_1 = "fixPreviewPatch_1";
        public final String FaceGuideTakePicture = "faceGuideTakePicture";
        public final String ShowCameraPatchBtn = "showCameraPatchBtn";
        public final String UsePatchBtn = "usePatchBtn";
        public final String TouchCapture = "touchCapture";
        public final String NoSound = "noSound";
        public final String NoTickSound = "noTickSound";
        public final String CameraFilterId = "cameraFilterId";
        public final String OpenBeauty = "openBeauty";
        public final String ActualBeauty = "actualBeauty";//实时美颜
        public final String CameraMode = "cameraMode";
        public final String PhotoSize = "photoSize";
        public final String DebugMode = "debugMode";
        public final String SettingNewTip = "settingNewTip";
        public final String TailorMadeSwitchOn = "tailorMadeSwitch"; // 新版美形定制 开关

        public final String UpdateAndFirstRun = "updateAndFirstRun"; //更新或第一次运行

        public final String SelectTab = "selectTab";
        public final String PhotoRatio = "photoRatio";
        public final String CuteRatio = "cuteRatio";
        public final String VideoRatio = "videoRatio";
        public final String GifRatio = "gifRatio";
    }

    public static CameraConfig getInstance() {
        if (instance == null) {
            synchronized (CameraConfig.class) {
                if (instance == null) {
                    instance = new CameraConfig();
                }
            }
        }
        return instance;
    }

    private CameraConfig() {
    }

    @Override
    public final SharedPreferences getSharedPreferences(Context context) {
        if (context == null) return null;
        return context.getSharedPreferences(configName, Context.MODE_PRIVATE);
    }

    @Override
    public final void initDefaultData(HashMap<String, Object> defaultData) {
        defaultData.put(ConfigMap.FlashMode, 0);//闪光灯模式
        defaultData.put(ConfigMap.FlashModeStr, FlashMode.Off);//闪光灯模式
        defaultData.put(ConfigMap.FrontFlashModeStr, FlashMode.Off);//前置闪光灯模式
        defaultData.put(ConfigMap.TimerMode, 0);//定时拍照模式
        defaultData.put(ConfigMap.VoiceGuide, 0);//语音提示
        defaultData.put(ConfigMap.UseVoiceGuideTimes, 0);//使用语音提示次数
        defaultData.put(ConfigMap.LastCameraId, 0);
        defaultData.put(ConfigMap.PreviewRatio, 0);
        defaultData.put(ConfigMap.PreviewPatch_0, 0);
        defaultData.put(ConfigMap.PreviewPatch_1, 0);
        defaultData.put(ConfigMap.PicturePatch_0, 0);
        defaultData.put(ConfigMap.PicturePatch_1, 0);
        defaultData.put(ConfigMap.FixPreviewPatch_0, false);
        defaultData.put(ConfigMap.FixPreviewPatch_1, false);
        defaultData.put(ConfigMap.FaceGuideTakePicture, false);
        defaultData.put(ConfigMap.ShowCameraPatchBtn, 0);
        defaultData.put(ConfigMap.UsePatchBtn, false);
        defaultData.put(ConfigMap.TouchCapture, false);
        defaultData.put(ConfigMap.NoSound, true);
        defaultData.put(ConfigMap.NoTickSound, false);
        defaultData.put(ConfigMap.CameraFilterId, 0);
        defaultData.put(ConfigMap.OpenBeauty, true);
        defaultData.put(ConfigMap.ActualBeauty, true);//实时美颜
        defaultData.put(ConfigMap.CameraMode, 6);
        defaultData.put(ConfigMap.PhotoSize, 1024);
        defaultData.put(ConfigMap.DebugMode, false);
        defaultData.put(ConfigMap.SettingNewTip, true);
        defaultData.put(ConfigMap.TailorMadeSwitchOn, false);
        defaultData.put(ConfigMap.UpdateAndFirstRun, true);

        defaultData.put(ConfigMap.SelectTab, -1);
        defaultData.put(ConfigMap.PhotoRatio, PreviewRatio.Ratio_4_3);
        defaultData.put(ConfigMap.CuteRatio, PreviewRatio.Ratio_Full);
        defaultData.put(ConfigMap.VideoRatio, PreviewRatio.Ratio_Full);
        defaultData.put(ConfigMap.GifRatio, PreviewRatio.Ratio_1_1);
    }

    @Override
    public void clearAll() {
        super.clearAll();
        instance = null;
    }
}
