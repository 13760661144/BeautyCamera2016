package cn.poco.camera3.cb;

import cn.poco.camera3.StickerPageListener;

public interface CameraPageListener extends StickerPageListener
{
    // tab 类型转换
    void onTabTypeChange(int type);

    void onCloseStickerList();

    // 4.3版本以下选中gif、视频要弹出提示
    void onShowLessThan18SDKTips(int type);

    //判断是否可录制
    boolean canRecord();

    //是否在录制状态
    boolean isRecording();

    //判断是否可暂停录制
    boolean canPauseRecord();

    //是否正在倒计时
    boolean isCountDown();

    // 倒计时
    void onTiming();

    boolean isPatchMode();

    // 快门单击
    void onShutterClick();

    void onVideoProgressFull();

    void onPauseVideo();

    void onBackBtnClick();

    void onCancelCountDown();

    /**
     * 打开相册
     */
    void onClickOpenPhoto();

    /**
     * 设置
     */
    void onClickSetting();

    /**
     * 切换镜头
     */
    void onClickCameraSwitch();

    /**
     * 校正预览
     */
    void onClickCameraPatch();

    /**
     * 比例切换
     */
    void onClickRatioBtn();

    void onClickVideoDurationBtn(int second);

    void onClickVideoSaveBtn();

    void onConfirmVideoDel();

    void onClearAllVideo();
}
