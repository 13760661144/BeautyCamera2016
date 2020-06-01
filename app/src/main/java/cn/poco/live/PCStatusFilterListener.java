package cn.poco.live;

/**
 * @author lmx
 *         Created by lmx on 2018-01-22.
 */

public interface PCStatusFilterListener extends PCStatusListener
{
    void onPCClickFilterTab();

    void onPCSelectedFilter(int filterId);

    void onPCFilterSubLayoutOpen(boolean open);

    void onPCResetFilterData(int filterId);
}
