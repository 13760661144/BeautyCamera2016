package cn.poco.live;

/**
 * @author lmx
 *         Created by lmx on 2018-01-22.
 */

public abstract class PCStatusListenerAdapter
        implements PCStatusListener, PCStatusFilterListener, PCStatusShapeListener, PCStatusBeautyListener
{
    public PCStatusListenerAdapter()
    {
    }

    @Override
    public void onPCConnected()
    {
    }

    @Override
    public void onPCDisconnected()
    {
    }

    @Override
    public void onPCSelectedDecor(int id)
    {
    }

    @Override
    public void onPCClickDecorTab()
    {
    }

    @Override
    public void onPCClickBeautyTab()
    {
    }

    @Override
    public void onPCClickShapeTab()
    {
    }

    @Override
    public void onPCClickFilterTab()
    {
    }

    @Override
    public void onPCSliderBeauty(int type, int progress)
    {

    }

    @Override
    public void onPCSelectedShape(int shapeId)
    {

    }

    @Override
    public void onPCSelectedFilter(int filterId)
    {

    }

    @Override
    public void onPCShapeSubLayoutOpen(int shapeId, boolean open)
    {

    }

    @Override
    public void onPCFilterSubLayoutOpen(boolean open)
    {

    }

    @Override
    public void onPCResetShapeData(int shapeId)
    {

    }

    @Override
    public void onPCResetFilterData(int filterId)
    {

    }

    @Override
    public void onPCSlideShapeAdjust(int type, int shapeId, int uiProgress)
    {

    }
}
