package cn.poco.camera3;

/**
 * 弹窗解锁操作回调
 *
 * @author lmx
 *         Created by lmx on 2017/11/9.
 */

public interface UnLockUIListener
{
    /**
     * 贴纸素材解锁用户登录
     */
    void onUserLogin();

    /**
     * 贴纸素材解锁弹框关闭回调
     */
    void closeUnLockView();

    /**
     * 贴纸素材解锁弹框打开回调
     */
    void openUnLockView();
}
