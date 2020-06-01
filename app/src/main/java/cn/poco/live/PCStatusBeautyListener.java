package cn.poco.live;

import cn.poco.camera3.beauty.STag;

/**
 * @author lmx
 *         Created by lmx on 2018-01-22.
 */

public interface PCStatusBeautyListener extends PCStatusListener
{
    void onPCClickBeautyTab();

    void onPCSliderBeauty(@STag.BeautyTag int type, int progress);
}
