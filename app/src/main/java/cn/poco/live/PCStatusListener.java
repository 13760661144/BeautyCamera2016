package cn.poco.live;

/**
 * Created by admin on 2018/1/19.
 */

public interface PCStatusListener
{
    void onPCConnected();

    void onPCDisconnected();

    void onPCSelectedDecor(int id);

    void onPCClickDecorTab();
}
