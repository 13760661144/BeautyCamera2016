package cn.poco.camera3.beauty.data;

/**
 * @author lmx
 *         Created by lmx on 2017-12-15.
 */

public abstract class BaseInfo
{
    public int id;              //id
    public String name;         //名字
    public int resType;         //类型 1、内置预设，2、用户设定（同步网络）
    public int showType =
            SHOW_TYPE_CAMERA;   //0：镜头社区&直播助手共用，1；镜头社区，2：直播助手


    public static int RES_TYPE_LOCAL = 1;
    public static int RES_TYPE_SYNC = 2;

    public static int SHOW_TYPE_ALL = 0;
    public static int SHOW_TYPE_CAMERA = 1;
    public static int SHOW_TYPE_LIVE = 2;

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }


    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public int getResType()
    {
        return resType;
    }

    /**
     * see {@link #RES_TYPE_LOCAL},{@link #RES_TYPE_SYNC}
     *
     * @param resType
     */
    public void setResType(int resType)
    {
        this.resType = resType;
    }

    public int getShowType()
    {
        return showType;
    }

    /**
     * see {@link #SHOW_TYPE_ALL},{@link #SHOW_TYPE_CAMERA},{@link #SHOW_TYPE_LIVE}
     *
     * @param showType
     */
    public void setShowType(int showType)
    {
        this.showType = showType;
    }
}
