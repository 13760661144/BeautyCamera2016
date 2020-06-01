package cn.poco.camera3.cb;

/**
 * @author Gxx
 *      Created by Gxx on 2017/8/24.
 */

public interface ControlUIListener
{
    void onClickColorFilterBtn();

    void onClickStickerBtn();

    void onClickVideoDelBtn();

    void onCancelSelectedVideo();

    void onClickBeautyBtn();

    boolean isShowFilterList();

    boolean isShowBeautyList();
}
