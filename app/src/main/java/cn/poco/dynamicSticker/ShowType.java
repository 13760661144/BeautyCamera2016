package cn.poco.dynamicSticker;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author lmx
 *         Created by lmx on 2017/6/1.
 */

public class ShowType
{
    //贴纸 显示 类型
    public static final String STICKER = "sticker"; // 0
    public static final String GIF = "gif"; // 1
    public static final String BOTH = "both"; // 3

    public static int GetType(String type)
    {
        if (STICKER.equals(type))
        {
            return 0;
        }
        else if (GIF.equals(type))
        {
            return 1;
        }
        else if (BOTH.equals(type))
        {
            return 3;
        }
        return 3;
    }


    //标签分类 显示 类型
    @IntDef({Label.ALL, Label.CAMERA, Label.LIVE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Label
    {
        public final int ALL = 0;       //全部
        public final int CAMERA = 1;    //镜头&社区
        public final int LIVE = 2;      //直播助手
    }
}
