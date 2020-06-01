package cn.poco.live;

import cn.poco.camera3.beauty.data.ShapeDataType;

/**
 * @author lmx
 *         Created by lmx on 2018-01-22.
 */

public interface PCStatusShapeListener extends PCStatusListener
{
    void onPCClickShapeTab();

    void onPCSelectedShape(int shapeId);

    void onPCShapeSubLayoutOpen(int shapeId, boolean open);

    void onPCResetShapeData(int shapeId);

    void onPCSlideShapeAdjust(@ShapeDataType int type, int shapeId, int uiProgress);
}
