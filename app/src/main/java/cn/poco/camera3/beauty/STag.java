package cn.poco.camera3.beauty;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author lmx
 *         Created by lmx on 2017-12-15.
 */

public class STag
{
    @IntDef({SeekBarType.SEEK_TAG_UNIDIRECTIONAL, SeekBarType.SEEK_TAG_BIDIRECTIONAL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface SeekBarType
    {
        int SEEK_TAG_UNIDIRECTIONAL = 0x11;    //单向Seek bar
        int SEEK_TAG_BIDIRECTIONAL = 0x12;     //双向Seek bar
    }

    @IntDef({SelectorViewMode.UNSET, SelectorViewMode.MODE_NORMAL, SelectorViewMode.MODE_CUSTOMIZED})
    @Retention(RetentionPolicy.SOURCE)
    public @interface SelectorViewMode
    {
        int UNSET = -1;
        int MODE_NORMAL = 0x13;         //美颜 + 默认脸型调节
        int MODE_CUSTOMIZED = 0x14;     //美颜 + 定制脸型调节
    }

    @IntDef({
            BeautyTag.SKINBEAUTY,
            BeautyTag.WHITENTEETH,
            BeautyTag.SKINTYPE
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface BeautyTag
    {
        int UNSET = -1;
        int SKINBEAUTY = 0x15;  //美肤、肤质、磨皮
        int WHITENTEETH = 0x16; //美牙
        int SKINTYPE = 0x17;   //肤色
    }

}
