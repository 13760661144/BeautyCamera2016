package cn.poco.camera3.beauty.callback;

import cn.poco.camera3.beauty.STag;
import cn.poco.camera3.beauty.data.BeautyData;

/**
 * @author lmx
 *         Created by lmx on 2018-01-17.
 */

public interface IBeautyPageCallback extends IPageCallback
{
    void onBeautyUpdate(@STag.BeautyTag int tag, BeautyData beautyData);
}
