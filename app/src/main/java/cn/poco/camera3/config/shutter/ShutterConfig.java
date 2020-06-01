package cn.poco.camera3.config.shutter;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Gxx on 2017/8/9.
 *
 */

public class ShutterConfig
{
    public static final int ALL_TYPE = TabType.GIF | TabType.PHOTO | TabType.CUTE | TabType.VIDEO; // 记录类型总数
    public static final int ALL_VIDEO_DURATION_TYPE = VideoDurationType.DURATION_TEN_SEC | VideoDurationType.DURATION_THREE_MIN;
    public static final int NO_TYPE = 0;

    /**
     * 是否是有效的 tab 类型
     * @param tabType 需要检查的类型 {@link ShutterConfig.TabType}
     * @return 有效true
     */
    public static boolean checkTabTypeIsValid(@TabType int tabType)
    {
        return (ALL_TYPE & tabType) != 0;
    }

    /**
     * 是否是有效的 快门 类型
     * @param shutterType 需要检查的类型 {@link ShutterConfig.ShutterType}
     * @return 有效true
     */
    public static boolean checkShutterTypeIsValid(@ShutterType int shutterType)
    {
        int all = ShutterType.DEF | ShutterType.PAUSE_RECORD | ShutterType.RECORDING | ShutterType.UNFOLD_RES;
        return (all & shutterType) != 0;
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface TabType // 四种类型
    {
       int GIF          = 1; // 表情包
       int PHOTO        = 1 << 1; // 高清拍照
       int CUTE         = 1 << 2; // 萌妆照
       int VIDEO        = 1 << 3; // 视频
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface ShutterType // 每种类型都有 四种类型
    {
        int DEF             = 1 << 4; // 默认
        int UNFOLD_RES      = 1 << 5; // 素材列表展开
        int RECORDING       = 1 << 6; // 录制过程
        int PAUSE_RECORD    = 1 << 7; // 暂停录制
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface VideoDurationType
    {
        int DURATION_TEN_SEC        = 1 << 8; // 10s
        int DURATION_THREE_MIN      = 1 << 9; // 3min
        int DURATION_ONE_MIN        = 1 << 10; //1min
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface RecordStatus
    {
        int CAN_RECORDED        = 1 << 10; // 可以录制
        int PAUSE_VIDEO         = 1 << 11; // 暂停 video 录制
        int LESS_THAN_ONE_SEC   = 1 << 12; // 录制 少于1s
        int FULL_PROGRESS       = 1 << 13; // 满进度
        int AT_THE_LAST_SEC     = 1 << 14; // 最后1s
        int RESET_CAN_RECORDED  = 1 << 15; // 重置可以录制状态，一般是 暂停后 使用
    }
}
