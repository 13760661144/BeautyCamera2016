package cn.poco.camera3.cb.sticker;

import cn.poco.resource.VideoStickerRes;

public interface StickerUIListener
{
	// 素材管理页监听
	void onOpenStickerMgrPage();

	void onCloseStickerMgrPage();

	// 素材选中监听
	void onSelectSticker(VideoStickerRes stickerRes, boolean repeat);

	void onSelectSticker(VideoStickerRes stickerRes, boolean repeat, boolean isTabChange);

	//贴纸音效开光
	void onStickerSoundMute(boolean mute);

	//判断audio是否静音
	boolean getAudioMute();

	//关闭素材列表
	void onCloseStickerList();
}
