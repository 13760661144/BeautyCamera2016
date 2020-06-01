package cn.poco.camera;

import java.util.HashMap;

import cn.poco.camera3.config.shutter.ShutterConfig;
import cn.poco.filterBeautify.FilterBeautifyProcessor;
import cn.poco.framework.MyFramework;

/**
 * 镜头setData参数
 *
 * @author lmx
 *         Created by lmx on 2017/8/24.
 */

public class CameraSetDataKey
{
    //tab 类型相关
    public static final String KEY_CAMERA_SELECT_TAB_TYPE = "selectTabType";    /*指定标签类型（{@link cn.poco.camera3.config.shutter.ShutterConfig.TabType}）*/
    public static final String KEY_CAMERA_SHOW_TAB_TYPE = "showTabType";      /*需显示标签类型（{@link cn.poco.camera3.config.shutter.ShutterConfig.TabType}）*/
    public static final String KEY_CAMERA_SELECT_RATIO = "selectRatio";      /*指定镜头比例（4：3 {@link CameraConfig.PreviewRatio#Ratio_4_3}）*/
    public static final String KEY_CAMERA_FORCE_TAB_RATIO = "forceTabRatio"; /*所有tab都用指定的比例*/


    //动态贴纸相关（int）
    public static final String KEY_CAMERA_STICKER_CATEGORY_ID = "camera_sticker_category_id";       /*需要显示的贴纸分类id*/
    public static final String KEY_CAMERA_STICKER_STICKER_ID = "camera_sticker_sticker_id";        /*贴纸id*/
    public static final String KEY_CAMERA_STICKER_REMEMBER_STICKER_ID = "camera_sticker_remember_sticker_id";        /*记录使用过的贴纸id*/
    public static final String KEY_CAMERA_STICKER_CATEGORY_ID_JUST_GOTO = "key_camera_sticker_category_id_just_goto"; /* 选中某一个贴纸分类id*/

    //指定滤镜id
    public static final String KEY_CAMERA_FILTER_ID = "camera_filter_id";                       /*滤镜id* -1情况下，使用默认滤镜*/

    //功能按钮组件隐显相关（boolean）
    public static final String KEY_HIDE_PHOTO_PICKER_BTN = "hidePhotoPickerBtn";       /*隐藏相册入口*/
    public static final String KEY_HIDE_STICKER_BTN = "hideStickerBtn";           /*隐藏贴纸按钮*/
    public static final String KEY_HIDE_FILTER_SELECTOR = "hideFilterSelector";       /*隐藏滤镜按钮（滤镜选择）*/
    public static final String KEY_HIDE_BEAUTY_SETTING = "hideBeautySetting";        /*隐藏美形定制按钮*/
    public static final String KEY_HIDE_WATER_MARK = "hideWaterMark";            /*隐藏美妆拍照水印*/
    public static final String KEY_HIDE_SETTING_BTN = "hideSettingBtn";            /*隐藏设置*/
    public static final String KEY_HIDE_RATIO_BTN = "hideRatioBtn";            /*隐藏比例*/
    public static final String KEY_HIDE_PATCH_BTN = "hidePatchBtn";            /*隐藏校正*/
    public static final String KEY_HIDE_STICKER_MANAGER_BTN = "hideStickerManagerBtn";      /*隐藏贴纸素材管理按钮入口*/


    //美形滤镜拍照处理相关
    public static final String KEY_IS_FILTER_BEAUTITY_PROCESS = "isFilterBeautifyProcess";      /*拍照后是否做美颜滤镜处理，耗时操作（boolean）*/
    public static final String KEY_FILTER_BEAUTITY_PROCESS_MASK = "filterBeautifyProcessMask";    /*美形 美颜 滤镜 位运算值 参看{@link FilterBeautifyProcessor#ONLY_BEAUTY}*/

    //美形定制设置
    public static final String KEY_TAILOR_MADE_SETTING = "tailorMadeSetting";
    public static final String KEY_HIDE_TAILOR_MADE_TIP = "hideTailorMadeTip";//隐藏美形定制对话框


    //第三方参数（boolean）
    public static final String KEY_IS_THIRD_PARTY_VIDEO = "isThirdPartyVideo";
    public static final String KEY_IS_THIRD_PARTY_PICTURE = "isThirdPartyPicture";
    public static final String KEY_EXTERNAL_CALL_IMG_SAVE_URI = MyFramework.EXTERNAL_CALL_IMG_SAVE_URI;

    //初始化基本参数
    public static final String KEY_HAS_OPEN_ANIM = "hasOpenAnim";                  /*打开镜头是否过度动画（boolean）*/
    public static final String KEY_START_MODE = "startMode";                    /*打开指定镜头，默认为-1:不指定，0:(正常模式/后置镜头)，1:(自拍模式/前置镜头)（int）*/
    public static final String KEY_PATCH_CAMERA = "patchCamera";                  /*校正镜头，默认为-1:不校正镜头，0:后置，1:前置（int）*/
    public static final String KEY_PATCH_FINISH_TO_CLOSE = "isPatchFinishToClose";         /*矫正完镜头后是否关闭page（boolean）*/
    public static final String KEY_EXPOSURE_VALUE = "exposureValue";                /*曝光值*/
    public static final String KEY_IS_SHOW_STICKER_SELECTOR = "isShowStickerSelector";        /*初始化显示列表*/
    public static final String KEY_CAMERA_FLASH_MODE = "camera_flash_mode";            /*设置闪关灯模式 {@link CameraConfig.FlashMode} */
    public static final String KEY_CAMERA_FRONT_SPLASH_MASK = "front_splash_mask";      /*前置模拟闪屏的前置补光*/


    public static final String KEY_VIDEO_RECORD_TIME_TYPE = "videoRecordTimeType";          /*视频录制时间时长类型 {@link cn.poco.camera3.config.shutter.ShutterConfig.VideoDurationType}*/
    public static final String KEY_VIDEO_RECORD_MIN_DURATION = "videoRecordMinDuration";    /*视频录制最短时长（long, 毫秒），1000L <= 最短时长 <= videoRecordTimeType * 1000L */
    public static final String KEY_VIDEO_MULTI_SECTION_ENABLE = "videoMultiSectionEnable";  /*是否支持多段录制 （boolean）*/
    public static final String KEY_VIDEO_MULTI_ORIENTATION_ENABLE = "videoMultiOrientationEnable";  /*是否支持多个方向录制 （boolean）*/

    //商业参数
    public static final String KEY_IS_BUSINESS = "isBusiness";               /*是否商业通道(boolean)*/

    //是否返回镜头（boolean）
    public static final String KEY_IS_PAGE_BACK = "isPageBack";               /*是否返回镜头page*/

    //是否贴纸预览返回
    public static final String KEY_IS_STICKER_PREVIEWBACK = "isStickerPreviewBack";     /*是否贴纸预览返回*/

    //是否恢复视频录制的暂停状态(boolean)
    public static final String KEY_IS_RESUME_VIDEO_PAUSE = "isResumeVideoPause";       /*是否恢复视频录制的暂停状态*/
    public static final String KEY_RESUME_VIDEO_PAUSE_MGR = "resumeVideoMgr";           /*恢复视频录制视频对象*/


    /**
     * 注册头像拍照（隐藏gif，隐藏美形定制，隐藏萌装照拍照水印）
     *
     * @param hideMakeupPicture 隐藏萌装照入口
     * @return
     */
    public static HashMap<String, Object> GetRegisterTakePicture(boolean hideMakeupPicture)
    {
        HashMap<String, Object> params = new HashMap<>();
        params.put(CameraSetDataKey.KEY_START_MODE, 1);
        params.put(CameraSetDataKey.KEY_HIDE_PHOTO_PICKER_BTN, true);
        int type = ShutterConfig.TabType.PHOTO | ShutterConfig.TabType.CUTE;
        if (hideMakeupPicture)
        {
            type = ShutterConfig.TabType.PHOTO;
        }
        params.put(CameraSetDataKey.KEY_CAMERA_SHOW_TAB_TYPE, type);
        params.put(CameraSetDataKey.KEY_CAMERA_SELECT_TAB_TYPE, ShutterConfig.TabType.PHOTO);
        params.put(CameraSetDataKey.KEY_HIDE_WATER_MARK, true);
        return params;
    }

    /**
     * 商业拍照（隐藏gif，隐藏萌装照拍照水印，是否显示美形定制，是否显示萌装照）
     *
     * @param hideMakeupPicture 隐藏萌装照入口
     * @param hideBeautySetting 隐藏美形定制开关
     * @return
     */
    public static HashMap<String, Object> GetBussinessTakePicture(boolean hideMakeupPicture, boolean hideBeautySetting)
    {
        HashMap<String, Object> params = new HashMap<>();
        params.put(CameraSetDataKey.KEY_START_MODE, 1);
        params.put(CameraSetDataKey.KEY_HIDE_PHOTO_PICKER_BTN, true);
        int type = ShutterConfig.TabType.PHOTO | ShutterConfig.TabType.CUTE;
        if (hideMakeupPicture)
        {
            type = ShutterConfig.TabType.PHOTO;
        }
        params.put(CameraSetDataKey.KEY_IS_FILTER_BEAUTITY_PROCESS, true);//拍照处理美颜滤镜效果
        params.put(CameraSetDataKey.KEY_FILTER_BEAUTITY_PROCESS_MASK, FilterBeautifyProcessor.ONLY_SHAPE | FilterBeautifyProcessor.ONLY_BEAUTY);
        params.put(CameraSetDataKey.KEY_CAMERA_SHOW_TAB_TYPE, type);
        params.put(CameraSetDataKey.KEY_CAMERA_SELECT_TAB_TYPE, ShutterConfig.TabType.PHOTO);
        params.put(CameraSetDataKey.KEY_HIDE_WATER_MARK, true);
        params.put(CameraSetDataKey.KEY_HIDE_FILTER_SELECTOR, true);
        params.put(CameraSetDataKey.KEY_HIDE_BEAUTY_SETTING, hideBeautySetting);
        params.put(CameraSetDataKey.KEY_IS_BUSINESS, true);
        return params;
    }


    /**
     * 通用通道调用拍照（只有拍照，隐藏滤镜，隐藏美形定制，拍照后处理美形美颜）
     *
     * @return
     */
    public static HashMap<String, Object> GetWayTakePicture(boolean isBusiness, boolean filterBeauty, int filterMask)
    {
        HashMap<String, Object> params = new HashMap<>();
        params.put(CameraSetDataKey.KEY_IS_BUSINESS, isBusiness);
        params.put(CameraSetDataKey.KEY_HIDE_PHOTO_PICKER_BTN, true);
        params.put(CameraSetDataKey.KEY_HIDE_BEAUTY_SETTING, true);
        params.put(CameraSetDataKey.KEY_HIDE_FILTER_SELECTOR, true);
        params.put(CameraSetDataKey.KEY_HIDE_STICKER_BTN, true);

        if (filterBeauty)
        {
            params.put(CameraSetDataKey.KEY_IS_FILTER_BEAUTITY_PROCESS, true);//拍照处理美颜滤镜效果
            params.put(CameraSetDataKey.KEY_FILTER_BEAUTITY_PROCESS_MASK, filterMask);
        }
        params.put(CameraSetDataKey.KEY_CAMERA_SELECT_TAB_TYPE, ShutterConfig.TabType.PHOTO);
        params.put(CameraSetDataKey.KEY_CAMERA_SHOW_TAB_TYPE, ShutterConfig.TabType.PHOTO);
        return params;
    }


    /**
     * 只有拍照（隐藏萌装照、隐藏gif、隐藏视频拍摄）
     *
     * @param hidePhotoPick 是否隐藏相册入口
     * @return
     */
    public static HashMap<String, Object> GetOnlyTakePicture(boolean hidePhotoPick)
    {
        HashMap<String, Object> params = new HashMap<>();
        params.put(CameraSetDataKey.KEY_CAMERA_SHOW_TAB_TYPE, ShutterConfig.TabType.PHOTO);
        params.put(CameraSetDataKey.KEY_CAMERA_SELECT_TAB_TYPE, ShutterConfig.TabType.PHOTO);
        params.put(CameraSetDataKey.KEY_HIDE_STICKER_BTN, true);
        params.put(CameraSetDataKey.KEY_HIDE_PHOTO_PICKER_BTN, hidePhotoPick);
        return params;
    }

    /**
     * 动态贴纸跳转协议（萌装照 + 视频 + GIF）
     *
     * @param defaultSelectTab 默认展开tab  {@link ShutterConfig.TabType}
     * @return
     */
    public static HashMap<String, Object> GetStickerStep(@ShutterConfig.TabType int defaultSelectTab)
    {
        HashMap<String, Object> params = new HashMap<>();
        params.put(CameraSetDataKey.KEY_START_MODE, 1);
        params.put(CameraSetDataKey.KEY_IS_SHOW_STICKER_SELECTOR, true);
        params.put(CameraSetDataKey.KEY_CAMERA_SELECT_TAB_TYPE, defaultSelectTab);
        params.put(CameraSetDataKey.KEY_CAMERA_SHOW_TAB_TYPE, ShutterConfig.TabType.CUTE | ShutterConfig.TabType.VIDEO | ShutterConfig.TabType.GIF);
        return params;
    }


    /**
     * 动态贴纸跳转协议（只有视频）
     *
     * @return
     */
    public static HashMap<String, Object> GetOnlyVideoStep()
    {
        HashMap<String, Object> params = new HashMap<>();
        params.put(CameraSetDataKey.KEY_START_MODE, 1);
        params.put(CameraSetDataKey.KEY_IS_SHOW_STICKER_SELECTOR, true);
        params.put(CameraSetDataKey.KEY_CAMERA_SELECT_TAB_TYPE, ShutterConfig.TabType.VIDEO);
        params.put(CameraSetDataKey.KEY_CAMERA_SHOW_TAB_TYPE, ShutterConfig.TabType.VIDEO);
        return params;
    }

    /**
     * gif跳转协议
     *
     * @return
     */
    public static HashMap<String, Object> GetGifStep()
    {
        HashMap<String, Object> params = new HashMap<>();
        params.put(CameraSetDataKey.KEY_START_MODE, 1);
        params.put(CameraSetDataKey.KEY_IS_SHOW_STICKER_SELECTOR, true);
        params.put(CameraSetDataKey.KEY_CAMERA_SHOW_TAB_TYPE, ShutterConfig.TabType.GIF);
        params.put(CameraSetDataKey.KEY_CAMERA_SELECT_TAB_TYPE, ShutterConfig.TabType.GIF);
        return params;
    }

    /**
     * 镜头萌妆照跳转协议
     *
     * @return
     */
    public static HashMap<String, Object> GetMakeupStep()
    {
        HashMap<String, Object> params = new HashMap<>();
        params.put(CameraSetDataKey.KEY_START_MODE, 1);
        params.put(CameraSetDataKey.KEY_IS_SHOW_STICKER_SELECTOR, true);
        params.put(CameraSetDataKey.KEY_CAMERA_SELECT_TAB_TYPE, ShutterConfig.TabType.CUTE);
        params.put(CameraSetDataKey.KEY_CAMERA_SHOW_TAB_TYPE, ShutterConfig.TabType.CUTE);
        return params;
    }

    /**
     * 视频 萌妆照 跳转协议(默认打开萌妆照)
     *
     * @return
     */
    public static HashMap<String, Object> GetMakeupAndVideo()
    {
        HashMap<String, Object> params = new HashMap<>();
        params.put(CameraSetDataKey.KEY_START_MODE, 1);
        params.put(CameraSetDataKey.KEY_IS_SHOW_STICKER_SELECTOR, true);
        params.put(CameraSetDataKey.KEY_CAMERA_SELECT_TAB_TYPE, ShutterConfig.TabType.CUTE);
        params.put(CameraSetDataKey.KEY_CAMERA_SHOW_TAB_TYPE, ShutterConfig.TabType.CUTE | ShutterConfig.TabType.VIDEO);
        return params;
    }

    /**
     * 只拍照 跳转协议(相当于只显示 高清拍照 萌妆照, 默认打开萌妆照)
     *
     * @return
     */
    public static HashMap<String, Object> GetMakeupAndTakePicture()
    {
        HashMap<String, Object> params = new HashMap<>();
        params.put(CameraSetDataKey.KEY_START_MODE, 1);
        params.put(CameraSetDataKey.KEY_IS_SHOW_STICKER_SELECTOR, true);
        params.put(CameraSetDataKey.KEY_CAMERA_SELECT_TAB_TYPE, ShutterConfig.TabType.CUTE);
        params.put(CameraSetDataKey.KEY_CAMERA_SHOW_TAB_TYPE, ShutterConfig.TabType.CUTE | ShutterConfig.TabType.PHOTO);
        return params;
    }

    /**
     * 第三方调用镜头录像
     *
     * @return
     */
    public static HashMap<String, Object> GetExternalTakeVideo()
    {
        HashMap<String, Object> params = new HashMap<>();
        params.put(CameraSetDataKey.KEY_HIDE_PHOTO_PICKER_BTN, true);
        params.put(CameraSetDataKey.KEY_HIDE_BEAUTY_SETTING, true);
        params.put(CameraSetDataKey.KEY_IS_THIRD_PARTY_VIDEO, true);
        params.put(CameraSetDataKey.KEY_VIDEO_RECORD_TIME_TYPE, ShutterConfig.VideoDurationType.DURATION_TEN_SEC);
        params.put(CameraSetDataKey.KEY_CAMERA_SHOW_TAB_TYPE, ShutterConfig.TabType.VIDEO);
        params.put(CameraSetDataKey.KEY_CAMERA_SELECT_TAB_TYPE, ShutterConfig.TabType.VIDEO);
        return params;
    }

    /**
     * 第三方调用镜头拍照（带有美形定制）
     *
     * @return
     */
    public static HashMap<String, Object> GetExternalTakePicture()
    {
        HashMap<String, Object> params = new HashMap<>();
        params.put(CameraSetDataKey.KEY_HIDE_PHOTO_PICKER_BTN, true);
        params.put(CameraSetDataKey.KEY_HIDE_STICKER_BTN, true);
        params.put(CameraSetDataKey.KEY_IS_THIRD_PARTY_PICTURE, true);
        params.put(CameraSetDataKey.KEY_VIDEO_RECORD_TIME_TYPE, ShutterConfig.VideoDurationType.DURATION_TEN_SEC);
        params.put(CameraSetDataKey.KEY_CAMERA_SHOW_TAB_TYPE, ShutterConfig.TabType.PHOTO);
        params.put(CameraSetDataKey.KEY_CAMERA_SELECT_TAB_TYPE, ShutterConfig.TabType.PHOTO);
        return params;
    }

}
