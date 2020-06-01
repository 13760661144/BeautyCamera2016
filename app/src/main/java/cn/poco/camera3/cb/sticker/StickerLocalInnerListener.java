package cn.poco.camera3.cb.sticker;

/**
 * 贴纸素材 管理页 ui 内部通信回调
 * @author Created by Gxx on 2017/10/30.
 */

public interface StickerLocalInnerListener
{
    void onSelectedLabel(int index);

    void onStickerPageSelected(int index);

    void onLabelScrollToSelected(int index);

    void onChangeSelectedIconStatus(int status);

    void onChangeDeleteIconAlpha(int alpha);
}
