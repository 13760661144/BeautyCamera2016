package cn.poco.camera3.cb.sticker;

import cn.poco.camera3.info.sticker.StickerInfo;
import cn.poco.resource.BaseRes;

/**
 * 贴纸素材 预览 ui 内部通信回调
 * @author Created by Gxx on 2017/10/12.
 */

public interface StickerInnerListener
{
    /**
     * 线程加载数据成功
     */
    void onLoadStickerDataSucceed();

    /**
     * 可以开始加载数据
     */
    void onCanLoadRes();

    /**
     * 模式转换
     */
    void onShutterTabChange();

    /**
     * 选中标签
     * @param index
     */
    void onSelectedLabel(int index);

    /**
     * 选中素材
     * @param info
     */
    void onSelectedSticker(StickerInfo info);

    /**
     * 左右滑动素材列表
     * @param index
     */
    void onStickerPageSelected(int index);

    /**
     * 将标签滚动到某个位置
     * @param index
     */
    void onLabelScrollToSelected(int index);

    /**
     * 更新镜头预览时，素材列表的全部数据(素材管理页返回时有调用)
     */
    void onRefreshAllData();

    /**
     * 比例切换时，更新UI
     * @param ratio
     */
    void onUpdateUIByRatio(float ratio);

    void popLockView(BaseRes res);

    void onScrollStickerToCenter();

    boolean checkNetworkAvailable();

    void onShowVolumeBtn(boolean show);
}
