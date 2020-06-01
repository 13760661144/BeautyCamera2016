package cn.poco.camera3.mgr;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 下载管理
 * Created by Gxx on 2017/10/10.
 */

public class StatusMgr
{
    @Retention(RetentionPolicy.SOURCE)
    public @interface Type
    {
        int NEW = 1;
        int NEED_DOWN_LOAD = 1 << 1;
        int DOWN_LOADING = 1 << 2;
        int LOCK = 1 << 3;
        int LIMIT = 1 << 4;
        int LOCAL = 1 << 5; // 已下载的
        int BUILT_IN = 1 << 6; // 内置
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface DownloadStatus
    {
        int IN_IDLE = 1;
        int SUCCEED = 1 << 1;
        int FAILED = 1 << 2;
        int ING = 1 << 3;
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface DownloadAnimStatus
    {
        int IN_IDLE = 1;
        int START = 1 << 1;
        int END = 1 << 2;
    }
}
