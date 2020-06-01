package cn.poco.camera3.beauty.callback;

import java.util.ArrayList;

import cn.poco.resource.FilterRes;
import cn.poco.resource.RecommendRes;

/**
 * @author lmx
 *         Created by lmx on 2018-01-17.
 */

public interface IFilterPageCallback extends IPageCallback
{
    void onFilterItemClick(FilterRes filterRes, boolean showToast);

    void onFilterItemDownload();

    void onFilterItemRecommend(ArrayList<RecommendRes> ress);

    FilterRes getCameraFilterRes();

    void onFilterUpdateAdd(FilterRes filterRes, int filter_id);

    void onFilterUpdateRemove(ArrayList<Integer> ids);
}
