package cn.poco.camera3.cb;

import cn.poco.resource.FilterRes;

/**
 * @author lmx
 *         Created by lmx on 2017/8/24.
 */

public interface CameraFilterListener
{
    void onItemClickFilterRes(FilterRes filterRes, boolean isShowFilterMsgToast);

    void onItemClickFilterDownloadMore();
}
