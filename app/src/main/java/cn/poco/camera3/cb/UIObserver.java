package cn.poco.camera3.cb;

import java.util.Observer;

/**
 * @author Gxx
 *      Created by Gxx on 2017/8/23.
 *      通知不同UI部件
 */

public interface UIObserver extends Observer
{
    int MSG_UNLOCK_UI                   = 0; // 解锁UI
    int MSG_LOCK_UI                     = 1; // 缩UI
    int MSG_ONLY_SHOW_CAMERA_SWITCH     = 2; // 只显示前后置转换
    int MSG_GONE_TOP_ALL_UI             = 3; // 隐藏 top bar 所有 ui
    int MSG_SHOW_TOP_ALL_UI             = 4; // 显示 top bar 所有 ui
    int MSG_DO_VIDEO_RECORD_ANIM        = 5; // 做视频录制的动画ing
    int MSG_DO_VIDEO_PAUSE_ANIM         = 6; // 做视频暂停的动画ing
}
