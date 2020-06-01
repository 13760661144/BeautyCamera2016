package cn.poco.camera3.beauty.callback;

import android.support.annotation.Nullable;

import cn.poco.camera3.beauty.data.ShapeData;
import cn.poco.camera3.beauty.recycler.ShapeExAdapter;

/**
 * @author lmx
 *         Created by lmx on 2018-01-17.
 */

public interface IShapePageCallback extends IPageCallback
{
    void onShapeItemClick(@Nullable ShapeExAdapter.ShapeExItemInfo itemInfo, ShapeData data);

    void onShapeUpdate(int subPosition, @Nullable ShapeExAdapter.ShapeExItemInfo itemInfo, ShapeData data);

    void onResetShapeData(int subPosition, @Nullable ShapeExAdapter.ShapeExItemInfo itemInfo, ShapeData data);

    void onSubLayoutOpen(boolean isOpen, boolean showSeekBar, int position, ShapeExAdapter.ShapeExItemInfo itemInfo);
}
