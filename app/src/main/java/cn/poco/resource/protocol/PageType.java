package cn.poco.resource.protocol;

/**
 * 素材分类
 *
 * @author lmx
 *         Created by lmx on 2017/7/21.
 */

public enum PageType
{
    BGM(1),             //贴纸预览bgm
    STICKER_TAG(2),     //动态贴纸分类标签
    STICKER(3),         //动态贴纸素材
    LIVE_STICKER_TAG(4),//直播助手动态贴纸标签
    LIVE_STICKER(5);    //直播助手动态贴纸

    protected int type;

    PageType(int type)
    {
        this.type = type;
    }

    public int getType()
    {
        return type;
    }
}
