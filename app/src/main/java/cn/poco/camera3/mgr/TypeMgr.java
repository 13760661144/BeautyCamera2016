package cn.poco.camera3.mgr;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Gxx on 2017/11/2.
 */

public class TypeMgr
{
    //约定顺序
    @Retention(RetentionPolicy.SOURCE)
    public @interface StickerLabelType
    {
        int HOT = 1 << 1;//热门
        int TEXT = 1 << 2;//只有文本,一般的tab
        int MANAGER = 1 << 3;//管理
        int FACE = 1 << 4;//脸型
    }
}
