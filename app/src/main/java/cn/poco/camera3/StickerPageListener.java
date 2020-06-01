package cn.poco.camera3;

/**
 * 镜头贴纸素材监听回调
 *
 * @author lmx
 *         Created by lmx on 2017/11/9.
 */

public interface StickerPageListener
{
    /**
     * 素材选中监听
     */
    void onSelectSticker(Object info, boolean isTabChange);

    /**
     * 重复素材点击监听
     */
    void onSelectRepeatSticker(Object info);

    /**
     * 关闭贴纸管理page回调
     */
    void closeStickerMgrPage();

    /**
     * 打开贴纸管理page回调
     */
    void openStickerMgrPage();

    /**
     * 设置贴纸音效静音
     *
     * @param mute true 静音
     * @return true 成功
     */
    boolean onStickerSoundMute(boolean mute);

    /**
     * 检查是否静音
     *
     * @return true 当前静音
     */
    boolean getAudioMute();
}
