package cn.poco.live;

/**
 * 直播
 * Created by Gxx on 2018/1/16.
 */

public interface LivePageListener
{
    void OnSelectedSticker(Object obj);

    void onOpenStickerMgrPage();

    void onCloseStickerMgrPage();

    void onCloseStickerList();
}
