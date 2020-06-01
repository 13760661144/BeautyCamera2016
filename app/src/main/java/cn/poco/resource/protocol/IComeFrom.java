package cn.poco.resource.protocol;

import android.content.Context;

/**
 * 素材平台请求 come_from字段
 * 当前APP来源
 *
 * @author lmx
 *         Created by lmx on 2017/7/21.
 */

public interface IComeFrom
{
    /**
     * 当前APP客户端
     *
     * @return beauty_camera_android
     */
    public String GetAppName(Context context);

    /**
     * 当前app版本号
     *
     * @return
     */
    public String GetAppVersion(Context context);

    /**
     * 当前APP项目名
     *
     * @return beauty_camera
     */
    public String GetProjectName();
}
