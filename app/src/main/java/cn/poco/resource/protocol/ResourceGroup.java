package cn.poco.resource.protocol;

/**
 * 获取下载区与内置的作品列表
 *
 * @author lmx
 *         Created by lmx on 2017/7/21.
 */

public enum ResourceGroup
{
    LOCAL_RES(0),           //内置素材
    DOWNLOAD_BEFORE(-1),    //引导下载前
    DOWNLOAD(-2),           //下载区（默认）
    DOWNLOAD_AFTER(1);      //引导下载后

    protected int type;

    ResourceGroup(int type)
    {
        this.type = type;
    }

    public int getType()
    {
        return type;
    }
}
