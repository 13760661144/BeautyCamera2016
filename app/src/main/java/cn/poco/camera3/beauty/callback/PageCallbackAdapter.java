package cn.poco.camera3.beauty.callback;

import android.support.annotation.Nullable;
import android.view.View;

import java.util.ArrayList;

import cn.poco.camera3.beauty.STag;
import cn.poco.camera3.beauty.TabUIConfig;
import cn.poco.camera3.beauty.data.BeautyData;
import cn.poco.camera3.beauty.data.ShapeData;
import cn.poco.camera3.beauty.recycler.ShapeExAdapter;
import cn.poco.resource.FilterRes;
import cn.poco.resource.RecommendRes;

/**
 * @author lmx
 *         Created by lmx on 2018-01-17.
 */

public abstract class PageCallbackAdapter implements
        IPageCallback,
        IFilterPageCallback,
        IShapePageCallback,
        IBeautyPageCallback
{

    @Override
    public void onSeekBarSlide(View seek, int progress, boolean isStop)
    {

    }

    @Override
    public void setShowSelectorView(boolean show)
    {
    }

    @Override
    public void onShowTopBar(boolean show)
    {
    }

    @Override
    public void onFilterItemClick(FilterRes filterRes, boolean showToast)
    {

    }

    @Override
    public void onFilterItemDownload()
    {

    }

    @Override
    public void onFilterItemRecommend(ArrayList<RecommendRes> ress)
    {

    }

    @Override
    public void onShapeItemClick(@Nullable ShapeExAdapter.ShapeExItemInfo itemInfo, ShapeData data)
    {

    }

    @Override
    public void onShapeUpdate(int subPosition, @Nullable ShapeExAdapter.ShapeExItemInfo itemInfo, ShapeData data)
    {

    }

    @Override
    public void onResetShapeData(int subPosition, @Nullable ShapeExAdapter.ShapeExItemInfo itemInfo, ShapeData data)
    {

    }

    @Override
    public void onBeautyUpdate(@STag.BeautyTag int type, BeautyData beautyData)
    {

    }

    @Override
    public void onSubLayoutOpen(boolean isOpen, boolean showSeekBar, int position, ShapeExAdapter.ShapeExItemInfo itemInfo)
    {

    }

    @Override
    public Object getFramePagerData(int position, String frameTag, boolean isUpdate)
    {
        return null;
    }

    @Override
    public void onPageSelected(int position, TabUIConfig.TabUI tabUI)
    {

    }

    @Override
    public void onFilterUpdateAdd(FilterRes filterRes, int filter_id)
    {

    }

    @Override
    public void onFilterUpdateRemove(ArrayList<Integer> ids)
    {

    }

    @Override
    public FilterRes getCameraFilterRes()
    {
        return null;
    }
}
