package cn.poco.camera3.config;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author Gxx
 *         Created by Gxx on 2017/9/1.
 */

public class CameraBtnConfig
{
    public static final int ALL_BTN_GONE = 0;

    public static final int SHOW_ALL_TOP_BAR_BTN = BarType.CAMERA_ADJUST | BarType.CAMERA_SETTING
            | BarType.CAMERA_RATIO | BarType.CAMERA_DIRECTION;

    @Retention(RetentionPolicy.SOURCE)
    public @interface ControlType
    {
        int CLOSE_PAGE          = 1; // 关闭页面 btn
        int COLOR_FILTER        = 1 << 1; // 滤镜 btn
        int STICKERS            = 1 << 2; // 贴纸 btn
        int PHOTO_ALBUM         = 1 << 3; // 相册 btn
        int VIDEO_DURATION      = 1 << 4; // 录制时长 btn
        int VIDEO_SAVE          = 1 << 5; // 跳转预览页 btn
        int VIDEO_NO_SAVE       = 1 << 6; // 放弃已录制视频 btn
        int VIDEO_DELETE        = 1 << 7; // 录制暂停时，删除分段视频 btn
        int TAB_SEL_POINT       = 1 << 8; // 当前tab 选中的 点
        int BEAUTY              = 1 << 9; // 美形定制 btn
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface BarType
    {
        int CAMERA_ADJUST               = 1; // 镜头校正 btn
        int CAMERA_SETTING              = 1 << 1; // 镜头设置 btn
        int CAMERA_RATIO                = 1 << 2; // 比例 btn
//        int CAMERA_BEAUTY_SETTING       = 1 << 3; // 美形定制 btn
        int CAMERA_DIRECTION            = 1 << 4; // 前后置镜头 btn
    }

    /**
     * 顺序根据需求而定, 从左至右
     */
    @Retention(RetentionPolicy.SOURCE)
    public @interface BarBtnIndex
    {
        int CAMERA_ADJUST           = 0; // 校正镜头方向
        int CAMERA_SETTING          = 1; // pop setting
        int CAMERA_RATIO            = 2; // 切换比例
//        int CAMERA_BEAUTY_SETTING   = 3; // 美形定制
        int CAMERA_DIRECTION        = 3; // 切换前后置镜头
    }
}
